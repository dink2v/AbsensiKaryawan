package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.PengajuanData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// ==========================================================
// WARNA
// ==========================================================

private object PengajuanScreenColors {

    val Green = PrimaryGreen
    val GreenSoft = SoftGreen
    val Dark = TextDark
    val Gray = TextGray
    val Background = com.example.absensikaryawan.screens.Background
    val Card = Color.White

    val OrangeSoft = Color(0xFFFFF4E5)
    val Orange = Color(0xFFF59E0B)

    val RedSoft = Color(0xFFFFEBEE)
    val Red = Color(0xFFDC2626)
}

// ==========================================================
// PENGAJUAN SCREEN
// ==========================================================

@Composable
fun PengajuanScreen(
    onBack: () -> Unit,
    onPengajuanBaru: () -> Unit,
    onStatusClick: (PengajuanData) -> Unit = {}
) {

    val scope = rememberCoroutineScope()

    val repository = remember {
        FirestoreRepository()
    }

    val firebaseAuth = remember {
        FirebaseAuth.getInstance()
    }

    // ======================================================
    // DATA
    // ======================================================

    var daftarPengajuan by remember {
        mutableStateOf<List<PengajuanData>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var filter by remember {
        mutableStateOf("Semua")
    }

    // ======================================================
    // LOAD DATA
    // ======================================================

    suspend fun loadData() {

        loading = true
        errorMessage = ""

        try {

            val uid =
                firebaseAuth.currentUser?.uid

            if (uid.isNullOrBlank()) {

                daftarPengajuan = emptyList()

                errorMessage =
                    "Sesi login tidak ditemukan. Silakan login kembali."

            } else {

                val result =
                    repository.getPengajuanSaya(uid)

                result.onSuccess { data ->

                    daftarPengajuan = data

                }.onFailure { exception ->

                    daftarPengajuan = emptyList()

                    errorMessage =
                        exception.message
                            ?: "Gagal mengambil data pengajuan."
                }
            }

        } catch (e: Exception) {

            daftarPengajuan = emptyList()

            errorMessage =
                e.message
                    ?: "Gagal mengambil data pengajuan."

        } finally {

            loading = false
        }
    }

    // ======================================================
    // LOAD PERTAMA
    // ======================================================

    LaunchedEffect(Unit) {
        loadData()
    }

    // ======================================================
    // JUMLAH STATUS
    // ======================================================

    val menunggu =
        daftarPengajuan.count {
            getPengajuanStatus(it) == "menunggu"
        }

    val disetujui =
        daftarPengajuan.count {
            getPengajuanStatus(it) == "disetujui"
        }

    val ditolak =
        daftarPengajuan.count {
            getPengajuanStatus(it) == "ditolak"
        }

    // ======================================================
    // FILTER
    // ======================================================

    val filteredList =
        when (filter) {

            "Menunggu" ->
                daftarPengajuan.filter {
                    getPengajuanStatus(it) == "menunggu"
                }

            "Disetujui" ->
                daftarPengajuan.filter {
                    getPengajuanStatus(it) == "disetujui"
                }

            "Ditolak" ->
                daftarPengajuan.filter {
                    getPengajuanStatus(it) == "ditolak"
                }

            else ->
                daftarPengajuan
        }

    // ======================================================
    // MAIN
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                PengajuanScreenColors.Background
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = PengajuanScreenColors.Dark
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Pengajuan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PengajuanScreenColors.Dark
                )

                Text(
                    text = "Riwayat dan status pengajuan",
                    fontSize = 12.sp,
                    color = PengajuanScreenColors.Gray
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        loadData()
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = PengajuanScreenColors.Green
                )
            }
        }

        // ==================================================
        // BUAT PENGAJUAN BARU
        // ==================================================

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 6.dp
                )
                .clickable {
                    onPengajuanBaru()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    PengajuanScreenColors.Green
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(
                        text = "Buat Pengajuan Baru",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text =
                            "Ajukan izin, cuti, atau keperluan lainnya",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // ==================================================
        // RINGKASAN STATUS
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Menunggu",
                count = menunggu,
                background =
                    PengajuanScreenColors.OrangeSoft,
                icon = Icons.Default.Pending,
                iconColor =
                    PengajuanScreenColors.Orange
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Disetujui",
                count = disetujui,
                background =
                    PengajuanScreenColors.GreenSoft,
                icon = Icons.Default.CheckCircle,
                iconColor =
                    PengajuanScreenColors.Green
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Ditolak",
                count = ditolak,
                background =
                    PengajuanScreenColors.RedSoft,
                icon = Icons.Default.Close,
                iconColor =
                    PengajuanScreenColors.Red
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ==================================================
        // FILTER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 16.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                text = "Semua",
                selected = filter == "Semua",
                onClick = {
                    filter = "Semua"
                }
            )

            FilterChip(
                text = "Menunggu",
                selected = filter == "Menunggu",
                onClick = {
                    filter = "Menunggu"
                }
            )

            FilterChip(
                text = "Disetujui",
                selected = filter == "Disetujui",
                onClick = {
                    filter = "Disetujui"
                }
            )

            FilterChip(
                text = "Ditolak",
                selected = filter == "Ditolak",
                onClick = {
                    filter = "Ditolak"
                }
            )
        }

        // ==================================================
        // ERROR
        // ==================================================

        if (errorMessage.isNotBlank()) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        PengajuanScreenColors.RedSoft
                )
            ) {

                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 13.sp,
                    color = PengajuanScreenColors.Red
                )
            }
        }

        // ==================================================
        // CONTENT
        // ==================================================

        if (loading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = PengajuanScreenColors.Green
                )
            }

        } else if (filteredList.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint =
                            PengajuanScreenColors.Green,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            if (filter == "Semua") {
                                "Belum ada pengajuan"
                            } else {
                                "Tidak ada pengajuan"
                            },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color =
                            PengajuanScreenColors.Dark
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            when (filter) {

                                "Menunggu" ->
                                    "Belum ada pengajuan yang menunggu keputusan Admin."

                                "Disetujui" ->
                                    "Belum ada pengajuan yang disetujui Admin."

                                "Ditolak" ->
                                    "Belum ada pengajuan yang ditolak Admin."

                                else ->
                                    "Pengajuan yang kamu buat akan muncul di sini."
                            },
                        fontSize = 13.sp,
                        color =
                            PengajuanScreenColors.Gray
                    )
                }
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = filteredList,
                    key = {
                        it.id
                    }
                ) { item ->

                    PengajuanCard(
                        data = item,
                        onClick = {
                            onStatusClick(item)
                        }
                    )
                }
            }
        }
    }
}

// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    count: Int,
    background: Color,
    icon: ImageVector,
    iconColor: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PengajuanScreenColors.Dark
            )

            Text(
                text = title,
                fontSize = 11.sp,
                color = PengajuanScreenColors.Gray
            )
        }
    }
}

// ==========================================================
// FILTER CHIP
// ==========================================================

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) {
                    PengajuanScreenColors.Green
                } else {
                    PengajuanScreenColors.Card
                }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (selected) 1.dp else 0.dp
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
            fontSize = 12.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
            color =
                if (selected) {
                    Color.White
                } else {
                    PengajuanScreenColors.Gray
                }
        )
    }
}

// ==========================================================
// PENGAJUAN CARD
// ==========================================================

@Composable
private fun PengajuanCard(
    data: PengajuanData,
    onClick: () -> Unit
) {

    val jenis =
        data.jenis.ifBlank {
            "Pengajuan"
        }

    val tanggalMulai =
        data.tanggalMulai

    val tanggalSelesai =
        data.tanggalSelesai

    val alasan =
        data.alasan

    val status =
        getPengajuanStatus(data)

    val statusInfo =
        when (status) {

            "disetujui" ->
                "Pengajuan telah disetujui Admin."

            "ditolak" ->
                "Pengajuan telah ditolak Admin."

            else ->
                "Menunggu keputusan Admin."
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                PengajuanScreenColors.Card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SurfaceIcon(
                    icon = Icons.Default.Description,
                    background =
                        PengajuanScreenColors.GreenSoft,
                    tint =
                        PengajuanScreenColors.Green
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = jenis,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                            PengajuanScreenColors.Dark
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            when {

                                tanggalMulai.isNotBlank() &&
                                        tanggalSelesai.isNotBlank() ->

                                    if (
                                        tanggalMulai ==
                                        tanggalSelesai
                                    ) {
                                        tanggalMulai
                                    } else {
                                        "$tanggalMulai s/d $tanggalSelesai"
                                    }

                                tanggalMulai.isNotBlank() ->
                                    tanggalMulai

                                data.tanggal.isNotBlank() ->
                                    data.tanggal

                                else ->
                                    "-"
                            },
                        fontSize = 12.sp,
                        color =
                            PengajuanScreenColors.Gray
                    )
                }

                StatusBadge(
                    status = status
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ==================================================
            // INFORMASI STATUS
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            statusBackground(status),
                        shape =
                            RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        statusIcon(status),
                    contentDescription = null,
                    tint =
                        statusColor(status),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = statusInfo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color =
                        statusColor(status)
                )
            }

            // ==================================================
            // DETAIL WAKTU
            // ==================================================

            val waktu =
                when {

                    data.jamPulang.isNotBlank() ->
                        "Jam Pulang: ${data.jamPulang}"

                    data.jamKeluar.isNotBlank() ->

                        if (data.jamKembali.isNotBlank()) {
                            "Keluar: ${data.jamKeluar} • Kembali: ${data.jamKembali}"
                        } else {
                            "Jam Keluar: ${data.jamKeluar}"
                        }

                    else ->
                        ""
                }

            if (waktu.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = waktu,
                    fontSize = 12.sp,
                    color =
                        PengajuanScreenColors.Gray
                )
            }

            // ==================================================
            // ALASAN
            // ==================================================

            if (alasan.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Alasan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        PengajuanScreenColors.Gray
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = alasan,
                    fontSize = 13.sp,
                    color =
                        PengajuanScreenColors.Dark,
                    maxLines = 3,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onClick,
                modifier =
                    Modifier.align(Alignment.End)
            ) {

                Text(
                    text = "Lihat Detail",
                    fontSize = 12.sp,
                    color =
                        PengajuanScreenColors.Green
                )
            }
        }
    }
}

