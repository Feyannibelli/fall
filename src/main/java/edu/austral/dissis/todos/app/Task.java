package edu.austral.dissis.todos.app;

public record Task(String id, String title, String description, boolean done) {
  public static Task create(String title, String description, boolean done) {
    return new Task(edu.austral.dissis.todos.fall.uuid.Uuid.generate(), title, description, done);
  }

  public Task update(String title, String description, boolean done) {
    return new Task(id, title, description, done);
  }
}
