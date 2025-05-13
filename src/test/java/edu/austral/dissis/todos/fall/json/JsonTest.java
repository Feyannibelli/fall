package edu.austral.dissis.todos.fall.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JsonTest {

  @Test
  public void shouldParsePersonJson() {

    record Person(String name, int age) {}

    String json =
        """
                {
                    "name": "John",
                    "age": 30
                }
                """;
    Person person = JsonParser.fromJson(json, Person.class);
    assertEquals("John", person.name());
    assertEquals(30, person.age());
  }

  @Test
  public void shouldWritePersonJson() {

    record Person(String name, int age) {}

    Person person = new Person("John", 30);
    String json = JsonParser.toJson(person);
    assertEquals(
        formatJson(
            """
                {
                    "name": "John",
                    "age": 30
                }
                """),
        formatJson(json));
  }

  private String formatJson(String json) {
    return json.replaceAll("\\s*([{}\\[\\]:,\"])\\s*", "$1");
  }
}
