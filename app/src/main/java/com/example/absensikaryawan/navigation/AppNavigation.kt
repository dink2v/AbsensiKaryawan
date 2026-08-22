package com.example.absensikaryawan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.UserRepository
import com.example.absensikaryawan.screens.AbsenBerhasilScreen
import com.example.absensikaryawan.screens.AbsenMasukScreen
import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.LoginScreen
import com.example.absensikaryawan.screens.ProfileScreen
import com.example.absensikaryawan.screens.ScanAbsenScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AppScreen {
    Login,
    Admin,
    Approval,
    Staff,
    Profile,
    Scan,
    AbsenMasuk,
    AbsenBerhasil
}

@Composable
fun AppNavigation() {

    val userRepository = remember {
        UserRepository()
    }

    val currentScreen = remember {
        mutableStateOf(AppScreen.Login)
    }

    val firestoreRepository = remember {
        FirestoreRepository()
    }

    val scope = rememberCoroutineScope()

    when (currentScreen.value) {

        // =========================
        // LOGIN
        // =========================

        AppScreen.Login -> {

            LoginScreen(

                onStaffLogin = {
                    currentScreen.value = AppScreen.Staff
                },

                onAdminLogin = {
                    currentScreen.value = AppScreen.Admin
                }
            )
        }

        // =========================
        // ADMIN
        // =========================

        AppScreen.Admin -> {

            AdminDashboardScreen(

                onApproval = {
                    currentScreen.value = AppScreen.Approval
                },

                onLogout = {
                    currentScreen.value = AppScreen.Login
                }
            )
        }

        // =========================
        // APPROVAL
        // =========================

        AppScreen.Approval -> {

            ApprovalScreen(

                onBack = {
                    currentScreen.value = AppScreen.Admin
                }
            )
        }

        // =========================
        // STAFF
        // =========================

        AppScreen.Staff -> {

            StaffDashboardScreen(
                onScan = {
                    currentScreen.value = AppScreen.ScanAbsen
                },

                onProfile = {
                    currentScreen.value = AppScreen.Profile
                },

                onHistory = {
                    currentScreen.value = AppScreen.RiwayatAbsensi
                },

                onSettings = {
                    currentScreen.value = AppScreen.Settings
                },

                onLogout = {
                    currentScreen.value = AppScreen.Login
                }
            )
        }

        // =========================
        // PROFILE
        // =========================

        AppScreen.Profile -> {

            ProfileScreen(
                onBack = {
                    currentScreen.value = AppScreen.Staff
                }
            )
        }

        // =========================
        // SCAN ABSEN
        // =========================

        AppScreen.Scan -> {

            ScanAbsenScreen(

                onBack = {
                    currentScreen.value = AppScreen.Staff
                },

                onQrScanned = { qrData ->

                    scope.launch {

                        try {

                            if (qrData != "svg") {

                                println(
                                    "QR TIDAK VALID: $qrData"
                                )

                                return@launch
                            }

                            val hasil =
                                userRepository
                                    .getCurrentUserName()

                            if (hasil.isSuccess) {

                                val nama =
                                    hasil.getOrNull() ?: ""

                                val tanggal =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.getDefault()
                                    ).format(Date())

                                val jam =
                                    SimpleDateFormat(
                                        "HH:mm:ss",
                                        Locale.getDefault()
                                    ).format(Date())

                                firestoreRepository
                                    .simpanAbsenMasuk(
                                        nama = nama,
                                        tanggal = tanggal,
                                        jamMasuk = jam,
                                        qrData = qrData,
                                        catatan = ""
                                    )

                                println(
                                    "ABSEN BERHASIL DISIMPAN"
                                )

                            } else {

                                println(
                                    "GAGAL AMBIL USER: ${
                                        hasil.exceptionOrNull()
                                            ?.message
                                    }"
                                )
                            }

                        } catch (e: Exception) {

                            println(
                                "GAGAL SIMPAN ABSEN: ${e.message}"
                            )

                            e.printStackTrace()
                        }
                    }
                }
            )
        }

        // =========================
        // ABSEN MASUK
        // =========================

        AppScreen.AbsenMasuk -> {

            AbsenMasukScreen(

                onBack = {
                    currentScreen.value = AppScreen.Staff
                },

                onAbsenSuccess = {
                    currentScreen.value =
                        AppScreen.AbsenBerhasil
                }
            )
        }

        // =========================
        // ABSEN BERHASIL
        // =========================

        AppScreen.AbsenBerhasil -> {

            AbsenBerhasilScreen(

                onSelesai = {
                    currentScreen.value = AppScreen.Staff
                }
            )
        }
    }
}