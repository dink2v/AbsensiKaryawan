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
import androidx.compose.runtime.getValue
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

                                        Log.e(
                                            "PENGAJUAN_DEBUG",
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

                                    val hasilSimpan =
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
                                "================================"
                            )

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

                            Log.d(
                                "ABSEN_DEBUG",
                                "================================"
                            )


                            // ==================================
                            // SEMUA PROSES FIRESTORE
                            // WAJIB DI DALAM COROUTINE
                            // ==================================

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


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "UID = $uid"
                                    )


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


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "NAMA = $nama"
                                    )


                                    // ==================================
                                    // TANGGAL HARI INI
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


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "TANGGAL = $tanggal"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "JAM = $jam"
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


                                    // ==================================================
                                    // KONDISI 1
                                    // BELUM ADA ABSEN HARI INI
                                    //
                                    // => SCAN PERTAMA = ABSEN MASUK
                                    // ==================================================

                                    if (
                                        absenHariIni == null
                                    ) {

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "BELUM ADA ABSEN HARI INI"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "SCAN PERTAMA"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "PROSES = ABSEN MASUK"
                                        )


                                        // ==================================
                                        // SIMPAN ABSEN MASUK FIRESTORE
                                        // ==================================

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


                                        // ==================================
                                        // SIMPAN DATASTORE
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


                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "ABSEN MASUK BERHASIL"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "JAM MASUK = $jam"
                                        )


                                        // ==================================
                                        // REFRESH DASHBOARD
                                        // ==================================

                                        refreshKey++


                                        // ==================================
                                        // KEMBALI BERANDA
                                        // ==================================

                                        currentScreen.value =
                                            AppScreen.Staff


                                        return@launch
                                    }


                                    // ==================================================
                                    // KONDISI 2
                                    // SUDAH ADA ABSEN MASUK
                                    // ==================================================

                                    val (
                                        documentId,
                                        jamPulangLama
                                    ) =
                                        absenHariIni


                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "ABSEN HARI INI DITEMUKAN"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "DOCUMENT ID = $documentId"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "JAM PULANG LAMA = $jamPulangLama"
                                    )


                                    // ==================================================
                                    // CEK ABSEN PULANG
                                    // ==================================================

                                    if (
                                        jamPulangLama.isBlank()
                                    ) {

                                        // ==================================
                                        // SCAN KEDUA
                                        // ==================================

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "SCAN KEDUA"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "PROSES = ABSEN PULANG"
                                        )


                                        // ==================================
                                        // SIMPAN JAM PULANG
                                        // ==================================

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


                                        // ==================================
                                        // DATASTORE
                                        // ==================================

                                        absensiDataStore
                                            .simpanPulang(
                                                jam
                                            )


                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "ABSEN PULANG BERHASIL"
                                        )

                                        Log.d(
                                            "ABSEN_DEBUG",
                                            "JAM PULANG = $jam"
                                        )


                                        // ==================================
                                        // REFRESH DASHBOARD
                                        // ==================================

                                        refreshKey++


                                        // ==================================
                                        // KEMBALI BERANDA
                                        // ==================================

                                        currentScreen.value =
                                            AppScreen.Staff


                                        return@launch
                                    }


                                    // ==================================================
                                    // KONDISI 3
                                    // ABSEN SUDAH LENGKAP
                                    //
                                    // => SCAN KETIGA TIDAK BOLEH MENAMBAH DATA
                                    // ==================================================

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "================================"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "ABSEN SUDAH LENGKAP"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "JAM MASUK SUDAH ADA"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "JAM PULANG SUDAH ADA"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "SCAN KETIGA DIABAIKAN"
                                    )

                                    Log.d(
                                        "ABSEN_DEBUG",
                                        "================================"
                                    )


                                    // ==================================
                                    // TETAP REFRESH
                                    // ==================================

                                    refreshKey++


                                    // ==================================
                                    // KEMBALI BERANDA
                                    // ==================================

                                    currentScreen.value =
                                        AppScreen.Staff

                                } catch (
                                    e: Exception
                                ) {

                                    Log.e(
                                        "ABSEN_DEBUG",
                                        "================================"
                                    )

                                    Log.e(
                                        "ABSEN_DEBUG",
                                        "GAGAL PROSES ABSEN",
                                        e
                                    )

                                    Log.e(
                                        "ABSEN_DEBUG",
                                        "ERROR = ${e.message}"
                                    )

                                    Log.e(
                                        "ABSEN_DEBUG",
                                        "================================"
                                    )
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