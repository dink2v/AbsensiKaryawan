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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// STAFF SETTINGS SCREEN
// ==========================================================

@Composable
fun StaffSettingsScreen(

    onBack: () -> Unit,

    onProfile: () -> Unit = {},

    onLogout: () -> Unit = {}

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
                            start = 12.dp,
                            end = 20.dp,
                            top = 18.dp,
                            bottom = 12.dp
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
                            TextDark
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
                        Modifier.size(24.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(

                    text =
                        "Setting",

                    fontSize =
                        23.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
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

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // ==================================================
                // PROFILE
                // ==================================================

                Text(

                    text =
                        "Akun",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                StaffSettingItem(

                    icon =
                        Icons.Default.Person,

                    title =
                        "Profile",

                    subtitle =
                        "Lihat informasi profile Anda",

                    onClick =
                        onProfile
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // ==================================================
                // KEAMANAN
                // ==================================================

                StaffSettingItem(

                    icon =
                        Icons.Default.Security,

                    title =
                        "Keamanan",

                    subtitle =
                        "Informasi keamanan akun",

                    onClick = {}
                )


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // TENTANG
                // ==================================================

                Text(

                    text =
                        "Tentang",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                StaffSettingItem(

                    icon =
                        Icons.Default.Info,

                    title =
                        "Tentang Aplikasi",

                    subtitle =
                        "Informasi aplikasi Absensi Karyawan",

                    onClick = {}
                )


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

                        Icon(

                            imageVector =
                                Icons.Default.Logout,

                            contentDescription =
                                "Keluar",

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier.size(23.dp)
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
                                    Color(0xFFB91C1C)
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )


                            Text(

                                text =
                                    "Keluar dari akun Anda",

                                fontSize =
                                    12.sp,

                                color =
                                    TextGray
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // VERSI
                // ==================================================

                Column(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text =
                            "Absensi Karyawan",

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(

                        text =
                            "Versi 1.0",

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }
            }
        }
    }
}


// ==========================================================
// SETTING ITEM
// ==========================================================

@Composable
private fun StaffSettingItem(

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

            // ==================================================
            // ICON
            // ==================================================

            Card(

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftGreen
                    )
            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        title,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier
                            .padding(11.dp)
                            .size(22.dp)
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
        }
    }
}