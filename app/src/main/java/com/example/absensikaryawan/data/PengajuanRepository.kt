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

            val pengajuanId =
                pengajuanReference.id

            // ==================================================
            // NOTIFIKASI ADMIN
            // ==================================================

            val adminSnapshot =
                firestore
                    .collection("users")
                    .whereEqualTo("isAdmin", true)
                    .get()
                    .await()

            if (!adminSnapshot.isEmpty) {

                val batch =
                    firestore.batch()

                adminSnapshot.documents.forEach { adminDocument ->

                    val adminUid =
                        adminDocument.id

                    val notificationReference =
                        firestore
                            .collection("notifications")
                            .document()

                    val notificationData =
                        hashMapOf<String, Any>(

                            "id" to
                                    notificationReference.id,

                            "userId" to
                                    adminUid,

                            "type" to
                                    "PENGAJUAN",

                            "title" to
                                    "Pengajuan Baru",

                            "message" to
                                    "$nama mengajukan $jenis dan menunggu persetujuan.",

                            "timestamp" to
                                    FieldValue.serverTimestamp(),

                            "isRead" to
                                    false,

                            "relatedId" to
                                    pengajuanId,

                            "target" to
                                    "PENGAJUAN_BARU"
                        )

                    batch.set(
                        notificationReference,
                        notificationData
                    )
                }

                batch.commit().await()
            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // ==========================================================
    // AMBIL PENGAJUAN SAYA
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
    // AMBIL SEMUA PENGAJUAN
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

                    dataMapNormalize(item)
                }

            Result.success(data)

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

            // ==================================================
            // CEK ADMIN
            // ==================================================

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("Admin belum login.")
                    )

            // ==================================================
            // VALIDASI DOCUMENT ID
            // ==================================================

            if (documentId.isBlank()) {

                return Result.failure(
                    Exception(
                        "ID pengajuan tidak ditemukan."
                    )
                )
            }

            // ==================================================
            // NORMALISASI STATUS
            // ==================================================

            val statusNormal =
                status
                    .trim()
                    .lowercase()

            if (
                statusNormal != "disetujui" &&
                statusNormal != "ditolak"
            ) {

                return Result.failure(
                    Exception(
                        "Status pengajuan tidak valid."
                    )
                )
            }

            // ==================================================
            // AMBIL DATA PENGAJUAN
            // ==================================================

            val pengajuanReference =
                firestore
                    .collection("pengajuan")
                    .document(documentId)

            val pengajuanDocument =
                pengajuanReference
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
            // CEK STATUS SAAT INI
            // ==================================================

            val statusSaatIni =
                pengajuanDocument
                    .getString("status")
                    ?.trim()
                    ?.lowercase()
                    ?: "menunggu"

            if (statusSaatIni != "menunggu") {

                return Result.failure(
                    Exception(
                        "Pengajuan ini sudah diproses sebelumnya."
                    )
                )
            }

            // ==================================================
            // UID STAFF
            // ==================================================

            val staffUid =
                pengajuanDocument
                    .getString("uid")

            if (staffUid.isNullOrBlank()) {

                return Result.failure(
                    Exception(
                        "UID karyawan pada pengajuan tidak ditemukan."
                    )
                )
            }

            // ==================================================
            // DATA STAFF
            // ==================================================

            val namaStaff =
                pengajuanDocument
                    .getString("nama")
                    ?: "Karyawan"

            val jenis =
                pengajuanDocument
                    .getString("jenis")
                    ?: "pengajuan"

            // ==================================================
            // UPDATE PENGAJUAN
            // ==================================================

            val updateData =
                hashMapOf<String, Any>(

                    "status" to statusNormal,

                    "approvedBy" to
                            currentUser.uid,

                    "approvedAt" to
                            FieldValue.serverTimestamp()
                )

            pengajuanReference
                .update(updateData)
                .await()

            // ==================================================
            // SIAPKAN NOTIFIKASI STAFF
            // ==================================================

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

                else -> {

                    title =
                        "Pengajuan Ditolak"

                    message =
                        "Pengajuan $jenis kamu telah ditolak oleh admin."

                    target =
                        "PENGAJUAN_DITOLAK"
                }
            }

            // ==================================================
            // SIMPAN NOTIFIKASI STAFF
            //
            // Jika notifikasi gagal, status pengajuan TETAP
            // sudah berhasil berubah.
            // ==================================================

            try {

                val notificationReference =
                    firestore
                        .collection("notifications")
                        .document()

                val notificationData =
                    hashMapOf<String, Any>(

                        "id" to
                                notificationReference.id,

                        "userId" to
                                staffUid,

                        "type" to
                                "PENGAJUAN",

                        "title" to
                                title,

                        "message" to
                                message,

                        "timestamp" to
                                FieldValue.serverTimestamp(),

                        "isRead" to
                                false,

                        "relatedId" to
                                documentId,

                        "target" to
                                target,

                        "namaStaff" to
                                namaStaff
                    )

                notificationReference
                    .set(notificationData)
                    .await()

            } catch (_: Exception) {

                // ==================================================
                // NOTIFIKASI GAGAL TIDAK MEMBATALKAN UPDATE STATUS
                // ==================================================
            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "Gagal memperbarui status pengajuan."
                )
            )
        }
    }

    // ==========================================================
    // NORMALISASI DATA
    // ==========================================================

    private fun dataMapNormalize(
        item: HashMap<String, Any>
    ): HashMap<String, Any> {

        return item
    }
}