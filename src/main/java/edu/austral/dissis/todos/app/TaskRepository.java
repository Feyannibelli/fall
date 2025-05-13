package edu.austral.dissis.todos.app;

import edu.austral.dissis.todos.fall.json.JsonParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepository {
  private static final String TASKS_DIRECTORY = "tasks";

  public TaskRepository() {
    createTasksDirectoryIfNotExists();
  }

  private void createTasksDirectoryIfNotExists() {
    File directory = new File(TASKS_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public List<Task> findAll() {
    List<Task> tasks = new ArrayList<>();
    File directory = new File(TASKS_DIRECTORY);

    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
      if (files != null) {
        for (File file : files) {
          try {
            String json = Files.readString(file.toPath());
            Task task = JsonParser.fromJson(json, Task.class);
            tasks.add(task);
          } catch (IOException e) {
            // Skipping invalid task file, continuing with other tasks
          }
        }
      }
    }

    return tasks;
  }

  public Optional<Task> findById(String id) {
    Path filePath = Paths.get(TASKS_DIRECTORY, id + ".json");
    if (Files.exists(filePath)) {
      try {
        String json = Files.readString(filePath);
        Task task = JsonParser.fromJson(json, Task.class);
        return Optional.of(task);
      } catch (IOException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  public Task save(Task task) {
    Path filePath = Paths.get(TASKS_DIRECTORY, task.id() + ".json");
    try {
      String json = JsonParser.toJson(task);
      Files.writeString(filePath, json);
      return task;
    } catch (IOException e) {
      throw new RuntimeException("Failed to save task", e);
    }
  }

  public void delete(String id) {
    Path filePath = Paths.get(TASKS_DIRECTORY, id + ".json");
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete task", e);
    }
  }
}
