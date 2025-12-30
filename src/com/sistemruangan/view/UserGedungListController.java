package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.GedungController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Gedung;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller untuk Daftar Gedung - User Side
 */
public class UserGedungListController {
    
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPaneGedung;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalGedung;
    @FXML private Label lblTotalRuangan;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    @FXML private ProgressIndicator progressIndicator;
    
    private GedungController gedungController;
    private RuanganController ruanganController;
    private FilteredList<Gedung> filteredData;
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing UserGedungListController...");
        
        gedungController = new GedungController();
        ruanganController = new RuanganController();
        
        // Setup FlowPane
        flowPaneGedung.setHgap(20);
        flowPaneGedung.setVgap(20);
        flowPaneGedung.setPadding(new Insets(10));
        flowPaneGedung.setStyle("-fx-background-color: transparent;");
        
        // Load data
        loadDataAsync();
    }
    
    /**
     * Load data asynchronously
     */
    private void loadDataAsync() {
        progressIndicator.setVisible(true);
        
        Task<ObservableList<Gedung>> loadTask = new Task<ObservableList<Gedung>>() {
            @Override
            protected ObservableList<Gedung> call() throws Exception {
                return gedungController.getAllGedung();
            }
            
            @Override
            protected void succeeded() {
                ObservableList<Gedung> gedungList = getValue();
                filteredData = new FilteredList<>(gedungList, p -> true);
                
                displayGedungCards();
                updateSummary();
                
                progressIndicator.setVisible(false);
                System.out.println("✅ Data loaded: " + gedungList.size() + " gedung");
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                DialogUtil.showDialog(
                    DialogUtil.DialogType.ERROR,
                    "Error",
                    "Gagal memuat data gedung!",
                    MainApp.getRootContainer()
                );
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Display gedung cards
     */
    private void displayGedungCards() {
        flowPaneGedung.getChildren().clear();
        
        if (filteredData.isEmpty()) {
            Label noData = new Label("Tidak ada gedung yang ditemukan");
            noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
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
        card.setPrefWidth(320);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
            "-fx-padding: 25;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(91,155,213,0.3), 15, 0, 0, 5);" +
                "-fx-padding: 25;" +
                "-fx-cursor: hand;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
                "-fx-padding: 25;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Icon
        Label iconLabel = new Label("🏢");
        iconLabel.setStyle("-fx-font-size: 64px;");
        iconLabel.setAlignment(Pos.CENTER);
        
        // Nama Gedung
        Label lblNama = new Label(gedung.getNamaGedung());
        lblNama.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblNama.setStyle("-fx-text-fill: #2c3e50;");
        lblNama.setWrapText(true);
        lblNama.setMaxWidth(280);
        lblNama.setAlignment(Pos.CENTER);
        
        // Info Lantai
        HBox infoLantai = new HBox(8);
        infoLantai.setAlignment(Pos.CENTER);
        Label iconLantai = new Label("📊");
        iconLantai.setStyle("-fx-font-size: 18px;");
        Label lblLantai = new Label(gedung.getJumlahLantai() + " Lantai");
        lblLantai.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-font-weight: bold;");
        infoLantai.getChildren().addAll(iconLantai, lblLantai);
        
        // Get ruangan count
        int jumlahRuangan = ruanganController.getRuanganByGedung(gedung.getId()).size();
        HBox infoRuangan = new HBox(8);
        infoRuangan.setAlignment(Pos.CENTER);
        Label iconRuangan = new Label("🚪");
        iconRuangan.setStyle("-fx-font-size: 18px;");
        Label lblRuangan = new Label(jumlahRuangan + " Ruangan");
        lblRuangan.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        infoRuangan.getChildren().addAll(iconRuangan, lblRuangan);
        
        Separator sep = new Separator();
        
        // Button
        Button btnLihatRuangan = new Button("📋 Lihat Ruangan");
        btnLihatRuangan.setMaxWidth(Double.MAX_VALUE);
        btnLihatRuangan.setStyle(
            "-fx-background-color: #5B9BD5;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btnLihatRuangan.setOnMouseEntered(e -> 
            btnLihatRuangan.setStyle(
                "-fx-background-color: #4a8bc2;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 12 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        btnLihatRuangan.setOnMouseExited(e -> 
            btnLihatRuangan.setStyle(
                "-fx-background-color: #5B9BD5;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 12 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        btnLihatRuangan.setOnAction(e -> handleLihatRuangan(gedung));
        
        card.getChildren().addAll(
            iconLabel, 
            lblNama, 
            infoLantai, 
            infoRuangan, 
            sep, 
            btnLihatRuangan
        );
        
        return card;
    }
    
    /**
     * Handle lihat ruangan
     */
    private void handleLihatRuangan(Gedung gedung) {
        System.out.println("📋 Opening ruangan for: " + gedung.getNamaGedung());
        UserRuanganPerGedungController.setSelectedGedung(gedung);
        MainApp.showUserRuanganPerGedung();
    }
    
    /**
     * Update summary
     */
    private void updateSummary() {
        lblTotalGedung.setText(String.valueOf(filteredData.size()));
        
        // Count total ruangan
        int totalRuangan = 0;
        for (Gedung g : filteredData) {
            totalRuangan += ruanganController.getRuanganByGedung(g.getId()).size();
        }
        lblTotalRuangan.setText(String.valueOf(totalRuangan));
    }
    
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        filteredData.setPredicate(gedung -> {
            if (keyword.isEmpty()) return true;
            return gedung.getNamaGedung().toLowerCase().contains(keyword);
        });
        
        displayGedungCards();
        updateSummary();
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadDataAsync();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}