# MASTER DOCUMENTATION

# LAPORAN LENGKAP PENGEMBANGAN APLIKASI ABSENSI KARYAWAN

## 1. IDENTITAS PROJECT

**Nama Aplikasi:**
Absensi Karyawan

**Nama Project:**
AbsensiKaryawan

**Package:**
`com.example.absensikaryawan`

**Platform:**
Android

**Bahasa Pemrograman:**
Kotlin

**Framework UI:**
Jetpack Compose

**Backend:**
Firebase

**Database:**
Cloud Firestore

**Authentication:**
Firebase Authentication

**Local Storage:**
DataStore Preferences

**QR Generator:**
ZXing

**QR Scanner:**
CameraX + ML Kit Barcode Scanning

**Versi Aplikasi:**
1.1

**Version Code:**
2

**Tahun Pengembangan:**
2026

**Gradle Version:**
9.7.1

**Android Gradle Plugin:**
9.4.0

**Kotlin Version:**
2.2.10

**Compile SDK:**
37

**Target SDK:**
37

**Minimum SDK:**
24

---

# 2. LATAR BELAKANG

Absensi merupakan salah satu kebutuhan penting dalam pengelolaan data kehadiran karyawan. Sistem absensi manual memiliki beberapa keterbatasan, seperti proses pencatatan yang membutuhkan waktu, kemungkinan kesalahan pencatatan, serta kesulitan dalam melakukan rekap data.

Untuk mengatasi permasalahan tersebut dikembangkan aplikasi **Absensi Karyawan berbasis Android** yang memanfaatkan QR Code dan Firebase.

Aplikasi ini dirancang agar proses absensi dapat dilakukan secara digital, data tersimpan secara terpusat di Cloud Firestore, dan Admin dapat melakukan pengelolaan data karyawan, absensi, pengajuan, komunikasi, serta notifikasi melalui aplikasi.

---

# 3. TUJUAN PENGEMBANGAN

Tujuan utama pengembangan aplikasi adalah:

1. Membuat sistem absensi karyawan berbasis Android.
2. Mempermudah proses absensi masuk dan pulang.
3. Menggunakan QR Code sebagai identifikasi lokasi kantor.
4. Menyimpan data absensi secara online menggunakan Firebase.
5. Memisahkan hak akses Admin dan Staff.
6. Menyediakan sistem pengajuan yang dapat diproses Admin.
7. Menyediakan komunikasi Staff dengan Admin.
8. Menyediakan sistem notifikasi.
9. Menyediakan riwayat dan rekap absensi.
10. Menyediakan sistem session dan keamanan login.
11. Menyediakan pengelolaan QR kantor.
12. Menyediakan aplikasi yang dapat digunakan sebagai sistem absensi terintegrasi.

---

# 4. TEKNOLOGI YANG DIGUNAKAN

Teknologi utama yang digunakan dalam pengembangan aplikasi:

### Android

Aplikasi dikembangkan khusus untuk perangkat Android.

### Kotlin

Kotlin digunakan sebagai bahasa pemrograman utama.

### Jetpack Compose

Jetpack Compose digunakan untuk membangun antarmuka aplikasi.

### Firebase Authentication

Digunakan untuk:

* Login
* Logout
* Reset Password
* Validasi user

### Cloud Firestore

Digunakan untuk menyimpan:

* Data user
* Data absensi
* Data pengajuan
* Data QR kantor
* Data chat
* Data notifikasi

### DataStore

Digunakan untuk menyimpan data lokal yang diperlukan aplikasi.

### CameraX

Digunakan untuk mengakses kamera perangkat.

### ML Kit

Digunakan untuk membaca QR Code melalui kamera.

### ZXing

Digunakan untuk menghasilkan QR Code kantor.

---

# 5. ARSITEKTUR SISTEM

Alur utama aplikasi:

```text
                    START
                      │
                      ▼
                Buka Aplikasi
                      │
                      ▼
                 Cek Session
                      │
                      ▼
                    Login
                      │
                      ▼
          Firebase Authentication
                      │
                      ▼
              Ambil Data User
                      │
                      ▼
               Cek isAdmin
                 /        \
                /          \
               ▼            ▼
            ADMIN          STAFF
               │            │
               ▼            ▼
       AdminNavigation  StaffNavigation
               │            │
               ▼            ▼
          Admin Menu     Staff Menu
```

