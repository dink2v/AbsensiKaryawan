package com.example.absensikaryawan

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.absensikaryawan.navigation.AppNavigation


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

            AppNavigation()
        }
    }
}