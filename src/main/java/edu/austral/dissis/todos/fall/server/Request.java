package edu.austral.dissis.todos.fall.server;

public record Request(String url, String method, String body) {

    public Request(String url, String method) {
        this(url, method, "");
    }
}
