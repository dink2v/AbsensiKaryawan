package com.example.absensikaryawan.models

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderType: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)