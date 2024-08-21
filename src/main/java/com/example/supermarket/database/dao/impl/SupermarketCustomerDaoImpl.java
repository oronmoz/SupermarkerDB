package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.dao.SupermarketCustomerDao;
import com.example.supermarket.database.queriesmanager.SupermarketCustomerQueries;
import com.example.supermarket.models.CustomerSupermarket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupermarketCustomerDaoImpl implements SupermarketCustomerDao {
    private DatabaseManager dbManager;

    public SupermarketCustomerDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public CustomerSupermarket create(CustomerSupermarket customerSupermarket) throws SQLException {
        dbManager.executeUpdate(SupermarketCustomerQueries.INSERT_CUSTOMER_SUPERMARKET,
                customerSupermarket.getCustomerId(),
                customerSupermarket.getSupermarketId(),
                customerSupermarket.getShopTimes(),
                customerSupermarket.getTotalSpend());
        return customerSupermarket;
    }

    @Override
    public CustomerSupermarket get(int customerId, int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(SupermarketCustomerQueries.SELECT_CUSTOMER_SUPERMARKET,
                customerId, supermarketId);
        if (rs.next()) {
            return extractCustomerSupermarketFromResultSet(rs);
        }
        return null;
    }

    @Override
    public List<CustomerSupermarket> getAllByCustomer(int customerId) throws SQLException {
        List<CustomerSupermarket> customerSupermarkets = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(SupermarketCustomerQueries.SELECT_ALL_BY_CUSTOMER, customerId);
        while (rs.next()) {
            customerSupermarkets.add(extractCustomerSupermarketFromResultSet(rs));
        }
        return customerSupermarkets;
    }

    @Override
    public List<CustomerSupermarket> getAllBySupermarket(int supermarketId) throws SQLException {
        List<CustomerSupermarket> customerSupermarkets = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(SupermarketCustomerQueries.SELECT_ALL_BY_SUPERMARKET, supermarketId);
        while (rs.next()) {
            customerSupermarkets.add(extractCustomerSupermarketFromResultSet(rs));
        }
        return customerSupermarkets;
    }

    @Override
    public void update(CustomerSupermarket SupermarketCustomer) throws SQLException {
        dbManager.executeUpdate(SupermarketCustomerQueries.UPDATE_CUSTOMER_SUPERMARKET,
                SupermarketCustomer.getShopTimes(),
                SupermarketCustomer.getTotalSpend(),
                SupermarketCustomer.getCustomerId(),
                SupermarketCustomer.getSupermarketId());
    }

    @Override
    public void delete(int customerId, int supermarketId) throws SQLException {
        dbManager.executeUpdate(SupermarketCustomerQueries.DELETE_CUSTOMER_SUPERMARKET,
                customerId, supermarketId);
    }

    @Override
    public void updateAfterShopping(int customerId, int supermarketId, float amount) throws SQLException {
        dbManager.executeUpdate(SupermarketCustomerQueries.UPDATE_AFTER_SHOPPING,
                amount, customerId, supermarketId);
    }

    private CustomerSupermarket extractCustomerSupermarketFromResultSet(ResultSet rs) throws SQLException {
        CustomerSupermarket customerSupermarket = new CustomerSupermarket();
        customerSupermarket.setCustomerId(rs.getInt("customer_id"));
        customerSupermarket.setSupermarketId(rs.getInt("supermarket_id"));
        customerSupermarket.setShopTimes(rs.getInt("shop_times"));
        customerSupermarket.setTotalSpend(rs.getFloat("total_spend"));
        return customerSupermarket;
    }
}