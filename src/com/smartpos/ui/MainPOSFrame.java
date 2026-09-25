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

    private ProductDatabase productDB;
    private List<CartItem> cartList;

    public MainPOSFrame() {
        productDB = new ProductDatabase();
        cartList = new ArrayList<>();

        initUI();
    }

    private void initUI() {
        setTitle("Smart POS System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // แสดงกลางหน้าจอ
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

        // --- ด้านล่าง: ยอดรวมราคา และ ปุ่ม Checkout ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblTotalPrice = new JLabel(" Total Price: 0.00 THB  ");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTotalPrice.setForeground(Color.BLUE);

        btnCheckout = new JButton("Checkout / Save");
        btnCheckout.setFont(new Font("Tahoma", Font.BOLD, 16));

        bottomPanel.add(lblTotalPrice, BorderLayout.WEST);
        bottomPanel.add(btnCheckout, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Bind Events ---
        // เมื่อพิมพ์/สแกนแล้วกด Enter
        txtBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processBarcodeScan(txtBarcode.getText().trim());
            }
        });

        // ปุ่มชำระเงิน/บันทึกรายการ
        btnCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
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
}