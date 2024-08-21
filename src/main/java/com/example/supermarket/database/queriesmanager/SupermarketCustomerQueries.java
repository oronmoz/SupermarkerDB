package com.example.supermarket.database.queriesmanager;

public class SupermarketCustomerQueries {
    public static final String CREATE_CUSTOMER_SUPERMARKET_TABLE =
            "CREATE TABLE IF NOT EXISTS customer_supermarket (" +
                    "customer_id INTEGER NOT NULL, " +
                    "supermarket_id INTEGER NOT NULL, " +
                    "shop_times INTEGER NOT NULL DEFAULT 0, " +
                    "total_spend DECIMAL(10, 2) NOT NULL DEFAULT 0.00, " +
                    "PRIMARY KEY (customer_id, supermarket_id), " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id), " +
                    "FOREIGN KEY (supermarket_id) REFERENCES supermarkets(id))";

    public static final String INSERT_CUSTOMER_SUPERMARKET =
            "INSERT INTO customer_supermarket (customer_id, supermarket_id, shop_times, total_spend) " +
                    "VALUES (?, ?, ?, ?)";

    public static final String UPDATE_CUSTOMER_SUPERMARKET =
            "UPDATE customer_supermarket SET shop_times = ?, total_spend = ? " +
                    "WHERE customer_id = ? AND supermarket_id = ?";

    public static final String DELETE_CUSTOMER_SUPERMARKET =
            "DELETE FROM customer_supermarket WHERE customer_id = ? AND supermarket_id = ?";

    public static final String SELECT_CUSTOMER_SUPERMARKET =
            "SELECT * FROM customer_supermarket WHERE customer_id = ? AND supermarket_id = ?";

    public static final String SELECT_ALL_BY_CUSTOMER =
            "SELECT * FROM customer_supermarket WHERE customer_id = ?";

    public static final String SELECT_ALL_BY_SUPERMARKET =
            "SELECT * FROM customer_supermarket WHERE supermarket_id = ?";

    public static final String UPDATE_AFTER_SHOPPING =
            "UPDATE customer_supermarket SET shop_times = shop_times + 1, total_spend = total_spend + ? " +
                    "WHERE customer_id = ? AND supermarket_id = ?";
}