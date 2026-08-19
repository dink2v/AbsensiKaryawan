package com.example.absensikaryawan.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.absensiDataStore by preferencesDataStore(
    name = "absensi_preferences"
)

class AbsensiDataStore(
    private val context: Context
) {

    private object Keys {

        val SUDAH_ABSEN =
            booleanPreferencesKey("sudah_absen")

        val JAM_ABSEN =
            stringPreferencesKey("jam_absen")

        val TANGGAL_ABSEN =
            stringPreferencesKey("tanggal_absen")
    }

    val sudahAbsen: Flow<Boolean>
        get() = context.absensiDataStore.data.map { preferences ->
            preferences[Keys.SUDAH_ABSEN] ?: false
        }

    val jamAbsen: Flow<String>
        get() = context.absensiDataStore.data.map { preferences ->
            preferences[Keys.JAM_ABSEN] ?: ""
        }

    val tanggalAbsen: Flow<String>
        get() = context.absensiDataStore.data.map { preferences ->
            preferences[Keys.TANGGAL_ABSEN] ?: ""
        }

    suspend fun simpanAbsensi(
        jam: String,
        tanggal: String
    ) {

        context.absensiDataStore.edit { preferences ->

            preferences[Keys.SUDAH_ABSEN] = true
            preferences[Keys.JAM_ABSEN] = jam
            preferences[Keys.TANGGAL_ABSEN] = tanggal
        }
    }

    suspend fun hapusAbsensi() {

        context.absensiDataStore.edit { preferences ->

            preferences[Keys.SUDAH_ABSEN] = false
            preferences.remove(Keys.JAM_ABSEN)
            preferences.remove(Keys.TANGGAL_ABSEN)
        }
    }
    suspend fun simpanRiwayat(
        tanggal: String,
        jam: String
    ) {

        context.absensiDataStore.edit { preferences ->

            preferences[Keys.SUDAH_ABSEN] = true
            preferences[Keys.JAM_ABSEN] = jam
            preferences[Keys.TANGGAL_ABSEN] = tanggal
        }
    }
}