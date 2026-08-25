package com.example.absensikaryawan.navigation

import android.content.Context
import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.AbsensiDataStore
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.UserRepository

import com.example.absensikaryawan.screens.AbsenBerhasilScreen
import com.example.absensikaryawan.screens.AbsenMasukScreen
import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.LoginScreen
import com.example.absensikaryawan.screens.NotifikasiScreen
import com.example.absensikaryawan.screens.PengajuanBaruScreen
import com.example.absensikaryawan.screens.PengajuanScreen
import com.example.absensikaryawan.screens.ProfileScreen
import com.example.absensikaryawan.screens.RiwayatScreen
import com.example.absensikaryawan.screens.ScanAbsenScreen
import com.example.absensikaryawan.screens.SettingsScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen

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

    Admin,

    Approval,

    Staff,

    Profile,

    Pengajuan,

    PengajuanBaru,

    Scan,

    AbsenMasuk,

    AbsenBerhasil,

    Riwayat,

    Settings,

    Notifikasi
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
// BOTTOM NAVIGATION MENU
// ==========================================================

private val bottomMenuItems = listOf(

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
// APP NAVIGATION
// ==========================================================

@Composable
fun AppNavigation() {

    // ==========================================================
    // REPOSITORY
    // ==========================================================

    val userRepository =
        remember {
            UserRepository()
        }

    val firestoreRepository =
        remember {
            FirestoreRepository()
        }


    // ==========================================================
    // NAVIGATION STATE
    // ==========================================================

    val currentScreen =
        remember {
            mutableStateOf(
                AppScreen.Login
            )
        }


    // ==========================================================
    // REFRESH DASHBOARD
    // ==========================================================

    var refreshKey by remember {
        mutableStateOf(0)
    }


    // ==========================================================
    // COROUTINE
    // ==========================================================

    val scope =
        rememberCoroutineScope()


    // ==========================================================
    // CONTEXT
    // ==========================================================

    val context: Context =
        LocalContext.current


    // ==========================================================
    // DATASTORE
    // ==========================================================

    val absensiDataStore =
        remember {
            AbsensiDataStore(context)
        }


    // ==========================================================
    // BOTTOM NAVIGATION
    // ==========================================================

    val showBottomNavigation =
        currentScreen.value == AppScreen.Staff ||
                currentScreen.value == AppScreen.Pengajuan ||
                currentScreen.value == AppScreen.Scan ||
                currentScreen.value == AppScreen.Riwayat ||
                currentScreen.value == AppScreen.Settings


    // ==========================================================
    // SCAFFOLD
    // ==========================================================

    Scaffold(

        bottomBar = {

            if (showBottomNavigation) {

                NavigationBar(

                    containerColor =
                        Color.White,

                    tonalElevation =
                        6.dp
                ) {

                    bottomMenuItems.forEach { item ->

                        val isSelected =
                            currentScreen.value == item.screen


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


            // ==================================================
            // SCREEN
            // ==================================================

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
                // ADMIN
                // ==================================================

                AppScreen.Admin -> {

                    AdminDashboardScreen(

                        onApproval = {

                            currentScreen.value =
                                AppScreen.Approval
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
                // APPROVAL
                // ==================================================

                AppScreen.Approval -> {

                    ApprovalScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Admin
                        }
                    )
                }


                // ==================================================
                // STAFF / BERANDA
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

                        // ==========================================
                        // NOTIFIKASI
                        // ==========================================

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
                // PROFILE
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
                                    // AMBIL NAMA USER
                                    // ==================================

                                    val hasil =
                                        userRepository
                                            .getCurrentUserName()

                                    val nama =
                                        hasil.getOrNull()
                                            ?: ""

                                    if (
                                        nama.isEmpty()
                                    ) {

                                        println(
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
                                    // SIMPAN PENGAJUAN
                                    // ==================================

                                    firestoreRepository
                                        .simpanPengajuan(

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

                                    println(
                                        "PENGAJUAN BERHASIL DISIMPAN"
                                    )

                                    currentScreen.value =
                                        AppScreen.Pengajuan

                                } catch (
                                    e: Exception
                                ) {

                                    println(
                                        "GAGAL SIMPAN PENGAJUAN: ${e.message}"
                                    )

                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // SCAN QR
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

                            // ==================================
                            // LOG QR
                            // ==================================

                            Log.d(
                                "ABSEN_DEBUG",
                                "===== QR TERBACA ====="
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

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "QR kosong"
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

                                        println(
                                            "USER BELUM LOGIN"
                                        )

                                        return@launch
                                    }


                                    // ==================================
                                    // UID
                                    // ==================================

                                    val uid =
                                        currentUser.uid


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "UID LOGIN = $uid"
                                    )


                                    // ==================================
                                    // NAMA USER
                                    // ==================================

                                    val hasil =
                                        userRepository
                                            .getCurrentUserName()

                                    val nama =
                                        hasil.getOrNull()
                                            ?: ""


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "NAMA USER = $nama"
                                    )


                                    if (
                                        nama.isEmpty()
                                    ) {

                                        println(
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


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "TANGGAL = $tanggal"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "JAM = $jam"
                                    )


                                    // ==================================
                                    // CARI ABSEN HARI INI
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


                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "HASIL SIMPAN = ${hasilSimpan.isSuccess}"
                                        )


                                        if (
                                            hasilSimpan.isFailure
                                        ) {

                                            Log.e(
                                                "ABSEN_DEBUG",
                                                "ERROR FIRESTORE = ${
                                                    hasilSimpan
                                                        .exceptionOrNull()
                                                        ?.message
                                                }",
                                                hasilSimpan
                                                    .exceptionOrNull()
                                            )

                                            return@launch
                                        }


                                        // ==================================
                                        // DATASTORE
                                        // ==================================

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


                                        println(
                                            "================================"
                                        )

                                        println(
                                            "ABSEN MASUK BERHASIL"
                                        )

                                        println(
                                            "UID : $uid"
                                        )

                                        println(
                                            "NAMA : $nama"
                                        )

                                        println(
                                            "TANGGAL : $tanggal"
                                        )

                                        println(
                                            "JAM MASUK : $jam"
                                        )

                                        println(
                                            "================================"
                                        )

                                    } else {

                                        // ==================================
                                        // ABSEN PULANG
                                        // ==================================

                                        val documentId =
                                            absenHariIni.first

                                        val jamPulangLama =
                                            absenHariIni.second


                                        if (
                                            jamPulangLama.isEmpty()
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
                                                hasilPulang.isSuccess
                                            ) {

                                                absensiDataStore
                                                    .simpanPulang(
                                                        jam
                                                    )

                                                println(
                                                    "================================"
                                                )

                                                println(
                                                    "ABSEN PULANG BERHASIL"
                                                )

                                                println(
                                                    "UID : $uid"
                                                )

                                                println(
                                                    "NAMA : $nama"
                                                )

                                                println(
                                                    "JAM PULANG : $jam"
                                                )

                                                println(
                                                    "================================"
                                                )

                                            } else {

                                                println(
                                                    "GAGAL SIMPAN ABSEN PULANG: " +
                                                            hasilPulang
                                                                .exceptionOrNull()
                                                                ?.message
                                                )

                                                return@launch
                                            }

                                        } else {

                                            println(
                                                "ABSEN SUDAH LENGKAP"
                                            )
                                        }
                                    }


                                    // ==================================
                                    // REFRESH BERANDA
                                    // ==================================

                                    refreshKey++


                                    // ==================================
                                    // KEMBALI KE BERANDA
                                    // ==================================

                                    currentScreen.value =
                                        AppScreen.Staff

                                } catch (
                                    e: Exception
                                ) {

                                    println(
                                        "================================"
                                    )

                                    println(
                                        "GAGAL PROSES ABSEN"
                                    )

                                    println(
                                        "ERROR : ${e.message}"
                                    )

                                    println(
                                        "================================"
                                    )

                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }


                // ==================================================
                // ABSEN MASUK LAMA
                // ==================================================

                AppScreen.AbsenMasuk -> {

                    AbsenMasukScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        },

                        onAbsenSuccess = {

                            refreshKey++

                            currentScreen.value =
                                AppScreen.AbsenBerhasil
                        }
                    )
                }


                // ==================================================
                // ABSEN BERHASIL
                // ==================================================

                AppScreen.AbsenBerhasil -> {

                    AbsenBerhasilScreen(

                        onSelesai = {

                            refreshKey++

                            currentScreen.value =
                                AppScreen.Staff
                        }
                    )
                }


                // ==================================================
                // RIWAYAT
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
                // SETTINGS
                // ==================================================

                AppScreen.Settings -> {

                    SettingsScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
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
                // NOTIFIKASI
                // ==================================================

                AppScreen.Notifikasi -> {

                    NotifikasiScreen(

                        onBack = {

                            currentScreen.value =
                                AppScreen.Staff
                        }
                    )
                }
            }
        }
    }
}