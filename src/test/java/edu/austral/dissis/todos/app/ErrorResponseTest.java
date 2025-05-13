package edu.austral.dissis.todos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.todos.fall.json.JsonParser;
import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

  @Test
  public void shouldCreateErrorResponseCorrectly() {
    String errorMessage = "Test error message";
    ErrorResponse errorResponse = new ErrorResponse(errorMessage);

    assertEquals(errorMessage, errorResponse.message());
  }

  @Test
  public void shouldSerializeToJson() {
    String errorMessage = "Serialization test error";
    ErrorResponse errorResponse = new ErrorResponse(errorMessage);

    String json = JsonParser.toJson(errorResponse);

    assertTrue(json.contains("\"message\""));
    assertTrue(json.contains(errorMessage));
  }

  @Test
  public void shouldDeserializeFromJson() {
    String errorMessage = "Deserialization test error";
    String json = "{\"message\":\"" + errorMessage + "\"}";

    ErrorResponse errorResponse = JsonParser.fromJson(json, ErrorResponse.class);

    assertEquals(errorMessage, errorResponse.message());
  }

  @Test
  public void shouldImplementEqualsAndHashCode() {
    ErrorResponse error1 = new ErrorResponse("Same error");
    ErrorResponse error2 = new ErrorResponse("Same error");
    ErrorResponse error3 = new ErrorResponse("Different error");

    assertEquals(error1, error2);
    assertNotEquals(error1, error3);

    assertEquals(error1.hashCode(), error2.hashCode());
  }

  @Test
  public void shouldImplementToString() {
    String errorMessage = "ToString test error";
    ErrorResponse errorResponse = new ErrorResponse(errorMessage);

    String toString = errorResponse.toString();

    assertTrue(toString.contains("ErrorResponse"));
    assertTrue(toString.contains(errorMessage));
  }
}
