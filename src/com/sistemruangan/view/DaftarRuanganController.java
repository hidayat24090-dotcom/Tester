package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.GedungController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Gedung;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DialogUtil;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Controller untuk halaman Daftar Ruangan - FIXED WITH GEDUNG & LANTAI
 */
public class DaftarRuanganController {
    
    @FXML private TableView<Ruangan> tableRuangan;
    @FXML private TableColumn<Ruangan, Integer> colId;
    @FXML private TableColumn<Ruangan, String> colGedung;
    @FXML private TableColumn<Ruangan, Integer> colLantai;
    @FXML private TableColumn<Ruangan, String> colNama;
    @FXML private TableColumn<Ruangan, Integer> colKursi;
    @FXML private TableColumn<Ruangan, String> colFasilitas;
    @FXML private TableColumn<Ruangan, String> colStatus;
    
    @FXML private ComboBox<Gedung> cbGedung;
    @FXML private ComboBox<String> cbLantai;
    @FXML private TextField txtNama;
    @FXML private TextField txtKursi;
    @FXML private TextArea txtFasilitas; 
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtSearch;
    
    @FXML private ImageView imgPreview;
    @FXML private Button btnUploadFoto;
    @FXML private Label lblFotoName;
    
    @FXML private Button btnTambah;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private GedungController gedungController;
    private Ruangan selectedRuangan;
    private String selectedFotoPath = null;
    
    // Folder untuk menyimpan foto
    private static final String FOTO_DIR = "resources/images/ruangan/";
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing DaftarRuanganController (FIXED)...");
        
