# ALUR APLIKASI ABSENSI KARYAWAN

Dokumentasi master mengenai alur aplikasi, struktur project, fitur, database, navigasi, sistem absensi, QR Scanner, pengajuan, approval, chat, notifikasi, session, theme, testing, dan roadmap pengembangan aplikasi **Absensi Karyawan** berbasis Android.

Dokumen ini menjadi **patokan utama pengembangan project** agar setiap perubahan tetap mempertahankan fitur yang sudah berjalan dan tidak merusak struktur aplikasi.

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

## Admin

Admin digunakan untuk:

```text
Melihat dashboard
Mengelola approval pengajuan
Mengelola data karyawan
Melihat rekap absensi
Mengelola QR kantor
Chat dengan Staff
Melihat notifikasi
Mengatur aplikasi
```

## Staff

Staff digunakan untuk:

```text
Melakukan absensi
Scan QR kantor
Absen masuk
Absen pulang
Absen di luar kantor
Melihat riwayat
Membuat pengajuan
Melihat detail pengajuan
Melihat notifikasi
Chat dengan Admin
Mengatur aplikasi
```

---

# 3. ALUR UTAMA APLIKASI

```text
                    ┌──────────────┐
                    │    APLIKASI  │
                    └──────┬───────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ Cek Session       │
                  └────────┬─────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │    LOGIN    │
                    └──────┬──────┘
                           │
                           ▼
                  Firebase Authentication
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
        AdminNavigation          StaffNavigation
               │                        │
               ▼                        ▼
       Admin Dashboard           Staff Dashboard
```

---

# 4. LOGIN

Screen:

```text
LoginScreen
```

Login menggunakan:

```text
Firebase Authentication
```

Metode:

```text
signInWithEmailAndPassword()
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
      │
      ├── true → ADMIN
      │
      └── false → STAFF
```

---

# 5. PEMISAHAN ROLE

Role user berasal dari:

```text
Firestore
```

Collection:

```text
users
```

Field:

```text
isAdmin
```

Logika:

```text
isAdmin = true
        ↓
ADMIN
```

```text
isAdmin = false
        ↓
STAFF
```

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
isAdmin
      │
 ┌────┴─────┐
 │          │
true      false
 │          │
 ▼          ▼
ADMIN      STAFF
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

# 7. SESSION MANAGER

File:

```text
SessionManager.kt
```

Package:

```text
com.example.absensikaryawan
```

Session menggunakan waktu perangkat.

## Jam Session Persisten

```text
06:00 - 17:59
```

Pada rentang ini session Firebase tetap dipertahankan.

## Jam Session Tidak Persisten

```text
18:00 - 05:59
```

Pada rentang ini aplikasi meminta login kembali ketika aplikasi masuk foreground.

---

# 8. SESSION PERSISTENT

Pada:

```text
06:00 - 17:59
```

user dapat tetap login ketika:

```text
Aplikasi ditutup sementara
Berpindah aplikasi
Aplikasi dibuka kembali
```

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
Tutup / pindah aplikasi
  │
  ▼
Buka kembali
  │
  ▼
Tetap Login
```

---

# 9. SESSION NON-PERSISTENT

Pada:

```text
18:00 - 05:59
```

ketika aplikasi masuk foreground:

```text
MainActivity
      │
      ▼
Lifecycle ON_START
      │
      ▼
SessionManager.shouldRequireLogin()
      │
      ▼
true
      │
      ▼
FirebaseAuth.signOut()
      │
      ▼
Login Screen
```

Yang dihapus hanya session login:

```text
FirebaseAuth.signOut()
```

Akun Firebase tidak dihapus.

Data Firestore juga tidak dihapus.

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

Lifecycle:

```text
ON_START
```

digunakan untuk mengecek apakah user harus login kembali.

Alur:

```text
MainActivity
     │
     ├── enableEdgeToEdge()
     │
     ├── ThemeDataStore
     │
     ├── SessionManager
     │
     └── AppNavigation()
```

---

# 12. APP NAVIGATION

File:

```text
AppNavigation.kt
```

Navigation menggunakan:

```text
Enum-based Navigation
```

Tidak menggunakan:

```text
NavHost
```

Role:

```text
AppRole.NONE
AppRole.ADMIN
AppRole.STAFF
```

Alur:

```text
NONE
 │
 ▼
LOGIN
 │
 ├── ADMIN
 │
 │     ▼
 │ AdminNavigation
 │
 └── STAFF
       ▼
     StaffNavigation
```

---

# 13. STRUKTUR NAVIGATION

```text
AppNavigation
│
├── AppRole.NONE
│      ↓
│    Login / Forgot Password
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

# 14. ADMIN NAVIGATION

File:

```text
AdminNavigation.kt
```

Bottom navigation:

```text
┌──────────┬──────────┬──────────┬──────────┬──────────┐
│ Beranda  │ Approval │ Karyawan │  Rekap   │ Setting  │
└──────────┴──────────┴──────────┴──────────┴──────────┘
```

Menu utama:

```text
Beranda
Approval
Karyawan
Rekap
Setting
```

---

# 15. STAFF NAVIGATION

File:

```text
StaffNavigation.kt
```

Bottom navigation:

```text
┌──────────┬────────────┬────────┬──────────┬──────────┐
│ Beranda  │ Pengajuan  │  Scan  │ Riwayat  │ Setting  │
└──────────┴────────────┴────────┴──────────┴──────────┘
```

Menu utama:

```text
Beranda
Pengajuan
Scan
Riwayat
Setting
```

---

# 16. SCREEN ADMIN

Screen utama Admin:

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
AdminChatListScreen
AdminChatDetailScreen
```

Fitur tambahan Admin:

```text
QR Settings
Notifikasi
Chat Staff
```

---

# 17. SCREEN STAFF

Screen utama Staff:

```text
StaffDashboardScreen
ScanAbsenScreen
AbsenLuarKantorScreen
PengajuanScreen
PengajuanBaruScreen
DetailPengajuan
RiwayatPengajuan
RiwayatScreen
Settings
Tampilan
Notifikasi
ChatAdmin
Bantuan
TentangAplikasi
Profile
```

---

# 18. ADMIN DASHBOARD

Screen:

```text
AdminDashboardScreen
```

Dashboard menjadi pusat aktivitas Admin.

Fungsi:

```text
Ringkasan aplikasi
Akses Approval
Akses Karyawan
Akses Rekap
Akses Settings
Akses notifikasi
Akses chat
```

Dashboard tidak menampilkan informasi versi sebagai elemen utama.

Informasi versi berada pada:

```text
Login
Settings
Tentang Aplikasi
```

---

# 19. ADMIN APPROVAL

Screen:

```text
ApprovalScreen
```

Digunakan untuk mengelola pengajuan Staff.

Alur:

```text
STAFF
  │
  ▼
Membuat Pengajuan
  │
  ▼
Firestore
  │
  ▼
ADMIN APPROVAL
  │
  ├── SETUJUI
  │
  └── TOLAK
```

Status:

```text
Menunggu
Disetujui
Ditolak
```

Setelah Admin mengambil keputusan:

```text
Status Pengajuan
       │
       ▼
