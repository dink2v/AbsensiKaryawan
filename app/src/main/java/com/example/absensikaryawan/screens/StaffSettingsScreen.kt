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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
// STAFF SETTINGS
// ==========================================================

@Composable
fun StaffSettingsScreen(

    onProfile: () -> Unit,

    onNotification: () -> Unit,

    onTampilan: () -> Unit,

    onBantuan: () -> Unit,

    onTentangAplikasi: () -> Unit,

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
                Modifier
                    .fillMaxSize()
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


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Text(

                text =
                    "Pengaturan akun dan aplikasi",

                fontSize =
                    14.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // ==================================================
            // AKUN
            // ==================================================

            Text(

                text =
                    "Akun",

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
            // PROFIL
            // ==================================================

            StaffSettingMenuCard(

                icon =
                    Icons.Default.Person,

                title =
                    "Profil",

                subtitle =
                    "Lihat dan kelola data pribadi",

                onClick =
                    onProfile
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // NOTIFIKASI
            // ==================================================

            StaffSettingMenuCard(

                icon =
                    Icons.Default.NotificationsNone,

                title =
                    "Notifikasi",

                subtitle =
                    "Atur pemberitahuan aplikasi",

                onClick =
                    onNotification
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // ==================================================
            // PREFERENSI
            // ==================================================

            Text(

                text =
                    "Preferensi",

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
            // TAMPILAN
            // ==================================================

            StaffSettingMenuCard(

                icon =
                    Icons.Default.Palette,

                title =
                    "Tampilan",

                subtitle =
                    "Atur mode tampilan aplikasi",

                onClick =
                    onTampilan
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // ==================================================
            // BANTUAN
            // ==================================================

            Text(

                text =
                    "Bantuan",

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


            StaffSettingMenuCard(

                icon =
                    Icons.Default.HelpOutline,

                title =
                    "Bantuan",

                subtitle =
                    "Panduan penggunaan aplikasi",

                onClick =
                    onBantuan
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // TENTANG APLIKASI
            // ==================================================

            StaffSettingMenuCard(

                icon =
                    Icons.Default.Info,

                title =
                    "Tentang Aplikasi",

                subtitle =
                    "Informasi aplikasi",

                onClick =
                    onTentangAplikasi
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // ==================================================
            // LOGOUT
            // ==========================================================

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {

                            onLogout()
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
                            2.dp

                    )

            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                16.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    // ==================================================
                    // LOGOUT ICON
                    // ==================================================

                    Surface(

                        modifier =
                            Modifier.size(46.dp),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        color =
                            Color(0xFFFDECEC)

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Logout,

                            contentDescription =
                                "Keluar",

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier
                                    .padding(11.dp)
                                    .size(24.dp)
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
                            Modifier.weight(
                                1f
                            )

                    ) {

                        Text(

                            text =
                                "Keluar",

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                Color(0xFFB91C1C)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )


                        Text(

                            text =
                                "Keluar dari akun Staff",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )
                    }


                    // ==================================================
                    // CHEVRON
                    // ==================================================

                    Icon(

                        imageVector =
                            Icons.Default.ChevronRight,

                        contentDescription =
                            "Keluar",

                        tint =
                            TextGray,

                        modifier =
                            Modifier.size(
                                22.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }
}


// ==========================================================
// STAFF SETTING MENU CARD
// ==========================================================

@Composable
private fun StaffSettingMenuCard(

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
                    2.dp

            )

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 15.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            // ==================================================
            // ICON BACKGROUND
            // ==================================================

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


            // ==================================================
            // TEXT
            // ==================================================

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )

            ) {

                Text(

                    text =
                        title,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.SemiBold,

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
                        12.sp,

                    color =
                        TextGray
                )
            }


            // ==================================================
            // CHEVRON
            // ==================================================

            Icon(

                imageVector =
                    Icons.Default.ChevronRight,

                contentDescription =
                    "Buka $title",

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