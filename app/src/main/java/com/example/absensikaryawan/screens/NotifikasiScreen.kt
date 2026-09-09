package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.navigation.NotificationTarget


// ==========================================================
// MODEL
// ==========================================================

private data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val target: NotificationTarget,
    val read: Boolean = false
)


// ==========================================================
// SCREEN
// ==========================================================

@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    onNotificationClick: (NotificationTarget) -> Unit
) {

    // ======================================================
    // DATA NOTIFIKASI
    // ======================================================

    var notifications by remember {

        mutableStateOf(
            listOf(

                NotificationItem(
                    id = "absensi_berhasil",
                    title = "Absensi Berhasil",
                    message = "Absensi masuk kamu berhasil dicatat.",
                    time = "Hari ini",
                    icon = Icons.Default.CheckCircle,
                    color = PrimaryGreen,
                    target = NotificationTarget.RIWAYAT_ABSENSI
                ),

                NotificationItem(
                    id = "pengajuan_menunggu",
                    title = "Pengajuan Menunggu",
                    message = "Pengajuan kamu sedang menunggu persetujuan.",
                    time = "Hari ini",
                    icon = Icons.Default.Description,
                    color = Color(0xFFD89B00),
                    target = NotificationTarget.PENGAJUAN_MENUNGGU
                ),

                NotificationItem(
                    id = "pengajuan_disetujui",
                    title = "Pengajuan Disetujui",
                    message = "Pengajuan kamu telah disetujui oleh admin.",
                    time = "Hari ini",
                    icon = Icons.Default.CheckCircle,
                    color = PrimaryGreen,
                    target = NotificationTarget.PENGAJUAN_DISETUJUI
                ),

                NotificationItem(
                    id = "pengajuan_ditolak",
                    title = "Pengajuan Ditolak",
                    message = "Pengajuan kamu telah ditolak oleh admin.",
                    time = "Hari ini",
                    icon = Icons.Default.Info,
                    color = Color(0xFFD64545),
                    target = NotificationTarget.PENGAJUAN_DITOLAK
                ),

                NotificationItem(
                    id = "pesan_baru",
                    title = "Pesan Baru",
                    message = "Admin mengirim pesan baru kepada kamu.",
                    time = "Hari ini",
                    icon = Icons.Default.NotificationsNone,
                    color = Color(0xFF2878D8),
                    target = NotificationTarget.CHAT_ADMIN
                ),

                NotificationItem(
                    id = "selamat_datang",
                    title = "Selamat Datang",
                    message = "Selamat datang di aplikasi Absensi Karyawan.",
                    time = "Hari ini",
                    icon = Icons.Default.Info,
                    color = Color(0xFF2878D8),
                    target = NotificationTarget.NONE,
                    read = true
                )
            )
        )
    }


    // ======================================================
    // JUMLAH NOTIFIKASI BELUM DIBACA
    // ======================================================

    val unread = notifications.count {
        !it.read
    }


    // ======================================================
    // ROOT
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(
                WindowInsets.statusBars
            )
            .windowInsetsPadding(
                WindowInsets.navigationBars
            )
            .padding(horizontal = 16.dp)
    ) {


        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 6.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.size(42.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextDark,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "Notifikasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        PrimaryGreen.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }


        // ==================================================
        // INFO + CLEAR ALL
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Notifikasi Kamu",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Informasi terbaru dari aplikasi",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }


            // ==============================================
            // CLEAR ALL
            // ==============================================

            if (notifications.isNotEmpty()) {

                Text(
                    text = "Clear All",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD64545),

                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Color(0xFFD64545).copy(alpha = 0.10f)
                        )
                        .clickable {

                            notifications = emptyList()

                        }
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                )
            }
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ==================================================
        // LIST NOTIFIKASI
        // ==================================================

        if (notifications.isNotEmpty()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = notifications,
                    key = {
                        it.id
                    }
                ) { notification ->


                    // ==========================================
                    // CARD
                    // ==========================================

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.White
                            ),

                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    // ==================================
                                    // TANDAI SUDAH DIBACA
                                    // ==================================

                                    notifications =
                                        notifications.map {

                                            if (
                                                it.id ==
                                                notification.id
                                            ) {

                                                it.copy(
                                                    read = true
                                                )

                                            } else {
                                                it
                                            }
                                        }


                                    // ==================================
                                    // NAVIGASI
                                    // ==================================

                                    if (
                                        notification.target !=
                                        NotificationTarget.NONE
                                    ) {

                                        onNotificationClick(
                                            notification.target
                                        )
                                    }
                                }
                                .padding(13.dp),

                            verticalAlignment =
                                Alignment.Top
                        ) {


                            // ==========================================
                            // ICON
                            // ==========================================

                            Box(
                                modifier = Modifier
                                    .size(43.dp)
                                    .clip(CircleShape)
                                    .background(
                                        notification.color.copy(
                                            alpha = 0.12f
                                        )
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        notification.icon,

                                    contentDescription =
                                        null,

                                    tint =
                                        notification.color,

                                    modifier =
                                        Modifier.size(22.dp)
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(11.dp)
                            )


                            // ==========================================
                            // TEXT
                            // ==========================================

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Text(
                                        text =
                                            notification.title,

                                        fontSize =
                                            13.sp,

                                        fontWeight =
                                            if (
                                                notification.read
                                            ) {
                                                FontWeight.SemiBold
                                            } else {
                                                FontWeight.Bold
                                            },

                                        color =
                                            TextDark,

                                        modifier =
                                            Modifier.weight(1f)
                                    )


                                    // ==================================
                                    // UNREAD DOT
                                    // ==================================

                                    if (
                                        !notification.read
                                    ) {

                                        Box(
                                            modifier =
                                                Modifier
                                                    .size(7.dp)
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
                                        Modifier.height(4.dp)
                                )


                                Text(
                                    text =
                                        notification.message,

                                    fontSize =
                                        11.sp,

                                    color =
                                        TextGray,

                                    lineHeight =
                                        15.sp
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )


                                // ==================================
                                // WAKTU
                                // ==================================

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
                                            Modifier.size(12.dp)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(3.dp)
                                    )

                                    Text(
                                        text =
                                            notification.time,

                                        fontSize =
                                            9.sp,

                                        color =
                                            TextGray
                                    )
                                }
                            }
                        }
                    }
                }


                // ==============================================
                // BOTTOM SPACE
                // ==============================================

                item {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }

        } else {

            // ==================================================
            // EMPTY STATE
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
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
                                Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            "Tidak ada notifikasi",

                        fontSize =
                            14.sp,

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
                            "Semua notifikasi sudah dibersihkan.",

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