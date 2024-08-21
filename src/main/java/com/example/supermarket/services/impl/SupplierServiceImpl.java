package com.example.supermarket.services.impl;

import com.example.supermarket.services.SupplierService;
import com.example.supermarket.models.Supplier;
import com.example.supermarket.database.dao.SupplierDao;
import java.sql.SQLException;
import java.util.List;

public class SupplierServiceImpl implements SupplierService {
    private final SupplierDao supplierDao;

    public SupplierServiceImpl(SupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }


    @Override
    public void updateSupplier(Supplier supplier) throws SQLException {
        supplierDao.update(supplier);
    }

    @Override
    public void deleteSupplier(int id) throws SQLException {
        supplierDao.delete(id);
    }


    @Override
    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplierDao.getAll();
    }

    @Override
    public Supplier createSupplier(Supplier supplier) throws SQLException {
        return supplierDao.create(supplier);
    }
}