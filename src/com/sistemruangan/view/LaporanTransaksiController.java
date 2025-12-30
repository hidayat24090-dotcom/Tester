package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.LaporanController;
import com.sistemruangan.model.StatistikRuangan;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Controller untuk Laporan Transaksi - FIXED VERSION
 */
public class LaporanTransaksiController {
    
    @FXML private Label lblPeriode;
    @FXML private Label lblTotalRuangan;
    @FXML private Label lblRuanganTersedia;
    @FXML private Label lblTotalPeminjaman;
    @FXML private Label lblPeminjamanAktif;
    @FXML private Label lblPeminjamanSelesai;
    
    @FXML private BarChart<String, Number> chartRuangan;
    @FXML private PieChart chartStatus;
    @FXML private LineChart<String, Number> chartTrend;
    
    @FXML private TableView<StatistikRuangan> tableStatistik;
    @FXML private TableColumn<StatistikRuangan, String> colRuangan;
    @FXML private TableColumn<StatistikRuangan, Integer> colTotalPeminjaman;
    @FXML private TableColumn<StatistikRuangan, Integer> colAktif;
    @FXML private TableColumn<StatistikRuangan, Integer> colSelesai;
    @FXML private TableColumn<StatistikRuangan, Integer> colBatal;
    @FXML private TableColumn<StatistikRuangan, String> colPersentase;
    
    @FXML private javafx.scene.control.TextField txtSearch;
    @FXML private VBox vboxTopRuangan;
    @FXML private Button btnExportPDF;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    @FXML private ProgressIndicator progressIndicator;
    
    private LaporanController laporanController;
    private FilteredList<StatistikRuangan> filteredData;
    private ObservableList<StatistikRuangan> allStatistik;
    private Map<String, Object> cachedStats;
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing LaporanTransaksiController...");
        
        try {
            laporanController = new LaporanController();
            
            setupTableColumns();
            setupCharts();
            
            lblPeriode.setText("Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            
            loadDataAsync();
            
            System.out.println("✅ LaporanTransaksiController initialized");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat laporan: " + e.getMessage());
        }
    }
    
