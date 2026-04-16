package org.ollama.client;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatTest {

    private static final String MODEL = "Model";
    private static final String QUESTION = "What is the capital of France?";
    private static final String ANSWER = "The capital of France is **Paris**";

    @Mock
    private Ollama ollama;

    @Mock
    private OllamaChatRequest chatRequest;

    @Mock
    private OllamaChatResult chatResult;

    @Mock
    private OllamaChatResponseModel responseModel;

    @Mock
    private OllamaChatMessage chatMessage;

    private Chat classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = spy(new Chat(ollama, MODEL, true));
    }

    @Test
    @DisplayName("It should throw an exception when given model is null")
    void setModel() {
        assertThrows(IllegalArgumentException.class, () -> classUnderTest.setModel(null));
    }

    @Test
    @DisplayName("It should return an answer to a question")
    void chat() throws Exception {
        doReturn(chatRequest).when(classUnderTest).newBuilder();
        when(chatRequest.withModel(MODEL)).thenReturn(chatRequest);
        when(chatRequest.withUseTools(true)).thenReturn(chatRequest);
        when(chatRequest.withMessage(OllamaChatMessageRole.USER, QUESTION)).thenReturn(chatRequest);
        when(chatRequest.build()).thenReturn(chatRequest);

        when(ollama.chat(eq(chatRequest), isNull())).thenReturn(chatResult);
        when(chatResult.getResponseModel()).thenReturn(responseModel);
        when(responseModel.getMessage()).thenReturn(chatMessage);
        when(chatMessage.getResponse()).thenReturn(ANSWER);

        String r = classUnderTest.chat(QUESTION);

        assertEquals(ANSWER, r);
    }
}
