package com.example.supermarket.services.impl;

import com.example.supermarket.database.dao.ProductDao;
import com.example.supermarket.database.dao.ProductSupermarketDao;
import com.example.supermarket.database.dao.SupermarketDao;
import com.example.supermarket.models.Product;
import com.example.supermarket.services.ProductService;

import java.sql.SQLException;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao;
    private ProductSupermarketDao productSupermarketDao;
    private SupermarketDao supermarketDao;

    public ProductServiceImpl(ProductDao productDao, ProductSupermarketDao productSupermarketDao, SupermarketDao supermarketDao) {
        this.productDao = productDao;
        this.productSupermarketDao = productSupermarketDao;
        this.supermarketDao = supermarketDao;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        return productDao.getAll();
    }

    @Override
    public Product saveProduct(Product product) throws SQLException {
        try {
            return productDao.create(product);
        } catch (SQLException e) {
            throw new SQLException("Failed to save product: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateProduct(Product product) throws SQLException {
        productDao.update(product);
    }

    @Override
    public void addOrUpdateProductInSupermarket(int productId, int supermarketId, int quantity) throws SQLException {
        int currentStock = productSupermarketDao.getStock(productId, supermarketId);
        if (currentStock > 0) {
            // Product already exists, update the stock
            int newStock = currentStock + quantity;
            productSupermarketDao.updateStock(productId, supermarketId, newStock);
        } else {
            // Product doesn't exist, add it
            productSupermarketDao.addProductToSupermarket(productId, supermarketId, quantity);
        }
    }

    @Override
    public void addProductToSupermarket(int productId, int supermarketId, int initialStock) throws SQLException {
        productSupermarketDao.addProductToSupermarket(productId, supermarketId, initialStock);
    }


    @Override
    public List<Product> getProductsBySupermarket(int supermarketId) throws SQLException {
        return productDao.getBySupermarket(supermarketId);
    }

    @Override
    public int getTotalCountForProduct(int productId) throws SQLException {
        List<Integer> supermarketIds = productSupermarketDao.getSupermarketIdsForProduct(productId);
        int totalCount = 0;
        for (int supermarketId : supermarketIds) {
            totalCount += productSupermarketDao.getStock(productId, supermarketId);
        }
        return totalCount;
    }

    @Override
    public void deleteProduct(int productId) throws SQLException {
        productDao.deleteWithRelatedData(productId);
    }


    @Override
    public List<Product> getProductsBySupplier(int supplierId) throws SQLException {
        return productDao.getBySupplier(supplierId);
    }
}