Notification Staff
```

---

# 20. ADMIN KARYAWAN

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

Dialog:

```text
TambahKaryawanDialog
```

Data user berada di:

```text
users
```

---

# 21. DATA KARYAWAN

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

Field dapat berkembang sesuai kebutuhan project.

---

# 22. PASSWORD KARYAWAN

Password Firebase Authentication tidak dapat dibaca atau ditampilkan kembali oleh Admin.

Password dikelola oleh:

```text
Firebase Authentication
```

Jika lupa:

```text
ForgotPasswordScreen
```

Aplikasi tidak menyediakan fitur melihat password asli.

---

# 23. ADMIN REKAP

Screen:

```text
AdminRekapScreen
```

Sumber data:

```text
attendance
```

Data dapat meliputi:

```text
Nama
Tanggal
Jam Masuk
Jam Pulang
QR / Kantor
Status
Catatan
```

---

# 24. ADMIN SETTINGS

Screen:

```text
AdminSettingsScreen
```

Menu:

```text
Profile
Tampilan
QR Kantor
Bantuan
Tentang Aplikasi
Keluar
```

---

# 25. ADMIN QR SETTINGS

Admin memiliki pengaturan QR kantor.

Collection:

```text
qr_settings
```

Struktur:

```text
qr_settings
│
├── malang
│   ├── officeName
│   ├── qrData
│   └── aktif
│
├── blitar
│   ├── officeName
│   ├── qrData
│   └── aktif
│
└── kediri
    ├── officeName
    ├── qrData
    └── aktif
```

Admin dapat mengatur:

```text
QR aktif
QR nonaktif
Nama kantor
Data QR
```

Scanner Staff membaca konfigurasi ini dari Firestore.

---

# 26. QR KANTOR RESMI

QR kantor yang digunakan project saat ini:

## Malang

```text
https://q.me-qr.com/x5ie23mg
```

## Blitar

```text
https://q.me-qr.com/hbywvgy7
```

## Kediri

```text
https://q.me-qr.com/14vy2ipr
```

QR tersebut menjadi data QR kantor yang dapat didaftarkan pada:

```text
qr_settings
```

Status:

```text
aktif = true
```

menentukan apakah QR dapat digunakan scanner.

---

# 27. ADMIN PROFILE

Screen:

```text
AdminProfile
```

Digunakan untuk menampilkan informasi profile Admin.

---

# 28. ADMIN TAMPILAN

Screen:

```text
AdminTampilan
```

Pilihan:

```text
TERANG
GELAP
SISTEM
```

Penyimpanan:

```text
ThemeDataStore
```

---

# 29. ADMIN BANTUAN

Screen:

```text
AdminBantuan
```

Berisi informasi bantuan penggunaan aplikasi untuk Admin.

---

# 30. ADMIN TENTANG

Screen:

```text
AdminTentangAplikasi
```

Informasi versi:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 31. ADMIN CHAT

Admin memiliki sistem chat dengan Staff.

Screen:

```text
AdminChatListScreen
AdminChatDetailScreen
```

Alur:

```text
Admin
  │
  ▼
Daftar Chat Staff
  │
  ▼
Pilih Staff
  │
  ▼
Chat Detail
  │
  ▼
Balas Pesan
  │
  ▼
Firestore
```

---

# 32. STAFF DASHBOARD

Screen:

```text
StaffDashboardScreen
```

Dashboard menampilkan informasi utama Staff:

```text
Nama
Tanggal
Jam real-time
Status absensi
Jam masuk
Jam pulang
Akses Scan
Akses Absen Luar Kantor
Notifikasi
Profile
```

Dashboard menjadi pusat aktivitas Staff.

---

# 33. JAM REAL-TIME

Format:

```text
HH:mm:ss
```

Contoh:

```text
07:35:21
```

Jam diperbarui secara berkala sehingga Staff dapat melihat waktu terkini.

---

# 34. STATUS KEHADIRAN

Status utama:

```text
BELUM ABSEN
```

```text
SUDAH ABSEN
```

Dashboard mengecek data:

```text
uid
+
tanggal hari ini
```

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
    ├── Tidak ditemukan
    │       ↓
    │   BELUM ABSEN
    │
    └── Ditemukan
            ↓
       SUDAH ABSEN
```

---

# 35. NOTIFIKASI STAFF

Staff memiliki screen:

```text
Notifikasi
```

Notifikasi dapat berasal dari:

```text
Admin membalas chat
Pengajuan disetujui
Pengajuan ditolak
```

Alur:

```text
Event
 │
 ▼
NotificationRepository
 │
 ▼
Firestore notifications
 │
 ▼
Notifikasi Staff
```

Notifikasi yang belum dibaca memiliki indikator:

```text
Baru
```

Ketika notifikasi dibuka:

```text
read = true
```

---

# 36. NOTIFIKASI ADMIN

Admin juga memiliki notifikasi.

Event yang dapat menghasilkan notifikasi:

```text
Staff mengirim chat
Staff membuat pengajuan
Staff melakukan absensi
```

Alur:

```text
Staff
  │
  ├── Chat
  │
  ├── Pengajuan
  │
  └── Absensi
       │
       ▼
NotificationRepository
       │
       ▼
Notifikasi Admin
```

---

# 37. SCAN QR

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

Scanner hanya memproses:

```text
QR_CODE
```

Scanner memiliki:

```text
QR Detection
Bounding Box
Corner Points
Scanner Frame
Scanner Line
Validation Progress
QR Lock
Reset Scanner
```

---

# 38. ALUR QR SCANNER

```text
Staff
  │
  ▼
ScanAbsenScreen
  │
  ▼
Load qr_settings
  │
  ├── Loading
  │
  ├── Error
  │
  └── Data berhasil
          │
          ▼
     Scanner Aktif
          │
          ▼
       Kamera
          │
          ▼
     ML Kit QR Detection
          │
          ▼
     Validasi Posisi QR
          │
          ▼
     Validasi Ukuran QR
          │
          ▼
     Validasi Overlap Frame
          │
          ▼
     Validasi Multi Frame
          │
          ▼
        QR LOCK
          │
          ▼
    Cocokkan qrData
          │
       ┌──┴──┐
       │     │
    INVALID VALID
       │     │
       ▼     ▼
     Tolak  Lanjut
```

---

# 39. QR SETTINGS FIRESTORE

Scanner tidak menggunakan daftar QR hard-coded sebagai satu-satunya sumber validasi.

Scanner membaca:

```text
qr_settings
```

Setiap data harus:

```text
qrData tidak kosong
officeName tersedia
aktif = true
```

Jika tidak ada QR aktif:

```text
Belum ada QR kantor aktif.
Hubungi Admin untuk mengatur QR kantor.
```

Jika Firestore gagal:

```text
Gagal mengambil pengaturan QR kantor.
Scanner tidak dapat digunakan.
```

---

# 40. NORMALISASI QR

Sebelum dibandingkan:

```text
Trim
Remove trailing slash
Ignore case
```

Contoh:

