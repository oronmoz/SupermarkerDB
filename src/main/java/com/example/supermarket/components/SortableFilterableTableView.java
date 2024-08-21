package com.example.supermarket.components;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.function.Predicate;

public class SortableFilterableTableView<T> extends VBox {
    private TableView<T> tableView;
    private TextField filterField;
    private FilteredList<T> filteredData;

    public SortableFilterableTableView() {
        initialize(FXCollections.observableArrayList());
    }

    public SortableFilterableTableView(ObservableList<T> items) {
        initialize(items);
    }

    private void initialize(ObservableList<T> items) {
        this.tableView = new TableView<>(items);
        this.filterField = new TextField();
        this.filterField.setPromptText("Filter");

        this.filteredData = new FilteredList<>(items, p -> true);

        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createPredicate(newValue));
        });

        this.getChildren().addAll(filterField, tableView);
    }

    public void setItems(ObservableList<T> items) {
        filteredData = new FilteredList<>(items, p -> true);
        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public void setFilterPredicate(Predicate<T> predicate) {
        filteredData.setPredicate(predicate);
    }

    private Predicate<T> createPredicate(String searchText) {
        return item -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return item.toString().toLowerCase().contains(searchText.toLowerCase());
        };
    }
}