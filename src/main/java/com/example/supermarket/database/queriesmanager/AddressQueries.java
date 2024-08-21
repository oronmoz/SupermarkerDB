package com.example.supermarket.database.queriesmanager;

public class AddressQueries {
    public static final String CREATE_ADDRESS_TABLE =
            "CREATE TABLE IF NOT EXISTS addresses (" +
                    "id SERIAL PRIMARY KEY, " +
                    "num INTEGER NOT NULL, " +
                    "street VARCHAR(255) NOT NULL, " +
                    "city VARCHAR(255) NOT NULL)";

    public static final String INSERT_ADDRESS =
            "INSERT INTO addresses (num, street, city) VALUES (?, ?, ?) RETURNING id";

    public static final String UPDATE_ADDRESS =
            "UPDATE addresses SET num = ?, street = ?, city = ? WHERE id = ?";

    public static final String DELETE_ADDRESS =
            "DELETE FROM addresses WHERE id = ?";

    public static final String SELECT_ADDRESS_BY_ID =
            "SELECT * FROM addresses WHERE id = ?";
}