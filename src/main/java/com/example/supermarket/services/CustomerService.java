package com.example.supermarket.services;

import com.example.supermarket.models.Customer;
import com.example.supermarket.models.Supermarket;

import java.sql.SQLException;
import java.util.List;

public interface CustomerService {
    List<Customer> getAll() throws SQLException;
    Customer save(Customer customer) throws SQLException;
    void delete(int customerId) throws SQLException;
    List<Customer> getSorted(SortOption sortOption) throws SQLException;
    List<Customer> getBySupermarket(int supermarketId) throws SQLException;
    void addToSupermarket(int customerId, int supermarketId) throws SQLException;
    float getTotalSpend(int customerId) throws SQLException;
    int getCustomerShopTimes(int customerId) throws SQLException;
    void update(Customer customer) throws SQLException;

    enum SortOption {
        NAME, SHOP_TIMES, TOTAL_SPEND
    }
}