package edu.austral.dissis.todos.fall.uuid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UuidTest {

  @Test
  public void shouldGenerateUuid() {
    var uuid = Uuid.generate();
    Assertions.assertEquals(36, uuid.length());
  }
}
