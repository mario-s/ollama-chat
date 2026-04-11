package org.ollama.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.JTextComponent;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;

import io.github.ollama4j.models.response.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.empty;
import static java.util.Optional.of;

final class ModelList extends JComboBox<Model> implements FocusListener {

    private static final Logger LOG = LoggerFactory.getLogger(ModelList.class);

    private final ModelEditor editor;

    public ModelList() {
        this.editor = new ModelEditor();
        editor.getEditorComponent().addFocusListener(this);

        setEditor(editor);
        setRenderer(new ModelRenderer());
    }

    void setModels(List<Model> models) {
        removeAllItems();
        models.forEach(this::addItem);
    }

    Optional<Model> getSelectedModel() {
        Object item = getSelectedItem();
        return switch(item) {
            case Model model -> of(model);
            default -> empty();
        };
    }

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        String text = editor.getTextComponent().getText();
        if (text.isEmpty()) {
            return;
        }

        if (!hasModel(text)) {
            var m = new Model();
            m.setName(text);
            addItem(m);
            setSelectedItem(m);
        }
    }

    private boolean hasModel(String name) {
        ComboBoxModel<Model> boxModel = getModel();
        for (int i = 0; i < boxModel.getSize(); i++) {
            Model m = boxModel.getElementAt(i);
            if (name.equals(m.getName())) {
                return true;
            }
        }
        return false;
    }
    private static class ModelRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean hasFocus) {

            String name = switch(value) {
                case null -> "";
                case Model model -> model.getName();
                default -> value.toString();
            };

            return super.getListCellRendererComponent(list, name, index, isSelected, hasFocus);
        }
    }

    private static class ModelEditor implements ComboBoxEditor  {

        private Model model;
        private JTextField textComponent;

        ModelEditor() {
            this.textComponent = new JTextField();
        }

        JTextComponent getTextComponent() {
            return textComponent;
        }

        @Override
        public Component getEditorComponent() {
            return textComponent;
        }

        @Override
        public void setItem(Object item) {
            if (item == null) {
                return;
            }
            switch(item) {
                case Model model -> {
                    String name = model.getName();
                    textComponent.setText(name);
                    this.model = model;
                }
                case String name -> {
                    textComponent.setText(name);
                    model = new Model();
                    model.setName(name);
                }
                default -> this.model = new Model();
            };
        }

        @Override
        public Object getItem() {
            return this.model;
        }

        @Override
        public void selectAll() {
            textComponent.selectAll();
        }

        @Override
        public void addActionListener(ActionListener l) {
            textComponent.addActionListener(l);
        }

        @Override
        public void removeActionListener(ActionListener l) {
            textComponent.removeActionListener(l);
        }
    }
}
