package com.example.absensikaryawan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AttendanceRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun simpanAbsenMasuk(
        jam: String,
        tanggal: String
    ): Result<Boolean> {

        return try {

            val currentUser =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User belum login")
                    )

            val uid = currentUser.uid

            val data = hashMapOf(
                "uid" to uid,
                "jamMasuk" to jam,
                "tanggal" to tanggal,
                "status" to "Hadir"
            )

            firestore
                .collection("attendance")
                .document("${uid}_$tanggal")
                .set(data)
                .await()

            Result.success(true)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}