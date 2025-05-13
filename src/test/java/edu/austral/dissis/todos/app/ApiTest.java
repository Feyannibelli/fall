package edu.austral.dissis.todos.app;

import static edu.austral.dissis.todos.app.Constants.NEW_TASK_DESCRIPTION;
import static edu.austral.dissis.todos.app.Constants.NEW_TASK_NAME;
import static edu.austral.dissis.todos.app.Constants.NEW_TASK_REQUEST;
import static edu.austral.dissis.todos.app.Constants.TASK_DESCRIPTION;
import static edu.austral.dissis.todos.app.Constants.TASK_NAME;
import static edu.austral.dissis.todos.app.Constants.UPDATE_TASK_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.todos.app.logging.Logger;
import edu.austral.dissis.todos.fall.json.JsonParser;
import edu.austral.dissis.todos.fall.server.Request;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiTest {

  @BeforeEach
  public void setup() {
    try {
      Path tasksDir = Paths.get("tasks");
      if (Files.exists(tasksDir)) {
        Files.walk(tasksDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      }
    } catch (Exception e) {
      // Ignore directory cleanup errors as they don't affect test execution
    }
  }

  @Test
  public void shouldRetrieveCreatedTask() {
    var taskRepository = new TaskRepository();
    var logger = new Logger(true);
    var app = new TodosApp(taskRepository, logger);

    var createResponse = app.handleRequest(new Request("/tasks", "POST", NEW_TASK_REQUEST));

    assertEquals(201, createResponse.statusCode());

    TaskResponse createdTask = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String createdTaskId = createdTask.id();

    var getTaskResponse = app.handleRequest(new Request("/tasks/" + createdTaskId, "GET"));

    assertEquals(200, getTaskResponse.statusCode());

    TaskResponse retrievedTask = JsonParser.fromJson(getTaskResponse.body(), TaskResponse.class);
    assertEquals(TASK_NAME, retrievedTask.title());
    assertEquals(TASK_DESCRIPTION, retrievedTask.description());
    assertFalse(retrievedTask.done());
  }

  @Test
  public void shouldRetrieveModifiedTask() {
    var taskRepository = new TaskRepository();
    var logger = new Logger(true);
    var app = new TodosApp(taskRepository, logger);

    var createResponse = app.handleRequest(new Request("/tasks", "POST", NEW_TASK_REQUEST));

    TaskResponse createdTask = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String createdTaskId = createdTask.id();

    var updateNameResponse =
        app.handleRequest(new Request("/tasks/" + createdTaskId, "PATCH", UPDATE_TASK_REQUEST));

    assertEquals(200, updateNameResponse.statusCode());

    var getTaskResponse = app.handleRequest(new Request("/tasks/" + createdTaskId, "GET"));

    assertEquals(200, getTaskResponse.statusCode());

    TaskResponse retrievedTask = JsonParser.fromJson(getTaskResponse.body(), TaskResponse.class);
    assertEquals(NEW_TASK_NAME, retrievedTask.title());
    assertEquals(NEW_TASK_DESCRIPTION, retrievedTask.description());
    assertTrue(retrievedTask.done());
  }

  @Test
  public void shouldNotRetrieveDeletedTask() {
    var taskRepository = new TaskRepository();
    var logger = new Logger(true);
    var app = new TodosApp(taskRepository, logger);

    var createResponse = app.handleRequest(new Request("/tasks", "POST", NEW_TASK_REQUEST));

    TaskResponse createdTask = JsonParser.fromJson(createResponse.body(), TaskResponse.class);
    String createdTaskId = createdTask.id();

    var deleteTaskResponse = app.handleRequest(new Request("/tasks/" + createdTaskId, "DELETE"));

    assertEquals(200, deleteTaskResponse.statusCode());

    var getTaskResponse = app.handleRequest(new Request("/tasks/" + createdTaskId, "GET"));

    assertEquals(404, getTaskResponse.statusCode());
  }

  @Test
  public void shouldNotRetrieveTaskWithInvalidId() {
    var taskRepository = new TaskRepository();
    var logger = new Logger(true);
    var app = new TodosApp(taskRepository, logger);

    var response = app.handleRequest(new Request("/tasks/invalid-id", "GET"));

    assertEquals(404, response.statusCode());
  }

  @Test
  public void shouldRetrieveAllTasks() {
    var taskRepository = new TaskRepository();
    var logger = new Logger(true);
    var app = new TodosApp(taskRepository, logger);

    app.handleRequest(new Request("/tasks", "POST", NEW_TASK_REQUEST));

    app.handleRequest(new Request("/tasks", "POST", NEW_TASK_REQUEST));

    var getAllTasksResponse = app.handleRequest(new Request("/tasks", "GET"));

    assertEquals(200, getAllTasksResponse.statusCode());

    TasksResponse tasksResponse =
        JsonParser.fromJson(getAllTasksResponse.body(), TasksResponse.class);
    assertEquals(2, tasksResponse.tasks().size());
  }
}
