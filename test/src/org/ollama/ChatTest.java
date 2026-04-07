package org.ollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import io.github.ollama4j.models.chat.OllamaChatMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isNull;
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
        try (MockedStatic<OllamaChatRequest> builder = Mockito.mockStatic(OllamaChatRequest.class)) {
            builder.when(OllamaChatRequest::builder).thenReturn(chatRequest);
        }

        classUnderTest = new Chat(ollama, MODEL);
    }

    @Test
    @DisplayName("It should return an answer to a question")
    void chat() throws Exception {
        when(ollama.chat(any(OllamaChatRequest.class), isNull())).thenReturn(chatResult);
        when(chatResult.getResponseModel()).thenReturn(responseModel);
        when(responseModel.getMessage()).thenReturn(chatMessage);
        when(chatMessage.getResponse()).thenReturn(ANSWER);

        String r = classUnderTest.chat(QUESTION);

        assertEquals(ANSWER, r);
    }
}
