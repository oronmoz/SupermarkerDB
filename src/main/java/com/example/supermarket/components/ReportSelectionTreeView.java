package com.example.supermarket.components;

import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeCell;

import java.util.function.Consumer;


public class ReportSelectionTreeView extends TreeView<String> {

    public ReportSelectionTreeView() {
        // default constructor
    }

    public ReportSelectionTreeView(Consumer<String> onReportSelected) {
        TreeItem<String> root = new TreeItem<>("Reports");
        root.setExpanded(true);

        TreeItem<String> salesReports = new TreeItem<>("Sales Reports");
        salesReports.getChildren().addAll(
                new TreeItem<>("Daily Sales"),
                new TreeItem<>("Monthly Sales"),
                new TreeItem<>("Annual Sales")
        );

        TreeItem<String> inventoryReports = new TreeItem<>("Inventory Reports");
        inventoryReports.getChildren().addAll(
                new TreeItem<>("Current Stock"),
                new TreeItem<>("Low Stock Alert")
        );

        TreeItem<String> customerReports = new TreeItem<>("Customer Reports");
        customerReports.getChildren().addAll(
                new TreeItem<>("Top Customers"),
                new TreeItem<>("Customer Spending Analysis")
        );

        TreeItem<String> supplierReports = new TreeItem<>("Supplier Reports");
        supplierReports.getChildren().add(
                new TreeItem<>("Supplier Product Count")
        );

        root.getChildren().addAll(salesReports, inventoryReports, customerReports, supplierReports);

        this.setRoot(root);
        this.setShowRoot(false);

        this.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isLeaf()) {
                onReportSelected.accept(newValue.getValue());
            }
        });
    }
}