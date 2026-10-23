import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Static export agar WebApp dapat di-host gratis di GitHub Pages.
  // Project Android di folder app/ tidak disentuh oleh konfigurasi ini.
  output: "export",
  basePath: "/AbsensiKaryawan",
  assetPrefix: "/AbsensiKaryawan/",
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
