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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================================
// SCREEN
// ==========================================================

@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    onNotificationClick: (NotificationTarget) -> Unit
) {

    // ======================================================
    // FIREBASE
    // ======================================================

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val notificationRepository = remember {
        NotificationRepository()
    }

    // ======================================================
    // STATE
    // ======================================================

    var notifications by remember {
        mutableStateOf<List<Notification>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    // ======================================================
    // REALTIME LISTENER
    // ======================================================

    DisposableEffect(userId) {

        if (userId.isNullOrBlank()) {

            notifications = emptyList()
            isLoading = false
            errorMessage = "User belum login."

            onDispose {}

        } else {

            isLoading = true
            errorMessage = ""

            val listener =
                notificationRepository.listenNotifications(
                    userId = userId,

                    onNotificationsChanged = { data ->

                        notifications = data
                        isLoading = false
                        errorMessage = ""
                    },

                    onError = { exception ->

                        isLoading = false
                        errorMessage =
                            exception.message
                                ?: "Gagal memuat notifikasi."
                    }
                )

            onDispose {
                listener.remove()
            }
        }
    }

    // ======================================================
    // JUMLAH BELUM DIBACA
    // ======================================================

    val unread = notifications.count {
        !it.isRead
    }

    // ======================================================
    // ROOT
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
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
                    text =
                        if (unread > 0) {
                            "$unread notifikasi belum dibaca"
                        } else {
                            "Informasi terbaru dari aplikasi"
                        },
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

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
                            Color(0xFFD64545)
                                .copy(alpha = 0.10f)
                        )
                        .clickable {

                            notifications.forEach { notification ->

                                notificationRepository
                                    .deleteNotification(
                                        notificationId =
                                            notification.id
                                    )
                            }
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
        // LOADING
        // ==================================================

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryGreen,
                    strokeWidth = 3.dp
                )
            }

        } else if (errorMessage.isNotBlank()) {

            // ==================================================
            // ERROR
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),

                contentAlignment = Alignment.Center
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
                                Color(0xFFD64545)
                                    .copy(alpha = 0.10f)
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Info,

                            contentDescription = null,

                            tint =
                                Color(0xFFD64545),

                            modifier =
                                Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Gagal memuat notifikasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }

        } else if (notifications.isNotEmpty()) {

            // ==================================================
            // LIST NOTIFIKASI
            // ==================================================

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

                    val icon =
                        getNotificationIcon(
                            notification.type,
                            notification.title
                        )

                    val iconColor =
                        getNotificationColor(
                            notification.type,
                            notification.title
                        )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    if (!notification.isRead) {
                                        Color.White
                                    } else {
                                        Color.White.copy(
                                            alpha = 0.88f
                                        )
                                    }
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

                                    if (!notification.isRead) {

                                        notificationRepository
                                            .markAsRead(
                                                notificationId =
                                                    notification.id
                                            )
                                    }

                                    // ==================================
                                    // NAVIGASI
                                    // ==================================

                                    val target =
                                        getNotificationTarget(
                                            notification
                                        )

                                    if (
                                        target !=
                                        NotificationTarget.NONE
                                    ) {

                                        onNotificationClick(
                                            target
                                        )
                                    }
                                }
                                .padding(13.dp),

                            verticalAlignment =
                                Alignment.Top
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(43.dp)
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
                                    imageVector = icon,

                                    contentDescription = null,

                                    tint = iconColor,

                                    modifier =
                                        Modifier.size(22.dp)
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(11.dp)
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
                                            notification.title,

                                        fontSize =
                                            13.sp,

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
                                            Modifier.weight(1f)
                                    )

                                    if (
                                        !notification.isRead
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
                                            formatNotificationTime(
                                                notification.timestamp
                                            ),

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
                            "Belum ada notifikasi untuk kamu.",

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
// NOTIFICATION ICON
// ==========================================================

private fun getNotificationIcon(
    type: String,
    title: String
): androidx.compose.ui.graphics.vector.ImageVector {

    val normalizedType =
        type.trim().uppercase(Locale.getDefault())

    val normalizedTitle =
        title.trim().uppercase(Locale.getDefault())

    return when {

        normalizedType == "ABSENSI" ->
            Icons.Default.CheckCircle

        normalizedType == "PENGAJUAN_DISETUJUI" ||
                normalizedType == "PENGAJUAN_DISETUJUI_H1" ->
            Icons.Default.CheckCircle

        normalizedType == "PENGAJUAN_DITOLAK" ->
            Icons.Default.Info

        normalizedType == "PENGAJUAN_BARU" ||
                normalizedType == "PENGAJUAN_APPROVAL" ->
            Icons.Default.Description

        normalizedType == "CHAT" ||
                normalizedType.contains("CHAT") ||
                normalizedType.contains("BALASAN") ||
                normalizedType.contains("PESAN") ->
            Icons.Default.NotificationsNone

        normalizedTitle.contains("PENGAJUAN") ->
            Icons.Default.Description

        else ->
            Icons.Default.Info
    }
}

// ==========================================================
// NOTIFICATION COLOR
// ==========================================================

private fun getNotificationColor(
    type: String,
    title: String
): Color {

    val normalizedType =
        type.trim().uppercase(Locale.getDefault())

    val normalizedTitle =
        title.trim().uppercase(Locale.getDefault())

    return when {

        normalizedType == "PENGAJUAN_DITOLAK" ||
                normalizedTitle.contains("DITOLAK") ->
            Color(0xFFD64545)

        normalizedType == "PENGAJUAN_BARU" ||
                normalizedType == "PENGAJUAN_APPROVAL" ||
                normalizedTitle.contains("MENUNGGU") ->
            Color(0xFFD89B00)

        normalizedType == "PENGAJUAN_DISETUJUI" ||
                normalizedType == "PENGAJUAN_DISETUJUI_H1" ||
                normalizedTitle.contains("DISETUJUI") ->
            PrimaryGreen

        normalizedType.contains("CHAT") ||
                normalizedType.contains("PESAN") ||
                normalizedType.contains("BALASAN") ->
            Color(0xFF2878D8)

        normalizedType.contains("ABSENSI") ->
            PrimaryGreen

        else ->
            Color(0xFF2878D8)
    }
}

// ==========================================================
// NOTIFICATION TARGET
// ==========================================================

private fun getNotificationTarget(
    notification: Notification
): NotificationTarget {

    val type =
        notification.type
            .trim()
            .uppercase(Locale.getDefault())

    val title =
        notification.title
            .trim()
            .uppercase(Locale.getDefault())

    // ======================================================
    // ABSENSI
    // ======================================================

    if (type == "ABSENSI") {
        return NotificationTarget.RIWAYAT_ABSENSI
    }

    // ======================================================
    // PENGAJUAN
    // ======================================================

    when (type) {

        "PENGAJUAN_BARU",
        "PENGAJUAN_APPROVAL",
        "PENGAJUAN_MENUNGGU" -> {
            return NotificationTarget.PENGAJUAN_MENUNGGU
        }

        "PENGAJUAN_DISETUJUI",
        "PENGAJUAN_DISETUJUI_H1" -> {
            return NotificationTarget.PENGAJUAN_DISETUJUI
        }

        "PENGAJUAN_DITOLAK" -> {
            return NotificationTarget.PENGAJUAN_DITOLAK
        }
    }

    // ======================================================
    // CHAT
    // ======================================================

    if (
        type == "CHAT" ||
        type.contains("CHAT") ||
        type.contains("BALASAN") ||
        type.contains("PESAN")
    ) {
        return NotificationTarget.CHAT_ADMIN
    }

    // ======================================================
    // FALLBACK BERDASARKAN JUDUL
    // Untuk menjaga kompatibilitas data lama.
    // ======================================================

    if (title.contains("ABSENSI")) {
        return NotificationTarget.RIWAYAT_ABSENSI
    }

    if (title.contains("DISETUJUI")) {
        return NotificationTarget.PENGAJUAN_DISETUJUI
    }

    if (title.contains("DITOLAK")) {
        return NotificationTarget.PENGAJUAN_DITOLAK
    }

    if (
        title.contains("MENUNGGU") ||
        title.contains("PENGAJUAN BARU")
    ) {
        return NotificationTarget.PENGAJUAN_MENUNGGU
    }

    return NotificationTarget.NONE
}

// ==========================================================
// FORMAT WAKTU
// ==========================================================

private fun formatNotificationTime(
    timestamp: Long
): String {

    if (timestamp <= 0L) {
        return "Baru saja"
    }

    val now =
        System.currentTimeMillis()

    val difference =
        now - timestamp

    val minute =
        60_000L

    val hour =
        60 * minute

    val day =
        24 * hour

    return when {

        difference < minute ->
            "Baru saja"

        difference < hour -> {

            val minutes =
                difference / minute

            "$minutes menit lalu"
        }

        difference < day -> {

            val hours =
                difference / hour

            "$hours jam lalu"
        }

        else -> {

            SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale("id", "ID")
            ).format(
                Date(timestamp)
            )
        }
    }
}