---

# 6. SISTEM LOGIN

Login menggunakan Firebase Authentication dengan metode:

```text
Email + Password
```

Alur login:

```text
User memasukkan email
        ↓
User memasukkan password
        ↓
Firebase Authentication
        ↓
Login berhasil
        ↓
Ambil data user
        ↓
Cek isAdmin
        ↓
Tentukan role
        ↓
Masuk ke halaman Admin/Staff
```

Sistem juga memiliki penanganan error login.

Jenis error yang ditangani antara lain:

* Email tidak valid
* Password salah
* User tidak ditemukan
* User dinonaktifkan
* Credential tidak valid
* Terlalu banyak percobaan
* Gangguan jaringan

**Status: SELESAI**

---

# 7. SISTEM ROLE USER

Role user ditentukan melalui collection:

```text
users
```

Dengan field:

```text
isAdmin
```

Ketentuan:

```text
isAdmin = true
→ ADMIN
```

```text
isAdmin = false
→ STAFF
```

Dengan sistem ini, menu dan akses antara Admin dan Staff dapat dipisahkan.

**Status: SELESAI**

---

# 8. FORGOT PASSWORD

Aplikasi menyediakan fitur lupa password.

Alurnya:

```text
Login
 ↓
Lupa Password
 ↓
Masukkan Email
 ↓
Firebase Authentication
 ↓
Email Reset Password
```

Password tidak ditampilkan atau disimpan secara langsung oleh aplikasi.

**Status: SELESAI**

---

# 9. SESSION MANAGEMENT

Aplikasi memiliki pengaturan session berdasarkan waktu.

Ketentuan:

```text
06:00 – 17:59
```

Session tetap dapat digunakan.

```text
18:00 – 05:59
```

User harus melakukan login kembali.

Session menggunakan Firebase Authentication.

Jika session berakhir, aplikasi melakukan:

```text
FirebaseAuth.signOut()
```

Proses tersebut tidak menghapus:

* Akun Firebase
* Data Firestore
* Data karyawan
* Data absensi

**Status: SELESAI**

---

# 10. SISTEM NAVIGATION

Navigation aplikasi dibagi menjadi:

```text
AppNavigation
├── Login
├── Forgot Password
├── AdminNavigation
└── StaffNavigation
```

Role aplikasi:

```text
NONE
ADMIN
STAFF
```

Setelah login, aplikasi menentukan navigation berdasarkan role user.

**Status: SELESAI**

---

# 11. ADMIN NAVIGATION

Menu utama Admin:

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
Chat
Notifikasi
Profile
Theme
Bantuan
Tentang Aplikasi
```

---

# 12. STAFF NAVIGATION

Menu utama Staff:

```text
Beranda
Pengajuan
Scan
Riwayat
Setting
```

Fitur tambahan:

```text
Absen Luar Kantor
Notifikasi
Chat Admin
Profile
Tampilan
Bantuan
Tentang Aplikasi
```

---

# 13. DASHBOARD ADMIN

Dashboard Admin digunakan sebagai halaman utama Admin.

Fungsinya sebagai pusat akses ke:

* Data karyawan
* Approval
* Rekap
* QR kantor
* Chat
* Notifikasi
* Settings

**Status: SELESAI**

---

# 14. DASHBOARD STAFF

Dashboard Staff digunakan sebagai halaman utama karyawan.

Fitur yang terhubung dengan Staff antara lain:

* Absensi
* Scan QR
* Pengajuan
* Riwayat
* Chat Admin
* Notifikasi
* Settings

**Status: SELESAI**

---

# 15. SISTEM ABSENSI

Data absensi disimpan pada:

```text
attendance
```

Data utama yang digunakan:

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

Data absensi disimpan ke Cloud Firestore sehingga dapat diakses kembali oleh sistem.

---

# 16. ABSEN MASUK

Proses absen masuk menggunakan QR Code kantor.

Alur:

```text
Staff
 ↓
Scan QR
 ↓
Validasi QR
 ↓
Deteksi Kantor
 ↓
Cek Absensi Hari Ini
 ↓
