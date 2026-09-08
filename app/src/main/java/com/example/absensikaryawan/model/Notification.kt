package com.example.absensikaryawan.models

data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val relatedId: String = ""
)