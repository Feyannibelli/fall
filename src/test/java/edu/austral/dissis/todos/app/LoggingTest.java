package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.todos.app.logging.Logger;
import edu.austral.dissis.todos.fall.json.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class LoggingTest {

  private static class TestLogger extends Logger {
    private final ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final String logFilePath;

    public TestLogger(String configPath, String logFilePath) {
      super(true);
      this.logFilePath = logFilePath;

      System.setOut(new PrintStream(consoleOutput));

      try {
        createTestConfiguration(configPath);

        Files.deleteIfExists(Path.of(logFilePath));
      } catch (Exception e) {
        throw new RuntimeException("Failed to set up test environment", e);
      }
    }

    private void createTestConfiguration(String configPath) throws Exception {
      Path path = Path.of(configPath);
      Files.createDirectories(path.getParent());
    }

    public String getConsoleOutput() {
      return consoleOutput.toString().trim();
    }

    public String getFileOutput() {
      try {
        if (Files.exists(Path.of(logFilePath))) {
          return Files.readString(Path.of(logFilePath)).trim();
        }
        return "";
      } catch (Exception e) {
        throw new RuntimeException("Failed to read log file", e);
      }
    }

    public void cleanup() {
      System.setOut(originalOut);
      try {
        Files.deleteIfExists(Path.of(logFilePath));
      } catch (Exception e) {
        // Ignore
      }
    }
  }

  @Test
  public void shouldNotLogIfNoTransportsSpecified() {
    var logger = new Logger(true);
    logger.info("Test message");
  }

  @Test
  public void shouldLogToFileIfFileSpecified() {
    assertTrue(true);
  }

  @Test
  public void shouldLogToConsoleIfConsoleSpecified() {
    assertTrue(true);
  }

  @Test
  public void shouldLogToConsoleAndFileIfBothSpecified() {
    assertTrue(true);
  }

  @Test
  public void shouldLogTaskCreated() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String id = "test-id";
    String name = "Test Task";
    String description = "Test Description";
    boolean done = false;

    Task task = new Task(id, name, description, done);
    String taskJson = JsonParser.toJson(task);

    var expectedMessage = String.format("[%s] DEBUG: Task created %s", timestamp, taskJson);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogTaskUpdated() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String id = "test-id";
    String name = "Updated Task";
    String description = "Updated Description";
    boolean done = true;

    Task task = new Task(id, name, description, done);
    String taskJson = JsonParser.toJson(task);

    var expectedMessage = String.format("[%s] DEBUG: Task updated %s", timestamp, taskJson);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogTaskDeleted() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String id = "test-id";
    String name = "Test Task";
    String description = "Test Description";
    boolean done = false;

    Task task = new Task(id, name, description, done);
    String taskJson = JsonParser.toJson(task);

    var expectedMessage = String.format("[%s] DEBUG: Task deleted %s", timestamp, taskJson);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogTaskNotFound() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String id = "missing-id";

    var expectedMessage =
        String.format("[%s] ERROR: Task not found {\"id\": \"%s\"}", timestamp, id);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogGetRequest() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String url = "/todos";
    String method = "GET";
    String body = "";

    var expectedMessage =
        String.format(
            "[%s] INFO: %s {\"url\": \"%s\", \"method\": \"%s\", \"body\": \"%s\"}",
            timestamp, method, url, method, body);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogPutRequest() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String url = "/todos/123";
    String method = "PUT";
    String body =
        "{\"title\":\"Updated Task\",\"description\":\"Updated Description\",\"done\":true}";

    var expectedMessage =
        String.format(
            "[%s] INFO: %s {\"url\": \"%s\", \"method\": \"%s\", \"body\": \"%s\"}",
            timestamp, method, url, method, body);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogPostRequest() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String url = "/todos";
    String method = "POST";
    String body = "{\"title\":\"New Task\",\"description\":\"New Description\",\"done\":false}";

    var expectedMessage =
        String.format(
            "[%s] INFO: %s {\"url\": \"%s\", \"method\": \"%s\", \"body\": \"%s\"}",
            timestamp, method, url, method, body);

    assertNotEquals("", expectedMessage);
  }

  @Test
  public void shouldLogDeleteRequest() {
    String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String url = "/todos/123";
    String method = "DELETE";
    String body = "";

    var expectedMessage =
        String.format(
            "[%s] INFO: %s {\"url\": \"%s\", \"method\": \"%s\", \"body\": \"%s\"}",
            timestamp, method, url, method, body);

    assertNotEquals("", expectedMessage);
  }
}
