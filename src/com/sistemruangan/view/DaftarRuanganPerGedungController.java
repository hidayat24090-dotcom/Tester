package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Gedung;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Controller untuk Daftar Ruangan Per Gedung - Admin
 */
public class DaftarRuanganPerGedungController {
    
    @FXML private Label lblGedungNama;
    @FXML private Label lblGedungInfo;
    @FXML private Label lblTotal;
    @FXML private Label lblTersedia;
    @FXML private Label lblDipinjam;
    
    @FXML private ImageView imgPreview;
    @FXML private Label lblFotoName;
    @FXML private ComboBox<String> cbLantai;
    @FXML private TextField txtNama;
    @FXML private TextField txtKursi;
    @FXML private TextArea txtFasilitas;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbFilterLantai;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private FlowPane flowPaneRuangan;
    
    @FXML private Button btnUploadFoto;
    @FXML private Button btnTambah;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private static Gedung selectedGedung;
    private Ruangan selectedRuangan;
    private String selectedFotoPath = null;
    private FilteredList<Ruangan> filteredData;
    
    private static final String FOTO_DIR = "resources/images/ruangan/";
    
    public static void setSelectedGedung(Gedung gedung) {
        selectedGedung = gedung;
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing DaftarRuanganPerGedungController...");
        
        if (selectedGedung == null) {
            System.err.println("❌ No gedung selected!");
            return;
        }
        
        ruanganController = new RuanganController();
        
        // Set gedung info
        lblGedungNama.setText(selectedGedung.getNamaGedung());
        
        // Setup lantai ComboBox
        for (int i = 1; i <= selectedGedung.getJumlahLantai(); i++) {
            cbLantai.getItems().add(String.valueOf(i));
        }
        
        // Setup filter lantai
        cbFilterLantai.getItems().add("Semua");
        for (int i = 1; i <= selectedGedung.getJumlahLantai(); i++) {
            cbFilterLantai.getItems().add("Lantai " + i);
        }
        cbFilterLantai.setValue("Semua");
        
        // Setup status
        cbStatus.getItems().addAll("tersedia", "dipinjam");
        cbStatus.setValue("tersedia");
        
        cbFilterStatus.getItems().addAll("Semua", "tersedia", "dipinjam");
        cbFilterStatus.setValue("Semua");
        
        // Load default image
        setDefaultImage();
        
        // Load data
        loadData();
        
        System.out.println("✅ Controller initialized for: " + selectedGedung.getNamaGedung());
    }
    
