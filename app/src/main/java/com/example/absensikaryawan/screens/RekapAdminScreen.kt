package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


// ==========================================================
// MODEL
// ==========================================================

private data class DataRekapAbsensi(
    val id: String,
    val uid: String,
    val nama: String,
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val catatan: String,
    val qrData: String,
    val qrDataPulang: String,
    val kantor: String
)


// ==========================================================
// FILTER KANTOR
// ==========================================================

private enum class FilterKantor(
    val label: String
) {
    SEMUA("Semua"),
    MALANG("Malang"),
    BLITAR("Blitar"),
    KEDIRI("Kediri")
}


// ==========================================================
// REKAP ADMIN
// ==========================================================

@Composable
fun RekapAdminScreen() {

    // ======================================================
    // FIRESTORE
    // ======================================================

    val db = remember {
        FirebaseFirestore.getInstance()
    }


    // ======================================================
    // STATE
    // ======================================================

    var daftarRekap by remember {
        mutableStateOf(
            emptyList<DataRekapAbsensi>()
        )
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var refreshKey by remember {
        mutableIntStateOf(0)
    }

    var filterKantor by remember {
        mutableStateOf(
            FilterKantor.SEMUA
        )
    }


    // ======================================================
    // LOAD DATA FIRESTORE
    // ======================================================

    LaunchedEffect(refreshKey) {

        try {

            loading = true
            errorMessage = ""

            val snapshot = db
                .collection("attendance")
                .get()
                .await()

            daftarRekap = snapshot.documents
                .map { document ->

                    val qrData =
                        document.getString("qrData")
                            ?: ""

                    val qrDataPulang =
                        document.getString("qrDataPulang")
                            ?: ""

                    DataRekapAbsensi(
                        id = document.id,

                        uid =
                            document.getString("uid")
                                ?: "",

                        nama =
                            document.getString("nama")
                                ?: "Tanpa Nama",

                        tanggal =
                            document.getString("tanggal")
                                ?: "",

                        jamMasuk =
                            document.getString("jamMasuk")
                                ?: "",

                        jamPulang =
                            document.getString("jamPulang")
                                ?: "",

                        catatan =
                            document.getString("catatan")
                                ?: "",

                        qrData =
                            qrData,

                        qrDataPulang =
                            qrDataPulang,

                        kantor =
                            getKantorFromQr(
                                qrData = qrData,
                                qrDataPulang =
                                    qrDataPulang
                            )
                    )
                }
                .sortedWith(
                    compareByDescending<DataRekapAbsensi> {
                        it.tanggal
                    }.thenByDescending {
                        it.jamMasuk
                    }
                )

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil data rekap."

        } finally {

            loading = false
        }
    }


    // ======================================================
    // FILTER DATA
    // ======================================================

    val daftarRekapFiltered =
        when (filterKantor) {

            FilterKantor.SEMUA ->
                daftarRekap

            FilterKantor.MALANG ->
                daftarRekap.filter {
                    it.kantor.equals(
                        "Malang",
                        ignoreCase = true
                    )
                }

            FilterKantor.BLITAR ->
                daftarRekap.filter {
                    it.kantor.equals(
                        "Blitar",
                        ignoreCase = true
                    )
                }

            FilterKantor.KEDIRI ->
                daftarRekap.filter {
                    it.kantor.equals(
                        "Kediri",
                        ignoreCase = true
                    )
                }
        }


    // ======================================================
    // STATISTIK SESUAI KANTOR
    // ======================================================

    val totalData =
        daftarRekapFiltered.size

    val totalHadir =
        daftarRekapFiltered.count {
            it.jamMasuk.isNotBlank()
        }

    val totalPulang =
        daftarRekapFiltered.count {
            it.jamPulang.isNotBlank()
        }


    // ======================================================
    // UI
    // ======================================================

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(44.dp),

                    shape =
                        RoundedCornerShape(13.dp),

                    color =
                        Color(0xFFE6EEE9)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Assessment,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Rekap Absensi",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Data kehadiran seluruh kantor",

                        fontSize =
                            12.sp,

                        color =
                            TextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
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
            // LABEL FILTER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FilterList,

                    contentDescription =
                        null,

                    tint =
                        TextGray,

                    modifier =
                        Modifier.size(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        "Pilih Kantor",

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            // ==================================================
            // BUTTON KANTOR
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {

                FilterKantorButton(
                    modifier =
                        Modifier.weight(1f),

                    label =
                        FilterKantor.SEMUA.label,

                    selected =
                        filterKantor ==
                                FilterKantor.SEMUA,

                    onClick = {

                        filterKantor =
                            FilterKantor.SEMUA
                    }
                )

                FilterKantorButton(
                    modifier =
                        Modifier.weight(1f),

                    label =
                        FilterKantor.MALANG.label,

                    selected =
                        filterKantor ==
                                FilterKantor.MALANG,

                    onClick = {

                        filterKantor =
                            FilterKantor.MALANG
                    }
                )

                FilterKantorButton(
                    modifier =
                        Modifier.weight(1f),

                    label =
                        FilterKantor.BLITAR.label,

                    selected =
                        filterKantor ==
                                FilterKantor.BLITAR,

                    onClick = {

                        filterKantor =
                            FilterKantor.BLITAR
                    }
                )

                FilterKantorButton(
                    modifier =
                        Modifier.weight(1f),

                    label =
                        FilterKantor.KEDIRI.label,

                    selected =
                        filterKantor ==
                                FilterKantor.KEDIRI,

                    onClick = {

                        filterKantor =
                            FilterKantor.KEDIRI
                    }
                )
            }


            // ==================================================
            // TOTAL / MASUK / PULANG
            // TEPAT DI BAWAH BUTTON KANTOR
            // ==================================================

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {

                RekapSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.Assessment,

                    title =
                        "Total",

                    value =
                        totalData.toString()
                )

                RekapSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.CheckCircle,

                    title =
                        "Masuk",

                    value =
                        totalHadir.toString()
                )

                RekapSummaryCard(
                    modifier =
                        Modifier.weight(1f),

                    icon =
                        Icons.Default.AccessTime,

                    title =
                        "Pulang",

                    value =
                        totalPulang.toString()
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ==================================================
            // SECTION DATA
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        when (filterKantor) {

                            FilterKantor.SEMUA ->
                                "Data Semua Kantor"

                            FilterKantor.MALANG ->
                                "Data Kantor Malang"

                            FilterKantor.BLITAR ->
                                "Data Kantor Blitar"

                            FilterKantor.KEDIRI ->
                                "Data Kantor Kediri"
                        },

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark,

                    modifier =
                        Modifier.weight(1f)
                )

                if (
                    !loading &&
                    errorMessage.isBlank()
                ) {

                    Surface(
                        shape =
                            RoundedCornerShape(20.dp),

                        color =
                            Color(0xFFE6EEE9)
                    ) {

                        Text(
                            text =
                                "$totalData data",

                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),

                            fontSize =
                                10.sp,

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
                    Modifier.height(10.dp)
            )


            // ==================================================
            // CONTENT
            // ==================================================

            when {

                // ==================================================
                // LOADING
                // ==================================================

                loading -> {

                    RekapLoading()
                }


                // ==================================================
                // ERROR
                // ==================================================

                errorMessage.isNotBlank() -> {

                    RekapError(
                        message =
                            errorMessage,

                        onRetry = {

                            refreshKey++
                        }
                    )
                }


                // ==================================================
                // EMPTY
                // ==================================================

                daftarRekapFiltered.isEmpty() -> {

                    RekapEmpty(
                        filterKantor =
                            filterKantor
                    )
                }


                // ==================================================
                // DATA
                // ==================================================

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp),

                        contentPadding =
                            PaddingValues(
                                start = 20.dp,
                                top = 0.dp,
                                end = 20.dp,
                                bottom = 24.dp
                            )
                    ) {

                        items(
                            items =
                                daftarRekapFiltered,

                            key = {
                                it.id
                            }
                        ) { data ->

                            RekapAttendanceCard(
                                data =
                                    data
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================
// BUTTON FILTER KANTOR
// ==========================================================

@Composable
private fun FilterKantorButton(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(11.dp),

        color =
            if (selected) {
                PrimaryGreen
            } else {
                Color.White
            },

        shadowElevation =
            if (selected) {
                0.dp
            } else {
                1.dp
            },

        onClick =
            onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 7.dp,
                    vertical = 9.dp
                ),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    label,

                fontSize =
                    10.sp,

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
                        TextDark
                    },

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun RekapSummaryCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String
) {

    Card(
        modifier =
            modifier,

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
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Surface(
                modifier =
                    Modifier.size(34.dp),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    Color(0xFFE6EEE9)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            icon,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(18.dp)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    title,

                fontSize =
                    10.sp,

                color =
                    TextGray,

                maxLines =
                    1
            )

            Text(
                text =
                    value,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// ATTENDANCE CARD
// ==========================================================

@Composable
private fun RekapAttendanceCard(
    data: DataRekapAbsensi
) {

    val sudahMasuk =
        data.jamMasuk.isNotBlank()

    val sudahPulang =
        data.jamPulang.isNotBlank()


    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(17.dp),

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {

            // ==================================================
            // IDENTITAS
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(45.dp),

                    shape =
                        CircleShape,

                    color =
                        Color(0xFFE6EEE9)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                getRekapInitials(
                                    data.nama
                                ),

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            data.nama.ifBlank {
                                "Tanpa Nama"
                            },

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
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

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(13.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                data.tanggal.ifBlank {
                                    "-"
                                },

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // KANTOR
            // ==================================================

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    Color(0xFFF3F7F5)
            ) {

                Row(
                    modifier =
                        Modifier.padding(9.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Business,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text =
                            if (
                                data.kantor.isBlank() ||
                                data.kantor ==
                                "Tidak Diketahui"
                            ) {
                                "Kantor tidak diketahui"
                            } else {
                                "Kantor ${data.kantor}"
                            },

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // STATUS
            // ==================================================

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    when {

                        sudahPulang ->
                            Color(0xFFE8F5E9)

                        sudahMasuk ->
                            Color(0xFFFFF7ED)

                        else ->
                            Color(0xFFF3F4F6)
                    }
            ) {

                Text(
                    text =
                        when {

                            sudahPulang ->
                                "✓ Absensi Lengkap"

                            sudahMasuk ->
                                "● Sudah Absen Masuk"

                            else ->
                                "Belum Absen"
                        },

                    modifier =
                        Modifier.padding(10.dp),

                    fontSize =
                        11.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        when {

                            sudahPulang ->
                                Color(0xFF15803D)

                            sudahMasuk ->
                                Color(0xFFC2410C)

                            else ->
                                TextGray
                        }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // WAKTU
            // ==================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                RekapTimeBox(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        "Jam Masuk",

                    value =
                        if (sudahMasuk) {
                            data.jamMasuk
                        } else {
                            "--:--:--"
                        }
                )

                RekapTimeBox(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        "Jam Pulang",

                    value =
                        if (sudahPulang) {
                            data.jamPulang
                        } else {
                            "--:--:--"
                        }
                )
            }


            // ==================================================
            // CATATAN
            // ==================================================

            if (
                data.catatan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(10.dp),

                    color =
                        Color(0xFFF8FAF9)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(10.dp)
                    ) {

                        Text(
                            text =
                                "Catatan",

                            fontSize =
                                10.sp,

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
                                data.catatan,

                            fontSize =
                                12.sp,

                            color =
                                TextDark
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// TIME BOX
// ==========================================================

@Composable
private fun RekapTimeBox(
    modifier: Modifier,
    title: String,
    value: String
) {

    Surface(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(11.dp),

        color =
            Color(0xFFF8FAF9)
    ) {

        Column(
            modifier =
                Modifier.padding(11.dp)
        ) {

            Text(
                text =
                    title,

                fontSize =
                    10.sp,

                color =
                    TextGray
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    value,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// LOADING
// ==========================================================

@Composable
private fun RekapLoading() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        CircularProgressIndicator(
            color =
                PrimaryGreen
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "Memuat rekap absensi...",

            fontSize =
                13.sp,

            color =
                TextGray
        )
    }
}


// ==========================================================
// ERROR
// ==========================================================

@Composable
private fun RekapError(
    message: String,
    onRetry: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Surface(
            modifier =
                Modifier.size(64.dp),

            shape =
                CircleShape,

            color =
                Color(0xFFFEECEC)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Warning,

                    contentDescription =
                        null,

                    tint =
                        Color(0xFFB91C1C),

                    modifier =
                        Modifier.size(30.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text =
                "Gagal memuat rekap",

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text =
                message,

            fontSize =
                12.sp,

            color =
                Color(0xFFB91C1C),

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        IconButton(
            onClick =
                onRetry
        ) {

            Icon(
                imageVector =
                    Icons.Default.Refresh,

                contentDescription =
                    "Coba lagi",

                tint =
                    PrimaryGreen
            )
        }
    }
}


// ==========================================================
// EMPTY
// ==========================================================

@Composable
private fun RekapEmpty(
    filterKantor: FilterKantor
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Surface(
            modifier =
                Modifier.size(70.dp),

            shape =
                CircleShape,

            color =
                Color(0xFFE6EEE9)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Assessment,

                    contentDescription =
                        null,

                    tint =
                        TextGray,

                    modifier =
                        Modifier.size(32.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Text(
            text =
                if (
                    filterKantor ==
                    FilterKantor.SEMUA
                ) {
                    "Belum Ada Rekap"
                } else {
                    "Belum Ada Data"
                },

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                if (
                    filterKantor ==
                    FilterKantor.SEMUA
                ) {
                    "Belum ada data absensi di Firestore."
                } else {
                    "Belum ada data absensi untuk kantor ${filterKantor.label}."
                },

            fontSize =
                12.sp,

            color =
                TextGray,

            textAlign =
                TextAlign.Center
        )
    }
}


// ==========================================================
// GET KANTOR DARI QR
// ==========================================================

private fun getKantorFromQr(
    qrData: String,
    qrDataPulang: String
): String {

    fun normalizeQr(
        value: String
    ): String {

        return value
            .trim()
            .removeSuffix("/")
            .lowercase()
    }


    val masuk =
        normalizeQr(qrData)

    val pulang =
        normalizeQr(qrDataPulang)


    // ======================================================
    // QR KANTOR MALANG
    // ======================================================

    val qrMalang =
        normalizeQr(
            "https://q.me-qr.com/x5ie23mg"
        )


    // ======================================================
    // QR KANTOR BLITAR
    // ======================================================

    val qrBlitar =
        normalizeQr(
            "https://q.me-qr.com/hbywvgy7"
        )


    // ======================================================
    // QR KANTOR KEDIRI
    // ======================================================

    val qrKediri =
        normalizeQr(
            "https://q.me-qr.com/14vy2ipr"
        )


    // ======================================================
    // ABSEN MASUK
    // ======================================================

    return when {

        masuk == qrMalang ->
            "Malang"

        masuk == qrBlitar ->
            "Blitar"

        masuk == qrKediri ->
            "Kediri"


        // ==================================================
        // JIKA QR MASUK KOSONG, CEK QR PULANG
        // ==================================================

        pulang == qrMalang ->
            "Malang"

        pulang == qrBlitar ->
            "Blitar"

        pulang == qrKediri ->
            "Kediri"


        // ==================================================
        // QR TIDAK DIKENAL
        // ==================================================

        else ->
            "Tidak Diketahui"
    }
}


// ==========================================================
// INITIAL KARYAWAN
// ==========================================================

private fun getRekapInitials(
    nama: String
): String {

    val parts =
        nama
            .trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }

    return when {

        parts.isEmpty() ->
            "?"

        parts.size == 1 ->
            parts[0]
                .take(2)
                .uppercase()

        else ->
            "${parts.first().first()}${parts.last().first()}"
                .uppercase()
    }
}