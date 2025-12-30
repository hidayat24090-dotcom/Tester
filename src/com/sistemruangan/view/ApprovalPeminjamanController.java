package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.util.DialogUtil;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller untuk halaman Approval Peminjaman - ADMIN (FIXED)
 */
public class ApprovalPeminjamanController {
    
    @FXML private Label lblPending;
    @FXML private Label lblApproved;
    @FXML private Label lblRejected;
    
    @FXML private ComboBox<String> cbFilter;
    @FXML private TextField txtSearch;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    @FXML private TableView<Peminjaman> tablePeminjaman;
    @FXML private TableColumn<Peminjaman, LocalDate> colTanggal;
    @FXML private TableColumn<Peminjaman, String> colPeminjam;
    @FXML private TableColumn<Peminjaman, String> colRuangan;
    @FXML private TableColumn<Peminjaman, String> colKeperluan;
    @FXML private TableColumn<Peminjaman, LocalDate> colTglPinjam;
    @FXML private TableColumn<Peminjaman, LocalDate> colTglKembali;
    @FXML private TableColumn<Peminjaman, String> colJam;
    @FXML private TableColumn<Peminjaman, String> colStatus;
    @FXML private TableColumn<Peminjaman, Void> colAksi;
    
    private PeminjamanController peminjamanController;
    private FilteredList<Peminjaman> filteredData;
    private ObservableList<Peminjaman> allData;
    
    private static final String SURAT_DIR = "resources/surat/";
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing ApprovalPeminjamanController...");
        
