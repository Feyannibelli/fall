package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void shouldCreateTaskWithGeneratedId() {
        // Act
        Task task = Task.create("Test Task", "Test Description", false);

        // Assert
        assertNotNull(task.id());
        assertEquals(36, task.id().length()); // UUID length
        assertEquals("Test Task", task.title());
        assertEquals("Test Description", task.description());
        assertFalse(task.done());
    }

    @Test
    public void shouldUpdateTaskPreservingId() {
        // Arrange
        String id = "test-id";
        Task originalTask = new Task(id, "Original Title", "Original Description", false);

        // Act
        Task updatedTask = originalTask.update("Updated Title", "Updated Description", true);

        // Assert
        assertEquals(id, updatedTask.id()); // ID should not change
        assertEquals("Updated Title", updatedTask.title());
        assertEquals("Updated Description", updatedTask.description());
        assertTrue(updatedTask.done());
    }

    @Test
    public void shouldCreateTaskResponse() {
        // Arrange
        String id = "test-id";
        String title = "Test Task";
        String description = "Test Description";
        boolean done = false;
        Task task = new Task(id, title, description, done);

        // Act
        TaskResponse response = TaskResponse.fromTask(task);

        // Assert
        assertEquals(id, response.id());
        assertEquals(title, response.title());
        assertEquals(description, response.description());
        assertEquals(done, response.done());
    }

    @Test
    public void shouldCreateTasksResponse() {
        // Arrange
        Task task1 = new Task("id1", "Task 1", "Description 1", false);
        Task task2 = new Task("id2", "Task 2", "Description 2", true);

        // Act
        TasksResponse response = TasksResponse.fromTasks(java.util.List.of(task1, task2));

        // Assert
        assertEquals(2, response.tasks().size());
        assertEquals("id1", response.tasks().get(0).id());
        assertEquals("Task 1", response.tasks().get(0).title());
        assertEquals("id2", response.tasks().get(1).id());
        assertTrue(response.tasks().get(1).done());
    }
}