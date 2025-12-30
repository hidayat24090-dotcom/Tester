package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.UserController;
import com.sistemruangan.model.User;
import com.sistemruangan.util.DialogUtil;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Controller untuk halaman Registrasi User
 */
public class UserRegisterController {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtNamaLengkap;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelepon;
    @FXML private Button btnRegister;
    @FXML private Label errorLabel;
    @FXML private Label lblUsernameError;
    
    private UserController userController;
    
    @FXML
    public void initialize() {
        userController = new UserController();
        
        // Add hover effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        
        btnRegister.setOnMouseEntered(e -> btnRegister.setEffect(shadow));
        btnRegister.setOnMouseExited(e -> btnRegister.setEffect(null));
        
        // Check username availability on text change
        txtUsername.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                checkUsernameAvailability(newVal.trim());
            } else {
                lblUsernameError.setVisible(false);
            }
        });
    }
    
    /**
     * Check apakah username sudah digunakan
     */
    private void checkUsernameAvailability(String username) {
        if (userController.isUsernameExist(username)) {
            lblUsernameError.setText("Username sudah digunakan");
            lblUsernameError.setVisible(true);
        } else {
            lblUsernameError.setText("Username tersedia");
            lblUsernameError.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            lblUsernameError.setVisible(true);
        }
    }
    
    @FXML
    private void handleRegister() {
        // Validasi input
        if (!validateInput()) {
            return;
        }
        
        // Cek username availability
        if (userController.isUsernameExist(txtUsername.getText().trim())) {
            showError("Username sudah digunakan, silakan pilih username lain!");
            return;
        }
        
        // Cek password match
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            showError("Password dan konfirmasi password tidak sama!");
            return;
        }
        
        // Buat user baru
        User newUser = new User();
        newUser.setUsername(txtUsername.getText().trim());
        newUser.setPassword(txtPassword.getText());
        newUser.setNamaLengkap(txtNamaLengkap.getText().trim());
        newUser.setEmail(txtEmail.getText().trim());
        newUser.setNoTelepon(txtTelepon.getText().trim());
        
        // Register user
        if (userController.registerUser(newUser)) {
            showSuccess();
        } else {
            showError("Gagal mendaftar! Silakan coba lagi.");
        }
    }
    
    /**
     * Validasi input form
     */
    private boolean validateInput() {
        // Username
        if (txtUsername.getText().trim().isEmpty()) {
            showError("Username tidak boleh kosong!");
            return false;
        }
        
        if (txtUsername.getText().trim().length() < 4) {
            showError("Username minimal 4 karakter!");
            return false;
        }
        
        // Password
        if (txtPassword.getText().isEmpty()) {
            showError("Password tidak boleh kosong!");
            return false;
        }
        
        if (txtPassword.getText().length() < 6) {
            showError("Password minimal 6 karakter!");
            return false;
        }
        
        // Confirm Password
        if (txtConfirmPassword.getText().isEmpty()) {
            showError("Konfirmasi password tidak boleh kosong!");
            return false;
        }
        
        // Nama Lengkap
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            showError("Nama lengkap tidak boleh kosong!");
            return false;
        }
        
        // Email validation (optional)
        if (!txtEmail.getText().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!txtEmail.getText().trim().matches(emailRegex)) {
                showError("Format email tidak valid!");
                return false;
            }
        }
        
        // Phone validation (optional)
        if (!txtTelepon.getText().trim().isEmpty()) {
            if (!txtTelepon.getText().trim().matches("^[0-9]{10,15}$")) {
                showError("Format nomor telepon tidak valid! (10-15 digit)");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Menampilkan pesan error
     */
    private void showError(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.ERROR,
            "Error Validasi",
            message,
            MainApp.getRootContainer()
        );
    }
    
    /**
     * Menampilkan pesan sukses
     */
    private void showSuccess() {
        StackPane root = MainApp.getRootContainer();
        
        DialogUtil.showDialog(
            DialogUtil.DialogType.SUCCESS,
            "Registrasi Berhasil",
            "Akun berhasil dibuat! Silakan login dengan username dan password Anda.",
            root
        );
        
        // Delay before redirect
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainApp.showUserLogin();
        });
    }
    
    @FXML
    private void handleBackToLogin() {
        MainApp.showUserLogin();
    }
}