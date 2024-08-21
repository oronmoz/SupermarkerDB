package com.example.supermarket.services.impl;

import com.example.supermarket.database.dao.AddressDao;
import com.example.supermarket.models.Address;
import com.example.supermarket.services.AddressService;

import java.sql.SQLException;
import java.util.List;

public class AddressServiceImpl implements AddressService {
    private AddressDao addressDao;

    public AddressServiceImpl(AddressDao addressDao) {
        this.addressDao = addressDao;
    }


    @Override
    public List<Address> getAllAddresses() throws SQLException {
        return addressDao.getAll();
    }



    @Override
    public void deleteAddress(int id) throws SQLException {
        addressDao.delete(id);
    }
}