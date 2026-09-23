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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanData
import com.example.absensikaryawan.repository.PengajuanRepository
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

            daftarPengajuan =
                result.getOrThrow()

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
    // SORT
    // ======================================================

    val daftarTerurut =
        remember(daftarPengajuan) {

            daftarPengajuan.sortedWith(

                compareBy<PengajuanData> {

                    if (
                        it.status
                            .trim()
                            .lowercase() == "menunggu"
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


    // ======================================================
    // MAIN
    // ======================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Background
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
                        start = 20.dp,
                        end = 12.dp,
                        top = 18.dp,
                        bottom = 10.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "Persetujuan",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Kelola pengajuan yang menunggu persetujuan",
                    fontSize = 13.sp,
                    color = TextGray
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
        // CONTENT
        // ==================================================

        when {

            // ==================================================
            // LOADING
            // ==================================================

            isLoading -> {

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

            errorMessage.isNotBlank() -> {

                ApprovalEmptyState(

                    title =
                        "Gagal memuat pengajuan",

                    message =
                        errorMessage,

                    onRefresh = {
                        refreshKey++
                    }
                )
            }


            // ==================================================
            // EMPTY
            // ==================================================

            daftarTerurut.isEmpty() -> {

                ApprovalEmptyState(

                    title =
                        "Tidak ada pengajuan",

                    message =
                        "Belum ada pengajuan yang perlu diproses.",

                    onRefresh = {
                        refreshKey++
                    }
                )
            }


            // ==================================================
            // LIST
            // ==================================================

            else -> {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 8.dp,
                            bottom = 24.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {

                    // ==========================================
                    // SUMMARY
                    // ==========================================

                    item {

                        ApprovalSummaryCard(
                            total =
                                daftarTerurut.size,

                            menunggu =
                                daftarTerurut.count {
                                    it.status
                                        .trim()
                                        .lowercase() == "menunggu"
                                }
                        )
                    }


                    // ==========================================
                    // REQUEST CARDS
                    // ==========================================

                    items(
                        items =
                            daftarTerurut,

                        key = {
                            it.id
                        }
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

                                            repository
                                                .updateStatusPengajuan(
                                                    documentId =
                                                        documentId,

                                                    status =
                                                        "ditolak"
                                                )
                                                .getOrThrow()

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
                }
            }
        }
    }


    // ======================================================
    // APPROVE DIALOG
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
                        "Pengajuan dari ${pengajuan.nama.ifBlank { "Karyawan" }} akan diteruskan ke tahap persetujuan berikutnya."
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

                                    repository
                                        .updateStatusPengajuan(
                                            documentId =
                                                documentId,

                                            status =
                                                "disetujui"
                                        )
                                        .getOrThrow()

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
// SUMMARY CARD
// ==========================================================

@Composable
private fun ApprovalSummaryCard(

    total:
    Int,

    menunggu:
    Int

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

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
                    1.dp
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(46.dp)
                        .clip(
                            RoundedCornerShape(
                                14.dp
                            )
                        )
                        .background(
                            SoftGreen
                        ),

                contentAlignment =
                    Alignment.Center
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
                            23.dp
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "Pengajuan",

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )

                Text(
                    text =
                        "$total pengajuan",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text =
                        "Perlu diproses",

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )

                Text(
                    text =
                        "$menunggu",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        PrimaryGreen
                )
            }
        }
    }
}


// ==========================================================
// REQUEST CARD
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

    val status =
        pengajuan.status
            .trim()
            .lowercase()

    val tanggalMulai =
        pengajuan.tanggalMulai
            .ifBlank {
                pengajuan.tanggal
            }

    val tanggalSelesai =
        pengajuan.tanggalSelesai

    val isWaiting =
        status == "menunggu"


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
                    1.dp
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
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

                Box(

                    modifier =
                        Modifier
                            .size(44.dp)
                            .clip(
                                RoundedCornerShape(
                                    12.dp
                                )
                            )
                            .background(
                                SoftGreen
                            ),

                    contentAlignment =
                        Alignment.Center
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
                        Modifier.width(
                            12.dp
                        )
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            nama,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                2.dp
                            )
                    )

                    Text(
                        text =
                            jenis,

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
                    Modifier.height(
                        16.dp
                    )
            )


            HorizontalDivider(
                color =
                    Color(0xFFE5E7EB)
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // ==============================================
            // INFORMATION
            // ==============================================

            Text(
                text =
                    "Tanggal pengajuan",

                fontSize =
                    11.sp,

                color =
                    TextGray
            )

            Spacer(
                modifier =
                    Modifier.height(
                        3.dp
                    )
            )

            Text(
                text =
                    if (
                        tanggalSelesai.isNotBlank() &&
                        tanggalSelesai != tanggalMulai
                    ) {

                        "$tanggalMulai - $tanggalSelesai"

                    } else {

                        tanggalMulai
                    },

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    TextDark
            )


            if (
                pengajuan.alasan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )

                Text(
                    text =
                        "Alasan",

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )

                Text(
                    text =
                        pengajuan.alasan,

                    fontSize =
                        13.sp,

                    color =
                        TextDark,

                    maxLines =
                        2
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // ==============================================
            // APPROVAL PROGRESS
            // ==============================================

            ApprovalProgress(
                pengajuan =
                    pengajuan
            )


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // ==============================================
            // ACTIONS
            // ==============================================

            if (isWaiting) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    OutlinedButton(

                        onClick =
                            onDetailClick,

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    ) {

                        Text(
                            text =
                                "Lihat Detail",

                            color =
                                TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    5.dp
                                )
                        )

                        Icon(
                            imageVector =
                                Icons.Default.ArrowForward,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(
                                    16.dp
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


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

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFFC62828)
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
                                Modifier.width(
                                    5.dp
                                )
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

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PrimaryGreen
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
                                Modifier.width(
                                    5.dp
                                )
                        )

                        Text(
                            text =
                                "Setujui"
                        )
                    }
                }

            } else {

                OutlinedButton(

                    onClick =
                        onDetailClick,

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                ) {

                    Text(
                        text =
                            "Lihat Detail"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                5.dp
                            )
                    )

                    Icon(
                        imageVector =
                            Icons.Default.ArrowForward,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                16.dp
                            )
                    )
                }
            }
        }
    }
}


