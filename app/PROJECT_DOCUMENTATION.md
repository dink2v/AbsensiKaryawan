# PROJECT DOCUMENTATION

# Aplikasi Absensi Karyawan

**Platform:** Android
**Bahasa:** Kotlin
**UI:** Jetpack Compose + Material 3
**Backend:** Firebase
**Database:** Cloud Firestore
**Authentication:** Firebase Authentication
**QR Scanner:** CameraX + ML Kit
**Status:** FINAL TEST / READY TO USE

---

# 1. INFORMASI PROJECT

## Nama Project

`AbsensiKaryawan`

## Package

`com.example.absensikaryawan`

## Fungsi Utama

Aplikasi digunakan untuk mengelola:

* Login Staff
* Login Admin
* Presensi masuk
* Presensi pulang
* Scan QR
* Riwayat absensi
* Pengajuan karyawan
* Approval Admin
* Data karyawan
* Rekap absensi
* Pengaturan aplikasi
* Profil pengguna
* Bantuan
* Tentang aplikasi

---

# 2. TEKNOLOGI YANG DIGUNAKAN

Project menggunakan:

* Android Studio
* Kotlin
* Jetpack Compose
* Material 3
* Firebase Authentication
* Cloud Firestore
* CameraX
* Google ML Kit Barcode Scanning
* DataStore Preferences
* Kotlin Coroutines
* AndroidX
* Firebase BOM

---

# 3. KOMPATIBILITAS ANDROID

Konfigurasi project saat ini menggunakan:

```kotlin
compileSdk = 37
targetSdk = 37
minSdk = 24
```

Artinya aplikasi ditargetkan untuk Android versi terbaru yang didukung SDK project dan tetap dapat berjalan mulai Android 7.0/API 24.

Target utama project:

```text
Android 15 dan versi Android terbaru
```

Catatan:

Project ini dibuat khusus untuk Android.

Versi iOS membutuhkan implementasi aplikasi terpisah menggunakan teknologi seperti Kotlin Multiplatform, SwiftUI, atau framework cross-platform.

---

# 4. STRUKTUR PROJECT

```text
AbsensiKaryawan/
│
├── app/
│   └── src/
│       └── main/
│           │
│           ├── java/
│           │   └── com/example/absensikaryawan/
│           │       │
│           │       ├── data/
│           │       │   ├── AbsensiDataStore.kt
│           │       │   ├── FirestoreRepository.kt
│           │       │   └── UserRepository.kt
│           │       │
│           │       ├── navigation/
│           │       │   └── AppNavigation.kt
│           │       │
│           │       ├── screens/
│           │       │   ├── LoginScreen.kt
│           │       │   ├── ForgotPasswordScreen.kt
│           │       │
│           │       │   ├── StaffDashboardScreen.kt
│           │       │   ├── ProfileScreen.kt
│           │       │   ├── PengajuanScreen.kt
│           │       │   ├── PengajuanBaruScreen.kt
│           │       │   ├── DetailPengajuanScreen.kt
│           │       │   ├── ScanAbsenScreen.kt
│           │       │   ├── RiwayatScreen.kt
│           │       │   ├── SettingsScreen.kt
│           │       │   ├── TampilanScreen.kt
│           │       │   ├── NotifikasiScreen.kt
│           │       │   ├── BantuanScreen.kt
│           │       │   └── TentangAplikasiScreen.kt
│           │       │
│           │       │   ├── AdminDashboardScreen.kt
│           │       │   ├── ApprovalScreen.kt
│           │       │   ├── KaryawanScreen.kt
│           │       │   └── RekapAdminScreen.kt
│           │       │
│           │       └── ThemeDataStore.kt
│           │
│           └── AndroidManifest.xml
│
├── google-services.json
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
│
└── PROJECT_DOCUMENTATION.md
```

---

# 5. STRUKTUR NAVIGASI APLIKASI

