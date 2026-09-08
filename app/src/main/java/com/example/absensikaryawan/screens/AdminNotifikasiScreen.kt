package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ADMIN NOTIFIKASI
// ==========================================================

@Composable
fun AdminNotifikasiScreen(
    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Background
                )
                .padding(
                    horizontal = 20.dp
                )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
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
                        PrimaryGreen
                )
            }


            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )


            Text(

                text =
                    "Notifikasi",

                modifier =
                    Modifier.weight(1f),

                fontSize =
                    24.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Icon(

                imageVector =
                    Icons.Default.Notifications,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(
                        24.dp
                    )
            )
        }


        // ==================================================
        // SUBTITLE
        // ==================================================

        Text(

            text =
                "Informasi aktivitas aplikasi",

            fontSize =
                13.sp,

            color =
                TextGray,

            modifier =
                Modifier.padding(
                    start = 4.dp
                )
        )


        Spacer(
            modifier =
                Modifier.size(20.dp)
        )


        // ==================================================
        // NOTIFIKASI PENGAJUAN
        // ==================================================

        AdminNotificationItem(

            icon =
                Icons.Default.PendingActions,

            iconBackground =
                Color(0xFFFFF3E0),

            iconColor =
                Color(0xFFD97706),

            title =
                "Pengajuan Menunggu",

            description =
                "Terdapat pengajuan karyawan yang menunggu persetujuan.",

            onClick = {

                // Untuk sementara belum diarahkan
                // ke halaman tertentu.
            }
        )


        Spacer(
            modifier =
                Modifier.size(12.dp)
        )


        // ==================================================
        // NOTIFIKASI INFORMASI
        // ==================================================

        AdminNotificationItem(

            icon =
                Icons.Default.Info,

            iconBackground =
                Color(0xFFE8F5E9),

            iconColor =
                PrimaryGreen,

            title =
                "Informasi Sistem",

            description =
                "Data dashboard admin diperbarui berdasarkan data terbaru.",

            onClick = {

                // Informasi sistem.
            }
        )


        Spacer(
            modifier =
                Modifier.size(12.dp)
        )


        // ==================================================
        // NOTIFIKASI KEHADIRAN
        // ==================================================

        AdminNotificationItem(

            icon =
                Icons.Default.CheckCircle,

            iconBackground =
                Color(0xFFE8F5E9),

            iconColor =
                PrimaryGreen,

            title =
                "Kehadiran Hari Ini",

            description =
                "Data kehadiran karyawan hari ini tersedia di dashboard.",

            onClick = {

                // Informasi kehadiran.
            }
        )


        Spacer(
            modifier =
                Modifier.weight(1f)
        )
    }
}


// ==========================================================
// NOTIFICATION ITEM
// ==========================================================

@Composable
private fun AdminNotificationItem(

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    iconBackground:
    Color,

    iconColor:
    Color,

    title:
    String,

    description:
    String,

    onClick:
        () -> Unit

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
                        horizontal = 17.dp,
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
                                iconBackground,

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
                        iconColor,

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
                        Modifier.size(4.dp)
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
        }
    }
}