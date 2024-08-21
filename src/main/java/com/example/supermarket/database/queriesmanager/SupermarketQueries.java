package com.example.supermarket.database.queriesmanager;

public class SupermarketQueries {

    public static final String CREATE_SUPERMARKET_TABLE =
            "CREATE TABLE IF NOT EXISTS supermarkets (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "location_id INTEGER, " +
                    "FOREIGN KEY (location_id) REFERENCES addresses(id))";

    public static final String INSERT_SUPERMARKET =
            "INSERT INTO supermarkets (name, location_id) VALUES (?, ?) RETURNING id";

    public static final String GET_ALL_SUPERMARKETS =
            "SELECT s.*, a.num, a.street, a.city " +
                    "FROM supermarkets s " +
                    "LEFT JOIN addresses a ON s.location_id = a.id";

    public static final String GET_SUPERMARKET_BY_NAME =
            "SELECT s.*, a.num, a.street, a.city " +
                    "FROM supermarkets s " +
                    "LEFT JOIN addresses a ON s.location_id = a.id " +
                    "WHERE s.id = ?";

    public static final String UPDATE_SUPERMARKET =
            "UPDATE supermarkets SET name = ?, location_id = ? WHERE id = ?";

    public static final String DELETE_SUPERMARKET =
            "DELETE FROM supermarkets WHERE id = ?";

    public static final String GET_SUPERMARKET_BY_ID =
            "SELECT s.*, a.* FROM supermarkets s " +
                    "JOIN addresses a ON s.location_id = a.id " +
                    "WHERE s.id = ?";

    public static final String GET_SORTED_SUPERMARKETS =
            "SELECT s.*, a.* FROM supermarkets s " +
                    "JOIN addresses a ON s.location_id = a.id " +
                    "ORDER BY %s %s";

    public static final String SEARCH_SUPERMARKETS_BY_NAME =
            "SELECT s.*, a.* FROM supermarkets s " +
                    "JOIN addresses a ON s.location_id = a.id " +
                    "WHERE s.name LIKE ?";

    public static final String GET_SUPERMARKETS_BY_PRODUCT =
            "SELECT s.*, a.* FROM supermarkets s " +
                    "JOIN SUPERMARKET_PRODUCT sp ON s.id = sp.supermarket_id " +
                    "JOIN addresses a ON s.location_id = a.id " +
                    "WHERE sp.product_id = ?";

    public static final String GET_SUPERMARKETS_BY_IDS =
            "SELECT s., a. FROM supermarkets s " +
                    "JOIN addresses a ON s.location_id = a.id " +
                    "WHERE s.id IN (%s)";

    public static final String GET_CUSTOMERS_BY_SUPERMARKET =
            "SELECT c.* FROM customers c " +
                    "JOIN customer_supermarket cs ON c.id = cs.customer_id " +
                    "WHERE cs.supermarket_id = ?";

    public static final String ADD_CUSTOMER_TO_SUPERMARKET =
            "INSERT INTO customer_supermarket (customer_id, supermarket_id, shop_times, total_spend) " +
                    "VALUES (?, ?, 0, 0.00)";

    public static final String REMOVE_CUSTOMER_FROM_SUPERMARKET =
            "DELETE FROM customer_supermarket WHERE customer_id = ? AND supermarket_id = ?";
}