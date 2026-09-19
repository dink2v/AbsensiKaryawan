package com.example.absensikaryawan.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Schedule

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.PengajuanRepository

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import kotlinx.coroutines.launch


@Composable
fun PengajuanBaruScreen(
    onBack: () -> Unit,
    onSubmit: (
        jenis: String,
        jamPulang: String,
        jamKeluar: String,
        jamKembali: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        alasan: String
    ) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var jenisDipilih by remember {
        mutableStateOf<JenisPengajuan?>(null)
    }

    var jamPulang by remember {
        mutableStateOf("")
    }

    var jamKeluar by remember {
        mutableStateOf("")
    }

    var jamKembali by remember {
        mutableStateOf("")
    }

    var tanggalMulai by remember {
        mutableStateOf("")
    }

    var tanggalSelesai by remember {
        mutableStateOf("")
    }

    var alasan by remember {
        mutableStateOf("")
    }

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    // =========================================================
    // KEYBOARD / BRING INTO VIEW
    // =========================================================

    val alasanBringIntoViewRequester =
        remember {
            BringIntoViewRequester()
        }

    // Tampilan tanggal hari ini tetap seperti desain lama.
    val tanggalHariIni = remember {
        SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        ).format(Date())
    }

    // Nilai tanggal untuk Firestore menggunakan yyyy-MM-dd.
    val tanggalHariIniFirestore = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {

            // =========================================================
            // HEADER
            // =========================================================

            Row(
                modifier = Modifier
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

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Column {
                    Text(
                        text = "Pengajuan Baru",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Pilih jenis pengajuan",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            // =========================================================
            // CONTENT
            // =========================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .imePadding()
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                // =====================================================
                // SUCCESS MESSAGE
                // =====================================================

                if (successMessage.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftGreen
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = successMessage,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // =====================================================
                // ERROR MESSAGE
                // =====================================================

                if (errorMessage.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE4E6)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // =====================================================
                // JENIS PENGAJUAN
                // =====================================================

                Text(
                    text = "Jenis Pengajuan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                JenisPengajuanCard(
                    icon = Icons.Default.ExitToApp,
                    title = "Pulang Cepat",
                    description = "Tanggal + Jam Pulang + Alasan",
                    selected =
                        jenisDipilih ==
                                JenisPengajuan.PulangCepat,
                    onClick = {
                        jenisDipilih =
                            JenisPengajuan.PulangCepat
                        errorMessage = ""
                        successMessage = ""
                    }
                )

                JenisPengajuanCard(
                    icon = Icons.Default.AccessTime,
                    title = "Izin Keluar",
                    description =
                        "Tanggal + Jam Keluar + Jam Kembali + Alasan",
                    selected =
                        jenisDipilih ==
                                JenisPengajuan.IzinKeluar,
                    onClick = {
                        jenisDipilih =
                            JenisPengajuan.IzinKeluar
                        errorMessage = ""
                        successMessage = ""
                    }
                )

                JenisPengajuanCard(
                    icon = Icons.Default.Schedule,
                    title = "Izin Terlambat",
                    description = "Alasan keterlambatan",
                    selected =
                        jenisDipilih ==
                                JenisPengajuan.IzinTerlambat,
                    onClick = {
                        jenisDipilih =
                            JenisPengajuan.IzinTerlambat
                        errorMessage = ""
                        successMessage = ""
                    }
                )

                JenisPengajuanCard(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Izin Sakit",
                    description =
                        "Tanggal Mulai – Selesai + Alasan",
                    selected =
                        jenisDipilih ==
                                JenisPengajuan.IzinSakit,
                    onClick = {
                        jenisDipilih =
                            JenisPengajuan.IzinSakit
                        errorMessage = ""
                        successMessage = ""
                    }
                )

                JenisPengajuanCard(
                    icon = Icons.Default.Event,
                    title = "Cuti Reguler",
                    description =
                        "Min. H-7, maksimal 2 hari, maksimal 2x/bulan",
                    selected =
                        jenisDipilih ==
                                JenisPengajuan.CutiReguler,
                    onClick = {
                        jenisDipilih =
                            JenisPengajuan.CutiReguler
                        errorMessage = ""
                        successMessage = ""
                    }
                )

                // =====================================================
                // DETAIL PENGAJUAN
                // =====================================================

                if (jenisDipilih != null) {

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Detail Pengajuan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            // =================================================
                            // PULANG CEPAT
                            // =================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.PulangCepat
                            ) {

                                TanggalInfo(
                                    tanggal = tanggalHariIni
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                TimePickerField(
                                    value = jamPulang,
                                    onValueChange = {
                                        jamPulang = it
                                    },
                                    label = "Jam Pulang"
                                )
                            }

                            // =================================================
                            // IZIN KELUAR
                            // =================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.IzinKeluar
                            ) {

                                TanggalInfo(
                                    tanggal = tanggalHariIni
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                TimePickerField(
                                    value = jamKeluar,
                                    onValueChange = {
                                        jamKeluar = it
                                    },
                                    label = "Jam Keluar"
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                TimePickerField(
                                    value = jamKembali,
                                    onValueChange = {
                                        jamKembali = it
                                    },
                                    label = "Jam Kembali"
                                )
                            }

                            // =================================================
                            // IZIN TERLAMBAT
                            // =================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.IzinTerlambat
                            ) {

                                TanggalInfo(
                                    tanggal = tanggalHariIni
                                )
                            }

                            // =================================================
                            // IZIN SAKIT
                            // =================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.IzinSakit
                            ) {

                                DatePickerField(
                                    value = tanggalMulai,
                                    onValueChange = {
                                        tanggalMulai = it
                                    },
                                    label = "Tanggal Mulai"
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                DatePickerField(
                                    value = tanggalSelesai,
                                    onValueChange = {
                                        tanggalSelesai = it
                                    },
                                    label = "Tanggal Selesai"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text =
                                        "Jika sakit 3 hari atau lebih, lampirkan surat dokter.",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }

                            // =================================================
                            // CUTI REGULER
                            // =================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.CutiReguler
                            ) {

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = SoftGreen
                                    )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {

                                        Text(
                                            text = "Sisa kuota cuti",
                                            fontSize = 12.sp,
                                            color = TextGray
                                        )

                                        Spacer(
                                            modifier = Modifier.height(3.dp)
                                        )

                                        Text(
                                            text = "8 / 12 hari",
                                            fontSize = 21.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )

                                        Spacer(
                                            modifier = Modifier.height(3.dp)
                                        )

                                        Text(
                                            text =
                                                "Ajukan minimal H-7. Maksimal 2 hari sekali dan 2 kali dalam 1 bulan.",
                                            fontSize = 12.sp,
                                            color = TextGray
                                        )
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                DatePickerField(
                                    value = tanggalMulai,
                                    onValueChange = {
                                        tanggalMulai = it
                                    },
                                    label = "Tanggal Mulai Cuti"
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                DatePickerField(
                                    value = tanggalSelesai,
                                    onValueChange = {
                                        tanggalSelesai = it
                                    },
                                    label = "Tanggal Selesai Cuti"
                                )
                            }

                            // =================================================
                            // ALASAN / KETERANGAN
                            // =================================================

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            OutlinedTextField(
                                value = alasan,
                                onValueChange = {
                                    alasan = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .bringIntoViewRequester(
                                        alasanBringIntoViewRequester
                                    ),
                                label = {
                                    Text("Alasan / Keterangan")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector =
                                            Icons.Default.Description,
                                        contentDescription = null
                                    )
                                },
                                singleLine = false,
                                maxLines = 5
                            )

                            // Saat teks mulai diketik, pastikan field
                            // tetap berada di atas keyboard.
                            LaunchedEffect(alasan) {
                                if (alasan.isNotEmpty()) {
                                    alasanBringIntoViewRequester
                                        .bringIntoView()
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            // =================================================
                            // SUBMIT
                            // =================================================

                            Button(
                                onClick = {

                                    errorMessage = ""
                                    successMessage = ""

                                    if (jenisDipilih == null) {
                                        errorMessage =
                                            "Silakan pilih jenis pengajuan."
                                        return@Button
                                    }

                                    if (alasan.isBlank()) {
                                        errorMessage =
                                            "Alasan / Keterangan wajib diisi."
                                        return@Button
                                    }

                                    isSubmitting = true

                                    coroutineScope.launch {

                                        val jenisValue =
                                            jenisDipilih?.name.orEmpty()

                                        val tanggalMulaiValue =
                                            when (jenisDipilih) {

                                                JenisPengajuan.PulangCepat,
                                                JenisPengajuan.IzinKeluar,
                                                JenisPengajuan.IzinTerlambat -> {
                                                    tanggalHariIniFirestore
                                                }

                                                else -> {
                                                    tanggalMulai
                                                }
                                            }

                                        val result =
                                            PengajuanRepository
                                                .simpanPengajuan(
                                                    jenis = jenisValue,
                                                    jamPulang = jamPulang,
                                                    jamKeluar = jamKeluar,
                                                    jamKembali = jamKembali,
                                                    tanggalMulai =
                                                        tanggalMulaiValue,
                                                    tanggalSelesai =
                                                        tanggalSelesai,
                                                    alasan = alasan
                                                )

                                        isSubmitting = false

                                        result.onSuccess {

                                            successMessage =
                                                "Pengajuan berhasil dikirim dan sedang menunggu approval."

                                            errorMessage = ""

                                            onSubmit(
                                                jenisValue,
                                                jamPulang,
                                                jamKeluar,
                                                jamKembali,
                                                tanggalMulaiValue,
                                                tanggalSelesai,
                                                alasan
                                            )

                                        }.onFailure { exception ->

                                            errorMessage =
                                                exception.message
                                                    ?: "Gagal menyimpan pengajuan."

                                            successMessage = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSubmitting,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                )
                            ) {

                                if (isSubmitting) {

                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Text(
                                        text = "Mengirim...",
                                        fontWeight = FontWeight.Bold
                                    )

                                } else {

                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Text(
                                        text = "Ajukan Sekarang",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}


// =====================================================================
// CARD JENIS PENGAJUAN
// =====================================================================

@Composable
private fun JenisPengajuanCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (selected) SoftGreen else Color.White

    val iconColor =
        if (selected) PrimaryGreen else TextGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextGray
                )
            }
        }
    }
}


// =====================================================================
// TANGGAL INFO
// =====================================================================

@Composable
private fun TanggalInfo(
    tanggal: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Tanggal",
            fontSize = 12.sp,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = SoftGreen,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Tanggal",
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = tanggal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}


// =====================================================================
// DATE PICKER
// =====================================================================

@Composable
private fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {

                    val calendar = Calendar.getInstance()

                    if (value.isNotBlank()) {
                        try {
                            val parsedDate =
                                SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.US
                                ).parse(value)

                            if (parsedDate != null) {
                                calendar.time = parsedDate
                            }
                        } catch (_: Exception) {
                        }
                    }

                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->

                            val selectedDate =
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
                                    Locale.US
                                )

                            onValueChange(
                                formatter.format(
                                    selectedDate.time
                                )
                            )
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Pilih tanggal"
                )
            }
        },
        readOnly = true,
        singleLine = true
    )
}


// =====================================================================
// TIME PICKER
// =====================================================================

@Composable
private fun TimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {

                    val calendar = Calendar.getInstance()

                    if (value.isNotBlank()) {
                        try {
                            val parts = value.split(":")

                            if (parts.size >= 2) {
                                calendar.set(
                                    Calendar.HOUR_OF_DAY,
                                    parts[0].toInt()
                                )

                                calendar.set(
                                    Calendar.MINUTE,
                                    parts[1].toInt()
                                )
                            }
                        } catch (_: Exception) {
                        }
                    }

                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->

                            val formatted =
                                String.format(
                                    Locale.US,
                                    "%02d:%02d",
                                    hourOfDay,
                                    minute
                                )

                            onValueChange(formatted)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Pilih waktu"
                )
            }
        },
        readOnly = true,
        singleLine = true
    )
}