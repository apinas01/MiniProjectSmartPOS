package com.smartpos.model;

/**
 * Product: Data Model สำหรับเก็บข้อมูลสินค้า
 * Principles: Encapsulation & Polymorphism (Overriding)
 */
public class Product {

    // 1. Attributes (Encapsulation - private fields)
    private String barcode;
    private String name;
    private double price;

    // 2. Constructor
    public Product(String barcode, String name, double price) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
    }

    // 3. Business Logic / Overridden Methods
    @Override
    public String toString() {
        return String.format("%s - %s (%.2f THB)", barcode, name, price);
    }

    // 4. Getters and Setters
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}