```text
LOGIN
│
├── Staff
│   │
│   └── Dashboard Staff
│       │
│       ├── Beranda
│       ├── Pengajuan
│       │   ├── Pengajuan Baru
│       │   ├── Riwayat Pengajuan
│       │   └── Detail Pengajuan
│       │
│       ├── Scan
│       │   ├── Scan Absen Masuk
│       │   └── Scan Absen Pulang
│       │
│       ├── Riwayat
│       │
│       └── Setting
│           ├── Profil
│           ├── Tampilan
│           ├── Notifikasi
│           ├── Bantuan
│           ├── Tentang Aplikasi
│           └── Keluar
│
└── Admin
    │
    └── Dashboard Admin
        │
        ├── Beranda
        ├── Approval
        ├── Karyawan
        ├── Rekap
        └── Setting
            ├── Profil
            ├── Tampilan
            ├── Bantuan
            ├── Tentang Aplikasi
            └── Keluar
```

---

# 6. LOGIN DAN ROLE USER

Login menggunakan:

```text
Firebase Authentication
        │
        ▼
Email + Password
        │
        ▼
Firebase Authentication
        │
        ▼
Ambil email user
        │
        ▼
Firestore collection users
        │
        ▼
Cek isAdmin
        │
        ├── true  → Admin
        │
        └── false → Staff
```

Field utama:

```text
email
isAdmin
nama
uid
```

---

# 7. CARA MEMBUAT USER STAFF DI FIREBASE

## Langkah 1 — Firebase Authentication

Buka Firebase Console.

Masuk ke:

```text
Authentication
→ Users
→ Add user
```

Isi:

```text
Email    : staff1@gmail.com
Password : password yang ditentukan
```

Setelah user dibuat, Firebase akan menghasilkan:

```text
UID
```

Contoh:

```text
UID = ABC123456
```

Catat UID tersebut.

---

# 8. MEMBUAT DATA STAFF DI FIRESTORE

Masuk:

```text
Firestore Database
→ Data
→ users
→ Add document
```

## Document ID

Document ID **boleh menggunakan Auto-ID**.

Contoh:

```text
Document ID:
L7LZmute0wSvcDXr54Lf
```

Tidak harus sama dengan UID.

Kemudian buat field:

```text
email
staff1@gmail.com
```

```text
isAdmin
false
```

```text
nama
Nama Staff
```

```text
uid
ABC123456
```

Struktur:

```text
users
└── Auto-ID
    ├── email    : staff1@gmail.com
    ├── isAdmin  : false
    ├── nama     : Nama Staff
    └── uid      : ABC123456
```

---

# 9. CARA MEMBUAT USER ADMIN DI FIREBASE

## Langkah 1 — Authentication

Masuk:

```text
Firebase Console
→ Authentication
→ Users
→ Add user
```

Contoh:

```text
Email    : admin1@gmail.com
Password : password Admin
```

Firebase menghasilkan UID.

Contoh:

```text
6YmZXG2y62UNdAfjaYL9moXIvze2
```

---

# 10. DATA ADMIN DI FIRESTORE

Masuk:

```text
Firestore
→ users
→ Add document
```

Document ID boleh menggunakan:

```text
Auto-ID
```

Contoh:

```text
L7LZmute0wSvcDXr54Lf
```

Isi:

```text
email
admin1@gmail.com
```

```text
isAdmin
true
```

```text
nama
Admin
```

```text
uid
6YmZXG2y62UNdAfjaYL9moXIvze2
```

Hasil:

```text
users
└── L7LZmute0wSvcDXr54Lf
    ├── email    : admin1@gmail.com
    ├── isAdmin  : true
    ├── nama     : Admin
    └── uid      : 6YmZXG2y62UNdAfjaYL9moXIvze2
```

---

# 11. PENTING — DOCUMENT ID VS UID

Document ID dan UID adalah dua hal berbeda.

Contoh:

```text
Document ID
L7LZmute0wSvcDXr54Lf
```

Sedangkan:

```text
uid
6YmZXG2y62UNdAfjaYL9moXIvze2
```

Tidak masalah jika berbeda.

Yang penting:

```text
Firebase Authentication UID
=
Firestore users.uid
```

Contoh:

```text
Authentication UID
6YmZXG2y62UNdAfjaYL9moXIvze2

Firestore uid
6YmZXG2y62UNdAfjaYL9moXIvze2

STATUS: COCOK
```

---

# 12. STRUKTUR FIRESTORE

Database utama:

```text
Cloud Firestore
│
├── users
│
├── attendance
│
└── pengajuan
```

---

# 13. COLLECTION USERS

Digunakan untuk menyimpan profile dan role user.

Struktur:

```text
users
└── Auto-ID
    ├── email
    ├── isAdmin
    ├── nama
    └── uid
```

