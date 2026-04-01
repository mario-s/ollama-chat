package org.ollama;

import java.util.List;

import javax.swing.JList;

final class ModelList extends JList<String> {

    void setModelNames(List<String> names) {
        String [] data = new String[names.size()];
        setListData(names.toArray(data));
    }
}
