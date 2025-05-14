package edu.austral.dissis.todos.app;

import edu.austral.dissis.todos.fall.json.JSONParser;
import edu.austral.dissis.todos.fall.server.Request;
import edu.austral.dissis.todos.fall.server.RequestHandler;
import edu.austral.dissis.todos.fall.server.Response;

public class TodosApp implements RequestHandler {

    public Response handleRequest(Request request) {
        var response = JSONParser.toJSON(new ExampleBody("Hello world"));

        // start here

        return new Response(200, response);
    }

}
