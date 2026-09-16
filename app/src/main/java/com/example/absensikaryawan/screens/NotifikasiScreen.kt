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

                    // ==========================================
                    // TENTUKAN ICON
                    // ==========================================

                    val icon =
                        getNotificationIcon(
                            notification.type,
                            notification.title
                        )

                    // ==========================================
                    // TENTUKAN WARNA
                    // ==========================================

                    val iconColor =
                        getNotificationColor(
                            notification.type,
                            notification.title
                        )

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

                            // ==========================================
                            // ICON
                            // ==========================================

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

                                    // ==================================
                                    // UNREAD DOT
                                    // ==================================

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

    val value =
        "$type $title".uppercase(Locale.getDefault())

    return when {

        value.contains("ABSENSI") ->
            Icons.Default.CheckCircle

        value.contains("PENGAJUAN") ->
            if (value.contains("DISETUJUI")) {
                Icons.Default.CheckCircle
            } else if (value.contains("DITOLAK")) {
                Icons.Default.Info
            } else {
                Icons.Default.Description
            }

        value.contains("CHAT") ||
                value.contains("PESAN") ||
                value.contains("BALASAN") ->
            Icons.Default.NotificationsNone

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

    val value =
        "$type $title".uppercase(Locale.getDefault())

    return when {

        value.contains("DITOLAK") ->
            Color(0xFFD64545)

        value.contains("MENUNGGU") ->
            Color(0xFFD89B00)

        value.contains("CHAT") ||
                value.contains("PESAN") ||
                value.contains("BALASAN") ->
            Color(0xFF2878D8)

        value.contains("ABSENSI") ||
                value.contains("DISETUJUI") ->
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

    return when (
        notification.type
            .uppercase(Locale.getDefault())
    ) {

        "ABSENSI" ->
            NotificationTarget.RIWAYAT_ABSENSI

        "PENGAJUAN" -> {

            when (
                notification.targetValue()
            ) {

                "PENGAJUAN_DISETUJUI" ->
                    NotificationTarget.PENGAJUAN_DISETUJUI

                "PENGAJUAN_DITOLAK" ->
                    NotificationTarget.PENGAJUAN_DITOLAK

                "PENGAJUAN_MENUNGGU" ->
                    NotificationTarget.PENGAJUAN_MENUNGGU

                "PENGAJUAN_BARU" ->
                    NotificationTarget.PENGAJUAN_MENUNGGU

                else -> {

                    val title =
                        notification.title
                            .uppercase(
                                Locale.getDefault()
                            )

                    when {

                        title.contains("DISETUJUI") ->
                            NotificationTarget.PENGAJUAN_DISETUJUI

                        title.contains("DITOLAK") ->
                            NotificationTarget.PENGAJUAN_DITOLAK

                        title.contains("MENUNGGU") ->
                            NotificationTarget.PENGAJUAN_MENUNGGU

                        else ->
                            NotificationTarget.PENGAJUAN_MENUNGGU
                    }
                }
            }
        }

        "CHAT" ->
            NotificationTarget.CHAT_ADMIN

        else -> {

            val value =
                "${notification.type} ${notification.title}"
                    .uppercase(Locale.getDefault())

            when {

                value.contains("BALASAN") ->
                    NotificationTarget.CHAT_ADMIN

                value.contains("PESAN") ->
                    NotificationTarget.CHAT_ADMIN

                value.contains("ABSENSI") ->
                    NotificationTarget.RIWAYAT_ABSENSI

                value.contains("DISETUJUI") ->
                    NotificationTarget.PENGAJUAN_DISETUJUI

                value.contains("DITOLAK") ->
                    NotificationTarget.PENGAJUAN_DITOLAK

                value.contains("MENUNGGU") ->
                    NotificationTarget.PENGAJUAN_MENUNGGU

                else ->
                    NotificationTarget.NONE
            }
        }
    }
}

// ==========================================================
// NOTIFICATION TARGET VALUE
// ==========================================================
//
// Karena model Notification saat ini hanya punya relatedId,
// kita gunakan relatedId/type/title untuk kompatibilitas dengan
// data Firestore yang sudah ada.
//

private fun Notification.targetValue(): String {

    val title =
        title.uppercase(
            Locale.getDefault()
        )

    return when {

        title.contains("DISETUJUI") ->
            "PENGAJUAN_DISETUJUI"

        title.contains("DITOLAK") ->
            "PENGAJUAN_DITOLAK"

        title.contains("MENUNGGU") ->
            "PENGAJUAN_MENUNGGU"

        title.contains("BARU") &&
                type.uppercase(Locale.getDefault())
                    .contains("PENGAJUAN") ->
            "PENGAJUAN_BARU"

        else ->
            ""
    }
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