        try {
            peminjamanController = new PeminjamanController();
            
            // Setup table columns
            setupTableColumns();
            
            // Setup action column
            setupActionColumn();
            
            // Setup filter
            cbFilter.getItems().addAll("Semua", "Menunggu", "Disetujui", "Ditolak");
            cbFilter.setValue("Semua");
            
            // Load data
            loadData();
            
            System.out.println("✅ ApprovalPeminjamanController initialized");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR initializing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colTanggal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(
                cellData.getValue().getTanggalPinjam()
            )
        );
        colTanggal.setCellFactory(col -> new TableCell<Peminjaman, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                }
            }
        });
        
        colPeminjam.setCellValueFactory(new PropertyValueFactory<>("namaPeminjam"));
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        colTglPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colTglKembali.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        
        colJam.setCellValueFactory(cellData -> {
            Peminjaman p = cellData.getValue();
            if (p.getJamMulai() != null && p.getJamSelesai() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    p.getJamMulai().toString() + " - " + p.getJamSelesai().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        
        colStatus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatusApprovalDisplay()
            )
        );
        
        // Style status column
        colStatus.setCellFactory(col -> new TableCell<Peminjaman, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.contains("Menunggu")) {
                        setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold;");
                    } else if (status.contains("Disetujui")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (status.contains("Ditolak")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Left align name columns
        colPeminjam.setStyle("-fx-alignment: CENTER-LEFT;");
        colRuangan.setStyle("-fx-alignment: CENTER-LEFT;");
        colKeperluan.setStyle("-fx-alignment: CENTER-LEFT;");
    }
    
    /**
     * Setup action column with buttons
     */
    private void setupActionColumn() {
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetail = new Button("👁 Detail");
            private final Button btnApprove = new Button("✅ Setuju");
            private final Button btnReject = new Button("❌ Tolak");
            private final HBox container = new HBox(5);
            
            {
                // Style buttons
                btnDetail.setStyle("-fx-background-color: #5B9BD5; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                 "-fx-cursor: hand; -fx-font-size: 11px;");
                
                btnApprove.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                  "-fx-cursor: hand; -fx-font-size: 11px;");
                
                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                 "-fx-cursor: hand; -fx-font-size: 11px;");
                
                // Hover effects
                btnDetail.setOnMouseEntered(e -> 
                    btnDetail.setStyle("-fx-background-color: #4a8bc2; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                     "-fx-cursor: hand; -fx-font-size: 11px;")
                );
                btnDetail.setOnMouseExited(e -> 
                    btnDetail.setStyle("-fx-background-color: #5B9BD5; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                     "-fx-cursor: hand; -fx-font-size: 11px;")
                );
                
                btnApprove.setOnMouseEntered(e -> btnApprove.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;"));
                btnApprove.setOnMouseExited(e -> btnApprove.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;"));
                
                btnReject.setOnMouseEntered(e -> btnReject.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;"));
                btnReject.setOnMouseExited(e -> btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;"));
                
                // Actions
                btnDetail.setOnAction(e -> {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    handleDetail(peminjaman);
                });
                
                btnApprove.setOnAction(e -> {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    handleApprove(peminjaman);
                });
                
                btnReject.setOnAction(e -> {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    handleReject(peminjaman);
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(btnDetail, btnApprove, btnReject);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    
                    if (peminjaman.isPending()) {
                        // Show all buttons for pending
                        btnApprove.setDisable(false);
                        btnReject.setDisable(false);
                        setGraphic(container);
                    } else {
                        // Only show detail for approved/rejected
                        btnApprove.setDisable(true);
                        btnReject.setDisable(true);
                        HBox detailOnly = new HBox(btnDetail);
                        detailOnly.setAlignment(Pos.CENTER);
                        setGraphic(detailOnly);
                    }
                }
            }
        });
    }

    /**
     * Load data peminjaman non-kuliah - FIXED VERSION
     */
    private void loadData() {
        try {
            System.out.println("📋 Loading approval data...");
            
            // Get ALL peminjaman
            allData = peminjamanController.getAllPeminjaman();
            
            if (allData == null) {
                System.err.println("❌ allData is NULL!");
                showError("Gagal memuat data!");
                return;
            }
            
            System.out.println("✅ Got " + allData.size() + " total peminjaman");
            
            // Filter HANYA yang jenis_kegiatan = 'lainnya' 
            ObservableList<Peminjaman> lainnyaList = FXCollections.observableArrayList();
            
            for (Peminjaman p : allData) {
                String jenis = p.getJenisKegiatan();
                
                System.out.println("  - ID: " + p.getId() + 
                                ", Jenis: '" + jenis + "'" +
                                ", Status Approval: '" + p.getStatusApproval() + "'");
                
                // CRITICAL FIX: Check for 'lainnya' (bukan 'non_kuliah')
                // Database menyimpan sebagai 'lainnya'
                if (jenis != null && "lainnya".equalsIgnoreCase(jenis.trim())) {
                    lainnyaList.add(p);
                    System.out.println("    ✅ Added to approval list");
                }
            }
            
            System.out.println("✅ Found " + lainnyaList.size() + " 'lainnya' peminjaman");
            
            // Create filtered list
            filteredData = new FilteredList<>(lainnyaList, p -> true);
            tablePeminjaman.setItems(filteredData);
            
            // Update summary
            updateSummary();
            
            // Show message if no data
            if (lainnyaList.isEmpty()) {
                System.out.println("ℹ️ No 'lainnya' peminjaman found");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading data: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * Update summary statistics
     */
    private void updateSummary() {
        int pending = 0;
        int approved = 0;
        int rejected = 0;
        
        for (Peminjaman p : filteredData.getSource()) {
            String status = p.getStatusApproval();
            if (status == null) continue;
            
            switch (status.toLowerCase()) {
                case "pending":
                    pending++;
                    break;
                case "approved":
                    approved++;
                    break;
                case "rejected":
                    rejected++;
                    break;
            }
        }
        
        lblPending.setText(String.valueOf(pending));
        lblApproved.setText(String.valueOf(approved));
        lblRejected.setText(String.valueOf(rejected));
    }
    
    /**
     * Handle detail button
     */
    private void handleDetail(Peminjaman peminjaman) {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Detail Pengajuan Peminjaman");
            dialog.setResizable(true);
            dialog.getDialogPane().setPrefWidth(600);
            
            // Content
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(12);
            content.setPadding(new javafx.geometry.Insets(15));
            content.setStyle("-fx-background-color: white;");
            
            // Info sections
            content.getChildren().addAll(
                createInfoRow("👤 Peminjam", peminjaman.getNamaPeminjam()),
                createInfoRow("🏢 Ruangan", peminjaman.getNamaRuangan()),
                createInfoRow("📅 Tanggal", peminjaman.getTanggalPinjam().toString() + " s/d " + peminjaman.getTanggalKembali().toString()),
                createInfoRow("⏰ Waktu", peminjaman.getJamMulai().toString() + " - " + peminjaman.getJamSelesai().toString()),
                createInfoRow("📋 Keperluan", peminjaman.getKeperluan()),
                new Separator(),
                new Label("Detail Kegiatan Non-Kuliah:") {{
                    setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                }},
                createInfoBox("Penjelasan:", peminjaman.getPenjelasanKegiatan())
            );
            
            // Surat section
            if (peminjaman.getSuratPath() != null && !peminjaman.getSuratPath().isEmpty()) {
                HBox suratBox = new HBox(10);
                suratBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                suratBox.setStyle("-fx-background-color: #FFF3CD; -fx-padding: 10; -fx-background-radius: 6;");
                
                Label lblSurat = new Label("📄 Surat: " + peminjaman.getSuratPath());
                Button btnBukaSurat = new Button("Buka File");
                btnBukaSurat.setStyle("-fx-background-color: #5B9BD5; -fx-text-fill: white; -fx-font-weight: bold;");
                btnBukaSurat.setOnAction(e -> openSurat(peminjaman.getSuratPath()));
                
                suratBox.getChildren().addAll(lblSurat, btnBukaSurat);
                content.getChildren().add(suratBox);
            }
            
            // Status approval section
            if (!peminjaman.isPending()) {
                content.getChildren().addAll(
                    new Separator(),
                    createInfoRow("✅ Status", peminjaman.getStatusApprovalDisplay()),
                    createInfoRow("👨‍💼 Diproses oleh", peminjaman.getApprovedBy()),
                    createInfoBox("Keterangan", peminjaman.getKeteranganApproval())
                );
            }
            
            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");
            
            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ ERROR showing detail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle approve
     */
    private void handleApprove(Peminjaman peminjaman) {
        try {
            StackPane root = MainApp.getRootContainer();
            
            // Show input dialog for keterangan
            DialogUtil.showInputDialog(
                "Setujui Peminjaman",
                "Masukkan keterangan approval (opsional):",
                "Contoh: Disetujui. Pastikan ruangan dibersihkan setelah acara.",
                root,
                new DialogUtil.InputCallback() {
                    @Override
                    public void onInput(String keterangan) {
                        // Show confirmation
                        DialogUtil.showConfirmation(
                            "Konfirmasi Persetujuan",
                            "Yakin menyetujui peminjaman ruangan " + peminjaman.getNamaRuangan() + "?",
                            root,
                            () -> {
                                // On Confirm - Approve
                                if (peminjamanController.approvePeminjaman(
                                        peminjaman.getId(), 
                                        peminjaman.getIdRuangan(), 
                                        "Admin", 
                                        keterangan.trim())) {
                                    
                                    DialogUtil.showDialog(
                                        DialogUtil.DialogType.SUCCESS,
                                        "Berhasil",
                                        "Peminjaman berhasil disetujui!",
                                        root
                                    );
                                    loadData();
                                } else {
                                    DialogUtil.showDialog(
                                        DialogUtil.DialogType.ERROR,
                                        "Gagal",
                                        "Gagal menyetujui peminjaman!",
                                        root
                                    );
                                }
                            },
                            () -> {
                                // On Cancel
                                System.out.println("Approval cancelled");
                            }
                        );
                    }
                    
                    @Override
                    public void onCancel() {
                        System.out.println("Input cancelled");
                    }
                }
            );
            
        } catch (Exception e) {
            System.err.println("❌ ERROR approving: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * Handle reject
     */
    private void handleReject(Peminjaman peminjaman) {
        try {
            StackPane root = MainApp.getRootContainer();
            
            DialogUtil.showInputDialog(
                "Tolak Peminjaman",
                "Masukkan alasan penolakan:",
                "Contoh: Ruangan sedang direnovasi untuk persiapan wisuda.",
                root,
                new DialogUtil.InputCallback() {
                    @Override
                    public void onInput(String alasan) {
                        if (alasan.trim().isEmpty()) {
                            DialogUtil.showDialog(
                                DialogUtil.DialogType.WARNING,
                                "Peringatan",
                                "Alasan penolakan harus diisi!",
                                root
                            );
                            return;
                        }
                        
                        // Show confirmation
                        DialogUtil.showConfirmation(
                            "Konfirmasi Penolakan",
                            "Yakin menolak peminjaman ini?",
                            root,
                            () -> {
                                if (peminjamanController.rejectPeminjaman(
                                        peminjaman.getId(), 
                                        "Admin", 
                                        alasan.trim())) {
                                    
                                    DialogUtil.showDialog(
                                        DialogUtil.DialogType.SUCCESS,
                                        "Berhasil",
                                        "Peminjaman berhasil ditolak!",
                                        root
                                    );
                                    loadData();
                                } else {
                                    DialogUtil.showDialog(
                                        DialogUtil.DialogType.ERROR,
                                        "Gagal",
                                        "Gagal menolak peminjaman!",
                                        root
                                    );
                                }
                            },
                            null
                        );
                    }
                    
                    @Override
                    public void onCancel() {
                        System.out.println("Rejection cancelled");
                    }
                }
            );
            
        } catch (Exception e) {
            System.err.println("❌ ERROR rejecting: " + e.getMessage());
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }
    
    /**
     * Open surat file
     */
    private void openSurat(String suratPath) {
        try {
            File file = new File(SURAT_DIR + suratPath);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
            } else {
                showWarning("File surat tidak ditemukan!");
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR opening surat: " + e.getMessage());
            showError("Gagal membuka file: " + e.getMessage());
        }
    }
    
    /**
     * Helper: Create info row
     */
    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 150px;");
        
        Label lblValue = new Label(value != null ? value : "-");
        lblValue.setWrapText(true);
        
        row.getChildren().addAll(lblLabel, lblValue);
        return row;
    }
    
    /**
     * Helper: Create info box with TextArea
     */
    private javafx.scene.layout.VBox createInfoBox(String label, String value) {
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(5);
        
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea txtValue = new TextArea(value != null ? value : "-");
        txtValue.setWrapText(true);
        txtValue.setEditable(false);
        txtValue.setPrefRowCount(3);
        txtValue.setStyle("-fx-background-color: #f8f9fa;");
        
        box.getChildren().addAll(lblLabel, txtValue);
        return box;
    }
    
    @FXML
    private void handleFilter() {
        String selected = cbFilter.getValue();
        
        filteredData.setPredicate(peminjaman -> {
            boolean matchSearch = true;
            if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
                String searchText = txtSearch.getText().toLowerCase();
                matchSearch = peminjaman.getNamaPeminjam().toLowerCase().contains(searchText) ||
                             peminjaman.getNamaRuangan().toLowerCase().contains(searchText);
            }
            
            boolean matchFilter = true;
            if (!"Semua".equals(selected)) {
                if ("Menunggu".equals(selected)) {
                    matchFilter = peminjaman.isPending();
                } else if ("Disetujui".equals(selected)) {
                    matchFilter = peminjaman.isApproved();
                } else if ("Ditolak".equals(selected)) {
                    matchFilter = peminjaman.isRejected();
                }
            }
            
            return matchSearch && matchFilter;
        });
        
        updateSummary();
    }
    
    @FXML
    private void handleSearch() {
        handleFilter();
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cbFilter.setValue("Semua");
        loadData();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDashboard();
    }
    
    private void showSuccess(String message) {
        DialogUtil.showDialog(
            DialogUtil.DialogType.SUCCESS,
            "Berhasil",
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
}