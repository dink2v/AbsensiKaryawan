package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.absensikaryawan.data.AbsensiDataStore

@Composable
fun RiwayatAbsensiScreen(
    dataStore: AbsensiDataStore,
    onBack: () -> Unit
) {

    val sudahAbsen by dataStore.sudahAbsen.collectAsState(initial = false)
    val tanggalAbsen by dataStore.tanggalAbsen.collectAsState(initial = "")
    val jamAbsen by dataStore.jamAbsen.collectAsState(initial = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Riwayat Absensi"
        )

        if (sudahAbsen) {

            Text(
                text = "Tanggal: $tanggalAbsen",
                modifier = Modifier.padding(top = 20.dp)
            )

            Text(
                text = "Jam Masuk: $jamAbsen",
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Status: Hadir",
                modifier = Modifier.padding(top = 8.dp)
            )

        } else {

            Text(
                text = "Belum ada data absensi.",
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
        ) {
            Text("Kembali")
        }
    }
}