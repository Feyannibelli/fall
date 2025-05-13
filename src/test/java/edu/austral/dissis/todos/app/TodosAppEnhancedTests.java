package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.todos.app.logging.Logger;
import edu.austral.dissis.todos.fall.json.JsonParser;
import edu.austral.dissis.todos.fall.server.Request;
import edu.austral.dissis.todos.fall.server.Response;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TodosAppEnhancedTests {
  private TodosApp app;
  private static final String TEST_TASKS_DIR = "test-todos-app-tasks";

  @BeforeEach
  public void setup() {
    try {
      Path tasksDir = Paths.get(TEST_TASKS_DIR);
      if (Files.exists(tasksDir)) {
        Files.walk(tasksDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }
      Files.createDirectories(tasksDir);
    } catch (Exception e) {
      // Ignore cleanup errors
    }

    TaskRepository taskRepository = new TaskRepository(TEST_TASKS_DIR);
    Logger logger = new Logger(true);
    app = new TodosApp(taskRepository, logger);
  }

  @Test
  public void shouldReturn404ForNonExistentResource() {
    Request request = new Request("/non-existent", "GET");
    Response response = app.handleRequest(request);

    assertEquals(404, response.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(response.body(), ErrorResponse.class);
    assertEquals("Resource not found", errorResponse.message());
  }

  @Test
  public void shouldReturn400ForInvalidTaskCreation() {
    Request request = new Request("/tasks", "POST", "this is not valid json");
    Response response = app.handleRequest(request);

    assertEquals(400, response.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(response.body(), ErrorResponse.class);
    assertEquals("Invalid task data", errorResponse.message());
  }

  @Test
  public void shouldReturn400ForInvalidTaskUpdate() {
    Request createRequest =
        new Request(
            "/tasks",
            "POST",
            "{\"title\":\"Test Task\",\"description\":\"Test Description\",\"done\":false}");
    Response createResponse = app.handleRequest(createRequest);

    TaskResponse taskResponse = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String taskId = taskResponse.id();

    Request updateRequest = new Request("/tasks/" + taskId, "PUT", "invalid json");
    Response updateResponse = app.handleRequest(updateRequest);

    assertEquals(400, updateResponse.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(updateResponse.body(), ErrorResponse.class);
    assertEquals("Invalid task data", errorResponse.message());
  }

  @Test
  public void shouldReturnEmptyTasksListWhenNoTasksExist() {
    Request request = new Request("/tasks", "GET");
    Response response = app.handleRequest(request);

    assertEquals(200, response.statusCode());

    TasksResponse tasksResponse = JsonParser.fromJson(response.body(), TasksResponse.class);
    assertTrue(tasksResponse.tasks().isEmpty());
  }

  @Test
  public void shouldHandlePutMethodForTaskUpdate() {
    Request createRequest =
        new Request(
            "/tasks",
            "POST",
            "{\"title\":\"Original Task\",\"description\":\"Original Description\","
                + "\"done\":false}");
    Response createResponse = app.handleRequest(createRequest);

    TaskResponse taskResponse = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String taskId = taskResponse.id();

    Request putRequest =
        new Request(
            "/tasks/" + taskId,
            "PUT",
            "{\"title\":\"Updated Task\",\"description\":\"Updated Description\","
                + "\"done\":true}");
    Response putResponse = app.handleRequest(putRequest);

    assertEquals(200, putResponse.statusCode());

    TaskResponse updatedTask = JsonParser.fromJson(putResponse.body(), TaskResponse.class);
    assertEquals("Updated Task", updatedTask.title());
    assertEquals("Updated Description", updatedTask.description());
    assertTrue(updatedTask.done());
  }

  @Test
  public void shouldReturn404WhenTaskIdMatchesPatternButDoesNotExist() {
    Request request = new Request("/tasks/non-existent-id", "GET");
    Response response = app.handleRequest(request);

    assertEquals(404, response.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(response.body(), ErrorResponse.class);
    assertEquals("Task not found", errorResponse.message());
  }

  @Test
  public void shouldReturn404WhenUpdatingNonexistentTask() {
    Request request =
        new Request(
            "/tasks/non-existent-id",
            "PATCH",
            "{\"title\":\"Updated Task\",\"description\":\"Updated Description\",\"done\":true}");
    Response response = app.handleRequest(request);

    assertEquals(404, response.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(response.body(), ErrorResponse.class);
    assertEquals("Task not found", errorResponse.message());
  }

  @Test
  public void shouldReturn404WhenDeletingNonexistentTask() {
    Request request = new Request("/tasks/non-existent-id", "DELETE");
    Response response = app.handleRequest(request);

    assertEquals(404, response.statusCode());

    ErrorResponse errorResponse = JsonParser.fromJson(response.body(), ErrorResponse.class);
    assertEquals("Task not found", errorResponse.message());
  }

  @Test
  public void shouldPerformCompleteTaskLifecycle() {
    Request createRequest =
        new Request(
            "/tasks",
            "POST",
            "{\"title\":\"Lifecycle Task\",\"description\":\"Testing complete lifecycle\","
                + "\"done\":false}");
    Response createResponse = app.handleRequest(createRequest);
    assertEquals(201, createResponse.statusCode());

    TaskResponse createdTask = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String taskId = createdTask.id();

    Request getRequest = new Request("/tasks/" + taskId, "GET");
    Response getResponse = app.handleRequest(getRequest);
    assertEquals(200, getResponse.statusCode());

    TaskResponse retrievedTask = JsonParser.fromJson(getResponse.body(), TaskResponse.class);
    assertEquals("Lifecycle Task", retrievedTask.title());

    Request patchRequest =
        new Request(
            "/tasks/" + taskId,
            "PATCH",
            "{\"title\":\"Updated Lifecycle Task\","
                + "\"description\":\"Updated lifecycle description\",\"done\":true}");
    Response patchResponse = app.handleRequest(patchRequest);
    assertEquals(200, patchResponse.statusCode());

    TaskResponse updatedTask = JsonParser.fromJson(patchResponse.body(), TaskResponse.class);
    assertTrue(updatedTask.done());

    Request getAllRequest = new Request("/tasks", "GET");
    Response getAllResponse = app.handleRequest(getAllRequest);
    assertEquals(200, getAllResponse.statusCode());

    TasksResponse allTasks = JsonParser.fromJson(getAllResponse.body(), TasksResponse.class);
    assertTrue(allTasks.tasks().stream().anyMatch(t -> t.id().equals(taskId)));

    Request deleteRequest = new Request("/tasks/" + taskId, "DELETE");
    Response deleteResponse = app.handleRequest(deleteRequest);
    assertEquals(200, deleteResponse.statusCode());

    Request verifyDeletedRequest = new Request("/tasks/" + taskId, "GET");
    Response verifyDeletedResponse = app.handleRequest(verifyDeletedRequest);
    assertEquals(404, verifyDeletedResponse.statusCode());
  }

  @Test
  public void shouldReturn404ForUnsupportedMethodOnTask() {
    Request createRequest =
        new Request(
            "/tasks",
            "POST",
            "{\"title\":\"Test Task\",\"description\":\"Test Description\",\"done\":false}");
    Response createResponse = app.handleRequest(createRequest);

    TaskResponse taskResponse = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String taskId = taskResponse.id();

    Request optionsRequest = new Request("/tasks/" + taskId, "OPTIONS");
    Response optionsResponse = app.handleRequest(optionsRequest);

    assertEquals(404, optionsResponse.statusCode());
  }
}
