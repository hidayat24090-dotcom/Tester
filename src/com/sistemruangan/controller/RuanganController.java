package com.sistemruangan.controller;

import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD Ruangan - WITH PHOTO SUPPORT
 */
public class RuanganController {

    
    /**
     * Mengambil semua data ruangan dari database (dengan foto)
     */
    public ObservableList<Ruangan> getAllRuangan() {
        ObservableList<Ruangan> ruanganList = FXCollections.observableArrayList();
        String query = "SELECT * FROM ruangan ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Ruangan ruangan = new Ruangan(
                    rs.getInt("id"),
                    rs.getInt("id_gedung"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("lantai"),
                    rs.getInt("jumlah_kursi"),
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ruanganList;
    }
    
    /**
     * Menambah ruangan baru ke database (dengan foto)
     */
    public boolean tambahRuangan(Ruangan ruangan) {
        String query = "INSERT INTO ruangan (id_gedung, nama_ruangan, lantai, jumlah_kursi, fasilitas, status, foto_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, ruangan.getIdGedung());
            pstmt.setString(2, ruangan.getNamaRuangan());
            pstmt.setInt(3, ruangan.getLantai());
            pstmt.setInt(4, ruangan.getJumlahKursi());
            pstmt.setString(5, ruangan.getFasilitas());
            pstmt.setString(6, ruangan.getStatus());
            pstmt.setString(7, ruangan.getFotoPath());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error menambah ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengupdate data ruangan (dengan foto)
     */
    public boolean updateRuangan(Ruangan ruangan) {
        String query = "UPDATE ruangan SET id_gedung=?, nama_ruangan=?, lantai=?, jumlah_kursi=?, fasilitas=?, status=?, foto_path=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, ruangan.getIdGedung());
            pstmt.setString(2, ruangan.getNamaRuangan());
            pstmt.setInt(3, ruangan.getLantai());
            pstmt.setInt(4, ruangan.getJumlahKursi());
            pstmt.setString(5, ruangan.getFasilitas());
            pstmt.setString(6, ruangan.getStatus());
            pstmt.setString(7, ruangan.getFotoPath());
            pstmt.setInt(8, ruangan.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update foto ruangan saja
     */
    public boolean updateFotoRuangan(int idRuangan, String fotoPath) {
        String query = "UPDATE ruangan SET foto_path=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, fotoPath);
            pstmt.setInt(2, idRuangan);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update foto ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus ruangan dari database
     */
    public boolean deleteRuangan(int id) {
        String query = "DELETE FROM ruangan WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error hapus ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengubah status ruangan
     */
    public boolean updateStatusRuangan(int id, String status) {
        String query = "UPDATE ruangan SET status=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update status ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mendapatkan ruangan berdasarkan ID
     */
    public Ruangan getRuanganById(int id) {
        String query = "SELECT * FROM ruangan WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Ruangan(
                    rs.getInt("id"),
                    rs.getInt("id_gedung"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("lantai"),
                    rs.getInt("jumlah_kursi"),
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error mendapatkan ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mencari ruangan berdasarkan nama
     */
    public ObservableList<Ruangan> searchRuangan(String keyword) {
        ObservableList<Ruangan> ruanganList = FXCollections.observableArrayList();
        String query = "SELECT * FROM ruangan WHERE nama_ruangan LIKE ? ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Ruangan ruangan = new Ruangan(
                    rs.getInt("id"),
                    rs.getInt("id_gedung"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("lantai"),
                    rs.getInt("jumlah_kursi"),
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ruanganList;
    }

    public ObservableList<Ruangan> getRuanganByGedung(int idGedung) {
        ObservableList<Ruangan> ruanganList = FXCollections.observableArrayList();
        String query = "SELECT r.*, g.nama_gedung FROM ruangan r " +
                      "LEFT JOIN gedung g ON r.id_gedung = g.id " +
                      "WHERE r.id_gedung = ? AND r.status = 'tersedia' " +
                      "ORDER BY r.nama_ruangan ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, idGedung);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ruanganList.add(new Ruangan(
                    rs.getInt("id"),
                    rs.getInt("id_gedung"),
                    rs.getString("nama_gedung") != null ? rs.getString("nama_gedung") : "",
                    rs.getString("nama_ruangan"),
                    rs.getInt("lantai"),
                    rs.getInt("jumlah_kursi"),
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getRuanganByGedung: " + e.getMessage());
            e.printStackTrace();
        }
        return ruanganList;
    }
}