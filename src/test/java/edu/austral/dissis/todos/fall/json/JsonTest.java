package edu.austral.dissis.todos.fall.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONTest {

    @Test
    public void shouldParsePersonJSON() {

        record Person(String name, int age) {
        }

        String json = """
                {
                    "name": "John",
                    "age": 30
                }
                """;
        Person person = JSONParser.fromJSON(json, Person.class);
        assertEquals("John", person.name());
        assertEquals(30, person.age());
    }

    @Test
    public void shouldWritePersonJSON() {

        record Person(String name, int age) {
        }

        Person person = new Person("John", 30);
        String json = JSONParser.toJSON(person);
        assertEquals(formatJSON("""
                {
                    "name": "John",
                    "age": 30
                }
                """), formatJSON(json));
    }

    private String formatJSON(String json) {
        return json.replaceAll("\\s*([{}\\[\\]:,\"])\\s*", "$1");
    }


}
