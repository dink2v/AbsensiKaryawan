"use client";

import { useEffect, useRef, useState } from "react";
import { collection, getDocs } from "firebase/firestore";
import { Html5Qrcode } from "html5-qrcode";
import { getFirebaseClient } from "@/lib/firebase";
import { Camera, RefreshCw, ScanQrCode } from "lucide-react";

type Props = {
  onValidQr: (qrData: string, officeName: string) => void;
};

type RegisteredQr = { qrData: string; officeName: string };

function normalize(value: string) {
  return value.trim().replace(/\/$/, "").toLowerCase();
}

export default function QrScanner({ onValidQr }: Props) {
  const [status, setStatus] = useState("Memeriksa QR kantor aktif...");
  const [error, setError] = useState("");
  const [ready, setReady] = useState(false);
  const [matched, setMatched] = useState(false);
  const scannerRef = useRef<Html5Qrcode | null>(null);
  const resultHandlerRef = useRef(onValidQr);
  resultHandlerRef.current = onValidQr;

  useEffect(() => {
    let cancelled = false;
    let started = false;
    let scanner: Html5Qrcode | null = null;

    async function start() {
      try {
        const { db } = getFirebaseClient();
        const snapshot = await getDocs(collection(db, "qr_settings"));
        const registered: RegisteredQr[] = snapshot.docs.flatMap((doc) => {
          const data = doc.data();
          const qrData = typeof data.qrData === "string" ? data.qrData.trim() : "";
          if (data.aktif !== true || !qrData) return [];
          return [{
            qrData,
            officeName: typeof data.officeName === "string" && data.officeName.trim()
              ? data.officeName.trim()
              : "Kantor",
          }];
        });

        if (cancelled) return;
        if (registered.length === 0) {
          setError("Belum ada QR kantor aktif. Hubungi Admin untuk mengaktifkan QR.");
          setStatus("QR kantor belum tersedia");
          return;
        }

        setReady(true);
        setStatus("Izinkan akses kamera, lalu arahkan ke QR kantor.");
        scanner = new Html5Qrcode("attendance-qr-reader");
        scannerRef.current = scanner;
        await scanner.start(
          { facingMode: "environment" },
          { fps: 10, qrbox: { width: 230, height: 230 }, aspectRatio: 1 },
          (decodedText) => {
            if (matched || cancelled) return;
            const found = registered.find((item) => normalize(item.qrData) === normalize(decodedText));
            if (!found) {
              setError("QR tidak cocok dengan QR kantor aktif. Pastikan kamu memindai QR resmi.");
              setStatus("QR tidak valid");
              return;
            }
            setMatched(true);
            setError("");
            setStatus(`QR valid — ${found.officeName}`);
            resultHandlerRef.current(found.qrData, found.officeName);
            void scanner?.stop().catch(() => undefined);
          },
          () => undefined
        );
        started = true;
      } catch (e) {
        if (!cancelled) {
          console.error("Gagal menjalankan pemindai QR", e);
          setError("Kamera tidak dapat dibuka. Izinkan akses kamera dan buka situs melalui HTTPS.");
          setStatus("Kamera tidak tersedia");
        }
      }
    }

    void start();
    return () => {
      cancelled = true;
      const current = scanner ?? scannerRef.current;
      if (current) {
        const cleanup = async () => {
          try {
            if (started || current.isScanning) await current.stop();
          } catch {}
          try { await current.clear(); } catch {}
        };
        void cleanup();
      }
      scannerRef.current = null;
    };
  }, []);

  return (
    <section className="scanner-card">
      <div className="scanner-status"><ScanQrCode size={19} /><strong>{status}</strong></div>
      <div id="attendance-qr-reader" className="qr-reader" aria-label="Kamera pemindai QR" />
      {!ready && !error && <p className="muted scanner-help"><RefreshCw size={15} /> Menghubungkan kamera...</p>}
      {error && <p className="error-message" role="alert">{error}</p>}
      {matched && <p className="success-message"><Camera size={16} /> QR kantor berhasil diverifikasi.</p>}
      {!matched && ready && <p className="muted scanner-help">Gunakan QR kantor yang aktif dan pastikan kamera memiliki izin.</p>}
    </section>
  );
}