// ==========================================================
// ICON STATUS
// ==========================================================

@Composable
private fun SurfaceIcon(
    icon: ImageVector,
    background: Color,
    tint: Color
) {

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = background,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ==========================================================
// STATUS BADGE
// ==========================================================

@Composable
private fun StatusBadge(
    status: String
) {

    Box(
        modifier = Modifier
            .background(
                color = statusBackground(status),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
    ) {

        Text(
            text =
                statusLabel(status),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color =
                statusColor(status)
        )
    }
}

// ==========================================================
// STATUS HELPER
// ==========================================================

private fun statusLabel(
    status: String
): String {

    return when (status) {

        "disetujui" ->
            "Disetujui"

        "ditolak" ->
            "Ditolak"

        else ->
            "Menunggu"
    }
}

private fun statusColor(
    status: String
): Color {

    return when (status) {

        "disetujui" ->
            PengajuanScreenColors.Green

        "ditolak" ->
            PengajuanScreenColors.Red

        else ->
            PengajuanScreenColors.Orange
    }
}

private fun statusBackground(
    status: String
): Color {

    return when (status) {

        "disetujui" ->
            PengajuanScreenColors.GreenSoft

        "ditolak" ->
            PengajuanScreenColors.RedSoft

        else ->
            PengajuanScreenColors.OrangeSoft
    }
}

private fun statusIcon(
    status: String
): ImageVector {

    return when (status) {

        "disetujui" ->
            Icons.Default.CheckCircle

        "ditolak" ->
            Icons.Default.Close

        else ->
            Icons.Default.Pending
    }
}

// ==========================================================
// NORMALISASI STATUS
// ==========================================================

private fun getPengajuanStatus(
    data: PengajuanData
): String {

    return data.status
        .trim()
        .lowercase()
        .ifBlank {
            "menunggu"
        }
}