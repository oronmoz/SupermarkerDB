package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.queriesmanager.AddressQueries;
import com.example.supermarket.models.Address;
import com.example.supermarket.database.dao.AddressDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressDaoImpl implements AddressDao {
    private DatabaseManager dbManager;

    public AddressDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Address create(Address address) throws SQLException {
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(AddressQueries.INSERT_ADDRESS)) {
            pstmt.setInt(1, address.getNum());
            pstmt.setString(2, address.getStreet());
            pstmt.setString(3, address.getCity());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    address.setId(rs.getInt(1));
                    return address;
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public List<Address> getAll() throws SQLException {
        List<Address> addresses = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery("SELECT * FROM addresses");
        while (rs.next()) {
            addresses.add(extractAddressFromResultSet(rs));
        }
        return addresses;
    }

    @Override
    public Address getById(int id) throws SQLException {
        ResultSet rs = dbManager.executeQuery(AddressQueries.SELECT_ADDRESS_BY_ID, id);
        if (rs.next()) {
            return extractAddressFromResultSet(rs);
        }
        return null;
    }

    @Override
    public void update(Address address) throws SQLException {
        dbManager.executeUpdate(AddressQueries.UPDATE_ADDRESS,
                address.getNum(), address.getStreet(), address.getCity(), address.getId());
    }

    @Override
    public void delete(int id) throws SQLException {
        dbManager.executeUpdate(AddressQueries.DELETE_ADDRESS, id);
    }

    private Address extractAddressFromResultSet(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setId(rs.getInt("id"));
        address.setNum(rs.getInt("num"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        return address;
    }
}