Contoh Staff:

```text
email    : staff1@gmail.com
isAdmin  : false
nama     : Staff
uid      : UID Firebase Staff
```

Contoh Admin:

```text
email    : admin1@gmail.com
isAdmin  : true
nama     : Admin
uid      : UID Firebase Admin
```

---

# 14. COLLECTION ATTENDANCE

Digunakan untuk menyimpan absensi.

Struktur:

```text
attendance
└── Document
    ├── uid
    ├── nama
    ├── tanggal
    ├── jamMasuk
    ├── jamPulang
    ├── qrData
    └── catatan
```

Contoh:

```text
uid       : UID Staff
nama      : Staff
tanggal   : 2026-09-01
jamMasuk  : 07:00:00
jamPulang : 16:00:00
qrData    : svg
catatan   :
```

---

# 15. COLLECTION PENGAJUAN

Digunakan untuk pengajuan karyawan.

Struktur:

```text
pengajuan
└── Document
    ├── uid
    ├── nama
    ├── jenis
    ├── tanggal
    ├── jamPulang
    ├── jamKeluar
    ├── jamKembali
    ├── tanggalMulai
    ├── tanggalSelesai
    ├── alasan
    ├── status
    └── catatanAdmin
```

Status pengajuan:

```text
menunggu
disetujui
ditolak
```

---

# 16. ABSENSI MASUK

Alur:

```text
Staff
 ↓
Dashboard
 ↓
Scan
 ↓
Scan QR
 ↓
Validasi QR
 ↓
Firebase Authentication
 ↓
Ambil UID
 ↓
Ambil nama user
 ↓
Cek attendance hari ini
 ↓
Belum ada absensi
 ↓
Simpan absensi masuk
 ↓
Simpan ke DataStore
 ↓
Dashboard diperbarui
```

Data yang disimpan:

```text
uid
nama
tanggal
jamMasuk
qrData
catatan
```

---

# 17. ABSENSI PULANG

Alur:

```text
Staff
 ↓
Scan QR
 ↓
Cek absensi hari ini
 ↓
Absensi masuk sudah ada
 ↓
Cek jamPulang
 ↓
Jam pulang masih kosong
 ↓
Update jamPulang
 ↓
Simpan ke Firestore
 ↓
Simpan ke DataStore
 ↓
Dashboard diperbarui
```

---

# 18. JIKA ABSENSI SUDAH LENGKAP

Jika:

```text
jamMasuk != kosong
jamPulang != kosong
```

maka aplikasi menganggap:

```text
ABSEN HARI INI SUDAH LENGKAP
```

---

# 19. PENGAJUAN STAFF

Staff dapat membuat pengajuan melalui:

```text
Pengajuan
→ Pengajuan Baru
```

Data yang dapat disimpan:

```text
jenis
tanggal
jamPulang
jamKeluar
jamKembali
tanggalMulai
tanggalSelesai
alasan
```

Aplikasi juga mengambil:

```text
uid
nama
```

secara otomatis dari user yang sedang login.

---

# 20. APPROVAL ADMIN

Admin dapat melihat pengajuan Staff.

Alur:

```text
Staff membuat pengajuan
        ↓
Firestore
        ↓
status = menunggu
        ↓
Admin membuka Approval
        ↓
Admin melihat detail
        ↓
Admin melakukan keputusan
        ↓
disetujui / ditolak
```

Catatan Admin dapat disimpan pada:

```text
catatanAdmin
```

---

# 21. DASHBOARD STAFF

Dashboard Staff berisi:

* Nama Staff
* Tanggal
* Jam real-time
* Status absensi
* Jam masuk
* Jam pulang
* Tombol Scan
* Informasi kehadiran hari ini
* Notifikasi
* Profil
* Pengajuan
* Riwayat
* Settings

---

# 22. DASHBOARD ADMIN

Dashboard Admin digunakan sebagai pusat kontrol Admin.

Menu utama:

```text
Beranda
Approval
Karyawan
Rekap
Setting
```

---

# 23. KARYAWAN ADMIN

Menu Karyawan digunakan untuk melihat data karyawan.

Data berasal dari:

```text
Firestore
→ users
```

Admin dapat melihat data user berdasarkan profile yang tersedia.

---

# 24. REKAP ADMIN

Menu Rekap digunakan untuk melihat data absensi.

Sumber:

