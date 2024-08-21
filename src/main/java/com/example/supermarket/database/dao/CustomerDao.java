package com.example.supermarket.database.dao;

import com.example.supermarket.models.Customer;
import java.sql.SQLException;
import java.util.List;

public interface CustomerDao {
    Customer create(Customer customer) throws SQLException;
    Customer getById(int id) throws SQLException;
    Customer getByName(String name) throws SQLException;
    List<Customer> getAll() throws SQLException;
    void update(Customer customer) throws SQLException;
    void delete(int id) throws SQLException;
    List<Customer> getSortedCustomers(String sortBy, String sortOrder) throws SQLException;
    List<Customer> getCustomersBySupermarket(int supermarketId) throws SQLException;
    void deleteWithRelatedData(int id) throws SQLException;

    }