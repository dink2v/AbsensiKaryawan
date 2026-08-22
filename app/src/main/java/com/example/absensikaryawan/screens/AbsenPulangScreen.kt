package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.AbsensiDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AbsenPulangScreen(
    onBack: () -> Unit,
    onPulangSuccess: () -> Unit
) {

    val context = LocalContext.current

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val scope = rememberCoroutineScope()

    // ==========================================
    // CEK SUDAH ABSEN MASUK
    // ==========================================

    val sudahAbsen by absensiDataStore.sudahAbsen.collectAsState(
        initial = false
    )

    // ==========================================
    // JAM MASUK
    // ==========================================

    val jamMasuk by absensiDataStore.jamAbsen.collectAsState(
        initial = ""
    )

    // ==========================================
    // JAM PULANG
    // ==========================================

    val jamPulang by absensiDataStore.jamPulang.collectAsState(
        initial = ""
    )

    // ==========================================
    // JAM REAL-TIME
    // ==========================================

    var jamSekarang by remember {

        mutableStateOf(
            SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        )
    }

    LaunchedEffect(Unit) {

        while (true) {

            jamSekarang =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            delay(1000)
        }
    }

    // ==========================================
    // SURFACE
    // ==========================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // ==================================
            // HEADER
            // ==================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription =
                            "Kembali"
                    )
                }

                Text(
                    text = "Absen Pulang",

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF111827)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==================================
            // JAM SEKARANG
            // ==================================

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
                        defaultElevation = 3.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Schedule,

                        contentDescription =
                            "Waktu",

                        tint =
                            Color(0xFF2563EB)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text = jamSekarang,

                        fontSize = 36.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF111827)
                    )

                    Text(
                        text =
                            "Waktu saat ini",

                        fontSize = 14.sp,

                        color =
                            Color(0xFF6B7280)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            // ==================================
            // INFORMASI ABSEN
            // ==================================

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
                        defaultElevation = 3.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                ) {

                    Text(
                        text =
                            "Kehadiran Hari Ini",

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF111827)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(
                        text =
                            "Jam Masuk",

                        fontSize = 13.sp,

                        color =
                            Color(0xFF6B7280)
                    )

                    Text(
                        text =
                            if (jamMasuk.isNotEmpty()) {
                                jamMasuk
                            } else {
                                "--:--"
                            },

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF111827)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    Text(
                        text =
                            "Jam Pulang",

                        fontSize = 13.sp,

                        color =
                            Color(0xFF6B7280)
                    )

                    Text(
                        text =
                            if (jamPulang.isNotEmpty()) {
                                jamPulang
                            } else {
                                "--:--"
                            },

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF111827)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==================================
            // BELUM ABSEN MASUK
            // ==================================

            if (!sudahAbsen) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFFEE2E2)
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
                                "Belum Absen Masuk",

                            fontSize = 18.sp,

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
                                "Silakan lakukan absen masuk terlebih dahulu.",

                            fontSize = 13.sp,

                            color =
                                Color(0xFF991B1B)
                        )
                    }
                }

            } else if (jamPulang.isNotEmpty()) {

                // ==================================
                // SUDAH PULANG
                // ==================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFDCFCE7)
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

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription =
                                "Berhasil",

                            tint =
                                Color(0xFF16A34A)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "SUDAH ABSEN PULANG",

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF15803D)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Jam pulang: $jamPulang",

                            fontSize = 14.sp,

                            color =
                                Color(0xFF166534)
                        )
                    }
                }

            } else {

                // ==================================
                // TOMBOL ABSEN PULANG
                // ==================================

                Button(

                    onClick = {

                        val jam =
                            SimpleDateFormat(
                                "HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())

                        scope.launch {

                            absensiDataStore.simpanPulang(
                                jam = jam
                            )

                            onPulangSuccess()
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF2563EB)
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,

                        contentDescription =
                            "Absen Pulang"
                    )

                    Text(
                        text =
                            "  KONFIRMASI ABSEN PULANG",

                        fontSize = 16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Jam pulang akan menggunakan waktu saat tombol dikonfirmasi.",

                    modifier =
                        Modifier.fillMaxWidth(),

                    fontSize = 13.sp,

                    color =
                        Color(0xFF6B7280)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ==================================
            // KEMBALI
            // ==================================

            Button(
                onClick = onBack,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Text("Kembali")
            }
        }
    }
}