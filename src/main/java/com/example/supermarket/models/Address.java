package com.example.supermarket.models;

public class Address {
    private int id;
    private int num;
    private String street;
    private String city;

    // default constructor
    public Address() {
        this.num = 0;
        this.street = "";
        this.city = "";
    }

    // constructor with all fields except id
    public Address(int num, String street, String city) {
        this.num = num;
        this.street = street;
        this.city = city;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNum() { return num; }
    public void setNum(int num) { this.num = num; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}