        try {
            ruanganController = new RuanganController();
            gedungController = new GedungController();
            
            // Create foto directory if not exists
            createFotoDirectory();
            
            // Setup table columns
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colGedung.setCellValueFactory(new PropertyValueFactory<>("namaGedung"));
            colLantai.setCellValueFactory(new PropertyValueFactory<>("lantai"));
            colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
            colKursi.setCellValueFactory(new PropertyValueFactory<>("jumlahKursi"));
            colFasilitas.setCellValueFactory(new PropertyValueFactory<>("fasilitas"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            colNama.setStyle("-fx-alignment: CENTER-LEFT;");
            colFasilitas.setStyle("-fx-alignment: CENTER-LEFT;");
            colGedung.setStyle("-fx-alignment: CENTER-LEFT;");
            
            // Setup ComboBox Status
            cbStatus.getItems().addAll("tersedia", "dipinjam");
            cbStatus.setValue("tersedia");
            
            // Load Gedung list
            loadGedungList();
            
            // Setup default image
            setDefaultImage();
            
            // Load data
            loadData();
            
            // Setup table selection
            tableRuangan.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newSelection) -> {
                    if (newSelection != null) {
                        selectedRuangan = newSelection;
                        populateFields(newSelection);
                    }
                }
            );
            
            // Setup button effects
            setupButtonEffect(btnUploadFoto);
            setupButtonEffect(btnTambah);
            setupButtonEffect(btnUpdate);
            setupButtonEffect(btnHapus);
            setupButtonEffect(btnClear);
            setupButtonEffect(btnRefresh);
            setupButtonEffect(btnKembali);
            
            System.out.println("✅ DaftarRuanganController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in initialize(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal menginisialisasi halaman: " + e.getMessage());
        }
    }
    
    /**
     * Load daftar gedung
     */
    private void loadGedungList() {
        try {
            ObservableList<Gedung> gedungList = gedungController.getAllGedung();
            cbGedung.setItems(gedungList);
            System.out.println("✅ Loaded " + gedungList.size() + " gedung(s)");
        } catch (Exception e) {
            System.err.println("❌ ERROR loading gedung list: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle perubahan gedung - Update lantai options
     */
    @FXML
    private void handleGedungChange() {
        Gedung selectedGedung = cbGedung.getValue();
        
        if (selectedGedung != null) {
            cbLantai.getItems().clear();
            
            for (int i = 1; i <= selectedGedung.getJumlahLantai(); i++) {
                cbLantai.getItems().add(String.valueOf(i));
            }
            
            cbLantai.setDisable(false);
            cbLantai.setValue("1"); // Default lantai 1
            
            System.out.println("✅ Loaded " + selectedGedung.getJumlahLantai() + " lantai(s) for " + selectedGedung.getNamaGedung());
        } else {
            cbLantai.getItems().clear();
            cbLantai.setDisable(true);
        }
    }
    
    /**
     * Create directory untuk menyimpan foto
     */
    private void createFotoDirectory() {
        try {
            Path path = Paths.get(FOTO_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("📁 Created foto directory: " + FOTO_DIR);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to create foto directory: " + e.getMessage());
        }
    }
    
    /**
     * Set default image
     */
    private void setDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_room.png"));
            imgPreview.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("⚠️ Default image not found, using placeholder");
        }
    }
    
    /**
     * Handle upload foto
     */
    @FXML
    private void handleUploadFoto() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Foto Ruangan");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            
            File selectedFile = fileChooser.showOpenDialog(btnUploadFoto.getScene().getWindow());
            
            if (selectedFile != null) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String extension = getFileExtension(selectedFile.getName());
                String newFileName = "room_" + timestamp + extension;
                
                Path sourcePath = selectedFile.toPath();
                Path destinationPath = Paths.get(FOTO_DIR + newFileName);
                
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                
                selectedFotoPath = newFileName;
                
                Image image = new Image(selectedFile.toURI().toString());
                imgPreview.setImage(image);
                
                lblFotoName.setText(selectedFile.getName());
                
                System.out.println("✅ Foto uploaded: " + newFileName);
                showInfo("Foto berhasil dipilih!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR uploading foto: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal upload foto: " + e.getMessage());
        }
    }
    
    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf);
    }
    
    /**
     * Load data ruangan ke table
     */
    private void loadData() {
        try {
            System.out.println("📋 Fetching ruangan data from database...");
            ObservableList<Ruangan> ruanganList = ruanganController.getAllRuangan();
            
            if (ruanganList == null) {
                System.err.println("⚠️ Ruangan list is NULL!");
                showError("Gagal memuat data: Tidak ada data yang dikembalikan dari database");
                return;
            }
            
            System.out.println("✅ Loaded " + ruanganList.size() + " ruangan(s)");
            
            tableRuangan.setItems(ruanganList);
            
            if (ruanganList.isEmpty()) {
                System.out.println("⚠️ No ruangan data found in database");
                showInfo("Belum ada data ruangan. Silakan tambah ruangan baru.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading data: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat data ruangan:\n" + e.getMessage());
        }
    }
    
    /**
     * Populate fields dengan data ruangan yang dipilih
     */
    private void populateFields(Ruangan ruangan) {
        try {
            // Find and select gedung
            for (Gedung g : cbGedung.getItems()) {
                if (g.getId() == ruangan.getIdGedung()) {
                    cbGedung.setValue(g);
                    break;
                }
            }
            
            // Set lantai
            cbLantai.setValue(String.valueOf(ruangan.getLantai()));
            
            txtNama.setText(ruangan.getNamaRuangan());
            txtKursi.setText(String.valueOf(ruangan.getJumlahKursi()));
            txtFasilitas.setText(ruangan.getFasilitas());
            cbStatus.setValue(ruangan.getStatus());
            
            // Load foto jika ada
            if (ruangan.getFotoPath() != null && !ruangan.getFotoPath().isEmpty()) {
                try {
                    File fotoFile = new File(FOTO_DIR + ruangan.getFotoPath());
                    if (fotoFile.exists()) {
                        Image image = new Image(fotoFile.toURI().toString());
                        imgPreview.setImage(image);
                        lblFotoName.setText(ruangan.getFotoPath());
                        selectedFotoPath = ruangan.getFotoPath();
                    } else {
                        setDefaultImage();
                        lblFotoName.setText("Foto tidak ditemukan");
                    }
                } catch (Exception e) {
                    setDefaultImage();
                    System.err.println("⚠️ Error loading foto: " + e.getMessage());
                }
            } else {
                setDefaultImage();
                lblFotoName.setText("Tidak ada foto");
            }
            
            System.out.println("✅ Fields populated for: " + ruangan.getNamaRuangan());
        } catch (Exception e) {
            System.err.println("❌ ERROR populating fields: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        cbGedung.getSelectionModel().clearSelection();
        cbLantai.getItems().clear();
        cbLantai.setDisable(true);
        txtNama.clear();
        txtKursi.clear();
        txtFasilitas.clear();
        cbStatus.setValue("tersedia");
        selectedRuangan = null;
        selectedFotoPath = null;
        setDefaultImage();
        lblFotoName.setText("");
        tableRuangan.getSelectionModel().clearSelection();
        System.out.println("🔄 Fields cleared");
    }
    
    @FXML
    private void handleTambah() {
        try {
            System.out.println("➕ Adding new ruangan...");
            
            if (!validateInput()) {
                System.out.println("❌ Validation failed");
                return;
            }
            
            Gedung selectedGedung = cbGedung.getValue();
            int lantai = Integer.parseInt(cbLantai.getValue());
            
            Ruangan ruangan = new Ruangan();
            ruangan.setIdGedung(selectedGedung.getId());
            ruangan.setNamaRuangan(txtNama.getText().trim());
            ruangan.setLantai(lantai);
            ruangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
            ruangan.setFasilitas(txtFasilitas.getText().trim());
            ruangan.setStatus(cbStatus.getValue());
            ruangan.setFotoPath(selectedFotoPath);
            
            if (ruanganController.tambahRuangan(ruangan)) {
                showSuccess("Ruangan berhasil ditambahkan!");
                System.out.println("✅ Ruangan added successfully");
                loadData();
                clearFields();
            } else {
                showError("Gagal menambahkan ruangan!");
                System.err.println("❌ Failed to add ruangan");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleTambah(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        try {
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan diupdate!");
                return;
            }
            
            if (!validateInput()) return;
            
            Gedung selectedGedung = cbGedung.getValue();
            int lantai = Integer.parseInt(cbLantai.getValue());
            
            selectedRuangan.setIdGedung(selectedGedung.getId());
            selectedRuangan.setNamaRuangan(txtNama.getText().trim());
            selectedRuangan.setLantai(lantai);
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
            System.err.println("❌ ERROR in handleUpdate(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleHapus() {
        try {
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan dihapus!");
                return;
            }
            
            StackPane root = MainApp.getRootContainer();
            
            DialogUtil.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus ruangan " + selectedRuangan.getNamaRuangan() + "?",
                root,
                () -> {
                    // On Confirm - Delete
                    // Delete foto file if exists
                    if (selectedRuangan.getFotoPath() != null) {
                        try {
                            Files.deleteIfExists(Paths.get(FOTO_DIR + selectedRuangan.getFotoPath()));
                        } catch (IOException e) {
                            System.err.println("⚠️ Failed to delete foto file: " + e.getMessage());
                        }
                    }
                    
                    if (ruanganController.deleteRuangan(selectedRuangan.getId())) {
                        showSuccess("Ruangan berhasil dihapus!");
                        loadData();
                        clearFields();
                    } else {
                        showError("Gagal menghapus ruangan!");
                    }
                },
                () -> {
                    System.out.println("Delete cancelled");
                }
            );
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleHapus(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    @FXML
    private void handleRefresh() {
        try {
            System.out.println("🔄 Refreshing data...");
            loadData();
            loadGedungList();
            txtSearch.clear();
            showInfo("Data berhasil di-refresh!");
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleRefresh(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal refresh data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            
            if (keyword.isEmpty()) {
                loadData();
            } else {
                System.out.println("🔍 Searching for: " + keyword);
                ObservableList<Ruangan> searchResult = ruanganController.searchRuangan(keyword);
                tableRuangan.setItems(searchResult);
                System.out.println("✅ Found " + searchResult.size() + " result(s)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleSearch(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal mencari data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleKembali() {
        try {
            System.out.println("⬅️ Navigating back to dashboard...");
            MainApp.showDashboard();
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleKembali(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal kembali ke dashboard: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        // Gedung
        if (cbGedung.getValue() == null) {
            showWarning("Pilih gedung terlebih dahulu!");
            return false;
        }
        
        // Lantai
        if (cbLantai.getValue() == null || cbLantai.getValue().isEmpty()) {
            showWarning("Pilih lantai terlebih dahulu!");
            return false;
        }
        
        // Nama
        if (txtNama.getText().trim().isEmpty()) {
            showWarning("Nama ruangan tidak boleh kosong!");
            return false;
        }
        
        // Kursi
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
        DialogUtil.showDialog(
            DialogUtil.DialogType.SUCCESS,
            "Sukses",
            message,
            MainApp.getRootContainer()
        );
    }

    private void showError(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.ERROR,
            "Error",
            message,
            MainApp.getRootContainer()
        );
    }

    private void showWarning(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.WARNING,
            "Peringatan",
            message,
            MainApp.getRootContainer()
        );
    }

    private void showInfo(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.INFO,
            "Informasi",
            message,
            MainApp.getRootContainer()
        );
    }
    
    private void setupButtonEffect(Button button) {
        try {
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.web("#5B9BD5"));
            
            button.setOnMouseEntered(e -> button.setEffect(shadow));
            button.setOnMouseExited(e -> button.setEffect(null));
        } catch (Exception e) {
            // Ignore styling errors
        }
    }
}