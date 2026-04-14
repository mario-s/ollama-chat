package org.ollama.client;

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.exceptions.OllamaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the user's conversation with the model.
 */
public final class Chat {

    private static final Logger LOG = LoggerFactory.getLogger(Chat.class);

    private final Ollama ollama;

    private String model;

    private OllamaChatResult chatResult;

    Chat(Ollama ollama, String model) {
        this.ollama = ollama;
        setModel(model);
    }

    public void setModel(String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("missing model");
        }
        this.model = model;
    }

    public String chat(String question) {
        if (question == null || question.isBlank()) {
            return "";
        }

        OllamaChatRequest request = buildRequest(question);

        try {
            chatResult = ollama.chat(request, null);
            return chatResult.getResponseModel().getMessage().getResponse();
        } catch (OllamaException e) {
            throw new IllegalStateException(e);
        }
    }

    private OllamaChatRequest buildRequest(String question) {
        OllamaChatRequest builder = OllamaChatRequest.builder().withModel(model);
        if (chatResult != null) {
            LOG.trace("using chat history");
            builder.withMessages(chatResult.getChatHistory());
        }

        return builder.withMessage(OllamaChatMessageRole.USER, question).build();
    }
}
