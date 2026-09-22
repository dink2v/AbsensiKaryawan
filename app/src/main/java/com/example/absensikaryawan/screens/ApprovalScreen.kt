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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanData
import com.example.absensikaryawan.repository.PengajuanRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================================
// APPROVAL SCREEN
// ==========================================================

@Composable
fun ApprovalScreen(
    initialDocumentId: String = "",
    onDetailClick: (PengajuanData) -> Unit
) {

    val repository = remember {
        PengajuanRepository
    }

    val coroutineScope = rememberCoroutineScope()

    var daftarPengajuan by remember {
        mutableStateOf<List<PengajuanData>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var refreshKey by remember {
        mutableStateOf(0)
    }

    var dialogPengajuan by remember {
        mutableStateOf<PengajuanData?>(null)
    }

    // ======================================================
    // LOAD DATA
    // ======================================================

    LaunchedEffect(refreshKey) {

        isLoading = true
        errorMessage = ""

        try {

            val result =
                repository.ambilPengajuanUntukApproval()

            val data =
                result.getOrThrow()

            daftarPengajuan = data

        } catch (exception: Exception) {

            errorMessage =
                exception.message
                    ?: "Gagal mengambil data pengajuan."

        } finally {

            isLoading = false
        }
    }

    // ======================================================
    // AUTO OPEN DARI NOTIFIKASI
    // ======================================================

    LaunchedEffect(
        daftarPengajuan,
        initialDocumentId
    ) {

        if (
            initialDocumentId.isBlank() ||
            daftarPengajuan.isEmpty()
        ) {
            return@LaunchedEffect
        }

        val pengajuanDituju =
            daftarPengajuan.firstOrNull { pengajuan ->

                pengajuan.id
                    .trim()
                    .equals(
                        initialDocumentId.trim(),
                        ignoreCase = true
                    )
            }

        if (pengajuanDituju != null) {

            onDetailClick(
                pengajuanDituju
            )
        }
    }

    // ======================================================
    // SORT DATA
    // ======================================================

    val daftarTerurut =
        remember(daftarPengajuan) {

            daftarPengajuan.sortedWith(
                compareBy<PengajuanData> {

                    if (
                        it.status
                            .lowercase()
                            .trim() == "menunggu"
                    ) {
                        0
                    } else {
                        1
                    }

                }.thenByDescending {

                    it.tanggal
                }
            )
        }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(
                    horizontal = 16.dp
                )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        bottom = 14.dp
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
                        "Approval Pengajuan",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )

                Spacer(
                    modifier =
                        Modifier.size(3.dp)
                )

                Text(
                    text =
                        "Daftar pengajuan yang perlu diproses",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }

            IconButton(
                onClick = {
                    refreshKey++
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,

                    contentDescription =
                        "Refresh",

                    tint =
                        PrimaryGreen
                )
            }
        }

        // ==================================================
        // LOADING
        // ==================================================

        if (isLoading) {

            Box(

                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        PrimaryGreen
                )
            }
        }

        // ==================================================
        // ERROR
        // ==================================================

        else if (errorMessage.isNotBlank()) {

            ApprovalEmptyState(

                title =
                    "Gagal memuat pengajuan",

                message =
                    errorMessage
            )
        }

        // ==================================================
        // EMPTY
        // ==================================================

        else if (daftarTerurut.isEmpty()) {

            ApprovalEmptyState(

                title =
                    "Belum ada pengajuan",

                message =
                    "Tidak ada pengajuan yang tersedia untuk diproses."
            )
        }

        // ==================================================
        // LIST
        // ==================================================

        else {

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                items(
                    items = daftarTerurut,
                    key = { it.id }
                ) { pengajuan ->

                    ApprovalRequestCard(

                        pengajuan =
                            pengajuan,

                        onDetailClick = {

                            onDetailClick(
                                pengajuan
                            )
                        },

                        onSetujui = {

                            dialogPengajuan =
                                pengajuan
                        },

                        onTolak = {

                            val documentId =
                                pengajuan.id

                            if (
                                documentId.isNotBlank()
                            ) {

                                coroutineScope.launch {

                                    try {

                                        val result =
                                            repository.updateStatusPengajuan(
                                                documentId = documentId,
                                                status = "ditolak"
                                            )

                                        result.getOrThrow()

                                        delay(300)

                                        refreshKey++

                                    } catch (
                                        exception: Exception
                                    ) {

                                        errorMessage =
                                            exception.message
                                                ?: "Gagal menolak pengajuan."
                                    }
                                }
                            }
                        }
                    )
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // ======================================================
    // DIALOG APPROVE
    // ======================================================

    dialogPengajuan?.let { pengajuan ->

        AlertDialog(

            onDismissRequest = {

                dialogPengajuan =
                    null
            },

            title = {

                Text(
                    text =
                        "Setujui Pengajuan?"
                )
            },

            text = {

                Text(
                    text =
                        "Apakah Anda yakin ingin menyetujui pengajuan ini?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val documentId =
                            pengajuan.id

                        dialogPengajuan =
                            null

                        if (
                            documentId.isNotBlank()
                        ) {

                            coroutineScope.launch {

                                try {

                                    val result =
                                        repository
                                            .updateStatusPengajuan(
                                                documentId =
                                                    documentId,

                                                status =
                                                    "disetujui"
                                            )

                                    result.getOrThrow()

                                    delay(300)

                                    refreshKey++

                                } catch (
                                    exception: Exception
                                ) {

                                    errorMessage =
                                        exception.message
                                            ?: "Gagal menyetujui pengajuan."
                                }
                            }
                        }
                    }
                ) {

                    Text(
                        text =
                            "Setujui",

                        color =
                            PrimaryGreen,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        dialogPengajuan =
                            null
                    }
                ) {

                    Text(
                        text =
                            "Batal"
                    )
                }
            }
        )
    }
}


