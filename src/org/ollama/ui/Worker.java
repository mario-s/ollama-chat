package org.ollama.ui;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private final Frame frame;

    Worker(Frame frame){
        this.frame = frame;
    }

    void pullModel(String name) {
        SwingWorker<Object, Void> worker = new SwingWorker<>() {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    frame.getApiClient().pullModel(name);
                } catch (Exception ex) {
                    LOG.warn(ex.getMessage(), ex);
                    frame.showErrorInChat(ex);
                }
                return new Object();
            }
        };
        worker.execute();
    }

    <T> void execute(Supplier<T> supplier, Consumer<T> consumer) {
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
