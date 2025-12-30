package com.sistemruangan.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Model class untuk Peminjaman - WITH APPROVAL SYSTEM
 */
public class Peminjaman {
    private final IntegerProperty id;
    private final IntegerProperty idRuangan;
    private final StringProperty namaRuangan;
    private final StringProperty namaPeminjam;
    private final StringProperty keperluan;
    private final StringProperty jenisKegiatan; // NEW: kuliah / non_kuliah
    private final StringProperty penjelasanKegiatan; // NEW: penjelasan untuk non_kuliah
    private final StringProperty suratPath; // NEW: path surat
    private final ObjectProperty<LocalDate> tanggalPinjam;
    private final ObjectProperty<LocalDate> tanggalKembali;
    private final ObjectProperty<LocalTime> jamMulai; 
    private final ObjectProperty<LocalTime> jamSelesai; 
    private final StringProperty statusPeminjaman;
    private final StringProperty statusApproval; // NEW: pending / approved / rejected
    private final StringProperty keteranganApproval; // NEW: keterangan dari admin
    private final StringProperty approvedBy; // NEW: admin yang approve
    private final ObjectProperty<LocalDateTime> approvedAt; // NEW: waktu approve
    
    // Constructor
    public Peminjaman() {
        this(0, 0, "", "", "", LocalDate.now(), LocalDate.now(), "aktif");
    }
    
    public Peminjaman(int id, int idRuangan, String namaRuangan, 
                      String namaPeminjam, String keperluan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembali, 
                      String statusPeminjaman) {
        this.id = new SimpleIntegerProperty(id);
        this.idRuangan = new SimpleIntegerProperty(idRuangan);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.namaPeminjam = new SimpleStringProperty(namaPeminjam);
        this.keperluan = new SimpleStringProperty(keperluan);
        this.jenisKegiatan = new SimpleStringProperty("kuliah");
        this.penjelasanKegiatan = new SimpleStringProperty("");
        this.suratPath = new SimpleStringProperty("");
        this.tanggalPinjam = new SimpleObjectProperty<>(tanggalPinjam);
        this.tanggalKembali = new SimpleObjectProperty<>(tanggalKembali);
        this.jamMulai = new SimpleObjectProperty<>(LocalTime.of(8, 0));
        this.jamSelesai = new SimpleObjectProperty<>(LocalTime.of(16, 0));
        this.statusPeminjaman = new SimpleStringProperty(statusPeminjaman);
        this.statusApproval = new SimpleStringProperty("approved");
        this.keteranganApproval = new SimpleStringProperty("");
        this.approvedBy = new SimpleStringProperty("");
        this.approvedAt = new SimpleObjectProperty<>(null);
    }
    
    // Full constructor with approval
    public Peminjaman(int id, int idRuangan, String namaRuangan, 
                      String namaPeminjam, String keperluan,
                      String jenisKegiatan, String penjelasanKegiatan, String suratPath,
                      LocalDate tanggalPinjam, LocalDate tanggalKembali, 
                      LocalTime jamMulai, LocalTime jamSelesai,
                      String statusPeminjaman, String statusApproval) {
        this.id = new SimpleIntegerProperty(id);
        this.idRuangan = new SimpleIntegerProperty(idRuangan);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.namaPeminjam = new SimpleStringProperty(namaPeminjam);
        this.keperluan = new SimpleStringProperty(keperluan);
        this.jenisKegiatan = new SimpleStringProperty(jenisKegiatan);
        this.penjelasanKegiatan = new SimpleStringProperty(penjelasanKegiatan);
        this.suratPath = new SimpleStringProperty(suratPath);
        this.tanggalPinjam = new SimpleObjectProperty<>(tanggalPinjam);
        this.tanggalKembali = new SimpleObjectProperty<>(tanggalKembali);
        this.jamMulai = new SimpleObjectProperty<>(jamMulai);
        this.jamSelesai = new SimpleObjectProperty<>(jamSelesai);
        this.statusPeminjaman = new SimpleStringProperty(statusPeminjaman);
        this.statusApproval = new SimpleStringProperty(statusApproval);
        this.keteranganApproval = new SimpleStringProperty("");
        this.approvedBy = new SimpleStringProperty("");
        this.approvedAt = new SimpleObjectProperty<>(null);
    }
    
    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public int getIdRuangan() { return idRuangan.get(); }
    public void setIdRuangan(int value) { idRuangan.set(value); }
    public IntegerProperty idRuanganProperty() { return idRuangan; }
    