```text
Firestore
→ attendance
```

Data utama:

```text
nama
tanggal
jamMasuk
jamPulang
```

---

# 25. SETTINGS STAFF

Menu:

```text
Setting
```

Berisi:

```text
Profil
Tampilan
Notifikasi
Bantuan
Tentang Aplikasi
Keluar
```

---

# 26. SETTINGS ADMIN

Menu:

```text
Setting
```

Berisi:

```text
Profil
Tampilan
Bantuan
Tentang Aplikasi
Keluar
```

Admin Settings tetap berada dalam area Admin dan menggunakan bottom navigation Admin.

---

# 27. TAMPILAN / THEME

Aplikasi menggunakan:

```text
ThemeDataStore
```

Mode yang digunakan:

```text
TERANG
```

Pengaturan theme disimpan menggunakan DataStore.

Perubahan mode dilakukan melalui:

```text
TampilanScreen
```

---

# 28. DATASTORE ABSENSI

Aplikasi menggunakan:

```text
AbsensiDataStore
```

Data yang digunakan antara lain:

```text
SUDAH_ABSEN
JAM_ABSEN
TANGGAL_ABSEN
JAM_PULANG
QR_ABSEN
CATATAN_ABSEN
QR_DATA
```

DataStore digunakan untuk membantu mempertahankan informasi absensi lokal pada perangkat.

---

# 29. QR SCANNER

Scanner menggunakan:

```text
CameraX
+
Google ML Kit Barcode Scanning
```

Library utama:

```text
androidx.camera
com.google.mlkit:barcode-scanning
```

Alur:

```text
Camera
 ↓
Image Analysis
 ↓
ML Kit Barcode Scanner
 ↓
QR terbaca
 ↓
QR Data diterima
 ↓
Proses absensi
```

---

# 30. LOGIN SCREEN

Login telah diperbarui dengan tampilan modern.

Komponen:

* Logo
* Judul aplikasi
* Email
* Password
* Tombol tampil/sembunyikan password
* Tombol Masuk
* Loading state
* Pesan error
* Lupa Sandi
* Informasi akun
* Footer aplikasi

Login menggunakan:

```text
FirebaseAuth.signInWithEmailAndPassword()
```

Setelah berhasil:

```text
Firebase Authentication
        ↓
Firestore users
        ↓
isAdmin
        ↓
Admin / Staff
```

---

# 31. ERROR LOGIN

Jika muncul:

```text
The supplied auth credential is incorrect,
malformed or has expired.
```

Periksa:

1. Email benar.
2. Password benar.
3. User sudah ada di Firebase Authentication.
4. Email Authentication aktif.
5. UID Authentication sesuai dengan Firestore.
6. User tidak terhapus/disabled.
7. Project Firebase yang digunakan Android sama dengan project yang digunakan Console.

---

# 32. CHECKLIST USER FIREBASE

## Staff

```text
Authentication
☑ Email dibuat
☑ Password dibuat
☑ UID tersedia

Firestore
☑ Collection users
☑ Document dibuat
☑ email benar
☑ nama benar
☑ isAdmin = false
☑ uid sama dengan Authentication
```

## Admin

```text
Authentication
☑ Email dibuat
☑ Password dibuat
☑ UID tersedia

Firestore
☑ Collection users
☑ Document dibuat
☑ email benar
☑ nama benar
☑ isAdmin = true
☑ uid sama dengan Authentication
```

---

# 33. FIREBASE ADMIN YANG SUDAH BERHASIL DITES

Admin:

```text
Email:
admin1@gmail.com
```

UID:

```text
6YmZXG2y62UNdAfjaYL9moXIvze2
```

Firestore:

```text
collection:
users
```

Field:

```text
email    = admin1@gmail.com
isAdmin  = true
nama     = Admin
uid      = 6YmZXG2y62UNdAfjaYL9moXIvze2
```

Document ID menggunakan Auto-ID dan tidak harus sama dengan UID.

Status:

```text
LOGIN ADMIN BERHASIL
```

---

# 34. STRUKTUR NAVIGASI BOTTOM BAR

## Staff

```text
┌────────┬──────────┬──────┬─────────┬─────────┐
│Beranda │ Pengajuan│ Scan │ Riwayat │ Setting │
└────────┴──────────┴──────┴─────────┴─────────┘
```

## Admin

