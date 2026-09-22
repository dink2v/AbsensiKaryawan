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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private data class HelpItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val steps: List<String>
)

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
                .background(Background)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // =========================
            // HEADER
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Bantuan",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Bantuan & informasi penggunaan",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // =========================
            // JUDUL SECTION
            // =========================
            Text(
                text = "Panduan Penggunaan",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // CARA MELAKUKAN ABSEN
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.QrCodeScanner,
                    title = "Cara Melakukan Absen",
                    description = "Panduan melakukan absensi masuk menggunakan QR Code.",
                    steps = listOf(
                        "Buka menu Scan pada halaman utama aplikasi.",
                        "Arahkan kamera ke QR Code kantor yang telah terdaftar.",
                        "Tunggu sampai QR Code berhasil terbaca.",
                        "Pastikan kantor yang terdeteksi sudah sesuai.",
                        "Isi catatan jika diperlukan.",
                        "Absensi masuk akan tersimpan secara otomatis."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // ABSEN PULANG
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.QrCodeScanner,
                    title = "Cara Melakukan Absen Pulang",
                    description = "Panduan melakukan absensi pulang setelah menyelesaikan pekerjaan.",
                    steps = listOf(
                        "Buka menu Scan pada aplikasi.",
                        "Arahkan kamera ke QR Code kantor.",
                        "Sistem akan mengenali bahwa absensi masuk sudah dilakukan.",
                        "Lakukan proses scan seperti biasa.",
                        "Sistem akan mencatat waktu sebagai jam pulang.",
                        "Data absensi pulang akan tersimpan otomatis."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // ABSEN LUAR KANTOR
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.QrCodeScanner,
                    title = "Cara Absen di Luar Kantor",
                    description = "Panduan melakukan absensi ketika sedang berada di luar kantor.",
                    steps = listOf(
                        "Buka menu Scan.",
                        "Tekan pilihan Absen di Luar Kantor.",
                        "Isi lokasi tempat Anda melakukan pekerjaan.",
                        "Contoh lokasi: SMK Negeri 1 Blitar atau Kantor Cabang Kediri.",
                        "Isi alasan melakukan pekerjaan di luar kantor.",
                        "Tekan Kirim Absen.",
                        "Sistem akan mencatat absensi masuk menggunakan waktu saat pengajuan dikirim.",
                        "Setelah selesai bekerja, lakukan absensi pulang seperti biasa."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // RIWAYAT
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.History,
                    title = "Cara Melihat Riwayat",
                    description = "Melihat riwayat absensi dan pengajuan yang telah dilakukan.",
                    steps = listOf(
                        "Buka menu Riwayat.",
                        "Pilih tab Absensi untuk melihat data kehadiran.",
                        "Pilih tab Pengajuan untuk melihat pengajuan.",
                        "Data masuk dan pulang akan ditampilkan pada riwayat absensi.",
                        "Gunakan filter yang tersedia untuk membantu mencari data."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // PENGAJUAN
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.NoteAdd,
                    title = "Cara Membuat Pengajuan",
                    description = "Panduan membuat pengajuan izin, keperluan, atau kebutuhan lainnya.",
                    steps = listOf(
                        "Buka menu Pengajuan.",
                        "Tekan tombol Pengajuan Baru.",
                        "Pilih jenis pengajuan.",
                        "Isi tanggal dan waktu sesuai kebutuhan.",
                        "Masukkan alasan pengajuan.",
                        "Periksa kembali data yang telah diisi.",
                        "Tekan Kirim Pengajuan.",
                        "Pengajuan akan masuk ke proses approval sesuai hierarki yang telah ditentukan."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =========================
            // NOTIFIKASI
            // =========================
            HelpCard(
                item = HelpItem(
                    icon = Icons.Default.Notifications,
                    title = "Cara Melihat Notifikasi",
                    description = "Melihat informasi terbaru mengenai absensi, pengajuan, dan chat.",
                    steps = listOf(
                        "Buka halaman Beranda.",
                        "Tekan ikon Notifikasi.",
                        "Daftar pemberitahuan terbaru akan ditampilkan.",
                        "Notifikasi dapat berisi informasi absensi, pengajuan, atau pesan dari Admin.",
                        "Tekan notifikasi untuk membuka informasi terkait."
                    )
                ),
                onClick = {
                    selectedHelp = it
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =========================
            // CHAT ADMIN / HRD
            // =========================
            Text(
                text = "Chat Admin / HRD",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SoftGreen
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Chat Admin",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Butuh bantuan?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Ada masalah atau bingung menggunakan aplikasi?",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onChatAdmin()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftGreen
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat Admin / HRD",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Chat Admin / HRD",
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // =========================
    // DETAIL BANTUAN
    // =========================
    selectedHelp?.let { item ->
        HelpDetailDialog(
            item = item,
            onDismiss = {
                selectedHelp = null
            }
        )
    }
}

@Composable
private fun HelpCard(
    item: HelpItem,
    onClick: (HelpItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(item)
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SoftGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Composable
private fun HelpDetailDialog(
    item: HelpItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                item.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            modifier = Modifier.width(24.dp)
                        )

                        Text(
                            text = step,
                            fontSize = 13.sp,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Tutup",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    )
}