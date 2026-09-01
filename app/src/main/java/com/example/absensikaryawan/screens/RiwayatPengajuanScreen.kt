package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Schedule

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
// RIWAYAT PENGAJUAN SCREEN
// ==========================================================

@Composable
fun RiwayatPengajuanScreen(

    filterStatus: String,

    onBack: () -> Unit,

    onDetailClick: (Map<String, Any>) -> Unit

) {

    // ======================================================
    // REPOSITORY
    // ======================================================

    val repository =
        remember {
            PengajuanRepository()
        }


    // ======================================================
    // STATE
    // ======================================================

    var daftarPengajuan by remember {

        mutableStateOf(
            emptyList<Map<String, Any>>()
        )
    }


    var sedangMemuat by remember {

        mutableStateOf(true)
    }


    // ======================================================
    // AMBIL DATA FIRESTORE
    // ======================================================

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


    // ======================================================
    // NORMALISASI FILTER
    // ======================================================

    val filter =
        filterStatus
            .trim()
            .lowercase()


    // ======================================================
    // FILTER DATA
    // ======================================================

    val daftarFiltered =

        when (filter) {

            "menunggu" -> {

                daftarPengajuan.filter {

                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() ==
                            "menunggu"
                }
            }

            "disetujui" -> {

                daftarPengajuan.filter {

                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() ==
                            "disetujui"
                }
            }

            "ditolak" -> {

                daftarPengajuan.filter {

                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() ==
                            "ditolak"
                }
            }

            else -> {

                daftarPengajuan
            }
        }


    // ======================================================
    // JUDUL HALAMAN
    // ======================================================

    val judulHalaman =

        when (filter) {

            "menunggu" ->
                "Menunggu Persetujuan"

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Riwayat Pengajuan"
        }


    // ======================================================
    // SUB JUDUL
    // ======================================================

    val subJudul =

        when (filter) {

            "menunggu" ->
                "Pengajuan yang sedang menunggu persetujuan."

            "disetujui" ->
                "Pengajuan yang telah disetujui admin."

            "ditolak" ->
                "Pengajuan yang telah ditolak admin."

            else ->
                "Riwayat seluruh pengajuan kamu."
        }


    // ======================================================
    // MAIN
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
                            22.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            subJudul,

                        fontSize =
                            12.sp,

                        color =
                            TextGray
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
                        Modifier.height(8.dp)
                )


                // ==================================================
                // JUDUL SECTION
                // ==================================================

                Text(

                    text =
                        "Riwayat Pengajuan",

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                // ==================================================
                // RINGKASAN
                // ==================================================

                RingkasanPengajuanCard(

                    jumlah =
                        daftarFiltered.size,

                    filter =
                        filter
                )


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
                                    vertical = 30.dp
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
                                "Memuat riwayat pengajuan...",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )
                    }

                } else {

                    // ==================================================
                    // DATA KOSONG
                    // ==================================================

                    if (daftarFiltered.isEmpty()) {

                        DataKosongPengajuan(

                            filter =
                                filter
                        )

                    } else {

                        // ==================================================
                        // LIST DATA
                        // ==================================================

                        daftarFiltered
                            .reversed()
                            .forEach { pengajuan ->

                                RiwayatPengajuanItem(

                                    pengajuan =
                                        pengajuan,

                                    onClick = {

                                        onDetailClick(
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
// RINGKASAN PENGAJUAN
// ==========================================================

@Composable
private fun RingkasanPengajuanCard(

    jumlah: Int,

    filter: String

) {

    val icon =

        when (filter) {

            "menunggu" ->
                Icons.Default.Pending

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Cancel

            else ->
                Icons.Default.Description
        }


    val warna =

        when (filter) {

            "menunggu" ->
                Color(0xFFD97706)

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                PrimaryGreen
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

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Surface(

                modifier =
                    Modifier.size(48.dp),

                shape =
                    RoundedCornerShape(14.dp),

                color =
                    warna.copy(
                        alpha = 0.10f
                    )

            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    tint =
                        warna,

                    modifier =
                        Modifier
                            .padding(10.dp)
                            .size(28.dp)
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
                        "Jumlah Pengajuan",

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
                        jumlah.toString(),

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        warna
                )
            }
        }
    }
}


// ==========================================================
// DATA KOSONG
// ==========================================================

@Composable
private fun DataKosongPengajuan(

    filter: String

) {

    val pesan =

        when (filter) {

            "menunggu" ->
                "Belum ada pengajuan yang menunggu."

            "disetujui" ->
                "Belum ada pengajuan yang disetujui."

            "ditolak" ->
                "Belum ada pengajuan yang ditolak."

            else ->
                "Belum ada riwayat pengajuan."
        }


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

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Icon(

                imageVector =
                    Icons.Default.Description,

                contentDescription =
                    null,

                tint =
                    TextGray,

                modifier =
                    Modifier.size(40.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(

                text =
                    pesan,

                fontSize =
                    13.sp,

                color =
                    TextGray
            )
        }
    }
}


// ==========================================================
// RIWAYAT PENGAJUAN ITEM
// ==========================================================

@Composable
private fun RiwayatPengajuanItem(

    pengajuan: Map<String, Any>,

    onClick: () -> Unit

) {

    val jenis =

        pengajuan["jenis"]
            ?.toString()
            ?.ifEmpty {
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


    val tanggal =

        when {

            tanggalMulai.isNotEmpty() &&
                    tanggalSelesai.isNotEmpty() ->

                "$tanggalMulai - $tanggalSelesai"

            tanggalMulai.isNotEmpty() ->

                tanggalMulai

            else ->

                pengajuan["tanggal"]
                    ?.toString()
                    ?: "-"
        }


    val status =

        pengajuan["status"]
            ?.toString()
            ?.trim()
            ?.lowercase()
            ?: "menunggu"


    val statusText =

        when (status) {

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Menunggu"
        }


    val statusIcon =

        when (status) {

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Cancel

            else ->
                Icons.Default.Pending
        }


    val statusColor =

        when (status) {

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                Color(0xFFD97706)
        }


    val alasan =

        pengajuan["alasan"]
            ?.toString()
            ?: ""


    val jamPulang =

        pengajuan["jamPulang"]
            ?.toString()
            ?: ""


    val jamKeluar =

        pengajuan["jamKeluar"]
            ?.toString()
            ?: ""


    val jamKembali =

        pengajuan["jamKembali"]
            ?.toString()
            ?: ""


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

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

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

                Row(

                    modifier =
                        Modifier
                            .size(46.dp)
                            .background(

                                color =
                                    statusColor.copy(
                                        alpha = 0.10f
                                    ),

                                shape =
                                    RoundedCornerShape(13.dp)
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
                            Modifier.size(25.dp)
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
                            Modifier.height(3.dp)
                    )


                    Row(

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Event,

                            contentDescription =
                                null,

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(14.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )


                        Text(

                            text =
                                tanggal,

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }
                }


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


            // ==================================================
            // DETAIL WAKTU
            // ==================================================

            if (
                jamPulang.isNotEmpty() ||
                jamKeluar.isNotEmpty() ||
                jamKembali.isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )

                ) {

                    if (jamPulang.isNotEmpty()) {

                        InfoMiniItem(

                            icon =
                                Icons.Default.Schedule,

                            title =
                                "Pulang",

                            value =
                                jamPulang
                        )
                    }


                    if (jamKeluar.isNotEmpty()) {

                        InfoMiniItem(

                            icon =
                                Icons.Default.Schedule,

                            title =
                                "Keluar",

                            value =
                                jamKeluar
                        )
                    }


                    if (jamKembali.isNotEmpty()) {

                        InfoMiniItem(

                            icon =
                                Icons.Default.Schedule,

                            title =
                                "Kembali",

                            value =
                                jamKembali
                        )
                    }
                }
            }


            // ==================================================
            // ALASAN
            // ==================================================

            if (alasan.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Text(

                    text =
                        alasan,

                    fontSize =
                        12.sp,

                    color =
                        TextGray,

                    maxLines =
                        2
                )
            }
        }
    }
}


// ==========================================================
// MINI INFO
// ==========================================================

@Composable
private fun InfoMiniItem(

    icon: ImageVector,

    title: String,

    value: String

) {

    Surface(

        shape =
            RoundedCornerShape(10.dp),

        color =
            SoftGreen

    ) {

        Row(

            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(14.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )


            Column {

                Text(

                    text =
                        title,

                    fontSize =
                        9.sp,

                    color =
                        TextGray
                )


                Text(

                    text =
                        value,

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }
        }
    }
}