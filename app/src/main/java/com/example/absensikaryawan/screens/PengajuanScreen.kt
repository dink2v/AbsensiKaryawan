package com.example.absensikaryawan.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.absensikaryawan.data.PengajuanRepository
import kotlinx.coroutines.launch

private object PengajuanScreenColors {
    val Green = Color(0xFF16A34A)
    val GreenSoft = Color(0xFFE8F5E9)
    val Dark = Color(0xFF1F2937)
    val Gray = Color(0xFF6B7280)
    val Background = Color(0xFFF7F9FC)
    val OrangeSoft = Color(0xFFFFF4E5)
    val Orange = Color(0xFFF59E0B)
    val RedSoft = Color(0xFFFFEBEE)
    val Red = Color(0xFFDC2626)
}

@Composable
fun PengajuanScreen(
    onBack: () -> Unit,
    onPengajuanBaru: () -> Unit,
    onStatusClick: (
        Map<String, Any>
    ) -> Unit = {}
) {

    val scope = rememberCoroutineScope()

    var daftarPengajuan by remember {
        mutableStateOf<List<Map<String, Any>>>(
            emptyList()
        )
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

    suspend fun loadData() {
        loading = true
        errorMessage = ""

        try {
            daftarPengajuan =
                PengajuanRepository.ambilPengajuanSaya()
        } catch (e: Exception) {
            errorMessage =
                e.message ?: "Gagal mengambil data pengajuan."
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                PengajuanScreenColors.Background
            )
    ) {

        // ============================================================
        // HEADER
        // ============================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
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
                        PengajuanScreenColors.Dark
                )
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "Pengajuan",
                    fontSize = 22.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        PengajuanScreenColors.Dark
                )

                Text(
                    text =
                        "Riwayat dan status pengajuan",
                    fontSize = 12.sp,
                    color =
                        PengajuanScreenColors.Gray
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
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription =
                        "Refresh",
                    tint =
                        PengajuanScreenColors.Green
                )
            }
        }

        // ============================================================
        // BUTTON PENGAJUAN BARU
        // ============================================================

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
            shape =
                RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        PengajuanScreenColors.Green
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
                        Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier.size(28.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column {

                    Text(
                        text =
                            "Buat Pengajuan Baru",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Ajukan izin, cuti, atau keperluan lainnya",
                        color =
                            Color.White.copy(
                                alpha = 0.9f
                            ),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        // ============================================================
        // RINGKASAN STATUS
        // ============================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            SummaryCard(
                modifier =
                    Modifier.weight(1f),
                title = "Menunggu",
                count = menunggu,
                background =
                    PengajuanScreenColors.OrangeSoft,
                icon =
                    Icons.Default.Pending,
                iconColor =
                    PengajuanScreenColors.Orange
            )

            SummaryCard(
                modifier =
                    Modifier.weight(1f),
                title = "Disetujui",
                count = disetujui,
                background =
                    PengajuanScreenColors.GreenSoft,
                icon =
                    Icons.Default.CheckCircle,
                iconColor =
                    PengajuanScreenColors.Green
            )

            SummaryCard(
                modifier =
                    Modifier.weight(1f),
                title = "Ditolak",
                count = ditolak,
                background =
                    PengajuanScreenColors.RedSoft,
                icon =
                    Icons.Default.Close,
                iconColor =
                    PengajuanScreenColors.Red
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // ============================================================
        // FILTER
        // ============================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                text = "Semua",
                selected =
                    filter == "Semua",
                onClick = {
                    filter = "Semua"
                }
            )

            FilterChip(
                text = "Menunggu",
                selected =
                    filter == "Menunggu",
                onClick = {
                    filter = "Menunggu"
                }
            )

            FilterChip(
                text = "Disetujui",
                selected =
                    filter == "Disetujui",
                onClick = {
                    filter = "Disetujui"
                }
            )

            FilterChip(
                text = "Ditolak",
                selected =
                    filter == "Ditolak",
                onClick = {
                    filter = "Ditolak"
                }
            )
        }

        // ============================================================
        // ERROR
        // ============================================================

        if (errorMessage.isNotBlank()) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                shape =
                    RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            PengajuanScreenColors.RedSoft
                    )
            ) {

                Text(
                    text = errorMessage,
                    modifier =
                        Modifier.padding(14.dp),
                    fontSize = 13.sp,
                    color =
                        PengajuanScreenColors.Red
                )
            }
        }

        // ============================================================
        // CONTENT
        // ============================================================

        if (loading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        PengajuanScreenColors.Green
                )
            }

        } else if (filteredList.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Belum ada pengajuan",
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            PengajuanScreenColors.Dark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Pengajuan yang Anda buat akan muncul di sini.",
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
                contentPadding =
                    PaddingValues(
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
                        it["documentId"]
                            ?.toString()
                            ?: it.hashCode()
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

// ====================================================================
// SUMMARY
// ====================================================================

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
        shape =
            RoundedCornerShape(14.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    background
            )
    ) {

        Column(
            modifier =
                Modifier.padding(12.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier =
                    Modifier.size(22.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    PengajuanScreenColors.Dark
            )

            Text(
                text = title,
                fontSize = 11.sp,
                color =
                    PengajuanScreenColors.Gray
            )
        }
    }
}

// ====================================================================
// FILTER
// ====================================================================

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.clickable {
                onClick()
            },
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        PengajuanScreenColors.Green
                    } else {
                        Color.White
                    }
            )
    ) {

        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 8.dp
                ),
            fontSize = 12.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
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

