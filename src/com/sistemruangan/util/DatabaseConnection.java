package com.sistemruangan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas DatabaseConnection untuk mengelola koneksi ke database MySQL
 * UPDATED VERSION - Pastikan port dan password sesuai dengan MySQL Anda
 */
public class DatabaseConnection {
    // ========== KONFIGURASI DATABASE ==========
    // Ganti nilai di bawah sesuai dengan konfigurasi MySQL Anda
    
    private static final String URL = "jdbc:mysql://localhost:3307/sistem_ruangan1";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // =========================================
    
    private static Connection connection = null;
    
    /**
     * Mendapatkan koneksi database (Singleton Pattern)
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Buat koneksi dengan konfigurasi tambahan
                String fullUrl = URL + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                connection = DriverManager.getConnection(fullUrl, USER, PASSWORD);
                
                System.out.println("✅ Koneksi database berhasil!");
                System.out.println("📍 URL: " + URL);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver tidak ditemukan!");
            System.err.println("⚠️  Pastikan file mysql-connector-j-9.1.0.jar ada di folder lib/");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Gagal membuat koneksi database!");
            System.err.println("⚠️  Periksa:");
            System.err.println("   1. MySQL sudah berjalan (services.msc)");
            System.err.println("   2. Port: " + URL.split(":")[2].split("/")[0]);
            System.err.println("   3. Username: " + USER);
            System.err.println("   4. Password sudah benar");
            System.err.println("   5. Database 'sistem_ruangan' sudah dibuat");
            System.err.println("");
            System.err.println("📋 Error detail:");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Gagal menutup koneksi database!");
            e.printStackTrace();
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            
            if (isValid) {
                System.out.println("✅ Test koneksi database: BERHASIL");
            } else {
                System.out.println("❌ Test koneksi database: GAGAL");
            }
            
            return isValid;
        } catch (SQLException e) {
            System.out.println("❌ Test koneksi database: GAGAL");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Main method untuk test koneksi standalone
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TEST KONEKSI DATABASE");
        System.out.println("========================================");
        System.out.println("");
        
        if (testConnection()) {
            System.out.println("");
            System.out.println("🎉 Koneksi database berhasil!");
            System.out.println("✅ Aplikasi siap dijalankan!");
        } else {
            System.out.println("");
            System.out.println("❌ Koneksi database gagal!");
            System.out.println("📋 Periksa konfigurasi di atas.");
        }
        
        closeConnection();
    }
}