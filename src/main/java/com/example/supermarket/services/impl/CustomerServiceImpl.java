package com.example.supermarket.services.impl;

import com.example.supermarket.database.dao.AddressDao;
import com.example.supermarket.database.dao.CustomerDao;
import com.example.supermarket.database.dao.SupermarketCustomerDao;
import com.example.supermarket.database.dao.SupermarketDao;
import com.example.supermarket.models.Address;
import com.example.supermarket.models.Customer;
import com.example.supermarket.models.CustomerSupermarket;
import com.example.supermarket.services.CustomerService;

import java.sql.SQLException;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao;
    private SupermarketCustomerDao supermarketCustomerDao;
    private SupermarketDao supermarketDao;
    private AddressDao addressDao;

    public CustomerServiceImpl(CustomerDao customerDao, SupermarketCustomerDao supermarketCustomerDao, SupermarketDao supermarketDao, AddressDao addressDao) {
        this.customerDao = customerDao;
        this.supermarketCustomerDao = supermarketCustomerDao;
        this.supermarketDao = supermarketDao;
        this.addressDao = addressDao;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        return customerDao.getAll();
    }

    @Override
    public Customer save(Customer customer) throws SQLException {
        Address address = customer.getAddress();
        if (address != null) {
            int addressId = getOrCreateAddress(address);
            address.setId(addressId);
            customer.setAddress(address);
        }
        return customerDao.create(customer);
    }

    @Override
    public void update(Customer customer) throws SQLException {
        Address address = customer.getAddress();
        if (address != null) {
            int addressId = getOrCreateAddress(address);
            address.setId(addressId);
            customer.setAddress(address);
        }
        customerDao.update(customer);
    }


    @Override
    public void delete(int customerId) throws SQLException {
        customerDao.deleteWithRelatedData(customerId);
    }


    @Override
    public List<Customer> getSorted(SortOption sortOption) throws SQLException {
        List<Customer> customers = getAll();
        switch (sortOption) {
            case NAME:
                customers.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
                break;
            case SHOP_TIMES:
                customers.sort((c1, c2) -> {
                    try {
                        return Integer.compare(getCustomerShopTimes(c2.getId()), getCustomerShopTimes(c1.getId()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
            case TOTAL_SPEND:
                customers.sort((c1, c2) -> {
                    try {
                        return Float.compare(getTotalSpend(c2.getId()), getTotalSpend(c1.getId()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
        }
        return customers;
    }

    @Override
    public List<Customer> getBySupermarket(int supermarketId) throws SQLException {
        return customerDao.getCustomersBySupermarket(supermarketId);
    }

    @Override
    public void addToSupermarket(int customerId, int supermarketId) throws SQLException {
        CustomerSupermarket customerSupermarket = new CustomerSupermarket();
        customerSupermarket.setCustomerId(customerId);
        customerSupermarket.setSupermarketId(supermarketId);
        customerSupermarket.setShopTimes(0);
        customerSupermarket.setTotalSpend(0);
        supermarketCustomerDao.create(customerSupermarket);
    }


    @Override
    public float getTotalSpend(int customerId) throws SQLException {
        List<CustomerSupermarket> relationships = supermarketCustomerDao.getAllByCustomer(customerId);
        return (float) relationships.stream().mapToDouble(CustomerSupermarket::getTotalSpend).sum();
    }

    @Override
    public int getCustomerShopTimes(int customerId) throws SQLException {
        List<CustomerSupermarket> relationships = supermarketCustomerDao.getAllByCustomer(customerId);
        return relationships.stream().mapToInt(CustomerSupermarket::getShopTimes).sum();
    }

    private int getOrCreateAddress(Address address) throws SQLException {
        List<Address> existingAddresses = addressDao.getAll();
        for (Address existingAddress : existingAddresses) {
            if (isSameAddress(existingAddress, address)) {
                return existingAddress.getId();
            }
        }

        // If no identical address exists, create a new one
        Address newAddress = addressDao.create(address);
        return newAddress.getId();
    }

    private boolean isSameAddress(Address a1, Address a2) {
        return a1.getNum() == a2.getNum() &&
                a1.getStreet().equals(a2.getStreet()) &&
                a1.getCity().equals(a2.getCity());
    }

}