```text
https://q.me-qr.com/x5ie23mg/
```

dinormalisasi menjadi:

```text
https://q.me-qr.com/x5ie23mg
```

Tujuannya agar perbedaan kecil pada format URL tidak menyebabkan QR valid ditolak.

---

# 41. VALIDASI UKURAN QR

Scanner menggunakan batas ukuran minimum:

```text
QR_MIN_SIZE_DP = 45
```

QR yang terlalu kecil tidak langsung diproses.

Scanner juga memiliki batas ukuran maksimum relatif terhadap frame scanner.

Tujuan:

```text
QR harus cukup jelas
QR harus berada dalam area scanner
QR tidak terlalu jauh
```

---

# 42. VALIDASI POSISI QR

QR harus berada pada area scanner.

Scanner melakukan pemeriksaan:

```text
Center QR
Bounding Box
Overlap dengan scanner frame
```

Minimum overlap:

```text
80%
```

Jika QR terlalu keluar dari frame:

```text
Belum valid
```

---

# 43. MULTI-FRAME VALIDATION

Scanner tidak langsung mengunci QR pada satu frame.

Jumlah frame yang dibutuhkan:

```text
3 frame valid
```

Konsep:

```text
Frame 1
   ↓
Frame 2
   ↓
Frame 3
   ↓
QR LOCK
```

Scanner juga memeriksa perpindahan posisi QR agar QR relatif stabil.

Tracking distance:

```text
40dp
```

Tujuannya:

```text
Mengurangi false detection
Mengurangi QR yang terbaca sesaat
Membuat scanner lebih stabil
```

---

# 44. QR LOCK

Setelah QR valid:

```text
scanLock = true
```

Scanner tidak memproses QR yang sama berkali-kali secara bersamaan.

Status:

```text
QR TERKUNCI
```

Setelah berhasil atau user melakukan reset:

```text
scanLock = false
```

---

# 45. QR INVALID

Jika QR terbaca tetapi tidak ada pada daftar QR aktif:

```text
QR Code tidak terdaftar sebagai QR kantor aktif.
```

State scan di-reset sehingga user dapat mencoba QR lain.

QR invalid tidak disimpan sebagai absensi.

---

# 46. QR VALID

Jika QR cocok dengan `qr_settings` aktif:

```text
QR VALID
```

Scanner mengambil:

```text
qrData
officeName
```

Kemudian menampilkan:

```text
KANTOR MALANG
```

atau:

```text
KANTOR BLITAR
```

atau:

```text
KANTOR KEDIRI
```

---

# 47. RESET SCANNER

Scanner memiliki mekanisme reset.

Reset digunakan ketika:

```text
QR invalid
User ingin scan ulang
Proses scan perlu diulang
```

Reset mengembalikan:

```text
Validation Progress
Bounding Box
Corner Points
Scan Lock
```

ke kondisi siap scan.

---

# 48. ABSEN MASUK

Alur utama:

```text
Staff Dashboard
      │
      ▼
Scan
      │
      ▼
Camera
      │
      ▼
QR Detection
      │
      ▼
QR Validation
      │
      ▼
Office Detection
      │
      ▼
Konfirmasi
      │
      ▼
Cek Attendance Hari Ini
      │
      ▼
Belum Absen
      │
      ▼
Simpan Jam Masuk
      │
      ▼
Firestore
      │
      ▼
DataStore
      │
      ▼
Dashboard Refresh
```

---

# 49. DATA ABSEN MASUK

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
uid       : UID USER
nama      : Nama Staff
tanggal   : 2026-09-11
jamMasuk  : 07:30:12
jamPulang : ""
qrData    : https://q.me-qr.com/x5ie23mg
catatan   : ""
```

---

# 50. CEK ABSENSI HARI INI

Identifikasi absensi:

```text
uid
+
tanggal
```

Contoh:

```text
uid = ABC123
tanggal = 2026-09-11
```

Data ini digunakan untuk:

```text
Dashboard
Absen Masuk
Absen Pulang
Riwayat
Admin Rekap
```

---

# 51. ABSEN PULANG

Setelah Staff memiliki absensi masuk:

```text
SUDAH ABSEN
```

Staff dapat melakukan absensi pulang.

Alur:

```text
Dashboard
    │
    ▼
Scan Absen Pulang
    │
    ▼
Scanner
    │
    ▼
QR Valid
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
```

---

# 52. DATA ABSEN PULANG

Data yang diperbarui:

```text
jamPulang
```

Contoh:

```text
jamMasuk  : 07:30:12
jamPulang : 16:05:21
```

Absensi pulang memperbarui data attendance yang sudah ada.

Tidak boleh membuat attendance baru secara tidak sengaja.

---

# 53. ABSEN DI LUAR KANTOR

Screen:

```text
AbsenLuarKantorScreen
```

Digunakan ketika Staff tidak melakukan absensi menggunakan QR kantor.

Alur:

```text
Staff
  │
  ▼
Absen Luar Kantor
  │
  ▼
Validasi User
  │
  ▼
Ambil Nama User
  │
  ▼
Tanggal + Waktu
  │
  ▼
Cek Attendance Hari Ini
  │
  ├── Sudah ada
  │      ↓
  │     Tolak
  │
  └── Belum ada
         ↓
      Isi Lokasi
         ↓
       Isi Alasan
         ↓
      Simpan Absen
         │
       ┌─┴─────────┐
       ▼           ▼
   Firestore    DataStore
```

---

# 54. DATA ABSEN LUAR KANTOR

Untuk absensi luar kantor:

```text
qrData = LUAR_KANTOR
```

Catatan dapat berisi:

```text
Lokasi: ...
Alasan: ...
```

Data tetap mengikuti identifikasi:

```text
uid + tanggal
```

---

# 55. DATASTORE ABSENSI

File:

```text
AbsensiDataStore.kt
```

Digunakan untuk kebutuhan lokal aplikasi.

Key yang digunakan antara lain:

```text
sudah_absen
jam_absen
tanggal_absen
jam_pulang
qr_absen
catatan_absen
```

Firestore tetap menjadi sumber utama data cloud.

DataStore digunakan sebagai pendukung state lokal aplikasi.

---

# 56. DASHBOARD MEMBACA ABSENSI

Dashboard melakukan:

```text
uid
+
tanggal hari ini
```

Kemudian:

```text
Firestore attendance
```

Jika ditemukan:

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
Sudah Absen Masuk
Belum Absen Pulang
```

Jika `jamPulang` terisi:

```text
Absensi Lengkap
```

---

# 57. RESET ABSENSI HARIAN

Status absensi mengikuti tanggal.

```text
Tanggal Lama
     │
     ▼
Tanggal Baru
     │
     ▼
Cek attendance tanggal baru
     │
     ▼
Status absensi baru
```

Data tanggal sebelumnya tetap tersimpan di Firestore.

---

# 58. RIWAYAT STAFF

Screen:

```text
RiwayatScreen
```

Tab/jenis data:

```text
Absensi
Pengajuan
```

Data absensi dapat menampilkan:

```text
Tanggal
Jam Masuk
Jam Pulang
Kantor
Status
Catatan
```

