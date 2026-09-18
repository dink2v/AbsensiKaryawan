package com.example.absensikaryawan.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Send

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.data.PengajuanRepository

import kotlinx.coroutines.launch


// ==========================================================
// PENGAJUAN BARU SCREEN
// ==========================================================

@Composable
fun PengajuanBaruScreen(

    onBack: () -> Unit,

    onSubmit: (
        String,
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit

) {

    // ==========================================================
    // STATE FORM
    // ==========================================================

    var jenis by remember {
        mutableStateOf("")
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


    // ==========================================================
    // STATE SUBMIT
    // ==========================================================

    var sedangMengirim by remember {
        mutableStateOf(false)
    }

    var pesanError by remember {
        mutableStateOf("")
    }

    var berhasil by remember {
        mutableStateOf(false)
    }


    val scope = rememberCoroutineScope()


    // ==========================================================
    // MAIN SCREEN
    // ==========================================================

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            Background

    ) {

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

            // ==================================================
            // HEADER
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 12.dp,
                            bottom = 12.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                IconButton(

                    onClick = {

                        if (!sedangMengirim) {
                            onBack()
                        }
                    }

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            TextDark
                    )
                }


                Column(

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text(

                        text =
                            "Pengajuan Baru",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            "Isi data pengajuan dengan lengkap.",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }


                Surface(

                    modifier =
                        Modifier.size(42.dp),

                    shape =
                        RoundedCornerShape(13.dp),

                    color =
                        SoftGreen

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.EventNote,

                        contentDescription =
                            "Pengajuan",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier
                                .padding(9.dp)
                                .size(24.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            // ==================================================
            // INFO
            // ==================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftGreen
                    )

            ) {

                Column(

                    modifier =
                        Modifier.padding(16.dp)

                ) {

                    Text(

                        text =
                            "Informasi Pengajuan",

                        fontSize =
                            15.sp,

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
                            "Pengajuan akan dikirim melalui jalur persetujuan sesuai jabatan dan atasan kamu.",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ==================================================
            // JENIS PENGAJUAN
            // ==========================================================

            Text(

                text =
                    "Jenis Pengajuan",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    jenis,

                onValueChange = {

                    jenis = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Contoh: Izin / Keperluan Pribadi")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // TANGGAL MULAI
            // ==================================================

            Text(

                text =
                    "Tanggal Mulai",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    tanggalMulai,

                onValueChange = {

                    tanggalMulai = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Format: yyyy-MM-dd")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // TANGGAL SELESAI
            // ==================================================

            Text(

                text =
                    "Tanggal Selesai",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    tanggalSelesai,

                onValueChange = {

                    tanggalSelesai = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Format: yyyy-MM-dd")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // JAM PULANG
            // ==================================================

            Text(

                text =
                    "Jam Pulang",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    jamPulang,

                onValueChange = {

                    jamPulang = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Contoh: 16:00")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // JAM KELUAR
            // ==========================================================

            Text(

                text =
                    "Jam Keluar",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    jamKeluar,

                onValueChange = {

                    jamKeluar = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Contoh: 16:30")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // JAM KEMBALI
            // ==========================================================

            Text(

                text =
                    "Jam Kembali",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    jamKembali,

                onValueChange = {

                    jamKembali = it
                    pesanError = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Contoh: 18:00")
                },

                singleLine = true,

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // ALASAN
            // ==========================================================

            Text(

                text =
                    "Alasan Pengajuan",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            OutlinedTextField(

                value =
                    alasan,

                onValueChange = {

                    alasan = it
                    pesanError = ""
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(130.dp),

                label = {
                    Text("Jelaskan alasan pengajuan")
                },

                enabled =
                    !sedangMengirim
            )


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ==================================================
            // ERROR
            // ==================================================

            if (pesanError.isNotBlank()) {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFFEE2E2)
                        )

                ) {

                    Text(

                        text =
                            pesanError,

                        modifier =
                            Modifier.padding(14.dp),

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFFB91C1C)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )
            }


            // ==================================================
            // BERHASIL
            // ==================================================

            if (berhasil) {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                SoftGreen
                        )

                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(14.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(28.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(10.dp)
                        )


                        Text(

                            text =
                                "Pengajuan berhasil dikirim.",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )
            }


            // ==================================================
            // TOMBOL KIRIM
            // ==================================================

            Button(

                onClick = {

                    pesanError = ""

                    // ==========================================
                    // VALIDASI
                    // ==========================================

                    if (jenis.isBlank()) {

                        pesanError =
                            "Jenis pengajuan wajib diisi."

                        return@Button
                    }

                    if (tanggalMulai.isBlank()) {

                        pesanError =
                            "Tanggal mulai wajib diisi."

                        return@Button
                    }

                    if (tanggalSelesai.isBlank()) {

                        pesanError =
                            "Tanggal selesai wajib diisi."

                        return@Button
                    }

                    if (alasan.isBlank()) {

                        pesanError =
                            "Alasan pengajuan wajib diisi."

                        return@Button
                    }


                    // ==========================================
                    // KIRIM
                    // ==========================================

                    scope.launch {

                        sedangMengirim = true
                        berhasil = false

                        try {

                            val result =
                                PengajuanRepository.simpanPengajuan(

                                    jenis =
                                        jenis.trim(),

                                    jamPulang =
                                        jamPulang.trim(),

                                    jamKeluar =
                                        jamKeluar.trim(),

                                    jamKembali =
                                        jamKembali.trim(),

                                    tanggalMulai =
                                        tanggalMulai.trim(),

                                    tanggalSelesai =
                                        tanggalSelesai.trim(),

                                    alasan =
                                        alasan.trim()
                                )


                            result
                                .onSuccess {

                                    berhasil = true

                                    onSubmit(

                                        jenis.trim(),

                                        jamPulang.trim(),

                                        jamKeluar.trim(),

                                        jamKembali.trim(),

                                        tanggalMulai.trim(),

                                        tanggalSelesai.trim(),

                                        alasan.trim()
                                    )
                                }
                                .onFailure { error ->

                                    pesanError =
                                        error.message
                                            ?: "Gagal mengirim pengajuan."
                                }

                        } catch (e: Exception) {

                            pesanError =
                                e.message
                                    ?: "Gagal mengirim pengajuan."

                        } finally {

                            sedangMengirim = false
                        }
                    }
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                enabled =
                    !sedangMengirim,

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    )

            ) {

                if (sedangMengirim) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.size(22.dp),

                        color =
                            Color.White,

                        strokeWidth =
                            2.dp
                    )

                } else {

                    Icon(

                        imageVector =
                            Icons.Default.Send,

                        contentDescription =
                            null
                    )


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    Text(
                        text =
                            "Kirim Pengajuan"
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}
