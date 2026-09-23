package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.absensikaryawan.repository.PengajuanRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
    documentId: String = "",

    // ======================================================
    // DATA APPROVAL
    // ======================================================

    approvalChain: List<String> = emptyList(),
    approvalStatuses: Map<String, String> = emptyMap(),
    currentApproverUid: String = "",
    currentApproverName: String = "",
    currentApproverJabatan: String = "",
    approvalLocked: Boolean = false,

    onBack: () -> Unit
) {

    // ======================================================
    // STATE
    // ======================================================

    val currentUserUid =
        remember {
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid
                .orEmpty()
        }

    val coroutineScope = rememberCoroutineScope()

    var isProcessingDecision by remember {
        mutableStateOf(false)
    }

    var decisionError by remember {
        mutableStateOf("")
    }

    // ======================================================
    // NORMALISASI DATA
    // ======================================================

    val jenisFinal =
        jenis.trim().ifEmpty {
            "Pengajuan"
        }

    val statusFinal =
        status.trim()
            .lowercase()
            .ifEmpty {
                "menunggu"
            }

    val statusText =
        when (statusFinal) {
            "disetujui" -> "Disetujui"
            "ditolak" -> "Ditolak"
            else -> "Menunggu persetujuan"
        }

    // ======================================================
    // WARNA STATUS
    // ======================================================

    val statusColor =
        when (statusFinal) {
            "disetujui" -> PrimaryGreen
            "ditolak" -> Color(0xFFDC2626)
            else -> Color(0xFFD97706)
        }

    val statusBackground =
        when (statusFinal) {
            "disetujui" -> SoftGreen
            "ditolak" -> Color(0xFFFFEBEE)
            else -> Color(0xFFFFF4E5)
        }

    val statusIcon =
        when (statusFinal) {
            "disetujui" -> Icons.Default.CheckCircle
            "ditolak" -> Icons.Default.Cancel
            else -> Icons.Default.Pending
        }

    // ======================================================
    // TANGGAL
    // ======================================================

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
    // JABATAN APPROVAL
    // ======================================================

    val approvalJabatan =
        listOf(
            "Supervisor",
            "Manager",
            "HRD",
            "Owner"
        )

    // ======================================================
    // HITUNG PROGRESS
    // ======================================================

    val totalApproval =
        if (approvalChain.isNotEmpty()) {
            approvalChain.size
        } else {
            4
        }

    val approvedCount =
        if (approvalChain.isNotEmpty()) {
            approvalChain.count { uid ->
                approvalStatuses[uid]
                    ?.lowercase() == "disetujui"
            }
        } else {
            0
        }

    val progressPercentage =
        if (totalApproval > 0) {
            ((approvedCount.toFloat() / totalApproval) * 100)
                .toInt()
        } else {
            0
        }

    val nextApproverIndex =
        approvalChain.indexOfFirst {
            it == currentApproverUid
        }

    val nextApproverJabatan =
        if (
            nextApproverIndex >= 0 &&
            nextApproverIndex < approvalJabatan.size
        ) {
            approvalJabatan[nextApproverIndex]
        } else {
            currentApproverJabatan
        }

    // ==========================================================
    // MAIN
    // ==========================================================

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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextDark
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Detail Pengajuan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Informasi lengkap pengajuan",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            // ==================================================
            // CONTENT
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 16.dp
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // ==================================================
                // JUDUL + STATUS
                // ==================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment =
                                Alignment.Top
                        ) {

                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = SoftGreen
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Description,
                                    contentDescription =
                                        "Pengajuan",
                                    tint = PrimaryGreen,
                                    modifier = Modifier
                                        .padding(11.dp)
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
                                    text = jenisFinal,
                                    fontSize = 18.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(5.dp)
                                )

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.CalendarMonth,
                                        contentDescription =
                                            null,
                                        tint = TextGray,
                                        modifier =
                                            Modifier.size(14.dp)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )

                                    Text(
                                        text =
                                            tanggalTampilan,
                                        fontSize = 12.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        // STATUS BADGE

                        Surface(
                            shape =
                                RoundedCornerShape(10.dp),
                            color = statusBackground
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 11.dp,
                                        vertical = 8.dp
                                    ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        statusIcon,
                                    contentDescription =
                                        statusText,
                                    tint = statusColor,
                                    modifier =
                                        Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(7.dp)
                                )

                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    fontWeight =
                                        FontWeight.SemiBold,
                                    color = statusColor
                                )
                            }
                        }

                        if (documentId.isNotBlank()) {

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Text(
                                text =
                                    "No. Pengajuan: $documentId",
                                fontSize = 10.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                // ==================================================
                // DETAIL PENGAJUAN
                // ==================================================

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                SectionTitle(
                    title = "Informasi Pengajuan"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        DetailInfoRow(
                            icon =
                                Icons.Default.Description,
                            title = "Jenis Pengajuan",
                            value = jenisFinal
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        DetailInfoRow(
                            icon =
                                Icons.Default.CalendarMonth,
                            title = "Tanggal",
                            value =
                                tanggalTampilan
                        )

                        if (tanggalMulai.isNotBlank()) {

                            Spacer(
                                modifier =
                                    Modifier.height(14.dp)
                            )

                            DetailInfoRow(
                                icon =
                                    Icons.Default.Event,
                                title =
                                    "Tanggal Mulai",
                                value =
                                    tanggalMulai
                            )
                        }

                        if (tanggalSelesai.isNotBlank()) {

                            Spacer(
                                modifier =
                                    Modifier.height(14.dp)
                            )

                            DetailInfoRow(
                                icon =
                                    Icons.Default.Event,
                                title =
                                    "Tanggal Selesai",
                                value =
                                    tanggalSelesai
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
                        modifier =
                            Modifier.height(18.dp)
                    )

                    SectionTitle(
                        title = "Detail Waktu"
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
                                    Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            if (
                                jamPulang.isNotBlank()
                            ) {

                                DetailInfoRow(
                                    icon =
                                        Icons.Default.Schedule,
                                    title =
                                        "Jam Pulang",
                                    value =
                                        jamPulang
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

                            if (
                                jamKeluar.isNotBlank()
                            ) {

                                DetailInfoRow(
                                    icon =
                                        Icons.Default.Schedule,
                                    title =
                                        "Jam Keluar",
                                    value =
                                        jamKeluar
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

                            if (
                                jamKembali.isNotBlank()
                            ) {

                                DetailInfoRow(
                                    icon =
                                        Icons.Default.Schedule,
                                    title =
                                        "Jam Kembali",
                                    value =
                                        jamKembali
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
                        modifier =
                            Modifier.height(18.dp)
                    )

                    SectionTitle(
                        title = "Alasan Pengajuan"
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
                                    Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                    ) {

                        Text(
                            text = alasan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = TextDark
                        )
                    }
                }

                // ==================================================
                // APPROVAL
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                SectionTitle(
                    title = "Jalur Persetujuan"
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
                                Color.White
                        ),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        if (approvalChain.isNotEmpty()) {

                            approvalChain.forEachIndexed {
                                    index,
                                    uid ->

                                val jabatan =
                                    approvalJabatan
                                        .getOrElse(index) {
                                            "Approver"
                                        }

                                val approvalStatus =
                                    approvalStatuses[uid]
                                        ?.lowercase()
                                        ?: "belum"

                                val isCurrent =
                                    uid ==
                                            currentApproverUid &&
                                            !approvalLocked

                                ApprovalTimelineItem(
                                    jabatan =
                                        jabatan,
                                    status =
                                        approvalStatus,
                                    isCurrent =
                                        isCurrent,
                                    isLast =
                                        index ==
                                                approvalChain.lastIndex
                                )
                            }

                        } else {

                            EmptyApprovalState()
                        }
                    }
                }

                // ==================================================
                // PROGRESS
                // ==================================================

                if (approvalChain.isNotEmpty()) {

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

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
                                defaultElevation = 1.dp
                            )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
                                            "Progress Approval",
                                        fontSize = 12.sp,
                                        color =
                                            TextGray
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(3.dp)
                                    )

                                    Text(
                                        text =
                                            "$approvedCount/$totalApproval disetujui",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            TextDark
                                    )
                                }

                                Text(
                                    text =
                                        "$progressPercentage%",
                                    fontSize = 20.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        PrimaryGreen
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(
                                        color =
                                            Color(0xFFE5E7EB),
                                        shape =
                                            RoundedCornerShape(10.dp)
                                    )
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(
                                            progressPercentage
                                                .coerceIn(
                                                    0,
                                                    100
                                                ) / 100f
                                        )
                                        .height(8.dp)
                                        .background(
                                            color =
                                                PrimaryGreen,
                                            shape =
                                                RoundedCornerShape(
                                                    10.dp
                                                )
                                        )
                                )
                            }

                            if (
                                statusFinal ==
                                "menunggu" &&
                                nextApproverJabatan
                                    .isNotBlank()
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
                                        SoftGreen
                                ) {

                                    Row(
                                        modifier =
                                            Modifier.padding(
                                                12.dp
                                            ),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.Person,
                                            contentDescription =
                                                null,
                                            tint =
                                                PrimaryGreen,
                                            modifier =
                                                Modifier.size(
                                                    20.dp
                                                )
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(
                                                    9.dp
                                                )
                                        )

                                        Column(
                                            modifier =
                                                Modifier.weight(
                                                    1f
                                                )
                                        ) {

                                            Text(
                                                text =
                                                    "Approver berikutnya",
                                                fontSize = 10.sp,
                                                color =
                                                    TextGray
                                            )

                                            Text(
                                                text =
                                                    if (
                                                        currentApproverName
                                                            .isNotBlank()
                                                    ) {
                                                        "$currentApproverName • $nextApproverJabatan"
                                                    } else {
                                                        nextApproverJabatan
                                                    },
                                                fontSize = 13.sp,
                                                fontWeight =
                                                    FontWeight.SemiBold,
                                                color =
                                                    TextDark
                                            )
                                        }

                                        Icon(
                                            imageVector =
                                                Icons.Default.ArrowForward,
                                            contentDescription =
                                                null,
                                            tint =
                                                PrimaryGreen,
                                            modifier =
                                                Modifier.size(
                                                    18.dp
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==================================================
                // STATUS AKHIR
                // ==================================================

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                when (statusFinal) {

                    "menunggu" -> {

                        InfoStatusCard(
                            icon =
                                Icons.Default.Pending,
                            title =
                                if (
                                    currentApproverJabatan
                                        .isNotBlank()
                                ) {
                                    "Menunggu $currentApproverJabatan"
                                } else {
                                    "Pengajuan sedang diproses"
                                },
                            message =
                                if (
                                    currentApproverName
                                        .isNotBlank()
                                ) {
                                    "Pengajuan sedang menunggu keputusan " +
                                            "$currentApproverName."
                                } else if (
                                    currentApproverJabatan
                                        .isNotBlank()
                                ) {
                                    "Pengajuan sedang menunggu keputusan " +
                                            "$currentApproverJabatan."
                                } else {
                                    "Pengajuan sedang diproses."
                                },
                            color =
                                Color(0xFFD97706)
                        )
                    }

                    "disetujui" -> {

                        InfoStatusCard(
                            icon =
                                Icons.Default.CheckCircle,
                            title =
                                "Pengajuan disetujui",
                            message =
                                "Seluruh tahapan approval telah disetujui.",
                            color =
                                PrimaryGreen
                        )
                    }

                    "ditolak" -> {

                        InfoStatusCard(
                            icon =
                                Icons.Default.Cancel,
                            title =
                                "Pengajuan ditolak",
                            message =
                                "Pengajuan ditolak pada salah satu tahap approval.",
                            color =
                                Color(0xFFDC2626)
                        )
                    }
                }

                // ==================================================
                // TOMBOL APPROVAL
                // ==================================================

                if (
                    documentId.isNotBlank() &&
                    currentUserUid.isNotBlank() &&
                    currentUserUid ==
                    currentApproverUid &&
                    !approvalLocked &&
                    statusFinal == "menunggu"
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    Text(
                        text = "Tindakan Approval",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = TextDark
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

                        OutlinedButton(
                            modifier =
                                Modifier.weight(1f),
                            enabled =
                                !isProcessingDecision,
                            shape =
                                RoundedCornerShape(12.dp),
                            onClick = {

                                coroutineScope.launch {

                                    isProcessingDecision =
                                        true

                                    decisionError = ""

                                    val result =
                                        PengajuanRepository
                                            .updateStatusPengajuan(
                                                documentId =
                                                    documentId,
                                                status =
                                                    "ditolak"
                                            )

                                    isProcessingDecision =
                                        false

                                    if (
                                        result.isSuccess
                                    ) {

                                        onBack()

                                    } else {

                                        decisionError =
                                            result
                                                .exceptionOrNull()
                                                ?.message
                                                ?: "Gagal menolak pengajuan."
                                    }
                                }
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Cancel,
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
                                    if (
                                        isProcessingDecision
                                    ) {
                                        "Memproses…"
                                    } else {
                                        "Tolak"
                                    }
                            )
                        }

                        Button(
                            modifier =
                                Modifier.weight(1f),
                            enabled =
                                !isProcessingDecision,
                            shape =
                                RoundedCornerShape(12.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        PrimaryGreen
                                ),
                            onClick = {

                                coroutineScope.launch {

                                    isProcessingDecision =
                                        true

                                    decisionError = ""

                                    val result =
                                        PengajuanRepository
                                            .updateStatusPengajuan(
                                                documentId =
                                                    documentId,
                                                status =
                                                    "disetujui"
                                            )

                                    isProcessingDecision =
                                        false

                                    if (
                                        result.isSuccess
                                    ) {

                                        onBack()

                                    } else {

                                        decisionError =
                                            result
                                                .exceptionOrNull()
                                                ?.message
                                                ?: "Gagal menyetujui pengajuan."
                                    }
                                }
                            }
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
                                    if (
                                        isProcessingDecision
                                    ) {
                                        "Memproses…"
                                    } else {
                                        "Setujui"
                                    }
                            )
                        }
                    }

                    if (
                        decisionError.isNotBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text = decisionError,
                            color =
                                Color(0xFFDC2626),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(32.dp)
                )
            }
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )
}


// ==========================================================
// APPROVAL TIMELINE
// ==========================================================

@Composable
private fun ApprovalTimelineItem(
    jabatan: String,
    status: String,
    isCurrent: Boolean,
    isLast: Boolean
) {

    val statusNormal =
        status.lowercase()

    val icon =
        when (statusNormal) {

            "disetujui" ->
                Icons.Default.CheckCircle

            "ditolak" ->
                Icons.Default.Cancel

            "menunggu" ->
                Icons.Default.Pending

            else ->
                Icons.Default.Schedule
        }

    val color =
        when (statusNormal) {

            "disetujui" ->
                PrimaryGreen

            "ditolak" ->
                Color(0xFFDC2626)

            "menunggu" ->
                Color(0xFFD97706)

            else ->
                TextGray
        }

    val background =
        when (statusNormal) {

            "disetujui" ->
                SoftGreen

            "ditolak" ->
                Color(0xFFFFEBEE)

            "menunggu" ->
                Color(0xFFFFF4E5)

            else ->
                Color(0xFFF3F4F6)
        }

    val statusText =
        when (statusNormal) {

            "disetujui" ->
                "Disetujui"

            "ditolak" ->
                "Ditolak"

            "menunggu" ->
                "Menunggu keputusan"

            else ->
                "Belum diproses"
        }

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Top
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier =
                    Modifier.size(40.dp),
                shape =
                    CircleShape,
                color =
                    background
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription =
                        jabatan,
                    tint = color,
                    modifier =
                        Modifier.padding(9.dp)
                )
            }

            if (!isLast) {

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(
                            if (
                                statusNormal ==
                                "disetujui"
                            ) {
                                PrimaryGreen.copy(
                                    alpha = 0.35f
                                )
                            } else {
                                Color(0xFFE5E7EB)
                            }
                        )
                )
            }
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        top = 2.dp
                    )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = jabatan,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        if (isCurrent) {
                            color
                        } else {
                            TextDark
                        },
                    modifier =
                        Modifier.weight(1f)
                )

                if (isCurrent) {

                    Surface(
                        shape =
                            RoundedCornerShape(20.dp),
                        color =
                            PrimaryGreen.copy(
                                alpha = 0.10f
                            )
                    ) {

                        Text(
                            text = "Saat ini",
                            modifier =
                                Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                            fontSize = 9.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                PrimaryGreen
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    if (isCurrent) {
                        "Sedang menunggu keputusan"
                    } else {
                        statusText
                    },
                fontSize = 12.sp,
                color = color
            )

            Spacer(
                modifier =
                    Modifier.height(
                        if (isLast) {
                            2.dp
                        } else {
                            10.dp
                        }
                    )
            )
        }
    }
}


