package com.example.absensikaryawan

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.absensikaryawan.screens.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(
    name = "theme_settings"
)

class ThemeDataStore(
    private val context: Context
) {

    private companion object {

        val THEME_MODE =
            stringPreferencesKey("theme_mode")
    }

    val themeMode: Flow<ThemeMode> =
        context.themeDataStore.data.map { preferences ->

            when (
                preferences[THEME_MODE]
            ) {

                "GELAP" ->
                    ThemeMode.GELAP

                "SISTEM" ->
                    ThemeMode.SISTEM

                else ->
                    ThemeMode.TERANG
            }
        }

    suspend fun saveThemeMode(
        mode: ThemeMode
    ) {

        context.themeDataStore.edit { preferences ->

            preferences[THEME_MODE] =
                when (mode) {

                    ThemeMode.TERANG ->
                        "TERANG"

                    ThemeMode.GELAP ->
                        "GELAP"

                    ThemeMode.SISTEM ->
                        "SISTEM"
                }
        }
    }
}