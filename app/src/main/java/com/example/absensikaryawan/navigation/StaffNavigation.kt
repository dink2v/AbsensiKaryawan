package com.example.absensikaryawan.navigation

import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import com.example.absensikaryawan.screens.AbsenLuarKantorScreen
import com.example.absensikaryawan.screens.BantuanScreen
import com.example.absensikaryawan.screens.BottomNavigationBar
import com.example.absensikaryawan.screens.ChatAdminScreen
import com.example.absensikaryawan.screens.DetailPengajuanScreen
import com.example.absensikaryawan.screens.NotifikasiScreen
import com.example.absensikaryawan.screens.PengajuanBaruScreen
import com.example.absensikaryawan.screens.PengajuanScreen
import com.example.absensikaryawan.screens.ProfileScreen
import com.example.absensikaryawan.screens.RiwayatJamPulangScreen
import com.example.absensikaryawan.screens.RiwayatScreen
import com.example.absensikaryawan.screens.ScanAbsenScreen
import com.example.absensikaryawan.screens.SettingsScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen
import com.example.absensikaryawan.screens.TampilanScreen
import com.example.absensikaryawan.screens.TentangAplikasiScreen
import com.example.absensikaryawan.screens.ThemeMode

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================================
// STAFF NAVIGATION
// ==========================================================

