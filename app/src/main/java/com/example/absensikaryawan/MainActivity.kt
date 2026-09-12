package com.example.absensikaryawan

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.example.absensikaryawan.navigation.AppNavigation
import com.example.absensikaryawan.screens.ThemeMode
import com.example.absensikaryawan.ui.theme.AbsensiKaryawanTheme

import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

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
                // SAFE AREA APLIKASI
                //
                // Mencegah konten aplikasi masuk ke:
                // - Status Bar Android
                // - Navigation Bar Android
                // ==============================================

                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {


                    // ==========================================
                    // NAVIGATION
                    // ==========================================

                    AppNavigation()
                }
            }
        }
    }
}