Jika belum absen
 ↓
Simpan Absensi
 ↓
Firestore
 ↓
Berhasil
```

Sistem mencegah proses absensi ganda pada hari yang sama.

**Status: SELESAI**

---

# 17. ABSEN PULANG

Jika Staff sudah melakukan absen masuk, sistem dapat memproses absensi pulang sesuai aturan waktu yang digunakan aplikasi.

Data jam pulang diperbarui pada data attendance yang sama.

Alur:

```text
Absensi Masuk
 ↓
Scan / proses Absen Pulang
 ↓
Cari Attendance Hari Ini
 ↓
Update jamPulang
 ↓
Firestore
```

Dengan demikian data masuk dan pulang tetap berada pada data absensi karyawan yang sama.

**Status: SELESAI**

---

# 18. ABSEN LUAR KANTOR

Aplikasi menyediakan fitur:

```text
Absen Luar Kantor
```

Staff dapat mengisi informasi yang diperlukan seperti:

```text
Lokasi
Alasan
```

Data tersebut disimpan bersama informasi:

```text
Nama
UID
Tanggal
Jam
Lokasi
Alasan
```

Sistem juga melakukan pengecekan apakah user sudah melakukan absensi pada hari tersebut.

**Status: SELESAI**

---

# 19. SISTEM QR CODE

Aplikasi menggunakan QR Code sebagai identifikasi kantor.

Terdapat tiga QR kantor:

```text
KANTOR MALANG
KANTOR BLITAR
KANTOR KEDIRI
```

QR resmi:

```text
KANTOR MALANG
https://q.me-qr.com/x5ie23mg

KANTOR BLITAR
https://q.me-qr.com/hbywvgy7

KANTOR KEDIRI
https://q.me-qr.com/14vy2ipr
```

---

# 20. QR SETTINGS

Data QR kantor disimpan pada:

```text
qr_settings
```

Field utama:

```text
officeName
qrData
aktif
```

QR yang berstatus:

```text
aktif = true
```

dapat digunakan oleh scanner.

QR yang tidak aktif tidak dapat digunakan untuk absensi.

---

# 21. QR GENERATOR

Admin memiliki sistem untuk membuat QR kantor.

Alur:

```text
Admin
 ↓
QR Settings
 ↓
Pilih Kantor
 ↓
Generate QR
 ↓
Preview QR
 ↓
Simpan
 ↓
Firestore
```

QR dibuat menggunakan:

```text
ZXing
```

---

# 22. QR SCANNER

Scanner menggunakan:

```text
CameraX
+
ML Kit Barcode Scanning
```

Scanner melakukan validasi terhadap QR yang ditemukan.

Alurnya:

```text
Kamera
 ↓
Deteksi QR
 ↓
Baca Data
 ↓
Normalisasi Data
 ↓
Cocokkan dengan qr_settings
 ↓
QR Valid?
 ├── YA → Deteksi Kantor
 └── TIDAK → Ditolak
```

Scanner hanya menerima QR kantor yang terdaftar dan aktif.

---

# 23. VALIDASI QR

Sistem scanner memiliki beberapa validasi:

* QR harus terdaftar.
* QR harus aktif.
* Data QR harus sesuai.
* QR harus berada pada area scan.
* QR tidak boleh terlalu kecil.
* QR tidak boleh berada terlalu jauh dari frame.
* QR harus terbaca beberapa frame sebelum diproses.
* QR tidak boleh diproses berkali-kali.

Validasi frame menggunakan mekanisme beberapa frame untuk mengurangi kesalahan pembacaan.

**Status: SELESAI**

---

# 24. DETEKSI KANTOR

Setelah QR berhasil divalidasi, aplikasi menentukan kantor berdasarkan QR.

Contoh:

```text
QR Malang
 ↓
KANTOR MALANG
```

```text
QR Blitar
 ↓
KANTOR BLITAR
```

```text
QR Kediri
 ↓
