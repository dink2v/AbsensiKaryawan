package com.example.absensikaryawan.repository

import android.util.Log

import com.example.absensikaryawan.data.PengajuanData
import com.example.absensikaryawan.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ==========================================================
// PENGAJUAN REPOSITORY
// ==========================================================

object PengajuanRepository {

    private val db get() = FirebaseFirestore.getInstance()

    private val auth get() = FirebaseAuth.getInstance()

    private val pengajuanCollection
        get() = db.collection("pengajuan")

    private val usersCollection
        get() = db.collection("users")

    private val notificationRepository =
        NotificationRepository()


    // ==========================================================
    // AMBIL DOKUMEN USER YANG SEDANG LOGIN
    // ==========================================================

    private suspend fun getCurrentUserDocument(): DocumentSnapshot? {

        val firebaseUser =
            auth.currentUser
                ?: return null

        val firebaseUid =
            firebaseUser.uid

        // ------------------------------------------------------
        // PRIORITAS 1
        // Cari berdasarkan field uid
        // ------------------------------------------------------

        val byUid =
            usersCollection
                .whereEqualTo("uid", firebaseUid)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

        // ------------------------------------------------------
        // PRIORITAS 2
        // Cari berdasarkan email
        // ------------------------------------------------------

        val email =
            firebaseUser.email

        if (!email.isNullOrBlank()) {

            val byEmail =
                usersCollection
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .await()

            if (!byEmail.isEmpty) {
                return byEmail.documents.first()
            }
        }

        // ------------------------------------------------------
        // PRIORITAS 3
        // Document ID = Firebase UID
        // ------------------------------------------------------

        val byDocumentId =
            usersCollection
                .document(firebaseUid)
                .get()
                .await()

        return if (byDocumentId.exists()) {
            byDocumentId
        } else {
            null
        }
    }


    // ==========================================================
    // AMBIL DATA USER BERDASARKAN UID
    // ==========================================================

    private suspend fun getUserDocumentByUid(
        uid: String
    ): DocumentSnapshot? {

        if (uid.isBlank()) {
            return null
        }

        // ------------------------------------------------------
        // Cari berdasarkan field uid
        // ------------------------------------------------------

        val byUid =
            usersCollection
                .whereEqualTo("uid", uid)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

        // ------------------------------------------------------
        // Fallback document ID
        // ------------------------------------------------------

        val byDocumentId =
            usersCollection
                .document(uid)
                .get()
                .await()

        return if (byDocumentId.exists()) {
            byDocumentId
        } else {
            null
        }
    }


    // ==========================================================
    // AMBIL UID FIREBASE DARI DOKUMEN USER
    // ==========================================================

    private fun getFirebaseUid(
        document: DocumentSnapshot
    ): String {

        val uidField =
            document
                .getString("uid")
                ?.trim()

        val firebaseUid =
            uidField
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: document.id.trim()

        Log.d(
            "PengajuanRepository",
            "Resolve user: documentId=${document.id}, uidField=$uidField, firebaseUid=$firebaseUid"
        )

        return firebaseUid
    }


    // ==========================================================
    // CARI USER BERDASARKAN IDENTITAS
    // Bisa berupa:
    // - Firebase UID
    // - Document ID
    // - Email
    // - Nama
    // ==========================================================

    private suspend fun findUserByIdentity(
        identity: String
    ): DocumentSnapshot? {

        val value =
            identity.trim()

        if (value.isBlank()) {
            return null
        }

        // ------------------------------------------------------
        // 1. Coba berdasarkan field uid
        // ------------------------------------------------------

        val byUid =
            usersCollection
                .whereEqualTo("uid", value)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

        // ------------------------------------------------------
        // 2. Coba berdasarkan document ID
        // ------------------------------------------------------

        val byDocumentId =
            usersCollection
                .document(value)
                .get()
                .await()

        if (byDocumentId.exists()) {
            return byDocumentId
        }

        // ------------------------------------------------------
        // 3. Coba berdasarkan email
        // ------------------------------------------------------

        val byEmail =
            usersCollection
                .whereEqualTo("email", value)
                .limit(1)
                .get()
                .await()

        if (!byEmail.isEmpty) {
            return byEmail.documents.first()
        }

        // ------------------------------------------------------
        // 4. Coba berdasarkan nama
        // ------------------------------------------------------

        return findUserByName(value)
    }


