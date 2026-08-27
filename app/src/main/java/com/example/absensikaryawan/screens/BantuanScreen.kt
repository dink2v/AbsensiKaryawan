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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// DATA PANDUAN
// ==========================================================

private data class HelpItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val steps: List<String>
)


// ==========================================================
// BANTUAN SCREEN
// ==========================================================

@Composable
fun BantuanScreen(
    onBack: () -> Unit,
    onChatAdmin: () -> Unit
) {

    var selectedHelp by remember {
        mutableStateOf<HelpItem?>(null)
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                            PrimaryGreen
                    )
                }


                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Bantuan",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "Bantuan & informasi penggunaan",

                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // ==================================================
            // JUDUL PANDUAN
            // ==================================================

            Text(
                text = "Panduan Penggunaan",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // ==================================================
            // PANDUAN
            // ==================================================

            val helpItems = listOf(

                HelpItem(
                    icon =
                        Icons.Default.QrCodeScanner,

                    title =
                        "Cara Melakukan Absen",

                    description =
                        "Panduan melakukan Absen Masuk",

                    steps = listOf(
                        "Buka menu Scan.",
                        "Arahkan kamera ke QR Code absensi.",
                        "Pastikan QR Code terbaca.",
                        "Isi catatan jika diperlukan.",
                        "Absensi akan tersimpan otomatis."
                    )
                ),

                HelpItem(
                    icon =
                        Icons.Default.QrCodeScanner,

                    title =
                        "Cara Melakukan Absen Pulang",

                    description =
                        "Panduan melakukan Absen Pulang",

                    steps = listOf(
                        "Buka menu Scan.",
                        "Arahkan kamera ke QR Code absensi.",
                        "Sistem akan mengenali bahwa Absen Masuk sudah dilakukan.",
                        "Sistem otomatis mencatat waktu Absen Pulang."
                    )
                ),

                HelpItem(
                    icon =
                        Icons.Default.History,

                    title =
                        "Cara Melihat Riwayat",

                    description =
                        "Lihat data kehadiran",

                    steps = listOf(
                        "Buka menu Riwayat.",
                        "Pilih bagian Absensi.",
                        "Lihat data Absen Masuk dan Absen Pulang.",
                        "Gunakan filter jika ingin melihat periode tertentu."
                    )
                ),

                HelpItem(
                    icon =
                        Icons.Default.NoteAdd,

                    title =
                        "Cara Membuat Pengajuan",

                    description =
                        "Buat pengajuan izin atau keperluan lainnya",

                    steps = listOf(
                        "Buka menu Pengajuan.",
                        "Tekan Pengajuan Baru.",
                        "Pilih jenis pengajuan.",
                        "Isi tanggal dan waktu sesuai kebutuhan.",
                        "Isi alasan pengajuan.",
                        "Kirim pengajuan."
                    )
                ),

                HelpItem(
                    icon =
                        Icons.Default.Notifications,

                    title =
                        "Cara Melihat Notifikasi",

                    description =
                        "Lihat informasi dan status pengajuan",

                    steps = listOf(
                        "Buka Beranda.",
                        "Tekan ikon Notifikasi.",
                        "Lihat informasi atau status pengajuan yang tersedia."
                    )
                )
            )


            helpItems.forEach { item ->

                HelpCard(
                    item = item,
                    onClick = {
                        selectedHelp = item
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // ==================================================
            // CHAT ADMIN / HRD
            // ==================================================

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Chat,

                            contentDescription =
                                "Chat Admin / HRD",

                            tint =
                                PrimaryGreen,

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
                                    "Chat Admin / HRD",

                                fontSize =
                                    16.sp,

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
                                    "Ada masalah atau bingung menggunakan aplikasi?",

                                fontSize =
                                    12.sp,

                                color =
                                    TextGray
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onChatAdmin()
                                },

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
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 13.dp
                                    ),

                            verticalAlignment =
                                Alignment.CenterVertically,

                            horizontalArrangement =
                                Arrangement.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Chat,

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen,

                                modifier =
                                    Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    "Chat Admin / HRD",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    PrimaryGreen
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }


    // ==========================================================
    // DETAIL PANDUAN
    // ==========================================================

    selectedHelp?.let { item ->

        HelpDetailDialog(
            item = item,
            onDismiss = {
                selectedHelp = null
            }
        )
    }
}


// ==========================================================
// HELP CARD
// ==========================================================

@Composable
private fun HelpCard(
    item: HelpItem,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 15.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    item.icon,

                contentDescription =
                    item.title,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(25.dp)
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
                        item.title,

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        item.description,

                    fontSize =
                        11.sp,

                    color =
                        TextGray
                )
            }


            Icon(
                imageVector =
                    Icons.Default.ChevronRight,

                contentDescription =
                    null,

                tint =
                    TextGray,

                modifier =
                    Modifier.size(21.dp)
            )
        }
    }
}


// ==========================================================
// DETAIL DIALOG
// ==========================================================

@Composable
private fun HelpDetailDialog(
    item: HelpItem,
    onDismiss: () -> Unit
) {

    androidx.compose.material3.AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                text =
                    item.title,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        },

        text = {

            Column {

                item.steps.forEachIndexed { index, step ->

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 5.dp
                                ),

                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Text(
                            text =
                                "${index + 1}.",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen,

                            modifier =
                                Modifier.width(24.dp)
                        )

                        Text(
                            text =
                                step,

                            fontSize =
                                13.sp,

                            color =
                                TextDark,

                            modifier =
                                Modifier.weight(1f)
                        )
                    }
                }
            }
        },

        confirmButton = {

            Text(
                text =
                    "Tutup",

                modifier =
                    Modifier
                        .clickable {
                            onDismiss()
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    PrimaryGreen
            )
        }
    )
}