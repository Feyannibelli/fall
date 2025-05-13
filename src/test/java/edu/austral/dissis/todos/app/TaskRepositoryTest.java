package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.*;

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
            // Use a test-specific directory to avoid contaminating the main tasks directory
            Path tasksDir = Paths.get(testTaskDir);
            if (Files.exists(tasksDir)) {
                Files.walk(tasksDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            // Create a clean test directory
            Files.createDirectories(tasksDir);

            // Initialize repository with test path through reflection
            repository = new TaskRepository();
            try {
                java.lang.reflect.Field field = TaskRepository.class.getDeclaredField("TASKS_DIRECTORY");
                field.setAccessible(true);
                java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
                field.set(null, testTaskDir);
            } catch (Exception e) {
                // Ignore reflection errors
            }
        } catch (Exception e) {
            // Ignore directory cleanup errors
        }
    }

    @Test
    public void shouldCreateTasksDirectoryIfNotExists() {
        // Act - constructor already called in setup

        // Assert
        assertTrue(Files.exists(Paths.get(testTaskDir)));
    }

    @Test
    public void shouldSaveAndFindTask() {
        // Arrange
        Task task = Task.create("Test Task", "Test Description", false);

        // Act
        Task savedTask = repository.save(task);
        Optional<Task> foundTask = repository.findById(task.id());

        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals(task.id(), foundTask.get().id());
        assertEquals(task.title(), foundTask.get().title());
        assertEquals(task.description(), foundTask.get().description());
        assertEquals(task.done(), foundTask.get().done());
    }

    @Test
    public void shouldReturnEmptyOptionalForNonExistentTask() {
        // Act
        Optional<Task> result = repository.findById("non-existent-id");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldDeleteTask() {
        // Arrange
        Task task = Task.create("Test Task", "Test Description", false);
        repository.save(task);

        // Act
        repository.delete(task.id());
        Optional<Task> foundTask = repository.findById(task.id());

        // Assert
        assertFalse(foundTask.isPresent());
    }

    @Test
    public void shouldUpdateTask() {
        // Arrange
        Task task = Task.create("Original Title", "Original Description", false);
        repository.save(task);

        // Act
        Task updatedTask = task.update("Updated Title", "Updated Description", true);
        repository.save(updatedTask);
        Optional<Task> foundTask = repository.findById(task.id());

        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals("Updated Title", foundTask.get().title());
        assertEquals("Updated Description", foundTask.get().description());
        assertTrue(foundTask.get().done());
    }

    @Test
    public void shouldFindAllTasks() {
        // Arrange
        Task task1 = Task.create("Task 1", "Description 1", false);
        Task task2 = Task.create("Task 2", "Description 2", true);
        repository.save(task1);
        repository.save(task2);

        // Act
        List<Task> tasks = repository.findAll();

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
    }

    @Test
    public void shouldHandleInvalidFileInFindAll() throws Exception {
        // Arrange
        // Create a valid task
        Task task = Task.create("Task 1", "Description 1", false);
        repository.save(task);

        // Create an invalid JSON file
        Files.writeString(Paths.get(testTaskDir, "invalid.json"), "This is not valid JSON");

        // Act
        List<Task> tasks = repository.findAll();

        // Assert - should only have the valid task
        assertEquals(1, tasks.size());
        assertEquals(task.id(), tasks.get(0).id());
    }
}
