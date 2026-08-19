package com.example.absensikaryawan.model

data class Absensi(
    val tanggal: String,
    val jamMasuk: String,
    val status: String = "Hadir"
)