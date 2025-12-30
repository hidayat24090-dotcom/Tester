package com.sistemruangan.controller;

import com.sistemruangan.model.User;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD User
 */
public class UserController {
    
    /**
     * Validasi login user
     */
    public User validateLogin(String username, String password) {
        String query = "SELECT * FROM user WHERE username=? AND password=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nama_lengkap"),
                    rs.getString("email"),
                    rs.getString("no_telepon")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error validasi login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mendapatkan user berdasarkan ID
     */
    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nama_lengkap"),
                    rs.getString("email"),
                    rs.getString("no_telepon")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error mendapatkan user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mendaftarkan user baru
     */
    public boolean registerUser(User user) {
        String query = "INSERT INTO user (username, password, nama_lengkap, email, no_telepon) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNamaLengkap());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getNoTelepon());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error mendaftar user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update profile user
     */
    public boolean updateProfile(User user) {
        String query = "UPDATE user SET nama_lengkap=?, email=?, no_telepon=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getNoTelepon());
            pstmt.setInt(4, user.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ganti password user
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Validasi password lama
        String checkQuery = "SELECT password FROM user WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String currentPassword = rs.getString("password");
                
                if (!currentPassword.equals(oldPassword)) {
                    System.err.println("Password lama tidak sesuai");
                    return false;
                }
                
                // Update password
                String updateQuery = "UPDATE user SET password=? WHERE id=?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newPassword);
                    updateStmt.setInt(2, userId);
                    
                    int rowsAffected = updateStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error ganti password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cek apakah username sudah ada
     */
    public boolean isUsernameExist(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE username=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error cek username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}