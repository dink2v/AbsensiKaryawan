# ALUR APLIKASI ABSENSI KARYAWAN

Dokumentasi lengkap alur, struktur, fitur, database, navigasi, dan pengembangan aplikasi **Absensi Karyawan** berbasis Android.

Dokumen ini digunakan sebagai **patokan utama pengembangan project** agar setiap perubahan tetap mengikuti struktur aplikasi yang sudah dibuat.

---

# 1. INFORMASI PROJECT

**Nama Project:**

```text
AbsensiKaryawan
```

**Package:**

```text
com.example.absensikaryawan
```

**Platform:**

```text
Android
```

**Bahasa:**

```text
Kotlin
```

**UI Framework:**

```text
Jetpack Compose
```

**Backend:**

```text
Firebase
```

**Database:**

```text
Cloud Firestore
```

**Authentication:**

```text
Firebase Authentication
```

**QR Scanner:**

```text
CameraX + ML Kit Barcode Scanning
```

**Local Storage:**

```text
DataStore Preferences
```

**Navigation:**

```text
Enum-based Navigation
```

**Version Aplikasi:**

```text
1.1
```

**Version Code:**

```text
1
```

**Tahun:**

```text
2026
```

---

# 2. TUJUAN APLIKASI

Aplikasi digunakan untuk mengelola proses absensi karyawan secara digital.

Aplikasi memiliki dua role utama:

```text
LOGIN
  │
  ├── ADMIN
  │
  └── STAFF
```

Admin digunakan untuk:

```text
Mengelola karyawan
Melihat rekap absensi
Mengelola pengajuan
Mengelola aplikasi
```

Staff digunakan untuk:

```text
Melakukan absensi
Scan QR kantor
Melihat riwayat
Membuat pengajuan
Melihat notifikasi
Chat dengan Admin
Mengatur aplikasi
```

---

# 3. ALUR UTAMA APLIKASI

Alur utama aplikasi:

```text
                    ┌──────────────┐
                    │    LOGIN     │
                    └──────┬───────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ Firebase Auth     │
                  └────────┬─────────┘
                           │
                           ▼
                  Ambil Data User
                           │
                           ▼
                    Cek isAdmin
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
        ┌─────────────┐          ┌─────────────┐
        │    ADMIN    │          │    STAFF    │
        └──────┬──────┘          └──────┬──────┘
               │                        │
               ▼                        ▼
        ADMIN DASHBOARD          STAFF DASHBOARD
```

---

# 4. LOGIN

Screen:

```text
LoginScreen
```

Login menggunakan Firebase Authentication.

Metode:

```text
FirebaseAuth.signInWithEmailAndPassword()
```

User memasukkan:

```text
Email
Password
```

Alur:

```text
Buka Aplikasi
      │
      ▼
Login Screen
      │
      ▼
Masukkan Email
      │
      ▼
Masukkan Password
      │
      ▼
Firebase Authentication
      │
      ▼
Login Berhasil
      │
      ▼
Ambil Data User Firestore
      │
      ▼
Cek isAdmin
```

---

# 5. PEMISAHAN ROLE

Informasi role berasal dari:

```text
Firestore
```

Collection:

```text
users
```

Field utama:

```text
isAdmin
```

Logika:

```text
isAdmin = true
        ↓
Admin Dashboard
```

atau:

```text
isAdmin = false
        ↓
Staff Dashboard
```

Alur:

```text
Login
  │
  ▼
FirebaseAuth
  │
  ▼
currentUser
  │
  ▼
Firestore users
  │
  ▼
isAdmin
  │
  ├── true
  │     ↓
  │   ADMIN
  │
  └── false
        ↓
      STAFF
```

---

# 6. LUPA PASSWORD

Screen:

```text
ForgotPasswordScreen
```

Fungsi:

```text
Reset password melalui Firebase Authentication
```

Alur:

```text
Login
  │
  ▼
Lupa Password
  │
  ▼
Masukkan Email
  │
  ▼
Firebase Password Reset
  │
  ▼
Email Reset Password
```

Password asli tidak pernah ditampilkan oleh aplikasi.

---

# 7. SESSION LOGIN

Aplikasi memiliki aturan session berdasarkan waktu.

File:

```text
SessionManager.kt
```

Package:

```text
com.example.absensikaryawan
```

Aturan session:

```text
05:00 - 20:59
```

Session dipertahankan.

Sedangkan:

```text
21:00 - 04:59
```

session tidak dipertahankan.

---

# 8. SESSION AKTIF

Pada:

```text
05:00 - 20:59
```

user tetap login ketika aplikasi ditutup sementara atau berpindah aplikasi.

Contoh:

```text
07:00
  │
  ▼
Login
  │
  ▼
Dashboard
  │
  ▼
Tutup Aplikasi
  │
  ▼
Buka Lagi
  │
  ▼
Tetap Login
```

---

# 9. SESSION NON-PERSISTENT

Pada:

```text
21:00 - 04:59
```

aplikasi meminta user login kembali ketika aplikasi masuk foreground.

Firebase Authentication tidak menghapus akun.

Yang dilakukan hanya:

```text
FirebaseAuth.signOut()
```

Alur:

```text
Aplikasi masuk foreground
          │
          ▼
    Cek waktu sekarang
          │
          ▼
     21:00 - 04:59
          │
          ▼
 FirebaseAuth.signOut()
          │
          ▼
      Login Screen
```

---

# 10. LOGOUT MANUAL

Logout manual tersedia melalui Settings.

Alur:

```text
Dashboard
   │
   ▼
Settings
   │
   ▼
Keluar
   │
   ▼
FirebaseAuth.signOut()
   │
   ▼
Login Screen
```

Logout manual dapat dilakukan kapan saja.

---

# 11. MAIN ACTIVITY

File:

```text
MainActivity.kt
```

MainActivity bertanggung jawab terhadap:

```text
Theme aplikasi
Lifecycle aplikasi
Session berdasarkan waktu
Menjalankan AppNavigation()
```

Lifecycle digunakan untuk mendeteksi saat aplikasi masuk foreground.

