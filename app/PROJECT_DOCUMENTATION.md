# ALUR APLIKASI ABSENSI KARYAWAN

Dokumentasi alur dan struktur aplikasi **Absensi Karyawan** berbasis Android.

---

# 1. INFORMASI PROJECT

**Nama Project:** AbsensiKaryawan

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

**Version Aplikasi:**

```text
1.1
```

**Version Code:**

```text
1
```

---

# 2. TUJUAN APLIKASI

Aplikasi digunakan untuk mengelola proses absensi karyawan secara digital.

Aplikasi memiliki dua area utama:

```text
LOGIN
  │
  ├── ADMIN
  │
  └── STAFF
```

Admin digunakan untuk mengelola dan memantau data karyawan, pengajuan, serta rekap absensi.

Staff digunakan untuk melakukan absensi, melihat riwayat, membuat pengajuan, serta mengatur akun/aplikasi.

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
                    Cek isAdmin
                     dari Firestore
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

Login menggunakan:

```text
FirebaseAuth.signInWithEmailAndPassword()
```

User memasukkan:

```text
Email
Password
```

Setelah login berhasil, aplikasi mengambil data user dari Firestore.

Collection:

```text
users
```

Salah satu informasi penting:

```text
isAdmin
```

Logika:

```text
Login berhasil
      │
      ▼
Ambil data user
      │
      ▼
Cek isAdmin
      │
      ├── true  → Admin Dashboard
      │
      └── false → Staff Dashboard
```

---

# 5. LUPA PASSWORD

Screen:

```text
ForgotPassword
```

Fungsinya untuk membantu user melakukan reset password melalui mekanisme Firebase Authentication.

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
```

---

# 6. SESSION LOGIN BERDASARKAN WAKTU

Aplikasi memiliki aturan session berdasarkan jam.

File:

```text
SessionManager.kt
```

Package:

```text
com.example.absensikaryawan
```

Jam session aktif:

```text
05:00 - 20:59
```

Jam session tidak persisten:

```text
21:00 - 04:59
```

---

## 6.1 JAM AKTIF

Pada jam:

```text
05:00 - 20:59
```

Session login dipertahankan.

Contoh:

```text
Login
  │
  ▼
Masuk Dashboard
  │
  ├── Tutup aplikasi
  │
  ├── Buka aplikasi kembali
  │
  └── Pindah aplikasi → kembali
          │
          ▼
       Tetap Login
```

Firebase Authentication tetap menyimpan akun yang sedang login.

---

## 6.2 JAM NON-PERSISTENT

Pada jam:

```text
21:00 - 04:59
```

Aplikasi meminta login kembali ketika aplikasi masuk ke foreground.

Akun Firebase **tidak dihapus**.

Yang dilakukan hanya:

```text
FirebaseAuth.signOut()
```

Alur:

```text
Aplikasi dibuka / kembali ke foreground
             │
             ▼
      Cek waktu sekarang
             │
             ▼
       21:00 - 04:59
             │
             ▼
       Sign Out Session
             │
             ▼
       Login Screen
```

---

# 7. LOGOUT MANUAL

Logout tersedia melalui:

```text
Settings
   │
   ▼
Keluar
```

Logout manual menggunakan:

```text
FirebaseAuth.signOut()
```

Setelah logout:

```text
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

# 8. MAIN ACTIVITY

File:

```text
MainActivity.kt
```

MainActivity bertanggung jawab terhadap:

* Theme aplikasi
* Lifecycle aplikasi
* Session berdasarkan waktu
* Menjalankan `AppNavigation()`

Lifecycle digunakan untuk mendeteksi:

```text
ON_START
```

Ketika aplikasi masuk ke foreground, session time diperiksa.

---

# 9. NAVIGASI APLIKASI

File:

```text
AppNavigation.kt
```

Navigation menggunakan enum:

```text
AppScreen
```

Bukan menggunakan:

