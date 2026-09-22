package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenLuarKantorScreen(
    onBack: () -> Unit,
    onKirim: (
        lokasi: String,
        alasan: String
    ) -> Unit
) {
    var lokasi by remember { mutableStateOf("") }
    var alasan by remember { mutableStateOf("") }

    var showAlasanSheet by remember { mutableStateOf(false) }
    var alasanDraft by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        containerColor = Color(0xFFF7F8FA)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
                .padding(paddingValues)
                .statusBarsPadding()
        ) {

            // =========================
            // HEADER
            // =========================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(Color.White)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1F2937)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "Absen di Luar Kantor",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Text(
                        text = "Isi lokasi dan keperluan tugas",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // =========================
            // ISI HALAMAN
            // =========================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                verticalArrangement = Arrangement.Top
            ) {

                // =========================
                // INFO CARD
                // =========================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Absen di luar kantor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Silakan isi lokasi dan alasan atau keperluan kamu berada di luar kantor.",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // =========================
                // LOKASI
                // =========================
                Text(
                    text = "Lokasi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(7.dp))

                OutlinedTextField(
                    value = lokasi,
                    onValueChange = {
                        lokasi = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Contoh: Kantor Cabang Kediri",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF2563EB)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =========================
                // ALASAN / KEPERLUAN
                // =========================
                Text(
                    text = "Alasan / Keperluan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF9CA3AF),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            alasanDraft = alasan
                            showAlasanSheet = true
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        if (alasan.isBlank()) {
                            Text(
                                text = "Klik untuk mengisi alasan atau keperluan",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        } else {
                            Text(
                                text = alasan,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937),
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Jelaskan secara singkat keperluan kamu berada di luar kantor.",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // =========================
                // KIRIM ABSEN
                // =========================
                Button(
                    onClick = {
                        onKirim(
                            lokasi.trim(),
                            alasan.trim()
                        )
                    },
                    enabled = lokasi.trim().isNotEmpty() &&
                            alasan.trim().isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        disabledContainerColor = Color(0xFFD1D5DB)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Kirim Absen",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = "Pastikan lokasi dan keperluan sudah benar sebelum dikirim.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    // =========================
    // BOTTOM SHEET ALASAN
    // =========================
    if (showAlasanSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAlasanSheet = false
            },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 24.dp
                    )
            ) {

                Text(
                    text = "Alasan / Keperluan",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Jelaskan keperluan kamu berada di luar kantor.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = alasanDraft,
                    onValueChange = {
                        alasanDraft = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Contoh: Bertemu klien untuk keperluan pekerjaan",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color(0xFF2563EB)
                        )
                    },
                    minLines = 4,
                    maxLines = 6,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {
                            showAlasanSheet = false
                        }
                    ) {
                        Text(
                            text = "Batal",
                            color = Color(0xFF6B7280)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            alasan = alasanDraft.trim()
                            showAlasanSheet = false
                        },
                        enabled = alasanDraft.trim().isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            disabledContainerColor = Color(0xFFD1D5DB)
                        )
                    ) {
                        Text(
                            text = "Simpan",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}