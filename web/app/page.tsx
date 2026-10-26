"use client";

import { useEffect, useState } from "react";
import { onAuthStateChanged, signInWithEmailAndPassword, signOut, type User } from "firebase/auth";
import { addDoc, collection, onSnapshot, query, serverTimestamp, where } from "firebase/firestore";
import { getFirebaseClient } from "@/lib/firebase";
import QrScanner from "./qr-scanner";
import {
  ArrowRight, Bell, CalendarDays, Check, ChevronRight, Clock3, FilePlus2,
  FileText, History, House, LockKeyhole, Eye, EyeOff, LogOut, MapPin,
  ScanQrCode, Settings, UserRound, X, BriefcaseBusiness, Palette, CircleHelp,
  Info, RefreshCw
} from "lucide-react";

type Page = "home" | "requests" | "history" | "settings" | "scan";
type HistoryTab = "attendance" | "requests";

const green = "#123f32";

function Brand() {
  return <div className="brand">
    <div className="brand-mark"><FileText size={42} strokeWidth={2.8} /></div>
    <h1>Absensi Karyawan</h1>
    <p>Sistem Presensi Karyawan</p>
  </div>;
}

function Login({ onLogin }: { onLogin: (email: string, password: string) => Promise<void> }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit() {
    setError("");
    setBusy(true);
    try {
      await onLogin(email.trim(), password);
    } catch (e) {
      const code = (e as { code?: string })?.code;
      setError(code === "auth/invalid-credential" || code === "auth/wrong-password" || code === "auth/user-not-found"
        ? "Email atau password salah."
        : code === "auth/too-many-requests"
          ? "Terlalu banyak percobaan. Coba lagi nanti."
          : "Login gagal. Periksa koneksi dan konfigurasi Firebase.");
    } finally {
      setBusy(false);
    }
  }
  return <main className="login-page">
    <Brand />
    <section className="login-card">
      <h2>Selamat Datang <span aria-hidden="true">👋</span></h2>
      <p className="muted intro">Masuk menggunakan akun yang telah didaftarkan Admin.</p>
      <form onSubmit={(e) => { e.preventDefault(); void submit(); }}>
        <label className="field">
          <span>Email</span><UserRound size={23} />
          <input aria-label="Email" type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required />
        </label>
        <label className="field">
          <LockKeyhole size={23} />
          <input aria-label="Password" type={showPassword ? "text" : "password"} placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required />
          <button className="icon-button" type="button" aria-label="Tampilkan password" onClick={() => setShowPassword(!showPassword)}>{showPassword ? <EyeOff /> : <Eye />}</button>
        </label>
        <button className="primary-button login-submit" type="submit" disabled={busy}>{busy ? "Memeriksa akun..." : <><ArrowRight size={23} /> Masuk</>}</button>{error && <p role="alert" className="error-message">{error}</p>}
      </form>
      <button className="text-button forgot" type="button" onClick={() => setError("Fitur lupa sandi belum diaktifkan. Hubungi Admin / HRD untuk sementara.")}>Lupa Sandi?</button>
      <p className="account-note">Akun dibuat dan dikelola oleh Admin / HRD.</p>
    </section>
    <footer className="login-footer"><p>Belum memiliki akun?</p><strong>Hubungi Admin / HRD untuk mendapatkan akses.</strong><span>© 2026 Absensi Karyawan · WebApp</span></footer>
  </main>;
}

