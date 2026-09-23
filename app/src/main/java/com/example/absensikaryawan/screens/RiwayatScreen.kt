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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SupervisorAccount
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ==========================================================
// WARNA
// ==========================================================

private object RiwayatScreenColors {

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

    val BlueSoft = Color(0xFFEFF6FF)
    val Blue = Color(0xFF2563EB)
}

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
    TEAM,
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

    val scope = rememberCoroutineScope()

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

                Log.d(
                    "RiwayatScreen",
                    "Role: $roleUser | UID: $uidUser"
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
    // CEK APPROVER
    // ======================================================

    fun isApprover(): Boolean {

        return roleUser == "SUPERVISOR" ||
                roleUser == "MANAGER" ||
                roleUser == "HRD" ||
                roleUser == "OWNER"
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
    // KONVERSI PENGAJUAN
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

                        repository
                            .getPengajuanSaya(
                                firebaseUser.uid
                            )
                            .getOrElse {
                                emptyList()
                            }
                    }

                    // --------------------------------------
                    // MENUNGGU APPROVAL
                    // --------------------------------------

                    FilterSumberPengajuan.MENUNGGU_APPROVAL -> {

                        repository
                            .getPengajuanMenungguApproval(
                                firebaseUser.uid
                            )
                            .getOrElse {
                                emptyList()
                            }
                    }

                    // --------------------------------------
                    // TEAM
                    // --------------------------------------

                    FilterSumberPengajuan.TEAM -> {

                        /*
                         * Struktur Team belum tersedia di users.
                         *
                         * Jangan mengambil seluruh pengajuan,
                         * karena itu akan membuat data Team salah.
                         */
                        emptyList()
                    }

                    // --------------------------------------
                    // SEMUA
                    // --------------------------------------

                    FilterSumberPengajuan.SEMUA -> {

                        /*
                         * Belum ada repository getSemuaPengajuan().
                         *
                         * Untuk sementara kosong agar tidak
                         * menampilkan data yang salah.
                         */
                        emptyList()
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
    // LOAD DATA
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
    // DEFAULT DARI NOTIFIKASI
    // ======================================================

    LaunchedEffect(
        filterStatusAwal,
        roleUser
    ) {

        if (
            filterStatusAwal ==
            FilterStatusPengajuan.MENUNGGU &&
            isApprover()
        ) {

            filterSumberPengajuan =
                FilterSumberPengajuan.MENUNGGU_APPROVAL
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

                FilterRiwayat.TUJUH_HARI -> {

                    filterAbsensiByDays(
                        data = semuaRiwayat,
                        jumlahHari = 7
                    )
                }

                FilterRiwayat.SATU_BULAN -> {

                    filterAbsensiByDays(
                        data = semuaRiwayat,
                        jumlahHari = 30
                    )
                }

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

                FilterStatusPengajuan.SEMUA ->
                    semuaPengajuan

                FilterStatusPengajuan.MENUNGGU ->
                    semuaPengajuan.filter {
                        it.status.equals(
                            "menunggu",
                            ignoreCase = true
                        )
                    }

                FilterStatusPengajuan.DISETUJUI ->
                    semuaPengajuan.filter {
                        it.status.equals(
                            "disetujui",
                            ignoreCase = true
                        )
                    }

                FilterStatusPengajuan.DITOLAK ->
                    semuaPengajuan.filter {
                        it.status.equals(
                            "ditolak",
                            ignoreCase = true
                        )
                    }
            }
        }

    // ======================================================
    // MAIN UI
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RiwayatScreenColors.Background
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
                    start = 8.dp,
                    end = 16.dp,
                    top = 8.dp,
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
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription =
                        "Kembali",
                    tint =
                        RiwayatScreenColors.Dark
                )
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "Riwayat",
                    fontSize = 22.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        RiwayatScreenColors.Dark
                )

                Text(
                    text =
                        if (
                            tabAktif ==
                            TabRiwayat.ABSENSI
                        ) {
                            "Riwayat kehadiran"
                        } else {
                            "Riwayat dan status pengajuan"
                        },
                    fontSize = 12.sp,
                    color =
                        RiwayatScreenColors.Gray
                )
            }

            IconButton(
                onClick = {

                    scope.launch {

                        isLoading = true

                        loadRoleUser()
                        loadAbsensi()
                        loadPengajuan()

                        isLoading = false
                    }
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription =
                        "Refresh",
                    tint =
                        RiwayatScreenColors.Green
                )
            }
        }

        // ==================================================
        // TAB UTAMA
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

            MainTab(
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

            MainTab(
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

                CircularProgressIndicator(
                    color =
                        RiwayatScreenColors.Green
                )
            }

        } else {

            // ==================================================
            // TAB ABSENSI
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

                        SimpleChip(
                            text = "7 Hari",
                            selected =
                                filterAktif ==
                                        FilterRiwayat.TUJUH_HARI,
                            onClick = {

                                filterAktif =
                                    FilterRiwayat.TUJUH_HARI
                            }
                        )

                        SimpleChip(
                            text = "1 Bulan",
                            selected =
                                filterAktif ==
                                        FilterRiwayat.SATU_BULAN,
                            onClick = {

                                filterAktif =
                                    FilterRiwayat.SATU_BULAN
                            }
                        )

                        SimpleChip(
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
                            icon =
                                Icons.Default.CalendarMonth,
                            title =
                                "Belum ada riwayat absensi",
                            message =
                                "Data kehadiran akan muncul di sini."
                        )

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentPadding =
                                PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 24.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
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
                // TAB PENGAJUAN
                // ==================================================

                Column(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    // ----------------------------------------------
                    // SUMBER PENGAJUAN
                    // ----------------------------------------------

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

                        SourceChip(
                            text =
                                "Pengajuan Saya",
                            icon =
                                Icons.Default.Description,
                            selected =
                                filterSumberPengajuan ==
                                        FilterSumberPengajuan.SAYA,
                            onClick = {

                                filterSumberPengajuan =
                                    FilterSumberPengajuan.SAYA

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.SEMUA
                            }
                        )

                        if (isApprover()) {

                            SourceChip(
                                text =
                                    "Menunggu Approval",
                                icon =
                                    Icons.Default.SupervisorAccount,
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

                            SourceChip(
                                text = "Team",
                                icon =
                                    Icons.Default.Groups,
                                selected =
                                    filterSumberPengajuan ==
                                            FilterSumberPengajuan.TEAM,
                                onClick = {

                                    filterSumberPengajuan =
                                        FilterSumberPengajuan.TEAM

                                    filterStatusPengajuan =
                                        FilterStatusPengajuan.SEMUA
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    // ----------------------------------------------
                    // LABEL STATUS SESUAI SUMBER
                    // ----------------------------------------------

                    Text(
                        text =
                            when (filterSumberPengajuan) {

                                FilterSumberPengajuan.SAYA ->
                                    "Status Pengajuan Saya"

                                FilterSumberPengajuan.MENUNGGU_APPROVAL ->
                                    "Status Menunggu Approval"

                                FilterSumberPengajuan.TEAM ->
                                    "Status Team"

                                FilterSumberPengajuan.SEMUA ->
                                    "Status Pengajuan"
                            },
                        modifier =
                            Modifier.padding(
                                horizontal = 16.dp
                            ),
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            RiwayatScreenColors.Gray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    // ----------------------------------------------
                    // FILTER STATUS
                    // ----------------------------------------------

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

                        SimpleChip(
                            text = "Semua",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.SEMUA,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.SEMUA
                            }
                        )

                        SimpleChip(
                            text = "Menunggu",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.MENUNGGU,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.MENUNGGU
                            }
                        )

                        SimpleChip(
                            text = "Disetujui",
                            selected =
                                filterStatusPengajuan ==
                                        FilterStatusPengajuan.DISETUJUI,
                            onClick = {

                                filterStatusPengajuan =
                                    FilterStatusPengajuan.DISETUJUI
                            }
                        )

                        SimpleChip(
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

                    // ----------------------------------------------
                    // TEAM EMPTY
                    // ----------------------------------------------

                    if (
                        filterSumberPengajuan ==
                        FilterSumberPengajuan.TEAM
                    ) {

                        EmptyRiwayatState(
                            icon =
                                Icons.Default.Groups,
                            title =
                                "Team belum tersedia",
                            message =
                                "Struktur Team belum diatur. " +
                                        "Data Team akan ditampilkan setelah " +
                                        "relasi Team tersedia."
                        )

                    } else if (
                        pengajuanFiltered.isEmpty()
                    ) {

                        val title =
                            when (
                                filterStatusPengajuan
                            ) {

                                FilterStatusPengajuan.SEMUA ->
                                    "Belum ada pengajuan"

                                FilterStatusPengajuan.MENUNGGU ->
                                    "Tidak ada pengajuan menunggu"

                                FilterStatusPengajuan.DISETUJUI ->
                                    "Tidak ada pengajuan disetujui"

                                FilterStatusPengajuan.DITOLAK ->
                                    "Tidak ada pengajuan ditolak"
                            }

                        val message =
                            when (
                                filterSumberPengajuan
                            ) {

                                FilterSumberPengajuan.SAYA ->
                                    "Pengajuan yang kamu buat akan muncul di sini."

                                FilterSumberPengajuan.MENUNGGU_APPROVAL ->
                                    "Tidak ada pengajuan yang sedang menunggu persetujuanmu."

                                else ->
                                    "Data pengajuan akan muncul di sini."
                            }

                        EmptyRiwayatState(
                            icon =
                                Icons.Default.Description,
                            title = title,
                            message = message
                        )

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentPadding =
                                PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 24.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
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
                                        filterSumberPengajuan ==
                                                FilterSumberPengajuan.MENUNGGU_APPROVAL ||
                                                filterSumberPengajuan ==
                                                FilterSumberPengajuan.TEAM,
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
// FILTER ABSENSI BY DAYS
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

    val today =
        formatter.format(
            Calendar.getInstance().time
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
                        parsedText <= today
            }

        } catch (
            e: Exception
        ) {

            false
        }
    }
}

// ==========================================================
// MAIN TAB
// ==========================================================

@Composable
private fun MainTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier =
            modifier.clickableNoRipple {
                onClick()
            },
        shape =
            RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        RiwayatScreenColors.Green
                    } else {
                        RiwayatScreenColors.Card
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (selected) {
                        1.dp
                    } else {
                        0.dp
                    }
            )
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 11.dp
                    ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = text,
                fontSize = 13.sp,
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
                        RiwayatScreenColors.Gray
                    }
            )
        }
    }
}