// ====================================================================
// PENGAJUAN CARD
// ====================================================================

@Composable
private fun PengajuanCard(
    data: Map<String, Any>,
    onClick: () -> Unit
) {

    val jenis =
        data["jenis"]
            ?.toString()
            ?.ifBlank {
                "Pengajuan"
            }
            ?: "Pengajuan"

    val tanggalMulai =
        data["tanggalMulai"]
            ?.toString()
            .orEmpty()

    val tanggalSelesai =
        data["tanggalSelesai"]
            ?.toString()
            .orEmpty()

    val alasan =
        data["alasan"]
            ?.toString()
            .orEmpty()

    val status =
        getPengajuanStatus(data)

    val currentApproverName =
        data["currentApproverName"]
            ?.toString()
            .orEmpty()

    val currentApproverJabatan =
        data["currentApproverJabatan"]
            ?.toString()
            .orEmpty()

    val locked =
        getBooleanValue(
            data["approvalLocked"]
        )

    val approvalChain =
        parseApprovalChain(
            data["approvalChain"]
        )

    val approvalStatuses =
        parseApprovalStatuses(
            data["approvalStatuses"]
        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
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
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            // ========================================================
            // JUDUL
            // ========================================================

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
                        text = jenis,
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            PengajuanScreenColors.Dark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (
                                tanggalMulai.isNotBlank() &&
                                tanggalSelesai.isNotBlank()
                            ) {
                                "$tanggalMulai s/d $tanggalSelesai"
                            } else {
                                tanggalMulai
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

            // ========================================================
            // ALASAN
            // ========================================================

            if (alasan.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Alasan",
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        PengajuanScreenColors.Gray
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
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
                modifier =
                    Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ========================================================
            // JALUR APPROVAL
            // ========================================================

            Text(
                text = "Jalur Approval",
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    PengajuanScreenColors.Dark
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            if (approvalChain.isEmpty()) {

                Text(
                    text =
                        "Jalur approval belum tersedia.",
                    fontSize = 12.sp,
                    color =
                        PengajuanScreenColors.Gray
                )

            } else {

                approvalChain
                    .sortedBy {
                        it.urutan
                    }
                    .forEach { person ->

                        ApprovalPersonRow(
                            person = person,
                            status =
                                approvalStatuses[
                                    person.uid
                                ] ?: "menunggu"
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )
                    }
            }

            // ========================================================
            // APPROVER SEKARANG
            // ========================================================

            if (
                !locked &&
                currentApproverName.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                PengajuanScreenColors.OrangeSoft
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(12.dp)
                    ) {

                        Text(
                            text =
                                "Sedang Menunggu",
                            fontSize = 11.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                PengajuanScreenColors.Orange
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                currentApproverName,
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                PengajuanScreenColors.Dark
                        )

                        if (
                            currentApproverJabatan
                                .isNotBlank()
                        ) {

                            Text(
                                text =
                                    currentApproverJabatan,
                                fontSize = 11.sp,
                                color =
                                    PengajuanScreenColors.Gray
                            )
                        }
                    }
                }
            }

            // ========================================================
            // FINAL OWNER
            // ========================================================

            if (locked) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                val finalGreen =
                    status == "disetujui"

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                if (finalGreen) {
                                    PengajuanScreenColors.GreenSoft
                                } else {
                                    PengajuanScreenColors.RedSoft
                                }
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(12.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Lock,
                            contentDescription = null,
                            tint =
                                if (finalGreen) {
                                    PengajuanScreenColors.Green
                                } else {
                                    PengajuanScreenColors.Red
                                },
                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Column {

                            Text(
                                text =
                                    "Keputusan Final Owner",
                                fontSize = 12.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    if (finalGreen) {
                                        PengajuanScreenColors.Green
                                    } else {
                                        PengajuanScreenColors.Red
                                    }
                            )

                            Text(
                                text =
                                    if (finalGreen) {
                                        "Pengajuan telah disetujui dan dikunci."
                                    } else {
                                        "Pengajuan telah ditolak dan dikunci."
                                    },
                                fontSize = 11.sp,
                                color =
                                    PengajuanScreenColors.Gray
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = onClick,
                modifier =
                    Modifier.align(
                        Alignment.End
                    )
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

// ====================================================================
// STATUS BADGE
// ====================================================================

@Composable
private fun StatusBadge(
    status: String
) {

    val background =
        when (status) {

            "disetujui" ->
                PengajuanScreenColors.GreenSoft

            "ditolak" ->
                PengajuanScreenColors.RedSoft

            else ->
                PengajuanScreenColors.OrangeSoft
        }

    val textColor =
        when (status) {

            "disetujui" ->
                PengajuanScreenColors.Green

            "ditolak" ->
                PengajuanScreenColors.Red

            else ->
                PengajuanScreenColors.Orange
        }

    val label =
        when (status) {

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Menunggu"
        }

    Box(
        modifier =
            Modifier
                .background(
                    color = background,
                    shape =
                        RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
    ) {

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Bold,
            color = textColor
        )
    }
}

// ====================================================================
// APPROVAL PERSON ROW
// ====================================================================

@Composable
private fun ApprovalPersonRow(
    person: ApprovalPersonUi,
    status: String
) {

    val normalized =
        status.lowercase()

    val icon =
        when (normalized) {

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Close

            else ->
                Icons.Default.Pending
        }

    val iconColor =
        when (normalized) {

            "disetujui" ->
                PengajuanScreenColors.Green

            "ditolak" ->
                PengajuanScreenColors.Red

            else ->
                PengajuanScreenColors.Orange
        }

    val label =
        when (normalized) {

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            else ->
                "Menunggu"
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    Color(0xFFF9FAFB),
                shape =
                    RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier =
                Modifier.size(22.dp)
        )

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = person.nama,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    PengajuanScreenColors.Dark
            )

            Text(
                text = person.jabatan,
                fontSize = 11.sp,
                color =
                    PengajuanScreenColors.Gray
            )
        }

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight =
                FontWeight.SemiBold,
            color = iconColor
        )
    }
}

// ====================================================================
// MODEL UI
// ====================================================================

private data class ApprovalPersonUi(
    val uid: String,
    val nama: String,
    val jabatan: String,
    val urutan: Int
)

// ====================================================================
// PARSE APPROVAL CHAIN
// ====================================================================

private fun parseApprovalChain(
    value: Any?
): List<ApprovalPersonUi> {

    val list =
        value as? List<*>
            ?: return emptyList()

    return list.mapNotNull { item ->

        val map =
            item as? Map<*, *>
                ?: return@mapNotNull null

        val uid =
            map["uid"]
                ?.toString()
                .orEmpty()

        val nama =
            map["nama"]
                ?.toString()
                .orEmpty()

        val jabatan =
            map["jabatan"]
                ?.toString()
                ?.uppercase()
                .orEmpty()

        val urutan =
            when (
                val valueUrutan =
                    map["urutan"]
            ) {

                is Number ->
                    valueUrutan.toInt()

                is String ->
                    valueUrutan.toIntOrNull()
                        ?: Int.MAX_VALUE

                else ->
                    Int.MAX_VALUE
            }

        if (
            uid.isBlank() ||
            nama.isBlank()
        ) {
            null
        } else {

            ApprovalPersonUi(
                uid = uid,
                nama = nama,
                jabatan = jabatan,
                urutan = urutan
            )
        }
    }
}

// ====================================================================
// PARSE APPROVAL STATUS
// ====================================================================

private fun parseApprovalStatuses(
    value: Any?
): Map<String, String> {

    val map =
        value as? Map<*, *>
            ?: return emptyMap()

    return map.mapNotNull { (key, value) ->

        val uid =
            key?.toString()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return@mapNotNull null

        val status =
            value
                ?.toString()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "menunggu"

        uid to status

    }.toMap()
}

// ====================================================================
// STATUS
// ====================================================================

private fun getPengajuanStatus(
    data: Map<String, Any>
): String {

    return data["status"]
        ?.toString()
        ?.trim()
        ?.lowercase()
        ?: "menunggu"
}

// ====================================================================
// BOOLEAN
// ====================================================================

private fun getBooleanValue(
    value: Any?
): Boolean {

    return when (value) {

        is Boolean ->
            value

        is String ->
            value.equals(
                "true",
                ignoreCase = true
            )

        else ->
            false
    }
}