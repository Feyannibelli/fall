package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskRepositoryTest {

  private TaskRepository repository;
  private final String testTaskDir = "test-tasks";

  @BeforeEach
  public void setup() {
    try {
      Path tasksDir = Paths.get(testTaskDir);
      if (Files.exists(tasksDir)) {
        Files.walk(tasksDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }

      Files.createDirectories(tasksDir);

      repository = new TaskRepository(testTaskDir);
    } catch (Exception e) {
      // Ignore directory cleanup errors
    }
  }

  @Test
  public void shouldCreateTasksDirectoryIfNotExists() {
    assertTrue(Files.exists(Paths.get(testTaskDir)));
  }

  @Test
  public void shouldSaveAndFindTask() {
    Task task = Task.create("Test Task", "Test Description", false);

    Task savedTask = repository.save(task);
    Optional<Task> foundTask = repository.findById(task.id());

    assertTrue(foundTask.isPresent());
    assertEquals(task.id(), foundTask.get().id());
    assertEquals(task.title(), foundTask.get().title());
    assertEquals(task.description(), foundTask.get().description());
    assertEquals(task.done(), foundTask.get().done());
  }

  @Test
  public void shouldReturnEmptyOptionalForNonExistentTask() {
    Optional<Task> result = repository.findById("non-existent-id");

    assertFalse(result.isPresent());
  }

  @Test
  public void shouldDeleteTask() {
    Task task = Task.create("Test Task", "Test Description", false);
    repository.save(task);

    repository.delete(task.id());
    Optional<Task> foundTask = repository.findById(task.id());

    assertFalse(foundTask.isPresent());
  }

  @Test
  public void shouldUpdateTask() {
    Task task = Task.create("Original Title", "Original Description", false);
    repository.save(task);

    Task updatedTask = task.update("Updated Title", "Updated Description", true);
    repository.save(updatedTask);
    Optional<Task> foundTask = repository.findById(task.id());

    assertTrue(foundTask.isPresent());
    assertEquals("Updated Title", foundTask.get().title());
    assertEquals("Updated Description", foundTask.get().description());
    assertTrue(foundTask.get().done());
  }

  @Test
  public void shouldFindAllTasks() {
    Task task1 = Task.create("Task 1", "Description 1", false);
    Task task2 = Task.create("Task 2", "Description 2", true);
    repository.save(task1);
    repository.save(task2);

    List<Task> tasks = repository.findAll();

    assertEquals(2, tasks.size());
    assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
    assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
  }

  @Test
  public void shouldHandleInvalidFileInFindAll() throws Exception {
    Task task = Task.create("Task 1", "Description 1", false);
    repository.save(task);

    Files.writeString(Paths.get(testTaskDir, "invalid.json"), "This is not valid JSON");

    List<Task> tasks = repository.findAll();

    assertEquals(1, tasks.size());
    assertEquals(task.id(), tasks.get(0).id());
  }
}
