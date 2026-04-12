package org.ollama.ui;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import io.github.ollama4j.models.response.Model;

import org.ollama.client.ApiClient;
import org.ollama.client.Chat;
import org.ollama.client.SiteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ApiFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ApiFacade.class);

    private final Frame frame;
    private final ApiClient apiClient;
    private final SiteClient siteClient;

    ApiFacade(Frame frame){
        this.frame = frame;

        apiClient = new ApiClient();
        siteClient = new SiteClient();
    }

    Chat createChat(String model) {
        return apiClient.createChat(model);
    }

    void pullModel(String name) {
        SwingWorker<Object, Void> worker = new SwingWorker<>() {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    apiClient.pullModel(name);
                } catch (Exception ex) {
                    LOG.warn(ex.getMessage(), ex);
                    frame.showErrorInChat(ex);
                }
                return new Object();
            }
        };
        worker.execute();
    }

    void loadRemoteModels(Consumer<List<Model>> consumer) {
        execute(siteClient::getRemoteModels, l -> consumer.accept(l));
    }

    void loadLocalModels(Consumer<List<Model>> consumer) {
        execute(apiClient::getLocalModels, l -> consumer.accept(l));
    }

    private <T> void execute(Supplier<T> supplier, Consumer<T> consumer) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        SwingWorker<T, Void> worker = new SwingWorker<>() {

            @Override
            protected T doInBackground() throws Exception {
                return supplier.get();
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        consumer.accept(get());
                    }
                } catch (Exception ex) {
                    LOG.warn(ex.getMessage(), ex);
                    frame.showErrorInChat(ex);
                } finally {
                    frame.lock(false);
                }
            }
        };

        frame.lock(true);
        worker.execute();
        scheduler.schedule(() -> {
            if (!worker.isDone()) {
                worker.cancel(true);
                frame.lock(false);
                LOG.info("Task ran into timeout");
            }
        }, 60, TimeUnit.SECONDS);
    }
}
