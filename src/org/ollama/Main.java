package org.ollama;

import io.github.ollama4j.Ollama;
import java.util.List;

import javax.swing.SwingUtilities;

import org.ollama.ui.Frame;


public class Main {
    void main() throws Exception {
        SwingUtilities.invokeLater(() -> {
            Frame f = new Frame();
            f.display();
        });
    }
}