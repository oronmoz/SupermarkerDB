package com.example.supermarket.managers;

import com.example.supermarket.services.CustomerService;
import com.example.supermarket.services.ProductService;
import com.example.supermarket.services.SupermarketService;
import com.example.supermarket.services.impl.FileOperationsServiceImpl;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SupermarketFileHandler {
    private SupermarketService supermarketService;
    private ProductService productService;
    private CustomerService customerService;
    private FileOperationsServiceImpl fileOperationsService;
    private Stage primaryStage;

    public SupermarketFileHandler(SupermarketService supermarketService, ProductService productService,
                                  CustomerService customerService, Stage primaryStage) {
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.fileOperationsService = new FileOperationsServiceImpl(supermarketService, productService, customerService);
        this.primaryStage = primaryStage;
    }

    public void exportProductsToCSV() throws SQLException {
        fileOperationsService.exportToCSV(productService.getAllProducts(), "products.csv", primaryStage);
    }

    public void importProductsFromCSV() {
        fileOperationsService.importFromCSV("Product", primaryStage);
    }

    public void exportCustomersToCSV() throws SQLException {
        fileOperationsService.exportToCSV(customerService.getAll(), "customers.csv", primaryStage);
    }

    public void importCustomersFromCSV() {
        fileOperationsService.importFromCSV("Customer", primaryStage);
    }

    public void exportSupermarketToCSV() throws SQLException {
        fileOperationsService.exportToCSV(supermarketService.viewSupermarkets(), "supermarket.csv", primaryStage);
    }

    public void importSupermarketFromCSV() {
        fileOperationsService.importFromCSV("Supermarket", primaryStage);
    }

    public void exportToCSV(List<?> data, String fileName, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Write CSV header
                writer.append("ID,Name,Value\n");

                // Write data
                for (Object item : data) {
                    writer.append(item.toString()).append("\n");
                }

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception (e.g., show an error dialog)
            }
        }
    }
}