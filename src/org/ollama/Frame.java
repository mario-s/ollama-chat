package org.ollama;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
    private final InputArea input;
    private final JButton submit;
    private final WaitPanel wait;

    private Chat chat;

    Frame() {
        super("Ollama Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client = new Client();
        modelList = new ModelList();
        chatPane = new ChatPane();
        input = new InputArea("What is in your mind?");
        wait = new WaitPanel();
        submit = new JButton();

        buildUi();
        addActions();
    }

    private void buildUi() {
        Border lowered = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        var gridbag = new GridBagLayout();
        var cnt = new GridBagConstraints();

        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(gridbag);

        cnt.fill = GridBagConstraints.HORIZONTAL;
        cnt.gridx = 0;
        cnt.gridy = 0;
        cnt.gridwidth = 3;
        cnt.insets = new Insets(0,0, 5,0);
        getContentPane().add(createModelPanel(), cnt);

        cnt.gridx = 0;
        cnt.gridy = 1;
        cnt.ipady = 300;
        cnt.weightx = 0.5;

        getContentPane().add(new JScrollPane(chatPane), cnt);
        chatPane.setBorder(lowered);

        cnt.gridwidth = 3;
        cnt.gridx = 0;
        cnt.gridy = 2;
        cnt.ipady = 100;
        getContentPane().add(new JScrollPane(input), cnt);
        input.setBorder(lowered);

        cnt.gridwidth = 1;
        cnt.gridx = 1;
        cnt.gridy = 3;
        cnt.ipady = 0;
        cnt.anchor = GridBagConstraints.PAGE_END;
        cnt.insets = new Insets(10,50,0,50);
        getContentPane().add(submit, cnt);

        setGlassPane(wait);
    }

    private JComponent createModelPanel() {
        var gridbag = new GridBagLayout();
        var cnt = new GridBagConstraints();
        var panel = new JPanel(gridbag);
        panel.setBorder(BorderFactory.createTitledBorder("Models"));

        cnt.fill = GridBagConstraints.HORIZONTAL;
        cnt.gridx = 0;
        cnt.gridy = 0;
        cnt.weightx = 0.5;
        cnt.insets = new Insets(0,5,0,5);
        panel.add(new JLabel("Local:"), cnt);

        cnt.gridx = 1;
        cnt.gridy = 0;
        cnt.weightx = 1;
        panel.add(modelList, cnt);

        return panel;
    }

    private void addActions() {
        var action = new AbstractAction("send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasNoInput()) {
                    return;
                }
                invoke(() -> ask(), a -> {
                    chatPane.addAnswer(a);
                    input.requestFocus();
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

        loadModels();
    }

    private void loadModels() {
        invoke(client::getLocalModels, modelList::setModels);
    }

    private boolean hasNoInput() {
        return input.getText().isBlank();
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
                    LOG.info(ex.getMessage());
                    chatPane.addError(Utils.getCause(ex).getMessage());
                } finally {
                    wait.unlock();
                }
            }
        };

        wait.lock();
        worker.execute();
        scheduler.schedule(() -> {
            if (!worker.isDone()) {
                worker.cancel(true);
                wait.unlock();
                LOG.info("Task ran into timeout");
            }
        }, 30, TimeUnit.SECONDS);
    }
}
