package org.ollama.ui;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github.ollama4j.models.response.Model;

final class ModelPanel extends JPanel {

    private final ModelList remoteList;
    private final ModelList localList;
    private final JButton btnPull;
    private final JButton btnReload;

    ModelPanel() {
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
        add(new JLabel("Remote:"), cnt);

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
}
