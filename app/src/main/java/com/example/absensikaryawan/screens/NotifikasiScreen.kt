package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotifikasiScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp)
    ) {

        // ======================================================
        // HEADER
        // ======================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = PrimaryGreen
                )
            }

            Text(
                text = "Notifikasi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ======================================================
        // NOTIFIKASI ABSENSI
        // ======================================================

        NotificationCard(
            title = "Absensi",
            message = "Riwayat dan status absensi Anda dapat dilihat di menu Riwayat.",
            onClick = {}
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ======================================================
        // NOTIFIKASI PENGAJUAN
        // ======================================================

        NotificationCard(
            title = "Pengajuan",
            message = "Status pengajuan izin atau sakit akan ditampilkan di sini.",
            onClick = {}
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ======================================================
        // INFORMASI
        // ======================================================

        NotificationCard(
            title = "Informasi",
            message = "Belum ada informasi baru.",
            onClick = {}
        )
    }
}


// ==========================================================
// NOTIFICATION CARD
// ==========================================================

@Composable
private fun NotificationCard(
    title: String,
    message: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
    }
}