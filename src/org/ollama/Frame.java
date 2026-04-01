package org.ollama;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Frame extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Frame.class);

    private final Client client;
    private final ModelList modelList;
    private final ChatPane chatPane;
    private final JTextArea input;
    private final JButton submit;
    private final WaitPanel wait;

    private Chat chat;

    Frame() {
        super("Ollama");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client = new Client();
        modelList = new ModelList();
        chatPane = new ChatPane();
        input = new JTextArea();
        wait = new WaitPanel();
        submit = new JButton();

        buildUi();
        addActions();
    }

    private void buildUi() {
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(new BorderLayout(5,5));

        Border lowered = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        modelList.setBorder(lowered);
        getContentPane().add(modelList, BorderLayout.PAGE_START);

        chatPane.setBorder(lowered);
        getContentPane().add(new JScrollPane(chatPane), BorderLayout.CENTER);

        input.setBorder(lowered);
        JPanel endPanel = new JPanel(new BorderLayout());
        endPanel.add(new JScrollPane(input), BorderLayout.CENTER);
        endPanel.add(submit, BorderLayout.LINE_END);
        getContentPane().add(endPanel, BorderLayout.PAGE_END);

        submit.setMnemonic(KeyEvent.VK_S);

        setGlassPane(wait);
    }

    private void addActions() {
        var action = new AbstractAction("send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                invoke(() -> {
                    lockUi();
                    return ask();
                }, a -> {
                    LOG.debug("got answer from ollama");
                    chatPane.addAnswer(a);
                    unlockUi();
                });
            }
        };
        submit.setAction(action);

        input.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke("control ENTER"), "submit");

        input.getActionMap().put("submit", action);
    }

    void display() {
        setSize(400, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        //starting phrase
        input.setText("what is the capital of france?");

        loadModels();
    }

    private void loadModels() {
        invoke(client::getLocalModels, modelList::setModels);
    }

    private void lockUi() {
        wait.setVisible(true);
    }

    private void unlockUi() {
        wait.setVisible(false);
    }

    private String ask() {
        String q = retrieveQuestion();
        chatPane.addQuestion(q);
        return getChat().chat(q);
    }

    private String retrieveQuestion() {
        String q = input.getText();
        input.setText("");
        return q;
    }

    private Chat getChat() {
        if (chat == null) {
            chat = client.createChat(modelList.getSelectedModelName());
        }
        return chat;
    }

    private <T> void invoke(Supplier<T> supplier, Consumer<T> consumer) {
        CompletableFuture
            .supplyAsync(supplier::get)
            .thenAccept(consumer);
    }
}
