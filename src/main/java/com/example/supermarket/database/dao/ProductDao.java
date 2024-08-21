package com.example.supermarket.database.dao;

import com.example.supermarket.models.Product;
import java.sql.SQLException;
import java.util.List;

public interface ProductDao {
    Product create(Product product) throws SQLException;
    Product getById(int id) throws SQLException;
    Product getByBarcode(String barcode) throws SQLException;
    List<Product> getAll() throws SQLException;
    void update(Product product) throws SQLException;
    void delete(String barcode) throws SQLException;
    List<Product> getByType(Product.ProductType type) throws SQLException;
    List<Product> getBySupermarket(int supermarketId) throws SQLException;
    List<Product> getBySupplier(int supplierId) throws SQLException;
    List<Product> getProductsByIds(List<Integer> ids) throws SQLException;
    void deleteWithRelatedData(int id) throws SQLException;
}