@Composable
fun StaffNavigation(
    onLogout: () -> Unit
) {

    // ======================================================
    // FIREBASE
    // ======================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    val coroutineScope = rememberCoroutineScope()

    // ======================================================
    // CURRENT SCREEN
    // ======================================================

    var currentScreen by remember {
        mutableStateOf(StaffScreen.Dashboard)
    }

    // ======================================================
    // BOTTOM NAVIGATION
    // ======================================================

    var selectedBottomItem by remember {
        mutableIntStateOf(0)
    }

    // ======================================================
    // REFRESH DASHBOARD / RIWAYAT
    // ======================================================

    var refreshKey by remember {
        mutableIntStateOf(0)
    }

    // ======================================================
    // DATA PENGAJUAN YANG DIPILIH
    // ======================================================

    var selectedPengajuan by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    // ======================================================
    // SCREEN ASAL DETAIL PENGAJUAN
    //
    // Supaya tombol kembali dari Detail tetap kembali ke
    // halaman asal:
    //
    // Pengajuan -> Detail -> Pengajuan
    // Riwayat  -> Detail -> Riwayat
    // ======================================================

    var detailReturnScreen by remember {
        mutableStateOf(StaffScreen.Pengajuan)
    }

    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        bottomBar = {

            if (
                currentScreen == StaffScreen.Dashboard ||
                currentScreen == StaffScreen.Pengajuan ||
                currentScreen == StaffScreen.Scan ||
                currentScreen == StaffScreen.Riwayat ||
                currentScreen == StaffScreen.Settings
            ) {

                BottomNavigationBar(

                    selectedItem =
                        selectedBottomItem,

                    onItemSelected = { index ->

                        Log.d(
                            "STAFF_NAV",
                            "BOTTOM ITEM = $index"
                        )

                        selectedBottomItem = index

                        currentScreen =
                            when (index) {

                                0 ->
                                    StaffScreen.Dashboard

                                1 ->
                                    StaffScreen.Pengajuan

                                2 ->
                                    StaffScreen.Scan

                                3 ->
                                    StaffScreen.Riwayat

                                4 ->
                                    StaffScreen.Settings

                                else ->
                                    StaffScreen.Dashboard
                            }
                    }
                )
            }
        }

    ) { paddingValues ->

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

        ) {

            when (currentScreen) {

                // ==================================================
                // DASHBOARD
                // ==================================================

                StaffScreen.Dashboard -> {

                    StaffDashboardScreen(

                        refreshKey =
                            refreshKey,

                        onScan = {

                            selectedBottomItem = 2

                            currentScreen =
                                StaffScreen.Scan
                        },

                        onAbsenLuarKantor = {

                            currentScreen =
                                StaffScreen.AbsenLuarKantor
                        },

                        onProfile = {

                            currentScreen =
                                StaffScreen.Profile
                        },

                        onHistory = {

                            selectedBottomItem = 3

                            currentScreen =
                                StaffScreen.Riwayat
                        },

                        onHistoryPulang = {

                            currentScreen =
                                StaffScreen.RiwayatJamPulang
                        },

                        onSettings = {

                            selectedBottomItem = 4

                            currentScreen =
                                StaffScreen.Settings
                        },

                        onPengajuan = {

                            selectedBottomItem = 1

                            currentScreen =
                                StaffScreen.Pengajuan
                        },

                        onLogout = {

                            onLogout()
                        },

                        onNotification = {

                            currentScreen =
                                StaffScreen.Notifikasi
                        }
                    )
                }

                // ==================================================
                // SCAN QR
                // ==================================================

                StaffScreen.Scan -> {

                    ScanAbsenScreen(

                        onBack = {

                            selectedBottomItem = 0

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onQrScanned = { qrData, catatan ->

                            coroutineScope.launch {

                                try {

                                    Log.d(
                                        "STAFF_NAV",
                                        "================================"
                                    )

                                    Log.d(
                                        "STAFF_NAV",
                                        "QR ABSEN DITERIMA"
                                    )

                                    Log.d(
                                        "STAFF_NAV",
                                        "QR DATA = $qrData"
                                    )

                                    Log.d(
                                        "STAFF_NAV",
                                        "CATATAN = $catatan"
                                    )

                                    // ==================================
                                    // USER LOGIN
                                    // ==================================

                                    val currentUser =
                                        auth.currentUser

                                    if (currentUser == null) {

                                        Log.e(
                                            "STAFF_NAV",
                                            "USER BELUM LOGIN"
                                        )

                                        return@launch
                                    }

                                    val uid =
                                        currentUser.uid

                                    // ==================================
                                    // TANGGAL HARI INI
                                    // ==================================

                                    val tanggalHariIni =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(Date())

                                    // ==================================
                                    // JAM SEKARANG
                                    // ==================================

                                    val jamSekarang =
                                        SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(Date())

                                    Log.d(
                                        "STAFF_NAV",
                                        "UID = $uid"
                                    )

                                    Log.d(
                                        "STAFF_NAV",
                                        "TANGGAL = $tanggalHariIni"
                                    )

                                    Log.d(
                                        "STAFF_NAV",
                                        "JAM = $jamSekarang"
                                    )

                                    // ==================================
                                    // CARI ABSENSI HARI INI
                                    // ==================================

                                    val attendanceSnapshot =
                                        db.collection("attendance")
                                            .whereEqualTo(
                                                "uid",
                                                uid
                                            )
                                            .whereEqualTo(
                                                "tanggal",
                                                tanggalHariIni
                                            )
                                            .limit(1)
                                            .get()
                                            .await()

                                    // ==================================
                                    // BELUM ADA ABSENSI
                                    // = ABSEN MASUK
                                    // ==================================

                                    if (attendanceSnapshot.isEmpty) {

                                        Log.d(
                                            "STAFF_NAV",
                                            "BELUM ADA ABSENSI HARI INI"
                                        )

                                        val namaUser =
                                            currentUser
                                                .displayName
                                                ?.takeIf {
                                                    it.isNotBlank()
                                                }
                                                ?: currentUser
                                                    .email
                                                    ?.substringBefore("@")
                                                ?: "Staff"

                                        val attendanceData =
                                            hashMapOf<String, Any>(

                                                "uid" to uid,

                                                "nama" to namaUser,

                                                "tanggal" to
                                                        tanggalHariIni,

                                                "jamMasuk" to
                                                        jamSekarang,

                                                "jamPulang" to "",

                                                "qrData" to
                                                        qrData,

                                                "catatan" to
                                                        catatan
                                            )

                                        db.collection("attendance")
                                            .add(attendanceData)
                                            .await()

                                        Log.d(
                                            "STAFF_NAV",
                                            "================================"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "ABSEN MASUK BERHASIL"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "JAM MASUK = $jamSekarang"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "================================"
                                        )

                                    } else {

                                        // ==================================
                                        // ABSENSI SUDAH ADA
                                        // ==================================

                                        val document =
                                            attendanceSnapshot
                                                .documents
                                                .first()

                                        val jamMasuk =
                                            document
                                                .getString(
                                                    "jamMasuk"
                                                )
                                                ?: ""

                                        val jamPulang =
                                            document
                                                .getString(
                                                    "jamPulang"
                                                )
                                                ?: ""

                                        Log.d(
                                            "STAFF_NAV",
                                            "ABSENSI DITEMUKAN"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "DOCUMENT ID = ${document.id}"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "JAM MASUK = $jamMasuk"
                                        )

                                        Log.d(
                                            "STAFF_NAV",
                                            "JAM PULANG = $jamPulang"
                                        )

                                        // ==================================
                                        // ABSEN PULANG
                                        // ==================================

                                        if (
                                            jamMasuk.isNotBlank() &&
                                            jamPulang.isBlank()
                                        ) {

                                            Log.d(
                                                "STAFF_NAV",
                                                "ABSEN PULANG DIPROSES"
                                            )

                                            val updateData =
                                                hashMapOf<String, Any>(

                                                    "jamPulang" to
                                                            jamSekarang,

                                                    "qrDataPulang" to
                                                            qrData,

                                                    "catatanPulang" to
                                                            catatan
                                                )

                                            document.reference
                                                .update(
                                                    updateData
                                                )
                                                .await()

                                            Log.d(
                                                "STAFF_NAV",
                                                "================================"
                                            )

                                            Log.d(
                                                "STAFF_NAV",
                                                "ABSEN PULANG BERHASIL"
                                            )

                                            Log.d(
                                                "STAFF_NAV",
                                                "JAM PULANG = $jamSekarang"
                                            )

                                            Log.d(
                                                "STAFF_NAV",
                                                "DOCUMENT ID = ${document.id}"
                                            )

                                            Log.d(
                                                "STAFF_NAV",
                                                "================================"
                                            )

                                        }

                                        // ==================================
                                        // ABSENSI SUDAH LENGKAP
                                        // ==================================

                                        else if (
                                            jamMasuk.isNotBlank() &&
                                            jamPulang.isNotBlank()
                                        ) {

                                            Log.w(
                                                "STAFF_NAV",
                                                "ABSEN HARI INI SUDAH LENGKAP"
                                            )

                                            Log.w(
                                                "STAFF_NAV",
                                                "JAM MASUK = $jamMasuk"
                                            )

                                            Log.w(
                                                "STAFF_NAV",
                                                "JAM PULANG = $jamPulang"
                                            )
                                        }

                                        // ==================================
                                        // JAM MASUK KOSONG
                                        // ==================================

                                        else if (
                                            jamMasuk.isBlank()
                                        ) {

                                            Log.d(
                                                "STAFF_NAV",
                                                "JAM MASUK KOSONG"
                                            )

                                            document.reference
                                                .update(

                                                    mapOf(

                                                        "jamMasuk" to
                                                                jamSekarang,

                                                        "qrData" to
                                                                qrData,

                                                        "catatan" to
                                                                catatan
                                                    )
                                                )
                                                .await()

                                            Log.d(
                                                "STAFF_NAV",
                                                "ABSEN MASUK BERHASIL MELALUI UPDATE"
                                            )
                                        }
                                    }

                                    // ==================================
                                    // REFRESH DATA
                                    // ==================================

                                    refreshKey++

                                    // ==================================
                                    // KEMBALI KE DASHBOARD
                                    // ==================================

                                    selectedBottomItem = 0

                                    currentScreen =
                                        StaffScreen.Dashboard

                                    Log.d(
                                        "STAFF_NAV",
                                        "KEMBALI KE DASHBOARD"
                                    )

                                } catch (e: Exception) {

                                    Log.e(
                                        "STAFF_NAV",
                                        "GAGAL MEMPROSES ABSEN",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }

                // ==================================================
                // ABSEN LUAR KANTOR
                // ==================================================

                StaffScreen.AbsenLuarKantor -> {

                    AbsenLuarKantorScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onKirim = { _, _ ->

                            currentScreen =
                                StaffScreen.Dashboard
                        }
                    )
                }

                // ==================================================
                // PENGAJUAN
                // ==================================================

                StaffScreen.Pengajuan -> {

                    PengajuanScreen(

                        onBack = {

                            selectedBottomItem = 0

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onPengajuanBaru = {

                            currentScreen =
                                StaffScreen.PengajuanBaru
                        },

                        onStatusClick = { pengajuan ->

                            selectedPengajuan =
                                pengajuan

                            detailReturnScreen =
                                StaffScreen.Pengajuan

                            currentScreen =
                                StaffScreen.DetailPengajuan
                        }
                    )
                }

                // ==================================================
                // PENGAJUAN BARU
                // ==================================================

                StaffScreen.PengajuanBaru -> {

                    PengajuanBaruScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Pengajuan
                        },

                        onSubmit = {
                                _,
                                _,
                                _,
                                _,
                                _,
                                _,
                                _ ->

                            refreshKey++

                            currentScreen =
                                StaffScreen.Pengajuan
                        }
                    )
                }

                // ==================================================
                // DETAIL PENGAJUAN
                // ==================================================

                StaffScreen.DetailPengajuan -> {

                    val data =
                        selectedPengajuan

                    if (data != null) {

                        DetailPengajuanScreen(

                            jenis =
                                data["jenis"] as? String
                                    ?: "",

                            tanggal =
                                data["tanggal"] as? String
                                    ?: "",

                            status =
                                data["status"] as? String
                                    ?: "",

                            jamPulang =
                                data["jamPulang"] as? String
                                    ?: "",

                            jamKeluar =
                                data["jamKeluar"] as? String
                                    ?: "",

                            jamKembali =
                                data["jamKembali"] as? String
                                    ?: "",

                            tanggalMulai =
                                data["tanggalMulai"] as? String
                                    ?: "",

                            tanggalSelesai =
                                data["tanggalSelesai"] as? String
                                    ?: "",

                            alasan =
                                data["alasan"] as? String
                                    ?: "",

                            catatanAdmin =
                                data["catatanAdmin"] as? String
                                    ?: "",

                            onBack = {

                                currentScreen =
                                    detailReturnScreen
                            }
                        )
                    }
                }

                // ==================================================
                // RIWAYAT
                // ==================================================

                StaffScreen.Riwayat -> {

                    RiwayatScreen(

                        refreshKey =
                            refreshKey,

                        onBack = {

                            selectedBottomItem = 0

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onDetailClick = { pengajuan ->

                            selectedPengajuan =
                                mapOf(

                                    "documentId" to
                                            pengajuan.documentId,

                                    "jenis" to
                                            pengajuan.jenis,

                                    "tanggal" to
                                            pengajuan.tanggalMulai,

                                    "status" to
                                            pengajuan.status,

                                    "jamPulang" to
                                            pengajuan.jamPulang,

                                    "jamKeluar" to
                                            pengajuan.jamKeluar,

                                    "jamKembali" to
                                            pengajuan.jamKembali,

                                    "tanggalMulai" to
                                            pengajuan.tanggalMulai,

                                    "tanggalSelesai" to
                                            pengajuan.tanggalSelesai,

                                    "alasan" to
                                            pengajuan.alasan,

                                    "catatanAdmin" to
                                            pengajuan.catatanAdmin
                                )

                            detailReturnScreen =
                                StaffScreen.Riwayat

                            currentScreen =
                                StaffScreen.DetailPengajuan
                        }
                    )
                }

                // ==================================================
                // RIWAYAT JAM PULANG
                // ==================================================

                StaffScreen.RiwayatJamPulang -> {

                    RiwayatJamPulangScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard
                        }
                    )
                }

                // ==================================================
                // PROFILE
                // ==================================================

                StaffScreen.Profile -> {

                    ProfileScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard
                        }
                    )
                }

                // ==================================================
                // NOTIFIKASI
                // ==================================================

                StaffScreen.Notifikasi -> {

                    NotifikasiScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard
                        }
                    )
                }

                // ==================================================
                // SETTINGS
                // ==================================================

                StaffScreen.Settings -> {

                    SettingsScreen(

                        onBack = {

                            selectedBottomItem = 0

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onNotification = {

                            currentScreen =
                                StaffScreen.Notifikasi
                        },

                        onTampilan = {

                            currentScreen =
                                StaffScreen.Tampilan
                        },

                        onChatAdmin = {

                            currentScreen =
                                StaffScreen.ChatAdmin
                        },

                        onBantuan = {

                            currentScreen =
                                StaffScreen.Bantuan
                        },

                        onTentangAplikasi = {

                            currentScreen =
                                StaffScreen.TentangAplikasi
                        },

                        onLogout = {

                            onLogout()
                        }
                    )
                }

                // ==================================================
                // TAMPILAN
                // ==================================================

                StaffScreen.Tampilan -> {

                    TampilanScreen(

                        selectedMode =
                            ThemeMode.TERANG,

                        onModeSelected = {

                            // ThemeDataStore disambungkan
                            // setelah navigation selesai.
                        },

                        onBack = {

                            currentScreen =
                                StaffScreen.Settings
                        }
                    )
                }

                // ==================================================
                // CHAT ADMIN
                // ==================================================

                StaffScreen.ChatAdmin -> {

                    ChatAdminScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Settings
                        }
                    )
                }

                // ==================================================
                // BANTUAN
                // ==================================================

                StaffScreen.Bantuan -> {

                    BantuanScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Settings
                        },

                        onChatAdmin = {

                            currentScreen =
                                StaffScreen.ChatAdmin
                        }
                    )
                }

                // ==================================================
                // TENTANG APLIKASI
                // ==================================================

                StaffScreen.TentangAplikasi -> {

                    TentangAplikasiScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Settings
                        }
                    )
                }
            }
        }
    }
}

// ==========================================================
// STAFF SCREEN
// ==========================================================

private enum class StaffScreen {

    Dashboard,

    Pengajuan,

    Scan,

    Riwayat,

    Settings,

    AbsenLuarKantor,

    RiwayatJamPulang,

    PengajuanBaru,

    DetailPengajuan,

    Profile,

    Notifikasi,

    Tampilan,

    ChatAdmin,

    Bantuan,

    TentangAplikasi
}