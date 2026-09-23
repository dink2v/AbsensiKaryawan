package com.example.absensikaryawan.data

import com.example.absensikaryawan.models.ChatMessage
import com.example.absensikaryawan.models.Notification
import com.example.absensikaryawan.repository.NotificationRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// ==========================================================
// FIRESTORE REPOSITORY
// ==========================================================

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    private val notificationRepository =
        NotificationRepository()

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
    // COLLECTION CHAT
    // ==========================================================

    private val chatCollection =
        db.collection("chats")

    // ==========================================================
    // COLLECTION PENGAJUAN
    // ==========================================================

    private val pengajuanCollection =
        db.collection("pengajuan")


    // ==========================================================
    // SIMPAN ABSEN MASUK
    // ==========================================================

    suspend fun simpanAbsenMasuk(
        uid: String,
        nama: String,
        tanggal: String,
        jamMasuk: String,
        qrData: String,
        catatan: String = "",
        kantor: String = ""
    ): Result<Unit> {

        return try {

            val data = hashMapOf<String, Any>(
                "uid" to uid,
                "nama" to nama,
                "tanggal" to tanggal,
                "jamMasuk" to jamMasuk,
                "jamPulang" to "",
                "status" to "Hadir",
                "qrData" to qrData,
                "kantor" to kantor,
                "catatan" to catatan
            )

            attendanceCollection
                .add(data)
                .await()

            notifyAdminsAbsen(
                uid = uid,
                nama = nama,
                tanggal = tanggal,
                jamMasuk = jamMasuk,
                kantor = kantor
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // SIMPAN ABSEN MASUK LUAR KANTOR
    // ==========================================================

    suspend fun simpanAbsenLuarKantor(
        uid: String,
        nama: String,
        tanggal: String,
        jamMasuk: String,
        lokasi: String,
        alasan: String
    ): Result<Unit> {

        return try {

            val data = hashMapOf<String, Any>(
                "uid" to uid,
                "nama" to nama,
                "tanggal" to tanggal,
                "jamMasuk" to jamMasuk,
                "jamPulang" to "",
                "status" to "Hadir",
                "qrData" to "LUAR_KANTOR",
                "lokasi" to lokasi,
                "alasan" to alasan
            )

            attendanceCollection
                .add(data)
                .await()

            notifyAdminsAbsen(
                uid = uid,
                nama = nama,
                tanggal = tanggal,
                jamMasuk = jamMasuk,
                kantor = "Luar Kantor"
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // NOTIFIKASI ABSENSI KE SEMUA ADMIN
    // ==========================================================

    private fun notifyAdminsAbsen(
        uid: String,
        nama: String,
        tanggal: String,
        jamMasuk: String,
        kantor: String
    ) {

        usersCollection
            .whereEqualTo("isAdmin", true)
            .get()
            .addOnSuccessListener { snapshot ->

                snapshot.documents.forEach { adminDocument ->

                    val adminUid =
                        adminDocument.id

                    val lokasi =
                        kantor.ifBlank {
                            "Kantor"
                        }

                    val notification =
                        Notification(
                            userId = adminUid,
                            type = "ABSENSI",
                            title = "Absensi Staff",
                            message =
                                "$nama melakukan absensi masuk " +
                                        "pada $jamMasuk di $lokasi.",
                            timestamp =
                                System.currentTimeMillis(),
                            isRead = false,
                            relatedId = uid
                        )

                    notificationRepository.createNotification(
                        notification = notification
                    )
                }
            }
    }


    // ==========================================================
    // CARI ABSEN HARI INI
    // ==========================================================

    suspend fun getAbsenHariIni(
        uid: String,
        tanggal: String
    ): AbsenHariIni? {

        val snapshot =
            attendanceCollection
                .whereEqualTo("uid", uid)
                .whereEqualTo("tanggal", tanggal)
                .limit(1)
                .get()
                .await()

        if (snapshot.isEmpty) {
            return null
        }

        val document =
            snapshot.documents.first()

        return AbsenHariIni(
            documentId = document.id,
            jamMasuk =
                document.getString("jamMasuk")
                    ?: "",
            jamPulang =
                document.getString("jamPulang")
                    ?: ""
        )
    }


    // ==========================================================
    // DATA REKAP ABSENSI
    // ==========================================================

    suspend fun getRekapAbsensi(
        tanggalMulai: String,
        tanggalAkhir: String
    ): Result<List<RekapAbsensiData>> {

        return try {

            val snapshot =
                attendanceCollection
                    .whereGreaterThanOrEqualTo(
                        "tanggal",
                        tanggalMulai
                    )
                    .whereLessThanOrEqualTo(
                        "tanggal",
                        tanggalAkhir
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents
                    .map { document ->

                        RekapAbsensiData(
                            id = document.id,
                            uid =
                                document.getString("uid")
                                    ?: "",
                            nama =
                                document.getString("nama")
                                    ?: "",
                            tanggal =
                                document.getString("tanggal")
                                    ?: "",
                            jamMasuk =
                                document.getString("jamMasuk")
                                    ?: "",
                            jamPulang =
                                document.getString("jamPulang")
                                    ?: "",
                            status =
                                document.getString("status")
                                    ?: "",
                            qrData =
                                document.getString("qrData")
                                    ?: "",
                            kantor =
                                document.getString("kantor")
                                    ?: ""
                        )
                    }
                    .sortedWith(
                        compareBy<RekapAbsensiData> {
                            it.tanggal
                        }.thenBy {
                            it.nama.lowercase()
                        }
                    )

            Result.success(daftar)

        } catch (e: Exception) {

            Result.failure(e)
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
                .document(documentId)
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
    // CARI APPROVAL BERDASARKAN JABATAN
    // ==========================================================

    private suspend fun cariApprover(
        jabatan: String
    ): ApprovalPerson? {

        val snapshot =
            usersCollection
                .get()
                .await()

        val kandidat =
            snapshot.documents.filter { document ->

                val jabatanUser =
                    document.getString("jabatan")
                        ?.trim()
                        ?.lowercase()
                        ?: ""

                jabatanUser ==
                        jabatan
                            .trim()
                            .lowercase()
            }

        if (kandidat.isEmpty()) {
            return null
        }

        if (kandidat.size > 1) {

            throw IllegalStateException(
                "Terdapat lebih dari satu karyawan dengan jabatan $jabatan. " +
                        "Tentukan satu approver terlebih dahulu."
            )
        }

        val document =
            kandidat.first()

        return ApprovalPerson(
            uid =
                document.getString("uid")
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: document.id,

            nama =
                document.getString("nama")
                    ?: "",

            jabatan =
                document.getString("jabatan")
                    ?: jabatan
        )
    }


    // ==========================================================
    // BUAT JALUR APPROVAL
    //
    // STAFF
    //   ↓
    // SUPERVISOR
    //   ↓
    // MANAGER
    //   ↓
    // HRD
    //   ↓
    // OWNER
    //
    // ADMIN TIDAK MASUK JALUR INI
    // ==========================================================

    private suspend fun buatApprovalChain():
            Result<List<ApprovalPerson>> {

        return try {

            val jabatanApproval =
                listOf(
                    "Supervisor",
                    "Manager",
                    "HRD",
                    "Owner"
                )

            val chain =
                mutableListOf<ApprovalPerson>()

            for (jabatan in jabatanApproval) {

                val approver =
                    cariApprover(jabatan)

                if (approver == null) {

                    return Result.failure(
                        IllegalStateException(
                            "Approver dengan jabatan $jabatan belum tersedia."
                        )
                    )
                }

                chain.add(approver)
            }

            Result.success(chain)

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

            // ==================================================
            // BUAT JALUR APPROVAL
            // ==================================================

            val chainResult =
                buatApprovalChain()

            if (chainResult.isFailure) {

                return Result.failure(
                    chainResult.exceptionOrNull()
                        ?: IllegalStateException(
                            "Gagal membuat jalur approval."
                        )
                )
            }

            val approvalChain =
                chainResult.getOrThrow()

            if (approvalChain.isEmpty()) {

                return Result.failure(
                    IllegalStateException(
                        "Jalur approval belum tersedia."
                    )
                )
            }

            // ==================================================
            // UID APPROVER
            // ==================================================

            val approvalChainUid =
                approvalChain.map {
                    it.uid
                }

            // ==================================================
            // STATUS APPROVER
            // ==================================================

            val approvalStatuses =
                hashMapOf<String, String>()

            approvalChain.forEachIndexed { index, person ->

                approvalStatuses[person.uid] =
                    if (index == 0) {
                        "menunggu"
                    } else {
                        "belum"
                    }
            }

            // ==================================================
            // APPROVER PERTAMA
            // ==================================================

            val currentApprover =
                approvalChain.first()

            // ==================================================
            // DATA PENGAJUAN
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

                    "waktuPengajuan" to Timestamp.now(),

                    "approvalChain" to approvalChainUid,

                    "approvalStatuses" to approvalStatuses,

                    "currentApproverUid" to
                            currentApprover.uid,

                    "currentApproverName" to
                            currentApprover.nama,

                    "currentApproverJabatan" to
                            currentApprover.jabatan,

                    "approvalLocked" to false
                )

            // ==================================================
            // SIMPAN PENGAJUAN
            // ==================================================

            val document =
                pengajuanCollection
                    .add(data)
                    .await()

            // ==================================================
            // NOTIFIKASI SUPERVISOR
            // ==================================================

            val notification =
                Notification(
                    userId =
                        currentApprover.uid,

                    type =
                        "PENGAJUAN_BARU",

                    title =
                        "Pengajuan Baru",

                    message =
                        "$nama membuat pengajuan $jenis " +
                                "dan menunggu persetujuan Anda.",

                    timestamp =
                        System.currentTimeMillis(),

                    isRead =
                        false,

                    relatedId =
                        document.id
                )

            notificationRepository.createNotification(
                notification = notification
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN YANG MENUNGGU APPROVAL USER INI
    // ==========================================================

    suspend fun getPengajuanMenungguApproval(
        approverUid: String
    ): Result<List<PengajuanData>> {

        return try {

            if (approverUid.isBlank()) {
                return Result.success(emptyList())
            }

            val snapshot =
                pengajuanCollection
                    .whereEqualTo(
                        "currentApproverUid",
                        approverUid
                    )
                    .whereEqualTo(
                        "approvalLocked",
                        false
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents.map { document ->
                    document.toPengajuanData()
                }

            Result.success(daftar)

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
                pengajuanCollection
                    .whereEqualTo(
                        "status",
                        "menunggu"
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents.map { document ->

                    document.toPengajuanData()
                }

            Result.success(daftar)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN SAYA
    // ==========================================================

    suspend fun getPengajuanSaya(
        uid: String
    ): Result<List<PengajuanData>> {

        return try {

            val snapshot =
                pengajuanCollection
                    .whereEqualTo(
                        "uid",
                        uid
                    )
                    .get()
                    .await()

            val daftar =
                snapshot.documents.map { document ->

                    document.toPengajuanData()
                }

            Result.success(daftar)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // AMBIL PENGAJUAN TEAM
    //
    // TEAM DITENTUKAN BERDASARKAN FIELD:
    // users.atasan == jabatan approver saat ini
    //
    // Contoh:
    //
    // Supervisor -> users dengan atasan = "Supervisor"
    // Manager    -> users dengan atasan = "Manager"
    // HRD        -> users dengan atasan = "HRD"
    // Owner      -> users dengan atasan = "Owner"
    //
    // HANYA pengajuan milik UID anggota team yang diambil.
    // Tidak mengambil seluruh pengajuan perusahaan.
    // ==========================================================

    suspend fun getPengajuanTeam(
        approverUid: String
    ): Result<List<PengajuanData>> {

        return try {

            if (approverUid.isBlank()) {
                return Result.success(emptyList())
            }

            // ==================================================
            // CARI DATA APPROVER BERDASARKAN AUTH UID
            // ==================================================

            val approverSnapshot =
                usersCollection
                    .whereEqualTo(
                        "uid",
                        approverUid
                    )
                    .limit(1)
                    .get()
                    .await()

            val approverDocument =
                approverSnapshot.documents.firstOrNull()
                    ?: return Result.success(emptyList())

            val jabatanApprover =
                approverDocument
                    .getString("jabatan")
                    ?.trim()
                    ?: ""

            if (jabatanApprover.isBlank()) {
                return Result.success(emptyList())
            }

            // ==================================================
            // CARI ANGGOTA TEAM
            //
            // Contoh:
            //
            // Supervisor
            // ->
            // users.atasan == "Supervisor"
            // ==================================================

            val teamSnapshot =
                usersCollection
                    .whereEqualTo(
                        "atasan",
                        jabatanApprover
                    )
                    .get()
                    .await()

            if (teamSnapshot.isEmpty) {
                return Result.success(emptyList())
            }

            // ==================================================
            // AMBIL AUTH UID ANGGOTA TEAM
            // ==================================================

            val teamUids =
                teamSnapshot.documents
                    .mapNotNull { userDocument ->

                        val uid =
                            userDocument
                                .getString("uid")
                                ?.trim()
                                ?.takeIf {
                                    it.isNotBlank()
                                }

                        uid
                    }
                    .filter {
                        it != approverUid
                    }
                    .distinct()

            if (teamUids.isEmpty()) {
                return Result.success(emptyList())
            }

            // ==================================================
            // FIRESTORE WHERE-IN MAKSIMAL 30 UID
            //
            // Jika team lebih dari 30 orang,
            // otomatis dibagi beberapa batch.
            // ==================================================

            val hasil =
                mutableListOf<PengajuanData>()

            teamUids
                .chunked(30)
                .forEach { batchUids ->

                    val snapshot =
                        pengajuanCollection
                            .whereIn(
                                "uid",
                                batchUids
                            )
                            .get()
                            .await()

                    snapshot.documents.forEach { document ->

                        hasil.add(
                            document.toPengajuanData()
                        )
                    }
                }

            // ==================================================
            // URUTKAN DATA
            // ==================================================

            val daftar =
                hasil.sortedByDescending {
                    it.id
                }

            Result.success(daftar)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // UPDATE STATUS PENGAJUAN
    //
    // FUNGSI LAMA TETAP DIPERTAHANKAN
    // ==========================================================

    suspend fun updateStatusPengajuan(
        documentId: String,
        status: String
    ): Result<Unit> {

        return try {

            pengajuanCollection
                .document(documentId)
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
    // PROSES APPROVAL BERDASARKAN APPROVER
    //
    // FLOW:
    //
    // Supervisor
    //      ↓ approve
    // Manager
    //      ↓ approve
    // HRD
    //      ↓ approve
    // Owner
    //      ↓ approve
    // Staff mendapat notifikasi final
    //
    // JIKA ADA YANG MENOLAK:
    // Approver → Staff mendapat notifikasi ditolak
    // ==========================================================

    suspend fun prosesApprovalPengajuan(
        documentId: String,
        approverUid: String,
        keputusan: String
    ): Result<Unit> {

        return try {

            // ==================================================
            // VALIDASI KEPUTUSAN
            // ==================================================

            if (
                keputusan != "disetujui" &&
                keputusan != "ditolak"
            ) {

                return Result.failure(
                    IllegalArgumentException(
                        "Keputusan approval tidak valid."
                    )
                )
            }

            // ==================================================
            // AMBIL DATA PENGAJUAN
            // ==================================================

            val document =
                pengajuanCollection
                    .document(documentId)
                    .get()
                    .await()

            if (!document.exists()) {

                return Result.failure(
                    IllegalStateException(
                        "Pengajuan tidak ditemukan."
                    )
                )
            }

            // ==================================================
            // STATUS SEKARANG
            // ==================================================

            val statusSekarang =
                document.getString("status")
                    ?: "menunggu"

            if (
                statusSekarang == "disetujui" ||
                statusSekarang == "ditolak"
            ) {

                return Result.failure(
                    IllegalStateException(
                        "Pengajuan sudah memiliki keputusan final."
                    )
                )
            }

            // ==================================================
            // APPROVER AKTIF
            // ==================================================

            val currentApproverUid =
                document.getString(
                    "currentApproverUid"
                ) ?: ""

            if (currentApproverUid != approverUid) {

                return Result.failure(
                    IllegalStateException(
                        "Anda bukan approver pada tahap ini."
                    )
                )
            }

            // ==================================================
            // DATA PEMOHON
            // ==================================================

            val pemohonUid =
                document.getString("uid")
                    ?: ""

            val pemohonNama =
                document.getString("nama")
                    ?: "Staff"

            val jenisPengajuan =
                document.getString("jenis")
                    ?: "Pengajuan"

            // ==================================================
            // NAMA DAN JABATAN APPROVER SAAT INI
            // ==================================================

            val approverSnapshot =
                usersCollection
                    .whereEqualTo("uid", approverUid)
                    .limit(1)
                    .get()
                    .await()

            val approverDocument =
                approverSnapshot.documents.firstOrNull()
                    ?: return Result.failure(
                        IllegalStateException(
                            "Data approver tidak ditemukan."
                        )
                    )

            val approverName =
                approverDocument
                    .getString("nama")
                    ?: "Approver"

            val approverJabatan =
                approverDocument
                    .getString("jabatan")
                    ?: "Approver"

            // ==================================================
            // APPROVAL CHAIN
            // ==================================================

            val approvalChain =
                document.get(
                    "approvalChain"
                ) as? List<*>
                    ?: emptyList<Any>()

            // ==================================================
            // APPROVAL STATUSES
            // ==================================================

            val approvalStatuses =
                (
                        document.get(
                            "approvalStatuses"
                        ) as? Map<*, *>
                            ?: emptyMap<Any, Any>()
                        )
                    .mapNotNull { entry ->

                        val key =
                            entry.key as? String
                                ?: return@mapNotNull null

                        val value =
                            entry.value as? String
                                ?: return@mapNotNull null

                        key to value
                    }
                    .toMap()
                    .toMutableMap()

            // ==================================================
            // SIMPAN KEPUTUSAN APPROVER SAAT INI
            // ==================================================

            approvalStatuses[approverUid] =
                keputusan

            // ==================================================
            // JIKA DITOLAK
            // ==================================================

            if (keputusan == "ditolak") {

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

                        "waktuKeputusan" to
                                Timestamp.now()
                    )

                pengajuanCollection
                    .document(documentId)
                    .update(updateData)
                    .await()

                // ==================================================
                // NOTIFIKASI PENOLAKAN KE STAFF
                // ==================================================

                if (pemohonUid.isNotBlank()) {

                    val notification =
                        Notification(

                            userId =
                                pemohonUid,

                            type =
                                "PENGAJUAN_DITOLAK",

                            title =
                                "Pengajuan Ditolak",

                            message =
                                "Pengajuan $jenisPengajuan Anda " +
                                        "ditolak pada tahap " +
                                        "$approverJabatan.",

                            timestamp =
                                System.currentTimeMillis(),

                            isRead =
                                false,

                            relatedId =
                                documentId
                        )

                    notificationRepository
                        .createNotification(
                            notification =
                                notification
                        )
                }

                return Result.success(Unit)
            }

            // ==================================================
            // CARI TAHAP BERIKUTNYA
            // ==================================================

            val currentIndex =
                approvalChain.indexOf(approverUid)

            if (currentIndex == -1) {

                return Result.failure(
                    IllegalStateException(
                        "Approver tidak ditemukan dalam approval chain."
                    )
                )
            }

            val nextIndex =
                currentIndex + 1

            // ==================================================
            // JIKA OWNER SUDAH MENYETUJUI
            // ==================================================

            if (nextIndex >= approvalChain.size) {

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

                        "waktuKeputusan" to
                                Timestamp.now()
                    )

                pengajuanCollection
                    .document(documentId)
                    .update(updateData)
                    .await()

                // ==================================================
                // NOTIFIKASI FINAL KE STAFF
                // ==================================================

                if (pemohonUid.isNotBlank()) {

                    val notification =
                        Notification(

                            userId =
                                pemohonUid,

                            type =
                                "PENGAJUAN_DISETUJUI",

                            title =
                                "Pengajuan Disetujui",

                            message =
                                "Pengajuan $jenisPengajuan Anda " +
                                        "telah disetujui seluruh approver.",

                            timestamp =
                                System.currentTimeMillis(),

                            isRead =
                                false,

                            relatedId =
                                documentId
                        )

                    notificationRepository
                        .createNotification(
                            notification =
                                notification
                        )
                }

                return Result.success(Unit)
            }

            // ==================================================
            // APPROVER BERIKUTNYA
            // ==================================================

            val nextApproverUid =
                approvalChain[nextIndex]
                        as? String
                    ?: return Result.failure(
                        IllegalStateException(
                            "UID approver berikutnya tidak valid."
                        )
                    )

            // ==================================================
            // AMBIL DATA APPROVER BERIKUTNYA
            // ==================================================

            val nextApproverSnapshot =
                usersCollection
                    .whereEqualTo("uid", nextApproverUid)
                    .limit(1)
                    .get()
                    .await()

            val nextApproverDocument =
                nextApproverSnapshot.documents.firstOrNull()
                    ?: return Result.failure(
                        IllegalStateException(
                            "Data approver berikutnya tidak ditemukan."
                        )
                    )

            val nextApproverName =
                nextApproverDocument
                    .getString("nama")
                    ?: ""

            val nextApproverJabatan =
                nextApproverDocument
                    .getString("jabatan")
                    ?: ""

            // ==================================================
            // AKTIFKAN APPROVER BERIKUTNYA
            // ==================================================

            approvalStatuses[nextApproverUid] =
                "menunggu"

            val updateData =
                hashMapOf<String, Any>(

                    "status" to
                            "menunggu",

                    "approvalStatuses" to
                            approvalStatuses,

                    "currentApproverUid" to
                            nextApproverUid,

                    "currentApproverName" to
                            nextApproverName,

                    "currentApproverJabatan" to
                            nextApproverJabatan,

                    "approvalLocked" to
                            false
                )

            pengajuanCollection
                .document(documentId)
                .update(updateData)
                .await()

            // ==================================================
            // NOTIFIKASI APPROVER BERIKUTNYA
            // ==========================================================

            val notification =
                Notification(

                    userId =
                        nextApproverUid,

                    type =
                        "PENGAJUAN_APPROVAL",

                    title =
                        "Pengajuan Menunggu Approval",

                    message =
                        "$pemohonNama membuat pengajuan " +
                                "$jenisPengajuan yang menunggu " +
                                "persetujuan Anda.",

                    timestamp =
                        System.currentTimeMillis(),

                    isRead =
                        false,

                    relatedId =
                        documentId
                )

            notificationRepository
                .createNotification(
                    notification =
                        notification
                )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


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

            Result.success(document.id)

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
                snapshot.documents
                    .map { document ->

                        KaryawanData(
                            id = document.id,

                            nama =
                                document.getString("nama")
                                    ?: "",

                            email =
                                document.getString("email")
                                    ?: "",

                            jabatan =
                                document.getString("jabatan")
                                    ?: "",

                            divisi =
                                document.getString("divisi")
                                    ?: "",

                            usernameTele =
                                document.getString("usernameTele")
                                    ?: "",

                            isAdmin =
                                document.getBoolean("isAdmin")
                                    ?: false
                        )
                    }
                    .sortedBy {
                        it.nama.lowercase()
                    }

            Result.success(daftar)

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
                .document(documentId)
                .update(data)
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
                .document(documentId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // KIRIM PESAN CHAT
    // ==========================================================

    suspend fun kirimPesanChat(
        staffUid: String,
        senderUid: String,
        senderType: String,
        message: String
    ): Result<Unit> {

        return try {

            val text =
                message.trim()

            if (text.isEmpty()) {

                return Result.failure(
                    IllegalArgumentException(
                        "Pesan tidak boleh kosong"
                    )
                )
            }

            val data =
                hashMapOf<String, Any>(
                    "senderUid" to senderUid,
                    "senderType" to senderType,
                    "message" to text,
                    "timestamp" to Timestamp.now()
                )

            chatCollection
                .document(staffUid)
                .collection("messages")
                .add(data)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================================
    // REALTIME PESAN CHAT
    // ==========================================================

    fun listenChatMessages(
        staffUid: String,
        onMessagesChanged:
            (List<ChatMessageData>) -> Unit,
        onError:
            (Exception) -> Unit
    ): ListenerRegistration {

        return chatCollection
            .document(staffUid)
            .collection("messages")
            .orderBy(
                "timestamp",
                Query.Direction.ASCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    onError(error)

                    return@addSnapshotListener
                }

                if (snapshot == null) {

                    onMessagesChanged(
                        emptyList()
                    )

                    return@addSnapshotListener
                }

                val messages =
                    snapshot.documents.mapNotNull { document ->

                        val message =
                            document.getString("message")
                                ?: return@mapNotNull null

                        val senderUid =
                            document.getString("senderUid")
                                ?: ""

                        val senderType =
                            document.getString("senderType")
                                ?: "staff"

                        val timestamp =
                            document.getTimestamp(
                                "timestamp"
                            )

                        ChatMessageData(
                            id = document.id,
                            message = message,
                            senderUid = senderUid,
                            senderType = senderType,
                            timestamp = timestamp
                        )
                    }

                onMessagesChanged(messages)
            }
    }
}


// ==========================================================
// EXTENSION: FIRESTORE DOCUMENT → PENGAJUAN DATA
// ==========================================================

private fun com.google.firebase.firestore.DocumentSnapshot.toPengajuanData():
        PengajuanData {

    val approvalStatusesRaw =
        get("approvalStatuses") as? Map<*, *>

    val approvalStatuses =
        approvalStatusesRaw
            ?.mapNotNull { entry ->

                val key =
                    entry.key as? String
                        ?: return@mapNotNull null

                val value =
                    entry.value as? String
                        ?: return@mapNotNull null

                key to value
            }
            ?.toMap()
            ?: emptyMap()

    val approvalChain =
        (get("approvalChain") as? List<*>)
            ?.mapNotNull {
                it as? String
            }
            ?: emptyList()

    return PengajuanData(

        id = id,

        nama =
            getString("nama")
                ?: "",

        jenis =
            getString("jenis")
                ?: "",

        tanggal =
            getString("tanggal")
                ?: "",

        jamPulang =
            getString("jamPulang")
                ?: "",

        jamKeluar =
            getString("jamKeluar")
                ?: "",

        jamKembali =
            getString("jamKembali")
                ?: "",

        tanggalMulai =
            getString("tanggalMulai")
                ?: "",

        tanggalSelesai =
            getString("tanggalSelesai")
                ?: "",

        alasan =
            getString("alasan")
                ?: "",

        status =
            getString("status")
                ?: "menunggu",

        approvalChain =
            approvalChain,

        approvalStatuses =
            approvalStatuses,

        currentApproverUid =
            getString("currentApproverUid")
                ?: "",

        currentApproverName =
            getString("currentApproverName")
                ?: "",

        currentApproverJabatan =
            getString("currentApproverJabatan")
                ?: "",

        approvalLocked =
            getBoolean("approvalLocked")
                ?: false
    )
}


// ==========================================================
// DATA APPROVAL PERSON
// ==========================================================

data class ApprovalPerson(
    val uid: String,
    val nama: String,
    val jabatan: String
)


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
    val status: String,

    val approvalChain: List<String> =
        emptyList(),

    val approvalStatuses: Map<String, String> =
        emptyMap(),

    val currentApproverUid: String =
        "",

    val currentApproverName: String =
        "",

    val currentApproverJabatan: String =
        "",

    val approvalLocked: Boolean =
        false
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


// ==========================================================
// DATA REKAP ABSENSI
// ==========================================================

data class RekapAbsensiData(
    val id: String,
    val uid: String,
    val nama: String,
    val tanggal: String,
    val jamMasuk: String,
    val jamPulang: String,
    val status: String,
    val qrData: String,
    val kantor: String
)


// ==========================================================
// DATA CHAT
// ==========================================================

data class ChatMessageData(
    val id: String,
    val message: String,
    val senderUid: String,
    val senderType: String,
    val timestamp: Timestamp?
)