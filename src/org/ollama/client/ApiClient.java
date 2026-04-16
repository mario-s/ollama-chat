package org.ollama.client;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.exceptions.OllamaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client to interact with a (local) Ollama server.
 */
final class ApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApiClient.class);

    private final Ollama ollama;
    private final ApiConfig conf;

    private boolean existsTools;

    public ApiClient(ApiConfig conf) {
        this(conf, new Ollama(conf.host()));
    }

    ApiClient(ApiConfig conf, Ollama ollama) {
        this.conf = conf;
        this.ollama = ollama;
        this.ollama.setRequestTimeoutSeconds(conf.timeout());
        this.ollama.setMaxChatToolCallRetries(conf.chatRetries());
        this.ollama.setMetricsEnabled(conf.metrics());

        loadMcpTools();
    }

    private void loadMcpTools() {
        if (isUseTools()) {
            existsTools = conf.getMcp()
                .flatMap(Mcp::getJsonPath)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(jp -> {
                    try {
                        ollama.loadMCPToolsFromJson(jp);
                        return true;
                    } catch (IOException exc) {
                        LOG.warn("unable to load MCP tool", exc);
                        return false;
                    }
                }).orElse(false);
        }
    }

    void pullModel(String name) throws IOException {
        LOG.info("pulling model {}", name);
        try {
            ollama.pullModel(name);
        } catch (OllamaException e) {
            String msg = String.format("%s for %s", e.getMessage(), name);
            throw new IOException(msg);
        }
    }

    /**
     * Get the local available models in an alphabetic sorted order.
     * @return a collection of local models
     */
    List<Model> getModels() {
        List<Model> models = Collections.emptyList();
        try {
            models = ollama.listModels();

            models.sort(Comparator.comparing(
                Model::getName,
                String.CASE_INSENSITIVE_ORDER
            ));
        } catch (OllamaException e) {
            throw new IllegalStateException(e);
        }
        return models;
    }

    Chat createChat(String model) {
        boolean tools = isUseTools() && existsTools;
        return new Chat(ollama, model, tools);
    }

    boolean isUseTools() {
        return conf.getMcp().map(Mcp::use).orElse(false);
    }
}