Event yang digunakan:

```text
ON_START
```

Alur:

```text
MainActivity
     │
     ├── Theme
     │
     ├── Session Manager
     │
     └── AppNavigation
```

---

# 12. APP NAVIGATION

File:

```text
AppNavigation.kt
```

Navigation menggunakan enum-based navigation.

Tidak menggunakan:

```text
NavHost
```

Role ditentukan oleh:

```text
AppRole
```

Role:

```text
NONE
ADMIN
STAFF
```

Alur:

```text
NONE
 │
 ▼
Login
 │
 ├── ADMIN
 │
 └── STAFF
```

---

# 13. STRUKTUR ROLE

```text
AppNavigation
│
├── AppRole.NONE
│      ↓
│    Login
│
├── AppRole.ADMIN
│      ↓
│    AdminNavigation
│
└── AppRole.STAFF
       ↓
     StaffNavigation
```

---

# 14. DAFTAR SCREEN ADMIN

Screen Admin:

```text
AdminDashboardScreen
ApprovalScreen
KaryawanScreen
AdminRekapScreen
AdminSettingsScreen
AdminProfile
AdminTampilan
AdminBantuan
AdminTentangAplikasi
```

---

# 15. DAFTAR SCREEN STAFF

Screen Staff:

```text
StaffDashboardScreen
Profile
PengajuanScreen
PengajuanBaruScreen
RiwayatPengajuan
DetailPengajuan
ScanAbsenScreen
RiwayatScreen
Settings
Tampilan
Notifikasi
Bantuan
TentangAplikasi
```

---

# 16. AREA ADMIN

Setelah login sebagai Admin:

```text
Login
  │
  ▼
isAdmin = true
  │
  ▼
Admin Dashboard
```

Bottom navigation Admin:

```text
┌──────────┬──────────┬──────────┬──────────┬──────────┐
│ Beranda  │ Approval │ Karyawan │  Rekap   │ Setting  │
└──────────┴──────────┴──────────┴──────────┴──────────┘
```

---

# 17. ADMIN DASHBOARD

Screen:

```text
AdminDashboardScreen
```

Dashboard menjadi pusat aktivitas Admin.

Fungsi:

```text
Ringkasan aplikasi
Navigasi Approval
Navigasi Karyawan
Navigasi Rekap
Navigasi Settings
```

Dashboard tidak menampilkan:

```text
Version 1.1
```

Informasi versi diletakkan di Login dan Settings.

---

# 18. ADMIN APPROVAL

Screen:

```text
ApprovalScreen
```

Digunakan untuk mengelola pengajuan Staff.

Alur:

```text
Staff
  │
  ▼
Membuat Pengajuan
  │
  ▼
Firestore
  │
  ▼
Admin Approval
  │
  ├── Setujui
  │
  └── Tolak
```

Status pengajuan dapat berupa:

```text
Menunggu
Disetujui
Ditolak
```

---

# 19. ADMIN KARYAWAN

Screen:

```text
KaryawanScreen
```

Fungsi:

```text
Melihat data karyawan
Menambah karyawan
Mengelola data karyawan
```

Tambah karyawan menggunakan:

```text
TambahKaryawanDialog
```

Form menggunakan layout yang mendukung layar kecil.

---

# 20. DATA KARYAWAN

Collection:

```text
users
```

Field utama:

```text
uid
nama
email
isAdmin
```

Field dapat berkembang sesuai kebutuhan aplikasi.

---

# 21. PASSWORD KARYAWAN

Password Firebase Authentication tidak dapat dibaca atau ditampilkan kembali oleh Admin.

Password dikelola oleh:

```text
Firebase Authentication
```

Jika karyawan lupa password:

```text
Forgot Password
```

Tidak ada fitur untuk melihat password asli.

---

# 22. ADMIN REKAP

Screen:

```text
AdminRekapScreen
```

Digunakan untuk melihat rekap absensi karyawan.

Sumber utama:

```text
attendance
```

Data dapat digunakan untuk menampilkan:

```text
Nama
Tanggal
Jam Masuk
Jam Pulang
Kantor
Status
```

---

# 23. ADMIN SETTINGS

Screen:

```text
AdminSettingsScreen
```

Settings Admin berada sebagai bagian utama navigasi Admin.

Menu:

```text
Profile
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

Struktur:

```text
Admin
 │
 └── Setting
      │
      ├── Profile
      ├── Tampilan
      ├── Bantuan
      ├── Tentang Aplikasi
      └── Keluar
```

---

# 24. ADMIN PROFILE

Screen:

```text
AdminProfile
```

Digunakan untuk menampilkan informasi profil Admin.

---

# 25. ADMIN TAMPILAN

Screen:

```text
AdminTampilan
```

Pilihan mode:

```text
TERANG
GELAP
SISTEM
```

Theme disimpan menggunakan:

```text
ThemeDataStore
```

---

# 26. ADMIN BANTUAN

Screen:

```text
AdminBantuan
```

Berisi informasi penggunaan aplikasi untuk Admin.

---

# 27. ADMIN TENTANG APLIKASI

Screen:

```text
AdminTentangAplikasi
```

Berisi informasi aplikasi.

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 28. ADMIN LOGOUT

Alur:

```text
Admin Settings
      │
      ▼
    Keluar
      │
      ▼
FirebaseAuth.signOut()
      │
      ▼
Login
```

---

# 29. AREA STAFF

Setelah login sebagai Staff:

```text
Login
  │
  ▼
isAdmin = false
  │
  ▼
