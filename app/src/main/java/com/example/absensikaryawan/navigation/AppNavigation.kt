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

import com.example.absensikaryawan.screens.AbsenLuarKantorScreen
import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.AdminProfileScreen
import com.example.absensikaryawan.screens.AdminSettingsScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.BantuanScreen
import com.example.absensikaryawan.screens.DetailPengajuanScreen
import com.example.absensikaryawan.screens.ForgotPasswordScreen
import com.example.absensikaryawan.screens.KaryawanScreen
import com.example.absensikaryawan.screens.LoginScreen
import com.example.absensikaryawan.screens.NotifikasiScreen
import com.example.absensikaryawan.screens.PengajuanBaruScreen
import com.example.absensikaryawan.screens.PengajuanScreen
import com.example.absensikaryawan.screens.ProfileScreen
import com.example.absensikaryawan.screens.RekapAdminScreen
import com.example.absensikaryawan.screens.RiwayatPengajuanScreen
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
// APP SCREEN
// ==========================================================

private enum class AppScreen {

    // ======================================================
    // AUTH
    // ======================================================

    Login,
    ForgotPassword,


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
    RiwayatPengajuan,
    DetailPengajuan,
    Scan,
    AbsenLuarKantor,
    Riwayat,
    Settings,
    Tampilan,
    Notifikasi,
    Bantuan,
    TentangAplikasi
}


// ==========================================================
// BOTTOM MENU ITEM
// ==========================================================

private data class BottomMenuItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)


// ==========================================================
// STAFF BOTTOM MENU
// ==========================================================

private val staffBottomMenuItems =
    listOf(

        BottomMenuItem(
            AppScreen.Staff,
            "Beranda",
            Icons.Default.Home
        ),

        BottomMenuItem(
            AppScreen.Pengajuan,
            "Pengajuan",
            Icons.Default.NoteAdd
        ),

        BottomMenuItem(
            AppScreen.Scan,
            "Scan",
            Icons.Default.QrCodeScanner
        ),

        BottomMenuItem(
            AppScreen.Riwayat,
            "Riwayat",
            Icons.Default.History
        ),

        BottomMenuItem(
            AppScreen.Settings,
            "Setting",
            Icons.Default.Settings
        )
    )


// ==========================================================
// ADMIN BOTTOM MENU
// ==========================================================

private val adminBottomMenuItems =
    listOf(

        BottomMenuItem(
            AppScreen.Admin,
            "Beranda",
            Icons.Default.Home
        ),

        BottomMenuItem(
            AppScreen.Approval,
            "Approval",
            Icons.Default.NoteAdd
        ),

        BottomMenuItem(
            AppScreen.Karyawan,
            "Karyawan",
            Icons.Default.People
        ),

        BottomMenuItem(
            AppScreen.AdminRekap,
            "Rekap",
            Icons.Default.Assessment
        ),

        BottomMenuItem(
            AppScreen.AdminSettings,
            "Setting",
            Icons.Default.Settings
        )
    )


// ==========================================================
// APP NAVIGATION
// ==========================================================

