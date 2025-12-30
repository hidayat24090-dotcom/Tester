package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model untuk Statistik Ruangan
 */
public class StatistikRuangan {
    private final IntegerProperty id;
    private final StringProperty namaRuangan;
    private final IntegerProperty totalPeminjaman;
    private final IntegerProperty peminjamanAktif;
    private final IntegerProperty peminjamanSelesai;
    private final IntegerProperty peminjamanBatal;
    private final DoubleProperty persentasePenggunaan;
    
    public StatistikRuangan() {
        this(0, "", 0, 0, 0, 0, 0.0);
    }
    
    public StatistikRuangan(int id, String namaRuangan, int totalPeminjaman, 
                           int peminjamanAktif, int peminjamanSelesai, 
                           int peminjamanBatal, double persentasePenggunaan) {
        this.id = new SimpleIntegerProperty(id);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.totalPeminjaman = new SimpleIntegerProperty(totalPeminjaman);
        this.peminjamanAktif = new SimpleIntegerProperty(peminjamanAktif);
        this.peminjamanSelesai = new SimpleIntegerProperty(peminjamanSelesai);
        this.peminjamanBatal = new SimpleIntegerProperty(peminjamanBatal);
        this.persentasePenggunaan = new SimpleDoubleProperty(persentasePenggunaan);
    }
    
    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public String getNamaRuangan() { return namaRuangan.get(); }
    public void setNamaRuangan(String value) { namaRuangan.set(value); }
    public StringProperty namaRuanganProperty() { return namaRuangan; }
    
    public int getTotalPeminjaman() { return totalPeminjaman.get(); }
    public void setTotalPeminjaman(int value) { totalPeminjaman.set(value); }
    public IntegerProperty totalPeminjamanProperty() { return totalPeminjaman; }
    
    public int getPeminjamanAktif() { return peminjamanAktif.get(); }
    public void setPeminjamanAktif(int value) { peminjamanAktif.set(value); }
    public IntegerProperty peminjamanAktifProperty() { return peminjamanAktif; }
    
    public int getPeminjamanSelesai() { return peminjamanSelesai.get(); }
    public void setPeminjamanSelesai(int value) { peminjamanSelesai.set(value); }
    public IntegerProperty peminjamanSelesaiProperty() { return peminjamanSelesai; }
    
    public int getPeminjamanBatal() { return peminjamanBatal.get(); }
    public void setPeminjamanBatal(int value) { peminjamanBatal.set(value); }
    public IntegerProperty peminjamanBatalProperty() { return peminjamanBatal; }
    
    public double getPersentasePenggunaan() { return persentasePenggunaan.get(); }
    public void setPersentasePenggunaan(double value) { persentasePenggunaan.set(value); }
    public DoubleProperty persentasePenggunaanProperty() { return persentasePenggunaan; }
    
    public String getPersentaseFormatted() {
        return String.format("%.2f%%", persentasePenggunaan.get());
    }
}