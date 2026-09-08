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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.absensikaryawan.repository.AdminChatRepository
import com.example.absensikaryawan.repository.ChatRoom
import com.example.absensikaryawan.ui.theme.Background
import com.example.absensikaryawan.ui.theme.PrimaryGreen
import com.example.absensikaryawan.ui.theme.SoftGreen
import com.example.absensikaryawan.ui.theme.TextDark
import com.example.absensikaryawan.ui.theme.TextGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@androidx.compose.runtime.Composable
fun AdminChatListScreen(
    onBack: () -> Unit,
    onStaffClick: (ChatRoom) -> Unit
) {

    val repository =
        remember {
            AdminChatRepository()
        }

    var chatRooms by remember {
        mutableStateOf<List<ChatRoom>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(Unit) {

        val listener =
            repository.listenChatRooms(
                onRoomsChanged = { rooms ->

                    chatRooms = rooms

                    isLoading = false

                    errorMessage = null
                },

                onError = { exception ->

                    isLoading = false

                    errorMessage =
                        exception.message
                            ?: "Gagal memuat chat."
                }
            )

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
    ) {

        // =========================
        // HEADER
        // =========================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(PrimaryGreen)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Kembali",

                    tint =
                        androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Icon(
                imageVector =
                    Icons.Default.Chat,

                contentDescription =
                    null,

                tint =
                    androidx.compose.ui.graphics.Color.White,

                modifier =
                    Modifier.size(28.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )

            Column {

                Text(
                    text = "Chat",

                    color =
                        androidx.compose.ui.graphics.Color.White,

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = "Pesan dari Staff",

                    color =
                        androidx.compose.ui.graphics.Color.White.copy(
                            alpha = 0.85f
                        ),

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }
        }

        // =========================
        // CONTENT
        // =========================

        when {

            isLoading -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = PrimaryGreen
                    )
                }
            }

            errorMessage != null -> {

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
                                Icons.Default.Chat,

                            contentDescription =
                                null,

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(52.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                errorMessage
                                    ?: "Gagal memuat chat.",

                            color =
                                TextDark,

                            style =
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            chatRooms.isEmpty() -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(SoftGreen),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Chat,

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen,

                                modifier =
                                    Modifier.size(40.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Text(
                            text =
                                "Belum ada chat",

                            color =
                                TextDark,

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Belum ada Staff yang mengirim pesan.",

                            color =
                                TextGray,

                            style =
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            else -> {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp),

                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        )
                ) {

                    items(
                        items = chatRooms,

                        key = {
                            it.staffId
                        }
                    ) { room ->

                        AdminChatRoomItem(
                            room = room,

                            onClick = {
                                onStaffClick(room)
                            }
                        )
                    }
                }
            }
        }
    }
}


// =====================================================
// CHAT ROOM ITEM
// =====================================================

@androidx.compose.runtime.Composable
private fun AdminChatRoomItem(
    room: ChatRoom,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .background(
                    androidx.compose.ui.graphics.Color.White
                )
                .clickable {
                    onClick()
                }
                .padding(16.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // =========================
        // AVATAR
        // =========================

        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SoftGreen),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.Person,

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
                Modifier.width(14.dp)
        )

        // =========================
        // CHAT INFO
        // =========================

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
                        room.staffName.ifBlank {
                            "Staff"
                        },

                    color =
                        TextDark,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis,

                    modifier =
                        Modifier.weight(1f)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        formatChatTime(
                            room.updatedAt
                        ),

                    color =
                        TextGray,

                    style =
                        MaterialTheme.typography.labelSmall
                )
            }

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    room.lastMessage.ifBlank {
                        "Belum ada pesan"
                    },

                color =
                    TextGray,

                style =
                    MaterialTheme.typography.bodyMedium,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        // =========================
        // CHEVRON
        // =========================

        Icon(
            imageVector =
                Icons.Default.ChevronRight,

            contentDescription =
                "Buka chat",

            tint =
                TextGray,

            modifier =
                Modifier.size(24.dp)
        )
    }
}


// =====================================================
// FORMAT WAKTU
// =====================================================

private fun formatChatTime(
    timestamp: Long
): String {

    if (timestamp <= 0L) {
        return ""
    }

    val date =
        Date(timestamp)

    val now =
        Date()

    val sameDay =
        SimpleDateFormat(
            "yyyyMMdd",
            Locale.getDefault()
        ).format(date) ==
                SimpleDateFormat(
                    "yyyyMMdd",
                    Locale.getDefault()
                ).format(now)

    return if (sameDay) {

        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(date)

    } else {

        SimpleDateFormat(
            "dd/MM",
            Locale.getDefault()
        ).format(date)
    }
}