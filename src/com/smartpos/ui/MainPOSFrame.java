package com.smartpos.ui;

import com.smartpos.model.CartItem;
import com.smartpos.model.Product;
import com.smartpos.service.ProductDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Inheritance: สืบทอดความสามารถจาก JFrame
public class MainPOSFrame extends JFrame {

    // Encapsulation: กำหนด UI Components เป็น private
    private JTextField txtBarcode;
    private JTable tblCart;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPrice;
    private JButton btnCheckout;
    private JButton btnReset; // <-- เพิ่มปุ่ม Reset
    private JButton btnUndo;  // <-- เพิ่มปุ่ม Undo

    private ProductDatabase productDB;
    private List<CartItem> cartList;

    public MainPOSFrame() {
        productDB = new ProductDatabase();
        cartList = new ArrayList<>();

        initUI();
    }

    private void initUI() {
        setTitle("Smart POS System");
        setSize(750, 500); // ขยายความกว้างเล็กน้อยเพื่อให้ปุ่มวางพอดี
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- ด้านบน: ช่องรับ Input บาร์โค้ด ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Barcode Scan: "));
        txtBarcode = new JTextField(20);
        topPanel.add(txtBarcode);
        add(topPanel, BorderLayout.NORTH);

        // --- ตรงกลาง: ตารางแสดงรายการสินค้า ---
        String[] columns = {"Barcode", "Product Name", "Price", "Qty", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        tblCart = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblCart);
        add(scrollPane, BorderLayout.CENTER);

        // --- ด้านล่าง: ยอดรวมราคา และ แผงปุ่มกด ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        lblTotalPrice = new JLabel(" Total Price: 0.00 THB  ");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTotalPrice.setForeground(Color.BLUE);

        // แผงรวมปุ่มควบคุมฝั่งขวา (Undo, Reset, Checkout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        btnUndo = new JButton("Undo");
        btnUndo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        btnCheckout = new JButton("Checkout / Save");
        btnCheckout.setFont(new Font("Tahoma", Font.BOLD, 16));

        buttonPanel.add(btnUndo);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCheckout);

        bottomPanel.add(lblTotalPrice, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Bind Events ---
        txtBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processBarcodeScan(txtBarcode.getText().trim());
            }
        });

        btnCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });

        // Event สำหรับปุ่ม Undo (ลบรายการล่าสุด)
        btnUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoLastItem();
            }
        });

        // Event สำหรับปุ่ม Reset (ล้างตะกร้าทั้งหมด)
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCart();
            }
        });
    }

    private void processBarcodeScan(String barcode) {
        if (barcode.isEmpty()) return;

        Product product = productDB.findByBarcode(barcode);
        if (product != null) {
            // เช็กว่ามีสินค้านี้ในตะกร้าหรือยัง
            boolean existing = false;
            for (CartItem item : cartList) {
                if (item.getProduct().getBarcode().equals(barcode)) {
                    item.incrementQuantity();
                    existing = true;
                    break;
                }
            }
            if (!existing) {
                cartList.add(new CartItem(product));
            }
            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Product Not Found: " + barcode, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        txtBarcode.setText("");
        txtBarcode.requestFocus(); // ให้ Cursor เด้งกลับมารอรับบาร์โค้ดถัดไปเสมอ
    }

    private void updateCartTable() {
        tableModel.setRowCount(0); // ล้างตารางเดิม
        double grandTotal = 0.0;

        for (CartItem item : cartList) {
            Object[] row = {
                item.getProduct().getBarcode(),
                item.getProduct().getName(),
                String.format("%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("%.2f", item.getTotalPrice())
            };
            tableModel.addRow(row);
            grandTotal += item.getTotalPrice();
        }

        lblTotalPrice.setText(String.format(" Total Price: %.2f THB  ", grandTotal));
    }

    private void checkout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Payment Successful! Saving transaction...", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset รายการเพื่อรอรับลูกค้าคนถัดไป
        cartList.clear();
        updateCartTable();
        txtBarcode.requestFocus();
    }
    
    // ฟังก์ชัน Undo: ลบรายการสินค้าอันล่าสุดออก
    private void undoLastItem() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // ลบรายการตัวสุดท้ายใน List
        cartList.remove(cartList.size() - 1);
        updateCartTable();
        txtBarcode.requestFocus();
    }

    // ฟังก์ชัน Reset: ล้างรายการทั้งหมดในตะกร้า
    private void resetCart() {
        if (cartList.isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to reset the cart?", 
            "Confirm Reset", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cartList.clear();
            updateCartTable();
            txtBarcode.requestFocus();
        }
    }
    
    
}