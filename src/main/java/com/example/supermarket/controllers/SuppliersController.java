package com.example.supermarket.controllers;

import com.example.supermarket.components.AdvancedSearchBar;
import com.example.supermarket.components.SortableFilterableTableView;
import com.example.supermarket.components.ReusableFormLayout;
import com.example.supermarket.models.Supplier;
import com.example.supermarket.services.SupplierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SuppliersController implements Initializable {

    @FXML private AdvancedSearchBar supplierSearchBar;
    @FXML private SortableFilterableTableView<Supplier> suppliersTable;
    @FXML private ReusableFormLayout supplierForm;

    private SupplierService supplierService;
    private ObservableList<Supplier> suppliers;

    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSupplierSearchBar();
        setupSupplierTable();
        setupSupplierForm();
    }

    public void loadSuppliers() {
        try {
            suppliers = FXCollections.observableArrayList(supplierService.getAllSuppliers());
            suppliersTable.setItems(suppliers);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load suppliers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSupplierSearchBar() {
        String[] searchCriteria = {"Name", "Contact Info"};
        supplierSearchBar.initialize(searchCriteria, this::performSupplierSearch, this::refreshSuppliers);
    }

    public void refreshSuppliers() {
        try {
            suppliers = FXCollections.observableArrayList(supplierService.getAllSuppliers());
            suppliersTable.setItems(suppliers);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error dialog)
        }
    }

    private void setupSupplierTable() {
        TableColumn<Supplier, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Supplier, String> contactInfoColumn = new TableColumn<>("Contact Info");
        contactInfoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactInfo()));

        suppliersTable.getTableView().getColumns().addAll(nameColumn, contactInfoColumn);
    }

    private void setupSupplierForm() {
        supplierForm.addField("Name", "Enter supplier name");
        supplierForm.addField("Contact Info", "Enter contact information");
    }

    @FXML
    private void addSupplier() {
        supplierForm.clearFields();
        showDialog("Add Supplier", supplierForm, this::saveSupplier);
    }

    @FXML
    private void editSupplier() {
        Supplier selectedSupplier = suppliersTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            populateSupplierForm(selectedSupplier);
            showDialog("Edit Supplier", supplierForm, this::saveSupplier);
        } else {
            showAlert("No Selection", "Please select a supplier to edit.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void deleteSupplier() {
        Supplier selectedSupplier = suppliersTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            try {
                supplierService.deleteSupplier(selectedSupplier.getSupplierId());
                suppliers.remove(selectedSupplier);
                showAlert("Success", "Supplier deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete supplier: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a supplier to delete.", Alert.AlertType.WARNING);
        }
    }

    private void saveSupplier() {
        try {
            Supplier supplier = extractSupplierFromForm();
            if (supplier.getSupplierId() == 0) {
                supplierService.createSupplier(supplier);
            } else {
                supplierService.updateSupplier(supplier);
            }
            loadSuppliers();
            showAlert("Success", "Supplier saved successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Failed to save supplier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void performSupplierSearch(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        suppliersTable.setFilterPredicate(supplier -> {
            switch (criteria) {
                case "Name":
                    return supplier.getName().toLowerCase().contains(term);
                case "Contact Info":
                    return supplier.getContactInfo().toLowerCase().contains(term);
                default:
                    return false;
            }
        });
    }

    private void populateSupplierForm(Supplier supplier) {
        supplierForm.setFieldValue("Name", supplier.getName());
        supplierForm.setFieldValue("Contact Info", supplier.getContactInfo());
    }

    private Supplier extractSupplierFromForm() {
        Supplier supplier = new Supplier();
        supplier.setName(supplierForm.getFieldValue("Name"));
        supplier.setContactInfo(supplierForm.getFieldValue("Contact Info"));
        return supplier;
    }

    private void showDialog(String title, ReusableFormLayout form, Runnable onSave) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(form);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                onSave.run();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}