// ==========================================================
// SOURCE CHIP
// ==========================================================

@Composable
private fun SourceChip(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.clickableNoRipple {
                onClick()
            },
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        RiwayatScreenColors.Green
                    } else {
                        RiwayatScreenColors.Card
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (selected) {
                        1.dp
                    } else {
                        0.dp
                    }
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 9.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint =
                    if (selected) {
                        Color.White
                    } else {
                        RiwayatScreenColors.Gray
                    },
                modifier =
                    Modifier.size(16.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Text(
                text = text,
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
                        RiwayatScreenColors.Gray
                    }
            )
        }
    }
}

// ==========================================================
// SIMPLE CHIP
// ==========================================================

@Composable
private fun SimpleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.clickableNoRipple {
                onClick()
            },
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        RiwayatScreenColors.Green
                    } else {
                        RiwayatScreenColors.Card
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (selected) {
                        1.dp
                    } else {
                        0.dp
                    }
            )
    ) {

        Text(
            text = text,
            modifier =
                Modifier.padding(
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
                    RiwayatScreenColors.Gray
                }
        )
    }
}

// ==========================================================
// EMPTY STATE
// ==========================================================

@Composable
private fun EmptyRiwayatState(
    icon: ImageVector,
    title: String,
    message: String
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
                    horizontal = 30.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint =
                    RiwayatScreenColors.Green,
                modifier =
                    Modifier.size(44.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    RiwayatScreenColors.Dark
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = message,
                fontSize = 13.sp,
                color =
                    RiwayatScreenColors.Gray
            )
        }
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
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    RiwayatScreenColors.Card
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
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SurfaceIcon(
                    icon =
                        Icons.Default.CalendarMonth,
                    background =
                        RiwayatScreenColors.GreenSoft,
                    tint =
                        RiwayatScreenColors.Green
                )

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
                            item.tanggal.ifBlank {
                                "-"
                            },
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            RiwayatScreenColors.Dark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (item.kantor.isNotBlank()) {
                                item.kantor
                            } else {
                                "Kehadiran"
                            },
                        fontSize = 12.sp,
                        color =
                            RiwayatScreenColors.Gray
                    )
                }

                if (item.status.isNotBlank()) {

                    AttendanceStatusBadge(
                        status =
                            item.status
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                TimeInfo(
                    label = "Masuk",
                    value =
                        item.jamMasuk.ifBlank {
                            "-"
                        },
                    modifier =
                        Modifier.weight(1f)
                )

                TimeInfo(
                    label = "Pulang",
                    value =
                        item.jamPulang.ifBlank {
                            "-"
                        },
                    modifier =
                        Modifier.weight(1f)
                )
            }

            if (item.catatan.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text = "Catatan",
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        RiwayatScreenColors.Gray
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text = item.catatan,
                    fontSize = 12.sp,
                    color =
                        RiwayatScreenColors.Dark,
                    maxLines = 3,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================================
// TIME INFO
// ==========================================================

@Composable
private fun TimeInfo(
    label: String,
    value: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier,
        shape =
            RoundedCornerShape(10.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    RiwayatScreenColors.Background
            )
    ) {

        Row(
            modifier =
                Modifier.padding(10.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.AccessTime,
                contentDescription = null,
                tint =
                    RiwayatScreenColors.Gray,
                modifier =
                    Modifier.size(17.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(7.dp)
            )

            Column {

                Text(
                    text = label,
                    fontSize = 10.sp,
                    color =
                        RiwayatScreenColors.Gray
                )

                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        RiwayatScreenColors.Dark
                )
            }
        }
    }
}

// ==========================================================
// ATTENDANCE STATUS
// ==========================================================

@Composable
private fun AttendanceStatusBadge(
    status: String
) {

    Box(
        modifier =
            Modifier
                .background(
                    color =
                        RiwayatScreenColors.GreenSoft,
                    shape =
                        RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 9.dp,
                    vertical = 5.dp
                )
    ) {

        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight =
                FontWeight.Bold,
            color =
                RiwayatScreenColors.Green
        )
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

    val status =
        normalizePengajuanStatus(
            item.status
        )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickableNoRipple {
                    onClick()
                },
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    RiwayatScreenColors.Card
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

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SurfaceIcon(
                    icon =
                        Icons.Default.Description,
                    background =
                        RiwayatScreenColors.GreenSoft,
                    tint =
                        RiwayatScreenColors.Green
                )

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
                            item.jenis.ifBlank {
                                "Pengajuan"
                            },
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            RiwayatScreenColors.Dark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            tanggalPengajuanText(
                                item
                            ),
                        fontSize = 12.sp,
                        color =
                            RiwayatScreenColors.Gray
                    )
                }

                StatusBadgeRiwayat(
                    status = status
                )
            }

            // ==================================================
            // PEMOHON
            // ==================================================

            if (
                tampilkanPemohon &&
                item.namaPemohon.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color =
                                    RiwayatScreenColors.BlueSoft,
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
                            Icons.Default.Groups,
                        contentDescription = null,
                        tint =
                            RiwayatScreenColors.Blue,
                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Column {

                        Text(
                            text = "Pemohon",
                            fontSize = 10.sp,
                            color =
                                RiwayatScreenColors.Gray
                        )

                        Text(
                            text =
                                item.namaPemohon,
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                RiwayatScreenColors.Dark
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // ==================================================
            // STATUS INFO
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                pengajuanStatusBackground(
                                    status
                                ),
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
                        pengajuanStatusIcon(
                            status
                        ),
                    contentDescription = null,
                    tint =
                        pengajuanStatusColor(
                            status
                        ),
                    modifier =
                        Modifier.size(17.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        pengajuanStatusInfo(
                            status
                        ),
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color =
                        pengajuanStatusColor(
                            status
                        )
                )
            }

            // ==================================================
            // ALASAN
            // ==================================================

            if (
                item.alasan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text = "Alasan",
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        RiwayatScreenColors.Gray
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        item.alasan,
                    fontSize = 13.sp,
                    color =
                        RiwayatScreenColors.Dark,
                    maxLines = 3,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

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
                        RiwayatScreenColors.Green
                )
            }
        }
    }
}

