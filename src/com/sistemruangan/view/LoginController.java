package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.util.DatabaseConnection;
import com.sistemruangan.util.DialogUtil;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controller untuk halaman Login Admin - DEBUG VERSION + NEW FEATURES
 */
public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button userLoginButton;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        // Add hover effect untuk login button
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        
        loginButton.setOnMouseEntered(e -> loginButton.setEffect(shadow));
        loginButton.setOnMouseExited(e -> loginButton.setEffect(null));
        
        // Add hover effect untuk user login button
        if (userLoginButton != null) {
            userLoginButton.setOnMouseEntered(e -> userLoginButton.setEffect(shadow));
            userLoginButton.setOnMouseExited(e -> userLoginButton.setEffect(null));
        }
        
        System.out.println("✅ LoginController initialized");
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        System.out.println("\n========================================");
        System.out.println("   LOGIN ATTEMPT - ADMIN");
        System.out.println("========================================");
        System.out.println("Username input: '" + username + "'");
        System.out.println("Password input: '" + password + "'");
        System.out.println("Username length: " + username.length());
        System.out.println("Password length: " + password.length());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong!");
            System.out.println("❌ Empty credentials");
            return;
        }
        
        if (validateLogin(username, password)) {
            System.out.println("✅ Login BERHASIL!");
            System.out.println("========================================\n");
            errorLabel.setVisible(false);
            MainApp.showDashboard();
        } else {
            showError("Username atau password salah!");
            System.out.println("❌ Login GAGAL!");
            System.out.println("========================================\n");
        }
    }
    
    @FXML
    private void handleUserLogin() {
        System.out.println("🔄 Switching to User Login...");
        MainApp.showUserLogin();
    }
    
    /**
     * Validasi login ke database - DEBUG VERSION
     */
    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username=? AND password=?";
        
        System.out.println("\n[DEBUG] Executing query: " + query);
        System.out.println("[DEBUG] Parameter 1 (username): '" + username + "'");
        System.out.println("[DEBUG] Parameter 2 (password): '" + password + "'");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (conn == null) {
                System.err.println("❌ Database connection is NULL!");
                return false;
            }
            
            System.out.println("✅ Database connected");
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            System.out.println("[DEBUG] Query executed, checking results...");
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("✅ User FOUND in database!");
                System.out.println("   ID: " + rs.getInt("id"));
                System.out.println("   Username: " + rs.getString("username"));
                System.out.println("   Password: " + rs.getString("password"));
                System.out.println("   Nama: " + rs.getString("nama_lengkap"));
                return true;
            } else {
                System.out.println("❌ User NOT FOUND in database!");
                System.out.println("\n[DEBUG] Checking all users in database:");
                
                // Debug: Tampilkan semua user di database
                try (PreparedStatement debugStmt = conn.prepareStatement("SELECT * FROM admin");
                     ResultSet debugRs = debugStmt.executeQuery()) {
                    
                    int count = 0;
                    while (debugRs.next()) {
                        count++;
                        System.out.println("   User " + count + ":");
                        System.out.println("      ID: " + debugRs.getInt("id"));
                        System.out.println("      Username: '" + debugRs.getString("username") + "'");
                        System.out.println("      Password: '" + debugRs.getString("password") + "'");
                        System.out.println("      Nama: " + debugRs.getString("nama_lengkap"));
                    }
                    
                    if (count == 0) {
                        System.out.println("   ⚠️  NO USERS in admin table!");
                        System.out.println("   Run this SQL:");
                        System.out.println("   INSERT INTO admin (username, password, nama_lengkap) VALUES ('admin', 'admin123', 'Administrator Sistem');");
                    }
                    
                } catch (Exception debugEx) {
                    System.err.println("❌ Error checking users: " + debugEx.getMessage());
                }
                
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR during login validation:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menampilkan pesan error
     */
    private void showError(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.ERROR,
            "Error",
            message,
            MainApp.getRootContainer()
        );
    }
    
    @FXML
    private void handleEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleLogin();
        }
    }
}