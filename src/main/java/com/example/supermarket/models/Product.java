package com.example.supermarket.models;

import java.math.BigDecimal;
public class Product {

    public enum ProductType {
        FRUIT_VEGETABLE("Fruit/Vegetable"),
        FRIDGE("Fridge"),
        FROZEN("Frozen"),
        SHELF("Shelf");

        private final String displayName;

        ProductType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return name();
        }

        public static ProductType fromString(String text) {
            for (ProductType b : ProductType.values()) {
                if (b.name().equalsIgnoreCase(text) || b.displayName.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }

    private int id;
    private String name;
    private String barcode;
    private ProductType type;
    private BigDecimal price;
    private int supplierId;

    // Default constructor
    public Product() {}

    public Product(String name, String barcode, ProductType type, BigDecimal price, int supplierId) {
        this.name = name;
        this.barcode = barcode;
        this.type = type;
        this.price = price;
        this.supplierId = supplierId;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPriceFromString(String price) {
        try {
            this.price = new BigDecimal(price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format. Please enter a valid number.");
        }
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-10s\t%-20s %5.2f", name, barcode, type, price);
    }
}