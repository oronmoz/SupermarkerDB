package com.example.supermarket.controllers;

import com.example.supermarket.components.AdvancedSearchBar;
import com.example.supermarket.models.*;
import com.example.supermarket.services.*;
import com.example.supermarket.utils.InsufficientStockException;
import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.lang.Float.parseFloat;


public class ShoppingSessionController implements Initializable {

    @FXML private TabPane wizardTabPane;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Supermarket> supermarketComboBox;;
    @FXML private AdvancedSearchBar productSearchBar;
    @FXML private TableView<Product> productTable;
    @FXML private TableView<ShoppingItem> cartTable;
    @FXML private TableView<ShoppingItem> orderSummaryTable;
    @FXML private Label totalLabel;

    private CustomerService customerService;
    private SupermarketService supermarketService;
    private ProductService productService;
    private ShoppingCartService shoppingCartService;
    private SupplierService supplierService;
    private ShoppingCart currentCart;
    private List<ShoppingCart> activeCarts = new ArrayList<>();

    private boolean servicesInitialized = false;




    public ShoppingSessionController() {

    }

    public void initServices(SupermarketService supermarketService, ProductService productService,
                             CustomerService customerService, ShoppingCartService shoppingCartService, SupplierService supplierService) {
        System.out.println("Initializing services in ShoppingSessionController");
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
        this.servicesInitialized = true;

        Platform.runLater(this::refreshShoppingWizard);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ShoppingSessionController initialized");
        if (customerComboBox == null) {
            System.err.println("customerComboBox is null. FXML injection failed.");
        }
        if (supermarketComboBox == null) {
            System.err.println("supermarketComboBox is null. FXML injection failed.");
        }
        if (productTable == null) {
            System.err.println("productTable is null. FXML injection failed.");
        }

        setupProductSearchBar();
        setupProductTable();
        setupCartTable();
        setupOrderSummaryTable();
        setupTabPane();
    }

