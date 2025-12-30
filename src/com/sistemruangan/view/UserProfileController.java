package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.UserController;
import com.sistemruangan.model.User;
import com.sistemruangan.util.SessionManager;
import com.sistemruangan.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Controller untuk halaman Profile User
 */
public class UserProfileController {
    
    @FXML private TextField txtUsername;
    @FXML private TextField txtNamaLengkap;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelepon;
    @FXML private PasswordField txtOldPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    
    @FXML private Button btnEdit;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnChangePassword;
    @FXML private Button btnKembali;
    
    @FXML private HBox hboxEditButtons;
    @FXML private Label lblSuccess;
    @FXML private Label lblPasswordError;
    @FXML private Label lblPasswordSuccess;
    
    private UserController userController;
    private User currentUser;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        userController = new UserController();
        
        // Load user data
        loadUserData();
    }
    
    /**
     * Load data user dari session
     */
    private void loadUserData() {
        if (SessionManager.isLoggedIn()) {
            int userId = SessionManager.getUserId();
            currentUser = userController.getUserById(userId);
            
            if (currentUser != null) {
                txtUsername.setText(currentUser.getUsername());
                txtNamaLengkap.setText(currentUser.getNamaLengkap());
                txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
                txtTelepon.setText(currentUser.getNoTelepon() != null ? currentUser.getNoTelepon() : "");
            }
        }
    }
    
    @FXML
    private void handleEdit() {
        isEditMode = true;
        
        // Enable fields
        txtNamaLengkap.setDisable(false);
        txtEmail.setDisable(false);
        txtTelepon.setDisable(false);
        
        // Show/hide buttons
        btnEdit.setVisible(false);
        hboxEditButtons.setVisible(true);
        lblSuccess.setVisible(false);
    }
    
    @FXML
    private void handleSave() {
        if (!validateProfileInput()) {
            return;
        }
        
        // Update user data
        currentUser.setNamaLengkap(txtNamaLengkap.getText().trim());
        currentUser.setEmail(txtEmail.getText().trim());
        currentUser.setNoTelepon(txtTelepon.getText().trim());
        
        if (userController.updateProfile(currentUser)) {
            // Update session
            SessionManager.setCurrentUser(currentUser);
            
            // Show success message
            lblSuccess.setVisible(true);
            
            // Reset to view mode
            cancelEditMode();
            
            // Hide success message after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> lblSuccess.setVisible(false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengupdate profile!");
        }
    }
    
    @FXML
    private void handleCancel() {
        // Reload original data
        loadUserData();
        cancelEditMode();
        lblSuccess.setVisible(false);
    }
    
    /**
     * Cancel edit mode
     */
    private void cancelEditMode() {
        isEditMode = false;
        
        // Disable fields
        txtNamaLengkap.setDisable(true);
        txtEmail.setDisable(true);
        txtTelepon.setDisable(true);
        
        // Show/hide buttons
        btnEdit.setVisible(true);
        hboxEditButtons.setVisible(false);
    }
    
    @FXML
    private void handleChangePassword() {
        lblPasswordError.setVisible(false);
        lblPasswordSuccess.setVisible(false);
        
        if (!validatePasswordInput()) {
            return;
        }
        
        // Change password
        if (userController.changePassword(
                SessionManager.getUserId(),
                txtOldPassword.getText(),
                txtNewPassword.getText())) {
            
            // Show success message
            lblPasswordSuccess.setVisible(true);
            
            // Clear password fields
            txtOldPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
            
            // Hide success message after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> lblPasswordSuccess.setVisible(false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showPasswordError("Password lama tidak sesuai!");
        }
    }
    
    /**
     * Validasi input profile
     */
    private boolean validateProfileInput() {
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama lengkap tidak boleh kosong!");
            return false;
        }
        
        // Email validation (optional)
        if (!txtEmail.getText().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!txtEmail.getText().trim().matches(emailRegex)) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Format email tidak valid!");
                return false;
            }
        }
        
        // Phone validation (optional)
        if (!txtTelepon.getText().trim().isEmpty()) {
            if (!txtTelepon.getText().trim().matches("^[0-9]{10,15}$")) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", 
                         "Format nomor telepon tidak valid! (10-15 digit)");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validasi input password
     */
    private boolean validatePasswordInput() {
        // Old password
        if (txtOldPassword.getText().isEmpty()) {
            showPasswordError("Password lama tidak boleh kosong!");
            return false;
        }
        
        // New password
        if (txtNewPassword.getText().isEmpty()) {
            showPasswordError("Password baru tidak boleh kosong!");
            return false;
        }
        
        if (txtNewPassword.getText().length() < 6) {
            showPasswordError("Password baru minimal 6 karakter!");
            return false;
        }
        
        // Confirm password
        if (txtConfirmPassword.getText().isEmpty()) {
            showPasswordError("Konfirmasi password tidak boleh kosong!");
            return false;
        }
        
        // Match check
        if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
            showPasswordError("Password baru dan konfirmasi password tidak sama!");
            return false;
        }
        
        // Check if new password same as old
        if (txtOldPassword.getText().equals(txtNewPassword.getText())) {
            showPasswordError("Password baru harus berbeda dengan password lama!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Show password error
     */
    private void showPasswordError(String message) {
        lblPasswordError.setText(message);
        lblPasswordError.setVisible(true);
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        StackPane root = MainApp.getRootContainer();
        
        DialogUtil.DialogType dialogType;
        switch (type) {
            case INFORMATION:
                dialogType = DialogUtil.DialogType.SUCCESS;
                break;
            case WARNING:
                dialogType = DialogUtil.DialogType.WARNING;
                break;
            case ERROR:
                dialogType = DialogUtil.DialogType.ERROR;
                break;
            default:
                dialogType = DialogUtil.DialogType.INFO;
        }
        
        DialogUtil.showDialog(dialogType, title, message, root);
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}