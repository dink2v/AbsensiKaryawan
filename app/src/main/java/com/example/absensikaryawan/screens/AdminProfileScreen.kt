package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Security

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.AdminProfile
import com.example.absensikaryawan.data.AdminRepository


// ==========================================================
// ADMIN PROFILE SCREEN
// ==========================================================

@Composable
fun AdminProfileScreen(
    onBack: () -> Unit
) {

    // ======================================================
    // REPOSITORY
    // ======================================================

    val adminRepository = remember {
        AdminRepository()
    }


    // ======================================================
    // STATE
    // ======================================================

    var adminProfile by remember {
        mutableStateOf<AdminProfile?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    // ======================================================
    // LOAD PROFILE ADMIN
    // ======================================================

    LaunchedEffect(Unit) {

        isLoading = true
        errorMessage = ""

        try {

            val result =
                adminRepository.getCurrentAdmin()

            if (result.isSuccess) {

                adminProfile =
                    result.getOrNull()

            } else {

                errorMessage =
                    result.exceptionOrNull()
                        ?.message
                        ?: "Data profile Admin tidak ditemukan."
            }

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil profile Admin."

        } finally {

            isLoading = false
        }
    }


    // ======================================================
    // DATA PROFILE
    // ======================================================

    val nama =
        adminProfile?.nama
            ?.ifBlank {
                "Admin"
            }
            ?: "Admin"


    val divisi =
        adminProfile?.divisi
            ?.ifBlank {
                "-"
            }
            ?: "-"


    val jabatan =
        adminProfile?.jabatan
            ?.ifBlank {
                "-"
            }
            ?: "-"


    // ======================================================
    // INITIAL
    // ======================================================

    val initial =
        remember(nama) {

            nama
                .trim()
                .split(" ")
                .filter {
                    it.isNotBlank()
                }
                .take(2)
                .joinToString("") {
                    it.first().uppercase()
                }
                .ifBlank {
                    "AD"
                }
        }


    // ======================================================
    // ROOT
    // ======================================================

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            Background
    ) {

        Column(

            modifier =
                Modifier.fillMaxSize()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(

                modifier =
                    Modifier
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

                    onClick =
                        onBack
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

                    text =
                        "Profile Admin",

                    modifier =
                        Modifier.weight(1f),

                    fontSize =
                        23.sp,

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

                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 20.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                // ==================================================
                // AVATAR
                // ==================================================

                Card(

                    modifier =
                        Modifier.size(100.dp),

                    shape =
                        CircleShape,

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkGreen
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                4.dp
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
                                    Modifier.size(32.dp),

                                color =
                                    Color.White,

                                strokeWidth =
                                    3.dp
                            )

                        } else {

                            Text(

                                text =
                                    initial,

                                fontSize =
                                    28.sp,

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
                        Modifier.height(16.dp)
                )


                // ==================================================
                // NAMA
                // ==================================================

                Text(

                    text =
                        if (isLoading) {
                            "Memuat profile..."
                        } else {
                            nama
                        },

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark,

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                // ==================================================
                // JABATAN
                // ==================================================

                Text(

                    text =
                        if (isLoading) {
                            "-"
                        } else {
                            jabatan
                        },

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        PrimaryGreen
                )


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // ==================================================
                // ERROR
                // ==================================================

                if (
                    errorMessage.isNotBlank()
                ) {

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
                                Color(0xFFC62828),

                            textAlign =
                                TextAlign.Center
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )
                }


                // ==================================================
                // INFORMASI ADMIN
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
                            defaultElevation =
                                2.dp
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
                                "Informasi Admin",

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        // ==================================================
                        // NAMA
                        // ==================================================

                        AdminProfileInfoRow(

                            icon =
                                Icons.Default.Person,

                            title =
                                "Nama Lengkap",

                            value =
                                nama
                        )


                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )


                        // ==================================================
                        // DIVISI
                        // ==================================================

                        AdminProfileInfoRow(

                            icon =
                                Icons.Default.Business,

                            title =
                                "Divisi",

                            value =
                                divisi
                        )


                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )


                        // ==================================================
                        // JABATAN
                        // ==================================================

                        AdminProfileInfoRow(

                            icon =
                                Icons.Default.Badge,

                            title =
                                "Jabatan",

                            value =
                                jabatan
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                // ==================================================
                // STATUS ADMIN
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

                        // ==================================================
                        // ICON SECURITY
                        // ==================================================

                        Box(

                            modifier =
                                Modifier
                                    .size(42.dp)
                                    .background(
                                        Color.White,
                                        RoundedCornerShape(13.dp)
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Security,

                                contentDescription =
                                    "Administrator",

                                tint =
                                    PrimaryGreen,

                                modifier =
                                    Modifier.size(23.dp)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )


                        // ==================================================
                        // STATUS TEXT
                        // ==================================================

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


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    "Administrator",

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
                            )
                        }


                        // ==================================================
                        // STATUS DOT
                        // ==================================================

                        Box(

                            modifier =
                                Modifier
                                    .size(10.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        PrimaryGreen
                                    )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )
            }
        }
    }
}


// ==========================================================
// ADMIN PROFILE INFO ROW
// ==========================================================

@Composable
private fun AdminProfileInfoRow(

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

        // ==================================================
        // ICON
        // ==================================================

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


        // ==================================================
        // TEXT
        // ==================================================

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
                    Modifier.height(2.dp)
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