Staff Dashboard
```

Bottom navigation:

```text
┌──────────┬────────────┬────────┬──────────┬──────────┐
│ Beranda  │ Pengajuan  │  Scan  │ Riwayat  │ Setting  │
└──────────┴────────────┴────────┴──────────┴──────────┘
```

---

# 30. STAFF DASHBOARD

Screen:

```text
StaffDashboardScreen
```

Dashboard menampilkan:

```text
Nama Staff
Tanggal
Jam real-time
Status absensi
Tombol Scan QR
Kehadiran Hari Ini
```

Dashboard menjadi pusat aktivitas Staff.

---

# 31. JAM REAL-TIME

Jam Staff menggunakan format:

```text
HH:mm:ss
```

Contoh:

```text
07:35:21
```

Jam diperbarui setiap detik.

Tanggal menggunakan:

```text
Locale Indonesia
```

---

# 32. STATUS KEHADIRAN

Status:

```text
SUDAH ABSEN
```

atau:

```text
BELUM ABSEN
```

Data berdasarkan attendance hari ini.

Alur:

```text
Dashboard
    │
    ▼
Firestore attendance
    │
    ▼
Cek uid + tanggal
    │
    ▼
Data ditemukan?
    │
    ├── Tidak → BELUM ABSEN
    │
    └── Ya → SUDAH ABSEN
```

---

# 33. NOTIFIKASI STAFF

Icon Notifikasi berada di area atas Dashboard.

Struktur:

```text
Notification
      │
      ▼
Profile
```

Notifikasi bukan menu utama bottom navigation.

---

# 34. PROFILE STAFF

Screen:

```text
Profile
```

Profile dapat diakses dari Dashboard.

Profile tidak menjadi menu utama Settings.

---

# 35. SCAN QR

Screen:

```text
ScanAbsenScreen
```

Teknologi:

```text
CameraX
+
ML Kit Barcode Scanning
```

Scanner digunakan untuk melakukan:

```text
Absen Masuk
Absen Pulang
```

---

# 36. QR KANTOR RESMI

Aplikasi memiliki tiga QR kantor resmi.

### KANTOR MALANG

```text
https://q.me-qr.com/x5ie23mg
```

### KANTOR BLITAR

```text
https://q.me-qr.com/hbywvgy7
```

### KANTOR KEDIRI

```text
https://q.me-qr.com/14vy2ipr
```

Scanner hanya menerima QR yang terdaftar.

---

# 37. VALIDASI QR

Validasi menggunakan URL QR resmi.

Normalisasi:

```text
Trim
Remove trailing slash
Ignore case
```

QR yang tidak terdaftar ditolak.

Alur:

```text
Scan QR
   │
   ▼
Baca Value
   │
   ▼
Normalisasi
   │
   ▼
Cocokkan dengan QR Resmi
   │
   ├── VALID
   │     ↓
   │   Deteksi Kantor
   │
   └── TIDAK VALID
         ↓
       Tolak
```

---

# 38. STABILISASI SCANNER

Scanner menggunakan mekanisme validasi beberapa frame agar QR tidak langsung dianggap valid hanya karena terbaca sesaat.

Konsep:

```text
QR terdeteksi
     │
     ▼
Validasi frame
     │
     ▼
Frame stabil
     │
     ▼
QR VALID
```

Scanner juga memiliki lock agar satu QR tidak diproses berkali-kali secara bersamaan.

---

# 39. ABSEN MASUK

Alur:

```text
Staff Dashboard
      │
      ▼
Scan QR Absen
      │
      ▼
Camera aktif
      │
      ▼
QR terdeteksi
      │
      ▼
Validasi QR
      │
      ▼
Kantor terdeteksi
      │
      ▼
Konfirmasi
      │
      ▼
Proses Absensi
      │
      ▼
Firestore
      │
      ▼
Absen Berhasil
      │
      ▼
Dashboard Refresh
```

---

# 40. DATA ABSEN MASUK

Collection:

```text
attendance
```

Field utama:

```text
uid
nama
tanggal
jamMasuk
jamPulang
qrData
catatan
```

Contoh:

```text
uid       : user UID
nama      : Nama Staff
tanggal   : 2026-09-08
jamMasuk  : 07:30:12
jamPulang : ""
qrData    : QR kantor
catatan   : ""
```

---

# 41. ABSEN PULANG

Setelah Absen Masuk:

```text
Status = SUDAH ABSEN
```

Dashboard menampilkan:

```text
Scan QR Absen Pulang
```

Alur:

```text
Dashboard
    │
    ▼
Scan QR Absen Pulang
    │
    ▼
Camera
    │
    ▼
QR Valid
    │
    ▼
Konfirmasi
    │
    ▼
Cari attendance hari ini
    │
    ▼
Update jamPulang
    │
    ▼
Firestore
    │
    ▼
Dashboard Refresh
```

---

# 42. DATA ABSEN PULANG

Data yang di-update:

```text
jamPulang
```

Dapat ditambahkan:

```text
qrDataPulang
catatanPulang
```

jika diperlukan oleh sistem.

Contoh:

```text
jamMasuk  : 07:30:12
jamPulang : 16:05:21
```

---

# 43. ATURAN ABSENSI HARIAN

Satu Staff menggunakan satu data attendance untuk satu tanggal.

Identifikasi:

```text
uid
+
tanggal
```

Contoh:

```text
uid = ABC123
tanggal = 2026-09-08
```

Data tersebut digunakan untuk:

```text
Absen Masuk
Absen Pulang
Dashboard
Riwayat
```

---

# 44. DATASTORE ABSENSI

File:

```text
AbsensiDataStore.kt
```

Key:

```text
SUDAH_ABSEN
JAM_ABSEN
TANGGAL_ABSEN
JAM_PULANG
QR_ABSEN
CATATAN_ABSEN
QR_DATA
```

DataStore digunakan untuk kebutuhan penyimpanan lokal aplikasi.

Firestore tetap menjadi sumber utama data absensi cloud.

---

# 45. RESET ABSENSI HARIAN

Status absensi perlu mengikuti pergantian tanggal.

Konsep:

```text
Hari ini
    │
    ▼
Attendance tanggal hari ini
```

Ketika tanggal berubah:

```text
Tanggal lama
    ↓
Tanggal baru
    ↓
Status absensi hari baru
```

Data hari sebelumnya tetap tersimpan di Firestore.

---

# 46. RIWAYAT STAFF

Screen:

```text
RiwayatScreen
```

Digunakan untuk melihat riwayat absensi Staff.

Sumber:

```text
attendance
```

Informasi dapat meliputi:

```text
Tanggal
Jam Masuk
Jam Pulang
Kantor
Status
```

---

# 47. PENGAJUAN STAFF

Screen:

```text
PengajuanScreen
```

Menampilkan pengajuan Staff.

Alur:

```text
Staff
  │
  ▼