KANTOR KEDIRI
```

Informasi kantor kemudian dapat digunakan dalam pencatatan absensi.

---

# 25. PENCEGAHAN ABSENSI GANDA

Sistem melakukan pengecekan berdasarkan:

```text
UID
+
Tanggal
```

Jika user sudah memiliki absensi pada tanggal tersebut, sistem tidak membuat data absensi baru.

Hal ini bertujuan untuk mencegah:

```text
Absensi masuk dua kali
Absensi luar kantor setelah sudah absen
Duplikasi data attendance
```

**Status: SELESAI**

---

# 26. RIWAYAT ABSENSI

Staff memiliki halaman:

```text
Riwayat
```

Riwayat menampilkan data absensi yang telah dilakukan.

Informasi yang dapat ditampilkan antara lain:

```text
Tanggal
Jam Masuk
Jam Pulang
Kantor
Status
Catatan
```

Riwayat juga mendukung pemisahan antara:

```text
Absensi
Pengajuan
```

---

# 27. SISTEM PENGAJUAN

Staff dapat membuat pengajuan melalui:

```text
Pengajuan Baru
```

Jenis data yang dapat digunakan antara lain:

```text
Jenis Pengajuan
Jam Pulang
Jam Keluar
Jam Kembali
Tanggal Mulai
Tanggal Selesai
Alasan
```

Status awal:

```text
menunggu
```

---

# 28. ALUR PENGAJUAN

```text
Staff
 ↓
Buat Pengajuan
 ↓
Isi Data
 ↓
Kirim
 ↓
Firestore
 ↓
Status = menunggu
 ↓
Notifikasi Admin
 ↓
Admin melakukan Approval
```

---

# 29. HALAMAN PENGAJUAN BERHASIL

Setelah pengajuan berhasil dikirim, aplikasi menampilkan halaman sukses.

Informasi yang ditampilkan:

```text
Pengajuan Berhasil Dikirim

Pengajuan kamu sudah berhasil dikirim
dan sedang menunggu persetujuan admin.

Menunggu Persetujuan Admin
```

Kemudian user dapat menekan:

```text
Selesai
```

---

# 30. APPROVAL ADMIN

Admin dapat memproses pengajuan Staff.

Status:

```text
menunggu
```

dapat diubah menjadi:

```text
disetujui
```

atau:

```text
ditolak
```

Alurnya:

```text
Pengajuan Staff
 ↓
Admin Approval
 ↓
Validasi Data
 ↓
Update Firestore
 ↓
Status Berubah
 ↓
Kirim Notifikasi Staff
```

---

# 31. NOTIFIKASI PENGAJUAN

Ketika Staff membuat pengajuan, Admin mendapatkan notifikasi.

Ketika Admin menyetujui atau menolak pengajuan, Staff mendapatkan notifikasi.

Target notifikasi antara lain:

```text
PENGAJUAN_DISETUJUI
PENGAJUAN_DITOLAK
PENGAJUAN_MENUNGGU
```

---

# 32. SISTEM CHAT

Aplikasi memiliki komunikasi:

```text
STAFF ↔ ADMIN
```

Data chat menggunakan struktur:

```text
chatRooms
 └── staffUid
      └── messages
```

Model pesan:

```text
id
senderId
senderName
senderType
message
timestamp
```

---

# 33. CHAT STAFF

Staff dapat membuka:

```text
Chat Admin
```

Header:

```text
Hubungi ADMIN
```

Status:

```text
Siap membantu
```

Staff dapat mengirim pesan kepada Admin.

---

# 34. CHAT ADMIN

Admin dapat melihat daftar percakapan Staff.

Admin dapat:

```text
Melihat chat
Membuka percakapan
Membalas pesan
```

Pesan Admin kemudian diterima oleh Staff secara realtime.

**Status: SELESAI**

---

# 35. NOTIFIKASI CHAT

Ketika Staff mengirim pesan:

```text
Staff
 ↓
Chat
 ↓
Admin mendapat notifikasi
```

Ketika Admin membalas:

```text
Admin
 ↓
Chat
 ↓
Staff mendapat notifikasi
```

---

# 36. SISTEM NOTIFIKASI

Collection:

```text
notifications
```

Model utama:

```text
id
userId
type
title
message
timestamp
isRead
relatedId
```

Notifikasi digunakan untuk:

```text
Absensi
Pengajuan
Chat
```

---

# 37. NOTIFIKASI ABSENSI

Ketika Staff melakukan absensi, sistem dapat mengirim notifikasi kepada Admin.

Contoh informasi:

```text
Absensi Baru

