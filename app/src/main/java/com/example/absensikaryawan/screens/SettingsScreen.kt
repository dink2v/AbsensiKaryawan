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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        // ======================================================
        // HEADER
        // ======================================================

        Text(
            text = "Setting",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Pengaturan aplikasi",
            fontSize = 13.sp,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ======================================================
        // APLIKASI
        // ======================================================

        SettingsSectionTitle(
            title = "Aplikasi"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "Tampilan",
                subtitle = "Mode aplikasi",
                onClick = {
                    // Nanti kita isi fitur tampilan
                }
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ======================================================
        // BANTUAN
        // ======================================================

        SettingsSectionTitle(
            title = "Bantuan"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.HelpOutline,
                title = "Bantuan",
                subtitle = "Panduan penggunaan",
                onClick = {
                    // Nanti kita buat halaman Bantuan
                }
            )

            SettingsDivider()

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Tentang Aplikasi",
                subtitle = "Informasi aplikasi",
                onClick = {
                    // Nanti kita buat halaman Tentang Aplikasi
                }
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // ======================================================
        // LOGOUT
        // ======================================================

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onLogout()
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
                        horizontal = 18.dp,
                        vertical = 16.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Keluar",
                    tint = Color.Red,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Keluar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Keluar dari akun",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // ======================================================
        // VERSI
        // ======================================================

        Text(
            text = "Versi 1.0.0",
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 11.sp,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
private fun SettingsSectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )
}


// ==========================================================
// SETTINGS CARD
// ==========================================================

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {

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
            modifier = Modifier.fillMaxWidth()
        ) {

            content()
        }
    }
}


// ==========================================================
// SETTINGS ITEM
// ==========================================================

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // ======================================================
        // ICON
        // ======================================================

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryGreen,
            modifier = Modifier.size(26.dp)
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        // ======================================================
        // TEXT
        // ======================================================

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextGray
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        // ======================================================
        // ARROW
        // ======================================================

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(22.dp)
        )
    }
}


// ==========================================================
// DIVIDER
// ==========================================================

@Composable
private fun SettingsDivider() {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 18.dp)
            .background(
                Color(0xFFEAEAEA)
            )
    )
}