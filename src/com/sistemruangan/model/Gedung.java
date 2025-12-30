package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model class untuk Gedung
 */
public class Gedung {
    private final IntegerProperty id;
    private final StringProperty namaGedung;
    private final IntegerProperty jumlahLantai;

    public Gedung() {
        this(0, "", 0);
    }

    public Gedung(int id, String namaGedung, int jumlahLantai) {
        this.id = new SimpleIntegerProperty(id);
        this.namaGedung = new SimpleStringProperty(namaGedung);
        this.jumlahLantai = new SimpleIntegerProperty(jumlahLantai);
    }

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getNamaGedung() { return namaGedung.get(); }
    public void setNamaGedung(String value) { namaGedung.set(value); }
    public StringProperty namaGedungProperty() { return namaGedung; }

    public int getJumlahLantai() { return jumlahLantai.get(); }
    public void setJumlahLantai(int value) { jumlahLantai.set(value); }
    public IntegerProperty jumlahLantaiProperty() { return jumlahLantai; }

    @Override
    public String toString() {
        return namaGedung.get();
    }
}
