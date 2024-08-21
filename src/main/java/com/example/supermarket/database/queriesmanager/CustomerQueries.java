package com.example.supermarket.database.queriesmanager;

public class CustomerQueries {
    public static final String CREATE_CUSTOMER_TABLE =
            "CREATE TABLE IF NOT EXISTS customers (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "address_id INTEGER, " +
                    "FOREIGN KEY (address_id) REFERENCES addresses(id))";

    public static final String INSERT_CUSTOMER =
            "INSERT INTO customers (name, address_id) VALUES (?, ?) RETURNING id";

    public static final String SELECT_ALL_CUSTOMERS =
            "SELECT c.*, a.num, a.street, a.city " +
                    "FROM customers c " +
                    "LEFT JOIN addresses a ON c.address_id = a.id";

    public static final String SELECT_CUSTOMER_BY_NAME =
            "SELECT c.*, a.num, a.street, a.city " +
                    "FROM customers c " +
                    "LEFT JOIN addresses a ON c.address_id = a.id " +
                    "WHERE c.name = ?";

    public static final String SELECT_CUSTOMER_BY_ID =
            "SELECT c.*, a.num, a.street, a.city " +
                    "FROM customers c " +
                    "LEFT JOIN addresses a ON c.address_id = a.id " +
                    "WHERE c.id = ?";

    public static final String UPDATE_CUSTOMER =
            "UPDATE customers SET name = ?, address_id = ? WHERE id = ?";

    public static final String DELETE_CUSTOMER =
            "DELETE FROM customers WHERE id = ?";

    public static final String SELECT_CUSTOMERS_BY_SUPERMARKET =
            "SELECT c.*, a.num, a.street, a.city " +
                    "FROM customers c " +
                    "JOIN customer_supermarket cs ON c.id = cs.customer_id " +
                    "LEFT JOIN addresses a ON c.address_id = a.id " +
                    "WHERE cs.supermarket_id = ?";

    public static final String SELECT_SORTED_CUSTOMERS =
            "SELECT c.*, a.num, a.street, a.city " +
                    "FROM customers c " +
                    "LEFT JOIN addresses a ON c.address_id = a.id " +
                    "ORDER BY %s %s";
}