Data pengajuan dapat menampilkan:

```text
Tanggal
Jenis
Status
```

---

# 59. PENGAJUAN STAFF

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

# 60. PENGAJUAN BARU

Screen:

```text
PengajuanBaruScreen
```

Jenis pengajuan dapat meliputi:

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
   │
   ▼
Status = Menunggu
```

---

# 61. DATA PENGAJUAN

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

Status:

```text
menunggu
disetujui
ditolak
```

---

# 62. DETAIL PENGAJUAN

Screen:

```text
DetailPengajuan
```

Data yang ditampilkan:

```text
Jenis
Tanggal
Waktu
Alasan
Status
```

---

# 63. RIWAYAT PENGAJUAN

Screen:

```text
RiwayatPengajuan
```

Digunakan untuk melihat pengajuan yang pernah dibuat Staff.

---

# 64. ALUR APPROVAL

```text
Staff
  │
  ▼
Pengajuan Baru
  │
  ▼
Firestore
  │
  ▼
Status = Menunggu
  │
  ▼
Admin
  │
  ▼
Approval
  │
 ┌┴───────────────┐
 ▼                ▼
Setujui          Tolak
 │                │
 ▼                ▼
Disetujui        Ditolak
 │                │
 └───────┬────────┘
         ▼
   Notification Staff
         │
         ▼
      Staff App
```

---

# 65. NOTIFICATION REPOSITORY

File:

```text
NotificationRepository.kt
```

Repository digunakan sebagai perantara pengelolaan notifikasi.

Konsep:

```text
Event
  │
  ▼
NotificationRepository
  │
  ▼
Firestore
  │
  ▼
Notification Screen
```

---

# 66. CHAT STAFF ↔ ADMIN

Sistem chat mendukung komunikasi dua arah.

## Staff

Screen:

```text
ChatAdminScreen
```

## Admin

Screen:

```text
AdminChatListScreen
AdminChatDetailScreen
```

---

# 67. DATA CHAT

Model:

```text
ChatMessage
```

Field:

```text
id
senderId
senderName
senderType
message
timestamp
```

Data disimpan pada struktur:

```text
chatRooms/{uid}/messages
```

Pesan diurutkan berdasarkan:

```text
timestamp
```

---

# 68. ALUR CHAT STAFF

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
ChatRepository
  │
  ▼
Firestore
  │
  ▼
Admin
```

---

# 69. ALUR CHAT ADMIN

```text
Admin
  │
  ▼
Admin Chat List
  │
  ▼
Pilih Staff
  │
  ▼
Admin Chat Detail
  │
  ▼
Tulis Balasan
  │
  ▼
Kirim
  │
  ▼
ChatRepository
  │
  ▼
Firestore
  │
  ▼
Staff
```

---

# 70. NOTIFIKASI CHAT

Jika Admin membalas pesan Staff:

```text
Admin
  │
  ▼
Kirim Chat
  │
  ▼
Firestore
  │
  ▼
Notification
  │
  ▼
Staff
```

Jika Staff mengirim chat:

```text
Staff
  │
  ▼
Kirim Chat
  │
  ▼
Firestore
  │
  ▼
Notification
  │
  ▼
Admin
```

---

# 71. THEME SYSTEM

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

`ThemeMode` berada pada:

```text
com.example.absensikaryawan.screens
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
Preference
      │
      ▼
AbsensiKaryawanTheme
```

---

# 72. PROFILE STAFF

Profile dapat diakses dari Dashboard.

Screen:

```text
Profile
```

Profile tidak menjadi menu utama bottom navigation.

---

# 73. STAFF SETTINGS

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

Profile tetap berada pada area Dashboard.

---

# 74. STAFF TAMPILAN

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

Disimpan melalui:

```text
ThemeDataStore
```

---

# 75. STAFF BANTUAN

Screen:

```text
Bantuan
```

Berisi informasi penggunaan aplikasi untuk Staff.

---

# 76. STAFF TENTANG APLIKASI

Screen:

```text
TentangAplikasi
```

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 77. FIREBASE

Firebase digunakan untuk:

```text
Authentication
Cloud Firestore
```

Struktur utama:

```text
Firebase
│
├── Authentication
│
└── Firestore
     │
     ├── users
     ├── attendance
     ├── pengajuan
     ├── qr_settings
     ├── chatRooms
     └── notifications
```

---

# 78. FIREBASE AUTHENTICATION

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

# 79. USERS

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

`isAdmin` menentukan role.

---

# 80. ATTENDANCE

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

Digunakan oleh:

```text
Staff Dashboard
Scan
Absen
Riwayat
Admin Rekap
Notifikasi Admin
```

---

# 81. PENGAJUAN

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
Riwayat
```

---

# 82. QR_SETTINGS

Collection:

```text
qr_settings
```

Field:

```text
officeName
qrData
aktif
```

Digunakan oleh:

```text
Admin QR Settings
ScanAbsenScreen
Validasi QR
Deteksi Kantor
```

---

# 83. CHATROOMS

Struktur:

```text
chatRooms
   │
   └── {uid}
        │
        └── messages
```

Field message:

```text
id
senderId
senderName
senderType
message
timestamp
```

---

# 84. NOTIFICATIONS

Collection konseptual:

```text
notifications
```

Digunakan untuk menyimpan informasi notifikasi user.

Event:

```text
Chat
Pengajuan
Absensi
```

Notifikasi memiliki status baca/tidak baca.

---

# 85. USER REPOSITORY

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
UserRepository
     │
     ▼
User Data
```

---

# 86. FIRESTORE REPOSITORY

File:

```text
FirestoreRepository.kt
```

Repository menjadi perantara antara UI dan Firestore.

Contoh fungsi:

```text
getAbsenHariIni()
simpanAbsenMasuk()
updateAbsenPulang()
simpanAbsenLuarKantor()
simpanPengajuan()
getPengajuan()
```

Konsep:

```text
Screen
  │
  ▼
Repository
  │
  ▼
Firestore
```

---

# 87. CHAT REPOSITORY

File:

```text
ChatRepository.kt
```

Digunakan untuk:

```text
Mengirim pesan
Membaca pesan
Listen pesan
Mengelola chat room
```

Alur:

```text
Screen
  │
  ▼
ChatRepository
  │
  ▼
Firestore
```

---

# 88. NOTIFICATION REPOSITORY

File:

```text
NotificationRepository.kt
```

Digunakan untuk:

```text
Membuat notifikasi
Membaca notifikasi
Menandai notifikasi sebagai dibaca
Mendengarkan perubahan notifikasi
```

---

