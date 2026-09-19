package com.example.absensikaryawan.data

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

            val userDocument = db
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (!userDocument.exists()) {
                return Result.failure(
                    Exception("Data user tidak ditemukan.")
                )
            }

            val nama = userDocument
                .getString("nama")
                .orEmpty()
                .trim()

            val jabatan = userDocument
                .getString("jabatan")
                .orEmpty()
                .trim()
                .uppercase()

            val atasan = userDocument
                .getString("atasan")
                .orEmpty()
                .trim()

            val owner = userDocument
                .getString("owner")
                .orEmpty()
                .trim()

            // ====================================================
            // BUILD APPROVAL CHAIN
            // ====================================================

            val approvalChain =
                buildApprovalChain(userDocument)

            if (
                jabatan != "OWNER" &&
                approvalChain.isEmpty()
            ) {
                return Result.failure(
                    Exception(
                        "Jalur approval tidak ditemukan. " +
                                "Periksa field atasan pada data user."
                    )
                )
            }

            val approvalStatuses =
                mutableMapOf<String, String>()

            approvalChain.forEach { person ->
                approvalStatuses[person.uid] = "menunggu"
            }

            val firstApprover =
                approvalChain.firstOrNull()

            val currentApproverUid =
                firstApprover?.uid.orEmpty()

            val currentApproverName =
                firstApprover?.nama.orEmpty()

            val currentApproverJabatan =
                firstApprover?.jabatan.orEmpty()

            val approvalChainData =
                approvalChain.map { person ->
                    mapOf(
                        "uid" to person.uid,
                        "nama" to person.nama,
                        "jabatan" to person.jabatan,
                        "urutan" to person.urutan
                    )
                }

            val dataPengajuan =
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
                    "approvalChain" to approvalChainData,
                    "approvalStatuses" to approvalStatuses,
                    "currentApproverUid" to currentApproverUid,
                    "currentApproverName" to currentApproverName,
                    "currentApproverJabatan" to currentApproverJabatan,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "approvedAt" to "",
                    "approvedBy" to "",
                    "catatanAdmin" to ""
                )

            val documentReference =
                db
                    .collection("pengajuan")
                    .add(dataPengajuan)
                    .await()

            // ====================================================
            // NOTIFIKASI KE SELURUH APPROVAL CHAIN
            // ====================================================

            approvalChain.forEach { approver ->

                val isCurrentApprover =
                    approver.uid == firstApprover?.uid

                val message =
                    if (isCurrentApprover) {

                        "$nama mengajukan $jenis. " +
                                "Menunggu persetujuan Anda sebagai " +
                                "${approver.jabatan}."

                    } else {

                        "$nama mengajukan $jenis. " +
                                "Anda termasuk dalam jalur approval sebagai " +
                                "${approver.jabatan}. " +
                                "Pengajuan akan diteruskan kepada Anda " +
                                "sesuai urutan approval."
                    }

                createNotification(
                    targetUid =
                        approver.uid,

                    title =
                        "Pengajuan Baru",

                    message =
                        message,

                    type =
                        "PENGAJUAN_BARU",

                    pengajuanId =
                        documentReference.id
                )
            }

            // ====================================================
            // NOTIFIKASI KE ADMIN
            //
            // Admin bukan bagian approval chain.
            // Semua user dengan isAdmin=true mendapatkan
            // informasi bahwa ada pengajuan baru.
            // ====================================================

            notifyAdminsPengajuanBaru(
                namaPengaju =
                    nama,

                jenis =
                    jenis,

                pengajuanId =
                    documentReference.id
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // ============================================================
    // NOTIFIKASI ADMIN - PENGAJUAN BARU
    // ============================================================

    private suspend fun notifyAdminsPengajuanBaru(
        namaPengaju: String,
        jenis: String,
        pengajuanId: String
    ) {

        try {

            val adminSnapshot =
                db
                    .collection("users")
                    .whereEqualTo(
                        "isAdmin",
                        true
                    )
                    .get()
                    .await()

            adminSnapshot.documents.forEach { adminDocument ->

                val adminUid =
                    adminDocument.id

                if (adminUid.isBlank()) {
                    return@forEach
                }

                createNotification(
                    targetUid =
                        adminUid,

                    title =
                        "Pengajuan Baru",

                    message =
                        "$namaPengaju mengajukan $jenis. " +
                                "Terdapat pengajuan baru pada sistem.",

                    type =
                        "PENGAJUAN_BARU",

                    pengajuanId =
                        pengajuanId
                )
            }

        } catch (_: Exception) {

            // Kegagalan notifikasi Admin tidak membatalkan
            // proses penyimpanan pengajuan.
        }
    }

    // ============================================================
    // BUILD APPROVAL CHAIN
    // ============================================================

    private suspend fun buildApprovalChain(
        userDocument: DocumentSnapshot
    ): List<ApprovalPerson> {

        val result =
            mutableListOf<ApprovalPerson>()

        var currentSuperiorName =
            userDocument
                .getString("atasan")
                .orEmpty()
                .trim()

        val visitedNames =
            mutableSetOf<String>()

        val visitedUids =
            mutableSetOf<String>()

        var urutan = 1

        while (currentSuperiorName.isNotBlank()) {

            val normalizedName =
                currentSuperiorName
                    .trim()
                    .uppercase()

            if (!visitedNames.add(normalizedName)) {
                break
            }

            val snapshot =
                db
                    .collection("users")
                    .whereEqualTo(
                        "nama",
                        currentSuperiorName
                    )
                    .limit(1)
                    .get()
                    .await()

            val superiorDocument =
                snapshot.documents.firstOrNull()
                    ?: break

            val superiorUid =
                superiorDocument.id

            if (!visitedUids.add(superiorUid)) {
                break
            }

            val superiorName =
                superiorDocument
                    .getString("nama")
                    .orEmpty()
                    .trim()

            val superiorJabatan =
                superiorDocument
                    .getString("jabatan")
                    .orEmpty()
                    .trim()
                    .uppercase()

            val superiorAtasan =
                superiorDocument
                    .getString("atasan")
                    .orEmpty()
                    .trim()

            val superiorIsAdmin =
                superiorDocument
                    .getBoolean("isAdmin") == true

            if (superiorIsAdmin) {

                currentSuperiorName =
                    superiorAtasan

                continue
            }

            result.add(
                ApprovalPerson(
                    uid =
                        superiorUid,

                    nama =
                        superiorName,

                    jabatan =
                        superiorJabatan,

                    urutan =
                        urutan
                )
            )

            urutan++

            if (superiorJabatan == "OWNER") {
                break
            }

            currentSuperiorName =
                superiorAtasan
        }

        return result
    }

    // ============================================================
    // PENGAJUAN SAYA
    // ============================================================

    suspend fun ambilPengajuanSaya():
            List<Map<String, Any>> {

        return try {

            prosesOtomatisH1()

            val uid =
                auth.currentUser?.uid
                    ?: return emptyList()

            val snapshot =
                db
                    .collection("pengajuan")
                    .whereEqualTo(
                        "uid",
                        uid
                    )
                    .get()
                    .await()

            snapshot.documents.map { document ->

                val data =
                    document.data?.toMutableMap()
                        ?: mutableMapOf()

                data["documentId"] =
                    document.id

                dataMapNormalize(data)
            }

        } catch (e: Exception) {

            emptyList()
        }
    }

    // ============================================================
    // SEMUA PENGAJUAN
    // ============================================================

    suspend fun ambilSemuaPengajuan():
            List<Map<String, Any>> {

        return try {

            prosesOtomatisH1()

            val snapshot =
                db
                    .collection("pengajuan")
                    .get()
                    .await()

            snapshot.documents.map { document ->

                val data =
                    document.data?.toMutableMap()
                        ?: mutableMapOf()

                data["documentId"] =
                    document.id

                dataMapNormalize(data)
            }

        } catch (e: Exception) {

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
                db
                    .collection("pengajuan")
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

            snapshot.documents.mapNotNull { document ->

                val data =
                    document.data?.toMutableMap()
                        ?: return@mapNotNull null

                val status =
                    data["status"]
                        ?.toString()
                        ?.trim()
                        ?.lowercase()
                        .orEmpty()

                val locked =
                    data["approvalLocked"]
                            as? Boolean
                        ?: false

                if (
                    status != "menunggu" ||
                    locked
                ) {
                    return@mapNotNull null
                }

                data["documentId"] =
                    document.id

                dataMapNormalize(data)
            }

        } catch (e: Exception) {

            emptyList()
        }
    }

    // ============================================================
    // H-1 OTOMATIS DISETUJUI
    // ============================================================

    private suspend fun prosesOtomatisH1() {

        try {

            val zonaIndonesia =
                ZoneId.of("Asia/Jakarta")

            val hariIni =
                LocalDate.now(zonaIndonesia)

            val tanggalH1 =
                hariIni.plusDays(1)

            val formatter =
                DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
                )

            val tanggalTarget =
                tanggalH1.format(formatter)

            val snapshot =
                db
                    .collection("pengajuan")
                    .whereEqualTo(
                        "status",
                        "menunggu"
                    )
                    .whereEqualTo(
                        "approvalLocked",
                        false
                    )
                    .get()
                    .await()

            for (document in snapshot.documents) {

                val tanggalMulai =
                    document
                        .getString("tanggalMulai")
                        .orEmpty()
                        .trim()

                if (
                    tanggalMulai != tanggalTarget
                ) {
                    continue
                }

                prosesH1SatuPengajuan(
                    document
                )
            }

        } catch (_: Exception) {

            // Jangan sampai proses H-1
            // mengganggu fungsi utama aplikasi.
        }
    }

    // ============================================================
    // PROSES SATU PENGAJUAN H-1
    // ============================================================

    private suspend fun prosesH1SatuPengajuan(
        document: DocumentSnapshot
    ) {

        val documentId =
            document.id

        val reference =
            db
                .collection("pengajuan")
                .document(documentId)

        val latestDocument =
            reference
                .get()
                .await()

        if (!latestDocument.exists()) {
            return
        }

        val latestStatus =
            latestDocument
                .getString("status")
                .orEmpty()
                .trim()
                .lowercase()

        val latestLocked =
            latestDocument
                .getBoolean("approvalLocked")
                ?: false

        if (
            latestStatus != "menunggu" ||
            latestLocked
        ) {
            return
        }

        val pengajuUid =
            latestDocument
                .getString("uid")
                .orEmpty()

        val namaPengaju =
            latestDocument
                .getString("nama")
                .orEmpty()

        val jenis =
            latestDocument
                .getString("jenis")
                .orEmpty()

        val approvalStatusesRaw =
            latestDocument
                .get("approvalStatuses")

        val approvalStatuses =
            mutableMapOf<String, String>()

        if (
            approvalStatusesRaw
                    is Map<*, *>
        ) {

            approvalStatusesRaw.forEach { (key, value) ->

                if (key != null) {

                    approvalStatuses[
                        key.toString()
                    ] =
                        value
                            ?.toString()
                            .orEmpty()
                }
            }
        }

        approvalStatuses.keys.forEach { uid ->

            approvalStatuses[uid] =
                "disetujui"
        }

        val updateData =
            hashMapOf<String, Any>(

                "status" to
                        "disetujui",

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
                        "SYSTEM_H1",

                "approvedAt" to
                        FieldValue.serverTimestamp(),

                "catatanAdmin" to
                        "Otomatis disetujui pada H-1 karena " +
                        "belum mendapat keputusan Owner."
            )

        reference
            .update(updateData)
            .await()

        createNotification(
            targetUid =
                pengajuUid,

            title =
                "Pengajuan Disetujui",

            message =
                "Pengajuan $jenis milik $namaPengaju " +
                        "otomatis disetujui pada H-1 karena " +
                        "belum mendapat keputusan Owner.",

            type =
                "PENGAJUAN_DISETUJUI_H1",

            pengajuanId =
                documentId
        )
    }

    // ============================================================
    // UPDATE STATUS APPROVAL
    //
    // ATURAN:
    //
    // Supervisor → Manager → HRD → Owner
    //
    // Jika Supervisor/Manager/HRD MENOLAK:
    // - status mereka dicatat "ditolak"
    // - TIDAK mengunci pengajuan
    // - pengajuan tetap lanjut ke approver berikutnya
    //
    // Jika Owner MENOLAK:
    // - status final "ditolak"
    // - approvalLocked = true
    //
    // Jika Owner MENYETUJUI:
    // - status final "disetujui"
    // - approvalLocked = true
    // ============================================================

    suspend fun updateStatusPengajuan(
        documentId: String,
        status: String
    ): Result<Unit> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "User belum login."
                        )
                    )

            val currentUid =
                currentUser.uid

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
                        "Status approval tidak valid."
                    )
                )
            }

            val pengajuanReference =
                db
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

            val approvalLocked =
                pengajuanDocument
                    .getBoolean(
                        "approvalLocked"
                    )
                    ?: false

            if (approvalLocked) {
                return Result.failure(
                    Exception(
                        "Pengajuan sudah dikunci dan tidak dapat diproses lagi."
                    )
                )
            }

            val currentApproverUid =
                pengajuanDocument
                    .getString(
                        "currentApproverUid"
                    )
                    .orEmpty()

            if (
                currentApproverUid !=
                currentUid
            ) {
                return Result.failure(
                    Exception(
                        "Anda bukan approver pada tahap pengajuan ini."
                    )
                )
            }

            val pengajuUid =
                pengajuanDocument
                    .getString("uid")
                    .orEmpty()

            val namaPengaju =
                pengajuanDocument
                    .getString("nama")
                    .orEmpty()

            val jenis =
                pengajuanDocument
                    .getString("jenis")
                    .orEmpty()

            val currentApproverJabatan =
                pengajuanDocument
                    .getString(
                        "currentApproverJabatan"
                    )
                    .orEmpty()
                    .trim()
                    .uppercase()

            val approvalStatusesRaw =
                pengajuanDocument
                    .get("approvalStatuses")

            val approvalStatuses =
                mutableMapOf<String, String>()

            if (
                approvalStatusesRaw
                        is Map<*, *>
            ) {

                approvalStatusesRaw.forEach { (key, value) ->

                    if (key != null) {

                        approvalStatuses[
                            key.toString()
                        ] =
                            value
                                ?.toString()
                                .orEmpty()
                    }
                }
            }

            // ====================================================
            // CATAT KEPUTUSAN APPROVER SAAT INI
            // ====================================================

            approvalStatuses[currentUid] =
                statusNormal

            // ====================================================
            // OWNER = FINAL
            //
            // Hanya Owner yang mengunci pengajuan.
            // ====================================================

            if (
                currentApproverJabatan ==
                "OWNER"
            ) {

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
                                currentUid,

                        "approvedAt" to
                                FieldValue.serverTimestamp()
                    )

                pengajuanReference
                    .update(updateData)
                    .await()

                createResultNotification(
                    targetUid =
                        pengajuUid,

                    namaPengaju =
                        namaPengaju,

                    jenis =
                        jenis,

                    status =
                        statusNormal,

                    pengajuanId =
                        documentId
                )

                return Result.success(Unit)
            }

            // ====================================================
            // CARI APPROVER BERIKUTNYA
            // ====================================================

            val approvalChainRaw =
                pengajuanDocument
                    .get("approvalChain")

            val approvalChain =
                mutableListOf<ApprovalPerson>()

            if (
                approvalChainRaw
                        is List<*>
            ) {

                approvalChainRaw.forEach { item ->

                    if (item is Map<*, *>) {

                        val uid =
                            item["uid"]
                                ?.toString()
                                .orEmpty()

                        val nama =
                            item["nama"]
                                ?.toString()
                                .orEmpty()

                        val jabatan =
                            item["jabatan"]
                                ?.toString()
                                .orEmpty()
                                .trim()
                                .uppercase()

                        val urutan =
                            when (
                                val value =
                                    item["urutan"]
                            ) {

                                is Long ->
                                    value.toInt()

                                is Int ->
                                    value

                                is Double ->
                                    value.toInt()

                                is String ->
                                    value.toIntOrNull()
                                        ?: 0

                                else ->
                                    0
                            }

                        if (
                            uid.isNotBlank()
                        ) {

                            approvalChain.add(
                                ApprovalPerson(
                                    uid =
                                        uid,

                                    nama =
                                        nama,

                                    jabatan =
                                        jabatan,

                                    urutan =
                                        urutan
                                )
                            )
                        }
                    }
                }
            }

            val currentIndex =
                approvalChain
                    .indexOfFirst {
                        it.uid == currentUid
                    }

            if (
                currentIndex == -1
            ) {

                return Result.failure(
                    Exception(
                        "Data approver tidak ditemukan di approval chain."
                    )
                )
            }

            val nextApprover =
                approvalChain
                    .getOrNull(
                        currentIndex + 1
                    )

            if (
                nextApprover == null
            ) {

                return Result.failure(
                    Exception(
                        "Approver berikutnya tidak ditemukan. " +
                                "Periksa struktur atasan sampai Owner."
                    )
                )
            }

            // ====================================================
            // INTERMEDIATE APPROVER
            //
            // Baik SETUJUI maupun TOLAK:
            // tetap lanjut ke tahap berikutnya.
            // ====================================================

            val updateData =
                hashMapOf<String, Any>(

                    "status" to
                            "menunggu",

                    "approvalStatuses" to
                            approvalStatuses,

                    "approvalLocked" to
                            false,

                    "currentApproverUid" to
                            nextApprover.uid,

                    "currentApproverName" to
                            nextApprover.nama,

                    "currentApproverJabatan" to
                            nextApprover.jabatan
                )

            pengajuanReference
                .update(updateData)
                .await()

            createNextApproverNotification(
                targetUid =
                    nextApprover.uid,

                namaPengaju =
                    namaPengaju,

                jenis =
                    jenis,

                jabatanApprover =
                    nextApprover.jabatan,

                pengajuanId =
                    documentId
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
        targetUid: String,
        namaPengaju: String,
        jenis: String,
        status: String,
        pengajuanId: String
    ) {

        if (targetUid.isBlank()) {
            return
        }

        val disetujui =
            status == "disetujui"

        val title =
            if (disetujui) {
                "Pengajuan Disetujui"
            } else {
                "Pengajuan Ditolak"
            }

        val message =
            if (disetujui) {

                "Pengajuan $jenis milik $namaPengaju " +
                        "telah disetujui Owner."

            } else {

                "Pengajuan $jenis milik $namaPengaju " +
                        "telah ditolak oleh Owner."
            }

        createNotification(
            targetUid =
                targetUid,

            title =
                title,

            message =
                message,

            type =
                if (disetujui) {
                    "PENGAJUAN_DISETUJUI"
                } else {
                    "PENGAJUAN_DITOLAK"
                },

            pengajuanId =
                pengajuanId
        )
    }

    // ============================================================
    // NOTIFIKASI APPROVER BERIKUTNYA
    // ============================================================

    private suspend fun createNextApproverNotification(
        targetUid: String,
        namaPengaju: String,
        jenis: String,
        jabatanApprover: String,
        pengajuanId: String
    ) {

        if (targetUid.isBlank()) {
            return
        }

        createNotification(
            targetUid =
                targetUid,

            title =
                "Menunggu Approval",

            message =
                "Pengajuan $jenis dari $namaPengaju " +
                        "menunggu persetujuan Anda sebagai " +
                        "$jabatanApprover.",

            type =
                "PENGAJUAN_APPROVAL",

            pengajuanId =
                pengajuanId
        )
    }

    // ============================================================
    // CREATE NOTIFICATION
    // ============================================================

    private suspend fun createNotification(
        targetUid: String,
        title: String,
        message: String,
        type: String,
        pengajuanId: String
    ) {

        if (targetUid.isBlank()) {
            return
        }

        val data =
            hashMapOf<String, Any>(

                "userId" to
                        targetUid,

                "title" to
                        title,

                "message" to
                        message,

                "type" to
                        type,

                "relatedId" to
                        pengajuanId,

                "isRead" to
                        false,

                "timestamp" to
                        FieldValue.serverTimestamp()
            )

        db
            .collection("notifications")
            .add(data)
            .await()
    }

    // ============================================================
    // NORMALIZE DATA
    // ============================================================

    private fun dataMapNormalize(
        data: MutableMap<String, Any>
    ): Map<String, Any> {

        return data
    }

    // ============================================================
    // MODEL APPROVAL
    // ============================================================

    private data class ApprovalPerson(
        val uid: String,
        val nama: String,
        val jabatan: String,
        val urutan: Int
    )
}