[Nama Staff] melakukan absensi
masuk pukul [jam] di [kantor].
```

Notifikasi menyimpan informasi terkait user, tanggal, dan data absensi.

---

# 38. NOTIFIKASI STAFF

Staff dapat menerima notifikasi:

```text
Pengajuan disetujui
Pengajuan ditolak
Pengajuan menunggu
Pesan dari Admin
```

---

# 39. NOTIFIKASI ADMIN

Admin dapat menerima notifikasi:

```text
Absensi Staff
Pengajuan baru
Pesan dari Staff
```

---

# 40. SISTEM READ / UNREAD

Notifikasi memiliki status:

```text
isRead = false
```

untuk notifikasi yang belum dibaca.

Ketika notifikasi dibuka:

```text
isRead = true
```

Sistem juga menampilkan jumlah notifikasi yang belum dibaca.

---

# 41. NOTIFICATION ROUTING

Notifikasi dapat mengarahkan user ke halaman yang sesuai.

Contoh:

```text
Notifikasi Absensi
 ↓
Riwayat Absensi
```

```text
Notifikasi Pengajuan
 ↓
Riwayat Pengajuan
```

```text
Notifikasi Chat
 ↓
Chat Admin
```

Dengan demikian notifikasi tidak hanya menjadi informasi, tetapi juga dapat digunakan sebagai shortcut menuju fitur terkait.

---

# 42. FIRESTORE

Collection utama:

```text
users
attendance
pengajuan
qr_settings
chatRooms
notifications
```

Struktur:

```text
Firestore
│
├── users
│
├── attendance
│
├── pengajuan
│
├── qr_settings
│
├── chatRooms
│   └── messages
│
└── notifications
```

---

# 43. FIRESTORE SECURITY RULES

Collection yang digunakan aplikasi telah diberikan akses untuk user yang sudah terautentikasi.

Collection meliputi:

```text
users
attendance
pengajuan
chatRooms
qr_settings
notifications
```

Akses membutuhkan:

```text
request.auth != null
```

Dengan demikian operasi Firestore dilakukan oleh user yang telah login.

---

# 44. DATASTORE

Data lokal menggunakan:

```text
DataStore Preferences
```

Key yang digunakan antara lain:

```text
sudah_absen
jam_absen
tanggal_absen
jam_pulang
qr_absen
catatan_absen
```

DataStore digunakan sebagai penyimpanan lokal pendukung.

Sedangkan data utama absensi tetap disimpan pada:

```text
Cloud Firestore
```

---

# 45. THEME APLIKASI

Aplikasi mendukung:

```text
TERANG
GELAP
SISTEM
```

Warna utama aplikasi menggunakan konsep hijau sebagai warna identitas aplikasi.

Warna utama antara lain:

```text
Background
Primary Green
Soft Green
Text Dark
Text Gray
Bottom Navigation Green
```

Theme dapat disimpan menggunakan DataStore.

---

# 46. STATUS BAR DAN NAVIGATION BAR

Aplikasi menggunakan:

```text
enableEdgeToEdge()
```

Penanganan inset Android telah dilakukan agar konten aplikasi tidak bertabrakan dengan:

```text
Status Bar
Navigation Bar
```

Penyesuaian juga dilakukan pada halaman yang memiliki masalah posisi konten.

Tujuannya agar tampilan tetap sesuai pada perangkat Android modern.

---

# 47. SETTINGS

Menu Settings digunakan untuk menyediakan pengaturan aplikasi.

Fitur yang tersedia mencakup:

```text
Profile
Tampilan
Bantuan
Tentang Aplikasi
Logout
```

Theme juga dapat dikontrol melalui pengaturan tampilan.

---

# 48. LOGOUT

Logout dilakukan melalui:

```text
FirebaseAuth.signOut()
```

Alur:

```text
Settings
 ↓
Logout
 ↓
Firebase Sign Out
 ↓
Session berakhir
 ↓
