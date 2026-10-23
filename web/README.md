# Absensi Karyawan WebApp

Fondasi awal WebApp karyawan yang terpisah dari proyek Android di folder `app/`. UI awal mengikuti screenshot APK yang diberikan: login, beranda, scan QR, pengajuan, riwayat, dan pengaturan.

## Menjalankan lokal

Persyaratan: Node.js 20.9 atau lebih baru.

```bash
cd web
npm install
npm run dev
```

Buka http://localhost:3000.

## Status saat ini

- UI prototipe untuk halaman login dan halaman utama telah dibuat.
- Login saat ini hanya simulasi UI; belum memakai Firebase Authentication.
- Data absensi dan pengajuan belum dibaca atau ditulis ke Firestore.
- Kamera QR belum diaktifkan.
- Jangan gunakan untuk presensi operasional sebelum autentikasi, validasi, aturan Firestore, dan alur data selesai diuji.

## Langkah integrasi berikutnya

1. Periksa konfigurasi Firebase Web untuk proyek yang sama dengan APK.
2. Audit struktur dokumen Firestore dan Firebase Authentication yang dipakai APK.
3. Implementasikan login dan pemulihan sandi dengan Firebase Authentication.
4. Sambungkan pembacaan/penulisan presensi dan pengajuan sesuai skema yang sudah ada.
5. Implementasikan pemindaian QR browser dan validasi sisi server/Firestore yang aman.
6. Uji peran karyawan, atasan, dan admin tanpa melemahkan Firestore Security Rules.

Folder Android `app/` tidak diubah oleh fondasi WebApp ini.