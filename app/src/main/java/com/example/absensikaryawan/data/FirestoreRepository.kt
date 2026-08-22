package com.example.absensikaryawan.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    private val absensiCollection =
        db.collection("absensi")

    suspend fun simpanAbsenMasuk(
        nama: String,
        tanggal: String,
        jamMasuk: String,
        qrData: String,
        catatan: String = ""
    ) {

        val data = hashMapOf(
            "nama" to nama,
            "tanggal" to tanggal,
            "jamMasuk" to jamMasuk,
            "jamPulang" to "",
            "qrData" to qrData,
            "catatan" to catatan
        )

        absensiCollection
            .add(data)
            .await()
    }

    suspend fun simpanAbsenPulang(
        documentId: String,
        jamPulang: String
    ) {

        absensiCollection
            .document(documentId)
            .update(
                "jamPulang",
                jamPulang
            )
            .await()
    }
}