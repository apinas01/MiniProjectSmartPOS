package com.smartpos.service;

import com.smartpos.model.Product;
import java.util.HashMap;
import java.util.Map;

public class ProductDatabase extends BaseDatabase {
    private Map<String, Product> products = new HashMap<>();

    public ProductDatabase() {
        super("Smart POS Database"); // เรียก Constructor คลาสแม่
        initMockData();
    }

    // ข้อมูลสินค้าจำลองชั่วคราว
    private void initMockData() {
        products.put("101", new Product("101", "Lay Classic Chips", 20.00));
        products.put("102", new Product("102", "Coca Cola 325ml", 15.00));
        products.put("103", new Product("103", "Lactasoy Milk 500ml", 12.00));
        products.put("104", new Product("104", "Potato", 10.00));
    }

    // ค้นหาสินค้าจากรหัสบาร์โค้ด
    public Product findByBarcode(String barcode) {
        return products.get(barcode);
    }

    // ฟังก์ชันสำหรับเพิ่มสินค้าใหม่ในอนาคต
    public void addProduct(Product product) {
        products.put(product.getBarcode(), product);
    }

    // Polymorphism (Overloading): ค้นหาด้วย int
    public Product findByBarcode(int barcode) {
        return findByBarcode(String.valueOf(barcode));
    }
}
