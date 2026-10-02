package com.smartpos.service;

import com.smartpos.model.Product;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductDatabase: ระบบคลังข้อมูลสินค้า
 * Principles: Inheritance, Polymorphism (Overloading), HashMap ($O(1)$ lookup)
 */
public class ProductDatabase extends BaseDatabase {

    // 1. Attributes
    private final Map<String, Product> products = new HashMap<>();

    // 2. Constructor
    public ProductDatabase() {
        super("Smart POS Database"); // เรียก Constructor คลาสแม่ (BaseDatabase)
        initMockData();
    }

    // 3. Business Logic Methods
    public Product findByBarcode(String barcode) {
        return products.get(barcode);
    }

    // Polymorphism (Overloading): ค้นหาด้วย int ได้ด้วย
    public Product findByBarcode(int barcode) {
        return findByBarcode(String.valueOf(barcode));
    }

    public void addProduct(Product product) {
        products.put(product.getBarcode(), product);
    }

    // 4. Private Helper Methods
    private void initMockData() {
        products.put("8901491101837", new Product("8901491101837", "Sample Test Product", 35.00));
        products.put("101", new Product("101", "Lay Classic Chips", 20.00));
        products.put("102", new Product("102", "Coca Cola 325ml", 15.00));
        products.put("103", new Product("103", "Lactasoy Milk 500ml", 12.00));
        products.put("104", new Product("104", "Potato", 10.00));
    }
}