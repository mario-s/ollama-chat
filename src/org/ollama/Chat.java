package org.ollama;

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.exceptions.OllamaException;

final class Chat {

    private final Ollama ollama;

    private OllamaChatRequest builder;
    private OllamaChatResult chatResult;

    Chat(Ollama ollama, String model) {
        this.ollama = ollama;
        builder = OllamaChatRequest.builder().withModel(model);
    }

    String chat(String question) {
        if (question == null || question.isBlank()) {
            return "";
        }

        OllamaChatRequest requestModel = buildRequestModel(question);

        try {
            chatResult = ollama.chat(requestModel, null);
            return chatResult.getResponseModel().getMessage().getResponse();
        } catch (OllamaException e) {
            throw new IllegalStateException(e);
        }
    }

    private OllamaChatRequest buildRequestModel(String question) {
        if (chatResult != null) {
            builder = builder.withMessages(chatResult.getChatHistory());
        }

        return builder.withMessage(OllamaChatMessageRole.USER, question).build();
    }
}
