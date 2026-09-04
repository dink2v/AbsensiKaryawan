# 📱 ALUR FULL APLIKASI ABSENSI KARYAWAN

## 1. 🔐 LOGIN

### Halaman Login

User membuka aplikasi.

**Jika belum memiliki session:**
→ Tampil halaman Login
→ Input Email
→ Input Password
→ Klik **Masuk**

### Login Staff

```text
Login
  ↓
Firebase Authentication
  ↓
Cek data user di Firestore
  ↓
isAdmin = false
  ↓
Staff Dashboard
```

### Login Admin

```text
Login
  ↓
Firebase Authentication
  ↓
Cek data user di Firestore
  ↓
isAdmin = true
  ↓
Admin Dashboard
```

### Lupa Password

```text
Login
  ↓
Lupa Password
  ↓
Masukkan Email
  ↓
Kirim Reset Password
  ↓
Firebase mengirim email
  ↓
User reset password
  ↓
Kembali Login
```

---

# 2. ⏰ TIME-BASED SESSION

Aturan session aplikasi:

### 🟢 05:00–20:59

Session tetap aktif.

```text
Sudah Login
     ↓
Tutup aplikasi
     ↓
Buka kembali
     ↓
Tetap Login ✅
```

Termasuk:

```text
Pindah aplikasi
     ↓
Kembali ke AbsensiKaryawan
     ↓
Tetap Login ✅
```

### 🔴 21:00–04:59

Session tidak dipertahankan ketika aplikasi dibuka/kembali aktif.

```text
Sudah Login
     ↓
Tutup / tinggalkan aplikasi
     ↓
Buka / kembali ke aplikasi
     ↓
Session diakhiri
     ↓
Login
     ↓
Email + Password
```

**Akun Firebase tetap ada.**

Yang diakhiri hanya session login.

### 🚪 Logout Manual

Kapan saja:

```text
Setting
   ↓
Keluar
   ↓
FirebaseAuth.signOut()
   ↓
Login
```

---

# 3. 👨‍💼 ADMIN

Setelah Admin berhasil login:

```text
ADMIN DASHBOARD
```

Bottom Navigation:

```text
Beranda | Approval | Karyawan | Rekap | Setting
```

---

## 3.1 🏠 ADMIN BERANDA

Menampilkan dashboard administrasi.

Akses cepat:

```text
Approval
Karyawan
Rekap
Setting
```

Tidak menampilkan:

```text
Logout
Version 1.1
```

---

# 4. 📋 ADMIN APPROVAL

```text
Admin
 ↓
Approval
```

Menampilkan daftar pengajuan staff.

Status pengajuan:

```text
Menunggu
Disetujui
Ditolak
```

Admin dapat membuka detail pengajuan.

```text
Approval
   ↓
Detail Pengajuan
   ↓
Review
   ↓
Setujui / Tolak
```

---

# 5. 👥 ADMIN KARYAWAN

```text
Admin
 ↓
Karyawan
```

Fitur:

```text
Daftar Karyawan
      ↓
Tambah Karyawan
      ↓
Isi data
      ↓
Simpan
```

Karyawan dapat dikelola dari halaman Admin.

Form tambah karyawan sudah menggunakan scroll agar seluruh form tetap dapat diakses.

**Catatan keamanan:**

Password Firebase Authentication **tidak dapat dilihat oleh Admin**.

---

# 6. 📊 ADMIN REKAP

```text
Admin
 ↓
Rekap
```

Digunakan untuk melihat data absensi/rekap karyawan.

Alurnya:

```text
Rekap
 ↓
Data Absensi
 ↓
Lihat informasi kehadiran
```

---

# 7. ⚙️ ADMIN SETTING

Bottom navigation tetap berada di:

```text
Beranda | Approval | Karyawan | Rekap | Setting
```

Setting Admin **tidak menggunakan back button sebagai navigasi utama**.

Menu:

```text
Profile
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

---

## 7.1 👤 PROFILE ADMIN

```text
Setting
 ↓
Profile
```

Menampilkan informasi profile Admin.

---

## 7.2 🎨 TAMPILAN ADMIN

```text
Setting
 ↓
