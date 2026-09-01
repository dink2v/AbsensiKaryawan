package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ==========================================================
// MODEL RIWAYAT ABSENSI
// ==========================================================

data class RiwayatAbsensi(
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val catatan: String
)

// ==========================================================
// MODEL RIWAYAT PENGAJUAN STAFF
// ==========================================================

data class RiwayatPengajuan(
    val documentId: String,
    val jenis: String,
    val jamPulang: String,
    val jamKeluar: String,
    val jamKembali: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val alasan: String,
    val status: String,
    val catatanAdmin: String
)

// ==========================================================
// FILTER RIWAYAT
// ==========================================================

enum class FilterRiwayat(
    val label: String,
    val jumlahHari: Int?
) {
    TUJUH_HARI(
        label = "7 Hari Terakhir",
        jumlahHari = 7
    ),

    TIGA_PULUH_HARI(
        label = "30 Hari Terakhir",
        jumlahHari = 30
    ),

    SEMUA(
        label = "Semua Riwayat",
        jumlahHari = null
    )
}

// ==========================================================
// TAB RIWAYAT
// ==========================================================

enum class TabRiwayat {
    ABSENSI,
    PENGAJUAN
}

// ==========================================================
// FILTER STATUS PENGAJUAN
// ==========================================================

enum class FilterStatusPengajuan {
    SEMUA,
    MENUNGGU,
    DISETUJUI,
    DITOLAK
}

// ==========================================================
// RIWAYAT SCREEN
// ==========================================================