    public String getNamaRuangan() { return namaRuangan.get(); }
    public void setNamaRuangan(String value) { namaRuangan.set(value); }
    public StringProperty namaRuanganProperty() { return namaRuangan; }
    
    public String getNamaPeminjam() { return namaPeminjam.get(); }
    public void setNamaPeminjam(String value) { namaPeminjam.set(value); }
    public StringProperty namaPeminjamProperty() { return namaPeminjam; }
    
    public String getKeperluan() { return keperluan.get(); }
    public void setKeperluan(String value) { keperluan.set(value); }
    public StringProperty keperluanProperty() { return keperluan; }
    
    // NEW: Jenis Kegiatan
    public String getJenisKegiatan() { return jenisKegiatan.get(); }
    public void setJenisKegiatan(String value) { jenisKegiatan.set(value); }
    public StringProperty jenisKegiatanProperty() { return jenisKegiatan; }
    
    // NEW: Penjelasan Kegiatan
    public String getPenjelasanKegiatan() { return penjelasanKegiatan.get(); }
    public void setPenjelasanKegiatan(String value) { penjelasanKegiatan.set(value); }
    public StringProperty penjelasanKegiatanProperty() { return penjelasanKegiatan; }
    
    // NEW: Surat Path
    public String getSuratPath() { return suratPath.get(); }
    public void setSuratPath(String value) { suratPath.set(value); }
    public StringProperty suratPathProperty() { return suratPath; }
    
    public LocalDate getTanggalPinjam() { return tanggalPinjam.get(); }
    public void setTanggalPinjam(LocalDate value) { tanggalPinjam.set(value); }
    public ObjectProperty<LocalDate> tanggalPinjamProperty() { return tanggalPinjam; }
    
    public LocalDate getTanggalKembali() { return tanggalKembali.get(); }
    public void setTanggalKembali(LocalDate value) { tanggalKembali.set(value); }
    public ObjectProperty<LocalDate> tanggalKembaliProperty() { return tanggalKembali; }
    
    public LocalTime getJamMulai() { return jamMulai.get(); }
    public void setJamMulai(LocalTime value) { jamMulai.set(value); }
    public ObjectProperty<LocalTime> jamMulaiProperty() { return jamMulai; }
    
    public LocalTime getJamSelesai() { return jamSelesai.get(); }
    public void setJamSelesai(LocalTime value) { jamSelesai.set(value); }
    public ObjectProperty<LocalTime> jamSelesaiProperty() { return jamSelesai; }
    
    public String getStatusPeminjaman() { return statusPeminjaman.get(); }
    public void setStatusPeminjaman(String value) { statusPeminjaman.set(value); }
    public StringProperty statusPeminjamanProperty() { return statusPeminjaman; }
    
    // NEW: Status Approval
    public String getStatusApproval() { return statusApproval.get(); }
    public void setStatusApproval(String value) { statusApproval.set(value); }
    public StringProperty statusApprovalProperty() { return statusApproval; }
    
    // NEW: Keterangan Approval
    public String getKeteranganApproval() { return keteranganApproval.get(); }
    public void setKeteranganApproval(String value) { keteranganApproval.set(value); }
    public StringProperty keteranganApprovalProperty() { return keteranganApproval; }
    
    // NEW: Approved By
    public String getApprovedBy() { return approvedBy.get(); }
    public void setApprovedBy(String value) { approvedBy.set(value); }
    public StringProperty approvedByProperty() { return approvedBy; }
    
    // NEW: Approved At
    public LocalDateTime getApprovedAt() { return approvedAt.get(); }
    public void setApprovedAt(LocalDateTime value) { approvedAt.set(value); }
    public ObjectProperty<LocalDateTime> approvedAtProperty() { return approvedAt; }
    
    // Helper methods
    public boolean isKuliah() {
        return "kuliah".equalsIgnoreCase(jenisKegiatan.get());
    }
    
    public boolean isPending() {
        return "pending".equalsIgnoreCase(statusApproval.get());
    }
    
    public boolean isApproved() {
        return "approved".equalsIgnoreCase(statusApproval.get());
    }
    
    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(statusApproval.get());
    }
    
    public String getStatusApprovalDisplay() {
        String status = statusApproval.get();
        if (status == null) return "Unknown";
        
        switch (status.toLowerCase()) {
            case "pending": return "Menunggu Persetujuan";
            case "approved": return "Disetujui";
            case "rejected": return "Ditolak";
            default: return status;
        }
    }
}