```text
┌────────┬──────────┬──────────┬───────┬─────────┐
│Beranda │ Approval │ Karyawan │ Rekap │ Setting │
└────────┴──────────┴──────────┴───────┴─────────┘
```

---

# 35. STRUKTUR APPNAVIGATION

Navigation menggunakan enum:

```kotlin
private enum class AppScreen
```

Area Admin:

```text
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

Area Staff:

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

# 36. REPOSITORY

## UserRepository

Digunakan untuk:

* Mengambil data user
* Mengambil nama user
* Mengambil profile user
* Mengecek role Admin/Staff

---

## FirestoreRepository

Digunakan untuk:

* Menyimpan absensi
* Mengambil absensi
* Mengupdate absensi pulang
* Menyimpan pengajuan
* Mengambil data pengajuan
* Mengelola data Firestore yang diperlukan aplikasi

---

## AbsensiDataStore

Digunakan untuk:

* Status absensi
* Jam masuk
* Jam pulang
* Tanggal
* QR
* Catatan

---

# 37. ALUR LENGKAP STAFF

```text
LOGIN
 ↓
Firebase Authentication
 ↓
Firestore users
 ↓
isAdmin = false
 ↓
STAFF DASHBOARD
 │
 ├── Scan
 │    ├── Absen Masuk
 │    └── Absen Pulang
 │
 ├── Pengajuan
 │    ├── Pengajuan Baru
 │    ├── Riwayat Pengajuan
 │    └── Detail
 │
 ├── Riwayat
 │
 ├── Profil
 │
 └── Settings
      ├── Tampilan
      ├── Notifikasi
      ├── Bantuan
      ├── Tentang
      └── Logout
```

---

# 38. ALUR LENGKAP ADMIN

```text
LOGIN
 ↓
Firebase Authentication
 ↓
Firestore users
 ↓
isAdmin = true
 ↓
ADMIN DASHBOARD
 │
 ├── Approval
 │    └── Detail Pengajuan
 │
 ├── Karyawan
 │
 ├── Rekap
 │
 └── Settings
      ├── Profil
      ├── Tampilan
      ├── Bantuan
      ├── Tentang
      └── Logout
```

---

# 39. KEAMANAN DAN ROLE

Role Admin ditentukan oleh:

```text
isAdmin = true
```

Sedangkan Staff:

```text
isAdmin = false
```

Role disimpan di:

```text
Firestore
→ users
```

Authentication tetap dilakukan oleh:

```text
Firebase Authentication
```

---

# 40. URUTAN PEMBUATAN PROJECT

```text
STEP 0
│
├── Membuat project Android Studio
│
STEP 1
│
├── Setup Kotlin
├── Jetpack Compose
└── Material 3
│
STEP 2
│
├── Firebase
├── Authentication
└── Firestore
│
STEP 3
│
├── Login
├── Role Staff
└── Role Admin
│
STEP 4
│
├── Dashboard Staff
└── Dashboard Admin
│
STEP 5
│
├── Karyawan Admin
├── Approval
└── Rekap Admin
│
STEP 6
│
├── Settings Admin
└── Settings Staff
│
STEP 7
│
├── QR Scanner
├── Absensi Masuk
└── Absensi Pulang
│
STEP 8
│
├── Riwayat
├── Pengajuan
└── Detail Pengajuan
│
STEP 9
│
├── DataStore
├── Theme
└── Penyempurnaan UI
│
STEP 10
│
├── Login Modern
├── Target Android terbaru
└── Final Testing
```

---

# 41. KONDISI PROJECT SAAT INI

```text
STEP 0  → Struktur Project              ✅ CLEAR
STEP 1  → Firebase                      ✅ CLEAR
STEP 2  → Authentication                ✅ CLEAR
STEP 3  → Login Staff/Admin             ✅ CLEAR
STEP 4  → Dashboard                     ✅ CLEAR
STEP 5  → Karyawan Admin                ✅ CLEAR
STEP 6  → Rekap Admin                   ✅ CLEAR
STEP 7  → Absensi QR                    ✅ CLEAR
STEP 8  → Pengajuan & Approval          ✅ CLEAR
STEP 9  → Settings & Theme              ✅ CLEAR
STEP 10 → Login Modern                  ✅ CLEAR
STEP 11 → Firebase Staff/Admin          ✅ CLEAR
STEP 12 → Target Android terbaru        ✅ CLEAR
```

---

# 42. FINAL TEST

Sebelum aplikasi dinyatakan Ready to Use:

## Login

```text
☑ Login Staff
☑ Login Admin
☑ Logout Staff
☑ Logout Admin
☑ Lupa Sandi
```

## Staff

```text
☑ Dashboard
☑ Profil
☑ Scan QR
☑ Absen Masuk
☑ Absen Pulang
☑ Riwayat
☑ Pengajuan
☑ Pengajuan Baru
☑ Detail Pengajuan
☑ Settings
☑ Tampilan
☑ Notifikasi
☑ Bantuan
☑ Tentang Aplikasi
```

## Admin

```text
☑ Dashboard
☑ Approval
☑ Detail Pengajuan
☑ Karyawan
☑ Rekap
☑ Settings
☑ Profil
☑ Tampilan
☑ Bantuan
☑ Tentang Aplikasi
```

## Firebase

```text
☑ Authentication Staff
☑ Authentication Admin
☑ users
☑ attendance
☑ pengajuan
```

---

# 43. STATUS AKHIR

```text
PROJECT
AbsensiKaryawan

