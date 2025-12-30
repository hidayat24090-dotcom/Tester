package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.model.Peminjaman;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Controller untuk halaman Jadwal Bulanan
 */
public class JadwalBulananController {
    
    @FXML private TableView<Peminjaman> tableJadwal;
    @FXML private TableColumn<Peminjaman, LocalDate> colTanggalPinjam;
    @FXML private TableColumn<Peminjaman, LocalDate> colTanggalKembali;
    @FXML private TableColumn<Peminjaman, String> colRuangan;
    @FXML private TableColumn<Peminjaman, String> colPeminjam;
    @FXML private TableColumn<Peminjaman, String> colKeperluan;
    @FXML private TableColumn<Peminjaman, LocalTime> colJamMulai;
    @FXML private TableColumn<Peminjaman, LocalTime> colJamSelesai;
    @FXML private TableColumn<Peminjaman, String> colStatus;
    
    @FXML private ComboBox<String> cbBulan;
    @FXML private ComboBox<Integer> cbTahun;
    @FXML private Label lblPeriode;
    @FXML private Label lblTotal;
    @FXML private Label lblAktif;
    @FXML private Label lblSelesai;
    @FXML private Label lblBatal;
    @FXML private Button btnFilter;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private PeminjamanController peminjamanController;
    
    @FXML
    public void initialize() {
        peminjamanController = new PeminjamanController();
        
        // Setup table columns
        colTanggalPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colTanggalKembali.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        colJamMulai.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        colJamSelesai.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colPeminjam.setCellValueFactory(new PropertyValueFactory<>("namaPeminjam"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPeminjaman"));
        
        // Setup combo boxes
        setupComboBoxes();
        
        // Load data bulan ini
        LocalDate now = LocalDate.now();
        cbBulan.setValue(now.getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID")));
        cbTahun.setValue(now.getYear());
        loadData(now.getMonthValue(), now.getYear());
        
    }
    
    /**
     * Setup ComboBox untuk bulan dan tahun
     */
    private void setupComboBoxes() {
        // Bulan
        for (Month month : Month.values()) {
            cbBulan.getItems().add(month.getDisplayName(TextStyle.FULL, new Locale("id", "ID")));
        }
        
        // Tahun (5 tahun ke belakang sampai 2 tahun ke depan)
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear + 2; year++) {
            cbTahun.getItems().add(year);
        }
    }
    
    /**
     * Load data peminjaman berdasarkan bulan dan tahun
     */
    private void loadData(int month, int year) {
        ObservableList<Peminjaman> peminjamanList = peminjamanController.getPeminjamanByMonth(month, year);
        tableJadwal.setItems(peminjamanList);
        
        // Update periode label
        String bulanNama = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        lblPeriode.setText("Periode: " + bulanNama + " " + year);
        
        // Update summary
        updateSummary(peminjamanList);
    }
    
    /**
     * Update summary statistik
     */
    private void updateSummary(ObservableList<Peminjaman> peminjamanList) {
        int total = peminjamanList.size();
        int aktif = 0;
        int selesai = 0;
        int batal = 0;
        
        for (Peminjaman p : peminjamanList) {
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
    
    @FXML
    private void handleFilter() {
        if (cbBulan.getValue() == null || cbTahun.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih bulan dan tahun terlebih dahulu!");
            return;
        }
        
        // Convert bulan name to number
        String bulanNama = cbBulan.getValue();
        int month = 0;
        for (Month m : Month.values()) {
            if (m.getDisplayName(TextStyle.FULL, new Locale("id", "ID")).equals(bulanNama)) {
                month = m.getValue();
                break;
            }
        }
        
        int year = cbTahun.getValue();
        loadData(month, year);
    }
    
    @FXML
    private void handleRefresh() {
        LocalDate now = LocalDate.now();
        cbBulan.setValue(now.getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID")));
        cbTahun.setValue(now.getYear());
        loadData(now.getMonthValue(), now.getYear());
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}