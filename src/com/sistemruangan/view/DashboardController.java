package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller untuk halaman Dashboard - WITH APPROVAL NOTIFICATION
 */
public class DashboardController {
    
    @FXML private Button btnRuangan;
    @FXML private Button btnPeminjaman;
    @FXML private Button btnApproval; // NEW
    @FXML private Button btnLaporan;
    @FXML private Button btnLogout;
    
    // NEW: Notification elements
    @FXML private HBox notificationBox;
    @FXML private Label lblNotification;
    @FXML private StackPane badgePending;
    @FXML private Label lblBadgeCount;
    
    private PeminjamanController peminjamanController;
    
    @FXML
    public void initialize() {
        peminjamanController = new PeminjamanController();
        
        setupButtonEffects(btnRuangan);
        setupButtonEffects(btnPeminjaman);
        setupButtonEffects(btnApproval); // NEW
        setupButtonEffects(btnLaporan);
        setupButtonEffects(btnLogout);
        
        // Load pending approvals notification
        loadPendingNotification();
    }
    
    /**
     * Load dan tampilkan notifikasi pending approvals
     */
    private void loadPendingNotification() {
        try {
            int pendingCount = peminjamanController.getPendingApprovalsCount();
            
            if (pendingCount > 0) {
                // Show notification box
                notificationBox.setVisible(true);
                notificationBox.setManaged(true);
                
                // Update notification text
                String text = pendingCount == 1 
                    ? "Ada 1 pengajuan peminjaman menunggu persetujuan" 
                    : "Ada " + pendingCount + " pengajuan peminjaman menunggu persetujuan";
                lblNotification.setText(text);
                
                // Show badge on approval card
                badgePending.setVisible(true);
                badgePending.setManaged(true);
                lblBadgeCount.setText(String.valueOf(pendingCount));
                
                // Animate notification
                FadeTransition fade = new FadeTransition(Duration.millis(500), notificationBox);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();
                
                System.out.println("⚠️ " + pendingCount + " pending approval(s) found");
            } else {
                // Hide notification if no pending
                notificationBox.setVisible(false);
                notificationBox.setManaged(false);
                badgePending.setVisible(false);
                badgePending.setManaged(false);
                
                System.out.println("✅ No pending approvals");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading pending notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup hover effect untuk button
     */
    private void setupButtonEffects(Button button) {
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
    
    @FXML
    private void handleDaftarRuangan() {
        MainApp.showDaftarGedung();
    }
    
    @FXML
    private void handleDataPeminjaman() {
        MainApp.showPeminjamanScene();
    }
    
    /**
     * NEW: Handle approval menu
     */
    @FXML
    private void handleApproval() {
        MainApp.showApprovalPeminjaman();
    }
    
    @FXML
    private void handleLaporan() {
        MainApp.showLaporanTransaksi();
    }
    
    @FXML
    private void handleLogout() {
        MainApp.showLoginScene();
    }
}