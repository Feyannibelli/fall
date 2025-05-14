package edu.austral.dissis.todos.fall.uuid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UUIDTest {

    @Test
    public void shouldGenerateUUID() {
        var uuid = UUID.generate();
        Assertions.assertEquals(36, uuid.length());
    }

}