```text
NavHost
```

---

# 10. DAFTAR SCREEN

Screen Admin:

```text
Login
Admin
Approval
Karyawan
AdminRekap
AdminSettings
AdminProfile
AdminTampilan
AdminBantuan
AdminTentangAplikasi
```

Screen Staff:

```text
Staff
Profile
Pengajuan
PengajuanBaru
RiwayatPengajuan
DetailPengajuan
Scan
Riwayat
Settings
Tampilan
Notifikasi
Bantuan
TentangAplikasi
```

---

# 11. AREA ADMIN

Setelah login sebagai admin:

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

# 12. ADMIN DASHBOARD

Screen:

```text
AdminDashboardScreen
```

Dashboard menjadi halaman utama Admin.

Fungsi utama:

* Ringkasan informasi
* Navigasi Approval
* Navigasi Karyawan
* Navigasi Rekap
* Navigasi Setting

Dashboard tidak menampilkan:

```text
Version 1.1
```

Informasi versi/copyright diletakkan di Login dan Settings.

---

# 13. ADMIN APPROVAL

Screen:

```text
ApprovalScreen
```

Admin dapat melihat pengajuan staff.

Alur:

```text
Staff membuat Pengajuan
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

Approval digunakan untuk mengelola status pengajuan karyawan.

---

# 14. ADMIN KARYAWAN

Screen:

```text
KaryawanScreen
```

Fungsi utama:

```text
Melihat data karyawan
Menambah karyawan
Mengelola data karyawan
```

Tambah karyawan menggunakan dialog:

```text
TambahKaryawanDialog
```

Form dibuat menggunakan layout scroll agar input tetap dapat diakses pada layar kecil.

---

# 15. DATA KARYAWAN

Collection:

```text
users
```

Data user tersimpan di Firestore.

Data yang berhubungan dengan akun digunakan untuk menentukan area aplikasi:

```text
isAdmin
```

Jika:

```text
isAdmin = true
```

maka user masuk Admin.

Jika:

```text
isAdmin = false
```

maka user masuk Staff.

---

# 16. PASSWORD KARYAWAN

Password Firebase Authentication **tidak dapat dibaca atau ditampilkan kembali oleh Admin**.

Password dikelola oleh:

```text
Firebase Authentication
```

Jika user lupa password, gunakan mekanisme:

```text
Forgot Password
```

Tidak ada fitur untuk menampilkan password asli user.

---

# 17. ADMIN REKAP

Screen:

```text
AdminRekapScreen
```

Digunakan untuk melihat data rekap absensi.

Sumber data utama:

```text
attendance
```

Data absensi digunakan sebagai dasar informasi kehadiran karyawan.

---

# 18. ADMIN SETTINGS

Screen:

```text
AdminSettingsScreen
```

Admin Settings dibuat sebagai satu level dengan bottom navigation Admin.

Tidak menggunakan tombol Back sebagai navigasi utama.

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

# 19. ADMIN PROFILE

Screen:

```text
AdminProfile
```

Digunakan untuk halaman profil Admin.

---

# 20. ADMIN TAMPILAN

Screen:

```text
AdminTampilan
```

Pengaturan mode aplikasi menggunakan:

```text
ThemeMode
```

Pilihan:

```text
TERANG
GELAP
SISTEM
```

Pengaturan disimpan menggunakan:

```text
ThemeDataStore
```

---

# 21. ADMIN BANTUAN

Screen:

```text
AdminBantuan
```

Berisi informasi bantuan penggunaan aplikasi untuk Admin.

---

# 22. ADMIN TENTANG APLIKASI

Screen:

```text
AdminTentangAplikasi
```

Berisi informasi mengenai aplikasi.

Footer:

```text
© 2026 Absensi Karyawan • Versi 1.1
```

---

# 23. ADMIN LOGOUT

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

# 24. AREA STAFF

Setelah login sebagai staff:

```text
Login
  │
  ▼
