package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.todos.fall.json.JsonParser;
import org.junit.jupiter.api.Test;

public class ExampleBodyTest {

  @Test
  public void shouldCreateExampleBodyCorrectly() {
    String testMessage = "This is a test message";
    ExampleBody exampleBody = new ExampleBody(testMessage);

    assertEquals(testMessage, exampleBody.message());
  }

  @Test
  public void shouldSerializeToJson() {
    String testMessage = "Test message for serialization";
    ExampleBody exampleBody = new ExampleBody(testMessage);

    String json = JsonParser.toJson(exampleBody);

    assertTrue(json.contains("\"message\""));
    assertTrue(json.contains(testMessage));
  }

  @Test
  public void shouldDeserializeFromJson() {
    String testMessage = "Test message for deserialization";
    String json = "{\"message\":\"" + testMessage + "\"}";

    ExampleBody exampleBody = JsonParser.fromJson(json, ExampleBody.class);

    assertEquals(testMessage, exampleBody.message());
  }

  @Test
  public void shouldImplementEqualsAndHashCode() {
    ExampleBody body1 = new ExampleBody("Same message");
    ExampleBody body2 = new ExampleBody("Same message");
    ExampleBody body3 = new ExampleBody("Different message");

    assertEquals(body1, body2);
    assertNotEquals(body1, body3);

    assertEquals(body1.hashCode(), body2.hashCode());
  }

  @Test
  public void shouldImplementToString() {
    String testMessage = "Test for toString";
    ExampleBody body = new ExampleBody(testMessage);

    String toString = body.toString();

    assertTrue(toString.contains("ExampleBody"));
    assertTrue(toString.contains(testMessage));
  }
}
