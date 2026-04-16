package org.ollama.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github.ollama4j.models.response.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;

/**
 * THe panel to deal with models.
 */
final class ModelPanel extends JPanel {

    private static final Logger LOG = LoggerFactory.getLogger(ModelPanel.class);

    private final ModelList remoteList;
    private final ModelList localList;
    private final JButton btnPull;
    private final JButton btnReload;

    ModelPanel() {
        this(null);
    }

    ModelPanel(Optional<String> searchUrl) {
        this.remoteList = new ModelList();
        this.remoteList.setEditable(true);
        this.btnPull = new JButton("⬇️");
        this.btnReload = new JButton("🔄");

        btnPull.setToolTipText("downloads the selected remote model");
        btnReload.setToolTipText("refreshes the list of local models");

        this.localList = new ModelList();

        setBorder(BorderFactory.createTitledBorder("Models"));

        var gridbag = new GridBagLayout();
        var cnt = new GridBagConstraints();
        setLayout(gridbag);

        cnt.fill = GridBagConstraints.HORIZONTAL;

        cnt.gridx = 0;
        cnt.gridy = 0;
        cnt.weightx = 0.5;
        cnt.insets = new Insets(0,5,0,5);
        add(createRemoteLabel(searchUrl), cnt);

        cnt.gridx = 1;
        cnt.gridy = 0;
        cnt.weightx = 1;
        add(remoteList, cnt);

        cnt.gridx = 2;
        cnt.gridy = 0;
        cnt.weightx = 0.1;
        add(btnPull, cnt);

        cnt.gridx = 0;
        cnt.gridy = 1;
        cnt.weightx = 0.5;
        cnt.insets = new Insets(0,5,0,5);
        add(new JLabel("Local:"), cnt);

        cnt.gridx = 1;
        cnt.gridy = 1;
        cnt.weightx = 1;
        add(localList, cnt);

        cnt.gridx = 2;
        cnt.gridy = 1;
        cnt.weightx = 0.1;
        add(btnReload, cnt);
    }

    void addPullActionListener(ActionListener l) {
        btnPull.addActionListener(l);
    }

    void addRefreshActionListener(ActionListener l) {
        btnReload.addActionListener(l);
    }

    void addLocalModelSelectionListener(Consumer<String> listener) {
        localList.addActionListener(e -> {
            String model = getSelectedLocalModel();
            if (!model.isBlank()) {
                listener.accept(model);
            }
        });
    }

    void setRemoteModels(List<Model> models) {
        this.remoteList.setModels(models);
    }

    void setLocalModels(List<Model> models) {
        this.localList.setModels(models);
    }

    String getSelectedLocalModel() {
        return localList.getSelectedModel().map(Model::getName).orElse("");
    }

    String getSelectedRemoteModel() {
        return remoteList.getSelectedModel().map(Model::getName).orElse("");
    }

    private JLabel createRemoteLabel(Optional<String> searchUrl) {
        String url = searchUrl.orElse("");
        if (url.isBlank()) {
            return new JLabel("Remote:");
        }
        
        JLabel label = new JLabel("<html><u>Remote:</u></html>");
        label.setForeground(Color.BLUE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setToolTipText("Click to open " + url);
        
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openBrowser(url);
            }
        });
        
        return label;
    }

    private void openBrowser(String url) {
        try {
            if (isDesktopSupported() && getDesktop().isSupported(Desktop.Action.BROWSE)) {
                getDesktop().browse(new URI(url));
            } else {
                LOG.warn("Desktop browsing not supported");
            }
        } catch (Exception e) {
            LOG.error("Failed to open browser: {}", e.getMessage(), e);
        }
    }
}
