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
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ADMIN SETTINGS SCREEN
// ==========================================================

@Composable
fun AdminSettingsScreen(

    onBack: () -> Unit,

    onProfile: () -> Unit = {},

    onNotification: () -> Unit = {},

    onAppearance: () -> Unit = {},

    onHelp: () -> Unit = {},

    onAbout: () -> Unit = {},

    onLogout: () -> Unit

) {

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
            // HEADER
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(

                    onClick =
                        onBack
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            PrimaryGreen
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                Icon(

                    imageVector =
                        Icons.Default.Settings,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(25.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                Column {

                    Text(

                        text =
                            "Pengaturan Admin",

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            "Kelola pengaturan aplikasi",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }
            }


            // ==================================================
            // CONTENT
            // ==================================================

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 20.dp
                        )
            ) {

                // ==================================================
                // AKUN
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(

                    text =
                        "Akun",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                AdminSettingItem(

                    icon =
                        Icons.Default.Person,

                    title =
                        "Profil Admin",

                    subtitle =
                        "Lihat informasi akun admin",

                    onClick =
                        onProfile
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                AdminSettingItem(

                    icon =
                        Icons.Default.Notifications,

                    title =
                        "Notifikasi",

                    subtitle =
                        "Kelola notifikasi aplikasi",

                    onClick =
                        onNotification
                )


                // ==================================================
                // APLIKASI
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )


                Text(

                    text =
                        "Aplikasi",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                AdminSettingItem(

                    icon =
                        Icons.Default.DarkMode,

                    title =
                        "Tampilan",

                    subtitle =
                        "Atur mode tampilan aplikasi",

                    onClick =
                        onAppearance
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                AdminSettingItem(

                    icon =
                        Icons.Default.HelpOutline,

                    title =
                        "Bantuan",

                    subtitle =
                        "Panduan penggunaan aplikasi",

                    onClick =
                        onHelp
                )


                // ==================================================
                // TENTANG
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )


                Text(

                    text =
                        "Tentang",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                AdminSettingItem(

                    icon =
                        Icons.Default.Info,

                    title =
                        "Tentang Aplikasi",

                    subtitle =
                        "Informasi aplikasi",

                    onClick =
                        onAbout
                )


                // ==================================================
                // LOGOUT
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(15.dp),

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
                                .background(
                                    Color.White
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 15.dp
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
                                Color(0xFFDC2626),

                            modifier =
                                Modifier.size(23.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(13.dp)
                        )


                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Keluar",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    Color(0xFFDC2626)
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    "Keluar dari akun admin",

                                fontSize =
                                    11.sp,

                                color =
                                    TextGray
                            )
                        }


                        IconButton(

                            onClick =
                                onLogout
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.ChevronRight,

                                contentDescription =
                                    "Keluar",

                                tint =
                                    Color(0xFFDC2626)
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}


// ==========================================================
// SETTING ITEM
// ==========================================================

@Composable
private fun AdminSettingItem(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    subtitle: String,

    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            ),

        onClick =
            onClick
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(

                modifier =
                    Modifier
                        .size(43.dp)
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
                        Modifier.size(21.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
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
                    Modifier.size(21.dp)
            )
        }
    }
}