# 89. STRUKTUR FILE UTAMA

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
        │       ├── ThemeDataStore.kt
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
        │       │   ├── AbsenLuarKantorScreen.kt
        │       │   ├── RiwayatScreen.kt
        │       │   ├── PengajuanScreen.kt
        │       │   ├── PengajuanBaruScreen.kt
        │       │   ├── DetailPengajuan.kt
        │       │   ├── RiwayatPengajuan.kt
        │       │   ├── Notifikasi.kt
        │       │   ├── ChatAdmin.kt
        │       │   └── ...
        │       │
        │       ├── repository/
        │       │   ├── UserRepository.kt
        │       │   ├── FirestoreRepository.kt
        │       │   ├── ChatRepository.kt
        │       │   └── NotificationRepository.kt
        │       │
        │       ├── datastore/
        │       │   └── AbsensiDataStore.kt
        │       │
        │       ├── models/
        │       │   ├── ChatMessage.kt
        │       │   └── ...
        │       │
        │       └── ui/
        │           └── theme/
        │               ├── Theme.kt
        │               └── ...
        │
        └── res/
```

---

# 90. STRUKTUR NAVIGASI ADMIN

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
       ├── QR Kantor
       ├── Bantuan
       ├── Tentang Aplikasi
       └── Keluar
```

Fitur tambahan:

```text
Notifikasi
Chat Staff
```

---

# 91. STRUKTUR NAVIGASI STAFF

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
 │     ├── Absen Pulang
 │     └── Absen Luar Kantor
 │
 ├── Riwayat
 │
 └── Setting
       ├── Notifikasi
       ├── Tampilan
       ├── Bantuan
       ├── Tentang Aplikasi
       └── Keluar
```

---

# 92. ALUR ABSENSI LENGKAP

```text
LOGIN
  │
  ▼
STAFF DASHBOARD
  │
  ▼
SCAN
  │
  ▼
LOAD QR SETTINGS
  │
  ▼
CAMERA
  │
  ▼
DETEKSI QR
  │
  ▼
VALIDASI UKURAN
  │
  ▼
VALIDASI POSISI
  │
  ▼
VALIDASI OVERLAP
  │
  ▼
VALIDASI 3 FRAME
  │
  ▼
QR LOCK
  │
  ▼
VALIDASI QR FIRESTORE
  │
  ├── INVALID
  │     ↓
  │    TOLAK
  │
  └── VALID
        │
        ▼
   DETEKSI KANTOR
        │
        ▼
   CEK ABSENSI HARI INI
        │
        ├── BELUM ADA
        │      ↓
        │   ABSEN MASUK
        │      ↓
        │   FIRESTORE
        │
        └── SUDAH ADA
               ↓
          ABSEN PULANG
               ↓
          UPDATE FIRESTORE
               │
               ▼
           DASHBOARD
```

---

# 93. ALUR PENGAJUAN LENGKAP

```text
STAFF
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
Status Menunggu
  │
  ▼
Notification Admin
  │
  ▼
Admin Approval
  │
 ┌┴──────────────┐
 ▼               ▼
Setujui         Tolak
 │               │
 ▼               ▼
Approved        Rejected
 │               │
 └──────┬────────┘
        ▼
Notification Staff
        │
        ▼
Staff melihat status
```

---

# 94. ALUR CHAT LENGKAP

```text
                    CHAT
                     │
          ┌──────────┴──────────┐
          │                     │
        STAFF                  ADMIN
          │                     │
          ▼                     ▼
    ChatAdminScreen       AdminChatList
          │                     │
          ▼                     ▼
    Kirim Pesan            Pilih Staff
          │                     │
          ▼                     ▼
       Firestore            Chat Detail
          │                     │
          └──────────┬──────────┘
                     ▼
               ChatRepository
                     │
                     ▼
             Pesan tersinkron
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
        STAFF                  ADMIN
```

---

# 95. ALUR NOTIFIKASI LENGKAP

```text
                         EVENT
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
       Chat            Pengajuan         Absensi
          │                │                │
          └────────────────┼────────────────┘
                           ▼
                NotificationRepository
                           │
                           ▼
                       Firestore
                           │
                 ┌─────────┴─────────┐
                 ▼                   ▼
              STAFF                ADMIN
                 │                   │
                 ▼                   ▼
           Notifikasi            Notifikasi
                 │                   │
                 ▼                   ▼
             Read/Unread          Read/Unread
```

---

# 96. ALUR DATA APLIKASI

```text
                     ANDROID
                        │
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
    Firebase        Firestore        DataStore
      Auth              │                │
        │               │                │
        │          ┌────┼────┐           │
        │          │    │    │           │
        │          ▼    ▼    ▼           │
        │        users attendance        │
        │               │    │           │
        │          pengajuan             │
        │          qr_settings           │
        │          chatRooms             │
        │          notifications         │
        │                                │
        └──────────────┬─────────────────┘
                       ▼
                  Application UI
