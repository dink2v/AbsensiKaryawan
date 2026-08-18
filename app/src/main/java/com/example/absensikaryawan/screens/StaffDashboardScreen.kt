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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StaffDashboardScreen(
    sudahAbsen: Boolean,
    jamAbsen: String,
    onLogout: () -> Unit,
    onAbsenMasuk: () -> Unit
) {

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Dashboard Staf",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Selamat datang kembali 👋",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    tint = Color(0xFF2563EB)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // STATUS ABSENSI
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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Status",
                            tint = Color(0xFF2563EB)
                        )

                        Spacer(
                            modifier = Modifier.padding(
                                horizontal = 6.dp
                            )
                        )

                        Text(
                            text = "Absensi Hari Ini",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    // =========================
                    // STATUS
                    // =========================

                    if (sudahAbsen) {

                        Text(
                            text = "✓ Sudah melakukan absensi",
                            fontSize = 15.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Jam masuk: $jamAbsen",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )

                    } else {

                        Text(
                            text = "Belum melakukan absensi",
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        // =========================
                        // TOMBOL ABSEN
                        // =========================

                        Button(
                            onClick = onAbsenMasuk,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Absen"
                            )

                            Text(
                                text = "  ABSEN MASUK",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // =========================
            // MENU
            // =========================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Izin",
                            tint = Color(0xFF2563EB)
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Izin",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Riwayat"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Riwayat",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // KELUAR
            // =========================

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Keluar"
                )

                Text(
                    text = "  Keluar"
                )
            }
        }
    }
}