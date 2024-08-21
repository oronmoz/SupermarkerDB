package com.example.supermarket.models;

import java.util.ArrayList;
import java.util.List;

public class Supermarket {
    private int id;
    private String name;
    private Address location;

    public Supermarket() {
        this.location = new Address();
    }

    public Supermarket(String name, Address location) {
        this();
        this.name = name;
        this.location = location;
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

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return getName();
    }
}