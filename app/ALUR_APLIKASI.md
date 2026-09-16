# MASTER DOCUMENTATION

# ALUR APLIKASI ABSENSI KARYAWAN

Dokumentasi master mengenai alur aplikasi, struktur project, fitur, database, navigasi, sistem absensi, QR Scanner, QR Generator, pengajuan, approval, chat, notifikasi, session, theme, testing, dan roadmap pengembangan aplikasi **Absensi Karyawan** berbasis Android.

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

**QR Generator:**

```text
ZXing
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

**Version Name:**

```text
1.1
```

**Version Code:**

```text
2
```

**Tahun:**

```text
2026
```

**Gradle:**

```text
9.7.1
```

**Android Gradle Plugin:**

```text
9.4.0
```

**Kotlin:**

```text
2.2.10
```

**Compile SDK:**

```text
37
```

**Target SDK:**

```text
37
```

**Minimum SDK:**

```text
24
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

## ADMIN

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

## STAFF

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
                    │   APLIKASI   │
                    └──────┬───────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │  Cek Session     │
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

Session menggunakan waktu perangkat.

## SESSION PERSISTEN

```text
06:00 - 17:59
```

Pada rentang ini session Firebase dipertahankan.

## SESSION LOGIN ULANG

```text
18:00 - 05:59
```

Pada rentang ini aplikasi meminta login kembali ketika aplikasi masuk foreground.

---

# 8. SESSION PERSISTEN

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

Alur:

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
Pindah / tutup aplikasi
  │
  ▼
Buka kembali
  │
  ▼
Tetap Login
```

---

# 9. SESSION MALAM

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
SessionManager
      │
      ▼
Cek waktu
      │
      ▼
Perlu login kembali
      │
      ▼
FirebaseAuth.signOut()
      │
      ▼
Login Screen
```

Yang dihapus hanya session login.

```text
FirebaseAuth.signOut()
```

Akun Firebase tidak dihapus.

Data Firestore tidak dihapus.

Data absensi juga tidak dihapus.

---

# 10. LOGOUT MANUAL

Logout tersedia melalui Settings.

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

---

# 11. MAIN ACTIVITY

File:

```text
MainActivity.kt
```

Tanggung jawab:

```text
Menjalankan Theme
Menjalankan Session
Lifecycle aplikasi
Edge-to-edge
Menjalankan AppNavigation
```

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

Navigation menggunakan enum:

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
 │     ↓
 │ AdminNavigation
 │
 └── STAFF
       ↓
     StaffNavigation
```

---

# 13. ADMIN NAVIGATION

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

Menu:

```text
Beranda
Approval
Karyawan
Rekap
Setting
```

Fitur tambahan:

```text
QR Kantor
Chat Staff
Notifikasi
```

---

# 14. STAFF NAVIGATION

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

Menu:

```text
Beranda
Pengajuan
Scan
Riwayat
Setting
```

---

# 15. SCREEN ADMIN

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
AdminChatListScreen
AdminChatDetailScreen
AdminQrSettingScreen
```

Fitur:

```text
Dashboard
Approval
Karyawan
Rekap
QR Kantor
Chat
Notifikasi
Settings
```

---

# 16. SCREEN STAFF

Screen Staff:

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
Notifikasi
ChatAdmin
Tampilan
Bantuan
TentangAplikasi
Profile
```

---

# 17. ADMIN DASHBOARD

Screen:

```text
AdminDashboardScreen
```

Fungsi:

```text
Ringkasan aplikasi
Akses Approval
Akses Karyawan
Akses Rekap
Akses QR Kantor
Akses Chat
Akses Notifikasi
Akses Settings
```

---

# 18. ADMIN APPROVAL

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
menunggu
disetujui
ditolak
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

Data berada pada:

```text
users
```

---

# 20. DATA USERS

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

`isAdmin` menentukan role aplikasi.

---

# 21. ADMIN REKAP

Screen:

```text
AdminRekapScreen
```

Sumber:

```text
attendance
```

Data:

```text
Nama
Tanggal
Jam Masuk
Jam Pulang
QR
Kantor
Status
Catatan
```

---

# 22. ADMIN SETTINGS

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

# 23. ADMIN QR SETTINGS

Screen:

```text
AdminQrSettingScreen
```

Fungsi:

```text
Mengatur QR kantor
Generate QR Code
Mengaktifkan QR
Menonaktifkan QR
Menyimpan QR ke Firestore
```

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

---

# 24. GENERATE QR CODE

Generator menggunakan:

```text
ZXing
```

Alur:

```text
Admin
  │
  ▼
QR Kantor
  │
  ▼
Pilih Kantor
  │
  ▼
QR Data
  │
  ▼
Generate QR
  │
  ▼
Bitmap QR
  │
  ▼
