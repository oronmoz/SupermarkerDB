package com.example.supermarket.managers;

import com.example.supermarket.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class SupermarketDialogCreator {
    public static void showAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(title);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public static void showCartDialog(String customerName, ShoppingCart cart) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Shopping Cart");
        dialog.setHeaderText("Shopping Cart for " + customerName);

        TableView<ShoppingItem> table = new TableView<>();
        ObservableList<ShoppingItem> data = FXCollections.observableArrayList(cart.getItems());

        TableColumn<ShoppingItem, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<ShoppingItem, Float> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<ShoppingItem, Integer> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        table.getColumns().addAll(barcodeCol, priceCol, countCol);
        table.setItems(data);

        VBox vbox = new VBox(table);
        vbox.getChildren().add(new Label("Total: $" + String.format("%.2f", cart.getTotalPrice())));
        dialog.getDialogPane().setContent(vbox);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
    }

    public static void showCustomerSupermarketsDialog(Customer customer, List<Supermarket> supermarkets) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Customer's Supermarkets");
        dialog.setHeaderText("Supermarkets for " + customer.getName());

        TableView<Supermarket> table = new TableView<>();
        ObservableList<Supermarket> data = FXCollections.observableArrayList(supermarkets);

        TableColumn<Supermarket, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supermarket, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        table.getColumns().addAll(nameCol, addressCol);
        table.setItems(data);

        VBox vbox = new VBox(table);
        dialog.getDialogPane().setContent(vbox);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
    }

    public static void showSupermarketCustomersDialog(Supermarket supermarket, List<Customer> customers) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supermarket's Customers");
        dialog.setHeaderText("Customers of " + supermarket.getName());

        TableView<Customer> table = new TableView<>();
        ObservableList<Customer> data = FXCollections.observableArrayList(customers);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameCol);
        table.setItems(data);

        VBox vbox = new VBox(table);
        dialog.getDialogPane().setContent(vbox);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
    }
}