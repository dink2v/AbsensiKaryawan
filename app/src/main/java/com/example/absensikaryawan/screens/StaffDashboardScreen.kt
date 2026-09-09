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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner

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
                            "ABSENSI KARYAWAN",

                        fontSize =
                            21.sp,

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
                            11.sp,

                        color =
                            TextGray
                    )
                }


                // ==================================================
                // NOTIFIKASI
                // ==================================================

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
                            Modifier.size(25.dp)
                    )
                }


                // ==================================================
                // PROFILE
                // ==================================================

                IconButton(
                    onClick =
                        onProfile,

                    modifier =
                        Modifier.size(44.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Person,

                        contentDescription =
                            "Profil",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(27.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ==================================================
            // JAM REAL-TIME
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            PrimaryGreen
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.AccessTime,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(28.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            jamSekarang,

                        fontSize =
                            32.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "Waktu Sekarang",

                        fontSize =
                            12.sp,

                        color =
                            Color.White
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // KEHADIRAN HARI INI
            // ==================================================

            Text(
                text =
                    "Kehadiran Hari Ini",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(17.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {

                    // ==========================================
                    // STATUS
                    // ==========================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "Status",

                            fontSize =
                                12.sp,

                            color =
                                TextGray,

                            modifier =
                                Modifier.weight(1f)
                        )

                        val statusBackground =
                            if (sudahAbsen) {
                                Color(0xFFE8F5E9)
                            } else {
                                Color(0xFFFFF3E0)
                            }

                        val statusText =
                            if (sudahAbsen) {
                                PrimaryGreen
                            } else {
                                Color(0xFFE67E22)
                            }

                        Text(
                            text =
                                if (sudahAbsen)
                                    "SUDAH ABSEN"
                                else
                                    "BELUM ABSEN",

                            fontSize =
                                11.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                statusText,

                            modifier =
                                Modifier
                                    .background(
                                        color =
                                            statusBackground,

                                        shape =
                                            RoundedCornerShape(
                                                50.dp
                                            )
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(13.dp)
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

                                fontSize =
                                    11.sp,

                                color =
                                    TextGray
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    jamMasuk,

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
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

                                fontSize =
                                    11.sp,

                                color =
                                    TextGray
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    jamPulang,

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // ==================================================
            // ABSENSI
            // ==================================================

            if (!sudahAbsen) {

                // ==================================================
                // SCAN QR ABSEN
                // ==================================================

                Card(
                    onClick =
                        onScan,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(76.dp),

                    shape =
                        RoundedCornerShape(17.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                PrimaryGreen
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                2.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.QrCodeScanner,

                            contentDescription =
                                "Scan QR Absen",

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(32.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column {

                            Text(
                                text =
                                    "Scan QR Absen",

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text =
                                    "Scan untuk absen masuk di kantor",

                                fontSize =
                                    10.sp,

                                color =
                                    Color.White
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                // ==================================================
                // ABSEN LUAR KANTOR
                // ==================================================

                Card(
                    onClick =
                        onAbsenLuarKantor,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(76.dp),

                    shape =
                        RoundedCornerShape(17.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                2.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.LocationOn,

                            contentDescription =
                                "Absen Luar Kantor",

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(31.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column {

                            Text(
                                text =
                                    "Absen Luar Kantor",

                                fontSize =
                                    15.sp,

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
                                    "Untuk tugas langsung ke klien",

                                fontSize =
                                    10.sp,

                                color =
                                    TextGray
                            )
                        }
                    }
                }

            } else {

                // ==================================================
                // SUDAH ABSEN
                // QR UNTUK ABSEN PULANG
                // ==================================================

                Card(
                    onClick =
                        onScan,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(76.dp),

                    shape =
                        RoundedCornerShape(17.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                PrimaryGreen
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                2.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.QrCodeScanner,

                            contentDescription =
                                "Scan QR Pulang",

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(32.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column {

                            Text(
                                text =
                                    "Scan QR Absen Pulang",

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text =
                                    "Scan QR saat sudah kembali ke kantor",

                                fontSize =
                                    10.sp,

                                color =
                                    Color.White
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
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
            modifier.height(105.dp),

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
                    2.dp
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

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(28.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
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