package com.example.absensikaryawan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(

    primary = PrimaryGreen,

    secondary = PrimaryGreen,

    tertiary = SoftGreen,

    background = Background,

    surface = Background,

    onPrimary = androidx.compose.ui.graphics.Color.White,

    onSecondary = androidx.compose.ui.graphics.Color.White,

    onTertiary = TextDark,

    onBackground = TextDark,

    onSurface = TextDark
)

private val DarkColorScheme = darkColorScheme(

    primary = PrimaryGreen,

    secondary = PrimaryGreen,

    tertiary = SoftGreen,

    background = TextDark,

    surface = TextDark,

    onPrimary = androidx.compose.ui.graphics.Color.White,

    onSecondary = androidx.compose.ui.graphics.Color.White,

    onTertiary = TextDark,

    onBackground = androidx.compose.ui.graphics.Color.White,

    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun AbsensiKaryawanTheme(

    darkTheme: Boolean =
        isSystemInDarkTheme(),

    dynamicColor: Boolean = false,

    content: @Composable () -> Unit

) {

    // ==========================================================
    // COLOR SCHEME
    // ==========================================================

    val colorScheme = when {

        darkTheme -> {

            DarkColorScheme
        }

        else -> {

            LightColorScheme
        }
    }

    // ==========================================================
    // MATERIAL THEME
    // ==========================================================

    MaterialTheme(

        colorScheme =
            colorScheme,

        typography =
            Typography,

        content =
            content
    )
}