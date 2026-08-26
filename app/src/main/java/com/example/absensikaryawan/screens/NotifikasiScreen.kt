package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


// ==========================================================
// MODEL NOTIFIKASI
// ==========================================================

private data class NotificationItem(

    val title: String,

    val message: String,

    val time: String,

    val type: NotificationType,

    val isRead: Boolean = false
)


// ==========================================================
// TYPE NOTIFIKASI
// ==========================================================

private enum class NotificationType {

    Absensi,

    Pengajuan,

    Informasi,

    Peringatan
}


// ==========================================================
// NOTIFIKASI SCREEN
// ==========================================================

@Composable
fun NotifikasiScreen(

    onBack: () -> Unit

) {

    // ==========================================================
    // DATA SEMENTARA
    // ==========================================================

    var notifications by remember {

        mutableStateOf(

            listOf(

                NotificationItem(

                    title =
                        "Absensi Berhasil",

                    message =
                        "Absensi masuk kamu berhasil dicatat.",

                    time =
                        "Hari ini",

                    type =
                        NotificationType.Absensi,

                    isRead =
                        false
                ),

                NotificationItem(

                    title =
                        "Pengajuan Menunggu",

                    message =
                        "Pengajuan kamu sedang menunggu persetujuan.",

                    time =
                        "Hari ini",

                    type =
                        NotificationType.Pengajuan,

                    isRead =
                        false
                ),

                NotificationItem(

                    title =
                        "Selamat Datang",

                    message =
                        "Selamat datang di aplikasi Absensi Karyawan.",

                    time =
                        "Hari ini",

                    type =
                        NotificationType.Informasi,

                    isRead =
                        true
                )
            )
        )
    }


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
                            top = 12.dp,
                            bottom = 12.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                // ==============================================
                // BACK BUTTON
                // ==============================================

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
                            TextDark,

                        modifier =
                            Modifier.size(
                                25.dp
                            )
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                // ==============================================
                // TITLE
                // ==============================================

                Text(

                    text =
                        "Notifikasi",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark,

                    modifier =
                        Modifier.weight(1f)
                )


                // ==============================================
                // ICON
                // ==============================================

                Icon(

                    imageVector =
                        Icons.Default.NotificationsNone,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(
                            27.dp
                        )
                )
            }


            // ==================================================
            // PEMBATAS
            // ==================================================

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            // ==================================================
            // HEADER INFO
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
                            "Notifikasi Kamu",

                        fontSize =
                            18.sp,

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
                            "Informasi terbaru dari aplikasi",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                // ==============================================
                // JUMLAH BELUM DIBACA
                // ==============================================

                val unreadCount =
                    notifications.count {
                        !it.isRead
                    }


                if (
                    unreadCount > 0
                ) {

                    Box(

                        modifier =
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        12.dp
                                    )
                                )
                                .background(
                                    PrimaryGreen.copy(
                                        alpha = 0.12f
                                    )
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp
                                )

                    ) {

                        Text(

                            text =
                                "$unreadCount baru",

                            fontSize =
                                11.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // ==================================================
            // DAFTAR NOTIFIKASI
            // ==================================================

            if (
                notifications.isEmpty()
            ) {

                // ==============================================
                // EMPTY STATE
                // ==============================================

                EmptyNotificationState()

            } else {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )

                ) {

                    items(

                        items =
                            notifications

                    ) { notification ->

                        NotificationCard(

                            notification =
                                notification,

                            onClick = {

                                notifications =
                                    notifications.map {

                                        if (
                                            it == notification
                                        ) {

                                            it.copy(
                                                isRead = true
                                            )

                                        } else {

                                            it
                                        }
                                    }
                            }
                        )
                    }


                    item {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    20.dp
                                )
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// NOTIFICATION CARD
// ==========================================================

@Composable
private fun NotificationCard(

    notification: NotificationItem,

    onClick: () -> Unit

) {

    val icon =
        when (
            notification.type
        ) {

            NotificationType.Absensi ->
                Icons.Default.CheckCircle

            NotificationType.Pengajuan ->
                Icons.Default.Description

            NotificationType.Informasi ->
                Icons.Default.Info

            NotificationType.Peringatan ->
                Icons.Default.Warning
        }


    val iconBackground =
        when (
            notification.type
        ) {

            NotificationType.Absensi ->
                PrimaryGreen.copy(
                    alpha = 0.12f
                )

            NotificationType.Pengajuan ->
                Color(0xFFFFF3CD)

            NotificationType.Informasi ->
                Color(0xFFE8F1FF)

            NotificationType.Peringatan ->
                Color(0xFFFFE8E8)
        }


    val iconColor =
        when (
            notification.type
        ) {

            NotificationType.Absensi ->
                PrimaryGreen

            NotificationType.Pengajuan ->
                Color(0xFFD89B00)

            NotificationType.Informasi ->
                Color(0xFF2878D8)

            NotificationType.Peringatan ->
                Color(0xFFD32F2F)
        }


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (
                        notification.isRead
                    ) {

                        Color.White

                    } else {

                        PrimaryGreen.copy(
                            alpha = 0.035f
                        )
                    }
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
                        15.dp
                    ),

            verticalAlignment =
                Alignment.Top

        ) {

            // ==================================================
            // ICON
            // ==================================================

            Box(

                modifier =
                    Modifier
                        .size(
                            45.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            iconBackground
                        ),

                contentAlignment =
                    Alignment.Center

            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    tint =
                        iconColor,

                    modifier =
                        Modifier.size(
                            24.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


            // ==================================================
            // CONTENT
            // ==================================================

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )

            ) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(

                        text =
                            notification.title,

                        fontSize =
                            14.sp,

                        fontWeight =
                            if (
                                notification.isRead
                            ) {

                                FontWeight.SemiBold

                            } else {

                                FontWeight.Bold
                            },

                        color =
                            TextDark,

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    // ==========================================
                    // TITIK BELUM DIBACA
                    // ==========================================

                    if (
                        !notification.isRead
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .size(
                                        8.dp
                                    )
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        PrimaryGreen
                                    )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )


                Text(

                    text =
                        notification.message,

                    fontSize =
                        12.sp,

                    lineHeight =
                        18.sp,

                    color =
                        TextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                Row(

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Schedule,

                        contentDescription =
                            null,

                        tint =
                            TextGray,

                        modifier =
                            Modifier.size(
                                14.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                4.dp
                            )
                    )


                    Text(

                        text =
                            notification.time,

                        fontSize =
                            10.sp,

                        color =
                            TextGray
                    )
                }
            }
        }
    }
}


// ==========================================================
// EMPTY NOTIFICATION STATE
// ==========================================================

@Composable
private fun EmptyNotificationState() {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 80.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Box(

            modifier =
                Modifier
                    .size(
                        70.dp
                    )
                    .clip(
                        CircleShape
                    )
                    .background(
                        PrimaryGreen.copy(
                            alpha = 0.10f
                        )
                    ),

            contentAlignment =
                Alignment.Center

        ) {

            Icon(

                imageVector =
                    Icons.Default.NotificationsNone,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(
                        34.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        Text(

            text =
                "Belum Ada Notifikasi",

            fontSize =
                17.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )


        Text(

            text =
                "Notifikasi terbaru akan muncul di sini.",

            fontSize =
                12.sp,

            color =
                TextGray
        )
    }
}