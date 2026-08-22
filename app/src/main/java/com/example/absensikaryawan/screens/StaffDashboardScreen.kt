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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.AbsensiDataStore
import com.example.absensikaryawan.data.UserRepository
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun StaffDashboardScreen(
    onScan: () -> Unit,
    onProfile: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    // ======================================================
    // DATASTORE ABSENSI
    // ======================================================

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val sudahAbsen by absensiDataStore
        .sudahAbsen
        .collectAsState(initial = false)

    val jamAbsen by absensiDataStore
        .jamAbsen
        .collectAsState(initial = "")

    val jamPulang by absensiDataStore
        .jamPulang
        .collectAsState(initial = "")

    // ======================================================
    // USER FIREBASE
    // ======================================================

    val userRepository = remember {
        UserRepository()
    }

    var namaUser by remember {
        mutableStateOf("Memuat...")
    }

    LaunchedEffect(Unit) {

        val result =
            userRepository.getCurrentUserName()

        if (result.isSuccess) {

            namaUser =
                result.getOrNull()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "User"

        } else {

            namaUser = "User"
        }
    }

    // ======================================================
    // STATE RIWAYAT
    // ======================================================

    var showHistory by remember {
        mutableStateOf(false)
    }

    // ======================================================
    // JAM REALTIME
    // ======================================================

    var jamSekarang by remember {

        mutableStateOf(
            SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        )
    }

    LaunchedEffect(Unit) {

        while (true) {

            jamSekarang =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            delay(1000)
        }
    }

    // ======================================================
    // TANGGAL REALTIME
    // ======================================================

    var tanggalSekarang by remember {

        mutableStateOf(
            SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale("id", "ID")
            ).format(Date())
        )
    }

    LaunchedEffect(Unit) {

        while (true) {

            tanggalSekarang =
                SimpleDateFormat(
                    "EEEE, dd MMMM yyyy",
                    Locale("id", "ID")
                ).format(Date())

            delay(1000)
        }
    }

    // ======================================================
    // ROOT
    // ======================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ==================================================
            // ISI BERANDA
            // ==================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp
                    )
            ) {

                // ==================================================
                // HEADER
                // ==================================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Staff",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text = "Absensi",
                            fontSize = 25.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text = tanggalSekarang,
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text = jamSekarang,
                            fontSize = 22.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {}
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Notifications,
                                contentDescription =
                                    "Notifikasi",
                                tint =
                                    PrimaryGreen
                            )
                        }

                        // ==================================================
                        // AVATAR PROFILE
                        // ==================================================

                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onProfile()
                                },

                            shape =
                                CircleShape,

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        DarkGreen
                                )
                        ) {

                            Box(
                                modifier =
                                    Modifier.fillMaxSize(),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        namaUser
                                            .trim()
                                            .split(" ")
                                            .filter {
                                                it.isNotEmpty()
                                            }
                                            .take(2)
                                            .joinToString("") {
                                                it.first()
                                                    .uppercase()
                                            }
                                            .ifEmpty {
                                                "PF"
                                            },

                                    fontSize = 13.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                // ==================================================
                // STATUS KEHADIRAN
                // ==================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkGreen
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(22.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "STATUS KEHADIRAN",

                            fontSize = 12.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier =
                                    Modifier
                                        .size(12.dp)
                                        .clip(
                                            CircleShape
                                        )
                                        .background(
                                            Color(0xFF69C27D)
                                        )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    if (sudahAbsen) {
                                        "SUDAH ABSEN"
                                    } else {
                                        "BELUM ABSEN"
                                    },

                                fontSize = 20.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )

                        // ==================================================
                        // TOMBOL SCAN
                        // ==================================================

                        Card(
                            modifier =
                                Modifier
                                    .size(145.dp)
                                    .clickable {
                                        onScan()
                                    },

                            shape =
                                RoundedCornerShape(28.dp),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        Color.White
                                ),

                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 5.dp
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .padding(18.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.QrCodeScanner,

                                    contentDescription =
                                        "Scan Absen",

                                    tint =
                                        PrimaryGreen,

                                    modifier =
                                        Modifier.size(52.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    text = "ABSEN",

                                    fontSize = 17.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        PrimaryGreen
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text =
                                if (sudahAbsen) {
                                    "Scan kembali untuk melakukan absen pulang"
                                } else {
                                    "Scan QR untuk melakukan absen masuk"
                                },

                            fontSize = 12.sp,

                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                // ==================================================
                // KEHADIRAN HARI INI
                // ==================================================

                Text(
                    text = "Kehadiran Hari Ini",

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                    ) {

                        // ==================================================
                        // JAM MASUK
                        // KLIK UNTUK RIWAYAT
                        // ==================================================

                        AttendanceInfoRow(
                            icon =
                                Icons.Default.AccessTime,

                            title =
                                "Jam Masuk",

                            value =
                                if (
                                    sudahAbsen &&
                                    jamAbsen.isNotEmpty()
                                ) {
                                    jamAbsen
                                } else {
                                    "--:--"
                                },

                            onClick = {
                                showHistory = true
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        // ==================================================
                        // JAM PULANG
                        // KLIK UNTUK RIWAYAT
                        // ==================================================

                        AttendanceInfoRow(
                            icon =
                                Icons.Default.ExitToApp,

                            title =
                                "Jam Pulang",

                            value =
                                if (
                                    jamPulang.isNotEmpty()
                                ) {
                                    jamPulang
                                } else {
                                    "--:--"
                                },

                            onClick = {
                                showHistory = true
                            }
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                // ==================================================
                // PENGAJUAN
                // ==================================================

                Text(
                    text = "Pengajuan Saya",

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Card(
                            shape =
                                RoundedCornerShape(13.dp),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        SoftGreen
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Schedule,

                                contentDescription =
                                    "Pengajuan",

                                tint =
                                    PrimaryGreen,

                                modifier =
                                    Modifier
                                        .padding(12.dp)
                                        .size(24.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "1 Pengajuan",

                                fontSize = 15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    "Menunggu persetujuan HRD",

                                fontSize = 12.sp,

                                color =
                                    TextGray
                            )
                        }

                        Text(
                            text =
                                "Menunggu",

                            fontSize = 11.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF9A6700)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }

            // ==================================================
            // BOTTOM NAVIGATION
            // ==================================================

            StaffBottomNavigation(
                onScan = onScan,
                onProfile = onProfile
            )
        }

        // ======================================================
        // DIALOG RIWAYAT ABSENSI
        // ======================================================

        if (showHistory) {

            AttendanceHistoryDialog(
                tanggal = tanggalSekarang,
                jamMasuk = jamAbsen,
                jamPulang = jamPulang,
                onDismiss = {
                    showHistory = false
                }
            )
        }
    }
}


// ======================================================
// ATTENDANCE INFO ROW
// ======================================================

@Composable
private fun AttendanceInfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(vertical = 4.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Card(
            shape =
                RoundedCornerShape(11.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        SoftGreen
                )
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier
                        .padding(10.dp)
                        .size(20.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Text(
            text = title,

            modifier =
                Modifier.weight(1f),

            fontSize = 14.sp,

            color =
                TextGray
        )

        Text(
            text = value,

            fontSize = 14.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )
    }
}


// ======================================================
// DIALOG RIWAYAT ABSENSI
// ======================================================

@Composable
private fun AttendanceHistoryDialog(
    tanggal: String,
    jamMasuk: String,
    jamPulang: String,
    onDismiss: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                text = "Riwayat Absensi",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column {

                Text(
                    text = tanggal,
                    fontSize = 13.sp,
                    color = TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                HistoryItem(
                    icon =
                        Icons.Default.AccessTime,

                    title =
                        "Jam Masuk",

                    value =
                        if (
                            jamMasuk.isNotEmpty()
                        ) {
                            jamMasuk
                        } else {
                            "Belum absen"
                        }
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                HistoryItem(
                    icon =
                        Icons.Default.ExitToApp,

                    title =
                        "Jam Pulang",

                    value =
                        if (
                            jamPulang.isNotEmpty()
                        ) {
                            jamPulang
                        } else {
                            "Belum absen pulang"
                        }
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Tutup",
                    color = PrimaryGreen
                )
            }
        }
    )
}


// ======================================================
// HISTORY ITEM
// ======================================================

@Composable
private fun HistoryItem(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Card(
            shape =
                RoundedCornerShape(10.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        SoftGreen
                )
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier
                        .padding(9.dp)
                        .size(20.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column {

            Text(
                text = title,
                fontSize = 12.sp,
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
}


// ======================================================
// BOTTOM NAVIGATION
// ======================================================

@Composable
private fun StaffBottomNavigation(
    onScan: () -> Unit,
    onProfile: () -> Unit
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        color =
            Color.White,

        shadowElevation = 8.dp
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceAround
        ) {

            BottomNavItem(
                icon =
                    Icons.Default.WbSunny,

                title =
                    "Beranda",

                selected = true
            )

            Card(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clickable {
                            onScan()
                        },

                shape =
                    CircleShape,

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            DarkGreen
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    )
            ) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.QrCodeScanner,

                        contentDescription =
                            "Scan Absen",

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(30.dp)
                    )
                }
            }

            Box(
                modifier =
                    Modifier.clickable {
                        onProfile()
                    }
            ) {

                BottomNavItem(
                    icon =
                        Icons.Default.Person,

                    title =
                        "Profile",

                    selected = false
                )
            }
        }
    }
}


// ======================================================
// BOTTOM NAV ITEM
// ======================================================

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    title: String,
    selected: Boolean
) {

    val color =
        if (selected) {
            PrimaryGreen
        } else {
            TextGray
        }

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector =
                icon,

            contentDescription =
                title,

            tint =
                color,

            modifier =
                Modifier.size(23.dp)
        )

        Spacer(
            modifier =
                Modifier.height(3.dp)
        )

        Text(
            text = title,

            fontSize = 11.sp,

            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },

            color =
                color
        )
    }
}