Pengajuan
  │
  ▼
Daftar Pengajuan
```

---

# 48. PENGAJUAN BARU

Screen:

```text
PengajuanBaruScreen
```

Staff dapat membuat pengajuan baru.

Jenis pengajuan:

```text
Sakit
Terlambat
Pulang Cepat
Izin Keluar
Cuti
```

Alur:

```text
Pengajuan
   │
   ▼
Pengajuan Baru
   │
   ▼
Pilih Jenis
   │
   ▼
Isi Form
   │
   ▼
Submit
   │
   ▼
Firestore
```

---

# 49. DATA PENGAJUAN

Collection:

```text
pengajuan
```

Field yang digunakan:

```text
uid
nama
jenis
tanggal
jamPulang
jamKeluar
jamKembali
tanggalMulai
tanggalSelesai
alasan
```

Status pengajuan dapat digunakan:

```text
Menunggu
Disetujui
Ditolak
```

---

# 50. DETAIL PENGAJUAN

Screen:

```text
DetailPengajuan
```

Digunakan untuk melihat detail pengajuan yang dipilih.

Data:

```text
Jenis
Tanggal
Waktu
Alasan
Status
```

---

# 51. RIWAYAT PENGAJUAN

Screen:

```text
RiwayatPengajuan
```

Digunakan untuk melihat pengajuan yang pernah dibuat.

---

# 52. ALUR PENGAJUAN

```text
Staff
  │
  ▼
Pengajuan
  │
  ▼
Pengajuan Baru
  │
  ▼
Isi Form
  │
  ▼
Submit
  │
  ▼
Firestore
  │
  ▼
Admin Approval
  │
  ├── Setujui
  │
  └── Tolak
  │
  ▼
Status Pengajuan
  │
  ▼
Staff
```

---

# 53. NOTIFIKASI

Notifikasi digunakan untuk memberikan informasi kepada Staff.

Contoh:

```text
Pengajuan Anda telah disetujui.
```

atau:

```text
Pengajuan Anda ditolak.
```

Alur:

```text
Admin melakukan aksi
        │
        ▼
Status berubah
        │
        ▼
Notifikasi
        │
        ▼
Staff
```

---

# 54. CHAT ADMIN

Fitur chat digunakan untuk komunikasi Staff dengan Admin.

Data:

```text
senderId
senderName
senderType
message
timestamp
```

Alur Staff:

```text
Staff
  │
  ▼
Chat Admin
  │
  ▼
Tulis Pesan
  │
  ▼
Kirim
  │
  ▼
Firestore
```

Alur Admin:

```text
Admin
  │
  ▼
Chat
  │
  ▼
Pilih Staff
  │
  ▼
Lihat Pesan
  │
  ▼
Balas
```

---

# 55. STAFF SETTINGS

Screen:

```text
Settings
```

Menu:

```text
Notifikasi
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

Profile tidak dimasukkan ke Settings karena tersedia di Dashboard.

---

# 56. STAFF TAMPILAN

Screen:

```text
Tampilan
```

Pilihan:

```text
Terang
Gelap
Sistem
```

Data disimpan menggunakan:

```text
ThemeDataStore
```

---

# 57. STAFF BANTUAN

Screen:

```text
Bantuan
```

Berisi informasi bantuan penggunaan aplikasi untuk Staff.

---

# 58. STAFF TENTANG APLIKASI

Screen:

```text
TentangAplikasi
```

Berisi informasi aplikasi.

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 59. STAFF LOGOUT

Alur:

```text
Staff Settings
      │
      ▼
    Keluar
      │
      ▼
FirebaseAuth.signOut()
      │
      ▼
Login
```

---

# 60. STRUKTUR FIREBASE

Firebase digunakan untuk:

```text
Authentication
Firestore
```

Struktur:

```text
Firebase
│
├── Authentication
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

# 61. FIREBASE AUTHENTICATION

Digunakan untuk:

```text
Login
Logout
Forgot Password
Session
```

Fungsi utama:

```text
signInWithEmailAndPassword()
signOut()
sendPasswordResetEmail()
```

---

# 62. USERS

Collection:

```text
users
```

Digunakan untuk data user.

Field utama:

```text
uid
nama
email
isAdmin
```

`isAdmin` digunakan untuk menentukan role.

---

# 63. ATTENDANCE

Collection:

```text
attendance
```

Field utama:

```text
uid
nama
tanggal
jamMasuk
jamPulang
qrData
catatan
```

Data digunakan oleh:

```text
Staff Dashboard
Scan
Riwayat
Admin Rekap
```

---

# 64. PENGAJUAN

Collection:

```text
pengajuan
```

Field:

```text
uid
nama
jenis
tanggal
jamPulang
jamKeluar
jamKembali
tanggalMulai
tanggalSelesai
alasan
status
```

Digunakan oleh:

```text
Staff Pengajuan
Admin Approval
Notifikasi
```

---

# 65. USER REPOSITORY

File:

```text
UserRepository.kt
```

Digunakan untuk mengambil data user yang sedang login.

Alur:

```text
FirebaseAuth
     │
     ▼
currentUser
     │
     ▼
Firestore users
     │
     ▼
User Data
```

Informasi `isAdmin` menentukan role.

---

# 66. FIRESTORE REPOSITORY

Jika digunakan sebagai lapisan repository:

```text
FirestoreRepository.kt
```

Repository menjadi perantara antara UI dan Firestore.

Alur:

```text
Screen
  │
  ▼
Repository
  │
  ▼
Firestore
```

Contoh fungsi:

```text
getAbsenHariIni()
simpanAbsenMasuk()
updateAbsenPulang()
simpanPengajuan()
getPengajuan()
```

---

# 67. THEME SYSTEM

Theme utama:

```text
AbsensiKaryawanTheme
```

Mode:

```text
TERANG
GELAP
SISTEM
```

Penyimpanan:

```text
ThemeDataStore
```

Alur:

```text
User pilih Theme
      │
      ▼