isAdmin = false
  │
  ▼
Staff Dashboard
```

Bottom navigation Staff:

```text
┌──────────┬────────────┬────────┬──────────┬──────────┐
│ Beranda  │ Pengajuan  │  Scan  │ Riwayat  │ Setting  │
└──────────┴────────────┴────────┴──────────┴──────────┘
```

---

# 25. STAFF DASHBOARD

Screen:

```text
StaffDashboardScreen
```

Dashboard Staff menampilkan:

* Nama staff
* Tanggal
* Jam real-time
* Status absensi
* Tombol Scan QR
* Kehadiran Hari Ini

Tidak menggunakan greeting:

```text
Halo, Muhammad Qomarudin 👋
```

---

# 26. JAM REAL-TIME STAFF

Jam ditampilkan secara real-time.

Format:

```text
HH:mm:ss
```

Contoh:

```text
07:35:21
```

Jam diperbarui setiap detik.

Tanggal menggunakan locale:

```text
id-ID
```

---

# 27. STATUS KEHADIRAN

Status absensi Staff:

```text
SUDAH ABSEN
```

atau:

```text
BELUM ABSEN
```

Status berdasarkan data absensi hari ini.

---

# 28. NOTIFIKASI STAFF

Icon notifikasi berada di area atas Dashboard.

Posisinya:

```text
Notification → Profile
```

Notifikasi tidak menjadi menu utama di bottom navigation.

---

# 29. PROFILE STAFF

Screen:

```text
Profile
```

Profil dapat diakses dari area Dashboard melalui icon/profile.

Profile tidak ditampilkan sebagai menu utama di Staff Settings.

---

# 30. ABSEN MASUK

Screen:

```text
ScanAbsenScreen
```

Staff melakukan scan QR menggunakan kamera.

Teknologi:

```text
CameraX
+
ML Kit Barcode Scanning
```

Alur:

```text
Staff Dashboard
      │
      ▼
     Scan
      │
      ▼
 Kamera aktif
      │
      ▼
 Scan QR
      │
      ▼
Validasi QR
      │
      ▼
Cek absensi hari ini
      │
      ▼
Simpan ke Firestore
      │
      ▼
ABSEN BERHASIL DISIMPAN
```

Tidak menggunakan tombol:

```text
Simpan Absen
```

Scan QR langsung diproses.

---

# 31. DATA ABSEN MASUK

Collection:

```text
attendance
```

Data yang digunakan antara lain:

```text
uid
nama
tanggal
jamMasuk
jamPulang
qrData
catatan
```

Contoh struktur:

```text
attendance
 └── document
      ├── uid
      ├── nama
      ├── tanggal
      ├── jamMasuk
      ├── jamPulang
      ├── qrData
      └── catatan
```

---

# 32. DATASTORE ABSENSI

Aplikasi menggunakan:

```text
AbsensiDataStore
```

Key yang digunakan:

```text
SUDAH_ABSEN
JAM_ABSEN
TANGGAL_ABSEN
JAM_PULANG
QR_ABSEN
CATATAN_ABSEN
QR_DATA
```

DataStore digunakan untuk menyimpan informasi lokal yang diperlukan aplikasi.

---

# 33. ABSEN PULANG

Data jam pulang menggunakan field:

```text
jamPulang
```

Jam pulang ditampilkan berdasarkan data absensi yang tersimpan.

Jam juga dapat digunakan untuk menentukan informasi kehadiran hari tersebut.

---

# 34. RIWAYAT STAFF

Screen:

```text
RiwayatScreen
```

Digunakan untuk melihat data aktivitas/riwayat absensi Staff yang tersedia pada aplikasi.

Data utama berkaitan dengan:

```text
attendance
```

Riwayat menjadi bagian dari bottom navigation Staff.

---

# 35. PENGAJUAN STAFF

Screen:

```text
PengajuanScreen
```

Staff dapat melihat pengajuan yang sudah dibuat.

Bottom navigation:

```text
Pengajuan
```

---

# 36. PENGAJUAN BARU

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

---

# 37. DATA PENGAJUAN

Collection:

```text
pengajuan
```

Data yang digunakan:

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

Alur:

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
```

