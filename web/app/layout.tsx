import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Absensi Karyawan",
  description: "Sistem Presensi Karyawan",
  applicationName: "Absensi Karyawan",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="id">
      <body>{children}</body>
    </html>
  );
}