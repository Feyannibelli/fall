package edu.austral.dissis.todos.app;

import java.util.List;

public record TasksResponse(List<TaskResponse> tasks) {
  public static TasksResponse fromTasks(List<Task> tasks) {
    return new TasksResponse(tasks.stream().map(TaskResponse::fromTask).toList());
  }
}