---

# 38. DETAIL PENGAJUAN

Screen:

```text
DetailPengajuan
```

Digunakan untuk melihat detail pengajuan yang dipilih.

---

# 39. RIWAYAT PENGAJUAN

Screen:

```text
RiwayatPengajuan
```

Digunakan untuk melihat pengajuan yang pernah dibuat Staff.

---

# 40. STAFF SETTINGS

Screen:

```text
Settings
```

Menu utama:

```text
Notifikasi
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

Namun:

```text
Profile
```

tidak ditampilkan di Settings karena Profile tetap dapat diakses dari Dashboard.

---

# 41. NOTIFIKASI

Notifikasi telah dipindahkan ke area Dashboard.

Settings tidak perlu menjadi tempat utama untuk mengakses notifikasi.

---

# 42. STAFF TAMPILAN

Screen:

```text
Tampilan
```

Digunakan untuk mengatur mode aplikasi.

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

# 43. STAFF BANTUAN

Screen:

```text
Bantuan
```

Berisi informasi bantuan penggunaan aplikasi untuk Staff.

---

# 44. STAFF TENTANG APLIKASI

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

# 45. STAFF LOGOUT

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

# 46. STRUKTUR FIREBASE

Firebase Authentication:

```text
Firebase Authentication
```

Firestore:

```text
users
attendance
pengajuan
```

Struktur umum:

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

# 47. USERS

Collection:

```text
users
```

Digunakan untuk menyimpan data akun/user.

Field penting:

```text
uid
nama
email
isAdmin
```

Field dapat berkembang sesuai kebutuhan aplikasi.

---

# 48. ATTENDANCE

Collection:

```text
attendance
```

Digunakan untuk data absensi.

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

---

# 49. PENGAJUAN

Collection:

```text
pengajuan
```

Digunakan untuk data pengajuan Staff.

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
```

---

# 50. REPOSITORY

Aplikasi menggunakan repository untuk memisahkan proses data dari UI.

Salah satu repository utama:

```text
FirestoreRepository
```

Contoh fungsi yang digunakan:

```text
getAbsenHariIni()
simpanAbsenMasuk()
simpanPengajuan()
```

Repository menjadi perantara antara:

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

# 51. USER REPOSITORY

UserRepository digunakan untuk mengambil informasi user yang sedang login.

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

Informasi `isAdmin` digunakan untuk menentukan dashboard.

---

# 52. THEME

Theme aplikasi menggunakan:

```text
AbsensiKaryawanTheme
```

Mode:

```text
TERANG
GELAP
SISTEM
```

Theme preference disimpan menggunakan:

```text
ThemeDataStore
```

---

# 53. STRUKTUR NAVIGASI SEDERHANA

```text
                         LOGIN
                           │
              ┌────────────┴────────────┐
              │                         │
          ADMIN                       STAFF
              │                         │
              ▼                         ▼
        ADMIN DASHBOARD          STAFF DASHBOARD
              │                         │
       ┌──────┼──────┐           ┌──────┼────────┐
       │      │      │           │      │        │
       ▼      ▼      ▼           ▼      ▼        ▼
    Approval Karyawan Rekap   Pengajuan Scan   Riwayat
       │
       ▼
    Settings
       │
 ┌─────┼──────────────┐
 ▼     ▼      ▼       ▼
Profile Tampilan Bantuan Tentang
       │
       ▼
     Keluar
```

---

# 54. ALUR ABSENSI STAFF

