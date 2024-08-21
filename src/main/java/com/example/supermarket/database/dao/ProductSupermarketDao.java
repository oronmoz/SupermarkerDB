package com.example.supermarket.database.dao;

import java.sql.SQLException;
import java.util.List;

public interface ProductSupermarketDao {
    void addProductToSupermarket(int productId, int supermarketId, int stock) throws SQLException;
    void removeProductFromSupermarket(int productId, int supermarketId) throws SQLException;
    void updateStock(int productId, int supermarketId, int stock) throws SQLException;
    int getStock(int productId, int supermarketId) throws SQLException;
    List<Integer> getSupermarketIdsForProduct(int productId) throws SQLException;
    List<Integer> getProductIdsForSupermarket(int supermarketId) throws SQLException;

}