    private void setDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_room.png"));
            imgPreview.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("⚠️ Default image not found");
        }
    }
    
    private void loadData() {
        try {
            ObservableList<Ruangan> allRuangan = ruanganController.getRuanganByGedung(selectedGedung.getId());
            filteredData = new FilteredList<>(allRuangan, p -> true);
            
            lblGedungInfo.setText(selectedGedung.getJumlahLantai() + " Lantai • " + allRuangan.size() + " Ruangan");
            
            displayRuanganCards();
            updateSummary();
            
            System.out.println("✅ Loaded " + allRuangan.size() + " ruangan(s)");
        } catch (Exception e) {
            System.err.println("❌ ERROR loading ruangan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayRuanganCards() {
        flowPaneRuangan.getChildren().clear();
        
        if (filteredData.isEmpty()) {
            VBox noData = new VBox(10);
            noData.setAlignment(Pos.CENTER);
            noData.setPadding(new Insets(40));
            
            Label icon = new Label("📭");
            icon.setStyle("-fx-font-size: 48px;");
            
            Label text = new Label("Belum ada ruangan di gedung ini");
            text.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            noData.getChildren().addAll(icon, text);
            flowPaneRuangan.getChildren().add(noData);
            return;
        }
        
        for (Ruangan ruangan : filteredData) {
            VBox card = createRuanganCard(ruangan);
            flowPaneRuangan.getChildren().add(card);
        }
    }
    
    private VBox createRuanganCard(Ruangan ruangan) {
        VBox card = new VBox(12);
        card.setPrefWidth(280);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
            "-fx-padding: 0;"
        );
        
        // Image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(150);
        imageContainer.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 10 10 0 0;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
        
        loadRuanganImage(ruangan, imageView);
        imageContainer.getChildren().add(imageView);
        
        // Content
        VBox content = new VBox(8);
        content.setPadding(new Insets(12));
        
        Label lblNama = new Label(ruangan.getNamaRuangan());
        lblNama.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNama.setWrapText(true);
        
        HBox infoBox = new HBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLantai = new Label("📍");
        Label lblLantai = new Label("Lantai " + ruangan.getLantai() + " • " + ruangan.getJumlahKursi() + " Kursi");
        lblLantai.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(iconLantai, lblLantai);
        
        Label lblStatus = new Label(ruangan.getStatus().toUpperCase());
        lblStatus.setPadding(new Insets(3, 10, 3, 10));
        lblStatus.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-font-size: 9px;" +
            "-fx-font-weight: bold;" +
            (ruangan.getStatus().equalsIgnoreCase("tersedia") 
                ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" 
                : "-fx-background-color: #fff3cd; -fx-text-fill: #856404;")
        );
        
        Separator sep = new Separator();
        
        Button btnEdit = new Button("✏️ Edit");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.setStyle(
            "-fx-background-color: #5B9BD5;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12;" +
            "-fx-background-radius: 6;"
        );
        btnEdit.setOnAction(e -> selectRuanganForEdit(ruangan));
        
        content.getChildren().addAll(lblNama, infoBox, lblStatus, sep, btnEdit);
        card.getChildren().addAll(imageContainer, content);
        
        return card;
    }
    
    private void loadRuanganImage(Ruangan ruangan, ImageView imageView) {
        try {
            if (ruangan.getFotoPath() != null && !ruangan.getFotoPath().isEmpty()) {
                File fotoFile = new File(FOTO_DIR + ruangan.getFotoPath());
                if (fotoFile.exists()) {
                    imageView.setImage(new Image(fotoFile.toURI().toString()));
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error loading image: " + e.getMessage());
        }
        
        // Default image
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_room.png")));
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void selectRuanganForEdit(Ruangan ruangan) {
        selectedRuangan = ruangan;
        
        cbLantai.setValue(String.valueOf(ruangan.getLantai()));
        txtNama.setText(ruangan.getNamaRuangan());
        txtKursi.setText(String.valueOf(ruangan.getJumlahKursi()));
        txtFasilitas.setText(ruangan.getFasilitas());
        cbStatus.setValue(ruangan.getStatus());
        
        // Load foto
        if (ruangan.getFotoPath() != null && !ruangan.getFotoPath().isEmpty()) {
            try {
                File fotoFile = new File(FOTO_DIR + ruangan.getFotoPath());
                if (fotoFile.exists()) {
                    imgPreview.setImage(new Image(fotoFile.toURI().toString()));
                    lblFotoName.setText(ruangan.getFotoPath());
                    selectedFotoPath = ruangan.getFotoPath();
                }
            } catch (Exception e) {
                setDefaultImage();
            }
        }
        
        txtNama.requestFocus();
    }
    
    private void updateSummary() {
        int total = filteredData.size();
        int tersedia = 0;
        int dipinjam = 0;
        
        for (Ruangan r : filteredData) {
            if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                tersedia++;
            } else {
                dipinjam++;
            }
        }
        
        lblTotal.setText(String.valueOf(total));
        lblTersedia.setText(String.valueOf(tersedia));
        lblDipinjam.setText(String.valueOf(dipinjam));
    }
    
    @FXML
    private void handleUploadFoto() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Foto Ruangan");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            
            File selectedFile = fileChooser.showOpenDialog(btnUploadFoto.getScene().getWindow());
            
            if (selectedFile != null) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String newFileName = "room_" + timestamp + extension;
                
                Path sourcePath = selectedFile.toPath();
                Path destinationPath = Paths.get(FOTO_DIR + newFileName);
                
                // Create directory if not exists
                Files.createDirectories(destinationPath.getParent());
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                
                selectedFotoPath = newFileName;
                imgPreview.setImage(new Image(selectedFile.toURI().toString()));
                lblFotoName.setText(selectedFile.getName());
                
                showInfo("Foto berhasil dipilih!");
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR uploading foto: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal upload foto: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleTambah() {
        try {
            if (!validateInput()) return;
            
            Ruangan ruangan = new Ruangan();
            ruangan.setIdGedung(selectedGedung.getId());
            ruangan.setLantai(Integer.parseInt(cbLantai.getValue()));
            ruangan.setNamaRuangan(txtNama.getText().trim());
            ruangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
            ruangan.setFasilitas(txtFasilitas.getText().trim());
            ruangan.setStatus(cbStatus.getValue());
            ruangan.setFotoPath(selectedFotoPath);
            
            if (ruanganController.tambahRuangan(ruangan)) {
                showSuccess("Ruangan berhasil ditambahkan!");
                loadData();
                clearFields();
            } else {
                showError("Gagal menambahkan ruangan!");
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
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan diupdate dengan klik Edit!");
                return;
            }
            
            if (!validateInput()) return;
            
            selectedRuangan.setLantai(Integer.parseInt(cbLantai.getValue()));
            selectedRuangan.setNamaRuangan(txtNama.getText().trim());
            selectedRuangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
            selectedRuangan.setFasilitas(txtFasilitas.getText().trim());
            selectedRuangan.setStatus(cbStatus.getValue());
            selectedRuangan.setFotoPath(selectedFotoPath);
            
            if (ruanganController.updateRuangan(selectedRuangan)) {
                showSuccess("Ruangan berhasil diupdate!");
                loadData();
                clearFields();
            } else {
                showError("Gagal update ruangan!");
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
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan dihapus dengan klik Edit!");
                return;
            }
            
            DialogUtil.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus ruangan " + selectedRuangan.getNamaRuangan() + "?",
                MainApp.getRootContainer(),
                () -> {
                    if (ruanganController.deleteRuangan(selectedRuangan.getId())) {
                        showSuccess("Ruangan berhasil dihapus!");
                        loadData();
                        clearFields();
                    } else {
                        showError("Gagal menghapus ruangan!");
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
        cbFilterLantai.setValue("Semua");
        cbFilterStatus.setValue("Semua");
        loadData();
    }
    
    @FXML
    private void handleSearch() {
        handleFilter();
    }
    
    @FXML
    private void handleFilter() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String filterLantai = cbFilterLantai.getValue();
        String filterStatus = cbFilterStatus.getValue();
        
        filteredData.setPredicate(ruangan -> {
            boolean matchSearch = keyword.isEmpty() || 
                ruangan.getNamaRuangan().toLowerCase().contains(keyword);
            
            boolean matchLantai = "Semua".equals(filterLantai) || 
                filterLantai.equals("Lantai " + ruangan.getLantai());
            
            boolean matchStatus = "Semua".equals(filterStatus) || 
                ruangan.getStatus().equalsIgnoreCase(filterStatus);
            
            return matchSearch && matchLantai && matchStatus;
        });
        
        displayRuanganCards();
        updateSummary();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDaftarGedung();
    }
    
    private void clearFields() {
        cbLantai.getSelectionModel().clearSelection();
        txtNama.clear();
        txtKursi.clear();
        txtFasilitas.clear();
        cbStatus.setValue("tersedia");
        selectedRuangan = null;
        selectedFotoPath = null;
        setDefaultImage();
        lblFotoName.setText("");
    }
    
    private boolean validateInput() {
        if (cbLantai.getValue() == null) {
            showWarning("Pilih lantai!");
            return false;
        }
        
        if (txtNama.getText().trim().isEmpty()) {
            showWarning("Nama ruangan tidak boleh kosong!");
            return false;
        }
        
        try {
            int kursi = Integer.parseInt(txtKursi.getText().trim());
            if (kursi <= 0) {
                showWarning("Jumlah kursi harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Jumlah kursi harus berupa angka!");
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
    
    private void showInfo(String message) {
        DialogUtil.showDialog(DialogUtil.DialogType.INFO, "Informasi", message, MainApp.getRootContainer());
    }
}