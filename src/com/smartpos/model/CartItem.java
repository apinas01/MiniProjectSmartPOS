package com.smartpos.model;

/**
 * CartItem: Model รายการสินค้าในตะกร้า (ผูก Product กับ Quantity เข้าด้วยกัน)
 * Principles: Composition & Encapsulation
 */
public class CartItem {

    // 1. Attributes
    private final Product product; // Composition: ดึง Product มาใช้งาน
    private int quantity;

    // 2. Constructor
    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1; // สแกนครั้งแรกเริ่มต้นที่ 1 ชิ้น
    }

    // 3. Business Logic Methods (จัดการจำนวนและราคา)
    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // 4. Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}