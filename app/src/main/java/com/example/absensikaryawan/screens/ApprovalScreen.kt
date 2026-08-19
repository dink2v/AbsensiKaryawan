package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF1F3A2D)
private val DarkGreen = Color(0xFF093628)
private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF17221C)
private val TextGray = Color(0xFF6B7280)

@Composable
fun ApprovalScreen(
    onBack: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = PrimaryGreen
                    )
                }

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Column {

                    Text(
                        text = "Admin / HRD",
                        fontSize = 13.sp,
                        color = TextGray
                    )

                    Text(
                        text = "Approval Pengajuan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // RINGKASAN
            // =========================

            Text(
                text = "Pengajuan Menunggu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ApprovalSummaryCard(
                    modifier = Modifier.weight(1f),
                    number = "3",
                    label = "Menunggu",
                    icon = Icons.Default.Schedule
                )

                ApprovalSummaryCard(
                    modifier = Modifier.weight(1f),
                    number = "8",
                    label = "Disetujui",
                    icon = Icons.Default.CheckCircle
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // DAFTAR PENGAJUAN
            // =========================

            Text(
                text = "Daftar Pengajuan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ApprovalRequestCard(
                initials = "AW",
                name = "Andi Wijaya",
                department = "IT",
                type = "Cuti Reguler",
                date = "20 - 21 Agustus 2026"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ApprovalRequestCard(
                initials = "SN",
                name = "Siti Nurhaliza",
                department = "Keuangan",
                type = "Izin Sakit",
                date = "19 Agustus 2026"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ApprovalRequestCard(
                initials = "DL",
                name = "Dewi Lestari",
                department = "Marketing",
                type = "Pulang Cepat",
                date = "19 Agustus 2026"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // INFO
            // =========================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE6EEE9)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = "Ketuk pengajuan untuk melihat detail dan melakukan approval.",
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun ApprovalSummaryCard(
    modifier: Modifier,
    number: String,
    label: String,
    icon: ImageVector
) {

    Card(
        modifier = modifier,
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
                .padding(16.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryGreen,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = number,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = label,
                fontSize = 13.sp,
                color = TextGray
            )
        }
    }
}

@Composable
private fun ApprovalRequestCard(
    initials: String,
    name: String,
    department: String,
    type: String,
    date: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Detail approval kita buat pada step berikutnya
            },
        shape = RoundedCornerShape(18.dp),
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
                .padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6EEE9)
                    )
                ) {

                    Text(
                        text = initials,
                        modifier = Modifier.padding(13.dp),
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = department,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Card(
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF4D6)
                    )
                ) {

                    Text(
                        text = "Menunggu",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9A6700)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = when (type) {
                        "Cuti Reguler" -> Icons.Default.Event
                        "Izin Sakit" -> Icons.Default.CheckCircle
                        else -> Icons.Default.Schedule
                    },
                    contentDescription = type,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Column {

                    Text(
                        text = type,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Setujui akan kita sambungkan nanti
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE9F5EC)
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Setujui",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "Setujui",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Tolak akan kita sambungkan nanti
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFCEAEA)
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tolak",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "Tolak",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }
        }
    }
}