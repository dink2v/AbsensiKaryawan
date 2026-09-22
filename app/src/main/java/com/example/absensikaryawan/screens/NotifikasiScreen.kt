package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.absensikaryawan.models.Notification
import com.example.absensikaryawan.navigation.NotificationTarget
import com.example.absensikaryawan.repository.NotificationRepository
import com.example.absensikaryawan.ui.theme.Background
import com.example.absensikaryawan.ui.theme.PrimaryGreen
import com.example.absensikaryawan.ui.theme.TextDark
import com.example.absensikaryawan.ui.theme.TextGray
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    onNotificationClick: (NotificationTarget, String) -> Unit
) {

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val repository = remember {
        NotificationRepository()
    }

    val currentUser = auth.currentUser

    val userId = currentUser?.uid.orEmpty()

    var notifications by remember {
        mutableStateOf<List<Notification>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // ==========================================================
    // LISTEN NOTIFICATION
    // ==========================================================

    DisposableEffect(userId) {

        if (userId.isBlank()) {

            isLoading = false

            errorMessage =
                "User belum login."

            onDispose { }

        } else {

            isLoading = true
            errorMessage = null

            val listener =
                repository.listenNotifications(

                    userId = userId,

                    onNotificationsChanged = { data ->

                        notifications = data
                        isLoading = false
                    },

                    onError = { exception ->

                        errorMessage =
                            exception.message
                                ?: "Gagal mengambil notifikasi."

                        isLoading = false
                    }
                )

            onDispose {
                listener.remove()
            }
        }
    }

    // ==========================================================
    // UNREAD
    // ==========================================================

    val unreadCount =
        notifications.count {
            !it.isRead
        }

    // ==========================================================
    // UI
    // ==========================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .systemBarsPadding()
    ) {

        // ======================================================
        // HEADER
        // ======================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ==================================================
            // TOMBOL KEMBALI
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable {
                            onBack()
                        },

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Kembali",

                    tint =
                        TextDark,

                    modifier =
                        Modifier.size(24.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.size(8.dp)
            )

            // ==================================================
            // ICON + JUDUL
            // ==================================================

            Icon(
                imageVector =
                    Icons.Default.Notifications,

                contentDescription =
                    "Notifikasi",

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(28.dp)
            )

            Spacer(
                modifier =
                    Modifier.size(10.dp)
            )

            Text(
                text =
                    "Notifikasi",

                color =
                    TextDark,

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }

        // ======================================================
        // INFO NOTIFIKASI
        // ======================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),

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

                    color =
                        TextDark,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        if (unreadCount > 0) {
                            "$unreadCount notifikasi belum dibaca"
                        } else {
                            "Semua notifikasi sudah dibaca"
                        },

                    color =
                        TextGray,

                    fontSize =
                        13.sp
                )
            }

            if (notifications.isNotEmpty()) {

                Text(
                    text =
                        "Clear All",

                    color =
                        PrimaryGreen,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {

                                notifications.forEach { notification ->

                                    repository.deleteNotification(
                                        notificationId =
                                            notification.id
                                    )
                                }
                            }
                            .padding(
                                horizontal = 8.dp,
                                vertical = 6.dp
                            )
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // ======================================================
        // LOADING
        // ======================================================

        if (isLoading) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "Memuat notifikasi...",

                    color =
                        TextGray,

                    fontSize =
                        14.sp
                )
            }

        } else if (errorMessage != null) {

            // ==================================================
            // ERROR
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        errorMessage
                            ?: "Terjadi kesalahan.",

                    color =
                        TextGray,

                    fontSize =
                        14.sp
                )
            }

        } else if (notifications.isEmpty()) {

            // ==================================================
            // EMPTY
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.NotificationsNone,

                        contentDescription =
                            null,

                        tint =
                            TextGray,

                        modifier =
                            Modifier.size(48.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Belum ada notifikasi",

                        color =
                            TextDark,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Notifikasi baru akan muncul di sini.",

                        color =
                            TextGray,

                        fontSize =
                            13.sp
                    )
                }
            }

        } else {

            // ==================================================
            // LIST NOTIFIKASI
            // ==================================================

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 24.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items =
                        notifications,

                    key = {
                        it.id
                    }
                ) { notification ->

                    NotificationCard(
                        notification =
                            notification,

                        onClick = {

                            if (!notification.isRead) {

                                repository.markAsRead(
                                    notificationId =
                                        notification.id
                                )
                            }

                            val target =
                                getNotificationTarget(
                                    notification
                                )

                            val relatedId =
                                notification.relatedId
                                    .trim()

                            onNotificationClick(
                                target,
                                relatedId
                            )
                        }
                    )
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
    notification: Notification,
    onClick: () -> Unit
) {

    val icon =
        getNotificationIcon(
            type =
                notification.type,

            title =
                notification.title
        )

    val iconColor =
        getNotificationColor(
            type =
                notification.type
        )

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    if (!notification.isRead) {
                        Color.White
                    } else {
                        Color.White.copy(
                            alpha = 0.75f
                        )
                    }
                )
                .clickable {
                    onClick()
                }
                .padding(14.dp),

        verticalAlignment =
            Alignment.Top
    ) {

        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        iconColor.copy(
                            alpha = 0.12f
                        )
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
                    Modifier.size(23.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.size(12.dp)
        )

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
                        notification.title.ifBlank {
                            "Notifikasi"
                        },

                    color =
                        TextDark,

                    fontSize =
                        15.sp,

                    fontWeight =
                        if (!notification.isRead) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },

                    modifier =
                        Modifier.weight(1f)
                )

                if (!notification.isRead) {

                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    PrimaryGreen
                                )
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    notification.message,

                color =
                    TextGray,

                fontSize =
                    13.sp,

                lineHeight =
                    19.sp
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    formatNotificationTime(
                        notification.timestamp
                    ),

                color =
                    TextGray.copy(
                        alpha = 0.8f
                    ),

                fontSize =
                    11.sp
            )
        }
    }
}


