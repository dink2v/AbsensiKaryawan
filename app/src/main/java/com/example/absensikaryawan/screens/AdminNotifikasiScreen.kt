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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.models.Notification
import com.example.absensikaryawan.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================================
// ADMIN NOTIFIKASI
// ==========================================================

@Composable
fun AdminNotifikasiScreen(
    onBack: () -> Unit,
    onApprovalClick: (String) -> Unit,
    onRekapClick: () -> Unit,
    onChatClick: () -> Unit
) {

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val notificationRepository =
        remember {
            NotificationRepository()
        }

    val currentUserId =
        auth.currentUser?.uid.orEmpty()

    var notifications by remember {
        mutableStateOf<List<Notification>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // ======================================================
    // REALTIME FIRESTORE LISTENER
    // ======================================================

    DisposableEffect(currentUserId) {

        if (currentUserId.isBlank()) {

            isLoading = false

            errorMessage =
                "Admin belum login."

            onDispose { }

        } else {

            val listener =
                notificationRepository.listenNotifications(

                    userId =
                        currentUserId,

                    onNotificationsChanged = { data ->

                        notifications =
                            data

                        isLoading =
                            false

                        errorMessage =
                            null
                    },

                    onError = { exception ->

                        isLoading =
                            false

                        errorMessage =
                            exception.message
                                ?: "Gagal mengambil notifikasi."
                    }
                )

            onDispose {
                listener.remove()
            }
        }
    }

    val unreadCount =
        notifications.count {
            !it.isRead
        }

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

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        "Notifikasi",

                    fontSize =
                        24.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )

                if (unreadCount > 0) {

                    Text(

                        text =
                            "$unreadCount belum dibaca",

                        fontSize =
                            11.sp,

                        color =
                            PrimaryGreen,

                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }

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
                Modifier.size(16.dp)
        )

        // ==================================================
        // ERROR
        // ==================================================

        if (errorMessage != null) {

            AdminNotificationMessage(

                icon =
                    Icons.Default.Error,

                title =
                    "Gagal memuat notifikasi",

                description =
                    errorMessage
                        ?: "Terjadi kesalahan."
            )
        }

        // ==================================================
        // LOADING
        // ==================================================

        else if (isLoading) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 40.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(

                    color =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(
                            32.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.size(12.dp)
                )

                Text(

                    text =
                        "Memuat notifikasi...",

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )
            }
        }

        // ==================================================
        // EMPTY
        // ==================================================

        else if (notifications.isEmpty()) {

            AdminNotificationMessage(

                icon =
                    Icons.Default.Notifications,

                title =
                    "Belum ada notifikasi",

                description =
                    "Belum ada aktivitas yang perlu ditampilkan."
            )
        }

        // ==================================================
        // LIST NOTIFIKASI
        // ==================================================

        else {

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                items(

                    items =
                        notifications,

                    key = {
                        it.id
                    }

                ) { notification ->

                    AdminNotificationItem(

                        notification =
                            notification,

                        onClick = {

                            // ==========================================
                            // TANDAI SUDAH DIBACA
                            // ==========================================

                            if (!notification.isRead) {

                                notificationRepository.markAsRead(

                                    notificationId =
                                        notification.id
                                )
                            }

                            // ==========================================
                            // ROUTING NOTIFIKASI
                            // ==========================================

                            when {

                                // --------------------------------------
                                // PENGAJUAN BARU
                                // --------------------------------------

                                notification.type.equals(
                                    "PENGAJUAN_BARU",
                                    ignoreCase = true
                                ) -> {

                                    onApprovalClick(
                                        notification.relatedId
                                    )
                                }

                                // --------------------------------------
                                // PENGAJUAN APPROVAL
                                // --------------------------------------

                                notification.type.equals(
                                    "PENGAJUAN_APPROVAL",
                                    ignoreCase = true
                                ) -> {

                                    onApprovalClick(
                                        notification.relatedId
                                    )
                                }

                                // --------------------------------------
                                // ABSENSI STAFF
                                // --------------------------------------

                                notification.type.equals(
                                    "ABSENSI",
                                    ignoreCase = true
                                ) -> {

                                    onRekapClick()
                                }

                                // --------------------------------------
                                // CHAT STAFF
                                // --------------------------------------

                                notification.type.equals(
                                    "CHAT",
                                    ignoreCase = true
                                ) -> {

                                    onChatClick()
                                }
                            }
                        }
                    )
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


// ==========================================================
// NOTIFICATION ITEM
// ==========================================================

@Composable
private fun AdminNotificationItem(

    notification:
    Notification,

    onClick:
        () -> Unit

) {

    val (
        icon,
        iconBackground,
        iconColor
    ) =
        getAdminNotificationStyle(
            notification.type
        )

    val backgroundColor =
        if (notification.isRead) {
            Color.White
        } else {
            Color(0xFFF1F8F3)
        }

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
                    backgroundColor
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (notification.isRead) {
                        2.dp
                    } else {
                        4.dp
                    }
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
                        notification.title,

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

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text =
                            notification.title,

                        modifier =
                            Modifier.weight(1f),

                        fontSize =
                            15.sp,

                        fontWeight =
                            if (notification.isRead) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Bold
                            },

                        color =
                            TextDark
                    )

                    if (!notification.isRead) {

                        Icon(

                            imageVector =
                                Icons.Default.Notifications,

                            contentDescription =
                                "Belum dibaca",

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(
                                    15.dp
                                )
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.size(4.dp)
                )

                Text(

                    text =
                        notification.message,

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Text(

                    text =
                        formatNotificationTime(
                            notification.timestamp
                        ),

                    fontSize =
                        10.sp,

                    color =
                        TextGray
                )
            }
        }
    }
}


