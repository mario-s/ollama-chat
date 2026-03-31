package org.home;

import java.awt.BorderLayout;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ChatFrame extends JFrame{

    private OllamaClient client;
    private ModelList modelList;
    private ChatWindow chat;

    public ChatFrame() {
        super("Ollama");
        this.client = new OllamaClient();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        buildUi();
    }

    private void buildUi() {
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(new BorderLayout(5,5));

        Border lowered = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        modelList = new ModelList();
        modelList.setBorder(lowered);
        getContentPane().add(modelList, BorderLayout.PAGE_START);

        chat = new ChatWindow();
        chat.setBorder(lowered);
        JScrollPane scrollPane = new JScrollPane(chat);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    void display() {
        setSize(400, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        invoke();
    }

    void invoke() {
        loadLocalModels();
        startChat();
    }

    private void loadLocalModels() {
        invoke(client::getLocalModels, modelList::setModelNames);
    }

    private void startChat() {
        chat.setText("what is the capital of france?");
        invoke(() -> {
            String question = chat.getText();
            return client.chat(question);
        }, chat::appendAnswer);
    }

    private <T> void invoke(Supplier<T> supplier, Consumer<T> consumer) {
        CompletableFuture
            .supplyAsync(supplier::get)
            .thenAccept(consumer);
    }
}