ThemeDataStore
      │
      ▼
Theme Preference
      │
      ▼
AbsensiKaryawanTheme
```

---

# 68. STRUKTUR NAVIGASI ADMIN

```text
ADMIN
 │
 ├── Beranda
 │
 ├── Approval
 │
 ├── Karyawan
 │
 ├── Rekap
 │
 └── Setting
       │
       ├── Profile
       ├── Tampilan
       ├── Bantuan
       ├── Tentang Aplikasi
       └── Keluar
```

---

# 69. STRUKTUR NAVIGASI STAFF

```text
STAFF
 │
 ├── Beranda
 │     ├── Profile
 │     └── Notifikasi
 │
 ├── Pengajuan
 │     ├── Pengajuan Baru
 │     ├── Riwayat Pengajuan
 │     └── Detail Pengajuan
 │
 ├── Scan
 │     ├── Absen Masuk
 │     └── Absen Pulang
 │
 ├── Riwayat
 │
 └── Setting
       ├── Tampilan
       ├── Bantuan
       ├── Tentang Aplikasi
       └── Keluar
```

---

# 70. ALUR ABSEN LENGKAP

```text
LOGIN
  │
  ▼
STAFF DASHBOARD
  │
  ▼
SCAN QR
  │
  ▼
CAMERA
  │
  ▼
DETEKSI QR
  │
  ▼
VALIDASI QR
  │
  ├── TIDAK VALID
  │       ↓
  │     TOLAK
  │
  └── VALID
          │
          ▼
     DETEKSI KANTOR
          │
          ▼
       KONFIRMASI
          │
          ▼
   CEK ATTENDANCE HARI INI
          │
          ├── BELUM ADA
          │      ↓
          │  ABSEN MASUK
          │      ↓
          │  FIRESTORE
          │
          └── SUDAH ADA
                 ↓
             ABSEN PULANG
                 ↓
             FIRESTORE
                 │
                 ▼
            DASHBOARD
```

---

# 71. ALUR ABSEN MASUK

```text
Dashboard
    │
    ▼
Scan QR
    │
    ▼
QR Kantor
    │
    ▼
Validasi
    │
    ▼
Kantor Terdeteksi
    │
    ▼
Konfirmasi
    │
    ▼
Cek Attendance
    │
    ▼
Belum Absen
    │
    ▼
Simpan jamMasuk
    │
    ▼
Firestore
    │
    ▼
Dashboard Refresh
    │
    ▼
SUDAH ABSEN
```

---

# 72. ALUR ABSEN PULANG

```text
Dashboard
    │
    ▼
Status SUDAH ABSEN
    │
    ▼
Scan QR Absen Pulang
    │
    ▼
QR Kantor
    │
    ▼
Validasi
    │
    ▼
Konfirmasi
    │
    ▼
Cari Attendance Hari Ini
    │
    ▼
Update jamPulang
    │
    ▼
Firestore
    │
    ▼
Dashboard Refresh
    │
    ▼
Jam Pulang Tampil
```

---

# 73. ALUR DASHBOARD MEMBACA ABSENSI

Dashboard melakukan pengecekan:

```text
uid
+
tanggal hari ini
```

Query:

```text
attendance
```

Jika data ditemukan:

```text
sudahAbsen = true
```

Kemudian membaca:

```text
jamMasuk
jamPulang
```

Jika `jamPulang` kosong:

```text
Belum Absen Pulang
```

Jika `jamPulang` terisi:

```text
Absen Lengkap
```

---

# 74. ALUR DATA ABSENSI

```text
Scanner
   │
   ▼
StaffNavigation
   │
   ▼
FirebaseAuth
   │
   ▼
UID User
   │
   ▼
Firestore
   │
   ▼
attendance
   │
   ▼
Dashboard
```

---

# 75. ALUR DATA PENGAJUAN

```text
PengajuanBaruScreen
       │
       ▼
Validasi Form
       │
       ▼
Firestore
       │
       ▼
pengajuan
       │
       ▼
ApprovalScreen
       │
       ▼
Update Status
       │
       ▼
Staff
```

---

# 76. ALUR DATA USER

```text
Login
  │
  ▼
FirebaseAuth
  │
  ▼
UID
  │
  ▼
Firestore users
  │
  ▼
UserRepository
  │
  ▼
isAdmin
  │
  ├── true → Admin
  │
  └── false → Staff
```

---

# 77. STRUKTUR FILE UTAMA

```text
app/
│
└── src/
    │
    └── main/
        │
        ├── java/
        │   │
        │   └── com/example/absensikaryawan/
        │       │
        │       ├── MainActivity.kt
        │       ├── SessionManager.kt
        │       │
        │       ├── navigation/
        │       │   ├── AppNavigation.kt
        │       │   ├── AdminNavigation.kt
        │       │   └── StaffNavigation.kt
        │       │
        │       ├── screens/
        │       │   ├── LoginScreen.kt
        │       │   ├── ForgotPasswordScreen.kt
        │       │   ├── StaffDashboardScreen.kt
        │       │   ├── AdminDashboardScreen.kt
        │       │   ├── ScanAbsenScreen.kt
        │       │   ├── RiwayatScreen.kt
        │       │   ├── PengajuanScreen.kt
        │       │   ├── PengajuanBaruScreen.kt
        │       │   ├── DetailPengajuan.kt
        │       │   ├── RiwayatPengajuan.kt
        │       │   ├── Settings.kt
        │       │   ├── Notifikasi.kt
        │       │   └── ...
        │       │
        │       ├── repository/
        │       │   ├── UserRepository.kt
        │       │   └── FirestoreRepository.kt
        │       │
        │       ├── datastore/
        │       │   ├── AbsensiDataStore.kt
        │       │   └── ThemeDataStore.kt
        │       │
        │       └── models/
        │           └── ...
        │
        └── res/
