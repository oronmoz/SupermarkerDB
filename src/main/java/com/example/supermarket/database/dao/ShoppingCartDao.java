package com.example.supermarket.database.dao;

import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.models.ShoppingItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ShoppingCartDao {
    ShoppingCart create(int customerId, int supermarketId) throws SQLException;
    void addItem(int cartId, ShoppingItem item) throws SQLException;
    List<ShoppingItem> getItemsByCartId(int cartId) throws SQLException;
    void deleteCart(int cartId) throws SQLException;
    float getTotalPrice(int cartId) throws SQLException;
    List<ShoppingCart> getCartsByCustomerId(int customerId) throws SQLException;
    List<ShoppingCart> getCartsBySupermarketId(int supermarketId) throws SQLException;
    ShoppingCart getCartByCustomerAndSupermarket(int customerId, int supermarketId) throws SQLException;
    void updateItemQuantity(int cartId, int itemId, int newQuantity) throws SQLException;
    void updateItems(int cartId, List<ShoppingItem> items) throws SQLException;
    void removeItem(int cartId, int itemId) throws SQLException;
    List<ShoppingCart> getAll() throws SQLException;
    public ShoppingCart getByCustomerId(int customerId, int supermarketId) throws SQLException;
    BigDecimal calculateCartTotal(int cartId) throws SQLException;

}
