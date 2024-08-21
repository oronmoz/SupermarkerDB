package com.example.supermarket.models;

public class ProductSupermarket {
    private int supermarketId;
    private int productId;
    private int stock;

    // default constructor
    public ProductSupermarket() {}

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
