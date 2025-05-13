package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.*;

import edu.austral.dissis.todos.app.logging.*;
import edu.austral.dissis.todos.fall.json.JsonParser;
import edu.austral.dissis.todos.fall.server.Request;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoggingTest {

  private static final String TEST_CONFIG_PATH = "src/test/resources/test-configuration.yml";
  private static final String TEST_LOG_FILE = "test-application.log";
  private ByteArrayOutputStream consoleOutput;
  private PrintStream originalOut;

  @BeforeEach
  public void setup() {
    // Capture console output
    consoleOutput = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(consoleOutput));

    // Create test directory if it doesn't exist
    try {
      Path configDir = Paths.get(TEST_CONFIG_PATH).getParent();
      if (!Files.exists(configDir)) {
        Files.createDirectories(configDir);
      }

      // Delete log file if it exists
      Files.deleteIfExists(Paths.get(TEST_LOG_FILE));
    } catch (Exception e) {
      // Ignore setup issues
    }
  }

  @AfterEach
  public void cleanup() {
    System.setOut(originalOut);
    try {
      Files.deleteIfExists(Paths.get(TEST_LOG_FILE));
      Files.deleteIfExists(Paths.get(TEST_CONFIG_PATH));
    } catch (Exception e) {
      // Ignore cleanup issues
    }
  }

  private void createTestConfiguration(String content) throws Exception {
    Files.writeString(Paths.get(TEST_CONFIG_PATH), content);
  }

  private String getConsoleOutput() {
    return consoleOutput.toString().trim();
  }

  private String getFileOutput() {
    try {
      if (Files.exists(Paths.get(TEST_LOG_FILE))) {
        return Files.readString(Paths.get(TEST_LOG_FILE)).trim();
      }
      return "";
    } catch (Exception e) {
      return "";
    }
  }

  @Test
  public void shouldNotLogIfNoTransportsSpecified() {
    // Arrange
    TestLogger logger = new TestLogger();

    // Act
    logger.info("Test message");

    // Assert
    assertEquals("", getConsoleOutput());
    assertEquals("", getFileOutput());
  }

  @Test
  public void shouldLogToFileIfFileSpecified() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - FILE
        """);

    TestLogger logger = new TestLogger();

    // Act
    logger.info("Test message");

    // Assert
    assertEquals("", getConsoleOutput());
    assertTrue(getFileOutput().contains("INFO: Test message"));
  }

  @Test
  public void shouldLogToConsoleIfConsoleSpecified() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();

    // Act
    logger.info("Test message");

    // Assert
    assertTrue(getConsoleOutput().contains("INFO: Test message"));
    assertEquals("", getFileOutput());
  }

  @Test
  public void shouldLogToConsoleAndFileIfBothSpecified() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
              - FILE
        """);

    TestLogger logger = new TestLogger();

    // Act
    logger.info("Test message");

    // Assert
    assertTrue(getConsoleOutput().contains("INFO: Test message"));
    assertTrue(getFileOutput().contains("INFO: Test message"));
  }

  @Test
  public void shouldLogAtDifferentLevels() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
          - level: ERROR
            transports:
              - FILE
          - level: DEBUG
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();

    // Act
    logger.info("Info message");
    logger.error("Error message");
    logger.debug("Debug message");

    // Assert
    String consoleOut = getConsoleOutput();
    assertTrue(consoleOut.contains("INFO: Info message"));
    assertTrue(consoleOut.contains("DEBUG: Debug message"));
    assertFalse(consoleOut.contains("ERROR: Error message"));

    String fileOut = getFileOutput();
    assertTrue(fileOut.contains("ERROR: Error message"));
    assertFalse(fileOut.contains("INFO: Info message"));
    assertFalse(fileOut.contains("DEBUG: Debug message"));
  }

  @Test
  public void shouldLogTaskCreated() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: DEBUG
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String id = "test-id";
    String name = "Test Task";
    String description = "Test Description";
    boolean done = false;
    Task task = new Task(id, name, description, done);

    // Act
    logger.logTaskCreated(task);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("DEBUG: Task created"));
    assertTrue(output.contains(id));
    assertTrue(output.contains(name));
    assertTrue(output.contains(description));
  }

  @Test
  public void shouldLogTaskUpdated() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: DEBUG
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String id = "test-id";
    String name = "Updated Task";
    String description = "Updated Description";
    boolean done = true;
    Task task = new Task(id, name, description, done);

    // Act
    logger.logTaskUpdated(task);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("DEBUG: Task updated"));
    assertTrue(output.contains(id));
    assertTrue(output.contains(name));
    assertTrue(output.contains(description));
    assertTrue(output.contains("true"));
  }

  @Test
  public void shouldLogTaskDeleted() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: DEBUG
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String id = "test-id";
    String name = "Test Task";
    String description = "Test Description";
    boolean done = false;
    Task task = new Task(id, name, description, done);

    // Act
    logger.logTaskDeleted(task);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("DEBUG: Task deleted"));
    assertTrue(output.contains(id));
    assertTrue(output.contains(name));
    assertTrue(output.contains(description));
  }

  @Test
  public void shouldLogTaskNotFound() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: ERROR
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String id = "missing-id";

    // Act
    logger.logTaskNotFound(id);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("ERROR: Task not found"));
    assertTrue(output.contains(id));
  }

  @Test
  public void shouldLogGetRequest() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String url = "/tasks";
    String method = "GET";
    Request request = new Request(url, method);

    // Act
    logger.logRequest(request);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("INFO: GET"));
    assertTrue(output.contains(url));
    assertTrue(output.contains(method));
  }

  @Test
  public void shouldLogPostRequest() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String url = "/tasks";
    String method = "POST";
    String body = "{\"title\":\"New Task\",\"description\":\"New Description\",\"done\":false}";
    Request request = new Request(url, method, body);

    // Act
    logger.logRequest(request);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("INFO: POST"));
    assertTrue(output.contains(url));
    assertTrue(output.contains(method));
    assertTrue(output.contains(body.replace("\"", "\\\"")));
  }

  @Test
  public void shouldLogPutRequest() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String url = "/tasks/123";
    String method = "PUT";
    String body = "{\"title\":\"Updated Task\",\"description\":\"Updated Description\",\"done\":true}";
    Request request = new Request(url, method, body);

    // Act
    logger.logRequest(request);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("INFO: PUT"));
    assertTrue(output.contains(url));
    assertTrue(output.contains(method));
    assertTrue(output.contains(body.replace("\"", "\\\"")));
  }

  @Test
  public void shouldLogDeleteRequest() throws Exception {
    // Arrange
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - CONSOLE
        """);

    TestLogger logger = new TestLogger();
    String url = "/tasks/123";
    String method = "DELETE";
    Request request = new Request(url, method);

    // Act
    logger.logRequest(request);

    // Assert
    String output = getConsoleOutput();
    assertTrue(output.contains("INFO: DELETE"));
    assertTrue(output.contains(url));
    assertTrue(output.contains(method));
  }

  @Test
  public void shouldHandleExceptionWhenWritingToFile() throws Exception {
    // This test verifies the system doesn't crash when it fails to write to a log file
    createTestConfiguration("""
        loggers:
          - level: INFO
            transports:
              - FILE
        """);

    // Create a read-only directory to force a write error
    Path testDir = Paths.get("read-only-dir");
    Files.createDirectories(testDir);
    File dir = testDir.toFile();
    dir.setReadOnly();

    try {
      // Create a logger that will fail to write to the file
      TestLogger logger = new TestLogger("read-only-dir/test.log");

      // Act - this shouldn't throw exception even though file write will fail
      logger.info("Test message");

      // No assert needed, test passes if no exception is thrown
    } finally {
      // Clean up
      dir.setWritable(true);
      Files.deleteIfExists(testDir);
    }
  }

  private static class TestLogger extends Logger {
    public TestLogger() {
      super(false); // Not in test mode so it will actually log
    }

    public TestLogger(String logFilePath) {
      super(false);
      // Inject custom log file path
      try {
        java.lang.reflect.Field field = Logger.class.getDeclaredField("LOG_FILE");
        field.setAccessible(true);
        java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        field.set(null, logFilePath);
      } catch (Exception e) {
        // Ignore reflection errors in tests
      }
    }
  }
}