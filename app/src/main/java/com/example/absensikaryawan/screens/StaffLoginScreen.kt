package com.example.absensikaryawan.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.absensikaryawan.testing.TestAccounts

@Composable
fun StaffLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    val context = LocalContext.current

    var nomorTelepon by remember {
        mutableStateOf("")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Staf",
                tint = Color(0xFF2563EB)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Login Staf",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Masuk untuk melakukan absensi,\npengajuan izin, dan melihat rekap.",
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 23.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedTextField(
                value = nomorTelepon,
                onValueChange = {
                    nomorTelepon = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Nomor Telepon")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Nomor telepon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = {

                    if (nomorTelepon == TestAccounts.STAFF_PHONE) {
                        Toast.makeText(
                            context,
                            "Login Staf berhasil (Mode Testing)",
                            Toast.LENGTH_SHORT
                        ).show()

                        onLoginSuccess()

                    } else {

                        Toast.makeText(
                            context,
                            "Nomor telepon salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Masuk"
                )

                Text(
                    text = "  Masuk dengan Nomor Telepon",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OutlinedButton(
                onClick = {

                    Toast.makeText(
                        context,
                        "Login Google berhasil (Mode Testing)",
                        Toast.LENGTH_SHORT
                    ).show()

                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Google"
                )

                Text(
                    text = "  Masuk dengan Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}