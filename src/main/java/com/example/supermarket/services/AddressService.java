package com.example.supermarket.services;

import com.example.supermarket.models.Address;

import java.sql.SQLException;
import java.util.List;

public interface AddressService {
    List<Address> getAllAddresses() throws SQLException;
    void deleteAddress(int id) throws SQLException;
}