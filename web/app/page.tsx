"use client";

import { useEffect, useState } from "react";
import { onAuthStateChanged, signInWithEmailAndPassword, signOut, type User } from "firebase/auth";
import { addDoc, collection, doc, getDoc, getDocs, onSnapshot, orderBy, query, runTransaction, serverTimestamp, setDoc, updateDoc, where } from "firebase/firestore";
import { getFirebaseClient } from "@/lib/firebase";
import {
  ArrowRight, Bell, CalendarDays, Check, ChevronRight, Clock3, FilePlus2,
  FileText, History, House, LockKeyhole, Eye, EyeOff, LogOut, MapPin,
  Settings, UserRound, X, BriefcaseBusiness, Palette, CircleHelp,
  Info, RefreshCw, MessageCircle, Send, Headset, ScanQrCode
} from "lucide-react";

type Page = "home" | "requests" | "history" | "settings" | "scan" | "profile" | "chat";
type HistoryTab = "attendance" | "requests";
type SettingsSection = "main" | "notifications" | "appearance" | "help" | "about";
type ThemeMode = "terang" | "gelap" | "sistem";
type RegisteredQr = { id: string; qrData: string; officeName: string; description: string };
type ChatMessageData = { id: string; senderId: string; senderName: string; senderType: string; message: string; timestamp: number };
type EmployeeNotification = {
  id: string;
  userId: string;
  type: string;
  title: string;
  message: string;
  read: boolean;
  relatedId: string;
  createdAt: Date | null;
};

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
  const [settingsSection, setSettingsSection] = useState<SettingsSection>("main");
  const [themeMode, setThemeMode] = useState<ThemeMode>("sistem");
  const [requestType, setRequestType] = useState("");
  const [requestTime, setRequestTime] = useState("");
  const [requestExitTime, setRequestExitTime] = useState("");
  const [requestReturnTime, setRequestReturnTime] = useState("");
  const [showRequestForm, setShowRequestForm] = useState(false);
  const [requestStart, setRequestStart] = useState("");
  const [requestEnd, setRequestEnd] = useState("");
  const [requestReason, setRequestReason] = useState("");
  const [requestBusy, setRequestBusy] = useState(false);
  const [requestMessage, setRequestMessage] = useState("");
  const [requestError, setRequestError] = useState("");
  const [employeeRequests, setEmployeeRequests] = useState<Array<{id:string; jenis:string; tanggal:string; jamPulang:string; jamKeluar:string; jamKembali:string; tanggalMulai:string; tanggalSelesai:string; keterangan:string; status:string; createdAt:Date|null}>>([]);
  const [requestLoadError, setRequestLoadError] = useState(false);
  const [requestStatusFilter, setRequestStatusFilter] = useState("Semua");
  const [historyRequestFilter, setHistoryRequestFilter] = useState("Semua");
  const [isApprover, setIsApprover] = useState(false);
  const [approverTab, setApproverTab] = useState<"approval" | "mine">("approval");
  const [approvalRequests, setApprovalRequests] = useState<Array<{id:string; uid:string; currentApproverUid:string; currentApproverName:string; currentApproverJabatan:string; approvalStep:number; approvalTotal:number; canApprove:boolean; nama:string; jenis:string; tanggal:string; jamPulang:string; jamKeluar:string; jamKembali:string; tanggalMulai:string; tanggalSelesai:string; keterangan:string; status:string; createdAt:Date|null}>>([]);
  const [approvalBusyId, setApprovalBusyId] = useState("");
  const [approvalMessage, setApprovalMessage] = useState("");
  const [approvalError, setApprovalError] = useState("");
  useEffect(() => {
    let active = true;
    async function loadRole() {
      try {
        const { db } = getFirebaseClient();
        // Some existing user records are keyed by a legacy document ID and store
        // the Firebase Authentication UID in their "uid" field instead.
        let profileSnapshot = await getDoc(doc(db, "users", user.uid));
        if (!profileSnapshot.exists()) {
          const matches = await getDocs(query(collection(db, "users"), where("uid", "==", user.uid)));
          profileSnapshot = matches.docs[0] ?? profileSnapshot;
        }
        const profile = profileSnapshot.data() ?? {};
        const role = String(profile.role ?? profile.peran ?? profile.jabatan ?? "").trim().toLowerCase();
        if (active) setIsApprover(profile.isAdmin === true || ["admin", "owner", "manager", "hrd", "superadmin", "atasan", "supervisor"].includes(role));
      } catch {
        if (active) setIsApprover(false);
      }
    }
    void loadRole();
    return () => { active = false; };
  }, [user.uid]);

  useEffect(() => {
    if (!isApprover || page !== "requests") return;
    const { db } = getFirebaseClient();
    setApprovalError("");
    // Listen to the same Firestore requests as Android, but only show requests
    // assigned to this approver in the APK approval chain.
    // Android records use waktuPengajuan; do not orderBy createdAt because
    // Firestore would hide Android documents that do not contain that field.
    const requestsQuery = query(collection(db, "pengajuan"));
    return onSnapshot(requestsQuery, snapshot => {
      const visible = snapshot.docs.filter(item => {
        const data = item.data();
        const viewers = Array.isArray(data.approvalViewers) ? data.approvalViewers.map(String) : [];
        const chain = Array.isArray(data.approvalChain) ? data.approvalChain.map(String) : [];
        const statuses = data.approvalStatuses && typeof data.approvalStatuses === "object" ? data.approvalStatuses as Record<string, string> : {};
        const currentUid = String(data.currentApproverUid ?? "");
        const pending = String(data.status ?? "").toLowerCase() === "menunggu";
        // Show every pending request assigned to this approver in the chain.
        // Only the active approver can take action; upcoming approvers can still see
        // the request and its current stage instead of an apparently empty inbox.
        return pending && data.approvalLocked !== true &&
          (currentUid === user.uid || chain.includes(user.uid) || viewers.includes(user.uid));
      });
      setApprovalRequests(visible.map(item => {
        const data = item.data();
        const chain = Array.isArray(data.approvalChain) ? data.approvalChain.map(String) : [];
        const rawCreated = data.createdAt ?? data.waktuPengajuan;
        const created = rawCreated?.toDate?.() ?? null;
        return { id: item.id, uid: String(data.uid ?? data.userId ?? data.pemohonUid ?? ""), currentApproverUid: String(data.currentApproverUid ?? ""), currentApproverName: String(data.currentApproverName ?? ""), currentApproverJabatan: String(data.currentApproverJabatan ?? ""), approvalStep: Math.max(0, chain.indexOf(String(data.currentApproverUid ?? "")) + 1), approvalTotal: chain.length, canApprove: chain.includes(user.uid), nama: String(data.nama ?? data.name ?? data.pemohonNama ?? "Karyawan"), jenis: String(data.jenis ?? data.type ?? data.jenisPengajuan ?? "Pengajuan"), tanggal: String(data.tanggal ?? ""), jamPulang: String(data.jamPulang ?? ""), jamKeluar: String(data.jamKeluar ?? ""), jamKembali: String(data.jamKembali ?? ""), tanggalMulai: String(data.tanggalMulai ?? data.tanggal ?? ""), tanggalSelesai: String(data.tanggalSelesai ?? data.tanggalMulai ?? data.tanggal ?? ""), keterangan: String(data.keterangan ?? data.alasan ?? data.alasanPengajuan ?? ""), status: String(data.status ?? "menunggu"), createdAt: created };
      }).sort((a, b) => (b.createdAt?.getTime() ?? 0) - (a.createdAt?.getTime() ?? 0)));
    }, () => setApprovalError("Pengajuan gagal dimuat. Periksa koneksi dan aturan akses Firestore."));
  }, [isApprover, page, user.uid]);

  async function decideRequest(item: {id:string; nama:string}, status: "Disetujui" | "Ditolak") {
    if (!isApprover || approvalBusyId) return;
    setApprovalBusyId(item.id);
    setApprovalError("");
    setApprovalMessage("");
    try {
      const { db } = getFirebaseClient();
      const requestRef = doc(db, "pengajuan", item.id);
      const outcome = await runTransaction(db, async transaction => {
        const snapshot = await transaction.get(requestRef);
        if (!snapshot.exists()) throw new Error("Pengajuan tidak ditemukan.");
        const data = snapshot.data();
        const chain = Array.isArray(data.approvalChain) ? data.approvalChain.map(String) : [];
        const statuses = data.approvalStatuses && typeof data.approvalStatuses === "object"
          ? { ...(data.approvalStatuses as Record<string, string>) }
          : {};
        const viewers = Array.isArray(data.approvalViewers) ? data.approvalViewers.map(String) : [];
        const currentUid = String(data.currentApproverUid ?? "");
        const pending = String(data.status ?? "").toLowerCase() === "menunggu";
        if (!pending || data.approvalLocked === true) throw new Error("Pengajuan sudah tidak menunggu persetujuan.");
        if (!chain.includes(user.uid)) {
          throw new Error("Akun ini tidak terdaftar sebagai atasan yang berwenang menyetujui pengajuan tersebut.");
        }

        statuses[user.uid] = status === "Disetujui" ? "disetujui" : "ditolak";
        if (status === "Ditolak") {
          transaction.update(requestRef, {
            status: "ditolak",
            approvalStatuses: statuses,
            approvalLocked: true,
            currentApproverUid: "",
            currentApproverName: "",
            currentApproverJabatan: "",
            waktuKeputusan: serverTimestamp(),
            diprosesOleh: user.uid,
            diprosesPada: serverTimestamp(),
          });
          return { outcome: "rejected" as const, requesterUid: String(data.uid ?? ""), jenis: String(data.jenis ?? "Pengajuan"), nextUid: "" };
        }

        transaction.update(requestRef, {
          status: "disetujui",
          approvalStatuses: statuses,
          approvalLocked: true,
          currentApproverUid: "",
          currentApproverName: "",
          currentApproverJabatan: "",
          waktuKeputusan: serverTimestamp(),
          diprosesOleh: user.uid,
          diprosesPada: serverTimestamp(),
        });
        return { outcome: "approved" as const, requesterUid: String(data.uid ?? ""), jenis: String(data.jenis ?? "Pengajuan") };
      });
      // Match the Android notifications collection/schema after the atomic status update.
      if (outcome.outcome === "rejected") {
        if (outcome.requesterUid) await addDoc(collection(db, "notifications"), {
          userId: outcome.requesterUid, type: "PENGAJUAN_DITOLAK", title: "Pengajuan Ditolak",
          message: `Pengajuan ${outcome.jenis} Anda ditolak pada tahap persetujuan.`,
          timestamp: Date.now(), isRead: false, read: false, relatedId: item.id,
        });
      } else if (outcome.outcome === "approved") {
        if (outcome.requesterUid) await addDoc(collection(db, "notifications"), {
          userId: outcome.requesterUid, type: "PENGAJUAN_DISETUJUI", title: "Pengajuan Disetujui",
          message: `Pengajuan ${outcome.jenis} Anda telah disetujui oleh atasan yang berwenang.`,
          timestamp: Date.now(), isRead: false, read: false, relatedId: item.id,
        });
next approver branch
      setApprovalMessage(status === "Ditolak"
        ? `Pengajuan ${item.nama} berhasil ditolak.`
        : `Pengajuan ${item.nama} berhasil disetujui. Tidak perlu menunggu atasan lain.`);
    } catch (error) {
      console.error("Gagal memproses persetujuan", error);
      setApprovalError(error instanceof Error && error.message
        ? error.message
        : "Status gagal diperbarui. Periksa izin akses Firestore.");
    } finally {
      setApprovalBusyId("");
    }
  }

  const [outsideOpen, setOutsideOpen] = useState(true);
  const [location, setLocation] = useState("");
  const [reason, setReason] = useState("");
  const [attendanceBusy, setAttendanceBusy] = useState(false);
  const [attendanceMessage, setAttendanceMessage] = useState("");
  const [scannedQr, setScannedQr] = useState<{ qrData: string; officeName: string } | null>(null);
  const [registeredQrs, setRegisteredQrs] = useState<RegisteredQr[]>([]);
  const [qrListLoading, setQrListLoading] = useState(false);
  const [qrListError, setQrListError] = useState("");
  const [qrNote, setQrNote] = useState("");

  const [todayAttendance, setTodayAttendance] = useState<{ jamMasuk: string; jamPulang: string } | null>(null);
  const [attendanceLoadError, setAttendanceLoadError] = useState(false);
  const [attendanceHistory, setAttendanceHistory] = useState<Array<{ id: string; tanggal: string; jamMasuk: string; jamPulang: string; status: string; kantor: string }>>([]);
  const [historyLoadError, setHistoryLoadError] = useState(false);
  const [employeeProfile, setEmployeeProfile] = useState<Record<string, unknown> | null>(null);
  const [profileLoadError, setProfileLoadError] = useState(false);
  const [notifications, setNotifications] = useState<EmployeeNotification[]>([]);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [notificationLoadError, setNotificationLoadError] = useState(false);
  const [notificationBusy, setNotificationBusy] = useState<string | null>(null);
  const [chatMessages, setChatMessages] = useState<ChatMessageData[]>([]);
  const [chatDraft, setChatDraft] = useState("");
  const [chatLoading, setChatLoading] = useState(false);
  const [chatSending, setChatSending] = useState(false);
  const [chatError, setChatError] = useState("");
  const unreadNotifications = notifications.filter((item) => !item.read).length;

  useEffect(() => {
    let active = true;
    async function loadProfile() {
      try {
        const { db } = getFirebaseClient();
        const snapshot = await getDoc(doc(db, "users", user.uid));
        if (!active) return;
        setEmployeeProfile(snapshot.exists() ? snapshot.data() : {});
        setProfileLoadError(false);
      } catch (error) {
        console.error("Gagal memuat profil karyawan", error);
        if (active) setProfileLoadError(true);
      }
    }
    void loadProfile();
    return () => { active = false; };
  }, [user.uid]);

  useEffect(() => {
    try {
      const { db } = getFirebaseClient();
      // Struktur mengikuti NotificationRepository Android:
      // userId, type, title, message, timestamp (epoch milliseconds),
      // read, dan relatedId.
      const notificationQuery = query(
        collection(db, "notifications"),
        where("userId", "==", user.uid)
      );
      return onSnapshot(notificationQuery, (snapshot) => {
        const records = snapshot.docs.map((item) => {
          const data = item.data();
          const rawTimestamp = data.timestamp ?? data.createdAt;
          let timestamp: Date | null = null;
          if (typeof rawTimestamp === "number" && Number.isFinite(rawTimestamp)) {
            timestamp = new Date(rawTimestamp);
          } else if (rawTimestamp && typeof rawTimestamp.toDate === "function") {
            timestamp = rawTimestamp.toDate() as Date;
          } else if (typeof rawTimestamp === "string") {
            const parsed = new Date(rawTimestamp);
            if (!Number.isNaN(parsed.getTime())) timestamp = parsed;
          }
          return {
            id: item.id,
            userId: typeof data.userId === "string" ? data.userId : "",
            type: typeof data.type === "string" ? data.type : "INFO",
            title: typeof data.title === "string" ? data.title : "Notifikasi",
            message: typeof data.message === "string" ? data.message : (typeof data.body === "string" ? data.body : ""),
            read: data.read === true || data.isRead === true,
            relatedId: typeof data.relatedId === "string" ? data.relatedId : "",
            createdAt: timestamp,
          };
        }).sort((a, b) => (b.createdAt?.getTime() ?? 0) - (a.createdAt?.getTime() ?? 0));
        setNotifications(records);
        setNotificationLoadError(false);
      }, (error) => {
        console.error("Gagal membaca notifikasi Firebase", error);
        setNotificationLoadError(true);
      });
    } catch (error) {
      console.error("Firebase belum siap untuk membaca notifikasi", error);
      setNotificationLoadError(true);
    }
  }, [user.uid]);

  useEffect(() => {
    if (page !== "requests" && page !== "history") return;
    try {
      const { db } = getFirebaseClient();
      return onSnapshot(
        query(collection(db, "pengajuan"), where("uid", "==", user.uid)),
        (snapshot) => {
          const records = snapshot.docs.map((item) => {
            const data = item.data();
            const rawCreatedAt = data.createdAt;
            let createdAt: Date | null = null;
            if (rawCreatedAt && typeof rawCreatedAt.toDate === "function") createdAt = rawCreatedAt.toDate() as Date;
            else if (typeof rawCreatedAt === "string") {
              const parsed = new Date(rawCreatedAt);
              if (!Number.isNaN(parsed.getTime())) createdAt = parsed;
            }
            return {
              id: item.id,
              jenis: typeof data.jenis === "string" ? data.jenis : "Lainnya",
              tanggal: typeof data.tanggal === "string" ? data.tanggal : "",
              jamPulang: typeof data.jamPulang === "string" ? data.jamPulang : "",
              jamKeluar: typeof data.jamKeluar === "string" ? data.jamKeluar : "",
              jamKembali: typeof data.jamKembali === "string" ? data.jamKembali : "",
              tanggalMulai: typeof data.tanggalMulai === "string" ? data.tanggalMulai : (typeof data.tanggal === "string" ? data.tanggal : ""),
              tanggalSelesai: typeof data.tanggalSelesai === "string" ? data.tanggalSelesai : (typeof data.tanggalMulai === "string" ? data.tanggalMulai : ""),
              keterangan: typeof data.keterangan === "string" ? data.keterangan : (typeof data.alasan === "string" ? data.alasan : ""),
              status: typeof data.status === "string" ? data.status : "Menunggu",
              createdAt,
            };
          }).sort((a, b) => (b.createdAt?.getTime() ?? 0) - (a.createdAt?.getTime() ?? 0));
          setEmployeeRequests(records);
          setRequestLoadError(false);
        },
        (error) => {
          console.error("Gagal memuat pengajuan", error);
          setRequestLoadError(true);
        }
      );
    } catch (error) {
      console.error("Firebase belum siap untuk pengajuan", error);
      setRequestLoadError(true);
    }
  }, [page, user.uid]);

  async function submitRequest() {
    setRequestError("");
    setRequestMessage("");
    if (!requestType || !requestReason.trim()) {
      setRequestError("Pilih jenis pengajuan dan isi alasan terlebih dahulu.");
      return;
    }
    const needsRange = requestType === "Izin Sakit" || requestType === "Cuti Reguler";
    if (needsRange && (!requestStart || !requestEnd)) {
      setRequestError("Tanggal mulai dan tanggal selesai wajib diisi.");
      return;
    }
    if (needsRange && requestEnd < requestStart) {
      setRequestError("Tanggal selesai tidak boleh sebelum tanggal mulai.");
      return;
    }
    if (requestType === "Cuti Reguler") {
      const today = new Date(); today.setHours(0, 0, 0, 0);
      const start = new Date(`${requestStart}T00:00:00`);
      const end = new Date(`${requestEnd}T00:00:00`);
      const daysUntilStart = Math.ceil((start.getTime() - today.getTime()) / 86400000);
      const durationDays = Math.floor((end.getTime() - start.getTime()) / 86400000) + 1;
      if (daysUntilStart < 7) { setRequestError("Cuti Reguler wajib diajukan minimal H-7."); return; }
      if (durationDays > 2) { setRequestError("Cuti Reguler maksimal 2 hari."); return; }
      const sameMonthCount = employeeRequests.filter(item => item.jenis === "Cuti Reguler" && (item.tanggalMulai || item.tanggal).slice(0, 7) === requestStart.slice(0, 7) && !["ditolak", "rejected"].includes(item.status.toLowerCase())).length;
      if (sameMonthCount >= 2) { setRequestError("Batas Cuti Reguler maksimal 2 kali dalam bulan yang sama."); return; }
    }
    if (requestType === "Pulang Cepat" && !requestTime) {
      setRequestError("Jam pulang wajib diisi.");
      return;
    }
    if (requestType === "Izin Keluar" && (!requestExitTime || !requestReturnTime)) {
      setRequestError("Jam keluar dan jam kembali wajib diisi.");
      return;
    }
    setRequestBusy(true);
    try {
      const { db } = getFirebaseClient();
      // Mirror the Android approval chain: Supervisor -> Manager -> HRD -> Owner.
      const usersSnapshot = await getDocs(collection(db, "users"));
      const approvalRoles = ["Supervisor", "Manager", "HRD", "Owner"];
      const approvers: Array<{ uid: string; nama: string; jabatan: string }> = [];
      for (const role of approvalRoles) {
        const matches = usersSnapshot.docs.filter((personDoc) =>
          String(personDoc.data().jabatan ?? "").trim().toLowerCase() === role.toLowerCase()
        );
        if (matches.length !== 1) {
          throw new Error(matches.length === 0
            ? `Approver dengan jabatan ${role} belum tersedia.`
            : `Terdapat lebih dari satu karyawan dengan jabatan ${role}. Tentukan satu approver terlebih dahulu.`);
        }
        const personDoc = matches[0];
        const person = personDoc.data();
        approvers.push({
          uid: String(person.uid ?? "").trim() || personDoc.id,
          nama: String(person.nama ?? ""),
          jabatan: String(person.jabatan ?? role),
        });
      }
      if (approvers.some((person) => !person.uid)) {
        throw new Error("Data UID approver belum lengkap. Hubungi Admin.");
      }
      const approvalChain = approvers.map((person) => person.uid);
      const approvalStatuses: Record<string, string> = {};
      approvers.forEach((person, index) => {
        approvalStatuses[person.uid] = index === 0 ? "menunggu" : "belum";
      });
      const employeeName = String(employeeProfile?.nama ?? employeeProfile?.name ?? user.displayName ?? user.email ?? "Karyawan");
      const requestRef = await addDoc(collection(db, "pengajuan"), {
        uid: user.uid,
        nama: employeeName,
        jenis: requestType,
        tanggal: (() => { const now = new Date(); return `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,"0")}-${String(now.getDate()).padStart(2,"0")}`; })(),
        jamPulang: requestTime,
        jamKeluar: requestExitTime,
        jamKembali: requestReturnTime,
        tanggalMulai: needsRange ? requestStart : "",
        tanggalSelesai: needsRange ? requestEnd : "",
        alasan: requestReason.trim(),
        keterangan: requestReason.trim(),
        status: "menunggu",
        waktuPengajuan: serverTimestamp(),
        createdAt: serverTimestamp(),
        approvalChain,
        approvalStatuses,
        currentApproverUid: approvers[0].uid,
        currentApproverName: approvers[0].nama,
        currentApproverJabatan: approvers[0].jabatan,
        approvalLocked: false,
      });
      setRequestMessage("Pengajuan berhasil dikirim dan menunggu persetujuan Supervisor.");
      setRequestStart("");
      setRequestEnd("");
      setRequestTime("");
      setRequestExitTime("");
      setRequestReturnTime("");
      setRequestReason("");
      setRequestType("");
      setShowRequestForm(false);

      // Notifikasi adalah proses terpisah supaya kegagalannya tidak membuat
      // pengajuan yang sudah tersimpan terlihat seolah-olah gagal dikirim.
      try {
        await addDoc(collection(db, "notifications"), {
          userId: approvers[0].uid,
          type: "PENGAJUAN_BARU",
          title: "Pengajuan Baru",
          message: `${employeeName} membuat pengajuan ${requestType} dan menunggu persetujuan Anda.`,
          timestamp: Date.now(),
          isRead: false,
          read: false,
          relatedId: requestRef.id,
        });
      } catch (notificationError) {
        console.error("Pengajuan tersimpan, tetapi notifikasi Supervisor gagal dibuat", notificationError);
        setRequestMessage("Pengajuan tersimpan, tetapi notifikasi Supervisor gagal dikirim. Hubungi Admin untuk memeriksa izin koleksi notifications.");
      }
    } catch (error) {
      console.error("Gagal mengirim pengajuan", error);
      const code = (error as { code?: string })?.code;
      setRequestError(code === "permission-denied"
        ? "Pengajuan ditolak oleh aturan keamanan Firestore. Hubungi Admin untuk memeriksa izin koleksi pengajuan."
        : error instanceof Error ? error.message : "Pengajuan belum tersimpan. Periksa koneksi lalu coba lagi.");
    } finally {
      setRequestBusy(false);
    }
  }

  useEffect(() => {
    if (page !== "chat") return;
    setChatLoading(true);
    setChatError("");
    try {
      const { db } = getFirebaseClient();
      return onSnapshot(
        query(collection(db, "chatRooms", user.uid, "messages"), orderBy("timestamp", "asc")),
        (snapshot) => {
          setChatMessages(snapshot.docs.map((item) => {
            const data = item.data();
            return {
              id: item.id,
              senderId: typeof data.senderId === "string" ? data.senderId : "",
              senderName: typeof data.senderName === "string" ? data.senderName : "",
              senderType: typeof data.senderType === "string" ? data.senderType : "",
              message: typeof data.message === "string" ? data.message : "",
              timestamp: typeof data.timestamp === "number" ? data.timestamp : 0,
            };
          }));
          setChatLoading(false);
          setChatError("");
        },
        (error) => {
          console.error("Gagal memuat Chat Admin", error);
          setChatLoading(false);
          setChatError("Chat belum bisa dimuat. Periksa aturan akses Firestore.");
        }
      );
    } catch (error) {
      console.error("Firebase belum siap untuk Chat Admin", error);
      setChatLoading(false);
      setChatError("Konfigurasi Firebase belum siap.");
    }
  }, [page, user.uid]);

  async function sendChatMessage() {
    const message = chatDraft.trim();
    if (!message || chatSending) return;
    setChatSending(true);
    setChatError("");
    try {
      const { db } = getFirebaseClient();
      const timestamp = Date.now();
      const staffName = String(employeeProfile?.nama ?? employeeProfile?.name ?? user.displayName ?? "Staff");
      const roomRef = doc(db, "chatRooms", user.uid);
      const messageRef = doc(collection(roomRef, "messages"));
      await setDoc(roomRef, {
        staffId: user.uid,
        staffName,
        lastMessage: message,
        updatedAt: timestamp,
      }, { merge: true });
      await setDoc(messageRef, {
        senderId: user.uid,
        senderName: staffName,
        senderType: "STAFF",
        message,
        timestamp,
      });
      setChatDraft("");
      // Notifikasi admin dicoba terpisah supaya kegagalannya tidak membatalkan pesan chat.
      try {
        const admins = await getDocs(query(collection(db, "users"), where("isAdmin", "==", true)));
        await Promise.all(admins.docs.map((admin) => addDoc(collection(db, "notifications"), {
          userId: admin.id,
          type: "CHAT",
          title: "Pesan Baru dari Staff",
          message: `${staffName}: ${message}`,
          timestamp: Date.now(),
          read: false,
          relatedId: user.uid,
        })));
      } catch (notificationError) {
        console.warn("Pesan terkirim, tetapi notifikasi admin tidak berhasil dibuat.", notificationError);
      }
    } catch (error) {
      console.error("Gagal mengirim pesan Chat Admin", error);
      setChatError("Pesan gagal dikirim. Periksa koneksi dan aturan akses Firestore.");
    } finally {
      setChatSending(false);
    }
  }

  async function markNotificationRead(notificationId: string) {
    setNotificationBusy(notificationId);
    try {
      const { db } = getFirebaseClient();
      await updateDoc(doc(db, "notifications", notificationId), {
        read: true,
      });
    } catch (error) {
      console.error("Gagal menandai notifikasi sudah dibaca", error);
      setNotificationLoadError(true);
    } finally {
      setNotificationBusy(null);
    }
  }

  async function markAllNotificationsRead() {
    const unread = notifications.filter((item) => !item.read);
    await Promise.all(unread.map((item) => markNotificationRead(item.id)));
  }

  useEffect(() => {
    if (page !== "scan") return;
    let active = true;
    async function loadRegisteredQrs() {
      setQrListLoading(true);
      setQrListError("");
      try {
        const { db } = getFirebaseClient();
        const snapshot = await getDocs(collection(db, "qr_settings"));
        const records: RegisteredQr[] = snapshot.docs.flatMap((item) => {
          const data = item.data();
          const qrData = typeof data.qrData === "string" ? data.qrData.trim() : "";
          if (data.aktif !== true || !qrData) return [];
          return [{
            id: item.id,
            qrData,
            officeName: typeof data.officeName === "string" && data.officeName.trim() ? data.officeName.trim() : "Kantor",
            description: typeof data.description === "string" ? data.description : "",
          }];
        });
        if (active) setRegisteredQrs(records);
      } catch (error) {
        console.error("Gagal memuat daftar QR kantor", error);
        if (active) setQrListError("Daftar kantor gagal dimuat. Periksa koneksi dan aturan akses Firestore.");
      } finally {
        if (active) setQrListLoading(false);
      }
    }
    void loadRegisteredQrs();
    return () => { active = false; };
  }, [page]);

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
    { id: "scan", label: "Absen", icon: CalendarDays },
    { id: "history", label: "Riwayat", icon: History },
    { id: "settings", label: "Setting", icon: Settings },
  ];

  return <div className={`app-shell ${themeMode === "gelap" ? "theme-dark" : ""}`}>
    <header className="topbar">
      <div><p className="greeting">Halo, Karyawan <span>👋</span></p><p className="muted">{new Intl.DateTimeFormat("id-ID", { weekday: "long", day: "numeric", month: "long", year: "numeric" }).format(new Date())}</p></div>
      <div className="notification-wrap">
        <button className="round-button notification-button" type="button" aria-label={unreadNotifications ? `Notifikasi, ${unreadNotifications} belum dibaca` : "Notifikasi"} aria-expanded={notificationsOpen} onClick={() => setNotificationsOpen((open) => !open)}>
          <Bell />
          {unreadNotifications > 0 && <span className="notification-badge">{unreadNotifications > 99 ? "99+" : unreadNotifications}</span>}
        </button>
        {notificationsOpen && <section className="notification-panel" aria-label="Daftar notifikasi">
          <div className="notification-panel-head"><strong>Notifikasi</strong><button type="button" className="text-button" disabled={unreadNotifications === 0 || notificationBusy !== null} onClick={() => void markAllNotificationsRead()}>Tandai semua dibaca</button></div>
          {notificationLoadError ? <p className="notification-empty">Notifikasi gagal dimuat. Periksa aturan akses Firestore.</p> :
            notifications.length === 0 ? <p className="notification-empty">Belum ada notifikasi.</p> :
              <div className="notification-list">{notifications.map((item) => <button key={item.id} type="button" className={item.read ? "notification-item read" : "notification-item unread"} disabled={notificationBusy === item.id} onClick={() => { if (!item.read) void markNotificationRead(item.id); }}>
                <span className="notification-dot" />
                <span className="notification-copy"><strong>{item.title}</strong>{item.message && <span>{item.message}</span>}<small>{item.createdAt ? new Intl.DateTimeFormat("id-ID", { dateStyle: "medium", timeStyle: "short" }).format(item.createdAt) : "Baru saja"}</small></span>
                {!item.read && <span className="notification-unread-label">Baru</span>}
              </button>)}</div>}
        </section>}
      </div>
      <button className="avatar avatar-button" type="button" aria-label="Buka Profil" title="Buka Profil" onClick={() => setPage("profile")}>{String(employeeProfile?.nama || employeeProfile?.name || user.displayName || user.email || "K").trim().charAt(0).toUpperCase()}</button>
    </header>

    {page === "home" && <main className="page-content home-content">
      <section className="clock-card">
        <div className="clock-head"><span><Clock3 size={18} /> Waktu Sekarang</span><b className={`status-pill ${attendanceLoadError ? "status-error" : todayAttendance ? "status-present" : "status-absent"}`}>{attendanceLoadError ? "GAGAL MEMUAT" : todayAttendance ? "SUDAH ABSEN" : "BELUM ABSEN"}</b></div>
        <div className="clock-time">{new Intl.DateTimeFormat("id-ID", { hour: "2-digit", minute: "2-digit", second: "2-digit", hour12: false }).format(new Date())}</div>
        <div className="clock-divider" /><div className="clock-foot"><span>Jam Masuk<strong>{todayAttendance?.jamMasuk || "-"}</strong></span><span>Jam Pulang<strong>{todayAttendance?.jamPulang || "-"}</strong></span></div>{attendanceLoadError && <p className="error-message">Status absensi gagal dimuat. Periksa aturan akses Firestore.</p>}
      </section>
      <h2 className="section-title">Absensi Hari Ini</h2>
      <button className="attendance-action dark-action" onClick={() => setPage("scan")}><span className="action-icon"><CalendarDays /></span><span className="action-copy"><strong>Pilih Kantor untuk Absen</strong><small>Pilih kantor terdaftar untuk absen masuk</small></span><ChevronRight /></button>
      <button className="attendance-action" onClick={() => { setPage("scan"); setOutsideOpen(true); }}><span className="action-icon pale"><MapPin /></span><span className="action-copy"><strong>Absen Luar Kantor</strong><small>Untuk tugas langsung ke klien</small></span><ChevronRight /></button>
      <h2 className="section-title quick-title">Aksi Cepat</h2>
      <div className="quick-grid">
        <button className="quick-card" onClick={() => setPage("history")}><span><History /></span><strong>Riwayat</strong><small>Absensi</small></button>
        <button className="quick-card" onClick={() => setPage("requests")}><span><FileText /></span><strong>Pengajuan</strong><small>Izin / Sakit</small></button>
        <button className="quick-card" onClick={() => setPage("profile")}><span><UserRound /></span><strong>Profil</strong><small>Data Saya</small></button>
      </div>
    </main>}

    {page === "scan" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Absen Masuk</h1><p className="muted">Pilih kantor dari daftar QR yang terdaftar</p></div><button className="icon-button" type="button" aria-label="Muat ulang daftar kantor" onClick={() => { setRegisteredQrs([]); setQrListError(""); setPage("home"); setTimeout(() => setPage("scan"), 0); }}><RefreshCw /></button></div>
      <div className="info-banner"><Info size={20} /> Pilih lokasi kantor yang sesuai sebelum mengonfirmasi kehadiran.</div>
      {qrListLoading && <div className="empty-state"><RefreshCw size={32} /><strong>Memuat daftar kantor...</strong><p>Mengambil QR kantor aktif dari Firebase.</p></div>}
      {qrListError && <div className="empty-state"><Info size={32} /><strong>Daftar kantor belum tersedia</strong><p>{qrListError}</p><button className="primary-button" type="button" onClick={() => { setPage("home"); setTimeout(() => setPage("scan"), 0); }}>Coba Lagi</button></div>}
      {!qrListLoading && !qrListError && registeredQrs.length === 0 && <div className="empty-state"><MapPin size={36} /><strong>Belum ada kantor aktif</strong><p>QR kantor aktif belum ditemukan di Firebase. Hubungi Admin untuk memeriksa koleksi qr_settings.</p></div>}
      {!qrListLoading && !qrListError && registeredQrs.length > 0 && <section className="form-card">
        <h2><MapPin /> Pilih Kantor</h2>
        <div className="office-choice-list">{registeredQrs.map((office) => <button key={office.id} type="button" className={scannedQr?.qrData === office.qrData ? "office-choice selected" : "office-choice"} onClick={() => { setScannedQr({ qrData: office.qrData, officeName: office.officeName }); setAttendanceMessage(""); }}><span className="office-choice-icon"><BriefcaseBusiness size={22} /></span><span className="office-choice-copy"><strong>{office.officeName}</strong><small>{office.description || "Kantor terdaftar"}</small></span><span className="office-choice-check">{scannedQr?.qrData === office.qrData && <Check size={20} />}</span></button>)}</div>
      </section>}
      {scannedQr && <div className="verified-qr"><Check size={18} /><span><strong>Kantor dipilih</strong><small>{scannedQr.officeName}</small></span></div>}
      <label className="input-block">Catatan (opsional)<input value={qrNote} onChange={e => setQrNote(e.target.value)} placeholder="Tambahkan catatan jika diperlukan" /></label>
      <button className="primary-button wide" disabled={!scannedQr || attendanceBusy || qrListLoading || Boolean(todayAttendance?.jamMasuk)} onClick={async () => {
        setAttendanceMessage("");
        setAttendanceBusy(true);
        try {
          if (!scannedQr) {
            setAttendanceMessage("Pilih kantor terlebih dahulu.");
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
          setPage("home");
        } catch (e) {
          console.error("Gagal menyimpan presensi", e);
          const code = (e as { code?: string })?.code;
          setAttendanceMessage(code === "permission-denied"
            ? "Presensi ditolak oleh aturan keamanan Firestore. Hubungi Admin."
            : "Presensi belum tersimpan. Periksa koneksi lalu coba lagi.");
        } finally {
          setAttendanceBusy(false);
        }
      }}>{attendanceBusy ? "Menyimpan..." : todayAttendance?.jamMasuk ? "Sudah Absen Hari Ini" : <><Check /> Konfirmasi Absen</>}</button>
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

    {page === "requests" && isApprover && approverTab === "approval" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Persetujuan Pengajuan</h1><p className="muted">Periksa dan putuskan pengajuan karyawan</p></div></div>
      <div className="segmented large" aria-label="Bagian pengajuan"><button type="button" className="selected" onClick={() => setApproverTab("approval")}>Persetujuan</button><button type="button" onClick={() => setApproverTab("mine")}>Pengajuan Saya</button></div>
      {approvalMessage && <p role="status" className="success-message">{approvalMessage}</p>}
      {approvalError && <p role="alert" className="error-message">{approvalError}</p>}
      <div className="request-summary"><div><span className="summary-dot" /><div><strong>{approvalRequests.filter(item => item.canApprove).length} pengajuan menunggu tindakan Anda</strong><p className="muted">Pengajuan yang berada dalam wewenang akunmu ditampilkan di sini. Kamu bisa menyetujui atau menolak langsung tanpa menunggu atasan lain.</p></div></div></div>
      {approvalRequests.length === 0 ? <div className="empty-state"><FileText size={42} /><strong>Belum ada pengajuan</strong><p>Pengajuan yang ditujukan ke akunmu akan muncul di sini.</p></div> : <div className="history-list">{approvalRequests.map(item => { const canAct = item.status.toLowerCase() === "menunggu" && item.canApprove; return <article className="history-record" key={item.id}><div className="history-record-date"><FileText size={19} /><strong>{item.nama} · {item.jenis}</strong><span className={`status-pill ${["disetujui","approved"].includes(item.status.toLowerCase()) ? "status-present" : ["ditolak","rejected"].includes(item.status.toLowerCase()) ? "status-error" : "status-waiting"}`}>{item.status}</span></div><div className="history-record-times"><span>Tanggal mulai<strong>{item.tanggalMulai || item.tanggal || "-"}</strong></span><span>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? "Tanggal selesai" : "Waktu / detail"}<strong>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? item.tanggalSelesai : item.jamPulang || (item.jamKeluar ? item.jamKeluar + (item.jamKembali ? " – " + item.jamKembali : "") : "-")}</strong></span></div><p className="muted history-location">{item.keterangan || "Tidak ada keterangan"}</p>{canAct && <div className="approval-actions"><button className="primary-button" type="button" disabled={!!approvalBusyId} onClick={() => void decideRequest(item, "Disetujui")}>{approvalBusyId === item.id ? "Memproses..." : "Setujui"}</button><button className="secondary-button" type="button" disabled={!!approvalBusyId} onClick={() => void decideRequest(item, "Ditolak")}>Tolak</button></div>}{!canAct && <p className="muted">Pengajuan ini bukan dalam wewenang persetujuan akunmu.</p>}{canAct && <p className="approval-turn-note">Kamu berwenang memutuskan pengajuan ini sekarang, tanpa menunggu atasan lain.</p>}</article>; })}</div>}
    </main>}

    {page === "requests" && (!isApprover || approverTab === "mine") && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Pengajuan</h1><p className="muted">Ajukan izin, sakit, cuti, atau keperluan lainnya</p></div><button className="icon-button" type="button" aria-label="Muat ulang pengajuan" onClick={() => { setRequestLoadError(false); setPage("home"); setTimeout(() => setPage("requests"), 0); }}><RefreshCw /></button></div>
      {isApprover && <div className="segmented large" aria-label="Bagian pengajuan"><button type="button" className={approverTab === "approval" ? "selected" : ""} onClick={() => setApproverTab("approval")}>Persetujuan</button><button type="button" className={approverTab === "mine" ? "selected" : ""} onClick={() => setApproverTab("mine")}>Pengajuan Saya</button></div>}
      <button className="new-request" type="button" onClick={() => { setShowRequestForm(!showRequestForm); setRequestError(""); setRequestMessage(""); }}><FilePlus2 size={30} /><span><strong>{showRequestForm ? "Tutup Form Pengajuan" : "Buat Pengajuan Baru"}</strong><small>Isi tanggal dan keterangan dengan benar</small></span></button>
      {requestMessage && <div className="info-banner" role="status"><Check size={20} /> {requestMessage}</div>}
{showRequestForm && <section className="form-card"><h2>Pengajuan Baru</h2><p className="muted">Pilih jenis pengajuan seperti di aplikasi Android.</p><div className="request-type-grid">{[{name:"Pulang Cepat",desc:"Tanggal hari ini, jam pulang, dan alasan"},{name:"Izin Keluar",desc:"Tanggal hari ini, jam keluar dan kembali"},{name:"Izin Terlambat",desc:"Jelaskan alasan keterlambatan"},{name:"Izin Sakit",desc:"Tanggal mulai sampai selesai"},{name:"Cuti Reguler",desc:"Ajukan cuti sesuai ketentuan perusahaan"}].map(type => <button key={type.name} type="button" className={requestType === type.name ? "request-type-card selected" : "request-type-card"} onClick={() => {setRequestType(type.name);setRequestError("");setRequestMessage("");}}><strong>{type.name}</strong><small>{type.desc}</small></button>)}</div>{requestType && <><h3>Detail Pengajuan · {requestType}</h3>{(requestType === "Izin Sakit" || requestType === "Cuti Reguler") && <><label className="input-block">Tanggal Mulai<input type="date" value={requestStart} onChange={e => setRequestStart(e.target.value)} required /></label><label className="input-block">Tanggal Selesai<input type="date" min={requestStart || undefined} value={requestEnd} onChange={e => setRequestEnd(e.target.value)} required /></label></>}{requestType === "Pulang Cepat" && <label className="input-block">Jam Pulang<input type="time" value={requestTime} onChange={e => setRequestTime(e.target.value)} required /></label>}{requestType === "Izin Keluar" && <><label className="input-block">Jam Keluar<input type="time" value={requestExitTime} onChange={e => setRequestExitTime(e.target.value)} required /></label><label className="input-block">Jam Kembali<input type="time" value={requestReturnTime} onChange={e => setRequestReturnTime(e.target.value)} required /></label></>}{requestType === "Izin Terlambat" && <p className="muted">Isi alasan keterlambatan pada kolom di bawah.</p>}<label className="input-block">Alasan<textarea rows={3} value={requestReason} onChange={e => setRequestReason(e.target.value)} placeholder="Jelaskan alasan pengajuan" required /></label><button className="primary-button wide" type="button" disabled={requestBusy} onClick={() => void submitRequest()}>{requestBusy ? "Mengirim..." : "Kirim Pengajuan"}</button></>}{requestError && <p role="alert" className="error-message">{requestError}</p>}</section>}
      <div className="stats-grid"><div className="stat stat-wait"><span>•••</span><strong>{employeeRequests.filter(item => item.status.toLowerCase() === "menunggu" || item.status.toLowerCase() === "pending").length}</strong><small>Menunggu</small></div><div className="stat stat-approved"><Check /><strong>{employeeRequests.filter(item => item.status.toLowerCase() === "disetujui" || item.status.toLowerCase() === "approved").length}</strong><small>Disetujui</small></div><div className="stat stat-rejected"><X /><strong>{employeeRequests.filter(item => item.status.toLowerCase() === "ditolak" || item.status.toLowerCase() === "rejected").length}</strong><small>Ditolak</small></div></div>
      <div className="filter-pills">{["Semua","Menunggu","Disetujui","Ditolak"].map(value => <button key={value} type="button" className={requestStatusFilter === value ? "selected" : ""} onClick={() => setRequestStatusFilter(value)}>{value}</button>)}</div>
      {requestLoadError ? <div className="empty-state"><Info size={42} /><strong>Pengajuan belum bisa dimuat</strong><p>Periksa koneksi dan aturan akses Firestore untuk koleksi pengajuan.</p></div> : employeeRequests.length === 0 ? <div className="empty-state"><FileText size={42} /><strong>Belum ada pengajuan</strong><p>Pengajuan yang kamu kirim akan muncul di sini.</p></div> : <div className="history-list">{employeeRequests.filter(item => requestStatusFilter === "Semua" || (requestStatusFilter === "Menunggu" ? ["menunggu","pending"].includes(item.status.toLowerCase()) : requestStatusFilter === "Disetujui" ? ["disetujui","approved"].includes(item.status.toLowerCase()) : ["ditolak","rejected"].includes(item.status.toLowerCase()))).map(item => <article className="history-record" key={item.id}><div className="history-record-date"><FileText size={19} /><strong>{item.jenis}</strong><span className={`status-pill ${["disetujui","approved"].includes(item.status.toLowerCase()) ? "status-present" : ["ditolak","rejected"].includes(item.status.toLowerCase()) ? "status-error" : "status-waiting"}`}>{item.status}</span></div><div className="history-record-times"><span>Tanggal<strong>{item.tanggalMulai || item.tanggal || "-"}</strong></span><span>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? "Selesai" : "Waktu"}<strong>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? item.tanggalSelesai : item.jamPulang || (item.jamKeluar ? item.jamKeluar + (item.jamKembali ? " – " + item.jamKembali : "") : "-")}</strong></span></div><p className="muted history-location">{item.keterangan}</p></article>)}</div>}
    </main>}

    {page === "history" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")}><ArrowRight className="back-arrow" /></button><div><h1>Riwayat</h1><p className="muted">Riwayat dan status pengajuan</p></div><button className="icon-button" aria-label="Muat ulang"><RefreshCw /></button></div>
      <div className="segmented large"><button className={historyTab === "attendance" ? "selected" : ""} onClick={() => setHistoryTab("attendance")}>Absensi</button><button className={historyTab === "requests" ? "selected" : ""} onClick={() => setHistoryTab("requests")}>Pengajuan</button></div>
      <div className="filter-pills">{(historyTab === "attendance" ? ["7 Hari", "1 Bulan", "Semua"] : ["Semua", "Menunggu", "Disetujui", "Ditolak"]).map((x) => <button type="button" className={(historyTab === "attendance" ? x === "7 Hari" : historyRequestFilter === x) ? "selected" : ""} key={x} onClick={() => historyTab === "requests" && setHistoryRequestFilter(x)}>{x}</button>)}</div>
      {historyTab === "attendance" ? (
        historyLoadError ? <div className="empty-state history-empty"><Info size={42} /><strong>Riwayat belum bisa dimuat</strong><p>Periksa koneksi dan aturan akses Firestore, lalu coba lagi.</p></div> :
        attendanceHistory.length === 0 ? <div className="empty-state history-empty"><CalendarDays size={42} /><strong>Belum ada riwayat absensi</strong><p>Data kehadiran akan muncul di sini setelah presensi tersimpan.</p></div> :
        <div className="history-list">{attendanceHistory.map((record) => <article className="history-record" key={record.id}><div className="history-record-date"><CalendarDays size={19} /><strong>{record.tanggal || "Tanggal tidak tersedia"}</strong><span className="status-pill status-present">{record.status}</span></div><div className="history-record-times"><span>Jam Masuk<strong>{record.jamMasuk || "-"}</strong></span><span>Jam Pulang<strong>{record.jamPulang || "-"}</strong></span></div>{record.kantor && <p className="muted history-location"><MapPin size={15} /> {record.kantor}</p>}</article>)}</div>
      ) : requestLoadError ? <div className="empty-state history-empty"><Info size={42} /><strong>Riwayat pengajuan belum bisa dimuat</strong><p>Periksa koneksi dan izin akses Firestore.</p></div> : employeeRequests.filter(item => historyRequestFilter === "Semua" || (historyRequestFilter === "Menunggu" ? ["menunggu","pending"].includes(item.status.toLowerCase()) : historyRequestFilter === "Disetujui" ? ["disetujui","approved"].includes(item.status.toLowerCase()) : ["ditolak","rejected"].includes(item.status.toLowerCase()))).length === 0 ? <div className="empty-state history-empty"><FileText size={42} /><strong>Belum ada riwayat pengajuan</strong><p>Pengajuan sesuai filter akan muncul di sini.</p></div> : <div className="history-list">{employeeRequests.filter(item => historyRequestFilter === "Semua" || (historyRequestFilter === "Menunggu" ? ["menunggu","pending"].includes(item.status.toLowerCase()) : historyRequestFilter === "Disetujui" ? ["disetujui","approved"].includes(item.status.toLowerCase()) : ["ditolak","rejected"].includes(item.status.toLowerCase()))).map(item => <article className="history-record" key={item.id}><div className="history-record-date"><FileText size={19} /><strong>{item.jenis}</strong><span className={`status-pill ${["disetujui","approved"].includes(item.status.toLowerCase()) ? "status-present" : ["ditolak","rejected"].includes(item.status.toLowerCase()) ? "status-error" : "status-waiting"}`}>{item.status}</span></div><div className="history-record-times"><span>Tanggal<strong>{item.tanggalMulai || item.tanggal || "-"}</strong></span><span>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? "Selesai" : "Waktu"}<strong>{item.tanggalSelesai && item.tanggalSelesai !== item.tanggalMulai ? item.tanggalSelesai : item.jamPulang || (item.jamKeluar ? item.jamKeluar + (item.jamKembali ? " – " + item.jamKembali : "") : "-")}</strong></span></div><p className="muted history-location">{item.keterangan}</p></article>)}</div>}
    </main>}

    {page === "profile" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => setPage("home")} aria-label="Kembali ke beranda"><ArrowRight className="back-arrow" /></button><div><h1>Profil Saya</h1><p className="muted">Informasi akun karyawan</p></div></div>
      {profileLoadError ? <div className="empty-state"><Info size={42} /><strong>Profil belum bisa dimuat</strong><p>Periksa koneksi dan izin akses Firestore, lalu coba lagi.</p><button className="primary-button" onClick={async () => { setProfileLoadError(false); try { const { db } = getFirebaseClient(); const snapshot = await getDoc(doc(db, "users", user.uid)); setEmployeeProfile(snapshot.exists() ? snapshot.data() : {}); } catch { setProfileLoadError(true); } }}>Coba Lagi</button></div> : <>
        <section className="profile-hero"><div className="profile-avatar"><UserRound size={34} /></div><h2>{String(employeeProfile?.nama ?? employeeProfile?.name ?? user.displayName ?? "Karyawan")}</h2><p>{user.email || "Email tidak tersedia"}</p></section>
        <section className="settings-card profile-details">
          <div className="profile-row"><span><UserRound /></span><div><small>Nama Lengkap</small><strong>{String(employeeProfile?.nama ?? employeeProfile?.name ?? user.displayName ?? "-")}</strong></div></div>
          <div className="profile-row"><span><BriefcaseBusiness /></span><div><small>Divisi</small><strong>{String(employeeProfile?.divisi ?? employeeProfile?.division ?? "-")}</strong></div></div>
          <div className="profile-row"><span><BriefcaseBusiness /></span><div><small>Jabatan</small><strong>{String(employeeProfile?.jabatan ?? employeeProfile?.position ?? "-")}</strong></div></div>
          <div className="profile-row"><span><Info /></span><div><small>Status Akun</small><strong>{String(employeeProfile?.status ?? (employeeProfile?.active === false ? "Nonaktif" : "Aktif"))}</strong></div></div>
          <div className="profile-row"><span><LockKeyhole /></span><div><small>Email Login</small><strong>{user.email || "-"}</strong></div></div>
        </section>
      </>}
    </main>}

    {page === "chat" && <main className="page-content subpage chat-page">
      <section className="chat-card">
        <div className="chat-info">
          <button className="chat-back" type="button" onClick={() => setPage("settings")} aria-label="Kembali ke Pengaturan"><ArrowRight className="back-arrow" /></button>
          <span className="chat-avatar"><Headset size={25} /></span>
          <div className="chat-contact"><strong>Hubungi ADMIN</strong><small><span className="chat-online-dot" />Siap membantu</small></div>
        </div>
        <div className="chat-messages" aria-live="polite">
          {chatLoading && <p className="chat-state">Memuat pesan...</p>}
          {!chatLoading && chatMessages.length === 0 && !chatError && <div className="chat-state">
            <span className="chat-empty-avatar"><Headset size={42} /></span>
            <strong>Mulai Percakapan</strong>
            <span>Sampaikan pertanyaan atau kendala kamu kepada ADMIN. Kami siap membantu.</span>
            <div className="chat-first-prompt">Silakan kirim pesan pertama kamu 👋</div>
          </div>}
          {!chatLoading && chatMessages.length > 0 && <div className="chat-date-label"><span>Percakapan</span></div>}
          {chatMessages.map((item) => {
            const own = item.senderType !== "ADMIN";
            return <div key={item.id} className={"chat-message-row " + (own ? "sent" : "received")}>
              <div className={own ? "chat-message own" : "chat-message"}>
                {!own && <small>{item.senderName || "Admin"}</small>}
                <p>{item.message}</p>
                <time>{item.timestamp ? new Intl.DateTimeFormat("id-ID", { hour: "2-digit", minute: "2-digit" }).format(new Date(item.timestamp)) : ""}</time>
              </div>
            </div>;
          })}
        </div>
        {chatError && <p role="alert" className="error-message chat-error">{chatError}</p>}
        <form className="chat-composer" onSubmit={(event) => { event.preventDefault(); void sendChatMessage(); }}>
          <input aria-label="Tulis pesan untuk Admin" value={chatDraft} onChange={(event) => setChatDraft(event.target.value)} placeholder="Tulis pesan..." maxLength={2000} />
          <button type="submit" aria-label="Kirim pesan" disabled={!chatDraft.trim() || chatSending}><Send size={20} /></button>
        </form>
      </section>
    </main>}

    {page === "settings" && <main className="page-content subpage">
      <div className="page-heading"><button className="back-button" onClick={() => settingsSection === "main" ? setPage("home") : setSettingsSection("main")} aria-label="Kembali"><ArrowRight className="back-arrow" /></button><div><h1>{settingsSection === "main" ? "Pengaturan" : settingsSection === "notifications" ? "Notifikasi" : settingsSection === "appearance" ? "Tampilan" : settingsSection === "help" ? "Bantuan" : "Tentang Aplikasi"}</h1><p className="muted">{settingsSection === "main" ? "Atur preferensi dan informasi aplikasi" : settingsSection === "notifications" ? "Lihat pemberitahuan akun kamu" : settingsSection === "appearance" ? "Atur tampilan aplikasi" : settingsSection === "help" ? "Panduan penggunaan WebApp" : "Informasi aplikasi"}</p></div></div>
      {settingsSection === "main" && <>
        <h2 className="section-title">Pengaturan Aplikasi</h2>
        <section className="settings-card">
          <button className="setting-row" onClick={() => setSettingsSection("notifications")}><span><Bell /></span><div><strong>Notifikasi</strong><small>Lihat pemberitahuan dan status terbaru</small></div><ChevronRight /></button>
          <button className="setting-row" onClick={() => setPage("chat")}><span><MessageCircle /></span><div><strong>Chat Admin</strong><small>Kirim pesan atau hubungi Admin / HRD</small></div><ChevronRight /></button>
          <button className="setting-row" onClick={() => setSettingsSection("appearance")}><span><Palette /></span><div><strong>Tampilan</strong><small>Atur mode terang atau gelap</small></div><ChevronRight /></button>
          <button className="setting-row" onClick={() => setSettingsSection("help")}><span><CircleHelp /></span><div><strong>Bantuan</strong><small>Panduan penggunaan aplikasi</small></div><ChevronRight /></button>
          <button className="setting-row" onClick={() => setSettingsSection("about")}><span><Info /></span><div><strong>Tentang Aplikasi</strong><small>Informasi aplikasi dan versi</small></div><ChevronRight /></button>
        </section>
        <h2 className="section-title account-title">Akun</h2>
        <button className="setting-row logout" onClick={() => { const { auth } = getFirebaseClient(); void signOut(auth); }}><span><LogOut /></span><div><strong>Keluar</strong><small>Keluar dari akun aplikasi</small></div><ChevronRight /></button>
      </>}
      {settingsSection === "notifications" && <section className="settings-card settings-detail">
        <div className="notification-panel-head"><strong>Notifikasi terbaru</strong><button type="button" className="text-button" disabled={unreadNotifications === 0 || notificationBusy !== null} onClick={() => void markAllNotificationsRead()}>Tandai semua dibaca</button></div>
        {notificationLoadError ? <p className="notification-empty">Notifikasi gagal dimuat. Periksa koneksi dan aturan akses Firestore.</p> : notifications.length === 0 ? <div className="empty-state"><Bell size={38} /><strong>Belum ada notifikasi</strong><p>Notifikasi ACC pengajuan dan pesan Admin akan muncul di sini jika sudah tercatat di Firebase.</p></div> :
          <div className="notification-list">{notifications.map((item) => <button key={item.id} type="button" className={item.read ? "notification-item read" : "notification-item unread"} disabled={notificationBusy === item.id} onClick={() => { if (!item.read) void markNotificationRead(item.id); }}><span className="notification-dot" /><span className="notification-copy"><strong>{item.title}</strong>{item.message && <span>{item.message}</span>}<small>{item.createdAt ? new Intl.DateTimeFormat("id-ID", { dateStyle: "medium", timeStyle: "short" }).format(item.createdAt) : "Baru saja"}</small></span>{!item.read && <span className="notification-unread-label">Baru</span>}</button>)}</div>}
      </section>}
      {settingsSection === "appearance" && <section className="settings-card settings-detail">
        <h2 className="settings-detail-title">Mode Tampilan</h2>
        {([{id:"terang",title:"Terang",subtitle:"Tampilan terang"},{id:"gelap",title:"Gelap",subtitle:"Tampilan dengan latar gelap"},{id:"sistem",title:"Sistem",subtitle:"Gunakan tampilan standar"}] as const).map((mode) => <button key={mode.id} type="button" className="setting-row theme-option" onClick={() => setThemeMode(mode.id)}><span><Palette /></span><div><strong>{mode.title}</strong><small>{mode.subtitle}</small></div>{themeMode === mode.id && <Check className="theme-check" />}</button>)}
        <p className="muted settings-note">Pilihan tema diterapkan pada WebApp selama sesi ini.</p>
      </section>}
      {settingsSection === "help" && <section className="settings-card settings-detail help-content">
        <h2 className="settings-detail-title">Panduan Singkat</h2>
        <article><strong>Presensi</strong><p>Buka menu Absen, pilih kantor dari daftar QR aktif, lalu konfirmasi kehadiran.</p></article>
        <article><strong>Pengajuan izin, sakit, atau cuti</strong><p>Buka menu Pengajuan untuk melihat dan mengirim pengajuan. Status akan terlihat setelah integrasi data pengajuan aktif.</p></article>
        <article><strong>Notifikasi dan Chat Admin</strong><p>Notifikasi ditampilkan jika sudah tersimpan di Firebase. Jika ada kendala akun atau presensi, hubungi Admin/HRD.</p></article>
        <article><strong>Akun bermasalah?</strong><p>Keluar lalu masuk kembali. Jika tetap gagal, minta Admin memeriksa akun dan aturan akses Firebase.</p></article>
      </section>}
      {settingsSection === "about" && <section className="settings-card settings-detail about-content">
        <div className="about-mark"><FileText size={38} /></div><h2>Absensi Karyawan</h2><p className="muted">Sistem Presensi Karyawan · WebApp</p>
        <div className="profile-row"><span><Info /></span><div><small>Platform</small><strong>Web</strong></div></div>
        <div className="profile-row"><span><RefreshCw /></span><div><small>Versi</small><strong>1.0.0</strong></div></div>
        <p className="muted settings-note">WebApp pendamping aplikasi Android Absensi Karyawan.</p>
      </section>}
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
      const { db } = getFirebaseClient();
      return onAuthStateChanged(auth, async (currentUser) => {
        if (!currentUser) {
          setUser(null);
          setAuthReady(true);
          setAuthError("");
          return;
        }

        try {
          // Employee portal accepts employee accounts only. Admin/control roles must
          // use ABSEN CONTROL, even if their Firebase session is valid.
          let profileSnapshot = await getDoc(doc(db, "users", currentUser.uid));
          // Support legacy user documents whose document ID differs from auth UID.
          if (!profileSnapshot.exists()) {
            const matches = await getDocs(query(collection(db, "users"), where("uid", "==", currentUser.uid)));
            profileSnapshot = matches.docs[0] ?? profileSnapshot;
          }
          const profile = profileSnapshot.data() ?? {};
          const role = String(profile.role ?? profile.peran ?? profile.jabatan ?? "").trim().toLowerCase();
          if (!profileSnapshot.exists() || profile.active === false ||
              String(profile.status ?? "").trim().toLowerCase() === "nonaktif") {
            await signOut(auth);
            setUser(null);
            setAuthError("Akun ini tidak memiliki akses. Periksa status akun atau hubungi Admin.");
          } else {
            setUser(currentUser);
            setAuthError("");
          }
        } catch (error) {
          await signOut(auth).catch(() => undefined);
          setUser(null);
          setAuthError("Hak akses akun tidak dapat diverifikasi. Silakan coba lagi atau hubungi Admin.");
        } finally {
          setAuthReady(true);
        }
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