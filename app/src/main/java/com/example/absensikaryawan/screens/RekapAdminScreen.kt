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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// ==========================================================
// MODEL REKAP ABSENSI
// ==========================================================

data class RekapAbsensi(

    val uid: String,

    val nama: String,

    val tanggal: String,

    val jamMasuk: String,

    val jamPulang: String,

    val catatan: String
)


// ==========================================================
// FILTER REKAP
// ==========================================================

enum class FilterRekap(

    val label: String,

    val jumlahHari: Int?

) {

    HARI_INI(
        label = "Hari Ini",
        jumlahHari = 1
    ),

    TUJUH_HARI(
        label = "7 Hari Terakhir",
        jumlahHari = 7
    ),

    TIGA_PULUH_HARI(
        label = "30 Hari Terakhir",
        jumlahHari = 30
    ),

    SEMUA(
        label = "Semua Rekap",
        jumlahHari = null
    )
}


// ==========================================================
// REKAP ADMIN SCREEN
// ==========================================================

@Composable
fun RekapAdminScreen(

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

    var semuaRekap by remember {

        mutableStateOf<List<RekapAbsensi>>(
            emptyList()
        )
    }


    var loading by remember {

        mutableStateOf(true)
    }


    var errorMessage by remember {

        mutableStateOf("")
    }


    // ======================================================
    // FILTER
    // ======================================================

    var filterAktif by remember {

        mutableStateOf(
            FilterRekap.HARI_INI
        )
    }


    var dropdownTerbuka by remember {

        mutableStateOf(false)
    }


    // ======================================================
    // LOAD FIRESTORE
    // ======================================================

    suspend fun loadRekap() {

        try {

            loading = true

            errorMessage = ""


            val snapshot =
                db.collection("attendance")
                    .get()
                    .await()


            semuaRekap =
                snapshot.documents
                    .mapNotNull { document ->

                        val tanggal =
                            document.getString(
                                "tanggal"
                            )
                                ?: return@mapNotNull null


                        val nama =
                            document.getString(
                                "nama"
                            )
                                ?: "Karyawan"


                        val uid =
                            document.getString(
                                "uid"
                            )
                                ?: ""


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


                        RekapAbsensi(

                            uid =
                                uid,

                            nama =
                                nama,

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
                    .sortedWith(

                        compareByDescending<RekapAbsensi> {

                            it.tanggal

                        }.thenBy {

                            it.nama
                        }
                    )


            loading = false

        } catch (e: Exception) {

            loading = false

            errorMessage =
                e.message
                    ?: "Gagal mengambil data rekap"

            e.printStackTrace()
        }
    }


    // ======================================================
    // LOAD SAAT SCREEN DIBUKA
    // ======================================================

    LaunchedEffect(Unit) {

        loadRekap()
    }


    // ======================================================
    // FILTER DATA
    // ======================================================

    val daftarRekap = remember(

        semuaRekap,

        filterAktif

    ) {

        if (
            filterAktif.jumlahHari == null
        ) {

            semuaRekap

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


            val hariIni =
                calendar.time


            calendar.add(
                Calendar.DAY_OF_YEAR,
                -(filterAktif.jumlahHari!! - 1)
            )


            val tanggalAwal =
                calendar.time


            semuaRekap.filter { data ->

                try {

                    val tanggal =
                        formatter.parse(
                            data.tanggal
                        )


                    tanggal != null &&
                            !tanggal.before(
                                tanggalAwal
                            ) &&
                            !tanggal.after(
                                hariIni
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
    // STATISTIK
    // ======================================================

    val total =
        daftarRekap.size


    val hadir =
        daftarRekap.count {

            it.jamMasuk.isNotBlank() &&
                    it.jamMasuk != "-"
        }


    val belumPulang =
        daftarRekap.count {

            it.jamMasuk.isNotBlank() &&
                    it.jamMasuk != "-" &&
                    (
                            it.jamPulang.isBlank() ||
                                    it.jamPulang == "-"
                            )
        }


    val lengkap =
        daftarRekap.count {

            it.jamMasuk.isNotBlank() &&
                    it.jamMasuk != "-" &&
                    it.jamPulang.isNotBlank() &&
                    it.jamPulang != "-"
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
                            horizontal = 20.dp,
                            vertical = 14.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Kembali",

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier
                            .size(28.dp)
                            .background(
                                Color.Transparent
                            )
                )


                androidx.compose.foundation.layout.Box(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .background(
                                Color.Transparent
                            )
                            .padding(0.dp)
                            .then(
                                Modifier
                            )
                    // area klik sengaja
                ) {

                    androidx.compose.material3.IconButton(

                        onClick =
                            onBack,

                        modifier =
                            Modifier.fillMaxSize()
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
                }


                Spacer(
                    modifier =
                        Modifier.width(2.dp)
                )


                Icon(

                    imageVector =
                        Icons.Default.Assessment,

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
                        "Rekap Absensi",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }


            // ==================================================
            // CONTENT
            // ==================================================

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
                            "Memuat rekap...",

                        fontSize =
                            13.sp,

                        color =
                            TextGray
                    )
                }

            } else if (
                errorMessage.isNotBlank()
            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),

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
                            Modifier.size(42.dp)
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

                        textAlign =
                            TextAlign.Center,

                        fontSize =
                            13.sp
                    )
                }

            } else {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        androidx.compose.foundation.layout
                            .PaddingValues(

                                start = 20.dp,

                                top = 4.dp,

                                end = 20.dp,

                                bottom = 24.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    // ==================================================
                    // FILTER
                    // ==================================================

                    item {

                        Text(

                            text =
                                "Periode Rekap",

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
                                    "Pilih periode"
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

                            FilterRekap.values()
                                .forEach { filter ->

                                    DropdownMenuItem(

                                        text = {

                                            Text(
                                                text =
                                                    filter.label
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
                    }


                    // ==================================================
                    // STATISTIK
                    // ==================================================

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )


                        Text(

                            text =
                                "Ringkasan",

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            RekapSummaryCard(

                                modifier =
                                    Modifier.weight(1f),

                                number =
                                    total.toString(),

                                label =
                                    "Total",

                                icon =
                                    Icons.Default.People
                            )


                            RekapSummaryCard(

                                modifier =
                                    Modifier.weight(1f),

                                number =
                                    hadir.toString(),

                                label =
                                    "Hadir",

                                icon =
                                    Icons.Default.CheckCircle
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            RekapSummaryCard(

                                modifier =
                                    Modifier.weight(1f),

                                number =
                                    lengkap.toString(),

                                label =
                                    "Lengkap",

                                icon =
                                    Icons.Default.EventAvailable
                            )


                            RekapSummaryCard(

                                modifier =
                                    Modifier.weight(1f),

                                number =
                                    belumPulang.toString(),

                                label =
                                    "Belum Pulang",

                                icon =
                                    Icons.Default.AccessTime
                            )
                        }
                    }


                    // ==================================================
                    // JUDUL DAFTAR
                    // ==================================================

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )


                        Text(

                            text =
                                "Daftar Absensi",

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )
                    }


                    // ==================================================
                    // DATA KOSONG
                    // ==================================================

                    if (
                        daftarRekap.isEmpty()
                    ) {

                        item {

                            Column(

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 50.dp
                                        ),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.Assessment,

                                    contentDescription =
                                        null,

                                    tint =
                                        TextGray,

                                    modifier =
                                        Modifier.size(44.dp)
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
                                )


                                Text(

                                    text =
                                        "Belum ada data absensi",

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        TextDark
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )


                                Text(

                                    text =
                                        "Tidak ada absensi pada periode ini",

                                    fontSize =
                                        12.sp,

                                    color =
                                        TextGray
                                )
                            }
                        }

                    } else {

                        // ==================================================
                        // DAFTAR
                        // ==================================================

                        items(

                            items =
                                daftarRekap
                        ) { rekap ->

                            RekapAbsensiCard(

                                rekap =
                                    rekap
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
private fun RekapSummaryCard(

    modifier: Modifier,

    number: String,

    label: String,

    icon: androidx.compose.ui.graphics.vector.ImageVector

) {

    Card(

        modifier =
            modifier,

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

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    label,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(22.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    number,

                fontSize =
                    25.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Text(

                text =
                    label,

                fontSize =
                    12.sp,

                color =
                    TextGray
            )
        }
    }
}


// ==========================================================
// REKAP CARD
// ==========================================================

@Composable
private fun RekapAbsensiCard(

    rekap: RekapAbsensi

) {

    val lengkap =
        rekap.jamMasuk.isNotBlank() &&
                rekap.jamMasuk != "-" &&
                rekap.jamPulang.isNotBlank() &&
                rekap.jamPulang != "-"


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

            // ==================================================
            // HEADER
            // ==================================================

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
                            rekap.nama,

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
                            formatTanggalRekap(
                                rekap.tanggal
                            ),

                        fontSize =
                            12.sp,

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
                            if (lengkap) {

                                Icons.Default.CheckCircle

                            } else {

                                Icons.Default.AccessTime
                            },

                        contentDescription =
                            null,

                        tint =
                            if (lengkap) {

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
                            if (lengkap) {

                                "Lengkap"

                            } else {

                                "Belum Pulang"
                            },

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            if (lengkap) {

                                PrimaryGreen

                            } else {

                                Color(0xFFD97706)
                            }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // JAM
            // ==================================================

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
                                if (
                                    rekap.jamMasuk.isBlank()
                                ) {

                                    "-"

                                } else {

                                    rekap.jamMasuk
                                },

                            fontSize =
                                14.sp,

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


                    Text(

                        text =
                            if (
                                rekap.jamPulang.isBlank()
                            ) {

                                "-"

                            } else {

                                rekap.jamPulang
                            },

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )
                }
            }


            // ==================================================
            // CATATAN
            // ==================================================

            if (
                rekap.catatan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
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
                        rekap.catatan,

                    fontSize =
                        13.sp,

                    color =
                        TextDark
                )
            }
        }
    }
}


// ==========================================================
// FORMAT TANGGAL
// ==========================================================

private fun formatTanggalRekap(

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