    // ==========================================================
    // CARI USER BERDASARKAN NAMA
    // ==========================================================

    private suspend fun findUserByName(
        nama: String
    ): DocumentSnapshot? {

        val targetName =
            nama.trim()

        if (targetName.isBlank()) {
            return null
        }

        // ------------------------------------------------------
        // Prioritas 1: exact match Firestore
        // ------------------------------------------------------

        val snapshot =
            usersCollection
                .whereEqualTo("nama", targetName)
                .limit(10)
                .get()
                .await()

        if (!snapshot.isEmpty) {

            val selected =
                snapshot.documents.firstOrNull {
                    it.getString("nama")
                        ?.trim()
                        ?.equals(
                            targetName,
                            ignoreCase = true
                        ) == true
                }

            if (selected != null) {
                return selected
            }

            return snapshot.documents.first()
        }

        // ------------------------------------------------------
        // Fallback:
        // pencarian case-insensitive di client
        // ------------------------------------------------------

        val allUsers =
            usersCollection
                .get()
                .await()

        return allUsers.documents.firstOrNull {
            it.getString("nama")
                ?.trim()
                ?.equals(
                    targetName,
                    ignoreCase = true
                ) == true
        }
    }


    // ==========================================================
    // CARI USER BERDASARKAN JABATAN
    // ==========================================================

    private suspend fun findUserByJabatan(
        jabatan: String
    ): DocumentSnapshot? {

        if (jabatan.isBlank()) {
            return null
        }

        val normalizedJabatan =
            jabatan
                .trim()
                .uppercase()

        val snapshot =
            usersCollection
                .get()
                .await()

        return snapshot.documents.firstOrNull { userDocument ->

            userDocument
                .getString("jabatan")
                ?.trim()
                ?.uppercase() == normalizedJabatan
        }
    }


    // ==========================================================
    // AMBIL STATUS APPROVAL
    // ==========================================================

    private fun getApprovalStatuses(
        document: DocumentSnapshot
    ): Map<String, String> {

        return (document.get("approvalStatuses") as? Map<*, *>)
            ?.mapNotNull { (key, value) ->

                if (
                    key is String &&
                    value is String
                ) {
                    key to value
                } else {
                    null
                }
            }
            ?.toMap()
            ?: emptyMap()
    }


    // ==========================================================
    // KONVERSI FIRESTORE → PENGAJUAN DATA
    // ==========================================================

    private fun documentToPengajuan(
        document: DocumentSnapshot
    ): PengajuanData {

        val statuses =
            getApprovalStatuses(document)

        return PengajuanData(

            id =
                document.id,

            nama =
                document.getString("nama") ?: "",

            jenis =
                document.getString("jenis") ?: "",

            tanggal =
                document.getString("tanggal") ?: "",

            jamPulang =
                document.getString("jamPulang") ?: "",

            jamKeluar =
                document.getString("jamKeluar") ?: "",

            jamKembali =
                document.getString("jamKembali") ?: "",

            tanggalMulai =
                document.getString("tanggalMulai") ?: "",

            tanggalSelesai =
                document.getString("tanggalSelesai") ?: "",

            alasan =
                document.getString("alasan") ?: "",

            status =
                document.getString("status") ?: "menunggu",

            approvalChain =
                (document.get("approvalChain") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?: emptyList(),

            approvalStatuses =
                statuses,

            currentApproverUid =
                document.getString("currentApproverUid") ?: "",

            currentApproverName =
                document.getString("currentApproverName") ?: "",

            currentApproverJabatan =
                document.getString("currentApproverJabatan") ?: "",

            approvalLocked =
                document.getBoolean("approvalLocked") ?: false
        )
    }


    // ==========================================================
    // SIMPAN PENGAJUAN BARU
    // ==========================================================

    suspend fun simpanPengajuan(
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

            val firebaseUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "User belum login."
                        )
                    )

