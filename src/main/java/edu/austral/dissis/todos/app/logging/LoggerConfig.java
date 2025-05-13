package edu.austral.dissis.todos.app.logging;

import java.util.List;

public record LoggerConfig(LogLevel level, List<LogTransport> transports) {}