@Composable
fun AppNavigation() {

    // ======================================================
    // CONTEXT
    // ======================================================

    val context: Context =
        LocalContext.current


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope =
        rememberCoroutineScope()


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
    // DATASTORE ABSENSI
    // ======================================================

    val absensiDataStore =
        remember {
            AbsensiDataStore(context)
        }


    // ======================================================
    // THEME DATASTORE
    // ======================================================

    val themeDataStore =
        remember {
            ThemeDataStore(context)
        }

    val selectedThemeMode by
    themeDataStore
        .themeMode
        .collectAsState(
            initial = ThemeMode.TERANG
        )


    // ======================================================
    // CURRENT SCREEN
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

    val refreshKey =
        remember {
            mutableIntStateOf(0)
        }


    // ======================================================
    // PENGAJUAN TERPILIH
    // ======================================================

    var pengajuanTerpilih by
    remember {
        mutableStateOf(
            emptyMap<String, Any>()
        )
    }


    // ======================================================
    // FILTER STATUS PENGAJUAN
    // ======================================================

    var filterStatusPengajuan by
    remember {
        mutableStateOf(
            "semua"
        )
    }


    // ======================================================
    // DATA ABSEN LUAR KANTOR
    // ======================================================

    var lokasiLuarKantor by
    remember {
        mutableStateOf("")
    }

    var alasanLuarKantor by
    remember {
        mutableStateOf("")
    }


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

                currentScreen.value == AppScreen.Profile ||

                currentScreen.value == AppScreen.Pengajuan ||

                currentScreen.value == AppScreen.PengajuanBaru ||

                currentScreen.value == AppScreen.RiwayatPengajuan ||

                currentScreen.value == AppScreen.DetailPengajuan ||

                currentScreen.value == AppScreen.Scan ||

                currentScreen.value == AppScreen.AbsenLuarKantor ||

                currentScreen.value == AppScreen.Riwayat ||

                currentScreen.value == AppScreen.Settings ||

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

                                else -> false
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

            } else if (isStaffArea) {

                // ==================================================
                // STAFF BOTTOM NAVIGATION
                // ==================================================

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

                                else -> false
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

                            refreshKey.intValue++

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onAdminLogin = {

                            currentScreen.value =
                                AppScreen.Admin
                        },

                        onForgotPassword = {

                            currentScreen.value =
                                AppScreen.ForgotPassword
                        }
                    )
                }


                // ==================================================
                // FORGOT PASSWORD
                // ==================================================

                AppScreen.ForgotPassword -> {

                    ForgotPasswordScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Login
                        }
                    )
                }


                // ==================================================
                // ADMIN DASHBOARD
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

                    ApprovalScreen(

                        onDetailClick = { pengajuan ->

                            pengajuanTerpilih =
                                pengajuan

                            currentScreen.value =
                                AppScreen.DetailPengajuan
                        }
                    )
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

                    AdminProfileScreen(

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
                // STAFF DASHBOARD
                // ==================================================

                AppScreen.Staff -> {

                    StaffDashboardScreen(

                        refreshKey =
                            refreshKey.intValue,

                        onScan = {

                            currentScreen.value =
                                AppScreen.Scan
                        },

                        onAbsenLuarKantor = {

                            // LANGSUNG KE FORM
                            currentScreen.value =
                                AppScreen.AbsenLuarKantor
                        },

                        onProfile = {

                            currentScreen.value =
                                AppScreen.Profile
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
                        },

                        onNotification = {

                            currentScreen.value =
                                AppScreen.Notifikasi
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
                // ABSEN LUAR KANTOR
                // ==================================================

                AppScreen.AbsenLuarKantor -> {

                    AbsenLuarKantorScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onKirim = { lokasi, alasan ->

                            // ======================================
                            // SIMPAN DATA FORM
                            // ======================================

                            lokasiLuarKantor =
                                lokasi

                            alasanLuarKantor =
                                alasan


                            Log.d(
                                "ABSEN_LUAR_KANTOR",
                                "LOKASI = $lokasi"
                            )

                            Log.d(
                                "ABSEN_LUAR_KANTOR",
                                "ALASAN = $alasan"
                            )


                            // ======================================
                            // PROSES FIRESTORE
                            // ======================================

                            scope.launch {

                                try {

                                    // ==================================
                                    // CEK USER LOGIN
                                    // ==================================

                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser

                                    if (
                                        currentUser == null
                                    ) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "USER BELUM LOGIN"
                                        )

                                        currentScreen.value =
                                            AppScreen.Login

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
                                        nama.isBlank()
                                    ) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
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
                                    // JAM
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
                                    // SUDAH ABSEN
                                    // ==================================

                                    if (
                                        absenHariIni != null
                                    ) {

                                        Log.d(
                                            "ABSEN_LUAR_KANTOR",
                                            "SUDAH ADA ABSEN HARI INI"
                                        )

                                        currentScreen.value =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // ==================================
                                    // SIMPAN FIRESTORE
                                    // ==================================

                                    val hasilSimpan =
                                        firestoreRepository
                                            .simpanAbsenLuarKantor(

                                                uid =
                                                    uid,

                                                nama =
                                                    nama,

                                                tanggal =
                                                    tanggal,

                                                jamMasuk =
                                                    jam,

                                                lokasi =
                                                    lokasi,

                                                alasan =
                                                    alasan
                                            )


                                    // ==================================
                                    // CEK HASIL SIMPAN
                                    // ==================================

                                    if (
                                        hasilSimpan.isFailure
                                    ) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "GAGAL SIMPAN ABSEN LUAR KANTOR",
                                            hasilSimpan
                                                .exceptionOrNull()
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // SIMPAN DATASTORE
                                    // ==================================

                                    val catatanLokal =
                                        "Lokasi: $lokasi\nAlasan: $alasan"

                                    absensiDataStore
                                        .simpanAbsen(

                                            jam =
                                                jam,

                                            tanggal =
                                                tanggal,

                                            qrData =
                                                "LUAR_KANTOR",

                                            catatan =
                                                catatanLokal
                                        )


                                    // ==================================
                                    // REFRESH DASHBOARD
                                    // ==================================

                                    refreshKey.intValue++


                                    Log.d(
                                        "ABSEN_LUAR_KANTOR",
                                        "ABSEN LUAR KANTOR BERHASIL"
                                    )


                                    // ==================================
                                    // KEMBALI DASHBOARD
                                    // ==================================

                                    currentScreen.value =
                                        AppScreen.Staff

                                } catch (
                                    e: Exception
                                ) {

                                    Log.e(
                                        "ABSEN_LUAR_KANTOR",
                                        "ERROR ABSEN LUAR KANTOR",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // PENGAJUAN
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
                        },

                        onStatusClick = { pengajuan ->

                            val filter =
                                pengajuan[
                                    "filterStatus"
                                ]?.toString()


                            if (
                                filter != null
                            ) {

                                filterStatusPengajuan =
                                    filter

                                currentScreen.value =
                                    AppScreen.RiwayatPengajuan

                            } else {

                                pengajuanTerpilih =
                                    pengajuan

                                currentScreen.value =
                                    AppScreen.DetailPengajuan
                            }
                        }
                    )
                }


                // ==================================================
                // RIWAYAT PENGAJUAN
                // ==================================================

                AppScreen.RiwayatPengajuan -> {

                    RiwayatPengajuanScreen(

                        filterStatus =
                            filterStatusPengajuan,

                        onBack = {

                            currentScreen.value =
                                AppScreen.Pengajuan
                        },

                        onDetailClick = { pengajuan ->

                            pengajuanTerpilih =
                                pengajuan

                            currentScreen.value =
                                AppScreen.DetailPengajuan
                        }
                    )
                }


                // ==================================================
                // PENGAJUAN BARU
                // ==================================================

                AppScreen.PengajuanBaru -> {

                    PengajuanBaruScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Pengajuan
                        },

                        onSubmit = {
                                _,
                                _,
                                _,
                                _,
                                _,
                                _,
                                _ ->

                            currentScreen.value =
                                AppScreen.Pengajuan
                        }
                    )
                }


                // ==================================================
                // DETAIL PENGAJUAN
                // ==================================================

                AppScreen.DetailPengajuan -> {

                    DetailPengajuanScreen(

                        jenis =
                            pengajuanTerpilih[
                                "jenis"
                            ]?.toString()
                                ?: "Pengajuan",

                        tanggal =
                            pengajuanTerpilih[
                                "tanggal"
                            ]?.toString()
                                ?: "",

                        status =
                            pengajuanTerpilih[
                                "status"
                            ]?.toString()
                                ?: "menunggu",

                        jamPulang =
                            pengajuanTerpilih[
                                "jamPulang"
                            ]?.toString()
                                ?: "",

                        jamKeluar =
                            pengajuanTerpilih[
                                "jamKeluar"
                            ]?.toString()
                                ?: "",

                        jamKembali =
                            pengajuanTerpilih[
                                "jamKembali"
                            ]?.toString()
                                ?: "",

                        tanggalMulai =
                            pengajuanTerpilih[
                                "tanggalMulai"
                            ]?.toString()
                                ?: "",

                        tanggalSelesai =
                            pengajuanTerpilih[
                                "tanggalSelesai"
                            ]?.toString()
                                ?: "",

                        alasan =
                            pengajuanTerpilih[
                                "alasan"
                            ]?.toString()
                                ?: "",

                        filterStatus =
                            "semua",

                        onBack = {

                            currentScreen.value =
                                AppScreen.Pengajuan
                        },

                        onDetailClick = { detail ->

                            pengajuanTerpilih =
                                detail
                        }
                    )
                }


                // ==================================================
                // SCAN ABSEN
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
                                    // CEK QR
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

                                        currentScreen.value =
                                            AppScreen.Login

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
                                    // JAM
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


                                        // ==============================
                                        // DATASTORE
                                        // ==============================

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


                                        refreshKey.intValue++


                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "ABSEN MASUK BERHASIL"
                                        )


                                        currentScreen.value =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // ==================================
                                    // ABSEN SUDAH ADA
                                    // ==================================

                                    val documentId =
                                        absenHariIni
                                            .documentId

                                    val jamMasukLama =
                                        absenHariIni
                                            .jamMasuk

                                    val jamPulangLama =
                                        absenHariIni
                                            .jamPulang


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


                                        // ==============================
                                        // DATASTORE PULANG
                                        // ==============================

                                        absensiDataStore
                                            .simpanPulang(
                                                jam
                                            )


                                        refreshKey.intValue++


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
                                    // ABSEN SUDAH LENGKAP
                                    // ==================================

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "ABSEN HARI INI SUDAH LENGKAP"
                                    )


                                    refreshKey.intValue++


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
                        },

                        // ==================================================
                        // ABSEN LUAR KANTOR
                        // ==================================================

                        onAbsenLuarKantor = {
                                lokasi,
                                alasan ->

                            lokasiLuarKantor =
                                lokasi

                            alasanLuarKantor =
                                alasan

                            Log.d(
                                "ABSEN_LUAR_KANTOR",
                                "LOKASI = $lokasi"
                            )

                            Log.d(
                                "ABSEN_LUAR_KANTOR",
                                "ALASAN = $alasan"
                            )

                            // =================================================
                            // Langsung buka form.
                            // Data lokasi/alasan dari Scan tidak digunakan
                            // karena form sekarang menginputnya langsung.
                            // =================================================

                            currentScreen.value =
                                AppScreen.AbsenLuarKantor
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
                        },

                        onDetailClick = { riwayat ->

                            pengajuanTerpilih =
                                mapOf(

                                    "jenis" to
                                            riwayat.jenis,

                                    "jamPulang" to
                                            riwayat.jamPulang,

                                    "jamKeluar" to
                                            riwayat.jamKeluar,

                                    "jamKembali" to
                                            riwayat.jamKembali,

                                    "tanggalMulai" to
                                            riwayat.tanggalMulai,

                                    "tanggalSelesai" to
                                            riwayat.tanggalSelesai,

                                    "alasan" to
                                            riwayat.alasan,

                                    "status" to
                                            riwayat.status,

                                    "catatanAdmin" to
                                            riwayat.catatanAdmin
                                )


                            currentScreen.value =
                                AppScreen.DetailPengajuan
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