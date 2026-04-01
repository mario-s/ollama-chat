package org.ollama;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

final class ChatPane extends JTextPane{

    private final StyledDocument doc;
    private final Style questionStyle;
    private final Style answerStyle;

    ChatPane() {
        setEditable(false);

        doc = getStyledDocument();

        questionStyle = addStyle("QuestionStyle", null);
        StyleConstants.setBold(questionStyle, true);

        answerStyle = addStyle("AnswerStyle", null);
        StyleConstants.setItalic(answerStyle, true);
    }

    void addQuestion(String question) {
        addText(question, questionStyle);
    }

    void addAnswer(String answer) {
        addText(answer, answerStyle);
    }

    void addText(String text, Style style) {
        var txt = String.format("%s\r\n\r\n", text);
        try {
            doc.insertString(doc.getLength(), txt, style);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }
}
