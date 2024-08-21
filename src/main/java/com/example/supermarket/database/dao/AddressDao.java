package com.example.supermarket.database.dao;

import com.example.supermarket.models.Address;
import java.sql.SQLException;
import java.util.List;

public interface AddressDao {
    Address create(Address address) throws SQLException;
    List<Address> getAll() throws SQLException;
    Address getById(int id) throws SQLException;
    void update(Address address) throws SQLException;
    void delete(int id) throws SQLException;
    }