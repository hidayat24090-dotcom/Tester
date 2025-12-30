package com.sistemruangan.controller;

import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD Peminjaman - WITH APPROVAL SYSTEM
 */
public class PeminjamanController {
    
    /**
     * Tambah peminjaman baru (dengan approval untuk non-kuliah)
     */
    public boolean tambahPeminjaman(Peminjaman peminjaman) {
        if (!validatePeminjaman(peminjaman)) {
            System.err.println("Data peminjaman tidak valid");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Cek ruangan tersedia
            String checkQuery = "SELECT status FROM ruangan WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, peminjaman.getIdRuangan());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    String currentStatus = rs.getString("status");
                    if (!"tersedia".equalsIgnoreCase(currentStatus)) {
                        System.err.println("Ruangan tidak tersedia");
                        return false;
                    }
                } else {
                    System.err.println("Ruangan tidak ditemukan");
                    return false;
                }
            }
            
            conn.setAutoCommit(false);
            
            // Tentukan status approval
            String statusApproval = peminjaman.isKuliah() ? "approved" : "pending";
            
            // Insert peminjaman dengan approval
            String query = "INSERT INTO peminjaman (id_ruangan, nama_peminjam, keperluan, " +
                          "jenis_kegiatan, penjelasan_kegiatan, surat_path, " +
                          "tanggal_pinjam, tanggal_kembali, jam_mulai, jam_selesai, status_peminjaman, status_approval) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, peminjaman.getIdRuangan());
            pstmt.setString(2, peminjaman.getNamaPeminjam());
            pstmt.setString(3, peminjaman.getKeperluan());
            pstmt.setString(4, peminjaman.getJenisKegiatan());
            pstmt.setString(5, peminjaman.getPenjelasanKegiatan());
            pstmt.setString(6, peminjaman.getSuratPath());
            pstmt.setDate(7, Date.valueOf(peminjaman.getTanggalPinjam()));
            pstmt.setDate(8, Date.valueOf(peminjaman.getTanggalKembali()));
            pstmt.setTime(9, Time.valueOf(peminjaman.getJamMulai()));
            pstmt.setTime(10, Time.valueOf(peminjaman.getJamSelesai()));
            pstmt.setString(11, peminjaman.getStatusPeminjaman());
            pstmt.setString(12, statusApproval);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Hanya update status ruangan jika kegiatan kuliah (langsung approved)
                if (peminjaman.isKuliah()) {
                    String updateQuery = "UPDATE ruangan SET status = 'dipinjam' WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, peminjaman.getIdRuangan());
                        updateStmt.executeUpdate();
                    }
                }
                
                conn.commit();
                System.out.println("Peminjaman berhasil ditambahkan (Status: " + statusApproval + ")");
                return true;
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error menambah peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get pending approvals (untuk admin)
     */
    public ObservableList<Peminjaman> getPendingApprovals() {
        ObservableList<Peminjaman> pendingList = FXCollections.observableArrayList();
        String query = "SELECT p.*, r.nama_ruangan FROM peminjaman p " +
                      "JOIN ruangan r ON p.id_ruangan = r.id " +
                      "WHERE p.status_approval = 'pending' " +
                      "ORDER BY p.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                pendingList.add(peminjaman);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil pending approvals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pendingList;
    }
    
    /**
     * Get jumlah pending approvals
     */
    public int getPendingApprovalsCount() {
        String query = "SELECT COUNT(*) as total FROM peminjaman WHERE status_approval = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung pending approvals: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Approve peminjaman
     */
    public boolean approvePeminjaman(int idPeminjaman, int idRuangan, String approvedBy, String keterangan) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update status approval
            String updateQuery = "UPDATE peminjaman SET status_approval = 'approved', " +
                               "keterangan_approval = ?, approved_by = ?, approved_at = NOW() " +
                               "WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, keterangan);
                pstmt.setString(2, approvedBy);
                pstmt.setInt(3, idPeminjaman);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Update status ruangan jadi dipinjam
                    String updateRuangan = "UPDATE ruangan SET status = 'dipinjam' WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRuangan)) {
                        updateStmt.setInt(1, idRuangan);
                        updateStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    System.out.println("Peminjaman approved by: " + approvedBy);
                    return true;
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error approve peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reject peminjaman
     */
    public boolean rejectPeminjaman(int idPeminjaman, String approvedBy, String keterangan) {
        String query = "UPDATE peminjaman SET status_approval = 'rejected', " +
                      "keterangan_approval = ?, approved_by = ?, approved_at = NOW() " +
                      "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, keterangan);
            pstmt.setString(2, approvedBy);
            pstmt.setInt(3, idPeminjaman);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Peminjaman rejected by: " + approvedBy);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reject peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get semua peminjaman - FIXED dengan handle NULL values
     */
    public ObservableList<Peminjaman> getAllPeminjaman() {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        String query = "SELECT p.*, r.nama_ruangan FROM peminjaman p " +
                    "JOIN ruangan r ON p.id_ruangan = r.id " +
                    "ORDER BY p.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                try {
                    // Get jenis_kegiatan dengan default 'kuliah' jika NULL
                    String jenisKegiatan = rs.getString("jenis_kegiatan");
                    if (jenisKegiatan == null || jenisKegiatan.trim().isEmpty()) {
                        jenisKegiatan = "kuliah";
                    }
                    
                    // Get status_approval dengan default 'approved' jika NULL
                    String statusApproval = rs.getString("status_approval");
                    if (statusApproval == null || statusApproval.trim().isEmpty()) {
                        statusApproval = "approved";
                    }
                    
                    // Get penjelasan_kegiatan (boleh NULL)
                    String penjelasanKegiatan = rs.getString("penjelasan_kegiatan");
                    if (penjelasanKegiatan == null) {
                        penjelasanKegiatan = "";
                    }
                    
                    // Get surat_path (boleh NULL)
                    String suratPath = rs.getString("surat_path");
                    if (suratPath == null) {
                        suratPath = "";
                    }
                    
                    // Create Peminjaman object
                    Peminjaman p = new Peminjaman(
                        rs.getInt("id"),
                        rs.getInt("id_ruangan"),
                        rs.getString("nama_ruangan"),
                        rs.getString("nama_peminjam"),
                        rs.getString("keperluan"),
                        jenisKegiatan,
                        penjelasanKegiatan,
                        suratPath,
                        rs.getDate("tanggal_pinjam").toLocalDate(),
                        rs.getDate("tanggal_kembali").toLocalDate(),
                        rs.getTime("jam_mulai").toLocalTime(),
                        rs.getTime("jam_selesai").toLocalTime(),
                        rs.getString("status_peminjaman"),
                        statusApproval
                    );
                    
                    // Set additional approval info if exists
                    String keterangan = rs.getString("keterangan_approval");
                    if (keterangan != null) {
                        p.setKeteranganApproval(keterangan);
                    }
                    
                    String approvedBy = rs.getString("approved_by");
                    if (approvedBy != null) {
                        p.setApprovedBy(approvedBy);
                    }
                    
                    Timestamp approvedAt = rs.getTimestamp("approved_at");
                    if (approvedAt != null) {
                        p.setApprovedAt(approvedAt.toLocalDateTime());
                    }
                    
                    peminjamanList.add(p);
                    
                } catch (Exception e) {
                    System.err.println("❌ Error parsing row: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("✅ Loaded " + peminjamanList.size() + " peminjaman records");
            
        } catch (SQLException e) {
            System.err.println("❌ Error mengambil data peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        
        return peminjamanList;
    }
    
    /**
     * Selesaikan peminjaman
     */
    public boolean selesaikanPeminjaman(int idPeminjaman, int idRuangan) {
        return updateStatusPeminjaman(idPeminjaman, idRuangan, "selesai");
    }
    
    /**
     * Batalkan peminjaman
     */
    public boolean batalkanPeminjaman(int idPeminjaman, int idRuangan) {
        return updateStatusPeminjaman(idPeminjaman, idRuangan, "batal");
    }
    
    /**
     * Update status peminjaman
     */
    private boolean updateStatusPeminjaman(int idPeminjaman, int idRuangan, String newStatus) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String updatePeminjamanQuery = "UPDATE peminjaman SET status_peminjaman=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(updatePeminjamanQuery)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, idPeminjaman);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    String updateRuanganQuery = "UPDATE ruangan SET status='tersedia' WHERE id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRuanganQuery)) {
                        updateStmt.setInt(1, idRuangan);
                        updateStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    return true;
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error mengubah status peminjaman: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Search peminjaman
     */
    public ObservableList<Peminjaman> searchPeminjaman(String keyword) {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPeminjaman();
        }
        
        String query = "SELECT p.*, r.nama_ruangan FROM peminjaman p " +
                      "JOIN ruangan r ON p.id_ruangan = r.id " +
                      "WHERE p.nama_peminjam LIKE ? OR r.nama_ruangan LIKE ? " +
                      "ORDER BY p.id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchKeyword = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                    peminjamanList.add(peminjaman);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari peminjaman: " + e.getMessage());
        }
        
        return peminjamanList;
    }
    
    /**
     * Get peminjaman by month
     */
    public ObservableList<Peminjaman> getPeminjamanByMonth(int month, int year) {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        String query = "SELECT p.*, r.nama_ruangan FROM peminjaman p " +
                      "JOIN ruangan r ON p.id_ruangan = r.id " +
                      "WHERE MONTH(p.tanggal_pinjam) = ? AND YEAR(p.tanggal_pinjam) = ? " +
                      "ORDER BY p.tanggal_pinjam ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                    peminjamanList.add(peminjaman);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil peminjaman bulanan: " + e.getMessage());
        }
        
        return peminjamanList;
    }
    
    /**
     * Create Peminjaman from ResultSet
     */
    private Peminjaman createPeminjamanFromResultSet(ResultSet rs) throws SQLException {
        Peminjaman p = new Peminjaman(
            rs.getInt("id"),
            rs.getInt("id_ruangan"),
            rs.getString("nama_ruangan"),
            rs.getString("nama_peminjam"),
            rs.getString("keperluan"),
            rs.getString("jenis_kegiatan") != null ? rs.getString("jenis_kegiatan") : "kuliah",
            rs.getString("penjelasan_kegiatan"),
            rs.getString("surat_path"),
            rs.getDate("tanggal_pinjam").toLocalDate(),
            rs.getDate("tanggal_kembali").toLocalDate(),
            rs.getTime("jam_mulai").toLocalTime(),
            rs.getTime("jam_selesai").toLocalTime(),
            rs.getString("status_peminjaman"),
            rs.getString("status_approval") != null ? rs.getString("status_approval") : "approved"
        );
        
        // Set additional approval info if exists
        String keterangan = rs.getString("keterangan_approval");
        if (keterangan != null) {
            p.setKeteranganApproval(keterangan);
        }
        
        String approvedBy = rs.getString("approved_by");
        if (approvedBy != null) {
            p.setApprovedBy(approvedBy);
        }
        
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            p.setApprovedAt(approvedAt.toLocalDateTime());
        }
        
        return p;
    }
    
    /**
     * Validasi peminjaman
     */
    private boolean validatePeminjaman(Peminjaman peminjaman) {
        if (peminjaman == null) return false;
        
        if (peminjaman.getIdRuangan() <= 0 || 
            peminjaman.getNamaPeminjam() == null || peminjaman.getNamaPeminjam().trim().isEmpty() ||
            peminjaman.getKeperluan() == null || peminjaman.getKeperluan().trim().isEmpty() ||
            peminjaman.getTanggalPinjam() == null || peminjaman.getTanggalKembali() == null) {
            return false;
        }
        
        return !peminjaman.getTanggalPinjam().isAfter(peminjaman.getTanggalKembali());
    }
}