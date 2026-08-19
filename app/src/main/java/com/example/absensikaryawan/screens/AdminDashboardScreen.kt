package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onApproval: () -> Unit
) {

    var sidebarOpen by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
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
                        onClick = {
                            sidebarOpen = true
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = PrimaryGreen
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Admin / HRD",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "Budi Hartono",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "19 Agustus 2026",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkGreen
                        )
                    ) {

                        Text(
                            text = "BH",
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
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
                    text = "Ringkasan kehadiran hari ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    AdminSummaryCard(
                        modifier = Modifier.weight(1f),
                        number = "4",
                        label = "Hadir",
                        icon = Icons.Default.CheckCircle
                    )

                    AdminSummaryCard(
                        modifier = Modifier.weight(1f),
                        number = "1",
                        label = "Terlambat",
                        icon = Icons.Default.Schedule
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    AdminSummaryCard(
                        modifier = Modifier.weight(1f),
                        number = "1",
                        label = "Izin",
                        icon = Icons.Default.EventAvailable
                    )

                    AdminSummaryCard(
                        modifier = Modifier.weight(1f),
                        number = "1",
                        label = "Alpha",
                        icon = Icons.Default.Warning
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // =========================
                // PENGAJUAN
                // =========================

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onApproval()
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkGreen
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
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Pengajuan",
                                tint = Color.White
                            )

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Text(
                                text = "3",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "Pengajuan menunggu approval",
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Ketuk untuk meninjau pengajuan karyawan",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // =========================
                // PERLU DITINJAU
                // =========================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Perlu Ditinjau",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Lihat semua",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreen
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                AdminApprovalCard(
                    initials = "AW",
                    name = "Andi Wijaya",
                    type = "Cuti Reguler"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AdminApprovalCard(
                    initials = "SN",
                    name = "Siti Nurhaliza",
                    type = "Izin Sakit"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                AdminApprovalCard(
                    initials = "DL",
                    name = "Dewi Lestari",
                    type = "Pulang Cepat"
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // =========================
                // MENU CEPAT
                // =========================

                Text(
                    text = "Menu Cepat",
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

                    QuickMenuCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.QrCodeScanner,
                        title = "Scan Absen"
                    )

                    QuickMenuCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Assessment,
                        title = "Rekap Semua"
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // =========================
                // LOGOUT
                // =========================

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLogout()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Keluar",
                            tint = Color(0xFFB91C1C)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Keluar",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

            // =========================
            // SIDEBAR
            // =========================

            if (sidebarOpen) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.35f)
                        )
                        .clickable {
                            sidebarOpen = false
                        }
                )

                AdminSidebar(
                    onClose = {
                        sidebarOpen = false
                    },
                    onLogout = {
                        sidebarOpen = false
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminSidebar(
    onClose: () -> Unit,
    onLogout: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(290.dp)
            .background(
                Color.White,
                RoundedCornerShape(
                    topEnd = 24.dp,
                    bottomEnd = 24.dp
                )
            )
            .clickable { }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkGreen
                    )
                ) {

                    Text(
                        text = "A",
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Absensi Karyawan",
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Admin / HRD",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                IconButton(
                    onClick = onClose
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextDark
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            AdminMenuItem(
                icon = Icons.Default.Dashboard,
                title = "Dashboard",
                selected = true
            )

            AdminMenuItem(
                icon = Icons.Default.Group,
                title = "Data Karyawan"
            )

            AdminMenuItem(
                icon = Icons.Default.CheckCircle,
                title = "Approval Pengajuan"
            )

            AdminMenuItem(
                icon = Icons.Default.Assessment,
                title = "Rekap Kehadiran"
            )

            AdminMenuItem(
                icon = Icons.Default.Settings,
                title = "Setting"
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            AdminMenuItem(
                icon = Icons.Default.Logout,
                title = "Keluar",
                danger = true,
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun AdminMenuItem(
    icon: ImageVector,
    title: String,
    selected: Boolean = false,
    danger: Boolean = false,
    onClick: () -> Unit = {}
) {

    val backgroundColor =
        if (selected) DarkGreen else Color.Transparent

    val contentColor =
        when {
            danger -> Color(0xFFB91C1C)
            selected -> Color.White
            else -> TextDark
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = contentColor
        )
    }

    Spacer(
        modifier = Modifier.height(6.dp)
    )
}

@Composable
private fun AdminSummaryCard(
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
                tint = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = number,
                fontSize = 26.sp,
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
private fun AdminApprovalCard(
    initials: String,
    name: String,
    type: String
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

            Card(
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE6EEE9)
                )
            ) {

                Text(
                    text = initials,
                    modifier = Modifier.padding(12.dp),
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
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = type,
                    fontSize = 13.sp,
                    color = TextGray
                )
            }

            Text(
                text = "Menunggu",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD97706)
            )
        }
    }
}

@Composable
private fun QuickMenuCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String
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
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )
        }
    }
}