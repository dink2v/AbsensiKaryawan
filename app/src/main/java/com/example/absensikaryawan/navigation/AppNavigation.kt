package com.example.absensikaryawan.navigation

import android.content.Context
import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.ThemeDataStore
import com.example.absensikaryawan.data.AbsensiDataStore
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.UserRepository

import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.AdminSettingsScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.BantuanScreen
import com.example.absensikaryawan.screens.KaryawanScreen
import com.example.absensikaryawan.screens.LoginScreen
import com.example.absensikaryawan.screens.NotifikasiScreen
import com.example.absensikaryawan.screens.PengajuanBaruScreen
import com.example.absensikaryawan.screens.PengajuanScreen
import com.example.absensikaryawan.screens.ProfileScreen
import com.example.absensikaryawan.screens.RekapAdminScreen
import com.example.absensikaryawan.screens.RiwayatScreen
import com.example.absensikaryawan.screens.ScanAbsenScreen
import com.example.absensikaryawan.screens.SettingsScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen
import com.example.absensikaryawan.screens.TampilanScreen
import com.example.absensikaryawan.screens.TentangAplikasiScreen
import com.example.absensikaryawan.screens.ThemeMode

import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// ==========================================================
// WARNA BOTTOM NAVIGATION
// ==========================================================

private val BottomNavGreen =
    Color(0xFF2E7D32)


// ==========================================================
// SCREEN
// ==========================================================

private enum class AppScreen {

    Login,

    // ======================================================
    // ADMIN
    // ======================================================

    Admin,
    Approval,
    Karyawan,
    AdminRekap,
    AdminSettings,
    AdminProfile,
    AdminTampilan,
    AdminBantuan,
    AdminTentangAplikasi,

    // ======================================================
    // STAFF
    // ======================================================

    Staff,
    Profile,
    Pengajuan,
    PengajuanBaru,
    Scan,
    Riwayat,
    Settings,
    Tampilan,
    Notifikasi,
    Bantuan,
    TentangAplikasi
}


// ==========================================================
// BOTTOM NAVIGATION ITEM
// ==========================================================

private data class BottomMenuItem(

    val screen: AppScreen,

    val label: String,

    val icon: ImageVector
)


// ==========================================================
// BOTTOM NAVIGATION STAFF
// ==========================================================

private val staffBottomMenuItems =
    listOf(

        BottomMenuItem(
            screen = AppScreen.Staff,
            label = "Beranda",
            icon = Icons.Default.Home
        ),

        BottomMenuItem(
            screen = AppScreen.Pengajuan,
            label = "Pengajuan",
            icon = Icons.Default.NoteAdd
        ),

        BottomMenuItem(
            screen = AppScreen.Scan,
            label = "Scan",
            icon = Icons.Default.QrCodeScanner
        ),

        BottomMenuItem(
            screen = AppScreen.Riwayat,
            label = "Riwayat",
            icon = Icons.Default.History
        ),

        BottomMenuItem(
            screen = AppScreen.Settings,
            label = "Setting",
            icon = Icons.Default.Settings
        )
    )


// ==========================================================
// BOTTOM NAVIGATION ADMIN
// ==========================================================

private val adminBottomMenuItems =
    listOf(

        BottomMenuItem(
            screen = AppScreen.Admin,
            label = "Beranda",
            icon = Icons.Default.Home
        ),

        BottomMenuItem(
            screen = AppScreen.Approval,
            label = "Approval",
            icon = Icons.Default.NoteAdd
        ),

        BottomMenuItem(
            screen = AppScreen.Karyawan,
            label = "Karyawan",
            icon = Icons.Default.People
        ),

        BottomMenuItem(
            screen = AppScreen.AdminRekap,
            label = "Rekap",
            icon = Icons.Default.Assessment
        ),

        BottomMenuItem(
            screen = AppScreen.AdminSettings,
            label = "Setting",
            icon = Icons.Default.Settings
        )
    )


// ==========================================================
// APP NAVIGATION
// ==========================================================

