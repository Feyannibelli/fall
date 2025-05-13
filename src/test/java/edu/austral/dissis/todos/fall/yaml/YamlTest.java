package edu.austral.dissis.todos.fall.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class YamlTest {

  @Test
  public void testFromYaml() {
    record Person(String name, int age, List<Person> children) {}

    var yaml =
        """
            name: John
            age: 30
            children:
              - name: Jane
                age: 10
            """;
    var person = YamlParser.fromYaml(yaml, Person.class);

    assertEquals("John", person.name());
    assertEquals(30, person.age());
    assertEquals(1, person.children().size());
    assertEquals("Jane", person.children().getFirst().name());
    assertEquals(10, person.children().getFirst().age());
  }
}
