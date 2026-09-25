package com.smartpos.model;

public class Product {
    // Encapsulation: ซ่อน Field ไว้เป็น private
    private String barcode;
    private String name;
    private double price;

    public Product(String barcode, String name, double price) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
    }

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

    @Override
    public String toString() {
        return String.format("%s - %s (%.2f THB)", barcode, name, price);
    }
}
