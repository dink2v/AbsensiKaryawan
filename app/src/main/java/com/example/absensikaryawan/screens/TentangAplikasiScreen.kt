package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// TENTANG APLIKASI SCREEN
// ==========================================================

@Composable
fun TentangAplikasiScreen(
    onBack: () -> Unit
) {

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
                        text = "Tentang Aplikasi",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Informasi aplikasi",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // ==================================================
            // IDENTITAS APLIKASI
            // ==================================================

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

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
                            .padding(22.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    // ==================================================
                    // ICON APLIKASI
                    // ==================================================

                    Row(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .background(
                                    color = SoftGreen,
                                    shape = CircleShape
                                ),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription =
                                "Absensi Karyawan",

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(42.dp)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Text(
                        text =
                            "Absensi Karyawan",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(
                        text =
                            "Aplikasi Absensi Karyawan",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            "Versi 1.0",

                        fontSize =
                            11.sp,

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
            // TENTANG
            // ==================================================

            Text(
                text =
                    "Tentang Aplikasi",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


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

                Text(
                    text =
                        "Absensi Karyawan merupakan aplikasi yang " +
                                "digunakan untuk membantu proses pencatatan " +
                                "kehadiran karyawan secara lebih mudah, cepat, " +
                                "dan terorganisir.",

                    modifier =
                        Modifier.padding(16.dp),

                    fontSize =
                        13.sp,

                    lineHeight =
                        20.sp,

                    color =
                        TextGray
                )
            }


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // ==================================================
            // FITUR UTAMA
            // ==================================================

            Text(
                text =
                    "Fitur Utama",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            FeatureInfo(
                icon =
                    Icons.Default.QrCodeScanner,

                title =
                    "Absen Masuk",

                description =
                    "Melakukan pencatatan kehadiran melalui QR Code."
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            FeatureInfo(
                icon =
                    Icons.Default.QrCodeScanner,

                title =
                    "Absen Pulang",

                description =
                    "Mencatat waktu kepulangan karyawan secara otomatis."
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            FeatureInfo(
                icon =
                    Icons.Default.History,

                title =
                    "Riwayat Absensi",

                description =
                    "Melihat riwayat kehadiran yang telah tercatat."
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            FeatureInfo(
                icon =
                    Icons.Default.NoteAdd,

                title =
                    "Pengajuan",

                description =
                    "Membuat dan melihat pengajuan keperluan karyawan."
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            FeatureInfo(
                icon =
                    Icons.Default.Notifications,

                title =
                    "Notifikasi",

                description =
                    "Menerima informasi dan status pengajuan."
            )


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // ==================================================
            // INFORMASI
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

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    verticalAlignment =
                        Alignment.Top
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Info,

                        contentDescription =
                            "Informasi",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(23.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )


                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Informasi Aplikasi",

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )


                        Text(
                            text =
                                "Gunakan aplikasi sesuai dengan prosedur " +
                                        "yang telah ditetapkan oleh perusahaan. " +
                                        "Jika mengalami masalah, silakan hubungi " +
                                        "Admin / HRD melalui menu Bantuan.",

                            fontSize =
                                12.sp,

                            lineHeight =
                                18.sp,

                            color =
                                TextGray
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // ==================================================
            // FOOTER
            // ==================================================

            Text(
                text =
                    "Absensi Karyawan",

                modifier =
                    Modifier.fillMaxWidth(),

                fontSize =
                    11.sp,

                color =
                    TextGray,

                textAlign =
                    androidx.compose.ui.text.style.TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(
                text =
                    "Versi 1.0",

                modifier =
                    Modifier.fillMaxWidth(),

                fontSize =
                    10.sp,

                color =
                    TextGray,

                textAlign =
                    androidx.compose.ui.text.style.TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}


// ==========================================================
// FEATURE INFO
// ==========================================================

@Composable
private fun FeatureInfo(
    icon: ImageVector,
    title: String,
    description: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(
                modifier =
                    Modifier
                        .size(44.dp)
                        .background(
                            color = SoftGreen,
                            shape = RoundedCornerShape(12.dp)
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        icon,

                    contentDescription =
                        title,

                    tint =
                        PrimaryGreen,

                    modifier =
                        Modifier.size(23.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        title,

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
                        description,

                    fontSize =
                        11.sp,

                    lineHeight =
                        16.sp,

                    color =
                        TextGray
                )
            }
        }
    }
}