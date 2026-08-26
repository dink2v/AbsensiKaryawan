package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

// ==========================================================
// PENGAJUAN BARU SCREEN
// ==========================================================

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

    // ==================================================
    // REPOSITORY
    // ==================================================

    val pengajuanRepository = remember {
        PengajuanRepository()
    }

    val coroutineScope = rememberCoroutineScope()

    // ==================================================
    // JENIS PENGAJUAN
    // ==================================================

    var jenisDipilih by remember {
        mutableStateOf<JenisPengajuan?>(null)
    }

    // ==================================================
    // DATA FORM
    // ==================================================

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

    // ==================================================
    // STATUS SUBMIT
    // ==================================================

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    // ==================================================
    // TANGGAL HARI INI
    // ==================================================

    val tanggalHariIni = remember {

        SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        ).format(Date())
    }

    // ==================================================
    // MAIN SCREEN
    // ==================================================

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
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        if (!isSubmitting) {
                            onBack()
                        }
                    }
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

            // ==================================================
            // CONTENT
            // ==================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                // SUCCESS MESSAGE
                // ==================================================

                if (successMessage.isNotBlank()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftGreen
                        )
                    ) {

                        Text(
                            text = successMessage,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                // ==================================================
                // ERROR MESSAGE
                // ==================================================

                if (errorMessage.isNotBlank()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE4E6)
                        )
                    ) {

                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                // ==================================================
                // JENIS PENGAJUAN
                // ==================================================

                Text(
                    text = "Jenis Pengajuan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // ==================================================
                // PULANG CEPAT
                // ==================================================

                JenisPengajuanCard(

                    icon = Icons.Default.ExitToApp,

                    title = "Pulang Cepat",

                    description =
                        "Tanggal + Jam Pulang + Alasan",

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

                // ==================================================
                // IZIN KELUAR
                // ==================================================

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

                // ==================================================
                // IZIN TERLAMBAT
                // ==================================================

                JenisPengajuanCard(

                    icon = Icons.Default.Schedule,

                    title = "Izin Terlambat",

                    description =
                        "Alasan keterlambatan",

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

                // ==================================================
                // IZIN SAKIT
                // ==================================================

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

                // ==================================================
                // CUTI REGULER
                // ==================================================

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

                // ==================================================
                // FORM DETAIL
                // ==================================================

                if (jenisDipilih != null) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {

                            // ==================================================
                            // JUDUL DETAIL
                            // ==================================================

                            Text(
                                text = "Detail Pengajuan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            // ==================================================
                            // PULANG CEPAT
                            // ==================================================

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

                                InputField(
                                    value = jamPulang,
                                    onValueChange = {
                                        jamPulang = it
                                    },
                                    label = "Jam Pulang",
                                    icon = Icons.Default.AccessTime
                                )
                            }

                            // ==================================================
                            // IZIN KELUAR
                            // ==================================================

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

                                InputField(
                                    value = jamKeluar,
                                    onValueChange = {
                                        jamKeluar = it
                                    },
                                    label = "Jam Keluar",
                                    icon = Icons.Default.AccessTime
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                InputField(
                                    value = jamKembali,
                                    onValueChange = {
                                        jamKembali = it
                                    },
                                    label = "Jam Kembali",
                                    icon = Icons.Default.AccessTime
                                )
                            }

                            // ==================================================
                            // IZIN TERLAMBAT
                            // ==================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.IzinTerlambat
                            ) {

                                TanggalInfo(
                                    tanggal = tanggalHariIni
                                )
                            }

                            // ==================================================
                            // IZIN SAKIT
                            // ==================================================

                            if (
                                jenisDipilih ==
                                JenisPengajuan.IzinSakit
                            ) {

                                InputField(
                                    value = tanggalMulai,
                                    onValueChange = {
                                        tanggalMulai = it
                                    },
                                    label = "Tanggal Mulai",
                                    icon = Icons.Default.CalendarMonth
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                InputField(
                                    value = tanggalSelesai,
                                    onValueChange = {
                                        tanggalSelesai = it
                                    },
                                    label = "Tanggal Selesai",
                                    icon = Icons.Default.CalendarMonth
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Text(
                                    text =
                                        "Jika sakit 3 hari atau lebih, lampirkan surat dokter.",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }

                            // ==================================================
                            // CUTI REGULER
                            // ==================================================

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
                                            modifier = Modifier.height(5.dp)
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

                                InputField(
                                    value = tanggalMulai,
                                    onValueChange = {
                                        tanggalMulai = it
                                    },
                                    label = "Tanggal Mulai Cuti",
                                    icon = Icons.Default.CalendarMonth
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                InputField(
                                    value = tanggalSelesai,
                                    onValueChange = {
                                        tanggalSelesai = it
                                    },
                                    label = "Tanggal Selesai Cuti",
                                    icon = Icons.Default.CalendarMonth
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            // ==================================================
                            // ALASAN
                            // ==================================================

                            OutlinedTextField(
                                value = alasan,
                                onValueChange = {
                                    alasan = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                label = {
                                    Text(
                                        "Alasan / Keterangan"
                                    )
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

                            Spacer(
                                modifier = Modifier.height(18.dp)
                            )

                            // ==================================================
                            // TOMBOL AJUKAN
                            // ==================================================

                            Button(
                                onClick = {

                                    if (isSubmitting) {
                                        return@Button
                                    }

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

                                    // --------------------------------------------------
                                    // SIMPAN FIRESTORE
                                    // --------------------------------------------------

                                    coroutineScope.launch {
                                        val result =
                                            pengajuanRepository.simpanPengajuan(

                                                jenis =
                                                    jenisDipilih
                                                        ?.name
                                                        ?: "",

                                                jamPulang =
                                                    jamPulang,

                                                jamKeluar =
                                                    jamKeluar,

                                                jamKembali =
                                                    jamKembali,

                                                tanggalMulai =
                                                    if (
                                                        jenisDipilih ==
                                                        JenisPengajuan.PulangCepat ||
                                                        jenisDipilih ==
                                                        JenisPengajuan.IzinKeluar ||
                                                        jenisDipilih ==
                                                        JenisPengajuan.IzinTerlambat
                                                    ) {
                                                        tanggalHariIni
                                                    } else {
                                                        tanggalMulai
                                                    },

                                                tanggalSelesai =
                                                    tanggalSelesai,

                                                alasan =
                                                    alasan
                                            )

                                        isSubmitting = false

                                        if (result.isSuccess) {

                                            successMessage =
                                                "Pengajuan berhasil dikirim dan sedang menunggu approval admin."

                                            errorMessage = ""

                                            onSubmit(
                                                jenisDipilih
                                                    ?.name
                                                    ?: "",

                                                jamPulang,

                                                jamKeluar,

                                                jamKembali,

                                                tanggalMulai,

                                                tanggalSelesai,

                                                alasan
                                            )

                                        } else {

                                            errorMessage =
                                                result.exceptionOrNull()
                                                    ?.message
                                                    ?: "Gagal menyimpan pengajuan."

                                            successMessage = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !isSubmitting,
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
                                        text = "Mengirim..."
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
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

// ==========================================================
// KARTU JENIS PENGAJUAN
// ==========================================================

@Composable
private fun JenisPengajuanCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) {
            SoftGreen
        } else {
            Color.White
        }

    val iconColor =
        if (selected) {
            PrimaryGreen
        } else {
            TextGray
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 5.dp
                )
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(17.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    backgroundColor
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
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
                    icon,
                contentDescription =
                    title,
                tint =
                    iconColor,
                modifier =
                    Modifier.size(28.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        title,
                    fontSize =
                        15.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        description,
                    fontSize =
                        11.sp,
                    color =
                        TextGray
                )
            }
        }
    }
}

// ==========================================================
// INFO TANGGAL
// ==========================================================

@Composable
private fun TanggalInfo(
    tanggal: String
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text =
                "Tanggal",
            fontSize =
                12.sp,
            color =
                TextGray
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            SoftGreen,
                        shape =
                            RoundedCornerShape(12.dp)
                    )
                    .padding(13.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.CalendarMonth,
                contentDescription =
                    "Tanggal",
                tint =
                    PrimaryGreen,
                modifier =
                    Modifier.size(20.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )

            Text(
                text =
                    tanggal,
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
// INPUT FIELD
// ==========================================================

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {

    OutlinedTextField(
        value =
            value,
        onValueChange =
            onValueChange,
        modifier =
            Modifier.fillMaxWidth(),
        label = {
            Text(
                text =
                    label
            )
        },
        leadingIcon = {
            Icon(
                imageVector =
                    icon,
                contentDescription =
                    null
            )
        },
        singleLine =
            true
    )
}