package com.smartpos.ui;

import com.smartpos.model.CartItem;
import com.smartpos.model.Product;
import com.smartpos.service.ProductDatabase;
import com.smartpos.service.ScannerServer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * MainPOSFrame: หน้าจอหลักระบบ POS (Swing UI)
 * Principles: Inheritance (extends JFrame), Encapsulation, Loose Coupling
 * Data Structures: List (Cart Management), Stack (Undo LIFO History)
 */
public class MainPOSFrame extends JFrame {

    // 1. UI Components (Encapsulation)
    private JTextField txtBarcode;
    private JTable tblCart;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPrice;
    private JButton btnCheckout;
    private JButton btnReset;
    private JButton btnUndo;

    // 2. Business Logic & Data Structures
    private final ProductDatabase productDB;
    private final List<CartItem> cartList;
    private final Stack<String> scanHistory; // LIFO Stack สำหรับฟังก์ชัน Undo
    private ScannerServer scannerServer;

    // 3. Constructor
    public MainPOSFrame() {
        this.productDB = new ProductDatabase();
        this.cartList = new ArrayList<>();
        this.scanHistory = new Stack<>();

        initUI();
        startServer();
    }

    // 4. Primary Business Logic / Action Handlers

    /**
     * ประมวลผลเมื่อมีการสแกนบาร์โค้ด (จาก Keyboard/Scanner หรือ Mobile LAN)
     */
    public void processBarcodeScan(String rawBarcode) {
        if (rawBarcode == null) return;

        String barcode = rawBarcode.trim(); // ล้างช่องว่างและขยะตัวอักษร
        if (barcode.isEmpty()) return;

        Product product = productDB.findByBarcode(barcode);
        if (product != null) {
            boolean existing = false;
            for (CartItem item : cartList) {
                if (item.getProduct().getBarcode().equalsIgnoreCase(barcode)) {
                    item.incrementQuantity();
                    existing = true;
                    break;
                }
            }

            if (!existing) {
                cartList.add(new CartItem(product));
            }

            // บันทึกรหัสลง Stack เพื่อใช้ย้อนกลับ (Undo)
            scanHistory.push(barcode);
            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Product Not Found: " + barcode,
                "Warning",
                JOptionPane.WARNING_MESSAGE
            );
        }

        txtBarcode.setText("");
        txtBarcode.requestFocus();
    }

    /**
     * ถอยกลับการสแกนล่าสุด (Undo using Stack - LIFO)
     */
    private void undoLastItem() {
        if (scanHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // ดึงรหัสบาร์โค้ดสแกนล่าสุดออกจาก Stack
        String lastScannedBarcode = scanHistory.pop();

        for (int i = 0; i < cartList.size(); i++) {
            CartItem item = cartList.get(i);
            if (item.getProduct().getBarcode().equalsIgnoreCase(lastScannedBarcode)) {
                item.decrementQuantity(); // ลดจำนวนลง 1

                if (item.getQuantity() <= 0) {
                    cartList.remove(i); // ถ้าราคา/จำนวนเหลือ 0 ให้ลบแถวออก
                }
                break;
            }
        }

        updateCartTable();
        txtBarcode.requestFocus();
    }

    /**
     * ล้างรายการสินค้าทั้งหมดในตะกร้า
     */
    private void resetCart() {
        if (cartList.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the cart?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cartList.clear();
            scanHistory.clear();
            updateCartTable();
            txtBarcode.requestFocus();
        }
    }

    /**
     * บันทึกชำระเงินและจบรายการ
     */
    private void checkout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Payment Successful! Transaction Saved.", "Success", JOptionPane.INFORMATION_MESSAGE);

        cartList.clear();
        scanHistory.clear();
        updateCartTable();
        txtBarcode.requestFocus();
    }

    // 5. Helper Methods (UI Construction & Server Management)

    private void initUI() {
        String localIP = ScannerServer.getLocalIP();
        setTitle("Smart POS System - Mobile Scan: http://" + localIP + ":8080");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Barcode Input
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Barcode Scan: "));
        txtBarcode = new JTextField(20);
        topPanel.add(txtBarcode);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Cart JTable
        String[] columns = {"Barcode", "Product Name", "Price", "Qty", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        tblCart = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblCart);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Total Price & Control Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        lblTotalPrice = new JLabel(" Total Price: 0.00 THB  ");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTotalPrice.setForeground(Color.BLUE);

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

        // Bind UI Event Listeners
        txtBarcode.addActionListener(e -> processBarcodeScan(txtBarcode.getText()));
        btnUndo.addActionListener(e -> undoLastItem());
        btnReset.addActionListener(e -> resetCart());
        btnCheckout.addActionListener(e -> checkout());
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);
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

    private void startServer() {
        try {
            scannerServer = new ScannerServer(8080, barcode -> {
                // Thread Safety for Swing Components
                SwingUtilities.invokeLater(() -> processBarcodeScan(barcode));
            });
            scannerServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}