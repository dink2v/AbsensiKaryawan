package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanRepository
import kotlinx.coroutines.launch

// ==========================================================
// PENGAJUAN SCREEN
// ==========================================================

@Composable
fun PengajuanScreen(
    onBack: () -> Unit,
    onPengajuanBaru: () -> Unit,
    onStatusClick: (Map<String, Any>) -> Unit = {}
) {

    val repository = PengajuanRepository

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
    // FILTER
    // ==========================================================

    var filterRiwayat by remember {
        mutableStateOf("semua")
    }

    var dropdownTerbuka by remember {
        mutableStateOf(false)
    }

    // ==========================================================
    // SCROLL
    // ==========================================================

    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    // ==========================================================
    // AMBIL DATA FIRESTORE
    // ==========================================================

    LaunchedEffect(Unit) {

        sedangMemuat = true

        try {
            daftarPengajuan =
                repository.ambilPengajuanSaya()

        } catch (_: Exception) {

            daftarPengajuan =
                emptyList()
        }

        sedangMemuat = false
    }

    // ==========================================================
    // HITUNG STATUS
    // ==========================================================

    val jumlahMenunggu =
        daftarPengajuan.count {
            it["status"]
                ?.toString()
                ?.trim()
                ?.lowercase() == "menunggu"
        }

    val jumlahDisetujui =
        daftarPengajuan.count {
            it["status"]
                ?.toString()
                ?.trim()
                ?.lowercase() == "disetujui"
        }

    val jumlahDitolak =
        daftarPengajuan.count {
            it["status"]
                ?.toString()
                ?.trim()
                ?.lowercase() == "ditolak"
        }

    val jumlahTotal =
        daftarPengajuan.size

    // ==========================================================
    // FILTER DATA
    // ==========================================================

    val daftarRiwayatFiltered =
        when (filterRiwayat) {

            "menunggu" ->
                daftarPengajuan.filter {
                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() == "menunggu"
                }

            "disetujui" ->
                daftarPengajuan.filter {
                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() == "disetujui"
                }

            "ditolak" ->
                daftarPengajuan.filter {
                    it["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase() == "ditolak"
                }

            else ->
                daftarPengajuan
        }

    // ==========================================================
    // JUDUL FILTER
    // ==========================================================

    val filterRiwayatText =
        when (filterRiwayat) {

            "menunggu" ->
                "Menunggu Persetujuan"

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Semua Pengajuan"
        }

    // ==========================================================
    // PILIH FILTER
    // ==========================================================

    fun pilihFilter(
        filter: String
    ) {

        filterRiwayat =
            filter

        dropdownTerbuka =
            false

        scope.launch {

            scrollState.animateScrollTo(
                scrollState.maxValue
            )
        }
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

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Pengajuan",
                        fontSize =
                            22.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Kelola pengajuan izin dan cuti",
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
                            scrollState
                        )
                        .imePadding()
                        .padding(
                            horizontal = 20.dp
                        )
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                // ==================================================
                // BUAT PENGAJUAN BARU
                // ==================================================

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPengajuanBaru()
                            },
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
                                3.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(48.dp),
                            shape =
                                RoundedCornerShape(14.dp),
                            color =
                                SoftGreen
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Send,
                                contentDescription =
                                    "Pengajuan Baru",
                                tint =
                                    PrimaryGreen,
                                modifier =
                                    Modifier
                                        .padding(11.dp)
                                        .size(26.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Buat Pengajuan Baru",
                                fontSize =
                                    16.sp,
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
                                    "Ajukan izin, sakit, pulang cepat, " +
                                            "atau cuti kepada atasan.",
                                fontSize =
                                    12.sp,
                                color =
                                    TextGray
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )

                // ==================================================
                // DESKRIPSI
                // ==================================================

                Text(
                    text =
                        "Deskripsi",
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

                        Surface(
                            modifier =
                                Modifier.size(44.dp),
                            shape =
                                RoundedCornerShape(13.dp),
                            color =
                                Color.White
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Description,
                                contentDescription =
                                    null,
                                tint =
                                    PrimaryGreen,
                                modifier =
                                    Modifier
                                        .padding(9.dp)
                                        .size(26.dp)
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
                                    "Jenis Pengajuan",
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
                                    "Pulang cepat, izin keluar, " +
                                            "izin terlambat, sakit, atau cuti.",
                                fontSize =
                                    12.sp,
                                color =
                                    TextGray
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                // ==================================================
                // STATUS PENGAJUAN
                // ==================================================

                Text(
                    text =
                        "Status Pengajuan",
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

                if (sedangMemuat) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 20.dp
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
                                Modifier.height(8.dp)
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
                    // BARIS 1
                    // ==================================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        StatusCard(
                            modifier =
                                Modifier.weight(1f),
                            icon =
                                Icons.Default.Pending,
                            title =
                                "Menunggu",
                            number =
                                jumlahMenunggu.toString(),
                            accentColor =
                                Color(0xFFD97706),
                            badgeColor =
                                Color(0xFFFFF3E0),
                            onClick = {
                                pilihFilter("menunggu")
                            }
                        )

                        StatusCard(
                            modifier =
                                Modifier.weight(1f),
                            icon =
                                Icons.Default.CheckCircle,
                            title =
                                "Disetujui",
                            number =
                                jumlahDisetujui.toString(),
                            accentColor =
                                PrimaryGreen,
                            badgeColor =
                                SoftGreen,
                            onClick = {
                                pilihFilter("disetujui")
                            }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    // ==================================================
                    // BARIS 2
                    // ==================================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        StatusCard(
                            modifier =
                                Modifier.weight(1f),
                            icon =
                                Icons.Default.Cancel,
                            title =
                                "Ditolak",
                            number =
                                jumlahDitolak.toString(),
                            accentColor =
                                Color(0xFFB91C1C),
                            badgeColor =
                                Color(0xFFFCE8E8),
                            onClick = {
                                pilihFilter("ditolak")
                            }
                        )

                        StatusCard(
                            modifier =
                                Modifier.weight(1f),
                            icon =
                                Icons.Default.EventNote,
                            title =
                                "Total",
                            number =
                                jumlahTotal.toString(),
                            accentColor =
                                TextDark,
                            badgeColor =
                                Color(0xFFF0F1F3),
                            onClick = {
                                pilihFilter("semua")
                            }
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                // ==================================================
                // RIWAYAT
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
                // DROPDOWN
                // ==================================================

                BoxDropdownFilter(
                    selectedText =
                        filterRiwayatText,
                    expanded =
                        dropdownTerbuka,
                    onClick = {
                        dropdownTerbuka =
                            !dropdownTerbuka
                    },
                    onDismiss = {
                        dropdownTerbuka =
                            false
                    },
                    onSelected = { filter ->
                        pilihFilter(filter)
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // ==================================================
                // LIST RIWAYAT
                // ==================================================

                if (!sedangMemuat) {

                    if (daftarRiwayatFiltered.isEmpty()) {

                        DataKosongPengajuan(
                            filter =
                                filterRiwayat
                        )

                    } else {

                        daftarRiwayatFiltered
                            .reversed()
                            .forEach { pengajuan ->

                                RiwayatPengajuanCard(
                                    pengajuan =
                                        pengajuan
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
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
// DROPDOWN FILTER
// ==========================================================

@Composable
private fun BoxDropdownFilter(
    selectedText: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {

    Column {

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick()
                    },
            shape =
                RoundedCornerShape(14.dp),
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
                        .padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
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
                            "Filter Riwayat",
                        fontSize =
                            10.sp,
                        color =
                            TextGray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            selectedText,
                        fontSize =
                            14.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            TextDark
                    )
                }

                Icon(
                    imageVector =
                        Icons.Default.KeyboardArrowDown,
                    contentDescription =
                        "Pilih filter",
                    tint =
                        PrimaryGreen,
                    modifier =
                        Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded =
                expanded,
            onDismissRequest =
                onDismiss
        ) {

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            "Semua Pengajuan"
                    )
                },
                onClick = {
                    onSelected(
                        "semua"
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            "Menunggu Persetujuan"
                    )
                },
                onClick = {
                    onSelected(
                        "menunggu"
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            "Disetujui"
                    )
                },
                onClick = {
                    onSelected(
                        "disetujui"
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            "Ditolak"
                    )
                },
                onClick = {
                    onSelected(
                        "ditolak"
                    )
                }
            )
        }
    }
}


// ==========================================================
// STATUS CARD
// ==========================================================

@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    number: String,
    accentColor: Color,
    badgeColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier
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
                    .padding(14.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    modifier =
                        Modifier
                            .size(38.dp)
                            .background(
                                color =
                                    badgeColor,
                                shape =
                                    RoundedCornerShape(12.dp)
                            ),
                    horizontalArrangement =
                        Arrangement.Center,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            icon,
                        contentDescription =
                            title,
                        tint =
                            accentColor,
                        modifier =
                            Modifier.size(20.dp)
                    )
                }

                Text(
                    text =
                        number,
                    fontSize =
                        24.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        accentColor
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    title,
                fontSize =
                    13.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    TextDark
            )
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
// RIWAYAT PENGAJUAN CARD
// ==========================================================

@Composable
private fun RiwayatPengajuanCard(
    pengajuan: Map<String, Any>
) {

    val nama =
        pengajuan["nama"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val jabatan =
        pengajuan["jabatanPengaju"]
            ?.toString()
            ?.trim()
            ?.uppercase()
            .orEmpty()

    val jenis =
        pengajuan["jenis"]
            ?.toString()
            ?.trim()
            ?.ifEmpty {
                "Pengajuan"
            }
            ?: "Pengajuan"

    val tanggalMulai =
        pengajuan["tanggalMulai"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val tanggalSelesai =
        pengajuan["tanggalSelesai"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val jamPulang =
        pengajuan["jamPulang"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val jamKeluar =
        pengajuan["jamKeluar"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val jamKembali =
        pengajuan["jamKembali"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val alasan =
        pengajuan["alasan"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val status =
        pengajuan["status"]
            ?.toString()
            ?.trim()
            ?.lowercase()
            ?: "menunggu"

    val approvalLocked =
        pengajuan["approvalLocked"]
            ?.toString()
            ?.toBoolean()
            ?: false

    val currentApproverUid =
        pengajuan["currentApproverUid"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val currentApproverName =
        pengajuan["currentApproverName"]
            ?.toString()
            ?.trim()
            .orEmpty()

    val currentApproverJabatan =
        pengajuan["currentApproverJabatan"]
            ?.toString()
            ?.trim()
            ?.uppercase()
            .orEmpty()

    // ==========================================================
    // STATUS UTAMA
    // ==========================================================

    val statusText =
        when (status) {

            "disetujui" ->
                "DISETUJUI"

            "ditolak" ->
                "DITOLAK"

            else ->
                "MENUNGGU"
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

    // ==========================================================
    // JAM
    // ==========================================================

    val daftarJam =
        buildList {

            if (jamKeluar.isNotBlank()) {
                add("Keluar: $jamKeluar")
            }

            if (jamKembali.isNotBlank()) {
                add("Kembali: $jamKembali")
            }

            if (jamPulang.isNotBlank()) {
                add("Pulang: $jamPulang")
            }
        }

    val jamText =
        daftarJam.joinToString(
            separator = "  •  "
        )

    // ==========================================================
    // TANGGAL
    // ==========================================================

    val tanggalText =
        when {

            tanggalMulai.isNotBlank() &&
                    tanggalSelesai.isNotBlank() &&
                    tanggalSelesai != tanggalMulai ->

                "$tanggalMulai - $tanggalSelesai"

            tanggalMulai.isNotBlank() ->
                tanggalMulai

            else ->
                "-"
        }

    // ==========================================================
    // APPROVAL CHAIN
    // ==========================================================

    val approvalChain =
        parseApprovalChain(
            pengajuan["approvalChain"]
        )

    val approvalStatuses =
        parseApprovalStatuses(
            pengajuan["approvalStatuses"]
        )

    // ==========================================================
    // CARD
    // ==========================================================

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
                    3.dp
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
                    Alignment.Top
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            nama.ifEmpty {
                                "Nama tidak tersedia"
                            },
                        fontSize =
                            17.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            jabatan.ifEmpty {
                                "Jabatan tidak tersedia"
                            },
                        fontSize =
                            11.sp,
                        fontWeight =
                            FontWeight.Medium,
                        color =
                            PrimaryGreen
                    )
                }

                Text(
                    text =
                        statusText,
                    fontSize =
                        10.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        statusColor,
                    modifier =
                        Modifier
                            .background(
                                color =
                                    statusColor.copy(
                                        alpha = 0.12f
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        50.dp
                                    )
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ==================================================
            // JENIS PENGAJUAN
            // ==================================================

            Text(
                text =
                    jenis,
                fontSize =
                    14.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // ==================================================
            // TANGGAL + JAM
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.EventNote,
                    contentDescription =
                        null,
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
                        tanggalText,
                    fontSize =
                        12.sp,
                    color =
                        TextDark
                )
            }

            if (jamText.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Pending,
                        contentDescription =
                            null,
                        tint =
                            TextGray,
                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            jamText,
                        fontSize =
                            12.sp,
                        color =
                            TextGray
                    )
                }
            }

            // ==================================================
            // ALASAN
            // ==================================================

            if (alasan.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Alasan",
                    fontSize =
                        11.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        alasan,
                    fontSize =
                        12.sp,
                    color =
                        TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // ==================================================
            // GARIS
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Color(0xFFE5E7EB)
                        )
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ==================================================
            // JALUR APPROVAL
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.EventNote,
                    contentDescription =
                        null,
                    tint =
                        PrimaryGreen,
                    modifier =
                        Modifier.size(20.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        "Jalur Approval",
                    fontSize =
                        14.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            if (approvalChain.isEmpty()) {

                Text(
                    text =
                        "Jalur approval belum tersedia.",
                    fontSize =
                        12.sp,
                    color =
                        TextGray
                )

            } else {

                approvalChain.forEachIndexed { index, person ->

                    ApprovalPersonCard(
                        nama =
                            person.nama,
                        jabatan =
                            person.jabatan,
                        status =
                            approvalStatuses[person.uid]
                                ?: "menunggu",
                        isCurrent =
                            person.uid ==
                                    currentApproverUid &&
                                    status == "menunggu" &&
                                    !approvalLocked,
                        isLast =
                            index ==
                                    approvalChain.lastIndex
                    )
                }
            }

            // ==================================================
            // TAHAP SAAT INI
            // ==================================================

            if (
                status == "menunggu" &&
                !approvalLocked &&
                currentApproverName.isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFFFF8E7)
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Pending,
                            contentDescription =
                                null,
                            tint =
                                Color(0xFFD97706),
                            modifier =
                                Modifier.size(19.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Column {

                            Text(
                                text =
                                    "Menunggu persetujuan",
                                fontSize =
                                    11.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    Color(0xFFD97706)
                            )

                            Text(
                                text =
                                    "$currentApproverName • " +
                                            currentApproverJabatan,
                                fontSize =
                                    11.sp,
                                color =
                                    TextDark
                            )
                        }
                    }
                }
            }

            // ==================================================
            // FINAL LOCK
            // ==================================================

            if (approvalLocked) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        when (status) {

                            "disetujui" ->
                                "Approval selesai dan telah dikunci."

                            "ditolak" ->
                                "Approval selesai dan telah dikunci."

                            else ->
                                "Approval telah dikunci."
                        },
                    fontSize =
                        10.sp,
                    color =
                        TextGray
                )
            }
        }
    }
}


// ==========================================================
// APPROVAL PERSON CARD
// ==========================================================

@Composable
private fun ApprovalPersonCard(
    nama: String,
    jabatan: String,
    status: String,
    isCurrent: Boolean,
    isLast: Boolean
) {

    val statusNormal =
        status
            .trim()
            .lowercase()

    val statusText =
        when {

            isCurrent ->
                "MENUNGGU"

            statusNormal == "disetujui" ->
                "DISETUJUI"

            statusNormal == "ditolak" ->
                "DITOLAK"

            else ->
                "MENUNGGU"
        }

    val statusColor =
        when {

            isCurrent ->
                Color(0xFFD97706)

            statusNormal == "disetujui" ->
                PrimaryGreen

            statusNormal == "ditolak" ->
                Color(0xFFB91C1C)

            else ->
                Color(0xFF9CA3AF)
        }

    val statusIcon =
        when {

            statusNormal == "disetujui" ->
                Icons.Default.CheckCircle

            statusNormal == "ditolak" ->
                Icons.Default.Cancel

            else ->
                Icons.Default.Pending
        }

    Column {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ==================================================
            // ICON STATUS
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .size(34.dp)
                        .background(
                            color =
                                statusColor.copy(
                                    alpha = 0.12f
                                ),
                            shape =
                                RoundedCornerShape(
                                    10.dp
                                )
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        statusIcon,
                    contentDescription =
                        statusText,
                    tint =
                        statusColor,
                    modifier =
                        Modifier.size(18.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )

            // ==================================================
            // NAMA + JABATAN
            // ==================================================

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        nama.ifEmpty {
                            "Nama tidak tersedia"
                        },
                    fontSize =
                        13.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        TextDark
                )

                Text(
                    text =
                        jabatan.ifEmpty {
                            "Jabatan tidak tersedia"
                        },
                    fontSize =
                        10.sp,
                    color =
                        TextGray
                )
            }

            // ==================================================
            // STATUS
            // ==================================================

            Text(
                text =
                    statusText,
                fontSize =
                    9.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    statusColor,
                modifier =
                    Modifier
                        .background(
                            color =
                                statusColor.copy(
                                    alpha = 0.10f
                                ),
                            shape =
                                RoundedCornerShape(
                                    50.dp
                                )
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        )
            )
        }

        // ==================================================
        // CONNECTOR
        // ==================================================

        if (!isLast) {

            Box(
                modifier =
                    Modifier
                        .padding(
                            start = 16.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        )
                        .width(2.dp)
                        .height(12.dp)
                        .background(
                            Color(0xFFE5E7EB)
                        )
            )
        }
    }
}


// ==========================================================
// PARSE APPROVAL CHAIN
// ==========================================================

private data class ApprovalPersonUi(
    val uid: String,
    val nama: String,
    val jabatan: String,
    val urutan: Int
)

private fun parseApprovalChain(
    rawData: Any?
): List<ApprovalPersonUi> {

    if (rawData !is List<*>) {
        return emptyList()
    }

    return rawData.mapNotNull { item ->

        if (item !is Map<*, *>) {
            return@mapNotNull null
        }

        val uid =
            item["uid"]
                ?.toString()
                .orEmpty()

        if (uid.isBlank()) {
            return@mapNotNull null
        }

        val nama =
            item["nama"]
                ?.toString()
                .orEmpty()

        val jabatan =
            item["jabatan"]
                ?.toString()
                ?.trim()
                ?.uppercase()
                .orEmpty()

        val urutan =
            when (
                val value =
                    item["urutan"]
            ) {

                is Long ->
                    value.toInt()

                is Int ->
                    value

                is Double ->
                    value.toInt()

                is Float ->
                    value.toInt()

                is String ->
                    value.toIntOrNull()
                        ?: 0

                else ->
                    0
            }

        ApprovalPersonUi(
            uid =
                uid,
            nama =
                nama,
            jabatan =
                jabatan,
            urutan =
                urutan
        )

    }.sortedBy {
        it.urutan
    }
}


// ==========================================================
// PARSE APPROVAL STATUSES
// ==========================================================

private fun parseApprovalStatuses(
    rawData: Any?
): Map<String, String> {

    if (rawData !is Map<*, *>) {
        return emptyMap()
    }

    val result =
        mutableMapOf<String, String>()

    rawData.forEach { (key, value) ->

        if (key != null) {

            result[
                key.toString()
            ] =
                value
                    ?.toString()
                    ?.trim()
                    ?.lowercase()
                    ?: "menunggu"
        }
    }

    return result
}