            val userDocument =
                getCurrentUserDocument()
                    ?: return Result.failure(
                        Exception(
                            "Data user tidak ditemukan."
                        )
                    )

            val uid =
                getFirebaseUid(
                    userDocument
                )

            val nama =
                userDocument
                    .getString("nama")
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: firebaseUser.email
                    ?: ""

            val jabatan =
                userDocument
                    .getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: "STAFF"

            val divisi =
                userDocument
                    .getString("divisi")
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
            // CARI SUPERVISOR
            // ==================================================

            val supervisorDocument =
                if (
                    jabatan == "STAFF" ||
                    jabatan == "KARYAWAN"
                ) {

                    if (atasan.isNotBlank()) {

                        Log.d(
                            "PengajuanRepository",
                            "Mencari atasan berdasarkan identity: $atasan"
                        )

                        findUserByIdentity(
                            atasan
                        )

                    } else {

                        findUserByJabatan(
                            "SUPERVISOR"
                        )
                    }

                } else {
                    null
                }

            // ==================================================
            // SUSUN JALUR APPROVAL
            // Supervisor → Manager → HRD → Owner
            // ==================================================

            val approvalDocuments =
                mutableListOf<DocumentSnapshot>()

            supervisorDocument?.let {

                approvalDocuments.add(
                    it
                )
            }

            listOf(
                "MANAGER",
                "HRD",
                "OWNER"
            ).forEach { role ->

                val candidate =
                    findUserByJabatan(
                        role
                    )

                if (candidate != null) {

                    val candidateUid =
                        getFirebaseUid(
                            candidate
                        )

                    if (
                        candidateUid.isNotBlank() &&
                        approvalDocuments.none {
                            getFirebaseUid(it) == candidateUid
                        }
                    ) {

                        approvalDocuments.add(
                            candidate
                        )
                    }
                }
            }

            // ==================================================
            // APPROVAL CHAIN
            // ==================================================

            val approvalChain =
                approvalDocuments.map {
                    getFirebaseUid(it)
                }.filter {
                    it.isNotBlank()
                }

            Log.i(
                "PengajuanRepository",
                "Jalur approval UID: $approvalChain"
            )

            val approvalStatuses =
                approvalChain
                    .mapIndexed { index, approverUid ->

                        approverUid to
                                if (
                                    index == 0
                                ) {
                                    "menunggu"
                                } else {
                                    "belum"
                                }
                    }
                    .toMap()

            val firstApprover =
                approvalDocuments.firstOrNull()

            val currentApproverUid =
                firstApprover
                    ?.let {
                        getFirebaseUid(it)
                    }
                    ?: ""

            val currentApproverName =
                firstApprover
                    ?.getString("nama")
                    ?.trim()
                    ?: ""

            val currentApproverJabatan =
                firstApprover
                    ?.getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: ""

            Log.i(
                "PengajuanRepository",
                "Approver aktif: UID=$currentApproverUid, Nama=$currentApproverName, Jabatan=$currentApproverJabatan"
            )

            // ==================================================
            // DATA FIRESTORE
            // ==================================================

            val data =
                hashMapOf<String, Any>(

                    // Data utama
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

                    // Status
                    "status" to "menunggu",

                    // Informasi user
                    "jabatanPengaju" to jabatan,
                    "divisiPengaju" to divisi,

                    // Approval
                    "currentApproverUid" to currentApproverUid,
                    "currentApproverName" to currentApproverName,
                    "currentApproverJabatan" to currentApproverJabatan,
                    "approvalChain" to approvalChain,
                    "approvalStatuses" to approvalStatuses,
                    "approvalLocked" to false,

                    // Informasi hirarki
                    "atasan" to atasan,
                    "owner" to owner,

                    // Waktu
                    "waktuPengajuan" to
                            com.google.firebase.Timestamp.now()
                )

