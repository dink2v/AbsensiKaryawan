package com.example.absensikaryawan.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


// ==========================================================
// FIRESTORE REPOSITORY
// ==========================================================

class FirestoreRepository {

    private val db =
        FirebaseFirestore.getInstance()


    // ==========================================================
    // COLLECTION ABSENSI
    // ==========================================================

    private val attendanceCollection =
        db.collection("attendance")


    // ==========================================================
    // SIMPAN ABSEN MASUK
    // ==========================================================

    suspend fun simpanAbsenMasuk(

        uid: String,

        nama: String,

        tanggal: String,

        jamMasuk: String,

        qrData: String,

        catatan: String = ""

    ): Result<Unit> {

        return try {

            val data =
                hashMapOf(

                    "uid" to uid,

                    "nama" to nama,

                    "tanggal" to tanggal,

                    "jamMasuk" to jamMasuk,

                    "jamPulang" to "",

                    "status" to "Hadir",

                    "qrData" to qrData,

                    "catatan" to catatan
                )


            attendanceCollection
                .add(data)
                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // CARI ABSEN HARI INI
    // ==========================================================
    //
    // HASIL:
    //
    // documentId
    // jamMasuk
    // jamPulang
    //
    // Pair digunakan supaya AppNavigation bisa mengetahui
    // kondisi absensi hari ini.
    //
    // ==========================================================

    suspend fun getAbsenHariIni(

        uid: String,

        tanggal: String

    ): AbsenHariIni? {

        return try {

            val snapshot =
                attendanceCollection

                    .whereEqualTo(
                        "uid",
                        uid
                    )

                    .whereEqualTo(
                        "tanggal",
                        tanggal
                    )

                    .limit(1)

                    .get()

                    .await()


            // ==================================================
            // TIDAK ADA ABSEN
            // ==================================================

            if (snapshot.isEmpty) {

                return null
            }


            // ==================================================
            // AMBIL DOCUMENT
            // ==================================================

            val document =
                snapshot.documents.first()


            // ==================================================
            // DOCUMENT ID
            // ==================================================

            val documentId =
                document.id


            // ==================================================
            // JAM MASUK
            // ==================================================

            val jamMasuk =
                document.getString(
                    "jamMasuk"
                ) ?: ""


            // ==================================================
            // JAM PULANG
            // ==================================================

            val jamPulang =
                document.getString(
                    "jamPulang"
                ) ?: ""


            // ==================================================
            // RETURN DATA
            // ==================================================

            AbsenHariIni(

                documentId =
                    documentId,

                jamMasuk =
                    jamMasuk,

                jamPulang =
                    jamPulang
            )

        } catch (e: Exception) {

            throw e
        }
    }


    // ==========================================================
    // SIMPAN ABSEN PULANG
    // ==========================================================

    suspend fun simpanAbsenPulang(

        documentId: String,

        jamPulang: String

    ): Result<Unit> {

        return try {

            attendanceCollection

                .document(
                    documentId
                )

                .update(
                    "jamPulang",
                    jamPulang
                )

                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // SIMPAN PENGAJUAN
    // ==========================================================

    suspend fun simpanPengajuan(

        nama: String,

        jenis: String,

        tanggal: String,

        jamPulang: String,

        jamKeluar: String,

        jamKembali: String,

        tanggalMulai: String,

        tanggalSelesai: String,

        alasan: String

    ): Result<Unit> {

        return try {

            val data =
                hashMapOf(

                    "nama" to nama,

                    "jenis" to jenis,

                    "tanggal" to tanggal,

                    "jamPulang" to jamPulang,

                    "jamKeluar" to jamKeluar,

                    "jamKembali" to jamKembali,

                    "tanggalMulai" to tanggalMulai,

                    "tanggalSelesai" to tanggalSelesai,

                    "alasan" to alasan,

                    "status" to "menunggu",

                    "waktuPengajuan" to
                            com.google.firebase.Timestamp.now()
                )


            db.collection(
                "pengajuan"
            )

                .add(data)

                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN MENUNGGU
    // ==========================================================

    suspend fun getPengajuanMenunggu():

            Result<List<PengajuanData>> {

        return try {

            val snapshot =
                db.collection(
                    "pengajuan"
                )

                    .whereEqualTo(
                        "status",
                        "menunggu"
                    )

                    .get()

                    .await()


            val daftar =
                snapshot.documents.map { document ->

                    PengajuanData(

                        id =
                            document.id,


                        nama =
                            document.getString(
                                "nama"
                            ) ?: "",


                        jenis =
                            document.getString(
                                "jenis"
                            ) ?: "",


                        tanggal =
                            document.getString(
                                "tanggal"
                            ) ?: "",


                        jamPulang =
                            document.getString(
                                "jamPulang"
                            ) ?: "",


                        jamKeluar =
                            document.getString(
                                "jamKeluar"
                            ) ?: "",


                        jamKembali =
                            document.getString(
                                "jamKembali"
                            ) ?: "",


                        tanggalMulai =
                            document.getString(
                                "tanggalMulai"
                            ) ?: "",


                        tanggalSelesai =
                            document.getString(
                                "tanggalSelesai"
                            ) ?: "",


                        alasan =
                            document.getString(
                                "alasan"
                            ) ?: "",


                        status =
                            document.getString(
                                "status"
                            ) ?: "menunggu"
                    )
                }


            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // UPDATE STATUS PENGAJUAN
    // ==========================================================

    suspend fun updateStatusPengajuan(

        documentId: String,

        status: String

    ): Result<Unit> {

        return try {

            db.collection(
                "pengajuan"
            )

                .document(
                    documentId
                )

                .update(
                    "status",
                    status
                )

                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}


// ==========================================================
// DATA ABSEN HARI INI
// ==========================================================
//
// Data ini digunakan AppNavigation untuk menentukan:
//
// 1. Belum ada data
//    → ABSEN MASUK
//
// 2. Sudah ada jamMasuk,
//    jamPulang masih kosong
//    → ABSEN PULANG
//
// 3. jamMasuk dan jamPulang sudah ada
//    → ABSEN SUDAH LENGKAP
//
// ==========================================================

data class AbsenHariIni(

    val documentId: String,

    val jamMasuk: String,

    val jamPulang: String
)


// ==========================================================
// DATA PENGAJUAN
// ==========================================================

data class PengajuanData(

    val id: String,

    val nama: String,

    val jenis: String,

    val tanggal: String,

    val jamPulang: String,

    val jamKeluar: String,

    val jamKembali: String,

    val tanggalMulai: String,

    val tanggalSelesai: String,

    val alasan: String,

    val status: String
)