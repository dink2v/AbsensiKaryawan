package com.example.absensikaryawan.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.FirestoreRepository
import com.example.absensikaryawan.data.RekapAbsensiData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

// ==========================================================
// REKAP SCREEN - ADMIN
// ==========================================================

@Composable
fun RekapScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val repository = remember {
        FirestoreRepository()
    }

    val scope = rememberCoroutineScope()
    // ======================================================
    // DEFAULT TANGGAL
    // ======================================================

    val calendar = remember {
        Calendar.getInstance()
    }

    val dateFormatter = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )
    }

    var tanggalMulai by remember {
        mutableStateOf(
            dateFormatter.format(calendar.time)
        )
    }

    var tanggalAkhir by remember {
        mutableStateOf(
            dateFormatter.format(calendar.time)
        )
    }

    // ======================================================
    // DATA
    // ======================================================

    var daftarAbsensi by remember {
        mutableStateOf<List<RekapAbsensiData>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    // ======================================================
    // LOAD DATA
    // ======================================================

    suspend fun loadData() {

        if (tanggalMulai > tanggalAkhir) {

            errorMessage =
                "Tanggal mulai tidak boleh lebih besar dari tanggal akhir."

            daftarAbsensi = emptyList()

            return
        }

        isLoading = true
        errorMessage = ""

        val result =
            repository.getRekapAbsensi(
                tanggalMulai = tanggalMulai,
                tanggalAkhir = tanggalAkhir
            )

        result
            .onSuccess { data ->

                daftarAbsensi = data
                errorMessage = ""
            }
            .onFailure { error ->

                daftarAbsensi = emptyList()

                errorMessage =
                    error.message
                        ?: "Gagal mengambil data absensi."
            }

        isLoading = false
    }

    // ======================================================
    // LOAD AWAL
    // ======================================================

    LaunchedEffect(Unit) {
        loadData()
    }

    // ======================================================
    // FILTER SEARCH
    // ======================================================

    val filteredData =
        remember(
            daftarAbsensi,
            searchQuery
        ) {

            if (searchQuery.isBlank()) {

                daftarAbsensi

            } else {

                daftarAbsensi.filter {

                    it.nama.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||

                            it.tanggal.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||

                            it.kantor.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                }
            }
        }

    // ======================================================
    // ROOT
    // ======================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
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

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            PrimaryGreen
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Rekap Absensi",

                        fontSize =
                            24.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Text(
                        text =
                            "Rekap kehadiran karyawan",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
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
                            PrimaryGreen
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================================
            // FILTER TANGGAL
            // ==================================================

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
                        defaultElevation = 2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {

                    Text(
                        text =
                            "Filter Berdasarkan Tanggal",

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    // ==========================================
                    // TANGGAL MULAI
                    // ==========================================

                    Text(
                        text =
                            "Tanggal Mulai",

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            TextGray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            showDatePicker(
                                context = context,
                                currentDate = tanggalMulai
                            ) { selectedDate ->

                                tanggalMulai =
                                    selectedDate
                            }
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CalendarMonth,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                formatTanggalTampil(
                                    tanggalMulai
                                )
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // ==========================================
                    // TANGGAL AKHIR
                    // ==========================================

                    Text(
                        text =
                            "Tanggal Akhir",

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            TextGray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            showDatePicker(
                                context = context,
                                currentDate = tanggalAkhir
                            ) { selectedDate ->

                                tanggalAkhir =
                                    selectedDate
                            }
                        },

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CalendarMonth,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                formatTanggalTampil(
                                    tanggalAkhir
                                )
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    // ==========================================
                    // TOMBOL TERAPKAN
                    // ==========================================

                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            scope.launch {
                                loadData()
                            }
                        },

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PrimaryGreen
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Event,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Terapkan Filter"
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // ==================================================
            // SEARCH
            // ==================================================

            OutlinedTextField(
                modifier =
                    Modifier.fillMaxWidth(),

                value =
                    searchQuery,

                onValueChange = {
                    searchQuery = it
                },

                singleLine = true,

                shape =
                    RoundedCornerShape(12.dp),

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Cari"
                    )
                },

                placeholder = {

                    Text(
                        text =
                            "Cari nama, tanggal, atau kantor"
                    )
                }
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // ==================================================
            // RINGKASAN
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFE8F5E9)
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text =
                                "Total Data",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )

                        Text(
                            text =
                                filteredData.size.toString(),

                            fontSize =
                                22.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }

                    Column(
                        horizontalAlignment =
                            Alignment.End
                    ) {

                        Text(
                            text =
                                "Periode",

                            fontSize =
                                12.sp,

                            color =
                                TextGray
                        )

                        Text(
                            text =
                                "${formatTanggalTampil(tanggalMulai)} - ${
                                    formatTanggalTampil(tanggalAkhir)
                                }",

                            fontSize =
                                12.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ==================================================
            // CONTENT
            // ==================================================

            when {

                isLoading -> {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(30.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            color =
                                PrimaryGreen
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Memuat data absensi...",

                            color =
                                TextGray
                        )
                    }
                }

                errorMessage.isNotBlank() -> {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFFFF1F2)
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    "Gagal Memuat Data",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color(0xFFB91C1C)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                    errorMessage,

                                fontSize =
                                    13.sp,

                                color =
                                    TextGray
                            )
                        }
                    }
                }

                filteredData.isEmpty() -> {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color.White
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Event,

                                contentDescription =
                                    null,

                                tint =
                                    TextGray
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Tidak Ada Data Absensi",

                                fontSize =
                                    17.sp,

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
                                    "Tidak ditemukan absensi pada periode yang dipilih.",

                                fontSize =
                                    13.sp,

                                color =
                                    TextGray
                            )
                        }
                    }
                }

                else -> {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            items =
                                filteredData,

                            key = {
                                it.id
                            }
                        ) { data ->

                            RekapAbsensiCard(
                                data = data
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// KARTU REKAP ABSENSI
// ==========================================================

@Composable
private fun RekapAbsensiCard(
    data: RekapAbsensiData
) {

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
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {

            // ==============================================
            // NAMA
            // ==============================================

            Text(
                text =
                    data.nama.ifBlank {
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
                    Modifier.height(6.dp)
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
                        Icons.Default.Event,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        formatTanggalTampil(
                            data.tanggal
                        ),

                    fontSize =
                        13.sp,

                    color =
                        TextGray
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ==============================================
            // JAM MASUK
            // ==============================================

            RekapDataRow(
                icon =
                    Icons.Default.AccessTime,

                title =
                    "Jam Masuk",

                value =
                    data.jamMasuk.ifBlank {
                        "--:--:--"
                    }
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // ==============================================
            // JAM PULANG
            // ==============================================

            RekapDataRow(
                icon =
                    Icons.Default.ExitToApp,

                title =
                    "Jam Pulang",

                value =
                    data.jamPulang.ifBlank {
                        "--:--:--"
                    }
            )

            // ==============================================
            // KANTOR
            // ==============================================

            if (data.kantor.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Kantor: ${data.kantor}",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }

            // ==============================================
            // STATUS
            // ==============================================

            if (data.status.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Surface(
                    shape =
                        RoundedCornerShape(8.dp),

                    color =
                        Color(0xFFDCFCE7)
                ) {

                    Text(
                        text =
                            data.status.uppercase(),

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF15803D)
                    )
                }
            }
        }
    }
}

// ==========================================================
// REKAP DATA ROW
// ==========================================================

@Composable
private fun RekapDataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            shape =
                RoundedCornerShape(8.dp),

            color =
                Color(0xFFE8F5E9)
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.padding(7.dp)
            )
        }

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Text(
            text =
                title,

            modifier =
                Modifier.weight(1f),

            fontSize =
                13.sp,

            color =
                TextGray
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

// ==========================================================
// DATE PICKER
// ==========================================================

private fun showDatePicker(
    context: android.content.Context,
    currentDate: String,
    onDateSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()

    try {

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val date =
            formatter.parse(currentDate)

        if (date != null) {
            calendar.time = date
        }

    } catch (_: Exception) {
        // gunakan tanggal hari ini
    }

    DatePickerDialog(
        context,

        { _, year, month, dayOfMonth ->

            val selected =
                Calendar.getInstance().apply {

                    set(
                        Calendar.YEAR,
                        year
                    )

                    set(
                        Calendar.MONTH,
                        month
                    )

                    set(
                        Calendar.DAY_OF_MONTH,
                        dayOfMonth
                    )
                }

            val formatter =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            onDateSelected(
                formatter.format(
                    selected.time
                )
            )
        },

        calendar.get(
            Calendar.YEAR
        ),

        calendar.get(
            Calendar.MONTH
        ),

        calendar.get(
            Calendar.DAY_OF_MONTH
        )

    ).show()
}

// ==========================================================
// FORMAT TANGGAL UNTUK UI
// ==========================================================

private fun formatTanggalTampil(
    tanggal: String
): String {

    if (tanggal.isBlank()) {
        return "-"
    }

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val output =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        val date =
            input.parse(tanggal)

        if (date != null) {
            output.format(date)
        } else {
            tanggal
        }

    } catch (_: Exception) {

        tanggal
    }
}