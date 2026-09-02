package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// SETTINGS ADMIN
// ==========================================================

@Composable
fun SettingsScreen(

    onBack: () -> Unit,

    onNotification: () -> Unit,

    onTampilan: () -> Unit,

    onBantuan: () -> Unit,

    onTentangAplikasi: () -> Unit,

    onLogout: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .statusBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
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

            Text(

                text =
                    "‹",

                modifier =
                    Modifier
                        .size(40.dp)
                        .clickable {
                            onBack()
                        },

                fontSize =
                    32.sp,

                color =
                    TextDark,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )


            Column {

                Text(

                    text =
                        "Setting",

                    fontSize =
                        26.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Text(

                    text =
                        "Pengaturan aplikasi",

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ==================================================
        // MENU SETTING
        // ==================================================

        SettingsCard {


            // ==================================================
            // NOTIFIKASI
            // ==================================================

            SettingsItem(

                icon =
                    Icons.Default.NotificationsNone,

                title =
                    "Notifikasi",

                subtitle =
                    "Atur pemberitahuan aplikasi",

                onClick =
                    onNotification
            )


            SettingsDivider()


            // ==================================================
            // TAMPILAN
            // ==================================================

            SettingsItem(

                icon =
                    Icons.Default.DarkMode,

                title =
                    "Tampilan",

                subtitle =
                    "Atur mode tampilan aplikasi",

                onClick =
                    onTampilan
            )


            SettingsDivider()


            // ==================================================
            // BANTUAN
            // ==================================================

            SettingsItem(

                icon =
                    Icons.Default.HelpOutline,

                title =
                    "Bantuan",

                subtitle =
                    "Panduan penggunaan aplikasi",

                onClick =
                    onBantuan
            )


            SettingsDivider()


            // ==================================================
            // TENTANG APLIKASI
            // ==================================================

            SettingsItem(

                icon =
                    Icons.Default.Info,

                title =
                    "Tentang Aplikasi",

                subtitle =
                    "Informasi dan pembaruan aplikasi",

                onClick =
                    onTentangAplikasi
            )
        }


        // ==================================================
        // JARAK SEBELUM KELUAR
        // ==================================================

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

                Icon(

                    imageVector =
                        Icons.Default.Logout,

                    contentDescription =
                        "Keluar",

                    tint =
                        Color.Red,

                    modifier =
                        Modifier.size(26.dp)
                )


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
                            Color.Red
                    )


                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )


                    Text(

                        text =
                            "Keluar dari akun",

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
                Modifier.weight(1f)
        )


        // ==================================================
        // VERSI
        // ==================================================

        Text(

            text =
                "© 2026 Absensi Karyawan • Versi 1.1",

            modifier =
                Modifier.fillMaxWidth(),

            textAlign =
                TextAlign.Center,

            fontSize =
                11.sp,

            color =
                TextGray
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )
    }
}


// ==========================================================
// SETTINGS CARD
// ==========================================================

@Composable
private fun SettingsCard(

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
// SETTINGS ITEM
// ==========================================================

@Composable
private fun SettingsItem(

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    title:
    String,

    subtitle:
    String,

    onClick:
        () -> Unit

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

        Icon(

            imageVector =
                icon,

            contentDescription =
                title,

            tint =
                PrimaryGreen,

            modifier =
                Modifier.size(26.dp)
        )


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


        Spacer(
            modifier =
                Modifier.width(8.dp)
        )


        // ==================================================
        // CHEVRON
        // ==================================================

        Icon(

            imageVector =
                Icons.Default.ChevronRight,

            contentDescription =
                null,

            tint =
                TextGray,

            modifier =
                Modifier.size(22.dp)
        )
    }
}


// ==========================================================
// DIVIDER
// ==========================================================

@Composable
private fun SettingsDivider() {

    Spacer(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(
                    horizontal = 18.dp
                )
                .background(
                    Color(0xFFEAEAEA)
                )
    )
}