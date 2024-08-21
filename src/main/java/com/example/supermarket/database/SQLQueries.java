package com.example.supermarket.database;

public class SQLQueries {
    // Product queries
    public static final String INSERT_PRODUCT = "INSERT INTO products (name, barcode, type, price, count) VALUES (?, ?, ?, ?, ?)";
    public static final String SELECT_ALL_PRODUCTS = "SELECT * FROM products";
    public static final String SELECT_PRODUCT_BY_BARCODE = "SELECT * FROM products WHERE barcode = ?";
    public static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, type = ?, price = ?, count = ? WHERE barcode = ?";
    public static final String DELETE_PRODUCT = "DELETE FROM products WHERE barcode = ?";

    // Customer queries
    public static final String INSERT_CUSTOMER = "INSERT INTO customers (name, shop_times, total_spend) VALUES (?, ?, ?)";
    public static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM customers";
    public static final String SELECT_CUSTOMER_BY_NAME = "SELECT * FROM customers WHERE name = ?";
    public static final String UPDATE_CUSTOMER = "UPDATE customers SET shop_times = ?, total_spend = ? WHERE name = ?";
    public static final String DELETE_CUSTOMER = "DELETE FROM customers WHERE name = ?";

    // Add more queries as needed for other entities and operations
}