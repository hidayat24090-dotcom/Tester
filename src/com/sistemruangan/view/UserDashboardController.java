package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.util.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller untuk Dashboard User
 */
public class UserDashboardController {
    
    @FXML private Label lblWelcome;
    @FXML private Button btnLogout;
    
    @FXML
    public void initialize() {
        // Set welcome message
        if (SessionManager.isLoggedIn()) {
            lblWelcome.setText("Selamat datang, " + SessionManager.getNamaLengkap() + "!");
        }
        
        // Setup button effects
        setupButtonEffect(btnLogout);
    }
    
    @FXML
    private void handleLihatRuangan() {
        MainApp.showUserRuanganList();
    }
    
    @FXML
    private void handleAjukanPeminjaman() {
        MainApp.showUserPeminjamanForm();
    }
    
    @FXML
    private void handleRiwayat() {
        MainApp.showUserRiwayat();
    }
    
    @FXML
    private void handleJadwal() {
        MainApp.showJadwalBulanan();
    }
    
    @FXML
    private void handleProfile() {
        MainApp.showUserProfile();
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.logout();
        MainApp.showUserLogin();
    }
    
    /**
     * Setup hover effect untuk button
     */
    private void setupButtonEffect(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        shadow.setRadius(10);
        
        button.setOnMouseEntered(e -> {
            button.setEffect(shadow);
            FadeTransition ft = new FadeTransition(Duration.millis(200), button);
            ft.setFromValue(1.0);
            ft.setToValue(0.8);
            ft.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setEffect(null);
            FadeTransition ft = new FadeTransition(Duration.millis(200), button);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });
    }
}