```

---

# 97. STATUS BAR DAN SYSTEM UI

Aplikasi menggunakan:

```text
enableEdgeToEdge()
```

Karena itu setiap screen harus memperhatikan:

```text
Status Bar
Content
Navigation Bar
```

Target:

```text
Content tidak tertutup status bar Android.
Header tidak bertabrakan dengan status bar.
Bottom navigation tidak tertutup navigation bar.
```

Jika diperlukan digunakan:

```text
WindowInsets
```

Contoh:

```text
WindowInsets.statusBars
WindowInsets.navigationBars
```

Perbaikan system bar harus dilakukan secara hati-hati agar tidak merusak desain screen yang sudah stabil.

---

# 98. UI/UX

Target UI:

```text
Simple
Modern
Responsive
Konsisten
Mudah digunakan
```

Yang diperhatikan:

```text
Spacing
Padding
Margin
Typography
Icon
Button
Card
Alignment
Header
Bottom Navigation
Status Bar
Navigation Bar
Dialog
Popup
Scanner
```

Perubahan desain tidak dilakukan tanpa permintaan.

---

# 99. TESTING LOGIN

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

# 100. TESTING SESSION

```text
[ ] 06:00 session persisten
[ ] 17:59 session persisten
[ ] 18:00 login kembali diperlukan
[ ] 05:59 login kembali diperlukan
[ ] Firebase account tidak dihapus
[ ] Login kembali berhasil
```

---

# 101. TESTING QR SCANNER

```text
[ ] Permission kamera muncul
[ ] Kamera aktif
[ ] QR Malang valid
[ ] QR Blitar valid
[ ] QR Kediri valid
[ ] QR tidak terdaftar ditolak
[ ] QR invalid ditolak
[ ] QR terlalu kecil ditolak
[ ] QR terlalu keluar frame ditolak
[ ] QR tidak stabil tidak langsung lock
[ ] 3 frame valid menghasilkan lock
[ ] Scanner tidak double process
[ ] Reset scanner berjalan
[ ] Popup valid tampil
[ ] Popup invalid tampil
```

---

# 102. TESTING ABSEN MASUK

```text
[ ] Staff belum absen
[ ] Scan QR berhasil
[ ] QR valid
[ ] Kantor terdeteksi
[ ] Jam masuk tersimpan
[ ] Tanggal tersimpan
[ ] UID tersimpan
[ ] Nama tersimpan
[ ] Data masuk Firestore
[ ] Dashboard berubah menjadi SUDAH ABSEN
```

---

# 103. TESTING ABSEN PULANG

```text
[ ] Staff sudah absen masuk
[ ] Scan pulang tersedia
[ ] Scanner dapat digunakan kembali
[ ] QR valid
[ ] Jam pulang tersimpan
[ ] Attendance hari ini diperbarui
[ ] Tidak membuat data attendance baru
[ ] Dashboard menampilkan jam pulang
```

---

# 104. TESTING ABSEN LUAR KANTOR

```text
[ ] User terdeteksi
[ ] Nama user tersedia
[ ] Tanggal benar
[ ] Waktu benar
[ ] Attendance hari ini dicek
[ ] Jika sudah absen → ditolak
[ ] Lokasi dapat diisi
[ ] Alasan dapat diisi
[ ] Data tersimpan
[ ] Firestore tersimpan
[ ] DataStore tersimpan
```

---

# 105. TESTING PENGAJUAN

```text
[ ] Pengajuan dapat dibuka
[ ] Pengajuan Baru dapat dibuka
[ ] Form dapat diisi
[ ] Data valid
[ ] Submit berhasil
[ ] Data masuk Firestore
[ ] Status Menunggu
[ ] Admin menerima pengajuan
[ ] Admin dapat approve
[ ] Admin dapat reject
[ ] Staff melihat status
```

---

# 106. TESTING NOTIFIKASI

```text
[ ] Notifikasi Staff tampil
[ ] Notifikasi Admin tampil
[ ] Chat menghasilkan notifikasi
[ ] Pengajuan menghasilkan notifikasi
[ ] Absensi menghasilkan notifikasi Admin
[ ] Badge unread tampil
[ ] Membuka notifikasi mengubah read
```

---

# 107. TESTING CHAT

```text
[ ] Staff dapat membuka chat
[ ] Staff dapat mengirim pesan
[ ] Admin menerima pesan
[ ] Admin dapat membalas
[ ] Staff menerima balasan
[ ] Timestamp tersimpan
[ ] Pesan tersinkron
[ ] Chat room benar
```

---

# 108. TESTING NAVIGATION STAFF

```text
[ ] Beranda
[ ] Pengajuan
[ ] Scan
[ ] Riwayat
[ ] Setting
[ ] Profile
[ ] Notifikasi
[ ] Absen Luar Kantor
[ ] Pengajuan Baru
[ ] Detail Pengajuan
[ ] Riwayat Pengajuan
[ ] Chat Admin
[ ] Tampilan
[ ] Bantuan
[ ] Tentang
[ ] Logout
```

---

# 109. TESTING NAVIGATION ADMIN

```text
[ ] Beranda
[ ] Approval
[ ] Karyawan
[ ] Rekap
[ ] Setting
[ ] Profile
[ ] Tampilan
[ ] QR Kantor
[ ] Bantuan
[ ] Tentang
[ ] Notifikasi
[ ] Chat Staff
[ ] Logout
```

---

# 110. FINAL BUILD

Urutan:

```text
Clean Project
      │
      ▼
Rebuild Project
      │
      ▼
Run Application
      │
      ▼
Testing
      │
      ▼
Fix Bug
      │
      ▼
Rebuild
      │
      ▼
Final Testing
      │
      ▼
Generate APK
```

Target:

```text
BUILD SUCCESSFUL
```

---

# 111. BACKUP PROJECT

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

Repository:

```text
AbsensiKaryawan
```

---

# 112. CHANGELOG

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
11-09-2026
10:00
Scan QR
Perbaikan parameter QR Settings dan scanner validation
Status: BUILD SUCCESSFUL
```

---

# 113. STATUS FITUR TERKINI

## Core System

```text
✅ Login Firebase
✅ Firebase Authentication
✅ Forgot Password
✅ Role Admin
✅ Role Staff
✅ AppNavigation
✅ AdminNavigation
✅ StaffNavigation
✅ SessionManager
```

## Admin

```text
✅ Admin Dashboard
✅ Approval
✅ Karyawan
✅ Rekap
✅ Settings
✅ Profile
✅ Tampilan
✅ Bantuan
✅ Tentang Aplikasi
✅ QR Settings
✅ Chat Staff
✅ Notifikasi
```

## Staff

```text
✅ Staff Dashboard
✅ Scan QR
✅ Absen Masuk
✅ Absen Pulang
✅ Absen Luar Kantor
✅ Pengajuan
✅ Pengajuan Baru
✅ Detail Pengajuan
✅ Riwayat Pengajuan
✅ Riwayat Absensi
✅ Settings
✅ Profile
✅ Tampilan
✅ Bantuan
✅ Tentang Aplikasi
✅ Notifikasi
✅ Chat Admin
```

## Backend

```text
✅ Firebase Authentication
✅ Cloud Firestore
✅ users
✅ attendance
✅ pengajuan
✅ qr_settings
✅ chatRooms
✅ notifications
```

## Local Storage

```text
✅ AbsensiDataStore
✅ ThemeDataStore
```

## QR Scanner

```text
✅ CameraX
✅ ML Kit
✅ QR-only scanning
✅ QR whitelist Firestore
✅ Active QR validation
✅ Office detection
✅ Bounding Box
✅ Corner Points
✅ Scanner Frame
✅ Scanner Line
✅ 3-frame validation
✅ QR Lock
✅ QR Reset
✅ Invalid QR rejection
```

---

# 114. POSISI PROJECT SAAT INI

Project sudah memiliki fondasi aplikasi utama:

```text
                    ABSENSI KARYAWAN
                           │
             ┌─────────────┴─────────────┐
             │                           │
           ADMIN                        STAFF
             │                           │
       ┌─────┼─────┐             ┌───────┼────────┐
       │     │     │             │       │        │
   Approval Karyawan Rekap    Scan  Pengajuan Riwayat
       │     │     │             │       │
       └─────┴─────┘             │       │
             │                   │       │
          QR Settings            │       │
             │                   │       │
          Chat/Notif             │       │
             │                   │       │
          Settings               │       │
                                 │       │
                         Chat / Notifikasi
                                 │
                              Settings
```

---

# 115. FITUR YANG SUDAH STABIL

Fitur yang sudah memiliki fondasi dan tidak boleh dirusak:

```text
Login
Role
Navigation
Dashboard
Firebase
Firestore
DataStore
Theme
Session
QR Settings
QR Scanner
QR Validation
Absensi
Pengajuan
Approval
Chat
Notifikasi
Settings
```

Setiap perubahan berikutnya harus mempertahankan fitur tersebut.

---

# 116. PRIORITAS PENGEMBANGAN BERIKUTNYA

Karena fitur utama sudah tersedia, tahap berikutnya berfokus pada:

```text
1. Testing menyeluruh
        ↓
2. Fix bug yang ditemukan
        ↓
3. Sinkronisasi data Firestore
        ↓
4. Validasi edge case absensi
        ↓
5. Validasi notifikasi
        ↓
6. Validasi chat
        ↓
7. UI/UX final
        ↓
8. System Bar / Android compatibility
        ↓
9. Final Testing
        ↓
10. Build APK
        ↓
11. Backup GitHub
```

---

# 117. POLA PENGEMBANGAN

Setiap fitur menggunakan pola:

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
PERBAIKAN MINIMAL
    │
    ▼
BUILD
    │
    ▼
