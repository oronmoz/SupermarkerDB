package com.example.supermarket;

import com.example.supermarket.controllers.MainWindowController;
import com.example.supermarket.controllers.ShoppingSessionController;
import com.example.supermarket.managers.*;
import com.example.supermarket.models.Address;
import com.example.supermarket.models.Supplier;
import com.example.supermarket.services.*;
import com.example.supermarket.services.impl.*;
import com.example.supermarket.database.dao.*;
import com.example.supermarket.database.dao.impl.*;
import com.example.supermarket.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SupermarketApp extends Application {
    private Stage primaryStage;
    private BorderPane root;
    private SupermarketFileHandler fileHandler;
    private MainWindowController mainWindowController;


    // Services
    private SupermarketService supermarketService;
    private CustomerService customerService;
    private ProductService productService;
    private ShoppingCartService shoppingCartService;
    private FileOperationsService fileOperationsService;
    private AddressService addressService;
    private SupplierService supplierService;

    // Controllers
    private ShoppingSessionController shoppingSessionController;


    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        initializeDatabase();
        initializeServices();
        loadMainWindow();
    }

    private void loadMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainWindow.fxml"));
        Parent root = loader.load();

        MainWindowController controller = loader.getController();
        controller.initializeServices(supermarketService, productService, customerService, shoppingCartService, addressService, supplierService, fileHandler);
        controller.setStage(primaryStage);
        controller.postInitialize();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setTitle("Supermarket Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeDatabase() {
        try {
            DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to the database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeServices() {
        CustomerDao customerDao = new CustomerDaoImpl();
        ProductDao productDao = new ProductDaoImpl();
        ShoppingCartDao shoppingCartDao = new ShoppingCartDaoImpl();
        SupermarketDao supermarketDao = new SupermarketDaoImpl();
        ProductSupermarketDao productSupermarketDao = new ProductSupermarketDaoImpl();
        SupermarketCustomerDao supermarketCustomerDao = new SupermarketCustomerDaoImpl();
        AddressDao addressDao = new AddressDaoImpl();
        SupplierDao supplierDao = new SupplierDaoImpl();

        this.supplierService = new SupplierServiceImpl(supplierDao);
        this.addressService = new AddressServiceImpl(addressDao);
        this.supermarketService = new SupermarketServiceImpl(supermarketDao, productSupermarketDao, addressService, customerDao, addressDao, productDao, supermarketCustomerDao);
        this.customerService = new CustomerServiceImpl(customerDao, supermarketCustomerDao, supermarketDao, addressDao);
        this.productService = new ProductServiceImpl(productDao, productSupermarketDao, supermarketDao);
        this.shoppingCartService = new ShoppingCartServiceImpl(shoppingCartDao);
        this.fileOperationsService = new FileOperationsServiceImpl(supermarketService, productService, customerService);
        this.fileHandler = new SupermarketFileHandler(supermarketService, productService, customerService, primaryStage);
    }

    public void setupServices(SupermarketService supermarketService,
                              ProductService productService,
                              CustomerService customerService,
                              ShoppingCartService shoppingCartService,
                              AddressService addressService,
                              SupplierService supplierService,
                              SupermarketFileHandler fileHandler) {
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.addressService = addressService;
        this.supplierService = supplierService;
        this.fileHandler = fileHandler;
    }



    private void displayWelcomeMessage() {
        Label welcomeLabel = new Label("Welcome to the Supermarket Management System!\n\n" +
                "Please use the menu options to manage the supermarket.");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
        root.setCenter(welcomeLabel);
    }

    public void exitApplication() {
        showConfirmationDialog("Exit Application", "Are you sure you want to exit?", "Any unsaved changes will be lost.")
                .ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        DatabaseManager.getInstance().closeConnection();
                        System.exit(0);
                    }
                });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private java.util.Optional<ButtonType> showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    // Getters for services, managers, and controllers
    public SupermarketService getSupermarketService() { return this.supermarketService; }
    public CustomerService getCustomerService() { return this.customerService; }
    public ProductService getProductService() { return this.productService; }
    public ShoppingCartService getShoppingCartService() { return this.shoppingCartService; }
    public FileOperationsService getFileOperationsService() { return this.fileOperationsService; }
    public ShoppingSessionController getShoppingSessionController() { return this.shoppingSessionController; }
    public BorderPane getRootPane() { return this.root; }
    public Stage getPrimaryStage() { return this.primaryStage; }
    public SupermarketFileHandler getFileHandler() { return this.fileHandler; }
    public AddressService getAddressService(){ return this.addressService; }
    public SupplierService getSupplierService() { return this.supplierService; }
}