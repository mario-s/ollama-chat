package org.ollama.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import io.github.ollama4j.models.response.Model;

/**
 * THis facade is the bridge between the ui and client package.
 */
public final class ClientFacade {

    private final ApiClient apiClient;
    private final SiteClient siteClient;


    public ClientFacade() {
        this(new ConfigLoader());
    }

    ClientFacade(ConfigLoader loader) {
        this(loader.load());
    }

    ClientFacade(Config config){
        this(new ApiClient(config), new SiteClient(config));
    }

    ClientFacade(ApiClient apiClient, SiteClient siteClient) {
        this.apiClient = apiClient;
        this.siteClient = siteClient;
    }

    public Chat createChat(String model) {
        return apiClient.createChat(model);
    }

    public void pullModel(String name) throws IOException {
        apiClient.pullModel(name);
    }

    public List<Model> getRemoteModels() {
        return siteClient.getModels();
    }

    public List<Model> getLocalModels() {
        return apiClient.getModels();
    }
}
