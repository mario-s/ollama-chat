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

    Chat createChat(String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("missing model!");
        }
        return new Chat(ollama, model);
    }
}
