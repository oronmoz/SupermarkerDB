package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.dao.ProductSupermarketDao;
import com.example.supermarket.database.queriesmanager.ProductSupermarketQueries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductSupermarketDaoImpl implements ProductSupermarketDao {
    private DatabaseManager dbManager;

    public ProductSupermarketDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public void addProductToSupermarket(int productId, int supermarketId, int stock) throws SQLException {
        dbManager.executeUpdate(ProductSupermarketQueries.ADD_PRODUCT_TO_SUPERMARKET, supermarketId, productId, stock);
    }

    @Override
    public void removeProductFromSupermarket(int productId, int supermarketId) throws SQLException {
        dbManager.executeUpdate(ProductSupermarketQueries.REMOVE_PRODUCT_FROM_SUPERMARKET, supermarketId, productId);
    }

    @Override
    public void updateStock(int productId, int supermarketId, int stock) throws SQLException {
        dbManager.executeUpdate(ProductSupermarketQueries.UPDATE_STOCK, stock, supermarketId, productId);
    }

    @Override
    public int getStock(int productId, int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ProductSupermarketQueries.GET_STOCK, supermarketId, productId);
        if (rs.next()) {
            return rs.getInt("stock");
        }
        return 0;
    }

    @Override
    public List<Integer> getSupermarketIdsForProduct(int productId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ProductSupermarketQueries.GET_SUPERMARKET_IDS_FOR_PRODUCT, productId);
        List<Integer> supermarketIds = new ArrayList<>();
        while (rs.next()) {
            supermarketIds.add(rs.getInt("supermarket_id"));
        }
        return supermarketIds;
    }

    @Override
    public List<Integer> getProductIdsForSupermarket(int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ProductSupermarketQueries.GET_PRODUCT_IDS_FOR_SUPERMARKET, supermarketId);
        List<Integer> productIds = new ArrayList<>();
        while (rs.next()) {
            productIds.add(rs.getInt("product_id"));
        }
        return productIds;
    }
}