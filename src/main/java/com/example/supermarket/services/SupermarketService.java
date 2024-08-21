package com.example.supermarket.services;

import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.models.Supermarket;
import com.example.supermarket.models.Product;
import com.example.supermarket.models.Customer;
import com.example.supermarket.utils.InsufficientStockException;

import java.sql.SQLException;
import java.util.List;

public interface SupermarketService {
    List<Supermarket> getAll() throws SQLException;
    Supermarket addSupermarket(Supermarket supermarket) throws SQLException;
    List<Supermarket> viewSupermarkets() throws SQLException;
    void updateSupermarket(Supermarket supermarket) throws SQLException;
    void deleteSupermarket(int id) throws SQLException;
    Supermarket getSupermarketById(int id) throws SQLException;
    List<Product> getProductsBySupermarket(int supermarketId) throws SQLException;
    List<Supermarket> getAllSupermarkets() throws SQLException;
    void addCustomerToSupermarketIfNotExists(int customerId, int supermarketId) throws SQLException;
    void updateAfterPurchase(ShoppingCart cart) throws SQLException, InsufficientStockException;
    int getProductStock(int productId, int supermarketId) throws SQLException;
    Supermarket addOrUpdateSupermarket(Supermarket supermarket) throws SQLException;
}
