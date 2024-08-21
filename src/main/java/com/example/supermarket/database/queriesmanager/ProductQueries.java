package com.example.supermarket.database.queriesmanager;

public class ProductQueries {
    public static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "barcode VARCHAR(7) UNIQUE NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "supplier_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (supplier_id) REFERENCES suppliers(id))";

    public static final String INSERT_PRODUCT =
            "INSERT INTO products (name, barcode, type, price, supplier_id) VALUES (?, ?, ?, ?, ?) RETURNING id";

    public static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM products WHERE id = ?";

    public static final String SELECT_ALL_PRODUCTS =
            "SELECT * FROM products";

    public static final String SELECT_PRODUCT_BY_BARCODE =
            "SELECT * FROM products WHERE barcode = ?";

    public static final String UPDATE_PRODUCT =
            "UPDATE products SET name = ?, type = ?, price = ?, supplier_id = ? WHERE id = ?";

    public static final String DELETE_PRODUCT =
            "DELETE FROM products WHERE id = ?";

    public static final String SELECT_PRODUCTS_BY_TYPE =
            "SELECT * FROM products WHERE type = ?";

    public static final String SELECT_PRODUCTS_BY_SUPERMARKET =
            "SELECT p.* FROM products p " +
                    "JOIN SUPERMARKET_PRODUCT sp ON p.id = sp.product_id " +
                    "WHERE sp.supermarket_id = ?";

    public static final String SELECT_PRODUCTS_BY_SUPPLIER =
            "SELECT * FROM products WHERE supplier_id = ?";

    public static final String UPDATE_PRODUCT_COUNT =
            "UPDATE products SET count = count + ? WHERE barcode = ?"; // Not Correct
}