package com.example.absensikaryawan.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.historyDataStore by preferencesDataStore(
    name = "absensi_history"
)

data class HistoryAbsensi(
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val qrData: String,
    val catatan: String
)

class AbsensiHistoryDataStore(
    private val context: Context
) {

    companion object {

        private val HISTORY_DATA =
            stringPreferencesKey("history_data")
    }

    // ==========================================================
    // AMBIL SEMUA HISTORY
    // ==========================================================

    val history: Flow<List<HistoryAbsensi>> =
        context.historyDataStore.data.map { preferences ->

            val data =
                preferences[HISTORY_DATA]
                    ?: "[]"

            parseHistory(data)
        }

    // ==========================================================
    // PARSE DATA JSON
    // ==========================================================

    private fun parseHistory(
        data: String
    ): List<HistoryAbsensi> {

        return try {

            val jsonArray =
                JSONArray(data)

            val result =
                mutableListOf<HistoryAbsensi>()

            for (i in 0 until jsonArray.length()) {

                val item =
                    jsonArray.getJSONObject(i)

                result.add(
                    HistoryAbsensi(

                        tanggal =
                            item.optString(
                                "tanggal",
                                ""
                            ),

                        jamMasuk =
                            item.optString(
                                "jamMasuk",
                                ""
                            ),

                        jamPulang =
                            item.optString(
                                "jamPulang",
                                ""
                            ),

                        qrData =
                            item.optString(
                                "qrData",
                                ""
                            ),

                        catatan =
                            item.optString(
                                "catatan",
                                ""
                            )
                    )
                )
            }

            result

        } catch (
            e: Exception
        ) {

            emptyList()
        }
    }

    // ==========================================================
    // BUAT JSON
    // ==========================================================

    private fun createJson(
        history: List<HistoryAbsensi>
    ): String {

        val jsonArray =
            JSONArray()

        history.forEach { item ->

            val jsonObject =
                JSONObject()

            jsonObject.put(
                "tanggal",
                item.tanggal
            )

            jsonObject.put(
                "jamMasuk",
                item.jamMasuk
            )

            jsonObject.put(
                "jamPulang",
                item.jamPulang
            )

            jsonObject.put(
                "qrData",
                item.qrData
            )

            jsonObject.put(
                "catatan",
                item.catatan
            )

            jsonArray.put(
                jsonObject
            )
        }

        return jsonArray.toString()
    }

    // ==========================================================
    // SIMPAN ABSEN MASUK
    // ==========================================================

    suspend fun simpanAbsenMasuk(
        tanggal: String,
        jamMasuk: String,
        qrData: String,
        catatan: String
    ) {

        context.historyDataStore.edit { preferences ->

            val dataLama =
                preferences[HISTORY_DATA]
                    ?: "[]"

            val history =
                parseHistory(dataLama)
                    .toMutableList()

            val index =
                history.indexOfFirst {
                    it.tanggal == tanggal
                }

            val dataBaru =
                HistoryAbsensi(

                    tanggal =
                        tanggal,

                    jamMasuk =
                        jamMasuk,

                    jamPulang =
                        "",

                    qrData =
                        qrData,

                    catatan =
                        catatan
                )

            if (index >= 0) {

                // Kalau tanggal sudah ada,
                // update data tanggal tersebut.

                history[index] =
                    dataBaru

            } else {

                // Kalau belum ada,
                // tambahkan sebagai data baru.

                history.add(
                    dataBaru
                )
            }

            // Urutkan tanggal terbaru di atas.

            history.sortByDescending {
                it.tanggal
            }

            preferences[HISTORY_DATA] =
                createJson(history)
        }
    }

    // ==========================================================
    // SIMPAN ABSEN PULANG
    // ==========================================================

    suspend fun simpanAbsenPulang(
        tanggal: String,
        jamPulang: String
    ) {

        context.historyDataStore.edit { preferences ->

            val dataLama =
                preferences[HISTORY_DATA]
                    ?: "[]"

            val history =
                parseHistory(dataLama)
                    .toMutableList()

            val index =
                history.indexOfFirst {
                    it.tanggal == tanggal
                }

            if (index >= 0) {

                val dataLamaHariIni =
                    history[index]

                history[index] =
                    dataLamaHariIni.copy(
                        jamPulang =
                            jamPulang
                    )

            } else {

                // Kalau data masuk belum ada,
                // tetap buat record baru.

                history.add(
                    HistoryAbsensi(

                        tanggal =
                            tanggal,

                        jamMasuk =
                            "",

                        jamPulang =
                            jamPulang,

                        qrData =
                            "",

                        catatan =
                            ""
                    )
                )
            }

            history.sortByDescending {
                it.tanggal
            }

            preferences[HISTORY_DATA] =
                createJson(history)
        }
    }

    // ==========================================================
    // UPDATE CATATAN
    // ==========================================================

    suspend fun updateCatatan(
        tanggal: String,
        catatan: String
    ) {

        context.historyDataStore.edit { preferences ->

            val dataLama =
                preferences[HISTORY_DATA]
                    ?: "[]"

            val history =
                parseHistory(dataLama)
                    .toMutableList()

            val index =
                history.indexOfFirst {
                    it.tanggal == tanggal
                }

            if (index >= 0) {

                val dataLama =
                    history[index]

                history[index] =
                    dataLama.copy(
                        catatan =
                            catatan
                    )
            }

            preferences[HISTORY_DATA] =
                createJson(history)
        }
    }

    // ==========================================================
    // HAPUS SEMUA HISTORY
    // ==========================================================

    suspend fun hapusSemuaHistory() {

        context.historyDataStore.edit { preferences ->

            preferences.remove(
                HISTORY_DATA
            )
        }
    }

    // ==========================================================
    // HAPUS HISTORY BERDASARKAN TANGGAL
    // ==========================================================

    suspend fun hapusHistoryTanggal(
        tanggal: String
    ) {

        context.historyDataStore.edit { preferences ->

            val dataLama =
                preferences[HISTORY_DATA]
                    ?: "[]"

            val history =
                parseHistory(dataLama)
                    .filter {
                        it.tanggal != tanggal
                    }

            preferences[HISTORY_DATA] =
                createJson(history)
        }
    }
}