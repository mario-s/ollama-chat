package org.ollama.client;

import java.util.List;
import java.util.ArrayList;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {
    private static final String NAME = "foo";

    @Mock
    private Ollama ollama;

    private ApiClient classUnderTest;

    @BeforeEach
    void setUp() {
        ApiConfig conf = ConfigLoader.defaultConfig().apiConfig();
        classUnderTest = new ApiClient(conf, ollama);
    }

    @Test
    @DisplayName("It should not support mcp with the default config")
    void hasMcp() {
        assertFalse(classUnderTest.hasMcp());
    }

    @Test
    @DisplayName("It should allow to pull a model")
    void pullModel() throws Exception {
        classUnderTest.pullModel(NAME);

        verify(ollama).pullModel(NAME);
    }

    @Test
    @DisplayName("It should return a ordered list of models")
    void getModels() throws Exception {
        Model m1 = new Model();
        m1.setName("a");
        Model m2 = new Model();
        m2.setName("b");
        when(ollama.listModels()).thenReturn(new ArrayList(List.of(m2, m1)));

        List<Model> models = classUnderTest.getModels();
        assertEquals(models.getFirst(), m1);
    }

    @Test
    @DisplayName("It should create an instance of a Chat based on the model")
    void createChat() {
        Chat chat = classUnderTest.createChat("foo");
        assertTrue(chat instanceof Chat);
    }
}