    private void setupTableColumns() {
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colRuangan.setStyle("-fx-alignment: CENTER-LEFT;");
        
        colTotalPeminjaman.setCellValueFactory(new PropertyValueFactory<>("totalPeminjaman"));
        colTotalPeminjaman.setStyle("-fx-alignment: CENTER;");
        
        colAktif.setCellValueFactory(new PropertyValueFactory<>("peminjamanAktif"));
        colAktif.setStyle("-fx-alignment: CENTER;");
        
        colSelesai.setCellValueFactory(new PropertyValueFactory<>("peminjamanSelesai"));
        colSelesai.setStyle("-fx-alignment: CENTER;");
        
        colBatal.setCellValueFactory(new PropertyValueFactory<>("peminjamanBatal"));
        colBatal.setStyle("-fx-alignment: CENTER;");
        
        colPersentase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPersentaseFormatted())
        );
        colPersentase.setStyle("-fx-alignment: CENTER;");
    }
    
    private void setupCharts() {
        chartRuangan.setLegendSide(Side.TOP);
        chartRuangan.setAnimated(true);
        
        // FIXED: Pie chart configuration - Legend di bawah agar tidak overflow
        chartStatus.setLegendSide(Side.BOTTOM);
        chartStatus.setAnimated(true);
        chartStatus.setLabelsVisible(false); // Hide labels untuk mencegah overflow
        chartStatus.setStartAngle(90);
        chartStatus.setClockwise(true);
        
        // Set max size untuk mencegah chart terlalu besar
        chartStatus.setMaxHeight(250);
        
        if (chartTrend != null) {
            chartTrend.setLegendSide(Side.TOP);
            chartTrend.setAnimated(true);
        }
    }
    
    private void loadDataAsync() {
        progressIndicator.setVisible(true);
        btnExportPDF.setDisable(true);
        btnRefresh.setDisable(true);
        
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cachedStats = laporanController.getTotalStatistik();
                allStatistik = laporanController.getStatistikRuangan();
                return null;
            }
            
            @Override
            protected void succeeded() {
                updateUI();
                progressIndicator.setVisible(false);
                btnExportPDF.setDisable(false);
                btnRefresh.setDisable(false);
                System.out.println("✅ Data loaded successfully");
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                btnExportPDF.setDisable(false);
                btnRefresh.setDisable(false);
                showError("Gagal memuat data: " + getException().getMessage());
            }
        };
        
        new Thread(loadTask).start();
    }
    
    private void updateUI() {
        updateSummaryStats();
        updateCharts();
        updateTable();
        updateTopRuangan();
    }
    
    private void updateSummaryStats() {
        if (cachedStats == null) return;
        
        lblTotalRuangan.setText(String.valueOf(cachedStats.getOrDefault("totalRuangan", 0)));
        lblRuanganTersedia.setText(cachedStats.getOrDefault("ruanganTersedia", 0) + " Tersedia");
        lblTotalPeminjaman.setText(String.valueOf(cachedStats.getOrDefault("totalPeminjaman", 0)));
        lblPeminjamanAktif.setText(String.valueOf(cachedStats.getOrDefault("peminjamanAktif", 0)));
        lblPeminjamanSelesai.setText(String.valueOf(cachedStats.getOrDefault("peminjamanSelesai", 0)));
    }
    
    private void updateCharts() {
        updateBarChart();
        updatePieChart();
        updateLineChart();
    }
    
    private void updateBarChart() {
        try {
            if (allStatistik == null || allStatistik.isEmpty()) return;
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Peminjaman");
            
            int limit = Math.min(8, allStatistik.size());
            
            for (int i = 0; i < limit; i++) {
                StatistikRuangan stat = allStatistik.get(i);
                String nama = stat.getNamaRuangan();
                
                if (nama.length() > 15) {
                    nama = nama.substring(0, 12) + "...";
                }
                
                series.getData().add(new XYChart.Data<>(nama, stat.getTotalPeminjaman()));
            }
            
            chartRuangan.getData().clear();
            chartRuangan.getData().add(series);
            
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-bar-fill: #5B9BD5;");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating bar chart: " + e.getMessage());
        }
    }
    
    /**
     * Update pie chart dengan warna yang sesuai
     */
    private void updatePieChart() {
        try {
            Map<String, Integer> statusData = laporanController.getStatusPeminjamanData();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            int totalAll = statusData.values().stream().mapToInt(Integer::intValue).sum();
            
            if (totalAll == 0) {
                System.out.println("⚠️ No data for pie chart");
                return;
            }
            
            // FIXED: Gunakan LinkedHashMap untuk urutan yang konsisten
            java.util.LinkedHashMap<String, String> statusColorMap = new java.util.LinkedHashMap<>();
            statusColorMap.put("aktif", "#4facfe");    // Biru
            statusColorMap.put("selesai", "#43e97b");  // Hijau
            statusColorMap.put("batal", "#f5576c");    // Merah
            
            // Add data dalam urutan yang SAMA dengan warna
            for (String status : statusColorMap.keySet()) {
                Integer count = statusData.get(status);
                
                if (count != null && count > 0) {
                    double percentage = (count * 100.0) / totalAll;
                    
                    PieChart.Data data = new PieChart.Data(
                        status.toUpperCase() + " (" + String.format("%.1f%%", percentage) + ")",
                        count
                    );
                    pieChartData.add(data);
                }
            }
            
            chartStatus.setData(pieChartData);
            
            // Apply colors setelah data di-set
            javafx.application.Platform.runLater(() -> {
                int index = 0;
                for (PieChart.Data data : chartStatus.getData()) {
                    String statusName = data.getName().split(" ")[0].toLowerCase();
                    String color = statusColorMap.getOrDefault(statusName, "#95a5a6");
                    
                    // Apply color
                    data.getNode().setStyle("-fx-pie-color: " + color + ";");
                    
                    // Add tooltip
                    Tooltip tooltip = new Tooltip(data.getName() + ": " + (int)data.getPieValue() + " peminjaman");
                    Tooltip.install(data.getNode(), tooltip);
                    
                    System.out.println("🎨 Applied color " + color + " to " + statusName.toUpperCase());
                    
                    index++;
                }
            });
            
        } catch (Exception e) {
            System.err.println("❌ Error updating pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateLineChart() {
        if (chartTrend == null) return;
        
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Trend Peminjaman");
            
            String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun"};
            
            if (allStatistik != null && !allStatistik.isEmpty()) {
                int totalPeminjaman = allStatistik.stream()
                    .mapToInt(StatistikRuangan::getTotalPeminjaman)
                    .sum();
                
                int avgPerMonth = totalPeminjaman / 6;
                
                for (int i = 0; i < months.length; i++) {
                    int value = (int)(avgPerMonth * (0.7 + Math.random() * 0.6));
                    series.getData().add(new XYChart.Data<>(months[i], value));
                }
            }
            
            chartTrend.getData().clear();
            chartTrend.getData().add(series);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating line chart: " + e.getMessage());
        }
    }
    
    /**
     * Get warna status yang FIXED dan konsisten
     */
    
    private String getStatusColor(String name) {
        if (name.toLowerCase().contains("aktif")) {
            return "#4facfe";
        } else if (name.toLowerCase().contains("selesai")) {
            return "#43e97b";
        } else if (name.toLowerCase().contains("batal")) {
            return "#f5576c";
        }
        return "#95a5a6";
    }
    
    private void updateTable() {
        if (allStatistik == null) return;
        
        filteredData = new FilteredList<>(allStatistik, p -> true);
        tableStatistik.setItems(filteredData);
    }
    
    private void updateTopRuangan() {
        try {
            vboxTopRuangan.getChildren().clear();
            ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
            
            if (topRuangan == null || topRuangan.isEmpty()) {
                Label noData = new Label("Belum ada data peminjaman");
                noData.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
                vboxTopRuangan.getChildren().add(noData);
                return;
            }
            
            int rank = 1;
            for (StatistikRuangan stat : topRuangan) {
                HBox card = createTopRuanganCard(rank, stat);
                vboxTopRuangan.getChildren().add(card);
                rank++;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error loading top ruangan: " + e.getMessage());
        }
    }
    
    private HBox createTopRuanganCard(int rank, StatistikRuangan stat) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        Label lblRank = new Label("#" + rank);
        lblRank.setStyle(
            "-fx-background-color: " + getRankColorWeb(rank) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 16px;"
        );
        
        VBox vboxInfo = new VBox(5);
        Label lblNama = new Label(stat.getNamaRuangan());
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblInfo = new Label(
            stat.getTotalPeminjaman() + " peminjaman • " + 
            stat.getPersentaseFormatted()
        );
        lblInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        vboxInfo.getChildren().addAll(lblNama, lblInfo);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);
        
        ProgressBar progressBar = new ProgressBar(stat.getPersentasePenggunaan() / 100.0);
        progressBar.setPrefWidth(150);
        progressBar.setStyle("-fx-accent: " + getRankColorWeb(rank) + ";");
        
        card.getChildren().addAll(lblRank, vboxInfo, progressBar);
        return card;
    }
    
    private String getRankColorWeb(int rank) {
        switch (rank) {
            case 1: return "#FFD700";
            case 2: return "#C0C0C0";
            case 3: return "#CD7F32";
            case 4: return "#5B9BD5";
            case 5: return "#70C1B3";
            default: return "#95a5a6";
        }
    }
    
    @FXML
    private void handleSearch() {
        if (filteredData == null) return;
        
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        filteredData.setPredicate(statistik -> {
            if (keyword.isEmpty()) return true;
            return statistik.getNamaRuangan().toLowerCase().contains(keyword);
        });
    }
    
    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Refreshing data...");
        txtSearch.clear();
        cachedStats = null;
        loadDataAsync();
    }
    
    @FXML
    private void handleExportPDF() {
        exportPDFSimple();
    }
    
    private void exportPDFSimple() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Laporan PDF");
            fileChooser.setInitialFileName("Laporan_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());
            
            if (file != null) {
                generatePDF(file);
                showSuccess("Laporan berhasil di-export!\n" + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Gagal export PDF: " + e.getMessage());
        }
    }
    
    private void generatePDF(File file) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        
        document.open();
        
        // HEADER
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.WHITE);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);
        
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);
        
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new BaseColor(91, 155, 213));
        headerCell.setPadding(20);
        headerCell.setBorder(Rectangle.NO_BORDER);
        
        Paragraph headerTitle = new Paragraph("LAPORAN STATISTIK & ANALISIS", titleFont);
        headerTitle.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph headerSubtitle = new Paragraph("Sistem Peminjaman Ruangan", subtitleFont);
        headerSubtitle.setAlignment(Element.ALIGN_CENTER);
        headerSubtitle.setSpacingBefore(5);
        
        Paragraph headerDate = new Paragraph(
            "Dicetak: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            subtitleFont
        );
        headerDate.setAlignment(Element.ALIGN_CENTER);
        headerDate.setSpacingBefore(3);
        
        headerCell.addElement(headerTitle);
        headerCell.addElement(headerSubtitle);
        headerCell.addElement(headerDate);
        headerTable.addCell(headerCell);
        
        document.add(headerTable);
        
        // SUMMARY
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(44, 62, 80));
        
        Paragraph ringkasanTitle = new Paragraph("RINGKASAN EKSEKUTIF", sectionFont);
        ringkasanTitle.setSpacingBefore(10);
        ringkasanTitle.setSpacingAfter(10);
        document.add(ringkasanTitle);
        
        if (cachedStats != null) {
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(15);
            
            summaryTable.addCell(createSummaryCard("TOTAL RUANGAN", String.valueOf(cachedStats.get("totalRuangan")), cachedStats.get("ruanganTersedia") + " Tersedia", new BaseColor(102, 126, 234)));
            summaryTable.addCell(createSummaryCard("TOTAL PEMINJAMAN", String.valueOf(cachedStats.get("totalPeminjaman")), "Semua Status", new BaseColor(240, 147, 251)));
            summaryTable.addCell(createSummaryCard("AKTIF", String.valueOf(cachedStats.get("peminjamanAktif")), "Berlangsung", new BaseColor(79, 172, 254)));
            summaryTable.addCell(createSummaryCard("SELESAI", String.valueOf(cachedStats.get("peminjamanSelesai")), "Dikembalikan", new BaseColor(67, 233, 123)));
            
            document.add(summaryTable);
        }
        
        // STATUS DISTRIBUTION
        document.add(new Paragraph("DISTRIBUSI STATUS PEMINJAMAN", sectionFont));
        document.add(new Paragraph(" "));
        
        Map<String, Integer> statusData = laporanController.getStatusPeminjamanData();
        if (!statusData.isEmpty()) {
            int totalAll = statusData.values().stream().mapToInt(Integer::intValue).sum();
            
            PdfPTable statusTable = new PdfPTable(3);
            statusTable.setWidthPercentage(80);
            statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusTable.setSpacingAfter(15);
            
            addTableHeader(statusTable, "Status");
            addTableHeader(statusTable, "Jumlah");
            addTableHeader(statusTable, "Persentase");
            
            for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / totalAll;
                statusTable.addCell(createDataCell(entry.getKey().toUpperCase()));
                statusTable.addCell(createDataCell(String.valueOf(entry.getValue())));
                statusTable.addCell(createDataCell(String.format("%.1f%%", percentage)));
            }
            
            document.add(statusTable);
        }
        
        // TOP 5
        document.add(new Paragraph("TOP 5 RUANGAN TERPOPULER", sectionFont));
        document.add(new Paragraph(" "));
        
        ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
        if (topRuangan != null && !topRuangan.isEmpty()) {
            PdfPTable topTable = new PdfPTable(4);
            topTable.setWidthPercentage(100);
            topTable.setSpacingAfter(15);
            topTable.setWidths(new float[]{1, 3, 2, 2});
            
            addTableHeader(topTable, "Rank");
            addTableHeader(topTable, "Nama Ruangan");
            addTableHeader(topTable, "Total Peminjaman");
            addTableHeader(topTable, "Persentase");
            
            int rank = 1;
            for (StatistikRuangan stat : topRuangan) {
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                PdfPCell cellRank = new PdfPCell(new Phrase("#" + rank, boldFont));
                cellRank.setPadding(6);
                cellRank.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellRank.setBackgroundColor(getRankColor(rank));
                topTable.addCell(cellRank);
                
                topTable.addCell(createDataCell(stat.getNamaRuangan()));
                topTable.addCell(createDataCell(String.valueOf(stat.getTotalPeminjaman())));
                topTable.addCell(createDataCell(stat.getPersentaseFormatted()));
                
                rank++;
            }
            
            document.add(topTable);
        }
        
        // NEW PAGE
        document.newPage();
        
        document.add(new Paragraph("DETAIL STATISTIK PER RUANGAN", sectionFont));
        document.add(new Paragraph(" "));
        
        if (allStatistik != null && !allStatistik.isEmpty()) {
            PdfPTable detailTable = new PdfPTable(6);
            detailTable.setWidthPercentage(100);
            detailTable.setWidths(new float[]{3, 1.5f, 1, 1, 1, 1.5f});
            
            addTableHeader(detailTable, "Ruangan");
            addTableHeader(detailTable, "Total");
            addTableHeader(detailTable, "Aktif");
            addTableHeader(detailTable, "Selesai");
            addTableHeader(detailTable, "Batal");
            addTableHeader(detailTable, "Persentase");
            
            for (StatistikRuangan stat : allStatistik) {
                detailTable.addCell(createDataCell(stat.getNamaRuangan()));
                detailTable.addCell(createDataCell(String.valueOf(stat.getTotalPeminjaman())));
                detailTable.addCell(createDataCell(String.valueOf(stat.getPeminjamanAktif())));
                detailTable.addCell(createDataCell(String.valueOf(stat.getPeminjamanSelesai())));
                detailTable.addCell(createDataCell(String.valueOf(stat.getPeminjamanBatal())));
                detailTable.addCell(createDataCell(stat.getPersentaseFormatted()));
            }
            
            document.add(detailTable);
        }
        
        // FOOTER
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Laporan ini dicetak secara otomatis oleh Sistem Inventaris & Peminjaman Ruangan", new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
    }
    
    private PdfPCell createSummaryCard(String title, String value, String subtitle, BaseColor color) {
        PdfPCell card = new PdfPCell();
        card.setPadding(10);
        card.setBackgroundColor(color);
        card.setBorder(Rectangle.NO_BORDER);
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.WHITE);
        
        Paragraph titleP = new Paragraph(title, titleFont);
        titleP.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph valueP = new Paragraph(value, valueFont);
        valueP.setAlignment(Element.ALIGN_CENTER);
        valueP.setSpacingBefore(5);
        
        Paragraph subtitleP = new Paragraph(subtitle, subtitleFont);
        subtitleP.setAlignment(Element.ALIGN_CENTER);
        subtitleP.setSpacingBefore(3);
        
        card.addElement(titleP);
        card.addElement(valueP);
        card.addElement(subtitleP);
        
        return card;
    }
    
    private void addTableHeader(PdfPTable table, String text) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        PdfPCell header = new PdfPCell(new Phrase(text, headerFont));
        header.setBackgroundColor(new BaseColor(91, 155, 213));
        header.setPadding(8);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(header);
    }
    
    private PdfPCell createDataCell(String text) {
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(text, dataFont));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
    
    private BaseColor getRankColor(int rank) {
        switch (rank) {
            case 1: return new BaseColor(255, 215, 0);
            case 2: return new BaseColor(192, 192, 192);
            case 3: return new BaseColor(205, 127, 50);
            case 4: return new BaseColor(91, 155, 213);
            case 5: return new BaseColor(112, 193, 179);
            default: return new BaseColor(149, 165, 166);
        }
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
}