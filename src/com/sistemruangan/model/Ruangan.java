package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model class untuk Ruangan - WITH PHOTO SUPPORT
 */
public class Ruangan {
    private final IntegerProperty id;
    private final StringProperty namaRuangan;
    private final IntegerProperty jumlahKursi;
    private final StringProperty fasilitas;
    private final IntegerProperty idGedung;
    private final StringProperty namaGedung;
    private final StringProperty status;
    private final IntegerProperty lantai; // NEW: Lantai column
    private final StringProperty fotoPath; // NEW: Path to photo
    
    // Constructor
    public Ruangan() {
        this(0, 0, "", "", 1, 0, "", "tersedia", null);
    }
    
    public Ruangan(int id, int idGedung, String namaRuangan, int lantai, int jumlahKursi, 
                   String fasilitas, String status) {
        this(id, idGedung, "", namaRuangan, lantai, jumlahKursi, fasilitas, status, null);
    }
    
    public Ruangan(int id, int idGedung, String namaRuangan, int lantai, int jumlahKursi, 
                   String fasilitas, String status, String fotoPath) {
        this(id, idGedung, "", namaRuangan, lantai, jumlahKursi, fasilitas, status, fotoPath);
    }
    
    public Ruangan(int id, int idGedung, String namaGedung, String namaRuangan, int lantai, int jumlahKursi, 
                   String fasilitas, String status, String fotoPath) {
        this.id = new SimpleIntegerProperty(id);
        this.idGedung = new SimpleIntegerProperty(idGedung);
        this.namaGedung = new SimpleStringProperty(namaGedung);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.lantai = new SimpleIntegerProperty(lantai);
        this.jumlahKursi = new SimpleIntegerProperty(jumlahKursi);
        this.fasilitas = new SimpleStringProperty(fasilitas != null ? fasilitas : "");
        this.status = new SimpleStringProperty(status);
        this.fotoPath = new SimpleStringProperty(fotoPath);
    }
    
    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public int getIdGedung() { return idGedung.get(); }
    public void setIdGedung(int value) { idGedung.set(value); }
    public IntegerProperty idGedungProperty() { return idGedung; }
    
    public String getNamaGedung() { return namaGedung.get(); }
    public void setNamaGedung(String value) { namaGedung.set(value); }
    public StringProperty namaGedungProperty() { return namaGedung; }
    
    public String getNamaRuangan() { return namaRuangan.get(); }
    public void setNamaRuangan(String value) { namaRuangan.set(value); }
    public StringProperty namaRuanganProperty() { return namaRuangan; }
    
    public int getLantai() { return lantai.get(); }
    public void setLantai(int value) { lantai.set(value); }
    public IntegerProperty lantaiProperty() { return lantai; }
    
    public int getJumlahKursi() { return jumlahKursi.get(); }
    public void setJumlahKursi(int value) { jumlahKursi.set(value); }
    public IntegerProperty jumlahKursiProperty() { return jumlahKursi; }
    
    public String getFasilitas() { return fasilitas.get(); }
    public void setFasilitas(String value) { fasilitas.set(value != null ? value : ""); }
    public StringProperty fasilitasProperty() { return fasilitas; }
    
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
    
    public String getFotoPath() { return fotoPath.get(); }
    public void setFotoPath(String value) { fotoPath.set(value); }
    public StringProperty fotoPathProperty() { return fotoPath; }
    
    // Helper methods untuk fasilitas
    public String[] getFasilitasArray() {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return new String[0];
        }
        return fasilitas.get().split(",");
    }
    
    public String getFasilitasFormatted() {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return "Tidak ada";
        }
        
        String[] items = getFasilitasArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            sb.append("• ").append(items[i].trim());
            if (i < items.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    public boolean hasFasilitas(String fasilitasName) {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return false;
        }
        
        String[] items = getFasilitasArray();
        for (String item : items) {
            if (item.trim().toLowerCase().contains(fasilitasName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if ruangan has photo
     */
    public boolean hasPhoto() {
        return fotoPath.get() != null && !fotoPath.get().trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return namaRuangan.get();
    }
}