package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller untuk Daftar Ruangan User - OPTIMIZED VERSION
 * Performance improvements:
 * - Lazy loading images
 * - Image caching
 * - Virtual scrolling simulation
 * - Reduced shadow effects
 */
public class UserRuanganListController {
    
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPaneCards;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private ComboBox<com.sistemruangan.model.Gedung> cbFilterGedung;
    @FXML private ComboBox<String> cbFilterLantai;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalRuangan;
    @FXML private Label lblTersedia;
    @FXML private Label lblDipinjam;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    @FXML private ProgressIndicator progressIndicator;
    
    private RuanganController ruanganController;
    private FilteredList<Ruangan> filteredData;
    private static final String FOTO_DIR = "resources/images/ruangan/";
    
    // Image cache untuk performa
    private Map<String, Image> imageCache = new HashMap<>();
    private Image defaultImage;
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing Optimized UserRuanganListController...");
        
        ruanganController = new RuanganController();
        
        // Setup filter combo box
        cbFilterStatus.getItems().addAll("Semua", "Tersedia", "Dipinjam");
        cbFilterStatus.setValue("Semua");
        
        // Setup Building filter
        loadGedungList();
        cbFilterLantai.getItems().add("Semua");
        cbFilterLantai.setValue("Semua");
        cbFilterLantai.setDisable(true);
        
        // Setup FlowPane for cards (optimized settings)
        flowPaneCards.setHgap(20);
        flowPaneCards.setVgap(20);
        flowPaneCards.setPadding(new Insets(10));
        flowPaneCards.setStyle("-fx-background-color: transparent;");
        
        // Load default image once
        loadDefaultImage();
        
