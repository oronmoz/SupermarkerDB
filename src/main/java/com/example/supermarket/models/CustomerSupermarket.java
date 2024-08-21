package com.example.supermarket.models;

public class CustomerSupermarket {
    private int supermarketId;
    private int customerId;
    private int shopTimes;
    private float totalSpend;

    // Default constructor
    public CustomerSupermarket() {}

    public int getSupermarketId() {
        return supermarketId;
    }
    public void setSupermarketId(int supermarketId) {
        this.supermarketId = supermarketId;
    }

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public int getShopTimes() {
        return shopTimes;
    }

    public void setShopTimes(int shopTimes) {
        this.shopTimes = shopTimes;
    }

    public float getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(float totalSpend) {
        this.totalSpend = totalSpend;
    }

}
