package org.ollama;

import javax.swing.JTextArea;

class ChatWindow extends JTextArea{

    static final String NL = "\r\n";

    void appendAnswer(String answer) {
        append(NL);
        append(NL);

        append(answer);
    }
}