TEST
    │
    ├── ERROR
    │     ↓
    │    FIX
    │     │
    │     └──────► BUILD
    │
    └── SUCCESS
          │
          ▼
       NEXT STEP
```

---

# 118. PRINSIP PENGEMBANGAN UTAMA

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
```

```text
JANGAN MERUSAK FITUR LAMA.
```

```text
JANGAN MERUSAK NAVIGASI.
```

```text
JANGAN MENGUBAH DESAIN TANPA PERMINTAAN.
```

```text
JANGAN MENGGANTI STRUKTUR YANG SUDAH STABIL TANPA ALASAN.
```

```text
TAMBAHKAN FITUR SECARA BERTAHAP.
```

```text
PERBAIKI BAGIAN YANG BERMASALAH SAJA.
```

```text
BUILD → TEST → FIX → BUILD → TEST
```

---

# 119. CHECKLIST SEBELUM CODING

```text
[ ] Tentukan fitur yang akan dikerjakan
[ ] Tentukan screen terkait
[ ] Tentukan file terkait
[ ] Cek callback
[ ] Cek navigation
[ ] Cek repository
[ ] Cek Firestore
[ ] Cek DataStore
[ ] Cek state UI
[ ] Pertahankan kode lama
[ ] Tentukan perubahan minimum
```

---

# 120. CHECKLIST SETELAH CODING

```text
[ ] Build Project
[ ] Tidak ada Kotlin error
[ ] Tidak ada unresolved reference
[ ] Tidak ada redeclaration
[ ] Navigation normal
[ ] Firebase normal
[ ] Firestore normal
[ ] Scanner normal
[ ] Notifikasi normal
[ ] Chat normal
[ ] UI tidak rusak
[ ] Status bar normal
[ ] Navigation bar normal
[ ] Test di Android
```

---

# 121. FINAL TESTING END-TO-END

Testing dilakukan dari awal hingga akhir:

```text
Buka Aplikasi
      ↓
Session Check
      ↓
Login
      ↓
Role Detection
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
Theme
      ↓
Logout
      ↓
Login kembali
```

---

# 122. TESTING ROLE

```text
LOGIN
  │
  ├── isAdmin = true
  │       ↓
  │    ADMIN
  │
  └── isAdmin = false
          ↓
        STAFF
```

Tidak boleh terjadi:

```text
Staff masuk Admin
Admin masuk Staff
```

---

# 123. TESTING QR KANTOR

```text
QR MALANG
   ↓
Jika aktif → VALID
Jika nonaktif → DITOLAK

QR BLITAR
   ↓
Jika aktif → VALID
Jika nonaktif → DITOLAK

QR KEDIRI
   ↓
Jika aktif → VALID
Jika nonaktif → DITOLAK

QR LAIN
   ↓
DITOLAK
```

---

# 124. TESTING DATA ABSENSI

Setiap absensi harus dapat ditelusuri berdasarkan:

```text
uid
tanggal
```

Data minimal:

```text
uid
nama
tanggal
jamMasuk
jamPulang
qrData
catatan
```

Tujuan:

```text
Tidak terjadi duplikasi absensi harian.
```

---

# 125. TESTING PERGANTIAN HARI

Contoh:

```text
11 September
   │
   ▼
Absensi 11 September
```

Kemudian:

```text
12 September
   │
   ▼
Cek attendance 12 September
```

Absensi tanggal 11 tetap tersimpan.

Status tanggal 12 tidak boleh mengambil status tanggal 11.

---

# 126. UI/UX FINAL

Tahap UI/UX dilakukan setelah fungsi stabil.

Prioritas:

```text
1. Tidak overlap
2. Responsive
3. Spacing konsisten
4. Typography konsisten
5. Button nyaman
6. Card rapi
7. Header aman
8. Bottom Navigation aman
9. Status Bar aman
10. Navigation Bar aman
```

---

# 127. SYSTEM BAR FINAL

Karena aplikasi menggunakan edge-to-edge:

```text
Status Bar Android
        ↓
Content App
        ↓
Navigation Bar Android
```

Tidak boleh:

```text
Header tertutup
Judul tertimpa status bar
Tombol bawah tertutup navigation bar
```

Jika ditemukan masalah:

```text
Cek screen
     ↓
Cek WindowInsets
     ↓
Cek MainActivity
     ↓
Cek enableEdgeToEdge
     ↓
Perbaiki sumber masalah
```

Tidak langsung menambal semua screen satu per satu tanpa analisis.

---

# 128. FINAL BUILD

```text
Clean Project
     ↓
Rebuild Project
     ↓
BUILD SUCCESSFUL
     ↓
Install APK
     ↓
Test Android
     ↓
Final QA
```

---

# 129. BACKUP FINAL

Setelah versi stabil:

```text
Project
   ↓
Git Commit
   ↓
Git Push
   ↓
GitHub
```

Backup harus dilakukan setelah perubahan besar yang sudah lolos testing.

---

# 130. VERSI APLIKASI

Saat ini:

```text
Version Name:
1.1
```

```text
Version Code:
1
```

```text
Tahun:
2026
```

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 131. ROADMAP PROJECT

## Tahap 1 — Core

```text
✅ Login
✅ Authentication
✅ Role
✅ Navigation
```

## Tahap 2 — Staff

```text
✅ Dashboard
✅ Scan QR
✅ Absen Masuk
✅ Absen Pulang
✅ Absen Luar Kantor
✅ Riwayat
```

## Tahap 3 — Pengajuan

```text
✅ Pengajuan
✅ Pengajuan Baru
✅ Detail
✅ Riwayat Pengajuan
```

## Tahap 4 — Admin

```text
✅ Approval
✅ Karyawan
✅ Rekap
✅ QR Settings
```

## Tahap 5 — Communication

```text
✅ Chat Staff ↔ Admin
✅ Notifikasi Staff
✅ Notifikasi Admin
```

## Tahap 6 — Settings

```text
✅ Profile
✅ Theme
✅ Bantuan
✅ Tentang
✅ Logout
```

## Tahap 7 — Finalization

```text
🔄 Testing menyeluruh
🔄 Bug Fix
🔄 UI/UX Final
🔄 System Bar Final
🔄 Final QA
🔄 Build APK
```

---

# 132. ALUR BESAR FINAL

```text
                         ┌──────────────┐
                         │    APLIKASI  │
                         └──────┬───────┘
                                │
                                ▼
                         ┌──────────────┐
                         │ Session Check│
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
       ┌─────────┼─────────┐          ┌────────┼────────┐
       │         │         │          │        │        │
       ▼         ▼         ▼          ▼        ▼        ▼
   Approval  Karyawan    Rekap      Scan   Pengajuan Riwayat
       │         │         │          │        │
       └─────────┴─────────┘          │        │
                 │                    │        │
                 ▼                    ▼        ▼
            QR Settings          Attendance  Pengajuan
                 │                    │        │
                 │                    └────┬───┘
                 │                         │
                 ▼                         ▼
            Chat / Notif              Notifikasi
                 │                         │
                 └──────────┬──────────────┘
                            ▼
                           CHAT
                            │
                            ▼
                         SETTINGS
                            │
                     ┌──────┼──────┐
                     │      │      │
                     ▼      ▼      ▼
                   Theme  Bantuan Tentang
                            │
                            ▼
                          Logout
                            │
                            ▼
                           LOGIN
```

