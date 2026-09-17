package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// ==========================================================
// STAFF DASHBOARD
// ==========================================================

@Composable
fun StaffDashboardScreen(
    refreshKey: Int,
    onScan: () -> Unit,
    onAbsenLuarKantor: () -> Unit,
    onProfile: () -> Unit,
    onHistory: () -> Unit,
    onHistoryPulang: () -> Unit,
    onSettings: () -> Unit,
    onPengajuan: () -> Unit,
    onLogout: () -> Unit,
    onNotification: () -> Unit
) {

    // ======================================================
    // FIREBASE
    // ======================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val db = remember {
        FirebaseFirestore.getInstance()
    }


    // ======================================================
    // FORMATTER
    // ======================================================

    val timeFormatter = remember {
        SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )
    }

    val dateFormatter = remember {
        SimpleDateFormat(
            "EEEE, dd MMMM yyyy",
            Locale("id", "ID")
        )
    }

    val firestoreDateFormatter = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )
    }


    // ======================================================
    // USER
    // ======================================================

    var namaUser by remember {
        mutableStateOf("Staff")
    }


    // ======================================================
    // JAM REAL-TIME
    // ======================================================

    var jamSekarang by remember {
        mutableStateOf("")
    }

    var tanggalSekarang by remember {
        mutableStateOf("")
    }


    // ======================================================
    // UPDATE JAM SETIAP DETIK
    // ======================================================

    LaunchedEffect(Unit) {

        while (true) {

            val sekarang = Date()

            jamSekarang =
                timeFormatter.format(sekarang)

            tanggalSekarang =
                dateFormatter.format(sekarang)

            delay(1000)
        }
    }


    // ======================================================
    // STATUS ABSEN
    // ======================================================

    var sudahAbsen by remember {
        mutableStateOf(false)
    }

    var jamMasuk by remember {
        mutableStateOf("-")
    }

    var jamPulang by remember {
        mutableStateOf("-")
    }


    // ======================================================
    // LOAD USER & ABSENSI
    // ======================================================

    LaunchedEffect(refreshKey) {

        try {

            val currentUser =
                auth.currentUser

            if (currentUser == null) {

                namaUser = "Staff"
                sudahAbsen = false
                jamMasuk = "-"
                jamPulang = "-"

                return@LaunchedEffect
            }


            val uid =
                currentUser.uid


            // ==================================================
            // LOAD USER
            // ==================================================

            try {

                val userDocument =
                    db.collection("users")
                        .document(uid)
                        .get()
                        .await()

                if (userDocument.exists()) {

                    namaUser =
                        userDocument
                            .getString("nama")
                            ?: "Staff"
                }

            } catch (e: Exception) {

                println(
                    "USER DASHBOARD ERROR : ${e.message}"
                )
            }


            // ==================================================
            // TANGGAL HARI INI
            // ==================================================

            val tanggalHariIni =
                firestoreDateFormatter.format(
                    Date()
                )


            // ==================================================
            // RESET STATE SEBELUM LOAD
            // ==================================================

            sudahAbsen = false
            jamMasuk = "-"
            jamPulang = "-"


            // ==================================================
            // CARI ABSENSI HARI INI
            // ==================================================

            val attendanceSnapshot =
                db.collection("attendance")
                    .whereEqualTo(
                        "uid",
                        uid
                    )
                    .whereEqualTo(
                        "tanggal",
                        tanggalHariIni
                    )
                    .limit(1)
                    .get()
                    .await()


            // ==================================================
            // ABSENSI DITEMUKAN
            // ==================================================

            if (!attendanceSnapshot.isEmpty) {

                val document =
                    attendanceSnapshot
                        .documents
                        .first()

                sudahAbsen = true

                jamMasuk =
                    document.getString(
                        "jamMasuk"
                    ) ?: "-"

                jamPulang =
                    document.getString(
                        "jamPulang"
                    ) ?: "-"

                if (jamPulang.isBlank()) {
                    jamPulang = "-"
                }
            }

        } catch (e: Exception) {

            println(
                "DASHBOARD ERROR : ${e.message}"
            )
        }
    }


    // ======================================================
    // RESET ABSENSI SAAT HARI BERGANTI
    // ======================================================

    LaunchedEffect(Unit) {

        while (true) {

            val sekarang =
                System.currentTimeMillis()

            val kalenderBesok =
                Calendar.getInstance()

            kalenderBesok.timeInMillis =
                sekarang

            kalenderBesok.add(
                Calendar.DAY_OF_YEAR,
                1
            )

            kalenderBesok.set(
                Calendar.HOUR_OF_DAY,
                0
            )

            kalenderBesok.set(
                Calendar.MINUTE,
                0
            )

            kalenderBesok.set(
                Calendar.SECOND,
                1
            )

            kalenderBesok.set(
                Calendar.MILLISECOND,
                0
            )

            val waktuMenujuBesok =
                kalenderBesok.timeInMillis -
                        sekarang

            delay(
                waktuMenujuBesok
                    .coerceAtLeast(1000L)
            )

            sudahAbsen = false
            jamMasuk = "-"
            jamPulang = "-"
        }
    }


    // ======================================================
    // SCROLL BERANDA
    // ======================================================

    val verticalScrollState =
        rememberScrollState()


    // ======================================================
    // UI
    // ======================================================

    Surface(
        modifier =
            Modifier.fillMaxSize(),

        color =
            Background
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        verticalScrollState
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Halo, $namaUser \uD83D\uDC4B",

                        fontSize =
                            19.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            tanggalSekarang,

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                // ==================================================
                // NOTIFIKASI
                // ==================================================

                Box(
                    modifier =
                        Modifier
                            .size(44.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    IconButton(
                        onClick =
                            onNotification,

                        modifier =
                            Modifier.size(44.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.NotificationsNone,

                            contentDescription =
                                "Notifikasi",

                            tint =
                                TextDark,

                            modifier =
                                Modifier.size(23.dp)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                // ==================================================
                // PROFILE — avatar bulat berinisial
                // ==================================================

                Box(
                    modifier =
                        Modifier
                            .size(44.dp)
                            .background(
                                color = SoftGreen,
                                shape = CircleShape
                            )
                            .padding(0.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    IconButton(
                        onClick =
                            onProfile,

                        modifier =
                            Modifier.size(44.dp)
                    ) {

                        Text(
                            text =
                                namaUser
                                    .trim()
                                    .firstOrNull()
                                    ?.uppercaseChar()
                                    ?.toString()
                                    ?: "S",

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ==================================================
            // HERO CARD — jam real-time + status kehadiran
            // digabung jadi satu kartu supaya lebih ringkas
            // dan jadi pusat perhatian di Beranda
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(24.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.Transparent
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                brush =
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            PrimaryGreen,
                                            DarkGreen
                                        )
                                    )
                            )
                            .padding(20.dp)
                ) {

                    // ==========================================
                    // BARIS ATAS: JAM + STATUS CHIP
                    // ==========================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.AccessTime,

                                    contentDescription = null,

                                    tint =
                                        Color.White.copy(alpha = 0.85f),

                                    modifier =
                                        Modifier.size(15.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(5.dp)
                                )

                                Text(
                                    text =
                                        "Waktu Sekarang",

                                    fontSize = 12.sp,

                                    color =
                                        Color.White.copy(alpha = 0.85f)
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    jamSekarang,

                                fontSize = 34.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }

                        val statusBackground =
                            if (sudahAbsen) {
                                Color.White.copy(alpha = 0.20f)
                            } else {
                                Color(0xFFE67E22)
                            }

                        Text(
                            text =
                                if (sudahAbsen)
                                    "SUDAH ABSEN"
                                else
                                    "BELUM ABSEN",

                            fontSize = 11.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White,

                            modifier =
                                Modifier
                                    .background(
                                        color =
                                            statusBackground,

                                        shape =
                                            RoundedCornerShape(50.dp)
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    HorizontalDivider(
                        color =
                            Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // ==========================================
                    // JAM MASUK & PULANG
                    // ==========================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Jam Masuk",

                                fontSize = 11.sp,

                                color =
                                    Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    jamMasuk,

                                fontSize = 17.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }


                        Column(
                            modifier =
                                Modifier.weight(1f),

                            horizontalAlignment =
                                Alignment.End
                        ) {

                            Text(
                                text =
                                    "Jam Pulang",

                                fontSize = 11.sp,

                                color =
                                    Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    jamPulang,

                                fontSize = 17.sp,

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
                    Modifier.height(20.dp)
            )


            // ==================================================
            // ABSENSI
            // ==================================================

            Text(
                text = "Absensi Hari Ini",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!sudahAbsen) {

                // ==================================================
                // SCAN QR ABSEN
                // ==================================================

                ActionRowCard(
                    onClick = onScan,
                    icon = Icons.Default.QrCodeScanner,
                    title = "Scan QR Absen",
                    subtitle = "Scan untuk absen masuk di kantor",
                    filled = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ==================================================
                // ABSEN LUAR KANTOR
                // ==================================================

                ActionRowCard(
                    onClick = onAbsenLuarKantor,
                    icon = Icons.Default.LocationOn,
                    title = "Absen Luar Kantor",
                    subtitle = "Untuk tugas langsung ke klien",
                    filled = false
                )

            } else {

                // ==================================================
                // SUDAH ABSEN
                // QR UNTUK ABSEN PULANG
                // ==================================================

                ActionRowCard(
                    onClick = onScan,
                    icon = Icons.Default.QrCodeScanner,
                    title = "Scan QR Absen Pulang",
                    subtitle = "Scan QR saat sudah kembali ke kantor",
                    filled = true
                )
            }


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ==================================================
            // AKSI CEPAT
            // ==================================================

            Text(
                text =
                    "Aksi Cepat",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            // ==================================================
            // 3 QUICK ACTION
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                QuickActionCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.History,

                    title =
                        "Riwayat",

                    subtitle =
                        "Absensi",

                    onClick =
                        onHistory
                )


                QuickActionCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Description,

                    title =
                        "Pengajuan",

                    subtitle =
                        "Izin / Sakit",

                    onClick =
                        onPengajuan
                )


                QuickActionCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Person,

                    title =
                        "Profil",

                    subtitle =
                        "Data Saya",

                    onClick =
                        onProfile
                )
            }


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }
}


// ==========================================================
// ACTION ROW CARD
// Kartu aksi utama (Scan / Absen Luar Kantor) dengan ikon
// dalam badge bulat + chevron sebagai penanda "bisa diklik"
// ==========================================================

@Composable
private fun ActionRowCard(
    onClick: () -> Unit,
    icon: ImageVector,
    title: String,
    subtitle: String,
    filled: Boolean
) {

    val containerColor =
        if (filled) PrimaryGreen else Color.White

    val badgeColor =
        if (filled) Color.White.copy(alpha = 0.18f) else SoftGreen

    val iconTint =
        if (filled) Color.White else PrimaryGreen

    val titleColor =
        if (filled) Color.White else TextDark

    val subtitleColor =
        if (filled) Color.White.copy(alpha = 0.75f) else TextGray

    val chevronTint =
        if (filled) Color.White.copy(alpha = 0.8f) else TextGray

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (filled) 3.dp else 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(color = badgeColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronTint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}


// ==========================================================
// QUICK ACTION CARD
// ==========================================================

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        onClick =
            onClick,

        modifier =
            modifier.height(108.dp),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color = SoftGreen, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        icon,

                    contentDescription =
                        title,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    title,

                fontSize =
                    12.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    subtitle,

                fontSize =
                    9.sp,

                color =
                    TextGray
            )
        }
    }
}