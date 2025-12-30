package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.util.SessionManager;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller untuk halaman Riwayat Peminjaman User
 */
public class UserRiwayatController {
    
    @FXML private TableView<Peminjaman> tableRiwayat;
    @FXML private TableColumn<Peminjaman, LocalDate> colTanggalPinjam;
    @FXML private TableColumn<Peminjaman, LocalTime> colJamMulai;
    @FXML private TableColumn<Peminjaman, LocalTime> colJamSelesai;
    @FXML private TableColumn<Peminjaman, LocalDate> colTanggalKembali;
    @FXML private TableColumn<Peminjaman, String> colRuangan;
    @FXML private TableColumn<Peminjaman, String> colKeperluan;
    @FXML private TableColumn<Peminjaman, String> colStatus;
    @FXML private TableColumn<Peminjaman, Void> colAksi;
    
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotal;
    @FXML private Label lblAktif;
    @FXML private Label lblSelesai;
    @FXML private Label lblBatal;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private PeminjamanController peminjamanController;
    private FilteredList<Peminjaman> filteredData;
    
    @FXML
    public void initialize() {
        peminjamanController = new PeminjamanController();
        
        // Setup table columns
        colTanggalPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colJamMulai.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        colJamSelesai.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
        colTanggalKembali.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPeminjaman"));
        
        // Setup action column
        setupActionColumn();
        
        // Setup filter combo box
        cbFilterStatus.getItems().addAll("Semua", "Aktif", "Selesai", "Batal");
        cbFilterStatus.setValue("Semua");
        
        // Load data
        loadData();
    }
    
    /**
     * Setup kolom aksi dengan tombol
     */
    private void setupActionColumn() {
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnBatal = new Button("❌ Batalkan");
            private final Button btnDetail = new Button("👁 Detail");
            private final HBox container = new HBox(5, btnDetail, btnBatal);
            
            {
                // Style untuk btnBatal
                btnBatal.setStyle("-fx-background-color: #ED6A5A; -fx-text-fill: white; " +
                                "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;");
                
                btnBatal.setOnMouseEntered(e -> 
                    btnBatal.setStyle("-fx-background-color: #e45547; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                    "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;")
                );
                
                btnBatal.setOnMouseExited(e -> 
                    btnBatal.setStyle("-fx-background-color: #ED6A5A; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                    "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;")
                );
                
                // Style untuk btnDetail
                btnDetail.setStyle("-fx-background-color: #5B9BD5; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                 "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;");
                
                btnDetail.setOnMouseEntered(e -> 
                    btnDetail.setStyle("-fx-background-color: #4a8bc2; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                     "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;")
                );
                
                btnDetail.setOnMouseExited(e -> 
                    btnDetail.setStyle("-fx-background-color: #5B9BD5; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                     "-fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px;")
                );
                
                btnBatal.setOnAction(e -> {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    handleBatalkan(peminjaman);
                });
                
                btnDetail.setOnAction(e -> {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    handleDetail(peminjaman);
                });
                
                container.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Peminjaman peminjaman = getTableView().getItems().get(getIndex());
                    
                    if ("aktif".equalsIgnoreCase(peminjaman.getStatusPeminjaman())) {
                        btnBatal.setDisable(false);
                        setGraphic(container);
                    } else {
                        btnBatal.setDisable(true);
                        btnBatal.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                                        "-fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
                                        "-fx-background-radius: 5px; -fx-font-size: 11px;");
                        setGraphic(container);
                    }
                }
            }
        });
    }
    
    /**
     * Load data peminjaman user
     */
    private void loadData() {
        ObservableList<Peminjaman> allPeminjaman = peminjamanController.getAllPeminjaman();
        ObservableList<Peminjaman> userPeminjaman = javafx.collections.FXCollections.observableArrayList();
        
        // Filter hanya peminjaman user yang sedang login
        String currentUser = SessionManager.getNamaLengkap();
        for (Peminjaman p : allPeminjaman) {
            if (currentUser.equals(p.getNamaPeminjam())) {
                userPeminjaman.add(p);
            }
        }
        
        filteredData = new FilteredList<>(userPeminjaman, p -> true);
        tableRiwayat.setItems(filteredData);
        updateSummary();
    }
    
    /**
     * Update summary statistik
     */
    private void updateSummary() {
        int total = filteredData.size();
        int aktif = 0;
        int selesai = 0;
        int batal = 0;
        
        for (Peminjaman p : filteredData) {
            String status = p.getStatusPeminjaman().toLowerCase();
            switch (status) {
                case "aktif":
                    aktif++;
                    break;
                case "selesai":
                    selesai++;
                    break;
                case "batal":
                    batal++;
                    break;
            }
        }
        
        lblTotal.setText(String.valueOf(total));
        lblAktif.setText(String.valueOf(aktif));
        lblSelesai.setText(String.valueOf(selesai));
        lblBatal.setText(String.valueOf(batal));
    }
    
    /**
     * Handle batalkan peminjaman
     */
    private void handleBatalkan(Peminjaman peminjaman) {
        StackPane root = MainApp.getRootContainer();
        
        DialogUtil.showConfirmation(
            "Konfirmasi Pembatalan",
            "Yakin ingin membatalkan peminjaman ruangan " + peminjaman.getNamaRuangan() + "?",
            root,
            () -> {
                if (peminjamanController.batalkanPeminjaman(peminjaman.getId(), peminjaman.getIdRuangan())) {
                    showSuccess("Peminjaman berhasil dibatalkan!");
                    loadData();
                } else {
                    showError("Gagal membatalkan peminjaman!");
                }
            },
            null
        );
    }
    
    /**
     * Handle detail peminjaman
     */
    private void handleDetail(Peminjaman peminjaman) {
        StackPane root = MainApp.getRootContainer();
        
        StringBuilder content = new StringBuilder();
        content.append("Ruangan: ").append(peminjaman.getNamaRuangan()).append("\n\n");
        content.append("Keperluan: ").append(peminjaman.getKeperluan()).append("\n\n");
        content.append("Tanggal Pinjam: ").append(peminjaman.getTanggalPinjam()).append("\n\n");
        content.append("Tanggal Kembali: ").append(peminjaman.getTanggalKembali()).append("\n\n");
        content.append("Jam: ").append(peminjaman.getJamMulai()).append(" - ").append(peminjaman.getJamSelesai()).append("\n\n");
        content.append("Status: ").append(peminjaman.getStatusPeminjaman().toUpperCase());
        
        DialogUtil.showDialog(
            DialogUtil.DialogType.INFO,
            "Detail Peminjaman",
            content.toString(),
            root
        );
    }
    
    @FXML
    private void handleFilter() {
        String selectedStatus = cbFilterStatus.getValue();
        
        filteredData.setPredicate(peminjaman -> {
            // Filter berdasarkan search text
            boolean matchSearch = true;
            if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
                String searchText = txtSearch.getText().toLowerCase();
                matchSearch = peminjaman.getNamaRuangan().toLowerCase().contains(searchText) ||
                             peminjaman.getKeperluan().toLowerCase().contains(searchText);
            }
            
            // Filter berdasarkan status
            boolean matchStatus = true;
            if (!"Semua".equals(selectedStatus)) {
                matchStatus = peminjaman.getStatusPeminjaman().equalsIgnoreCase(selectedStatus);
            }
            
            return matchSearch && matchStatus;
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
        cbFilterStatus.setValue("Semua");
        loadData();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
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
}