package com.sistemruangan.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Custom Dialog Utility with Overlay Effect
 * NO WINDOW SWITCHING - Pure overlay on current window!
 */
public class DialogUtil {
    
    public enum DialogType {
        SUCCESS, ERROR, WARNING, INFO, CONFIRMATION
    }
    
    /**
     * Show simple dialog (auto-close capable)
     */
    public static void showDialog(DialogType type, String title, String message, StackPane rootContainer) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL! Cannot show dialog.");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createDialogBox(type, title, message, overlay, rootContainer, null);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    /**
     * Show confirmation dialog with callbacks
     */
    public static void showConfirmation(String title, String message, StackPane rootContainer, 
                                       Runnable onConfirm, Runnable onCancel) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL!");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createConfirmationBox(title, message, overlay, rootContainer, onConfirm, onCancel);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    /**
     * Show input dialog with TextArea
     */
    public static void showInputDialog(String title, String message, String placeholder, 
                                      StackPane rootContainer, InputCallback callback) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL!");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createInputBox(title, message, placeholder, overlay, rootContainer, callback);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    // ========== PRIVATE METHODS ==========
    
    private static StackPane createOverlay() {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("custom-dialog-overlay");
        overlay.setAlignment(Pos.CENTER);
        
        // Click outside to close
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                // Clicked on overlay background
                // Don't close automatically - user must click button
            }
        });
        
        return overlay;
    }
    
    private static VBox createDialogBox(DialogType type, String title, String message, 
                                       StackPane overlay, StackPane rootContainer, Runnable callback) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(380);
        dialog.setMaxHeight(Region.USE_PREF_SIZE); // Prevent vertical stretching
        dialog.getStyleClass().add("custom-dialog-box");
        
        // Header
        HBox header = new HBox(15);
        header.getStyleClass().add("custom-dialog-header");
        header.getStyleClass().add("header-" + type.toString().toLowerCase());
        
        Label iconLabel = new Label(getIcon(type));
        iconLabel.getStyleClass().add("custom-dialog-icon");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("custom-dialog-title");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.getStyleClass().add("custom-dialog-content");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("custom-dialog-message");
        messageLabel.setMaxWidth(420);
        
        content.getChildren().add(messageLabel);
        
        // Button
        Button btnOk = new Button("OK");
        btnOk.setPrefWidth(120);
        btnOk.getStyleClass().add("primary-button"); // Reusing existing button class
        // Add specific color class based on type if needed, or just let it be primary blue
        if (type == DialogType.ERROR || type == DialogType.WARNING) {
             btnOk.getStyleClass().add("logout-button"); // Use reddish/warning style if error
        }
        
        // Custom button styling override for dialog consistency
        btnOk.setStyle("-fx-font-size: 14px; -fx-padding: 10 25;");
        
        btnOk.setOnAction(e -> {
            if (callback != null) callback.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(btnOk);
        buttonBox.getStyleClass().add("custom-dialog-button-bar");
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        return dialog;
    }
    
    private static VBox createConfirmationBox(String title, String message, 
                                             StackPane overlay, StackPane rootContainer,
                                             Runnable onConfirm, Runnable onCancel) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(400);
        dialog.setMaxHeight(Region.USE_PREF_SIZE); // Prevent vertical stretching
        dialog.getStyleClass().add("custom-dialog-box");
        
        // Header
        HBox header = new HBox(15);
        header.getStyleClass().add("custom-dialog-header");
        header.getStyleClass().add("header-confirmation");
        
        Label iconLabel = new Label("⚠️");
        iconLabel.getStyleClass().add("custom-dialog-icon");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("custom-dialog-title");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.getStyleClass().add("custom-dialog-content");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("custom-dialog-message");
        messageLabel.setMaxWidth(440);
        
        content.getChildren().add(messageLabel);
        
        // Buttons
        Button btnConfirm = new Button("Ya, Lanjutkan");
        btnConfirm.setPrefWidth(150);
        btnConfirm.getStyleClass().add("success-button");
        btnConfirm.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button btnCancel = new Button("Batal");
        btnCancel.setPrefWidth(120);
        btnCancel.getStyleClass().add("danger-button");
        btnCancel.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        
        btnConfirm.setOnAction(e -> {
            if (onConfirm != null) onConfirm.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        btnCancel.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(15, btnConfirm, btnCancel);
        buttonBox.getStyleClass().add("custom-dialog-button-bar");
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        return dialog;
    }
    
    private static VBox createInputBox(String title, String message, String placeholder,
                                      StackPane overlay, StackPane rootContainer,
                                      InputCallback callback) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(420);
        dialog.setMaxHeight(Region.USE_PREF_SIZE); // Prevent vertical stretching
        dialog.getStyleClass().add("custom-dialog-box");
        
        // Header
        HBox header = new HBox(15);
        header.getStyleClass().add("custom-dialog-header");
        header.getStyleClass().add("header-info");
        
        Label iconLabel = new Label("✏️");
        iconLabel.getStyleClass().add("custom-dialog-icon");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("custom-dialog-title");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.getStyleClass().add("custom-dialog-content");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("custom-dialog-message");
        messageLabel.setMaxWidth(490);
        
        TextArea textArea = new TextArea();
        textArea.setPromptText(placeholder);
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);
        textArea.getStyleClass().add("text-field"); // Use existing text-field style which looks good
        textArea.setStyle("-fx-font-size: 14px;");
        
        content.getChildren().addAll(messageLabel, textArea);
        
        // Buttons
        Button btnSubmit = new Button("Kirim");
        btnSubmit.setPrefWidth(130);
        btnSubmit.getStyleClass().add("primary-button");
        btnSubmit.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button btnCancel = new Button("Batal");
        btnCancel.setPrefWidth(120);
        btnCancel.getStyleClass().add("secondary-button");
        btnCancel.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        
        btnSubmit.setOnAction(e -> {
            String input = textArea.getText().trim();
            if (!input.isEmpty() && callback != null) {
                callback.onInput(input);
            }
            closeDialog(overlay, rootContainer, dialog);
        });
        
        btnCancel.setOnAction(e -> {
            if (callback != null) callback.onCancel();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(15, btnSubmit, btnCancel);
        buttonBox.getStyleClass().add("custom-dialog-button-bar");
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        // Auto-focus on TextArea
        javafx.application.Platform.runLater(() -> textArea.requestFocus());
        
        return dialog;
    }
    
    private static void animateIn(StackPane overlay, VBox dialog) {
        overlay.setOpacity(0);
        dialog.setScaleX(0.7);
        dialog.setScaleY(0.7);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), dialog);
        scaleIn.setFromX(0.7);
        scaleIn.setFromY(0.7);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        fadeIn.play();
        scaleIn.play();
    }
    
    private static void closeDialog(StackPane overlay, StackPane rootContainer, VBox dialog) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), dialog);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.7);
        scaleOut.setToY(0.7);
        
        fadeOut.setOnFinished(e -> rootContainer.getChildren().remove(overlay));
        
        fadeOut.play();
        scaleOut.play();
    }
    
    private static String getIcon(DialogType type) {
        switch (type) {
            case SUCCESS: return "✅";
            case ERROR: return "❌";
            case WARNING: return "⚠️";
            case INFO: return "ℹ️";
            case CONFIRMATION: return "❓";
            default: return "ℹ️";
        }
    }
    
    // getHeaderColor no longer needed as we use CSS classes
    
    /**
     * Callback interface for input dialog
     */
    public interface InputCallback {
        void onInput(String input);
        default void onCancel() {}
    }
}