package com.example.supermarket.services;

import com.example.supermarket.models.Product;
import com.example.supermarket.models.Supermarket;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts() throws SQLException;
    Product saveProduct(Product product) throws SQLException;
    void updateProduct(Product product) throws SQLException;
    void addProductToSupermarket(int productId, int supermarketId, int initialStock) throws SQLException;
    List<Product> getProductsBySupermarket(int supermarketId) throws SQLException;
    int getTotalCountForProduct(int productId) throws SQLException;
    List<Product> getProductsBySupplier(int supplierId) throws SQLException;
    void deleteProduct(int productId) throws SQLException;
}