```

---

# 78. FOLDER DOKUMENTASI

Dokumentasi project disimpan dalam:

```text
DOKUMENTASI/
```

Struktur:

```text
DOKUMENTASI/
│
├── ALUR_APLIKASI.md
│
└── CHANGELOG.md
```

`ALUR_APLIKASI.md`:

```text
Alur aplikasi
Struktur aplikasi
Firebase
Navigation
Screen
Absensi
Pengajuan
Session
Theme
```

`CHANGELOG.md`:

```text
Catatan perubahan project
```

---

# 79. CHANGELOG

Setiap perubahan besar dicatat.

Format:

```text
Tanggal:
Jam:
Fitur:
Perubahan:
Status:
```

Contoh:

```text
08-09-2026
09:00
Scan QR
Perbaikan validasi QR kantor
Status: Selesai
```

---

# 80. PRINSIP PENGEMBANGAN

Project dikembangkan secara incremental.

Aturan:

```text
1. Jangan menghapus fitur yang sudah berjalan.

2. Jangan mengganti struktur yang sudah stabil tanpa alasan.

3. Jangan mengubah desain yang sudah disepakati tanpa permintaan.

4. Perubahan dilakukan hanya pada bagian yang diperlukan.

5. Setelah perubahan lakukan Build.

6. Setelah Build berhasil lakukan Test.

7. Jika error, perbaiki error terlebih dahulu.

8. Jangan membuat ulang screen jika cukup memperbaiki bagian tertentu.

9. Pertahankan navigasi Admin dan Staff.

10. Pertahankan struktur Firebase.

11. Jangan menghapus kode lama hanya karena ingin menambahkan fitur baru.

12. Utamakan perubahan yang aman dan terisolasi.
```

---

# 81. ATURAN SAAT MEMPERBAIKI KODE

Jika ditemukan bug:

```text
BUG
 │
 ▼
Cari file penyebab
 │
 ▼
Baca kode yang sudah ada
 │
 ▼
Identifikasi bagian bermasalah
 │
 ▼
Perbaiki bagian tersebut
 │
 ▼
Pertahankan kode lain
 │
 ▼
Build
 │
 ▼
Test
```

Jangan langsung mengganti seluruh project.

---

# 82. CHECKLIST SEBELUM CODING

```text
[ ] Tentukan screen yang bermasalah
[ ] Tentukan file yang digunakan
[ ] Cek callback
[ ] Cek navigation
[ ] Cek Firebase
[ ] Cek DataStore
[ ] Pertahankan kode lama
[ ] Tentukan perubahan minimum
```

---

# 83. CHECKLIST SETELAH CODING

```text
[ ] Build Project
[ ] Tidak ada error Kotlin
[ ] Tidak ada unresolved reference
[ ] Tidak ada redeclaration
[ ] Navigation normal
[ ] Firebase normal
[ ] Scanner normal
[ ] UI tidak rusak
[ ] Status bar normal
[ ] Navigation bar normal
[ ] Test di Android
```

---

# 84. TESTING LOGIN

```text
[ ] Login Admin berhasil
[ ] Login Staff berhasil
[ ] Password salah ditolak
[ ] Forgot Password berjalan
[ ] Role Admin benar
[ ] Role Staff benar
[ ] Logout berhasil
```

---

# 85. TESTING SESSION

```text
[ ] 05:00 session aktif
[ ] 20:59 session aktif
[ ] 21:00 session tidak persisten
[ ] 04:59 session tidak persisten
[ ] Firebase account tidak dihapus
[ ] Login kembali berhasil
```

---

# 86. TESTING SCANNER

```text
[ ] Kamera meminta permission
[ ] Kamera aktif
[ ] QR Malang valid
[ ] QR Blitar valid
[ ] QR Kediri valid
[ ] QR lain ditolak
[ ] QR tidak terdaftar ditolak
[ ] Scanner tidak double process
[ ] Popup valid tampil
[ ] Popup invalid tampil
```

---

# 87. TESTING ABSEN MASUK

```text
[ ] Staff belum absen
[ ] Scan QR berhasil
[ ] Jam Masuk tersimpan
[ ] Tanggal tersimpan
[ ] UID tersimpan
[ ] Nama tersimpan
[ ] Dashboard berubah menjadi SUDAH ABSEN
[ ] Data Firestore benar
```

---

# 88. TESTING ABSEN PULANG

```text
[ ] Staff sudah absen masuk
[ ] Tombol Absen Pulang muncul
[ ] Scanner dapat dibuka kembali
[ ] QR valid
[ ] Jam Pulang tersimpan
[ ] Data attendance ter-update
[ ] Dashboard menampilkan Jam Pulang
[ ] Tidak membuat attendance baru secara tidak sengaja
```

---

# 89. TESTING PENGAJUAN

```text
[ ] Pengajuan Baru dapat dibuka
[ ] Form dapat diisi
[ ] Data valid
[ ] Submit berhasil
[ ] Data masuk Firestore
[ ] Admin dapat melihat
[ ] Admin dapat approve
[ ] Admin dapat reject
[ ] Staff melihat status
```

---

# 90. TESTING NAVIGATION STAFF

```text
[ ] Beranda
[ ] Pengajuan
[ ] Scan
[ ] Riwayat
[ ] Settings
[ ] Profile
[ ] Notifikasi
[ ] Tampilan
[ ] Bantuan
[ ] Tentang Aplikasi
[ ] Logout
```

---

# 91. TESTING NAVIGATION ADMIN

```text
[ ] Beranda
[ ] Approval
[ ] Karyawan
[ ] Rekap
[ ] Settings
[ ] Profile
[ ] Tampilan
[ ] Bantuan
[ ] Tentang Aplikasi
[ ] Logout
```

---

# 92. UI/UX FINAL

Setelah fungsi selesai, dilakukan tahap UI/UX.

Yang dirapikan:

```text
Spacing
Padding
Margin
Typography
Icon
Button
Card
Alignment
Top Bar
Bottom Navigation
Status Bar
Navigation Bar
Dialog
Popup
Scanner
```

Target:

```text
Tampilan konsisten
Responsive
Nyaman digunakan
Tidak tertutup system bar
Tidak bertabrakan dengan status bar
```

---

# 93. STATUS BAR

Status bar Android tidak boleh bertabrakan dengan UI aplikasi.

Setiap screen harus memperhatikan:

```text
Status Bar
Content
Navigation Bar
```

Jika menggunakan edge-to-edge:

```text
Window Insets
```

harus diperhatikan agar content tidak tertutup system bar.

---

# 94. NAVIGATION BAR

Bottom navigation aplikasi harus memiliki jarak yang aman dari navigation bar Android.

Tujuan:

```text
Tidak tertutup tombol gesture
Tidak tertutup navigation bar
Tidak terlalu menempel ke bawah
```

---

# 95. FINAL BUILD

Setelah semua fitur selesai:

```text
Clean Project
      │
      ▼
