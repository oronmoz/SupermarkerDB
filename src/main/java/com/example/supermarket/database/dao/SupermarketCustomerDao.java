package com.example.supermarket.database.dao;

import com.example.supermarket.models.CustomerSupermarket;
import java.sql.SQLException;
import java.util.List;

public interface SupermarketCustomerDao {
    CustomerSupermarket create(CustomerSupermarket customerSupermarket) throws SQLException;
    CustomerSupermarket get(int customerId, int supermarketId) throws SQLException;
    List<CustomerSupermarket> getAllByCustomer(int customerId) throws SQLException;
    List<CustomerSupermarket> getAllBySupermarket(int supermarketId) throws SQLException;
    void update(CustomerSupermarket customerSupermarket) throws SQLException;
    void delete(int customerId, int supermarketId) throws SQLException;
    void updateAfterShopping(int customerId, int supermarketId, float amount) throws SQLException;

}