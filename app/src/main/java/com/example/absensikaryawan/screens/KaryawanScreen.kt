
package com.example.absensikaryawan.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
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
    val atasan: String,
    val owner: String,
    val usernameTele: String,
    val isAdmin: Boolean
)


// ==========================================================
// MODEL PILIHAN ATASAN
// ==========================================================

private data class PilihanAtasan(
    val uid: String,
    val nama: String,
    val jabatan: String
)


// ==========================================================
// FILTER KARYAWAN
// ==========================================================

private enum class FilterKaryawan {
    TOTAL,
    STAFF,
    ADMIN
}


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

    var selectedFilter by remember {
        mutableStateOf(FilterKaryawan.TOTAL)
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

                        atasan = document.getString("atasan")
                            ?: "",

                        owner = document.getString("owner")
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
            },

            onDataChanged = {
                selectedKaryawan = null
                refreshKey++
            }
        )

        return
    }


    // ======================================================
    // TAMBAH DATA
    // ======================================================

    if (showTambahData) {

        TambahKaryawanDialog(

            daftarKaryawan = daftarKaryawan,

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
        totalKaryawan - totalAdmin


    // ======================================================
    // FILTER
    // ======================================================

    val daftarSesuaiFilter =
        remember(
            daftarKaryawan,
            selectedFilter
        ) {

            when (selectedFilter) {

                FilterKaryawan.TOTAL ->
                    daftarKaryawan

                FilterKaryawan.STAFF ->
                    daftarKaryawan.filter {
                        !it.isAdmin
                    }

                FilterKaryawan.ADMIN ->
                    daftarKaryawan.filter {
                        it.isAdmin
                    }
            }
        }


    // ======================================================
    // NORMALISASI SEARCH
    // ======================================================

    fun normalizeSearchText(
        value: String
    ): String {

        return value
            .trim()
            .lowercase()
            .replace(
                Regex("\\s+"),
                " "
            )
    }


    // ======================================================
    // HASIL PENCARIAN
    // ======================================================

    val hasilPencarian =
        remember(
            daftarSesuaiFilter,
            searchQuery
        ) {

            val query =
                normalizeSearchText(
                    searchQuery
                )

            if (query.isBlank()) {

                daftarSesuaiFilter

            } else {

                daftarSesuaiFilter.filter { karyawan ->

                    normalizeSearchText(
                        karyawan.nama
                    ).contains(query) ||

                            normalizeSearchText(
                                karyawan.email
                            ).contains(query) ||

                            normalizeSearchText(
                                karyawan.jabatan
                            ).contains(query) ||

                            normalizeSearchText(
                                karyawan.divisi
                            ).contains(query) ||

                            normalizeSearchText(
                                karyawan.atasan
                            ).contains(query) ||

                            normalizeSearchText(
                                karyawan.owner
                            ).contains(query) ||

                            normalizeSearchText(
                                karyawan.usernameTele
                            ).contains(query)
                }
            }
        }


    // ======================================================
    // LABEL FILTER AKTIF
    // ======================================================

    val filterAktifText =
        when (selectedFilter) {

            FilterKaryawan.TOTAL ->
                "Semua Karyawan"

            FilterKaryawan.STAFF ->
                "Staff"

            FilterKaryawan.ADMIN ->
                "Admin"
        }


    // ======================================================
    // JUMLAH HASIL
    // ======================================================

    val jumlahHasil =
        hasilPencarian.size


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
                        Modifier.width(3.dp)
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
                                Modifier.size(22.dp)
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
                            when {

                                loading ->
                                    "Memuat data karyawan..."

                                totalKaryawan == 0 ->
                                    "Belum ada karyawan terdaftar"

                                else ->
                                    "$totalKaryawan karyawan terdaftar"
                            },

                        fontSize =
                            12.sp,

                        color =
                            TextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    enabled =
                        !loading,

                    onClick = {

                        if (!loading) {
                            refreshKey++
                        }
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Refresh,

                        contentDescription =
                            "Refresh",

                        tint =
                            if (loading) {
                                TextGray
                            } else {
                                PrimaryGreen
                            }
                    )
                }
            }


            // ==================================================
            // TAMBAH
            // ==================================================

            Button(
                onClick = {
                    showTambahData = true
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    ),

                contentPadding =
                    PaddingValues(
                        vertical = 12.dp
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
                        "Tambah Data Karyawan",

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            // ==================================================
            // SUMMARY
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

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
                        PrimaryGreen,

                    selected =
                        selectedFilter ==
                                FilterKaryawan.TOTAL,

                    onClick = {

                        selectedFilter =
                            FilterKaryawan.TOTAL
                    }
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
                        PrimaryGreen,

                    selected =
                        selectedFilter ==
                                FilterKaryawan.STAFF,

                    onClick = {

                        selectedFilter =
                            FilterKaryawan.STAFF
                    }
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
                        Color(0xFF7C3AED),

                    selected =
                        selectedFilter ==
                                FilterKaryawan.ADMIN,

                    onClick = {

                        selectedFilter =
                            FilterKaryawan.ADMIN
                    }
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
                            when (selectedFilter) {

                                FilterKaryawan.TOTAL ->
                                    "Cari nama, email, jabatan, atasan, owner"

                                FilterKaryawan.STAFF ->
                                    "Cari staff berdasarkan nama, email, atasan"

                                FilterKaryawan.ADMIN ->
                                    "Cari admin berdasarkan nama atau email"
                            },

                        fontSize =
                            12.sp,

                        color =
                            TextGray
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
                                    "Hapus pencarian",

                                tint =
                                    TextGray
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
                            Color.White,

                        cursorColor =
                            PrimaryGreen
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // HASIL FILTER + SEARCH
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            when {

                                jumlahHasil == 1 ->
                                    "1 karyawan ditemukan"

                                else ->
                                    "$jumlahHasil karyawan ditemukan"
                            },

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextDark
                    )

                    if (searchQuery.isNotBlank()) {

                        Text(
                            text =
                                "Pencarian: \"${searchQuery.trim()}\"",

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

                Surface(
                    shape =
                        RoundedCornerShape(20.dp),

                    color =
                        when (selectedFilter) {

                            FilterKaryawan.ADMIN ->
                                Color(0xFFF3E8FF)

                            FilterKaryawan.STAFF ->
                                Color(0xFFE8F5E9)

                            FilterKaryawan.TOTAL ->
                                Color(0xFFE6EEE9)
                        }
                ) {

                    Text(
                        text =
                            filterAktifText,

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            when (selectedFilter) {

                                FilterKaryawan.ADMIN ->
                                    Color(0xFF7C3AED)

                                FilterKaryawan.STAFF ->
                                    PrimaryGreen

                                FilterKaryawan.TOTAL ->
                                    PrimaryGreen
                            }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(8.dp)
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
                            modifier =
                                Modifier.size(30.dp),

                            color =
                                PrimaryGreen,

                            strokeWidth =
                                3.dp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Memuat data karyawan...",

                            fontSize =
                                13.sp,

                            color =
                                TextDark
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
                                Modifier.size(45.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
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
                                Modifier.height(15.dp)
                        )

                        OutlinedButton(
                            onClick = {

                                if (!loading) {
                                    refreshKey++
                                }
                            },

                            shape =
                                RoundedCornerShape(11.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Refresh,

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
                                Modifier.size(72.dp),

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
                                        if (searchQuery.isBlank()) {
                                            Icons.Default.Group
                                        } else {
                                            Icons.Default.Search
                                        },

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
                                when {

                                    searchQuery.isNotBlank() ->
                                        "Karyawan tidak ditemukan"

                                    selectedFilter ==
                                            FilterKaryawan.STAFF ->
                                        "Belum ada Staff"

                                    selectedFilter ==
                                            FilterKaryawan.ADMIN ->
                                        "Belum ada Admin"

                                    else ->
                                        "Belum ada karyawan"
                                },

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
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
    iconColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) {

            if (title == "Admin") {
                Color(0xFFF3E8FF)
            } else {
                Color(0xFFE8F5E9)
            }

        } else {
            Color.White
        }

    val borderColor =
        if (selected) {

            if (title == "Admin") {
                Color(0xFF7C3AED)
            } else {
                PrimaryGreen
            }

        } else {
            Color.Transparent
        }

    Card(
        modifier =
            modifier.clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    backgroundColor
            ),

        border =
            BorderStroke(
                width =
                    if (selected) {
                        1.5.dp
                    } else {
                        0.dp
                    },

                color =
                    borderColor
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (selected) {
                        3.dp
                    } else {
                        1.dp
                    }
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
        ) {

            Surface(
                modifier =
                    Modifier.size(35.dp),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    if (selected) {

                        if (title == "Admin") {
                            Color(0xFFE9D5FF)
                        } else {
                            Color.White
                        }

                    } else {
                        Color(0xFFE6EEE9)
                    }
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
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    title,

                fontSize =
                    10.sp,

                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },

                color =
                    if (selected) {
                        iconColor
                    } else {
                        TextGray
                    }
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
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
                    Modifier.size(50.dp),

                shape =
                    CircleShape,

                color =
                    if (karyawan.isAdmin) {
                        Color(0xFFF3E8FF)
                    } else {
                        Color(0xFFE6EEE9)
                    }
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
                            if (karyawan.isAdmin) {
                                Color(0xFF7C3AED)
                            } else {
                                PrimaryGreen
                            }
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
                            Modifier.height(6.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        if (karyawan.jabatan.isNotBlank()) {

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

                        if (karyawan.divisi.isNotBlank()) {

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
                                    TextGray
                            )
                        }
                    }
                }

                if (
                    !karyawan.isAdmin &&
                    karyawan.atasan.isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.SupervisorAccount,

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
                                "Atasan: ${karyawan.atasan}",

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

                Text(
                    text =
                        if (karyawan.isAdmin) {
                            "Admin"
                        } else {
                            karyawan.jabatan.ifBlank {
                                "Staff"
                            }
                        },

                    modifier =
                        Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        ),

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


// ==========================================================
// DETAIL KARYAWAN
// ==========================================================

@Composable
private fun KaryawanDetailScreen(
    karyawan: DataKaryawan,
    onBack: () -> Unit,
    onDataChanged: () -> Unit
) {

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    val scope =
        rememberCoroutineScope()

    var showEditEmail by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirm by remember {
        mutableStateOf(false)
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    var actionMessage by remember {
        mutableStateOf("")
    }


    // ======================================================
    // EDIT EMAIL
    // ======================================================

    if (showEditEmail) {

        EditEmailDialog(

            karyawan =
                karyawan,

            isSaving =
                isProcessing,

            onDismiss = {

                if (!isProcessing) {
                    showEditEmail = false
                }
            },

            onSave = { newEmail ->

                isProcessing = true
                actionMessage = ""

                scope.launch {

                    try {

                        db.collection("users")
                            .document(karyawan.id)
                            .update(
                                "email",
                                newEmail
                            )
                            .await()

                        showEditEmail = false
                        isProcessing = false

                        onDataChanged()

                    } catch (e: Exception) {

                        isProcessing = false

                        actionMessage =
                            e.message
                                ?: "Gagal mengubah email."
                    }
                }
            }
        )
    }


    // ======================================================
    // DELETE CONFIRM
    // ======================================================

    if (showDeleteConfirm) {

        AlertDialog(

            onDismissRequest = {

                if (!isProcessing) {
                    showDeleteConfirm = false
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
                            Color(0xFFFFEBEE)
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Delete,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFFC62828)
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Text(
                        text =
                            "Hapus Data Karyawan?"
                    )
                }
            },

            text = {

                Column {

                    Text(
                        text =
                            "Data karyawan berikut akan dihapus dari Firestore:"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            karyawan.nama,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            karyawan.email,

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(10.dp),

                        color =
                            Color(0xFFFFF8E1)
                    ) {

                        Text(
                            text =
                                "Catatan: akun Firebase Authentication tidak ikut terhapus. Jika akun login tersebut sudah tidak diperlukan, hapus secara manual dari Firebase Console.",

                            modifier =
                                Modifier.padding(12.dp),

                            fontSize =
                                12.sp,

                            color =
                                Color(0xFF8D6E00)
                        )
                    }
                }
            },

            confirmButton = {

                Button(

                    enabled =
                        !isProcessing,

                    onClick = {

                        isProcessing = true
                        actionMessage = ""

                        scope.launch {

                            try {

                                db.collection("users")
                                    .document(karyawan.id)
                                    .delete()
                                    .await()

                                showDeleteConfirm = false
                                isProcessing = false

                                onDataChanged()

                            } catch (e: Exception) {

                                isProcessing = false

                                actionMessage =
                                    e.message
                                        ?: "Gagal menghapus data karyawan."
                            }
                        }
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFFC62828)
                        ),

                    shape =
                        RoundedCornerShape(10.dp)
                ) {

                    if (isProcessing) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(17.dp),

                            color =
                                Color.White,

                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(17.dp)
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            if (isProcessing) {
                                "Menghapus..."
                            } else {
                                "Hapus Data"
                            }
                    )
                }
            },

            dismissButton = {

                TextButton(

                    enabled =
                        !isProcessing,

                    onClick = {
                        showDeleteConfirm = false
                    }
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


    // ======================================================
    // DETAIL UI
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

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

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
                        top = 5.dp,
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
                                    Modifier.size(84.dp),

                                shape =
                                    CircleShape,

                                color =
                                    if (karyawan.isAdmin) {
                                        Color(0xFFF3E8FF)
                                    } else {
                                        Color(0xFFE6EEE9)
                                    }
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
                                            26.sp,

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
                                            karyawan.jabatan
                                                .ifBlank {
                                                    "STAFF"
                                                }
                                                .uppercase()
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

                        if (!karyawan.isAdmin) {

                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )

                            DetailRow(
                                icon =
                                    Icons.Default.SupervisorAccount,

                                title =
                                    "Atasan",

                                value =
                                    karyawan.atasan.ifBlank {
                                        "Belum ditentukan"
                                    }
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )

                            DetailRow(
                                icon =
                                    Icons.Default.AccountTree,

                                title =
                                    "Owner",

                                value =
                                    karyawan.owner.ifBlank {
                                        "Belum ditentukan"
                                    }
                            )
                        }

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


                item {
                    SectionTitle(
                        title =
                            "Kelola Akun"
                    )
                }

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(18.dp),

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
                                Modifier.padding(16.dp)
                        ) {

                            OutlinedButton(

                                enabled =
                                    !isProcessing,

                                onClick = {
                                    showEditEmail = true
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(12.dp),

                                border =
                                    BorderStroke(
                                        1.dp,
                                        PrimaryGreen
                                    )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Email,

                                    contentDescription =
                                        null,

                                    tint =
                                        PrimaryGreen
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Text(
                                    text =
                                        "Edit Email",

                                    color =
                                        PrimaryGreen,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )


                            Button(

                                enabled =
                                    !isProcessing,

                                onClick = {
                                    showDeleteConfirm = true
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(12.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            Color(0xFFFFEBEE),

                                        contentColor =
                                            Color(0xFFC62828)
                                    )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,

                                    contentDescription =
                                        null
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Text(
                                    text =
                                        "Hapus Data Karyawan",

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }


                            if (actionMessage.isNotBlank()) {

                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
                                )

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
                                            actionMessage,

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
                }
            }
        }
    }
}


// ==========================================================
// EDIT EMAIL DIALOG
// ==========================================================

@Composable
private fun EditEmailDialog(
    karyawan: DataKaryawan,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {

    var email by remember {
        mutableStateOf(karyawan.email)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    AlertDialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                dismissOnClickOutside = false
            ),

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
                                Icons.Default.Email,

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
                            "Edit Email",

                        fontSize =
                            19.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            karyawan.nama,

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }
            }
        },

        text = {

            Column {

                OutlinedTextField(

                    value =
                        email,

                    onValueChange = {

                        email = it
                        errorMessage = ""
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isSaving,

                    singleLine =
                        true,

                    label = {
                        Text("Email Baru")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Email,

                            contentDescription =
                                null
                        )
                    },

                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType =
                                KeyboardType.Email,

                            imeAction =
                                ImeAction.Done
                        ),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                PrimaryGreen,

                            cursorColor =
                                PrimaryGreen
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Email profil yang tersimpan di data Firestore akan diperbarui. Email login Firebase Authentication tidak berubah.",

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )

                if (errorMessage.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            errorMessage,

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFFC62828)
                    )
                }
            }
        },

        confirmButton = {

            Button(

                enabled =
                    !isSaving,

                onClick = {

                    val emailClean =
                        email.trim()

                    when {

                        emailClean.isBlank() -> {

                            errorMessage =
                                "Email wajib diisi."
                        }

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(emailClean)
                            .matches() -> {

                            errorMessage =
                                "Format email tidak valid."
                        }

                        emailClean.equals(
                            karyawan.email.trim(),
                            ignoreCase = true
                        ) -> {

                            errorMessage =
                                "Email baru sama dengan email lama."
                        }

                        else -> {

                            onSave(emailClean)
                        }
                    }
                },

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    ),

                shape =
                    RoundedCornerShape(10.dp)
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
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Menyimpan..."
                    )

                } else {

                    Text(
                        text =
                            "Simpan"
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
            15.sp,

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
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

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

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(20.dp)
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
                    TextDark,

                maxLines =
                    3,

                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}


// ==========================================================
// TAMBAH KARYAWAN DIALOG
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TambahKaryawanDialog(
    daftarKaryawan: List<DataKaryawan>,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {

    val context =
        LocalContext.current

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    val scope =
        rememberCoroutineScope()

    val focusManager =
        LocalFocusManager.current

    val namaFocusRequester =
        remember {
            FocusRequester()
        }

    val emailFocusRequester =
        remember {
            FocusRequester()
        }

    val passwordFocusRequester =
        remember {
            FocusRequester()
        }

    val jabatanFocusRequester =
        remember {
            FocusRequester()
        }

    val divisiFocusRequester =
        remember {
            FocusRequester()
        }

    val telegramFocusRequester =
        remember {
            FocusRequester()
        }

    val formListState =
        rememberLazyListState()

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

    var atasan by remember {
        mutableStateOf("")
    }

    var owner by remember {
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

    var focusedField by remember {
        mutableStateOf<String?>(null)
    }

    var atasanExpanded by remember {
        mutableStateOf(false)
    }

    var ownerExpanded by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // PILIHAN ATASAN
    // ======================================================

    val pilihanAtasan =
        remember(
            daftarKaryawan,
            jabatan
        ) {

            val targetJabatan =
                when (
                    jabatan.trim()
                        .lowercase()
                ) {

                    "staff" ->
                        "Supervisor"

                    "supervisor" ->
                        "Manager"

                    "manager" ->
                        "HRD"

                    "hrd" ->
                        "Owner"

                    else ->
                        ""
                }

            if (targetJabatan.isBlank()) {
                emptyList()
            } else {
                daftarKaryawan
                    .filter {
                        !it.isAdmin &&
                                it.jabatan.equals(
                                    targetJabatan,
                                    ignoreCase = true
                                )
                    }
                    .map {
                        PilihanAtasan(
                            uid = it.id,
                            nama = it.nama,
                            jabatan = it.jabatan
                        )
                    }
                    .sortedBy {
                        it.nama.lowercase()
                    }
            }
        }


    // ======================================================
    // PILIHAN OWNER
    // ======================================================

    val pilihanOwner =
        remember(daftarKaryawan) {

            daftarKaryawan
                .filter {
                    !it.isAdmin &&
                            it.jabatan.equals(
                                "Owner",
                                ignoreCase = true
                            )
                }
                .map {
                    PilihanAtasan(
                        uid = it.id,
                        nama = it.nama,
                        jabatan = it.jabatan
                    )
                }
                .sortedBy {
                    it.nama.lowercase()
                }
        }


    // ======================================================
    // JIKA JABATAN BERUBAH
    // ======================================================

    LaunchedEffect(jabatan) {

        val targetJabatan =
            when (
                jabatan.trim()
                    .lowercase()
            ) {

                "staff" ->
                    "Supervisor"

                "supervisor" ->
                    "Manager"

                "manager" ->
                    "HRD"

                "hrd" ->
                    "Owner"

                else ->
                    ""
            }

        if (
            atasan.isNotBlank() &&
            pilihanAtasan.none {
                it.nama.equals(
                    atasan,
                    ignoreCase = true
                )
            }
        ) {

            atasan = ""
        }

        if (
            targetJabatan.isBlank()
        ) {

            atasan = ""
        }

        if (
            jabatan.equals(
                "Owner",
                ignoreCase = true
            )
        ) {

            atasan = ""
        }

        if (
            jabatan.equals(
                "Admin",
                ignoreCase = true
            )
        ) {

            atasan = ""
            owner = ""
        }
    }


    // ======================================================
    // AUTO OWNER DARI PILIHAN ATASAN
    // ======================================================

    LaunchedEffect(atasan, daftarKaryawan) {

        if (atasan.isBlank()) {
            return@LaunchedEffect
        }

        val dataAtasan =
            daftarKaryawan.firstOrNull {
                it.nama.equals(
                    atasan,
                    ignoreCase = true
                )
            }

        if (dataAtasan != null) {

            if (
                dataAtasan.owner.isNotBlank()
            ) {

                owner =
                    dataAtasan.owner

            } else if (
                dataAtasan.jabatan.equals(
                    "Owner",
                    ignoreCase = true
                )
            ) {

                owner =
                    dataAtasan.nama
            }
        }
    }


    LaunchedEffect(focusedField) {

        when (focusedField) {

            "nama" ->
                formListState.scrollToItem(1)

            "email" ->
                formListState.scrollToItem(2)

            "password" ->
                formListState.scrollToItem(3)

            "jabatan" ->
                formListState.scrollToItem(5)

            "divisi" ->
                formListState.scrollToItem(6)

            "telegram" ->
                formListState.scrollToItem(7)

            "atasan" ->
                formListState.scrollToItem(8)

            "owner" ->
                formListState.scrollToItem(9)
        }
    }


    AlertDialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true
            ),

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

                state =
                    formListState,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 500.dp
                        ),

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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    namaFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "nama"
                                    }
                                },

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

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Text,

                                imeAction =
                                    ImeAction.Next
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onNext = {
                                    emailFocusRequester
                                        .requestFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    emailFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "email"
                                    }
                                },

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

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email,

                                imeAction =
                                    ImeAction.Next
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onNext = {
                                    passwordFocusRequester
                                        .requestFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    passwordFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "password"
                                    }
                                },

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
                                        if (showPassword) {
                                            "Sembunyikan password"
                                        } else {
                                            "Tampilkan password"
                                        }
                                )
                            }
                        },

                        visualTransformation =
                            if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Password,

                                imeAction =
                                    ImeAction.Next
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onNext = {
                                    jabatanFocusRequester
                                        .requestFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
                    )
                }

                item {

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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    jabatanFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "jabatan"
                                    }
                                },

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

                        supportingText = {

                            Text(
                                text =
                                    "Gunakan: Staff, Supervisor, Manager, HRD, atau Owner",

                                fontSize =
                                    10.sp
                            )
                        },

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Text,

                                imeAction =
                                    ImeAction.Next
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onNext = {
                                    divisiFocusRequester
                                        .requestFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    divisiFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "divisi"
                                    }
                                },

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

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Text,

                                imeAction =
                                    ImeAction.Next
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onNext = {
                                    telegramFocusRequester
                                        .requestFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
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
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(
                                    telegramFocusRequester
                                )
                                .onFocusChanged {

                                    if (it.isFocused) {
                                        focusedField =
                                            "telegram"
                                    }
                                },

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

                        keyboardOptions =
                            androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Text,

                                imeAction =
                                    ImeAction.Done
                            ),

                        keyboardActions =
                            androidx.compose.foundation.text.KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                cursorColor =
                                    PrimaryGreen
                            )
                    )
                }

                item {

                    FormSectionLabel(
                        text =
                            "Struktur Hirarki"
                    )
                }

                // ==================================================