Rebuild Project
      │
      ▼
Run
      │
      ▼
Test
      │
      ▼
Generate APK
```

Target:

```text
BUILD SUCCESSFUL
```

---

# 96. FINAL TESTING

Testing final dilakukan dari awal:

```text
Login
 ↓
Role
 ↓
Dashboard
 ↓
Absensi
 ↓
Riwayat
 ↓
Pengajuan
 ↓
Approval
 ↓
Notifikasi
 ↓
Chat
 ↓
Settings
 ↓
Logout
```

---

# 97. BACKUP PROJECT

Setelah versi stabil:

```text
Android Studio
      │
      ▼
Git
      │
      ▼
Commit
      │
      ▼
Push
      │
      ▼
GitHub
```

Tujuan:

```text
Backup
Version Control
Riwayat Perubahan
Recovery
```

---

# 98. VERSI APLIKASI

Saat ini:

```text
Version Name:
1.1
```

```text
Version Code:
1
```

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

Footer ditampilkan pada:

```text
Login
Admin Settings
Staff Settings
```

Tidak ditampilkan pada:

```text
Admin Dashboard
Staff Dashboard
```

---

# 99. STATUS FITUR PROJECT

Fitur yang sudah dibuat / dikerjakan:

```text
✅ Login Firebase
✅ Firebase Authentication
✅ Forgot Password
✅ Role Admin
✅ Role Staff
✅ Admin Dashboard
✅ Staff Dashboard
✅ Admin Approval
✅ Admin Karyawan
✅ Admin Rekap
✅ Admin Settings
✅ Admin Profile
✅ Admin Tampilan
✅ Admin Bantuan
✅ Admin Tentang Aplikasi
✅ Staff Profile
✅ Staff Pengajuan
✅ Pengajuan Baru
✅ Riwayat Pengajuan
✅ Detail Pengajuan
✅ Scan QR
✅ Validasi QR Kantor
✅ Absen Masuk
✅ Absen Pulang
✅ Riwayat Staff
✅ Staff Settings
✅ Staff Tampilan
✅ Staff Bantuan
✅ Staff Tentang Aplikasi
✅ Logout
✅ Firebase Firestore
✅ DataStore
✅ Theme DataStore
✅ Session berdasarkan waktu
```

---

# 100. POSISI PROJECT SAAT INI

Project sudah memiliki fondasi utama:

```text
                         ABSENSI KARYAWAN
                                │
                ┌───────────────┴───────────────┐
                │                               │
              ADMIN                            STAFF
                │                               │
       ┌────────┼────────┐             ┌────────┼─────────┐
       │        │        │             │        │         │
   Approval Karyawan   Rekap       Pengajuan   Scan    Riwayat
       │        │        │             │        │         │
       └────────┴────────┘             └────────┴─────────┘
                │                               │
             Settings                        Settings
                │                               │
       ┌────────┼────────┐             ┌────────┼────────┐
       │        │        │             │        │        │
    Profile Tampilan Bantuan        Tampilan Bantuan Tentang
       │
       ▼
    Tentang
       │
       ▼
     Keluar
```

---

# 101. PRIORITAS PENGEMBANGAN BERIKUTNYA

Urutan pengerjaan berikutnya:

```text
1. Pastikan Absen Masuk stabil
          ↓
2. Pastikan Absen Pulang stabil
          ↓
3. Riwayat Staff
          ↓
4. Pengajuan Staff
          ↓
5. Detail Pengajuan
          ↓
6. Approval Admin
          ↓
7. Notifikasi
          ↓
8. Chat Admin
          ↓
9. Rekap Admin
          ↓
10. Karyawan Admin
          ↓
11. Settings
          ↓
12. UI/UX Final
          ↓
13. Testing Total
          ↓
14. Bug Fix
          ↓
15. Build APK
```

---

# 102. POLA PENGEMBANGAN SETIAP FITUR

Setiap fitur baru menggunakan pola:

```text
ANALISIS
    │
    ▼
CEK KODE SAAT INI
    │
    ▼
TENTUKAN FILE
    │
    ▼
PERBAIKAN / PENAMBAHAN
    │
    ▼
BUILD
    │
    ▼
TEST
    │
    ├── ERROR
    │     ↓
    │   FIX
    │     │
    │     └──────► BUILD
    │
    └── SUCCESS
          │
          ▼
       NEXT STEP
```

---

# 103. ATURAN UTAMA PROJECT

Project harus selalu mengikuti prinsip:

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
```

```text
JANGAN MERUSAK NAVIGASI.
```

```text
JANGAN MENGUBAH DESAIN TANPA PERMINTAAN.
```

```text
JANGAN MENGHAPUS FITUR LAMA SAAT MENAMBAHKAN FITUR BARU.
```

```text
PERBAIKI SECARA INCREMENTAL.
```

```text
BUILD → TEST → FIX → BUILD → TEST.
```

---

# 104. ALUR BESAR FINAL