Preview QR
```

Data QR yang dibuat bukan data random.

Data QR menggunakan URL kantor yang sudah ditentukan.

---

# 25. QR KANTOR RESMI

## MALANG

```text
https://q.me-qr.com/x5ie23mg
```

## BLITAR

```text
https://q.me-qr.com/hbywvgy7
```

## KEDIRI

```text
https://q.me-qr.com/14vy2ipr
```

QR tersebut disimpan pada:

```text
qr_settings
```

dengan:

```text
aktif = true
```

jika QR tersebut boleh digunakan.

---

# 26. DATA QR_SETTINGS

Field:

```text
officeName
qrData
aktif
```

Contoh:

```text
officeName : Malang
qrData     : https://q.me-qr.com/x5ie23mg
aktif      : true
```

---

# 27. ALUR GENERATE DAN SAVE QR

```text
ADMIN
   │
   ▼
QR Settings
   │
   ▼
Masukkan / cek QR Data
   │
   ▼
Generate QR
   │
   ▼
QR Preview
   │
   ▼
Save
   │
   ▼
Firestore
   │
   ▼
qr_settings
```

---

# 28. STAFF DASHBOARD

Screen:

```text
StaffDashboardScreen
```

Dashboard menampilkan:

```text
Nama Staff
Tanggal
Jam Real-Time
Status Absensi
Jam Masuk
Jam Pulang
Akses Scan
Akses Absen Luar Kantor
Notifikasi
Profile
```

---

# 29. JAM REAL-TIME

Format:

```text
HH:mm:ss
```

Contoh:

```text
07:35:21
```

Jam diperbarui secara berkala.

---

# 30. STATUS KEHADIRAN

Status:

```text
BELUM ABSEN
```

```text
SUDAH ABSEN
```

Identifikasi berdasarkan:

```text
uid + tanggal
```

Alur:

```text
Dashboard
    │
    ▼
Firestore attendance
    │
    ▼
Cek UID + tanggal
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

# 31. QR SCANNER

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

Scanner difokuskan pada:

```text
QR_CODE
```

Komponen scanner:

```text
Camera
Scanner Frame
QR Detection
Bounding Box
Corner Points
Scanner Line
Validation Progress
QR Lock
QR Reset
```

---

# 32. ALUR SCAN QR

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
  └── Berhasil
          │
          ▼
      Scanner Aktif
          │
          ▼
        Kamera
          │
          ▼
     ML Kit Detection
          │
          ▼
     Deteksi QR
          │
          ▼
     Validasi QR
```

---

# 33. QR SETTINGS SEBAGAI WHITELIST

Scanner mengambil daftar QR aktif dari:

```text
qr_settings
```

Scanner hanya menerima QR yang:

```text
qrData tidak kosong
aktif = true
```

Jika QR tidak terdaftar:

```text
QR Code tidak terdaftar sebagai QR kantor aktif.
```

QR tersebut tidak boleh digunakan untuk absensi.

---

# 34. LOAD QR SETTINGS

Alur:

```text
ScanAbsenScreen
       │
       ▼
Firestore
       │
       ▼
qr_settings
       │
       ▼
Ambil dokumen
       │
       ▼
Filter aktif = true
       │
       ▼
registeredQrCodes
       │
       ▼
Scanner Aktif
```

Jika tidak ada:

```text
Belum ada QR kantor aktif.
Hubungi Admin untuk mengatur QR kantor.
```

Jika gagal:

```text
Gagal mengambil pengaturan QR kantor.
```

---

# 35. NORMALISASI QR

Sebelum dibandingkan, QR dinormalisasi.

Proses:

```text
Trim
Remove trailing slash
Ignore case
```

Contoh:

```text
https://q.me-qr.com/x5ie23mg/
```

menjadi:

```text
https://q.me-qr.com/x5ie23mg
```

Tujuannya menghindari perbedaan format kecil menyebabkan QR valid ditolak.

---

# 36. VALIDASI UKURAN QR

Scanner menggunakan batas:

```text
QR_MIN_SIZE_DP = 45
```

Scanner juga membatasi ukuran maksimum QR terhadap frame.

Tujuan:

```text
QR cukup jelas
QR tidak terlalu jauh
QR tidak terlalu besar
```

---

# 37. VALIDASI POSISI QR

QR harus berada di area scanner.

Pemeriksaan:

```text
Bounding Box
Center QR
Scanner Frame
Overlap
```

Minimum overlap:

```text
80%
```

Jika QR terlalu keluar dari frame:

```text
QR belum valid
```

---

# 38. MULTI-FRAME VALIDATION

Scanner tidak langsung mengunci QR pada satu frame.

Jumlah frame:

```text
3 frame valid
```

Alur:

```text
Frame 1
   ↓
Frame 2
   ↓
Frame 3
   ↓
QR LOCK
```

Scanner juga memeriksa kestabilan posisi QR.

Tracking distance:

```text
40dp
```

Tujuan:

```text
Mengurangi false detection
Mengurangi scan sesaat
Membuat scanner lebih stabil
```

---

# 39. QR LOCK

Setelah QR valid:

```text
scanLock = true
```

Scanner menghentikan proses QR yang sama agar tidak terjadi double processing.

Alur:

```text
QR valid
   ↓
3 frame valid
   ↓
scanLock
   ↓
QR terkunci
   ↓