// ==========================================================
// STYLE NOTIFIKASI
// ==========================================================

private fun getAdminNotificationStyle(

    type:
    String

): Triple<ImageVector, Color, Color> {

    return when {

        type.equals(
            "PENGAJUAN_BARU",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.PendingActions,

                Color(0xFFFFF3E0),

                Color(0xFFD97706)
            )
        }

        type.equals(
            "PENGAJUAN_APPROVAL",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.PendingActions,

                Color(0xFFFFF3E0),

                Color(0xFFD97706)
            )
        }

        type.equals(
            "PENGAJUAN_DISETUJUI",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.CheckCircle,

                Color(0xFFE8F5E9),

                PrimaryGreen
            )
        }

        type.equals(
            "PENGAJUAN_DISETUJUI_H1",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.CheckCircle,

                Color(0xFFE8F5E9),

                PrimaryGreen
            )
        }

        type.equals(
            "PENGAJUAN_DITOLAK",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.Error,

                Color(0xFFFFEBEE),

                Color(0xFFC62828)
            )
        }

        type.equals(
            "ABSENSI",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.CheckCircle,

                Color(0xFFE8F5E9),

                PrimaryGreen
            )
        }

        type.contains(
            "CHAT",
            ignoreCase = true
        ) -> {

            Triple(

                Icons.Default.Info,

                Color(0xFFE8F5E9),

                PrimaryGreen
            )
        }

        else -> {

            Triple(

                Icons.Default.Info,

                Color(0xFFE8F5E9),

                PrimaryGreen
            )
        }
    }
}


// ==========================================================
// EMPTY / ERROR MESSAGE
// ==========================================================

@Composable
private fun AdminNotificationMessage(

    icon:
    ImageVector,

    title:
    String,

    description:
    String

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

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
                        vertical = 18.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(

                modifier =
                    Modifier
                        .size(46.dp)
                        .background(

                            color =
                                Color(0xFFE8F5E9),

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

            Column {

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


// ==========================================================
// FORMAT WAKTU
// ==========================================================

private fun formatNotificationTime(
    timestamp: Long
): String {

    if (timestamp <= 0L) {
        return ""
    }

    return try {

        SimpleDateFormat(
            "dd MMM yyyy • HH:mm",
            Locale("id", "ID")
        ).format(
            Date(timestamp)
        )

    } catch (
        exception: Exception
    ) {

        ""
    }
}