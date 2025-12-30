package com.sistemruangan.test;

import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Ruangan;
import javafx.collections.ObservableList;

public class TestRuanganController {
    public static void main(String[] args) {
        System.out.println("TESTING RuanganController...");
        
        RuanganController controller = new RuanganController();
        try {
            ObservableList<Ruangan> list = controller.getAllRuangan();
            System.out.println("✅ getAllRuangan() executed successfully.");
            System.out.println("   Items found: " + list.size());
            for (Ruangan r : list) {
                System.out.println("   - " + r.getNamaRuangan() + " (Lantai " + r.getLantai() + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ Error calling getAllRuangan():");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
