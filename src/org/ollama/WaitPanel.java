package org.ollama;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

final class WaitPanel extends JPanel {

    WaitPanel() {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    void lock() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    void unlock() {
        SwingUtilities.invokeLater(() -> setVisible(false));
    }
}
