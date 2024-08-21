package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.queriesmanager.CustomerQueries;
import com.example.supermarket.database.queriesmanager.ShoppingCartQueries;
import com.example.supermarket.models.Customer;
import com.example.supermarket.models.ShoppingCart;
import com.example.supermarket.models.ShoppingItem;
import com.example.supermarket.database.dao.ShoppingCartDao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Float.parseFloat;

public class ShoppingCartDaoImpl implements ShoppingCartDao {
    private DatabaseManager dbManager;

    public ShoppingCartDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public ShoppingCart create(int customerId, int supermarketId) throws SQLException {
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(ShoppingCartQueries.INSERT_SHOPPING_CART, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, supermarketId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating shopping cart failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ShoppingCart cart = new ShoppingCart(customerId, supermarketId);
                    cart.setId(generatedKeys.getInt(1));
                    return cart;
                } else {
                    throw new SQLException("Creating shopping cart failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public ShoppingCart getByCustomerId(int customerId, int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_CART_BY_CUSTOMER_ID, customerId);
        if (rs.next()) {
            ShoppingCart cart = new ShoppingCart(customerId, supermarketId);
            cart.setId(rs.getInt("id"));
            cart.setItems(getItemsByCartId(cart.getId()));
            return cart;
        }
        return null;
    }

    @Override
    public void addItem(int cartId, ShoppingItem item) throws SQLException {
        dbManager.executeUpdate(ShoppingCartQueries.INSERT_SHOPPING_ITEM,
                cartId,
                item.getBarcode(),
                item.getQuantity(),
                item.getPrice());
    }

    @Override
    public List<ShoppingItem> getItemsByCartId(int cartId) throws SQLException {
        List<ShoppingItem> items = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_ITEMS_BY_CART_ID, cartId);
        while (rs.next()) {
            ShoppingItem item = new ShoppingItem(
                    rs.getInt("id"),
                    rs.getInt("cart_id"),
                    rs.getString("product_barcode"),
                    rs.getString("product_name"),
                    new BigDecimal(rs.getString("price")),
                    rs.getInt("quantity")
            );
            items.add(item);
        }
        return items;
    }


    @Override
    public void deleteCart(int cartId) throws SQLException {
        dbManager.executeUpdate(ShoppingCartQueries.DELETE_ITEMS_BY_CART_ID, cartId);
        dbManager.executeUpdate(ShoppingCartQueries.DELETE_CART, cartId);
    }

    @Override
    public float getTotalPrice(int cartId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_TOTAL_PRICE_BY_CART_ID, cartId);
        if (rs.next()) {
            return rs.getFloat("total_price");
        }
        return 0;
    }

    public List<ShoppingCart> getCartsByCustomerId(int customerId) throws SQLException {
        List<ShoppingCart> carts = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_CART_BY_CUSTOMER_ID, customerId);
        while (rs.next()) {
            carts.add(extractShoppingCartFromResultSet(rs));
        }
        return carts;
    }

    public List<ShoppingCart> getCartsBySupermarketId(int supermarketId) throws SQLException {
        List<ShoppingCart> carts = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_CART_BY_SUPERMARKET_ID, supermarketId);
        while (rs.next()) {
            carts.add(extractShoppingCartFromResultSet(rs));
        }
        return carts;
    }

    public ShoppingCart getCartByCustomerAndSupermarket(int customerId, int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_CART_BY_CUSTOMER_AND_SUPERMARKET, customerId, supermarketId);
        if (rs.next()) {
            return extractShoppingCartFromResultSet(rs);
        }
        return null;
    }

    private ShoppingCart extractShoppingCartFromResultSet(ResultSet rs) throws SQLException {
        ShoppingCart cart = new ShoppingCart(rs.getInt("customer_id"), rs.getInt("supermarket_id"));
        cart.setId(rs.getInt("id"));
        // Set other properties as needed
        return cart;
    }

    @Override
    public void updateItemQuantity(int cartId, int itemId, int newQuantity) throws SQLException {
        dbManager.executeUpdate(ShoppingCartQueries.UPDATE_ITEM_QUANTITY, newQuantity, cartId, itemId);
    }

    @Override
    public void updateItems(int cartId, List<ShoppingItem> items) throws SQLException {
        for (ShoppingItem item : items) {
            dbManager.executeUpdate(ShoppingCartQueries.UPDATE_ITEMS,
                    item.getQuantity(), item.getPrice(), cartId, item.getBarcode());
        }
    }


    @Override
    public void removeItem(int cartId, int itemId) throws SQLException {
        dbManager.executeUpdate(ShoppingCartQueries.DELETE_ITEM, cartId, itemId);
    }

    @Override
    public List<ShoppingCart> getAll() throws SQLException {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ShoppingCartQueries.SELECT_ALL_CARTS);
        while (rs.next()) {
            shoppingCarts.add(extractShoppingCartFromResultSet(rs));
        }
        return shoppingCarts;
    }

    @Override
    public BigDecimal calculateCartTotal(int cartId) throws SQLException {
        String query = "SELECT SUM(price * quantity) as total FROM shopping_items WHERE cart_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

}
