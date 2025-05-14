package edu.austral.dissis.todos.fall.yaml;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YAMLTest {

    @Test
    public void testFromYAML() {
        record Person(String name, int age, List<Person> children) {
        }
        var yaml = """
                name: John
                age: 30
                children:
                  - name: Jane
                    age: 10
                """;
        var person = YAMLParser.fromYAML(yaml, Person.class);

        assertEquals("John", person.name());
        assertEquals(30, person.age());
        assertEquals(1, person.children().size());
        assertEquals("Jane", person.children().getFirst().name());
        assertEquals(10, person.children().getFirst().age());

    }

}