@Composable
fun RiwayatScreen(
    onBack: () -> Unit,

    onDetailClick: (RiwayatPengajuan) -> Unit,

    filterStatusAwal: FilterStatusPengajuan =
        FilterStatusPengajuan.SEMUA
) {

    // ======================================================
    // FIREBASE
    // ======================================================

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    // ======================================================
    // REPOSITORY PENGAJUAN
    // ======================================================

    val pengajuanRepository =
        remember {
            PengajuanRepository()
        }

    // ======================================================
    // DATA ABSENSI
    // ======================================================

    var semuaRiwayat by remember {
        mutableStateOf(
            emptyList<RiwayatAbsensi>()
        )
    }

    // ======================================================
    // DATA PENGAJUAN
    // ======================================================

    var semuaPengajuan by remember {
        mutableStateOf(
            emptyList<RiwayatPengajuan>()
        )
    }

    // ======================================================
    // LOADING
    // ======================================================

    var loading by remember {
        mutableStateOf(true)
    }

    // ======================================================
    // ERROR
    // ======================================================

    var errorMessage by remember {
        mutableStateOf("")
    }

    // ======================================================
    // TAB
    // ======================================================

    var tabAktif by remember {

        mutableStateOf(
            if (
                filterStatusAwal !=
                FilterStatusPengajuan.SEMUA
            ) {
                TabRiwayat.PENGAJUAN
            } else {
                TabRiwayat.ABSENSI
            }
        )
    }

    // ======================================================
    // FILTER STATUS PENGAJUAN
    // ======================================================

    var filterStatusPengajuan by remember {

        mutableStateOf(
            filterStatusAwal
        )
    }

    // ======================================================
    // FILTER ABSENSI
    // ======================================================

    var filterAktif by remember {

        mutableStateOf(
            FilterRiwayat.TUJUH_HARI
        )
    }

    var dropdownTerbuka by remember {
        mutableStateOf(false)
    }

    // ======================================================
    // LOAD ABSENSI
    // ======================================================

    suspend fun loadAbsensi() {

        val currentUser =
            auth.currentUser
                ?: throw Exception(
                    "User belum login"
                )

        val uid =
            currentUser.uid

        val snapshot =
            db.collection("attendance")
                .whereEqualTo(
                    "uid",
                    uid
                )
                .get()
                .await()

        semuaRiwayat =
            snapshot.documents
                .mapNotNull { document ->

                    val tanggal =
                        document.getString(
                            "tanggal"
                        )
                            ?: return@mapNotNull null

                    val jamMasuk =
                        document.getString(
                            "jamMasuk"
                        )
                            ?: "-"

                    val jamPulang =
                        document.getString(
                            "jamPulang"
                        )
                            ?: "-"

                    val catatan =
                        document.getString(
                            "catatan"
                        )
                            ?: ""

                    RiwayatAbsensi(
                        tanggal =
                            tanggal,

                        jamMasuk =
                            jamMasuk,

                        jamPulang =
                            if (
                                jamPulang.isBlank()
                            ) {
                                "-"
                            } else {
                                jamPulang
                            },

                        catatan =
                            catatan
                    )
                }
                .sortedByDescending {
                    it.tanggal
                }
    }

    // ======================================================
    // LOAD PENGAJUAN STAFF
    // ======================================================

    suspend fun loadPengajuan() {

        val result =
            pengajuanRepository
                .ambilPengajuanSaya()

        result.fold(

            onSuccess = { data ->

                semuaPengajuan =
                    data
                        .map { item ->

                            RiwayatPengajuan(

                                documentId =
                                    item["documentId"]
                                        ?.toString()
                                        ?: "",

                                jenis =
                                    item["jenis"]
                                        ?.toString()
                                        ?: "-",

                                jamPulang =
                                    item["jamPulang"]
                                        ?.toString()
                                        ?: "",

                                jamKeluar =
                                    item["jamKeluar"]
                                        ?.toString()
                                        ?: "",

                                jamKembali =
                                    item["jamKembali"]
                                        ?.toString()
                                        ?: "",

                                tanggalMulai =
                                    item["tanggalMulai"]
                                        ?.toString()
                                        ?: "",

                                tanggalSelesai =
                                    item["tanggalSelesai"]
                                        ?.toString()
                                        ?: "",

                                alasan =
                                    item["alasan"]
                                        ?.toString()
                                        ?: "",

                                status =
                                    item["status"]
                                        ?.toString()
                                        ?: "menunggu",

                                catatanAdmin =
                                    item["catatanAdmin"]
                                        ?.toString()
                                        ?: ""
                            )
                        }
                        .sortedByDescending {
                            it.tanggalMulai
                        }
            },

            onFailure = { error ->
                throw error
            }
        )
    }

    // ======================================================
    // LOAD SEMUA DATA
    // ======================================================

    suspend fun loadSemuaData() {

        try {

            loading =
                true

            errorMessage =
                ""

            val currentUser =
                auth.currentUser

            if (
                currentUser == null
            ) {

                errorMessage =
                    "User belum login"

                loading =
                    false

                return
            }

            loadAbsensi()

            loadPengajuan()

            loading =
                false

        } catch (
            e: Exception
        ) {

            loading =
                false

            errorMessage =
                e.message
                    ?: "Gagal mengambil data"

            e.printStackTrace()
        }
    }

    // ======================================================
    // LOAD SAAT SCREEN DIBUKA
    // ======================================================

    LaunchedEffect(Unit) {
        loadSemuaData()
    }

    // ======================================================
    // FILTER DATA ABSENSI
    // ======================================================

    val daftarRiwayat =
        remember(
            semuaRiwayat,
            filterAktif
        ) {

            val jumlahHari =
                filterAktif.jumlahHari

            if (
                jumlahHari == null
            ) {

                semuaRiwayat

            } else {

                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                formatter.isLenient =
                    false

                val hariIni =
                    Calendar.getInstance()

                hariIni.set(
                    Calendar.HOUR_OF_DAY,
                    0
                )

                hariIni.set(
                    Calendar.MINUTE,
                    0
                )

                hariIni.set(
                    Calendar.SECOND,
                    0
                )

                hariIni.set(
                    Calendar.MILLISECOND,
                    0
                )

                val tanggalAwal =
                    hariIni.clone()
                            as Calendar

                tanggalAwal.add(
                    Calendar.DAY_OF_YEAR,
                    -(jumlahHari - 1)
                )

                val waktuAwal =
                    tanggalAwal.time

                val waktuAkhir =
                    hariIni.time

                semuaRiwayat.filter { riwayat ->

                    try {

                        val tanggalAbsensi =
                            formatter.parse(
                                riwayat.tanggal
                            )

                        tanggalAbsensi != null &&
                                !tanggalAbsensi.before(
                                    waktuAwal
                                ) &&
                                !tanggalAbsensi.after(
                                    waktuAkhir
                                )

                    } catch (
                        e: Exception
                    ) {

                        false
                    }
                }
            }
        }

    // ======================================================
    // FILTER DATA PENGAJUAN
    // ======================================================

    val daftarPengajuanTerfilter =
        remember(
            semuaPengajuan,
            filterStatusPengajuan
        ) {

            if (
                filterStatusPengajuan ==
                FilterStatusPengajuan.SEMUA
            ) {

                semuaPengajuan

            } else {

                semuaPengajuan.filter { pengajuan ->

                    when (
                        filterStatusPengajuan
                    ) {

                        FilterStatusPengajuan.MENUNGGU ->
                            pengajuan.status
                                .trim()
                                .lowercase() ==
                                    "menunggu"

                        FilterStatusPengajuan.DISETUJUI ->
                            pengajuan.status
                                .trim()
                                .lowercase() ==
                                    "disetujui"

                        FilterStatusPengajuan.DITOLAK ->
                            pengajuan.status
                                .trim()
                                .lowercase() ==
                                    "ditolak"

                        FilterStatusPengajuan.SEMUA ->
                            true
                    }
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
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

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
                        Icons.Default.History,

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
                        "Riwayat Absensi",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // ==================================================
            // TAB
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                Color.White,

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                )
                        )
                        .padding(4.dp)
            ) {

                Button(
                    onClick = {

                        tabAktif =
                            TabRiwayat.ABSENSI
                    },

                    modifier =
                        Modifier.weight(1f),

                    shape =
                        RoundedCornerShape(
                            9.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                if (
                                    tabAktif ==
                                    TabRiwayat.ABSENSI
                                ) {
                                    PrimaryGreen
                                } else {
                                    Color.Transparent
                                },

                            contentColor =
                                if (
                                    tabAktif ==
                                    TabRiwayat.ABSENSI
                                ) {
                                    Color.White
                                } else {
                                    TextGray
                                }
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Absensi",

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {

                        tabAktif =
                            TabRiwayat.PENGAJUAN
                    },

                    modifier =
                        Modifier.weight(1f),

                    shape =
                        RoundedCornerShape(
                            9.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                if (
                                    tabAktif ==
                                    TabRiwayat.PENGAJUAN
                                ) {
                                    PrimaryGreen
                                } else {
                                    Color.Transparent
                                },

                            contentColor =
                                if (
                                    tabAktif ==
                                    TabRiwayat.PENGAJUAN
                                ) {
                                    Color.White
                                } else {
                                    TextGray
                                }
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Description,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "Pengajuan",

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // ==================================================
            // TAB ABSENSI
            // ==================================================

            if (
                tabAktif ==
                TabRiwayat.ABSENSI
            ) {

                Text(
                    text =
                        "Filter Riwayat",

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Button(
                    onClick = {

                        dropdownTerbuka =
                            !dropdownTerbuka
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color.White,

                            contentColor =
                                TextDark
                        )
                ) {

                    Text(
                        text =
                            filterAktif.label,

                        modifier =
                            Modifier.weight(1f),

                        textAlign =
                            TextAlign.Start,

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Medium
                    )

                    Icon(
                        imageVector =
                            Icons.Default.KeyboardArrowDown,

                        contentDescription =
                            "Pilih filter"
                    )
                }

                DropdownMenu(
                    expanded =
                        dropdownTerbuka,

                    onDismissRequest = {

                        dropdownTerbuka =
                            false
                    }
                ) {

                    FilterRiwayat
                        .values()
                        .forEach { filter ->

                            DropdownMenuItem(

                                text = {

                                    Text(
                                        text =
                                            filter.label,

                                        fontSize =
                                            14.sp
                                    )
                                },

                                onClick = {

                                    filterAktif =
                                        filter

                                    dropdownTerbuka =
                                        false
                                }
                            )
                        }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // ==================================================
                // ISI ABSENSI
                // ==================================================

                if (loading) {

                    LoadingRiwayat()

                } else if (
                    errorMessage.isNotEmpty()
                ) {

                    ErrorRiwayat(
                        message =
                            errorMessage
                    )

                } else if (
                    daftarRiwayat.isEmpty()
                ) {

                    EmptyRiwayat(
                        message =
                            "Tidak ada absensi dalam " +
                                    filterAktif.label.lowercase()
                    )

                } else {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        items(
                            items =
                                daftarRiwayat,

                            key = {
                                "${it.tanggal}_${it.jamMasuk}_${it.jamPulang}"
                            }
                        ) { riwayat ->

                            RiwayatCard(
                                riwayat =
                                    riwayat
                            )
                        }
                    }
                }

            } else {

                // ==================================================
                // TAB PENGAJUAN STAFF
                // ==================================================

                if (loading) {

                    LoadingRiwayat()

                } else if (
                    errorMessage.isNotEmpty()
                ) {

                    ErrorRiwayat(
                        message =
                            errorMessage
                    )

                } else if (
                    daftarPengajuanTerfilter.isEmpty()
                ) {

                    EmptyPengajuanStaff()

                } else {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        items(
                            items =
                                daftarPengajuanTerfilter,

                            key = {
                                it.documentId
                            }
                        ) { pengajuan ->

                            RiwayatPengajuanStaffCard(

                                pengajuan =
                                    pengajuan,

                                onClick = {

                                    onDetailClick(
                                        pengajuan
                                    )
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
// LOADING
// ==========================================================

@Composable
private fun LoadingRiwayat() {

    Column(
        modifier =
            Modifier.fillMaxSize(),

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
                "Memuat riwayat...",

            color =
                TextGray,

            fontSize =
                13.sp
        )
    }
}

// ==========================================================
// ERROR
// ==========================================================

@Composable
private fun ErrorRiwayat(
    message: String
) {

    Column(
        modifier =
            Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Icon(
            imageVector =
                Icons.Default.Warning,

            contentDescription =
                null,

            tint =
                Color.Red,

            modifier =
                Modifier.size(40.dp)
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                message,

            color =
                Color.Red,

            fontSize =
                14.sp,

            textAlign =
                TextAlign.Center
        )
    }
}

// ==========================================================
// EMPTY ABSENSI
// ==========================================================

@Composable
private fun EmptyRiwayat(
    message: String
) {

    Column(
        modifier =
            Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Icon(
            imageVector =
                Icons.Default.History,

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
                "Tidak ada riwayat",

            fontSize =
                15.sp,

            fontWeight =
                FontWeight.Medium,

            color =
                TextDark
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                message,

            fontSize =
                12.sp,

            color =
                TextGray,

            textAlign =
                TextAlign.Center
        )
    }
}

// ==========================================================
// EMPTY PENGAJUAN STAFF
// ==========================================================

@Composable
private fun EmptyPengajuanStaff() {

    Column(
        modifier =
            Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Surface(
            modifier =
                Modifier.size(64.dp),

            shape =
                CircleShape,

            color =
                Color(0xFFE8F5E9)
        ) {

            Icon(
                imageVector =
                    Icons.Default.Description,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.padding(
                        17.dp
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Text(
            text =
                "Belum Ada Pengajuan",

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
                "Pengajuan yang kamu buat akan\n" +
                        "tampil di halaman ini.",

            fontSize =
                12.sp,

            color =
                TextGray,

            textAlign =
                TextAlign.Center
        )
    }
}

// ==========================================================
// CARD RIWAYAT ABSENSI
// ==========================================================

@Composable
private fun RiwayatCard(
    riwayat: RiwayatAbsensi
) {

    val sudahPulang =
        riwayat.jamPulang.isNotBlank() &&
                riwayat.jamPulang != "-"

    val statusText =
        if (sudahPulang) {
            "HADIR"
        } else {
            "BELUM PULANG"
        }

    val statusColor =
        if (sudahPulang) {
            PrimaryGreen
        } else {
            Color(0xFFD97706)
        }

    val statusBackground =
        if (sudahPulang) {
            Color(0xFFE8F5E9)
        } else {
            Color(0xFFFFF3E0)
        }

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
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(17.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            formatTanggal(
                                riwayat.tanggal
                            ),

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            riwayat.tanggal,

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(
                            20.dp
                        ),

                    color =
                        statusBackground
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                if (sudahPulang) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.Schedule
                                },

                            contentDescription =
                                null,

                            tint =
                                statusColor,

                            modifier =
                                Modifier.size(15.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                statusText,

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                statusColor
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text =
                            "Jam Masuk",

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )

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
                                Icons.Default.AccessTime,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            text =
                                riwayat.jamMasuk,

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )
                    }
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text =
                            "Jam Pulang",

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                riwayat.jamPulang,

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Icon(
                            imageVector =
                                Icons.Default.AccessTime,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (
                riwayat.catatan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Catatan",

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
                        riwayat.catatan,

                    fontSize =
                        13.sp,

                    color =
                        TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        if (sudahPulang) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Schedule
                        },

                    contentDescription =
                        null,

                    tint =
                        if (sudahPulang) {
                            PrimaryGreen
                        } else {
                            Color(0xFFD97706)
                        },

                    modifier =
                        Modifier.size(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text(
                    text =
                        if (sudahPulang) {
                            "Absensi Lengkap"
                        } else {
                            "Belum Absen Pulang"
                        },

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        if (sudahPulang) {
                            PrimaryGreen
                        } else {
                            Color(0xFFD97706)
                        }
                )
            }
        }
    }
}

// ==========================================================
// CARD PENGAJUAN STAFF
// ==========================================================

@Composable
private fun RiwayatPengajuanStaffCard(

    pengajuan: RiwayatPengajuan,

    onClick: () -> Unit

) {

    val statusNormal =
        pengajuan.status
            .trim()
            .lowercase()

    val statusText =
        when (statusNormal) {

            "menunggu" ->
                "MENUNGGU"

            "disetujui" ->
                "DISETUJUI"

            "ditolak" ->
                "DITOLAK"

            else ->
                pengajuan.status.uppercase()
        }

    val statusColor =
        when (statusNormal) {

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                Color(0xFFE67E22)
        }

    val statusBackground =
        when (statusNormal) {

            "disetujui" ->
                Color(0xFFE8F5E9)

            "ditolak" ->
                Color(0xFFFFEBEE)

            else ->
                Color(0xFFFFF3E0)
        }

    Card(
        onClick =
            onClick,

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
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(17.dp)
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(44.dp),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    color =
                        Color(0xFFE8F5E9)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Description,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.padding(
                                10.dp
                            )
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
                            pengajuan.jenis
                                .ifBlank {
                                    "Pengajuan"
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
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (
                                pengajuan.tanggalSelesai
                                    .isNotBlank() &&
                                pengajuan.tanggalSelesai !=
                                pengajuan.tanggalMulai
                            ) {

                                "${pengajuan.tanggalMulai} - " +
                                        pengajuan.tanggalSelesai

                            } else {

                                pengajuan.tanggalMulai
                            },

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(
                            20.dp
                        ),

                    color =
                        statusBackground
                ) {

                    Text(
                        text =
                            statusText,

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),

                        fontSize =
                            10.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            statusColor
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================================
            // DETAIL WAKTU
            // ==================================================

            if (
                pengajuan.jamPulang.isNotBlank() ||
                pengajuan.jamKeluar.isNotBlank() ||
                pengajuan.jamKembali.isNotBlank()
            ) {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    color =
                        Background
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                12.dp
                            )
                    ) {

                        Text(
                            text =
                                "Detail Waktu",

                            fontSize =
                                11.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                TextGray
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        if (
                            pengajuan.jamPulang.isNotBlank()
                        ) {

                            PengajuanDetailRow(
                                label =
                                    "Jam Pulang",

                                value =
                                    pengajuan.jamPulang
                            )
                        }

                        if (
                            pengajuan.jamKeluar.isNotBlank()
                        ) {

                            PengajuanDetailRow(
                                label =
                                    "Jam Keluar",

                                value =
                                    pengajuan.jamKeluar
                            )
                        }

                        if (
                            pengajuan.jamKembali.isNotBlank()
                        ) {

                            PengajuanDetailRow(
                                label =
                                    "Jam Kembali",

                                value =
                                    pengajuan.jamKembali
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )
            }

            // ==================================================
            // ALASAN
            // ==================================================

            if (
                pengajuan.alasan.isNotBlank()
            ) {

                Text(
                    text =
                        "Alasan",

                    fontSize =
                        11.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        pengajuan.alasan,

                    fontSize =
                        13.sp,

                    color =
                        TextDark
                )
            }

            // ==================================================
            // CATATAN ADMIN
            // ==================================================

            if (
                pengajuan.catatanAdmin.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    color =
                        Color(0xFFF5F5F5)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                12.dp
                            )
                    ) {

                        Text(
                            text =
                                "Catatan Admin",

                            fontSize =
                                11.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                TextGray
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                pengajuan.catatanAdmin,

                            fontSize =
                                13.sp,

                            color =
                                TextDark
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// DETAIL ROW PENGAJUAN
// ==========================================================

@Composable
private fun PengajuanDetailRow(

    label: String,

    value: String

) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text =
                label,

            fontSize =
                12.sp,

            color =
                TextGray
        )

        Text(
            text =
                value,

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                TextDark
        )
    }

    Spacer(
        modifier =
            Modifier.height(5.dp)
    )
}

// ==========================================================
// FORMAT TANGGAL
// ==========================================================

private fun formatTanggal(
    tanggal: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        input.isLenient =
            false

        val output =
            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale(
                    "id",
                    "ID"
                )
            )

        val date =
            input.parse(
                tanggal
            )

        if (date != null) {

            output.format(
                date
            )

        } else {

            tanggal
        }

    } catch (
        e: Exception
    ) {

        tanggal
    }
}