Konfirmasi
```

---

# 40. QR INVALID

Jika QR terbaca tetapi tidak terdaftar:

```text
QR Code tidak terdaftar sebagai QR kantor aktif.
```

QR invalid:

```text
Tidak disimpan
Tidak menghasilkan absensi
Tidak masuk attendance
```

Scanner dapat di-reset untuk mencoba kembali.

---

# 41. QR VALID

Jika cocok:

```text
QR VALID
```

Scanner mengambil:

```text
qrData
officeName
```

Kemudian kantor terdeteksi:

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

# 42. RESET SCANNER

Reset digunakan ketika:

```text
QR invalid
User ingin scan ulang
Proses scan selesai
```

State yang dikembalikan:

```text
validationProgress
qrBoundingBox
qrCornerPoints
scanLock
```

ke kondisi siap scan.

---

# 43. ALUR ABSEN MASUK

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
Deteksi Kantor
      │
      ▼
Konfirmasi
      │
      ▼
Cek Attendance Hari Ini
      │
      ▼
Belum Ada
      │
      ▼
Simpan Jam Masuk
      │
      ▼
Firestore
      │
      ▼
Dashboard Refresh
```

---

# 44. DATA ABSEN MASUK

Collection:

```text
attendance
```

Data:

```text
uid
nama
tanggal
jamMasuk
jamPulang
status
qrData
kantor
catatan
```

Contoh:

```text
uid       : UID USER
nama      : Nama Staff
tanggal   : 2026-09-15
jamMasuk  : 07:30:12
jamPulang : ""
status    : Hadir
qrData    : https://q.me-qr.com/x5ie23mg
kantor    : KANTOR MALANG
catatan   : ""
```

---

# 45. DATA QR PADA LOG ABSENSI

Setiap absensi QR menyimpan data QR yang digunakan.

Contoh:

```text
qrData:
https://q.me-qr.com/x5ie23mg
```

dan kantor:

```text
kantor:
KANTOR MALANG
```

Dengan demikian Admin dapat mengetahui:

```text
Staff siapa
Tanggal berapa
Jam berapa
Menggunakan QR apa
Dari kantor mana
```

---

# 46. CEK ABSENSI HARI INI

Query berdasarkan:

```text
uid
+
tanggal
```

Contoh:

```text
uid = ABC123
tanggal = 2026-09-15
```

Digunakan untuk:

```text
Dashboard
Absen Masuk
Absen Pulang
Riwayat
Rekap Admin
```

---

# 47. ABSEN PULANG

Jika attendance hari ini sudah memiliki:

```text
jamMasuk
```

tetapi:

```text
jamPulang = kosong
```

maka scan berikutnya dapat digunakan sebagai absensi pulang.

Alur:

```text
Scan QR
   │
   ▼
QR Valid
   │
   ▼
Attendance Hari Ini
   │
   ▼
Jam Masuk Ada
   │
   ▼
Jam Pulang Kosong
   │
   ▼
Update jamPulang
```

---

# 48. DATA ABSEN PULANG

Field tambahan:

```text
jamPulang
qrDataPulang
kantorPulang
catatanPulang
```

Contoh:

```text
jamMasuk       : 07:30:12
jamPulang      : 16:05:21
qrData         : https://q.me-qr.com/x5ie23mg
qrDataPulang   : https://q.me-qr.com/x5ie23mg
kantor         : KANTOR MALANG
kantorPulang   : KANTOR MALANG
```

Absensi pulang melakukan update terhadap attendance yang sudah ada.

Tidak boleh membuat attendance baru secara tidak sengaja.

---

# 49. ABSEN LUAR KANTOR

Screen:

```text
AbsenLuarKantorScreen
```

Digunakan ketika Staff melakukan absensi di luar kantor.

Alur:

```text
Staff
  │
  ▼
Absen Luar Kantor
  │
  ▼
Ambil User
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

# 50. DATA ABSEN LUAR KANTOR

Data:

```text
uid
nama
tanggal
jamMasuk
jamPulang
status
qrData
lokasi
alasan
```

Untuk luar kantor:

```text
qrData = LUAR_KANTOR
```

Catatan lokasi dan alasan disimpan sebagai data absensi luar kantor.

---

# 51. DATASTORE ABSENSI

File:

```text
AbsensiDataStore.kt
```

Key:

```text
sudah_absen
jam_absen
tanggal_absen
jam_pulang
qr_absen
catatan_absen
```

DataStore digunakan untuk state lokal.

Firestore tetap menjadi sumber utama data cloud.

---

# 52. DASHBOARD ABSENSI

Dashboard melakukan:

```text
uid
+
tanggal hari ini
```

Kemudian membaca:

```text
attendance
```

Jika ditemukan:

```text
SUDAH ABSEN
```

Jika:

```text
jamMasuk terisi
jamPulang kosong
```

maka:

```text
Sudah Absen Masuk
Belum Absen Pulang
```

Jika:

```text
jamMasuk terisi
jamPulang terisi
```

maka:

```text
Absensi Lengkap
```

---

# 53. RIWAYAT STAFF

Screen:

```text
RiwayatScreen
```

Tab:

```text
Absensi
Pengajuan
```

Riwayat absensi menampilkan:

```text
Tanggal
Jam Masuk
Jam Pulang
Kantor
Status
Catatan
```

---

# 54. PENGAJUAN STAFF

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

# 55. PENGAJUAN BARU

Screen:

```text
PengajuanBaruScreen
```

Jenis pengajuan dapat berupa:

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
Status = menunggu
```

