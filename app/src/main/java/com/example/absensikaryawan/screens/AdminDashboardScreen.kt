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
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ADMIN DASHBOARD
// ==========================================================

@Composable
fun AdminDashboardScreen(

    onApproval: () -> Unit,

    onEmployees: () -> Unit,

    onRecap: () -> Unit,

    onSettings: () -> Unit,

    onLogout: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Text(

            text =
                "Dashboard Admin",

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
                "Kelola aplikasi dan aktivitas karyawan",

            fontSize =
                13.sp,

            color =
                TextGray
        )


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ==================================================
        // MENU UTAMA
        // ==================================================

        Text(

            text =
                "Menu Utama",

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ==================================================
        // APPROVAL
        // ==================================================

        AdminDashboardCard {

            AdminDashboardItem(

                icon =
                    Icons.Default.Approval,

                title =
                    "Approval",

                subtitle =
                    "Kelola pengajuan karyawan",

                onClick =
                    onApproval
            )
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // ==================================================
        // KARYAWAN
        // ==================================================

        AdminDashboardCard {

            AdminDashboardItem(

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


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // ==================================================
        // REKAP
        // ==================================================

        AdminDashboardCard {

            AdminDashboardItem(

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


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // ==================================================
        // SETTING
        // ==================================================

        AdminDashboardCard {

            AdminDashboardItem(

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


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ==================================================
        // KELUAR
        // ==================================================

        Card(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {

                        onLogout()
                    },

            shape =
                RoundedCornerShape(18.dp),

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
                            vertical = 16.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ==========================================
                // LOGOUT ICON
                // ==========================================

                Row(

                    modifier =
                        Modifier
                            .size(46.dp)
                            .background(

                                color =
                                    Color(0xFFFDECEC),

                                shape =
                                    RoundedCornerShape(12.dp)
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
                            Modifier.size(24.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                // ==========================================
                // LOGOUT TEXT
                // ==========================================

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
                            Modifier.height(2.dp)
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
            }
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        // ==================================================
        // VERSI
        // ==================================================

        Text(

            text =
                "Versi 1.0.0",

            modifier =
                Modifier.fillMaxWidth(),

            fontSize =
                11.sp,

            color =
                TextGray
        )
    }
}


// ==========================================================
// DASHBOARD CARD
// ==========================================================

@Composable
private fun AdminDashboardCard(

    content: @Composable () -> Unit

) {

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

                defaultElevation =
                    2.dp
            )
    ) {

        Column(

            modifier =
                Modifier.fillMaxWidth()
        ) {

            content()
        }
    }
}


// ==========================================================
// DASHBOARD ITEM
// ==========================================================

@Composable
private fun AdminDashboardItem(

    icon: ImageVector,

    title: String,

    subtitle: String,

    onClick: () -> Unit

) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                }
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ==================================================
        // ICON
        // ==================================================

        Row(

            modifier =
                Modifier
                    .size(46.dp)
                    .background(

                        color =
                            Color(0xFFE6EEE9),

                        shape =
                            RoundedCornerShape(12.dp)
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
                    Modifier.size(24.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.width(14.dp)
        )


        // ==================================================
        // TEXT
        // ==================================================

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
                    Modifier.height(2.dp)
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


        // ==================================================
        // ARROW
        // ==================================================

        Icon(

            imageVector =
                androidx.compose.material.icons.Icons.Default.ChevronRight,

            contentDescription =
                null,

            tint =
                TextGray,

            modifier =
                Modifier.size(22.dp)
        )
    }
}