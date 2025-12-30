package com.sistemruangan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.sistemruangan.util.DatabaseConnection;

/**
 * Main Application with Overlay Dialog Support
 * CRITICAL: contentContainer for content, rootContainer for overlays
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    private static StackPane rootContainer;      // For overlays
    private static StackPane contentContainer;   // For actual content
    private static Scene mainScene;
    private static boolean isFullscreen = false;
    private static HBox customTitleBar;
    
    @Override
    public void start(Stage stage) {
        try {
            System.out.println("========================================");
            System.out.println("   APLIKASI STARTING...");
            System.out.println("========================================");
            
            System.out.println("\n[1/4] Testing database connection...");
            if (!DatabaseConnection.testConnection()) {
                showErrorDialog("Database Error", 
                    "Gagal koneksi ke database!\n\n" +
                    "Pastikan:\n" +
                    "1. MySQL service sudah running\n" +
                    "2. Database 'sistem_ruangan' sudah dibuat\n" +
                    "3. Port dan password di DatabaseConnection.java sudah benar");
                System.exit(1);
            }
            System.out.println("✅ Database connection OK");
            
            primaryStage = stage;
            
            System.out.println("\n[2/4] Creating main scene container with OVERLAY support...");
            StackPane mainContainer = new StackPane();
            
            // CRITICAL: Two-layer system
            // Layer 1: contentContainer (for FXML content)
            contentContainer = new StackPane();
            contentContainer.setStyle("-fx-background-color: transparent;");
            
            // Layer 2: rootContainer (includes content + overlays)
            rootContainer = new StackPane();
            rootContainer.setStyle("-fx-background-color: transparent;");
            rootContainer.getChildren().add(contentContainer);
            
            // Custom title bar
            customTitleBar = createCustomTitleBar();
            customTitleBar.setManaged(false);
            customTitleBar.setVisible(false);
            
            // Stack all layers
            mainContainer.getChildren().addAll(rootContainer, customTitleBar);
            StackPane.setAlignment(customTitleBar, Pos.TOP_RIGHT);
            
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            double screenWidth = screen.getBounds().getWidth();
            double screenHeight = screen.getBounds().getHeight();
            
            mainScene = new Scene(mainContainer, screenWidth, screenHeight);
            
            String cssPath = "/css/style.css";
            if (MainApp.class.getResource(cssPath) != null) {
                mainScene.getStylesheets().add(MainApp.class.getResource(cssPath).toExternalForm());
                System.out.println("✅ CSS loaded");
            }
            
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Sistem Inventaris & Peminjaman Ruangan");
            primaryStage.setFullScreenExitHint("");
            
            setupFullscreenToggle();
            
            primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                isFullscreen = isNowFullScreen;
                customTitleBar.setVisible(!isNowFullScreen);
                customTitleBar.setManaged(!isNowFullScreen);
                
                if (isNowFullScreen) {
                    System.out.println("🖥️  Fullscreen Mode: ON");
                } else {
                    System.out.println("🖥️  Fullscreen Mode: OFF - Controls visible");
                }
            });
            
            System.out.println("\n[3/4] Loading initial content...");
            showUserLogin();
            
            System.out.println("\n[4/4] Showing stage...");
            primaryStage.show();
            
            primaryStage.setFullScreen(true);
            isFullscreen = true;
            System.out.println("🖥️  Starting in FULLSCREEN mode");
            
            System.out.println("\n✅ Application started successfully!");
            System.out.println("✨ OVERLAY DIALOG SYSTEM: ACTIVE");
            System.out.println("📌 F11 atau ESC: Toggle fullscreen");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("\n❌ FATAL ERROR:");
            e.printStackTrace();
            showErrorDialog("Application Error", 
                "Aplikasi gagal dimulai!\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Lihat console untuk detail error.");
        }
    }
    
    private HBox createCustomTitleBar() {
        HBox titleBar = new HBox(0);
        titleBar.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7);" +
            "-fx-padding: 5 10 5 10;" +
            "-fx-background-radius: 5;"
        );
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(40);
        titleBar.setMaxWidth(200);
        
        Button btnMinimize = new Button("—");
        styleWindowButton(btnMinimize, "#F1C40F");
        btnMinimize.setOnAction(e -> primaryStage.setIconified(true));
        
        Button btnMaximize = new Button("□");
        styleWindowButton(btnMaximize, "#2ECC71");
        btnMaximize.setOnAction(e -> {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);
                btnMaximize.setText("□");
            } else {
                primaryStage.setMaximized(true);
                btnMaximize.setText("❐");
            }
        });
        
        Button btnClose = new Button("✕");
        styleWindowButton(btnClose, "#E74C3C");
        btnClose.setOnAction(e -> handleClose());
        
        titleBar.getChildren().addAll(btnMinimize, btnMaximize, btnClose);
        
        return titleBar;
    }
    
    private void styleWindowButton(Button button, String hoverColor) {
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 15 5 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 3;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 15 5 15;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 3;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 15 5 15;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 3;"
            )
        );
    }
    
    private void handleClose() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Keluar");
        confirm.setHeaderText("Keluar dari Aplikasi");
        confirm.setContentText("Yakin ingin keluar dari aplikasi?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            DatabaseConnection.closeConnection();
            System.out.println("\n👋 Application closed by user.");
            primaryStage.close();
            System.exit(0);
        }
    }
    
    private static void setupFullscreenToggle() {
        if (mainScene != null) {
            mainScene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F11),
                () -> toggleFullscreen()
            );
            
            mainScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE && isFullscreen) {
                    toggleFullscreen();
                    event.consume();
                }
            });
        }
    }
    
    public static void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        primaryStage.setFullScreen(isFullscreen);
        
        if (isFullscreen) {
            System.out.println("🖥️  Fullscreen: ON");
        } else {
            System.out.println("🖥️  Fullscreen: OFF (Controls visible)");
        }
    }
    
    // ========== NAVIGATION METHODS ==========
    
    public static void showLoginScene() {
        loadContent("/fxml/Login.fxml", "Login Admin");
    }
    
    public static void showDashboard() {
        loadContent("/fxml/Dashboard.fxml", "Dashboard Admin");
    }
    
    public static void showRuanganScene() {
        loadContent("/fxml/DaftarRuangan.fxml", "Daftar Ruangan");
    }
    
    public static void showPeminjamanScene() {
        loadContent("/fxml/DataPeminjaman.fxml", "Data Peminjaman");
    }
    
    public static void showLaporanTransaksi() {
        loadContent("/fxml/LaporanTransaksi.fxml", "Laporan Transaksi");
    }
    
    public static void showUserLogin() {
        loadContent("/fxml/UserLogin.fxml", "Login User");
    }
    
    public static void showUserRegister() {
        loadContent("/fxml/UserRegister.fxml", "Registrasi User");
    }
    
    public static void showUserDashboard() {
        loadContent("/fxml/UserDashboard.fxml", "Dashboard User");
    }
    
    public static void showUserRuanganList() {
        loadContent("/fxml/UserRuanganList.fxml", "Daftar Ruangan");
    }
    
    public static void showUserPeminjamanForm() {
        loadContent("/fxml/UserPeminjamanForm.fxml", "Form Peminjaman");
    }
    
    public static void showUserRiwayat() {
        loadContent("/fxml/UserRiwayat.fxml", "Riwayat Peminjaman");
    }
    
    public static void showJadwalBulanan() {
        loadContent("/fxml/JadwalBulanan.fxml", "Jadwal Bulanan");
    }
    
    public static void showUserProfile() {
        loadContent("/fxml/UserProfile.fxml", "Profile User");
    }

    public static void showApprovalPeminjaman() {
        loadContent("/fxml/ApprovalPeminjaman.fxml", "Persetujuan Peminjaman");
    }

    public static void showDaftarGedung() {
        loadContent("/fxml/DaftarGedung.fxml", "Kelola Gedung");
    }

    public static void showRuanganPerGedung() {
        loadContent("/fxml/DaftarRuanganPerGedung.fxml", "Kelola Ruangan");
    }

    public static void showUserGedungList() {
        loadContent("/fxml/UserGedungList.fxml", "Daftar Gedung");
    }

    public static void showUserRuanganPerGedung() {
        loadContent("/fxml/UserRuanganPerGedung.fxml", "Daftar Ruangan");
    }

    private static void loadContent(String fxmlPath, String title) {
        try {
            System.out.println("\n📄 Loading: " + fxmlPath);
            
            if (MainApp.class.getResource(fxmlPath) == null) {
                throw new Exception("FXML not found: " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent content = loader.load();
            
            // CRITICAL: Use contentContainer, NOT rootContainer
            contentContainer.getChildren().setAll(content);
            
            if (content instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region region = (javafx.scene.layout.Region) content;
                region.setPrefWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
                region.setPrefHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
            }
            
            primaryStage.setTitle(title);
            System.out.println("   ✅ Loaded: " + title);
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR loading: " + fxmlPath);
            e.printStackTrace();
            showErrorDialog("Load Error", "Gagal memuat: " + title);
        }
    }
    
    /**
     * CRITICAL: Get rootContainer for overlay dialogs
     * This is the ONLY way controllers should access the root!
     */
    public static StackPane getRootContainer() {
        return rootContainer;
    }
    
    private static void showErrorDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing dialog: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
        System.out.println("\n👋 Application closed.");
    }
    
    public static void main(String[] args) {
        System.out.println("\n🚀 Starting Sistem Inventaris & Peminjaman Ruangan...");
        launch(args);
    }
}