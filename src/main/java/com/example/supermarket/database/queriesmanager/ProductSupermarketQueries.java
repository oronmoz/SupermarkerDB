package com.example.supermarket.database.queriesmanager;

public class ProductSupermarketQueries {
    public static final String CREATE_SUPERMARKET_PRODUCT_TABLE =
            "CREATE TABLE IF NOT EXISTS SUPERMARKET_PRODUCT (" +
                    "supermarket_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "stock INTEGER NOT NULL, " +
                    "PRIMARY KEY (supermarket_id, product_id), " +
                    "FOREIGN KEY (supermarket_id) REFERENCES supermarkets(id), " +
                    "FOREIGN KEY (product_id) REFERENCES products(id))";

    public static final String ADD_PRODUCT_TO_SUPERMARKET =
            "INSERT INTO SUPERMARKET_PRODUCT (supermarket_id, product_id, stock) VALUES (?, ?, ?)";

    public static final String REMOVE_PRODUCT_FROM_SUPERMARKET =
            "DELETE FROM SUPERMARKET_PRODUCT WHERE supermarket_id = ? AND product_id = ?";

    public static final String UPDATE_STOCK =
            "UPDATE SUPERMARKET_PRODUCT SET stock = ? WHERE supermarket_id = ? AND product_id = ?";

    public static final String GET_STOCK =
            "SELECT stock FROM SUPERMARKET_PRODUCT WHERE supermarket_id = ? AND product_id = ?";

    public static final String GET_SUPERMARKET_IDS_FOR_PRODUCT =
            "SELECT supermarket_id FROM SUPERMARKET_PRODUCT WHERE product_id = ?";

    public static final String GET_PRODUCT_IDS_FOR_SUPERMARKET =
            "SELECT product_id FROM SUPERMARKET_PRODUCT WHERE supermarket_id = ?";
}