```text
                         ┌──────────────┐
                         │    APLIKASI  │
                         └──────┬───────┘
                                │
                                ▼
                         ┌──────────────┐
                         │    LOGIN     │
                         └──────┬───────┘
                                │
                                ▼
                       Firebase Authentication
                                │
                                ▼
                           Cek isAdmin
                                │
                 ┌──────────────┴──────────────┐
                 │                             │
                 ▼                             ▼
             ┌───────┐                     ┌───────┐
             │ ADMIN │                     │ STAFF │
             └───┬───┘                     └───┬───┘
                 │                             │
                 ▼                             ▼
          ADMIN DASHBOARD              STAFF DASHBOARD
                 │                             │
        ┌────────┼────────┐          ┌─────────┼─────────┐
        │        │        │          │         │         │
        ▼        ▼        ▼          ▼         ▼         ▼
    Approval Karyawan  Rekap    Pengajuan   Scan     Riwayat
        │        │        │          │         │
        └────────┴────────┘          │         │
                 │                   │         │
                 ▼                   ▼         ▼
             SETTINGS            FIRESTORE  ATTENDANCE
                 │
        ┌────────┼─────────┐
        │        │         │
        ▼        ▼         ▼
     Profile Tampilan Bantuan
                 │
                 ▼
              Tentang
                 │
                 ▼
               Logout
                 │
                 ▼
               LOGIN
```

---

# 105. KESIMPULAN

Aplikasi **Absensi Karyawan** memiliki dua role:

```text
ADMIN
STAFF
```

Admin berfokus pada:

```text
Approval
Karyawan
Rekap
Settings
```

Staff berfokus pada:

```text
Absensi
Pengajuan
Riwayat
Notifikasi
Chat
Settings
```

Backend menggunakan:

```text
Firebase Authentication
Cloud Firestore
```

Scanner menggunakan:

```text
CameraX
ML Kit Barcode Scanning
```

Local storage menggunakan:

```text
DataStore Preferences
```

Theme menggunakan:

```text
ThemeDataStore
```

Session menggunakan aturan:

```text
05:00 - 20:59
→ Session dipertahankan
```

```text
21:00 - 04:59
→ Login kembali diperlukan
```

Akun Firebase tidak dihapus ketika session berakhir.

Logout manual menggunakan:

```text
FirebaseAuth.signOut()
```

Sistem absensi menggunakan:

```text
uid + tanggal
```

sebagai dasar pencarian attendance hari tersebut.

QR kantor resmi:

```text
KANTOR MALANG
https://q.me-qr.com/x5ie23mg

KANTOR BLITAR
https://q.me-qr.com/hbywvgy7

KANTOR KEDIRI
https://q.me-qr.com/14vy2ipr
```

---

# 106. PATOKAN PENGEMBANGAN

Dokumen ini menjadi patokan utama pengembangan project.

Setiap perubahan harus mengikuti:

```text
CEK KONDISI SEKARANG
        ↓
PERTAHANKAN FITUR LAMA
        ↓
TENTUKAN BAGIAN YANG DIUBAH
        ↓
UPDATE SECARA INCREMENTAL
        ↓
BUILD
        ↓
TEST
        ↓
FIX JIKA ERROR
        ↓
BUILD ULANG
        ↓
LANJUT FITUR BERIKUTNYA
```

Tujuan akhir:

```text
APLIKASI ABSENSI KARYAWAN
            ↓
STABIL
            ↓
AMAN
            ↓
RESPONSIVE
            ↓
UI/UX RAPI
            ↓
FUNGSI LENGKAP
            ↓
BUILD SUCCESSFUL
            ↓
APK SIAP DIGUNAKAN
```

---

# 107. DOKUMENTASI PROJECT

**Project:**

```text
AbsensiKaryawan
```

**Package:**

```text
com.example.absensikaryawan
```

**Version:**

```text
1.1
```

**Version Code:**

```text
1
```

**Tahun:**

```text
2026
```

**Dokumen:**

```text
ALUR_APLIKASI.md
```

**Folder:**

```text
DOKUMENTASI/
```

---

# 108. STATUS TERAKHIR

Posisi pengembangan saat dokumentasi dibuat:

```text
✅ Struktur aplikasi
✅ Login
✅ Role Admin / Staff
✅ Dashboard Admin
✅ Dashboard Staff
✅ Firebase Authentication
✅ Firestore
✅ DataStore
✅ Theme
✅ Session Manager
✅ Scanner QR
✅ Validasi QR kantor
✅ Absen Masuk
✅ Absen Pulang
```

Tahap berikutnya mengikuti roadmap:

```text
Riwayat
   ↓
Pengajuan
   ↓
Approval
   ↓
Notifikasi
   ↓
Chat
   ↓
Rekap
   ↓
Karyawan
   ↓
UI/UX Final
   ↓
Testing
   ↓
Build APK
```

---

# 109. CATATAN PENTING

Jika ada perubahan project di masa depan, dokumentasi ini harus ikut diperbarui apabila perubahan tersebut memengaruhi:

```text
Navigation
Screen
Firebase
Firestore
Authentication
Session
DataStore
Theme
QR Scanner
Absensi
Pengajuan
Role
UI/UX
Version
```

Dengan demikian:

```text
KODE PROJECT
     +
DOKUMENTASI
     +
CHANGELOG
```

tetap sinkron.

---

# 110. FINAL

```text
ABSENSI KARYAWAN
       │
       ├── ADMIN
       │     ├── Dashboard
       │     ├── Approval
       │     ├── Karyawan
       │     ├── Rekap
       │     └── Settings
       │
       └── STAFF
             ├── Dashboard
             ├── Pengajuan
             ├── Scan
             ├── Riwayat
             └── Settings
```

Prinsip utama:

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
JANGAN RUSAK FITUR LAMA.
JANGAN RUSAK NAVIGASI.
JANGAN UBAH DESAIN TANPA PERMINTAAN.
TAMBAHKAN FITUR SECARA BERTAHAP.
BUILD → TEST → FIX → BUILD → TEST.
```

**Dokumen ini menjadi master roadmap project AbsensiKaryawan.**

---

**Project:** AbsensiKaryawan
**Package:** com.example.absensikaryawan
**Version:** 1.1
**Version Code:** 1
**Year:** 2026
**Document:** ALUR_APLIKASI.md
