package edu.austral.dissis.todos.app.logging;

import edu.austral.dissis.todos.app.Task;
import edu.austral.dissis.todos.fall.json.JsonParser;
import edu.austral.dissis.todos.fall.server.Request;
import edu.austral.dissis.todos.fall.yaml.YamlParser;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Logger {
  private static final String CONFIG_FILE = "src/main/resources/configuration.yml";
  private static final String LOG_FILE = "application.log";

  private final Map<LogLevel, List<LogTransport>> configuredTransports;
  private final boolean testMode;

  public Logger() {
    this(false);
  }

  public Logger(boolean testMode) {
    this.testMode = testMode;
    this.configuredTransports = new EnumMap<>(LogLevel.class);
    loadConfiguration();
  }

  private void loadConfiguration() {
    try {
      if (Files.exists(Paths.get(CONFIG_FILE))) {
        String yamlContent = Files.readString(Paths.get(CONFIG_FILE));
        LoggerConfiguration config = YamlParser.fromYaml(yamlContent, LoggerConfiguration.class);

        for (LoggerConfig loggerConfig : config.loggers()) {
          configuredTransports.put(loggerConfig.level(), loggerConfig.transports());
        }
      }
    } catch (IOException e) {
      // Configuration file not found or inaccessible, using default settings
    }
  }

  private void log(LogLevel level, String message) {
    if (testMode) {
      return;
    }

    List<LogTransport> transports = configuredTransports.getOrDefault(level, List.of());
    if (transports.isEmpty()) {
      return;
    }

    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String formattedMessage = String.format("[%s] %s: %s", timestamp, level, message);

    for (LogTransport transport : transports) {
      switch (transport) {
        case CONSOLE:
          System.out.println(formattedMessage);
          break;
        case FILE:
          writeToFile(formattedMessage);
          break;
        default:
          System.err.println("Unhandled transport type: " + transport);
          break;
      }
    }
  }

  private void writeToFile(String message) {
    try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
      out.println(message);
    } catch (IOException e) {
      System.err.println("Failed to write to log file: " + e.getMessage());
      System.err.println("Original message: " + message);
    }
  }

  public void info(String message) {
    log(LogLevel.INFO, message);
  }

  public void error(String message) {
    log(LogLevel.ERROR, message);
  }

  public void debug(String message) {
    log(LogLevel.DEBUG, message);
  }

  public void logRequest(Request request) {
    String message =
        String.format(
            "%s {\"url\": \"%s\", \"method\": \"%s\", \"body\": \"%s\"}",
            request.method(), request.url(), request.method(), request.body());
    info(message);
  }

  public void logTaskCreated(Task task) {
    String taskJson = JsonParser.toJson(task);
    debug("Task created " + taskJson);
  }

  public void logTaskUpdated(Task task) {
    String taskJson = JsonParser.toJson(task);
    debug("Task updated " + taskJson);
  }

  public void logTaskDeleted(Task task) {
    String taskJson = JsonParser.toJson(task);
    debug("Task deleted " + taskJson);
  }

  public void logTaskNotFound(String id) {
    String message = String.format("Task not found {\"id\": \"%s\"}", id);
    error(message);
  }
}