---

# 133. KESIMPULAN

Aplikasi **Absensi Karyawan** memiliki dua role:

```text
ADMIN
STAFF
```

Backend:

```text
Firebase Authentication
Cloud Firestore
```

Scanner:

```text
CameraX
ML Kit Barcode Scanning
```

Local storage:

```text
DataStore Preferences
```

Navigation:

```text
Enum-based Navigation
```

Session:

```text
06:00 - 17:59
→ Session dipertahankan
```

```text
18:00 - 05:59
→ Login kembali diperlukan
```

QR kantor dikontrol melalui:

```text
qr_settings
```

QR aktif:

```text
Malang
Blitar
Kediri
```

Scanner menggunakan:

```text
QR Detection
Position Validation
Size Validation
Overlap Validation
3-Frame Validation
QR Lock
Firestore Whitelist
```

Sistem komunikasi:

```text
Staff ↔ Admin Chat
```

Sistem notifikasi:

```text
Staff Notifications
Admin Notifications
```

---

# 134. PATOKAN PENGEMBANGAN

Dokumen ini adalah **MASTER ROADMAP PROJECT**.

Setiap perubahan harus mengikuti:

```text
CEK KONDISI SEKARANG
        ↓
BACA KODE YANG SUDAH ADA
        ↓
PERTAHANKAN FITUR LAMA
        ↓
TENTUKAN BAGIAN YANG PERLU DIUBAH
        ↓
UBAH SESEDIKIT MUNGKIN
        ↓
BUILD
        ↓
TEST
        ↓
FIX JIKA ERROR
        ↓
BUILD ULANG
        ↓
TEST ULANG
        ↓
LANJUT FITUR BERIKUTNYA
```

Prinsip:

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
JANGAN RUSAK FITUR LAMA.
JANGAN RUSAK NAVIGASI.
JANGAN UBAH DESAIN TANPA PERMINTAAN.
TAMBAHKAN FITUR SECARA BERTAHAP.
BUILD → TEST → FIX → BUILD → TEST.
```

---

# 135. STATUS TERAKHIR PROJECT

Pada tahap dokumentasi ini:

```text
CORE SYSTEM              ✅
LOGIN                    ✅
ROLE                     ✅
NAVIGATION               ✅
ADMIN                    ✅
STAFF                    ✅
FIREBASE AUTH            ✅
FIRESTORE                ✅
DATASTORE                ✅
THEME                    ✅
SESSION MANAGER          ✅
QR SETTINGS              ✅
QR SCANNER               ✅
QR VALIDATION            ✅
QR LOCK                  ✅
ABSEN MASUK              ✅
ABSEN PULANG             ✅
ABSEN LUAR KANTOR        ✅
RIWAYAT                  ✅
PENGAJUAN                ✅
APPROVAL                 ✅
CHAT STAFF ↔ ADMIN       ✅
NOTIFIKASI STAFF         ✅
NOTIFIKASI ADMIN         ✅
SETTINGS                 ✅
```

Status build terakhir:

```text
BUILD SUCCESSFUL
```

---

# 136. FOKUS PENGEMBANGAN SAAT INI

Karena fitur inti sudah tersedia, fokus project selanjutnya adalah:

```text
TEST
  ↓
TEMUKAN BUG
  ↓
PERBAIKI BUG
  ↓
TEST ULANG
  ↓
UI/UX FINAL
  ↓
FINAL QA
  ↓
BUILD APK
```

Bukan lagi membuat ulang fitur dasar.

Prioritas utama:

```text
STABILITAS
DATA
VALIDASI
UI/UX
ANDROID COMPATIBILITY
FINAL TESTING
```

---

# 137. DOKUMENTASI PROJECT

Folder:

```text
DOKUMENTASI/
```

File utama:

```text
ALUR_APLIKASI.md
CHANGELOG.md
```

`ALUR_APLIKASI.md` berisi:

```text
Alur aplikasi
Struktur project
Navigation
Screen
Firebase
Firestore
Authentication
Session
Theme
QR Scanner
Absensi
Pengajuan
Approval
Chat
Notifikasi
Testing
Roadmap
```

`CHANGELOG.md` berisi:

```text
Riwayat perubahan project
Tanggal perubahan
Fitur yang diubah
Status build
```

---

# 138. FINAL PROJECT STRUCTURE

```text
ABSENSI KARYAWAN
│
├── AUTHENTICATION
│   ├── Login
│   ├── Forgot Password
│   └── Logout
│
├── ROLE
│   ├── Admin
│   └── Staff
│
├── ADMIN
│   ├── Dashboard
│   ├── Approval
│   ├── Karyawan
│   ├── Rekap
│   ├── QR Settings
│   ├── Chat
│   ├── Notifikasi
│   └── Settings
│
├── STAFF
│   ├── Dashboard
│   ├── Scan QR
│   ├── Absen Masuk
│   ├── Absen Pulang
│   ├── Absen Luar Kantor
│   ├── Pengajuan
│   ├── Riwayat
│   ├── Chat
│   ├── Notifikasi
│   └── Settings
│
├── FIRESTORE
│   ├── users
│   ├── attendance
│   ├── pengajuan
│   ├── qr_settings
│   ├── chatRooms
│   └── notifications
│
├── LOCAL STORAGE
│   ├── AbsensiDataStore
│   └── ThemeDataStore
│
└── FINALIZATION
    ├── Testing
    ├── Bug Fix
    ├── UI/UX
    ├── Final QA
    ├── Build APK
    └── GitHub Backup
```

---

# 139. FINAL

```text
PROJECT
AbsensiKaryawan

PACKAGE
com.example.absensikaryawan

VERSION
1.1

VERSION CODE
1

YEAR
2026

BUILD STATUS
BUILD SUCCESSFUL
```

Aplikasi saat ini sudah memiliki:

```text
ADMIN
STAFF
LOGIN
FIREBASE
FIRESTORE
SESSION
THEME
QR SETTINGS
QR SCANNER
ABSENSI
PENGAJUAN
APPROVAL
RIWAYAT
CHAT
NOTIFIKASI
SETTINGS
```

Dokumen ini menjadi **MASTER DOCUMENTATION / MASTER ROADMAP** untuk pengembangan project AbsensiKaryawan selanjutnya.

```text
KODE PROJECT
     +
FIREBASE
     +
DATABASE
     +
NAVIGATION
     +
DOKUMENTASI
     +
CHANGELOG
```

harus tetap sinkron.

**PRINSIP UTAMA PROJECT:**

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
JANGAN RUSAK FITUR LAMA.
JANGAN RUSAK NAVIGASI.
JANGAN UBAH DESAIN TANPA PERMINTAAN.
TAMBAHKAN FITUR SECARA BERTAHAP.
BUILD → TEST → FIX → BUILD → TEST.
```

**END OF MASTER DOCUMENTATION**
