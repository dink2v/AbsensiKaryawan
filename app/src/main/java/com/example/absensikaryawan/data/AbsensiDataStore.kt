package com.example.absensikaryawan.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.absensiDataStore by preferencesDataStore(
    name = "absensi"
)

class AbsensiDataStore(
    private val context: Context
) {

    companion object {

        // ==================================================
        // KEY
        // ==================================================

        private val SUDAH_ABSEN =
            booleanPreferencesKey("sudah_absen")

        private val JAM_ABSEN =
            stringPreferencesKey("jam_absen")

        private val TANGGAL_ABSEN =
            stringPreferencesKey("tanggal_absen")

        private val JAM_PULANG =
            stringPreferencesKey("jam_pulang")

        private val QR_ABSEN =
            stringPreferencesKey("qr_absen")

        private val CATATAN_ABSEN =
            stringPreferencesKey("catatan_absen")
    }


    // ==================================================
    // TANGGAL HARI INI
    // ==================================================

    private fun tanggalHariIni(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }


    // ==================================================
    // SUDAH ABSEN
    // ==================================================

    val sudahAbsen: Flow<Boolean> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN]

            val tanggalHariIni =
                tanggalHariIni()

            if (
                tanggalTersimpan != null &&
                tanggalTersimpan != tanggalHariIni
            ) {

                false

            } else {

                preferences[SUDAH_ABSEN]
                    ?: false
            }
        }


    // ==================================================
    // JAM ABSEN
    // ==================================================

    val jamAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN]

            if (
                tanggalTersimpan != null &&
                tanggalTersimpan != tanggalHariIni()
            ) {

                ""

            } else {

                preferences[JAM_ABSEN]
                    ?: ""
            }
        }


    // ==================================================
    // TANGGAL ABSEN
    // ==================================================

    val tanggalAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            preferences[TANGGAL_ABSEN]
                ?: ""
        }


    // ==================================================
    // JAM PULANG
    // ==================================================

    val jamPulang: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN]

            if (
                tanggalTersimpan != null &&
                tanggalTersimpan != tanggalHariIni()
            ) {

                ""

            } else {

                preferences[JAM_PULANG]
                    ?: ""
            }
        }


    // ==================================================
    // QR ABSEN
    // ==================================================

    val qrAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            preferences[QR_ABSEN]
                ?: ""
        }


    // ==================================================
    // CATATAN ABSEN
    // ==================================================

    val catatanAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            preferences[CATATAN_ABSEN]
                ?: ""
        }


    // ==================================================
    // SIMPAN ABSEN MASUK
    // ==================================================

    suspend fun simpanAbsen(
        jam: String,
        tanggal: String,
        qrData: String,
        catatan: String = ""
    ) {

        context.absensiDataStore.edit { preferences ->

            preferences[SUDAH_ABSEN] =
                true

            preferences[JAM_ABSEN] =
                jam

            preferences[TANGGAL_ABSEN] =
                tanggal

            preferences[QR_ABSEN] =
                qrData

            preferences[CATATAN_ABSEN] =
                catatan

            // Reset jam pulang ketika
            // membuat absensi masuk baru.
            preferences[JAM_PULANG] =
                ""
        }
    }


    // ==================================================
    // SIMPAN ABSEN PULANG
    // ==================================================

    suspend fun simpanPulang(
        jam: String
    ) {

        context.absensiDataStore.edit { preferences ->

            preferences[JAM_PULANG] =
                jam
        }
    }


    // ==================================================
    // RESET ABSENSI
    // ==================================================

    suspend fun resetAbsensi() {

        context.absensiDataStore.edit { preferences ->

            preferences[SUDAH_ABSEN] =
                false

            preferences[JAM_ABSEN] =
                ""

            preferences[TANGGAL_ABSEN] =
                ""

            preferences[JAM_PULANG] =
                ""

            preferences[QR_ABSEN] =
                ""

            preferences[CATATAN_ABSEN] =
                ""
        }
    }
    // ==================================================
// CEK DAN RESET JIKA TANGGAL BERUBAH
// ==================================================

    suspend fun cekDanResetJikaTanggalBerubah() {

        context.absensiDataStore.edit { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN]

            val tanggalHariIni =
                tanggalHariIni()

            if (
                tanggalTersimpan != null &&
                tanggalTersimpan != tanggalHariIni
            ) {

                preferences[SUDAH_ABSEN] =
                    false

                preferences[JAM_ABSEN] =
                    ""

                preferences[TANGGAL_ABSEN] =
                    ""

                preferences[JAM_PULANG] =
                    ""

                preferences[QR_ABSEN] =
                    ""

                preferences[CATATAN_ABSEN] =
                    ""
            }
        }
    }
}