package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.UserProfile
import com.example.absensikaryawan.data.UserRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {

    val userRepository = remember {
        UserRepository()
    }

    var profile by remember {
        mutableStateOf<UserProfile?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    // ==========================================
    // AMBIL PROFILE USER DARI FIREBASE
    // ==========================================

    LaunchedEffect(Unit) {

        try {

            val currentUser =
                FirebaseAuth
                    .getInstance()
                    .currentUser

            if (currentUser == null) {

                errorMessage =
                    "User belum login."

                isLoading = false

                return@LaunchedEffect
            }

            val email =
                currentUser.email

            if (email.isNullOrEmpty()) {

                errorMessage =
                    "Email user tidak ditemukan."

                isLoading = false

                return@LaunchedEffect
            }

            // ==========================================
            // CARI DATA USER BERDASARKAN EMAIL
            // ==========================================

            val result =
                userRepository.getUserByEmail(email)

            if (result == null) {

                errorMessage =
                    "Data profile tidak ditemukan."

            } else {

                profile = result
            }

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil profile."

        } finally {

            isLoading = false
        }
    }

    // ==========================================
    // ROOT
    // ==========================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
        ) {

            // ==========================================
            // HEADER PROFILE
            // ==========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        onBack()
                    }
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

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text = "Profile",

                    fontSize = 26.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==========================================
            // LOADING
            // ==========================================

            if (isLoading) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Memuat profile...",

                        color =
                            TextGray,

                        fontSize =
                            14.sp
                    )
                }

            }

            // ==========================================
            // ERROR
            // ==========================================

            else if (errorMessage.isNotEmpty()) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {

                    Text(
                        text =
                            errorMessage,

                        modifier =
                            Modifier.padding(20.dp),

                        color =
                            Color.Red
                    )
                }

            }

            // ==========================================
            // PROFILE BERHASIL
            // ==========================================

            else {

                val user = profile

                if (user != null) {

                    // ==========================================
                    // AVATAR
                    // ==========================================

                    Column(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Card(
                            modifier =
                                Modifier.size(90.dp),

                            shape =
                                CircleShape,

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        DarkGreen
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier.fillMaxSize(),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.Center
                            ) {

                                Text(
                                    text =
                                        getInitials(
                                            user.nama
                                        ),

                                    fontSize =
                                        26.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        // ==========================================
                        // NAMA FIREBASE
                        // ==========================================

                        Text(
                            text =
                                user.nama.ifEmpty {
                                    "Nama belum diisi"
                                },

                            fontSize =
                                22.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        // ==========================================
                        // JABATAN FIREBASE
                        // ==========================================

                        Text(
                            text =
                                user.jabatan.ifEmpty {
                                    "Jabatan belum diisi"
                                },

                            fontSize =
                                14.sp,

                            color =
                                PrimaryGreen
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(25.dp)
                    )

                    // ==========================================
                    // NAMA
                    // ==========================================

                    ProfileInfoCard(
                        icon =
                            Icons.Default.Person,

                        title =
                            "Nama",

                        value =
                            user.nama.ifEmpty {
                                "-"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // ==========================================
                    // DIVISI
                    // ==========================================

                    ProfileInfoCard(
                        icon =
                            Icons.Default.Business,

                        title =
                            "Divisi",

                        value =
                            user.divisi.ifEmpty {
                                "-"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // ==========================================
                    // JABATAN
                    // ==========================================

                    ProfileInfoCard(
                        icon =
                            Icons.Default.Work,

                        title =
                            "Jabatan",

                        value =
                            user.jabatan.ifEmpty {
                                "-"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // ==========================================
                    // USERNAME TELEGRAM
                    // ==========================================

                    ProfileInfoCard(
                        icon =
                            Icons.Default.Send,

                        title =
                            "Username Telegram",

                        value =
                            user.usernameTele.ifEmpty {
                                "-"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // ==========================================
                    // STATUS ADMIN / STAFF
                    // ==========================================

                    ProfileInfoCard(
                        icon =
                            Icons.Default.Badge,

                        title =
                            "Status",

                        value =
                            if (user.isAdmin) {
                                "Administrator"
                            } else {
                                "Staff"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )
                }
            }
        }
    }
}


// ======================================================
// INISIAL NAMA
// ======================================================

private fun getInitials(
    nama: String
): String {

    val words =
        nama
            .trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }

    return when {

        words.size >= 2 -> {

            "${words[0].first()}${words[1].first()}"
                .uppercase()
        }

        words.size == 1 -> {

            words[0]
                .take(2)
                .uppercase()
        }

        else -> {

            "PF"
        }
    }
}


// ======================================================
// PROFILE INFO CARD
// ======================================================

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    title: String,
    value: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

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
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Card(
                shape =
                    RoundedCornerShape(12.dp),

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
                            .size(22.dp)
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
                        12.sp,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        value,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }
        }
    }
}