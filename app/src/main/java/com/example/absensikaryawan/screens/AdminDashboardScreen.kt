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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ChevronRight

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// ==========================================================
// ADMIN DASHBOARD / BERANDA ADMIN
// ==========================================================

@Composable
fun AdminDashboardScreen(
    onApproval: () -> Unit,
    onEmployees: () -> Unit,
    onRecap: () -> Unit,
    onSettings: () -> Unit,
    onChat: () -> Unit,
    onNotification: () -> Unit
) {

    // ======================================================
    // FIRESTORE
    // ======================================================

    val db = remember {
        FirebaseFirestore.getInstance()
    }


    // ======================================================
    // FORMATTER
    // ======================================================

    val dateFormatter = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )
    }


    // ======================================================
    // STATE PENGAJUAN
    // ======================================================

    var jumlahPengajuanHariIni by remember {
        mutableStateOf(0)
    }

    var jumlahPengajuanMenunggu by remember {
        mutableStateOf(0)
    }


    // ======================================================
    // STATE KARYAWAN
    // ======================================================

    var jumlahKaryawan by remember {
        mutableStateOf(0)
    }


    // ======================================================
    // STATE KEHADIRAN
    // ======================================================

    var jumlahHadir by remember {
        mutableStateOf(0)
    }

    var jumlahSudahPulang by remember {
        mutableStateOf(0)
    }

    var jumlahBelumPulang by remember {
        mutableStateOf(0)
    }


    // ======================================================
    // LOAD DATA DASHBOARD
    // ======================================================

    LaunchedEffect(Unit) {

        try {

            val tanggalHariIni =
                dateFormatter.format(
                    Date()
                )

            coroutineScope {

                // ==========================================
                // QUERY USERS
                // ==========================================

                val usersDeferred =
                    async {

                        db.collection("users")
                            .get()
                            .await()
                    }


                // ==========================================
                // QUERY ATTENDANCE
                // ==========================================

                val attendanceDeferred =
                    async {

                        db.collection("attendance")
                            .whereEqualTo(
                                "tanggal",
                                tanggalHariIni
                            )
                            .get()
                            .await()
                    }


                // ==========================================
                // QUERY PENGAJUAN
                // ==========================================

                val pengajuanDeferred =
                    async {

                        db.collection("pengajuan")
                            .get()
                            .await()
                    }


                // ==========================================
                // TUNGGU SEMUA QUERY
                // ==========================================

                val results =
                    awaitAll(
                        usersDeferred,
                        attendanceDeferred,
                        pengajuanDeferred
                    )


                // ==================================================
                // USERS
                // ==================================================

                try {

                    val usersSnapshot =
                        results[0]

                    jumlahKaryawan =
                        usersSnapshot.size()

                } catch (e: Exception) {

                    e.printStackTrace()
                }


                // ==================================================
                // ATTENDANCE
                // ==================================================

                try {

                    val attendanceSnapshot =
                        results[1]

                    var totalHadir =
                        0

                    var totalSudahPulang =
                        0

                    var totalBelumPulang =
                        0


                    attendanceSnapshot.documents
                        .forEach { document ->

                            val jamMasuk =
                                document.getString(
                                    "jamMasuk"
                                ) ?: ""


                            val jamPulang =
                                document.getString(
                                    "jamPulang"
                                ) ?: ""


                            if (
                                jamMasuk.isNotBlank()
                            ) {

                                totalHadir++
                            }


                            if (
                                jamPulang.isNotBlank()
                            ) {

                                totalSudahPulang++

                            } else if (
                                jamMasuk.isNotBlank()
                            ) {

                                totalBelumPulang++
                            }
                        }


                    jumlahHadir =
                        totalHadir

                    jumlahSudahPulang =
                        totalSudahPulang

                    jumlahBelumPulang =
                        totalBelumPulang

                } catch (e: Exception) {

                    e.printStackTrace()
                }


                // ==================================================
                // PENGAJUAN
                // ==================================================

                try {

                    val snapshot =
                        results[2]

                    var totalHariIni =
                        0

                    var totalMenunggu =
                        0


                    snapshot.documents
                        .forEach { document ->

                            val tanggalMulai =
                                document.getString(
                                    "tanggalMulai"
                                ) ?: ""


                            val status =
                                document.getString(
                                    "status"
                                )
                                    ?.lowercase()
                                    ?: ""


                            if (
                                tanggalMulai ==
                                tanggalHariIni
                            ) {

                                totalHariIni++
                            }


                            if (
                                status ==
                                "menunggu"
                            ) {

                                totalMenunggu++
                            }
                        }


                    jumlahPengajuanHariIni =
                        totalHariIni

                    jumlahPengajuanMenunggu =
                        totalMenunggu

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }


    // ======================================================
    // UI
    // ======================================================

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Background
                )
                .padding(
                    horizontal = 18.dp
                ),

        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        item {

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 4.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Beranda",

                        fontSize =
                            23.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Kelola aktivitas aplikasi",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                // ==========================================
                // NOTIFIKASI
                // ==========================================

                CompactIconButton(
                    icon =
                        Icons.Default.Notifications,

                    contentDescription =
                        "Notifikasi Admin",

                    onClick =
                        onNotification
                )
            }
        }


        // ==================================================
        // STATISTIK HARI INI
        // ==================================================

        item {

            AdminSectionTitle(
                title =
                    "Statistik Hari Ini"
            )
        }


        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

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

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 13.dp
                            ),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    AdminCompactStat(
                        icon =
                            Icons.Default.Groups,

                        title =
                            "Karyawan",

                        value =
                            jumlahKaryawan.toString()
                    )


                    AdminVerticalDivider()


                    AdminCompactStat(
                        icon =
                            Icons.Default.CheckCircle,

                        title =
                            "Hadir",

                        value =
                            jumlahHadir.toString()
                    )


                    AdminVerticalDivider()


                    AdminCompactStat(
                        icon =
                            Icons.Default.Schedule,

                        title =
                            "Belum Pulang",

                        value =
                            jumlahBelumPulang.toString()
                    )


                    AdminVerticalDivider()


                    AdminCompactStat(
                        icon =
                            Icons.Default.CheckCircle,

                        title =
                            "Sudah Pulang",

                        value =
                            jumlahSudahPulang.toString()
                    )
                }
            }
        }


        // ==================================================
        // PENGAJUAN
        // ==================================================

        item {

            AdminSectionTitle(
                title =
                    "Pengajuan"
            )
        }


        item {

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onApproval()
                        },

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

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

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    AdminSmallIconBox(
                        icon =
                            Icons.Default.NoteAdd
                    )


                    Spacer(
                        modifier =
                            Modifier.width(11.dp)
                    )


                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "$jumlahPengajuanHariIni Pengajuan Hari Ini",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "$jumlahPengajuanMenunggu menunggu approval",

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }


                    Icon(
                        imageVector =
                            Icons.Default.ChevronRight,

                        contentDescription =
                            null,

                        tint =
                            TextGray,

                        modifier =
                            Modifier.size(20.dp)
                    )
                }
            }
        }


        // ==================================================
        // AKSES CEPAT
        // ==================================================

        item {

            AdminSectionTitle(
                title =
                    "Akses Cepat"
            )
        }


        // ==================================================
        // ROW 1
        // REKAP + KARYAWAN
        // ==================================================

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {

                AdminQuickCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Assessment,

                    title =
                        "Rekap",

                    onClick =
                        onRecap
                )


                AdminQuickCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Groups,

                    title =
                        "Karyawan",

                    onClick =
                        onEmployees
                )
            }
        }


        // ==================================================
        // ROW 2
        // CHAT + SETTING
        // ==================================================

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {

                AdminQuickCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Chat,

                    title =
                        "Chat",

                    onClick =
                        onChat
                )


                AdminQuickCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Settings,

                    title =
                        "Setting",

                    onClick =
                        onSettings
                )
            }
        }


        // ==================================================
        // BOTTOM SPACING
        // ==================================================

        item {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
private fun AdminSectionTitle(
    title: String
) {

    Text(
        text =
            title,

        fontSize =
            15.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            TextDark,

        modifier =
            Modifier.padding(
                top = 2.dp
            )
    )
}


// ==========================================================
// COMPACT STAT
// ==========================================================

@Composable
private fun AdminCompactStat(
    icon: ImageVector,
    title: String,
    value: String
) {

    Column(
        modifier =
            Modifier.width(72.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector =
                icon,

            contentDescription =
                title,

            tint =
                PrimaryGreen,

            modifier =
                Modifier.size(19.dp)
        )


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        Text(
            text =
                value,

            fontSize =
                19.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )


        Spacer(
            modifier =
                Modifier.height(1.dp)
        )


        Text(
            text =
                title,

            fontSize =
                9.sp,

            color =
                TextGray,

            maxLines =
                1
        )
    }
}


// ==========================================================
// VERTICAL DIVIDER
// ==========================================================

@Composable
private fun AdminVerticalDivider() {

    Spacer(
        modifier =
            Modifier
                .width(1.dp)
                .height(40.dp)
                .background(
                    color =
                        Color(0xFFE5E7EB)
                )
    )
}


// ==========================================================
// SMALL ICON BOX
// ==========================================================

@Composable
private fun AdminSmallIconBox(
    icon: ImageVector
) {

    Row(
        modifier =
            Modifier
                .size(40.dp)
                .background(
                    color =
                        SoftGreen,

                    shape =
                        RoundedCornerShape(
                            11.dp
                        )
                ),

        horizontalArrangement =
            Arrangement.Center,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                icon,

            contentDescription =
                null,

            tint =
                PrimaryGreen,

            modifier =
                Modifier.size(21.dp)
        )
    }
}


// ==========================================================
// QUICK CARD
// ==========================================================

@Composable
private fun AdminQuickCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(
                15.dp
            ),

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

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 12.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(
                modifier =
                    Modifier
                        .size(36.dp)
                        .background(
                            color =
                                SoftGreen,

                            shape =
                                RoundedCornerShape(
                                    10.dp
                                )
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
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
                    Modifier.width(8.dp)
            )


            Text(
                text =
                    title,

                modifier =
                    Modifier.weight(1f),

                fontSize =
                    12.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TextDark
            )


            Icon(
                imageVector =
                    Icons.Default.ChevronRight,

                contentDescription =
                    null,

                tint =
                    TextGray,

                modifier =
                    Modifier.size(17.dp)
            )
        }
    }
}


// ==========================================================
// COMPACT HEADER ICON BUTTON
// ==========================================================

@Composable
private fun CompactIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .size(42.dp)
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            13.dp
                        )
                )
                .clickable {
                    onClick()
                },

        horizontalArrangement =
            Arrangement.Center,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                icon,

            contentDescription =
                contentDescription,

            tint =
                TextDark,

            modifier =
                Modifier.size(21.dp)
        )
    }
}