function AppShell({ user }: { user: User }) {
  const [page, setPage] = useState<Page>("home");
  const [historyTab, setHistoryTab] = useState<HistoryTab>("attendance");
  const [requestType, setRequestType] = useState("Izin");
  const [showRequestForm, setShowRequestForm] = useState(false);
  const [outsideOpen, setOutsideOpen] = useState(true);
  const [location, setLocation] = useState("");
  const [reason, setReason] = useState("");
  const [sent, setSent] = useState(false);
  const [attendanceBusy, setAttendanceBusy] = useState(false);
  const [attendanceMessage, setAttendanceMessage] = useState("");
  const [scannedQr, setScannedQr] = useState<{ qrData: string; officeName: string } | null>(null);
  const [qrNote, setQrNote] = useState("");

  const [todayAttendance, setTodayAttendance] = useState<{ jamMasuk: string; jamPulang: string } | null>(null);
  const [attendanceLoadError, setAttendanceLoadError] = useState(false);
  const [attendanceHistory, setAttendanceHistory] = useState<Array<{ id: string; tanggal: string; jamMasuk: string; jamPulang: string; status: string; kantor: string }>>([]);
  const [historyLoadError, setHistoryLoadError] = useState(false);

  useEffect(() => {
    try {
      const { db } = getFirebaseClient();
      const attendanceQuery = query(
        collection(db, "attendance"),
        where("uid", "==", user.uid)
      );
      return onSnapshot(attendanceQuery, (snapshot) => {
        const records = snapshot.docs.map((item) => {
          const data = item.data();
          return {
            id: item.id,
            tanggal: typeof data.tanggal === "string" ? data.tanggal : "",
            jamMasuk: typeof data.jamMasuk === "string" ? data.jamMasuk : "",
            jamPulang: typeof data.jamPulang === "string" ? data.jamPulang : "",
            status: typeof data.status === "string" ? data.status : "Hadir",
            kantor: typeof data.kantor === "string" ? data.kantor : (typeof data.lokasi === "string" ? data.lokasi : ""),
          };
        }).sort((a, b) => b.tanggal.localeCompare(a.tanggal));
        setAttendanceHistory(records);
        setHistoryLoadError(false);
      }, (error) => {
        console.error("Gagal membaca riwayat absensi", error);
        setHistoryLoadError(true);
      });
    } catch (error) {
      console.error("Firebase belum siap untuk membaca riwayat", error);
      setHistoryLoadError(true);
    }
  }, [user.uid]);

  useEffect(() => {
    try {
      const { db } = getFirebaseClient();
      const now = new Date();
      const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
      const attendanceQuery = query(
        collection(db, "attendance"),
        where("uid", "==", user.uid),
        where("tanggal", "==", today)
      );
      return onSnapshot(attendanceQuery, (snapshot) => {
        const records = snapshot.docs.map((item) => item.data());
        const record = records.find((item) => Boolean(item.jamMasuk)) ?? records[0];
        setTodayAttendance(record ? {
          jamMasuk: typeof record.jamMasuk === "string" ? record.jamMasuk : "",
          jamPulang: typeof record.jamPulang === "string" ? record.jamPulang : "",
        } : null);
        setAttendanceLoadError(false);
      }, (error) => {
        console.error("Gagal membaca absensi hari ini", error);
        setAttendanceLoadError(true);
      });
    } catch (error) {
      console.error("Firebase belum siap untuk membaca absensi", error);
      setAttendanceLoadError(true);
    }
  }, [user.uid]);

  const navItems: { id: Page; label: string; icon: typeof House }[] = [
    { id: "home", label: "Beranda", icon: House },
    { id: "requests", label: "Pengajuan", icon: FilePlus2 },
    { id: "scan", label: "Scan QR", icon: ScanQrCode },
    { id: "history", label: "Riwayat", icon: History },
    { id: "settings", label: "Setting", icon: Settings },
  ];

  return <div className="app-shell">
    <header className="topbar">
      <div><p className="greeting">Halo, Karyawan <span>👋</span></p><p className="muted">{new Intl.DateTimeFormat("id-ID", { weekday: "long", day: "numeric", month: "long", year: "numeric" }).format(new Date())}</p></div>
      <button className="round-button" aria-label="Notifikasi" onClick={() => alert("Notifikasi akan dihubungkan ke Firebase.")}><Bell /></button>
      <div className="avatar">K</div>
    </header>

    {page === "home" && <main className="page-content">
      <section className="clock-card">
        <div className="clock-head"><span><Clock3 size={18} /> Waktu Sekarang</span><b className={`status-pill ${attendanceLoadError ? "status-error" : todayAttendance ? "status-present" : "status-absent"}`}>{attendanceLoadError ? "GAGAL MEMUAT" : todayAttendance ? "SUDAH ABSEN" : "BELUM ABSEN"}</b></div>
        <div className="clock-time">{new Intl.DateTimeFormat("id-ID", { hour: "2-digit", minute: "2-digit", second: "2-digit", hour12: false }).format(new Date())}</div>
        <div className="clock-divider" /><div className="clock-foot"><span>Jam Masuk<strong>{todayAttendance?.jamMasuk || "-"}</strong></span><span>Jam Pulang<strong>{todayAttendance?.jamPulang || "-"}</strong></span></div>{attendanceLoadError && <p className="error-message">Status absensi gagal dimuat. Periksa aturan akses Firestore.</p>}
      </section>
      <h2 className="section-title">Absensi Hari Ini</h2>
      <button className="attendance-action dark-action" onClick={() => setPage("scan")}><span className="action-icon"><ScanQrCode /></span><span className="action-copy"><strong>Scan QR Absen</strong><small>Scan untuk absen masuk di kantor</small></span><ChevronRight /></button>
      <button className="attendance-action" onClick={() => { setPage("scan"); setOutsideOpen(true); }}><span className="action-icon pale"><MapPin /></span><span className="action-copy"><strong>Absen Luar Kantor</strong><small>Untuk tugas langsung ke klien</small></span><ChevronRight /></button>
      <h2 className="section-title quick-title">Aksi Cepat</h2>
      <div className="quick-grid">
        <button className="quick-card" onClick={() => setPage("history")}><span><History /></span><strong>Riwayat</strong><small>Absensi</small></button>
        <button className="quick-card" onClick={() => setPage("requests")}><span><FileText /></span><strong>Pengajuan</strong><small>Izin / Sakit</small></button>
        <button className="quick-card" onClick={() => setPage("settings")}><span><UserRound /></span><strong>Profil</strong><small>Data Saya</small></button>
      </div>
    </main>}

    {page === "scan" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Scan Kehadiran</h1><p className="muted">Arahkan kamera ke QR Code</p></div></div>
      <div className="info-banner"><Info size={20} /> Pastikan pencahayaan cukup dan QR terlihat jelas di dalam kotak.</div>
      <QrScanner onValidQr={(qrData, officeName) => setScannedQr({ qrData, officeName })} />
      {scannedQr && <div className="verified-qr"><Check size={18} /><span><strong>QR terverifikasi</strong><small>{scannedQr.officeName}</small></span></div>}
      <label className="input-block">Catatan (opsional)<input value={qrNote} onChange={e => setQrNote(e.target.value)} placeholder="Tambahkan catatan jika diperlukan" /></label>
      <button className="primary-button wide" disabled={!scannedQr || attendanceBusy} onClick={async () => {
        setAttendanceMessage("");
        setAttendanceBusy(true);
        try {
          if (!scannedQr) {
            setAttendanceMessage("Scan QR yang valid terlebih dahulu.");
            return;
          }
          const { db } = getFirebaseClient();
          const now = new Date();
          const tanggal = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
          const jamMasuk = `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}:${String(now.getSeconds()).padStart(2, "0")}`;
          if (attendanceHistory.some(record => record.tanggal === tanggal && record.jamMasuk)) {
            setAttendanceMessage("Presensi masuk hari ini sudah tercatat.");
            return;
          }
          await addDoc(collection(db, "attendance"), {
            uid: user.uid,
            nama: user.displayName || user.email || "Karyawan",
            tanggal,
            jamMasuk,
            jamPulang: "",
            status: "Hadir",
            qrData: scannedQr.qrData,
            kantor: scannedQr.officeName,
            catatan: qrNote.trim(),
            createdAt: serverTimestamp(),
          });
          setAttendanceMessage("Presensi masuk berhasil disimpan.");
          setScannedQr(null);
          setQrNote("");
        } catch (e) {
          console.error("Gagal menyimpan presensi QR", e);
          const code = (e as { code?: string })?.code;
          setAttendanceMessage(code === "permission-denied"
            ? "Presensi ditolak oleh aturan keamanan Firestore. Hubungi Admin."
            : "Presensi belum tersimpan. Periksa koneksi lalu coba lagi.");
        } finally {
          setAttendanceBusy(false);
        }
      }}>{attendanceBusy ? "Menyimpan..." : <><Check /> Konfirmasi Absen</>}</button>
      {attendanceMessage && <p role="status" aria-live="polite" className={attendanceMessage.includes("berhasil disimpan") ? "success-message" : "error-message"}>{attendanceMessage}</p>}
      <button className="accordion" onClick={() => setOutsideOpen(!outsideOpen)}><BriefcaseBusiness /> Absen di Luar Kantor <ChevronRight className={outsideOpen ? "rotate" : ""} /></button>
      {outsideOpen && <section className="form-card"><h2><BriefcaseBusiness /> Absen dari lokasi tugas</h2><p>Gunakan jika kamu langsung menuju lokasi klien atau tempat tugas tanpa datang ke kantor terlebih dahulu.</p><label className="input-block">Lokasi<input value={location} onChange={e => setLocation(e.target.value)} placeholder="Contoh: SMK Negeri 1 Blitar" /></label><label className="input-block">Alasan / Keperluan<textarea value={reason} onChange={e => setReason(e.target.value)} placeholder="Contoh: Bertemu klien untuk keperluan pekerjaan" rows={3} /></label><button className="primary-button wide" disabled={!location.trim() || !reason.trim() || attendanceBusy} onClick={async () => {
        setAttendanceMessage("");
        setAttendanceBusy(true);
        try {
          const { db } = getFirebaseClient();
          const now = new Date();
          const tanggal = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
          const jamMasuk = `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}:${String(now.getSeconds()).padStart(2, "0")}`;
          await addDoc(collection(db, "attendance"), {
            uid: user.uid,
            nama: user.displayName || user.email || "Karyawan",
            tanggal,
            jamMasuk,
            jamPulang: "",
            kantor: location.trim(),
            status: "Hadir",
            qrData: "",
            catatan: reason.trim(),
            jenis: "lokasi_tugas",
            createdAt: serverTimestamp(),
          });
          setAttendanceMessage("Presensi lokasi tugas berhasil disimpan.");
          setLocation("");
          setReason("");
        } catch (e) {
          console.error("Gagal menyimpan presensi lokasi tugas", e);
          const code = (e as { code?: string })?.code;
          setAttendanceMessage(code === "permission-denied"
            ? "Presensi belum tersimpan. Aturan keamanan Firestore belum mengizinkan akses ini."
            : "Presensi belum tersimpan. Periksa konfigurasi Firebase dan aturan keamanan, lalu coba lagi.");
        } finally {
          setAttendanceBusy(false);
        }
      }}>{attendanceBusy ? "Menyimpan..." : "➤ Kirim Absen"}</button>{attendanceMessage && <p role="status" aria-live="polite" className={attendanceMessage.includes("berhasil disimpan") ? "success-message" : "error-message"}>{attendanceMessage}</p>}</section>}
    </main>}

    {page === "requests" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Pengajuan</h1><p className="muted">Pengajuan yang kamu buat</p></div><button className="icon-button" onClick={() => setSent(false)} aria-label="Muat ulang"><RefreshCw /></button></div>
      <button className="new-request" onClick={() => setShowRequestForm(!showRequestForm)}><FilePlus2 size={30} /><span><strong>{showRequestForm ? "Tutup Form Pengajuan" : "Buat Pengajuan Baru"}</strong><small>Ajukan izin, cuti, atau keperluan lainnya</small></span></button>
      {showRequestForm && <section className="form-card"><h2>Pengajuan Baru</h2><label className="input-block">Jenis Pengajuan<select value={requestType} onChange={e => setRequestType(e.target.value)}><option>Izin</option><option>Sakit</option><option>Cuti</option><option>Lainnya</option></select></label><label className="input-block">Tanggal Mulai<input type="date" /></label><label className="input-block">Tanggal Selesai<input type="date" /></label><label className="input-block">Keterangan<textarea rows={3} placeholder="Jelaskan keperluan pengajuan" /></label><button className="primary-button wide" onClick={() => { setSent(true); setShowRequestForm(false); }}>Kirim Pengajuan</button></section>}
      <div className="segmented"><button className="selected">Pengajuan Saya</button><button onClick={() => alert("Daftar approval mengikuti hak akses akun.")}>Menunggu Approval</button></div>
      <div className="stats-grid"><div className="stat stat-wait"><span>•••</span><strong>{sent ? "1" : "0"}</strong><small>Menunggu</small></div><div className="stat stat-approved"><Check /><strong>0</strong><small>Disetujui</small></div><div className="stat stat-rejected"><X /><strong>0</strong><small>Ditolak</small></div></div>
      <div className="filter-pills"><button className="selected">Semua</button><button>Menunggu</button><button>Disetujui</button><button>Ditolak</button></div>
      <div className="empty-state"><FileText size={42} /><strong>{sent ? "Pengajuan tersimpan sebagai contoh" : "Belum ada pengajuan"}</strong><p>Pengajuan yang kamu buat akan muncul di sini.</p></div>
    </main>}

    {page === "history" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Riwayat</h1><p className="muted">Riwayat dan status pengajuan</p></div><button className="icon-button" aria-label="Muat ulang"><RefreshCw /></button></div>
      <div className="segmented large"><button className={historyTab === "attendance" ? "selected" : ""} onClick={() => setHistoryTab("attendance")}>Absensi</button><button className={historyTab === "requests" ? "selected" : ""} onClick={() => setHistoryTab("requests")}>Pengajuan</button></div>
      <div className="filter-pills">{(historyTab === "attendance" ? ["7 Hari", "1 Bulan", "Semua"] : ["Semua", "Menunggu", "Disetujui", "Ditolak"]).map((x, i) => <button className={i === 0 ? "selected" : ""} key={x}>{x}</button>)}</div>
      {historyTab === "attendance" ? (
        historyLoadError ? <div className="empty-state history-empty"><Info size={42} /><strong>Riwayat belum bisa dimuat</strong><p>Periksa koneksi dan aturan akses Firestore, lalu coba lagi.</p></div> :
        attendanceHistory.length === 0 ? <div className="empty-state history-empty"><CalendarDays size={42} /><strong>Belum ada riwayat absensi</strong><p>Data kehadiran akan muncul di sini setelah presensi tersimpan.</p></div> :
        <div className="history-list">{attendanceHistory.map((record) => <article className="history-record" key={record.id}><div className="history-record-date"><CalendarDays size={19} /><strong>{record.tanggal || "Tanggal tidak tersedia"}</strong><span className="status-pill status-present">{record.status}</span></div><div className="history-record-times"><span>Jam Masuk<strong>{record.jamMasuk || "-"}</strong></span><span>Jam Pulang<strong>{record.jamPulang || "-"}</strong></span></div>{record.kantor && <p className="muted history-location"><MapPin size={15} /> {record.kantor}</p>}</article>)}</div>
      ) : <div className="empty-state history-empty"><FileText size={42} /><strong>Riwayat pengajuan belum terhubung</strong><p>Data pengajuan akan ditampilkan setelah integrasi koleksi pengajuan selesai.</p></div>}
    </main>}

    {page === "settings" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Pengaturan</h1><p className="muted">Atur preferensi dan informasi aplikasi</p></div></div>
      <h2 className="section-title">Pengaturan Aplikasi</h2>
      <section className="settings-card">
        <button className="setting-row"><span><Bell /></span><div><strong>Notifikasi</strong><small>Kelola pemberitahuan aplikasi</small></div><ChevronRight /></button>
        <button className="setting-row"><span><Palette /></span><div><strong>Tampilan</strong><small>Atur tema dan tampilan aplikasi</small></div><ChevronRight /></button>
        <button className="setting-row"><span><CircleHelp /></span><div><strong>Bantuan</strong><small>Panduan penggunaan aplikasi</small></div><ChevronRight /></button>
        <button className="setting-row"><span><Info /></span><div><strong>Tentang Aplikasi</strong><small>Informasi aplikasi dan versi</small></div><ChevronRight /></button>
      </section>
      <h2 className="section-title account-title">Akun</h2>
      <button className="setting-row logout" onClick={() => window.location.reload()}><span><LogOut /></span><div><strong>Keluar</strong><small>Keluar dari akun aplikasi</small></div><ChevronRight /></button>
    </main>}

    <nav className="bottom-nav" aria-label="Navigasi utama">
      {navItems.map(({ id, label, icon: Icon }) => <button key={id} className={page === id || (id === "scan" && page === "scan") ? "nav-item active" : "nav-item"} onClick={() => setPage(id)}><span className={id === "scan" ? "nav-scan-icon" : "nav-icon"}><Icon /></span><span>{label}</span></button>)}
    </nav>
  </div>;
}

export default function Home() {
  const [user, setUser] = useState<User | null>(null);
  const [authReady, setAuthReady] = useState(false);
  const [authError, setAuthError] = useState("");
  useEffect(() => {
    try {
      const { auth } = getFirebaseClient();
      return onAuthStateChanged(auth, (currentUser) => {
        setUser(currentUser);
        setAuthReady(true);
        setAuthError("");
      }, (error) => {
        setAuthError(error.message || "Tidak dapat memeriksa sesi Firebase.");
        setAuthReady(true);
      });
    } catch (e) {
      setAuthError((e as Error).message || "Konfigurasi Firebase belum tersedia.");
      setAuthReady(true);
    }
  }, []);
  async function login(email: string, password: string) {
    const { auth } = getFirebaseClient();
    await signInWithEmailAndPassword(auth, email, password);
  }
  if (!authReady) return <main className="login-page"><Brand /><p className="muted">Memeriksa sesi akun...</p></main>;
  if (authError && !user) return <main className="login-page"><Brand /><section className="login-card"><p role="alert" className="error-message">{authError}</p><p className="muted">Pastikan variabel Firebase Web sudah diatur pada GitHub Actions Variables.</p></section></main>;
  return user ? <AppShell user={user} /> : <Login onLogin={login} />;
}