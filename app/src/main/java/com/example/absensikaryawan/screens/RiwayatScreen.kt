package com.example.absensikaryawan.screens

import android.util.Log

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.PengajuanData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ==========================================================
// MODEL RIWAYAT ABSENSI
// ==========================================================

data class RiwayatAbsensi(
    val id: String,
    val nama: String,
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val status: String,
    val kantor: String,
    val qrData: String,
    val catatan: String
)

// ==========================================================
// MODEL RIWAYAT PENGAJUAN
// ==========================================================

data class RiwayatPengajuan(
    val id: String,
    val jenis: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val alasan: String,
    val status: String,
    val createdAt: String = "",
    val namaPemohon: String = "",
    val jabatanPemohon: String = "",
    val divisiPemohon: String = ""
)

// ==========================================================
// FILTER ABSENSI
// ==========================================================

enum class FilterRiwayat {
    TUJUH_HARI,
    SATU_BULAN,
    SEMUA
}

// ==========================================================
// TAB
// ==========================================================

enum class TabRiwayat {
    ABSENSI,
    PENGAJUAN
}

// ==========================================================
// FILTER STATUS PENGAJUAN
// ==========================================================

enum class FilterStatusPengajuan {
    SEMUA,
    MENUNGGU,
    DISETUJUI,
    DITOLAK
}

// ==========================================================
// FILTER SUMBER PENGAJUAN
// ==========================================================

enum class FilterSumberPengajuan {
    SAYA,
    MENUNGGU_APPROVAL,
    BAWAHAN,
    SEMUA
}

// ==========================================================
// RIWAYAT SCREEN
// ==========================================================

