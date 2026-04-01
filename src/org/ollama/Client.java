package org.ollama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.exceptions.OllamaException;

class Client {

    private final Ollama ollama;

    Client() {
        this(new Ollama());
    }

    Client(Ollama ollama) {
        this.ollama = ollama;
    }

    List<String> getLocalModels() {
        List<String> models = Collections.emptyList();
        try {
            models = this.ollama.listModels().stream().map(Model::getName).toList();
        } catch (OllamaException e) {
            throw new IllegalStateException(e);
        }
        return models;
    }

    String chat(String question) {
        if (question == null || question.isBlank()) {
            return "";
        }

        String model = "gemma3:latest";
        OllamaChatRequest builder = OllamaChatRequest.builder().withModel(model);

        OllamaChatRequest requestModel =
                builder.withMessage(OllamaChatMessageRole.USER, question).build();

        try {
            OllamaChatResult chatResult = ollama.chat(requestModel, null);
            return chatResult.getResponseModel().getMessage().getResponse();
        } catch (OllamaException e) {
            throw new IllegalStateException(e);
        }
    }
}