Login Screen
```

Logout tidak menghapus data Firebase.

---

# 49. STRUKTUR FITUR ADMIN

```text
ADMIN
│
├── Dashboard
│
├── Approval
│
├── Karyawan
│
├── Rekap
│
├── QR Settings
│
├── Chat
│
├── Notifikasi
│
└── Settings
    ├── Profile
    ├── Tampilan
    ├── Bantuan
    ├── Tentang
    └── Logout
```

---

# 50. STRUKTUR FITUR STAFF

```text
STAFF
│
├── Dashboard
│
├── Scan QR
│
├── Absen Masuk
│
├── Absen Pulang
│
├── Absen Luar Kantor
│
├── Pengajuan
│   ├── Pengajuan Baru
│   └── Detail Pengajuan
│
├── Riwayat
│   ├── Riwayat Absensi
│   └── Riwayat Pengajuan
│
├── Chat Admin
│
├── Notifikasi
│
└── Settings
    ├── Profile
    ├── Tampilan
    ├── Bantuan
    ├── Tentang
    └── Logout
```

---

# 51. TESTING LOGIN

Pengujian yang dilakukan:

| Pengujian         | Status |
| ----------------- | ------ |
| Login Admin       | ✅      |
| Login Staff       | ✅      |
| Password salah    | ✅      |
| Email tidak valid | ✅      |
| Forgot Password   | ✅      |
| Role Admin        | ✅      |
| Role Staff        | ✅      |
| Logout            | ✅      |

---

# 52. TESTING SESSION

Pengujian:

| Kondisi                  | Status |
| ------------------------ | ------ |
| Session 06:00            | ✅      |
| Session 17:59            | ✅      |
| Session 18:00            | ✅      |
| Session malam            | ✅      |
| Login ulang              | ✅      |
| Data Firebase tetap aman | ✅      |

---

# 53. TESTING QR

Pengujian:

| Pengujian               | Status    |
| ----------------------- | --------- |
| QR Malang               | ✅         |
| QR Blitar               | ✅         |
| QR Kediri               | ✅         |
| QR tidak terdaftar      | ✅ Ditolak |
| QR tidak aktif          | ✅ Ditolak |
| QR terlalu kecil        | ✅         |
| QR di luar frame        | ✅         |
| Validasi beberapa frame | ✅         |
| Pencegahan double scan  | ✅         |
| Reset scanner           | ✅         |
| Deteksi kantor          | ✅         |

---

# 54. TESTING ABSENSI

Pengujian:

```text
Absen Masuk
✓

Absen Pulang
✓

Absen Luar Kantor
✓

Penyimpanan Firestore
✓

UID
✓

Nama
✓

Tanggal
✓

Jam
✓

Kantor
✓

Pencegahan duplikasi
✓
```

**Status: SELESAI**

---

# 55. TESTING PENGAJUAN

Pengujian:

```text
Buat pengajuan
✓

Simpan Firestore
✓

Status menunggu
✓

Notifikasi Admin
✓

Approval
✓

Penolakan
✓

Notifikasi Staff
✓

Riwayat Pengajuan
✓
```

**Status: SELESAI**

---

# 56. TESTING CHAT

Pengujian:

```text
Staff kirim pesan
✓

Admin menerima
✓

Admin membalas
✓

Staff menerima
✓

Realtime update
✓

Notifikasi chat
✓
```

**Status: SELESAI**

---

# 57. TESTING NOTIFIKASI

Pengujian:

```text
Notifikasi absensi
✓

Notifikasi pengajuan
✓

Notifikasi approval
✓

Notifikasi penolakan
✓

Notifikasi chat
✓

Unread count
✓

Mark as read
✓

Delete notification
✓

Routing notification
✓
```

**Status: SELESAI**

---

# 58. TESTING NAVIGATION

Navigation Admin:

```text
Dashboard
✓

Approval
✓

Karyawan
✓

Rekap
✓

Settings
✓
```

Navigation Staff:

```text
Dashboard
✓

Pengajuan
✓

Scan
✓

Riwayat
✓

Settings
✓
```

**Status: SELESAI**

---

# 59. TESTING FIRESTORE

Collection yang telah digunakan:

```text
users
✓

attendance
✓

pengajuan
✓

qr_settings
✓

chatRooms
✓

