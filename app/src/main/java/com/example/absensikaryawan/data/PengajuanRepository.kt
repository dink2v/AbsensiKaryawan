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
            // SIMPAN PENGAJUAN
            // ==================================================

            val pengajuanReference =
                firestore
                    .collection("pengajuan")
                    .add(data)
                    .await()


            // ==================================================
            // NOTIFIKASI PENGAJUAN MENUNGGU
            // ==================================================

            val notificationData =
                hashMapOf<String, Any>(

                    "userId" to uid,

                    "title" to
                            "Pengajuan Menunggu",

                    "message" to
                            "Pengajuan $jenis kamu sedang menunggu persetujuan admin.",

                    "type" to
                            "PENGAJUAN",

                    "target" to
                            "PENGAJUAN_MENUNGGU",

                    "read" to
                            false,

                    "timestamp" to
                            FieldValue.serverTimestamp(),

                    "pengajuanId" to
                            pengajuanReference.id
                )


            firestore
                .collection("notifications")
                .add(notificationData)
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


            // ==================================================
            // AMBIL DATA PENGAJUAN
            // ==================================================

            val pengajuanDocument =
                firestore
                    .collection("pengajuan")
                    .document(documentId)
                    .get()
                    .await()


            if (!pengajuanDocument.exists()) {

                return Result.failure(
                    Exception(
                        "Data pengajuan tidak ditemukan."
                    )
                )
            }


            // ==================================================
            // AMBIL UID STAFF
            // ==================================================

            val staffUid =
                pengajuanDocument.getString("uid")


            if (staffUid.isNullOrBlank()) {

                return Result.failure(
                    Exception(
                        "UID karyawan pada pengajuan tidak ditemukan."
                    )
                )
            }


            // ==================================================
            // AMBIL JENIS PENGAJUAN
            // ==================================================

            val jenis =
                pengajuanDocument.getString("jenis")
                    ?: "pengajuan"


            // ==================================================
            // UPDATE STATUS
            // ==================================================

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


            // ==================================================
            // SIAPKAN NOTIFIKASI
            // ==================================================

            val statusNormal =
                status
                    .trim()
                    .lowercase()


            val title: String
            val message: String
            val target: String


            when (statusNormal) {

                "disetujui" -> {

                    title =
                        "Pengajuan Disetujui"

                    message =
                        "Pengajuan $jenis kamu telah disetujui oleh admin."

                    target =
                        "PENGAJUAN_DISETUJUI"
                }


                "ditolak" -> {

                    title =
                        "Pengajuan Ditolak"

                    message =
                        "Pengajuan $jenis kamu telah ditolak oleh admin."

                    target =
                        "PENGAJUAN_DITOLAK"
                }


                else -> {

                    title =
                        "Status Pengajuan Diperbarui"

                    message =
                        "Status pengajuan $jenis kamu telah diperbarui."

                    target =
                        "PENGAJUAN_MENUNGGU"
                }
            }


            // ==================================================
            // SIMPAN NOTIFIKASI
            // ==================================================

            val notificationData =
                hashMapOf<String, Any>(

                    "userId" to staffUid,

                    "title" to title,

                    "message" to message,

                    "type" to
                            "PENGAJUAN",

                    "target" to target,

                    "read" to
                            false,

                    "timestamp" to
                            FieldValue.serverTimestamp(),

                    "pengajuanId" to
                            documentId
                )


            firestore
                .collection("notifications")
                .add(notificationData)
                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}
