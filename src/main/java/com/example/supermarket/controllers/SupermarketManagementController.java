package com.example.supermarket.controllers;

import com.example.supermarket.components.AdvancedSearchBar;
import com.example.supermarket.components.SortableFilterableTableView;
import com.example.supermarket.components.ReusableFormLayout;
import com.example.supermarket.models.*;
import com.example.supermarket.services.*;
import com.example.supermarket.managers.SearchManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.supermarket.managers.SupermarketDialogCreator.showAlert;
import static java.lang.Integer.valueOf;

public class SupermarketManagementController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SupermarketManagementController.class.getName());


    // Supermarket Management
    @FXML private AdvancedSearchBar supermarketSearchBar;
    @FXML private SortableFilterableTableView<Supermarket> supermarketsTable;
    @FXML private ReusableFormLayout supermarketForm;

    // Product Management
    @FXML private AdvancedSearchBar productSearchBar;
    @FXML private SortableFilterableTableView<Product> productsTable;
    @FXML private ReusableFormLayout productForm;
    private int editingProductId = 0;

    // Customer Management
    @FXML private AdvancedSearchBar customerSearchBar;
    @FXML private SortableFilterableTableView<Customer> customersTable;
    @FXML private ReusableFormLayout customerForm;
    private int editingCustomerId = 0;

    private SupermarketService supermarketService;
    private ProductService productService;
    private CustomerService customerService;
    private ShoppingCartService shoppingCartService;
    private SupplierService supplierService;

    private ObservableList<Supermarket> supermarkets;
    private ObservableList<Product> products;
    private ObservableList<Customer> customers;


    public void initServices(SupermarketService supermarketService, ProductService productService,
                             CustomerService customerService, ShoppingCartService shoppingCartService,
                             SupplierService supplierService) {
        this.supermarketService = supermarketService;
        this.productService = productService;
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.supplierService = supplierService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void postInitialize() throws SQLException {
        setupSupermarketManagement();
        setupProductManagement();
        setupCustomerManagement();
        loadAllData();
    }


    private void loadAllData() throws SQLException {
        loadSupermarkets();
        loadProducts();
        loadCustomers();
    }

    // Supermarket Management
    private void setupSupermarketManagement() {
        setupSupermarketSearchBar();
        setupSupermarketTable();
        setupSupermarketForm();
    }

    private void setupSupermarketSearchBar() {
        String[] searchCriteria = {"Name", "City"};
        supermarketSearchBar.initialize(searchCriteria, this::performSupermarketSearch, this::refreshSupermarketSearch);
    }

    private void refreshSupermarketSearch() {
        try {
            loadSupermarkets();
            supermarketSearchBar.clearSearch();
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh supermarkets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSupermarketTable() {
        TableView<Supermarket> tableView = supermarketsTable.getTableView();

        TableColumn<Supermarket, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Supermarket, String> cityColumn = new TableColumn<>("City");
        cityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getCity()));

        tableView.getColumns().addAll(nameColumn, cityColumn);
    }

    private void setupSupermarketForm() {
        supermarketForm.addField("Name", "Enter supermarket name");
        supermarketForm.addField("Number", "Enter street address");
        supermarketForm.addField("Street", "Enter city");
        supermarketForm.addField("City", "Enter state");
    }

    @FXML
    private void addSupermarket() {
        supermarketForm.setVisible(true);
        supermarketForm.clearFields();
        showDialog("Add Supermarket", supermarketForm, () -> {
            try {
                saveSupermarket();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void editSupermarket() {
        supermarketForm.setVisible(true);
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            populateSupermarketForm(selectedSupermarket);
            showDialog("Edit Supermarket", supermarketForm, () -> {
                try {
                    saveSupermarket();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            showAlert("No Selection", "Please select a supermarket to edit.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void deleteSupermarket() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            try {
                supermarketService.deleteSupermarket(selectedSupermarket.getId());
                supermarkets.remove(selectedSupermarket);
                showAlert("Success", "Supermarket deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                LOGGER.severe("Error deleting supermarket: " + e.getMessage());
                showAlert("Error", "Failed to delete supermarket. Error: " + e.getMessage() + "\nSee logs for more details.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a supermarket to delete.", Alert.AlertType.WARNING);
        }
    }

    private void performSupermarketSearch(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        supermarketsTable.setFilterPredicate(supermarket -> {
            switch (criteria) {
                case "Name":
                    return supermarket.getName().toLowerCase().contains(term);
                case "City":
                    return supermarket.getLocation().getCity().toLowerCase().contains(term);
                default:
                    return false;
            }
        });
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> showEditForm());
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            try {
                deleteSelectedSupermarket();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        MenuItem viewCustomersItem = new MenuItem("View Customers");
        viewCustomersItem.setOnAction(e -> viewCustomersForSupermarket());
        MenuItem viewProductsItem = new MenuItem("View Products");
        viewProductsItem.setOnAction(e -> viewProductsForSupermarket());

        contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), viewCustomersItem, viewProductsItem);

        supermarketsTable.getTableView().setContextMenu(contextMenu);
    }

    private void setupTableView() {
        TableView<Supermarket> tableView = supermarketsTable.getTableView();

        TableColumn<Supermarket, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Supermarket, String> cityColumn = new TableColumn<>("City");
        cityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getCity()));

        tableView.getColumns().addAll(nameColumn, cityColumn);
    }

    @FXML
    void showAddForm() {
        supermarketForm.clearFields();
    }

    @FXML
    private void showEditForm() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            supermarketForm.setFieldValue("Name", selectedSupermarket.getName());
            supermarketForm.setFieldValue("Street", selectedSupermarket.getLocation().getStreet());
            supermarketForm.setFieldValue("City", selectedSupermarket.getLocation().getCity());
        }
    }

    @FXML
    private void deleteSelectedSupermarket() throws SQLException {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            supermarketService.deleteSupermarket(selectedSupermarket.getId());
            supermarkets.remove(selectedSupermarket);
        }
    }

    @FXML
    private void saveSupermarket() throws SQLException {
        Supermarket supermarket = new Supermarket();
        var idTemp = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (idTemp != null) {
            supermarket.setId(idTemp.getId());
        }
        supermarket.setName(supermarketForm.getFieldValue("Name"));

        Address address = new Address();
        address.setStreet(supermarketForm.getFieldValue("Street"));
        address.setCity(supermarketForm.getFieldValue("City"));
        address.setNum(Integer.parseInt(supermarketForm.getFieldValue("Number")));

        // If it's an update, set the existing address ID
        if (idTemp != null && idTemp.getLocation() != null) {
            address.setId(idTemp.getLocation().getId());
        }

        supermarket.setLocation(address);

        // Use the new addOrUpdateSupermarket method
        supermarketService.addOrUpdateSupermarket(supermarket);

        // Refresh the table
        supermarkets.setAll(supermarketService.getAllSupermarkets());
    }

    public void setSupermarketService(SupermarketService supermarketService) {
        this.supermarketService = supermarketService;
    }

    public void initializeView() throws SQLException {
        setupTableView();
        loadSupermarkets();
    }



    // Product Management
    private void setupProductManagement() {
        setupProductSearchBar();
        setupProductTable();
        setupProductForm();
    }

    private void setupProductSearchBar() {
        String[] searchCriteria = {"Name", "Barcode", "Type"};
        productSearchBar.initialize(searchCriteria, this::performProductSearch, this::refreshProductSearch);
    }

    private void refreshProductSearch() {
        try {
            loadProducts();
            productSearchBar.clearSearch();
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupProductTable() {
        TableView<Product> tableView = productsTable.getTableView();

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Product, String> barcodeColumn = new TableColumn<>("Barcode");
        barcodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));

        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));

        TableColumn<Product, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrice().setScale(2, RoundingMode.HALF_UP).toString()));

        tableView.getColumns().addAll(nameColumn, barcodeColumn, typeColumn, priceColumn);
    }

    private void setupProductForm() {
        productForm.addField("Name", "Enter product name");
        productForm.addField("Barcode", "Enter barcode");

        // Add a ComboBox for product type selection
        ComboBox<Product.ProductType> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList(Product.ProductType.values()));
        typeComboBox.setPromptText("Select product type");
        typeComboBox.setCellFactory(listView -> new ListCell<Product.ProductType>() {
            @Override
            protected void updateItem(Product.ProductType item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getDisplayName());
                } else {
                    setText(null);
                }
            }
        });
        typeComboBox.setButtonCell(typeComboBox.getCellFactory().call(null));
        productForm.addCustomField("Type", typeComboBox);

        productForm.addField("Price", "Enter price");

        // Add a ComboBox for supplier selection
        ComboBox<Supplier> supplierComboBox = new ComboBox<>();
        supplierComboBox.setPromptText("Select a supplier");
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            supplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
            productForm.addCustomField("Supplier", supplierComboBox);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load suppliers", e);
            showAlert("Error", "Failed to load suppliers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void addProduct() {
        productForm.setVisible(true);
        productForm.clearFields();
        showDialog("Add Product", productForm, this::saveProduct);
    }

    @FXML
    private void editProduct() {
        productForm.setVisible(true);
        Product selectedProduct = productsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            editingProductId = selectedProduct.getId();
            populateProductForm(selectedProduct);
            showDialog("Edit Product", productForm, () -> {
                try {
                    Product updatedProduct = extractProductFromForm();
                    productService.updateProduct(updatedProduct);
                    loadProducts();
                    showAlert("Success", "Product updated successfully.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update product: " + e.getMessage(), Alert.AlertType.ERROR);
                } catch (IllegalArgumentException e) {
                    showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
                } finally {
                    editingProductId = 0; // Reset after editing
                }
            });
        } else {
            showAlert("No Selection", "Please select a product to edit.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void deleteProduct() {
        Product selectedProduct = productsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                productService.deleteProduct(selectedProduct.getId());
                products.remove(selectedProduct);
                productsTable.getTableView().refresh();
                showAlert("Success", "Product deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                LOGGER.severe("Error deleting product: " + e.getMessage());
                showAlert("Error", "Failed to delete product. Error: " + e.getMessage() + "\nSee logs for more details.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a product to delete.", Alert.AlertType.WARNING);
        }
    }

    private void saveProduct() {
        try {
            Product product = extractProductFromForm();
            if (product.getId() == 0) {
                productService.saveProduct(product);
            } else {
                productService.updateProduct(product);
            }
            loadProducts();
            showAlert("Success", "Product saved successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Failed to save product: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void performProductSearch(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        productsTable.setFilterPredicate(product -> {
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
        });
    }

    // Customer Management
    private void setupCustomerManagement() {
        setupCustomerSearchBar();
        setupCustomerTable();
        setupCustomerForm();
    }

    private void setupCustomerSearchBar() {
        String[] searchCriteria = {"Name", "Total Spend", "Shop Times"};
        customerSearchBar.initialize(searchCriteria, this::performCustomerSearch, this::refreshCustomerSearch);
    }

    private void refreshCustomerSearch() {
        try {
            loadCustomers();
            customerSearchBar.clearSearch();
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh customers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupCustomerTable() {
        TableView<Customer> tableView = customersTable.getTableView();

        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        tableView.getColumns().add(nameColumn);
    }

    private void setupCustomerForm() {
        customerForm.addField("Name", "Enter customer name");
        customerForm.addField("Number", "Enter house number");
        customerForm.addField("Street", "Enter street name");
        customerForm.addField("City", "Enter city name");
    }

    @FXML
    private void addCustomer() {
        customerForm.setVisible(true);
        customerForm.clearFields();
        showDialog("Add Customer", customerForm, this::saveCustomer);
    }

    @FXML
    private void editCustomer() {
        customerForm.setVisible(true);
        Customer selectedCustomer = customersTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            editingCustomerId = selectedCustomer.getId();
            populateCustomerForm(selectedCustomer);
            showDialog("Edit Customer", customerForm, () -> {
                try {
                    Customer updatedCustomer = extractCustomerFromForm();
                    customerService.update(updatedCustomer);
                    loadCustomers();
                    showAlert("Success", "Customer updated successfully.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update customer: " + e.getMessage(), Alert.AlertType.ERROR);
                } catch (IllegalArgumentException e) {
                    showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
                } finally {
                    editingCustomerId = 0; // Reset after editing
                }
            });
        } else {
            showAlert("No Selection", "Please select a customer to edit.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void deleteCustomer() {
        Customer selectedCustomer = customersTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            try {
                customerService.delete(selectedCustomer.getId());
                customers.remove(selectedCustomer);
                customersTable.getTableView().refresh();
                showAlert("Success", "Customer deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                LOGGER.severe("Error deleting customer: " + e.getMessage());
                showAlert("Error", "Failed to delete customer. Error: " + e.getMessage() + "\nSee logs for more details.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a customer to delete.", Alert.AlertType.WARNING);
        }
    }

    private void saveCustomer() {
        try {
            int tempId;
            Customer customer = extractCustomerFromForm();
            if (customersTable.getTableView().getSelectionModel().getSelectedItem()==null)
                tempId = 0;
            else
                tempId = customersTable.getTableView().getSelectionModel().getSelectedItem().getId();
            customer.setId(tempId);
            if (customer.getName() == null || customer.getName().isEmpty()) {
                throw new IllegalArgumentException("Customer name cannot be empty");
            }
            if (customer.getAddress() == null) {
                throw new IllegalArgumentException("Customer must have an address");
            }
            if (customer.getId() == 0) {
                customerService.save(customer);
            } else {
                customerService.update(customer);
            }
            loadCustomers();
            showAlert("Success", "Customer saved successfully.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) {
            showAlert("Invalid Input", e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Error", "Failed to save customer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void performCustomerSearch(String searchCriteria) {
        String[] parts = searchCriteria.split(":");
        String criteria = parts[0];
        String term = parts[1].toLowerCase();

        customersTable.setFilterPredicate(customer -> {
            switch (criteria) {
                case "Name":
                    return customer.getName().toLowerCase().contains(term);
                case "Total Spend":
                    try {
                        float spend = Float.parseFloat(term);
                        return customerService.getTotalSpend(customer.getId()) >= spend;
                    } catch (NumberFormatException | SQLException e) {
                        return false;
                    }
                case "Shop Times":
                    try {
                        int times = Integer.parseInt(term);
                        return customerService.getCustomerShopTimes(customer.getId()) >= times;
                    } catch (NumberFormatException | SQLException e) {
                        return false;
                    }
                default:
                    return false;
            }
        });
    }

    // Helper methods
    private void loadSupermarkets() throws SQLException {
        supermarkets = FXCollections.observableArrayList(supermarketService.getAllSupermarkets());
        supermarketsTable.setItems(supermarkets);
    }

    private void loadProducts() throws SQLException {
        products = FXCollections.observableArrayList(productService.getAllProducts());
        productsTable.setItems(products);
    }

    private void loadCustomers() throws SQLException {
        customers = FXCollections.observableArrayList(customerService.getAll());
        customersTable.setItems(customers);
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

    private Supermarket extractSupermarketFromForm() {
        Supermarket supermarket = new Supermarket();
        supermarket.setName(supermarketForm.getFieldValue("Name"));
        Address address = new Address();
        supermarket.setLocation(extractAddressFromForm());
        // Set other address fields as needed

        supermarket.setLocation(address);
        return supermarket;
    }

    private void populateSupermarketForm(Supermarket supermarket) {
        supermarketForm.setFieldValue("Name", supermarket.getName());
        supermarketForm.setFieldValue("Number", String.valueOf(supermarket.getLocation().getNum()));
        supermarketForm.setFieldValue("Street", supermarket.getLocation().getStreet());
        supermarketForm.setFieldValue("City", supermarket.getLocation().getCity());
        // Set other fields as needed
    }

    private Product extractProductFromForm() {
        Product product = new Product();
        if (editingProductId != 0) {
            product.setId(editingProductId);
        }
        product.setName(productForm.getFieldValue("Name"));
        product.setBarcode(productForm.getFieldValue("Barcode"));

        ComboBox<Product.ProductType> typeComboBox = (ComboBox<Product.ProductType>) productForm.getField("Type");
        Product.ProductType selectedType = typeComboBox.getValue();
        if (selectedType == null) {
            throw new IllegalArgumentException("Please select a product type.");
        }
        product.setType(selectedType);

        try {
            String priceString = productForm.getFieldValue("Price");
            BigDecimal price = new BigDecimal(priceString).setScale(2, RoundingMode.HALF_UP);
            product.setPrice(price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format. Please enter a valid number.");
        }

        ComboBox<Supplier> supplierComboBox = (ComboBox<Supplier>) productForm.getField("Supplier");
        Supplier selectedSupplier = supplierComboBox.getValue();
        if (selectedSupplier == null) {
            throw new IllegalArgumentException("Please select a supplier.");
        }
        product.setSupplierId(selectedSupplier.getSupplierId());

        return product;
    }

    private void populateProductForm(Product product) {
        productForm.setFieldValue("Name", product.getName());
        productForm.setFieldValue("Barcode", product.getBarcode());

        ComboBox<Product.ProductType> typeComboBox = (ComboBox<Product.ProductType>) productForm.getField("Type");
        typeComboBox.setValue(product.getType());

        productForm.setFieldValue("Price", product.getPrice().setScale(2, RoundingMode.HALF_UP).toString());

        ComboBox<Supplier> supplierComboBox = (ComboBox<Supplier>) productForm.getField("Supplier");
        supplierComboBox.getItems().stream()
                .filter(supplier -> supplier.getSupplierId() == product.getSupplierId())
                .findFirst()
                .ifPresent(supplierComboBox::setValue);
    }

    private Customer extractCustomerFromForm() {
        Customer customer = new Customer();
        if (editingCustomerId != 0) {
            customer.setId(editingCustomerId);
        }
        customer.setName(customerForm.getFieldValue("Name"));

        Address address = new Address();
        try {
            address.setNum(Integer.parseInt(customerForm.getFieldValue("Number")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid house number");
        }
        address.setStreet(customerForm.getFieldValue("Street"));
        address.setCity(customerForm.getFieldValue("City"));

        customer.setAddress(address);
        return customer;
    }


    private Address extractAddressFromForm() {
        Address address = new Address();
        address.setNum(Integer.parseInt(customerForm.getFieldValue("Number")));
        address.setStreet(customerForm.getFieldValue("Street"));
        address.setStreet(customerForm.getFieldValue("City"));
        // Set other customer fields as needed
        return address;
    }

    private void populateCustomerForm(Customer customer) {
        customerForm.setFieldValue("Name", customer.getName());
        if (customer.getAddress() != null) {
            customerForm.setFieldValue("Number", String.valueOf(customer.getAddress().getNum()));
            customerForm.setFieldValue("Street", customer.getAddress().getStreet());
            customerForm.setFieldValue("City", customer.getAddress().getCity());
        }
    }

    public void refreshView() throws SQLException {
        try {
            loadAllData();
        }catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    // Additional methods for advanced functionality

    @FXML
    private void viewCustomersForSupermarket() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            try {
                List<Customer> supermarketCustomers = customerService.getBySupermarket(selectedSupermarket.getId());
                showCustomerListDialog(selectedSupermarket.getName() + " Customers", supermarketCustomers);
            } catch (SQLException e) {
                showAlert("Error", "Failed to load customers: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a supermarket to view its customers.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void viewProductsForSupermarket() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            try {
                List<Product> supermarketProducts = productService.getProductsBySupermarket(selectedSupermarket.getId());
                showProductListDialog(selectedSupermarket.getName() + " Products", supermarketProducts);
            } catch (SQLException e) {
                showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a supermarket to view its products.", Alert.AlertType.WARNING);
        }
    }

    private void showCustomerListDialog(String title, List<Customer> customers) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);

        VBox content = new VBox(10);
        ListView<String> customerListView = new ListView<>();
        for (Customer customer : customers) {
            customerListView.getItems().add(customer.getName());
        }
        content.getChildren().add(customerListView);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showProductListDialog(String title, List<Product> products) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);

        VBox content = new VBox(10);
        ListView<String> productListView = new ListView<>();
        for (Product product : products) {
            productListView.getItems().add(product.getName() + " - " + product.getPrice());
        }
        content.getChildren().add(productListView);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void addProductToSupermarket() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            showAddProductToSupermarketDialog(selectedSupermarket);
        } else {
            showAlert("No Selection", "Please select a supermarket to add a product.", Alert.AlertType.WARNING);
        }
    }

    private void showAddProductToSupermarketDialog(Supermarket supermarket) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add/Update Product in " + supermarket.getName());

        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.setItems(FXCollections.observableArrayList(products));
        productComboBox.setPromptText("Select a product");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");

        VBox content = new VBox(10, new Label("Product:"), productComboBox, new Label("Quantity:"), quantityField);
        dialog.getDialogPane().setContent(content);

        ButtonType addButtonType = new ButtonType("Add/Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Product selectedProduct = productComboBox.getValue();
                if (selectedProduct != null) {
                    try {
                        int quantity = Integer.parseInt(quantityField.getText());
                        productService.addOrUpdateProductInSupermarket(selectedProduct.getId(), supermarket.getId(), quantity);
                        showAlert("Success", "Product added/updated in supermarket successfully.", Alert.AlertType.INFORMATION);
                    } catch (NumberFormatException e) {
                        showAlert("Invalid Input", "Please enter a valid quantity.", Alert.AlertType.ERROR);
                    } catch (SQLException e) {
                        showAlert("Error", "Failed to add/update product in supermarket: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void addCustomerToSupermarket() {
        Supermarket selectedSupermarket = supermarketsTable.getTableView().getSelectionModel().getSelectedItem();
        if (selectedSupermarket != null) {
            showAddCustomerToSupermarketDialog(selectedSupermarket);
        } else {
            showAlert("No Selection", "Please select a supermarket to add a customer.", Alert.AlertType.WARNING);
        }
    }

    private void showAddCustomerToSupermarketDialog(Supermarket supermarket) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Customer to " + supermarket.getName());

        ComboBox<Customer> customerComboBox = new ComboBox<>();
        customerComboBox.setItems(FXCollections.observableArrayList(customers));
        customerComboBox.setPromptText("Select a customer");

        VBox content = new VBox(10, new Label("Customer:"), customerComboBox);
        dialog.getDialogPane().setContent(content);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Customer selectedCustomer = customerComboBox.getValue();
                if (selectedCustomer != null) {
                    try {
                        customerService.addToSupermarket(selectedCustomer.getId(), supermarket.getId());
                        showAlert("Success", "Customer added to supermarket successfully.", Alert.AlertType.INFORMATION);
                    } catch (SQLException e) {
                        showAlert("Error", "Failed to add customer to supermarket: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }





}