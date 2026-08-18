package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onStaffClick: () -> Unit,
    onAdminClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Absensi Karyawan",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Kelola kehadiran, pengajuan izin, dan rekap dalam satu aplikasi.",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            Text(
                text = "Silakan pilih jenis akun untuk masuk",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onStaffClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Staf"
                )

                Text(
                    text = "  Saya Staf",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onAdminClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin"
                )

                Text(
                    text = "  Saya Admin",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}