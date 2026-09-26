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

        val byUid =
            usersCollection
                .whereEqualTo("uid", firebaseUid)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

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

        val byUid =
            usersCollection
                .whereEqualTo("uid", uid)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

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
    //
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

        val byUid =
            usersCollection
                .whereEqualTo("uid", value)
                .limit(1)
                .get()
                .await()

        if (!byUid.isEmpty) {
            return byUid.documents.first()
        }

        val byDocumentId =
            usersCollection
                .document(value)
                .get()
                .await()

        if (byDocumentId.exists()) {
            return byDocumentId
        }

        val byEmail =
            usersCollection
                .whereEqualTo("email", value)
                .limit(1)
                .get()
                .await()

        if (!byEmail.isEmpty) {
            return byEmail.documents.first()
        }

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

        val snapshot =
            usersCollection
                .whereEqualTo(
                    "nama",
                    targetName
                )
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
    // CARI USER ATASAN
    //
    // Field "atasan" bisa berisi:
    // - UID
    // - Document ID
    // - Email
    // - Nama
    // - Jabatan
    // ==========================================================

    private suspend fun findUserByAtasan(
        identity: String
    ): DocumentSnapshot? {

        val value =
            identity.trim()

        if (value.isBlank()) {
            return null
        }

        val byIdentity =
            findUserByIdentity(
                value
            )

        if (byIdentity != null) {
            return byIdentity
        }

        return findUserByJabatan(
            value
        )
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
            // SUSUN JALUR APPROVAL BERDASARKAN ATASAN
            //
            // Tidak dipaksa sampai Owner.
            // Mengikuti struktur atasan sebenarnya.
            // ==================================================

            val approvalDocuments =
                mutableListOf<DocumentSnapshot>()

            var currentAtasan =
                atasan

            val visitedUids =
                mutableSetOf<String>()

            while (
                currentAtasan.isNotBlank() &&
                approvalDocuments.size < 10
            ) {

                val approverDocument =
                    findUserByAtasan(
                        currentAtasan
                    )

                if (approverDocument == null) {

                    Log.w(
                        "PengajuanRepository",
                        "Atasan tidak ditemukan: $currentAtasan"
                    )

                    break
                }

                val approverUid =
                    getFirebaseUid(
                        approverDocument
                    )

                if (
                    approverUid.isBlank() ||
                    approverUid == uid
                ) {

                    Log.w(
                        "PengajuanRepository",
                        "Approval dihentikan karena approver sama dengan pengaju."
                    )

                    break
                }

                if (
                    !visitedUids.add(
                        approverUid
                    )
                ) {

                    Log.w(
                        "PengajuanRepository",
                        "Loop approval terdeteksi pada UID=$approverUid"
                    )

                    break
                }

                approvalDocuments.add(
                    approverDocument
                )

                val approverNama =
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
                    "Approval chain → Nama=$approverNama, Jabatan=$approverJabatan, UID=$approverUid"
                )

                currentAtasan =
                    approverDocument
                        .getString("atasan")
                        ?.trim()
                        ?: ""

                if (
                    currentAtasan.isBlank()
                ) {
                    break
                }
            }


            // ==================================================
            // APPROVAL CHAIN
            // ==================================================

            val approvalChain =
                approvalDocuments
                    .map {
                        getFirebaseUid(it)
                    }
                    .filter {
                        it.isNotBlank()
                    }

            Log.i(
                "PengajuanRepository",
                "Jalur approval final UID: $approvalChain"
            )


            // ==================================================
            // SEMUA ATASAN DALAM CHAIN BOLEH MELIHAT
            // ==================================================

            val approvalViewers =
                approvalChain
                    .distinct()

            Log.i(
                "PengajuanRepository",
                "Approval viewers: $approvalViewers"
            )


            // ==================================================
            // VALIDASI
            //
            // Owner boleh membuat pengajuan walaupun tidak
            // memiliki atasan.
            //
            // Pengguna lain wajib mempunyai atasan.
            // ==================================================

            if (
                jabatan != "OWNER" &&
                approvalChain.isEmpty()
            ) {

                return Result.failure(
                    Exception(
                        "Atasan untuk pengajuan tidak ditemukan."
                    )
                )
            }


            // ==================================================
            // STATUS AWAL APPROVAL
            // ==================================================

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


            // ==================================================
            // APPROVER PERTAMA
            // ==================================================

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


            // ==================================================
            // DATA FIRESTORE
            // ==================================================

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

                    "jabatanPengaju" to jabatan,

                    "divisiPengaju" to divisi,

                    "currentApproverUid" to
                            currentApproverUid,

                    "currentApproverName" to
                            currentApproverName,

                    "currentApproverJabatan" to
                            currentApproverJabatan,

                    "approvalChain" to
                            approvalChain,

                    "approvalViewers" to
                            approvalViewers,

                    "approvalStatuses" to
                            approvalStatuses,

                    "approvalLocked" to
                            false,

                    "atasan" to
                            atasan,

                    "owner" to
                            owner,

                    "waktuPengajuan" to
                            com.google.firebase.Timestamp.now()
                )


            // ==================================================
            // SIMPAN
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
            // NOTIFIKASI APPROVER PERTAMA
            // ==================================================

            firstApprover?.let { approverDocument ->

                val approverUid =
                    getFirebaseUid(
                        approverDocument
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

            Log.e(
                "PengajuanRepository",
                "Gagal mengambil pengajuan saya",
                e
            )

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN YANG BERADA DALAM JALUR SAYA
    //
    // Semua atasan yang terdapat di approvalViewers dapat
    // melihat pengajuan yang masih menunggu.
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
                    .whereArrayContains(
                        "approvalViewers",
                        uid
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map {
                        documentToPengajuan(it)
                    }
                    .filter {

                        it.status.equals(
                            "menunggu",
                            ignoreCase = true
                        ) &&

                                !it.approvalLocked
                    }
                    .sortedByDescending {
                        it.tanggal
                    }

            Result.success(
                daftar
            )

        } catch (e: Exception) {

            Log.e(
                "PengajuanRepository",
                "Gagal mengambil pengajuan untuk approval",
                e
            )

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

            Log.e(
                "PengajuanRepository",
                "Gagal mengambil pengajuan bawahan",
                e
            )

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

            Log.e(
                "PengajuanRepository",
                "Gagal mengambil semua pengajuan",
                e
            )

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

            Log.e(
                "PengajuanRepository",
                "Gagal mengambil detail pengajuan",
                e
            )

            Result.failure(
                e
            )
        }
    }


    // ==========================================================
    // APPROVAL PENGAJUAN
    //
    // ATURAN:
    //
    // 1. Approver aktif dapat Approve/Tolak.
    //
    // 2. Atasan yang lebih tinggi dalam approvalChain
    //    dapat mengambil alih.
    //
    // 3. Tidak menggunakan timer.
    //
    // 4. Saat atasan lebih tinggi melakukan Approve/Tolak,
    //    dia otomatis menjadi approver aktif.
    //
    // 5. Setelah Take Over + Approve:
    //    proses dilanjutkan dari posisi approver tersebut.
    //
    // 6. Setelah Tolak:
    //    pengajuan final dan terkunci.
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

            val currentStatus =
                document
                    .getString("status")
                    ?: "menunggu"

            val approvalLocked =
                document
                    .getBoolean("approvalLocked")
                    ?: false

            val currentApproverUid =
                document
                    .getString("currentApproverUid")
                    ?: ""


            // ==================================================
            // VALIDASI STATUS
            // ==================================================

            if (
                approvalLocked ||
                !currentStatus.equals(
                    "menunggu",
                    ignoreCase = true
                )
            ) {

                return Result.failure(
                    Exception(
                        "Pengajuan sudah selesai atau tidak menunggu approval."
                    )
                )
            }


            // ==================================================
            // AMBIL USER YANG MENEKAN APPROVE/TOLAK
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

            if (
                approvalChain.isEmpty()
            ) {

                return Result.failure(
                    Exception(
                        "Jalur approval tidak ditemukan."
                    )
                )
            }


            // ==================================================
            // CARI POSISI APPROVER AKTUAL
            // ==================================================

            val currentChainIndex =
                approvalChain.indexOf(
                    currentApproverUid
                )

            val actingChainIndex =
                approvalChain.indexOf(
                    approverUid
                )


            // ==================================================
            // VALIDASI HAK APPROVAL / TAKE OVER
            //
            // Jika:
            //
            // acting == current
            // → approver aktif
            //
            // acting berada SETELAH current
            // → atasan lebih tinggi, boleh Take Over
            //
            // acting tidak ditemukan
            // → tidak berhak
            // ==================================================

            if (
                currentApproverUid.isBlank() ||
                currentChainIndex < 0 ||
                actingChainIndex < 0
            ) {

                return Result.failure(
                    Exception(
                        "Anda tidak memiliki hak approval untuk pengajuan ini."
                    )
                )
            }

            val isCurrentApprover =
                approverUid ==
                        currentApproverUid

            val isHigherApprover =
                actingChainIndex >
                        currentChainIndex


            if (
                !isCurrentApprover &&
                !isHigherApprover
            ) {

                return Result.failure(
                    Exception(
                        "Anda bukan approver aktif atau atasan yang lebih tinggi."
                    )
                )
            }


            // ==================================================
            // TAKE OVER
            // ==================================================
            //
            // Jika atasan lebih tinggi melakukan aksi,
            // otomatis posisi approval berpindah kepadanya.
            // ==================================================

            val currentApprovalStatuses =
                getApprovalStatuses(
                    document
                )

            var workingStatuses =
                currentApprovalStatuses.toMutableMap()


            if (
                isHigherApprover
            ) {

                Log.i(
                    "PengajuanRepository",
                    "TAKE OVER → " +
                            "current=$currentApproverUid " +
                            "oleh=$approverUid " +
                            "index=$actingChainIndex"
                )

                // ----------------------------------------------
                // Approver sebelumnya ditandai diambil alih.
                // ----------------------------------------------

                workingStatuses[
                    currentApproverUid
                ] =
                    "diambil_alih"

                // ----------------------------------------------
                // Approver yang mengambil alih menjadi menunggu.
                // ----------------------------------------------

                workingStatuses[
                    approverUid
                ] =
                    "menunggu"

                // ----------------------------------------------
                // Simpan informasi Take Over.
                // ----------------------------------------------

                pengajuanCollection
                    .document(
                        documentId
                    )
                    .update(

                        mapOf(

                            "currentApproverUid" to
                                    approverUid,

                            "currentApproverName" to
                                    approverName,

                            "currentApproverJabatan" to
                                    approverJabatan,

                            "approvalStatuses" to
                                    workingStatuses,

                            "takenOverByUid" to
                                    approverUid,

                            "takenOverByName" to
                                    approverName,

                            "takenOverByJabatan" to
                                    approverJabatan,

                            "takenOverAt" to
                                    com.google.firebase.Timestamp.now(),

                            "approvalLocked" to
                                    false
                        )
                    )
                    .await()
            }


            // ==================================================
            // SETELAH TAKE OVER
            //
            // Posisi yang digunakan untuk proses sekarang adalah
            // posisi user yang melakukan aksi.
            // ==================================================

            val effectiveApproverIndex =
                actingChainIndex

            val effectiveApproverUid =
                approverUid


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
            // VALIDASI STATUS AKSI
            // ==================================================

            if (
                !status.equals(
                    "disetujui",
                    ignoreCase = true
                ) &&
                !status.equals(
                    "ditolak",
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
            // JIKA DITOLAK
            // ==================================================

            if (
                status.equals(
                    "ditolak",
                    ignoreCase = true
                )
            ) {

                workingStatuses[
                    effectiveApproverUid
                ] =
                    "ditolak"

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
                                    workingStatuses,

                            "approvedByUid" to
                                    effectiveApproverUid,

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
                                    "Pengajuan $jenis Anda ditolak oleh $approverName.",

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
            // APPROVE
            // ==================================================

            workingStatuses[
                effectiveApproverUid
            ] =
                "disetujui"


            // ==================================================
            // CARI APPROVER BERIKUTNYA
            //
            // Jika Manager mengambil alih Staff/Supervisor:
            //
            // chain:
            // Supervisor
            // Manager
            // HRD
            // Owner
            //
            // Manager approve
            // → lanjut HRD.
            //
            // Jadi approver sebelumnya dilewati karena Take Over.
            // ==================================================

            val nextApproverUid =
                approvalChain
                    .getOrNull(
                        effectiveApproverIndex + 1
                    )
                    .orEmpty()


            // ==================================================
            // TIDAK ADA APPROVER BERIKUTNYA
            //
            // Approver terakhir menjadi final approval.
            //
            // Tidak bergantung pada jabatan Owner.
            // ==================================================

            if (
                nextApproverUid.isBlank()
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
                                    effectiveApproverUid,

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
                                    workingStatuses
                        )
                    )
                    .await()


                // ==================================================
                // NOTIFIKASI FINAL KE PENGAJU
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
                                    "Pengajuan $jenis Anda telah disetujui oleh $approverName.",

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
            // CARI DATA APPROVER BERIKUTNYA
            // ==================================================

            val nextApproverDocument =
                getUserDocumentByUid(
                    nextApproverUid
                )


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
            // APPROVER BERIKUTNYA MENUNGGU
            // ==================================================

            workingStatuses[
                nextApproverUid
            ] =
                "menunggu"


            pengajuanCollection
                .document(
                    documentId
                )
                .update(

                    mapOf(

                        "status" to
                                "menunggu",

                        "approvedByUid" to
                                effectiveApproverUid,

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
                                workingStatuses
                    )
                )
                .await()


            // ==================================================
            // NOTIFIKASI APPROVER BERIKUTNYA
            // ==================================================

            if (
                nextApproverUid.isNotBlank()
            ) {

                Log.i(
                    "PengajuanRepository",
                    "Approval lanjut → " +
                            "UID=$nextApproverUid, " +
                            "Nama=$nextApproverName, " +
                            "Jabatan=$nextApproverJabatan"
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