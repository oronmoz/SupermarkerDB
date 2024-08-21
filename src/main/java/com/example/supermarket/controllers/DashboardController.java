package com.example.supermarket.controllers;

import com.example.supermarket.components.AdvancedSearchBar;
import com.example.supermarket.components.ReusableFormLayout;
import com.example.supermarket.components.SortableFilterableTableView;
import com.example.supermarket.models.*;
import com.example.supermarket.services.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.supermarket.managers.SupermarketFileHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    @FXML private Label totalSalesLabel;
    @FXML private Label customerCountLabel;
    @FXML private Label productCountLabel;
    @FXML private Label lowStockItemsLabel;
    @FXML private ListView<String> recentActivitiesList;
    @FXML private LineChart<String, Number> salesTrendChart;
    @FXML private PieChart topProductsChart;
    @FXML private Label supermarketCountLabel;
    @FXML private VBox addressManagementSection;
    @FXML private AdvancedSearchBar addressSearchBar;
    @FXML private SortableFilterableTableView<Address> addressesTable;
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private AddressService addressService;
    private ObservableList<Address> addresses;
    private SupermarketService supermarketService;
    private ProductService productService;
    private CustomerService customerService;
    private ShoppingCartService shoppingCartService;
    private SupplierService supplierService;

    private SupermarketFileHandler fileHandler;

    public DashboardController(){}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("DashboardController initialized");

        // Initialize charts
        salesTrendChart.setAnimated(false);
        topProductsChart.setAnimated(false);

        // Set some initial text to verify labels are working
        totalSalesLabel.setText("Loading...");
        customerCountLabel.setText("Loading...");
        productCountLabel.setText("Loading...");
        lowStockItemsLabel.setText("Loading...");

        // Call refreshDashboard after a short delay to ensure JavaFX is ready
        Platform.runLater(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            refreshDashboard();
        });
    }



    public void initServices(SupermarketService supermarketService, ProductService productService,
                             CustomerService customerService, ShoppingCartService shoppingCartService,
                             SupplierService supplierService, SupermarketFileHandler fileHandler) {
        LOGGER.info("Initializing services in DashboardController");
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
        this.fileHandler = fileHandler;
        refreshDashboard();
    }


    public void refreshDashboard() {
        LOGGER.info("Refreshing dashboard");

        if (shoppingCartService == null || customerService == null || productService == null) {
            LOGGER.warning("One or more services are null. Cannot refresh dashboard.");
            return;
        }

        try {
            LOGGER.info("Fetching data for dashboard");
            List<ShoppingCart> purchases = shoppingCartService.getAll();
            List<Customer> customers = customerService.getAll();
            List<Product> products = productService.getAllProducts();

            LOGGER.info("Purchases: " + purchases.size() + ", Customers: " + customers.size() + ", Products: " + products.size());

            Platform.runLater(() -> {
                applyStyles();

                updateComponent(() -> {
                    try {
                        updateTotalSales(purchases);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, "update total sales");
                updateComponent(() -> updateCustomerCount(customers.size()), "update customer count");
                updateComponent(() -> updateProductCount(products.size()), "update product count");
                updateComponent(() -> updateLowStockItems(products), "low stock product count");
                updateComponent(() -> {
                    try {
                        updateRecentActivities(purchases);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, "update recent activities");
                updateComponent(() -> {
                    updateSalesTrendChart(purchases);
                }, "update sales trend chart");
                updateComponent(() -> {
                    updateTopProductsChart(purchases);
                }, "update top products chart");

                forceLayoutRefresh();
                logComponentStates();

                LOGGER.info("Dashboard refresh completed");
            });
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error refreshing dashboard", e);
            showAlert("Error", "Failed to refresh dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateComponent(Runnable updateAction, String componentName) {
        try {
            updateAction.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating " + componentName, e);
            showAlert("Error", "Failed to " + componentName + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    void applyStyles() {
        totalSalesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        customerCountLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        productCountLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        recentActivitiesList.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        salesTrendChart.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        topProductsChart.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }

    private void forceLayoutRefresh() {
        totalSalesLabel.getParent().layout();
        salesTrendChart.layout();
        topProductsChart.layout();
        recentActivitiesList.layout();
        lowStockItemsLabel.layout();

        Scene scene = totalSalesLabel.getScene();
        if (scene != null) {
            scene.getWindow().sizeToScene();
        }
    }

    private void logComponentStates() {
        LOGGER.info("Total Sales Label Text: " + totalSalesLabel.getText());
        LOGGER.info("Customer Count Label Text: " + customerCountLabel.getText());
        LOGGER.info("Product Count Label Text: " + productCountLabel.getText());
        LOGGER.info("Recent Activities List Items: " + recentActivitiesList.getItems().size());
        LOGGER.info("Sales Trend Chart Data Points: " + (salesTrendChart.getData().isEmpty() ? 0 : salesTrendChart.getData().get(0).getData().size()));
        LOGGER.info("Top Products Chart Data Points: " + topProductsChart.getData().size());
    }


    private void updateTotalSales(List<ShoppingCart> purchases) throws SQLException {
        BigDecimal totalSales = BigDecimal.ZERO;
        for (ShoppingCart cart : purchases) {
            BigDecimal cartTotal = shoppingCartService.getCartTotal(cart.getId());
            totalSales = totalSales.add(cartTotal);
        }
        String totalSalesString = String.format("$%.2f", totalSales);
        totalSalesLabel.setText(totalSalesString);
        LOGGER.info("Total sales label text: " + totalSalesString);
    }

    private void updateCustomerCount(int count) {
        LOGGER.info("Updating customer count: " + count);
        customerCountLabel.setText(String.valueOf(count));
        LOGGER.info("Customer count label text: " + customerCountLabel.getText());
    }

    private void updateProductCount(int count) {
        LOGGER.info("Updating product count: " + count);
        productCountLabel.setText(String.valueOf(count));
        LOGGER.info("Product count label text: " + productCountLabel.getText());
    }


    private void updateSupermarketCount(int count) {
        supermarketCountLabel.setText(String.valueOf(count));
    }

    private void updateLowStockItems(List<Product> products) {
        long lowStockCount = products.stream()
                .filter(product -> {
                    try {
                        return productService.getTotalCountForProduct(product.getId()) < 10; // Assuming 10 is the threshold
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .count();

        Platform.runLater(() -> {
            lowStockItemsLabel.setText(lowStockCount == 0 ? "None" : String.valueOf(lowStockCount));
        });
    }

    private void updateRecentActivities(List<ShoppingCart> purchases) throws SQLException {
        ObservableList<String> activities = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (ShoppingCart purchase : purchases.subList(0, Math.min(10, purchases.size()))) {
            LocalDateTime purchaseDate = purchase.getCreatedAt();
            String formattedDate = purchaseDate.format(formatter);
            BigDecimal total = shoppingCartService.getCartTotal(purchase.getId());
            String activity = String.format("Purchase of $%.2f on %s", total, formattedDate);
            activities.add(activity);
            LOGGER.info("Added activity: " + activity);
        }

        recentActivitiesList.setItems(activities);
        LOGGER.info("Recent activities list updated with " + activities.size() + " items");
    }

    private void updateSalesTrendChart(List<ShoppingCart> purchases) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        Map<LocalDate, BigDecimal> dailySales = purchases.stream()
                .collect(Collectors.groupingBy(
                        cart -> cart.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, ShoppingCart::getAllTotalPrices, BigDecimal::add)
                ));

        dailySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue().doubleValue()));
                    LOGGER.info("Added data point to sales chart: Date=" + entry.getKey() + ", Sales=" + entry.getValue());
                });

        Platform.runLater(() -> {
            salesTrendChart.getData().clear();
            salesTrendChart.getData().add(series);
            LOGGER.info("Sales trend chart updated with " + series.getData().size() + " data points");
        });
    }

    private void updateTopProductsChart(List<ShoppingCart> purchases) {
        Map<String, BigDecimal> productSales = purchases.stream()
                .flatMap(cart -> cart.getItems().stream())
                .collect(Collectors.groupingBy(
                        ShoppingItem::getProductName,
                        Collectors.reducing(BigDecimal.ZERO,
                                item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                BigDecimal::add)
                ));

        LOGGER.info("Product sales map: " + productSales);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        productSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue().doubleValue()));
                    LOGGER.info("Added product to chart: " + entry.getKey() + " - " + entry.getValue());
                });

        Platform.runLater(() -> {
            topProductsChart.setData(pieChartData);
            LOGGER.info("Top products chart updated with " + pieChartData.size() + " products");
        });
    }


    @FXML
    private void addNewProduct(ActionEvent event) {
        try {
            ReusableFormLayout productForm = new ReusableFormLayout();
            productForm.addField("Name", "Enter product name");
            productForm.addField("Barcode", "Enter product barcode");
            productForm.addField("Type", "Enter product type");
            productForm.addField("Price", "Enter product price");
            productForm.addField("Supplier ID", "Enter supplier ID");

            Dialog<Product> dialog = new Dialog<>();
            dialog.setTitle("Add New Product");
            dialog.setHeaderText("Enter Product Details");
            dialog.getDialogPane().setContent(productForm);

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    Product newProduct = new Product();
                    newProduct.setName(productForm.getFieldValue("Name"));
                    newProduct.setBarcode(productForm.getFieldValue("Barcode"));
                    newProduct.setType(Product.ProductType.valueOf(productForm.getFieldValue("Type")));
                    newProduct.setPriceFromString(productForm.getFieldValue("Price"));
                    newProduct.setSupplierId(Integer.parseInt(productForm.getFieldValue("Supplier ID")));
                    return newProduct;
                }
                return null;
            });

            Optional<Product> result = dialog.showAndWait();
            result.ifPresent(product -> {
                try {
                    productService.saveProduct(product);
                    refreshDashboard();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to save product: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add new product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void addNewCustomer(ActionEvent event) {
        try {
            ReusableFormLayout customerForm = new ReusableFormLayout();
            customerForm.addField("Name", "Enter customer name");

            Dialog<Customer> dialog = new Dialog<>();
            dialog.setTitle("Add New Customer");
            dialog.setHeaderText("Enter Customer Details");
            dialog.getDialogPane().setContent(customerForm);

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(customerForm.getFieldValue("Name"));
                    return newCustomer;
                }
                return null;
            });

            Optional<Customer> result = dialog.showAndWait();
            result.ifPresent(customer -> {
                try {
                    customerService.save(customer);
                    refreshDashboard();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to save customer: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add new customer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupAddressSearchBar() {
        String[] searchCriteria = {"ID", "Street", "City"};
        addressSearchBar.initialize(searchCriteria, this::performAddressSearch, this::refreshAddresses);
    }

    private void refreshAddresses() {
        try {
            addresses = FXCollections.observableArrayList(addressService.getAllAddresses());
            addressesTable.setItems(addresses);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load addresses: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void performAddressSearch(String searchCriteria) {
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

    @FXML
    private void generateReport(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReportingInterface.fxml"));

            // Create the controller manually
            ReportingController controller = new ReportingController();
            loader.setController(controller);

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Generate Report");
            stage.setScene(new Scene(root));


            controller.init(supermarketService, productService, customerService,
                    shoppingCartService, supplierService,
                    fileHandler, stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open reporting interface: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
