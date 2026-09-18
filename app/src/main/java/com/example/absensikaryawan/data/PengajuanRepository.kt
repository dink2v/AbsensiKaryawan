package com.example.absensikaryawan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
            // DATA USER PENGAJU
            // ==================================================

            val userDocument =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

            if (!userDocument.exists()) {

                return Result.failure(
                    Exception(
                        "Data user tidak ditemukan."
                    )
                )
            }


            val nama =
                userDocument
                    .getString("nama")
                    ?.trim()
                    ?.takeIf {
                        it.isNotEmpty()
                    }
                    ?: currentUser.displayName
                    ?: "Karyawan"


            val jabatan =
                userDocument
                    .getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: ""


            val atasan =
                userDocument
                    .getString("atasan")
                    ?.trim()
                    ?: ""


            val owner =
                userDocument
                    .getString("owner")
                    ?.trim()
                    ?: ""


            // ==================================================
            // BENTUK JALUR APPROVAL
            //
            // Pengaju TIDAK masuk sebagai approver.
            //
            // Staff:
            // Supervisor -> Manager -> HRD -> Owner
            //
            // Supervisor:
            // Manager -> HRD -> Owner
            //
            // Manager:
            // HRD -> Owner
            //
            // HRD:
            // Owner
            //
            // Owner:
            // Tidak mempunyai approver berikutnya.
            // ==================================================

            val approvalChain =
                buildApprovalChain(
                    userDocument
                )


            // ==================================================
            // CEK JALUR APPROVAL
            // ==================================================

            val jabatanNormal =
                jabatan.uppercase()

            if (
                !jabatanNormal.equals(
                    "OWNER",
                    ignoreCase = true
                ) &&
                approvalChain.isEmpty()
            ) {

                return Result.failure(
                    Exception(
                        "Jalur approval tidak ditemukan. Periksa field atasan pada data user."
                    )
                )
            }


            // ==================================================
            // STATUS AWAL APPROVAL
            // ==================================================

            val approvalStatuses =
                linkedMapOf<String, String>()

            approvalChain.forEach { approver ->

                approvalStatuses[
                    approver.uid
                ] = "menunggu"
            }


            // ==================================================
            // APPROVER PERTAMA
            // ==================================================

            val firstApprover =
                approvalChain.firstOrNull()


            val currentApproverUid =
                firstApprover
                    ?.uid
                    ?: ""


            val currentApproverName =
                firstApprover
                    ?.nama
                    ?: ""


            val currentApproverJabatan =
                firstApprover
                    ?.jabatan
                    ?: ""


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

                    "jabatanPengaju" to jabatan,

                    "atasanPengaju" to atasan,

                    "ownerPengaju" to owner,

                    "status" to "menunggu",

                    "approvalLocked" to false,

                    "approvalChain" to
                            approvalChain.map { approver ->

                                hashMapOf(
                                    "uid" to approver.uid,
                                    "nama" to approver.nama,
                                    "jabatan" to approver.jabatan,
                                    "urutan" to approver.urutan
                                )
                            },

                    "approvalStatuses" to
                            approvalStatuses,

                    "currentApproverUid" to
                            currentApproverUid,

                    "currentApproverName" to
                            currentApproverName,

                    "currentApproverJabatan" to
                            currentApproverJabatan,

                    "createdAt" to
                            FieldValue.serverTimestamp(),

                    "approvedAt" to "",

                    "approvedBy" to "",

                    "catatanAdmin" to ""
                )


            // ==================================================
            // SIMPAN KE FIRESTORE
            // ==================================================

            val pengajuanReference =
                firestore
                    .collection("pengajuan")
                    .add(data)
                    .await()


            val pengajuanId =
                pengajuanReference.id


            // ==================================================
            // NOTIFIKASI SEMUA APPROVER
            //
            // Semua orang dalam jalur langsung mendapatkan
            // notifikasi saat pengajuan dibuat.
            //
            // Tetapi yang boleh melakukan approval tetap hanya
            // currentApproverUid.
            // ==================================================

            if (approvalChain.isNotEmpty()) {

                val batch =
                    firestore.batch()


                approvalChain.forEach { approver ->

                    val notificationReference =
                        firestore
                            .collection("notifications")
                            .document()


                    val notificationData =
                        hashMapOf<String, Any>(

                            "id" to
                                    notificationReference.id,

                            "userId" to
                                    approver.uid,

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
                                    "PENGAJUAN_BARU",

                            "namaPengaju" to
                                    nama,

                            "jabatanPengaju" to
                                    jabatan,

                            "jabatanApprover" to
                                    approver.jabatan
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

            Result.failure(
                Exception(
                    e.message
                        ?: "Gagal menyimpan pengajuan."
                )
            )
        }
    }


    // ==========================================================
    // BENTUK JALUR APPROVAL
    // ==========================================================

    private suspend fun buildApprovalChain(
        userDocument: DocumentSnapshot
    ): List<ApprovalPerson> {

        val result =
            mutableListOf<ApprovalPerson>()


        var currentSuperiorName =
            userDocument
                .getString("atasan")
                ?.trim()
                .orEmpty()


        val visitedNames =
            mutableSetOf<String>()


        val visitedUids =
            mutableSetOf<String>()


        var urutan =
            1


        while (
            currentSuperiorName.isNotBlank() &&
            !visitedNames.contains(
                currentSuperiorName.lowercase()
            )
        ) {

            visitedNames.add(
                currentSuperiorName.lowercase()
            )


            // ==================================================
            // CARI USER BERDASARKAN NAMA ATASAN
            // ==================================================

            val superiorSnapshot =
                firestore
                    .collection("users")
                    .whereEqualTo(
                        "nama",
                        currentSuperiorName
                    )
                    .limit(1)
                    .get()
                    .await()


            val superiorDocument =
                superiorSnapshot
                    .documents
                    .firstOrNull()
                    ?: break


            val superiorUid =
                superiorDocument.id


            // ==================================================
            // PROTEKSI LOOP UID
            // ==================================================

            if (
                visitedUids.contains(
                    superiorUid
                )
            ) {
                break
            }


            visitedUids.add(
                superiorUid
            )


            val superiorNama =
                superiorDocument
                    .getString("nama")
                    ?.trim()
                    ?: currentSuperiorName


            val superiorJabatan =
                superiorDocument
                    .getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: ""


            // ==================================================
            // ADMIN TIDAK MASUK HIERARCHY
            // ==================================================

            val isAdmin =
                superiorDocument
                    .getBoolean("isAdmin")
                    ?: false


            if (isAdmin) {

                currentSuperiorName =
                    superiorDocument
                        .getString("atasan")
                        ?.trim()
                        .orEmpty()

                continue
            }


            // ==================================================
            // TAMBAHKAN APPROVER
            // ==================================================

            result.add(
                ApprovalPerson(
                    uid = superiorUid,
                    nama = superiorNama,
                    jabatan = superiorJabatan,
                    urutan = urutan
                )
            )


            urutan++


            // ==================================================
            // OWNER ADALAH LEVEL PALING ATAS
            // ==================================================

            if (
                superiorJabatan.equals(
                    "OWNER",
                    ignoreCase = true
                )
            ) {
                break
            }


            // ==================================================
            // LANJUT KE ATASAN BERIKUTNYA
            // ==================================================

            currentSuperiorName =
                superiorDocument
                    .getString("atasan")
                    ?.trim()
                    .orEmpty()
        }


        return result
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


                    dataMapNormalize(
                        item
                    )
                }


            Result.success(
                data
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL SEMUA PENGAJUAN
    //
    // Tetap dipertahankan karena masih digunakan oleh screen
    // lama dan kebutuhan lain.
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


                    dataMapNormalize(
                        item
                    )
                }


            Result.success(
                data
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN YANG MENJADI TANGGUNG JAWAB SAYA
    //
    // Ini yang nantinya dipakai ApprovalScreen.
    //
    // Hanya pengajuan dengan:
    //
    // currentApproverUid == UID user login
    //
    // yang dianggap menjadi tugas user tersebut.
    // ==========================================================

    suspend fun ambilPengajuanUntukApproval():
            Result<List<Map<String, Any>>> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User belum login.")
                    )


            val currentUid =
                currentUser.uid


            val snapshot =
                firestore
                    .collection("pengajuan")
                    .whereEqualTo(
                        "currentApproverUid",
                        currentUid
                    )
                    .whereEqualTo(
                        "approvalLocked",
                        false
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


                    dataMapNormalize(
                        item
                    )
                }


            Result.success(
                data
            )

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "Gagal mengambil pengajuan approval."
                )
            )
        }
    }


    // ==========================================================
    // APPROVE / TOLAK PENGAJUAN
    // ==========================================================

    suspend fun updateStatusPengajuan(
        documentId: String,
        status: String
    ): Result<Unit> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User belum login.")
                    )


            if (documentId.isBlank()) {

                return Result.failure(
                    Exception(
                        "ID pengajuan tidak ditemukan."
                    )
                )
            }


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
            // REFERENSI PENGAJUAN
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
            // CEK LOCK
            // ==================================================

            val approvalLocked =
                pengajuanDocument
                    .getBoolean(
                        "approvalLocked"
                    )
                    ?: false


            if (approvalLocked) {

                return Result.failure(
                    Exception(
                        "Pengajuan ini sudah final dan terkunci."
                    )
                )
            }


            // ==================================================
            // UID PENGAJU
            // ==================================================

            val pengajuUid =
                pengajuanDocument
                    .getString("uid")


            if (pengajuUid.isNullOrBlank()) {

                return Result.failure(
                    Exception(
                        "UID pengaju tidak ditemukan."
                    )
                )
            }


            // ==================================================
            // CEK CURRENT APPROVER
            // ==================================================

            val currentApproverUid =
                pengajuanDocument
                    .getString(
                        "currentApproverUid"
                    )
                    .orEmpty()


            if (currentApproverUid.isBlank()) {

                return Result.failure(
                    Exception(
                        "Tidak ada approver aktif pada pengajuan ini."
                    )
                )
            }


            if (
                currentApproverUid !=
                currentUser.uid
            ) {

                return Result.failure(
                    Exception(
                        "Kamu belum menjadi approver pada tahap ini."
                    )
                )
            }


            // ==================================================
            // DATA PENGAJUAN
            // ==================================================

            val namaPengaju =
                pengajuanDocument
                    .getString("nama")
                    ?: "Karyawan"


            val jenis =
                pengajuanDocument
                    .getString("jenis")
                    ?: "pengajuan"


            val currentApproverJabatan =
                pengajuanDocument
                    .getString(
                        "currentApproverJabatan"
                    )
                    ?.trim()
                    ?.uppercase()
                    .orEmpty()


            // ==================================================
            // AMBIL STATUS APPROVAL
            // ==================================================

            val approvalStatuses =
                (
                        pengajuanDocument
                            .get("approvalStatuses")
                                as? Map<*, *>
                        )
                    ?.mapNotNull { entry ->

                        val key =
                            entry.key
                                ?.toString()


                        val value =
                            entry.value
                                ?.toString()


                        if (
                            key != null &&
                            value != null
                        ) {

                            key to value

                        } else {

                            null
                        }
                    }
                    ?.toMap()
                    ?.toMutableMap()
                    ?: mutableMapOf()


            // ==================================================
            // OWNER = FINAL DECISION
            // ==================================================

            val isOwner =
                currentApproverJabatan
                    .equals(
                        "OWNER",
                        ignoreCase = true
                    )


            if (isOwner) {

                // ==============================================
                // SEMUA STATUS MENGIKUTI KEPUTUSAN OWNER
                // ==============================================

                approvalStatuses.keys.forEach { uid ->

                    approvalStatuses[uid] =
                        statusNormal
                }


                val updateData =
                    hashMapOf<String, Any>(

                        "status" to
                                statusNormal,

                        "approvalStatuses" to
                                approvalStatuses,

                        "approvalLocked" to
                                true,

                        "currentApproverUid" to
                                "",

                        "currentApproverName" to
                                "",

                        "currentApproverJabatan" to
                                "",

                        "approvedBy" to
                                currentUser.uid,

                        "approvedAt" to
                                FieldValue.serverTimestamp()
                    )


                pengajuanReference
                    .update(
                        updateData
                    )
                    .await()


                // ==============================================
                // NOTIFIKASI HASIL KE PENGAJU
                // ==============================================

                createResultNotification(
                    staffUid =
                        pengajuUid,

                    documentId =
                        documentId,

                    namaPengaju =
                        namaPengaju,

                    jenis =
                        jenis,

                    status =
                        statusNormal
                )


                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // APPROVAL NON-OWNER
            // ==================================================

            approvalStatuses[
                currentUser.uid
            ] =
                statusNormal


            // ==================================================
            // TOLAK SEBELUM OWNER
            //
            // Untuk menjaga sistem tidak menggantung, apabila
            // approver aktif menolak, pengajuan menjadi ditolak
            // dan terkunci.
            //
            // Keputusan Owner tetap menjadi keputusan final
            // untuk jalur yang berhasil mencapai Owner.
            // ==================================================

            if (
                statusNormal ==
                "ditolak"
            ) {

                approvalStatuses.keys.forEach { uid ->

                    approvalStatuses[uid] =
                        "ditolak"
                }


                val updateData =
                    hashMapOf<String, Any>(

                        "status" to
                                "ditolak",

                        "approvalStatuses" to
                                approvalStatuses,

                        "approvalLocked" to
                                true,

                        "currentApproverUid" to
                                "",

                        "currentApproverName" to
                                "",

                        "currentApproverJabatan" to
                                "",

                        "approvedBy" to
                                currentUser.uid,

                        "approvedAt" to
                                FieldValue.serverTimestamp()
                    )


                pengajuanReference
                    .update(
                        updateData
                    )
                    .await()


                createResultNotification(
                    staffUid =
                        pengajuUid,

                    documentId =
                        documentId,

                    namaPengaju =
                        namaPengaju,

                    jenis =
                        jenis,

                    status =
                        "ditolak"
                )


                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // AMBIL APPROVAL CHAIN
            // ==================================================

            val approvalChain =
                (
                        pengajuanDocument
                            .get("approvalChain")
                                as? List<*>
                        )
                    ?: emptyList<Any>()


            // ==================================================
            // CARI POSISI APPROVER SEKARANG
            // ==================================================

            val currentIndex =
                approvalChain.indexOfFirst { item ->

                    val map =
                        item as? Map<*, *>


                    map?.get("uid")
                        ?.toString() ==
                            currentUser.uid
                }


            if (currentIndex < 0) {

                return Result.failure(
                    Exception(
                        "Approver tidak ditemukan dalam jalur approval."
                    )
                )
            }


            // ==================================================
            // APPROVER BERIKUTNYA
            // ==================================================

            val nextApprover =
                if (
                    currentIndex + 1 <
                    approvalChain.size
                ) {

                    approvalChain[
                        currentIndex + 1
                    ] as? Map<*, *>

                } else {

                    null
                }


            // ==================================================
            // MASIH ADA APPROVER
            // ==================================================

            if (
                nextApprover != null
            ) {

                val nextUid =
                    nextApprover["uid"]
                        ?.toString()
                        .orEmpty()


                val nextNama =
                    nextApprover["nama"]
                        ?.toString()
                        .orEmpty()


                val nextJabatan =
                    nextApprover["jabatan"]
                        ?.toString()
                        ?.uppercase()
                        .orEmpty()


                if (nextUid.isBlank()) {

                    return Result.failure(
                        Exception(
                            "UID approver berikutnya tidak ditemukan."
                        )
                    )
                }


                val updateData =
                    hashMapOf<String, Any>(

                        "approvalStatuses" to
                                approvalStatuses,

                        "currentApproverUid" to
                                nextUid,

                        "currentApproverName" to
                                nextNama,

                        "currentApproverJabatan" to
                                nextJabatan
                    )


                pengajuanReference
                    .update(
                        updateData
                    )
                    .await()


                // ==============================================
                // NOTIFIKASI TAHAP BERIKUTNYA
                // ==============================================

                createNextApproverNotification(
                    approverUid =
                        nextUid,

                    documentId =
                        documentId,

                    namaPengaju =
                        namaPengaju,

                    jenis =
                        jenis,

                    jabatanApprover =
                        nextJabatan
                )


                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // FALLBACK
            // ==================================================

            Result.failure(
                Exception(
                    "Tidak ditemukan approver berikutnya."
                )
            )

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
    // NOTIFIKASI HASIL KE PENGAJU
    // ==========================================================

    private suspend fun createResultNotification(
        staffUid: String,
        documentId: String,
        namaPengaju: String,
        jenis: String,
        status: String
    ) {

        try {

            val notificationReference =
                firestore
                    .collection("notifications")
                    .document()


            val title: String
            val message: String
            val target: String


            if (
                status ==
                "disetujui"
            ) {

                title =
                    "Pengajuan Disetujui"


                message =
                    "Pengajuan $jenis kamu telah disetujui."


                target =
                    "PENGAJUAN_DISETUJUI"

            } else {

                title =
                    "Pengajuan Ditolak"


                message =
                    "Pengajuan $jenis kamu telah ditolak."


                target =
                    "PENGAJUAN_DITOLAK"
            }


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

                    "namaPengaju" to
                            namaPengaju
                )


            notificationReference
                .set(
                    notificationData
                )
                .await()

        } catch (_: Exception) {

            // Notifikasi gagal tidak membatalkan
            // keputusan approval.
        }
    }


    // ==========================================================
    // NOTIFIKASI APPROVER BERIKUTNYA
    // ==========================================================

    private suspend fun createNextApproverNotification(
        approverUid: String,
        documentId: String,
        namaPengaju: String,
        jenis: String,
        jabatanApprover: String
    ) {

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
                            approverUid,

                    "type" to
                            "PENGAJUAN",

                    "title" to
                            "Pengajuan Menunggu Persetujuan",

                    "message" to
                            "$namaPengaju mengajukan $jenis dan menunggu persetujuan $jabatanApprover.",

                    "timestamp" to
                            FieldValue.serverTimestamp(),

                    "isRead" to
                            false,

                    "relatedId" to
                            documentId,

                    "target" to
                            "PENGAJUAN_APPROVAL",

                    "namaPengaju" to
                            namaPengaju,

                    "jabatanApprover" to
                            jabatanApprover
                )


            notificationReference
                .set(
                    notificationData
                )
                .await()

        } catch (_: Exception) {

            // Notifikasi gagal tidak membatalkan
            // proses approval.
        }
    }


    // ==========================================================
    // MODEL APPROVER INTERNAL
    // ==========================================================

    private data class ApprovalPerson(

        val uid: String,

        val nama: String,

        val jabatan: String,

        val urutan: Int
    )


    // ==========================================================
    // NORMALISASI DATA
    // ==========================================================

    private fun dataMapNormalize(
        item: HashMap<String, Any>
    ): HashMap<String, Any> {

        return item
    }
}