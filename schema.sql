-- ========================================
--   SCHEMA DATABASE LENGKAP
--   Sistem Inventaris & Peminjaman Ruangan
-- ========================================

DROP DATABASE IF EXISTS sistem_ruangan;
CREATE DATABASE sistem_ruangan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistem_ruangan;

-- ========================================
-- 1. TABEL ADMIN
-- ========================================
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB;

-- ========================================
-- 2. TABEL USER
-- ========================================
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    no_telepon VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB;

-- ========================================
-- 3. TABEL RUANGAN (WITH PHOTO SUPPORT)
-- ========================================
CREATE TABLE gedung (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama_gedung VARCHAR(100) NOT NULL UNIQUE,
    jumlah_lantai INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nama (nama_gedung)
) ENGINE=InnoDB;

CREATE TABLE ruangan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_gedung INT,
    nama_ruangan VARCHAR(100) NOT NULL UNIQUE,
    lantai INT NOT NULL DEFAULT 1, -- Added lantai column
    jumlah_kursi INT NOT NULL,
    fasilitas TEXT,  -- Comma-separated fasilitas
    status ENUM('tersedia', 'dipinjam') DEFAULT 'tersedia',
    foto_path VARCHAR(255),  -- Path to room photo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_nama (nama_ruangan),
    INDEX idx_lantai (lantai), -- Added index for lantai
    FOREIGN KEY (id_gedung) REFERENCES gedung(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ========================================
-- 4. TABEL PEMINJAMAN (WITH APPROVAL SYSTEM)
-- ========================================
CREATE TABLE peminjaman (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_user INT,
    id_ruangan INT NOT NULL,
    nama_peminjam VARCHAR(100) NOT NULL,
    keperluan TEXT NOT NULL,
    jenis_kegiatan ENUM('kuliah', 'lainnya') DEFAULT 'kuliah',
    penjelasan_kegiatan TEXT,
    surat_path VARCHAR(255),
    tanggal_pinjam DATE NOT NULL,
    tanggal_kembali DATE NOT NULL,
    jam_mulai TIME NOT NULL DEFAULT '08:00:00', -- Ditambahkan agar sesuai plan
    jam_selesai TIME NOT NULL DEFAULT '16:00:00', -- Ditambahkan agar sesuai plan
    status_peminjaman ENUM('aktif', 'selesai', 'batal') DEFAULT 'aktif',
    status_approval ENUM('pending', 'approved', 'rejected') DEFAULT 'approved',
    keterangan_approval TEXT,
    approved_by VARCHAR(100),
    approved_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (id_ruangan) REFERENCES ruangan(id) ON DELETE CASCADE,
    INDEX idx_tanggal (tanggal_pinjam, tanggal_kembali),
    INDEX idx_status_peminjaman (status_peminjaman),
    INDEX idx_status_approval (status_approval),
    INDEX idx_jenis_kegiatan (jenis_kegiatan)
) ENGINE=InnoDB;

-- ========================================
-- 5. VIEWS UNTUK STATISTIK
-- ========================================

-- View: Statistik Ruangan
CREATE OR REPLACE VIEW v_statistik_ruangan AS
SELECT 
    r.id,
    r.nama_ruangan,
    COUNT(p.id) as total_peminjaman,
    SUM(CASE WHEN p.status_peminjaman = 'aktif' THEN 1 ELSE 0 END) as peminjaman_aktif,
    SUM(CASE WHEN p.status_peminjaman = 'selesai' THEN 1 ELSE 0 END) as peminjaman_selesai,
    SUM(CASE WHEN p.status_peminjaman = 'batal' THEN 1 ELSE 0 END) as peminjaman_batal,
    ROUND((COUNT(p.id) * 100.0 / NULLIF((SELECT COUNT(*) FROM peminjaman), 0)), 2) as persentase_penggunaan
FROM ruangan r
LEFT JOIN peminjaman p ON r.id = p.id_ruangan
GROUP BY r.id, r.nama_ruangan
ORDER BY total_peminjaman DESC;

-- View: Statistik Bulanan
CREATE OR REPLACE VIEW v_statistik_bulanan AS
SELECT 
    YEAR(p.tanggal_pinjam) as tahun,
    MONTH(p.tanggal_pinjam) as bulan,
    MONTHNAME(p.tanggal_pinjam) as nama_bulan,
    COUNT(*) as total_peminjaman
FROM peminjaman p
GROUP BY tahun, bulan, nama_bulan
ORDER BY tahun DESC, bulan DESC;

-- View: Peminjaman Bulanan per Ruangan
CREATE OR REPLACE VIEW v_peminjaman_bulanan AS
SELECT 
    YEAR(p.tanggal_pinjam) as tahun,
    MONTH(p.tanggal_pinjam) as bulan,
    MONTHNAME(p.tanggal_pinjam) as nama_bulan,
    TIME(p.tanggal_pinjam) as waktu_pinjam,
    r.nama_ruangan,
    COUNT(*) as jumlah_peminjaman,
    SUM(CASE WHEN p.status_peminjaman = 'aktif' THEN 1 ELSE 0 END) as aktif,
    SUM(CASE WHEN p.status_peminjaman = 'selesai' THEN 1 ELSE 0 END) as selesai,
    SUM(CASE WHEN p.status_peminjaman = 'batal' THEN 1 ELSE 0 END) as batal
FROM peminjaman p
JOIN ruangan r ON p.id_ruangan = r.id
GROUP BY tahun, bulan, nama_bulan, r.nama_ruangan
ORDER BY tahun DESC, bulan DESC;

-- View: Ruangan Populer
CREATE OR REPLACE VIEW v_ruangan_populer AS
SELECT 
    r.id, r.nama_ruangan,
    COUNT(p.id) as total_peminjaman,
    SUM(CASE WHEN p.status_peminjaman = 'selesai' THEN 1 ELSE 0 END) as berhasil,
    ROUND((COUNT(p.id) * 100.0 / NULLIF((SELECT COUNT(*) FROM peminjaman), 0)), 2) as persentase
FROM ruangan r
LEFT JOIN peminjaman p ON r.id = p.id_ruangan
GROUP BY r.id, r.nama_ruangan
ORDER BY total_peminjaman DESC;

-- ========================================
-- 6. DATA DUMMY - ADMIN
-- ========================================
INSERT INTO admin (username, password, nama_lengkap) VALUES 
('admin', 'admin123', 'Administrator Sistem'),
('admin2', 'admin456', 'Super Admin');

-- ========================================
-- 7. DATA DUMMY - USER
-- ========================================
INSERT INTO user (username, password, nama_lengkap, email, no_telepon) VALUES 
('budi', 'user123', 'Budi Santoso', 'budi.santoso@email.com', '081234567890'),
('siti', 'user123', 'Siti Nurhaliza', 'siti.nur@email.com', '082345678901'),
('ahmad', 'user123', 'Ahmad Rizki', 'ahmad.rizki@email.com', '083456789012'),
('rina', 'user123', 'Rina Kusuma', 'rina.kusuma@email.com', '084567890123'),
('doni', 'user123', 'Doni Prasetyo', 'doni.prasetyo@email.com', '085678901234');

-- ========================================
-- 8. DATA DUMMY - GEDUNG
-- ========================================
INSERT INTO gedung (nama_gedung, jumlah_lantai) VALUES 
('Gedung A', 4),
('Gedung B', 3),
('Gedung C', 5),
('Gedung D', 5),
('Gedung E', 4);
-- ========================================
-- 9. DATA DUMMY - RUANGAN
-- ========================================
INSERT INTO ruangan (id_gedung, nama_ruangan, lantai, jumlah_kursi, fasilitas, status, foto_path) VALUES 
(1, 'Ruang A101', 1, 30, 'Proyektor (baik), HDMI (baik), AC, Whiteboard, Kursi Lipat', 'tersedia', NULL),
(1, 'Ruang A201', 2, 25, 'Proyektor (baik), HDMI (baik), AC, Whiteboard', 'tersedia', NULL),
(2, 'Ruang B101', 1, 40, 'Proyektor (rusak), HDMI (baik), AC, Whiteboard, Sound System', 'tersedia', NULL),
(2, 'Ruang B202', 2, 35, 'Proyektor (baik), HDMI (baik), AC, Whiteboard', 'dipinjam', NULL),
(3, 'Ruang Seminar 1', 1, 100, 'Proyektor (baik), HDMI (baik), AC, Sound System, Mic Wireless, Kursi Auditorium', 'tersedia', NULL),
(3, 'Ruang Seminar 2', 2, 80, 'Proyektor (baik), HDMI (baik), AC, Sound System, Kursi Auditorium', 'tersedia', NULL),
(4, 'Lab Komputer 1', 1, 40, 'Proyektor (baik), HDMI (baik), AC, 40 Unit PC, Whiteboard', 'tersedia', NULL),
(4, 'Lab Komputer 2', 2, 35, 'Proyektor (baik), HDMI (baik), AC, 35 Unit PC, Whiteboard', 'tersedia', NULL),
(5, 'Aula Utama', 1, 200, 'Proyektor (baik), HDMI (baik), AC Central, Sound System Premium, Panggung, Lighting', 'tersedia', NULL),
(5, 'Ruang Rapat Kecil', 2, 12, 'TV LED, HDMI (baik), AC, Meja Meeting Oval', 'tersedia', NULL);

-- ========================================
-- 9. DATA DUMMY - PEMINJAMAN (MIXED DATA)
-- ========================================

-- Peminjaman KULIAH (auto-approved)
INSERT INTO peminjaman (id_user, id_ruangan, nama_peminjam, keperluan, jenis_kegiatan, penjelasan_kegiatan, surat_path, tanggal_pinjam, tanggal_kembali, status_peminjaman, status_approval) VALUES 
(1, 1, 'Budi Santoso', 'Kuliah Pemrograman Web', 'kuliah', NULL, NULL, '2024-12-20', '2024-12-20', 'selesai', 'approved'),
(2, 2, 'Siti Nurhaliza', 'Kuliah Basis Data', 'kuliah', NULL, NULL, '2024-12-21', '2024-12-21', 'selesai', 'approved'),
(3, 7, 'Ahmad Rizki', 'Praktikum Jaringan Komputer', 'kuliah', NULL, NULL, '2024-12-22', '2024-12-22', 'selesai', 'approved'),
(1, 3, 'Budi Santoso', 'Kuliah Sistem Operasi', 'kuliah', NULL, NULL, '2024-12-23', '2024-12-23', 'selesai', 'approved'),
(4, 4, 'Rina Kusuma', 'Kuliah Algoritma Pemrograman', 'kuliah', NULL, NULL, '2024-12-24', '2024-12-24', 'aktif', 'approved'),
(2, 1, 'Siti Nurhaliza', 'Kuliah Matematika Diskrit', 'kuliah', NULL, NULL, '2024-12-26', '2024-12-26', 'aktif', 'approved'),
(5, 8, 'Doni Prasetyo', 'Praktikum Pemrograman Mobile', 'kuliah', NULL, NULL, '2024-12-27', '2024-12-27', 'aktif', 'approved');

-- Peminjaman NON-KULIAH (dengan approval - APPROVED)
INSERT INTO peminjaman (id_user, id_ruangan, nama_peminjam, keperluan, jenis_kegiatan, penjelasan_kegiatan, surat_path, tanggal_pinjam, tanggal_kembali, status_peminjaman, status_approval, approved_by, approved_at, keterangan_approval) VALUES 
(3, 5, 'Ahmad Rizki', 'Seminar Teknologi AI', 'lainnya', 'Seminar tentang perkembangan AI dan Machine Learning untuk mahasiswa informatika. Dihadiri oleh 80 peserta dengan pembicara dari industri.', 'surat_1234567890.pdf', '2024-12-18', '2024-12-18', 'selesai', 'approved', 'Administrator Sistem', '2024-12-15 10:30:00', 'Disetujui. Pastikan ruangan dibersihkan setelah acara.'),
(4, 9, 'Rina Kusuma', 'Workshop Desain Grafis', 'lainnya', 'Workshop desain grafis menggunakan Adobe Creative Suite untuk mahasiswa DKV. Peserta 150 orang dengan instruktur profesional dari studio desain ternama.', 'surat_1234567891.pdf', '2024-12-25', '2024-12-25', 'aktif', 'approved', 'Administrator Sistem', '2024-12-20 14:15:00', 'Disetujui. Silakan koordinasi dengan bagian IT untuk setup proyektor.'),
(5, 6, 'Doni Prasetyo', 'Rapat ORMAWA', 'lainnya', 'Rapat koordinasi organisasi mahasiswa untuk perencanaan kegiatan semester depan. Dihadiri oleh pengurus dari 10 organisasi kemahasiswaan.', 'surat_1234567892.pdf', '2024-12-28', '2024-12-28', 'aktif', 'approved', 'Administrator Sistem', '2024-12-22 09:00:00', 'Disetujui untuk kegiatan internal kampus.');

-- Peminjaman NON-KULIAH (PENDING - Menunggu Approval)
INSERT INTO peminjaman (id_user, id_ruangan, nama_peminjam, keperluan, jenis_kegiatan, penjelasan_kegiatan, surat_path, tanggal_pinjam, tanggal_kembali, status_peminjaman, status_approval) VALUES 
(1, 5, 'Budi Santoso', 'Seminar Kewirausahaan', 'lainnya', 'Seminar kewirausahaan digital untuk mahasiswa fakultas ekonomi. Menghadirkan pengusaha sukses sebagai pembicara. Estimasi peserta 100 orang.', 'surat_1234567893.pdf', '2025-01-05', '2025-01-05', 'aktif', 'pending'),
(2, 9, 'Siti Nurhaliza', 'Lomba Debat Antar Fakultas', 'lainnya', 'Kompetisi debat bahasa Inggris tingkat universitas. Peserta 16 tim dari berbagai fakultas. Membutuhkan sound system dan panggung untuk juri.', 'surat_1234567894.pdf', '2025-01-10', '2025-01-10', 'aktif', 'pending'),
(3, 6, 'Ahmad Rizki', 'Pelatihan Public Speaking', 'lainnya', 'Workshop public speaking untuk meningkatkan kemampuan presentasi mahasiswa. Trainer profesional dari lembaga pelatihan nasional. Target peserta 60 orang.', 'surat_1234567895.pdf', '2025-01-12', '2025-01-12', 'aktif', 'pending');

-- Peminjaman NON-KULIAH (REJECTED)
INSERT INTO peminjaman (id_user, id_ruangan, nama_peminjam, keperluan, jenis_kegiatan, penjelasan_kegiatan, surat_path, tanggal_pinjam, tanggal_kembali, status_peminjaman, status_approval, approved_by, approved_at, keterangan_approval) VALUES 
(4, 9, 'Rina Kusuma', 'Konser Musik Kampus', 'lainnya', 'Acara konser musik untuk hiburan mahasiswa dengan menghadirkan band lokal.', 'surat_1234567896.pdf', '2024-12-30', '2024-12-30', 'aktif', 'rejected', 'Administrator Sistem', '2024-12-23 11:00:00', 'Ditolak. Aula sedang direnovasi untuk persiapan wisuda. Silakan ajukan kembali bulan depan.');

-- Peminjaman BATAL
INSERT INTO peminjaman (id_user, id_ruangan, nama_peminjam, keperluan, jenis_kegiatan, penjelasan_kegiatan, surat_path, tanggal_pinjam, tanggal_kembali, status_peminjaman, status_approval) VALUES 
(5, 2, 'Doni Prasetyo', 'Kuliah Pengganti', 'kuliah', NULL, NULL, '2024-12-19', '2024-12-19', 'batal', 'approved'),
(1, 10, 'Budi Santoso', 'Meeting Proyek', 'lainnya', 'Meeting tim proyek akhir untuk koordinasi pengerjaan.', 'surat_1234567897.pdf', '2024-12-17', '2024-12-17', 'batal', 'approved');

-- ========================================
-- 10. SUMMARY QUERY (untuk testing)
-- ========================================

-- Query untuk mengecek data
SELECT 'ADMIN COUNT' as info, COUNT(*) as jumlah FROM admin
UNION ALL
SELECT 'USER COUNT', COUNT(*) FROM user 
UNION ALL
SELECT 'RUANGAN COUNT', COUNT(*) FROM ruangan
UNION ALL
SELECT 'PEMINJAMAN COUNT', COUNT(*) FROM peminjaman
UNION ALL
SELECT 'PEMINJAMAN KULIAH', COUNT(*) FROM peminjaman WHERE jenis_kegiatan = 'kuliah'
UNION ALL
SELECT 'PEMINJAMAN NON-KULIAH', COUNT(*) FROM peminjaman WHERE jenis_kegiatan = 'lainnya'
UNION ALL
SELECT 'PENDING APPROVAL', COUNT(*) FROM peminjaman WHERE status_approval = 'pending'
UNION ALL
SELECT 'APPROVED', COUNT(*) FROM peminjaman WHERE status_approval = 'approved'
UNION ALL
SELECT 'REJECTED', COUNT(*) FROM peminjaman WHERE status_approval = 'rejected';

-- SELESAI
SELECT '✅ Database schema created successfully!' as status;