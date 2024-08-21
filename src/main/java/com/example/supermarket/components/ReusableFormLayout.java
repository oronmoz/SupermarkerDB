package com.example.supermarket.components;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.HashMap;
import java.util.Map;

public class ReusableFormLayout extends GridPane {
    private Map<String, Node> fields;

    public ReusableFormLayout() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));

        fields = new HashMap<>();
    }

    public void addField(String labelText, String promptText) {
        int rowIndex = this.getRowCount();

        Label label = new Label(labelText);
        TextField textField = new TextField();
        textField.setPromptText(promptText);

        this.add(label, 0, rowIndex);
        this.add(textField, 1, rowIndex);

        fields.put(labelText, textField);
    }

    public String getFieldValue(String fieldName) {
        TextField field = (TextField) fields.get(fieldName);
        return field != null ? field.getText() : null;
    }

    public void setFieldValue(String fieldName, String value) {
        TextField field = (TextField) fields.get(fieldName);
        if (field != null) {
            field.setText(value);
        }
    }

    public void clearFields() {
        fields.values().forEach(node -> {
            if (node instanceof TextField) {
                ((TextField) node).clear();
            }
        });
    }

    public void addCustomField(String label, Node field) {
        int rowIndex = this.getRowCount();

        Label labelNode = new Label(label);
        this.add(labelNode, 0, rowIndex);
        this.add(field, 1, rowIndex);

        fields.put(label, field);
    }

    public Node getField(String fieldName) {
        return fields.get(fieldName);
    }
}