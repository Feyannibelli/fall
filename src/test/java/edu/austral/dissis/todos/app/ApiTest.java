package edu.austral.dissis.todos.app;

import edu.austral.dissis.todos.fall.server.Request;
import org.junit.jupiter.api.Test;

import static edu.austral.dissis.todos.app.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class APITest {

    @Test
    public void shouldRetrieveCreatedTask() {

        var app = new TodosApp();
        var response = app.handleRequest(new Request(
                "/todos",
                "POST",
                NEW_TASK_REQUEST
        ));

        String createdTaskId = ""; // to be parsed from the response

        var getTaskResponse = app.handleRequest(new Request(
                "/todos/${%s}".formatted(createdTaskId),
                "GET"
        ));

        assertEquals(200, getTaskResponse.statusCode());
        assertEquals(TASK_NAME, ""); // to be parsed from the response
        assertEquals(TASK_DESCRIPTION, ""); // to be parsed from the response
        assertFalse(false); // to be parsed from the response
    }

    @Test
    public void shouldRetrieveModifiedTask() {
        var app = new TodosApp();
        var response = app.handleRequest(new Request(
                "/todos",
                "POST",
                NEW_TASK_REQUEST
        ));

        String createdTaskId = ""; // to be parsed from the response

        var updateNameResponse = app.handleRequest(new Request(
                "/todos/${%s}".formatted(createdTaskId),
                "PATCH",
                UPDATE_TASK_REQUEST
        ));

        assertEquals(200, updateNameResponse.statusCode());

        var getTaskResponse = app.handleRequest(new Request(
                "/todos/${%s}".formatted(createdTaskId),
                "GET"
        ));

        assertEquals(200, getTaskResponse.statusCode());
        assertEquals(NEW_TASK_NAME, ""); // to be parsed from the response
        assertEquals(NEW_TASK_DESCRIPTION, ""); // to be parsed from the response
        assertTrue(true); // to be parsed from the response
    }

    @Test
    public void shouldNotRetrieveDeletedTask() {

        var app = new TodosApp();
        var response = app.handleRequest(new Request(
                "/todos",
                "POST",
                NEW_TASK_REQUEST
        ));

        String createdTaskId = ""; // to be parsed from the response

        var deleteTaskResponse = app.handleRequest(new Request(
                "/todos/${%s}".formatted(createdTaskId),
                "DELETE"
        ));

        assertEquals(200, deleteTaskResponse.statusCode());

        var getTaskResponse = app.handleRequest(new Request(
                "/todos/${%s}".formatted(createdTaskId),
                "GET"
        ));

        assertEquals(404, getTaskResponse.statusCode());
    }

    @Test
    public void shouldNotRetrieveTaskWithInvalidId() {
        var app = new TodosApp();
        var response = app.handleRequest(new Request(
                "/todos/invalid-id",
                "GET"
        ));

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldRetrieveAllTasks() {
        var app = new TodosApp();
        app.handleRequest(new Request(
                "/todos",
                "POST",
                NEW_TASK_REQUEST
        ));
        app.handleRequest(new Request(
                "/todos",
                "POST",
                NEW_TASK_REQUEST
        ));
        var getAllTasksResponse = app.handleRequest(new Request(
                "/todos",
                "GET"
        ));
        var length = 0; // to be parsed from the response
        assertEquals(200, getAllTasksResponse.statusCode());
        assertEquals(2, length);
    }
}
