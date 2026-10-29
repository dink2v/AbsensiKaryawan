package com.example.absensikaryawan.navigation

import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import com.example.absensikaryawan.data.PengajuanData

import com.example.absensikaryawan.repository.screens.AbsenLuarKantorScreen
import com.example.absensikaryawan.repository.screens.ApprovalScreen
import com.example.absensikaryawan.repository.screens.BantuanScreen
import com.example.absensikaryawan.repository.screens.BottomNavigationBar
import com.example.absensikaryawan.repository.screens.ChatAdminScreen
import com.example.absensikaryawan.repository.screens.DetailPengajuanScreen
import com.example.absensikaryawan.repository.screens.FilterStatusPengajuan
import com.example.absensikaryawan.repository.screens.NotifikasiScreen
import com.example.absensikaryawan.repository.screens.PengajuanBaruScreen
import com.example.absensikaryawan.repository.screens.PengajuanScreen
import com.example.absensikaryawan.repository.screens.ProfileScreen
import com.example.absensikaryawan.repository.screens.RiwayatJamPulangScreen
import com.example.absensikaryawan.repository.screens.RiwayatScreen
import com.example.absensikaryawan.repository.screens.ScanAbsenScreen
import com.example.absensikaryawan.repository.screens.SettingsScreen
import com.example.absensikaryawan.repository.screens.StaffDashboardScreen
import com.example.absensikaryawan.screens.TampilanScreen
import com.example.absensikaryawan.repository.screens.TentangAplikasiScreen
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
    // CEK APPROVER
    // Supervisor / Manager / HRD / Owner
    // ======================================================

    var isApprover by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(auth.currentUser?.uid) {

        val uid =
            auth.currentUser?.uid

        if (uid.isNullOrBlank()) {

            isApprover = false

            return@LaunchedEffect
        }

        try {

            // Role admin web dibaca dari koleksi controlAdmins pada Firebase
            // yang sama. Setiap admin harus punya dokumen dengan ID UID-nya.
            val controlAdmin =
                db.collection("controlAdmins")
                    .document(uid)
                    .get()
                    .await()

            val adminRole =
                controlAdmin.getString("role")
                    ?.trim()
                    ?.uppercase()
                    .orEmpty()

            val isControlAdmin =
                controlAdmin.exists() &&
                    controlAdmin.getBoolean("active") == true &&
                    (adminRole == "ADMIN" || adminRole == "OWNER")

            // Profil utama memakai UID Firebase sebagai document ID (users/{uid}),
            // sama dengan UID yang dipakai Authentication dan controlAdmins/{uid}.
            // Fallback query menjaga kompatibilitas dengan data lama yang ID dokumennya berbeda.
            val usersRef = db.collection("users")
            val directUser = usersRef.document(uid).get().await()
            val document = if (directUser.exists()) {
                directUser
            } else {
                usersRef.whereEqualTo("uid", uid).limit(1).get().await().documents.firstOrNull()
            }

            val jabatan =
                document?.getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    .orEmpty()

            val userRole =
                document?.getString("role")
                    ?.trim()
                    ?.uppercase()
                    .orEmpty()

            isApprover =
                isControlAdmin ||
                    userRole == "ADMIN" ||
                    userRole == "OWNER" ||
                    jabatan == "ADMIN" ||
                    jabatan == "SUPERVISOR" ||
                    jabatan == "MANAGER" ||
                    jabatan == "HRD" ||
                    jabatan == "OWNER"

            Log.d(
                "STAFF_NAV",
                "CEK ROLE → uid=$uid | role=$adminRole/$userRole | jabatan=$jabatan | isApprover=$isApprover"
            )

        } catch (e: Exception) {

            Log.e(
                "STAFF_NAV",
                "GAGAL CEK JABATAN USER",
                e
            )

            isApprover = false
        }
    }


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
        mutableStateOf<PengajuanData?>(null)
    }


    // ======================================================
    // SCREEN ASAL DETAIL PENGAJUAN
    // ======================================================

    var detailReturnScreen by remember {
        mutableStateOf(StaffScreen.Pengajuan)
    }


    // ======================================================
    // TARGET NOTIFIKASI
    // ======================================================

    var notificationTarget by remember {
        mutableStateOf(NotificationTarget.NONE)
    }


    // ======================================================
    // ID PENGAJUAN DARI NOTIFIKASI
    // ======================================================

    var notificationRelatedId by remember {
        mutableStateOf("")
    }


    // ======================================================
    // PESAN ABSENSI
    // ======================================================

    var attendanceMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ======================================================
    // RESET SCANNER QR
    // ======================================================

    var scanResetKey by remember {
        mutableIntStateOf(0)
    }


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        bottomBar = {

            if (
                currentScreen == StaffScreen.Dashboard ||
                currentScreen == StaffScreen.Pengajuan ||
                currentScreen == StaffScreen.Approval ||
                currentScreen == StaffScreen.Scan ||
                currentScreen == StaffScreen.Riwayat ||
                currentScreen == StaffScreen.Settings
            ) {

                _root_ide_package_.com.example.absensikaryawan.repository.screens.BottomNavigationBar(

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

                                1 -> {

                                    if (isApprover) {

                                        StaffScreen.Approval

                                    } else {

                                        StaffScreen.Pengajuan
                                    }
                                }

                                2 ->
                                    StaffScreen.Scan

                                3 ->
                                    StaffScreen.Riwayat

                                4 ->
                                    StaffScreen.Settings

                                else ->
                                    StaffScreen.Dashboard
                            }

                        notificationTarget =
                            NotificationTarget.NONE
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

            // ==================================================
            // SCREEN NAVIGATION
            // ==================================================

            when (currentScreen) {

                // ==================================================
                // DASHBOARD
                // ==================================================

                StaffScreen.Dashboard -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.StaffDashboardScreen(

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

                            notificationTarget =
                                NotificationTarget.RIWAYAT_ABSENSI

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
                                if (isApprover) {

                                    StaffScreen.Approval

                                } else {

                                    StaffScreen.Pengajuan
                                }
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
                // APPROVAL
                // ==================================================

                StaffScreen.Approval -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ApprovalScreen(

                        initialDocumentId =
                            notificationRelatedId,

                        onDetailClick = { pengajuan ->

                            Log.d(
                                "STAFF_NAV",
                                "DETAIL APPROVAL: ${pengajuan.id}"
                            )

                            selectedPengajuan =
                                pengajuan

                            // Setelah pengajuan dari notifikasi
                            // berhasil ditemukan dan dibuka,
                            // kosongkan ID agar tidak terbuka
                            // kembali secara otomatis.
                            notificationRelatedId = ""

                            detailReturnScreen =
                                StaffScreen.Approval

                            currentScreen =
                                StaffScreen.DetailPengajuan
                        }
                    )
                }


                // ==================================================
                // SCAN QR
                // ==================================================

                StaffScreen.Scan -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ScanAbsenScreen(

                        externalResetKey =
                            scanResetKey,

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard

                            selectedBottomItem = 0
                        },

                        onQrScanned = { qrData,
                                        catatan,
                                        kantor ->

                            coroutineScope.launch {

                                try {

                                    // ==========================================
                                    // CEK LOGIN
                                    // ==========================================

                                    val currentUser =
                                        auth.currentUser

                                    if (currentUser == null) {

                                        Log.e(
                                            "STAFF_NAV",
                                            "User belum login"
                                        )

                                        attendanceMessage =
                                            "Sesi login tidak ditemukan.\n\n" +
                                                    "Silakan login kembali."

                                        return@launch
                                    }

                                    val uid =
                                        currentUser.uid


                                    // ==========================================
                                    // WAKTU SEKARANG
                                    // ==========================================

                                    val now =
                                        Date()

                                    val tanggalHariIni =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(now)

                                    val jamSekarang =
                                        SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(now)


                                    // ==========================================
                                    // PARSING JAM
                                    // ==========================================

                                    val jamMenitSekarang =
                                        SimpleDateFormat(
                                            "HH:mm",
                                            Locale.getDefault()
                                        ).format(now)

                                    val jamSekarangMenit =
                                        jamMenitSekarang
                                            .substring(0, 2)
                                            .toInt()

                                    val menitSekarang =
                                        jamMenitSekarang
                                            .substring(3, 5)
                                            .toInt()

                                    val totalMenitSekarang =
                                        (jamSekarangMenit * 60) +
                                                menitSekarang


                                    // ==========================================
                                    // BATAS WAKTU ABSENSI
                                    // ==========================================

                                    val waktuMasukMulai =
                                        6 * 60

                                    val waktuPulangMulai =
                                        16 * 60

                                    val waktuPulangSelesai =
                                        21 * 60


                                    // ==========================================
                                    // IDENTITAS USER
                                    // ==========================================

                                    val namaUser =
                                        currentUser.displayName
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: currentUser.email
                                                ?.substringBefore("@")
                                                ?.takeIf {
                                                    it.isNotBlank()
                                                }
                                            ?: "Staff"


                                    Log.d(
                                        "STAFF_NAV",
                                        "QR SCAN → " +
                                                "qrData=$qrData | " +
                                                "kantor=$kantor | " +
                                                "tanggal=$tanggalHariIni | " +
                                                "jam=$jamSekarang"
                                    )


                                    // ==========================================
                                    // AMBIL DATA ABSENSI HARI INI
                                    // ==========================================

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


                                    // ==================================================
                                    // BELUM ADA DATA ABSENSI
                                    // ==================================================

                                    if (
                                        attendanceSnapshot.isEmpty
                                    ) {

                                        if (
                                            totalMenitSekarang <
                                            waktuMasukMulai
                                        ) {

                                            Log.w(
                                                "STAFF_NAV",
                                                "ABSEN DITOLAK → Belum masuk jam absensi"
                                            )

                                            attendanceMessage =
                                                "Absen masuk belum dibuka.\n\n" +
                                                        "Silakan melakukan absen mulai pukul 06.00."

                                            scanResetKey++

                                            return@launch
                                        }


                                        if (
                                            totalMenitSekarang >=
                                            waktuPulangMulai
                                        ) {

                                            Log.w(
                                                "STAFF_NAV",
                                                "ABSEN DITOLAK → Belum melakukan absen masuk"
                                            )

                                            attendanceMessage =
                                                "Anda belum melakukan absen masuk hari ini.\n\n" +
                                                        "Absen masuk hanya dapat dilakukan sebelum pukul 16.00."

                                            return@launch
                                        }


                                        val attendanceData =
                                            hashMapOf<String, Any>(

                                                "uid" to
                                                        uid,

                                                "nama" to
                                                        namaUser,

                                                "tanggal" to
                                                        tanggalHariIni,

                                                "jamMasuk" to
                                                        jamSekarang,

                                                "jamPulang" to
                                                        "",

                                                "status" to
                                                        "Hadir",

                                                "qrData" to
                                                        qrData,

                                                "kantor" to
                                                        kantor,

                                                "catatan" to
                                                        catatan
                                            )


                                        db.collection(
                                            "attendance"
                                        )
                                            .add(
                                                attendanceData
                                            )
                                            .await()


                                        Log.d(
                                            "STAFF_NAV",
                                            "ABSEN MASUK BERHASIL → " +
                                                    "$kantor | $jamSekarang"
                                        )

                                    } else {

                                        val document =
                                            attendanceSnapshot
                                                .documents
                                                .first()

                                        val jamMasuk =
                                            document.getString(
                                                "jamMasuk"
                                            ) ?: ""

                                        val jamPulang =
                                            document.getString(
                                                "jamPulang"
                                            ) ?: ""


                                        if (
                                            jamMasuk.isNotBlank() &&
                                            jamPulang.isBlank()
                                        ) {

                                            if (
                                                totalMenitSekarang <
                                                waktuPulangMulai
                                            ) {

                                                Log.w(
                                                    "STAFF_NAV",
                                                    "ABSEN PULANG DITOLAK → Belum masuk jam pulang"
                                                )

                                                attendanceMessage =
                                                    "Absen pulang belum dibuka.\n\n" +
                                                            "Absen pulang dapat dilakukan mulai pukul 16.00."

                                                return@launch
                                            }


                                            if (
                                                totalMenitSekarang >
                                                waktuPulangSelesai
                                            ) {

                                                Log.w(
                                                    "STAFF_NAV",
                                                    "ABSEN PULANG DITOLAK → Sudah melewati jam pulang"
                                                )

                                                attendanceMessage =
                                                    "Anda sudah melakukan absen masuk dan pulang hari ini."

                                                scanResetKey++

                                                return@launch
                                            }


                                            val updateData =
                                                hashMapOf<String, Any>(

                                                    "jamPulang" to
                                                            jamSekarang,

                                                    "qrDataPulang" to
                                                            qrData,

                                                    "kantorPulang" to
                                                            kantor,

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
                                                "ABSEN PULANG BERHASIL → " +
                                                        "$kantor | $jamSekarang"
                                            )

                                        } else if (
                                            jamMasuk.isNotBlank() &&
                                            jamPulang.isNotBlank()
                                        ) {

                                            Log.w(
                                                "STAFF_NAV",
                                                "USER SUDAH ABSEN MASUK DAN PULANG HARI INI"
                                            )

                                            attendanceMessage =
                                                "Anda sudah melakukan absen masuk dan pulang hari ini."

                                            return@launch

                                        } else if (
                                            jamMasuk.isBlank()
                                        ) {

                                            if (
                                                totalMenitSekarang <
                                                waktuMasukMulai
                                            ) {

                                                Log.w(
                                                    "STAFF_NAV",
                                                    "ABSEN MASUK DITOLAK → Belum masuk jam absensi"
                                                )

                                                attendanceMessage =
                                                    "Absen masuk belum dibuka.\n\n" +
                                                            "Silakan melakukan absen mulai pukul 06.00."

                                                return@launch
                                            }


                                            if (
                                                totalMenitSekarang >=
                                                waktuPulangMulai
                                            ) {

                                                Log.w(
                                                    "STAFF_NAV",
                                                    "ABSEN MASUK DITOLAK → Sudah masuk waktu pulang"
                                                )

                                                attendanceMessage =
                                                    "Absen pulang belum dibuka.\n\n" +
                                                            "Absen pulang dapat dilakukan mulai pukul 16.00."

                                                scanResetKey++

                                                return@launch
                                            }


                                            val updateData =
                                                hashMapOf<String, Any>(

                                                    "jamMasuk" to
                                                            jamSekarang,

                                                    "status" to
                                                            "Hadir",

                                                    "qrData" to
                                                            qrData,

                                                    "kantor" to
                                                            kantor,

                                                    "catatan" to
                                                            catatan
                                                )


                                            document.reference
                                                .update(
                                                    updateData
                                                )
                                                .await()


                                            Log.d(
                                                "STAFF_NAV",
                                                "JAM MASUK DIUPDATE → " +
                                                        "$kantor | $jamSekarang"
                                            )
                                        }
                                    }


                                    // ==========================================
                                    // REFRESH UI
                                    // ==========================================

                                    refreshKey++

                                    selectedBottomItem = 0

                                    currentScreen =
                                        StaffScreen.Dashboard

                                } catch (e: Exception) {

                                    Log.e(
                                        "STAFF_NAV",
                                        "GAGAL MEMPROSES ABSEN",
                                        e
                                    )

                                    attendanceMessage =
                                        "Terjadi kesalahan saat memproses absensi.\n\n" +
                                                "Silakan coba lagi."

                                    scanResetKey++
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // ABSEN LUAR KANTOR
                // ==================================================

                StaffScreen.AbsenLuarKantor -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.AbsenLuarKantorScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.PengajuanScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.PengajuanBaruScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Pengajuan
                        },

                        onSubmit = { _,
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

                        _root_ide_package_.com.example.absensikaryawan.repository.screens.DetailPengajuanScreen(

                            documentId =
                                data.id,

                            jenis =
                                data.jenis,

                            tanggal =
                                data.tanggal,

                            status =
                                data.status,

                            jamPulang =
                                data.jamPulang,

                            jamKeluar =
                                data.jamKeluar,

                            jamKembali =
                                data.jamKembali,

                            tanggalMulai =
                                data.tanggalMulai,

                            tanggalSelesai =
                                data.tanggalSelesai,

                            alasan =
                                data.alasan,

                            approvalChain =
                                data.approvalChain,

                            approvalStatuses =
                                data.approvalStatuses,

                            currentApproverUid =
                                data.currentApproverUid,

                            currentApproverName =
                                data.currentApproverName,

                            currentApproverJabatan =
                                data.currentApproverJabatan,

                            approvalLocked =
                                data.approvalLocked,

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

                    val filterAwal =
                        when (notificationTarget) {

                            NotificationTarget.PENGAJUAN_DISETUJUI ->
                                _root_ide_package_.com.example.absensikaryawan.repository.screens.FilterStatusPengajuan.DISETUJUI

                            NotificationTarget.PENGAJUAN_DITOLAK ->
                                _root_ide_package_.com.example.absensikaryawan.repository.screens.FilterStatusPengajuan.DITOLAK

                            NotificationTarget.PENGAJUAN_MENUNGGU ->
                                _root_ide_package_.com.example.absensikaryawan.repository.screens.FilterStatusPengajuan.MENUNGGU

                            else ->
                                _root_ide_package_.com.example.absensikaryawan.repository.screens.FilterStatusPengajuan.SEMUA
                        }


                    _root_ide_package_.com.example.absensikaryawan.repository.screens.RiwayatScreen(

                        refreshKey =
                            refreshKey,

                        filterStatusAwal =
                            filterAwal,

                        onBack = {

                            selectedBottomItem = 0

                            notificationTarget =
                                NotificationTarget.NONE

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onDetailClick = { pengajuan ->

                            selectedPengajuan =
                                PengajuanData(

                                    id =
                                        pengajuan.id,

                                    nama =
                                        pengajuan.namaPemohon,

                                    jenis =
                                        pengajuan.jenis,

                                    tanggal =
                                        pengajuan.tanggalMulai,

                                    jamPulang =
                                        "",

                                    jamKeluar =
                                        "",

                                    jamKembali =
                                        "",

                                    tanggalMulai =
                                        pengajuan.tanggalMulai,

                                    tanggalSelesai =
                                        pengajuan.tanggalSelesai,

                                    alasan =
                                        pengajuan.alasan,

                                    status =
                                        pengajuan.status
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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.RiwayatJamPulangScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ProfileScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.NotifikasiScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Dashboard
                        },

                        onNotificationClick = { target,
                                                relatedId ->

                            Log.d(
                                "STAFF_NAV",
                                "NOTIFIKASI DIKLIK = " +
                                        "$target | relatedId=$relatedId"
                            )

                            notificationTarget =
                                target

                            when (target) {

                                NotificationTarget.RIWAYAT_ABSENSI -> {

                                    selectedBottomItem = 3

                                    currentScreen =
                                        StaffScreen.Riwayat
                                }

                                NotificationTarget.RIWAYAT_PENGAJUAN -> {

                                    selectedBottomItem = 3

                                    currentScreen =
                                        StaffScreen.Riwayat
                                }

                                NotificationTarget.PENGAJUAN_DISETUJUI -> {

                                    selectedBottomItem = 3

                                    currentScreen =
                                        StaffScreen.Riwayat
                                }

                                NotificationTarget.PENGAJUAN_DITOLAK -> {

                                    selectedBottomItem = 3

                                    currentScreen =
                                        StaffScreen.Riwayat
                                }

                                NotificationTarget.PENGAJUAN_MENUNGGU -> {

                                    // ==========================================
                                    // APPROVAL DARI NOTIFIKASI
                                    // ==========================================

                                    selectedBottomItem = 1

                                    notificationRelatedId =
                                        relatedId

                                    currentScreen =
                                        StaffScreen.Approval
                                }

                                NotificationTarget.CHAT_ADMIN -> {

                                    currentScreen =
                                        StaffScreen.ChatAdmin
                                }

                                NotificationTarget.NONE -> {

                                    // Tidak melakukan apa-apa.
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // SETTINGS
                // ==================================================

                StaffScreen.Settings -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.SettingsScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.screens.TampilanScreen(

                        selectedMode =
                            _root_ide_package_.com.example.absensikaryawan.screens.ThemeMode.TERANG,

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ChatAdminScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.BantuanScreen(

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

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.TentangAplikasiScreen(

                        onBack = {

                            currentScreen =
                                StaffScreen.Settings
                        }
                    )
                }
            }


            // ==================================================
            // DIALOG PESAN ABSENSI
            // ==================================================

            attendanceMessage?.let { message ->

                AlertDialog(

                    onDismissRequest = {

                        attendanceMessage = null
                    },

                    title = {

                        Text(
                            text =
                                "Absensi Tidak Dapat Dilakukan"
                        )
                    },

                    text = {

                        Text(
                            text =
                                message
                        )
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                attendanceMessage = null
                            }

                        ) {

                            Text(
                                text = "OK"
                            )
                        }
                    }
                )
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

    Approval,

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