Tampilan
```

Pilihan:

```text
Terang
Gelap
Sistem
```

Pilihan disimpan menggunakan `ThemeDataStore`.

---

## 7.3 ❓ BANTUAN ADMIN

```text
Setting
 ↓
Bantuan
```

Berisi bantuan penggunaan aplikasi.

---

## 7.4 ℹ️ TENTANG APLIKASI

```text
Setting
 ↓
Tentang Aplikasi
```

Menampilkan informasi aplikasi.

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

## 7.5 🚪 KELUAR ADMIN

```text
Setting
 ↓
Keluar
 ↓
FirebaseAuth.signOut()
 ↓
Login
```

---

# 8. 👨‍🔧 STAFF

Setelah Staff berhasil login:

```text
STAFF DASHBOARD
```

Bottom Navigation:

```text
Beranda | Pengajuan | Scan | Riwayat | Setting
```

---

# 9. 🏠 STAFF BERANDA

Menampilkan:

```text
Nama Staff
Tanggal
Jam Real-Time
Status Kehadiran
```

Status:

```text
SUDAH ABSEN
```

atau:

```text
BELUM ABSEN
```

Tersedia:

```text
Scan QR
```

Selain itu:

```text
🔔 Notifikasi
👤 Profile
```

Tidak menampilkan:

```text
Halo, Muhammad Qomarudin 👋
Logout
Version 1.1
```

---

# 10. 👤 PROFILE STAFF

Diakses melalui profile pada Beranda.

```text
Beranda
 ↓
Profile
```

Setelah selesai:

```text
Profile
 ↓
Kembali
 ↓
Beranda
```

---

# 11. 🔔 NOTIFIKASI STAFF

```text
Beranda
 ↓
Notifikasi
```

Menampilkan informasi/notifikasi aplikasi.

---

# 12. 📷 ABSEN MASUK

Alur utama absensi:

```text
Staff Dashboard
      ↓
Scan
      ↓
Kamera aktif
      ↓
Scan QR
      ↓
Validasi QR
      ↓
Cek user login
      ↓
Cek absensi hari ini
      ↓
Belum ada absensi?
      ↓
Simpan Absen Masuk
      ↓
Firestore
      ↓
DataStore
      ↓
Dashboard Staff
```

Data yang disimpan antara lain:

```text
UID
Nama
Tanggal
Jam Masuk
QR Data
Catatan
```

---

# 13. 🕘 ABSEN PULANG

Jika absensi masuk sudah ada:

```text
Scan QR
   ↓
Cek absensi hari ini
   ↓
Absen masuk ditemukan
   ↓
Jam Pulang masih kosong?
   ↓
Ya
   ↓
Simpan Jam Pulang
   ↓
Firestore
   ↓
DataStore
   ↓
Dashboard
```

Sehingga satu hari memiliki:

```text
Jam Masuk
Jam Pulang
```

---

# 14. 📜 RIWAYAT STAFF

```text
Staff
 ↓
Riwayat
```

Menampilkan riwayat pengajuan/aktivitas yang tersedia.

User dapat memilih detail:

```text
Riwayat
 ↓
Detail Pengajuan
```

---

# 15. 📝 PENGAJUAN

```text
Staff
 ↓
Pengajuan
```

Jenis pengajuan:

```text
Sakit
Terlambat
Pulang Cepat
Izin Keluar
Cuti
```

---

# 16. ➕ PENGAJUAN BARU

```text
Pengajuan
 ↓
Pengajuan Baru
 ↓
Pilih Jenis
 ↓
Isi Data
 ↓
Alasan
 ↓
Submit
 ↓
Firestore
 ↓
Kembali ke Pengajuan
```

Data pengajuan dapat mencakup:

```text
UID
Nama
Jenis
Tanggal
Jam Pulang
Jam Keluar
Jam Kembali
Tanggal Mulai
Tanggal Selesai
Alasan
```

---

# 17. 📄 DETAIL PENGAJUAN

```text
Pengajuan
 ↓
