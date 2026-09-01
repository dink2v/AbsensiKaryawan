package com.example.absensikaryawan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PengajuanRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // ==========================================================
    // SIMPAN PENGAJUAN
    // ==========================================================

    suspend fun simpanPengajuan(
        jenis: String,
        jamPulang: String,
        jamKeluar: String,
        jamKembali: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        alasan: String
    ): Result<Unit> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User belum login.")
                    )

            val uid =
                currentUser.uid


            // ==================================================
            // DATA USER
            // ==================================================

            val userDocument =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()


            val nama =
                userDocument.getString("nama")
                    ?: currentUser.displayName
                    ?: "Karyawan"


            // ==================================================
            // DATA PENGAJUAN
            // ==================================================

            val data =
                hashMapOf<String, Any>(

                    "uid" to uid,

                    "nama" to nama,

                    "jenis" to jenis,

                    "jamPulang" to jamPulang,

                    "jamKeluar" to jamKeluar,

                    "jamKembali" to jamKembali,

                    "tanggalMulai" to tanggalMulai,

                    "tanggalSelesai" to tanggalSelesai,

                    "alasan" to alasan,

                    "status" to "menunggu",

                    "createdAt" to
                            FieldValue.serverTimestamp(),

                    "approvedAt" to "",

                    "approvedBy" to "",

                    "catatanAdmin" to ""
                )


            // ==================================================
            // SIMPAN
            // ==================================================

            firestore
                .collection("pengajuan")
                .add(data)
                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN SAYA
    // KHUSUS USER YANG SEDANG LOGIN
    // ==========================================================

    suspend fun ambilPengajuanSaya():
            Result<List<Map<String, Any>>> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User belum login.")
                    )

            val uid =
                currentUser.uid


            val snapshot =
                firestore
                    .collection("pengajuan")
                    .whereEqualTo(
                        "uid",
                        uid
                    )
                    .get()
                    .await()


            val data =
                snapshot.documents.map { document ->

                    val item =
                        HashMap<String, Any>()


                    // ==================================================
                    // DOCUMENT ID
                    // ==================================================

                    item["documentId"] =
                        document.id


                    // ==================================================
                    // SEMUA FIELD
                    // ==================================================

                    document.data?.forEach { entry ->

                        val key =
                            entry.key

                        val value =
                            entry.value

                        if (value != null) {

                            item[key] =
                                value
                        }
                    }

                    item
                }


            Result.success(data)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL SEMUA PENGAJUAN
    // KHUSUS ADMIN
    // ==========================================================

    suspend fun ambilSemuaPengajuan():
            Result<List<Map<String, Any>>> {

        return try {

            val snapshot =
                firestore
                    .collection("pengajuan")
                    .get()
                    .await()


            val data =
                snapshot.documents.map { document ->

                    val item =
                        HashMap<String, Any>()


                    item["documentId"] =
                        document.id


                    document.data?.forEach { entry ->

                        val key =
                            entry.key

                        val value =
                            entry.value

                        if (value != null) {

                            item[key] =
                                value
                        }
                    }

                    item
                }


            Result.success(data)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // UPDATE STATUS
    // KHUSUS ADMIN
    // ==========================================================

    suspend fun updateStatusPengajuan(
        documentId: String,
        status: String
    ): Result<Unit> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("Admin belum login.")
                    )


            val data =
                hashMapOf<String, Any>(

                    "status" to status,

                    "approvedBy" to
                            currentUser.uid,

                    "approvedAt" to
                            FieldValue.serverTimestamp()
                )


            firestore
                .collection("pengajuan")
                .document(documentId)
                .update(data)
                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}