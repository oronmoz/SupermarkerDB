package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.queriesmanager.ProductQueries;
import com.example.supermarket.models.Product;
import com.example.supermarket.database.dao.ProductDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private DatabaseManager dbManager;

    public ProductDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Product create(Product product) throws SQLException {
        try (var connection = dbManager.getConnection();
             var stmt = connection.prepareStatement(ProductQueries.INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getBarcode());
            stmt.setString(3, product.getType().toString());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setInt(5, product.getSupplierId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        }
        return product;
    }


    @Override
    public Product getByBarcode(String barcode) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCT_BY_BARCODE, barcode);
        if (rs.next()) {
            return extractProductFromResultSet(rs);
        }
        return null;
    }



    @Override
    public Product getById(int id) throws SQLException {
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCT_BY_ID, id);
        if (rs.next()) {
            return extractProductFromResultSet(rs);
        }
        return null;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_ALL_PRODUCTS);
        while (rs.next()) {
            products.add(extractProductFromResultSet(rs));
        }
        return products;
    }

    @Override
    public void update(Product product) throws SQLException {
        dbManager.executeUpdate(ProductQueries.UPDATE_PRODUCT,
                product.getName(),
                product.getType().toString(),
                product.getPrice(),
                product.getSupplierId(),
                product.getBarcode(),
                product.getId());
    }

    @Override
    public void delete(String id) throws SQLException {
        dbManager.executeUpdate(ProductQueries.DELETE_PRODUCT, Integer.parseInt(id));
    }

    @Override
    public List<Product> getByType(Product.ProductType type) throws SQLException {
        List<Product> products = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCTS_BY_TYPE, type.toString());
        while (rs.next()) {
            products.add(extractProductFromResultSet(rs));
        }
        return products;
    }

    @Override
    public List<Product> getBySupermarket(int supermarketId) throws SQLException {
        List<Product> products = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCTS_BY_SUPERMARKET, supermarketId);
        while (rs.next()) {
            products.add(extractProductFromResultSet(rs));
        }
        return products;
    }

    @Override
    public List<Product> getBySupplier(int supplierId) throws SQLException {
        List<Product> products = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCTS_BY_SUPPLIER, supplierId);
        while (rs.next()) {
            products.add(extractProductFromResultSet(rs));
        }
        return products;
    }



    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setBarcode(rs.getString("barcode"));
        product.setType(Product.ProductType.fromString(rs.getString("type")));
        product.setPrice(rs.getBigDecimal("price"));
        product.setSupplierId(rs.getInt("supplier_id"));
        return product;
    }

    @Override
    public List<Product> getProductsByIds(List<Integer> ids) throws SQLException {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = new ArrayList<>();
        for (Integer id : ids) {
            ResultSet rs = dbManager.executeQuery(ProductQueries.SELECT_PRODUCT_BY_ID, id);
            if (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        }
        return products;
    }

    @Override
    public void deleteWithRelatedData(int id) throws SQLException {
        // First, remove the product from all supermarkets
        dbManager.executeUpdate("DELETE FROM SUPERMARKET_PRODUCT WHERE product_id = ?", id);

        // Then, delete the product itself
        dbManager.executeUpdate(ProductQueries.DELETE_PRODUCT, id);
    }


}