package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
private val PrimaryGreen = Color(0xFF1F3A2D)
private val DarkGreen = Color(0xFF093628)
private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF17221C)
private val TextGray = Color(0xFF6B7280)


@Composable
fun ScanAbsenScreen(
    onBack: () -> Unit
) {

    val flashOn = remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =========================
            // HEADER
            // =========================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Absensi",
                        fontSize = 13.sp,
                        color = Color.White.copy(
                            alpha = 0.7f
                        )
                    )

                    Text(
                        text = "Scan QR Code",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        flashOn.value = !flashOn.value
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = if (flashOn.value) {
                            Color(0xFFFFD54F)
                        } else {
                            Color.White
                        }
                    )
                }
            }

            // =========================
            // AREA SCANNER
            // =========================

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF101412)),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Kotak scanner
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        // Ikon QR di tengah
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "QR Scanner",
                            tint = Color.White.copy(
                                alpha = 0.35f
                            ),
                            modifier = Modifier.size(90.dp)
                        )

                        // Garis scan
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(
                                    PrimaryGreen
                                )
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )

                    Text(
                        text = "Arahkan QR Code ke dalam kotak",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Pastikan QR Code terlihat jelas dan tidak terhalang",
                        fontSize = 12.sp,
                        color = Color.White.copy(
                            alpha = 0.65f
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // =========================
            // BOTTOM PANEL
            // =========================

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Background
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Scan Absensi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Scan QR Code yang tersedia di lokasi kerja.",
                        fontSize = 13.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // Tombol simulasi
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Scanner asli akan kita sambungkan
                                // pada tahap berikutnya.
                            },
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkGreen
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(21.dp)
                            )

                            Spacer(
                                modifier = Modifier.size(8.dp)
                            )

                            Text(
                                text = "Mulai Scan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "GPS akan diperiksa setelah QR berhasil dibaca.",
                        fontSize = 11.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
