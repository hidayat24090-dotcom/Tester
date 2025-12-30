package com.sistemruangan.view;

import com.sistemruangan.model.Peminjaman;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dialog untuk menampilkan dan print bukti peminjaman
 * Package: view (karena ini UI component)
 */
public class BuktiPeminjamanDialog {
    
    private static String generateKodePeminjaman() {
        LocalDateTime now = LocalDateTime.now();
        return "PM-" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * Show bukti peminjaman dengan opsi print dan export
     */
    public static void showBuktiPeminjaman(Peminjaman peminjaman) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Bukti Peminjaman Ruangan");
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(500, 700);
        
        // Create receipt content
        VBox receiptContent = createReceiptContent(peminjaman);
        
        // Wrap in ScrollPane
        ScrollPane scrollPane = new ScrollPane(receiptContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");
        
        dialog.getDialogPane().setContent(scrollPane);
        
        // Custom buttons
        ButtonType btnPrint = new ButtonType("🖨️ Print", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnExport = new ButtonType("💾 Export PNG", ButtonBar.ButtonData.APPLY);
        ButtonType btnClose = new ButtonType("Tutup", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(btnPrint, btnExport, btnClose);
        
        // Handle buttons
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnPrint) {
                printReceipt(receiptContent);
            } else if (buttonType == btnExport) {
                exportReceiptToImage(receiptContent);
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Create receipt content yang cantik
     */
    private static VBox createReceiptContent(Peminjaman peminjaman) {
        VBox receipt = new VBox(15);
        receipt.setPadding(new Insets(30));
        receipt.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8edf2;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Label lblTitle = new Label("BUKTI PEMINJAMAN RUANGAN");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");
        
        Label lblSubtitle = new Label("Sistem Inventaris & Peminjaman Ruangan");
        lblSubtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        // Kode Peminjaman
        String kodePeminjaman = generateKodePeminjaman();
        Label lblKode = new Label("Kode: " + kodePeminjaman);
        lblKode.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblKode.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 20;" +
            "-fx-text-fill: #1976D2;"
        );
        
        // Timestamp
        Label lblTimestamp = new Label(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss"))
        );
        lblTimestamp.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        
        header.getChildren().addAll(lblTitle, lblSubtitle, lblKode, lblTimestamp);
        
        // Separator
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #e8edf2;");
        
        // Detail Section
        VBox detailSection = new VBox(12);
        detailSection.setPadding(new Insets(10, 0, 10, 0));
        
        // Detail items
        detailSection.getChildren().addAll(
            createDetailRow("👤 Nama Peminjam", peminjaman.getNamaPeminjam(), true),
            createDetailRow("🏢 Ruangan", peminjaman.getNamaRuangan(), true),
            createDetailRow("📋 Keperluan", peminjaman.getKeperluan(), false),
            createDetailRow("📅 Tanggal Pinjam", peminjaman.getTanggalPinjam().toString(), false),
            createDetailRow("📅 Tanggal Kembali", peminjaman.getTanggalKembali().toString(), false),
            createDetailRow("⏱️ Durasi", 
                calculateDuration(peminjaman.getTanggalPinjam().toString(), 
                                peminjaman.getTanggalKembali().toString()) + " hari", 
                false),
            createDetailRow("✅ Status", peminjaman.getStatusPeminjaman().toUpperCase(), false)
        );
        
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #e8edf2;");
        
        // Footer / Important Notes
        VBox footer = new VBox(10);
        footer.setStyle(
            "-fx-background-color: #FFF3CD;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 8;"
        );
        
        Label lblNotesTitle = new Label("⚠️ PENTING:");
        lblNotesTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblNotesTitle.setStyle("-fx-text-fill: #856404;");
        
        Label lblNotes = new Label(
            "1. Harap menjaga kebersihan dan kerapihan ruangan\n" +
            "2. Kembalikan ruangan sesuai waktu yang ditentukan\n" +
            "3. Laporkan jika ada kerusakan fasilitas\n" +
            "4. Simpan bukti ini sebagai referensi"
        );
        lblNotes.setStyle("-fx-text-fill: #856404; -fx-font-size: 11px;");
        lblNotes.setWrapText(true);
        
        footer.getChildren().addAll(lblNotesTitle, lblNotes);
        
        // QR-like Code (Barcode simulation)
        VBox barcodeSection = new VBox(5);
        barcodeSection.setAlignment(Pos.CENTER);
        
        Label lblBarcode = new Label("| | || ||| || | ||| | || | ||| || |");
        lblBarcode.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        lblBarcode.setStyle("-fx-text-fill: black;");
        
        Label lblBarcodeText = new Label(kodePeminjaman);
        lblBarcodeText.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px;");
        
        barcodeSection.getChildren().addAll(lblBarcode, lblBarcodeText);
        
        // Signature section
        HBox signatureSection = new HBox(40);
        signatureSection.setPadding(new Insets(20, 0, 0, 0));
        signatureSection.setAlignment(Pos.CENTER);
        
        VBox peminjamSign = createSignatureBox("Peminjam", peminjaman.getNamaPeminjam());
        VBox adminSign = createSignatureBox("Petugas", "________________");
        
        signatureSection.getChildren().addAll(peminjamSign, adminSign);
        
        // Add all sections
        receipt.getChildren().addAll(
            header,
            sep1,
            detailSection,
            sep2,
            footer,
            barcodeSection,
            signatureSection
        );
        
        return receipt;
    }
    
    /**
     * Create detail row
     */
    private static VBox createDetailRow(String label, String value, boolean bold) {
        VBox row = new VBox(3);
        
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        
        Label lblValue = new Label(value);
        if (bold) {
            lblValue.setFont(Font.font("System", FontWeight.BOLD, 14));
        } else {
            lblValue.setFont(Font.font("System", FontWeight.NORMAL, 13));
        }
        lblValue.setStyle("-fx-text-fill: #2c3e50;");
        lblValue.setWrapText(true);
        
        row.getChildren().addAll(lblLabel, lblValue);
        return row;
    }
    
    /**
     * Create signature box
     */
    private static VBox createSignatureBox(String role, String name) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(150);
        
        Label lblRole = new Label(role);
        lblRole.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        
        Region spacer = new Region();
        spacer.setPrefHeight(40);
        spacer.setStyle("-fx-border-color: transparent transparent #e8edf2 transparent; -fx-border-width: 0 0 1 0;");
        
        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 11px;");
        lblName.setWrapText(true);
        lblName.setTextAlignment(TextAlignment.CENTER);
        lblName.setMaxWidth(150);
        
        box.getChildren().addAll(lblRole, spacer, lblName);
        return box;
    }
    
    /**
     * Calculate duration
     */
    private static long calculateDuration(String start, String end) {
        try {
            return java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.parse(start),
                java.time.LocalDate.parse(end)
            ) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    /**
     * Print receipt
     */
    private static void printReceipt(Node content) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(content.getScene().getWindow())) {
                
                PageLayout pageLayout = job.getJobSettings().getPageLayout();
                
                boolean success = job.printPage(content);
                
                if (success) {
                    job.endJob();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Print Berhasil");
                    alert.setHeaderText(null);
                    alert.setContentText("Bukti peminjaman berhasil di-print!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Print Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal print bukti: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Export receipt to PNG
     */
    private static void exportReceiptToImage(Node content) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Bukti Peminjaman");
            fileChooser.setInitialFileName("Bukti_Peminjaman_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".png");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Images", "*.png")
            );
            
            File file = fileChooser.showSaveDialog(content.getScene().getWindow());
            
            if (file != null) {
                WritableImage writableImage = new WritableImage((int)content.getBoundsInLocal().getWidth(), 
                                                                (int)content.getBoundsInLocal().getHeight());
                content.snapshot(new SnapshotParameters(), writableImage);
                
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(bufferedImage, "png", file);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Berhasil");
                alert.setHeaderText(null);
                alert.setContentText("Bukti peminjaman berhasil disimpan!\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal export bukti: " + e.getMessage());
            alert.showAndWait();
        }
    }
}