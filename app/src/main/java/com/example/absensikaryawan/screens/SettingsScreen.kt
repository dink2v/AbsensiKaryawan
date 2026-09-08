package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
// SETTINGS SCREEN
// ==========================================================

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNotification: () -> Unit,
    onTampilan: () -> Unit,
    onChatAdmin: () -> Unit,
    onBantuan: () -> Unit,
    onTentangAplikasi: () -> Unit,
    onLogout: () -> Unit
) {

    // Parameter tetap dipertahankan agar kompatibel
    // dengan AppNavigation.kt.
    // Chat Admin / HRD sekarang berada di dalam Bantuan.
    @Suppress("UNUSED_VARIABLE")
    val keepChatAdminCallback = onChatAdmin

    Scaffold(
        containerColor = Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Pengaturan",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Atur preferensi dan informasi aplikasi",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            // ==================================================
            // JUDUL
            // ==================================================

            Text(
                text = "Pengaturan Aplikasi",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // ==================================================
            // MENU SETTINGS
            // ==================================================

            SettingsCard {

                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifikasi",
                    description = "Kelola pemberitahuan aplikasi",
                    onClick = onNotification
                )

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Tampilan",
                    description = "Atur tema dan tampilan aplikasi",
                    onClick = onTampilan
                )

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Bantuan",
                    description = "Panduan penggunaan aplikasi",
                    onClick = onBantuan
                )

                SettingsDivider()

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Tentang Aplikasi",
                    description = "Informasi aplikasi dan versi",
                    onClick = onTentangAplikasi
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ==================================================
            // LOGOUT
            // ==================================================

            Text(
                text = "Akun",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LogoutCard(
                onClick = onLogout
            )
        }
    }
}


// ==========================================================
// SETTINGS CARD
// ==========================================================

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}


// ==========================================================
// SETTINGS ITEM
// ==========================================================

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 15.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==================================================
        // ICON CONTAINER
        // ==================================================

        Card(
            modifier = Modifier.size(42.dp),

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
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        // ==================================================
        // TEXT
        // ==================================================

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = description,
                fontSize = 11.sp,
                color = TextGray
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        // ==================================================
        // CHEVRON
        // ==================================================

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(21.dp)
        )
    }
}


// ==========================================================
// DIVIDER
// ==========================================================

@Composable
private fun SettingsDivider() {

    HorizontalDivider(
        modifier = Modifier.padding(
            horizontal = 16.dp
        ),

        color = TextGray.copy(
            alpha = 0.12f
        )
    )
}


// ==========================================================
// LOGOUT CARD
// ==========================================================

@Composable
private fun LogoutCard(
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
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

            // ==================================================
            // LOGOUT ICON
            // ==================================================

            Card(
                modifier = Modifier.size(42.dp),

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
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Keluar",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // ==================================================
            // TEXT
            // ==================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Keluar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Keluar dari akun aplikasi",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            // ==================================================
            // CHEVRON
            // ==================================================

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Keluar",
                tint = TextGray,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}