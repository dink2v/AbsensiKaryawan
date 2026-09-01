package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule

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
import androidx.compose.ui.draw.clip
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
    onProfile: () -> Unit,
    onHistory: () -> Unit,
    onHistoryPulang: () -> Unit,
    onSettings: () -> Unit,
    onPengajuan: () -> Unit,
    onLogout: () -> Unit,

    // ==========================================================
    // NOTIFIKASI
    // ==========================================================

    onNotification: () -> Unit

) {

    // ==========================================================
    // FIREBASE
    // ==========================================================

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }


    // ==========================================================
    // USER
    // ==========================================================

    var namaUser by remember {
        mutableStateOf("Staff")
    }


    // ==========================================================
    // JAM REAL-TIME
    // ==========================================================

    var jamSekarang by remember {
        mutableStateOf("")
    }

    var tanggalSekarang by remember {
        mutableStateOf("")
    }


    // ==========================================================
    // UPDATE JAM SETIAP DETIK
    // ==========================================================

    LaunchedEffect(Unit) {

        while (true) {

            val sekarang =
                Date()

            jamSekarang =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(sekarang)

            tanggalSekarang =
                SimpleDateFormat(
                    "EEEE, dd MMMM yyyy",
                    Locale("id", "ID")
                ).format(sekarang)

            delay(1000)
        }
    }


    // ==========================================================
    // STATUS ABSEN
    // ==========================================================

    var sudahAbsen by remember {
        mutableStateOf(false)
    }

    var jamMasuk by remember {
        mutableStateOf("-")
    }

    var jamPulang by remember {
        mutableStateOf("-")
    }


    // ==========================================================
    // LOAD USER & ABSENSI
    // ==========================================================

    LaunchedEffect(refreshKey) {

        while (true) {

            try {

                // ==================================================
                // USER LOGIN
                // ==================================================

                val currentUser =
                    auth.currentUser

                if (currentUser == null) {

                    sudahAbsen = false
                    jamMasuk = "-"
                    jamPulang = "-"

                    return@LaunchedEffect
                }


                // ==================================================
                // UID
                // ==================================================

                val uid =
                    currentUser.uid


                // ==================================================
                // DATA USER
                // ==================================================

                val userDocument =
                    db.collection("users")
                        .document(uid)
                        .get()
                        .await()


                if (userDocument.exists()) {

                    namaUser =
                        userDocument.getString(
                            "nama"
                        ) ?: "Staff"
                }


                // ==================================================
                // TANGGAL HARI INI
                // ==================================================

                val tanggalHariIni =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())


                // ==================================================
                // RESET STATE
                // ==================================================

                sudahAbsen = false
                jamMasuk = "-"
                jamPulang = "-"


                // ==================================================
                // CARI ABSENSI
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

                        jamPulang =
                            "-"
                    }
                }


                // ==================================================
                // HITUNG PERGANTIAN HARI
                // ==================================================

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


                val waktuBesok =
                    kalenderBesok.timeInMillis


                val waktuMenujuBesok =
                    waktuBesok - sekarang


                // ==================================================
                // TUNGGU HARI BERGANTI
                // ==================================================

                delay(
                    waktuMenujuBesok
                )


                // ==================================================
                // RESET ABSENSI
                // ==================================================

                sudahAbsen = false
                jamMasuk = "-"
                jamPulang = "-"

            } catch (e: Exception) {

                println(
                    "DASHBOARD ERROR : ${e.message}"
                )

                delay(10_000)
            }
        }
    }


    // ==========================================================
    // SCROLL
    // ==========================================================

    val verticalScrollState =
        rememberScrollState()


    // ==========================================================
    // UI
    // ==========================================================

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
                    .statusBarsPadding()
                    .verticalScroll(
                        verticalScrollState
                    )
                    .padding(
                        horizontal = 18.dp,
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
                            "Absensi Karyawan",

                        fontSize =
                            22.sp,

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
                            "Selamat datang kembali",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                // ==================================================
                // NOTIFIKASI
                // ==================================================

                IconButton(

                    onClick =
                        onNotification

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


                Spacer(
                    modifier =
                        Modifier.width(2.dp)
                )


                // ==================================================
                // PROFILE
                // ==================================================

                Surface(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape),

                    color =
                        PrimaryGreen

                ) {

                    IconButton(

                        onClick =
                            onProfile

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Person,

                            contentDescription =
                                "Profil",

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(23.dp)
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ==================================================
            // JAM REAL-TIME
            // ==========================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(24.dp),

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
                                horizontal = 22.dp,
                                vertical = 22.dp
                            )

                ) {

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Surface(

                            modifier =
                                Modifier.size(48.dp),

                            shape =
                                CircleShape,

                            color =
                                Color.White.copy(
                                    alpha = 0.16f
                                )

                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.AccessTime,

                                contentDescription =
                                    null,

                                tint =
                                    Color.White,

                                modifier =
                                    Modifier
                                        .padding(12.dp)
                                        .size(24.dp)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )


                        Column {

                            Text(

                                text =
                                    "Waktu Sekarang",

                                fontSize =
                                    12.sp,

                                color =
                                    Color.White.copy(
                                        alpha = 0.85f
                                    )
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    jamSekarang,

                                fontSize =
                                    30.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Text(

                        text =
                            tanggalSekarang,

                        fontSize =
                            13.sp,

                        color =
                            Color.White.copy(
                                alpha = 0.9f
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

            SectionTitle(
                title =
                    "Kehadiran Hari Ini"
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

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
                            .fillMaxWidth()
                            .padding(18.dp)

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

                        Surface(

                            modifier =
                                Modifier.size(42.dp),

                            shape =
                                CircleShape,

                            color =
                                if (sudahAbsen) {
                                    Color(0xFFE8F5E9)
                                } else {
                                    Color(0xFFFFF3E0)
                                }

                        ) {

                            Icon(

                                imageVector =
                                    if (sudahAbsen) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Default.Schedule
                                    },

                                contentDescription =
                                    null,

                                tint =
                                    if (sudahAbsen) {
                                        PrimaryGreen
                                    } else {
                                        Color(0xFFE67E22)
                                    },

                                modifier =
                                    Modifier
                                        .padding(9.dp)
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
                                    "Status Kehadiran",

                                fontSize =
                                    12.sp,

                                color =
                                    TextGray
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )


                            Text(

                                text =
                                    if (sudahAbsen)
                                        "SUDAH ABSEN"
                                    else
                                        "BELUM ABSEN",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    if (sudahAbsen) {
                                        PrimaryGreen
                                    } else {
                                        Color(0xFFE67E22)
                                    }
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    // ==========================================
                    // JAM MASUK & PULANG
                    // ==========================================

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)

                    ) {

                        AttendanceTimeCard(

                            modifier =
                                Modifier.weight(1f),

                            title =
                                "Jam Masuk",

                            time =
                                jamMasuk,

                            icon =
                                Icons.Default.AccessTime
                        )


                        AttendanceTimeCard(

                            modifier =
                                Modifier.weight(1f),

                            title =
                                "Jam Pulang",

                            time =
                                jamPulang,

                            icon =
                                Icons.Default.Schedule
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // ==================================================
            // AKSI CEPAT
            // ==========================================================

            SectionTitle(
                title =
                    "Aksi Cepat"
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // QUICK ACTION
            // ==========================================================

            val horizontalScrollState =
                rememberScrollState()


            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            horizontalScrollState
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {

                QuickActionCard(

                    icon =
                        Icons.Default.QrCodeScanner,

                    title =
                        "Scan QR",

                    subtitle =
                        "Absensi",

                    onClick =
                        onScan,

                    highlighted =
                        true
                )


                QuickActionCard(

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
                    Modifier.height(22.dp)
            )


            // ==================================================
            // AKTIVITAS HARI INI
            // ==========================================================

            SectionTitle(
                title =
                    "Aktivitas Hari Ini"
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

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
                            .fillMaxWidth()
                            .padding(18.dp)

                ) {

                    ActivityItem(

                        icon =
                            Icons.Default.AccessTime,

                        title =
                            "Absen Masuk",

                        description =
                            if (
                                sudahAbsen &&
                                jamMasuk != "-"
                            ) {
                                "Berhasil melakukan absensi masuk"
                            } else {
                                "Belum melakukan absensi masuk"
                            },

                        time =
                            jamMasuk,

                        active =
                            jamMasuk != "-"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    ActivityItem(

                        icon =
                            Icons.Default.Schedule,

                        title =
                            "Absen Pulang",

                        description =
                            if (
                                jamPulang != "-"
                            ) {
                                "Berhasil melakukan absensi pulang"
                            } else {
                                "Belum melakukan absensi pulang"
                            },

                        time =
                            jamPulang,

                        active =
                            jamPulang != "-"
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
private fun SectionTitle(

    title: String

) {

    Text(

        text =
            title,

        fontSize =
            18.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            TextDark
    )
}


// ==========================================================
// ATTENDANCE TIME CARD
// ==========================================================

@Composable
private fun AttendanceTimeCard(

    modifier: Modifier,

    title: String,

    time: String,

    icon: ImageVector

) {

    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(15.dp),

        color =
            Background

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Surface(

                modifier =
                    Modifier.size(34.dp),

                shape =
                    CircleShape,

                color =
                    if (time != "-") {
                        Color(0xFFE8F5E9)
                    } else {
                        Color(0xFFEDEDED)
                    }

            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    tint =
                        if (time != "-") {
                            PrimaryGreen
                        } else {
                            TextGray
                        },

                    modifier =
                        Modifier
                            .padding(8.dp)
                            .size(18.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(9.dp)
            )


            Column {

                Text(

                    text =
                        title,

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(

                    text =
                        time,

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


// ==========================================================
// QUICK ACTION CARD
// ==========================================================

@Composable
private fun QuickActionCard(

    icon: ImageVector,

    title: String,

    subtitle: String,

    onClick: () -> Unit,

    highlighted: Boolean = false

) {

    Card(

        onClick =
            onClick,

        modifier =
            Modifier
                .width(124.dp)
                .height(118.dp),

        shape =
            RoundedCornerShape(18.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (highlighted) {
                        PrimaryGreen
                    } else {
                        Color.White
                    }
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (highlighted) {
                        0.dp
                    } else {
                        1.dp
                    }
            )

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            Surface(

                modifier =
                    Modifier.size(42.dp),

                shape =
                    CircleShape,

                color =
                    if (highlighted) {
                        Color.White.copy(
                            alpha = 0.18f
                        )
                    } else {
                        Color(0xFFE8F5E9)
                    }

            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        title,

                    tint =
                        if (highlighted) {
                            Color.White
                        } else {
                            PrimaryGreen
                        },

                    modifier =
                        Modifier
                            .padding(9.dp)
                            .size(24.dp)
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
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    if (highlighted) {
                        Color.White
                    } else {
                        TextDark
                    }
            )


            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )


            Text(

                text =
                    subtitle,

                fontSize =
                    11.sp,

                color =
                    if (highlighted) {
                        Color.White.copy(
                            alpha = 0.8f
                        )
                    } else {
                        TextGray
                    }
            )
        }
    }
}


// ==========================================================
// ACTIVITY ITEM
// ==========================================================

@Composable
private fun ActivityItem(

    icon: ImageVector,

    title: String,

    description: String,

    time: String,

    active: Boolean

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Surface(

            modifier =
                Modifier.size(42.dp),

            shape =
                CircleShape,

            color =
                if (active) {
                    Color(0xFFE8F5E9)
                } else {
                    Color(0xFFF1F1F1)
                }

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    if (active) {
                        PrimaryGreen
                    } else {
                        TextGray
                    },

                modifier =
                    Modifier
                        .padding(9.dp)
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
                    title,

                fontSize =
                    14.sp,

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
                    description,

                fontSize =
                    11.sp,

                color =
                    TextGray
            )
        }


        Text(

            text =
                time,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                if (active) {
                    PrimaryGreen
                } else {
                    TextGray
                }
        )
    }
}