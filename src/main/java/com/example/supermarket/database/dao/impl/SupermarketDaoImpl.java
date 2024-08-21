package com.example.supermarket.database.dao.impl;

import com.example.supermarket.database.DatabaseManager;
import com.example.supermarket.database.dao.SupermarketDao;
import com.example.supermarket.database.queriesmanager.AddressQueries;
import com.example.supermarket.database.queriesmanager.ShoppingCartQueries;
import com.example.supermarket.database.queriesmanager.SupermarketQueries;
import com.example.supermarket.models.Customer;
import com.example.supermarket.models.Supermarket;
import com.example.supermarket.models.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SupermarketDaoImpl implements SupermarketDao {
    private DatabaseManager dbManager;
    private static final Logger LOGGER = Logger.getLogger(SupermarketDaoImpl.class.getName());

    public SupermarketDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Supermarket create(Supermarket supermarket) throws SQLException {
        int addressId = getOrCreateAddress(supermarket.getLocation());

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(SupermarketQueries.INSERT_SUPERMARKET)) {
            pstmt.setString(1, supermarket.getName());
            pstmt.setInt(2, addressId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    supermarket.setId(rs.getInt(1));
                    supermarket.getLocation().setId(addressId);
                    return supermarket;
                } else {
                    throw new SQLException("Creating supermarket failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Supermarket getById(int id) throws SQLException {
        ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_SUPERMARKET_BY_ID, id);
        if (rs.next()) {
            return extractSupermarketFromResultSet(rs);
        }
        return null;
    }

    @Override
    public List<Supermarket> getAll() throws SQLException {
        List<Supermarket> supermarkets = new ArrayList<>();
        ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_ALL_SUPERMARKETS);
        while (rs.next()) {
            supermarkets.add(extractSupermarketFromResultSet(rs));
        }
        return supermarkets;
    }

    @Override
    public void update(Supermarket supermarket) throws SQLException {
        Address location = supermarket.getLocation();
        int addressId = getOrCreateAddress(location);

        // Update the supermarket with the correct address id
        dbManager.executeUpdate(SupermarketQueries.UPDATE_SUPERMARKET,
                supermarket.getName(),
                addressId,
                supermarket.getId());

        // Update the address id in the supermarket object
        location.setId(addressId);
        supermarket.setLocation(location);
    }


    @Override
    public void delete(int id) throws SQLException {
        // First, get the location_id of the supermarket
        ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_SUPERMARKET_BY_ID, id);
        int locationId;
        if (rs.next()) {
            locationId = rs.getInt("location_id");
        } else {
            throw new SQLException("Supermarket not found");
        }

        // Delete related records from customer_supermarket
        dbManager.executeUpdate("DELETE FROM customer_supermarket WHERE supermarket_id = ?", id);

        // Delete related records from supermarket_product
        dbManager.executeUpdate("DELETE FROM supermarket_product WHERE supermarket_id = ?", id);

        // Set the location_id to null in the supermarkets table
        dbManager.executeUpdate("UPDATE supermarkets SET location_id = NULL WHERE id = ?", id);

        // Delete the supermarket
        dbManager.executeUpdate(SupermarketQueries.DELETE_SUPERMARKET, id);

        // Delete the associated address
        dbManager.executeUpdate(AddressQueries.DELETE_ADDRESS, locationId);
    }

    @Override
    public List<Supermarket> getByName(String name) throws SQLException {
        ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_SUPERMARKET_BY_NAME, name);
        List<Supermarket> supermarkets = new ArrayList<>();
        while (rs.next()) {
            supermarkets.add(extractSupermarketFromResultSet(rs));
        }
        return supermarkets;
    }


    private Supermarket extractSupermarketFromResultSet(ResultSet rs) throws SQLException {
        Supermarket supermarket = new Supermarket();
        supermarket.setId(rs.getInt("id"));
        supermarket.setName(rs.getString("name"));

        int locationId = rs.getInt("location_id");
        Address address = new Address();

        ResultSet rsAddress = dbManager.executeQuery(AddressQueries.SELECT_ADDRESS_BY_ID, locationId);

        if (rsAddress.next()) {
            address.setId(locationId);
            address.setNum(rsAddress.getInt("num"));
            address.setStreet(rsAddress.getString("street"));
            address.setCity(rsAddress.getString("city"));
        } else {
            // handle the case where no address is found
            throw new SQLException("No address found for location_id: " + locationId);
        }

        supermarket.setLocation(address);
        return supermarket;
    }

    @Override
    public List<Supermarket> getSorted(String sortBy, String sortOrder) throws SQLException {
        String query = String.format(SupermarketQueries.GET_SORTED_SUPERMARKETS, sortBy, sortOrder);
        ResultSet rs = dbManager.executeQuery(query);
        List<Supermarket> supermarkets = new ArrayList<>();
        while (rs.next()) {
            supermarkets.add(extractSupermarketFromResultSet(rs));
        }
        return supermarkets;
    }

    @Override
    public List<Customer> getCustomersBySupermarket(int supermarketId) throws SQLException {
        ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_CUSTOMERS_BY_SUPERMARKET, supermarketId);
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(extractCustomerFromResultSet(rs));
        }
        return customers;
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        return customer;
    }

    @Override
    public Supermarket createOrUpdate(Supermarket supermarket) throws SQLException {
        int addressId = getOrCreateAddress(supermarket.getLocation());

        if (supermarket.getId() == 0) {
            // This is a new supermarket
            try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(SupermarketQueries.INSERT_SUPERMARKET)) {
                pstmt.setString(1, supermarket.getName());
                pstmt.setInt(2, addressId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        supermarket.setId(rs.getInt(1));
                        supermarket.getLocation().setId(addressId);
                        return supermarket;
                    } else {
                        throw new SQLException("Creating supermarket failed, no ID obtained.");
                    }
                }
            }
        } else {
            // This is an existing supermarket being updated
            dbManager.executeUpdate(SupermarketQueries.UPDATE_SUPERMARKET,
                    supermarket.getName(),
                    addressId,
                    supermarket.getId());
            supermarket.getLocation().setId(addressId);
            return supermarket;
        }
    }

    private int getOrCreateAddress(Address address) throws SQLException {
        // Check if an identical address already exists
        String checkAddressQuery = "SELECT id FROM addresses WHERE num = ? AND street = ? AND city = ?";
        ResultSet rs = dbManager.executeQuery(checkAddressQuery,
                address.getNum(),
                address.getStreet(),
                address.getCity());

        if (rs.next()) {
            // If an identical address exists, return its id
            return rs.getInt("id");
        } else {
            // If no identical address exists, create a new one
            String createAddressQuery = "INSERT INTO addresses (num, street, city) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(createAddressQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, address.getNum());
                pstmt.setString(2, address.getStreet());
                pstmt.setString(3, address.getCity());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating address failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating address failed, no ID obtained.");
                    }
                }
            }
        }
    }


    @Override
    public void deleteWithRelatedData(int id) throws SQLException {
        LOGGER.info("Starting deletion of supermarket with ID: " + id);

        try {
            // Get the location_id of the supermarket
            ResultSet rs = dbManager.executeQuery(SupermarketQueries.GET_SUPERMARKET_BY_ID, id);
            int locationId;
            if (rs.next()) {
                locationId = rs.getInt("location_id");
                LOGGER.info("Found location ID: " + locationId);
            } else {
                LOGGER.warning("Supermarket not found with ID: " + id);
                throw new SQLException("Supermarket not found");
            }

            // Delete shopping items associated with carts of this supermarket
            int shoppingItemsDeleted = dbManager.executeUpdate(
                    "DELETE FROM shopping_items WHERE cart_id IN " +
                            "(SELECT id FROM shopping_carts WHERE supermarket_id = ?)", id);
            LOGGER.info("Deleted " + shoppingItemsDeleted + " shopping items");

            // Delete shopping carts associated with this supermarket
            int shoppingCartsDeleted = dbManager.executeUpdate(
                    "DELETE FROM shopping_carts WHERE supermarket_id = ?", id);
            LOGGER.info("Deleted " + shoppingCartsDeleted + " shopping carts");

            // Delete related records from customer_supermarket
            int customerSupermarketDeleted = dbManager.executeUpdate(
                    "DELETE FROM customer_supermarket WHERE supermarket_id = ?", id);
            LOGGER.info("Deleted " + customerSupermarketDeleted + " records from customer_supermarket");

            // Delete related records from supermarket_product
            int supermarketProductDeleted = dbManager.executeUpdate(
                    "DELETE FROM supermarket_product WHERE supermarket_id = ?", id);
            LOGGER.info("Deleted " + supermarketProductDeleted + " records from supermarket_product");

            // Set the location_id to null in the supermarkets table
            int locationNulled = dbManager.executeUpdate(
                    "UPDATE supermarkets SET location_id = NULL WHERE id = ?", id);
            LOGGER.info("Set location_id to NULL for " + locationNulled + " supermarket records");

            // Delete the supermarket
            int supermarketDeleted = dbManager.executeUpdate(SupermarketQueries.DELETE_SUPERMARKET, id);
            LOGGER.info("Deleted " + supermarketDeleted + " supermarket records");

            // Optionally delete the associated address if it's not used by other entities
            // int addressDeleted = dbManager.executeUpdate(AddressQueries.DELETE_ADDRESS, locationId);
            // LOGGER.info("Deleted " + addressDeleted + " address records");

            LOGGER.info("Supermarket deletion completed successfully");
        } catch (SQLException e) {
            LOGGER.severe("Error during supermarket deletion: " + e.getMessage());
            throw e;
        }
    }


}