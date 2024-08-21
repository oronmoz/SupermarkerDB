package com.example.supermarket.database.dao;

import com.example.supermarket.models.Supplier;
import java.sql.SQLException;
import java.util.List;

public interface SupplierDao {
    Supplier getById(int id) throws SQLException;
    List<Supplier> getAll() throws SQLException;
    Supplier create(Supplier supplier) throws SQLException;
    void update(Supplier supplier) throws SQLException;
    void delete(int id) throws SQLException;
}
