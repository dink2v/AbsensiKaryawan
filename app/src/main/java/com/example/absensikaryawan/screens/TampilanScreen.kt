package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.ArrowBack
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
            Color(0xFFBDBDBD)
        } else {
            TextGray
        }


    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            pageBackground
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
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

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(

                    onClick =
                        onBack
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


                Text(

                    text =
                        "Tampilan",

                    modifier =
                        Modifier.weight(1f),

                    fontSize =
                        23.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        primaryText
                )
            }


            Spacer(
                modifier =
                    Modifier.size(4.dp)
            )


            Text(

                text =
                    "Atur tampilan aplikasi",

                fontSize =
                    13.sp,

                color =
                    secondaryText,

                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.size(28.dp)
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
                    primaryText,

                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.size(8.dp)
            )


            // ==================================================
            // PILIHAN MODE
            // ==================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

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

                Column {

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
                    Modifier.size(18.dp)
            )


            // ==================================================
            // INFO
            // ==================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            if (isDark) {
                                Color(0xFF1B2A1D)
                            } else {
                                SoftGreen
                            }
                    )
            ) {

                Text(

                    text =
                        "Tampilan akan diterapkan ke seluruh " +
                                "aplikasi dan disimpan secara otomatis.",

                    modifier =
                        Modifier.padding(
                            16.dp
                        ),

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
            Color(0xFFBDBDBD)
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
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ==================================================
        // ICON
        // ==================================================

        Icon(

            imageVector =
                icon,

            contentDescription =
                title,

            tint =
                PrimaryGreen,

            modifier =
                Modifier.size(
                    25.dp
                )
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
                    15.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    textColor
            )


            Spacer(
                modifier =
                    Modifier.size(2.dp)
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


        // ==================================================
        // CHECK
        // ==================================================

        if (selected) {

            Row(

                modifier =
                    Modifier
                        .size(28.dp)
                        .background(
                            PrimaryGreen,
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
                        Modifier.size(
                            18.dp
                        )
                )
            }
        }
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

                        Color(0xFF333333)

                    } else {

                        Color(0xFFEAEAEA)
                    }
                )
    )
}