package com.example.supermarket.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public class ShoppingCart {
    private int id;
    private int supermarketId;
    private int customerId;
    private List<ShoppingItem> items;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;

    // Default constructor
    public ShoppingCart() {}

    public ShoppingCart(int customerId, int supermarketId) {
        this.supermarketId = supermarketId;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.totalPrice = new BigDecimal("0.00") ;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addItem(ShoppingItem newItem) {
        Optional<ShoppingItem> existingItem = items.stream()
                .filter(item -> item.getBarcode().equals(newItem.getBarcode()))
                .findFirst();

        if (existingItem.isPresent()) {
            ShoppingItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
        updateTotalPrice();
    }

    public void removeItem(ShoppingItem item) {
        this.items.remove(item);
        updateTotalPrice();
    }

    public int getItemCount() {
        return items.size();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void updateTotalPrice() {
        this.totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAllTotalPrices(){
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("ShoppingCart " + id + " total price: " + total);
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Shopping Cart:\n");
        for (ShoppingItem item : items) {
            sb.append(item).append("\n");
        }
        sb.append("Total: $").append(String.format("%.2f", getTotalPrice()));
        return sb.toString();
    }

    public List<ShoppingItem> getItems() {
        return new ArrayList<>(items); // Return a copy to prevent direct manipulation
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = new ArrayList<>(items); // Create a new list to prevent direct manipulation
        updateTotalPrice();
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public void setSupermarketId(int supermarketId) {
        this.supermarketId = supermarketId;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public int getSupermarketId() {
        return this.supermarketId;
    }

    public void clearItems() {
        this.items.clear();
        updateTotalPrice();
    }
}
