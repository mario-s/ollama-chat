package org.ollama.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;

import io.github.ollama4j.models.response.Model;

import static java.util.Optional.empty;
import static java.util.Optional.of;

final class ModelList extends JComboBox<Model> {

    public ModelList() {
        setRenderer(new ModelRenderer());
        setEditor(new ModelEditor());
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

        private JTextField editor;

        ModelEditor() {
            this.editor = new JTextField();
        }

        @Override
        public Component getEditorComponent() {
            return editor;
        }

        @Override
        public void setItem(Object value) {
            if (value instanceof Model m) {
                editor.setText(m.getName());
            }
        }

        @Override
        public Object getItem() {
            return editor.getText();
        }

        @Override
        public void selectAll() {
            editor.selectAll();
        }

        @Override
        public void addActionListener(ActionListener l) {
            editor.addActionListener(l);
        }

        @Override
        public void removeActionListener(ActionListener l) {
            editor.removeActionListener(l);
        }
    }
}
