package com.example.absensikaryawan.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================================
// MODEL DATA ABSENSI LOCAL
// ==========================================================

data class AbsensiLocal(
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val qrData: String,
    val catatan: String
)

// ==========================================================
// LOCAL STORAGE ABSENSI
// ==========================================================

class AbsensiLocalStorage(
    private val context: Context
) {

    companion object {

        private const val PREF_NAME =
            "absensi_local_storage"

        private const val KEY_DATA =
            "data_absensi"

        private const val PEMISAH_DATA =
            "|||"

        private const val PEMISAH_FIELD =
            "###"

        private const val MAX_DATA_PER_HARI =
            40
    }

    private val preferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    // ==========================================================
    // TANGGAL SEKARANG
    // ==========================================================

    private fun tanggalSekarang(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }

    // ==========================================================
    // AMBIL SEMUA DATA
    // ==========================================================

    private fun ambilSemuaData(): MutableList<AbsensiLocal> {

        val rawData =
            preferences.getString(
                KEY_DATA,
                ""
            ) ?: ""

        if (rawData.isBlank()) {
            return mutableListOf()
        }

        return rawData
            .split(PEMISAH_DATA)
            .mapNotNull { item ->

                val field =
                    item.split(
                        PEMISAH_FIELD
                    )

                if (field.size >= 5) {

                    AbsensiLocal(
                        tanggal = field[0],
                        jamMasuk = field[1],
                        jamPulang = field[2],
                        qrData = field[3],
                        catatan = field[4]
                    )

                } else {
                    null
                }
            }
            .toMutableList()
    }

    // ==========================================================
    // SIMPAN SEMUA DATA
    // ==========================================================

    private fun simpanSemuaData(
        data: List<AbsensiLocal>
    ) {

        val rawData =
            data.joinToString(
                separator = PEMISAH_DATA
            ) {

                listOf(
                    it.tanggal,
                    it.jamMasuk,
                    it.jamPulang,
                    it.qrData,
                    it.catatan
                ).joinToString(
                    separator = PEMISAH_FIELD
                )
            }

        preferences
            .edit()
            .putString(
                KEY_DATA,
                rawData
            )
            .apply()
    }

    // ==========================================================
    // JUMLAH DATA HARI INI
    // ==========================================================

    fun jumlahHariIni(): Int {

        val tanggal =
            tanggalSekarang()

        return ambilSemuaData()
            .count {
                it.tanggal == tanggal
            }
    }

    // ==========================================================
    // CEK BATAS 40
    // ==========================================================

    fun masihBisaScan(): Boolean {

        return jumlahHariIni() < MAX_DATA_PER_HARI
    }

    // ==========================================================
    // SIMPAN ABSEN MASUK
    // ==========================================================

    fun simpanAbsenMasuk(
        jamMasuk: String,
        qrData: String,
        catatan: String
    ): Boolean {

        if (!masihBisaScan()) {
            return false
        }

        val data =
            ambilSemuaData()

        val tanggal =
            tanggalSekarang()

        data.add(
            AbsensiLocal(
                tanggal = tanggal,
                jamMasuk = jamMasuk,
                jamPulang = "",
                qrData = qrData,
                catatan = catatan
            )
        )

        simpanSemuaData(data)

        return true
    }

    // ==========================================================
    // SIMPAN ABSEN PULANG
    // ==========================================================

    fun simpanAbsenPulang(
        jamPulang: String
    ): Boolean {

        val data =
            ambilSemuaData()

        val tanggal =
            tanggalSekarang()

        val index =
            data.indexOfLast {

                it.tanggal == tanggal &&
                        it.jamPulang.isBlank()
            }

        if (index == -1) {
            return false
        }

        val dataLama =
            data[index]

        data[index] =
            dataLama.copy(
                jamPulang = jamPulang
            )

        simpanSemuaData(data)

        return true
    }

    // ==========================================================
    // AMBIL DATA HARI INI
    // ==========================================================

    fun ambilHariIni(): List<AbsensiLocal> {

        val tanggal =
            tanggalSekarang()

        return ambilSemuaData()
            .filter {
                it.tanggal == tanggal
            }
    }

    // ==========================================================
    // AMBIL SEMUA DATA
    // ==========================================================

    fun ambilSemua(): List<AbsensiLocal> {

        return ambilSemuaData()
            .sortedByDescending {
                it.tanggal
            }
    }

    // ==========================================================
    // HAPUS DATA LOCAL
    // KHUSUS TESTING
    // ==========================================================

    fun hapusSemuaData() {

        preferences
            .edit()
            .remove(KEY_DATA)
            .apply()
    }

    // ==========================================================
    // INFO LIMIT
    // ==========================================================

    fun getMaksimalDataPerHari(): Int {

        return MAX_DATA_PER_HARI
    }
}