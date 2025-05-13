package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

  @Test
  public void shouldCreateTaskWithGeneratedId() {
    Task task = Task.create("Test Task", "Test Description", false);

    assertNotNull(task.id());
    assertEquals(36, task.id().length()); // UUID length
    assertEquals("Test Task", task.title());
    assertEquals("Test Description", task.description());
    assertFalse(task.done());
  }

  @Test
  public void shouldUpdateTaskPreservingId() {
    String id = "test-id";
    Task originalTask = new Task(id, "Original Title", "Original Description", false);

    Task updatedTask = originalTask.update("Updated Title", "Updated Description", true);

    assertEquals(id, updatedTask.id()); // ID should not change
    assertEquals("Updated Title", updatedTask.title());
    assertEquals("Updated Description", updatedTask.description());
    assertTrue(updatedTask.done());
  }

  @Test
  public void shouldCreateTaskResponse() {
    String id = "test-id";
    String title = "Test Task";
    String description = "Test Description";
    boolean done = false;
    Task task = new Task(id, title, description, done);

    TaskResponse response = TaskResponse.fromTask(task);

    assertEquals(id, response.id());
    assertEquals(title, response.title());
    assertEquals(description, response.description());
    assertEquals(done, response.done());
  }

  @Test
  public void shouldCreateTasksResponse() {
    Task task1 = new Task("id1", "Task 1", "Description 1", false);
    Task task2 = new Task("id2", "Task 2", "Description 2", true);

    TasksResponse response = TasksResponse.fromTasks(java.util.List.of(task1, task2));

    assertEquals(2, response.tasks().size());
    assertEquals("id1", response.tasks().get(0).id());
    assertEquals("Task 1", response.tasks().get(0).title());
    assertEquals("id2", response.tasks().get(1).id());
    assertTrue(response.tasks().get(1).done());
  }
}
