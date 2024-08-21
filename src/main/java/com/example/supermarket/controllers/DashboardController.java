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
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    @FXML private Label totalSalesLabel;
    @FXML private Label customerCountLabel;
    @FXML private Label productCountLabel;
    @FXML private Label lowStockItemsLabel;
    @FXML private Label supermarketCountLabel;
    @FXML private ListView<String> recentActivitiesList;
    @FXML private LineChart<String, Number> salesTrendChart;
    @FXML private PieChart topProductsChart;
    @FXML private VBox addressManagementSection;
    @FXML private AdvancedSearchBar addressSearchBar;
    @FXML private SortableFilterableTableView<Address> addressesTable;

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
    }

    public void initServices(SupermarketService supermarketService, ProductService productService,
                             CustomerService customerService, ShoppingCartService shoppingCartService,
                             SupplierService supplierService, SupermarketFileHandler fileHandler) {
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
        this.fileHandler = fileHandler;
        refreshDashboard();
    }

    public void refreshDashboard() {
        if (shoppingCartService == null || customerService == null || productService == null) {
            return;
        }

        try {
            List<ShoppingCart> purchases = shoppingCartService.getAll();
            List<Customer> customers = customerService.getAll();
            List<Product> products = productService.getAllProducts();

            Platform.runLater(() -> {
                updateTotalSales(purchases);
                updateCustomerCount(customers.size());
                updateProductCount(products.size());
                updateRecentActivities(purchases);
                updateSalesTrendChart(purchases);
                updateTopProductsChart(purchases);

                // Force a layout pass
                totalSalesLabel.getParent().layout();
                salesTrendChart.layout();
                topProductsChart.layout();

            });
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }



    private void updateTotalSales(List<ShoppingCart> purchases) {
        BigDecimal totalSales = BigDecimal.ZERO;
        try {
            for (ShoppingCart cart : purchases) {
                BigDecimal cartTotal = shoppingCartService.getCartTotal(cart.getId());
                totalSales = totalSales.add(cartTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        final String totalSalesString = String.format("$%.2f", totalSales);
        totalSalesLabel.setText(totalSalesString);

    }

    private void updateCustomerCount(int count) {
        customerCountLabel.setText(String.valueOf(count));

    }

    private void updateProductCount(int count) {
        productCountLabel.setText(String.valueOf(count));

    }

    private void updateSupermarketCount(int count) {
        supermarketCountLabel.setText(String.valueOf(count));
    }

    private void updateRecentActivities(List<ShoppingCart> purchases) {
        ObservableList<String> activities = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (ShoppingCart purchase : purchases.subList(0, Math.min(10, purchases.size()))) {
            LocalDateTime purchaseDate = purchase.getCreatedAt();
            String formattedDate = purchaseDate.format(formatter);
            activities.add(String.format("Purchase of $%.2f on %s", purchase.getTotalPrice(), formattedDate));
        }

        recentActivitiesList.setItems(activities);
    }

    private void updateSalesTrendChart(List<ShoppingCart> purchases) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        Map<LocalDate, BigDecimal> dailySales = new HashMap<>();

        try {
            for (ShoppingCart cart : purchases) {
                LocalDate date = cart.getCreatedAt().toLocalDate();
                BigDecimal total = shoppingCartService.getCartTotal(cart.getId());
                dailySales.merge(date, total, BigDecimal::add);
            }

            dailySales.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        double salesAmount = entry.getValue().doubleValue();
                        series.getData().add(new XYChart.Data<>(entry.getKey().toString(), salesAmount));
                    });

            salesTrendChart.getData().clear();
            salesTrendChart.getData().add(series);

            // Force layout update
            salesTrendChart.layout();
            salesTrendChart.setAnimated(false);
            salesTrendChart.setAnimated(true);


        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    private void updateTopProductsChart(List<ShoppingCart> purchases) {
        Map<String, BigDecimal> productSales = purchases.stream()
                .flatMap(cart -> cart.getItems().stream())
                .collect(Collectors.groupingBy(
                        ShoppingItem::getProductName,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                BigDecimal::add
                        )
                ));
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        productSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue().toBigInteger().doubleValue())));

        topProductsChart.setData(pieChartData);
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