STATUS:
FINAL TEST

AUTHENTICATION:
Firebase Authentication

DATABASE:
Cloud Firestore

PLATFORM:
Android

TARGET:
Android 15 / Android terbaru

ROLE:
Staff + Admin

QR:
CameraX + ML Kit

LOCAL STORAGE:
DataStore

UI:
Jetpack Compose + Material 3
```

---

# 44. CATATAN PENTING UNTUK PENGEMBANGAN SELANJUTNYA

Fitur yang sudah berjalan **jangan dihapus atau dirombak tanpa alasan**.

Jika melakukan perubahan:

```text
1. Pertahankan fitur yang sudah berjalan.
2. Tambahkan fitur secara incremental.
3. Jangan menghapus navigation yang sudah digunakan.
4. Jangan mengubah struktur Firestore tanpa pengecekan.
5. Pastikan UID Authentication dan Firestore tetap sinkron.
6. Build project setelah perubahan.
7. Test Staff.
8. Test Admin.
9. Test Firebase.
10. Baru commit ke Git.
```

---

# 45. BACKUP DAN GITHUB

Sebelum perubahan besar:

```bash
git status
```

Kemudian:

```bash
git add .
```

Commit:

```bash
git commit -m "Final update AbsensiKaryawan"
```

Push:

```bash
git push
```

Jika project sudah benar-benar stabil, gunakan Git sebagai titik backup sebelum melakukan perubahan besar berikutnya.

---

# 46. KESIMPULAN

Aplikasi **AbsensiKaryawan** telah dibangun dari awal menggunakan Kotlin dan Jetpack Compose dengan Firebase sebagai backend.

Sistem sudah memiliki dua role:

```text
STAFF
ADMIN
```

Staff digunakan untuk aktivitas presensi dan pengajuan.

Admin digunakan untuk mengelola approval, karyawan, dan rekap.

Firebase Authentication digunakan untuk proses login, sedangkan Cloud Firestore digunakan untuk menyimpan profile user, absensi, dan pengajuan.

Document ID Firestore dapat menggunakan **Auto-ID**. UID Firebase Authentication tetap disimpan pada field `uid` dan harus sama dengan UID user pada Firebase Authentication.

Kondisi terakhir:

```text
LOGIN STAFF       ✅
LOGIN ADMIN       ✅
FIREBASE          ✅
FIRESTORE         ✅
ABSENSI           ✅
QR SCANNER        ✅
PENGAJUAN         ✅
APPROVAL          ✅
KARYAWAN          ✅
REKAP             ✅
SETTINGS          ✅
LOGIN MODERN      ✅
ANDROID TARGET    ✅
```

**Status Project: FINAL TEST / READY TO USE**

---

# 47. IDENTITAS PROJECT

```text
Nama        : Absensi Karyawan
Package     : com.example.absensikaryawan
Platform    : Android
Framework   : Jetpack Compose
Language    : Kotlin
Backend     : Firebase
Database    : Cloud Firestore
Auth        : Firebase Authentication
Scanner     : CameraX + ML Kit
Storage     : DataStore
UI          : Material 3
```

**Dokumentasi ini menjadi catatan utama struktur dan alur project AbsensiKaryawan.**
