package org.ollama.client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import io.github.ollama4j.models.response.Model;

import org.ollama.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This facade is the bridge between the ui and client package.
 */
public final class ClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ClientFacade.class);

    private final ApiClient apiClient;
    private final SiteClient siteClient;
    private final Config config;

    private Chat chat;

    public ClientFacade(Config config){
        this(config, new ApiClient(config.apiConfig()), new SiteClient(config.tags()));
    }

    ClientFacade(Config config, ApiClient apiClient, SiteClient siteClient) {
        this.apiClient = apiClient;
        this.siteClient = siteClient;
        this.config = config;
    }

    /**
     * Retrieves the list of available models from the remote Ollama repository.
     *
     * @return list of remote models available for download
     */
    public List<Model> getRemoteModels() {
        return siteClient.getModels();
    }

    /**
     * Retrieves the list of models installed locally on the Ollama server.
     *
     * @return list of locally installed models, sorted alphabetically by name
     */
    public List<Model> getLocalModels() {
        return apiClient.getModels();
    }

    /**
     * Downloads and installs a model from the remote Ollama repository.
     *
     * @param name the name of the model to download
     * @throws IOException if the download fails or the model cannot be found
     */
    public void pullModel(String name) throws IOException {
        apiClient.pullModel(name);
    }

    /**
     * Sets the model that the user will chat with.
     * This method must be called before using the {@link #chat(String)} method.
     *
     * @param model the name of the model to use for chat interactions
     * @throws IllegalArgumentException if model is null or blank
     */
    public void setChatModel(String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or blank");
        }
        LOG.debug("setting selected model to {}", model);
        chat = getChat(model);
    }

    /**
     * Sends a question to the currently selected model and returns the response.
     *
     * @param question the user's question or message
     * @return the model's response
     * @throws IllegalStateException if no model has been selected via {@link #setChatModel(String)}
     */
    public String chat(String question) {
        if (chat == null) {
            throw new IllegalStateException("No model selected");
        }
        return chat.chat(question);
    }

    Chat getChat(String model) {
        LOG.debug("using model {} for chat", model);
        if (chat == null) {
            chat = apiClient.createChat(model);
        } else {
            chat.setModel(model);
        }
        return chat;
    }
}
