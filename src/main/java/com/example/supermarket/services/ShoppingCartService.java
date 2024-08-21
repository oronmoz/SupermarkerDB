package com.example.supermarket.services;

import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.models.ShoppingItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ShoppingCartService {
    ShoppingCart createShoppingCart(int customerId, int supermarketId) throws SQLException;
    ShoppingCart getCartByCustomerAndSupermarket(int customerId, int supermarketId);
    List<ShoppingCart> getAll() throws SQLException;
    List<ShoppingCart> getShoppingCartsBySupermarketId(int supermarketId) throws SQLException;
    void checkout(int customerId, int supermarketId) throws SQLException;
    void deleteCart(int cartId) throws SQLException;
    void saveCart(ShoppingCart cart) throws SQLException;
    BigDecimal getCartTotal(int cartId) throws SQLException;

}