---

# 56. DATA PENGAJUAN

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

# 57. DETAIL PENGAJUAN

Screen:

```text
DetailPengajuan
```

Menampilkan:

```text
Jenis
Tanggal
Waktu
Alasan
Status
```

---

# 58. RIWAYAT PENGAJUAN

Screen:

```text
RiwayatPengajuan
```

Digunakan untuk melihat seluruh pengajuan yang pernah dibuat Staff.

---

# 59. ALUR APPROVAL

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
```

---

# 60. NOTIFIKASI STAFF

Screen:

```text
Notifikasi
```

Event:

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
Firestore
 │
 ▼
Notifikasi Staff
```

---

# 61. NOTIFIKASI ADMIN

Event:

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
  ├── Pengajuan
  └── Absensi
       │
       ▼
NotificationRepository
       │
       ▼
Firestore
       │
       ▼
Notifikasi Admin
```

---

# 62. CHAT STAFF ↔ ADMIN

Sistem chat mendukung komunikasi dua arah.

Staff:

```text
ChatAdminScreen
```

Admin:

```text
AdminChatListScreen
AdminChatDetailScreen
```

---

# 63. MODEL CHAT

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

---

# 64. STRUKTUR CHAT

Struktur yang digunakan project:

```text
chatRooms
   │
   └── {staffUid}
        │
        └── messages
```

Pesan diurutkan berdasarkan:

```text
timestamp
```

---

# 65. ALUR CHAT STAFF

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

# 66. ALUR CHAT ADMIN

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
Chat Detail
  │
  ▼
Balas
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

# 67. WHATSAPP

Untuk kebutuhan komunikasi WhatsApp, project tidak perlu membuat sistem chat WhatsApp sendiri.

Jika nantinya diperlukan:

```text
api.whatsapp.com
```

dapat digunakan untuk membuka percakapan WhatsApp.

Konsep:

```text
Aplikasi
   │
   ▼
Tombol WhatsApp
   │
   ▼
api.whatsapp.com
   │
   ▼
WhatsApp
```

Fitur ini tidak menggantikan:

```text
Chat Staff ↔ Admin
```

Chat internal Firebase tetap menjadi sistem chat aplikasi.

---

# 68. THEME SYSTEM

Theme:

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
Preference
      │
      ▼
AbsensiKaryawanTheme
```

---

# 69. WARNA UTAMA

Warna yang digunakan project:

```text
Background
#F7F9FC
```

```text
Primary Green
#16A34A
```

```text
Soft Green
#E8F5E9
```

```text
Text Dark
#1F2937
```

```text
Text Gray
#6B7280
```

```text
Bottom Navigation Green
#2E7D32
```

---

# 70. FIREBASE

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

# 71. FIREBASE AUTHENTICATION

Digunakan untuk:

```text
Login
Logout
Forgot Password
Session
```

Fungsi:

```text
signInWithEmailAndPassword()
signOut()
sendPasswordResetEmail()
```

---

# 72. ATTENDANCE

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
status
qrData
kantor
catatan
```

Field tambahan untuk pulang:

```text
qrDataPulang
kantorPulang
catatanPulang
```

---

# 73. PENGAJUAN

Collection:

```text
pengajuan
```

Digunakan oleh:

```text
Staff Pengajuan
Admin Approval
Notifikasi
Riwayat
```

---

# 74. QR_SETTINGS

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
QR Generator
QR Scanner
Validasi QR
Deteksi Kantor
```

---

# 75. CHATROOMS

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

# 76. NOTIFICATIONS

Collection:

```text
notifications
```

Digunakan untuk:

```text
Notifikasi Staff
Notifikasi Admin
```

Event:

```text
Chat
Pengajuan
Absensi
```

---

# 77. USER REPOSITORY

File:

```text
UserRepository.kt
```

Fungsi:

```text
Mengambil data user
Mengambil nama user
Mengambil data profile
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
UserRepository
     │
     ▼
User Data
```

---

# 78. FIRESTORE REPOSITORY

File:

```text
FirestoreRepository.kt
```

Fungsi utama:

