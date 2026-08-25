package com.example.absensikaryawan.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PengajuanRepository {

    private val db =
        FirebaseFirestore.getInstance()

    private val collection =
        db.collection("pengajuan")

    // ==================================================
    // SIMPAN PENGAJUAN
    // ==================================================

    suspend fun simpanPengajuan(
        nama: String,
        jenis: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        jamPulang: String,
        jamKeluar: String,
        jamKembali: String,
        alasan: String
    ): Result<String> {

        return try {

            val data = hashMapOf(

                "nama" to nama,

                "jenis" to jenis,

                "tanggalMulai" to tanggalMulai,

                "tanggalSelesai" to tanggalSelesai,

                "jamPulang" to jamPulang,

                "jamKeluar" to jamKeluar,

                "jamKembali" to jamKembali,

                "alasan" to alasan,

                "status" to "Menunggu",

                "timestamp" to
                        com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            val document =
                collection
                    .add(data)
                    .await()

            Result.success(
                document.id
            )

        } catch (
            e: Exception
        ) {

            Result.failure(e)
        }
    }
}