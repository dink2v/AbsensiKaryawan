package com.example.absensikaryawan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.example.absensikaryawan.navigation.AppNavigation
import com.example.absensikaryawan.screens.ThemeMode
import com.example.absensikaryawan.ui.theme.AbsensiKaryawanTheme

import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    // ==========================================================
    // NOTIFICATION PERMISSION
    // Android 13 / API 33+
    // ==========================================================

    private val requestNotificationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            // Permission sudah diproses.
            //
            // Jika true:
            // notifikasi Android diizinkan.
            //
            // Jika false:
            // aplikasi tetap berjalan normal.
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        // ======================================================
        // EDGE TO EDGE
        // ======================================================

        enableEdgeToEdge()


        // ======================================================
        // REQUEST NOTIFICATION PERMISSION
        //
        // Android 13+ membutuhkan permission runtime
        // POST_NOTIFICATIONS.
        // ======================================================

        requestNotificationPermissionIfNeeded()


        // ======================================================
        // SESSION CHECK
        //
        // 06:00 - 17:59
        // Login tetap tersimpan.
        //
        // 18:00 - 05:59
        // User harus login kembali.
        // ======================================================

        lifecycle.addObserver(

            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_START
                ) {

                    if (
                        SessionManager
                            .shouldRequireLogin()
                    ) {

                        val firebaseAuth =
                            FirebaseAuth
                                .getInstance()


                        if (
                            firebaseAuth
                                .currentUser != null
                        ) {

                            firebaseAuth
                                .signOut()
                        }
                    }
                }
            }
        )


        // ======================================================
        // COMPOSE
        // ======================================================

        setContent {

            // ==================================================
            // THEME DATA STORE
            // ==================================================

            val themeDataStore =
                ThemeDataStore(
                    applicationContext
                )


            // ==================================================
            // THEME MODE
            // ==================================================

            val selectedThemeMode by
            themeDataStore
                .themeMode
                .collectAsState(

                    initial =
                        ThemeMode.TERANG
                )


            // ==================================================
            // DARK THEME
            // ==================================================

            val darkTheme =

                when (
                    selectedThemeMode
                ) {

                    ThemeMode.TERANG -> {

                        false
                    }


                    ThemeMode.GELAP -> {

                        true
                    }


                    ThemeMode.SISTEM -> {

                        isSystemInDarkTheme()
                    }


                    else -> {

                        false
                    }
                }


            // ==================================================
            // APP THEME
            // ==================================================

            AbsensiKaryawanTheme(

                darkTheme =
                    darkTheme,

                dynamicColor =
                    true

            ) {

                // ==============================================
                // NAVIGATION
                //
                // JANGAN menggunakan safeDrawingPadding()
                // di level global.
                //
                // Insets akan ditangani oleh masing-masing
                // screen agar posisi TopBar dan BottomBar
                // konsisten dengan system bar Android.
                // ==============================================

                AppNavigation()
            }
        }
    }


    // ==========================================================
    // REQUEST NOTIFICATION PERMISSION
    // ==========================================================

    private fun requestNotificationPermissionIfNeeded() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED


            if (!permissionGranted) {

                requestNotificationPermission.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
}