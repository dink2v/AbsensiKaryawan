package com.example.absensikaryawan.data

import com.google.firebase.Timestamp
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
    // COLLECTION USERS / KARYAWAN
    // ==========================================================

    private val usersCollection =
        db.collection("users")


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
                hashMapOf<String, Any>(
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

            if (snapshot.isEmpty) {
                return null
            }

            val document =
                snapshot.documents.first()

            AbsenHariIni(
                documentId = document.id,

                jamMasuk =
                    document.getString(
                        "jamMasuk"
                    ) ?: "",

                jamPulang =
                    document.getString(
                        "jamPulang"
                    ) ?: ""
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
        uid: String,
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
                hashMapOf<String, Any>(
                    "uid" to uid,
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
                    "waktuPengajuan" to Timestamp.now()
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


    // ==========================================================
    // KARYAWAN
    // ==========================================================


    // ==========================================================
    // TAMBAH KARYAWAN
    // ==========================================================

    suspend fun tambahKaryawan(
        nama: String,
        email: String,
        jabatan: String,
        divisi: String,
        usernameTele: String,
        isAdmin: Boolean
    ): Result<String> {

        return try {

            val data =
                hashMapOf<String, Any>(

                    "nama" to nama,

                    "email" to email,

                    "jabatan" to jabatan,

                    "divisi" to divisi,

                    "usernameTele" to usernameTele,

                    "isAdmin" to isAdmin
                )

            val document =
                usersCollection
                    .add(data)
                    .await()

            Result.success(
                document.id
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL SEMUA KARYAWAN
    // ==========================================================

    suspend fun getSemuaKaryawan():
            Result<List<KaryawanData>> {

        return try {

            val snapshot =
                usersCollection
                    .get()
                    .await()

            val daftar =
                snapshot.documents.map { document ->

                    KaryawanData(

                        id =
                            document.id,

                        nama =
                            document.getString(
                                "nama"
                            ) ?: "",

                        email =
                            document.getString(
                                "email"
                            ) ?: "",

                        jabatan =
                            document.getString(
                                "jabatan"
                            ) ?: "",

                        divisi =
                            document.getString(
                                "divisi"
                            ) ?: "",

                        usernameTele =
                            document.getString(
                                "usernameTele"
                            ) ?: "",

                        isAdmin =
                            document.getBoolean(
                                "isAdmin"
                            ) ?: false
                    )
                }
                    .sortedBy {
                        it.nama.lowercase()
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // UPDATE KARYAWAN
    // ==========================================================

    suspend fun updateKaryawan(
        documentId: String,
        nama: String,
        email: String,
        jabatan: String,
        divisi: String,
        usernameTele: String,
        isAdmin: Boolean
    ): Result<Unit> {

        return try {

            val data =
                hashMapOf<String, Any>(

                    "nama" to nama,

                    "email" to email,

                    "jabatan" to jabatan,

                    "divisi" to divisi,

                    "usernameTele" to usernameTele,

                    "isAdmin" to isAdmin
                )

            usersCollection
                .document(
                    documentId
                )
                .update(
                    data
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // HAPUS KARYAWAN
    // ==========================================================

    suspend fun hapusKaryawan(
        documentId: String
    ): Result<Unit> {

        return try {

            usersCollection
                .document(
                    documentId
                )
                .delete()
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


// ==========================================================
// DATA KARYAWAN
// ==========================================================

data class KaryawanData(

    val id: String,

    val nama: String,

    val email: String,

    val jabatan: String,

    val divisi: String,

    val usernameTele: String,

    val isAdmin: Boolean
)