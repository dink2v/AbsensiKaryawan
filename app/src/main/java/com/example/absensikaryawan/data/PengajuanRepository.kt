package com.example.absensikaryawan.repository

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

    private val db =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private val pengajuanCollection =
        db.collection("pengajuan")

    private val usersCollection =
        db.collection("users")

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
        // Fallback document ID = Firebase UID
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

        return document
            .getString("uid")
            ?.takeIf {
                it.isNotBlank()
            }
            ?: document.id
    }


    // ==========================================================
    // CARI USER BERDASARKAN NAMA
    // ==========================================================

    private suspend fun findUserByName(
        nama: String
    ): DocumentSnapshot? {

        if (nama.isBlank()) {
            return null
        }

        val snapshot =
            usersCollection
                .whereEqualTo("nama", nama)
                .limit(1)
                .get()
                .await()

        return if (!snapshot.isEmpty) {
            snapshot.documents.first()
        } else {
            null
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

        val snapshot =
            usersCollection
                .whereEqualTo(
                    "jabatan",
                    jabatan
                )
                .limit(1)
                .get()
                .await()

        return if (!snapshot.isEmpty) {
            snapshot.documents.first()
        } else {
            null
        }
    }


    // ==========================================================
    // KONVERSI FIRESTORE → PENGAJUAN DATA
    // ==========================================================

    private fun documentToPengajuan(
        document: DocumentSnapshot
    ): PengajuanData {

        return PengajuanData(

            id = document.id,

            nama =
                document.getString("nama")
                    ?: "",

            jenis =
                document.getString("jenis")
                    ?: "",

            tanggal =
                document.getString("tanggal")
                    ?: "",

            jamPulang =
                document.getString("jamPulang")
                    ?: "",

            jamKeluar =
                document.getString("jamKeluar")
                    ?: "",

            jamKembali =
                document.getString("jamKembali")
                    ?: "",

            tanggalMulai =
                document.getString("tanggalMulai")
                    ?: "",

            tanggalSelesai =
                document.getString("tanggalSelesai")
                    ?: "",

            alasan =
                document.getString("alasan")
                    ?: "",

            status =
                document.getString("status")
                    ?: "menunggu"
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
                    ?: firebaseUser.email
                    ?: ""

            val jabatan =
                userDocument
                    .getString("jabatan")
                    ?.uppercase()
                    ?: "STAFF"

            val divisi =
                userDocument
                    .getString("divisi")
                    ?: ""

            val atasan =
                userDocument
                    .getString("atasan")
                    ?: ""

            val owner =
                userDocument
                    .getString("owner")
                    ?: ""

            // --------------------------------------------------
            // Cari Supervisor sebagai approval pertama
            // --------------------------------------------------

            val supervisorDocument =
                if (
                    jabatan == "STAFF" ||
                    jabatan == "KARYAWAN"
                ) {

                    if (atasan.isNotBlank()) {
                        findUserByName(
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

            val supervisorUid =
                supervisorDocument
                    ?.let {
                        getFirebaseUid(it)
                    }
                    ?: ""

            val supervisorName =
                supervisorDocument
                    ?.getString("nama")
                    ?: atasan

            // --------------------------------------------------
            // Approval berikutnya
            // --------------------------------------------------

            val currentApproverUid =
                when {

                    supervisorUid.isNotBlank() ->
                        supervisorUid

                    jabatan == "SUPERVISOR" ->
                        ""

                    else ->
                        ""
                }

            val currentApproverName =
                when {

                    supervisorName.isNotBlank() ->
                        supervisorName

                    jabatan == "SUPERVISOR" ->
                        ""

                    else ->
                        ""
                }

            val currentApproverJabatan =
                when {

                    supervisorUid.isNotBlank() ->
                        "SUPERVISOR"

                    else ->
                        ""
                }

            // --------------------------------------------------
            // DATA FIRESTORE
            // --------------------------------------------------

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

                    // Informasi hirarki
                    "atasan" to atasan,
                    "owner" to owner,

                    // Waktu
                    "waktuPengajuan" to
                            com.google.firebase.Timestamp.now()
                )

            val document =
                pengajuanCollection
                    .add(data)
                    .await()

            // --------------------------------------------------
            // NOTIFIKASI APPROVER PERTAMA
            // --------------------------------------------------

            if (
                currentApproverUid.isNotBlank()
            ) {

                val notification =
                    Notification(

                        userId =
                            currentApproverUid,

                        type =
                            "PENGAJUAN_APPROVAL",

                        title =
                            "Pengajuan Baru",

                        message =
                            "$nama mengajukan $jenis " +
                                    "yang membutuhkan persetujuan Anda.",

                        timestamp =
                            System.currentTimeMillis(),

                        isRead =
                            false,

                        relatedId =
                            document.id
                    )

                notificationRepository
                    .createNotification(
                        notification
                    )
            }

            // --------------------------------------------------
            // NOTIFIKASI ADMIN
            // --------------------------------------------------

            notifyAdmins(
                nama = nama,
                jenis = jenis,
                documentId = document.id
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
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
                        adminDocument
                            .getString("uid")
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: adminDocument.id

                    if (adminUid.isBlank()) {
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

            Result.failure(e)
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

            // --------------------------------------------------
            // Cari menggunakan currentApproverUid
            // --------------------------------------------------

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

            Result.failure(e)
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
                    ?: ""

            if (namaAtasan.isBlank()) {

                return Result.success(
                    emptyList()
                )
            }

            // --------------------------------------------------
            // Cari berdasarkan field atasan
            // --------------------------------------------------

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

            Result.failure(e)
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

            Result.failure(e)
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

            if (!document.exists()) {

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

            Result.failure(e)
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

            if (!document.exists()) {

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

            // --------------------------------------------------
            // Pastikan yang approve memang approver saat ini
            // --------------------------------------------------

            if (
                currentApproverUid.isNotBlank() &&
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

                pengajuanCollection
                    .document(documentId)
                    .update(
                        mapOf(
                            "status" to "ditolak",
                            "approvedByUid" to approverUid,
                            "approvedByName" to
                                    (
                                            firebaseUser.displayName
                                                ?: ""
                                            ),
                            "approvedAt" to
                                    com.google.firebase.Timestamp.now()
                        )
                    )
                    .await()

                // ----------------------------------------------
                // Notifikasi ke pengaju
                // ----------------------------------------------

                if (requesterUid.isNotBlank()) {

                    val notification =
                        Notification(

                            userId =
                                requesterUid,

                            type =
                                "PENGAJUAN_DISETUJUI",

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

                    notificationRepository
                        .createNotification(
                            notification
                        )
                }

                return Result.success(
                    Unit
                )
            }


            // ==================================================
            // JIKA DISETUJUI
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
                    ?: firebaseUser.displayName
                    ?: ""

            val approverJabatan =
                approverDocument
                    ?.getString("jabatan")
                    ?.uppercase()
                    ?: ""


            // ==================================================
            // TENTUKAN APPROVER BERIKUTNYA
            // ==================================================

            var nextApproverUid = ""
            var nextApproverName = ""
            var nextApproverJabatan = ""

            when (approverJabatan) {

                // ------------------------------------------------
                // STAFF → SUPERVISOR
                // ------------------------------------------------

                "STAFF",
                "KARYAWAN" -> {

                    val supervisorDocument =
                        findUserByJabatan(
                            "SUPERVISOR"
                        )

                    if (
                        supervisorDocument != null
                    ) {

                        nextApproverUid =
                            getFirebaseUid(
                                supervisorDocument
                            )

                        nextApproverName =
                            supervisorDocument
                                .getString("nama")
                                ?: ""

                        nextApproverJabatan =
                            "SUPERVISOR"
                    }
                }


                // ------------------------------------------------
                // SUPERVISOR → MANAGER
                // ------------------------------------------------

                "SUPERVISOR" -> {

                    val managerDocument =
                        findUserByJabatan(
                            "MANAGER"
                        )

                    if (
                        managerDocument != null
                    ) {

                        nextApproverUid =
                            getFirebaseUid(
                                managerDocument
                            )

                        nextApproverName =
                            managerDocument
                                .getString("nama")
                                ?: ""

                        nextApproverJabatan =
                            "MANAGER"
                    }
                }


                // ------------------------------------------------
                // MANAGER → HRD
                // ------------------------------------------------

                "MANAGER" -> {

                    val hrdDocument =
                        findUserByJabatan(
                            "HRD"
                        )

                    if (
                        hrdDocument != null
                    ) {

                        nextApproverUid =
                            getFirebaseUid(
                                hrdDocument
                            )

                        nextApproverName =
                            hrdDocument
                                .getString("nama")
                                ?: ""

                        nextApproverJabatan =
                            "HRD"
                    }
                }


                // ------------------------------------------------
                // HRD → OWNER
                // ------------------------------------------------

                "HRD" -> {

                    val ownerDocument =
                        findUserByJabatan(
                            "OWNER"
                        )

                    if (
                        ownerDocument != null
                    ) {

                        nextApproverUid =
                            getFirebaseUid(
                                ownerDocument
                            )

                        nextApproverName =
                            ownerDocument
                                .getString("nama")
                                ?: ""

                        nextApproverJabatan =
                            "OWNER"
                    }
                }


                // ------------------------------------------------
                // OWNER → FINAL
                // ------------------------------------------------

                "OWNER" -> {

                    nextApproverUid = ""
                    nextApproverName = ""
                    nextApproverJabatan = ""
                }
            }


            // ==================================================
            // OWNER = FINAL APPROVAL
            // ==================================================

            if (
                approverJabatan == "OWNER"
            ) {

                pengajuanCollection
                    .document(documentId)
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
                                    ""
                        )
                    )
                    .await()

                // ----------------------------------------------
                // Notifikasi FINAL ke Staff
                // ----------------------------------------------

                if (
                    requesterUid.isNotBlank()
                ) {

                    val notification =
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

                    notificationRepository
                        .createNotification(
                            notification
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

                pengajuanCollection
                    .document(documentId)
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
                                    nextApproverJabatan
                        )
                    )
                    .await()

                // ----------------------------------------------
                // Notifikasi hanya ke approver berikutnya
                // ----------------------------------------------

                val notification =
                    Notification(

                        userId =
                            nextApproverUid,

                        type =
                            "PENGAJUAN_APPROVAL",

                        title =
                            "Pengajuan Menunggu Approval",

                        message =
                            "$namaPengaju mengajukan $jenis " +
                                    "dan membutuhkan persetujuan Anda.",

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

            } else {

                // ------------------------------------------------
                // Fallback jika tidak ditemukan approver berikut
                // ------------------------------------------------

                pengajuanCollection
                    .document(documentId)
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
                                    ""
                        )
                    )
                    .await()

                // ----------------------------------------------
                // Notifikasi ke pengaju
                // ----------------------------------------------

                if (
                    requesterUid.isNotBlank()
                ) {

                    val notification =
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

                    notificationRepository
                        .createNotification(
                            notification
                        )
                }
            }

            Result.success(
                Unit
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}