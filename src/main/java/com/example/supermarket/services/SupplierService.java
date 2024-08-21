package com.example.supermarket.services;

import com.example.supermarket.models.Supplier;

import java.sql.SQLException;
import java.util.List;

public interface SupplierService {
    List<Supplier> getAllSuppliers() throws SQLException;
    Supplier createSupplier(Supplier supplier) throws SQLException;
    void updateSupplier(Supplier supplier) throws SQLException;
    void deleteSupplier(int id) throws SQLException;
}
