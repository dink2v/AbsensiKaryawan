package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.absensikaryawan.models.ChatMessage
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



@Composable
fun AdminChatDetailScreen(
    room: ChatRoom,
    onBack: () -> Unit
) {

    val repository =
        remember {
            AdminChatRepository()
        }

    val listState =
        rememberLazyListState()

    var messages by remember {
        mutableStateOf<List<ChatMessage>>(emptyList())
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // =====================================================
    // LISTEN CHAT
    // =====================================================

    DisposableEffect(room.staffId) {

        isLoading = true
        errorMessage = null

        val listener =
            repository.listenMessages(
                staffUid = room.staffId,

                onMessagesChanged = { newMessages ->

                    messages = newMessages
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

    // =====================================================
    // AUTO SCROLL KE PESAN TERBARU
    // =====================================================

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
    ) {

        // =================================================
        // HEADER
        // =================================================

        AdminChatDetailHeader(
            staffName = room.staffName,
            onBack = onBack
        )

        // =================================================
        // CHAT AREA
        // =================================================

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
        ) {

            when {

                isLoading -> {

                    CircularProgressIndicator(
                        color = PrimaryGreen,

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }

                errorMessage != null -> {

                    Column(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.Center
                                )
                                .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                errorMessage
                                    ?: "Gagal memuat chat.",

                            color =
                                TextDark,

                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        TextButton(
                            onClick = {

                                errorMessage = null
                                isLoading = true
                            }
                        ) {

                            Text(
                                text = "Coba lagi",
                                color = PrimaryGreen
                            )
                        }
                    }
                }

                messages.isEmpty() -> {

                    Column(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.Center
                                )
                                .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(72.dp)
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
                                    Modifier.size(36.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text =
                                "Belum ada pesan",

                            color =
                                TextDark,

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Mulai percakapan dengan Staff.",

                            color =
                                TextGray,

                            style =
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),

                        state =
                            listState,

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp),

                        contentPadding =
                            androidx.compose.foundation.layout.PaddingValues(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                    ) {

                        items(
                            items = messages,

                            key = {
                                it.id
                            }
                        ) { message ->

                            AdminChatMessageBubble(
                                message = message
                            )
                        }
                    }
                }
            }
        }

        // =================================================
        // INPUT MESSAGE
        // =================================================

        AdminChatInput(
            message = messageText,

            onMessageChange = {
                messageText = it
            },

            isSending = isSending,

            onSend = {

                val cleanMessage =
                    messageText.trim()

                if (
                    cleanMessage.isNotEmpty() &&
                    !isSending
                ) {

                    isSending = true

                    repository.sendMessage(
                        staffUid = room.staffId,

                        message = cleanMessage,

                        onSuccess = {

                            messageText = ""
                            isSending = false
                        },

                        onError = { exception ->

                            isSending = false

                            errorMessage =
                                exception.message
                                    ?: "Gagal mengirim pesan."
                        }
                    )
                }
            }
        )
    }
}


// =====================================================
// HEADER
// =====================================================

@Composable
private fun AdminChatDetailHeader(
    staffName: String,
    onBack: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(PrimaryGreen)
                .padding(
                    horizontal = 8.dp,
                    vertical = 10.dp
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

                tint = Color.White
            )
        }

        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),

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
                    Modifier.size(25.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    staffName.ifBlank {
                        "Staff"
                    },

                color =
                    Color.White,

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight =
                    FontWeight.Bold,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )

            Text(
                text = "Percakapan Staff",

                color =
                    Color.White.copy(
                        alpha = 0.85f
                    ),

                style =
                    MaterialTheme.typography.bodySmall
            )
        }
    }
}


// =====================================================
// MESSAGE BUBBLE
// =====================================================

@Composable
private fun AdminChatMessageBubble(
    message: ChatMessage
) {

    val isAdmin =
        message.senderType.equals(
            "ADMIN",
            ignoreCase = true
        )

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (isAdmin) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Column(
            horizontalAlignment =
                if (isAdmin) {
                    Alignment.End
                } else {
                    Alignment.Start
                }
        ) {

            if (!isAdmin) {

                Text(
                    text =
                        message.senderName.ifBlank {
                            "Staff"
                        },

                    color =
                        TextGray,

                    style =
                        MaterialTheme.typography.labelSmall,

                    modifier =
                        Modifier.padding(
                            start = 4.dp,
                            bottom = 3.dp
                        )
                )
            }

            Box(
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart =
                                    if (isAdmin) {
                                        16.dp
                                    } else {
                                        4.dp
                                    },
                                bottomEnd =
                                    if (isAdmin) {
                                        4.dp
                                    } else {
                                        16.dp
                                    }
                            )
                        )
                        .background(
                            if (isAdmin) {
                                PrimaryGreen
                            } else {
                                Color.White
                            }
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        )
            ) {

                Column {

                    Text(
                        text = message.message,

                        color =
                            if (isAdmin) {
                                Color.White
                            } else {
                                TextDark
                            },

                        style =
                            MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            formatMessageTime(
                                message.timestamp
                            ),

                        color =
                            if (isAdmin) {
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                            } else {
                                TextGray
                            },

                        style =
                            MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}


// =====================================================
// INPUT
// =====================================================

@Composable
private fun AdminChatInput(
    message: String,
    onMessageChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),

        verticalAlignment =
            Alignment.Bottom
    ) {

        OutlinedTextField(
            value = message,

            onValueChange = onMessageChange,

            modifier =
                Modifier.weight(1f),

            placeholder = {
                Text(
                    text = "Tulis pesan..."
                )
            },

            maxLines = 4,

            shape =
                RoundedCornerShape(24.dp),

            enabled = !isSending,

            singleLine = false
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        IconButton(
            onClick = onSend,

            enabled =
                message.trim().isNotEmpty() &&
                        !isSending,

            modifier =
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (
                            message.trim().isNotEmpty() &&
                            !isSending
                        ) {
                            PrimaryGreen
                        } else {
                            TextGray.copy(
                                alpha = 0.3f
                            )
                        }
                    )
        ) {

            if (isSending) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(22.dp),

                    color = Color.White,

                    strokeWidth = 2.dp
                )

            } else {

                Icon(
                    imageVector =
                        Icons.Default.Send,

                    contentDescription =
                        "Kirim",

                    tint = Color.White
                )
            }
        }
    }
}


// =====================================================
// FORMAT WAKTU
// =====================================================

private fun formatMessageTime(
    timestamp: Long
): String {

    if (timestamp <= 0L) {
        return ""
    }

    return SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )
}