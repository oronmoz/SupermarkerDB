package com.example.supermarket.database.queriesmanager;

public class SupplierQueries {
    public static final String CREATE_SUPPLIER_TABLE =
            "CREATE TABLE IF NOT EXISTS suppliers (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "contact_info VARCHAR(255))";

    public static final String INSERT_SUPPLIER =
            "INSERT INTO suppliers (name, contact_info) VALUES (?, ?) RETURNING id";

    public static final String SELECT_ALL_SUPPLIERS =
            "SELECT * FROM suppliers";

    public static final String SELECT_SUPPLIER_BY_ID =
            "SELECT * FROM suppliers WHERE id = ?";

    public static final String UPDATE_SUPPLIER =
            "UPDATE suppliers SET name = ?, contact_info = ? WHERE id = ?";

    public static final String DELETE_SUPPLIER =
            "DELETE FROM suppliers WHERE id = ?";
}