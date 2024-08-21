package com.example.supermarket.controllers;

import com.example.supermarket.components.AdvancedSearchBar;
import com.example.supermarket.components.SortableFilterableTableView;
import com.example.supermarket.models.Address;
import com.example.supermarket.services.AddressService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddressViewController implements Initializable {

    @FXML
    private AdvancedSearchBar addressSearchBar;

    @FXML
    private SortableFilterableTableView<Address> addressesTable;

    private AddressService addressService;
    private ObservableList<Address> addresses;

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupSearchBar();
    }

    private void setupTable() {
        TableColumn<Address, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Address, Integer> numColumn = new TableColumn<>("Number");
        numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));

        TableColumn<Address, String> streetColumn = new TableColumn<>("Street");
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));

        TableColumn<Address, String> cityColumn = new TableColumn<>("City");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        addressesTable.getTableView().getColumns().addAll(idColumn, numColumn, streetColumn, cityColumn);
    }

    private void setupSearchBar() {
        String[] searchCriteria = {"ID", "Street", "City"};
        addressSearchBar.initialize(searchCriteria, this::performSearch, this::refreshAddresses);
    }

    public void refreshAddresses() {
        try {
            addresses = FXCollections.observableArrayList(addressService.getAllAddresses());
            addressesTable.setItems(addresses);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error dialog)
        }
    }

    private void performSearch(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        ObservableList<Address> filteredList = addresses.filtered(address -> {
            switch (criteria) {
                case "ID":
                    return String.valueOf(address.getId()).contains(term);
                case "Street":
                    return address.getStreet().toLowerCase().contains(term);
                case "City":
                    return address.getCity().toLowerCase().contains(term);
                default:
                    return false;
            }
        });

        addressesTable.setItems(filteredList);
    }
}