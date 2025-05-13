package edu.austral.dissis.todos.fall.server;

import com.sun.net.httpserver.HttpServer;
import edu.austral.dissis.todos.app.TodosApp;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {

  public void init(TodosApp todosApp) {
    HttpServer server;
    try {
      server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext(
          "/",
          exchange -> {
            var url = exchange.getRequestURI().toString();
            var method = exchange.getRequestMethod();
            var requestBody = new String(exchange.getRequestBody().readAllBytes());
            var request = new Request(url, method, requestBody);
            var response = todosApp.handleRequest(request);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(response.statusCode(), response.body().length());
            exchange.getResponseBody().write(response.body().getBytes());
            exchange.close();
          });
      server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
      server.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
