package org.ollama.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.ollama4j.models.response.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * THis facade is the bridge between the ui and client package.
 */
public final class ClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ClientFacade.class);

    private final ApiClient apiClient;
    private final SiteClient siteClient;

    private Chat chat;

    public ClientFacade() {
        this(new ConfigLoader());
    }

    ClientFacade(ConfigLoader loader) {
        this(loader.load());
    }

    ClientFacade(Config config){
        this(new ApiClient(config.apiConfig()), new SiteClient(config.tags()));
    }

    ClientFacade(ApiClient apiClient, SiteClient siteClient) {
        this.apiClient = apiClient;
        this.siteClient = siteClient;
    }

    public List<Model> getRemoteModels() {
        return siteClient.getModels();
    }

    public List<Model> getLocalModels() {
        return apiClient.getModels();
    }

    public void pullModel(String name) throws IOException {
        apiClient.pullModel(name);
    }

    public String chat(String model, String question) {
        return getChat(model).chat(question);
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
