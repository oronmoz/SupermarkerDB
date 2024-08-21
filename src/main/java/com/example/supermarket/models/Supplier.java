package com.example.supermarket.models;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactInfo;
    private List<Product> suppliedProducts;

    // Default constructor
    public Supplier() {
        this.suppliedProducts = new ArrayList<>();
    }

    // Parameterized constructor
    public Supplier(int supplierId, String name, String contactInfo) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.suppliedProducts = new ArrayList<>();
    }

    // Getters and setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }


    @Override
    public String toString() {
        return name; // or any other meaningful representation
    }
}