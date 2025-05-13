package edu.austral.dissis.todos.app;

import edu.austral.dissis.todos.app.logging.Logger;
import edu.austral.dissis.todos.fall.json.JsonParser;
import edu.austral.dissis.todos.fall.server.Request;
import edu.austral.dissis.todos.fall.server.RequestHandler;
import edu.austral.dissis.todos.fall.server.Response;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodosApp implements RequestHandler {
  private static final Pattern TASK_ID_PATTERN = Pattern.compile("/tasks/([^/]+)$");

  private final TaskRepository repository;
  private final Logger logger;

  public TodosApp() {
    this(new TaskRepository(), new Logger());
  }

  public TodosApp(TaskRepository repository, Logger logger) {
    this.repository = repository;
    this.logger = logger;
  }

  @Override
  public Response handleRequest(Request request) {
    logger.logRequest(request);

    if (request.url().equals("/tasks") && request.method().equals("GET")) {
      return handleGetAllTasks();
    } else if (request.url().equals("/tasks") && request.method().equals("POST")) {
      return handleCreateTask(request);
    } else {
      Matcher matcher = TASK_ID_PATTERN.matcher(request.url());
      if (matcher.matches()) {
        String taskId = matcher.group(1);

        if (request.method().equals("GET")) {
          return handleGetTask(taskId);
        } else if (request.method().equals("PATCH") || request.method().equals("PUT")) {
          return handleUpdateTask(taskId, request);
        } else if (request.method().equals("DELETE")) {
          return handleDeleteTask(taskId);
        }
      }
    }

    return new Response(404, JsonParser.toJson(new ErrorResponse("Resource not found")));
  }

  private Response handleGetAllTasks() {
    List<Task> tasks = repository.findAll();
    TasksResponse response = TasksResponse.fromTasks(tasks);
    return new Response(200, JsonParser.toJson(response));
  }

  private Response handleCreateTask(Request request) {
    try {
      TaskRequest taskRequest = JsonParser.fromJson(request.body(), TaskRequest.class);
      Task task = Task.create(taskRequest.title(), taskRequest.description(), taskRequest.done());

      Task savedTask = repository.save(task);
      logger.logTaskCreated(savedTask);

      return new Response(201, JsonParser.toJson(TaskResponse.fromTask(savedTask)));
    } catch (Exception e) {
      logger.error("Failed to create task: " + e.getMessage());
      return new Response(400, JsonParser.toJson(new ErrorResponse("Invalid task data")));
    }
  }

  private Response handleGetTask(String taskId) {
    Optional<Task> taskOpt = repository.findById(taskId);

    if (taskOpt.isPresent()) {
      Task task = taskOpt.get();
      return new Response(200, JsonParser.toJson(TaskResponse.fromTask(task)));
    } else {
      logger.logTaskNotFound(taskId);
      return new Response(404, JsonParser.toJson(new ErrorResponse("Task not found")));
    }
  }

  private Response handleUpdateTask(String taskId, Request request) {
    Optional<Task> taskOpt = repository.findById(taskId);

    if (taskOpt.isPresent()) {
      try {
        Task existingTask = taskOpt.get();
        TaskRequest taskRequest = JsonParser.fromJson(request.body(), TaskRequest.class);

        Task updatedTask =
            existingTask.update(taskRequest.title(), taskRequest.description(), taskRequest.done());

        Task savedTask = repository.save(updatedTask);
        logger.logTaskUpdated(savedTask);

        return new Response(200, JsonParser.toJson(TaskResponse.fromTask(savedTask)));
      } catch (Exception e) {
        logger.error("Failed to update task: " + e.getMessage());
        return new Response(400, JsonParser.toJson(new ErrorResponse("Invalid task data")));
      }
    } else {
      logger.logTaskNotFound(taskId);
      return new Response(404, JsonParser.toJson(new ErrorResponse("Task not found")));
    }
  }

  private Response handleDeleteTask(String taskId) {
    Optional<Task> taskOpt = repository.findById(taskId);

    if (taskOpt.isPresent()) {
      Task task = taskOpt.get();
      repository.delete(taskId);
      logger.logTaskDeleted(task);

      return new Response(
          200,
          JsonParser.toJson(
              new TaskResponse(task.id(), task.title(), task.description(), task.done())));
    } else {
      logger.logTaskNotFound(taskId);
      return new Response(404, JsonParser.toJson(new ErrorResponse("Task not found")));
    }
  }
}
