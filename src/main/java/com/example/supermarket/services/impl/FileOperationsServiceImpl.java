package com.example.supermarket.services.impl;

import com.example.supermarket.models.*;
import com.example.supermarket.services.CustomerService;
import com.example.supermarket.services.FileOperationsService;
import com.example.supermarket.services.ProductService;
import com.example.supermarket.services.SupermarketService;
import org.apache.commons.csv.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class FileOperationsServiceImpl implements FileOperationsService {
    private SupermarketService supermarketService;
    private ProductService productService;
    private CustomerService customerService;

    public FileOperationsServiceImpl(SupermarketService supermarketService, ProductService productService, CustomerService customerService) {
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
    }

    public void exportToCSV(List<?> data, String fileName, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

                for (Object item : data) {
                    csvPrinter.printRecord((Object[]) convertToCSV(item));
                }
                csvPrinter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }

    public void importFromCSV(String entityType, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (Reader reader = Files.newBufferedReader(file.toPath());
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

                for (CSVRecord csvRecord : csvParser) {
                    importEntity(entityType, csvRecord);
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }

    public String[] convertToCSV(Object item) {
        if (item instanceof Supermarket) {
            Supermarket supermarket = (Supermarket) item;
            return new String[]{
                    String.valueOf(supermarket.getId()),
                    supermarket.getName(),
                    supermarket.getLocation().getStreet(),
                    supermarket.getLocation().getCity(),
            };
        } else if (item instanceof Product) {
            Product product = (Product) item;
            return new String[]{
                    String.valueOf(product.getId()),
                    product.getName(),
                    product.getBarcode(),
                    product.getType().toString(),
                    String.valueOf(product.getPrice())
            };
        } else if (item instanceof Customer) {
            Customer customer = (Customer) item;
            return new String[]{
                    String.valueOf(customer.getId()),
                    customer.getName(),
            };
        }
        return new String[]{};
    }

    public void importEntity(String entityType, CSVRecord record) throws SQLException {
        switch (entityType) {
            case "Supermarket":
                Supermarket supermarket = new Supermarket();
                supermarket.setName(record.get(1));
                Address address = new Address();
                address.setStreet(record.get(2));
                address.setCity(record.get(3));
                supermarket.setLocation(address);
                supermarketService.addSupermarket(supermarket);
                break;
            case "Product":
                Product product = new Product();
                product.setName(record.get(1));
                product.setBarcode(record.get(2));
                product.setType(Product.ProductType.valueOf(record.get(3)));
                product.setPriceFromString(record.get(4));
                productService.saveProduct(product);
                break;
            case "Customer":
                Customer customer = new Customer();
                customer.setName(record.get(1));
                customerService.save(customer);
                break;
        }
    }
}