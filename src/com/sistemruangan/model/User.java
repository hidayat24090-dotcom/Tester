package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model class untuk User
 */
public class User {
    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty namaLengkap;
    private final StringProperty email;
    private final StringProperty noTelepon;
    
    // Constructor
    public User() {
        this(0, "", "", "", "", "");
    }
    
    public User(int id, String username, String password, String namaLengkap, 
                String email, String noTelepon) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.namaLengkap = new SimpleStringProperty(namaLengkap);
        this.email = new SimpleStringProperty(email);
        this.noTelepon = new SimpleStringProperty(noTelepon);
    }
    
    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }
    
    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }
    
    public String getNamaLengkap() { return namaLengkap.get(); }
    public void setNamaLengkap(String value) { namaLengkap.set(value); }
    public StringProperty namaLengkapProperty() { return namaLengkap; }
    
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }
    
    public String getNoTelepon() { return noTelepon.get(); }
    public void setNoTelepon(String value) { noTelepon.set(value); }
    public StringProperty noTeleponProperty() { return noTelepon; }
    
    @Override
    public String toString() {
        return namaLengkap.get();
    }
}