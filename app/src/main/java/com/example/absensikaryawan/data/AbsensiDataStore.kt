package com.example.absensikaryawan.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "absensi_preferences"
)

class AbsensiDataStore(
    private val context: Context
) {

    private val sudahAbsenKey =
        booleanPreferencesKey("sudah_absen")

    private val jamAbsenKey =
        stringPreferencesKey("jam_absen")

    private val tanggalAbsenKey =
        stringPreferencesKey("tanggal_absen")

    val sudahAbsen: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[sudahAbsenKey] ?: false
        }

    val jamAbsen: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[jamAbsenKey] ?: ""
        }

    val tanggalAbsen: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[tanggalAbsenKey] ?: ""
        }

    suspend fun simpanAbsensi(
        jam: String,
        tanggal: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[sudahAbsenKey] = true
            preferences[jamAbsenKey] = jam
            preferences[tanggalAbsenKey] = tanggal
        }
    }
}