// ==========================================================
// SURFACE ICON
// ==========================================================

@Composable
private fun SurfaceIcon(
    icon: ImageVector,
    background: Color,
    tint: Color
) {

    Box(
        modifier =
            Modifier
                .size(44.dp)
                .background(
                    color = background,
                    shape =
                        RoundedCornerShape(12.dp)
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier =
                Modifier.size(22.dp)
        )
    }
}

// ==========================================================
// STATUS BADGE
// ==========================================================

@Composable
private fun StatusBadgeRiwayat(
    status: String
) {

    Box(
        modifier =
            Modifier
                .background(
                    color =
                        pengajuanStatusBackground(
                            status
                        ),
                    shape =
                        RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
    ) {

        Text(
            text =
                pengajuanStatusLabel(
                    status
                ),
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Bold,
            color =
                pengajuanStatusColor(
                    status
                )
        )
    }
}

// ==========================================================
// PENGAJUAN STATUS
// ==========================================================

private fun normalizePengajuanStatus(
    status: String
): String {

    return status
        .trim()
        .lowercase()
        .ifBlank {
            "menunggu"
        }
}

private fun pengajuanStatusLabel(
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

private fun pengajuanStatusColor(
    status: String
): Color {

    return when (status) {

        "disetujui" ->
            RiwayatScreenColors.Green

        "ditolak" ->
            RiwayatScreenColors.Red

        else ->
            RiwayatScreenColors.Orange
    }
}

private fun pengajuanStatusBackground(
    status: String
): Color {

    return when (status) {

        "disetujui" ->
            RiwayatScreenColors.GreenSoft

        "ditolak" ->
            RiwayatScreenColors.RedSoft

        else ->
            RiwayatScreenColors.OrangeSoft
    }
}

private fun pengajuanStatusIcon(
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

private fun pengajuanStatusInfo(
    status: String
): String {

    return when (status) {

        "disetujui" ->
            "Pengajuan telah disetujui seluruh approver."

        "ditolak" ->
            "Pengajuan ditolak pada salah satu tahap approval."

        else ->
            "Pengajuan masih dalam proses approval."
    }
}

// ==========================================================
// TANGGAL PENGAJUAN
// ==========================================================

private fun tanggalPengajuanText(
    item: RiwayatPengajuan
): String {

    return when {

        item.tanggalMulai.isNotBlank() &&
                item.tanggalSelesai.isNotBlank() &&
                item.tanggalMulai !=
                item.tanggalSelesai -> {

            "${item.tanggalMulai} s/d ${item.tanggalSelesai}"
        }

        item.tanggalMulai.isNotBlank() ->
            item.tanggalMulai

        item.tanggalSelesai.isNotBlank() ->
            item.tanggalSelesai

        else ->
            "-"
    }
}

// ==========================================================
// CLICK WITHOUT RIPPLE
// ==========================================================

private fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier {

    return this.then(
        Modifier.clickable(
            indication = null,
            interactionSource =
                androidx.compose.foundation.interaction.MutableInteractionSource()
        ) {
            onClick()
        }
    )
}