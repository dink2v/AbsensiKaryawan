package com.example.absensikaryawan.data

import com.example.absensikaryawan.models.Notification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object PengajuanRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ============================================================
    // SIMPAN PENGAJUAN
    // ============================================================

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

            val currentUser = auth.currentUser
                ?: return Result.failure(
                    Exception("User belum login.")
                )

            val uid = currentUser.uid

            val userDocument = db.collection("users")
                .document(uid)
                .get()
                .await()

            if (!userDocument.exists()) {
                return Result.failure(
                    Exception("Data pengguna tidak ditemukan.")
                )
            }

            val nama = userDocument
                .getString("nama")
                .orEmpty()

            val jabatan = userDocument
                .getString("jabatan")
                ?.trim()
                ?.uppercase()
                .orEmpty()

            val approvalChain =
                buildApprovalChain(userDocument)

            if (approvalChain.isEmpty()) {
                return Result.failure(
                    Exception(
                        "Jalur approval tidak ditemukan. Pastikan field 'atasan' pada data pengguna sudah benar."
                    )
                )
            }

            val approvalStatuses =
                mutableMapOf<String, Any>()

            approvalChain.forEach { person ->
                approvalStatuses[person.uid] = "menunggu"
            }

            val firstApprover =
                approvalChain.first()

            val documentReference =
                db.collection("pengajuan")
                    .document()

            val data = hashMapOf<String, Any>(

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

                "catatanAdmin" to "",

                "timestamp" to FieldValue.serverTimestamp(),

                "approvalChain" to approvalChain.map { person ->
                    mapOf(
                        "uid" to person.uid,
                        "nama" to person.nama,
                        "jabatan" to person.jabatan,
                        "urutan" to person.urutan
                    )
                },

                "approvalStatuses" to approvalStatuses,

                "currentApproverUid" to firstApprover.uid,

                "currentApproverName" to firstApprover.nama,

                "currentApproverJabatan" to firstApprover.jabatan,

                "approvalLocked" to false,

                "approvedBy" to "",

                "approvalNote" to "",

                "createdByJabatan" to jabatan
            )

            documentReference
                .set(data)
                .await()

            val documentId =
                documentReference.id

            // Notifikasi semua orang dalam jalur approval
            notifyApprovalChain(
                approvalChain = approvalChain,
                requesterName = nama,
                jenis = jenis,
                documentId = documentId
            )

            // Notifikasi admin
            notifyAdminsPengajuanBaru(
                requesterName = nama,
                jenis = jenis,
                documentId = documentId
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // ============================================================
    // NOTIFIKASI APPROVAL CHAIN
    // ============================================================

    private suspend fun notifyApprovalChain(
        approvalChain: List<ApprovalPerson>,
        requesterName: String,
        jenis: String,
        documentId: String
    ) {

        approvalChain.forEachIndexed { index, person ->

            val isFirstApprover =
                index == 0

            val title =
                if (isFirstApprover) {
                    "Pengajuan Baru"
                } else {
                    "Informasi Pengajuan"
                }

            val message =
                if (isFirstApprover) {

                    "$requesterName mengajukan $jenis. Pengajuan menunggu persetujuan Anda."

                } else {

                    "$requesterName mengajukan $jenis. Anda termasuk dalam jalur approval pengajuan ini."
                }

            createNotification(
                userId = person.uid,
                type = "PENGAJUAN_APPROVAL",
                title = title,
                message = message,
                relatedId = documentId
            )
        }
    }

    // ============================================================
    // NOTIFIKASI ADMIN
    // ============================================================

    private suspend fun notifyAdminsPengajuanBaru(
        requesterName: String,
        jenis: String,
        documentId: String
    ) {

        val snapshot = db.collection("users")
            .whereEqualTo("isAdmin", true)
            .get()
            .await()

        snapshot.documents.forEach { document ->

            createNotification(
                userId = document.id,
                type = "PENGAJUAN_BARU",
                title = "Pengajuan Baru",
                message = "$requesterName mengajukan $jenis.",
                relatedId = documentId
            )
        }
    }

    // ============================================================
    // BUILD APPROVAL CHAIN
    //
    // Staff
    //   ↓
    // Supervisor
    //   ↓
    // Manager
    //   ↓
    // HRD
    //   ↓
    // Owner
    // ============================================================

    private suspend fun buildApprovalChain(
        userDocument: DocumentSnapshot
    ): List<ApprovalPerson> {

        val result =
            mutableListOf<ApprovalPerson>()

        var currentDocument =
            userDocument

        val visitedNames =
            mutableSetOf<String>()

        var urutan = 1

        while (true) {

            val atasanName =
                currentDocument
                    .getString("atasan")
                    ?.trim()
                    .orEmpty()

            if (atasanName.isBlank()) {
                break
            }

            val normalizedAtasan =
                atasanName.lowercase()

            if (!visitedNames.add(normalizedAtasan)) {
                break
            }

            val snapshot =
                db.collection("users")
                    .whereEqualTo("nama", atasanName)
                    .limit(1)
                    .get()
                    .await()

            if (snapshot.isEmpty) {
                break
            }

            val approverDocument =
                snapshot.documents.first()

            val isAdmin =
                approverDocument
                    .getBoolean("isAdmin")
                    ?: false

            if (isAdmin) {
                break
            }

            val approverUid =
                approverDocument.id

            val approverName =
                approverDocument
                    .getString("nama")
                    ?.trim()
                    .orEmpty()

            val approverJabatan =
                approverDocument
                    .getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    .orEmpty()

            if (
                approverUid.isBlank() ||
                approverName.isBlank()
            ) {
                break
            }

            result.add(
                ApprovalPerson(
                    uid = approverUid,
                    nama = approverName,
                    jabatan = approverJabatan,
                    urutan = urutan
                )
            )

            if (approverJabatan == "OWNER") {
                break
            }

            currentDocument =
                approverDocument

            urutan++
        }

        return result
    }

    // ============================================================
    // AMBIL PENGAJUAN SAYA
    // ============================================================

    suspend fun ambilPengajuanSaya():
            List<Map<String, Any>> {

        return try {

            prosesOtomatisH1()

            val uid =
                auth.currentUser?.uid
                    ?: return emptyList()

            val snapshot =
                db.collection("pengajuan")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()

            snapshot.documents
                .map { document ->

                    val data =
                        document.data
                            ?.toMutableMap()
                            ?: mutableMapOf()

                    data["documentId"] =
                        document.id

                    dataMapNormalize(data)
                }
                .sortedByDescending {

                    it["timestamp"] as? Long
                        ?: 0L
                }

        } catch (_: Exception) {

            emptyList()
        }
    }

    // ============================================================
    // AMBIL SEMUA PENGAJUAN
    // ============================================================

    suspend fun ambilSemuaPengajuan():
            List<Map<String, Any>> {

        return try {

            prosesOtomatisH1()

            val snapshot =
                db.collection("pengajuan")
                    .get()
                    .await()

            snapshot.documents.map { document ->

                val data =
                    document.data
                        ?.toMutableMap()
                        ?: mutableMapOf()

                data["documentId"] =
                    document.id

                dataMapNormalize(data)
            }

        } catch (_: Exception) {

            emptyList()
        }
    }

    // ============================================================
    // PENGAJUAN UNTUK APPROVAL
    // ============================================================

    suspend fun ambilPengajuanUntukApproval():
            List<Map<String, Any>> {

        return try {

            prosesOtomatisH1()

            val uid =
                auth.currentUser?.uid
                    ?: return emptyList()

            val snapshot =
                db.collection("pengajuan")
                    .whereEqualTo(
                        "currentApproverUid",
                        uid
                    )
                    .whereEqualTo(
                        "approvalLocked",
                        false
                    )
                    .get()
                    .await()

            snapshot.documents.map { document ->

                val data =
                    document.data
                        ?.toMutableMap()
                        ?: mutableMapOf()

                data["documentId"] =
                    document.id

                dataMapNormalize(data)
            }

        } catch (_: Exception) {

            emptyList()
        }
    }

    // ============================================================
    // OTOMATIS H-1
    // ============================================================

    private suspend fun prosesOtomatisH1() {

        try {

            val besok =
                LocalDate.now(
                    ZoneId.of("Asia/Jakarta")
                ).plusDays(1)

            val formatter =
                DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
                )

            val tanggalBesok =
                besok.format(formatter)

            val snapshot =
                db.collection("pengajuan")
                    .whereEqualTo(
                        "tanggalMulai",
                        tanggalBesok
                    )
                    .get()
                    .await()

            snapshot.documents.forEach { document ->

                prosesH1SatuPengajuan(
                    document
                )
            }

        } catch (_: Exception) {
            // Jangan mengganggu halaman utama
        }
    }

    // ============================================================
    // PROSES H-1 SATU PENGAJUAN
    // ============================================================

    private suspend fun prosesH1SatuPengajuan(
        document: DocumentSnapshot
    ) {

        val data =
            document.data
                ?: return

        val status =
            data["status"]
                ?.toString()
                ?.lowercase()
                .orEmpty()

        val approvalLocked =
            when (
                val value =
                    data["approvalLocked"]
            ) {

                is Boolean -> value

                is String ->
                    value.equals(
                        "true",
                        ignoreCase = true
                    )

                else -> false
            }

        if (status != "menunggu") {
            return
        }

        if (approvalLocked) {
            return
        }

        val approvalChain =
            data["approvalChain"]
                    as? List<*>
                ?: emptyList<Any>()

        val approvalStatuses =
            mutableMapOf<String, Any>()

        approvalChain.forEach { item ->

            val map =
                item as? Map<*, *>
                    ?: return@forEach

            val uid =
                map["uid"]
                    ?.toString()
                    .orEmpty()

            if (uid.isNotBlank()) {
                approvalStatuses[uid] =
                    "disetujui"
            }
        }

        document.reference
            .update(
                mapOf(
                    "status" to "disetujui",
                    "approvalLocked" to true,
                    "approvalStatuses" to approvalStatuses,
                    "currentApproverUid" to "",
                    "currentApproverName" to "",
                    "currentApproverJabatan" to "",
                    "approvedBy" to "SYSTEM_H1",
                    "approvalNote" to
                            "Otomatis disetujui H-1 karena Owner belum memberikan keputusan."
                )
            )
            .await()

        val requesterUid =
            data["uid"]
                ?.toString()
                .orEmpty()

        val jenis =
            data["jenis"]
                ?.toString()
                .orEmpty()

        if (requesterUid.isNotBlank()) {

            createNotification(
                userId = requesterUid,
                type = "PENGAJUAN_DISETUJUI_H1",
                title = "Pengajuan Disetujui",
                message =
                    "Pengajuan $jenis otomatis disetujui H-1 karena belum ada keputusan Owner.",
                relatedId = document.id
            )
        }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

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

            val uid =
                currentUser.uid

            val reference =
                db.collection("pengajuan")
                    .document(documentId)

            val document =
                reference.get().await()

            if (!document.exists()) {
                return Result.failure(
                    Exception(
                        "Pengajuan tidak ditemukan."
                    )
                )
            }

            val data =
                document.data
                    ?: return Result.failure(
                        Exception(
                            "Data pengajuan kosong."
                        )
                    )

            val approvalLocked =
                when (
                    val value =
                        data["approvalLocked"]
                ) {

                    is Boolean -> value

                    is String ->
                        value.equals(
                            "true",
                            ignoreCase = true
                        )

                    else -> false
                }

            if (approvalLocked) {

                return Result.failure(
                    Exception(
                        "Pengajuan sudah dikunci oleh Owner."
                    )
                )
            }

            val currentApproverUid =
                data["currentApproverUid"]
                    ?.toString()
                    .orEmpty()

            if (currentApproverUid != uid) {

                return Result.failure(
                    Exception(
                        "Anda bukan approver pada tahap ini."
                    )
                )
            }

            val currentApproverJabatan =
                data["currentApproverJabatan"]
                    ?.toString()
                    ?.uppercase()
                    .orEmpty()

            val approvalStatuses =
                mutableMapOf<String, Any>()

            val oldStatuses =
                data["approvalStatuses"]
                        as? Map<*, *>

            oldStatuses?.forEach { (key, value) ->

                val keyString =
                    key?.toString()
                        .orEmpty()

                if (keyString.isNotBlank()) {

                    approvalStatuses[keyString] =
                        value?.toString()
                            ?: "menunggu"
                }
            }

            approvalStatuses[uid] =
                status

            val approvalChain =
                data["approvalChain"]
                        as? List<*>
                    ?: emptyList<Any>()

            val currentIndex =
                approvalChain.indexOfFirst { item ->

                    val map =
                        item as? Map<*, *>
                            ?: return@indexOfFirst false

                    map["uid"]?.toString() == uid
                }

            // ====================================================
            // OWNER
            // ====================================================

            if (
                currentApproverJabatan ==
                "OWNER"
            ) {

                val finalStatus =
                    if (
                        status.equals(
                            "disetujui",
                            ignoreCase = true
                        )
                    ) {
                        "disetujui"
                    } else {
                        "ditolak"
                    }

                reference.update(
                    mapOf(
                        "status" to finalStatus,
                        "approvalStatuses" to
                                approvalStatuses,
                        "approvalLocked" to true,
                        "currentApproverUid" to "",
                        "currentApproverName" to "",
                        "currentApproverJabatan" to "",
                        "approvedBy" to
                                currentUser.email.orEmpty(),
                        "approvalNote" to
                                "Keputusan final oleh Owner."
                    )
                ).await()

                createResultNotification(
                    requesterUid =
                        data["uid"]
                            ?.toString()
                            .orEmpty(),
                    jenis =
                        data["jenis"]
                            ?.toString()
                            .orEmpty(),
                    status = finalStatus,
                    documentId = documentId
                )

                return Result.success(Unit)
            }

            // ====================================================
            // APPROVER BERIKUTNYA
            // ====================================================

            val nextApprover =
                if (
                    currentIndex >= 0 &&
                    currentIndex + 1 <
                    approvalChain.size
                ) {

                    val map =
                        approvalChain[
                            currentIndex + 1
                        ] as? Map<*, *>

                    if (map != null) {

                        ApprovalPerson(
                            uid =
                                map["uid"]
                                    ?.toString()
                                    .orEmpty(),

                            nama =
                                map["nama"]
                                    ?.toString()
                                    .orEmpty(),

                            jabatan =
                                map["jabatan"]
                                    ?.toString()
                                    ?.uppercase()
                                    .orEmpty(),

                            urutan =
                                when (
                                    val value =
                                        map["urutan"]
                                ) {

                                    is Number ->
                                        value.toInt()

                                    is String ->
                                        value.toIntOrNull()
                                            ?: currentIndex + 2

                                    else ->
                                        currentIndex + 2
                                }
                        )

                    } else {
                        null
                    }

                } else {
                    null
                }

            if (nextApprover == null) {

                return Result.failure(
                    Exception(
                        "Approver berikutnya tidak ditemukan."
                    )
                )
            }

            reference.update(
                mapOf(
                    "approvalStatuses" to
                            approvalStatuses,

                    "currentApproverUid" to
                            nextApprover.uid,

                    "currentApproverName" to
                            nextApprover.nama,

                    "currentApproverJabatan" to
                            nextApprover.jabatan,

                    "approvalLocked" to false,

                    "status" to "menunggu"
                )
            ).await()

            createNextApproverNotification(
                approver = nextApprover,
                requesterName =
                    data["nama"]
                        ?.toString()
                        .orEmpty(),
                jenis =
                    data["jenis"]
                        ?.toString()
                        .orEmpty(),
                documentId = documentId
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // ============================================================
    // NOTIFIKASI HASIL FINAL
    // ============================================================

    private suspend fun createResultNotification(
        requesterUid: String,
        jenis: String,
        status: String,
        documentId: String
    ) {

        if (requesterUid.isBlank()) {
            return
        }

        val approved =
            status.equals(
                "disetujui",
                ignoreCase = true
            )

        createNotification(
            userId = requesterUid,

            type =
                if (approved) {
                    "PENGAJUAN_DISETUJUI"
                } else {
                    "PENGAJUAN_DITOLAK"
                },

            title =
                if (approved) {
                    "Pengajuan Disetujui"
                } else {
                    "Pengajuan Ditolak"
                },

            message =
                if (approved) {
                    "Pengajuan $jenis telah disetujui Owner."
                } else {
                    "Pengajuan $jenis telah ditolak Owner."
                },

            relatedId = documentId
        )
    }

    // ============================================================
    // NOTIFIKASI APPROVER BERIKUTNYA
    // ============================================================

    private suspend fun createNextApproverNotification(
        approver: ApprovalPerson,
        requesterName: String,
        jenis: String,
        documentId: String
    ) {

        createNotification(
            userId = approver.uid,
            type = "PENGAJUAN_APPROVAL",
            title = "Menunggu Persetujuan",
            message =
                "$requesterName mengajukan $jenis. Sekarang menunggu persetujuan Anda.",
            relatedId = documentId
        )
    }

    // ============================================================
    // CREATE NOTIFICATION
    // ============================================================

    private suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        relatedId: String
    ) {

        if (userId.isBlank()) {
            return
        }

        val reference =
            db.collection("notifications")
                .document()

        val notification =
            Notification(
                id = reference.id,
                userId = userId,
                type = type,
                title = title,
                message = message,
                timestamp =
                    System.currentTimeMillis(),
                isRead = false,
                relatedId = relatedId
            )

        reference
            .set(notification)
            .await()
    }

    // ============================================================
    // NORMALIZE DATA
    // ============================================================

    private fun dataMapNormalize(
        data: MutableMap<String, Any>
    ): Map<String, Any> {

        val timestamp =
            data["timestamp"]

        if (timestamp is Timestamp) {

            data["timestamp"] =
                timestamp.toDate().time
        }

        return data
    }

    // ============================================================
    // APPROVAL PERSON
    // ============================================================

    private data class ApprovalPerson(
        val uid: String,
        val nama: String,
        val jabatan: String,
        val urutan: Int
    )
}