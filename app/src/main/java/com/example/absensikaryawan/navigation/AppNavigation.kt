package com.example.absensikaryawan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.absensikaryawan.screens.AdminDashboardScreen
import com.example.absensikaryawan.screens.ApprovalScreen
import com.example.absensikaryawan.screens.LoginScreen
import com.example.absensikaryawan.screens.ScanAbsenScreen
import com.example.absensikaryawan.screens.StaffDashboardScreen
private enum class AppScreen {
    Login,
    Admin,
    Approval,
    Staff,
    Scan
}

@Composable
fun AppNavigation() {

    val currentScreen = remember {
        mutableStateOf(AppScreen.Login)
    }

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
                    currentScreen.value = AppScreen.Scan
                },

                onLogout = {
                    currentScreen.value = AppScreen.Login
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
                }
            )
        }
    }
}