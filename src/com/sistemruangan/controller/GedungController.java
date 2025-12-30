package com.sistemruangan.controller;

import com.sistemruangan.model.Gedung;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD Gedung
 */
public class GedungController {

    public ObservableList<Gedung> getAllGedung() {
        ObservableList<Gedung> gedungList = FXCollections.observableArrayList();
        String query = "SELECT * FROM gedung ORDER BY nama_gedung ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                gedungList.add(new Gedung(
                    rs.getInt("id"),
                    rs.getString("nama_gedung"),
                    rs.getInt("jumlah_lantai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data gedung: " + e.getMessage());
            e.printStackTrace();
        }
        return gedungList;
    }

    public boolean tambahGedung(Gedung gedung) {
        String query = "INSERT INTO gedung (nama_gedung, jumlah_lantai) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, gedung.getNamaGedung());
            pstmt.setInt(2, gedung.getJumlahLantai());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error menambah gedung: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGedung(Gedung gedung) {
        String query = "UPDATE gedung SET nama_gedung=?, jumlah_lantai=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, gedung.getNamaGedung());
            pstmt.setInt(2, gedung.getJumlahLantai());
            pstmt.setInt(3, gedung.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update gedung: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGedung(int id) {
        String query = "DELETE FROM gedung WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error hapus gedung: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
