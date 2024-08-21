package com.example.supermarket.models;

import java.time.LocalDate;

public class Employee {
    private int employeeId;
    private String name;
    private String position;
    private LocalDate hireDate;
    private double salary;
    private int supermarketId;

    // Default constructor
    public Employee() {
    }

    // Parameterized constructor
    public Employee(int employeeId, String name, String position, LocalDate hireDate, double salary, int supermarketId) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
        this.supermarketId = supermarketId;
    }

    // Getters and setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getSupermarketId() {
        return supermarketId;
    }

    public void setSupermarketId(int supermarketId) {
        this.supermarketId = supermarketId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", hireDate=" + hireDate +
                ", salary=" + salary +
                ", supermarketId=" + supermarketId +
                '}';
    }
}