notifications
✓
```

Authentication:

```text
Firebase Authentication
✓
```

**Status: SELESAI**

---

# 60. PERBAIKAN ERROR YANG TELAH DILAKUKAN

Selama proses pengembangan dilakukan beberapa perbaikan, antara lain:

### Login

Perbaikan error authentication dan error handling.

### Firestore

Perbaikan permission pada collection yang digunakan aplikasi.

### Notification

Penambahan rule Firestore untuk collection:

```text
notifications
```

### Pengajuan

Perbaikan proses:

```text
Submit
Approval
Reject
Notification
```

### Chat

Perbaikan komunikasi:

```text
Staff → Admin
Admin → Staff
```

### Navigation

Perbaikan routing berdasarkan role.

### Android Insets

Perbaikan posisi UI agar tidak tertutup Status Bar atau Navigation Bar.

### QR Scanner

Perbaikan validasi QR, tracking, frame validation, dan pencegahan double processing.

---

# 61. BUILD PROJECT

Project telah melalui proses build menggunakan Gradle.

Tahapan:

```text
Clean
 ↓
Build
 ↓
Compile
 ↓
Check Kotlin
 ↓
BUILD SUCCESSFUL
```

Build terakhir yang dilaporkan berhasil:

```text
BUILD SUCCESSFUL
```

Dengan demikian konfigurasi project setelah upgrade Gradle/AGP dapat melakukan proses build tanpa error Kotlin yang sebelumnya muncul.

---

# 62. KOMPATIBILITAS ANDROID

Project menggunakan:

```text
minSdk 24
targetSdk 37
compileSdk 37
```

Aplikasi menggunakan pendekatan modern Android:

```text
Jetpack Compose
Edge-to-Edge
CameraX
ML Kit
Firebase
```

Penyesuaian system bar juga dilakukan untuk mendukung perangkat Android modern.

---

# 63. STATUS FITUR PROJECT

| Fitur                   | Status         |
| ----------------------- | -------------- |
| Firebase Authentication | ✅ SELESAI      |
| Login                   | ✅ SELESAI      |
| Forgot Password         | ✅ SELESAI      |
| Role Admin/Staff        | ✅ SELESAI      |
| Session                 | ✅ SELESAI      |
| Admin Dashboard         | ✅ SELESAI      |
| Staff Dashboard         | ✅ SELESAI      |
| Absensi Masuk           | ✅ SELESAI      |
| Absensi Pulang          | ✅ SELESAI      |
| Absen Luar Kantor       | ✅ SELESAI      |
| QR Scanner              | ✅ SELESAI      |
| QR Validation           | ✅ SELESAI      |
| QR Settings             | ✅ SELESAI      |
| Data Karyawan           | ✅ SELESAI      |
| Rekap                   | ✅ SELESAI      |
| Pengajuan               | ✅ SELESAI      |
| Approval                | ✅ SELESAI      |
| Chat Staff/Admin        | ✅ SELESAI      |
| Notifikasi              | ✅ SELESAI      |
| Riwayat                 | ✅ SELESAI      |
| Theme                   | ✅ SELESAI      |
| Settings                | ✅ SELESAI      |
| Android Insets          | ✅ SELESAI      |
| Firestore               | ✅ SELESAI      |
| Testing                 | ✅ SELESAI      |
| Build Project           | ✅ SELESAI      |
| Release APK             | 🔄 TAHAP FINAL |
| Backup GitHub           | 🔄 TAHAP FINAL |

---

# 64. ROADMAP PENGEMBANGAN

### Tahap 1 — Core System

```text
Login
Firebase
Role
Navigation
Session
```

**Status: SELESAI**

### Tahap 2 — Staff

```text
Dashboard
Absensi
QR Scanner
Riwayat
Pengajuan
```

**Status: SELESAI**

### Tahap 3 — Pengajuan

```text
Pengajuan
Approval
Status
Notification
```

**Status: SELESAI**

### Tahap 4 — Admin

```text
Dashboard
Karyawan
Rekap
Approval
QR Settings
```

**Status: SELESAI**

### Tahap 5 — Communication

```text
Chat
Notification
Realtime
```

**Status: SELESAI**

### Tahap 6 — Settings

```text
Profile
Theme
Bantuan
Tentang
Logout
```

**Status: SELESAI**

### Tahap 7 — Finalization

```text
Testing
Bug Fix
Firestore Validation
QR Validation
Attendance Validation
UI/UX Final
Android Compatibility
Final QA
Build APK
GitHub Backup
```

**Status: DALAM TAHAP FINALISASI**

---

# 65. TAHAP FINALISASI

Setelah fitur utama selesai, tahap berikutnya adalah menyiapkan aplikasi untuk release.

Urutan pekerjaan:

```text
SOURCE CODE FINAL
        ↓