// ==========================================================
// APPROVAL PROGRESS
// ==========================================================

@Composable
private fun ApprovalProgress(
    pengajuan: PengajuanData
) {

    val chain =
        if (
            pengajuan.approvalChain.isNotEmpty()
        ) {

            pengajuan.approvalChain

        } else {

            listOf(
                "SUPERVISOR",
                "MANAGER",
                "HRD",
                "OWNER"
            )
        }

    val statuses =
        pengajuan.approvalStatuses

    val approvedCount =
        chain.count { uid ->

            statuses[uid]
                ?.trim()
                ?.lowercase() == "disetujui"
        }

    val total =
        chain.size.coerceAtLeast(1)

    val progress =
        approvedCount.toFloat() /
                total.toFloat()


    Column {

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "Jalur Persetujuan",

                fontSize =
                    12.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TextDark,

                modifier =
                    Modifier.weight(1f)
            )

            Text(
                text =
                    "$approvedCount/$total disetujui",

                fontSize =
                    11.sp,

                color =
                    PrimaryGreen,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(
                        RoundedCornerShape(
                            10.dp
                        )
                    )
                    .background(
                        Color(0xFFE5E7EB)
                    )
        ) {

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth(
                            progress
                                .coerceIn(
                                    0f,
                                    1f
                                )
                        )
                        .height(6.dp)
                        .clip(
                            RoundedCornerShape(
                                10.dp
                            )
                        )
                        .background(
                            PrimaryGreen
                        )
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    9.dp
                )
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(
                    6.dp
                )
        ) {

            ApprovalStep(
                label = "Supervisor",
                status =
                    approvalStepStatus(
                        chain,
                        statuses,
                        0
                    ),
                modifier =
                    Modifier.weight(1f)
            )

            ApprovalStep(
                label = "Manager",
                status =
                    approvalStepStatus(
                        chain,
                        statuses,
                        1
                    ),
                modifier =
                    Modifier.weight(1f)
            )

            ApprovalStep(
                label = "HRD",
                status =
                    approvalStepStatus(
                        chain,
                        statuses,
                        2
                    ),
                modifier =
                    Modifier.weight(1f)
            )

            ApprovalStep(
                label = "Owner",
                status =
                    approvalStepStatus(
                        chain,
                        statuses,
                        3
                    ),
                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}


private fun approvalStepStatus(
    chain: List<String>,
    statuses: Map<String, String>,
    index: Int
): String {

    if (
        index >= chain.size
    ) {
        return "belum"
    }

    return statuses[
        chain[index]
    ]
        ?.trim()
        ?.lowercase()
        ?: "menunggu"
}


// ==========================================================
// APPROVAL STEP
// ==========================================================

@Composable
private fun ApprovalStep(

    label:
    String,

    status:
    String,

    modifier:
    Modifier

) {

    val isApproved =
        status == "disetujui"

    val isWaiting =
        status == "menunggu"


    Column(

        modifier =
            modifier,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(

            modifier =
                Modifier
                    .size(
                        24.dp
                    )
                    .clip(
                        CircleShape
                    )
                    .background(

                        if (
                            isApproved
                        ) {

                            PrimaryGreen

                        } else {

                            Color(0xFFE5E7EB)
                        }
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            if (
                isApproved
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Check,

                    contentDescription =
                        null,

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(
                            14.dp
                        )
                )
            } else {

                Box(

                    modifier =
                        Modifier
                            .size(
                                if (isWaiting) {
                                    8.dp
                                } else {
                                    6.dp
                                }
                            )
                            .clip(
                                CircleShape
                            )
                            .background(
                                if (isWaiting) {
                                    PrimaryGreen
                                } else {
                                    Color(0xFF9CA3AF)
                                }
                            )
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    4.dp
                )
        )

        Text(
            text =
                label,

            fontSize =
                9.sp,

            fontWeight =
                if (
                    isApproved ||
                    isWaiting
                ) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },

            color =
                if (
                    isApproved ||
                    isWaiting
                ) {
                    TextDark
                } else {
                    TextGray
                }
        )
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

                SoftGreen,

                PrimaryGreen            )

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
                    background,
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
    String,

    onRefresh:
        () -> Unit

) {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            modifier =
                Modifier.padding(
                    30.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(

                modifier =
                    Modifier
                        .size(
                            64.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            SoftGreen
                        ),

                contentAlignment =
                    Alignment.Center
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
                            30.dp
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )

            Text(
                text =
                    title,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )

            Text(
                text =
                    message,

                fontSize =
                    12.sp,

                color =
                    TextGray
            )

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            OutlinedButton(
                onClick =
                    onRefresh,

                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            16.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            6.dp
                        )
                )

                Text(
                    text =
                        "Muat Ulang"
                )
            }
        }
    }
}