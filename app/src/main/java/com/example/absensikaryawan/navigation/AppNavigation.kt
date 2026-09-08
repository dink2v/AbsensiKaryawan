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

import com.example.absensikaryawan.repository.ChatRoom

import com.example.absensikaryawan.screens.AbsenLuarKantorScreen
import com.example.absensikaryawan.screens.AdminChatDetailScreen
import com.example.absensikaryawan.screens.AdminChatListScreen
import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.AdminSettingsScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.BantuanScreen
import com.example.absensikaryawan.screens.ChatAdminScreen
import com.example.absensikaryawan.screens.DetailPengajuanScreen
import com.example.absensikaryawan.screens.ForgotPasswordScreen
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


private val BottomNavGreen =
    Color(0xFF2E7D32)


// ==========================================================
// APP SCREEN
// ==========================================================

private enum class AppScreen {

    Login,
    ForgotPassword,

    // ======================================================
    // ADMIN
    // ======================================================

    Admin,
    AdminChat,
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
    ChatAdmin,
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

private val staffBottomMenuItems = listOf(

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
// ADMIN BOTTOM MENU
// ==========================================================

private val adminBottomMenuItems = listOf(

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
    // CONTEXT
    // ======================================================

    val context: Context =
        LocalContext.current


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

    var currentScreen by remember {
        mutableStateOf(
            AppScreen.Login
        )
    }


    // ======================================================
    // PENGAJUAN TERPILIH
    // ======================================================

    var pengajuanTerpilih by remember {
        mutableStateOf<Map<String, Any>>(
            emptyMap()
        )
    }


    // ======================================================
    // CHAT ROOM TERPILIH
    // ======================================================

    var chatRoomTerpilih by remember {
        mutableStateOf<ChatRoom?>(null)
    }


    // ======================================================
    // FILTER PENGAJUAN
    // ======================================================

    var filterStatusPengajuan by remember {
        mutableStateOf("semua")
    }


    // ======================================================
    // REFRESH DASHBOARD
    // ======================================================

    val refreshKey =
        remember {
            mutableIntStateOf(0)
        }


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope =
        rememberCoroutineScope()


    // ======================================================
    // AREA ADMIN
    // ======================================================

    val isAdminArea =
        when (currentScreen) {

            AppScreen.Admin,
            AppScreen.AdminChat,
            AppScreen.Approval,
            AppScreen.Karyawan,
            AppScreen.AdminRekap,
            AppScreen.AdminSettings,
            AppScreen.AdminProfile,
            AppScreen.AdminTampilan,
            AppScreen.AdminBantuan,
            AppScreen.AdminTentangAplikasi -> true

            else -> false
        }


    // ======================================================
    // AREA STAFF
    // ======================================================

    val isStaffArea =
        when (currentScreen) {

            AppScreen.Staff,
            AppScreen.Profile,
            AppScreen.Pengajuan,
            AppScreen.PengajuanBaru,
            AppScreen.RiwayatPengajuan,
            AppScreen.DetailPengajuan,
            AppScreen.Scan,
            AppScreen.AbsenLuarKantor,
            AppScreen.Riwayat,
            AppScreen.Settings,
            AppScreen.ChatAdmin,
            AppScreen.Tampilan,
            AppScreen.Notifikasi,
            AppScreen.Bantuan,
            AppScreen.TentangAplikasi -> true

            else -> false
        }


    // ==========================================================
    // SCAFFOLD
    // ==========================================================

    Scaffold(

        bottomBar = {

            // ==================================================
            // ADMIN NAVIGATION
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
                                    currentScreen ==
                                            AppScreen.Admin

                                AppScreen.Approval ->
                                    currentScreen ==
                                            AppScreen.Approval

                                AppScreen.Karyawan ->
                                    currentScreen ==
                                            AppScreen.Karyawan

                                AppScreen.AdminRekap ->
                                    currentScreen ==
                                            AppScreen.AdminRekap

                                AppScreen.AdminSettings ->
                                    currentScreen ==
                                            AppScreen.AdminSettings ||
                                            currentScreen ==
                                            AppScreen.AdminProfile ||
                                            currentScreen ==
                                            AppScreen.AdminTampilan ||
                                            currentScreen ==
                                            AppScreen.AdminBantuan ||
                                            currentScreen ==
                                            AppScreen.AdminTentangAplikasi

                                else ->
                                    false
                            }


                        NavigationBarItem(

                            selected =
                                isSelected,

                            onClick = {

                                currentScreen =
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
            // STAFF NAVIGATION
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
                                    currentScreen ==
                                            AppScreen.Staff

                                AppScreen.Pengajuan ->
                                    currentScreen ==
                                            AppScreen.Pengajuan

                                AppScreen.Scan ->
                                    currentScreen ==
                                            AppScreen.Scan

                                AppScreen.Riwayat ->
                                    currentScreen ==
                                            AppScreen.Riwayat

                                AppScreen.Settings ->
                                    currentScreen ==
                                            AppScreen.Settings ||
                                            currentScreen ==
                                            AppScreen.Profile ||
                                            currentScreen ==
                                            AppScreen.ChatAdmin ||
                                            currentScreen ==
                                            AppScreen.Tampilan ||
                                            currentScreen ==
                                            AppScreen.Notifikasi ||
                                            currentScreen ==
                                            AppScreen.Bantuan ||
                                            currentScreen ==
                                            AppScreen.TentangAplikasi

                                else ->
                                    false
                            }


                        NavigationBarItem(

                            selected =
                                isSelected,

                            onClick = {

                                currentScreen =
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


        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
        ) {


            when (currentScreen) {

                // ==================================================
                // LOGIN
                // ==================================================

                AppScreen.Login -> {

                    LoginScreen(

                        onStaffLogin = {

                            refreshKey.intValue++

                            currentScreen =
                                AppScreen.Staff
                        },

                        onAdminLogin = {

                            currentScreen =
                                AppScreen.Admin
                        },

                        onForgotPassword = {

                            currentScreen =
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

                            currentScreen =
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

                            currentScreen =
                                AppScreen.Approval
                        },

                        onEmployees = {

                            currentScreen =
                                AppScreen.Karyawan
                        },

                        onRecap = {

                            currentScreen =
                                AppScreen.AdminRekap
                        },

                        onSettings = {

                            currentScreen =
                                AppScreen.AdminSettings
                        },

                        onChat = {

                            currentScreen =
                                AppScreen.AdminChat
                        }
                    )
                }



                // ==================================================
                // ADMIN CHAT
                // ==================================================

                AppScreen.AdminChat -> {

                    val room =
                        chatRoomTerpilih

                    if (room == null) {

                        AdminChatListScreen(

                            onBack = {

                                currentScreen =
                                    AppScreen.Admin
                            },

                            onStaffClick = { selectedRoom ->

                                chatRoomTerpilih =
                                    selectedRoom
                            }
                        )

                    } else {

                        AdminChatDetailScreen(

                            room = room,

                            onBack = {

                                chatRoomTerpilih =
                                    null
                            }
                        )
                    }
                }


                // ==================================================
                // ADMIN APPROVAL
                // ==================================================

                AppScreen.Approval -> {

                    ApprovalScreen(

                        onDetailClick = { pengajuan ->

                            pengajuanTerpilih =
                                pengajuan

                            currentScreen =
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

                            currentScreen =
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

                            currentScreen =
                                AppScreen.AdminTampilan
                        },

                        onBantuan = {

                            currentScreen =
                                AppScreen.AdminBantuan
                        },

                        onTentangAplikasi = {

                            currentScreen =
                                AppScreen.AdminTentangAplikasi
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen =
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

                            currentScreen =
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
                                "MODE ADMIN = $mode"
                            )
                        },

                        onBack = {

                            currentScreen =
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

                            currentScreen =
                                AppScreen.AdminSettings
                        },

                        onChatAdmin = {

                            currentScreen =
                                AppScreen.AdminChat
                        }
                    )
                }


                // ==================================================
                // ADMIN TENTANG
                // ==================================================

                AppScreen.AdminTentangAplikasi -> {

                    TentangAplikasiScreen(

                        onBack = {

                            currentScreen =
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

                            currentScreen =
                                AppScreen.Scan
                        },

                        onAbsenLuarKantor = {

                            currentScreen =
                                AppScreen.AbsenLuarKantor
                        },

                        onProfile = {

                            currentScreen =
                                AppScreen.Profile
                        },

                        onHistory = {

                            currentScreen =
                                AppScreen.Riwayat
                        },

                        onHistoryPulang = {

                            currentScreen =
                                AppScreen.Riwayat
                        },

                        onSettings = {

                            currentScreen =
                                AppScreen.Settings
                        },

                        onPengajuan = {

                            currentScreen =
                                AppScreen.Pengajuan
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen =
                                AppScreen.Login
                        },

                        onNotification = {

                            currentScreen =
                                AppScreen.Notifikasi
                        }
                    )
                }


                // ==================================================
                // ABSEN LUAR KANTOR
                // ==================================================

                AppScreen.AbsenLuarKantor -> {

                    AbsenLuarKantorScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Staff
                        },

                        onKirim = {
                                lokasi,
                                alasan ->

                            scope.launch {

                                try {

                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser


                                    if (currentUser == null) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "USER BELUM LOGIN"
                                        )

                                        currentScreen =
                                            AppScreen.Login

                                        return@launch
                                    }


                                    val uid =
                                        currentUser.uid


                                    val hasilNama =
                                        userRepository
                                            .getCurrentUserName()


                                    val nama =
                                        hasilNama
                                            .getOrNull()
                                            ?: ""


                                    if (nama.isBlank()) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "NAMA USER TIDAK DITEMUKAN"
                                        )

                                        return@launch
                                    }


                                    if (lokasi.isBlank()) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "LOKASI KOSONG"
                                        )

                                        return@launch
                                    }


                                    if (alasan.isBlank()) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "ALASAN KOSONG"
                                        )

                                        return@launch
                                    }


                                    val tanggal =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    val jam =
                                        SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    val absenHariIni =
                                        firestoreRepository
                                            .getAbsenHariIni(
                                                uid = uid,
                                                tanggal = tanggal
                                            )


                                    if (absenHariIni != null) {

                                        Log.d(
                                            "ABSEN_LUAR_KANTOR",
                                            "SUDAH ABSEN HARI INI"
                                        )

                                        refreshKey.intValue++

                                        currentScreen =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    val hasilSimpan =
                                        firestoreRepository
                                            .simpanAbsenLuarKantor(

                                                uid = uid,

                                                nama = nama,

                                                tanggal = tanggal,

                                                jamMasuk = jam,

                                                lokasi = lokasi,

                                                alasan = alasan
                                            )


                                    if (hasilSimpan.isFailure) {

                                        Log.e(
                                            "ABSEN_LUAR_KANTOR",
                                            "GAGAL SIMPAN",
                                            hasilSimpan.exceptionOrNull()
                                        )

                                        return@launch
                                    }


                                    val catatanLokal =
                                        "Lokasi: $lokasi\nAlasan: $alasan"


                                    absensiDataStore
                                        .simpanAbsen(

                                            jam = jam,

                                            tanggal = tanggal,

                                            qrData = "LUAR_KANTOR",

                                            catatan =
                                                catatanLokal
                                        )


                                    refreshKey.intValue++


                                    Log.d(
                                        "ABSEN_LUAR_KANTOR",
                                        "BERHASIL"
                                    )


                                    currentScreen =
                                        AppScreen.Staff

                                } catch (e: Exception) {

                                    Log.e(
                                        "ABSEN_LUAR_KANTOR",
                                        "ERROR",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // PROFILE STAFF
                // ==================================================

                AppScreen.Profile -> {

                    ProfileScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Staff
                        }
                    )
                }


                // ==================================================
                // PENGAJUAN
                // ==================================================

                AppScreen.Pengajuan -> {

                    PengajuanScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Staff
                        },

                        onPengajuanBaru = {

                            currentScreen =
                                AppScreen.PengajuanBaru
                        },

                        onStatusClick = { pengajuan ->

                            val filter =
                                pengajuan[
                                    "filterStatus"
                                ]?.toString()


                            if (filter != null) {

                                filterStatusPengajuan =
                                    filter

                                currentScreen =
                                    AppScreen.RiwayatPengajuan

                            } else {

                                pengajuanTerpilih =
                                    pengajuan

                                currentScreen =
                                    AppScreen.DetailPengajuan
                            }
                        }
                    )
                }


                // ==================================================
                // RIWAYAT PENGAJUAN
                // ==================================================

                AppScreen.RiwayatPengajuan -> {

                    DetailPengajuanScreen(

                        filterStatus =
                            filterStatusPengajuan,

                        onBack = {

                            currentScreen =
                                AppScreen.Pengajuan
                        },

                        onDetailClick = { pengajuan ->

                            pengajuanTerpilih =
                                pengajuan

                            currentScreen =
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

                            currentScreen =
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

                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser


                                    if (currentUser == null) {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "USER BELUM LOGIN"
                                        )

                                        return@launch
                                    }


                                    val uid =
                                        currentUser.uid


                                    val hasilNama =
                                        userRepository
                                            .getCurrentUserName()


                                    val nama =
                                        hasilNama
                                            .getOrNull()
                                            ?: ""


                                    if (nama.isBlank()) {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "NAMA TIDAK DITEMUKAN"
                                        )

                                        return@launch
                                    }


                                    val tanggal =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    val hasilSimpan =
                                        firestoreRepository
                                            .simpanPengajuan(

                                                uid = uid,

                                                nama = nama,

                                                jenis = jenis,

                                                tanggal = tanggal,

                                                jamPulang = jamPulang,

                                                jamKeluar = jamKeluar,

                                                jamKembali = jamKembali,

                                                tanggalMulai =
                                                    tanggalMulai,

                                                tanggalSelesai =
                                                    tanggalSelesai,

                                                alasan = alasan
                                            )


                                    if (hasilSimpan.isSuccess) {

                                        Log.d(
                                            "PENGAJUAN_DEBUG",
                                            "BERHASIL DISIMPAN"
                                        )

                                        currentScreen =
                                            AppScreen.Pengajuan

                                    } else {

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
                                            "GAGAL SIMPAN",
                                            hasilSimpan.exceptionOrNull()
                                        )
                                    }

                                } catch (e: Exception) {

                                    Log.e(
                                        "PENGAJUAN_DEBUG",
                                        "ERROR",
                                        e
                                    )
                                }
                            }
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

                        onBack = {

                            currentScreen =
                                AppScreen.Pengajuan
                        },

                        onDetailClick = { detail ->

                            pengajuanTerpilih =
                                detail

                            currentScreen =
                                AppScreen.DetailPengajuan
                        }
                    )
                }


                // ==================================================
                // SCAN QR
                // ==================================================

                AppScreen.Scan -> {

                    ScanAbsenScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Staff
                        },

                        onQrScanned = {
                                qrData,
                                catatan ->

                            Log.d(
                                "ABSEN_DEBUG",
                                "QR DATA = $qrData"
                            )


                            scope.launch {

                                try {

                                    if (qrData.isBlank()) {
                                        return@launch
                                    }


                                    val currentUser =
                                        FirebaseAuth
                                            .getInstance()
                                            .currentUser


                                    if (currentUser == null) {

                                        currentScreen =
                                            AppScreen.Login

                                        return@launch
                                    }


                                    val uid =
                                        currentUser.uid


                                    val hasilNama =
                                        userRepository
                                            .getCurrentUserName()


                                    val nama =
                                        hasilNama
                                            .getOrNull()
                                            ?: ""


                                    if (nama.isBlank()) {
                                        return@launch
                                    }


                                    val tanggal =
                                        SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    val jam =
                                        SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(
                                            Date()
                                        )


                                    val absenHariIni =
                                        firestoreRepository
                                            .getAbsenHariIni(
                                                uid = uid,
                                                tanggal = tanggal
                                            )


                                    // =========================================
                                    // ABSEN MASUK
                                    // =========================================

                                    if (absenHariIni == null) {

                                        val hasilSimpan =
                                            firestoreRepository
                                                .simpanAbsenMasuk(

                                                    uid = uid,

                                                    nama = nama,

                                                    tanggal = tanggal,

                                                    jamMasuk = jam,

                                                    qrData = qrData,

                                                    catatan = catatan
                                                )


                                        if (hasilSimpan.isFailure) {
                                            return@launch
                                        }


                                        absensiDataStore
                                            .simpanAbsen(

                                                jam = jam,

                                                tanggal = tanggal,

                                                qrData = qrData,

                                                catatan = catatan
                                            )


                                        refreshKey.intValue++


                                        currentScreen =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // =========================================
                                    // ABSEN PULANG
                                    // =========================================

                                    val documentId =
                                        absenHariIni.documentId


                                    val jamPulangLama =
                                        absenHariIni.jamPulang


                                    if (jamPulangLama.isBlank()) {

                                        val hasilPulang =
                                            firestoreRepository
                                                .simpanAbsenPulang(

                                                    documentId =
                                                        documentId,

                                                    jamPulang =
                                                        jam
                                                )


                                        if (hasilPulang.isFailure) {
                                            return@launch
                                        }


                                        absensiDataStore
                                            .simpanPulang(
                                                jam
                                            )


                                        refreshKey.intValue++


                                        currentScreen =
                                            AppScreen.Staff

                                        return@launch
                                    }


                                    // =========================================
                                    // SUDAH MASUK + SUDAH PULANG
                                    // =========================================

                                    refreshKey.intValue++


                                    currentScreen =
                                        AppScreen.Staff

                                } catch (e: Exception) {

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
                // RIWAYAT
                // ==================================================

                AppScreen.Riwayat -> {

                    RiwayatScreen(

                        onBack = {

                            currentScreen =
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


                            currentScreen =
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

                            currentScreen =
                                AppScreen.Staff
                        },

                        onNotification = {

                            currentScreen =
                                AppScreen.Notifikasi
                        },

                        onTampilan = {

                            currentScreen =
                                AppScreen.Tampilan
                        },

                        onChatAdmin = {

                            currentScreen =
                                AppScreen.ChatAdmin
                        },

                        onBantuan = {

                            currentScreen =
                                AppScreen.Bantuan
                        },

                        onTentangAplikasi = {

                            currentScreen =
                                AppScreen.TentangAplikasi
                        },

                        onLogout = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen =
                                AppScreen.Login
                        }
                    )
                }


                // ==================================================
                // CHAT ADMIN STAFF
                // ==================================================

                AppScreen.ChatAdmin -> {

                    ChatAdminScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Settings
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
                                "MODE STAFF = $mode"
                            )
                        },

                        onBack = {

                            currentScreen =
                                AppScreen.Settings
                        }
                    )
                }


                // ==================================================
                // NOTIFIKASI
                // ==================================================

                AppScreen.Notifikasi -> {

                    NotifikasiScreen(

                        onBack = {

                            currentScreen =
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

                            currentScreen =
                                AppScreen.Settings
                        },

                        onChatAdmin = {

                            currentScreen =
                                AppScreen.ChatAdmin
                        }
                    )
                }


                // ==================================================
                // TENTANG APLIKASI STAFF
                // ==================================================

                AppScreen.TentangAplikasi -> {

                    TentangAplikasiScreen(

                        onBack = {

                            currentScreen =
                                AppScreen.Settings
                        }
                    )
                }
            }
        }
    }
}