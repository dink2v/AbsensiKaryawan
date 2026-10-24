# Absensi Karyawan WebApp

WebApp dibuat terpisah dari aplikasi Android yang berada di folder `app/`. UI awal mengikuti referensi APK: login, beranda, scan QR, pengajuan, riwayat, dan pengaturan.

## Menjalankan lokal

Persyaratan: Node.js 20 atau lebih baru.

```bash
cd web
npm install
npm run dev
```

Buka http://localhost:3000.

## GitHub Pages

WebApp dikonfigurasi untuk static export dan deployment melalui GitHub Actions pada branch `feature/webapp-foundation`.

- Workflow: `.github/workflows/deploy-pages.yml`
- Hasil build Next.js: `web/out`
- URL target setelah deployment berhasil: https://dink2v.github.io/AbsensiKaryawan/

Jika deployment belum berjalan, buka **Settings → Pages** pada repository dan pilih **Build and deployment → Source: GitHub Actions**. Periksa tab **Actions** untuk melihat status build/deploy.

## Status dan batasan saat ini

- UI masih prototipe awal; perlu dicocokkan lebih rinci dengan layar Android yang sudah jadi.
- Login masih simulasi UI, belum memakai Firebase Authentication.
- Data absensi dan pengajuan belum dibaca atau ditulis ke Firestore.
- Kamera QR belum diaktifkan.
- Jangan gunakan untuk presensi operasional sebelum autentikasi, validasi, aturan Firestore, dan alur data selesai diuji.

## Tahap integrasi selanjutnya

1. Cocokkan halaman dan alur WebApp dengan implementasi Android di folder `app/`.
2. Verifikasi konfigurasi Firebase yang digunakan aplikasi Android sebelum menyambungkan WebApp.
3. Audit skema dokumen Firestore dan aturan akses.
4. Implementasikan Firebase Authentication dan pembacaan/penulisan data sesuai skema yang sudah ada.
5. Implementasikan pemindaian QR browser serta validasi yang aman.
6. Uji role Staff/Admin dan semua alur sebelum digunakan.

**Prinsip:** perubahan WebApp dilakukan di folder `web/` dan workflow GitHub Pages. Kode Android di folder `app/` tidak diubah.
