package com.example.supermarket.controllers;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.managers.SupermarketFileHandler;
import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.services.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MainWindowController{

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    private Stack<String> navigationHistory = new Stack<>();
    private Map<String, Parent> views = new HashMap<>();
    private SupermarketService supermarketService;
    private ProductService productService;
    private CustomerService customerService;
    private ShoppingCartService shoppingCartService;
    private AddressService addressService;
    private SupplierService supplierService;
    private SupermarketFileHandler fileHandler;
    private ShoppingSessionController activeShoppingSession;
    private SuppliersController suppliersController;
    private ReportingController reportingController;

    private Stage stage;


    public MainWindowController(){

    }

    @FXML
    public void initialize(){
        //setupMenuBar();
    }

    public void postInitialize() {
        loadViews();
        showDashboard();
        Platform.runLater(() -> {
            Parent dashboardView = views.get("dashboard");
            if (dashboardView != null && dashboardView.getUserData() instanceof DashboardController) {
                DashboardController dashboardController = (DashboardController) dashboardView.getUserData();
                dashboardController.refreshDashboard();
            } else {
                System.out.println("Dashboard view or controller not found.");
            }
        });
    }

    public void initializeServices(SupermarketService supermarketService,
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

    @FXML
    private void startNewShoppingSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ShoppingSessionWizard.fxml"));
            Parent root = loader.load();
            ShoppingSessionController controller = loader.getController();

            // Ensure all services are non-null before passing them
            if (supermarketService == null || productService == null || customerService == null ||
                    shoppingCartService == null || supplierService == null) {
                System.out.println("One or more services are null in MainWindowController");
                // Handle this error appropriately
                return;
            }

            controller.initServices(supermarketService, productService, customerService, shoppingCartService, supplierService);

            Stage stage = new Stage();
            stage.setTitle("New Shopping Session");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to start new shopping session: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void loadViews() {
        try {

            // Load Reporting Interface

            FXMLLoader reportingLoader = new FXMLLoader(getClass().getResource("/views/ReportingInterface.fxml"));
            Parent reportingView = reportingLoader.load();
            reportingController = reportingLoader.getController();
            reportingController.initServices(supermarketService, productService, customerService,
                    shoppingCartService, supplierService, fileHandler, stage);
            views.put("reporting", reportingView);

            // Load Shopping Session Wizard
            FXMLLoader shoppingSessionLoader = new FXMLLoader(getClass().getResource("/views/ShoppingSessionWizard.fxml"));
            Parent shoppingSessionView = shoppingSessionLoader.load();
            ShoppingSessionController shoppingSessionController = shoppingSessionLoader.getController();
            shoppingSessionController.initServices(supermarketService, productService, customerService, shoppingCartService, supplierService);
            views.put("shoppingSession", shoppingSessionView);

            // Load AddressView
            FXMLLoader addressLoader = new FXMLLoader(getClass().getResource("/views/AddressView.fxml"));
            Parent addressView = addressLoader.load();
            AddressViewController addressViewController = addressLoader.getController();
            addressViewController.setAddressService(addressService);
            addressViewController.refreshAddresses();
            views.put("addressView", addressView);

            // Load SupplierView
            FXMLLoader supplierLoader = new FXMLLoader(getClass().getResource("/views/SupplierView.fxml"));
            Parent supplierView = supplierLoader.load();
            SuppliersController supplierViewController = supplierLoader.getController();
            supplierViewController.setSupplierService(supplierService);
            supplierViewController.refreshSuppliers();
            views.put("supplierView", supplierView);

            // Load Supermarket Management
            FXMLLoader supermarketManagementLoader = new FXMLLoader(getClass().getResource("/views/SupermarketManagement.fxml"));
            Parent supermarketManagementView = supermarketManagementLoader.load();
            SupermarketManagementController supermarketManagementController = supermarketManagementLoader.getController();
            supermarketManagementController.initServices(supermarketService, productService, customerService, shoppingCartService, supplierService);
            supermarketManagementController.postInitialize(); // Add this line
            views.put("supermarketManagement", supermarketManagementView);

            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            Parent dashboardView = dashboardLoader.load();
            DashboardController dashboardController = dashboardLoader.getController();
            dashboardController.initServices(supermarketService, productService, customerService,
                    shoppingCartService, supplierService, fileHandler);
            dashboardView.setUserData(dashboardController);
            views.put("dashboard", dashboardView);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading views", "An error occurred while loading application views.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showView(String viewName) {
        if (views.containsKey(viewName)) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(views.get(viewName));
            statusLabel.setText(viewName);
        } else {
            showAlert("View not found", "The requested view does not exist.", Alert.AlertType.WARNING);
        }
    }



    private void handleBreadcrumbClick(int index) {
        while (navigationHistory.size() > index + 1) {
            navigationHistory.pop();
        }
        showView(navigationHistory.peek());
    }

    @FXML
    private void goBack() {
        if (navigationHistory.size() > 1) {
            navigationHistory.pop(); // Remove current view
            showView(navigationHistory.peek());
        }
    }

    private void viewCart() {
        if (activeShoppingSession != null) {
            ShoppingCart currentCart = activeShoppingSession.getCurrentCart();
            if (currentCart != null) {
                showCartDialog(currentCart);
            } else {
                showAlert("No Active Cart", "There is no active shopping cart.", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("No Active Session", "There is no active shopping session.", Alert.AlertType.INFORMATION);
        }
    }

    private void showCartDialog(ShoppingCart cart) {
        startNewShoppingSession();
        //showAlert("Cart View", "Cart viewing functionality not yet implemented", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void checkout() {
        if (activeShoppingSession != null) {
            ShoppingCart currentCart = activeShoppingSession.getCurrentCart();
            if (currentCart != null) {
                try {
                    shoppingCartService.checkout(currentCart.getCustomerId(), currentCart.getSupermarketId());
                    showAlert("Checkout Successful", "Your order has been processed.", Alert.AlertType.INFORMATION);
                    activeShoppingSession.resetShoppingSession();
                } catch (SQLException e) {
                    showAlert("Checkout Failed", "An error occurred during checkout: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("No Active Cart", "There is no active shopping cart to checkout.", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("No Active Session", "There is no active shopping session.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void importSupermarkets() {
        fileHandler.importSupermarketFromCSV();
        showAlert("Export Successful", "Supermarkets have been exported to CSV.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void exportProducts() {
        try {
            fileHandler.exportProductsToCSV();
            showAlert("Export Successful", "Products have been exported to CSV.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Export Failed", "An error occurred while exporting products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void importProducts() {
        fileHandler.importProductsFromCSV();
        showAlert("Import Successful", "Products have been imported from CSV.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void exportCustomers() {
        try {
            fileHandler.exportCustomersToCSV();
            showAlert("Export Successful", "Customers have been exported to CSV.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Export Failed", "An error occurred while exporting customers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void importCustomers() {
        fileHandler.importCustomersFromCSV();
        showAlert("Import Successful", "Customers have been imported from CSV.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void showAbout() {
        showAlert("About", "Supermarket Management System\nVersion 1.0\n© 2024", Alert.AlertType.INFORMATION);
    }



    private void cleanup() {
        try {
            DatabaseManager.getInstance().closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while closing the database connection", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void openReportingInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReportingInterface.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reporting System");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    private Stage getStage() {
        return (Stage) contentArea.getScene().getWindow();
    }


    @FXML
    private void showDashboard() {
        loadView("Dashboard.fxml", "Dashboard");
    }

    @FXML
    private void showProducts() {
        loadView("SupermarketManagement.fxml", "Supermarket Management");
    }

    @FXML
    private void showReports() {
        if (reportingController != null) {
            reportingController.refreshView();
        }
        showView("reporting");
    }


    private void loadView(String fxmlFile, String viewName) {
        if (!views.containsKey(viewName)) {
            try {
                URL resourceUrl = getClass().getResource("/views/" + fxmlFile);
                if (resourceUrl == null) {
                    throw new IOException("Cannot find resource: " + fxmlFile);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent view = loader.load();
                views.put(viewName, view);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error loading view", "Failed to load " + viewName + ": " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        showView(viewName);
    }

    private void loadView(String fxmlFile, String viewName, boolean addToHistory) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            statusLabel.setText(viewName);

            if (addToHistory) {
                navigationHistory.push(viewName + "|" + fxmlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error dialog)
        }
    }


    @FXML
    private void exitApplication() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cleanup();
            Platform.exit();
        }
    }

    @FXML
    public void exportSupermarkets() {
        try {
            fileHandler.exportSupermarketToCSV();
            showAlert("Export Successful", "Supermarkets have been exported to CSV.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Export Failed", "An error occurred while exporting supermarkets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showSupermarketManagement() {
        showView("supermarketManagement");
    }

    @FXML
    public void showProductManagement() {
        showView("productManagement");
    }

    @FXML
    public void showCustomerManagement() {
        showView("customerManagement");
    }

    @FXML
    private void showSupplierManagement() {
        showView("supplierView");
    }


    @FXML
    public void viewActiveCarts() {
        if (activeShoppingSession != null) {
            List<ShoppingCart> activeCarts = activeShoppingSession.getActiveCarts();
            if (!activeCarts.isEmpty()) {
                // Implement a dialog or view to display active carts
                showAlert("Active Carts", "Number of active carts: " + activeCarts.size(), Alert.AlertType.INFORMATION);
            } else {
                showAlert("No Active Carts", "There are no active shopping carts.", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("No Active Session", "There is no active shopping session.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void showSalesReports() {
        showView("salesReports");
    }

    @FXML
    private void showInventoryReports() {
        showView("inventoryReports");
    }


    @FXML
    private void showSupplierReports(ActionEvent actionEvent) {
        showView("suppliersView");
    }


    @FXML
    private void showSupermarketPerformance() {
        showView("supermarketPerformance");
    }

    @FXML
    public void startNewSale() {
        startNewShoppingSession();
    }

    @FXML
    public void showCustomerReports(ActionEvent actionEvent) {
        showView("customerReports");
    }

    public void showAddressManagement(ActionEvent actionEvent) {
        showView("addressView");
    }
}