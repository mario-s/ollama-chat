package org.ollama;

import java.awt.BorderLayout;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

final class ChatFrame extends JFrame{

    private final Client client;
    private final ModelList modelList;
    private final ChatWindow chat;
    private final JTextArea input;
    private final WaitPanel wait;

    ChatFrame() {
        super("Ollama");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client = new Client();
        modelList = new ModelList();
        chat = new ChatWindow();
        input = new JTextArea();
        wait = new WaitPanel();

        buildUi();
    }

    private void buildUi() {
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(new BorderLayout(5,5));

        Border lowered = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        modelList.setBorder(lowered);
        getContentPane().add(modelList, BorderLayout.PAGE_START);

        chat.setBorder(lowered);
        getContentPane().add(new JScrollPane(chat), BorderLayout.CENTER);

        input.setBorder(lowered);
        getContentPane().add(new JScrollPane(input), BorderLayout.PAGE_END);

        setGlassPane(wait);
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
        input.setText("what is the capital of france?");
        invoke(() -> {
            lockUi();
            String q = retrieveQuestion();
            chat.addQuestion(q);
            return client.chat(q);
        }, a -> {
            chat.addAnswer(a);
            unlockUi();
        });
    }

    private void lockUi() {
        wait.setVisible(true);
    }

    private void unlockUi() {
        wait.setVisible(false);
    }

    private String retrieveQuestion() {
        String q = input.getText();
        input.setText("");
        return q;
    }

    private <T> void invoke(Supplier<T> supplier, Consumer<T> consumer) {
        CompletableFuture
            .supplyAsync(supplier::get)
            .thenAccept(consumer);
    }
}
