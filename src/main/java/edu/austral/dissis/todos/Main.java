package edu.austral.dissis.todos;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import edu.austral.dissis.todos.app.TodosApp;
import edu.austral.dissis.todos.fall.server.Server;

import java.util.Date;

public class Main {

  public static void main(String[] args) {
    new Server().init(new TodosApp());
  }
}
