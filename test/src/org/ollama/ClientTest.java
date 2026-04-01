package org.ollama;

import java.util.ArrayList;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ClientTest {

    @Mock
    private Ollama ollama;

    private Client classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new Client(ollama);
    }

    @Test
    @DisplayName("It should return a ordered list of models")
    void getLocalModels() throws Exception {
        Model m1 = new Model();
        m1.setName("a");
        Model m2 = new Model();
        m2.setName("b");
        when(ollama.listModels()).thenReturn(new ArrayList(List.of(m2, m1)));

        List<Model> models = classUnderTest.getLocalModels();
        assertEquals(models.getFirst(), m1);
    }

    @Test
    @DisplayName("It should create an instance of a Chat based on the model")
    void createChat() {
        Chat chat = classUnderTest.createChat("foo");
        assertTrue(chat instanceof Chat);
    }
}