CLEAN PROJECT
        ↓
REBUILD PROJECT
        ↓
BUILD SUCCESSFUL
        ↓
GENERATE RELEASE APK
        ↓
INSTALL APK
        ↓
SMOKE TEST
        ↓
FINAL QA
        ↓
BACKUP PROJECT
        ↓
GIT COMMIT
        ↓
GIT PUSH
        ↓
PROJECT FINAL
```

---

# 66. SMOKE TEST RELEASE APK

Sebelum aplikasi dianggap final, APK release perlu diuji kembali.

Pengujian utama:

```text
1. Install APK
2. Login Admin
3. Login Staff
4. Scan QR
5. Absen Masuk
6. Absen Pulang
7. Absen Luar Kantor
8. Buat Pengajuan
9. Approval Admin
10. Chat
11. Notifikasi
12. Riwayat
13. Settings
14. Logout
15. Login kembali
```

Tujuan pengujian adalah memastikan aplikasi release memiliki perilaku yang sama dengan hasil pengembangan terakhir.

---

# 67. BACKUP PROJECT

Setelah release APK dinyatakan stabil, project perlu dibackup.

Repository:

```text
AbsensiKaryawan
```

GitHub:

```text
https://github.com/dink2v/AbsensiKaryawan.git
```

Backup meliputi:

```text
Source Code
Gradle Configuration
Firebase Configuration
Documentation
Release APK
```

---

# 68. PRINSIP PENGEMBANGAN

Pengembangan aplikasi menggunakan prinsip:

```text
JANGAN HAPUS FITUR YANG SUDAH BERJALAN

JANGAN MERUSAK FITUR LAMA

JANGAN MERUSAK NAVIGATION

JANGAN MENGUBAH DESAIN TANPA PERMINTAAN

JANGAN MENGGANTI STRUKTUR STABIL TANPA ALASAN

PERBAIKI BAGIAN YANG BERMASALAH SAJA

TAMBAHKAN FITUR SECARA BERTAHAP
```

Setiap perubahan mengikuti siklus:

```text
BUILD
 ↓
TEST
 ↓
FIX
 ↓
BUILD
 ↓
TEST
```

---

# 69. KESIMPULAN

Aplikasi **Absensi Karyawan** telah dikembangkan sebagai sistem absensi Android yang terintegrasi dengan Firebase.

Sistem telah mencakup dua role utama:

```text
ADMIN
STAFF
```

Fitur utama yang telah diselesaikan meliputi:

```text
Login
Authentication
Role Management
Session
QR Scanner
QR Validation
Absensi Masuk
Absensi Pulang
Absen Luar Kantor
Riwayat
Pengajuan
Approval
Data Karyawan
Rekap
Chat
Notifikasi
Settings
Theme
Firebase
Firestore
```

Sistem QR menggunakan daftar QR kantor yang terdaftar dan aktif sehingga scanner tidak menerima QR yang tidak sesuai.

Data utama aplikasi tersimpan pada Cloud Firestore, sedangkan DataStore digunakan sebagai penyimpanan lokal pendukung.

Sistem komunikasi antara Staff dan Admin juga telah tersedia melalui fitur Chat dan Notification.

Dari sisi teknis, project telah berhasil melewati proses build dengan konfigurasi Android terbaru yang digunakan dalam project dan telah mencapai tahap **finalisasi**.

Tahap terakhir yang perlu dilakukan adalah:

```text
FINAL BUILD
      ↓
RELEASE APK
      ↓
INSTALL & SMOKE TEST
      ↓
FINAL QA
      ↓
BACKUP GITHUB
```

Dengan selesainya tahap tersebut, aplikasi dapat dipersiapkan sebagai **versi release final Absensi Karyawan**.