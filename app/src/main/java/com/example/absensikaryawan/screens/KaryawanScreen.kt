package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Badge

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


// ==========================================================
// MODEL KARYAWAN
// ==========================================================

data class DataKaryawan(

    val id: String,

    val nama: String,

    val email: String,

    val jabatan: String,

    val divisi: String,

    val usernameTele: String,

    val isAdmin: Boolean
)


// ==========================================================
// KARYAWAN SCREEN
// ==========================================================

@Composable
fun KaryawanScreen(

    onBack: () -> Unit

) {

    // ======================================================
    // FIRESTORE
    // ======================================================

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }


    // ======================================================
    // STATE
    // ======================================================

    var daftarKaryawan by remember {

        mutableStateOf(
            emptyList<DataKaryawan>()
        )
    }


    var loading by remember {

        mutableStateOf(true)
    }


    var errorMessage by remember {

        mutableStateOf("")
    }


    var searchQuery by remember {

        mutableStateOf("")
    }


    // ======================================================
    // LOAD DATA
    // ======================================================

    LaunchedEffect(Unit) {

        try {

            loading = true
            errorMessage = ""

            val snapshot =
                db.collection("users")
                    .get()
                    .await()


            daftarKaryawan =
                snapshot.documents.map { document ->

                    DataKaryawan(

                        id =
                            document.id,

                        nama =
                            document.getString("nama")
                                ?: "",

                        email =
                            document.getString("email")
                                ?: "",

                        jabatan =
                            document.getString("jabatan")
                                ?: "",

                        divisi =
                            document.getString("divisi")
                                ?: "",

                        usernameTele =
                            document.getString("usernameTele")
                                ?: "",

                        isAdmin =
                            document.getBoolean("isAdmin")
                                ?: false
                    )
                }
                    .sortedBy {
                        it.nama.lowercase()
                    }


            loading = false

        } catch (e: Exception) {

            loading = false

            errorMessage =
                e.message
                    ?: "Gagal mengambil data karyawan"
        }
    }


    // ======================================================
    // FILTER SEARCH
    // ======================================================

    val hasilPencarian =
        remember(
            daftarKaryawan,
            searchQuery
        ) {

            if (
                searchQuery.isBlank()
            ) {

                daftarKaryawan

            } else {

                val query =
                    searchQuery
                        .trim()
                        .lowercase()


                daftarKaryawan.filter { karyawan ->

                    karyawan.nama
                        .lowercase()
                        .contains(query)

                            ||

                            karyawan.email
                                .lowercase()
                                .contains(query)
                }
            }
        }


    // ======================================================
    // UI
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
                            horizontal = 16.dp,
                            vertical = 12.dp
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
                            PrimaryGreen
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                Icon(

                    imageVector =
                        Icons.Default.Group,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(25.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                Column(

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            "Data Karyawan",

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            "${daftarKaryawan.size} karyawan terdaftar",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }
            }


            // ==================================================
            // SEARCH
            // ==================================================

            OutlinedTextField(

                value =
                    searchQuery,

                onValueChange = {

                    searchQuery =
                        it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(14.dp),

                placeholder = {

                    Text(

                        text =
                            "Cari nama atau email...",

                        fontSize =
                            13.sp
                    )
                },

                leadingIcon = {

                    Icon(

                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Cari",

                        tint =
                            PrimaryGreen
                    )
                },

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            PrimaryGreen,

                        unfocusedBorderColor =
                            Color(0xFFD1D5DB),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // ==================================================
            // CONTENT
            // ==================================================

            when {

                // ==================================================
                // LOADING
                // ==================================================

                loading -> {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 40.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(

                            color =
                                PrimaryGreen
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(

                            text =
                                "Memuat data karyawan...",

                            fontSize =
                                13.sp,

                            color =
                                TextGray
                        )
                    }
                }


                // ==================================================
                // ERROR
                // ==================================================

                errorMessage.isNotBlank() -> {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 40.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Group,

                            contentDescription =
                                null,

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier.size(40.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(

                            text =
                                "Gagal memuat data",

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )


                        Text(

                            text =
                                errorMessage,

                            fontSize =
                                12.sp,

                            color =
                                Color(0xFFB91C1C),

                            textAlign =
                                TextAlign.Center
                        )
                    }
                }


                // ==================================================
                // SEARCH TIDAK DITEMUKAN
                // ==================================================

                hasilPencarian.isEmpty() -> {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 40.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Search,

                            contentDescription =
                                null,

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(42.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        Text(

                            text =
                                if (
                                    searchQuery.isBlank()
                                ) {
                                    "Belum ada karyawan"
                                } else {
                                    "Karyawan tidak ditemukan"
                                },

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )


                        if (
                            searchQuery.isNotBlank()
                        ) {

                            Text(

                                text =
                                    "Coba gunakan nama atau email lain.",

                                fontSize =
                                    12.sp,

                                color =
                                    TextGray,

                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }


                // ==================================================
                // LIST
                // ==================================================

                else -> {

                    LazyColumn(

                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            ),

                        contentPadding =
                            androidx.compose.foundation.layout
                                .PaddingValues(
                                    start = 20.dp,
                                    top = 0.dp,
                                    end = 20.dp,
                                    bottom = 24.dp
                                )
                    ) {

                        items(
                            items =
                                hasilPencarian,

                            key = {
                                it.id
                            }

                        ) { karyawan ->

                            KaryawanCard(
                                karyawan =
                                    karyawan
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================
// KARYAWAN CARD
// ==========================================================

@Composable
private fun KaryawanCard(

    karyawan: DataKaryawan

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(17.dp),

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

            // ==================================================
            // AVATAR
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .size(48.dp)
                        .background(

                            color =
                                Color(0xFFE6EEE9),

                            shape =
                                RoundedCornerShape(
                                    50.dp
                                )
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        getKaryawanInitials(
                            karyawan.nama
                        ),

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        PrimaryGreen
                )
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            // ==================================================
            // INFO
            // ==================================================

            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        karyawan.nama.ifBlank {
                            "Tanpa Nama"
                        },

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )


                if (
                    karyawan.email.isNotBlank()
                ) {

                    Text(

                        text =
                            karyawan.email,

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    if (
                        karyawan.jabatan.isNotBlank()
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Badge,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(14.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )


                        Text(

                            text =
                                karyawan.jabatan,

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }


                    if (
                        karyawan.divisi.isNotBlank()
                    ) {

                        Text(

                            text =
                                if (
                                    karyawan.jabatan.isNotBlank()
                                ) {
                                    " • ${karyawan.divisi}"
                                } else {
                                    karyawan.divisi
                                },

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }
                }
            }


            // ==================================================
            // STATUS
            // ==================================================

            Column(

                horizontalAlignment =
                    Alignment.End
            ) {

                Icon(

                    imageVector =
                        if (
                            karyawan.isAdmin
                        ) {
                            Icons.Default.Badge
                        } else {
                            Icons.Default.Person
                        },

                    contentDescription =
                        null,

                    tint =
                        if (
                            karyawan.isAdmin
                        ) {
                            Color(0xFF7C3AED)
                        } else {
                            PrimaryGreen
                        },

                    modifier =
                        Modifier.size(19.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )


                Text(

                    text =
                        if (
                            karyawan.isAdmin
                        ) {
                            "Admin"
                        } else {
                            "Staff"
                        },

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        if (
                            karyawan.isAdmin
                        ) {
                            Color(0xFF7C3AED)
                        } else {
                            PrimaryGreen
                        }
                )
            }
        }
    }
}


// ==========================================================
// INITIAL
// ==========================================================

private fun getKaryawanInitials(

    nama: String

): String {

    val parts =
        nama.trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }


    return when {

        parts.isEmpty() ->
            "?"

        parts.size == 1 ->
            parts[0]
                .take(2)
                .uppercase()

        else ->
            "${parts.first().first()}${parts.last().first()}"
                .uppercase()
    }
}