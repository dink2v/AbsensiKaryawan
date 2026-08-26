package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.AdminProfile
import com.example.absensikaryawan.data.AdminRepository

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// ==========================================================
// ADMIN DASHBOARD
// ==========================================================

@Composable
fun AdminDashboardScreen(

    onLogout: () -> Unit,

    onApproval: () -> Unit,

    // ======================================================
    // BOTTOM NAVIGATION
    // ======================================================

    onDashboard: () -> Unit = {},

    onEmployees: () -> Unit = {},

    onRecap: () -> Unit = {},

    onSettings: () -> Unit = {}

) {

    // ==========================================================
    // ADMIN DATA
    // ==========================================================

    var adminProfile by remember {
        mutableStateOf<AdminProfile?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    // ==========================================================
    // REPOSITORY
    // ==========================================================

    val adminRepository = remember {
        AdminRepository()
    }


    // ==========================================================
    // LOAD ADMIN
    // ==========================================================

    LaunchedEffect(Unit) {

        isLoading = true
        errorMessage = ""

        val result =
            adminRepository.getCurrentAdmin()

        if (result.isSuccess) {

            adminProfile =
                result.getOrNull()

        } else {

            errorMessage =
                result.exceptionOrNull()
                    ?.message
                    ?: "Gagal mengambil data Admin."
        }

        isLoading = false
    }


    // ==========================================================
    // ROOT
    // ==========================================================

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            Background
    ) {

        Column(

            modifier =
                Modifier.fillMaxSize()
        ) {


            // ==================================================
            // DASHBOARD CONTENT
            // ==================================================

            Column(

                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 20.dp
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
                                "Admin / HRD",

                            fontSize =
                                13.sp,

                            color =
                                TextGray
                        )


                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )


                        Text(

                            text =
                                when {

                                    isLoading ->
                                        "Memuat..."

                                    adminProfile != null ->
                                        adminProfile!!.nama
                                            .ifBlank {
                                                "Admin"
                                            }

                                    else ->
                                        "Admin"
                                },

                            fontSize =
                                24.sp,

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
                                when {

                                    adminProfile != null &&
                                            adminProfile!!.divisi.isNotBlank() ->
                                        "${adminProfile!!.jabatan} • ${adminProfile!!.divisi}"

                                    adminProfile != null &&
                                            adminProfile!!.jabatan.isNotBlank() ->
                                        adminProfile!!.jabatan

                                    else ->
                                        "Sistem Absensi Karyawan"
                                },

                            fontSize =
                                13.sp,

                            color =
                                TextGray
                        )
                    }


                    // ==================================================
                    // ADMIN INITIAL
                    // ==================================================

                    Card(

                        shape =
                            RoundedCornerShape(50.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    DarkGreen
                            )
                    ) {

                        Text(

                            text =
                                getInitials(
                                    adminProfile?.nama
                                        ?: "Admin"
                                ),

                            modifier =
                                Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                ),

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // ERROR
                // ==================================================

                if (
                    errorMessage.isNotBlank()
                ) {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFFFE4E6)
                            )
                    ) {

                        Text(

                            text =
                                errorMessage,

                            modifier =
                                Modifier.padding(16.dp),

                            fontSize =
                                13.sp,

                            color =
                                Color(0xFFB91C1C)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )
                }


                // ==================================================
                // RINGKASAN
                // ==================================================

                Text(

                    text =
                        "Ringkasan kehadiran hari ini",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    AdminSummaryCard(

                        modifier =
                            Modifier.weight(1f),

                        number =
                            "4",

                        label =
                            "Hadir",

                        icon =
                            Icons.Default.CheckCircle
                    )


                    AdminSummaryCard(

                        modifier =
                            Modifier.weight(1f),

                        number =
                            "1",

                        label =
                            "Terlambat",

                        icon =
                            Icons.Default.Schedule
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    AdminSummaryCard(

                        modifier =
                            Modifier.weight(1f),

                        number =
                            "1",

                        label =
                            "Izin",

                        icon =
                            Icons.Default.EventAvailable
                    )


                    AdminSummaryCard(

                        modifier =
                            Modifier.weight(1f),

                        number =
                            "1",

                        label =
                            "Alpha",

                        icon =
                            Icons.Default.Warning
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                // ==================================================
                // PENGAJUAN
                // ==================================================

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onApproval()
                            },

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkGreen
                        )
                ) {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                    ) {

                        Row(

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Assignment,

                                contentDescription =
                                    "Pengajuan",

                                tint =
                                    Color.White
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )


                            Text(

                                text =
                                    "3",

                                fontSize =
                                    24.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )


                            Text(

                                text =
                                    "Pengajuan menunggu approval",

                                fontSize =
                                    15.sp,

                                color =
                                    Color.White
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        Text(

                            text =
                                "Ketuk untuk meninjau pengajuan karyawan",

                            fontSize =
                                13.sp,

                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // PERLU DITINJAU
                // ==================================================

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text =
                            "Perlu Ditinjau",

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            "Lihat semua",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            PrimaryGreen
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                AdminApprovalCard(

                    initials =
                        "AW",

                    name =
                        "Andi Wijaya",

                    type =
                        "Cuti Reguler"
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                AdminApprovalCard(

                    initials =
                        "SN",

                    name =
                        "Siti Nurhaliza",

                    type =
                        "Izin Sakit"
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                AdminApprovalCard(

                    initials =
                        "DL",

                    name =
                        "Dewi Lestari",

                    type =
                        "Pulang Cepat"
                )


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // MENU CEPAT
                // ==================================================

                Text(

                    text =
                        "Menu Cepat",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    QuickMenuCard(

                        modifier =
                            Modifier.weight(1f),

                        icon =
                            Icons.Default.Group,

                        title =
                            "Data Karyawan",

                        onClick =
                            onEmployees
                    )


                    QuickMenuCard(

                        modifier =
                            Modifier.weight(1f),

                        icon =
                            Icons.Default.Assessment,

                        title =
                            "Rekap Semua",

                        onClick =
                            onRecap
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // LOGOUT
                // ==================================================

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLogout()
                            },

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

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
                                Color(0xFFB91C1C)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )


                        Text(

                            text =
                                "Keluar",

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                Color(0xFFB91C1C)
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
            //
            // Surface TETAP menempel di bawah.
            //
            // navigationBarsPadding() hanya diberikan
            // kepada isi Row, bukan Surface.
            //
            // Jadi tidak terlihat mengambang.
            // ==================================================

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    Color.White
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(68.dp)
                            .padding(
                                horizontal = 6.dp
                            ),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    // ==================================================
                    // BERANDA
                    // ==================================================

                    AdminBottomItem(

                        icon =
                            Icons.Default.Home,

                        title =
                            "Beranda",

                        selected =
                            true,

                        onClick =
                            onDashboard
                    )


                    // ==================================================
                    // KARYAWAN
                    // ==================================================

                    AdminBottomItem(

                        icon =
                            Icons.Default.Group,

                        title =
                            "Karyawan",

                        onClick =
                            onEmployees
                    )


                    // ==================================================
                    // PENGAJUAN
                    // ==================================================

                    AdminBottomItem(

                        icon =
                            Icons.Default.Assignment,

                        title =
                            "Pengajuan",

                        onClick =
                            onApproval
                    )


                    // ==================================================
                    // REKAP
                    // ==================================================

                    AdminBottomItem(

                        icon =
                            Icons.Default.Assessment,

                        title =
                            "Rekap",

                        onClick =
                            onRecap
                    )


                    // ==================================================
                    // SETTING
                    // ==================================================

                    AdminBottomItem(

                        icon =
                            Icons.Default.Settings,

                        title =
                            "Setting",

                        onClick =
                            onSettings
                    )
                }
            }
        }
    }
}


// ==========================================================
// BOTTOM NAV ITEM
// ==========================================================

@Composable
private fun AdminBottomItem(

    icon: ImageVector,

    title: String,

    selected: Boolean = false,

    onClick: () -> Unit

) {

    val iconColor =

        if (selected) {

            PrimaryGreen

        } else {

            TextGray
        }


    Column(

        modifier =
            Modifier
                .width(68.dp)
                .clickable {
                    onClick()
                }
                .padding(
                    vertical = 6.dp
                ),

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
                iconColor,

            modifier =
                Modifier.size(24.dp)
        )


        Spacer(
            modifier =
                Modifier.height(3.dp)
        )


        Text(

            text =
                title,

            fontSize =
                10.sp,

            fontWeight =

                if (selected) {

                    FontWeight.Bold

                } else {

                    FontWeight.Medium
                },

            color =
                iconColor,

            textAlign =
                TextAlign.Center
        )
    }
}