// ==========================================================
// EMPTY APPROVAL
// ==========================================================

@Composable
private fun EmptyApprovalState() {

    Column(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Surface(
            modifier =
                Modifier.size(46.dp),
            shape =
                CircleShape,
            color =
                SoftGreen
        ) {

            Icon(
                imageVector =
                    Icons.Default.Schedule,
                contentDescription =
                    null,
                tint =
                    PrimaryGreen,
                modifier =
                    Modifier.padding(11.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "Data approval belum tersedia",
            fontSize = 13.sp,
            fontWeight =
                FontWeight.SemiBold,
            color =
                TextDark
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Jalur persetujuan akan ditampilkan setelah pengajuan diproses.",
            fontSize = 11.sp,
            color =
                TextGray
        )
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
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier.size(40.dp),
            shape =
                RoundedCornerShape(11.dp),
            color =
                SoftGreen
        ) {

            Icon(
                imageVector = icon,
                contentDescription =
                    title,
                tint =
                    PrimaryGreen,
                modifier =
                    Modifier.padding(9.dp)
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
                text = title,
                fontSize = 10.sp,
                color = TextGray
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    value.ifBlank {
                        "-"
                    },
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.SemiBold,
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
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    color.copy(
                        alpha = 0.07f
                    )
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
                modifier =
                    Modifier.size(40.dp),
                shape =
                    RoundedCornerShape(11.dp),
                color =
                    color.copy(
                        alpha = 0.12f
                    )
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription =
                        title,
                    tint = color,
                    modifier =
                        Modifier.padding(9.dp)
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
                    text = title,
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = color
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
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