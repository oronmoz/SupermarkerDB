package com.example.supermarket.database.dao;

import com.example.supermarket.models.Customer;
import com.example.supermarket.models.Supermarket;
import java.sql.SQLException;
import java.util.List;

public interface SupermarketDao {
    Supermarket create(Supermarket supermarket) throws SQLException;
    Supermarket getById(int id) throws SQLException;
    List<Supermarket> getAll() throws SQLException;
    void update(Supermarket supermarket) throws SQLException;
    void delete(int id) throws SQLException;
    List<Supermarket> getByName(String name) throws SQLException;
    List<Supermarket> getSorted(String sortBy, String sortOrder) throws SQLException;
    List<Customer> getCustomersBySupermarket(int supermarketId) throws SQLException;
    Supermarket createOrUpdate(Supermarket supermarket) throws SQLException;
    void deleteWithRelatedData(int id) throws SQLException;
}