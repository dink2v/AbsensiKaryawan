package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.UserRepository

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {

    val userRepository = remember {
        UserRepository()
    }

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var divisi by remember { mutableStateOf("") }
    var jabatan by remember { mutableStateOf("") }
    var usernameTele by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    // ======================================================
    // AMBIL PROFILE BERDASARKAN EMAIL USER LOGIN
    // ======================================================

    LaunchedEffect(Unit) {

        try {

            val currentUser =
                userRepository.getCurrentUser()

            if (currentUser.isFailure) {

                errorMessage =
                    currentUser.exceptionOrNull()?.message
                        ?: "User belum login"

                isLoading = false

                return@LaunchedEffect
            }

            val firebaseUser =
                currentUser.getOrNull()

            val userEmail =
                firebaseUser?.email

            if (userEmail.isNullOrBlank()) {

                errorMessage =
                    "Email user tidak ditemukan"

                isLoading = false

                return@LaunchedEffect
            }

            val profile =
                userRepository.getUserByEmail(
                    email = userEmail
                )

            if (profile != null) {

                nama =
                    profile.nama

                email =
                    profile.email

                divisi =
                    profile.divisi

                jabatan =
                    profile.jabatan

                usernameTele =
                    profile.usernameTele

                isAdmin =
                    profile.isAdmin

                errorMessage = ""

            } else {

                errorMessage =
                    "Data profile tidak ditemukan di Firestore"
            }

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil profile"

        } finally {

            isLoading = false
        }
    }

    // ======================================================
    // INITIAL
    // ======================================================

    val initial =
        remember(nama) {

            nama
                .trim()
                .split(" ")
                .filter {
                    it.isNotEmpty()
                }
                .take(2)
                .joinToString("") {
                    it.first().uppercase()
                }
                .ifEmpty {
                    "PF"
                }
        }

    // ======================================================
    // ROOT
    // ======================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        end = 20.dp,
                        top = 18.dp,
                        bottom = 12.dp
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
                            TextDark
                    )
                }

                Text(
                    text = "Profile",

                    modifier =
                        Modifier.weight(1f),

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }

            // ==================================================
            // CONTENT
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 20.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier =
                        Modifier.size(20.dp)
                )

                // ==================================================
                // AVATAR
                // ==================================================

                Card(
                    modifier =
                        Modifier.size(96.dp),

                    shape =
                        CircleShape,

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkGreen
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                ) {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(30.dp),

                                color =
                                    Color.White,

                                strokeWidth =
                                    3.dp
                            )

                        } else {

                            Text(
                                text = initial,

                                fontSize = 26.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.size(16.dp)
                )

                // ==================================================
                // NAMA
                // ==================================================

                Text(
                    text =
                        if (isLoading)
                            "Memuat profile..."
                        else if (nama.isNotBlank())
                            nama
                        else
                            "Nama belum tersedia",

                    fontSize = 21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )

                Spacer(
                    modifier =
                        Modifier.size(5.dp)
                )

                // ==================================================
                // JABATAN
                // ==================================================

                Text(
                    text =
                        if (jabatan.isNotBlank())
                            jabatan
                        else
                            "-",

                    fontSize = 13.sp,

                    color =
                        PrimaryGreen,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.size(24.dp)
                )

                // ==================================================
                // ERROR
                // ==================================================

                if (errorMessage.isNotEmpty()) {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFFFEBEE)
                            )
                    ) {

                        Text(
                            text =
                                errorMessage,

                            modifier =
                                Modifier.padding(16.dp),

                            fontSize =
                                13.sp,

                            color =
                                Color(0xFFC62828)
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.size(16.dp)
                    )
                }

                // ==================================================
                // INFORMASI PROFILE
                // ==================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Informasi Profile",

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        ProfileInfoRow(
                            icon =
                                Icons.Default.Person,

                            title =
                                "Nama Lengkap",

                            value =
                                nama.ifBlank { "-" }
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        ProfileInfoRow(
                            icon =
                                Icons.Default.Business,

                            title =
                                "Divisi",

                            value =
                                divisi.ifBlank { "-" }
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        ProfileInfoRow(
                            icon =
                                Icons.Default.Badge,

                            title =
                                "Jabatan",

                            value =
                                jabatan.ifBlank { "-" }
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        ProfileInfoRow(
                            icon =
                                Icons.Default.Phone,

                            title =
                                "Username",

                            value =
                                usernameTele.ifBlank { "-" }
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        ProfileInfoRow(
                            icon =
                                Icons.Default.Person,

                            title =
                                "Email",

                            value =
                                email.ifBlank { "-" }
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.size(16.dp)
                )

                // ==================================================
                // STATUS AKUN
                // ==================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                SoftGreen
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        PrimaryGreen
                                    )
                        )

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
                                    "Status Akun",

                                fontSize =
                                    11.sp,

                                color =
                                    TextGray
                            )

                            Text(
                                text =
                                    if (isAdmin)
                                        "Administrator"
                                    else
                                        "Staff",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================================
// PROFILE INFO ROW
// ==========================================================

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Card(
            shape =
                RoundedCornerShape(11.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        SoftGreen
                )
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier
                        .padding(10.dp)
                        .size(20.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    title,

                fontSize =
                    11.sp,

                color =
                    TextGray
            )

            Spacer(
                modifier =
                    Modifier.size(2.dp)
            )

            Text(
                text =
                    value,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TextDark
            )
        }
    }
}