Detail
```

Menampilkan:

```text
Jenis
Tanggal
Jam
Tanggal Mulai
Tanggal Selesai
Alasan
Status
Catatan Admin
```

Status:

```text
Menunggu
Disetujui
Ditolak
```

---

# 18. ⚙️ SETTING STAFF

Menu:

```text
Profile        → tidak ditampilkan di Setting
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

Profile tetap dapat diakses dari Beranda.

---

## 18.1 🎨 TAMPILAN STAFF

Pilihan:

```text
Terang
Gelap
Sistem
```

Disimpan melalui:

```text
ThemeDataStore
```

---

## 18.2 ❓ BANTUAN STAFF

```text
Setting
 ↓
Bantuan
```

Berisi panduan penggunaan aplikasi.

---

## 18.3 ℹ️ TENTANG APLIKASI

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

## 18.4 🚪 KELUAR STAFF

```text
Setting
 ↓
Keluar
 ↓
FirebaseAuth.signOut()
 ↓
Login
```

---

# 19. 🗄️ DATA FIREBASE

Struktur utama:

```text
Firebase
│
├── Authentication
│   └── Email + Password
│
└── Firestore
    │
    ├── users
    │
    ├── attendance
    │
    └── pengajuan
```

---

# 20. 💾 DATASTORE

Digunakan untuk data lokal aplikasi seperti:

```text
SUDAH_ABSEN
JAM_ABSEN
TANGGAL_ABSEN
JAM_PULANG
QR_ABSEN
CATATAN_ABSEN
QR_DATA
```

Theme menggunakan:

```text
ThemeDataStore
```

---

# 21. 🧭 STRUKTUR NAVIGASI UTAMA

```text
                         LOGIN
                           │
             ┌─────────────┴─────────────┐
             │                           │
          ADMIN                        STAFF
             │                           │
             ▼                           ▼
         BERANDA                      BERANDA
             │                           │
      ┌──────┼──────┐             ┌─────┼─────┐
      │      │      │             │     │     │
  Approval Karyawan Rekap      Pengajuan Scan Riwayat
      │      │      │             │     │     │
      └──────┴──────┘             └─────┴─────┘
             │                           │
          Setting                     Setting
             │                           │
      ┌──────┼──────┐             ┌─────┼─────┐
      │      │      │             │     │     │
   Profile Tampilan Bantuan    Tampilan Bantuan Tentang
             │                           │
          Tentang                      Keluar
             │                           │
          Keluar                         ▼
             │                          LOGIN
             ▼
           LOGIN
```

---

# 22. 🔐 ATURAN SESSION FINAL

```text
                 APLIKASI
                    │
                    ▼
              Cek waktu sekarang
                    │
          ┌─────────┴─────────┐
          │                   │
     05:00–20:59          21:00–04:59
          │                   │
          ▼                   ▼
   Session tetap         Session tidak
       aktif               persisten
          │                   │
          ▼                   ▼
   Tetap login          Login kembali
```

### Batas waktu final:

```text
04:59 → 🔒 wajib login
05:00 → 🟢 session tetap
20:59 → 🟢 session tetap
21:00 → 🔴 session tidak persisten
```

---

# 23. 🏁 STATUS PROJECT SAAT INI

Yang sudah berjalan:

```text
✅ Login
✅ Lupa Password
✅ Firebase Authentication
✅ Admin Dashboard
✅ Staff Dashboard
✅ Approval
✅ Karyawan
✅ Rekap Admin
✅ Admin Setting
✅ Staff Setting
✅ Theme DataStore
✅ Scan QR
✅ Absen Masuk
✅ Absen Pulang
✅ Firestore Attendance
✅ Pengajuan
✅ Detail Pengajuan
✅ Riwayat
✅ Profile
✅ Notifikasi
✅ Bantuan
✅ Tentang Aplikasi
✅ Logout
✅ Time-Based Session
```

### Prinsip pengembangan berikutnya:

```text
JANGAN HAPUS FITUR YANG SUDAH JALAN
          ↓
JANGAN UBAH UI YANG SUDAH DISETUJUI
          ↓
TAMBAH / PERBAIKI SECARA INCREMENTAL
          ↓
BUILD
          ↓
TEST
          ↓
LANJUT KE FITUR BERIKUTNYA
```

**Versi aplikasi saat ini: `1.1`**
