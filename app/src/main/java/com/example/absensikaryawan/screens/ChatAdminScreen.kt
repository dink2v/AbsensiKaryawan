package com.example.absensikaryawan.screens

import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.models.ChatMessage
import com.example.absensikaryawan.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatAdminScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val repository = remember {
        ChatRepository()
    }

    val currentUser = auth.currentUser
    val uid = currentUser?.uid

    var messages by remember {
        mutableStateOf<List<ChatMessage>>(emptyList())
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    // ============================================================
    // FIRESTORE REALTIME LISTENER
    // ============================================================

    DisposableEffect(uid) {

        var listener: ListenerRegistration? = null

        if (uid != null) {

            listener = repository.listenMessages(
                uid = uid,

                onMessagesChanged = {
                    messages = it
                },

                onError = { exception ->

                    Toast.makeText(
                        context,
                        "Gagal memuat chat: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        onDispose {
            listener?.remove()
        }
    }

    // ============================================================
    // AUTO SCROLL KE PESAN TERBARU
    // ============================================================

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Scaffold(
        containerColor = Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {

            // ========================================================
            // HEADER
            // ========================================================

            ChatHeader(
                onBack = onBack
            )

            Divider(
                color = TextGray.copy(alpha = 0.10f),
                thickness = 1.dp
            )

            // ========================================================
            // CHAT AREA
            // ========================================================

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                if (messages.isEmpty()) {

                    EmptyChatState()

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp),
                        contentPadding =
                            PaddingValues(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            )
                    ) {

                        item {

                            DateLabel(
                                text = "Percakapan"
                            )
                        }

                        items(
                            items = messages,
                            key = {
                                it.id
                            }
                        ) { message ->

                            ChatBubble(
                                message = message
                            )
                        }
                    }
                }
            }

            // ========================================================
            // INPUT CHAT
            // ========================================================

            ChatInput(
                value = messageText,

                enabled = !isSending,

                onValueChange = {
                    messageText = it
                },

                onSend = {

                    if (messageText.trim().isEmpty()) {
                        return@ChatInput
                    }

                    if (isSending) {
                        return@ChatInput
                    }

                    isSending = true

                    repository.sendMessage(

                        message = messageText.trim(),

                        onSuccess = {

                            messageText = ""

                            isSending = false
                        },

                        onError = { exception ->

                            isSending = false

                            Toast.makeText(
                                context,
                                exception.message
                                    ?: "Pesan gagal dikirim.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            )
        }
    }
}


// =====================================================================
// HEADER
// =====================================================================

@Composable
private fun ChatHeader(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier.size(44.dp)
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

        // =========================================================
        // AVATAR ADMIN
        // =========================================================

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(SoftGreen),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = "ADMIN",
                tint = PrimaryGreen,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Hubungi ADMIN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = "Siap membantu",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }
        }
    }
}


// =====================================================================
// EMPTY STATE
// =====================================================================

@Composable
private fun EmptyChatState() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(SoftGreen),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Mulai Percakapan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Sampaikan pertanyaan atau kendala kamu kepada ADMIN. Kami siap membantu.",
            fontSize = 12.sp,
            color = TextGray,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftGreen
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {

            Text(
                text = "Silakan kirim pesan pertama kamu 👋",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryGreen,
                textAlign = TextAlign.Center
            )
        }
    }
}


// =====================================================================
// DATE LABEL
// =====================================================================

@Composable
private fun DateLabel(
    text: String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftGreen
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {

            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 6.dp
                ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryGreen
            )
        }
    }
}


// =====================================================================
// CHAT BUBBLE
// =====================================================================

@Composable
private fun ChatBubble(
    message: ChatMessage
) {

    val fromAdmin =
        message.senderType == "ADMIN"

    val time = remember(message.timestamp) {

        if (message.timestamp > 0) {

            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(
                Date(message.timestamp)
            )

        } else {
            ""
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (fromAdmin) {
                Arrangement.Start
            } else {
                Arrangement.End
            }
    ) {

        Column(
            horizontalAlignment =
                if (fromAdmin) {
                    Alignment.Start
                } else {
                    Alignment.End
                },
            modifier = Modifier.fillMaxWidth(
                0.82f
            )
        ) {

            // =========================================================
            // LABEL ADMIN
            // =========================================================

            if (fromAdmin) {

                Text(
                    text = "ADMIN",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(
                        start = 5.dp,
                        bottom = 4.dp
                    )
                )
            }

            // =========================================================
            // BUBBLE
            // =========================================================

            Card(
                shape =
                    if (fromAdmin) {

                        RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )

                    } else {

                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 5.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    },

                colors = CardDefaults.cardColors(
                    containerColor =
                        if (fromAdmin) {
                            Color.White
                        } else {
                            PrimaryGreen
                        }
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
                ) {

                    Text(
                        text = message.message,
                        fontSize = 13.sp,
                        color =
                            if (fromAdmin) {
                                TextDark
                            } else {
                                Color.White
                            },
                        lineHeight = 19.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = time,
                        fontSize = 9.sp,
                        color =
                            if (fromAdmin) {
                                TextGray
                            } else {
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                            }
                    )
                }
            }
        }
    }
}


// =====================================================================
// INPUT CHAT
// =====================================================================

@Composable
private fun ChatInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {

    val canSend =
        value.trim().isNotEmpty() &&
                enabled

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        Divider(
            color = TextGray.copy(alpha = 0.10f),
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 9.dp
                ),
            verticalAlignment = Alignment.Bottom
        ) {

            OutlinedTextField(
                value = value,

                onValueChange = onValueChange,

                enabled = enabled,

                modifier = Modifier.weight(1f),

                placeholder = {

                    Text(
                        text = "Tulis pesan...",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                },

                maxLines = 4,

                shape = RoundedCornerShape(22.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            PrimaryGreen,

                        unfocusedBorderColor =
                            TextGray.copy(
                                alpha = 0.20f
                            ),

                        disabledBorderColor =
                            TextGray.copy(
                                alpha = 0.12f
                            ),

                        focusedContainerColor =
                            Background,

                        unfocusedContainerColor =
                            Background,

                        disabledContainerColor =
                            Background,

                        cursorColor =
                            PrimaryGreen
                    )
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            IconButton(
                onClick = onSend,

                enabled = canSend,

                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) {
                            PrimaryGreen
                        } else {
                            TextGray.copy(
                                alpha = 0.18f
                            )
                        }
                    )
            ) {

                Icon(
                    imageVector = Icons.Default.Send,

                    contentDescription =
                        "Kirim pesan",

                    tint =
                        if (canSend) {
                            Color.White
                        } else {
                            TextGray
                        },

                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}