```text
Login
  │
  ▼
Staff Dashboard
  │
  ▼
Scan
  │
  ▼
Kamera
  │
  ▼
Scan QR
  │
  ▼
Validasi
  │
  ▼
Cek Absen Hari Ini
  │
  ▼
Simpan Absen Masuk
  │
  ▼
Firestore
  │
  ▼
Dashboard Refresh
  │
  ▼
Status = SUDAH ABSEN
```

---

# 55. ALUR PENGAJUAN

```text
Staff Dashboard
      │
      ▼
Pengajuan
      │
      ▼
Pengajuan Baru
      │
      ▼
Pilih Jenis
      │
      ▼
Isi Data
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
      ├── Disetujui
      │
      └── Ditolak
```

---

# 56. ALUR ADMIN MENGELOLA KARYAWAN

```text
Admin Dashboard
      │
      ▼
Karyawan
      │
      ▼
Daftar Karyawan
      │
      ▼
Tambah / Kelola
      │
      ▼
Firestore users
```

---

# 57. ALUR ADMIN MELIHAT ABSENSI

```text
Admin Dashboard
      │
      ▼
Rekap
      │
      ▼
Data Attendance
      │
      ▼
Informasi Kehadiran
```

---

# 58. ALUR LOGOUT

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
Login
```

---

# 59. ATURAN SESSION FINAL

Aturan yang sudah disepakati:

| Waktu         | Session                  |
| ------------- | ------------------------ |
| 05:00 - 20:59 | Tetap login              |
| 21:00 - 04:59 | Login kembali diperlukan |

Contoh:

### Siang

```text
07:00 Login
08:00 Tutup aplikasi
09:00 Buka kembali
      ↓
Tetap Login
```

### Malam

```text
22:00 Aplikasi dibuka
      ↓
Session diperiksa
      ↓
Login kembali
```

Akun Firebase tetap ada.

Yang dihapus hanya session login:

```text
FirebaseAuth.signOut()
```

---

# 60. FILE PENTING PROJECT

File yang berhubungan dengan alur aplikasi:

```text
MainActivity.kt
AppNavigation.kt
SessionManager.kt
```

Screen utama:

```text
LoginScreen.kt
AdminDashboardScreen.kt
StaffDashboardScreen.kt
ApprovalScreen.kt
KaryawanScreen.kt
AdminRekapScreen.kt
AdminSettingsScreen.kt
ScanAbsenScreen.kt
RiwayatScreen.kt
PengajuanScreen.kt
PengajuanBaruScreen.kt
SettingsScreen.kt
```

Repository:

```text
FirestoreRepository.kt
UserRepository.kt
```

DataStore:

```text
AbsensiDataStore.kt
ThemeDataStore.kt
```

---

# 61. PRINSIP PENGEMBANGAN PROJECT

Project ini dikembangkan secara incremental.

Aturan utama:

1. Jangan menghapus fitur yang sudah berjalan.
2. Jangan mengganti struktur yang sudah stabil tanpa alasan.
3. Jangan mengubah desain UI yang sudah disepakati tanpa permintaan.
4. Perubahan dilakukan pada bagian yang diperlukan saja.
5. Setelah perubahan, lakukan Build.
6. Jika Build berhasil, lanjut ke fitur berikutnya.
7. Jika error, perbaiki error terlebih dahulu sebelum melanjutkan.
8. Jangan membuat ulang screen yang sudah berjalan jika cukup melakukan perubahan kecil.
9. Pertahankan navigasi Admin dan Staff.
10. Pertahankan struktur Firebase yang sudah digunakan.

---

# 62. KONDISI FITUR SAAT INI

Fitur yang sudah dibuat/berjalan:

```text
✅ Login Firebase
✅ Forgot Password
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
✅ Absen Masuk
✅ Absen Pulang
✅ Riwayat Staff
✅ Staff Settings
✅ Staff Tampilan
✅ Staff Bantuan
✅ Staff Tentang Aplikasi
✅ Logout
✅ Firebase Firestore
✅ Firebase Authentication
✅ DataStore
✅ Theme DataStore
✅ Session berdasarkan waktu
```

---

# 63. SESSION MANAGER FINAL

File:

```text
SessionManager.kt
```

Aturan normal:

```kotlin
return hour in 5..20
```

Artinya:

```text
05:00 sampai 20:59
```

adalah waktu session persisten.

Sedangkan:

```text
21:00 sampai 04:59
```

adalah waktu session tidak persisten.

---

# 64. KOMPONEN YANG TIDAK BOLEH DIUBAH SEMBARANGAN

Beberapa bagian yang sudah dianggap stabil:

```text
Admin Settings
Staff Settings
Admin Navigation
Staff Navigation
Login
Firebase Authentication
Firestore structure
Session Manager
Theme system
QR Scanner
```

Perubahan pada bagian tersebut harus dilakukan secara incremental.

---

# 65. VERSI APLIKASI

Saat ini:

```text
Version Name : 1.1
Version Code : 1
```

Tampilan footer:

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

# 66. CHECKLIST SEBELUM MENAMBAH FITUR

Sebelum mengubah project:

```text
[ ] Tentukan file yang akan diubah
[ ] Baca kode yang sudah ada
[ ] Jangan hapus fitur lama
[ ] Pertahankan UI lama
[ ] Tambahkan perubahan seperlunya
[ ] Build Project
[ ] Test di emulator/device
[ ] Pastikan navigasi tetap normal
[ ] Pastikan Firebase tetap normal
[ ] Pastikan tidak ada regression
```

---

# 67. ALUR PENGEMBANGAN KE DEPAN

Pengembangan berikutnya mengikuti prinsip:

```text
ANALISIS
   │
   ▼