// ==========================================================
// INITIAL ADMIN
// ==========================================================

private fun getInitials(
    name: String
): String {

    val parts =
        name.trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }


    return when {

        parts.isEmpty() ->
            "A"

        parts.size == 1 ->
            parts[0]
                .take(2)
                .uppercase()

        else ->
            "${parts.first().first()}${parts.last().first()}"
                .uppercase()
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun AdminSummaryCard(

    modifier: Modifier,

    number: String,

    label: String,

    icon: ImageVector

) {

    Card(

        modifier =
            modifier,

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
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    label,

                tint =
                    PrimaryGreen
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(

                text =
                    number,

                fontSize =
                    26.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Text(

                text =
                    label,

                fontSize =
                    13.sp,

                color =
                    TextGray
            )
        }
    }
}


// ==========================================================
// APPROVAL CARD
// ==========================================================

@Composable
private fun AdminApprovalCard(

    initials: String,

    name: String,

    type: String

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

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

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Card(

                shape =
                    RoundedCornerShape(50.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFE6EEE9)
                    )
            ) {

                Text(

                    text =
                        initials,

                    modifier =
                        Modifier.padding(12.dp),

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        PrimaryGreen
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
                        name,

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
                        type,

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )
            }


            Text(

                text =
                    "Menunggu",

                fontSize =
                    12.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    Color(0xFFD97706)
            )
        }
    }
}


// ==========================================================
// QUICK MENU
// ==========================================================

@Composable
private fun QuickMenuCard(

    modifier: Modifier,

    icon: ImageVector,

    title: String,

    onClick: () -> Unit

) {

    Card(

        modifier =
            modifier.clickable {
                onClick()
            },

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
                    .fillMaxWidth()
                    .padding(18.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    title,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}