package org.ollama;

import io.github.ollama4j.Ollama;
import java.util.List;

import javax.swing.SwingUtilities;


public class Main {
    void main() throws Exception {
        SwingUtilities.invokeLater(() -> {
            ChatFrame f = new ChatFrame();
            f.display();
        });
    }
}