package org.ollama.ui;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

import org.ollama.client.ClientFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Frame extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Frame.class);

    private final ModelPanel modelPanel;
    private final ChatPane chatPane;
    private final InputArea input;
    private final JButton submit;
    private final WaitPanel wait;
    private final ClientFacade facade;

    public Frame() {
        super("Ollama Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        facade = new ClientFacade();

        modelPanel = new ModelPanel(facade.getSearchUrl());
        chatPane = new ChatPane();
        input = new InputArea("What do you have in mind?");
        submit = new JButton();
        wait = new WaitPanel();

        buildUi();
        addActions();
    }

    private void buildUi() {
        Border lowered = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        var gridbag = new GridBagLayout();
        var cnt = new GridBagConstraints();

        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(gridbag);

        cnt.gridx = 0;
        cnt.gridy = 0;
        cnt.gridwidth = 3;
        cnt.weightx = 1;
        cnt.weighty = 0;
        cnt.fill = GridBagConstraints.HORIZONTAL;
        cnt.insets = new Insets(0,0, 5,0);
        getContentPane().add(modelPanel, cnt);

        cnt.gridx = 0;
        cnt.gridy = 1;
        cnt.gridwidth = 3;
        cnt.weightx = 1;
        cnt.weighty = 1;
        cnt.fill = GridBagConstraints.BOTH;
        cnt.ipady = 0;

        getContentPane().add(new JScrollPane(chatPane), cnt);
        chatPane.setBorder(lowered);

        cnt.gridx = 0;
        cnt.gridy = 2;
        cnt.gridwidth = 2;
        cnt.weightx = 1;
        cnt.weighty = 0;
        cnt.fill = GridBagConstraints.HORIZONTAL;
        cnt.ipady = 100;
        cnt.insets = new Insets(10,0,0,10);
        getContentPane().add(new JScrollPane(input), cnt);
        input.setBorder(lowered);

        cnt.gridx = 2;
        cnt.gridy = 2;
        cnt.gridwidth = 1;
        cnt.weightx = 0;
        cnt.weighty = 0;
        cnt.fill = GridBagConstraints.NONE;
        cnt.ipady = 0;
        cnt.anchor = GridBagConstraints.NORTH;
        cnt.insets = new Insets(10,0,0,0);
        getContentPane().add(submit, cnt);

        setGlassPane(wait);
    }

    private void addActions() {

        var action = new AbstractAction("▶️") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasNoInput()) {
                    return;
                }
                execute(() -> ask(), a -> {
                    chatPane.addAnswer(a);
                    input.requestFocus();
                });
            }
        };
        submit.setAction(action);
        submit.setToolTipText("sends input to the local model");

        input.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke("control ENTER"), "submit");
        input.getActionMap().put("submit", action);

        modelPanel.addPullActionListener(e -> {
            SwingWorker<Object, Void> worker = new SwingWorker<>() {

                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        String name = modelPanel.getSelectedRemoteModel();
                        facade.pullModel(name);
                    } catch (Exception ex) {
                        LOG.warn(ex.getMessage(), ex);
                        showError(ex);
                    }
                    return new Object();
                }
            };
            worker.execute();
        });
        modelPanel.addRefreshActionListener(e -> loadLocalModels());
        modelPanel.addLocalModelSelectionListener(m -> facade.setChatModel(m));
    }

    public void display() {
        setSize(600, 620);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);

        loadModels();
        input.requestFocus();
    }

    private void loadModels() {
        execute(facade::getRemoteModels, modelPanel::setRemoteModels);
        loadLocalModels();
    }

    private void loadLocalModels() {
        execute(facade::getLocalModels, modelPanel::setLocalModels);
    }

    private boolean hasNoInput() {
        return input.getText().isBlank();
    }

    private String ask() {
        String question = retrieveQuestion();
        chatPane.addQuestion(question);
        return facade.chat(question);
    }

    private String retrieveQuestion() {
        String q = input.getText();
        input.setText("");
        return q;
    }

    void showError(Throwable ex) {
        SwingUtilities.invokeLater(() -> {
            String msg = ExceptionUtil.getCauseMessage(ex);
            chatPane.addError(msg);
        });
    }

    private <T> void execute(Supplier<T> supplier, Consumer<T> consumer) {
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
                    showError(ex);
                    LOG.error(ex.getMessage(), ex);
                } finally {
                    lock(false);
                }
            }
        };

        lock(true);
        worker.execute();
        scheduleInterrupt(worker);
    }

    private <T> void scheduleInterrupt(SwingWorker<T, Void> worker) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(() -> {
            if (!worker.isDone()) {
                worker.cancel(true);
                lock(false);
                LOG.info("Task ran into timeout");
            }
        }, 60, TimeUnit.SECONDS);
    }

    private void lock(boolean l) {
        SwingUtilities.invokeLater(() -> wait.setVisible(l));
    }
}