// ==========================================================
// CARD APPROVAL
// ==========================================================

@Composable
private fun ApprovalRequestCard(

    pengajuan:
    PengajuanData,

    onDetailClick:
        () -> Unit,

    onSetujui:
        () -> Unit,

    onTolak:
        () -> Unit

) {

    val nama =
        pengajuan.nama
            .ifBlank {
                "Karyawan"
            }

    val jenis =
        pengajuan.jenis
            .ifBlank {
                "Pengajuan"
            }

    val tanggalMulai =
        pengajuan.tanggalMulai

    val tanggalSelesai =
        pengajuan.tanggalSelesai

    val alasan =
        pengajuan.alasan

    val status =
        pengajuan.status
            .lowercase()
            .trim()

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onDetailClick()
                },

        shape =
            RoundedCornerShape(
                18.dp
            ),

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
                    .padding(
                        16.dp
                    )
        ) {

            // ==============================================
            // HEADER
            // ==============================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(

                    modifier =
                        Modifier
                            .size(44.dp)
                            .background(

                                color =
                                    Color(0xFFE8F5E9),

                                shape =
                                    RoundedCornerShape(
                                        12.dp
                                    )
                            ),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Description,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(
                                22.dp
                            )
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
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(

                        text =
                            nama,

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }

                ApprovalStatusBadge(
                    status =
                        status
                )
            }

            Spacer(
                modifier =
                    Modifier.size(14.dp)
            )

            // ==============================================
            // TANGGAL
            // ==============================================

            if (
                tanggalMulai.isNotBlank()
            ) {

                Text(

                    text =
                        if (
                            tanggalSelesai.isNotBlank() &&
                            tanggalSelesai != tanggalMulai
                        ) {

                            "Tanggal: " +
                                    tanggalMulai +
                                    " s/d " +
                                    tanggalSelesai

                        } else {

                            "Tanggal: " +
                                    tanggalMulai
                        },

                    fontSize =
                        12.sp,

                    color =
                        TextDark
                )
            }

            // ==============================================
            // ALASAN
            // ==============================================

            if (
                alasan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.size(5.dp)
                )

                Text(

                    text =
                        "Alasan: $alasan",

                    fontSize =
                        11.sp,

                    color =
                        TextGray,

                    maxLines =
                        3
                )
            }

            Spacer(
                modifier =
                    Modifier.size(14.dp)
            )

            // ==============================================
            // BUTTON
            // ==============================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {

                Button(

                    onClick =
                        onTolak,

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFFC62828)
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Close,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                17.dp
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Text(
                        text =
                            "Tolak"
                    )
                }

                Button(

                    onClick =
                        onSetujui,

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                PrimaryGreen
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Check,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                17.dp
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Text(
                        text =
                            "Setujui"
                    )
                }
            }
        }
    }
}


// ==========================================================
// STATUS BADGE
// ==========================================================

@Composable
private fun ApprovalStatusBadge(
    status: String
) {

    val (
        text,
        background,
        foreground
    ) =
        when (status) {

            "menunggu" -> Triple(

                "Menunggu",

                Color(0xFFFFF3E0),

                Color(0xFFD97706)
            )

            "disetujui" -> Triple(

                "Disetujui",

                Color(0xFFE8F5E9),

                Color(0xFF2E7D32)
            )

            "ditolak" -> Triple(

                "Ditolak",

                Color(0xFFFFEBEE),

                Color(0xFFC62828)
            )

            else -> Triple(

                status.ifBlank {
                    "Menunggu"
                },

                Color(0xFFF3F4F6),

                TextGray
            )
        }

    Row(

        modifier =
            Modifier
                .background(

                    color =
                        background,

                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text =
                text,

            fontSize =
                10.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                foreground
        )
    }
}


// ==========================================================
// EMPTY STATE
// ==========================================================

@Composable
private fun ApprovalEmptyState(

    title:
    String,

    message:
    String

) {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(

                imageVector =
                    Icons.Default.Description,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(
                        42.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.size(12.dp)
            )

            Text(

                text =
                    title,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.size(5.dp)
            )

            Text(

                text =
                    message,

                fontSize =
                    12.sp,

                color =
                    TextGray
            )
        }
    }
}