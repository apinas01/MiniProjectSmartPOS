package com.smartpos.model;

public class CartItem {
    // Composition: ดึงคลาส Product มาใช้งานข้างใน
    private Product product;
    private int quantity;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1; // สแกนครั้งแรกเริ่มที่ 1 ชิ้น
    }

    // เพิ่มจำนวนสินค้าเมื่อสแกนรหัสเดิมซ้ำ
    public void incrementQuantity() {
        this.quantity++;
    }

    // คำนวณราคารวมของรายการนี้ (ราคา x จำนวน)
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
    
    // เพิ่ม Method นี้ต่อจาก incrementQuantity()
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
    
}