            // ==================================================
            // SIMPAN PENGAJUAN
            // ==================================================

            val document =
                pengajuanCollection
                    .add(data)
                    .await()

            Log.i(
                "PengajuanRepository",
                "Pengajuan tersimpan: ${document.id}"
            )

            // ==================================================
            // NOTIFIKASI SEMUA APPROVER
            // ==================================================

            approvalDocuments.forEach { approverDocument ->

                val approverUid =
                    getFirebaseUid(
                        approverDocument
                    )

                val approverName =
                    approverDocument
                        .getString("nama")
                        ?.trim()
                        ?: ""

                val approverJabatan =
                    approverDocument
                        .getString("jabatan")
                        ?.trim()
                        ?.uppercase()
                        ?: ""

                Log.i(
                    "PengajuanRepository",
                    "Notifikasi approval → UID=$approverUid, Nama=$approverName, Jabatan=$approverJabatan"
                )

                if (
                    approverUid.isNotBlank()
                ) {

                    notificationRepository
                        .createNotification(

                            Notification(

                                userId =
                                    approverUid,

                                type =
                                    "PENGAJUAN_APPROVAL",

                                title =
                                    "Pengajuan Baru",

                                message =
                                    "$nama mengajukan $jenis dan membutuhkan persetujuan Anda.",

                                timestamp =
                                    System.currentTimeMillis(),

                                isRead =
                                    false,

                                relatedId =
                                    document.id
                            )
                        )
                }
            }

            // ==================================================
            // NOTIFIKASI ADMIN
            // ==================================================

            notifyAdmins(
                nama =
                    nama,

                jenis =
                    jenis,

                documentId =
                    document.id
            )

