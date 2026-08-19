package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF1F3A2D)
private val DarkGreen = Color(0xFF093628)
private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF17221C)
private val TextGray = Color(0xFF6B7280)

@Composable
fun LoginScreen(
    onStaffLogin: () -> Unit,
    onAdminLogin: () -> Unit
) {

    var selectedRole by remember {
        mutableStateOf("Staff")
    }

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            // =========================
            // LOGO
            // =========================

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGreen
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(48.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Absensi Karyawan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Sistem Presensi Karyawan",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // =========================
            // PILIH ROLE
            // =========================

            Text(
                text = "Masuk sebagai",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                LoginRoleCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Person,
                    title = "Saya Staff",
                    selected = selectedRole == "Staff",
                    onClick = {
                        selectedRole = "Staff"
                    }
                )

                LoginRoleCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Saya Admin",
                    selected = selectedRole == "Admin",
                    onClick = {
                        selectedRole = "Admin"
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // FORM STAFF
            // =========================

            if (selectedRole == "Staff") {

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
                            .padding(18.dp)
                    ) {

                        Text(
                            text = "Login Staff",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Gunakan akun yang sudah terdaftar.",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        LoginActionButton(
                            icon = Icons.Default.Person,
                            text = "Masuk dengan Google",
                            dark = true,
                            onClick = onStaffLogin
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        LoginActionButton(
                            icon = Icons.Default.Phone,
                            text = "Masuk dengan Nomor Telepon",
                            dark = false,
                            onClick = onStaffLogin
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Text(
                            text = "Belum terdaftar? Hubungi HRD.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }

            // =========================
            // FORM ADMIN
            // =========================

            if (selectedRole == "Admin") {

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
                            .padding(18.dp)
                    ) {

                        Text(
                            text = "Login Admin / HRD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Masukkan akun administrator.",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Username")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Password")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        LoginActionButton(
                            icon = Icons.Default.ArrowForward,
                            text = "Masuk sebagai Admin",
                            dark = true,
                            onClick = onAdminLogin
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // =========================
            // FOOTER
            // =========================

            Text(
                text = "Absensi Karyawan • Versi 1.0",
                fontSize = 11.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}

@Composable
private fun LoginRoleCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) DarkGreen else Color.White

    val contentColor =
        if (selected) Color.White else TextDark

    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun LoginActionButton(
    icon: ImageVector,
    text: String,
    dark: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (dark) DarkGreen else Color(0xFFE6EEE9)

    val contentColor =
        if (dark) Color.White else PrimaryGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}