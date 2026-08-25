package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RiwayatJamMasukScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "Riwayat Jam Masuk"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Riwayat jam masuk akan ditampilkan di sini."
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Button(
            onClick = onBack
        ) {

            Text(
                text = "Kembali"
            )
        }
    }
}