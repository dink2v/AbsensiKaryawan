package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.absensikaryawan.data.AbsensiDataStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RiwayatAbsensiScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val jamMasuk by absensiDataStore
        .jamAbsen
        .collectAsState(initial = "")

    val jamPulang by absensiDataStore
        .jamPulang
        .collectAsState(initial = "")

    val tanggalAbsen by absensiDataStore
        .tanggalAbsen
        .collectAsState(initial = "")

    val catatan by absensiDataStore
        .catatanAbsen
        .collectAsState(initial = "")

    val tanggalTampil =
        if (tanggalAbsen.isNotEmpty()) {

            try {

                val input =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                val output =
                    SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale("id", "ID")
                    )

                output.format(
                    input.parse(tanggalAbsen)
                        ?: Date()
                )

            } catch (e: Exception) {

                tanggalAbsen
            }

        } else {

            SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale("id", "ID")
            ).format(Date())
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // ==========================================
            // HEADER
            // ==========================================

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
                        tint = TextDark
                    )
                }

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "Riwayat Absensi",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==========================================
            // TANGGAL
            // ==========================================

            Text(
                text = tanggalTampil,
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ==========================================
            // JAM MASUK
            // ==========================================

            HistoryCard(
                icon = Icons.Default.AccessTime,
                title = "Jam Masuk",
                value =
                    if (jamMasuk.isNotEmpty()) {
                        jamMasuk
                    } else {
                        "--:--"
                    },
                iconBackground = SoftGreen,
                iconColor = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // ==========================================
            // CATATAN
            // ==========================================

            HistoryCard(
                icon = Icons.Default.Description,
                title = "Catatan",
                value =
                    if (catatan.isNotEmpty()) {
                        catatan
                    } else {
                        "Tidak ada catatan"
                    },
                iconBackground = SoftGreen,
                iconColor = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // ==========================================
            // JAM PULANG
            // ==========================================

            HistoryCard(
                icon = Icons.Default.ExitToApp,
                title = "Jam Pulang",
                value =
                    if (jamPulang.isNotEmpty()) {
                        jamPulang
                    } else {
                        "--:--"
                    },
                iconBackground = SoftGreen,
                iconColor = PrimaryGreen
            )
        }
    }
}


// ======================================================
// HISTORY CARD
// ======================================================

@Composable
private fun HistoryCard(
    icon: ImageVector,
    title: String,
    value: String,
    iconBackground: Color,
    iconColor: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = iconBackground
                )
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier
                        .padding(11.dp)
                        .size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextGray
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}