package com.example.supermarket.database.queriesmanager;

public class ShoppingCartQueries {
    public static final String CREATE_SHOPPING_CART_TABLE =
            "CREATE TABLE IF NOT EXISTS shopping_carts (" +
                    "id SERIAL PRIMARY KEY, " +
                    "customer_id INTEGER NOT NULL, " +
                    "supermarket_id INTEGER NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id), " +
                    "FOREIGN KEY (supermarket_id) REFERENCES supermarkets(id))";


    public static final String INSERT_SHOPPING_CART =
            "INSERT INTO shopping_carts (customer_id, supermarket_id) VALUES (?, ?) RETURNING id";

    public static final String INSERT_SHOPPING_ITEM =
            "INSERT INTO shopping_items (cart_id, product_barcode, quantity, price) VALUES (?, ?, ?, ?)";

    public static final String SELECT_CART_BY_CUSTOMER_AND_SUPERMARKET =
            "SELECT * FROM shopping_carts WHERE customer_id = ? AND supermarket_id = ? " +
                    "ORDER BY created_at DESC LIMIT 1";

    public static final String SELECT_CART_BY_CUSTOMER_ID =
            "SELECT * FROM shopping_carts WHERE customer_id = ? ORDER BY created_at DESC";

    public static final String SELECT_CART_BY_SUPERMARKET_ID =
            "SELECT * FROM shopping_carts WHERE supermarket_id = ? ORDER BY created_at DESC";

    public static final String SELECT_ITEMS_BY_CART_ID =
            "SELECT * FROM shopping_items WHERE cart_id = ?";

    public static final String DELETE_CART =
            "DELETE FROM shopping_carts WHERE id = ?";

    public static final String DELETE_ITEMS_BY_CART_ID =
            "DELETE FROM shopping_items WHERE cart_id = ?";

    public static final String SELECT_TOTAL_PRICE_BY_CART_ID =
            "SELECT SUM(quantity * price) AS total_price FROM shopping_items WHERE cart_id = ?";

    public static final String UPDATE_ITEM_QUANTITY =
            "UPDATE shopping_items SET quantity = ? WHERE cart_id = ? AND id = ?";

    public static final String UPDATE_ITEMS =
            "UPDATE shopping_items SET quantity = ?, price = ? WHERE cart_id = ? AND product_barcode = ?";

    public static final String SELECT_ALL_CARTS = "SELECT * FROM shopping_carts";

    public static final String DELETE_ITEM = "DELETE FROM shopping_items WHERE cart_id = ? AND id = ?";
}