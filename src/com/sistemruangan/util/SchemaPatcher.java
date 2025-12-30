package com.sistemruangan.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaPatcher {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   DATABASE SCHEMA PATCHER");
        System.out.println("========================================");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("❌ Failed to connect to database!");
                return;
            }
            
            System.out.println("✅ Connected to database.");
            
            // 1. Create table 'gedung' if not exists
            if (!tableExists(conn, "gedung")) {
                System.out.println("⚠️ Table 'gedung' missing. Creating...");
                createGedungTable(conn);
            } else {
                System.out.println("✅ Table 'gedung' already exists.");
            }
            
            // 2. Add columns to 'ruangan' if missing
            if (tableExists(conn, "ruangan")) {
                System.out.println("🔍 Checking 'ruangan' table columns...");
                
                checkAndAddColumn(conn, "ruangan", "id_gedung", "INT");
                checkAndAddColumn(conn, "ruangan", "lantai", "INT NOT NULL DEFAULT 1");
                checkAndAddColumn(conn, "ruangan", "foto_path", "VARCHAR(255)");
                
                // Ensure foreign key exists (optional, might fail if data inconsistent, skipping for safety in simple patch)
            } else {
                System.out.println("❌ Table 'ruangan' NOT found! Run full schema.sql instead.");
            }

            System.out.println("\n✅ Patching completed successfully!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[] {"TABLE"})) {
            return rs.next();
        }
    }
    
    private static void createGedungTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE gedung (" +
                     "id INT PRIMARY KEY AUTO_INCREMENT," +
                     "nama_gedung VARCHAR(100) NOT NULL UNIQUE," +
                     "jumlah_lantai INT NOT NULL," +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                     "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                     "INDEX idx_nama (nama_gedung)" +
                     ") ENGINE=InnoDB";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("   ✅ Created table 'gedung'");
            
            // Insert dummy buildings
            stmt.executeUpdate("INSERT INTO gedung (nama_gedung, jumlah_lantai) VALUES ('Gedung A', 4), ('Gedung B', 3), ('Gedung C', 5)");
            System.out.println("   ✅ Inserted default buildings");
        }
    }
    
    private static void checkAndAddColumn(Connection conn, String tableName, String columnName, String columnType) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        boolean columnExists = false;
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            if (rs.next()) {
                columnExists = true;
            }
        }
        
        if (!columnExists) {
            System.out.println("   ⚠️ Column '" + columnName + "' missing. Adding...");
            String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
                System.out.println("   ✅ Added column '" + columnName + "'");
            }
        } else {
            System.out.println("   ✅ Column '" + columnName + "' exists.");
        }
    }
}
