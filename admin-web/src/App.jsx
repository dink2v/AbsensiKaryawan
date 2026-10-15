import { useEffect, useMemo, useState } from "react";
import { onAuthStateChanged, signInWithEmailAndPassword, signOut } from "firebase/auth";
import {
  collection, doc, getCountFromServer, getDoc, onSnapshot, orderBy,
  query, runTransaction,
} from "firebase/firestore";
import { auth, db, firebaseConfigured } from "./firebase.js";

const sections = [
  { id: "overview", label: "Ringkasan", icon: "▦" },
  { id: "chat", label: "Chat Admin", icon: "☏" },
];

function formatTime(value) {
  if (typeof value !== "number") return "—";
  return new Date(value).toLocaleString("id-ID", { dateStyle: "short", timeStyle: "short" });
}

export default function App() {
  const [user, setUser] = useState(null);
  const [authReady, setAuthReady] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loginBusy, setLoginBusy] = useState(false);
  const [error, setError] = useState("");
  const [section, setSection] = useState("overview");
  const [metrics, setMetrics] = useState({ users: "—", attendance: "—", pengajuan: "—", chat: "—" });
  const [rooms, setRooms] = useState([]);
  const [selectedRoomId, setSelectedRoomId] = useState("");
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const [chatError, setChatError] = useState("");

  useEffect(() => {
    if (!firebaseConfigured || !auth) {
      setAuthReady(true);
      return undefined;
    }
    return onAuthStateChanged(auth, async (nextUser) => {
      setUser(nextUser);
      setIsAdmin(false);
      setAuthReady(true);
      if (!nextUser) return;
      try {
        const marker = await getDoc(doc(db, "controlAdmins", nextUser.uid));
        if (marker.exists()) {
          setIsAdmin(true);
          setError("");
        } else {
          await signOut(auth);
          setError("Akun ini belum terdaftar sebagai admin ABSEN CONTROL.");
        }
      } catch {
        await signOut(auth);
        setError("Tidak dapat memverifikasi hak admin. Periksa Firestore Rules.");
      }
    });
  }, []);

  useEffect(() => {
    if (!user || !isAdmin || !db) return undefined;
    let cancelled = false;
    const loadMetrics = async () => {
      const names = ["users", "attendance", "pengajuan", "chatRooms"];
      const values = await Promise.all(names.map(async (name) => {
        try {
          const snapshot = await getCountFromServer(collection(db, name));
          return snapshot.data().count.toLocaleString("id-ID");
        } catch {
          return "—";
        }
      }));
      if (!cancelled) setMetrics({ users: values[0], attendance: values[1], pengajuan: values[2], chat: values[3] });
    };
    loadMetrics();
    return () => { cancelled = true; };
  }, [user, isAdmin]);

  useEffect(() => {
    if (!user || !isAdmin || !db) return undefined;
    const roomsQuery = query(collection(db, "chatRooms"), orderBy("updatedAt", "desc"));
    return onSnapshot(roomsQuery, (snapshot) => {
      const nextRooms = snapshot.docs.map((item) => ({
        id: item.id,
        staffId: item.data().staffId || item.id,
        staffName: item.data().staffName || "Karyawan",
        lastMessage: item.data().lastMessage || "",
        updatedAt: item.data().updatedAt || 0,
      }));
      setRooms(nextRooms);
      setChatError("");
      setSelectedRoomId((current) => current && nextRooms.some((room) => room.id === current) ? current : nextRooms[0]?.id || "");
    }, () => setChatError("Tidak bisa memuat daftar chat. Periksa Firestore Rules."));
  }, [user, isAdmin]);

  useEffect(() => {
    if (!selectedRoomId || !user || !isAdmin || !db) {
      setMessages([]);
      return undefined;
    }
    const messagesQuery = query(collection(db, "chatRooms", selectedRoomId, "messages"), orderBy("timestamp", "asc"));
    return onSnapshot(messagesQuery, (snapshot) => {
      setMessages(snapshot.docs.map((item) => ({ id: item.id, ...item.data() })));
      setChatError("");
    }, () => setChatError("Pesan tidak bisa dibaca. Periksa Firestore Rules."));
  }, [selectedRoomId, user, isAdmin]);

  const selectedRoom = useMemo(() => rooms.find((room) => room.id === selectedRoomId) || null, [rooms, selectedRoomId]);

  async function handleLogin(event) {
    event.preventDefault();
    if (!auth) return;
    setError("");
    setLoginBusy(true);
    try {
      await signInWithEmailAndPassword(auth, email.trim(), password);
      // The auth-state listener verifies the trusted controlAdmins allowlist.
    } catch {
      setError("Login gagal. Periksa email, password, dan status Firebase Authentication.");
    } finally {
      setLoginBusy(false);
    }
  }

  async function handleLogout() {
    if (auth) await signOut(auth);
    setUser(null);
    setIsAdmin(false);
    setPassword("");
  }

  async function sendReply(event) {
    event.preventDefault();
    const message = draft.trim();
    if (!message || !selectedRoom || !user || !db || sending) return;
    setSending(true);
    setChatError("");
    try {
      const roomRef = doc(db, "chatRooms", selectedRoom.id);
      const messageRef = doc(collection(roomRef, "messages"));
      const timestamp = Date.now();
      await runTransaction(db, async (transaction) => {
        const room = await transaction.get(roomRef);
        if (!room.exists()) throw new Error("Ruang chat sudah tidak ada.");
        transaction.set(messageRef, {
          senderId: user.uid,
          senderName: user.email || "ADMIN",
          senderType: "ADMIN",
          message,
          timestamp,
        });
        transaction.update(roomRef, { lastMessage: message, updatedAt: timestamp });
      });
      setDraft("");
    } catch {
      setChatError("Balasan gagal dikirim. Pastikan Rules sudah diuji dan ruang chat masih ada.");
    } finally {
      setSending(false);
    }
  }

  if (!firebaseConfigured) {
    return <main className="setup-screen"><div className="setup-card">
      <div className="brand-mark">AC</div><p className="eyebrow">PENYIAPAN AWAL</p>
      <h1>Hubungkan Firebase</h1><p>Dashboard web sudah disiapkan, tetapi konfigurasi Firebase belum diisi.</p>
      <ol><li>Salin <code>.env.example</code> menjadi <code>.env.local</code> di folder <code>admin-web</code>.</li><li>Isi konfigurasi aplikasi Web dari Firebase Console → Project settings → Your apps.</li><li>Aktifkan Email/Password pada Firebase Authentication.</li></ol>
      <p className="muted">Jangan masukkan service-account key atau private key ke aplikasi web.</p>
    </div></main>;
  }

  if (!authReady) return <main className="center-screen"><div className="loader" />Memeriksa sesi...</main>;

  if (!user || !isAdmin) {
    return <main className="login-screen">
      <section className="login-panel"><div className="brand-mark">AC</div><p className="eyebrow">ABSEN CONTROL</p>
        <h1>Selamat datang kembali</h1><p className="muted">Masuk menggunakan akun yang terdaftar sebagai admin.</p>
        <form onSubmit={handleLogin} className="login-form">
          <label>Email admin<input type="email" autoComplete="username" required value={email} onChange={(event) => setEmail(event.target.value)} placeholder="nama@perusahaan.com" /></label>
          <label>Password<input type="password" autoComplete="current-password" required value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Masukkan password" /></label>
          {error && <p className="error-text" role="alert">{error}</p>}
          <button className="primary-button" type="submit" disabled={loginBusy}>{loginBusy ? "Memeriksa..." : "Masuk ke dashboard"}</button>
        </form>
        <p className="fine-print">Akses admin diperiksa melalui dokumen <code>controlAdmins/{'{uid}'}</code>.</p>
      </section>
      <aside className="login-aside"><div className="aside-orb orb-one" /><div className="aside-orb orb-two" /><div className="aside-copy"><span className="status-dot" /> Satu pusat kendali<h2>Pantau kehadiran. Tanggapi karyawan. Lebih terarah.</h2><p>Dashboard administrasi yang terhubung ke backend Firebase aplikasi absensi.</p></div></aside>
    </main>;
  }

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="sidebar-brand"><div className="brand-mark small">AC</div><div><strong>ABSEN CONTROL</strong><span>ADMIN CONSOLE</span></div></div>
      <p className="nav-caption">WORKSPACE</p>
      <nav>{sections.map((item) => <button key={item.id} className={section === item.id ? "nav-item active" : "nav-item"} onClick={() => setSection(item.id)}><span className="nav-icon">{item.icon}</span>{item.label}{item.id === "chat" && rooms.length > 0 && <span className="nav-count">{rooms.length}</span>}</button>)}</nav>
      <div className="sidebar-bottom"><div className="user-chip"><div className="avatar">{(user.email || "A").slice(0, 1).toUpperCase()}</div><div className="user-details"><strong>{user.email || "Admin"}</strong><span>Admin terverifikasi</span></div></div><button className="logout-button" onClick={handleLogout}>Keluar <span>↗</span></button></div>
    </aside>
    <main className="main-panel">
      <header className="topbar"><div><p className="eyebrow">ADMINISTRATION</p><h1>{section === "chat" ? "Chat Admin" : "Ringkasan"}</h1></div><div className="topbar-meta"><span className="live-pill"><i /> Firebase terhubung</span><div className="avatar">{(user.email || "A").slice(0, 1).toUpperCase()}</div></div></header>
      {section === "overview" ? <section className="content-area">
        <div className="welcome-banner"><div><p className="eyebrow light">OVERVIEW</p><h2>Halo, Admin 👋</h2><p>Pantau aktivitas absensi dan pesan karyawan dari satu tempat.</p></div><div className="banner-symbol">✳</div></div>
        <div className="section-heading"><div><h2>Ringkasan data</h2><p>Jumlah dokumen dari koleksi Firebase yang terhubung.</p></div><span className="subtle-tag">LIVE COUNTS</span></div>
        <div className="metric-grid"><Metric label="Akun pengguna" value={metrics.users} icon="♙" /><Metric label="Catatan absensi" value={metrics.attendance} icon="◷" /><Metric label="Pengajuan" value={metrics.pengajuan} icon="▤" /><Metric label="Ruang chat" value={metrics.chat} icon="☏" /></div>
        <div className="bottom-grid"><div className="info-card"><div className="card-heading"><h3>Chat terbaru</h3><button className="text-button" onClick={() => setSection("chat")}>Buka chat →</button></div>{rooms.slice(0, 4).map((room) => <button key={room.id} className="recent-room" onClick={() => { setSelectedRoomId(room.id); setSection("chat"); }}><div className="avatar soft">{room.staffName.slice(0, 1).toUpperCase()}</div><div className="room-summary"><strong>{room.staffName}</strong><span>{room.lastMessage || "Belum ada pesan"}</span></div><time>{formatTime(room.updatedAt)}</time></button>)}{rooms.length === 0 && <p className="empty-note">Belum ada percakapan masuk.</p>}</div>
          <div className="info-card health-card"><div className="health-icon">✓</div><h3>Kontrol akses aktif</h3><p>Akun yang masuk harus memiliki dokumen di <code>controlAdmins</code>. Pengguna tanpa penanda admin ditolak.</p><div className="health-row"><span>Autentikasi</span><strong>Firebase Auth</strong></div><div className="health-row"><span>Hak admin</span><strong>Allowlist Firestore</strong></div></div></div>
      </section> : <section className="chat-layout">
        <div className="chat-list-panel"><div className="chat-list-header"><div><h2>Pesan masuk</h2><p>{rooms.length} ruang percakapan</p></div><span className="round-icon">⌕</span></div>
          {rooms.map((room) => <button key={room.id} className={selectedRoomId === room.id ? "chat-room active" : "chat-room"} onClick={() => setSelectedRoomId(room.id)}><div className="avatar soft">{room.staffName.slice(0, 1).toUpperCase()}</div><div className="chat-room-body"><div className="room-title"><strong>{room.staffName}</strong><time>{formatTime(room.updatedAt)}</time></div><p>{room.lastMessage || "Belum ada pesan"}</p></div></button>)}
          {rooms.length === 0 && <p className="empty-note padded">Belum ada chat dari karyawan.</p>}
        </div>
        <div className="conversation-panel">
          {selectedRoom ? <><div className="conversation-header"><div className="avatar soft">{selectedRoom.staffName.slice(0, 1).toUpperCase()}</div><div><strong>{selectedRoom.staffName}</strong><span>ID karyawan: {selectedRoom.staffId}</span></div><span className="online-label"><i /> Ruang aktif</span></div>
            <div className="messages-area">{messages.map((message) => <div key={message.id} className={message.senderType === "ADMIN" ? "message-row outgoing" : "message-row"}><div className="message-bubble"><p>{message.message}</p><time>{formatTime(message.timestamp)}</time></div></div>)}{messages.length === 0 && <div className="empty-conversation"><div>☏</div><strong>Mulai percakapan</strong><p>Kirim balasan pertama untuk karyawan ini.</p></div>}</div>
            <form className="reply-form" onSubmit={sendReply}><input value={draft} onChange={(event) => setDraft(event.target.value)} placeholder="Tulis balasan untuk karyawan..." maxLength={4000} aria-label="Tulis balasan" /><button className="send-button" type="submit" disabled={!draft.trim() || sending}>{sending ? "..." : "Kirim ↗"}</button></form>
          </> : <div className="empty-conversation"><div>☏</div><strong>Pilih percakapan</strong><p>Pilih ruang chat di sebelah kiri untuk melihat pesan.</p></div>}
          {chatError && <p className="error-text chat-error" role="alert">{chatError}</p>}
        </div>
      </section>}
    </main>
  </div>;
}

function Metric({ label, value, icon }) {
  return <article className="metric-card"><div className="metric-top"><span>{label}</span><div>{icon}</div></div><strong>{value}</strong><span className="metric-foot"><i /> Data Firestore</span></article>;
}
