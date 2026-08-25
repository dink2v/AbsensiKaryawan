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
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Description
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

@Composable
fun PengajuanScreen(
    onBack: () -> Unit,
    onPengajuanBaru: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        tint = TextDark
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Pengajuan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Kelola pengajuan izin dan cuti",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            // ==================================================
            // CONTENT
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // ==================================================
                // INFO
                // ==================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftGreen
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Pengajuan Izin / Cuti",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Ajukan izin, sakit, pulang cepat, " +
                                        "atau cuti kepada admin.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // ==================================================
                // BUAT PENGAJUAN BARU
                // ==================================================

                Text(
                    text = "Pengajuan Baru",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPengajuanBaru()
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Pengajuan Baru",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Buat Pengajuan Baru",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Pulang cepat, izin keluar, " +
                                        "izin terlambat, sakit, atau cuti.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // ==================================================
                // RIWAYAT PENGAJUAN
                // ==================================================

                Text(
                    text = "Status Pengajuan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // ==================================================
                // MENUNGGU
                // ==================================================

                StatusCard(
                    icon = Icons.Default.Pending,
                    title = "Menunggu Persetujuan",
                    description = "Pengajuan yang sedang diperiksa admin.",
                    number = "0"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // ==================================================
                // DISETUJUI
                // ==================================================

                StatusCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Disetujui",
                    description = "Pengajuan yang telah disetujui admin.",
                    number = "0"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // ==================================================
                // TOTAL
                // ==================================================

                StatusCard(
                    icon = Icons.Default.EventNote,
                    title = "Total Pengajuan",
                    description = "Jumlah seluruh pengajuan kamu.",
                    number = "0"
                )

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}


// ==========================================================
// STATUS CARD
// ==========================================================

@Composable
private fun StatusCard(
    icon: ImageVector,
    title: String,
    description: String,
    number: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = SoftGreen,
                        shape = RoundedCornerShape(14.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(25.dp)
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
        }
    }
}