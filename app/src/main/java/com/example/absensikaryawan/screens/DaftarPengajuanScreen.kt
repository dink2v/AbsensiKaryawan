package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Pending

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.PengajuanRepository


// ==========================================================
// DAFTAR PENGAJUAN SCREEN
// ==========================================================

@Composable
fun DaftarPengajuanScreen(

    statusFilter: String,

    onBack: () -> Unit,

    onPengajuanClick: (Map<String, Any>) -> Unit

) {

    // ==========================================================
    // REPOSITORY
    // ==========================================================

    val repository =
        remember {
            PengajuanRepository()
        }


    // ==========================================================
    // STATE
    // ==========================================================

    var daftarPengajuan by remember {

        mutableStateOf(
            emptyList<Map<String, Any>>()
        )
    }


    var sedangMemuat by remember {

        mutableStateOf(true)
    }


    // ==========================================================
    // AMBIL DATA FIRESTORE
    // ==========================================================

    LaunchedEffect(Unit) {

        sedangMemuat = true

        val result =
            repository.ambilPengajuanSaya()

        result.onSuccess { data ->

            daftarPengajuan =
                data
        }

        sedangMemuat = false
    }


    // ==========================================================
    // FILTER DATA
    // ==========================================================

    val daftarTerfilter =

        if (
            statusFilter.lowercase() == "semua"
        ) {

            daftarPengajuan

        } else {

            daftarPengajuan.filter { pengajuan ->

                pengajuan["status"]
                    ?.toString()
                    ?.lowercase()
                    ?.trim() ==
                        statusFilter
                            .lowercase()
                            .trim()
            }
        }


    // ==========================================================
    // JUDUL HALAMAN
    // ==========================================================

    val judulHalaman =

        when (
            statusFilter.lowercase()
        ) {

            "menunggu" ->
                "Menunggu Persetujuan"

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Total Pengajuan"
        }


    // ==========================================================
    // DESKRIPSI HALAMAN
    // ==========================================================

    val deskripsiHalaman =

        when (
            statusFilter.lowercase()
        ) {

            "menunggu" ->
                "Pengajuan yang sedang diperiksa admin."

            "disetujui" ->
                "Pengajuan yang telah disetujui admin."

            "ditolak" ->
                "Pengajuan yang ditolak admin."

            else ->
                "Seluruh riwayat pengajuan kamu."
        }


    // ==========================================================
    // ICON HALAMAN
    // ==========================================================

    val iconHalaman: ImageVector =

        when (
            statusFilter.lowercase()
        ) {

            "menunggu" ->
                Icons.Default.Pending

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Cancel

            else ->
                Icons.Default.EventNote
        }


    // ==========================================================
    // WARNA ICON
    // ==========================================================

    val warnaIcon =

        when (
            statusFilter.lowercase()
        ) {

            "menunggu" ->
                Color(0xFFD97706)

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                PrimaryGreen
        }


    // ==========================================================
    // MAIN SCREEN
    // ==========================================================

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
                            TextDark
                    )
                }


                Column(

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text(

                        text =
                            judulHalaman,

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            deskripsiHalaman,

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                Surface(

                    modifier =
                        Modifier.size(42.dp),

                    shape =
                        RoundedCornerShape(13.dp),

                    color =
                        SoftGreen

                ) {

                    Icon(

                        imageVector =
                            iconHalaman,

                        contentDescription =
                            judulHalaman,

                        tint =
                            warnaIcon,

                        modifier =
                            Modifier
                                .padding(9.dp)
                                .size(24.dp)
                    )
                }
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
                        )

            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                // ==================================================
                // INFO JUMLAH
                // ==================================================

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(16.dp),

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

                        Column(

                            modifier =
                                Modifier.weight(1f)

                        ) {

                            Text(

                                text =
                                    "Jumlah Pengajuan",

                                fontSize =
                                    13.sp,

                                color =
                                    TextGray
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )


                            Text(

                                text =
                                    daftarTerfilter
                                        .size
                                        .toString(),

                                fontSize =
                                    25.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    PrimaryGreen
                            )
                        }


                        Icon(

                            imageVector =
                                iconHalaman,

                            contentDescription =
                                null,

                            tint =
                                warnaIcon,

                            modifier =
                                Modifier.size(32.dp)
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                // ==================================================
                // LOADING
                // ==================================================

                if (sedangMemuat) {

                    Column(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 40.dp
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
                                "Memuat pengajuan...",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )
                    }

                } else {

                    // ==================================================
                    // TIDAK ADA DATA
                    // ==================================================

                    if (
                        daftarTerfilter.isEmpty()
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
                                )

                        ) {

                            Column(

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally

                            ) {

                                Icon(

                                    imageVector =
                                        iconHalaman,

                                    contentDescription =
                                        null,

                                    tint =
                                        warnaIcon,

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
                                        15.sp,

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

                                        when (
                                            statusFilter.lowercase()
                                        ) {

                                            "menunggu" ->
                                                "Belum ada pengajuan yang menunggu persetujuan."

                                            "disetujui" ->
                                                "Belum ada pengajuan yang disetujui."

                                            "ditolak" ->
                                                "Belum ada pengajuan yang ditolak."

                                            else ->
                                                "Kamu belum memiliki pengajuan."
                                        },

                                    fontSize =
                                        12.sp,

                                    color =
                                        TextGray
                                )
                            }
                        }

                    } else {

                        // ==================================================
                        // DAFTAR PENGAJUAN
                        // ==================================================

                        daftarTerfilter
                            .reversed()
                            .forEach { pengajuan ->

                                DaftarPengajuanItem(

                                    pengajuan =
                                        pengajuan,

                                    onClick = {

                                        onPengajuanClick(
                                            pengajuan
                                        )
                                    }
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(10.dp)
                                )
                            }
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
// ITEM PENGAJUAN
// ==========================================================

@Composable
private fun DaftarPengajuanItem(

    pengajuan: Map<String, Any>,

    onClick: () -> Unit

) {

    val jenis =

        pengajuan["jenis"]
            ?.toString()
            ?.ifBlank {
                "Pengajuan"
            }
            ?: "Pengajuan"


    val tanggalMulai =

        pengajuan["tanggalMulai"]
            ?.toString()
            ?: ""


    val tanggalSelesai =

        pengajuan["tanggalSelesai"]
            ?.toString()
            ?: ""


    val status =

        pengajuan["status"]
            ?.toString()
            ?.lowercase()
            ?.trim()
            ?: "menunggu"


    // ==========================================================
    // STATUS TEXT
    // ==========================================================

    val statusText =

        when (status) {

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Menunggu"
        }


    // ==========================================================
    // STATUS ICON
    // ==========================================================

    val statusIcon =

        when (status) {

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Cancel

            else ->
                Icons.Default.Pending
        }


    // ==========================================================
    // STATUS COLOR
    // ==========================================================

    val statusColor =

        when (status) {

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                Color(0xFFD97706)
        }


    // ==========================================================
    // TANGGAL
    // ==========================================================

    val tanggalText =

        when {

            tanggalMulai.isNotBlank() &&
                    tanggalSelesai.isNotBlank() &&
                    tanggalMulai != tanggalSelesai ->

                "$tanggalMulai - $tanggalSelesai"

            tanggalMulai.isNotBlank() ->
                tanggalMulai

            tanggalSelesai.isNotBlank() ->
                tanggalSelesai

            else ->
                "Tanggal tidak tersedia"
        }


    // ==========================================================
    // CARD
    // ==========================================================

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                },

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

            // ==================================================
            // ICON
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .size(48.dp)
                        .background(

                            color =
                                SoftGreen,

                            shape =
                                RoundedCornerShape(14.dp)
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Icon(

                    imageVector =
                        statusIcon,

                    contentDescription =
                        statusText,

                    tint =
                        statusColor,

                    modifier =
                        Modifier.size(26.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            // ==================================================
            // INFORMASI
            // ==================================================

            Column(

                modifier =
                    Modifier.weight(1f)

            ) {

                Text(

                    text =
                        jenis,

                    fontSize =
                        15.sp,

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
                        tanggalText,

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )


                // ==================================================
                // JAM PULANG
                // ==================================================

                val jamPulang =

                    pengajuan["jamPulang"]
                        ?.toString()
                        ?: ""


                if (
                    jamPulang.isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(

                        text =
                            "Jam pulang: $jamPulang",

                        fontSize =
                            11.sp,

                        color =
                            TextGray
                    )
                }
            }


            // ==================================================
            // STATUS
            // ==================================================

            Text(

                text =
                    statusText,

                fontSize =
                    11.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    statusColor
            )
        }
    }
}