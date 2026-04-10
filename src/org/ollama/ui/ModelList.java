package org.ollama.ui;

import java.awt.Component;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;

import io.github.ollama4j.models.response.Model;

import static java.util.Optional.empty;
import static java.util.Optional.of;

final class ModelList extends JComboBox<Model> {

    public ModelList() {
        setRenderer(new ModelRenderer());
    }

    void setModels(List<Model> models) {
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

}
