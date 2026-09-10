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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// ==========================================================
// ADMIN QR SETTING SCREEN
// ==========================================================

@Composable
fun AdminQrSettingScreen(
    onBack: () -> Unit
) {

    // ======================================================
    // FIRESTORE
    // ======================================================

    val db = remember {
        FirebaseFirestore.getInstance()
    }


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope = rememberCoroutineScope()


    // ======================================================
    // QR MALANG
    // ======================================================

    var qrMalang by remember {
        mutableStateOf("")
    }

    var aktifMalang by remember {
        mutableStateOf(true)
    }


    // ======================================================
    // QR BLITAR
    // ======================================================

    var qrBlitar by remember {
        mutableStateOf("")
    }

    var aktifBlitar by remember {
        mutableStateOf(true)
    }


    // ======================================================
    // QR KEDIRI
    // ======================================================

    var qrKediri by remember {
        mutableStateOf("")
    }

    var aktifKediri by remember {
        mutableStateOf(true)
    }


    // ======================================================
    // STATUS
    // ======================================================

    var loading by remember {
        mutableStateOf(true)
    }

    var saving by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }


    // ======================================================
    // LOAD DATA QR
    // ======================================================

    LaunchedEffect(Unit) {

        try {

            val snapshot =
                db.collection("qr_settings")
                    .get()
                    .await()


            snapshot.documents.forEach { document ->

                val qrData =
                    document.getString(
                        "qrData"
                    ) ?: ""


                val aktif =
                    document.getBoolean(
                        "aktif"
                    ) ?: true


                when (
                    document.id.lowercase()
                ) {

                    "malang" -> {

                        qrMalang =
                            qrData

                        aktifMalang =
                            aktif
                    }


                    "blitar" -> {

                        qrBlitar =
                            qrData

                        aktifBlitar =
                            aktif
                    }


                    "kediri" -> {

                        qrKediri =
                            qrData

                        aktifKediri =
                            aktif
                    }
                }
            }

        } catch (e: Exception) {

            message =
                "Gagal mengambil pengaturan QR."

        } finally {

            loading =
                false
        }
    }


    // ======================================================
    // SIMPAN QR
    // ======================================================

    fun simpanQr() {

        scope.launch {

            saving =
                true

            message =
                ""

            try {

                // ==========================================
                // MALANG
                // ==========================================

                db.collection("qr_settings")
                    .document("malang")
                    .set(
                        mapOf(
                            "officeName" to "Malang",
                            "qrData" to qrMalang.trim(),
                            "aktif" to aktifMalang
                        )
                    )
                    .await()


                // ==========================================
                // BLITAR
                // ==========================================

                db.collection("qr_settings")
                    .document("blitar")
                    .set(
                        mapOf(
                            "officeName" to "Blitar",
                            "qrData" to qrBlitar.trim(),
                            "aktif" to aktifBlitar
                        )
                    )
                    .await()


                // ==========================================
                // KEDIRI
                // ==========================================

                db.collection("qr_settings")
                    .document("kediri")
                    .set(
                        mapOf(
                            "officeName" to "Kediri",
                            "qrData" to qrKediri.trim(),
                            "aktif" to aktifKediri
                        )
                    )
                    .await()


                message =
                    "Pengaturan QR berhasil disimpan."

            } catch (e: Exception) {

                e.printStackTrace()

                message =
                    "Gagal menyimpan pengaturan QR."

            } finally {

                saving =
                    false
            }
        }
    }


    // ======================================================
    // ROOT
    // ======================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Background
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )

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
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Kembali",

                    tint =
                        TextDark
                )
            }


            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "QR Kantor",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Text(
                    text =
                        "Atur QR yang boleh digunakan Staff",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }


            Icon(
                imageVector =
                    Icons.Default.QrCode,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(28.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ==================================================
        // INFO
        // ==================================================

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    16.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        PrimaryGreen.copy(
                            alpha = 0.08f
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

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(22.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )


                Text(
                    text =
                        "Staff hanya dapat melakukan absensi menggunakan QR yang terdaftar dan aktif di halaman ini.",

                    fontSize =
                        12.sp,

                    color =
                        TextDark,

                    lineHeight =
                        18.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ==================================================
        // LOADING
        // ==================================================

        if (loading) {

            Text(
                text =
                    "Memuat pengaturan QR...",

                fontSize =
                    13.sp,

                color =
                    TextGray
            )

        } else {

            // ==================================================
            // QR MALANG
            // ==================================================

            QrOfficeCard(

                namaKantor =
                    "Malang",

                qrData =
                    qrMalang,

                aktif =
                    aktifMalang,

                onQrChange = { value: String ->

                    qrMalang =
                        value
                },

                onAktifChange = {

                    aktifMalang =
                        !aktifMalang
                }
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ==================================================
            // QR BLITAR
            // ==================================================

            QrOfficeCard(

                namaKantor =
                    "Blitar",

                qrData =
                    qrBlitar,

                aktif =
                    aktifBlitar,

                onQrChange = { value: String ->

                    qrBlitar =
                        value
                },

                onAktifChange = {

                    aktifBlitar =
                        !aktifBlitar
                }
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ==================================================
            // QR KEDIRI
            // ==================================================

            QrOfficeCard(

                namaKantor =
                    "Kediri",

                qrData =
                    qrKediri,

                aktif =
                    aktifKediri,

                onQrChange = { value: String ->

                    qrKediri =
                        value
                },

                onAktifChange = {

                    aktifKediri =
                        !aktifKediri
                }
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // ==================================================
        // MESSAGE
        // ==================================================

        if (
            message.isNotBlank()
        ) {

            Text(

                text =
                    message,

                fontSize =
                    12.sp,

                color =
                    if (
                        message ==
                        "Pengaturan QR berhasil disimpan."
                    ) {

                        PrimaryGreen

                    } else {

                        Color(0xFFB91C1C)
                    },

                modifier =
                    Modifier.padding(
                        horizontal = 4.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
        }


        // ==================================================
        // SAVE
        // ==================================================

        Button(

            onClick = {

                simpanQr()
            },

            enabled =
                !saving &&
                        !loading,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        PrimaryGreen
                )

        ) {

            Icon(

                imageVector =
                    Icons.Default.Save,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(20.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Text(

                text =
                    if (saving) {

                        "Menyimpan..."

                    } else {

                        "Simpan Pengaturan QR"
                    },

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )
    }
}


// ==========================================================
// QR OFFICE CARD
// ==========================================================

@Composable
private fun QrOfficeCard(

    namaKantor: String,

    qrData: String,

    aktif: Boolean,

    onQrChange: (String) -> Unit,

    onAktifChange: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

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

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp)

        ) {

            // ==================================================
            // TITLE
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Icon(

                    imageVector =
                        Icons.Default.QrCode,

                    contentDescription =
                        null,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(23.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )


                Text(

                    text =
                        "QR $namaKantor",

                    modifier =
                        Modifier.weight(1f),

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )


                Row(

                    modifier =
                        Modifier.clickable {

                            onAktifChange()
                        },

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Icon(

                        imageVector =
                            if (aktif) {

                                Icons.Default.ToggleOn

                            } else {

                                Icons.Default.ToggleOff
                            },

                        contentDescription =
                            if (aktif) {

                                "Nonaktifkan QR $namaKantor"

                            } else {

                                "Aktifkan QR $namaKantor"
                            },

                        tint =
                            if (aktif) {

                                PrimaryGreen

                            } else {

                                TextGray
                            },

                        modifier =
                            Modifier.size(32.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ==================================================
            // QR DATA
            // ==================================================

            OutlinedTextField(

                value =
                    qrData,

                onValueChange =
                    onQrChange,

                modifier =
                    Modifier.fillMaxWidth(),

                label = {

                    Text(
                        "Data / URL QR"
                    )
                },

                placeholder = {

                    Text(
                        "Masukkan URL atau data QR"
                    )
                },

                singleLine =
                    true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Uri
                    ),

                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            // ==================================================
            // STATUS
            // ==================================================

            Text(

                text =
                    if (aktif) {

                        "Status: Aktif — Staff dapat menggunakan QR ini."

                    } else {

                        "Status: Nonaktif — QR ini akan ditolak."
                    },

                fontSize =
                    11.sp,

                color =
                    if (aktif) {

                        PrimaryGreen

                    } else {

                        Color(0xFFB91C1C)
                    }
            )
        }
    }
}