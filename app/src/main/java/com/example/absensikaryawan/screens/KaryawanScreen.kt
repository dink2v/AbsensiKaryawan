package com.example.absensikaryawan.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch


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

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    var daftarKaryawan by remember {
        mutableStateOf(emptyList<DataKaryawan>())
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

    var refreshKey by remember {
        mutableStateOf(0)
    }

    var selectedKaryawan by remember {
        mutableStateOf<DataKaryawan?>(null)
    }

    var showTambahData by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // LOAD DATA FIRESTORE
    // ======================================================

    LaunchedEffect(refreshKey) {

        try {

            loading = true
            errorMessage = ""

            val snapshot = db
                .collection("users")
                .get()
                .await()

            daftarKaryawan = snapshot.documents
                .map { document ->

                    DataKaryawan(
                        id = document.id,

                        nama = document.getString("nama")
                            ?: "",

                        email = document.getString("email")
                            ?: "",

                        jabatan = document.getString("jabatan")
                            ?: "",

                        divisi = document.getString("divisi")
                            ?: "",

                        usernameTele = document
                            .getString("usernameTele")
                            ?: "",

                        isAdmin = document
                            .getBoolean("isAdmin")
                            ?: false
                    )
                }
                .sortedBy {
                    it.nama.lowercase()
                }

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil data karyawan."

        } finally {

            loading = false
        }
    }


    // ======================================================
    // DETAIL
    // ======================================================

    if (selectedKaryawan != null) {

        KaryawanDetailScreen(
            karyawan = selectedKaryawan!!,
            onBack = {
                selectedKaryawan = null
            }
        )

        return
    }


    // ======================================================
    // TAMBAH DATA
    // ======================================================

    if (showTambahData) {

        TambahKaryawanDialog(

            onDismiss = {
                showTambahData = false
            },

            onSuccess = {

                showTambahData = false
                refreshKey++
            }
        )
    }


    // ======================================================
    // STATISTIK
    // ======================================================

    val totalKaryawan =
        daftarKaryawan.size

    val totalAdmin =
        daftarKaryawan.count {
            it.isAdmin
        }

    val totalStaff =
        daftarKaryawan.count {
            !it.isAdmin
        }


    // ======================================================
    // SEARCH
    // ======================================================

    val hasilPencarian = remember(
        daftarKaryawan,
        searchQuery
    ) {

        if (searchQuery.isBlank()) {

            daftarKaryawan

        } else {

            val query =
                searchQuery.trim().lowercase()

            daftarKaryawan.filter { karyawan ->

                karyawan.nama
                    .lowercase()
                    .contains(query) ||

                        karyawan.email
                            .lowercase()
                            .contains(query) ||

                        karyawan.jabatan
                            .lowercase()
                            .contains(query) ||

                        karyawan.divisi
                            .lowercase()
                            .contains(query) ||

                        karyawan.usernameTele
                            .lowercase()
                            .contains(query)
            }
        }
    }


    // ======================================================
    // UI
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
                        horizontal = 16.dp,
                        vertical = 12.dp
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
                            PrimaryGreen
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Surface(
                    modifier =
                        Modifier.size(42.dp),

                    shape =
                        RoundedCornerShape(13.dp),

                    color =
                        Color(0xFFE6EEE9)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Group,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(23.dp)
                        )
                    }
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
                            "Data Karyawan",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "$totalKaryawan karyawan terdaftar",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }

                IconButton(
                    onClick = {
                        refreshKey++
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Refresh,

                        contentDescription =
                            "Refresh",

                        tint =
                            PrimaryGreen
                    )
                }
            }


            // ==================================================
            // BUTTON TAMBAH
            // ==================================================

            Button(
                onClick = {
                    showTambahData = true
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                shape =
                    RoundedCornerShape(13.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        "Tambah Data",

                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // SUMMARY
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {

                KaryawanSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Group,

                    title =
                        "Total",

                    value =
                        totalKaryawan.toString(),

                    iconColor =
                        PrimaryGreen
                )

                KaryawanSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Person,

                    title =
                        "Staff",

                    value =
                        totalStaff.toString(),

                    iconColor =
                        PrimaryGreen
                )

                KaryawanSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.AdminPanelSettings,

                    title =
                        "Admin",

                    value =
                        totalAdmin.toString(),

                    iconColor =
                        Color(0xFF7C3AED)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // SEARCH
            // ==================================================

            OutlinedTextField(

                value =
                    searchQuery,

                onValueChange = {
                    searchQuery = it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(14.dp),

                placeholder = {

                    Text(
                        text =
                            "Cari nama, email, jabatan...",

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

                trailingIcon = {

                    if (searchQuery.isNotBlank()) {

                        IconButton(
                            onClick = {
                                searchQuery = ""
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,

                                contentDescription =
                                    "Hapus pencarian"
                            )
                        }
                    }
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
                    Modifier.height(14.dp)
            )


            // ==================================================
            // CONTENT
            // ==================================================

            when {

                loading -> {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
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


                errorMessage.isNotBlank() -> {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(20.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Group,

                            contentDescription =
                                null,

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier.size(42.dp)
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
                                Modifier.height(6.dp)
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

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                refreshKey++
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Refresh,

                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )

                            Text(
                                text =
                                    "Coba Lagi"
                            )
                        }
                    }
                }


                hasilPencarian.isEmpty() -> {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(20.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(70.dp),

                            shape =
                                CircleShape,

                            color =
                                Color(0xFFE6EEE9)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Search,

                                    contentDescription =
                                        null,

                                    tint =
                                        TextGray,

                                    modifier =
                                        Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text =
                                if (searchQuery.isBlank()) {
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

                        Text(
                            text =
                                if (searchQuery.isBlank()) {
                                    "Tambahkan data karyawan menggunakan tombol di atas."
                                } else {
                                    "Coba gunakan kata kunci pencarian lain."
                                },

                            fontSize =
                                12.sp,

                            color =
                                TextGray,

                            textAlign =
                                TextAlign.Center
                        )
                    }
                }


                else -> {

                    LazyColumn(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp),

                        contentPadding =
                            PaddingValues(
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
                                    karyawan,

                                onClick = {

                                    selectedKaryawan =
                                        karyawan
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun KaryawanSummaryCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    iconColor: Color
) {

    Card(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(11.dp)
        ) {

            Surface(
                modifier =
                    Modifier.size(34.dp),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    Color(0xFFE6EEE9)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            icon,

                        contentDescription =
                            title,

                        tint =
                            iconColor,

                        modifier =
                            Modifier.size(18.dp)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    title,

                fontSize =
                    10.sp,

                color =
                    TextGray
            )

            Spacer(
                modifier =
                    Modifier.height(1.dp)
            )

            Text(
                text =
                    value,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// KARYAWAN CARD
// ==========================================================

@Composable
private fun KaryawanCard(
    karyawan: DataKaryawan,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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
                    .padding(15.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier =
                    Modifier.size(48.dp),

                shape =
                    CircleShape,

                color =
                    Color(0xFFE6EEE9)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            getKaryawanInitials(
                                karyawan.nama
                            ),

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            PrimaryGreen
                    )
                }
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
                        karyawan.nama.ifBlank {
                            "Tanpa Nama"
                        },

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark,

                    maxLines =
                        1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                if (karyawan.email.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            karyawan.email,

                        fontSize =
                            11.sp,

                        color =
                            TextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }


                if (
                    karyawan.jabatan.isNotBlank() ||
                    karyawan.divisi.isNotBlank()
                ) {

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
                                    Modifier.size(13.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(4.dp)
                            )

                            Text(
                                text =
                                    karyawan.jabatan,

                                fontSize =
                                    10.sp,

                                color =
                                    TextGray,

                                maxLines =
                                    1,

                                overflow =
                                    TextOverflow.Ellipsis
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
                                    10.sp,

                                color =
                                    TextGray,

                                maxLines =
                                    1,

                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Surface(
                shape =
                    RoundedCornerShape(20.dp),

                color =
                    if (karyawan.isAdmin) {
                        Color(0xFFF3E8FF)
                    } else {
                        Color(0xFFE8F5E9)
                    }
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            if (karyawan.isAdmin) {
                                Icons.Default.AdminPanelSettings
                            } else {
                                Icons.Default.Person
                            },

                        contentDescription =
                            null,

                        tint =
                            if (karyawan.isAdmin) {
                                Color(0xFF7C3AED)
                            } else {
                                PrimaryGreen
                            },

                        modifier =
                            Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text =
                            if (karyawan.isAdmin) {
                                "Admin"
                            } else {
                                "Staff"
                            },

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            if (karyawan.isAdmin) {
                                Color(0xFF7C3AED)
                            } else {
                                PrimaryGreen
                            }
                    )
                }
            }
        }
    }
}


// ==========================================================
// DETAIL KARYAWAN
// ==========================================================

@Composable
private fun KaryawanDetailScreen(
    karyawan: DataKaryawan,
    onBack: () -> Unit
) {

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

                Column {

                    Text(
                        text =
                            "Detail Karyawan",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Informasi lengkap karyawan",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }
            }


            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        start = 20.dp,
                        top = 6.dp,
                        end = 20.dp,
                        bottom = 30.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(22.dp),

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
                                    .padding(22.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Surface(
                                modifier =
                                    Modifier.size(82.dp),

                                shape =
                                    CircleShape,

                                color =
                                    Color(0xFFE6EEE9)
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            getKaryawanInitials(
                                                karyawan.nama
                                            ),

                                        fontSize =
                                            25.sp,

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            PrimaryGreen
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(13.dp)
                            )

                            Text(
                                text =
                                    karyawan.nama.ifBlank {
                                        "Tanpa Nama"
                                    },

                                fontSize =
                                    21.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark,

                                textAlign =
                                    TextAlign.Center
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(7.dp)
                            )

                            Surface(
                                shape =
                                    RoundedCornerShape(20.dp),

                                color =
                                    if (karyawan.isAdmin) {
                                        Color(0xFFF3E8FF)
                                    } else {
                                        Color(0xFFE8F5E9)
                                    }
                            ) {

                                Text(
                                    text =
                                        if (karyawan.isAdmin) {
                                            "ADMIN"
                                        } else {
                                            "STAFF"
                                        },

                                    modifier =
                                        Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 6.dp
                                        ),

                                    fontSize =
                                        10.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        if (karyawan.isAdmin) {
                                            Color(0xFF7C3AED)
                                        } else {
                                            PrimaryGreen
                                        }
                                )
                            }
                        }
                    }
                }


                item {

                    SectionTitle(
                        title =
                            "Informasi Pribadi"
                    )
                }


                item {

                    DetailCard {

                        DetailRow(
                            icon =
                                Icons.Default.Person,

                            title =
                                "Nama",

                            value =
                                karyawan.nama.ifBlank {
                                    "-"
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        DetailRow(
                            icon =
                                Icons.Default.Email,

                            title =
                                "Email",

                            value =
                                karyawan.email.ifBlank {
                                    "-"
                                }
                        )
                    }
                }


                item {

                    SectionTitle(
                        title =
                            "Informasi Pekerjaan"
                    )
                }


                item {

                    DetailCard {

                        DetailRow(
                            icon =
                                Icons.Default.Badge,

                            title =
                                "Jabatan",

                            value =
                                karyawan.jabatan.ifBlank {
                                    "-"
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        DetailRow(
                            icon =
                                Icons.Default.Work,

                            title =
                                "Divisi",

                            value =
                                karyawan.divisi.ifBlank {
                                    "-"
                                }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        DetailRow(
                            icon =
                                Icons.Default.AdminPanelSettings,

                            title =
                                "Hak Akses",

                            value =
                                if (karyawan.isAdmin) {
                                    "Administrator"
                                } else {
                                    "Staff"
                                }
                        )
                    }
                }


                item {

                    SectionTitle(
                        title =
                            "Informasi Kontak"
                    )
                }


                item {

                    DetailCard {

                        DetailRow(
                            icon =
                                Icons.Default.Send,

                            title =
                                "Username Telegram",

                            value =
                                karyawan.usernameTele.ifBlank {
                                    "-"
                                }
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text =
            title,

        fontSize =
            16.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            TextDark
    )
}


// ==========================================================
// DETAIL CARD
// ==========================================================

@Composable
private fun DetailCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            content()
        }
    }
}


// ==========================================================
// DETAIL ROW
// ==========================================================

@Composable
private fun DetailRow(
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

        Surface(
            modifier =
                Modifier.size(42.dp),

            shape =
                RoundedCornerShape(12.dp),

            color =
                Color(0xFFE8F5E9)
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.padding(10.dp)
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
                    Modifier.height(3.dp)
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


// ==========================================================
// TAMBAH KARYAWAN DIALOG
// ==========================================================

@Composable
private fun TambahKaryawanDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {

    val context =
        LocalContext.current

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    var nama by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var jabatan by remember {
        mutableStateOf("")
    }

    var divisi by remember {
        mutableStateOf("")
    }

    var usernameTele by remember {
        mutableStateOf("")
    }

    var isAdmin by remember {
        mutableStateOf(false)
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    AlertDialog(

        onDismissRequest = {

            if (!isSaving) {
                onDismiss()
            }
        },

        title = {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(40.dp),

                    shape =
                        RoundedCornerShape(11.dp),

                    color =
                        Color(0xFFE8F5E9)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text =
                            "Tambah Data",

                        fontSize =
                            19.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Buat akun karyawan baru",

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }
            }
        },

        text = {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(470.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                item {

                    FormSectionLabel(
                        text =
                            "Data Akun"
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            nama,

                        onValueChange = {
                            nama = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Nama Lengkap")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,

                                contentDescription =
                                    null
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            email,

                        onValueChange = {
                            email = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Email")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Email,

                                contentDescription =
                                    null
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            password,

                        onValueChange = {
                            password = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Password")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,

                                contentDescription =
                                    null
                            )
                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    showPassword =
                                        !showPassword
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (showPassword) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },

                                    contentDescription =
                                        "Tampilkan password"
                                )
                            }
                        },

                        visualTransformation =
                            if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    FormSectionLabel(
                        text =
                            "Data Pekerjaan"
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            jabatan,

                        onValueChange = {
                            jabatan = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Jabatan")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Badge,

                                contentDescription =
                                    null
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            divisi,

                        onValueChange = {
                            divisi = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Divisi")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Work,

                                contentDescription =
                                    null
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    OutlinedTextField(

                        value =
                            usernameTele,

                        onValueChange = {
                            usernameTele = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text("Username Telegram")
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Send,

                                contentDescription =
                                    null
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    )
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    FormSectionLabel(
                        text =
                            "Hak Akses"
                    )
                }


                item {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFF8FAF9)
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(8.dp)
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                RadioButton(
                                    selected =
                                        !isAdmin,

                                    onClick = {
                                        isAdmin = false
                                    }
                                )

                                Text(
                                    text =
                                        "Staff",

                                    fontSize =
                                        13.sp,

                                    fontWeight =
                                        if (!isAdmin) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(18.dp)
                                )

                                RadioButton(
                                    selected =
                                        isAdmin,

                                    onClick = {
                                        isAdmin = true
                                    }
                                )

                                Text(
                                    text =
                                        "Admin",

                                    fontSize =
                                        13.sp,

                                    fontWeight =
                                        if (isAdmin) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                )
                            }
                        }
                    }
                }


                if (errorMessage.isNotBlank()) {

                    item {

                        Surface(
                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(10.dp),

                            color =
                                Color(0xFFFFEBEE)
                        ) {

                            Text(
                                text =
                                    errorMessage,

                                modifier =
                                    Modifier.padding(12.dp),

                                fontSize =
                                    12.sp,

                                color =
                                    Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {

            Button(

                enabled =
                    !isSaving,

                onClick = {

                    val namaClean =
                        nama.trim()

                    val emailClean =
                        email.trim()

                    val passwordClean =
                        password.trim()

                    if (
                        namaClean.isBlank() ||
                        emailClean.isBlank() ||
                        passwordClean.isBlank()
                    ) {

                        errorMessage =
                            "Nama, email, dan password wajib diisi."

                        return@Button
                    }

                    if (
                        passwordClean.length < 6
                    ) {

                        errorMessage =
                            "Password minimal 6 karakter."

                        return@Button
                    }


                    isSaving = true
                    errorMessage = ""


                    MainScope().launch {

                        var secondaryApp:
                                FirebaseApp? = null

                        try {

                            val primaryApp =
                                FirebaseApp.getInstance()

                            val options =
                                primaryApp.options


                            secondaryApp =
                                try {

                                    FirebaseApp.getInstance(
                                        "CreateUserApp"
                                    )

                                } catch (
                                    _: Exception
                                ) {

                                    FirebaseApp.initializeApp(
                                        context,
                                        options,
                                        "CreateUserApp"
                                    )
                                }


                            val secondaryAuth =
                                FirebaseAuth.getInstance(
                                    secondaryApp
                                )


                            val result =
                                secondaryAuth
                                    .createUserWithEmailAndPassword(
                                        emailClean,
                                        passwordClean
                                    )
                                    .await()


                            val newUid =
                                result.user?.uid
                                    ?: throw Exception(
                                        "UID akun tidak ditemukan."
                                    )


                            val userData =
                                hashMapOf(

                                    "uid" to
                                            newUid,

                                    "nama" to
                                            namaClean,

                                    "email" to
                                            emailClean,

                                    "jabatan" to
                                            jabatan.trim(),

                                    "divisi" to
                                            divisi.trim(),

                                    "usernameTele" to
                                            usernameTele.trim(),

                                    "isAdmin" to
                                            isAdmin
                                )


                            db.collection("users")
                                .document(newUid)
                                .set(userData)
                                .await()


                            secondaryAuth.signOut()

                            onSuccess()

                        } catch (e: Exception) {

                            errorMessage =
                                e.message
                                    ?: "Gagal membuat akun."

                        } finally {

                            isSaving = false
                        }
                    }
                },

                shape =
                    RoundedCornerShape(11.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    )
            ) {

                if (isSaving) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.size(17.dp),

                        color =
                            Color.White,

                        strokeWidth =
                            2.dp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text =
                            "Menyimpan..."
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Add,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Simpan Data"
                    )
                }
            }
        },

        dismissButton = {

            TextButton(

                enabled =
                    !isSaving,

                onClick =
                    onDismiss
            ) {

                Text(
                    text =
                        "Batal",

                    color =
                        TextGray
                )
            }
        }
    )
}


// ==========================================================
// FORM SECTION LABEL
// ==========================================================

@Composable
private fun FormSectionLabel(
    text: String
) {

    Text(
        text =
            text,

        fontSize =
            14.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            TextDark
    )
}


// ==========================================================
// INITIAL KARYAWAN
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