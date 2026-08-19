package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val LightGreen = Color(0xFFE6EEE9)

@Composable
fun StaffDashboardScreen(
    onScan: () -> Unit,
    onLogout: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp
                    )
            ) {

                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Staff",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Budi Hartono",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Rabu, 19 Agustus 2026",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = PrimaryGreen
                            )
                        }

                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = DarkGreen
                            )
                        ) {

                            Text(
                                text = "BH",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // STATUS KEHADIRAN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkGreen
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "STATUS KEHADIRAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.75f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF69C27D))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "BELUM ABSEN",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // SCAN BESAR
                        Card(
                            modifier = Modifier
                                .size(145.dp)
                                .clickable {
                                    onScan()
                                },
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan Absen",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(52.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "ABSEN",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Scan QR untuk melakukan absensi",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // KEHADIRAN HARI INI
                Text(
                    text = "Kehadiran Hari Ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        AttendanceInfoRow(
                            icon = Icons.Default.AccessTime,
                            title = "Jam Masuk",
                            value = "--:--"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        AttendanceInfoRow(
                            icon = Icons.Default.ExitToApp,
                            title = "Jam Pulang",
                            value = "--:--"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        AttendanceInfoRow(
                            icon = Icons.Default.LocationOn,
                            title = "Lokasi",
                            value = "Belum dicek"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PENGAJUAN
                Text(
                    text = "Pengajuan Saya",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                            shape = RoundedCornerShape(13.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LightGreen
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Pengajuan",
                                tint = PrimaryGreen,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "1 Pengajuan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Menunggu persetujuan HRD",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }

                        Text(
                            text = "Menunggu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A6700)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // LOKASI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGreen
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {

                            Text(
                                text = "Lokasi absensi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "GPS akan diperiksa saat melakukan absensi.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // LOGOUT
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLogout()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
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
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Keluar",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Keluar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // BOTTOM NAVIGATION
            StaffBottomNavigation(
                onScan = onScan
            )
        }
    }
}

@Composable
private fun AttendanceInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            shape = RoundedCornerShape(11.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightGreen
            )
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryGreen,
                modifier = Modifier
                    .padding(10.dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = TextGray
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}

@Composable
private fun StaffBottomNavigation(
    onScan: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            BottomNavItem(
                icon = Icons.Default.WbSunny,
                title = "Beranda",
                selected = true
            )

            Card(
                modifier = Modifier
                    .size(64.dp)
                    .clickable {
                        onScan()
                    },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = DarkGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan Absen",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            BottomNavItem(
                icon = Icons.Default.Person,
                title = "Rekap",
                selected = false
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    title: String,
    selected: Boolean
) {

    val color =
        if (selected) PrimaryGreen else TextGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = color
        )
    }
}