@Composable
fun AppNavigation() {

    // ======================================================
    // REPOSITORY
    // ======================================================

    val userRepository =
        remember {
            UserRepository()
        }

    val firestoreRepository =
        remember {
            FirestoreRepository()
        }


    // ======================================================
    // NAVIGATION STATE
    // ======================================================

    val currentScreen =
        remember {

            mutableStateOf(
                AppScreen.Login
            )
        }


    // ======================================================
    // REFRESH DASHBOARD
    // ======================================================

    var refreshKey by remember {

        mutableIntStateOf(0)
    }


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope =
        rememberCoroutineScope()


    // ======================================================
    // CONTEXT
    // ======================================================

    val context: Context =
        LocalContext.current


    // ======================================================
    // DATASTORE ABSENSI
    // ======================================================

    val absensiDataStore =
        remember {

            AbsensiDataStore(
                context
            )
        }


    // ======================================================
    // THEME DATASTORE
    // ======================================================

    val themeDataStore =
        remember {

            ThemeDataStore(
                context
            )
        }


    val selectedThemeMode by
    themeDataStore.themeMode
        .collectAsState(
            initial = ThemeMode.TERANG
        )


    // ======================================================
    // ADMIN AREA
    // ======================================================

    val isAdminArea =

        currentScreen.value == AppScreen.Admin ||

                currentScreen.value == AppScreen.Approval ||

                currentScreen.value == AppScreen.Karyawan ||

                currentScreen.value == AppScreen.AdminRekap ||

                currentScreen.value == AppScreen.AdminSettings ||

                currentScreen.value == AppScreen.AdminProfile ||

                currentScreen.value == AppScreen.AdminTampilan ||

                currentScreen.value == AppScreen.AdminBantuan ||

                currentScreen.value == AppScreen.AdminTentangAplikasi


    // ======================================================
    // STAFF AREA
    // ======================================================

    val isStaffArea =

        currentScreen.value == AppScreen.Staff ||

                currentScreen.value == AppScreen.Pengajuan ||

                currentScreen.value == AppScreen.PengajuanBaru ||

                currentScreen.value == AppScreen.Scan ||

                currentScreen.value == AppScreen.Riwayat ||

                currentScreen.value == AppScreen.Settings ||

                currentScreen.value == AppScreen.Profile ||

                currentScreen.value == AppScreen.Tampilan ||

                currentScreen.value == AppScreen.Notifikasi ||

                currentScreen.value == AppScreen.Bantuan ||

                currentScreen.value == AppScreen.TentangAplikasi


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        bottomBar = {

            // ==================================================
            // ADMIN BOTTOM NAVIGATION
            // ==================================================

            if (isAdminArea) {

                NavigationBar(

                    containerColor =
                        Color.White,

                    tonalElevation =
                        6.dp

                ) {

                    adminBottomMenuItems.forEach { item ->

                        val isSelected =
                            when (item.screen) {

                                AppScreen.Admin ->
                                    currentScreen.value ==
                                            AppScreen.Admin

                                AppScreen.Approval ->
                                    currentScreen.value ==
                                            AppScreen.Approval

                                AppScreen.Karyawan ->
                                    currentScreen.value ==
                                            AppScreen.Karyawan

                                AppScreen.AdminRekap ->
                                    currentScreen.value ==
                                            AppScreen.AdminRekap

                                AppScreen.AdminSettings ->
                                    currentScreen.value ==
                                            AppScreen.AdminSettings ||
                                            currentScreen.value ==
                                            AppScreen.AdminProfile ||
                                            currentScreen.value ==
                                            AppScreen.AdminTampilan ||
                                            currentScreen.value ==
                                            AppScreen.AdminBantuan ||
                                            currentScreen.value ==
                                            AppScreen.AdminTentangAplikasi

                                else ->
                                    false
                            }


                        NavigationBarItem(

                            selected =
                                isSelected,

                            onClick = {

                                currentScreen.value =
                                    item.screen
                            },

                            icon = {

                                Icon(

                                    imageVector =
                                        item.icon,

                                    contentDescription =
                                        item.label,

                                    modifier =
                                        Modifier.size(

                                            if (
                                                item.screen ==
                                                AppScreen.Admin
                                            ) {

                                                25.dp

                                            } else {

                                                23.dp
                                            }
                                        )
                                )
                            },

                            label = {

                                Text(

                                    text =
                                        item.label,

                                    fontSize =
                                        11.sp
                                )
                            },

                            colors =
                                NavigationBarItemDefaults.colors(

                                    selectedIconColor =
                                        BottomNavGreen,

                                    selectedTextColor =
                                        BottomNavGreen,

                                    unselectedIconColor =
                                        Color.Gray,

                                    unselectedTextColor =
                                        Color.Gray,

                                    indicatorColor =
                                        BottomNavGreen.copy(
                                            alpha = 0.12f
                                        )
                                )
                        )
                    }
                }
            }


            // ==================================================
            // STAFF BOTTOM NAVIGATION
            // ==================================================

            else if (isStaffArea) {

                NavigationBar(

                    containerColor =
                        Color.White,

                    tonalElevation =
                        6.dp

                ) {

                    staffBottomMenuItems.forEach { item ->

                        val isSelected =
                            when (item.screen) {

                                AppScreen.Staff ->
                                    currentScreen.value ==
                                            AppScreen.Staff

                                AppScreen.Pengajuan ->
                                    currentScreen.value ==
                                            AppScreen.Pengajuan

                                AppScreen.Scan ->
                                    currentScreen.value ==
                                            AppScreen.Scan

                                AppScreen.Riwayat ->
                                    currentScreen.value ==
                                            AppScreen.Riwayat

                                AppScreen.Settings ->
                                    currentScreen.value ==
                                            AppScreen.Settings ||
                                            currentScreen.value ==
                                            AppScreen.Profile ||
                                            currentScreen.value ==
                                            AppScreen.Tampilan ||
                                            currentScreen.value ==
                                            AppScreen.Notifikasi ||
                                            currentScreen.value ==
                                            AppScreen.Bantuan ||
                                            currentScreen.value ==
                                            AppScreen.TentangAplikasi

                                else ->
                                    false
                            }


                        NavigationBarItem(

                            selected =
                                isSelected,

                            onClick = {

                                currentScreen.value =
                                    item.screen
                            },

                            icon = {

                                Icon(

                                    imageVector =
                                        item.icon,

                                    contentDescription =
                                        item.label,

                                    modifier =
                                        Modifier.size(

                                            if (
                                                item.screen ==
                                                AppScreen.Scan
                                            ) {

                                                28.dp

                                            } else {

                                                23.dp
                                            }
                                        )
                                )
                            },

                            label = {

                                Text(

                                    text =
                                        item.label,

                                    fontSize =
                                        11.sp
                                )
                            },

                            colors =
                                NavigationBarItemDefaults.colors(

                                    selectedIconColor =
                                        BottomNavGreen,

                                    selectedTextColor =
                                        BottomNavGreen,

                                    unselectedIconColor =
                                        Color.Gray,

                                    unselectedTextColor =
                                        Color.Gray,

                                    indicatorColor =
                                        BottomNavGreen.copy(
                                            alpha = 0.12f
                                        )
                                )
                        )
                    }
                }
            }
        }

    ) { paddingValues ->


        // ======================================================
        // CONTENT
        // ======================================================

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
        ) {

            when (currentScreen.value) {


                // ==================================================
                // LOGIN
                // ==================================================

                AppScreen.Login -> {

                    LoginScreen(

                        onStaffLogin = {

                            refreshKey++

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onAdminLogin = {

                            currentScreen.value =
                                AppScreen.Admin
                        }
                    )
                }


                // ==================================================
                // ADMIN BERANDA
                // ==================================================

                AppScreen.Admin -> {

                    AdminDashboardScreen(

                        onApproval = {

                            currentScreen.value =
                                AppScreen.Approval
                        },

                        onEmployees = {

                            currentScreen.value =
                                AppScreen.Karyawan
                        },

                        onRecap = {

                            currentScreen.value =
                                AppScreen.AdminRekap
                        },

                        onSettings = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        }
                    )
                }


                // ==================================================
                // ADMIN APPROVAL
                // ==================================================

                AppScreen.Approval -> {

                    ApprovalScreen()
                }


                // ==================================================
                // ADMIN KARYAWAN
                // ==================================================

                AppScreen.Karyawan -> {

                    KaryawanScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Admin
                        }
                    )
                }


                // ==================================================
                // ADMIN REKAP
                // ==================================================

                AppScreen.AdminRekap -> {

                    RekapAdminScreen()
                }


                // ==================================================
                // ADMIN SETTINGS
                // ==================================================

                AppScreen.AdminSettings -> {

                    AdminSettingsScreen(

                        onTampilan = {

                            currentScreen.value =
                                AppScreen.AdminTampilan
                        },

                        onBantuan = {

                            currentScreen.value =
                                AppScreen.AdminBantuan
                        },

                        onTentangAplikasi = {

                            currentScreen.value =
                                AppScreen.AdminTentangAplikasi
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen.value =
                                AppScreen.Login
                        }
                    )
                }


                // ==================================================
                // ADMIN PROFILE
                // ==================================================

                AppScreen.AdminProfile -> {

                    ProfileScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        }
                    )
                }


                // ==================================================
                // ADMIN TAMPILAN
                // ==================================================

                AppScreen.AdminTampilan -> {

                    TampilanScreen(

                        selectedMode =
                            selectedThemeMode,

                        onModeSelected = { mode ->

                            scope.launch {

                                themeDataStore
                                    .saveThemeMode(
                                        mode
                                    )
                            }

                            Log.d(
                                "THEME_DEBUG",
                                "MODE ADMIN DIPILIH = $mode"
                            )
                        },

                        onBack = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        }
                    )
                }


                // ==================================================
                // ADMIN BANTUAN
                // ==================================================

                AppScreen.AdminBantuan -> {

                    BantuanScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        },

                        onChatAdmin = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        }
                    )
                }


                // ==================================================
                // ADMIN TENTANG APLIKASI
                // ==================================================

                AppScreen.AdminTentangAplikasi -> {

                    TentangAplikasiScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.AdminSettings
                        }
                    )
                }


                // ==================================================
                // STAFF BERANDA
                // ==================================================

                AppScreen.Staff -> {

                    StaffDashboardScreen(

                        refreshKey =
                            refreshKey,

                        onScan = {

                            currentScreen.value =
                                AppScreen.Scan
                        },

                        onProfile = {

                            currentScreen.value =
                                AppScreen.Profile
                        },

                        onNotification = {

                            currentScreen.value =
                                AppScreen.Notifikasi
                        },

                        onHistory = {

                            currentScreen.value =
                                AppScreen.Riwayat
                        },

                        onHistoryPulang = {

                            currentScreen.value =
                                AppScreen.Riwayat
                        },

                        onSettings = {

                            currentScreen.value =
                                AppScreen.Settings
                        },

                        onPengajuan = {

                            currentScreen.value =
                                AppScreen.Pengajuan
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen.value =
                                AppScreen.Login
                        }
                    )
                }


                // ==================================================
                // STAFF PROFILE
                // ==================================================

                AppScreen.Profile -> {

                    ProfileScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        }
                    )
                }


                // ==================================================
                // STAFF PENGAJUAN
                // ==================================================

                AppScreen.Pengajuan -> {

                    PengajuanScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onPengajuanBaru = {

                            currentScreen.value =
                                AppScreen.PengajuanBaru
                        }
                    )
                }


                // ==================================================
                // STAFF PENGAJUAN BARU
                // ==================================================

                AppScreen.PengajuanBaru -> {

                    PengajuanBaruScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Pengajuan
                        },

                        onSubmit = {

                                jenis,
                                jamPulang,
                                jamKeluar,
                                jamKembali,
                                tanggalMulai,
                                tanggalSelesai,
                                alasan ->

                            scope.launch {

                                try {

                                    // ==================================
                                    // USER LOGIN
                                    // ==================================

                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser


                                    if (
                                        currentUser == null
                                    ) {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "USER BELUM LOGIN"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // UID USER
                                    // ==================================

                                    val uid =
                                        currentUser.uid


                                    // ==================================
                                    // NAMA USER
                                    // ==================================

                                    val hasilNama =
                                        userRepository
                                            .getCurrentUserName()


                                    val nama =
                                        hasilNama
                                            .getOrNull()
                                            ?: ""


                                    if (
                                        nama.isEmpty()
                                    ) {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "NAMA USER TIDAK DITEMUKAN"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // TANGGAL PENGAJUAN
                                    // ==================================

                                    val tanggal =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    // ==================================
                                    // SIMPAN PENGAJUAN
                                    // ==================================

                                    val hasilSimpan =
                                        firestoreRepository
                                            .simpanPengajuan(

                                                uid =
                                                    uid,

                                                nama =
                                                    nama,

                                                jenis =
                                                    jenis,

                                                tanggal =
                                                    tanggal,

                                                jamPulang =
                                                    jamPulang,

                                                jamKeluar =
                                                    jamKeluar,

                                                jamKembali =
                                                    jamKembali,

                                                tanggalMulai =
                                                    tanggalMulai,

                                                tanggalSelesai =
                                                    tanggalSelesai,

                                                alasan =
                                                    alasan
                                            )


                                    if (
                                        hasilSimpan.isSuccess
                                    ) {

                                        Log.d(
                                            "PENGAJUAN_DEBUG",
                                            "PENGAJUAN BERHASIL DISIMPAN"
                                        )

                                        currentScreen.value =
                                            AppScreen.Pengajuan

                                    } else {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "GAGAL SIMPAN PENGAJUAN",
                                            hasilSimpan
                                                .exceptionOrNull()
                                        )
                                    }

                                } catch (
                                    e: Exception
                                ) {

                                    Log.e(
                                        "PENGAJUAN_DEBUG",
                                        "ERROR PENGAJUAN",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // STAFF SCAN QR
                // ==================================================

                AppScreen.Scan -> {

                    ScanAbsenScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onQrScanned = {

                                qrData,
                                catatan ->

                            Log.d(
                                "ABSEN_DEBUG",
                                "QR TERBACA"
                            )

                            Log.d(
                                "ABSEN_DEBUG",
                                "QR DATA = $qrData"
                            )

                            Log.d(
                                "ABSEN_DEBUG",
                                "CATATAN = $catatan"
                            )


                            scope.launch {

                                try {

                                    // ==================================
                                    // VALIDASI QR
                                    // ==================================

                                    if (
                                        qrData.isBlank()
                                    ) {

                                        Log.e(
                                            "ABSEN_DEBUG",
                                            "QR KOSONG"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // USER LOGIN
                                    // ==================================

                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser


                                    if (
                                        currentUser == null
                                    ) {

                                        Log.e(
                                            "ABSEN_DEBUG",
                                            "USER BELUM LOGIN"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // UID
                                    // ==================================

                                    val uid =
                                        currentUser.uid


                                    // ==================================
                                    // NAMA USER
                                    // ==================================

                                    val hasilNama =
                                        userRepository
                                            .getCurrentUserName()


                                    val nama =
                                        hasilNama
                                            .getOrNull()
                                            ?: ""


                                    if (
                                        nama.isEmpty()
                                    ) {

                                        Log.e(
                                            "ABSEN_DEBUG",
                                            "NAMA USER TIDAK DITEMUKAN"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // TANGGAL
                                    // ==================================

                                    val tanggal =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    // ==================================
                                    // JAM SEKARANG
                                    // ==================================

                                    val jam =
                                        SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    // ==================================
                                    // CEK ABSEN HARI INI
                                    // ==================================

                                    val absenHariIni =
                                        firestoreRepository
                                            .getAbsenHariIni(

                                                uid =
                                                    uid,

                                                tanggal =
                                                    tanggal
                                            )


                                    // ==================================
                                    // ABSEN MASUK
                                    // ==================================

                                    if (
                                        absenHariIni == null
                                    ) {

                                        val hasilSimpan =
                                            firestoreRepository
                                                .simpanAbsenMasuk(

                                                    uid =
                                                        uid,

                                                    nama =
                                                        nama,

                                                    tanggal =
                                                        tanggal,

                                                    jamMasuk =
                                                        jam,

                                                    qrData =
                                                        qrData,

                                                    catatan =
                                                        catatan
                                                )


                                        if (
                                            hasilSimpan.isFailure
                                        ) {

                                            Log.e(
                                                "ABSEN_DEBUG",
                                                "GAGAL SIMPAN ABSEN MASUK",
                                                hasilSimpan
                                                    .exceptionOrNull()
                                            )

                                            return@launch
                                        }


                                        absensiDataStore
                                            .simpanAbsen(

                                                jam =
                                                    jam,

                                                tanggal =
                                                    tanggal,

                                                qrData =
                                                    qrData,

                                                catatan =
                                                    catatan
                                            )


                                        refreshKey++

                                        currentScreen.value =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // ==================================
                                    // DATA ABSEN
                                    // ==================================

                                    val documentId =
                                        absenHariIni.documentId

                                    val jamMasukLama =
                                        absenHariIni.jamMasuk

                                    val jamPulangLama =
                                        absenHariIni.jamPulang


                                    // ==================================
                                    // ABSEN PULANG
                                    // ==================================

                                    if (
                                        jamPulangLama.isBlank()
                                    ) {

                                        val hasilPulang =
                                            firestoreRepository
                                                .simpanAbsenPulang(

                                                    documentId =
                                                        documentId,

                                                    jamPulang =
                                                        jam
                                                )


                                        if (
                                            hasilPulang.isFailure
                                        ) {

                                            Log.e(
                                                "ABSEN_DEBUG",
                                                "GAGAL SIMPAN ABSEN PULANG",
                                                hasilPulang
                                                    .exceptionOrNull()
                                            )

                                            return@launch
                                        }


                                        absensiDataStore
                                            .simpanPulang(
                                                jam
                                            )


                                        refreshKey++

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "ABSEN PULANG BERHASIL"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "JAM MASUK = $jamMasukLama"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "JAM PULANG = $jam"
                                        )


                                        currentScreen.value =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // ==================================
                                    // SUDAH LENGKAP
                                    // ==================================

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "ABSEN HARI INI SUDAH LENGKAP"
                                    )


                                    refreshKey++

                                    currentScreen.value =
                                        AppScreen.Staff

                                } catch (
                                    e: Exception
                                ) {

                                    Log.e(
                                        "ABSEN_DEBUG",
                                        "GAGAL PROSES ABSEN",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // RIWAYAT STAFF
                // ==================================================

                AppScreen.Riwayat -> {

                    RiwayatScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        }
                    )
                }


                // ==================================================
                // SETTINGS STAFF
                // ==================================================

                AppScreen.Settings -> {

                    SettingsScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onNotification = {

                            currentScreen.value =
                                AppScreen.Notifikasi
                        },

                        onTampilan = {

                            currentScreen.value =
                                AppScreen.Tampilan
                        },

                        onBantuan = {

                            currentScreen.value =
                                AppScreen.Bantuan
                        },

                        onTentangAplikasi = {

                            currentScreen.value =
                                AppScreen.TentangAplikasi
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen.value =
                                AppScreen.Login
                        }
                    )
                }


                // ==================================================
                // TAMPILAN STAFF
                // ==================================================

                AppScreen.Tampilan -> {

                    TampilanScreen(

                        selectedMode =
                            selectedThemeMode,

                        onModeSelected = { mode ->

                            scope.launch {

                                themeDataStore
                                    .saveThemeMode(
                                        mode
                                    )
                            }

                            Log.d(
                                "THEME_DEBUG",
                                "MODE STAFF DIPILIH = $mode"
                            )
                        },

                        onBack = {

                            currentScreen.value =
                                AppScreen.Settings
                        }
                    )
                }


                // ==================================================
                // NOTIFIKASI STAFF
                // ==================================================

                AppScreen.Notifikasi -> {

                    NotifikasiScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        }
                    )
                }


                // ==================================================
                // BANTUAN STAFF
                // ==================================================

                AppScreen.Bantuan -> {

                    BantuanScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Settings
                        },

                        onChatAdmin = {

                            currentScreen.value =
                                AppScreen.Settings
                        }
                    )
                }


                // ==================================================
                // TENTANG APLIKASI STAFF
                // ==================================================

                AppScreen.TentangAplikasi -> {

                    TentangAplikasiScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Settings
                        }
                    )
                }
            }
        }
    }
}