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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// MODEL
// ==========================================================

private data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val read: Boolean = false
)


// ==========================================================
// SCREEN
// ==========================================================

@Composable
fun NotifikasiScreen(
    onBack: () -> Unit
) {

    var notifications by remember {

        mutableStateOf(
            listOf(

                NotificationItem(
                    title = "Absensi Berhasil",
                    message = "Absensi masuk kamu berhasil dicatat.",
                    time = "Hari ini",
                    icon = Icons.Default.CheckCircle,
                    color = PrimaryGreen
                ),

                NotificationItem(
                    title = "Pengajuan Menunggu",
                    message = "Pengajuan kamu sedang menunggu persetujuan.",
                    time = "Hari ini",
                    icon = Icons.Default.Description,
                    color = Color(0xFFD89B00)
                ),

                NotificationItem(
                    title = "Selamat Datang",
                    message = "Selamat datang di aplikasi Absensi Karyawan.",
                    time = "Hari ini",
                    icon = Icons.Default.Info,
                    color = Color(0xFF2878D8),
                    read = true
                )
            )
        )
    }


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

            // Aman dari status bar Android
            .windowInsetsPadding(
                WindowInsets.statusBars
            )

            // TURUNKAN LAGI KONTEN APLIKASI
            .padding(
                top = 50.dp
            )

            // Aman dari navigation bar Android
            .windowInsetsPadding(
                WindowInsets.navigationBars
            )

            .padding(
                horizontal = 20.dp
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextDark
                )
            }

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = "Notifikasi",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
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
                    modifier = Modifier.size(23.dp)
                )
            }
        }


        // ==================================================
        // INFO
        // ==================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Notifikasi Kamu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Informasi terbaru dari aplikasi",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            if (unread > 0) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            PrimaryGreen.copy(alpha = 0.12f)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                ) {

                    Text(
                        text = "$unread baru",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // ==================================================
        // LIST
        // ==================================================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(notifications) { notification ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            notifications =
                                notifications.map {

                                    if (it == notification) {
                                        it.copy(read = true)
                                    } else {
                                        it
                                    }
                                }
                        },

                    shape = RoundedCornerShape(18.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),

                        verticalAlignment = Alignment.Top
                    ) {

                        // ==========================================
                        // ICON
                        // ==========================================

                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(
                                    notification.color.copy(
                                        alpha = 0.12f
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = notification.icon,
                                contentDescription = null,
                                tint = notification.color,
                                modifier = Modifier.size(23.dp)
                            )
                        }


                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )


                        // ==========================================
                        // TEXT
                        // ==========================================

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = notification.title,
                                    fontSize = 14.sp,
                                    fontWeight =
                                        if (notification.read) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Bold
                                        },
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )

                                if (!notification.read) {

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                PrimaryGreen
                                            )
                                    )
                                }
                            }


                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )


                            Text(
                                text = notification.message,
                                fontSize = 12.sp,
                                color = TextGray,
                                lineHeight = 17.sp
                            )


                            Spacer(
                                modifier = Modifier.height(7.dp)
                            )


                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextGray,
                                    modifier = Modifier.size(13.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                )

                                Text(
                                    text = notification.time,
                                    fontSize = 10.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }
                }
            }


            item {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }
        }
    }
}