// ==========================================================
// ICON NOTIFICATION
// ==========================================================

private fun getNotificationIcon(
    type: String,
    title: String
) =
    when {

        type.contains(
            "ABSENSI",
            ignoreCase = true
        ) ->
            Icons.Default.CheckCircle

        type.contains(
            "PENGAJUAN_DISETUJUI",
            ignoreCase = true
        ) ->
            Icons.Default.CheckCircle

        type.contains(
            "PENGAJUAN_H1",
            ignoreCase = true
        ) ->
            Icons.Default.CheckCircle

        type.contains(
            "PENGAJUAN_DITOLAK",
            ignoreCase = true
        ) ->
            Icons.Default.Info

        type.contains(
            "PENGAJUAN_BARU",
            ignoreCase = true
        ) ->
            Icons.Default.Description

        type.contains(
            "PENGAJUAN_APPROVAL",
            ignoreCase = true
        ) ->
            Icons.Default.Description

        type.contains(
            "CHAT",
            ignoreCase = true
        ) ||
                type.contains(
                    "BALASAN",
                    ignoreCase = true
                ) ||
                type.contains(
                    "PESAN",
                    ignoreCase = true
                ) ->
            Icons.Default.NotificationsNone

        title.contains(
            "PENGAJUAN",
            ignoreCase = true
        ) ->
            Icons.Default.Description

        else ->
            Icons.Default.Info
    }


// ==========================================================
// COLOR NOTIFICATION
// ==========================================================

private fun getNotificationColor(
    type: String
): Color =
    when {

        type.contains(
            "DITOLAK",
            ignoreCase = true
        ) ->
            Color(0xFFD32F2F)

        type.contains(
            "PENGAJUAN_BARU",
            ignoreCase = true
        ) ||
                type.contains(
                    "PENGAJUAN_APPROVAL",
                    ignoreCase = true
                ) ->
            Color(0xFFF9A825)

        type.contains(
            "DISETUJUI",
            ignoreCase = true
        ) ||
                type.contains(
                    "H1",
                    ignoreCase = true
                ) ->
            Color(0xFF2E7D32)

        type.contains(
            "CHAT",
            ignoreCase = true
        ) ||
                type.contains(
                    "BALASAN",
                    ignoreCase = true
                ) ||
                type.contains(
                    "PESAN",
                    ignoreCase = true
                ) ->
            Color(0xFF1976D2)

        type.contains(
            "ABSENSI",
            ignoreCase = true
        ) ->
            Color(0xFF2E7D32)

        else ->
            Color(0xFF1976D2)
    }


// ==========================================================
// TARGET NOTIFICATION
// ==========================================================

private fun getNotificationTarget(
    notification: Notification
): NotificationTarget {

    val type =
        notification.type.uppercase()

    val title =
        notification.title.uppercase()

    return when {

        type.contains(
            "ABSENSI"
        ) ->
            NotificationTarget.RIWAYAT_ABSENSI

        type.contains(
            "PENGAJUAN_DISETUJUI"
        ) ||
                type.contains(
                    "PENGAJUAN_H1"
                ) ->
            NotificationTarget.PENGAJUAN_DISETUJUI

        type.contains(
            "PENGAJUAN_DITOLAK"
        ) ->
            NotificationTarget.PENGAJUAN_DITOLAK

        type.contains(
            "PENGAJUAN_BARU"
        ) ||
                type.contains(
                    "PENGAJUAN_APPROVAL"
                ) ||
                type.contains(
                    "PENGAJUAN_MENUNGGU"
                ) ->
            NotificationTarget.PENGAJUAN_MENUNGGU

        type.contains(
            "CHAT"
        ) ||
                type.contains(
                    "BALASAN"
                ) ||
                type.contains(
                    "PESAN"
                ) ->
            NotificationTarget.CHAT_ADMIN

        title.contains(
            "PENGAJUAN"
        ) ->
            NotificationTarget.PENGAJUAN_MENUNGGU

        else ->
            NotificationTarget.NONE
    }
}


// ==========================================================
// FORMAT WAKTU
// ==========================================================

private fun formatNotificationTime(
    timestamp: Long
): String {

    if (timestamp <= 0L) {
        return ""
    }

    val now =
        System.currentTimeMillis()

    val difference =
        now - timestamp

    if (difference < 60_000L) {
        return "Baru saja"
    }

    val minutes =
        difference / 60_000L

    if (minutes < 60L) {
        return "$minutes menit lalu"
    }

    val hours =
        minutes / 60L

    if (hours < 24L) {
        return "$hours jam lalu"
    }

    return SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale("id", "ID")
    ).format(
        Date(timestamp)
    )
}