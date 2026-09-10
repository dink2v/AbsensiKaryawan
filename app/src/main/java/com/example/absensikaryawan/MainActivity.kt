package com.example.absensikaryawan

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.example.absensikaryawan.navigation.AppNavigation
import com.example.absensikaryawan.data.screens.ThemeMode
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
        //
        // Aplikasi boleh menggambar sampai area system bar.
        //
        // Padding/inset untuk status bar dan navigation bar
        // akan ditangani oleh Compose pada masing-masing screen.
        //

        enableEdgeToEdge()


        // ======================================================
        // SESSION CHECK
        // ======================================================

        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_START
                ) {

                    if (
                        _root_ide_package_.com.example.absensikaryawan.SessionManager.shouldRequireLogin()
                    ) {

                        val firebaseAuth =
                            FirebaseAuth.getInstance()

                        if (
                            firebaseAuth.currentUser != null
                        ) {

                            firebaseAuth.signOut()
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
            // THEME DATASTORE
            // ==================================================

            val themeDataStore =
                _root_ide_package_.com.example.absensikaryawan.ThemeDataStore(
                    applicationContext
                )


            // ==================================================
            // BACA TEMA YANG TERSIMPAN
            // ==================================================

            val selectedThemeMode by
            themeDataStore.themeMode
                .collectAsState(
                    initial =
                        _root_ide_package_.com.example.absensikaryawan.data.screens.ThemeMode.TERANG
                )


            // ==================================================
            // TENTUKAN DARK MODE
            // ==================================================

            val darkTheme =
                when (
                    selectedThemeMode
                ) {

                    _root_ide_package_.com.example.absensikaryawan.data.screens.ThemeMode.TERANG -> {
                        false
                    }

                    _root_ide_package_.com.example.absensikaryawan.data.screens.ThemeMode.GELAP -> {
                        true
                    }

                    _root_ide_package_.com.example.absensikaryawan.data.screens.ThemeMode.SISTEM -> {
                        isSystemInDarkTheme()
                    }

                    else -> {
                        false
                    }
                }


            // ==================================================
            // THEME APLIKASI
            // ==================================================

            AbsensiKaryawanTheme(

                darkTheme =
                    darkTheme,

                dynamicColor =
                    true

            ) {

                // ==================================================
                // NAVIGATION
                // ==================================================

                AppNavigation()
            }
        }
    }
}