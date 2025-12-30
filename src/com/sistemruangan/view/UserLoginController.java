package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.UserController;
import com.sistemruangan.model.User;
import com.sistemruangan.util.SessionManager;
import com.sistemruangan.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Controller untuk halaman Login User
 */
public class UserLoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button adminButton;
    @FXML private Label errorLabel;
    
    private UserController userController;
    
    @FXML
    public void initialize() {
        userController = new UserController();
        
        // Add hover effects
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        
        loginButton.setOnMouseEntered(e -> loginButton.setEffect(shadow));
        loginButton.setOnMouseExited(e -> loginButton.setEffect(null));
        
        adminButton.setOnMouseEntered(e -> adminButton.setEffect(shadow));
        adminButton.setOnMouseExited(e -> adminButton.setEffect(null));
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong!");
            return;
        }
        
        User user = userController.validateLogin(username, password);
        
        if (user != null) {
            errorLabel.setVisible(false);
            SessionManager.setCurrentUser(user);
            MainApp.showUserDashboard();
        } else {
            showError("Username atau password salah!");
        }
    }
    
    @FXML
    private void handleRegister() {
        MainApp.showUserRegister();
    }
    
    @FXML
    private void handleAdminLogin() {
        MainApp.showLoginScene();
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