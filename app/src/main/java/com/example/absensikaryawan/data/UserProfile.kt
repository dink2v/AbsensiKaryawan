package com.example.absensikaryawan.data

data class UserProfile(
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val divisi: String = "",
    val jabatan: String = "",
    val usernameTele: String = "",
    val isAdmin: Boolean = false
)