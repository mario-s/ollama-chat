package org.ollama;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    @DisplayName("It should return the same exception if there is no other cause.")
    void getCause() {
        var ex = new RuntimeException("test");

        Throwable result = Utils.getCause(ex);
        assertEquals(ex, result);
    }

    @Test
    @DisplayName("It should return the second exception if there is another cause.")
    void getCause_Pair() {
        var first = new RuntimeException("test");
        var ex = new RuntimeException(first);

        Throwable result = Utils.getCause(ex);
        assertEquals(first, result);
    }

    @Test
    @DisplayName("It should return the last exception if it is a chain.")
    void getCause_Chain() {
        var first = new RuntimeException("test");
        var second = new RuntimeException(first);
        var ex = new RuntimeException(second);

        Throwable result = Utils.getCause(ex);
        assertEquals(first, result);
    }
}