    private void setupTabPane() {
        wizardTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Disable all tabs except the first one
        for (int i = 1; i < wizardTabPane.getTabs().size(); i++) {
            wizardTabPane.getTabs().get(i).setDisable(true);
        }

        // Prevent tab selection by clicking
        wizardTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != wizardTabPane.getSelectionModel().getSelectedItem()) {
                wizardTabPane.getSelectionModel().select(oldValue);
            }
        });
    }

    public void refreshShoppingWizard() {
        System.out.println("Refreshing shopping wizard");
        if (!servicesInitialized) {
            System.out.println("Services not initialized. Skipping refresh.");
            return;
        }
        try {
            setupCustomerComboBox();
            setupSupermarketComboBox();
            setupProductSearchBar();
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error refreshing shopping wizard: " + e.getMessage());
        }
    }



    private void setupProductSearchBar() {
        String[] searchCriteria = {"Name", "Barcode", "Type"};
        productSearchBar = new AdvancedSearchBar(searchCriteria, this::searchProducts, this::refreshSearchResults);
    }

    private void refreshSearchResults() {
        try {
            loadProducts();
            productSearchBar.clearSearch(); // Clear the search bar
        } catch (SQLException e) {
            showAlert("Error refreshing products: " + e.getMessage());
        }
    }

    public ShoppingCart getCurrentCart() {
        return currentCart;
    }

    private void setupProductTable() {
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Product, String> barcodeColumn = new TableColumn<>("Barcode");
        barcodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));

        TableColumn<Product, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));

        TableColumn<Product, Number> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(cellData -> {
            try {
                Supermarket selectedSupermarket = supermarketComboBox.getValue();
                if (selectedSupermarket != null) {
                    return new SimpleIntegerProperty(supermarketService.getProductStock(cellData.getValue().getId(), selectedSupermarket.getId()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleIntegerProperty(0);
        });

        productTable.getColumns().addAll(nameColumn, barcodeColumn, priceColumn, stockColumn);
    }

    private void setupCartTable() {
        TableColumn<ShoppingItem, String> productNameColumn = new TableColumn<>("Product");
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<ShoppingItem, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()));

        TableColumn<ShoppingItem, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getPrice().floatValue()));


        cartTable.getColumns().addAll(productNameColumn, quantityColumn, priceColumn);
    }

    private void setupOrderSummaryTable() {
        // Similar to cartTable setup
        TableColumn<ShoppingItem, String> productNameColumn = new TableColumn<>("Product");
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<ShoppingItem, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()));

        TableColumn<ShoppingItem, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getPrice().floatValue()));


        orderSummaryTable.getColumns().addAll(productNameColumn, quantityColumn, priceColumn);
    }

    @FXML
    public void startShopping() {
        resetWizard(); // Reset the wizard when starting a new shopping session
        Customer selectedCustomer = customerComboBox.getValue();
        Supermarket selectedSupermarket = supermarketComboBox.getValue();
        if (selectedCustomer != null && selectedSupermarket != null) {
            try {
                currentCart = shoppingCartService.createShoppingCart(selectedCustomer.getId(), selectedSupermarket.getId());
                activeCarts.add(currentCart);
                loadProducts();
                wizardTabPane.getTabs().get(2).setDisable(false);
                wizardTabPane.getSelectionModel().select(2); // Move to product selection tab
            } catch (SQLException e) {
                showAlert("Error starting new session: " + e.getMessage());
            }
        } else {
            showAlert("Please select a customer and a supermarket");
        }
    }



    private void searchProducts(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        productTable.setItems(productTable.getItems().filtered(product -> {
            switch (criteria) {
                case "Name":
                    return product.getName().toLowerCase().contains(term);
                case "Barcode":
                    return product.getBarcode().toLowerCase().contains(term);
                case "Type":
                    return product.getType().toString().toLowerCase().contains(term);
                default:
                    return false;
            }
        }));
    }

    @FXML
    private void addToCart() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && currentCart != null) {
            try {
                int currentStock = supermarketService.getProductStock(selectedProduct.getId(), currentCart.getSupermarketId());
                int currentQuantityInCart = getCurrentQuantityInCart(selectedProduct.getBarcode());
                int availableQuantity = currentStock - currentQuantityInCart;

                if (availableQuantity <= 0) {
                    showAlert("Error: This product is out of stock.");
                    return;
                }

                int quantityToAdd = showQuantityDialog(availableQuantity);
                if (quantityToAdd > 0) {
                    ShoppingItem existingItem = findExistingCartItem(selectedProduct.getBarcode());
                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + quantityToAdd);
                    } else {
                        ShoppingItem newItem = new ShoppingItem(0, currentCart.getId(), selectedProduct.getBarcode(),
                                selectedProduct.getName(), selectedProduct.getPrice(), quantityToAdd);
                        currentCart.addItem(newItem);
                    }
                    updateCartTable();
                    showAlert("Product added to cart successfully.");
                }
            } catch (SQLException e) {
                showAlert("Error adding item to cart: " + e.getMessage());
            }
        } else {
            showAlert("Please select a product");
        }
    }

    private int getCurrentQuantityInCart(String barcode) {
        return currentCart.getItems().stream()
                .filter(item -> item.getBarcode().equals(barcode))
                .mapToInt(ShoppingItem::getQuantity)
                .sum();
    }

    private ShoppingItem findExistingCartItem(String barcode) {
        return currentCart.getItems().stream()
                .filter(item -> item.getBarcode().equals(barcode))
                .findFirst()
                .orElse(null);
    }



    private void updateCartTable() {
        // Clear the existing items in the TableView
        cartTable.getItems().clear();

        // Add all items from the currentCart to the TableView
        cartTable.getItems().addAll(currentCart.getItems());

        // Refresh the TableView to reflect the changes
        cartTable.refresh();

        updateTotal();
    }


    private void updateTotal() {
        BigDecimal total = currentCart.getTotalPrice();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void proceedToCheckout() {
        if (!currentCart.getItems().isEmpty()) {
            wizardTabPane.getTabs().get(3).setDisable(false);
            wizardTabPane.getSelectionModel().select(3);
            orderSummaryTable.setItems(FXCollections.observableArrayList(currentCart.getItems()));
            updateTotal();
        } else {
            showAlert("Your cart is empty");
        }
    }


    public void resetShoppingSession() {
        currentCart = null;
        if (customerComboBox != null) {
            customerComboBox.setValue(null);
        }
        if (supermarketComboBox != null) {
            supermarketComboBox.setValue(null);
        }
        if (cartTable != null) {
            cartTable.getItems().clear();
        }
        if (orderSummaryTable != null) {
            orderSummaryTable.getItems().clear();
        }
        if (totalLabel != null) {
            totalLabel.setText("Total: $0.00");
        }
        if (wizardTabPane != null) {
            wizardTabPane.getSelectionModel().select(0);
        }
    }


    private int showQuantityDialog(int maxQuantity) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Enter Quantity");
        dialog.setHeaderText("Enter the quantity for the selected product (Max: " + maxQuantity + ")");
        dialog.setContentText("Quantity:");

        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int quantity = Integer.parseInt(result.get());
                    if (quantity > 0 && quantity <= maxQuantity) {
                        return quantity;
                    } else {
                        showAlert("Please enter a valid quantity between 1 and " + maxQuantity);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Please enter a valid number");
                }
            } else {
                return 0; // User cancelled the dialog
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Shopping Session");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public List<ShoppingCart> getActiveCarts() {
        return new ArrayList<>(activeCarts);
    }


    @FXML
    private void nextToSupermarketSelection() {
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer != null) {
            wizardTabPane.getTabs().get(1).setDisable(false);
            wizardTabPane.getSelectionModel().select(1);
        } else {
            showAlert("Please select a customer");
        }
    }

    @FXML
    private void nextToProductSelection() throws SQLException {
        Supermarket selectedSupermarket = supermarketComboBox.getValue();
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedSupermarket != null && selectedCustomer != null) {
            // Check and add customer to supermarket if necessary
            supermarketService.addCustomerToSupermarketIfNotExists(selectedCustomer.getId(), selectedSupermarket.getId());

            // Create a temporary cart in memory, not in the database
            currentCart = new ShoppingCart(selectedCustomer.getId(), selectedSupermarket.getId());
            loadProducts();
            wizardTabPane.getTabs().get(2).setDisable(false);
            wizardTabPane.getSelectionModel().select(2);
        } else {
            showAlert("Please select both a customer and a supermarket");
        }
    }

    private void resetWizard() {
        customerComboBox.setValue(null);
        supermarketComboBox.setValue(null);
        currentCart = null;
        cartTable.getItems().clear();
        orderSummaryTable.getItems().clear();
        totalLabel.setText("Total: $0.00");

        // Disable all tabs except the first one
        for (int i = 1; i < wizardTabPane.getTabs().size(); i++) {
            wizardTabPane.getTabs().get(i).setDisable(true);
        }
        wizardTabPane.getSelectionModel().select(0);

        // Clear the product table
        productTable.getItems().clear();

        // Reset the product search bar
        productSearchBar.clearSearch();
    }

    @FXML
    private void viewCart() {
        if (currentCart != null) {
            updateCartTable();
            wizardTabPane.getSelectionModel().select(3); // Move to cart view tab
        } else {
            showAlert("No Active Cart");
        }
    }

    @FXML
    private void completePurchase() {
        if (currentCart != null && !currentCart.getItems().isEmpty()) {
            try {
                // Save the cart and its items to the database
                shoppingCartService.saveCart(currentCart);

                // Update customer-supermarket relationship and product stock
                supermarketService.updateAfterPurchase(currentCart);

                // Show confirmation to the user
                showAlert("Purchase completed successfully!\nTotal: $" + String.format("%.2f", currentCart.getTotalPrice()));

                // Reset the wizard after completing a purchase
                resetWizard();

            } catch (InsufficientStockException e) {
                showAlert("Error: " + e.getMessage() + "\nPlease adjust your cart and try again.");
            } catch (SQLException e) {
                showAlert("Error completing purchase: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Cannot complete purchase: Cart is empty");
        }
    }

    private void setupCustomerComboBox() throws SQLException {
        List<Customer> customers = customerService.getAll();
        Platform.runLater(() -> {
            customerComboBox.setItems(FXCollections.observableArrayList(customers));
            customerComboBox.setPromptText(customers.isEmpty() ? "No customers available" : "Select a customer");
        });
    }

    private void setupSupermarketComboBox() throws SQLException {
        List<Supermarket> supermarkets = supermarketService.getAllSupermarkets();

        Platform.runLater(() -> {
            supermarketComboBox.setItems(FXCollections.observableArrayList(supermarkets));
            supermarketComboBox.setPromptText(supermarkets.isEmpty() ? "No supermarkets available" : "Select a supermarket");
        });
    }

    private void loadProducts() throws SQLException {
        Supermarket selectedSupermarket = supermarketComboBox.getValue();
        List<Product> products;
        if (selectedSupermarket != null) {
            products = supermarketService.getProductsBySupermarket(selectedSupermarket.getId());
        } else {
            products = productService.getAllProducts();
        }
        productTable.setItems(FXCollections.observableArrayList(products));
        productTable.refresh(); // This will trigger the cellValueFactory to recalculate stock
        if (products.isEmpty()) {
            showAlert("No products available for the selected supermarket");
        }
    }


    @FXML
    private void cancelShopping() {
        if (currentCart != null && !currentCart.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel Shopping");
            alert.setHeaderText("Are you sure you want to cancel your shopping?");
            alert.setContentText("Your current cart will be discarded.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        shoppingCartService.deleteCart(currentCart.getId());
                        activeCarts.remove(currentCart);
                        resetWizard(); // Reset the wizard after canceling shopping
                        showAlert("Shopping canceled. Your cart has been discarded.");
                    } catch (SQLException e) {
                        showAlert("Error canceling shopping: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            resetWizard(); // If the cart is empty, just reset the wizard
        }
    }


}