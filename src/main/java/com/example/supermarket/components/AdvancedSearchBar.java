package com.example.supermarket.components;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.control.Tooltip;

import java.util.function.Consumer;

public class AdvancedSearchBar extends HBox {
    private TextField searchField;
    private ComboBox<String> searchCriteria;
    private Button searchButton;
    private Button refreshButton;

    public AdvancedSearchBar() {
        // default constructor for FXML
    }

    public AdvancedSearchBar(String[] criteria, Consumer<String> onSearch, Runnable onRefresh) {
        initialize(criteria, onSearch, onRefresh);
    }

    public void initialize(String[] criteria, Consumer<String> onSearch, Runnable onRefresh) {
        // clear existing children if any
        this.getChildren().clear();

        searchField = new TextField();
        searchField.setPromptText("Enter search term");

        searchCriteria = new ComboBox<>(FXCollections.observableArrayList(criteria));
        searchCriteria.setPromptText("Select criteria");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText();
            String selectedCriteria = searchCriteria.getValue();
            if (selectedCriteria != null && !searchTerm.isEmpty()) {
                onSearch.accept(selectedCriteria + ":" + searchTerm);
            }
        });

        refreshButton = new Button("↻"); // Unicode character for refresh
        refreshButton.setTooltip(new Tooltip("Refresh search results"));
        refreshButton.setOnAction(e -> {
            searchField.clear();
            searchCriteria.getSelectionModel().clearSelection();
            onRefresh.run();
        });

        this.setSpacing(10);
        this.getChildren().addAll(searchField, searchCriteria, searchButton, refreshButton);
    }

    public String getSearchTerm() {
        return searchField.getText();
    }

    public String getSelectedCriteria() {
        return searchCriteria.getValue();
    }

    public void clearSearch() {
        searchField.clear();
        searchCriteria.getSelectionModel().clearSelection();
    }
}