@Composable
fun RiwayatScreen(
    onBack: () -> Unit,
    onDetailClick: (RiwayatPengajuan) -> Unit,
    filterStatusAwal: FilterStatusPengajuan =
        FilterStatusPengajuan.SEMUA,
    refreshKey: Int = 0
) {

    val firebaseAuth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val repository = remember {
        FirestoreRepository()
    }

    // ======================================================
    // TAB
    // ======================================================

    var tabAktif by remember {
        mutableStateOf(TabRiwayat.ABSENSI)
    }

    // ======================================================
    // FILTER ABSENSI
    // ======================================================

    var filterAktif by remember {
        mutableStateOf(FilterRiwayat.TUJUH_HARI)
    }

    // ======================================================
    // FILTER PENGAJUAN
    // ======================================================

    var filterStatusPengajuan by remember {
        mutableStateOf(filterStatusAwal)
    }

    var filterSumberPengajuan by remember {
        mutableStateOf(FilterSumberPengajuan.SAYA)
    }

    // ======================================================
    // USER
    // ======================================================

    var uidUser by remember {
        mutableStateOf("")
    }

    var roleUser by remember {
        mutableStateOf("")
    }

    var namaUser by remember {
        mutableStateOf("")
    }

    // ======================================================
    // DATA
    // ======================================================

    var semuaRiwayat by remember {
        mutableStateOf<List<RiwayatAbsensi>>(emptyList())
    }

    var semuaPengajuan by remember {
        mutableStateOf<List<RiwayatPengajuan>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    // ======================================================
    // LOAD USER
    // ======================================================

    suspend fun loadRoleUser() {

        val firebaseUser =
            firebaseAuth.currentUser
                ?: return

        uidUser = firebaseUser.uid

        try {

            val queryUid =
                firestore
                    .collection("users")
                    .whereEqualTo(
                        "uid",
                        firebaseUser.uid
                    )
                    .limit(1)
                    .get()
                    .await()

            val document =
                if (!queryUid.isEmpty) {

                    queryUid.documents.first()

                } else {

                    val email =
                        firebaseUser.email

                    if (email.isNullOrBlank()) {

                        null

                    } else {

                        firestore
                            .collection("users")
                            .whereEqualTo(
                                "email",
                                email
                            )
                            .limit(1)
                            .get()
                            .await()
                            .documents
                            .firstOrNull()
                    }
                }

            if (document != null) {

                roleUser =
                    document
                        .getString("jabatan")
                        ?.uppercase()
                        ?: ""

                namaUser =
                    document
                        .getString("nama")
                        ?: ""

                Log.d(
                    "RiwayatScreen",
                    "User: $namaUser | Role: $roleUser | UID: $uidUser"
                )
            }

        } catch (e: Exception) {

            Log.e(
                "RiwayatScreen",
                "Gagal mengambil data user",
                e
            )
        }
    }

    // ======================================================
    // LOAD ABSENSI
    // ======================================================

    suspend fun loadAbsensi() {

        val firebaseUser =
            firebaseAuth.currentUser
                ?: return

        try {

            val snapshot =
                firestore
                    .collection("attendance")
                    .whereEqualTo(
                        "uid",
                        firebaseUser.uid
                    )
                    .get()
                    .await()

            semuaRiwayat =
                snapshot.documents.mapNotNull { document ->

                    try {

                        RiwayatAbsensi(
                            id = document.id,

                            nama =
                                document
                                    .getString("nama")
                                    ?: "",

                            tanggal =
                                document
                                    .getString("tanggal")
                                    ?: "",

                            jamMasuk =
                                document
                                    .getString("jamMasuk")
                                    ?: "",

                            jamPulang =
                                document
                                    .getString("jamPulang")
                                    ?: "",

                            status =
                                document
                                    .getString("status")
                                    ?: "",

                            kantor =
                                document
                                    .getString("kantor")
                                    ?: "",

                            qrData =
                                document
                                    .getString("qrData")
                                    ?: "",

                            catatan =
                                document
                                    .getString("catatan")
                                    ?: ""
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "RiwayatScreen",
                            "Gagal membaca attendance ${document.id}",
                            e
                        )

                        null
                    }
                }

        } catch (e: Exception) {

            Log.e(
                "RiwayatScreen",
                "Gagal mengambil riwayat absensi",
                e
            )

            semuaRiwayat = emptyList()
        }
    }

    // ======================================================
    // KONVERSI PENGAJUAN DATA
    // ======================================================

    fun convertPengajuan(
        data: PengajuanData
    ): RiwayatPengajuan {

        return RiwayatPengajuan(
            id = data.id,
            jenis = data.jenis,
            tanggalMulai =
                data.tanggalMulai.ifBlank {
                    data.tanggal
                },
            tanggalSelesai =
                data.tanggalSelesai.ifBlank {
                    data.tanggal
                },
            alasan = data.alasan,
            status = data.status,
            namaPemohon = data.nama
        )
    }

    // ======================================================
    // LOAD PENGAJUAN
    // ======================================================

    suspend fun loadPengajuan() {

        val firebaseUser =
            firebaseAuth.currentUser

        if (firebaseUser == null) {

            semuaPengajuan = emptyList()
            return
        }

        try {

            val hasil: List<PengajuanData> =
                when (filterSumberPengajuan) {

                    // --------------------------------------
                    // PENGAJUAN SAYA
                    // --------------------------------------

                    FilterSumberPengajuan.SAYA -> {

                        val result =
                            repository.getPengajuanSaya(
                                firebaseUser.uid
                            )

                        result.getOrElse {
                            emptyList()
                        }
                    }

                    // --------------------------------------
                    // MENUNGGU APPROVAL
                    // --------------------------------------

                    FilterSumberPengajuan.MENUNGGU_APPROVAL -> {

                        val result =
                            repository.getPengajuanMenunggu()

                        result.getOrElse {
                            emptyList()
                        }
                    }

                    // --------------------------------------
                    // BAWAHAN
                    // --------------------------------------
                    //
                    // Struktur PengajuanData saat ini belum
                    // memiliki approvalChain / approverUid.
                    //
                    // Untuk sementara gunakan data pengajuan
                    // yang sedang menunggu approval.
                    // --------------------------------------

                    FilterSumberPengajuan.BAWAHAN -> {

                        val result =
                            repository.getPengajuanMenunggu()

                        result.getOrElse {
                            emptyList()
                        }
                    }

                    // --------------------------------------
                    // SEMUA
                    // --------------------------------------
                    //
                    // Repository saat ini belum menyediakan
                    // getSemuaPengajuan().
                    //
                    // Gunakan pengajuan menunggu agar tidak
                    // memanggil API repository yang sudah tidak
                    // tersedia.
                    // --------------------------------------

                    FilterSumberPengajuan.SEMUA -> {

                        val result =
                            repository.getPengajuanMenunggu()

                        result.getOrElse {
                            emptyList()
                        }
                    }
                }

            semuaPengajuan =
                hasil.map {
                    convertPengajuan(it)
                }

        } catch (e: Exception) {

            Log.e(
                "RiwayatScreen",
                "Gagal mengambil pengajuan",
                e
            )

            semuaPengajuan = emptyList()
        }
    }

    // ======================================================
    // LOAD SEMUA DATA
    // ======================================================

    LaunchedEffect(
        refreshKey,
        filterSumberPengajuan
    ) {

        isLoading = true

        loadRoleUser()
        loadAbsensi()
        loadPengajuan()

        isLoading = false
    }

    // ======================================================
    // DEFAULT FILTER DARI NOTIFIKASI APPROVAL
    // ======================================================

    LaunchedEffect(
        filterStatusAwal,
        roleUser
    ) {

        if (
            filterStatusAwal ==
            FilterStatusPengajuan.MENUNGGU
        ) {

            if (
                roleUser == "SUPERVISOR" ||
                roleUser == "MANAGER" ||
                roleUser == "HRD" ||
                roleUser == "OWNER"
            ) {

                filterSumberPengajuan =
                    FilterSumberPengajuan.MENUNGGU_APPROVAL
            }
        }
    }

    // ======================================================
    // FILTER ABSENSI
    // ======================================================

    val riwayatAbsensiFiltered =
        remember(
            semuaRiwayat,
            filterAktif
        ) {

            when (filterAktif) {

                // ------------------------------------------
                // 7 HARI
                // ------------------------------------------

                FilterRiwayat.TUJUH_HARI -> {

                    filterAbsensiByDays(
                        data = semuaRiwayat,
                        jumlahHari = 7
                    )
                }

                // ------------------------------------------
                // 1 BULAN
                // ------------------------------------------

                FilterRiwayat.SATU_BULAN -> {

                    filterAbsensiByDays(
                        data = semuaRiwayat,
                        jumlahHari = 30
                    )
                }

                // ------------------------------------------
                // SEMUA
                // ------------------------------------------

                FilterRiwayat.SEMUA -> {

                    semuaRiwayat
                }
            }
        }

    // ======================================================
    // FILTER STATUS PENGAJUAN
    // ======================================================

    val pengajuanFiltered =
        remember(
            semuaPengajuan,
            filterStatusPengajuan
        ) {

            when (filterStatusPengajuan) {

                FilterStatusPengajuan.SEMUA -> {

                    semuaPengajuan
                }

                FilterStatusPengajuan.MENUNGGU -> {

                    semuaPengajuan.filter { item ->

                        item.status.equals(
                            "menunggu",
                            ignoreCase = true
                        )
                    }
                }

                FilterStatusPengajuan.DISETUJUI -> {

                    semuaPengajuan.filter { item ->

                        item.status.equals(
                            "disetujui",
                            ignoreCase = true
                        )
                    }
                }

                FilterStatusPengajuan.DITOLAK -> {

                    semuaPengajuan.filter { item ->

                        item.status.equals(
                            "ditolak",
                            ignoreCase = true
                        )
                    }
                }
            }
        }

    // ======================================================
    // UI
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF7F9FC)
            )
            .statusBarsPadding()
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription =
                        "Kembali",
                    tint =
                        Color(0xFF1F2937)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )

            Text(
                text = "Riwayat",
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    Color(0xFF1F2937)
            )
        }

        // ==================================================
        // TAB ABSENSI / PENGAJUAN
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            TabButtonRiwayat(
                text = "Absensi",
                selected =
                    tabAktif ==
                            TabRiwayat.ABSENSI,
                onClick = {

                    tabAktif =
                        TabRiwayat.ABSENSI
                },
                modifier =
                    Modifier.weight(1f)
            )

            TabButtonRiwayat(
                text = "Pengajuan",
                selected =
                    tabAktif ==
                            TabRiwayat.PENGAJUAN,
                onClick = {

                    tabAktif =
                        TabRiwayat.PENGAJUAN
                },
                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

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

                CircularProgressIndicator()
            }

        } else {

            // ==================================================
            // ABSENSI
            // ==================================================

            if (
                tabAktif ==
                TabRiwayat.ABSENSI
            ) {

                Column(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

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

                        FilterChipSimple(
                            text = "7 Hari",
                            selected =
                                filterAktif ==
                                        FilterRiwayat.TUJUH_HARI,
                            onClick = {

                                filterAktif =
                                    FilterRiwayat.TUJUH_HARI
                            }
                        )

                        FilterChipSimple(
                            text = "1 Bulan",
                            selected =
                                filterAktif ==
                                        FilterRiwayat.SATU_BULAN,
                            onClick = {

                                filterAktif =
                                    FilterRiwayat.SATU_BULAN
                            }
                        )

                        FilterChipSimple(
                            text = "Semua",
                            selected =
                                filterAktif ==
                                        FilterRiwayat.SEMUA,
                            onClick = {

                                filterAktif =
                                    FilterRiwayat.SEMUA
                            }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    if (
                        riwayatAbsensiFiltered.isEmpty()
                    ) {

                        EmptyRiwayatState(
                            text =
                                "Belum ada riwayat absensi."
                        )

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentPadding =
                                PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {

                            items(
                                items =
                                    riwayatAbsensiFiltered,
                                key = {
                                    it.id
                                }
                            ) { item ->

                                RiwayatAbsensiCard(
                                    item = item
                                )
                            }
                        }
                    }
                }

            } else {

                // ==================================================
                // PENGAJUAN
                // ==================================================

                Column(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    // --------------------------------------
                    // FILTER SUMBER
                    // --------------------------------------

                    if (
                        roleUser == "SUPERVISOR" ||
                        roleUser == "MANAGER" ||
                        roleUser == "HRD" ||
                        roleUser == "OWNER"
                    ) {

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
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {

                            FilterChipSimple(
                                text =
                                    "Pengajuan Saya",
                                selected =
                                    filterSumberPengajuan ==
                                            FilterSumberPengajuan.SAYA,
                                onClick = {

                                    filterSumberPengajuan =
                                        FilterSumberPengajuan.SAYA
                                }
                            )

                            FilterChipSimple(
                                text =
                                    "Menunggu Approval",
                                selected =
                                    filterSumberPengajuan ==
                                            FilterSumberPengajuan.MENUNGGU_APPROVAL,
                                onClick = {

                                    filterSumberPengajuan =
                                        FilterSumberPengajuan.MENUNGGU_APPROVAL

                                    filterStatusPengajuan =
                                        FilterStatusPengajuan.MENUNGGU
                                }
                            )

                            if (
                                roleUser != "OWNER"
                            ) {

                                FilterChipSimple(
                                    text =
                                        "Bawahan",
                                    selected =
                                        filterSumberPengajuan ==
                                                FilterSumberPengajuan.BAWAHAN,
                                    onClick = {

                                        filterSumberPengajuan =
                                            FilterSumberPengajuan.BAWAHAN
                                    }
                                )
                            }

                            if (
                                roleUser == "OWNER"
                            ) {

                                FilterChipSimple(
                                    text =
                                        "Semua",
                                    selected =
                                        filterSumberPengajuan ==
                                                FilterSumberPengajuan.SEMUA,
                                    onClick = {

                                        filterSumberPengajuan =
                                            FilterSumberPengajuan.SEMUA
                                    }
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )
                    }

                    // --------------------------------------
                    // FILTER STATUS
                    // --------------------------------------

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
                            Arrangement.spacedBy(
                                8.dp
                            )
                    ) {

                        FilterChipSimple(
                            text = "Semua",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.SEMUA,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.SEMUA
                            }
                        )

                        FilterChipSimple(
                            text = "Menunggu",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.MENUNGGU,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.MENUNGGU
                            }
                        )

                        FilterChipSimple(
                            text = "Disetujui",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.DISETUJUI,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.DISETUJUI
                            }
                        )

                        FilterChipSimple(
                            text = "Ditolak",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.DITOLAK,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.DITOLAK
                            }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // --------------------------------------
                    // LIST PENGAJUAN
                    // --------------------------------------

                    if (
                        pengajuanFiltered.isEmpty()
                    ) {

                        val emptyText =
                            when (
                                filterSumberPengajuan
                            ) {

                                FilterSumberPengajuan.SAYA ->
                                    "Belum ada pengajuan."

                                FilterSumberPengajuan.MENUNGGU_APPROVAL ->
                                    "Tidak ada pengajuan yang menunggu approval."

                                FilterSumberPengajuan.BAWAHAN ->
                                    "Belum ada pengajuan dari bawahan."

                                FilterSumberPengajuan.SEMUA ->
                                    "Belum ada data pengajuan."
                            }

                        EmptyRiwayatState(
                            text = emptyText
                        )

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentPadding =
                                PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {

                            items(
                                items =
                                    pengajuanFiltered,
                                key = {
                                    it.id
                                }
                            ) { item ->

                                RiwayatPengajuanCard(
                                    item = item,
                                    tampilkanPemohon =
                                        filterSumberPengajuan !=
                                                FilterSumberPengajuan.SAYA,
                                    onClick = {

                                        onDetailClick(
                                            item
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// FILTER TANGGAL ABSENSI
// Aman untuk minSdk 24
// ==========================================================

private fun filterAbsensiByDays(
    data: List<RiwayatAbsensi>,
    jumlahHari: Int
): List<RiwayatAbsensi> {

    val formatter =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

    formatter.isLenient = false

    val calendar =
        Calendar.getInstance()

    calendar.add(
        Calendar.DAY_OF_YEAR,
        -(jumlahHari - 1)
    )

    val startDate =
        formatter.format(
            calendar.time
        )

    return data.filter { item ->

        try {

            val tanggal =
                item.tanggal.trim()

            if (tanggal.isBlank()) {
                false
            } else {

                val parsedDate =
                    formatter.parse(tanggal)

                val parsedText =
                    parsedDate?.let {
                        formatter.format(it)
                    }

                parsedText != null &&
                        parsedText >= startDate &&
                        parsedText <= formatter.format(
                    Calendar.getInstance().time
                )
            }

        } catch (
            e: Exception
        ) {

            false
        }
    }
}

// ==========================================================
// TAB BUTTON
// ==========================================================

@Composable
private fun TabButtonRiwayat(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {

            Text(
                text = text,
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
            )
        },
        modifier = modifier
    )
}

// ==========================================================
// FILTER CHIP
// ==========================================================

@Composable
private fun FilterChipSimple(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {

            Text(
                text = text,
                fontSize = 13.sp
            )
        }
    )
}

// ==========================================================
// EMPTY STATE
// ==========================================================

@Composable
private fun EmptyRiwayatState(
    text: String
) {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = text,
            color =
                Color(0xFF6B7280),
            fontSize = 14.sp
        )
    }
}

// ==========================================================
// CARD ABSENSI
// ==========================================================

@Composable
private fun RiwayatAbsensiCard(
    item: RiwayatAbsensi
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
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

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CalendarMonth,
                    contentDescription =
                        null,
                    tint =
                        Color(0xFF16A34A)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        item.tanggal.ifBlank {
                            "-"
                        },
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 16.sp,
                    color =
                        Color(0xFF1F2937)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Row {

                Icon(
                    imageVector =
                        Icons.Default.AccessTime,
                    contentDescription =
                        null,
                    tint =
                        Color(0xFF6B7280)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Masuk: ${
                            item.jamMasuk.ifBlank {
                                "-"
                            }
                        }",
                    color =
                        Color(0xFF4B5563)
                )

                Spacer(
                    modifier =
                        Modifier.width(16.dp)
                )

                Text(
                    text =
                        "Pulang: ${
                            item.jamPulang.ifBlank {
                                "-"
                            }
                        }",
                    color =
                        Color(0xFF4B5563)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            if (
                item.kantor.isNotBlank()
            ) {

                Text(
                    text =
                        "Kantor: ${item.kantor}",
                    fontSize = 13.sp,
                    color =
                        Color(0xFF6B7280)
                )
            }

            if (
                item.status.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Status: ${item.status}",
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color =
                        Color(0xFF374151)
                )
            }
        }
    }
}

// ==========================================================
// CARD PENGAJUAN
// ==========================================================

@Composable
private fun RiwayatPengajuanCard(
    item: RiwayatPengajuan,
    tampilkanPemohon: Boolean,
    onClick: () -> Unit
) {

    val statusLower =
        item.status.lowercase()

    val statusIcon =
        when {

            statusLower.contains("setuju") ->
                Icons.Default.CheckCircle

            statusLower.contains("tolak") ->
                Icons.Default.Close

            else ->
                Icons.Default.Pending
        }

    val statusColor =
        when {

            statusLower.contains("setuju") ->
                Color(0xFF16A34A)

            statusLower.contains("tolak") ->
                Color(0xFFDC2626)

            else ->
                Color(0xFFD97706)
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        onClick = onClick,
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

            // ==============================================
            // HEADER
            // ==============================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Description,
                    contentDescription =
                        null,
                    tint =
                        Color(0xFF16A34A)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        item.jenis.ifBlank {
                            "Pengajuan"
                        },
                    modifier =
                        Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF1F2937)
                )

                Icon(
                    imageVector =
                        statusIcon,
                    contentDescription =
                        null,
                    tint =
                        statusColor
                )
            }

            // ==============================================
            // PEMOHON
            // ==============================================

            if (
                tampilkanPemohon
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    color =
                        Color(0xFFF3F4F6),
                    shape =
                        MaterialTheme.shapes.medium
                ) {

                    Column(
                        modifier =
                            Modifier.padding(10.dp)
                    ) {

                        Text(
                            text = "Pemohon",
                            fontSize = 12.sp,
                            color =
                                Color(0xFF6B7280),
                            fontWeight =
                                FontWeight.Medium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                item.namaPemohon
                                    .ifBlank {
                                        "-"
                                    },
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                Color(0xFF1F2937)
                        )

                        if (
                            item.jabatanPemohon
                                .isNotBlank()
                        ) {

                            Text(
                                text =
                                    item.jabatanPemohon,
                                fontSize = 12.sp,
                                color =
                                    Color(0xFF6B7280)
                            )
                        }

                        if (
                            item.divisiPemohon
                                .isNotBlank()
                        ) {

                            Text(
                                text =
                                    item.divisiPemohon,
                                fontSize = 12.sp,
                                color =
                                    Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // ==============================================
            // TANGGAL
            // ==============================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CalendarMonth,
                    contentDescription =
                        null,
                    tint =
                        Color(0xFF6B7280)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                val tanggalText =
                    if (
                        item.tanggalMulai.isNotBlank() &&
                        item.tanggalSelesai.isNotBlank() &&
                        item.tanggalMulai !=
                        item.tanggalSelesai
                    ) {

                        "${item.tanggalMulai} - ${item.tanggalSelesai}"

                    } else {

                        item.tanggalMulai.ifBlank {
                            item.tanggalSelesai.ifBlank {
                                "-"
                            }
                        }
                    }

                Text(
                    text = tanggalText,
                    fontSize = 13.sp,
                    color =
                        Color(0xFF4B5563)
                )
            }

            // ==============================================
            // ALASAN
            // ==============================================

            if (
                item.alasan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        item.alasan,
                    fontSize = 13.sp,
                    color =
                        Color(0xFF4B5563),
                    maxLines = 3
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // ==============================================
            // STATUS
            // ==============================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        statusIcon,
                    contentDescription =
                        null,
                    tint =
                        statusColor
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        item.status.ifBlank {
                            "Menunggu"
                        },
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        statusColor
                )
            }
        }
    }
}