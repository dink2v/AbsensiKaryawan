package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.AbsensiDataStore

@Composable
fun RekapScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val sudahAbsen by absensiDataStore.sudahAbsen.collectAsState(
        initial = false
    )

    val jamAbsen by absensiDataStore.jamAbsen.collectAsState(
        initial = ""
    )

    val tanggalAbsen by absensiDataStore.tanggalAbsen.collectAsState(
        initial = ""
    )

    val jamPulang by absensiDataStore.jamPulang.collectAsState(
        initial = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // HEADER

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextDark
                    )
                }

                Text(
                    text = "Rekap Absensi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            if (sudahAbsen) {

                // TANGGAL

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Tanggal",
                                tint = PrimaryGreen
                            )

                            Spacer(
                                modifier = Modifier.padding(
                                    horizontal = 6.dp
                                )
                            )

                            Text(
                                text = tanggalAbsen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        // JAM MASUK

                        RekapInfoRow(
                            icon = Icons.Default.AccessTime,
                            title = "Jam Masuk",
                            value = if (
                                jamAbsen.isNotEmpty()
                            ) {
                                jamAbsen
                            } else {
                                "--:--:--"
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        // JAM PULANG

                        RekapInfoRow(
                            icon = Icons.Default.ExitToApp,
                            title = "Jam Pulang",
                            value = if (
                                jamPulang.isNotEmpty()
                            ) {
                                jamPulang
                            } else {
                                "--:--:--"
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // STATUS

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDCFCE7)
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,
                            contentDescription = "Hadir",
                            tint = Color(0xFF16A34A)
                        )

                        Spacer(
                            modifier = Modifier.padding(
                                horizontal = 8.dp
                            )
                        )

                        Column {

                            Text(
                                text = "Status Kehadiran",
                                fontSize = 13.sp,
                                color = Color(0xFF166534)
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp)
                            )

                            Text(
                                text = "HADIR",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

            } else {

                // BELUM ADA ABSENSI

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Event,
                            contentDescription = null,
                            tint = TextGray
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Belum Ada Absensi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Belum ada data absensi hari ini.",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RekapInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryGreen
        )

        Spacer(
            modifier = Modifier.padding(
                horizontal = 8.dp
            )
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = TextGray
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}