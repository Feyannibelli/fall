package edu.austral.dissis.todos.fall.uuid;

public class Uuid {

  public static String generate() {
    return java.util.UUID.randomUUID().toString();
  }
}
