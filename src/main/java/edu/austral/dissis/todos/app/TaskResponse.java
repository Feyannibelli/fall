package edu.austral.dissis.todos.app;

public record TaskResponse(String id, String title, String description, boolean done) {
  public static TaskResponse fromTask(Task task) {
    return new TaskResponse(task.id(), task.title(), task.description(), task.done());
  }
}
