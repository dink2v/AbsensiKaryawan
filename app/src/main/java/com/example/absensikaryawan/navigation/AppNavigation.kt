package com.example.absensikaryawan.navigation

import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import com.example.absensikaryawan.screens.ForgotPasswordScreen
import com.example.absensikaryawan.screens.LoginScreen

// ==========================================================
// ROLE APLIKASI
// ==========================================================

enum class AppRole {
    NONE,
    ADMIN,
    STAFF
}

// ==========================================================
// APP NAVIGATION
// ==========================================================

@Composable
fun AppNavigation() {

    // ======================================================
    // STATE ROLE
    // ======================================================

    var currentRole by remember {
        mutableStateOf(AppRole.NONE)
    }

    // ======================================================
    // FORGOT PASSWORD
    // ======================================================

    var showForgotPassword by remember {
        mutableStateOf(false)
    }

    // ======================================================
    // FORGOT PASSWORD SCREEN
    // ======================================================

    if (showForgotPassword) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            ForgotPasswordScreen(
                onBack = {

                    Log.d(
                        "APP_NAV",
                        "KEMBALI DARI FORGOT PASSWORD"
                    )

                    showForgotPassword = false
                }
            )
        }

        return
    }

    // ======================================================
    // ADMIN NAVIGATION
    // ======================================================

    if (currentRole == AppRole.ADMIN) {

        AdminNavigation(
            onLogout = {

                Log.d(
                    "APP_NAV",
                    "================================"
                )

                Log.d(
                    "APP_NAV",
                    "ADMIN LOGOUT"
                )

                Log.d(
                    "APP_NAV",
                    "KEMBALI KE LOGIN"
                )

                currentRole = AppRole.NONE
            }
        )

        return
    }

    // ======================================================
    // STAFF NAVIGATION
    // ======================================================

    if (currentRole == AppRole.STAFF) {

        StaffNavigation(
            onLogout = {

                Log.d(
                    "APP_NAV",
                    "================================"
                )

                Log.d(
                    "APP_NAV",
                    "STAFF LOGOUT"
                )

                Log.d(
                    "APP_NAV",
                    "KEMBALI KE LOGIN"
                )

                currentRole = AppRole.NONE
            }
        )

        return
    }

    // ======================================================
    // LOGIN SCREEN
    // ======================================================

    LoginScreen(

        // ==================================================
        // STAFF LOGIN
        // ==================================================

        onStaffLogin = {

            Log.d(
                "APP_NAV",
                "================================"
            )

            Log.d(
                "APP_NAV",
                "STAFF LOGIN BERHASIL"
            )

            currentRole = AppRole.STAFF

            Log.d(
                "APP_NAV",
                "ROLE = $currentRole"
            )
        },

        // ==================================================
        // ADMIN LOGIN
        // ==================================================

        onAdminLogin = {

            Log.d(
                "APP_NAV",
                "================================"
            )

            Log.d(
                "APP_NAV",
                "ADMIN LOGIN BERHASIL"
            )

            currentRole = AppRole.ADMIN

            Log.d(
                "APP_NAV",
                "ROLE = $currentRole"
            )
        },

        // ==================================================
        // FORGOT PASSWORD
        // ==================================================

        onForgotPassword = {

            Log.d(
                "APP_NAV",
                "================================"
            )

            Log.d(
                "APP_NAV",
                "FORGOT PASSWORD DIKLIK"
            )

            showForgotPassword = true
        }
    )
}