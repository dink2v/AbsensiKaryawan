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
    name = "absensi_data"
)

class AbsensiDataStore(
    private val context: Context
) {

    companion object {

        // =====================================
        // KEY DATA ABSENSI
        // =====================================

        private val SUDAH_ABSEN =
            booleanPreferencesKey("sudah_absen")

        private val JAM_ABSEN =
            stringPreferencesKey("jam_absen")

        private val TANGGAL_ABSEN =
            stringPreferencesKey("tanggal_absen")

        private val JAM_PULANG =
            stringPreferencesKey("jam_pulang")

        private val QR_DATA =
            stringPreferencesKey("qr_data")

        private val CATATAN_ABSEN =
            stringPreferencesKey("catatan_absen")
    }

    // =====================================
    // TANGGAL HARI INI
    // =====================================

    private fun tanggalHariIni(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }

    // =====================================
    // SUDAH ABSEN
    // =====================================

    val sudahAbsen: Flow<Boolean> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            val sudahAbsen =
                preferences[SUDAH_ABSEN] ?: false

            sudahAbsen &&
                    tanggalTersimpan == tanggalHariIni()
        }

    // =====================================
    // JAM MASUK
    // =====================================

    val jamAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            if (
                tanggalTersimpan ==
                tanggalHariIni()
            ) {

                preferences[JAM_ABSEN] ?: ""

            } else {

                ""
            }
        }

    // =====================================
    // TANGGAL ABSEN
    // =====================================

    val tanggalAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            preferences[TANGGAL_ABSEN] ?: ""
        }

    // =====================================
    // JAM PULANG
    // =====================================

    val jamPulang: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            if (
                tanggalTersimpan ==
                tanggalHariIni()
            ) {

                preferences[JAM_PULANG] ?: ""

            } else {

                ""
            }
        }

    // =====================================
    // QR DATA
    // =====================================

    val qrData: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            if (
                tanggalTersimpan ==
                tanggalHariIni()
            ) {

                preferences[QR_DATA] ?: ""

            } else {

                ""
            }
        }

    // =====================================
    // CATATAN ABSENSI
    // =====================================

    val catatanAbsen: Flow<String> =
        context.absensiDataStore.data.map { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            if (
                tanggalTersimpan ==
                tanggalHariIni()
            ) {

                preferences[CATATAN_ABSEN] ?: ""

            } else {

                ""
            }
        }

    // =====================================
    // SIMPAN ABSEN MASUK
    // =====================================

    suspend fun simpanAbsen(
        jam: String,
        tanggal: String,
        qrData: String = "",
        catatan: String = ""
    ) {

        context.absensiDataStore.edit { preferences ->

            // Tandai sudah absen
            preferences[SUDAH_ABSEN] = true

            // Simpan jam masuk
            preferences[JAM_ABSEN] = jam

            // Simpan tanggal
            preferences[TANGGAL_ABSEN] = tanggal

            // Simpan QR
            preferences[QR_DATA] = qrData

            // Simpan catatan
            preferences[CATATAN_ABSEN] = catatan

            // Absensi baru = belum pulang
            preferences[JAM_PULANG] = ""
        }
    }

    // =====================================
    // SIMPAN ABSEN PULANG
    // =====================================

    suspend fun simpanPulang(
        jam: String
    ) {

        context.absensiDataStore.edit { preferences ->

            val tanggalTersimpan =
                preferences[TANGGAL_ABSEN] ?: ""

            if (
                tanggalTersimpan ==
                tanggalHariIni()
            ) {

                preferences[JAM_PULANG] = jam
            }
        }
    }

    // =====================================
    // RESET ABSEN
    // =====================================

    suspend fun resetAbsen() {

        context.absensiDataStore.edit { preferences ->

            preferences[SUDAH_ABSEN] = false

            preferences[JAM_ABSEN] = ""

            preferences[TANGGAL_ABSEN] = ""

            preferences[JAM_PULANG] = ""

            preferences[QR_DATA] = ""

            preferences[CATATAN_ABSEN] = ""
        }
    }
}