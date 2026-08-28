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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Description
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
// RIWAYAT SCREEN
// ==========================================================

@Composable
fun RiwayatScreen(
    onBack: () -> Unit
) {

    // ======================================================
    // FIREBASE
    // ======================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    // ======================================================
    // STATE DATA
    // ======================================================

    var semuaRiwayat by remember {
        mutableStateOf<List<RiwayatAbsensi>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    // ======================================================
    // STATE TAB
    // ======================================================

    var tabAktif by remember {
        mutableStateOf(
            TabRiwayat.ABSENSI
        )
    }

    // ======================================================
    // STATE FILTER
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
    // FUNGSI LOAD DATA FIRESTORE
    // ======================================================

    suspend fun loadRiwayat() {

        try {

            loading = true
            errorMessage = ""

            val currentUser =
                auth.currentUser

            if (currentUser == null) {

                errorMessage =
                    "User belum login"

                loading = false

                return
            }

            val uid =
                currentUser.uid

            // ==================================================
            // AMBIL DATA ATTENDANCE
            // ==================================================

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
                                jamPulang,

                            catatan =
                                catatan
                        )
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            loading = false

        } catch (e: Exception) {

            loading = false

            errorMessage =
                e.message
                    ?: "Gagal mengambil riwayat"

            e.printStackTrace()
        }
    }

    // ======================================================
    // LOAD SAAT SCREEN DIBUKA
    // ======================================================

    LaunchedEffect(Unit) {

        loadRiwayat()
    }

    // ======================================================
    // FILTER DATA
    // ======================================================

    val daftarRiwayat =
        remember(
            semuaRiwayat,
            filterAktif
        ) {

            if (
                filterAktif.jumlahHari == null
            ) {

                semuaRiwayat

            } else {

                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                formatter.isLenient = false

                val calendar =
                    Calendar.getInstance()

                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    0
                )

                calendar.set(
                    Calendar.MINUTE,
                    0
                )

                calendar.set(
                    Calendar.SECOND,
                    0
                )

                calendar.set(
                    Calendar.MILLISECOND,
                    0
                )

                val tanggalHariIni =
                    calendar.time

                calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -(filterAktif.jumlahHari!! - 1)
                )

                val tanggalAwal =
                    calendar.time

                semuaRiwayat.filter { riwayat ->

                    try {

                        val tanggalAbsensi =
                            formatter.parse(
                                riwayat.tanggal
                            )

                        tanggalAbsensi != null &&
                                !tanggalAbsensi.before(
                                    tanggalAwal
                                ) &&
                                !tanggalAbsensi.after(
                                    tanggalHariIni
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

            // HEADER
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

            // TAB ABSENSI / PENGAJUAN
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

                    FilterRiwayat.values()
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

                if (loading) {

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

                } else if (
                    errorMessage.isNotEmpty()
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
                                Color.Red
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                errorMessage,

                            color =
                                Color.Red,

                            fontSize =
                                14.sp
                        )
                    }

                } else if (
                    daftarRiwayat.isEmpty()
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
                                TextGray
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
                                "Tidak ada absensi dalam " +
                                        filterAktif.label.lowercase(),

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )
                    }

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
                                daftarRiwayat
                        ) { riwayat ->

                            RiwayatCard(
                                riwayat =
                                    riwayat
                            )
                        }
                    }
                }

            } else {

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
                            Icons.Default.Description,

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
                            "Belum ada pengajuan",

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
                            "Riwayat pengajuan akan tampil di sini",

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
    }
}

// ==========================================================
// CARD RIWAYAT ABSENSI
// ==========================================================

@Composable
private fun RiwayatCard(
    riwayat: RiwayatAbsensi
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

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.height(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Text(
                        text =
                            "ABSENSI",

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            PrimaryGreen
                    )
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
                                if (
                                    riwayat.jamPulang
                                        .isBlank()
                                ) {
                                    "-"
                                } else {
                                    riwayat.jamPulang
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

                val lengkap =
                    riwayat.jamPulang.isNotBlank() &&
                            riwayat.jamPulang != "-"

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription =
                        null,

                    tint =
                        if (lengkap) {
                            PrimaryGreen
                        } else {
                            Color.Red
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
                        if (lengkap) {
                            "Absensi Lengkap"
                        } else {
                            "Belum Absen Pulang"
                        },

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        if (lengkap) {
                            PrimaryGreen
                        } else {
                            Color.Red
                        }
                )
            }
        }
    }
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

        input.isLenient = false

        val output =
            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale(
                    "id",
                    "ID"
                )
            )

        val date =
            input.parse(tanggal)

        if (date != null) {

            output.format(date)

        } else {

            tanggal
        }

    } catch (
        e: Exception
    ) {

        tanggal
    }
}