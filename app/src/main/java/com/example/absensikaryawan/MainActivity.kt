package com.example.absensikaryawan

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.absensikaryawan.navigation.AppNavigation
import com.example.absensikaryawan.screens.ThemeMode
import com.example.absensikaryawan.ui.theme.AbsensiKaryawanTheme


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
        // COMPOSE
        // ======================================================

        setContent {

            // ==================================================
            // THEME DATASTORE
            // ==================================================

            val themeDataStore =
                ThemeDataStore(
                    applicationContext
                )


            // ==================================================
            // BACA TEMA YANG TERSIMPAN
            // ==================================================

            val selectedThemeMode by
            themeDataStore.themeMode
                .collectAsState(
                    initial =
                        ThemeMode.TERANG
                )


            // ==================================================
            // TENTUKAN DARK MODE
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