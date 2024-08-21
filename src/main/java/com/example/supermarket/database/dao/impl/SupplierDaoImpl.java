package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.dao.SupplierDao;
import com.example.supermarket.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements SupplierDao {
    private DatabaseManager dbManager;

    public SupplierDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Supplier getById(int id) throws SQLException {
        String query = "SELECT * FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractSupplierFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Supplier> getAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                suppliers.add(extractSupplierFromResultSet(rs));
            }
        }
        return suppliers;
    }

    @Override
    public Supplier create(Supplier supplier) throws SQLException {
        String query = "INSERT INTO suppliers (name, contact_info) VALUES (?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactInfo());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating supplier failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setSupplierId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating supplier failed, no ID obtained.");
                }
            }
        }
        return supplier;
    }

    @Override
    public void update(Supplier supplier) throws SQLException {
        String query = "UPDATE suppliers SET name = ?, contact_info = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactInfo());
            stmt.setInt(3, supplier.getSupplierId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Supplier extractSupplierFromResultSet(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactInfo(rs.getString("contact_info"));
        return supplier;
    }


}