// ATASAN
// ==================================================

                item {

                    val isHierarchyPosition =
                        jabatan.equals("Staff", ignoreCase = true) ||
                                jabatan.equals("Supervisor", ignoreCase = true) ||
                                jabatan.equals("Manager", ignoreCase = true) ||
                                jabatan.equals("HRD", ignoreCase = true)

                    if (isHierarchyPosition) {

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            OutlinedTextField(

                                value = atasan,

                                onValueChange = {},

                                readOnly = true,

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (pilihanAtasan.isNotEmpty()) {
                                                atasanExpanded = !atasanExpanded
                                            }
                                        },

                                label = {
                                    Text("Atasan")
                                },

                                placeholder = {
                                    Text(
                                        if (pilihanAtasan.isEmpty()) {
                                            "Belum ada ${getTargetJabatan(jabatan)}"
                                        } else {
                                            "Pilih atasan"
                                        }
                                    )
                                },

                                leadingIcon = {

                                    Icon(
                                        imageVector =
                                            Icons.Default.SupervisorAccount,

                                        contentDescription =
                                            null
                                    )
                                },

                                trailingIcon = {

                                    Icon(
                                        imageVector =
                                            if (atasanExpanded) {
                                                Icons.Default.KeyboardArrowUp
                                            } else {
                                                Icons.Default.KeyboardArrowDown
                                            },

                                        contentDescription =
                                            null
                                    )
                                },

                                supportingText = {

                                    Text(
                                        text =
                                            "Atasan otomatis mengikuti struktur jabatan",

                                        fontSize =
                                            10.sp
                                    )
                                },

                                shape =
                                    RoundedCornerShape(12.dp),

                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor =
                                            PrimaryGreen,

                                        cursorColor =
                                            PrimaryGreen
                                    )
                            )

                            DropdownMenu(

                                expanded =
                                    atasanExpanded,

                                onDismissRequest = {
                                    atasanExpanded = false
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                if (pilihanAtasan.isEmpty()) {

                                    DropdownMenuItem(

                                        text = {

                                            Text(
                                                text =
                                                    "Belum ada ${getTargetJabatan(jabatan)}"
                                            )
                                        },

                                        onClick = {
                                            atasanExpanded = false
                                        }
                                    )

                                } else {

                                    pilihanAtasan.forEach { pilihan ->

                                        DropdownMenuItem(

                                            text = {

                                                Column {

                                                    Text(
                                                        text =
                                                            pilihan.nama,

                                                        fontWeight =
                                                            FontWeight.SemiBold
                                                    )

                                                    Text(
                                                        text =
                                                            pilihan.jabatan,

                                                        fontSize =
                                                            11.sp,

                                                        color =
                                                            TextGray
                                                    )
                                                }
                                            },

                                            onClick = {

                                                atasan =
                                                    pilihan.nama

                                                atasanExpanded =
                                                    false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                    } else {

                        OutlinedTextField(

                            value =
                                if (
                                    jabatan.equals(
                                        "Owner",
                                        ignoreCase = true
                                    )
                                ) {
                                    "Tidak ada"
                                } else {
                                    ""
                                },

                            onValueChange = {},

                            readOnly = true,

                            enabled = false,

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text("Atasan")
                            },

                            supportingText = {

                                Text(
                                    text =
                                        if (
                                            jabatan.equals(
                                                "Owner",
                                                ignoreCase = true
                                            )
                                        ) {
                                            "Owner merupakan tingkat paling atas."
                                        } else {
                                            "Isi jabatan terlebih dahulu."
                                        },

                                    fontSize =
                                        10.sp
                                )
                            },

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Default.SupervisorAccount,

                                    contentDescription =
                                        null
                                )
                            },

                            shape =
                                RoundedCornerShape(12.dp)
                        )
                    }
                }


                // ==================================================
                // OWNER
                // ==================================================

                item {

                    val showOwnerField =
                        !isAdmin &&
                                !jabatan.equals(
                                    "Admin",
                                    ignoreCase = true
                                )

                    if (showOwnerField) {

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            OutlinedTextField(

                                value = owner,

                                onValueChange = {},

                                readOnly = true,

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (pilihanOwner.isNotEmpty()) {
                                                ownerExpanded = !ownerExpanded
                                            }
                                        },

                                label = {
                                    Text("Owner")
                                },

                                placeholder = {
                                    Text(
                                        if (pilihanOwner.isEmpty()) {
                                            "Belum ada Owner"
                                        } else {
                                            "Pilih Owner"
                                        }
                                    )
                                },

                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccountTree,
                                        contentDescription = null
                                    )
                                },

                                trailingIcon = {
                                    Icon(
                                        imageVector =
                                            if (ownerExpanded) {
                                                Icons.Default.ExpandLess
                                            } else {
                                                Icons.Default.ExpandMore
                                            },
                                        contentDescription = null
                                    )
                                },

                                supportingText = {
                                    Text(
                                        text =
                                            "Owner menjadi tujuan akhir jalur approval",
                                        fontSize = 10.sp
                                    )
                                },

                                shape = RoundedCornerShape(12.dp),

                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryGreen,
                                        cursorColor = PrimaryGreen
                                    )
                            )

                            DropdownMenu(
                                expanded = ownerExpanded,
                                onDismissRequest = {
                                    ownerExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                if (pilihanOwner.isEmpty()) {

                                    DropdownMenuItem(
                                        text = {
                                            Text("Belum ada Owner")
                                        },
                                        onClick = {
                                            ownerExpanded = false
                                        }
                                    )

                                } else {

                                    pilihanOwner.forEach { pilihan ->

                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        text = pilihan.nama,
                                                        fontWeight = FontWeight.SemiBold
                                                    )

                                                    Text(
                                                        text = pilihan.jabatan,
                                                        fontSize = 11.sp,
                                                        color = TextGray
                                                    )
                                                }
                                            },
                                            onClick = {
                                                owner = pilihan.nama
                                                ownerExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                item {

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

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),

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
                                    "Staff"
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(18.dp)
                            )

                            RadioButton(
                                selected =
                                    isAdmin,

                                onClick = {

                                    isAdmin =
                                        true

                                    atasan =
                                        ""

                                    owner =
                                        ""
                                }
                            )

                            Text(
                                text =
                                    "Admin"
                            )
                        }
                    }
                }

                if (errorMessage.isNotBlank()) {

                    item {

                        Text(
                            text =
                                errorMessage,

                            fontSize =
                                12.sp,

                            color =
                                Color(0xFFC62828)
                        )
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

                    val jabatanClean =
                        jabatan.trim()

                    val divisiClean =
                        divisi.trim()

                    val telegramClean =
                        usernameTele.trim()

                    val atasanClean =
                        atasan.trim()

                    val ownerClean =
                        owner.trim()


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

                    if (
                        !isAdmin &&
                        jabatanClean.isBlank()
                    ) {

                        errorMessage =
                            "Jabatan wajib diisi."

                        return@Button
                    }


                    // ==========================================
                    // VALIDASI HIRARKI
                    // ==========================================

                    if (!isAdmin) {

                        val targetJabatan =
                            getTargetJabatan(
                                jabatanClean
                            )

                        if (
                            targetJabatan.isNotBlank() &&
                            atasanClean.isBlank()
                        ) {

                            errorMessage =
                                "Atasan wajib dipilih untuk jabatan $jabatanClean."

                            return@Button
                        }

                        if (
                            targetJabatan.isNotBlank() &&
                            pilihanAtasan.isEmpty()
                        ) {

                            errorMessage =
                                "Belum ada $targetJabatan yang tersedia sebagai atasan."

                            return@Button
                        }

                        if (
                            ownerClean.isBlank()
                        ) {

                            errorMessage =
                                "Owner wajib dipilih."

                            return@Button
                        }
                    }


                    isSaving = true
                    errorMessage = ""

                    scope.launch {

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
                                com.google.firebase.auth.FirebaseAuth
                                    .getInstance(
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


                            // ==================================
                            // DATA USER FIRESTORE
                            // ==================================

                            val userData =
                                hashMapOf(

                                    "uid" to newUid,

                                    "nama" to namaClean,

                                    "email" to emailClean,

                                    "jabatan" to
                                            if (isAdmin) {
                                                "Admin"
                                            } else {
                                                jabatanClean
                                            },

                                    "divisi" to
                                            divisiClean,

                                    "atasan" to
                                            if (isAdmin) {
                                                ""
                                            } else {
                                                atasanClean
                                            },

                                    "owner" to
                                            if (isAdmin) {
                                                ""
                                            } else {
                                                ownerClean
                                            },

                                    "usernameTele" to
                                            telegramClean,

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
// TARGET JABATAN ATASAN
// ==========================================================

private fun getTargetJabatan(
    jabatan: String
): String {

    return when (
        jabatan.trim()
            .lowercase()
    ) {

        "staff" ->
            "Supervisor"

        "supervisor" ->
            "Manager"

        "manager" ->
            "HRD"

        "hrd" ->
            "Owner"

        else ->
            ""
    }
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
