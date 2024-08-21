package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.queriesmanager.CustomerQueries;
import com.example.supermarket.models.Address;
import com.example.supermarket.models.Customer;
import com.example.supermarket.database.dao.CustomerDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    private DatabaseManager dbManager;

    public CustomerDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Customer create(Customer customer) throws SQLException {
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(CustomerQueries.INSERT_CUSTOMER)) {
            pstmt.setString(1, customer.getName());
            pstmt.setInt(2, customer.getAddress().getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                    return customer;
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Customer getById(int id) throws SQLException {
        ResultSet rs = dbManager.executeQuery(CustomerQueries.SELECT_CUSTOMER_BY_ID, id);
        if (rs.next()) {
            return extractCustomerFromResultSet(rs);
        }
        return null;
    }

    @Override
    public Customer getByName(String name) throws SQLException {
        ResultSet rs = dbManager.executeQuery(CustomerQueries.SELECT_CUSTOMER_BY_NAME, name);
        if (rs.next()) {
            return extractCustomerFromResultSet(rs);
        }
        return null;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(CustomerQueries.SELECT_ALL_CUSTOMERS);
        while (rs.next()) {
            customers.add(extractCustomerFromResultSet(rs));
        }
        return customers;
    }

    @Override
    public void update(Customer customer) throws SQLException {
        dbManager.executeUpdate(CustomerQueries.UPDATE_CUSTOMER, customer.getName(), customer.getAddress().getId(), customer.getId());
    }

    @Override
    public void delete(int id) throws SQLException {
        dbManager.executeUpdate(CustomerQueries.DELETE_CUSTOMER, id);
    }

    @Override
    public List<Customer> getSortedCustomers(String sortBy, String sortOrder) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = String.format(CustomerQueries.SELECT_SORTED_CUSTOMERS, sortBy, sortOrder);
        ResultSet rs = dbManager.executeQuery(query);
        while (rs.next()) {
            customers.add(extractCustomerFromResultSet(rs));
        }
        return customers;
    }

    @Override
    public List<Customer> getCustomersBySupermarket(int supermarketId) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(CustomerQueries.SELECT_CUSTOMERS_BY_SUPERMARKET, supermarketId);
        while (rs.next()) {
            customers.add(extractCustomerFromResultSet(rs));
        }
        return customers;
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));

        Address address = new Address();
        address.setId(rs.getInt("address_id"));
        address.setNum(rs.getInt("num"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        customer.setAddress(address);

        return customer;
    }

    @Override
    public void deleteWithRelatedData(int id) throws SQLException {
        // First, delete the customer from customer_supermarket table
        dbManager.executeUpdate("DELETE FROM customer_supermarket WHERE customer_id = ?", id);

        // Then, delete the customer
        dbManager.executeUpdate(CustomerQueries.DELETE_CUSTOMER, id);

    }
}