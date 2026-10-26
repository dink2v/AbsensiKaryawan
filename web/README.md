# Absensi Karyawan WebApp

WebApp dibuat terpisah dari aplikasi Android yang berada di folder `app/`. UI awal mengikuti referensi aplikasi: login, beranda, scan QR, pengajuan, riwayat, dan pengaturan.

## Menjalankan lokal

Persyaratan: Node.js 20 atau lebih baru.

```bash
cd web
npm install
npm run dev
```

Buka http://localhost:3000.

## GitHub Pages

WebApp dikonfigurasi untuk static export dan deployment melalui GitHub Actions.

- Workflow: `.github/workflows/deploy-pages.yml`
- Hasil build Next.js: `web/out`
- URL target setelah deployment berhasil: https://dink2v.github.io/AbsensiKaryawan/

Workflow saat ini berjalan dari branch `feature/webapp-foundation`. Pekerjaan integrasi dilakukan di branch terpisah `feature/webapp-integration` dan belum dipublikasikan ke situs live.

Agar deployment berjalan, buka **Settings → Pages** pada repository dan pilih **Build and deployment → Source: GitHub Actions**. Pastikan variabel Firebase Web yang dibutuhkan workflow tersedia pada **Settings → Secrets and variables → Actions → Variables**.

## Status implementasi

- Login: menggunakan Firebase Authentication (email/password).
- Beranda: membaca data absensi hari ini dari koleksi `attendance` berdasarkan UID akun dan tanggal.
- Riwayat absensi: kini membaca koleksi `attendance` secara langsung dan menampilkan tanggal, jam masuk/pulang, status, serta lokasi bila tersedia.
- Presensi QR: kamera browser dan validasi terhadap `qr_settings` belum diaktifkan.
- Pengajuan: formulir dan daftar pengajuan masih perlu dihubungkan ke skema koleksi `pengajuan` yang dipakai Android.
- Setting/profil dan notifikasi: sebagian interaksi masih berupa placeholder.

**Jangan gunakan untuk presensi operasional sebelum kamera QR, validasi data, aturan Firestore, dan alur pengajuan selesai diuji.**

## Prinsip integrasi

1. Perubahan WebApp hanya dilakukan di folder `web/` dan konfigurasi workflow yang relevan.
2. Jangan mengubah kode Android di folder `app/` untuk pekerjaan WebApp.
3. Gunakan struktur Firestore yang sudah dipakai Android, termasuk koleksi `attendance`, `qr_settings`, `pengajuan`, dan `users`.
4. Jangan menganggap UI sukses sebagai bukti data tersimpan; tampilkan status error jika Firestore menolak akses.
5. Uji build dan alur dengan akun uji sebelum menggabungkan branch atau melakukan deployment.
