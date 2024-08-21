package com.example.supermarket.services.impl;

import com.example.supermarket.database.dao.*;
import com.example.supermarket.models.*;
import com.example.supermarket.services.SupermarketService;
import com.example.supermarket.services.AddressService;
import com.example.supermarket.utils.InsufficientStockException;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SupermarketServiceImpl implements SupermarketService {
    private SupermarketDao supermarketDao;
    private ProductSupermarketDao productSupermarketDao;
    private AddressService addressService;
    private CustomerDao customerDao;
    private AddressDao addressDao;
    private ProductDao productDao;
    private SupermarketCustomerDao supermarketCustomerDao;
    private static final Logger LOGGER = Logger.getLogger(SupermarketServiceImpl.class.getName());

    public SupermarketServiceImpl(SupermarketDao supermarketDao, ProductSupermarketDao productSupermarketDao,
                                  AddressService addressService, CustomerDao customerDao, AddressDao addressDao, ProductDao productDao, SupermarketCustomerDao supermarketCustomerDao) {
        this.supermarketDao = supermarketDao;
        this.productSupermarketDao = productSupermarketDao;
        this.addressService = addressService;
        this.customerDao = customerDao;
        this.addressDao = addressDao;
        this.productDao = productDao;
        this.supermarketCustomerDao = supermarketCustomerDao;
    }

    @Override
    public List<Supermarket> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Supermarket addSupermarket(Supermarket supermarket) throws SQLException {
        Address location = supermarket.getLocation();
        if (location != null) {
            if (location.getId() == 0) {
                location = addressDao.create(location);
            } else {
                addressDao.update(location);
            }
            supermarket.setLocation(location);
        }
        return supermarketDao.create(supermarket);
    }

    @Override
    public List<Supermarket> viewSupermarkets() throws SQLException {
        return supermarketDao.getAll();
    }

    @Override
    public void updateSupermarket(Supermarket supermarket) throws SQLException {
        Address location = supermarket.getLocation();
        if (location != null) {
            if (location.getId() == 0) {
                location = addressDao.create(location);
            } else {
                addressDao.update(location);
            }
            supermarket.setLocation(location);
        }
        supermarketDao.update(supermarket);
    }

    @Override
    public void deleteSupermarket(int id) throws SQLException {
        try {
            supermarketDao.deleteWithRelatedData(id);
            LOGGER.info("Supermarket deleted successfully: " + id);
        } catch (SQLException e) {
            LOGGER.severe("Error deleting supermarket: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Supermarket getSupermarketById(int id) throws SQLException {
        return supermarketDao.getById(id);
    }


    @Override
    public List<Product> getProductsBySupermarket(int supermarketId) throws SQLException {
        List<Integer> productIds = productSupermarketDao.getProductIdsForSupermarket(supermarketId);
        return productDao.getProductsByIds(productIds);
    }

    @Override
    public List<Supermarket> getAllSupermarkets() throws SQLException {
        return supermarketDao.getAll();
    }


    @Override
    public void addCustomerToSupermarketIfNotExists(int customerId, int supermarketId) throws SQLException {
        CustomerSupermarket cs = supermarketCustomerDao.get(customerId, supermarketId);
        if (cs == null) {
            CustomerSupermarket newCs = new CustomerSupermarket();
            newCs.setCustomerId(customerId);
            newCs.setSupermarketId(supermarketId);
            newCs.setShopTimes(0);
            newCs.setTotalSpend(0f);
            supermarketCustomerDao.create(newCs);
        }
    }

    @Override
    public void updateAfterPurchase(ShoppingCart cart) throws SQLException, InsufficientStockException {
        for (ShoppingItem item : cart.getItems()) {
            // Get the product using the barcode
            Product product = productDao.getByBarcode(item.getBarcode());
            if (product == null) {
                throw new SQLException("Product not found for barcode: " + item.getBarcode());
            }

            // Update product stock
            int currentStock = productSupermarketDao.getStock(product.getId(), cart.getSupermarketId());
            int newStock = currentStock - item.getQuantity();

            if (newStock < 0) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            productSupermarketDao.updateStock(product.getId(), cart.getSupermarketId(), newStock);
        }

        // Update customer-supermarket relationship
        CustomerSupermarket cs = supermarketCustomerDao.get(cart.getCustomerId(), cart.getSupermarketId());
        if (cs == null) {
            cs = new CustomerSupermarket();
            cs.setCustomerId(cart.getCustomerId());
            cs.setSupermarketId(cart.getSupermarketId());
            cs.setShopTimes(1);
            cs.setTotalSpend(cart.getTotalPrice().floatValue());
            supermarketCustomerDao.create(cs);
        } else {
            cs.setShopTimes(cs.getShopTimes() + 1);
            cs.setTotalSpend(cs.getTotalSpend() + cart.getTotalPrice().floatValue());
            supermarketCustomerDao.update(cs);
        }
    }

    @Override
    public int getProductStock(int productId, int supermarketId) throws SQLException {
        return productSupermarketDao.getStock(productId, supermarketId);
    }

    @Override
    public Supermarket addOrUpdateSupermarket(Supermarket supermarket) throws SQLException {
        return supermarketDao.createOrUpdate(supermarket);
    }





}