            Result.success(
                Unit
            )

        } catch (e: Exception) {

            Log.e(
                "PengajuanRepository",
                "Gagal menyimpan pengajuan",
                e
            )

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // NOTIFIKASI SEMUA ADMIN
    // ==========================================================

    private fun notifyAdmins(
        nama: String,
        jenis: String,
        documentId: String
    ) {

        usersCollection
            .whereEqualTo(
                "isAdmin",
                true
            )
            .get()
            .addOnSuccessListener { snapshot ->

                snapshot.documents.forEach { adminDocument ->

                    val adminUid =
                        getFirebaseUid(
                            adminDocument
                        )

                    if (
                        adminUid.isBlank()
                    ) {
                        return@forEach
                    }

                    val notification =
                        Notification(

                            userId =
                                adminUid,

                            type =
                                "PENGAJUAN_BARU",

                            title =
                                "Pengajuan Baru",

                            message =
                                "$nama mengajukan $jenis.",

                            timestamp =
                                System.currentTimeMillis(),

                            isRead =
                                false,

                            relatedId =
                                documentId
                        )

                    notificationRepository
                        .createNotification(
                            notification
                        )
                }
            }
    }


    // ==========================================================
    // AMBIL PENGAJUAN SAYA
    // ==========================================================

    suspend fun ambilPengajuanSaya():
            Result<List<PengajuanData>> {

        return try {

            val firebaseUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "User belum login."
                        )
                    )

            val uid =
                firebaseUser.uid

            val snapshot =
                pengajuanCollection
                    .whereEqualTo(
                        "uid",
                        uid
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map {
                        documentToPengajuan(it)
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN YANG MENUNGGU APPROVAL SAYA
    // ==========================================================

    suspend fun ambilPengajuanUntukApproval():
            Result<List<PengajuanData>> {

        return try {

            val firebaseUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "User belum login."
                        )
                    )

            val uid =
                firebaseUser.uid

            val snapshot =
                pengajuanCollection
                    .whereEqualTo(
                        "currentApproverUid",
                        uid
                    )
                    .whereEqualTo(
                        "status",
                        "menunggu"
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map {
                        documentToPengajuan(it)
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN BAWAHAN
    // ==========================================================

    suspend fun ambilPengajuanBawahan():
            Result<List<PengajuanData>> {

        return try {

            val userDocument =
                getCurrentUserDocument()
                    ?: return Result.failure(
                        Exception(
                            "Data user tidak ditemukan."
                        )
                    )

            val namaAtasan =
                userDocument
                    .getString("nama")
                    ?.trim()
                    ?: ""

            if (
                namaAtasan.isBlank()
            ) {

                return Result.success(
                    emptyList()
                )
            }

            val snapshot =
                pengajuanCollection
                    .whereEqualTo(
                        "atasan",
                        namaAtasan
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map {
                        documentToPengajuan(it)
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL SEMUA PENGAJUAN
    // ==========================================================

    suspend fun ambilSemuaPengajuan():
            Result<List<PengajuanData>> {

        return try {

            val snapshot =
                pengajuanCollection
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map {
                        documentToPengajuan(it)
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL DETAIL PENGAJUAN
    // ==========================================================

    suspend fun ambilDetailPengajuan(
        documentId: String
    ): Result<PengajuanData?> {

        return try {

            val document =
                pengajuanCollection
                    .document(documentId)
                    .get()
                    .await()

            if (
                !document.exists()
            ) {

                return Result.success(
                    null
                )
            }

            Result.success(
                documentToPengajuan(
                    document
                )
            )

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // APPROVAL PENGAJUAN
    // ==========================================================

    suspend fun updateStatusPengajuan(
        documentId: String,
        status: String
    ): Result<Unit> {

        return try {

            val firebaseUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "User belum login."
                        )
                    )

            val approverUid =
                firebaseUser.uid

            val document =
                pengajuanCollection
                    .document(documentId)
                    .get()
                    .await()

            if (
                !document.exists()
            ) {

                return Result.failure(
                    Exception(
                        "Pengajuan tidak ditemukan."
                    )
                )
            }

            val currentApproverUid =
                document
                    .getString(
                        "currentApproverUid"
                    )
                    ?: ""

            val approvalLocked =
                document
                    .getBoolean(
                        "approvalLocked"
                    )
                    ?: false

            val currentStatus =
                document
                    .getString(
                        "status"
                    )
                    ?: "menunggu"

            // ==================================================
            // VALIDASI APPROVER
            // ==================================================

            if (
                approvalLocked ||
                currentStatus != "menunggu" ||
                currentApproverUid.isBlank()
            ) {

                return Result.failure(
                    Exception(
                        "Pengajuan sudah terkunci atau tidak menunggu approval."
                    )
                )
            }

            if (
                currentApproverUid != approverUid
            ) {

                return Result.failure(
                    Exception(
                        "Anda bukan approver pengajuan ini."
                    )
                )
            }

            val namaPengaju =
                document
                    .getString("nama")
                    ?: ""

            val jenis =
                document
                    .getString("jenis")
                    ?: ""

            val requesterUid =
                document
                    .getString("uid")
                    ?: ""

            // ==================================================
            // JIKA DITOLAK
            // ==================================================

            if (
                status.equals(
                    "ditolak",
                    ignoreCase = true
                )
            ) {

                val updatedStatuses =
                    getApprovalStatuses(
                        document
                    ) +
                            (
                                    approverUid to
                                            "ditolak"
                                    )

                pengajuanCollection
                    .document(
                        documentId
                    )
                    .update(

                        mapOf(

                            "status" to
                                    "ditolak",

                            "approvalLocked" to
                                    true,

                            "approvalStatuses" to
                                    updatedStatuses,

                            "approvedByUid" to
                                    approverUid,

                            "approvedByName" to
                                    (
                                            firebaseUser.displayName
                                                ?: ""
                                            ),

                            "approvedAt" to
                                    com.google.firebase.Timestamp.now(),

                            "currentApproverUid" to
                                    "",

                            "currentApproverName" to
                                    "",

                            "currentApproverJabatan" to
                                    ""
                        )
                    )
                    .await()

                // ==================================================
                // NOTIFIKASI KE PENGAJU
                // ==================================================

                if (
                    requesterUid.isNotBlank()
                ) {

                    notificationRepository
                        .createNotification(

                            Notification(

                                userId =
                                    requesterUid,

                                type =
                                    "PENGAJUAN_DITOLAK",

                                title =
                                    "Pengajuan Ditolak",

                                message =
                                    "Pengajuan $jenis Anda ditolak.",

                                timestamp =
                                    System.currentTimeMillis(),

                                isRead =
                                    false,

                                relatedId =
                                    documentId
                            )
                        )
                }

                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // VALIDASI STATUS APPROVE
            // ==================================================

            if (
                !status.equals(
                    "disetujui",
                    ignoreCase = true
                )
            ) {

                return Result.failure(
                    Exception(
                        "Status approval tidak valid."
                    )
                )
            }


            // ==================================================
            // CARI USER YANG MENYETUJUI
            // ==================================================

            val approverDocument =
                getUserDocumentByUid(
                    approverUid
                )

            val approverName =
                approverDocument
                    ?.getString("nama")
                    ?.trim()
                    ?: firebaseUser.displayName
                    ?: ""

            val approverJabatan =
                approverDocument
                    ?.getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: ""


            // ==================================================
            // AMBIL APPROVAL CHAIN
            // ==================================================

            val approvalChain =
                (document.get("approvalChain") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?: emptyList()

            val currentChainIndex =
                approvalChain.indexOf(
                    approverUid
                )

            val nextRole =
                when (approverJabatan) {

                    "STAFF",
                    "KARYAWAN" ->
                        "SUPERVISOR"

                    "SUPERVISOR" ->
                        "MANAGER"

                    "MANAGER" ->
                        "HRD"

                    "HRD" ->
                        "OWNER"

                    else ->
                        ""
                }

            // ==================================================
            // CARI APPROVER BERIKUTNYA
            // ==================================================

            val nextApproverUid =

                if (
                    currentChainIndex >= 0
                ) {

                    approvalChain
                        .getOrNull(
                            currentChainIndex + 1
                        )
                        .orEmpty()

                } else {

                    findUserByJabatan(
                        nextRole
                    )
                        ?.let {
                            getFirebaseUid(it)
                        }
                        .orEmpty()
                }

            val nextApproverDocument =
                if (
                    nextApproverUid.isNotBlank()
                ) {

                    getUserDocumentByUid(
                        nextApproverUid
                    )

                } else {
                    null
                }

            val nextApproverName =
                nextApproverDocument
                    ?.getString("nama")
                    ?.trim()
                    ?: ""

            val nextApproverJabatan =
                nextApproverDocument
                    ?.getString("jabatan")
                    ?.trim()
                    ?.uppercase()
                    ?: ""


            // ==================================================
            // UPDATE STATUS APPROVAL SAAT INI
            // ==================================================

            val currentApprovalStatuses =
                getApprovalStatuses(
                    document
                )

            val approvedStatuses =
                currentApprovalStatuses +
                        (
                                approverUid to
                                        "disetujui"
                                )


            // ==================================================
            // OWNER = FINAL APPROVAL
            // ==================================================

            if (
                approverJabatan == "OWNER"
            ) {

                pengajuanCollection
                    .document(
                        documentId
                    )
                    .update(

                        mapOf(

                            "status" to
                                    "disetujui",

                            "approvedByUid" to
                                    approverUid,

                            "approvedByName" to
                                    approverName,

                            "approvedByJabatan" to
                                    approverJabatan,

                            "approvedAt" to
                                    com.google.firebase.Timestamp.now(),

                            "currentApproverUid" to
                                    "",

                            "currentApproverName" to
                                    "",

                            "currentApproverJabatan" to
                                    "",

                            "approvalLocked" to
                                    true,

                            "approvalStatuses" to
                                    approvedStatuses
                        )
                    )
                    .await()

                // ==================================================
                // NOTIFIKASI FINAL KE STAFF
                // ==================================================

                if (
                    requesterUid.isNotBlank()
                ) {

                    notificationRepository
                        .createNotification(

                            Notification(

                                userId =
                                    requesterUid,

                                type =
                                    "PENGAJUAN_DISETUJUI",

                                title =
                                    "Pengajuan Disetujui",

                                message =
                                    "Pengajuan $jenis Anda telah disetujui.",

                                timestamp =
                                    System.currentTimeMillis(),

                                isRead =
                                    false,

                                relatedId =
                                    documentId
                            )
                        )
                }

                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // MASIH ADA APPROVER BERIKUTNYA
            // ==================================================

            if (
                nextApproverUid.isNotBlank()
            ) {

                val nextStatuses =
                    approvedStatuses +
                            (
                                    nextApproverUid to
                                            "menunggu"
                                    )

                pengajuanCollection
                    .document(
                        documentId
                    )
                    .update(

                        mapOf(

                            "status" to
                                    "menunggu",

                            "approvedByUid" to
                                    approverUid,

                            "approvedByName" to
                                    approverName,

                            "approvedByJabatan" to
                                    approverJabatan,

                            "approvedAt" to
                                    com.google.firebase.Timestamp.now(),

                            "currentApproverUid" to
                                    nextApproverUid,

                            "currentApproverName" to
                                    nextApproverName,

                            "currentApproverJabatan" to
                                    nextApproverJabatan,

                            "approvalLocked" to
                                    false,

                            "approvalStatuses" to
                                    nextStatuses
                        )
                    )
                    .await()

                // ==================================================
                // NOTIFIKASI APPROVER BERIKUTNYA
                // ==================================================

                Log.i(
                    "PengajuanRepository",
                    "Approval lanjut → UID=$nextApproverUid, Nama=$nextApproverName, Jabatan=$nextApproverJabatan"
                )

                notificationRepository
                    .createNotification(

                        Notification(

                            userId =
                                nextApproverUid,

                            type =
                                "PENGAJUAN_APPROVAL",

                            title =
                                "Pengajuan Menunggu Approval",

                            message =
                                "$namaPengaju mengajukan $jenis dan membutuhkan persetujuan Anda.",

                            timestamp =
                                System.currentTimeMillis(),

                            isRead =
                                false,

                            relatedId =
                                documentId
                        )
                    )

            } else {

                // ==================================================
                // FALLBACK
                // JIKA APPROVER BERIKUTNYA TIDAK DITEMUKAN
                // ==================================================

                pengajuanCollection
                    .document(
                        documentId
                    )
                    .update(

                        mapOf(

                            "status" to
                                    "disetujui",

                            "approvedByUid" to
                                    approverUid,

                            "approvedByName" to
                                    approverName,

                            "approvedByJabatan" to
                                    approverJabatan,

                            "approvedAt" to
                                    com.google.firebase.Timestamp.now(),

                            "currentApproverUid" to
                                    "",

                            "currentApproverName" to
                                    "",

                            "currentApproverJabatan" to
                                    "",

                            "approvalLocked" to
                                    true,

                            "approvalStatuses" to
                                    approvedStatuses
                        )
                    )
                    .await()

                // ==================================================
                // NOTIFIKASI KE PENGAJU
                // ==================================================

                if (
                    requesterUid.isNotBlank()
                ) {

                    notificationRepository
                        .createNotification(

                            Notification(

                                userId =
                                    requesterUid,

                                type =
                                    "PENGAJUAN_DISETUJUI",

                                title =
                                    "Pengajuan Disetujui",

                                message =
                                    "Pengajuan $jenis Anda telah disetujui.",

                                timestamp =
                                    System.currentTimeMillis(),

                                isRead =
                                    false,

                                relatedId =
                                    documentId
                            )
                        )
                }
            }

            Result.success(
                Unit
            )

        } catch (e: Exception) {

            Log.e(
                "PengajuanRepository",
                "Gagal memproses approval",
                e
            )

            Result.failure(
                e
            )
        }
    }
}