```text
simpanAbsenMasuk()
getAbsenHariIni()
simpanAbsenLuarKantor()
simpanAbsenPulang()
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

# 79. CHAT REPOSITORY

File:

```text
ChatRepository.kt
```

Digunakan untuk:

```text
Mengirim pesan
Membaca pesan
Listen pesan
Mengelola chat
```

---

# 80. NOTIFICATION REPOSITORY

File:

```text
NotificationRepository.kt
```

Digunakan untuk:

```text
Membuat notifikasi
Membaca notifikasi
Menandai notifikasi dibaca
Mendengarkan perubahan
```

---

# 81. STRUKTUR PROJECT

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
        │       │   ├── AdminQrSettingScreen.kt
        │       │   └── ...
        │       │
        │       ├── data/
        │       │   └── FirestoreRepository.kt
        │       │
        │       ├── repository/
        │       │   ├── UserRepository.kt
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

# 82. SYSTEM BAR DAN EDGE-TO-EDGE

Aplikasi menggunakan:

```text
enableEdgeToEdge()
```

Target:

```text
Header tidak tertutup Status Bar
Content tidak tertimpa area Android
Bottom Navigation tidak tertutup Navigation Bar
```

Screen harus memperhatikan:

```text
WindowInsets.statusBars
WindowInsets.navigationBars
```

Perbaikan dilakukan berdasarkan screen yang bermasalah agar screen yang sudah stabil tidak ikut rusak.

---

# 83. UI/UX

Target:

```text
Simple
Modern
Responsive
Konsisten
Mudah digunakan
```

Perhatian:

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

Desain yang sudah stabil tidak diubah tanpa kebutuhan.

---

# 84. FIRESTORE SECURITY RULES

Aturan dasar project harus memastikan user login sebelum mengakses data aplikasi.

Contoh rules yang digunakan sebagai dasar:

```firestore
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read, write: if request.auth != null;
    }

    match /attendance/{attendanceId} {
      allow read, write: if request.auth != null;
    }

    match /pengajuan/{pengajuanId} {
      allow read, write: if request.auth != null;
    }

    match /chatRooms/{staffUid} {
      allow read, write: if request.auth != null;

      match /messages/{messageId} {
        allow read, write: if request.auth != null;
      }
    }

    match /qr_settings/{qrId} {
      allow read, write: if request.auth != null;
    }

    match /notifications/{notificationId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Rules tersebut adalah dasar pengembangan dan masih dapat diperketat lagi sebelum production.

---

# 85. ALUR DATA QR SAMPAI LOG

Ini merupakan salah satu alur utama project:

```text
ADMIN
   │
   ▼
Generate QR
   │
   ▼
QR Data
   │
   ▼
qr_settings
   │
   ▼
STAFF
   │
   ▼
Scan QR
   │
   ▼
ML Kit
   │
   ▼
QR terbaca
   │
   ▼
Validasi Firestore
   │
   ├── INVALID
   │      ↓
   │     DITOLAK
   │
   └── VALID
          │
          ▼
     Deteksi Kantor
          │
          ▼
     Konfirmasi
          │
          ▼
     attendance
          │
          ├── qrData
          ├── kantor
          ├── tanggal
          ├── jamMasuk
          └── uid
```

---

# 86. HASIL GENERATE QR DAN SCAN QR

Status pekerjaan:

```text
GENERATE QR CODE
        ↓
       ✅
        ↓
SIMPAN QR SETTINGS
        ↓
       ✅
        ↓
SCAN QR CODE
        ↓
       ✅
        ↓
VALIDASI QR
        ↓
       ✅
        ↓
DETEKSI KANTOR
        ↓
       ✅
        ↓
SIMPAN QR DATA KE ABSENSI
        ↓
       ✅
```

Dengan demikian:

```text
Generate QR Code
+
Scan QR Code
+
Data tersimpan sesuai QR
```

sudah menjadi bagian dari alur aplikasi.

---

# 87. ALUR ABSENSI LENGKAP

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
VALIDASI FIRESTORE
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
 CEK ATTENDANCE HARI INI
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

# 88. ALUR PENGAJUAN LENGKAP

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
Disetujui       Ditolak
 │               │
 └──────┬────────┘
        ▼
Notification Staff
        │
        ▼
Staff melihat status
```

---

# 89. ALUR CHAT LENGKAP

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
             Pesan Tersinkron
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
        STAFF                  ADMIN
```

---

# 90. ALUR NOTIFIKASI

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
              Read/Unread        Read/Unread
```

---

# 91. ALUR DATA APLIKASI

```text
                     ANDROID
                        │
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
    Firebase        Firestore        DataStore
      Auth              │                │
        │          ┌────┼────┐           │
        │          │    │    │           │
        │          ▼    ▼    ▼           │
        │        users attendance        │
        │               │                │
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

# 92. TESTING LOGIN

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

# 93. TESTING SESSION

```text
[ ] 06:00 session persisten
[ ] 17:59 session persisten
[ ] 18:00 login kembali diperlukan
[ ] 05:59 login kembali diperlukan
[ ] Firebase account tidak dihapus
[ ] Data Firestore tidak dihapus
[ ] Login kembali berhasil
```

---

# 94. TESTING GENERATE QR

```text
[ ] QR Malang dapat dibuat
[ ] QR Blitar dapat dibuat
[ ] QR Kediri dapat dibuat
[ ] Preview QR tampil
[ ] QR Data benar
[ ] QR dapat disimpan
[ ] Firestore qr_settings terisi
[ ] Status aktif tersimpan
```

---

# 95. TESTING QR SCANNER

```text
[ ] Permission kamera muncul
[ ] Kamera aktif
[ ] QR Malang valid
[ ] QR Blitar valid
[ ] QR Kediri valid
[ ] QR tidak terdaftar ditolak
[ ] QR nonaktif ditolak
[ ] QR terlalu kecil ditolak
[ ] QR terlalu keluar frame ditolak
[ ] QR tidak stabil tidak langsung lock
[ ] 3 frame valid menghasilkan lock
[ ] Scanner tidak double process
[ ] Reset scanner berjalan
[ ] Kantor terdeteksi
```

---

# 96. TESTING ABSEN MASUK

```text
[ ] Staff belum absen
[ ] Scan QR berhasil
[ ] QR valid
[ ] Kantor terdeteksi
[ ] Jam masuk tersimpan
[ ] Tanggal tersimpan
[ ] UID tersimpan
[ ] Nama tersimpan
[ ] QR Data tersimpan
[ ] Kantor tersimpan
[ ] Data masuk Firestore
[ ] Dashboard berubah menjadi SUDAH ABSEN
```

---

# 97. TESTING ABSEN PULANG

```text
[ ] Staff sudah absen masuk
[ ] Scanner dapat digunakan kembali
[ ] QR valid
[ ] Jam pulang tersimpan
[ ] QR pulang tersimpan
[ ] Kantor pulang tersimpan
[ ] Attendance hari ini diperbarui
[ ] Tidak membuat attendance baru
[ ] Dashboard menampilkan jam pulang
```

---

# 98. TESTING ABSEN LUAR KANTOR

```text
[ ] User terdeteksi
[ ] Nama tersedia
[ ] Tanggal benar
[ ] Waktu benar
[ ] Attendance hari ini dicek
[ ] Jika sudah absen → ditolak
[ ] Lokasi dapat diisi
[ ] Alasan dapat diisi
[ ] Data Firestore tersimpan
[ ] DataStore tersimpan
```

---

# 99. TESTING PENGAJUAN

```text
[ ] Pengajuan dapat dibuka
[ ] Pengajuan Baru dapat dibuka
[ ] Form dapat diisi
[ ] Submit berhasil
[ ] Data masuk Firestore
[ ] Status Menunggu
[ ] Admin menerima pengajuan
[ ] Admin dapat approve
[ ] Admin dapat reject
[ ] Staff melihat status
```

---

# 100. TESTING CHAT

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

# 101. TESTING NOTIFIKASI

```text
[ ] Notifikasi Staff tampil
[ ] Notifikasi Admin tampil
[ ] Chat menghasilkan notifikasi
[ ] Pengajuan menghasilkan notifikasi
[ ] Absensi menghasilkan notifikasi Admin
[ ] Badge unread tampil
[ ] Notifikasi dapat dibuka
[ ] Status read berubah
```

---

# 102. TESTING NAVIGATION STAFF

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

# 103. TESTING NAVIGATION ADMIN

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

# 104. TESTING FIRESTORE

```text
[ ] users dapat dibaca
[ ] attendance dapat disimpan
[ ] attendance dapat diperbarui
[ ] pengajuan dapat disimpan
[ ] approval dapat diperbarui
[ ] qr_settings dapat dibaca
[ ] qr_settings dapat disimpan Admin
[ ] chat dapat dikirim
[ ] chat dapat dibaca
[ ] notification dapat dibuat
[ ] notification dapat dibaca
```

---

# 105. FINAL BUILD

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

# 106. BACKUP PROJECT

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

Repository:

```text
AbsensiKaryawan
```

Tujuan:

```text
Backup
Version Control
Riwayat Perubahan
Recovery
```

---

# 107. CHANGELOG

Setiap perubahan besar dicatat:

```text
Tanggal:
Jam:
Fitur:
Perubahan:
Status:
```

Contoh:

```text
15-09-2026
17:00
QR Scanner
Validasi QR kantor dan penyimpanan qrData + kantor
Status: Testing
```

---

# 108. STATUS FITUR TERKINI

## CORE

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

## ADMIN

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
✅ Generate QR Code
✅ Chat Staff
✅ Notifikasi
```

## STAFF

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

## BACKEND

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

## LOCAL STORAGE

```text
✅ AbsensiDataStore
✅ ThemeDataStore
```

## QR SYSTEM

```text
✅ Generate QR
✅ QR Settings
✅ QR Firestore
✅ CameraX
✅ ML Kit
✅ QR-only scanning
✅ QR whitelist
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
✅ qrData logging
✅ Office logging
```

---

# 109. POSISI PROJECT SAAT INI

```text
                 ABSENSI KARYAWAN
                         │
             ┌───────────┴───────────┐
             │                       │
           ADMIN                   STAFF
             │                       │
       ┌─────┼─────┐         ┌───────┼────────┐
       │     │     │         │       │        │
   Approval QR    Rekap     Scan  Pengajuan Riwayat
       │    │      │         │       │
       │    │      │         │       │
       └────┴──────┘         │       │
             │               │       │
         Chat/Notif           │       │
             │               │       │
          Settings        Attendance
                             │
                             ▼
                       Firestore
```

---

# 110. FITUR YANG TIDAK BOLEH RUSAK

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
QR Generator
QR Scanner
QR Validation
Office Detection
Absensi
Pengajuan
Approval
Chat
Notifikasi
Settings
```

---

# 111. PRIORITAS PENGEMBANGAN

Karena core system sudah tersedia:

```text
1. Testing
        ↓
2. Temukan Bug
        ↓
3. Fix Bug
        ↓
4. Testing Firestore
        ↓
5. Testing QR
        ↓
6. Testing Absensi
        ↓
7. Testing Pengajuan
        ↓
8. Testing Chat
        ↓
9. Testing Notifikasi
        ↓
10. UI/UX Final
        ↓
11. Android Compatibility
        ↓
12. Final QA
        ↓
13. Build APK
        ↓
14. Backup GitHub
```

---

# 112. POLA PENGEMBANGAN

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

# 113. PRINSIP PENGEMBANGAN

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
```

```text
JANGAN RUSAK FITUR LAMA.
```

```text
JANGAN RUSAK NAVIGASI.
```

```text
JANGAN UBAH DESAIN TANPA PERMINTAAN.
```

```text
JANGAN MENGGANTI STRUKTUR STABIL TANPA ALASAN.
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

# 114. CHECKLIST SEBELUM CODING

```text
[ ] Tentukan fitur
[ ] Tentukan screen
[ ] Tentukan file
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

# 115. CHECKLIST SETELAH CODING

```text
[ ] Build Project
[ ] Tidak ada Kotlin error
[ ] Tidak ada unresolved reference
[ ] Tidak ada redeclaration
[ ] Navigation normal
[ ] Firebase normal
[ ] Firestore normal
[ ] Scanner normal
[ ] QR validation normal
[ ] Notifikasi normal
[ ] Chat normal
[ ] UI tidak rusak
[ ] Status bar normal
[ ] Navigation bar normal
[ ] Test di Android
```

---

# 116. FINAL TESTING END-TO-END

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
Generate / Settings QR
      ↓
Scan QR
      ↓
Validasi QR
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

# 117. TESTING ROLE

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

Tidak boleh:

```text
Staff masuk Admin
Admin masuk Staff
```

---

# 118. TESTING QR KANTOR

```text
QR MALANG
   ↓
aktif = true → VALID
aktif = false → DITOLAK

QR BLITAR
   ↓
aktif = true → VALID
aktif = false → DITOLAK

QR KEDIRI
   ↓
aktif = true → VALID
aktif = false → DITOLAK

QR LAIN
   ↓
DITOLAK
```

---

# 119. TESTING DATA ABSENSI

Setiap absensi dapat ditelusuri berdasarkan:

```text
uid
tanggal
```

Data utama:

```text
uid
nama
tanggal
jamMasuk
jamPulang
status
qrData
kantor
catatan
```

Tujuan:

```text
Tidak terjadi duplikasi absensi harian.
```

---

# 120. TESTING PERGANTIAN HARI

Contoh:

```text
15 September
   │
   ▼
Absensi 15 September
```

Kemudian:

```text
16 September
   │
   ▼
Cek attendance 16 September
```

Absensi tanggal 15 tetap tersimpan.

Status tanggal 16 tidak boleh mengambil status tanggal 15.

---

# 121. UI/UX FINAL

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

# 122. SYSTEM BAR FINAL

Karena aplikasi menggunakan:

```text
enableEdgeToEdge()
```

maka:

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

Jika terjadi:

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

---

# 123. FINAL BUILD

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

# 124. BACKUP FINAL

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

Backup dilakukan setelah perubahan besar yang sudah lolos testing.

---

# 125. VERSI APLIKASI

```text
Version Name:
1.1
```

```text
Version Code:
2
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

# 126. ROADMAP PROJECT

## TAHAP 1 — CORE

```text
✅ Login
✅ Authentication
✅ Role
✅ Navigation
```

## TAHAP 2 — STAFF

```text
✅ Dashboard
✅ Scan QR
✅ Absen Masuk
✅ Absen Pulang
✅ Absen Luar Kantor
✅ Riwayat
```

## TAHAP 3 — PENGAJUAN

```text
✅ Pengajuan
✅ Pengajuan Baru
✅ Detail
✅ Riwayat Pengajuan
```

## TAHAP 4 — ADMIN

```text
✅ Approval
✅ Karyawan
✅ Rekap
✅ QR Settings
✅ Generate QR
```

## TAHAP 5 — COMMUNICATION

```text
✅ Chat Staff ↔ Admin
✅ Notifikasi Staff
✅ Notifikasi Admin
```

## TAHAP 6 — SETTINGS

```text
✅ Profile
✅ Theme
✅ Bantuan
✅ Tentang
✅ Logout
```

## TAHAP 7 — FINALIZATION

```text
🔄 Testing menyeluruh
🔄 Bug Fix
🔄 Sinkronisasi Firestore
🔄 UI/UX Final
🔄 System Bar Final
🔄 Final QA
🔄 Build APK
🔄 GitHub Backup
```

---

# 127. ALUR BESAR FINAL

```text
                         ┌──────────────┐
                         │   APLIKASI   │
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
            Generate QR              Notifikasi
                 │                         │
                 ▼                         │
            QR Firestore                    │
                 │                         │
                 ▼                         │
            QR Scanner                     │
                 │                         │
                 ▼                         │
             Absensi                       │
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

# 128. KESIMPULAN

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

QR Generator:

```text
ZXing
```

Local Storage:

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

QR resmi:

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
Office Detection
```

Sistem absensi:

```text
Absen Masuk
Absen Pulang
Absen Luar Kantor
```

Sistem pengajuan:

```text
Pengajuan Staff
Approval Admin
Notifikasi
```

Sistem komunikasi:

```text
Staff ↔ Admin Chat
```

WhatsApp dapat menggunakan:

```text
api.whatsapp.com
```

---

# 129. FITUR UTAMA YANG SUDAH TERBANGUN

```text
ADMIN
│
├── Dashboard
├── Approval
├── Karyawan
├── Rekap
├── QR Settings
├── Generate QR
├── Chat Staff
├── Notifikasi
└── Settings

STAFF
│
├── Dashboard
├── Scan QR
├── Absen Masuk
├── Absen Pulang
├── Absen Luar Kantor
├── Pengajuan
├── Riwayat
├── Chat Admin
├── Notifikasi
└── Settings
```

---

# 130. STATUS PROJECT TERAKHIR

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
GENERATE QR              ✅
QR SCANNER               ✅
QR VALIDATION            ✅
QR LOCK                  ✅
OFFICE DETECTION         ✅
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

---

# 131. POSISI PENGEMBANGAN SEKARANG

Project sudah melewati tahap pembuatan fitur dasar.

Fokus saat ini:

```text
TESTING
   ↓
BUG FIX
   ↓
DATA VALIDATION
   ↓
QR VALIDATION
   ↓
ABSENSI VALIDATION
   ↓
UI/UX FINAL
   ↓
ANDROID COMPATIBILITY
   ↓
FINAL QA
   ↓
BUILD APK
   ↓
BACKUP GITHUB
```

---

# 132. DOKUMENTASI PROJECT

Folder dokumentasi:

```text
DOKUMENTASI/
```

File:

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
QR Generator
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
Tanggal perubahan
Fitur yang diubah
Perubahan kode
Bug fix
Status build
```

---

# 133. FINAL PROJECT STRUCTURE

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
│   ├── Generate QR
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
    ├── Android Compatibility
    ├── Final QA
    ├── Build APK
    └── GitHub Backup
```

---

# 134. PATOKAN PENGEMBANGAN

Dokumen ini merupakan:

```text
MASTER DOCUMENTATION
MASTER ROADMAP
MASTER ALUR PROJECT
```

Setiap perubahan berikutnya mengikuti:

```text
CEK KONDISI SEKARANG
        ↓
BACA KODE YANG SUDAH ADA
        ↓
PERTAHANKAN FITUR LAMA
        ↓
TENTUKAN FILE TERKAIT
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

---

# 135. PRINSIP UTAMA PROJECT

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
```

```text
JANGAN RUSAK FITUR LAMA.
```

```text
JANGAN RUSAK NAVIGASI.
```

```text
JANGAN UBAH DESAIN TANPA PERMINTAAN.
```

```text
JANGAN MENGGANTI STRUKTUR STABIL TANPA ALASAN.
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

# 136. FINAL

```text
PROJECT
AbsensiKaryawan

PACKAGE
com.example.absensikaryawan

VERSION
1.1

VERSION CODE
2

YEAR
2026

GRADLE
9.7.1

AGP
9.4.0

KOTLIN
2.2.10

COMPILE SDK
37

TARGET SDK
37

MIN SDK
24
```

Aplikasi sudah memiliki:

```text
ADMIN
STAFF
LOGIN
FIREBASE
FIRESTORE
SESSION
THEME
QR SETTINGS
GENERATE QR
QR SCANNER
QR VALIDATION
OFFICE DETECTION
ABSEN MASUK
ABSEN PULANG
ABSEN LUAR KANTOR
PENGAJUAN
APPROVAL
RIWAYAT
CHAT
NOTIFIKASI
SETTINGS
```

Alur QR utama:

```text
GENERATE QR
      ↓
QR SETTINGS FIRESTORE
      ↓
STAFF SCAN QR
      ↓
VALIDASI QR
      ↓
DETEKSI KANTOR
      ↓
ABSENSI
      ↓
qrData + kantor
      ↓
ATTENDANCE FIRESTORE
```

Dokumen ini menjadi **MASTER DOCUMENTATION PROJECT ABSENSI KARYAWAN** untuk pengembangan berikutnya.

```text
KODE PROJECT
     +
FIREBASE
     +
DATABASE
     +
NAVIGATION
     +
QR SYSTEM
     +
ABSENSI
     +
PENGAJUAN
     +
CHAT
     +
NOTIFIKASI
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

# END OF MASTER DOCUMENTATION
