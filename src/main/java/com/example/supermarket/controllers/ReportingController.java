package com.example.supermarket.controllers;

import com.example.supermarket.components.ReportSelectionTreeView;
import com.example.supermarket.managers.SupermarketFileHandler;
import com.example.supermarket.models.*;
import com.example.supermarket.services.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportingController implements Initializable {
    @FXML private VBox reportSelectionContainer;
    @FXML private TreeView<String> reportSelectionTree;
    @FXML private StackPane reportContentArea;
    @FXML private LineChart<String, Number> salesTrendChart;

    private SupermarketService supermarketService;
    private ShoppingCartService shoppingCartService;
    private CustomerService customerService;
    private ProductService productService;
    private SupplierService supplierService;
    private SupermarketFileHandler fileHandler;
    private Stage primaryStage;

    private String currentReportTitle;
    private List<?> currentReportData;


    private boolean servicesInitialized = false;


    public ReportingController() {
        System.out.println("ReportingController constructor called");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ReportingController initialized");

        setupReportSelectionTree();
    }


    public void init(SupermarketService supermarketService, ProductService productService,
                     CustomerService customerService, ShoppingCartService shoppingCartService,
                     SupplierService supplierService, SupermarketFileHandler fileHandler, Stage primaryStage) {
        System.out.println("init called in ReportingController");
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
        this.fileHandler = fileHandler;
        this.primaryStage = primaryStage;
        this.servicesInitialized = true;
        System.out.println("Services initialized: " + servicesInitialized);
    }



    public void initServices(SupermarketService supermarketService, ProductService productService,
                             CustomerService customerService, ShoppingCartService shoppingCartService,
                             SupplierService supplierService, SupermarketFileHandler fileHandler, Stage primaryStage) {
        System.out.println("initServices called in ReportingController");
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
        this.fileHandler = fileHandler;
        this.primaryStage = primaryStage;
        this.servicesInitialized = true;
        System.out.println("Services initialized: " + servicesInitialized);
    }

    public void refreshView() {
        setupReportSelectionTree();
    }

    private void setupReportSelectionTree() {
        System.out.println("Setting up report selection tree");
        ReportSelectionTreeView reportTree = new ReportSelectionTreeView(this::handleReportSelection);
        reportSelectionContainer.getChildren().clear();
        reportSelectionContainer.getChildren().add(reportTree);
    }

    private void handleReportSelection(String reportType) {
        System.out.println("Handling report selection: " + reportType);
        System.out.println("Services initialized: " + servicesInitialized);
        if (!servicesInitialized) {
            showAlert("Services not initialized", "Please try again later.");
            return;
        }

        currentReportTitle = reportType;
        try {
            switch (reportType) {
                case "Daily Sales":
                    generateDailySalesReport();
                    break;
                case "Monthly Sales":
                    generateMonthlySalesReport();
                    break;
                case "Annual Sales":
                    generateAnnualSalesReport();
                    break;
                case "Inventory Report":
                    generateInventoryReport();
                    break;
                case "Customer Report":
                    generateCustomerReport();
                    break;
                case "Supplier Report":
                    generateSupplierReport();
                    break;
                case "Supermarket Performance":
                    generateSupermarketPerformanceReport();
                    break;
                case "Current Stock":
                    generateCurrentStockReport();
                    break;
                case "Low Stock Alert":
                    generateLowStockAlertReport();
                    break;
                case "Top Customers":
                    generateTopCustomersReport();
                    break;
                case "Customer Spending Analysis":
                    generateCustomerSpendingAnalysisReport();
                    break;
                case "Supplier Product Count":
                    generateSupplierProductCountReport();
                default:
                    // showAlert("Unknown report type", "Report type: " + reportType);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Chart generateSalesChart(List<ShoppingCart> purchases, String title) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Sales");

        Map<LocalDate, BigDecimal> salesByDate = purchases.stream()
                .collect(Collectors.groupingBy(
                        cart -> cart.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, ShoppingCart::getTotalPrice, BigDecimal::add)
                ));

        salesByDate.forEach((date, total) ->
                series.getData().add(new XYChart.Data<>(date.toString(), total.doubleValue()))
        );

        chart.getData().add(series);
        return chart;
    }

    private void generateCurrentStockReport() throws SQLException {
        List<Product> products = productService.getAllProducts();
        currentReportData = products;
        Chart chart = generateInventoryChart(products, "Current Stock Report");
        showChartInReportArea(chart);
    }

    private void generateLowStockAlertReport() throws SQLException {
        List<Product> products = productService.getAllProducts();
        List<Product> lowStockProducts = products.stream()
                .filter(product -> {
                    try {
                        return productService.getTotalCountForProduct(product.getId()) < 10; // Assuming 10 is the low stock threshold
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
        currentReportData = lowStockProducts;
        Chart chart = generateInventoryChart(lowStockProducts, "Low Stock Alert Report");
        showChartInReportArea(chart);
    }

    private Chart generateInventoryChart(List<Product> products, String title) {
        PieChart chart = new PieChart();
        chart.setTitle(title);

        Map<Product.ProductType, Long> productCounts = products.stream()
                .collect(Collectors.groupingBy(Product::getType, Collectors.counting()));

        productCounts.forEach((type, count) ->
                chart.getData().add(new PieChart.Data(type.toString(), count))
        );

        return chart;
    }

    private void generateTopCustomersReport() throws SQLException {
        List<Customer> customers = customerService.getAll();
        List<Customer> topCustomers = customers.stream()
                .sorted(Comparator.comparing(this::getCustomerTotalSpend).reversed())
                .limit(10)
                .collect(Collectors.toList());
        currentReportData = topCustomers;
        Chart chart = generateCustomerChart(topCustomers, "Top 10 Customers by Total Spend");
        showChartInReportArea(chart);
    }

    private void generateCustomerSpendingAnalysisReport() throws SQLException {
        List<Customer> customers = customerService.getAll();
        currentReportData = customers;
        Chart chart = generateCustomerSpendingAnalysisChart(customers);
        showChartInReportArea(chart);
    }

    private Chart generateCustomerChart(List<Customer> customers, String title) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Spend");

        for (Customer customer : customers) {
            series.getData().add(new XYChart.Data<>(customer.getName(), getCustomerTotalSpend(customer)));
        }

        chart.getData().add(series);
        return chart;
    }

    private Chart generateCustomerSpendingAnalysisChart(List<Customer> customers) {
        ScatterChart<Number, Number> chart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        chart.setTitle("Customer Spending Analysis");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Total Spend vs. Shop Times");

        for (Customer customer : customers) {
            double totalSpend = getCustomerTotalSpend(customer);
            int shopTimes = getCustomerShopTimes(customer);
            series.getData().add(new XYChart.Data<>(shopTimes, totalSpend));
        }

        chart.getData().add(series);
        return chart;
    }


    private int getCustomerShopTimes(Customer customer) {
        try {
            return customerService.getCustomerShopTimes(customer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void exportToPDF() {
        System.out.println("Exporting to PDF");
        if (currentReportData == null || currentReportTitle == null) {
            showAlert("No report data", "Please generate a report before exporting.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
                writer.println(currentReportTitle);
                writer.println();

                for (Object item : currentReportData) {
                    writer.println(item.toString());
                }

                showAlert("error","Report exported to PDF successfully.");
            } catch (IOException e) {
                showAlert("error","Error exporting report to PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportToCSV() {
        if (currentReportData == null || currentReportTitle == null) {
            showAlert("No report data", "Please generate a report before exporting.");
            return;
        }
        if (fileHandler == null) {
            showAlert("Export error", "File handler is not initialized.");
            return;
        }

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Date", "Total Sales"});

        Map<LocalDate, BigDecimal> salesByDate = new HashMap<>();

        try {
            for (Object item : currentReportData) {
                if (item instanceof ShoppingCart) {
                    ShoppingCart cart = (ShoppingCart) item;
                    LocalDate date = cart.getCreatedAt().toLocalDate();
                    BigDecimal total = shoppingCartService.getCartTotal(cart.getId());
                    System.out.println("Cart " + cart.getId() + " total: " + total);
                    salesByDate.merge(date, total, BigDecimal::add);
                }
            }

            for (Map.Entry<LocalDate, BigDecimal> entry : salesByDate.entrySet()) {
                csvData.add(new String[]{entry.getKey().toString(), entry.getValue().toString()});
            }

            fileHandler.exportToCSV(csvData, currentReportTitle + ".csv", primaryStage);
        } catch (SQLException e) {
            showAlert("Export error", "Error calculating cart totals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateDailySalesReport() throws SQLException {
        LocalDate today = LocalDate.now();
        List<ShoppingCart> todaysPurchases = shoppingCartService.getAll().stream()
                .filter(cart -> cart.getCreatedAt().toLocalDate().equals(today))
                .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Sales");

        Map<LocalDate, BigDecimal> salesByDate = new HashMap<>();

        for (ShoppingCart cart : todaysPurchases) {
            LocalDate date = cart.getCreatedAt().toLocalDate();
            BigDecimal total = shoppingCartService.getCartTotal(cart.getId());
            salesByDate.merge(date, total, BigDecimal::add);
        }

        salesByDate.forEach((date, total) ->
                series.getData().add(new XYChart.Data<>(date.toString(), total.doubleValue()))
        );

        salesTrendChart.getData().clear();
        salesTrendChart.getData().add(series);

        currentReportData = todaysPurchases;
        currentReportTitle = "Daily Sales Report";
    }

    private void generateMonthlySalesReport() throws SQLException {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        List<ShoppingCart> monthlyPurchases = shoppingCartService.getAll().stream()
                .filter(cart -> cart.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Sales");

        Map<LocalDate, BigDecimal> salesByDate = new HashMap<>();

        for (ShoppingCart cart : monthlyPurchases) {
            LocalDate date = cart.getCreatedAt().toLocalDate();
            BigDecimal total = shoppingCartService.getCartTotal(cart.getId());
            salesByDate.merge(date, total, BigDecimal::add);
        }

        salesByDate.forEach((date, total) ->
                series.getData().add(new XYChart.Data<>(date.toString(), total.doubleValue()))
        );

        salesTrendChart.getData().clear();
        salesTrendChart.getData().add(series);

        currentReportData = monthlyPurchases;
        currentReportTitle = "Monthly Sales Report";
    }

    private void generateAnnualSalesReport() throws SQLException {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        List<ShoppingCart> yearlyPurchases = shoppingCartService.getAll().stream()
                .filter(cart -> cart.getCreatedAt().toLocalDate().isAfter(startOfYear.minusDays(1)))
                .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Sales");

        Map<LocalDate, BigDecimal> salesByDate = new HashMap<>();

        for (ShoppingCart cart : yearlyPurchases) {
            LocalDate date = cart.getCreatedAt().toLocalDate();
            BigDecimal total = shoppingCartService.getCartTotal(cart.getId());
            salesByDate.merge(date, total, BigDecimal::add);
        }

        salesByDate.forEach((date, total) ->
                series.getData().add(new XYChart.Data<>(date.toString(), total.doubleValue()))
        );

        salesTrendChart.getData().clear();
        salesTrendChart.getData().add(series);

        currentReportData = yearlyPurchases;
        currentReportTitle = "Annual Sales Report";
    }

    private void generateInventoryReport() {
        try {
            List<Product> products = productService.getAllProducts();
            currentReportData = products;
            Chart chart = generateInventoryChart(products);
            showChartInReportArea(chart);
        } catch (SQLException e) {
            showAlert("error","Error generating inventory report: " + e.getMessage());
        }
    }

    private void generateCustomerReport() {
        try {
            List<Customer> customers = customerService.getAll();
            currentReportData = customers;
            Chart chart = generateCustomerChart(customers);
            showChartInReportArea(chart);
        } catch (SQLException e) {
            showAlert("error","Error generating customer report: " + e.getMessage());
        }
    }



    private void generateSupplierReport() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            currentReportData = suppliers;
            Chart chart = generateSupplierChart(suppliers);
            showChartInReportArea(chart);
        } catch (SQLException e) {
            showAlert("error","Error generating supplier report: " + e.getMessage());
        }
    }

    private void generateSupermarketPerformanceReport() {
        try {
            List<Supermarket> supermarkets = supermarketService.getAllSupermarkets();
            currentReportData = supermarkets;
            Chart chart = generateSupermarketPerformanceChart(supermarkets);
            showChartInReportArea(chart);
        } catch (SQLException e) {
            showAlert("error","Error generating supermarket performance report: " + e.getMessage());
        }
    }

    private Chart generateSalesChart(List<ShoppingCart> purchases) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(currentReportTitle);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Sales");

        Map<LocalDate, BigDecimal> salesByDate = purchases.stream()
                .collect(Collectors.groupingBy(
                        cart -> cart.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, ShoppingCart::getTotalPrice, BigDecimal::add)
                ));

        salesByDate.forEach((date, total) ->
                series.getData().add(new XYChart.Data<>(date.toString(), total.doubleValue()))
        );

        chart.getData().add(series);
        return chart;
    }

    private Chart generateInventoryChart(List<Product> products) {
        PieChart chart = new PieChart();
        chart.setTitle(currentReportTitle);

        Map<Product.ProductType, Long> productCounts = products.stream()
                .collect(Collectors.groupingBy(Product::getType, Collectors.counting()));

        productCounts.forEach((type, count) ->
                chart.getData().add(new PieChart.Data(type.toString(), count))
        );

        return chart;
    }

    private Chart generateCustomerChart(List<Customer> customers) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(currentReportTitle);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Spend");

        customers.stream()
                .sorted(Comparator.comparing(this::getCustomerTotalSpend).reversed())
                .limit(10)
                .forEach(customer -> series.getData().add(
                        new XYChart.Data<>(customer.getName(), getCustomerTotalSpend(customer))
                ));

        chart.getData().add(series);
        return chart;
    }

    private Chart generateSupplierChart(List<Supplier> suppliers) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(currentReportTitle);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of Products Supplied");

        for (Supplier supplier : suppliers) {
            try {
                int productCount = productService.getProductsBySupplier(supplier.getSupplierId()).size();
                series.getData().add(new XYChart.Data<>(supplier.getName(), productCount));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        chart.getData().add(series);
        return chart;
    }

    private Chart generateSupermarketPerformanceChart(List<Supermarket> supermarkets) {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle(currentReportTitle);

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Total Sales");

        XYChart.Series<String, Number> customerSeries = new XYChart.Series<>();
        customerSeries.setName("Customer Count");

        for (Supermarket supermarket : supermarkets) {
            try {
                double totalSales = getSupermarketTotalSales(supermarket);
                int customerCount = getSupermarketCustomerCount(supermarket);

                salesSeries.getData().add(new XYChart.Data<>(supermarket.getName(), totalSales));
                customerSeries.getData().add(new XYChart.Data<>(supermarket.getName(), customerCount));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        chart.getData().addAll(salesSeries, customerSeries);
        return chart;
    }

    private double getCustomerTotalSpend(Customer customer) {
        try {
            return customerService.getTotalSpend(customer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getSupermarketTotalSales(Supermarket supermarket) throws SQLException {
        List<ShoppingCart> carts = shoppingCartService.getShoppingCartsBySupermarketId(supermarket.getId());
        return carts.stream()
                .mapToDouble(cart -> cart.getTotalPrice().doubleValue())
                .sum();
    }

    private int getSupermarketCustomerCount(Supermarket supermarket) throws SQLException {
        return customerService.getBySupermarket(supermarket.getId()).size();
    }

    private void showChartInReportArea(Chart chart) {
        reportContentArea.getChildren().clear();
        reportContentArea.getChildren().add(chart);
    }

    private void generateSupplierProductCountReport() throws SQLException {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        currentReportData = suppliers;
        Chart chart = generateSupplierProductCountChart(suppliers);
        showChartInReportArea(chart);
    }

    private Chart generateSupplierProductCountChart(List<Supplier> suppliers) throws SQLException {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Supplier Product Count");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of Products Supplied");

        for (Supplier supplier : suppliers) {
            int productCount = productService.getProductsBySupplier(supplier.getSupplierId()).size();
            series.getData().add(new XYChart.Data<>(supplier.getName(), productCount));
        }

        chart.getData().add(series);
        return chart;
    }


}