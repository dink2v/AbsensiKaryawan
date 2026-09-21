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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// DETAIL PENGAJUAN
// ==========================================================

@Composable
fun DetailPengajuanScreen(
    jenis: String = "",
    tanggal: String = "",
    status: String = "",
    jamPulang: String = "",
    jamKeluar: String = "",
    jamKembali: String = "",
    tanggalMulai: String = "",
    tanggalSelesai: String = "",
    alasan: String = "",
    catatanAdmin: String = "",

    approvalChain: List<Map<String, Any>> = emptyList(),
    approvalStatuses: Map<String, Any> = emptyMap(),
    currentApproverUid: String = "",
    currentApproverName: String = "",
    currentApproverJabatan: String = "",
    approvalLocked: Boolean = false,

    onBack: () -> Unit
) {

    // ======================================================
    // NORMALISASI DATA
    // ======================================================

    val jenisFinal =
        jenis.trim().ifEmpty {
            "Pengajuan"
        }

    val statusFinal =
        status.trim().lowercase().ifEmpty {
            "menunggu"
        }

    val statusText =
        when (statusFinal) {
            "disetujui" -> "Disetujui"
            "ditolak" -> "Ditolak"
            else -> "Menunggu"
        }

    val statusColor =
        when (statusFinal) {
            "disetujui" -> PrimaryGreen
            "ditolak" -> Color(0xFFB91C1C)
            else -> Color(0xFFD97706)
        }

    val statusIcon =
        when (statusFinal) {
            "disetujui" -> Icons.Default.CheckCircle
            "ditolak" -> Icons.Default.Cancel
            else -> Icons.Default.Pending
        }

    val tanggalTampilan =
        when {
            tanggalMulai.isNotBlank() &&
                    tanggalSelesai.isNotBlank() -> {
                if (tanggalMulai == tanggalSelesai) {
                    tanggalMulai
                } else {
                    "$tanggalMulai - $tanggalSelesai"
                }
            }

            tanggalMulai.isNotBlank() ->
                tanggalMulai

            tanggal.isNotBlank() ->
                tanggal

            else ->
                "-"
        }

    // ======================================================
    // MAIN
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
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextDark
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Detail Pengajuan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Informasi lengkap pengajuan kamu.",
                        fontSize = 12.sp,
                        color = TextGray
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
                    modifier = Modifier.height(8.dp)
                )

                // ==================================================
                // HEADER DETAIL CARD
                // ==================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.White
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(15.dp),
                                color =
                                    PrimaryGreen.copy(
                                        alpha = 0.10f
                                    )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Description,
                                    contentDescription =
                                        "Pengajuan",
                                    tint = PrimaryGreen,
                                    modifier =
                                        Modifier
                                            .padding(12.dp)
                                            .size(28.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(13.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = jenisFinal,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Event,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier =
                                            Modifier.size(14.dp)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )

                                    Text(
                                        text = tanggalTampilan,
                                        fontSize = 11.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color =
                                statusColor.copy(
                                    alpha = 0.08f
                                )
                        ) {

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = statusIcon,
                                    contentDescription = statusText,
                                    tint = statusColor,
                                    modifier = Modifier.size(23.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Column {

                                    Text(
                                        text = "Status Pengajuan",
                                        fontSize = 10.sp,
                                        color = TextGray
                                    )

                                    Spacer(
                                        modifier = Modifier.height(2.dp)
                                    )

                                    Text(
                                        text = statusText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }
                        }
                    }
                }

                // ==================================================
                // JALUR APPROVAL
                // ==================================================

                if (approvalChain.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Jalur Approval",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.White
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

                            approvalChain
                                .sortedBy {
                                    approvalUrutan(it["urutan"])
                                }
                                .forEachIndexed {
                                        index,
                                        person ->

                                    val uid =
                                        person["uid"]
                                            ?.toString()
                                            .orEmpty()

                                    val nama =
                                        person["nama"]
                                            ?.toString()
                                            .orEmpty()
                                            .ifBlank {
                                                "Tidak diketahui"
                                            }

                                    val jabatan =
                                        person["jabatan"]
                                            ?.toString()
                                            ?.trim()
                                            ?.uppercase()
                                            .orEmpty()

                                    val savedStatus =
                                        approvalStatuses[uid]
                                            ?.toString()
                                            ?.trim()
                                            ?.lowercase()
                                            .orEmpty()

                                    val stageStatus =
                                        when {
                                            savedStatus == "disetujui" ->
                                                "disetujui"

                                            savedStatus == "ditolak" ->
                                                "ditolak"

                                            uid == currentApproverUid ->
                                                "sedang_diproses"

                                            else ->
                                                "menunggu"
                                        }

                                    DetailApprovalStage(
                                        nama = nama,
                                        jabatan = jabatan,
                                        status = stageStatus,
                                        isCurrent =
                                            uid == currentApproverUid,
                                        isLast =
                                            index ==
                                                    approvalChain.lastIndex
                                    )
                                }

                            // ======================================
                            // APPROVER SAAT INI
                            // ======================================

                            if (
                                currentApproverUid.isNotBlank() &&
                                !approvalLocked
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(14.dp)
                                )

                                Surface(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    shape =
                                        RoundedCornerShape(12.dp),
                                    color =
                                        Color(0xFFFFF7ED)
                                ) {

                                    Column(
                                        modifier =
                                            Modifier.padding(12.dp)
                                    ) {

                                        Text(
                                            text =
                                                "Sedang Menunggu Keputusan",
                                            fontSize = 10.sp,
                                            color = TextGray
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(3.dp)
                                        )

                                        Text(
                                            text =
                                                if (
                                                    currentApproverName.isNotBlank()
                                                ) {
                                                    "$currentApproverName • $currentApproverJabatan"
                                                } else {
                                                    currentApproverJabatan
                                                        .ifBlank {
                                                            "Approver"
                                                        }
                                                },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color =
                                                Color(0xFFD97706)
                                        )
                                    }
                                }
                            }

                            // ======================================
                            // LOCK FINAL
                            // ======================================

                            if (approvalLocked) {

                                Spacer(
                                    modifier =
                                        Modifier.height(14.dp)
                                )

                                Surface(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    shape =
                                        RoundedCornerShape(12.dp),
                                    color =
                                        if (
                                            statusFinal ==
                                            "disetujui"
                                        ) {
                                            SoftGreen
                                        } else {
                                            Color(0xFFFFE7E7)
                                        }
                                ) {

                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Lock,
                                            contentDescription =
                                                "Terkunci",
                                            tint =
                                                if (
                                                    statusFinal ==
                                                    "disetujui"
                                                ) {
                                                    PrimaryGreen
                                                } else {
                                                    Color(0xFFB91C1C)
                                                },
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
                                                text =
                                                    "Keputusan Owner Final",
                                                fontSize = 13.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color = TextDark
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.height(3.dp)
                                            )

                                            Text(
                                                text =
                                                    if (
                                                        statusFinal ==
                                                        "disetujui"
                                                    ) {
                                                        "Pengajuan telah disetujui dan dikunci."
                                                    } else {
                                                        "Pengajuan telah ditolak dan dikunci."
                                                    },
                                                fontSize = 11.sp,
                                                color = TextGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==================================================
                // DETAIL PENGAJUAN
                // ==================================================

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Detail Pengajuan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.White
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

                        DetailInfoRow(
                            icon = Icons.Default.Description,
                            title = "Jenis Pengajuan",
                            value = jenisFinal
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        DetailInfoRow(
                            icon = Icons.Default.CalendarMonth,
                            title = "Tanggal",
                            value = tanggalTampilan
                        )

                        if (tanggalMulai.isNotBlank()) {

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            DetailInfoRow(
                                icon = Icons.Default.Event,
                                title = "Tanggal Mulai",
                                value = tanggalMulai
                            )
                        }

                        if (tanggalSelesai.isNotBlank()) {

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            DetailInfoRow(
                                icon = Icons.Default.Event,
                                title = "Tanggal Selesai",
                                value = tanggalSelesai
                            )
                        }
                    }
                }

                // ==================================================
                // DETAIL WAKTU
                // ==================================================

                if (
                    jamPulang.isNotBlank() ||
                    jamKeluar.isNotBlank() ||
                    jamKembali.isNotBlank()
                ) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Detail Waktu",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.White
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

                            if (jamPulang.isNotBlank()) {

                                DetailInfoRow(
                                    icon = Icons.Default.Schedule,
                                    title = "Jam Pulang",
                                    value = jamPulang
                                )
                            }

                            if (
                                jamPulang.isNotBlank() &&
                                (
                                        jamKeluar.isNotBlank() ||
                                                jamKembali.isNotBlank()
                                        )
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(14.dp)
                                )
                            }

                            if (jamKeluar.isNotBlank()) {

                                DetailInfoRow(
                                    icon = Icons.Default.Schedule,
                                    title = "Jam Keluar",
                                    value = jamKeluar
                                )
                            }

                            if (
                                jamKeluar.isNotBlank() &&
                                jamKembali.isNotBlank()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(14.dp)
                                )
                            }

                            if (jamKembali.isNotBlank()) {

                                DetailInfoRow(
                                    icon = Icons.Default.Schedule,
                                    title = "Jam Kembali",
                                    value = jamKembali
                                )
                            }
                        }
                    }
                }

                // ==================================================
                // ALASAN
                // ==================================================

                if (alasan.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Alasan Pengajuan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                    ) {

                        Text(
                            text = alasan,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = TextDark
                        )
                    }
                }

                // ==================================================
                // CATATAN ADMIN
                // ==================================================

                if (catatanAdmin.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Catatan Admin",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            verticalAlignment =
                                Alignment.Top
                        ) {

                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(11.dp),
                                color =
                                    PrimaryGreen.copy(
                                        alpha = 0.10f
                                    )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Info,
                                    contentDescription =
                                        "Catatan Admin",
                                    tint = PrimaryGreen,
                                    modifier =
                                        Modifier
                                            .padding(9.dp)
                                            .size(22.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = catatanAdmin,
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = TextDark
                            )
                        }
                    }
                }

                // ==================================================
                // STATUS INFO
                // ==================================================

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                when (statusFinal) {

                    "menunggu" -> {

                        InfoStatusCard(
                            icon = Icons.Default.Pending,
                            title = "Pengajuan sedang diproses",
                            message =
                                if (
                                    currentApproverJabatan.isNotBlank()
                                ) {
                                    "Saat ini menunggu keputusan ${currentApproverJabatan}."
                                } else {
                                    "Pengajuan sedang berjalan dalam proses approval."
                                },
                            color = Color(0xFFD97706)
                        )
                    }

                    "disetujui" -> {

                        InfoStatusCard(
                            icon = Icons.Default.CheckCircle,
                            title = "Pengajuan disetujui",
                            message =
                                if (approvalLocked) {
                                    "Pengajuan telah disetujui oleh Owner dan keputusan sudah final."
                                } else {
                                    "Pengajuan kamu telah disetujui."
                                },
                            color = PrimaryGreen
                        )
                    }

                    "ditolak" -> {

                        InfoStatusCard(
                            icon = Icons.Default.Cancel,
                            title = "Pengajuan ditolak",
                            message =
                                if (approvalLocked) {
                                    "Pengajuan telah ditolak oleh Owner dan keputusan sudah final."
                                } else {
                                    "Pengajuan kamu ditolak."
                                },
                            color = Color(0xFFB91C1C)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}

// ==========================================================
// APPROVAL STAGE
// ==========================================================

@Composable
private fun DetailApprovalStage(
    nama: String,
    jabatan: String,
    status: String,
    isCurrent: Boolean,
    isLast: Boolean
) {

    val icon: ImageVector
    val iconColor: Color
    val backgroundColor: Color
    val statusText: String

    when (status) {

        "disetujui" -> {
            icon = Icons.Default.CheckCircle
            iconColor = PrimaryGreen
            backgroundColor = SoftGreen
            statusText = "Disetujui"
        }

        "ditolak" -> {
            icon = Icons.Default.Cancel
            iconColor = Color(0xFFB91C1C)
            backgroundColor = Color(0xFFFFE7E7)
            statusText = "Ditolak"
        }

        "sedang_diproses" -> {
            icon = Icons.Default.Pending
            iconColor = Color(0xFFD97706)
            backgroundColor = Color(0xFFFFF7ED)
            statusText = "Sedang diproses"
        }

        else -> {
            icon = Icons.Default.Pending
            iconColor = TextGray
            backgroundColor = Color(0xFFF3F4F6)
            statusText = "Menunggu"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (isCurrent) {
                            Color(0xFFFFFBEB)
                        } else {
                            Color.Transparent
                        },
                        RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 7.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(
                modifier =
                    Modifier
                        .size(38.dp)
                        .background(
                            backgroundColor,
                            RoundedCornerShape(50.dp)
                        ),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = jabatan.ifBlank { "APPROVER" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = nama,
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
        }

        if (!isLast) {

            Row(
                modifier =
                    Modifier
                        .padding(start = 25.dp)
                        .height(14.dp)
                        .width(2.dp)
                        .background(
                            Color(0xFFD1D5DB)
                        )
            ) {}
        }
    }
}

// ==========================================================
// URUTAN APPROVAL
// ==========================================================

private fun approvalUrutan(
    value: Any?
): Int {

    return when (value) {

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
}

// ==========================================================
// DETAIL INFO ROW
// ==========================================================

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(11.dp),
            color = SoftGreen
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryGreen,
                modifier =
                    Modifier
                        .padding(9.dp)
                        .size(22.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 10.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value.ifBlank { "-" },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }
    }
}

// ==========================================================
// INFO STATUS CARD
// ==========================================================

@Composable
private fun InfoStatusCard(
    icon: ImageVector,
    title: String,
    message: String,
    color: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    color.copy(alpha = 0.07f)
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
            verticalAlignment =
                Alignment.Top
        ) {

            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(11.dp),
                color =
                    color.copy(alpha = 0.12f)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier =
                        Modifier
                            .padding(9.dp)
                            .size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = message,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextGray
                )
            }
        }
    }
}