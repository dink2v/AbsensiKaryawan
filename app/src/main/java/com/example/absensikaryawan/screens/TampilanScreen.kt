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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
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
// MODE TAMPILAN
// ==========================================================

enum class ThemeMode {
    TERANG,
    GELAP,
    SISTEM
}

// ==========================================================
// TAMPILAN SCREEN
// ==========================================================

@Composable
fun TampilanScreen(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {

    /*
     * Warna mengikuti pola warna Beranda Staff:
     *
     * Background   -> Background
     * Card         -> Color.White
     * Hijau utama  -> PrimaryGreen
     * Hijau lembut -> SoftGreen
     * Teks utama   -> TextDark
     * Teks kedua   -> TextGray
     *
     * Untuk mode GELAP, warna dasar mengikuti theme.
     * Warna hijau tetap menggunakan PrimaryGreen.
     */

    val isDark =
        selectedMode == ThemeMode.GELAP

    val pageBackground =
        if (isDark) {
            Color(0xFF121212)
        } else {
            Background
        }

    val cardBackground =
        if (isDark) {
            Color(0xFF1E1E1E)
        } else {
            Color.White
        }

    val primaryText =
        if (isDark) {
            Color.White
        } else {
            TextDark
        }

    val secondaryText =
        if (isDark) {
            Color.White.copy(alpha = 0.70f)
        } else {
            TextGray
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = pageBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
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
                    onClick = onBack,
                    modifier = Modifier.size(44.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(24.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Tampilan",

                        fontSize =
                            23.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            primaryText
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "Atur tampilan aplikasi",

                        fontSize =
                            12.sp,

                        color =
                            secondaryText
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(26.dp)
            )

            // ==================================================
            // JUDUL MODE
            // ==================================================

            Text(
                text =
                    "Mode Tampilan",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    primaryText
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // ==================================================
            // PILIHAN MODE
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            cardBackground
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    // ==========================================
                    // TERANG
                    // ==========================================

                    ThemeOption(
                        icon =
                            Icons.Default.LightMode,

                        title =
                            "Terang",

                        subtitle =
                            "Tampilan terang",

                        selected =
                            selectedMode ==
                                    ThemeMode.TERANG,

                        darkMode =
                            isDark,

                        onClick = {
                            onModeSelected(
                                ThemeMode.TERANG
                            )
                        }
                    )

                    ThemeDivider(
                        darkMode =
                            isDark
                    )

                    // ==========================================
                    // GELAP
                    // ==========================================

                    ThemeOption(
                        icon =
                            Icons.Default.DarkMode,

                        title =
                            "Gelap",

                        subtitle =
                            "Tampilan gelap",

                        selected =
                            selectedMode ==
                                    ThemeMode.GELAP,

                        darkMode =
                            isDark,

                        onClick = {
                            onModeSelected(
                                ThemeMode.GELAP
                            )
                        }
                    )

                    ThemeDivider(
                        darkMode =
                            isDark
                    )

                    // ==========================================
                    // SISTEM
                    // ==========================================

                    ThemeOption(
                        icon =
                            Icons.Default.SettingsSuggest,

                        title =
                            "Mengikuti Sistem",

                        subtitle =
                            "Mengikuti pengaturan HP",

                        selected =
                            selectedMode ==
                                    ThemeMode.SISTEM,

                        darkMode =
                            isDark,

                        onClick = {
                            onModeSelected(
                                ThemeMode.SISTEM
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            // ==================================================
            // INFO
            // ==================================================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            if (isDark) {
                                PrimaryGreen.copy(
                                    alpha = 0.15f
                                )
                            } else {
                                SoftGreen
                            }
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
            ) {

                Text(
                    text =
                        "Tampilan akan diterapkan ke seluruh " +
                                "aplikasi dan disimpan secara otomatis.",

                    modifier =
                        Modifier.padding(16.dp),

                    fontSize =
                        12.sp,

                    color =
                        secondaryText
                )
            }

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}

// ==========================================================
// THEME OPTION
// ==========================================================

@Composable
private fun ThemeOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    darkMode: Boolean,
    onClick: () -> Unit
) {

    val textColor =
        if (darkMode) {
            Color.White
        } else {
            TextDark
        }

    val subtitleColor =
        if (darkMode) {
            Color.White.copy(alpha = 0.70f)
        } else {
            TextGray
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ==================================================
        // ICON
        // ==================================================

        BoxIcon(
            icon =
                icon,

            darkMode =
                darkMode
        )

        Spacer(
            modifier =
                Modifier.width(14.dp)
        )

        // ==================================================
        // TEXT
        // ==================================================

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
                    textColor
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    subtitle,

                fontSize =
                    11.sp,

                color =
                    subtitleColor
            )
        }

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        // ==================================================
        // CHECK
        // ==================================================

        if (selected) {

            Row(
                modifier =
                    Modifier
                        .size(28.dp)
                        .background(
                            color =
                                PrimaryGreen,

                            shape =
                                CircleShape
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Check,

                    contentDescription =
                        "Dipilih",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==========================================================
// ICON
// ==========================================================

@Composable
private fun BoxIcon(
    icon: ImageVector,
    darkMode: Boolean
) {

    val iconBackground =
        if (darkMode) {
            PrimaryGreen.copy(
                alpha = 0.16f
            )
        } else {
            SoftGreen
        }

    Row(
        modifier =
            Modifier
                .size(42.dp)
                .background(
                    color =
                        iconBackground,

                    shape =
                        RoundedCornerShape(12.dp)
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
                null,

            tint =
                PrimaryGreen,

            modifier =
                Modifier.size(22.dp)
        )
    }
}

// ==========================================================
// DIVIDER
// ==========================================================

@Composable
private fun ThemeDivider(
    darkMode: Boolean
) {

    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(

                    if (darkMode) {

                        Color.White.copy(
                            alpha = 0.08f
                        )

                    } else {

                        TextGray.copy(
                            alpha = 0.12f
                        )
                    }
                )
    )
}