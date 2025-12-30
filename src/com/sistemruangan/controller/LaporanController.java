package com.sistemruangan.controller;

import com.sistemruangan.model.StatistikRuangan;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller untuk Laporan dan Statistik
 */
public class LaporanController {
    
    /**
     * Get statistik semua ruangan
     */
    public ObservableList<StatistikRuangan> getStatistikRuangan() {
        ObservableList<StatistikRuangan> statistikList = FXCollections.observableArrayList();
        String query = "SELECT * FROM v_statistik_ruangan";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                StatistikRuangan statistik = new StatistikRuangan(
                    rs.getInt("id"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("total_peminjaman"),
                    rs.getInt("peminjaman_aktif"),
                    rs.getInt("peminjaman_selesai"),
                    rs.getInt("peminjaman_batal"),
                    rs.getDouble("persentase_penggunaan")
                );
                statistikList.add(statistik);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil statistik ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return statistikList;
    }
    
    /**
     * Get statistik peminjaman per bulan untuk periode tertentu
     */
    public Map<String, Integer> getStatistikBulanan(int tahun) {
        Map<String, Integer> statistik = new HashMap<>();
        String query = "SELECT nama_bulan, total_peminjaman FROM v_statistik_bulanan WHERE tahun = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, tahun);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                statistik.put(rs.getString("nama_bulan"), rs.getInt("total_peminjaman"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil statistik bulanan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return statistik;
    }
    
    /**
     * Get total statistik keseluruhan
     */
    public Map<String, Object> getTotalStatistik() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total ruangan
            String queryRuangan = "SELECT COUNT(*) as total FROM ruangan";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(queryRuangan)) {
                if (rs.next()) {
                    stats.put("totalRuangan", rs.getInt("total"));
                }
            }
            
            // Total peminjaman
            String queryPeminjaman = "SELECT COUNT(*) as total FROM peminjaman";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(queryPeminjaman)) {
                if (rs.next()) {
                    stats.put("totalPeminjaman", rs.getInt("total"));
                }
            }
            
            // Peminjaman aktif
            String queryAktif = "SELECT COUNT(*) as total FROM peminjaman WHERE status_peminjaman = 'aktif'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(queryAktif)) {
                if (rs.next()) {
                    stats.put("peminjamanAktif", rs.getInt("total"));
                }
            }
            
            // Peminjaman selesai
            String querySelesai = "SELECT COUNT(*) as total FROM peminjaman WHERE status_peminjaman = 'selesai'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(querySelesai)) {
                if (rs.next()) {
                    stats.put("peminjamanSelesai", rs.getInt("total"));
                }
            }
            
            // Ruangan tersedia
            String queryTersedia = "SELECT COUNT(*) as total FROM ruangan WHERE status = 'tersedia'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(queryTersedia)) {
                if (rs.next()) {
                    stats.put("ruanganTersedia", rs.getInt("total"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error mengambil total statistik: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get data untuk pie chart status peminjaman
     */
    public Map<String, Integer> getStatusPeminjamanData() {
        Map<String, Integer> data = new HashMap<>();
        String query = "SELECT status_peminjaman, COUNT(*) as jumlah FROM peminjaman GROUP BY status_peminjaman";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                data.put(rs.getString("status_peminjaman"), rs.getInt("jumlah"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data status peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }
    
    /**
     * Get ruangan paling populer (top 5)
     */
    public ObservableList<StatistikRuangan> getRuanganPopuler(int limit) {
        ObservableList<StatistikRuangan> populerList = FXCollections.observableArrayList();
        String query = "SELECT * FROM v_ruangan_populer LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StatistikRuangan statistik = new StatistikRuangan();
                statistik.setNamaRuangan(rs.getString("nama_ruangan"));
                statistik.setTotalPeminjaman(rs.getInt("total_peminjaman"));
                statistik.setPeminjamanSelesai(rs.getInt("berhasil"));
                statistik.setPersentasePenggunaan(rs.getDouble("persentase"));
                populerList.add(statistik);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil ruangan populer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return populerList;
    }
}