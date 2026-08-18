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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AbsenMasukScreen(
    onBack: () -> Unit,
    onAbsenSuccess: () -> Unit
) {

    var currentTime by remember {
        mutableStateOf(
            SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        )
    }

    LaunchedEffect(Unit) {

        while (true) {

            currentTime =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            delay(1000)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // =========================
            // HEADER
            // =========================

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
                        contentDescription = "Kembali"
                    )
                }

                Text(
                    text = "Absen Masuk",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // WAKTU
            // =========================

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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Waktu",
                        tint = Color(0xFF2563EB)
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = currentTime,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Text(
                        text = "Waktu saat ini",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // =========================
            // LOKASI TESTING
            // =========================

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
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi",
                            tint = Color(0xFF2563EB)
                        )

                        Spacer(
                            modifier = Modifier.padding(
                                horizontal = 6.dp
                            )
                        )

                        Text(
                            text = "Lokasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Lokasi terdeteksi",
                        fontSize = 15.sp,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Mode Testing",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // KONFIRMASI ABSEN
            // =========================

            Button(
                onClick = onAbsenSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Konfirmasi"
                )

                Text(
                    text = "  KONFIRMASI ABSEN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Pastikan waktu dan lokasi sudah sesuai.",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}