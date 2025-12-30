package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.GedungController;
import com.sistemruangan.model.Gedung;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller untuk Kelola Gedung - Admin
 */
public class DaftarGedungController {
    
    @FXML private TextField txtNamaGedung;
    @FXML private TextField txtJumlahLantai;
    @FXML private TextField txtSearch;
    @FXML private FlowPane flowPaneGedung;
    
    @FXML private Button btnTambah;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private GedungController gedungController;
    private FilteredList<Gedung> filteredData;
    private Gedung selectedGedung;
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing DaftarGedungController...");
        
        gedungController = new GedungController();
        
        // Setup FlowPane
        flowPaneGedung.setHgap(15);
        flowPaneGedung.setVgap(15);
        
        // Load data
        loadData();
    }
    
    /**
     * Load data gedung
     */
    private void loadData() {
        try {
            ObservableList<Gedung> gedungList = gedungController.getAllGedung();
            filteredData = new FilteredList<>(gedungList, p -> true);
            
            displayGedungCards();
            
            System.out.println("✅ Loaded " + gedungList.size() + " gedung(s)");
        } catch (Exception e) {
            System.err.println("❌ ERROR loading gedung: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Display gedung as cards
     */
    private void displayGedungCards() {
        flowPaneGedung.getChildren().clear();
        
        if (filteredData.isEmpty()) {
            Label noData = new Label("Belum ada gedung. Silakan tambahkan gedung baru.");
            noData.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            flowPaneGedung.getChildren().add(noData);
            return;
        }
        
        for (Gedung gedung : filteredData) {
            VBox card = createGedungCard(gedung);
            flowPaneGedung.getChildren().add(card);
        }
    }
    
    /**
     * Create gedung card
     */
    private VBox createGedungCard(Gedung gedung) {
        VBox card = new VBox(15);
        card.setPrefWidth(280);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
            "-fx-padding: 20;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(91,155,213,0.3), 15, 0, 0, 5);" +
                "-fx-padding: 20;" +
                "-fx-cursor: hand;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
                "-fx-padding: 20;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Icon
        Label iconLabel = new Label("🏢");
        iconLabel.setStyle("-fx-font-size: 48px;");
        iconLabel.setAlignment(Pos.CENTER);
        
        // Nama Gedung
        Label lblNama = new Label(gedung.getNamaGedung());
        lblNama.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblNama.setStyle("-fx-text-fill: #2c3e50;");
        lblNama.setWrapText(true);
        lblNama.setMaxWidth(240);
        
        // Info Lantai
        HBox infoBox = new HBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLantai = new Label("📊");
        Label lblLantai = new Label(gedung.getJumlahLantai() + " Lantai");
        lblLantai.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        infoBox.getChildren().addAll(iconLantai, lblLantai);
        
        Separator sep = new Separator();
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button btnLihatRuangan = new Button("📋 Kelola Ruangan");
        btnLihatRuangan.setMaxWidth(Double.MAX_VALUE);
        btnLihatRuangan.setStyle(
            "-fx-background-color: #5B9BD5;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8 15;" +
            "-fx-background-radius: 6;"
        );
        HBox.setHgrow(btnLihatRuangan, Priority.ALWAYS);
        
        Button btnEdit = new Button("✏️");
        btnEdit.setStyle(
            "-fx-background-color: #70C1B3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 6;"
        );
        
        btnLihatRuangan.setOnAction(e -> handleLihatRuangan(gedung));
        btnEdit.setOnAction(e -> selectGedungForEdit(gedung));
        
        buttonBox.getChildren().addAll(btnLihatRuangan, btnEdit);
        
        card.getChildren().addAll(iconLabel, lblNama, infoBox, sep, buttonBox);
        
        return card;
    }
    
    /**
     * Handle lihat ruangan per gedung
     */
    private void handleLihatRuangan(Gedung gedung) {
        System.out.println("📋 Opening ruangan for: " + gedung.getNamaGedung());
        DaftarRuanganPerGedungController.setSelectedGedung(gedung);
        MainApp.showRuanganPerGedung();
    }
    
    /**
     * Select gedung for edit
     */
    private void selectGedungForEdit(Gedung gedung) {
        selectedGedung = gedung;
        txtNamaGedung.setText(gedung.getNamaGedung());
        txtJumlahLantai.setText(String.valueOf(gedung.getJumlahLantai()));
        
        // Scroll to form
        txtNamaGedung.requestFocus();
    }
    
    @FXML
    private void handleTambah() {
        try {
            if (!validateInput()) return;
            
            Gedung gedung = new Gedung();
            gedung.setNamaGedung(txtNamaGedung.getText().trim());
            gedung.setJumlahLantai(Integer.parseInt(txtJumlahLantai.getText().trim()));
            
            if (gedungController.tambahGedung(gedung)) {
                showSuccess("Gedung berhasil ditambahkan!");
                loadData();
                clearFields();
            } else {
                showError("Gagal menambahkan gedung!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        try {
            if (selectedGedung == null) {
                showWarning("Pilih gedung yang akan diupdate dengan klik tombol Edit!");
                return;
            }
            
            if (!validateInput()) return;
            
            selectedGedung.setNamaGedung(txtNamaGedung.getText().trim());
            selectedGedung.setJumlahLantai(Integer.parseInt(txtJumlahLantai.getText().trim()));
            
            if (gedungController.updateGedung(selectedGedung)) {
                showSuccess("Gedung berhasil diupdate!");
                loadData();
                clearFields();
            } else {
                showError("Gagal update gedung!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleHapus() {
        try {
            if (selectedGedung == null) {
                showWarning("Pilih gedung yang akan dihapus dengan klik tombol Edit!");
                return;
            }
            
            DialogUtil.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus gedung " + selectedGedung.getNamaGedung() + "?\n\n" +
                "⚠️ Semua ruangan di gedung ini juga akan terhapus!",
                MainApp.getRootContainer(),
                () -> {
                    if (gedungController.deleteGedung(selectedGedung.getId())) {
                        showSuccess("Gedung berhasil dihapus!");
                        loadData();
                        clearFields();
                    } else {
                        showError("Gagal menghapus gedung!");
                    }
                },
                null
            );
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadData();
    }
    
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        filteredData.setPredicate(gedung -> {
            if (keyword.isEmpty()) return true;
            return gedung.getNamaGedung().toLowerCase().contains(keyword);
        });
        
        displayGedungCards();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDashboard();
    }
    
    private void clearFields() {
        txtNamaGedung.clear();
        txtJumlahLantai.clear();
        selectedGedung = null;
    }
    
    private boolean validateInput() {
        if (txtNamaGedung.getText().trim().isEmpty()) {
            showWarning("Nama gedung tidak boleh kosong!");
            return false;
        }
        
        try {
            int lantai = Integer.parseInt(txtJumlahLantai.getText().trim());
            if (lantai <= 0) {
                showWarning("Jumlah lantai harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Jumlah lantai harus berupa angka!");
            return false;
        }
        
        return true;
    }
    
    private void showSuccess(String message) {
        DialogUtil.showDialog(DialogUtil.DialogType.SUCCESS, "Berhasil", message, MainApp.getRootContainer());
    }
    
    private void showError(String message) {
        DialogUtil.showDialog(DialogUtil.DialogType.ERROR, "Error", message, MainApp.getRootContainer());
    }
    
    private void showWarning(String message) {
        DialogUtil.showDialog(DialogUtil.DialogType.WARNING, "Peringatan", message, MainApp.getRootContainer());
    }
}