CEK KODE SAAT INI
   │
   ▼
TENTUKAN PERUBAHAN
   │
   ▼
UPDATE SECARA INCREMENTAL
   │
   ▼
BUILD
   │
   ▼
TEST
   │
   ├── ERROR → PERBAIKI
   │
   └── SUCCESS
          │
          ▼
       LANJUT
```

---

# 68. STATUS PROJECT

Project saat ini sudah memiliki struktur utama:

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
                      Tentang
                         │
                       Keluar
```

---

# 69. PATOKAN UTAMA

Dokumen ini menjadi patokan alur aplikasi **AbsensiKaryawan**.

Jika ada pengembangan berikutnya:

```text
Jangan langsung mengganti struktur.
```

Lakukan:

```text
Cek kondisi sekarang
        ↓
Pertahankan fitur lama
        ↓
Tambahkan fitur baru
        ↓
Build
        ↓
Test
```

Tujuan utama:

> **Menambah kemampuan aplikasi tanpa merusak fitur, navigasi, desain, dan struktur yang sudah berjalan.**

---

# 70. KESIMPULAN

Aplikasi Absensi Karyawan memiliki dua role utama:

```text
ADMIN
STAFF
```

Keduanya menggunakan Firebase Authentication untuk login dan Firestore untuk penyimpanan data.

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
Settings
```

Session login mengikuti waktu:

```text
05:00 - 20:59
→ Session dipertahankan

21:00 - 04:59
→ Login kembali diperlukan
```

Akun Firebase tidak dihapus ketika session berakhir.

Logout manual tetap tersedia melalui Settings.

Project selanjutnya dikembangkan secara incremental dengan prinsip:

```text
JANGAN HAPUS YANG SUDAH BERJALAN.
JANGAN UBAH DESAIN YANG SUDAH DISEPAKATI.
TAMBAHKAN FITUR SECARA BERTAHAP.
BUILD → TEST → LANJUT.
```

---

**Dokumen:** `ALUR_APLIKASI.md`
**Project:** `AbsensiKaryawan`
**Version:** `1.1`
**Tahun:** `2026`