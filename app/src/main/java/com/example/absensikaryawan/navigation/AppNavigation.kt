package com.example.absensikaryawan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.absensikaryawan.data.AbsensiDataStore
import com.example.absensikaryawan.screens.AbsenMasukScreen
import com.example.absensikaryawan.screens.AdminLoginScreen
import com.example.absensikaryawan.screens.HomeScreen
import com.example.absensikaryawan.screens.RiwayatAbsensiScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen
import com.example.absensikaryawan.screens.StaffLoginScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val coroutineScope = rememberCoroutineScope()

    val sudahAbsen by absensiDataStore.sudahAbsen
        .collectAsState(initial = false)

    val jamAbsen by absensiDataStore.jamAbsen
        .collectAsState(initial = "")

    val tanggalAbsen by absensiDataStore.tanggalAbsen
        .collectAsState(initial = "")

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // HOME
        composable("home") {

            HomeScreen(
                onStaffClick = {
                    navController.navigate("staff_login")
                },
                onAdminClick = {
                    navController.navigate("admin_login")
                }
            )
        }

        // STAFF LOGIN
        composable("staff_login") {

            StaffLoginScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate("staff_dashboard")
                }
            )
        }

        // ADMIN LOGIN
        composable("admin_login") {

            AdminLoginScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // STAFF DASHBOARD
        composable("staff_dashboard") {

            StaffDashboardScreen(
                sudahAbsen = sudahAbsen,
                jamAbsen = jamAbsen,

                onLogout = {
                    navController.popBackStack()
                },

                onAbsenMasuk = {
                    navController.navigate("absen_masuk")
                },

                onRiwayat = {
                    navController.navigate("riwayat_absensi")
                }
            )
        }

        // ABSEN MASUK
        composable("absen_masuk") {

            AbsenMasukScreen(

                onBack = {
                    navController.popBackStack()
                },

                onAbsenSuccess = {

                    val sekarang = java.util.Date()

                    val jam = java.text.SimpleDateFormat(
                        "HH:mm:ss",
                        java.util.Locale.getDefault()
                    ).format(sekarang)

                    val tanggal = java.text.SimpleDateFormat(
                        "dd/MM/yyyy",
                        java.util.Locale.getDefault()
                    ).format(sekarang)

                    coroutineScope.launch {

                        absensiDataStore.simpanAbsensi(
                            jam = jam,
                            tanggal = tanggal
                        )

                        navController.popBackStack()
                    }
                }
            )
        }

        // RIWAYAT ABSENSI
        composable("riwayat_absensi") {

            RiwayatAbsensiScreen(
                dataStore = absensiDataStore,

                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}