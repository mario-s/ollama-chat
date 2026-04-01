package org.ollama;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;

import io.github.ollama4j.models.response.Model;

final class ModelList extends JComboBox<Model> {

    public ModelList() {
        setRenderer(new ModelRenderer());
    }

    void setModels(List<Model> models) {
        models.forEach(this::addItem);
    }

    private static class ModelRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean hasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

            if (value instanceof Model model) {
                setText(model.getName());
            }

            return this;
        }
    }
}
