package com.example.supermarket.services.impl;

import com.example.supermarket.database.dao.ShoppingCartDao;
import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.models.ShoppingItem;
import com.example.supermarket.services.ShoppingCartService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ShoppingCartServiceImpl implements ShoppingCartService {
    private ShoppingCartDao shoppingCartDao;

    public ShoppingCartServiceImpl(ShoppingCartDao shoppingCartDao) {
        this.shoppingCartDao = shoppingCartDao;
    }

    @Override
    public ShoppingCart createShoppingCart(int customerId, int supermarketId) throws SQLException {
        return shoppingCartDao.create(customerId, supermarketId);
    }

    @Override
    public ShoppingCart getCartByCustomerAndSupermarket(int customerId, int supermarketId) {
        try {
            return shoppingCartDao.getCartByCustomerAndSupermarket(customerId, supermarketId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void checkout(int customerId, int supermarketId) throws SQLException {
        ShoppingCart cart = getCartByCustomerAndSupermarket(customerId, supermarketId);
        if (cart != null) {
            deleteCart(cart.getId());
        }
    }

    @Override
    public void deleteCart(int cartId) throws SQLException {
        shoppingCartDao.deleteCart(cartId);
    }


    @Override
    public List<ShoppingCart> getAll() throws SQLException {
        return shoppingCartDao.getAll();
    }

    @Override
    public List<ShoppingCart> getShoppingCartsBySupermarketId(int supermarketId) throws SQLException {
        return shoppingCartDao.getCartsByCustomerId(supermarketId);
    }


    @Override
    public void saveCart(ShoppingCart cart) throws SQLException {
        ShoppingCart savedCart = shoppingCartDao.create(cart.getCustomerId(), cart.getSupermarketId());
        for (ShoppingItem item : cart.getItems()) {
            item.setCartId(savedCart.getId());
            shoppingCartDao.addItem(savedCart.getId(), item);
        }
    }

    @Override
    public BigDecimal getCartTotal(int cartId) throws SQLException {
        return shoppingCartDao.calculateCartTotal(cartId);
    }

}