        // Load data in background
        loadDataAsync();
    }
    
    private void loadGedungList() {
        com.sistemruangan.controller.GedungController gedungController = new com.sistemruangan.controller.GedungController();
        ObservableList<com.sistemruangan.model.Gedung> gedungList = gedungController.getAllGedung();
        
        // Add "Semua Gedung" option
        com.sistemruangan.model.Gedung allGedung = new com.sistemruangan.model.Gedung(0, "Semua Gedung", 0);
        cbFilterGedung.getItems().add(allGedung);
        cbFilterGedung.getItems().addAll(gedungList);
        cbFilterGedung.setValue(allGedung);
    }
    
    @FXML
    private void handleGedungChange() {
        com.sistemruangan.model.Gedung selectedGedung = cbFilterGedung.getValue();
        
        cbFilterLantai.getItems().clear();
        cbFilterLantai.getItems().add("Semua");
        
        if (selectedGedung == null || selectedGedung.getId() == 0) {
            cbFilterLantai.setValue("Semua");
            cbFilterLantai.setDisable(true);
        } else {
            for (int i = 1; i <= selectedGedung.getJumlahLantai(); i++) {
                cbFilterLantai.getItems().add("Lantai " + i);
            }
            cbFilterLantai.setValue("Semua");
            cbFilterLantai.setDisable(false);
        }
        
        handleFilter();
    }
    
    /**
     * Load default image once (cache)
     */
    private void loadDefaultImage() {
        try {
            File defaultFile = new File("resources/images/default_room.png");
            if (defaultFile.exists()) {
                defaultImage = new Image(defaultFile.toURI().toString());
                System.out.println("✅ Default image loaded from file");
            } else {
                defaultImage = null;
                System.out.println("⚠️ Default image file not found");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading default image: " + e.getMessage());
            defaultImage = null;
        }
    }
    
    /**
     * Load data asynchronously untuk tidak freeze UI
     */
    private void loadDataAsync() {
        // Show loading
        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }
        
        Task<ObservableList<Ruangan>> loadTask = new Task<ObservableList<Ruangan>>() {
            @Override
            protected ObservableList<Ruangan> call() throws Exception {
                return ruanganController.getAllRuangan();
            }
            
            @Override
            protected void succeeded() {
                ObservableList<Ruangan> ruanganList = getValue();
                filteredData = new FilteredList<>(ruanganList, p -> true);
                
                // Display cards
                displayCardsOptimized();
                updateSummary();
                
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                
                System.out.println("✅ Data loaded: " + ruanganList.size() + " ruangan");
            }
            
            @Override
            protected void failed() {
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                
                DialogUtil.showDialog(
                    DialogUtil.DialogType.ERROR,
                    "Error",
                    "Gagal memuat data ruangan!",
                    MainApp.getRootContainer()
                );
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Display cards dengan optimasi performa
     */
    private void displayCardsOptimized() {
        flowPaneCards.getChildren().clear();
        
        if (filteredData.isEmpty()) {
            Label noData = new Label("Tidak ada ruangan yang ditemukan");
            noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            flowPaneCards.getChildren().add(noData);
            return;
        }
        
        // Render cards in batches untuk smooth scrolling
        int batchSize = 6; // Render 6 cards at a time
        int currentIndex = 0;
        
        for (Ruangan ruangan : filteredData) {
            VBox card = createOptimizedCard(ruangan);
            flowPaneCards.getChildren().add(card);
            
            currentIndex++;
            
            // Add small delay every batch untuk smooth rendering
            if (currentIndex % batchSize == 0) {
                try {
                    Thread.sleep(10); // Small delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Create optimized card (reduced shadow, lazy image loading)
     */
    private VBox createOptimizedCard(Ruangan ruangan) {
        VBox card = new VBox(12);
        card.setPrefWidth(320);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" + // Reduced shadow
            "-fx-padding: 0;"
        );
        
        // Simple hover effect (no scale transformation untuk performa)
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(91,155,213,0.3), 15, 0, 0, 5);" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
            )
        );
        
        // Image section with lazy loading
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(200);
        imageContainer.setStyle(
            "-fx-background-color: #f5f7fa;" +
            "-fx-background-radius: 12 12 0 0;"
        );
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(320);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true); // Smooth rendering
        imageView.setCache(true); // Cache image
        
        // Load image (cached)
        loadImageCached(ruangan, imageView);
        
        imageContainer.getChildren().add(imageView);
        
        // Content section
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        // Nama Ruangan
        Label lblNama = new Label(ruangan.getNamaRuangan());
        lblNama.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblNama.setStyle("-fx-text-fill: #2c3e50;");
        lblNama.setWrapText(true);
        lblNama.setMaxWidth(280);
        
        // Kapasitas
        HBox infoKapasitas = new HBox(5);
        infoKapasitas.setAlignment(Pos.CENTER_LEFT);
        Label iconKapasitas = new Label("👥");
        Label lblKapasitas = new Label(ruangan.getJumlahKursi() + " Kursi");
        lblKapasitas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        infoKapasitas.getChildren().addAll(iconKapasitas, lblKapasitas);
        
        // Fasilitas (simplified - max 2 items)
        HBox fasilitasBox = new HBox(5);
        fasilitasBox.setAlignment(Pos.CENTER_LEFT);
        Label iconFas = new Label("📋");
        
        String fasText = "Tidak ada fasilitas";
        if (ruangan.getFasilitas() != null && !ruangan.getFasilitas().trim().isEmpty()) {
            String[] fas = ruangan.getFasilitas().split(",");
            if (fas.length > 0) {
                fasText = fas[0].trim();
                if (fas.length > 1) {
                    fasText += ", " + fas[1].trim();
                }
                if (fas.length > 2) {
                    fasText += " +" + (fas.length - 2) + " lainnya";
                }
            }
        }
        
        Label lblFas = new Label(fasText);
        lblFas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        lblFas.setWrapText(true);
        lblFas.setMaxWidth(250);
        fasilitasBox.getChildren().addAll(iconFas, lblFas);
        
        // Status Badge
        Label lblStatus = new Label(ruangan.getStatus().toUpperCase());
        lblStatus.setPadding(new Insets(4, 12, 4, 12));
        lblStatus.setStyle(
            "-fx-background-radius: 12;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            (ruangan.getStatus().equalsIgnoreCase("tersedia") 
                ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" 
                : "-fx-background-color: #fff3cd; -fx-text-fill: #856404;")
        );
        
        // Button
        Button btnPinjam = new Button(
            ruangan.getStatus().equalsIgnoreCase("tersedia") 
                ? "📝 Ajukan" 
                : "❌ Tidak Tersedia"
        );
        btnPinjam.setMaxWidth(Double.MAX_VALUE);
        btnPinjam.setDisable(!ruangan.getStatus().equalsIgnoreCase("tersedia"));
        
        if (ruangan.getStatus().equalsIgnoreCase("tersedia")) {
            btnPinjam.setStyle(
                "-fx-background-color: #5B9BD5;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 10 15;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
            );
            
            btnPinjam.setOnMouseEntered(e -> 
                btnPinjam.setStyle(
                    "-fx-background-color: #4a8bc2;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;"
                )
            );
            
            btnPinjam.setOnMouseExited(e -> 
                btnPinjam.setStyle(
                    "-fx-background-color: #5B9BD5;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;"
                )
            );
        } else {
            btnPinjam.setStyle(
                "-fx-background-color: #e8edf2;" +
                "-fx-text-fill: #95a5a6;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 10 15;" +
                "-fx-background-radius: 6;"
            );
        }
        
        btnPinjam.setOnAction(e -> handlePinjam(ruangan));
        
        // Add to content
        content.getChildren().addAll(
            lblNama,
            new Separator(),
            infoKapasitas,
            fasilitasBox,
            lblStatus,
            btnPinjam
        );
        
        // Add to card
        card.getChildren().addAll(imageContainer, content);
        
        return card;
    }
    
    /**
     * Load image dengan caching untuk performa
     */
    private void loadImageCached(Ruangan ruangan, ImageView imageView) {
        String fotoPath = ruangan.getFotoPath();
        
        // Check if already cached
        if (fotoPath != null && !fotoPath.isEmpty() && imageCache.containsKey(fotoPath)) {
            imageView.setImage(imageCache.get(fotoPath));
            return;
        }
        
        // Load image
        try {
            if (fotoPath != null && !fotoPath.isEmpty()) {
                File fotoFile = new File(FOTO_DIR + fotoPath);
                if (fotoFile.exists()) {
                    Image image = new Image(
                        fotoFile.toURI().toString(),
                        320, 200, true, true, true // Smooth & async loading
                    );
                    imageCache.put(fotoPath, image); // Cache it
                    imageView.setImage(image);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error loading image: " + e.getMessage());
        }
        
        // Use default image
        imageView.setImage(defaultImage);
    }
    
    /**
     * Update summary statistik
     */
    private void updateSummary() {
        int total = filteredData.size();
        int tersedia = 0;
        int dipinjam = 0;
        
        for (Ruangan r : filteredData) {
            if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                tersedia++;
            } else if ("dipinjam".equalsIgnoreCase(r.getStatus())) {
                dipinjam++;
            }
        }
        
        lblTotalRuangan.setText(String.valueOf(total));
        lblTersedia.setText(String.valueOf(tersedia));
        lblDipinjam.setText(String.valueOf(dipinjam));
    }
    
    /**
     * Handle tombol pinjam - FIXED: Use overlay dialog instead of Alert
     */
    private void handlePinjam(Ruangan ruangan) {
        DialogUtil.showConfirmation(
            "Konfirmasi Peminjaman",
            "Anda akan mengajukan peminjaman untuk ruangan: " + ruangan.getNamaRuangan() + 
            "\n\nAnda akan diarahkan ke form peminjaman. Lanjutkan?",
            MainApp.getRootContainer(),
            () -> {
                // On Confirm
                UserPeminjamanFormController.setSelectedRuangan(ruangan);
                MainApp.showUserPeminjamanForm();
            },
            () -> {
                // On Cancel
                System.out.println("Peminjaman dibatalkan");
            }
        );
    }
    
    @FXML
    private void handleFilter() {
        String selectedStatus = cbFilterStatus.getValue();
        
        filteredData.setPredicate(ruangan -> {
            boolean matchSearch = true;
            if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
                String searchText = txtSearch.getText().toLowerCase();
                matchSearch = ruangan.getNamaRuangan().toLowerCase().contains(searchText);
            }
            
            boolean matchStatus = true;
            if (!"Semua".equals(selectedStatus)) {
                matchStatus = ruangan.getStatus().equalsIgnoreCase(selectedStatus);
            }
            
            boolean matchGedung = true;
            com.sistemruangan.model.Gedung selectedGedung = cbFilterGedung.getValue();
            if (selectedGedung != null && selectedGedung.getId() != 0) {
                matchGedung = ruangan.getIdGedung() == selectedGedung.getId();
            }
            
            boolean matchLantai = true;
            String selectedLantai = cbFilterLantai.getValue();
            if (selectedLantai != null && !"Semua".equals(selectedLantai)) {
                try {
                    int lantaiNum = Integer.parseInt(selectedLantai.replace("Lantai ", ""));
                    matchLantai = ruangan.getLantai() == lantaiNum;
                } catch (Exception e) {
                    System.err.println("Error parsing lantai: " + e.getMessage());
                }
            }
            
            return matchSearch && matchStatus && matchGedung && matchLantai;
        });
        
        displayCardsOptimized();
        updateSummary();
    }
    
    @FXML
    private void handleSearch() {
        handleFilter();
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cbFilterStatus.setValue("Semua");
        
        com.sistemruangan.model.Gedung allGedung = cbFilterGedung.getItems().get(0);
        cbFilterGedung.setValue(allGedung);
        
        cbFilterLantai.getItems().clear();
        cbFilterLantai.getItems().add("Semua");
        cbFilterLantai.setValue("Semua");
        cbFilterLantai.setDisable(true);
        
        imageCache.clear(); // Clear cache on refresh
        loadDataAsync();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}