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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Settings

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

    onSettings: () -> Unit

) {

    // ======================================================
    // FIRESTORE
    // ======================================================

    val db =
        remember {
            FirebaseFirestore.getInstance()
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
    // LOAD PENGAJUAN
    // ======================================================

    LaunchedEffect(Unit) {

        try {

            val snapshot =
                db.collection("pengajuan")
                    .get()
                    .await()


            val tanggalHariIni =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(
                    Date()
                )


            var totalHariIni =
                0


            var totalMenunggu =
                0


            snapshot.documents.forEach { document ->

                val tanggalMulai =
                    document.getString(
                        "tanggalMulai"
                    )
                        ?: ""


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
                    status == "menunggu"
                ) {

                    totalMenunggu++
                }
            }


            jumlahPengajuanHariIni =
                totalHariIni


            jumlahPengajuanMenunggu =
                totalMenunggu

        } catch (
            e: Exception
        ) {

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
                    horizontal = 20.dp
                ),

        verticalArrangement =
            Arrangement.spacedBy(
                14.dp
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        item {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
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
                            26.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            "Kelola aktivitas aplikasi",

                        fontSize =
                            13.sp,

                        color =
                            TextGray
                    )
                }


                // ==========================================
                // NOTIFIKASI
                // ==========================================

                Row(

                    modifier =
                        Modifier
                            .size(44.dp)
                            .background(

                                color =
                                    Color.White,

                                shape =
                                    RoundedCornerShape(
                                        14.dp
                                    )
                            ),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Notifications,

                        contentDescription =
                            "Notifikasi",

                        tint =
                            TextDark,

                        modifier =
                            Modifier.size(
                                23.dp
                            )
                    )
                }
            }
        }


        // ==================================================
        // PANEL ADMIN
        // ==================================================

        item {

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

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
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 20.dp
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
                                "Panel Admin",

                            fontSize =
                                19.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        Text(

                            text =
                                "Pantau dan kelola aktivitas karyawan.",

                            fontSize =
                                12.sp,

                            color =
                                Color.White.copy(
                                    alpha = 0.88f
                                )
                        )
                    }


                    Row(

                        modifier =
                            Modifier
                                .size(56.dp)
                                .background(

                                    color =
                                        Color.White.copy(
                                            alpha = 0.14f
                                        ),

                                    shape =
                                        RoundedCornerShape(
                                            16.dp
                                        )
                                ),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Home,

                            contentDescription =
                                null,

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(
                                    30.dp
                                )
                        )
                    }
                }
            }
        }


        // ==================================================
        // PENGAJUAN BARU
        // ==================================================

        item {

            if (
                jumlahPengajuanMenunggu > 0
            ) {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {

                                onApproval()
                            },

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

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
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 17.dp,
                                    vertical = 15.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(

                            modifier =
                                Modifier
                                    .size(46.dp)
                                    .background(

                                        color =
                                            Color(0xFFFFF3E0),

                                        shape =
                                            RoundedCornerShape(
                                                12.dp
                                            )
                                    ),

                            horizontalArrangement =
                                Arrangement.Center,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.PendingActions,

                                contentDescription =
                                    "Pengajuan baru",

                                tint =
                                    Color(0xFFD97706),

                                modifier =
                                    Modifier.size(
                                        24.dp
                                    )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )


                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Pengajuan Baru",

                                fontSize =
                                    15.sp,

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
                                    "$jumlahPengajuanMenunggu pengajuan menunggu persetujuan",

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
                                Modifier.size(
                                    22.dp
                                )
                        )
                    }
                }
            }
        }


        // ==================================================
        // RINGKASAN
        // ==================================================

        item {

            Text(

                text =
                    "Ringkasan",

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark,

                modifier =
                    Modifier.padding(
                        top = 4.dp
                    )
            )
        }


        // ==================================================
        // STATISTIK RINGKASAN
        // ==================================================

        item {

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                AdminSummaryCard(

                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.NoteAdd,

                    title =
                        "Pengajuan Hari Ini",

                    value =
                        jumlahPengajuanHariIni.toString()
                )


                AdminSummaryCard(

                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.PendingActions,

                    title =
                        "Menunggu Approval",

                    value =
                        jumlahPengajuanMenunggu.toString()
                )
            }
        }


        // ==================================================
        // AKSES CEPAT
        // ==================================================

        item {

            Text(

                text =
                    "Akses Cepat",

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark,

                modifier =
                    Modifier.padding(
                        top = 4.dp
                    )
            )
        }


        // ==================================================
        // APPROVAL
        // ==================================================

        item {

            AdminQuickMenu(

                icon =
                    Icons.Default.NoteAdd,

                title =
                    "Approval",

                subtitle =
                    "Kelola pengajuan karyawan",

                onClick =
                    onApproval
            )
        }


        // ==================================================
        // KARYAWAN
        // ==================================================

        item {

            AdminQuickMenu(

                icon =
                    Icons.Default.Groups,

                title =
                    "Karyawan",

                subtitle =
                    "Kelola data karyawan",

                onClick =
                    onEmployees
            )
        }


        // ==================================================
        // REKAP
        // ==================================================

        item {

            AdminQuickMenu(

                icon =
                    Icons.Default.Assessment,

                title =
                    "Rekap",

                subtitle =
                    "Lihat rekap absensi karyawan",

                onClick =
                    onRecap
            )
        }


        // ==================================================
        // SETTING
        // ==================================================

        item {

            AdminQuickMenu(

                icon =
                    Icons.Default.Settings,

                title =
                    "Setting",

                subtitle =
                    "Pengaturan akun dan aplikasi",

                onClick =
                    onSettings
            )
        }


        // ==================================================
        // KELUAR
        // ==================================================

        item {

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {

                            // onLogout()
                        },

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

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
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 15.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(

                        modifier =
                            Modifier
                                .size(46.dp)
                                .background(

                                    color =
                                        Color(0xFFFDECEC),

                                    shape =
                                        RoundedCornerShape(
                                            12.dp
                                        )
                                ),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Logout,

                            contentDescription =
                                "Keluar",

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier.size(
                                    23.dp
                                )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(14.dp)
                    )


                    Column(

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                "Keluar",

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFFB91C1C)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )


                        Text(

                            text =
                                "Keluar dari akun Admin",

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
                            Modifier.size(
                                21.dp
                            )
                    )
                }
            }
        }


        // ==================================================
        // VERSI
        // ==================================================

        item {

            Text(

                text =
                    "Versi 1.0.0",

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 2.dp,
                            bottom = 14.dp
                        ),

                fontSize =
                    11.sp,

                color =
                    TextGray
            )
        }
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun AdminSummaryCard(

    modifier: Modifier,

    icon: ImageVector,

    title: String,

    value: String

) {

    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                18.dp
            ),

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
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    )
        ) {

            Row(

                modifier =
                    Modifier
                        .size(40.dp)
                        .background(

                            color =
                                Color(0xFFE6EEE9),

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
                        title,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(
                            21.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(11.dp)
            )


            Text(

                text =
                    title,

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
                    value,

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// QUICK MENU
// ==========================================================

@Composable
private fun AdminQuickMenu(

    icon: ImageVector,

    title: String,

    subtitle: String,

    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                },

        shape =
            RoundedCornerShape(
                18.dp
            ),

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
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 15.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(

                modifier =
                    Modifier
                        .size(46.dp)
                        .background(

                            color =
                                Color(0xFFE6EEE9),

                            shape =
                                RoundedCornerShape(
                                    12.dp
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
                        Modifier.size(
                            23.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        title,

                    fontSize =
                        15.sp,

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
                        subtitle,

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
                    Modifier.size(
                        22.dp
                    )
            )
        }
    }
}