package com.smartpos.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.function.Consumer;

/**
 * ScannerServer: Embedded HTTP Server รับค่าการสแกนบาร์โค้ดจากมือถือผ่านวง LAN
 * Network Layer & Event Callback Pattern
 */
public class ScannerServer {

    // 1. Attributes
    private HttpServer server;
    private final int port;
    private final Consumer<String> onBarcodeScanned; // Event Callback ไปยัง UI/Controller

    // 2. Constructor
    public ScannerServer(int port, Consumer<String> onBarcodeScanned) {
        this.port = port;
        this.onBarcodeScanned = onBarcodeScanned;
    }

    // 3. Public Server Control Methods
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Route 1: หน้า Web Scanner สำหรับเบราว์เซอร์บนมือถือ
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String html = getWebScannerPage();
                byte[] responseBytes = html.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        });

        // Route 2: API Endpoint รับรหัสบาร์โค้ดที่สแกนจากมือถือ
        server.createContext("/scan", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery(); // รับ ?code=XXXX
                String barcode = "";
                if (query != null && query.startsWith("code=")) {
                    barcode = query.substring(5);
                }

                // ส่งค่ารหัสบาร์โค้ดกลับไปประมวลผลที่ UI
                if (!barcode.isEmpty() && onBarcodeScanned != null) {
                    onBarcodeScanned.accept(barcode);
                }

                String response = "OK";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });

        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // 4. Utility / Helper Methods
    /**
     * ดึง IPv4 ของเครื่องในวง LAN (Wi-Fi) อัตโนมัติ
     */
    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
                        return addr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }

    /**
     * หน้า HTML + JavaScript (Html5-QRCode) สำหรับเปิดบนเบราว์เซอร์มือถือ
     */
    private String getWebScannerPage() {
        return "<!DOCTYPE html>"
                + "<html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Smart POS Mobile Scanner</title>"
                + "<script src='https://unpkg.com/html5-qrcode'></script>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; text-align: center; background: #f4f4f9; margin: 0; padding: 15px; }"
                + "#reader { width: 100%; max-width: 400px; margin: 15px auto; background: white; border-radius: 8px; padding: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }"
                + "#result { color: #2e7d32; font-weight: bold; font-size: 1.2em; margin-top: 10px; }"
                + "</style>"
                + "</head><body>"
                + "<h2>Smart POS Scanner</h2>"
                + "<p>Point camera at product barcode</p>"
                + "<div id='reader'></div>"
                + "<div id='result'>Ready...</div>"
                + "<script>"
                + "let lastCode = '';"
                + "function onScanSuccess(decodedText) {"
                + "  if(decodedText !== lastCode) {"
                + "    lastCode = decodedText;"
                + "    document.getElementById('result').innerText = 'Scanned: ' + decodedText;"
                + "    fetch('/scan?code=' + encodeURIComponent(decodedText));"
                + "    setTimeout(() => { lastCode = ''; }, 1800);"
                + "  }"
                + "}"
                + "let html5QrcodeScanner = new Html5QrcodeScanner('reader', { fps: 10, qrbox: {width: 250, height: 150} });"
                + "html5QrcodeScanner.render(onScanSuccess);"
                + "</script></body></html>";
    }
}