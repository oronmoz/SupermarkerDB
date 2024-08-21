package com.example.supermarket.models;

import java.math.BigDecimal;

public class ShoppingItem {
    private int id;
    private int cartId;
    private String barcode;
    private BigDecimal price;
    private int quantity;
    private String productName;

    // Default Constructor
    public ShoppingItem() {}

    public ShoppingItem(int id, int cartId, String barcode, String productName, BigDecimal price, int quantity) {
        this.id = id;
        this.cartId = cartId;
        this.barcode = barcode;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int count) {
        this.quantity = count;
    }

    @Override
    public String toString() {
        return String.format("Barcode: %s, Price: %.2f, Quantity: %d", barcode, price, quantity);
    }
}