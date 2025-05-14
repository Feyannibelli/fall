package edu.austral.dissis.todos.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingTest {

    @Test
    public void shouldNotLogIfNoTransportsSpecified() {

    }

    @Test
    public void shouldLogToFileIfFileSpecified() {

    }

    @Test
    public void shouldLogToConsoleIfFileSpecified() {

    }

    @Test
    public void shouldLogToConsoleAndFileIfBothSpecified() {

    }

    @Test
    public void shouldLogTaskCreated() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] DEBUG: Task created {"id": "%s", "name": "%s", "description": "%s", "done": %s}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogTaskUpdated() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] DEBUG: Task updated {"id": "%s", "name": "%s", "description": "%s", "done": %s}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogTaskDeleted() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] DEBUG: Task deleted {"id": "%s", "name": "%s", "description": "%s", "done": %s}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogTaskNotFound() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] ERROR: Task not found {"id": "%s"}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogGetRequest() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] INFO: GET {"url": "%s", "method": "%s", "body": "%s"}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogPUTRequest() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] INFO: PUT {"url": "%s", "method": "%s", "body": "%s"}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogPOSTRequest() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] INFO: POST {"url": "%s", "method": "%s", "body": "%s"}
                """;

        assertEquals(expectedMessage, "");
    }

    @Test
    public void shouldLogDELETERequest() {
        var expectedMessage = """
                [2025-01-01T00:00:00.000+00:00] INFO: DELETE {"url": "%s", "method": "%s", "body": "%s"}
                """;

        assertEquals(expectedMessage, "");
    }
}
