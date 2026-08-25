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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AccessTime
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.PengajuanData
import kotlinx.coroutines.launch

// ==========================================================
// DATA MODEL PENGAJUAN
// ==========================================================

data class PengajuanData(
    val id: String,
    val nama: String,
    val jenis: String,
    val tanggal: String,
    val jamPulang: String,
    val jamKeluar: String,
    val jamKembali: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val alasan: String,
    val status: String
)


// ==========================================================
// APPROVAL SCREEN
// ==========================================================

@Composable
fun ApprovalScreen(
    onBack: () -> Unit
) {

    val repository = remember {
        FirestoreRepository()
    }

    // SCOPE HARUS DI DALAM @Composable
    val scope = rememberCoroutineScope()

    var daftarPengajuan by remember {
        mutableStateOf<List<PengajuanData>>(emptyList())
    }

    var sedangMemuat by remember {
        mutableStateOf(true)
    }

    // ======================================================
    // LOAD DATA FIRESTORE
    // ======================================================

    fun loadPengajuan() {

        scope.launch {

            sedangMemuat = true

            val hasil =
                repository.getPengajuanMenunggu()

            if (hasil.isSuccess) {

                daftarPengajuan =
                    hasil.getOrNull()
                        ?: emptyList()
            }

            sedangMemuat = false
        }
    }

    // Load pertama kali
    LaunchedEffect(Unit) {
        loadPengajuan()
    }

    // ======================================================
    // UI
    // ======================================================

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

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                    modifier = Modifier.width(4.dp)
                )

                Column {

                    Text(
                        text = "Admin / HRD",
                        fontSize = 13.sp,
                        color = TextGray
                    )

                    Text(
                        text = "Approval Pengajuan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==================================================
            // RINGKASAN
            // ==================================================

            Text(
                text = "Pengajuan Menunggu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                ApprovalSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    number =
                        daftarPengajuan.size.toString(),

                    label =
                        "Menunggu",

                    icon =
                        Icons.Default.Schedule
                )

                ApprovalSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    number =
                        "-",

                    label =
                        "Disetujui",

                    icon =
                        Icons.Default.CheckCircle
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==================================================
            // JUDUL DAFTAR
            // ==================================================

            Text(
                text = "Daftar Pengajuan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ==================================================
            // LOADING
            // ==================================================

            if (sedangMemuat) {

                Text(
                    text = "Memuat pengajuan...",
                    fontSize = 14.sp,
                    color = TextGray,
                    modifier = Modifier.padding(
                        vertical = 20.dp
                    )
                )
            }

            // ==================================================
            // TIDAK ADA DATA
            // ==================================================

            else if (daftarPengajuan.isEmpty()) {

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
                                .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Group,

                            contentDescription =
                                null,

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(42.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
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
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Pengajuan dari staff akan muncul di sini.",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )
                    }
                }
            }

            // ==================================================
            // DAFTAR DATA FIRESTORE
            // ==================================================

            else {

                daftarPengajuan.forEach { pengajuan ->

                    ApprovalRequestCard(

                        pengajuan =
                            pengajuan,

                        onApprove = {

                            scope.launch {

                                val hasil =
                                    repository.updateStatusPengajuan(
                                        documentId =
                                            pengajuan.id,

                                        status =
                                            "disetujui"
                                    )

                                if (hasil.isSuccess) {

                                    loadPengajuan()
                                }
                            }
                        },

                        onReject = {

                            scope.launch {

                                val hasil =
                                    repository.updateStatusPengajuan(
                                        documentId =
                                            pengajuan.id,

                                        status =
                                            "ditolak"
                                    )

                                if (hasil.isSuccess) {

                                    loadPengajuan()
                                }
                            }
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==================================================
            // INFO
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFE6EEE9)
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

                    Icon(
                        imageVector =
                            Icons.Default.Group,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen
                    )

                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )

                    Text(
                        text =
                            "Pengajuan yang masuk dapat disetujui atau ditolak oleh Admin / HRD.",

                        fontSize =
                            13.sp,

                        color =
                            TextDark
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun ApprovalSummaryCard(
    modifier: Modifier,
    number: String,
    label: String,
    icon: ImageVector
) {

    Card(
        modifier = modifier,

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                    Modifier.height(10.dp)
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
                    13.sp,

                color =
                    TextGray
            )
        }
    }
}


// ==========================================================
// APPROVAL REQUEST CARD
// ==========================================================

@Composable
private fun ApprovalRequestCard(
    pengajuan: PengajuanData,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {

    val initials =
        pengajuan.nama
            .trim()
            .split(" ")
            .filter {
                it.isNotEmpty()
            }
            .take(2)
            .joinToString("") {
                it.first().uppercase()
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
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
        ) {

            // ==================================================
            // NAMA STAFF
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Card(
                    shape =
                        RoundedCornerShape(50.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFE6EEE9)
                        )
                ) {

                    Text(
                        text =
                            if (initials.isNotEmpty())
                                initials
                            else
                                "ST",

                        modifier =
                            Modifier.padding(13.dp),

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            PrimaryGreen
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
                            pengajuan.nama,

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
                            "Staff",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }

                Card(
                    shape =
                        RoundedCornerShape(50.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFFFF4D6)
                        )
                ) {

                    Text(
                        text =
                            "Menunggu",

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            Color(0xFF9A6700)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================================
            // JENIS PENGAJUAN
            // ==================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        getJenisIcon(
                            pengajuan.jenis
                        ),

                    contentDescription =
                        pengajuan.jenis,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(22.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Column {

                    Text(
                        text =
                            formatJenis(
                                pengajuan.jenis
                            ),

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Tanggal: ${pengajuan.tanggal}",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }
            }

            // ==================================================
            // DETAIL JAM
            // ==================================================

            if (
                pengajuan.jamPulang.isNotBlank() ||
                pengajuan.jamKeluar.isNotBlank() ||
                pengajuan.jamKembali.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color =
                                    Color(0xFFF5F7F6),

                                shape =
                                    RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                ) {

                    if (
                        pengajuan.jamPulang.isNotBlank()
                    ) {

                        DetailText(
                            label =
                                "Jam Pulang",

                            value =
                                pengajuan.jamPulang
                        )
                    }

                    if (
                        pengajuan.jamKeluar.isNotBlank()
                    ) {

                        DetailText(
                            label =
                                "Jam Keluar",

                            value =
                                pengajuan.jamKeluar
                        )
                    }

                    if (
                        pengajuan.jamKembali.isNotBlank()
                    ) {

                        DetailText(
                            label =
                                "Jam Kembali",

                            value =
                                pengajuan.jamKembali
                        )
                    }
                }
            }

            // ==================================================
            // DETAIL TANGGAL
            // ==================================================

            if (
                pengajuan.tanggalMulai.isNotBlank() ||
                pengajuan.tanggalSelesai.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Periode: ${pengajuan.tanggalMulai} - ${pengajuan.tanggalSelesai}",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }

            // ==================================================
            // ALASAN
            // ==================================================

            if (
                pengajuan.alasan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Alasan:",

                    fontSize =
                        12.sp,

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
                        pengajuan.alasan,

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================================
            // TOMBOL
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                // ============================
                // SETUJUI
                // ============================

                Card(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable {
                                onApprove()
                            },

                    shape =
                        RoundedCornerShape(10.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFE9F5EC)
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription =
                                "Setujui",

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                "Setujui",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }
                }

                // ============================
                // TOLAK
                // ============================

                Card(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable {
                                onReject()
                            },

                    shape =
                        RoundedCornerShape(10.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFFCEAEA)
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Close,

                            contentDescription =
                                "Tolak",

                            tint =
                                Color(0xFFB91C1C),

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                "Tolak",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFFB91C1C)
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// DETAIL TEXT
// ==========================================================

@Composable
private fun DetailText(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 2.dp
                )
    ) {

        Text(
            text =
                "$label:",

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                TextDark,

            modifier =
                Modifier.width(100.dp)
        )

        Text(
            text =
                value,

            fontSize =
                12.sp,

            color =
                TextGray
        )
    }
}


// ==========================================================
// ICON JENIS PENGAJUAN
// ==========================================================

private fun getJenisIcon(
    jenis: String
): ImageVector {

    return when {

        jenis.contains(
            "Sakit",
            ignoreCase = true
        ) ->
            Icons.Default.HealthAndSafety

        jenis.contains(
            "Cuti",
            ignoreCase = true
        ) ->
            Icons.Default.Event

        jenis.contains(
            "Keluar",
            ignoreCase = true
        ) ->
            Icons.Default.AccessTime

        jenis.contains(
            "Terlambat",
            ignoreCase = true
        ) ->
            Icons.Default.Schedule

        jenis.contains(
            "Pulang",
            ignoreCase = true
        ) ->
            Icons.Default.Schedule

        else ->
            Icons.Default.Event
    }
}


// ==========================================================
// FORMAT JENIS
// ==========================================================

private fun formatJenis(
    jenis: String
): String {

    return when (jenis) {

        "PulangCepat" ->
            "Pulang Cepat"

        "IzinKeluar" ->
            "Izin Keluar"

        "IzinTerlambat" ->
            "Izin Terlambat"

        "IzinSakit" ->
            "Izin Sakit"

        "CutiReguler" ->
            "Cuti Reguler"

        else ->
            jenis
    }
}