package com.example.absensikaryawan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AdminProfile(
    val nama: String = "",
    val divisi: String = "",
    val jabatan: String = ""
)

class AdminRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentAdmin(): Result<AdminProfile> {
        return try {

            val currentUser = auth.currentUser
                ?: return Result.failure(
                    Exception("User belum login")
                )

            val document = firestore
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(
                    Exception("Data Admin belum ditemukan")
                )
            }

            val isAdmin =
                document.getBoolean("isAdmin") ?: false

            if (!isAdmin) {
                return Result.failure(
                    Exception("Akun bukan Admin")
                )
            }

            val profile = AdminProfile(
                nama = document.getString("nama") ?: "",
                divisi = document.getString("divisi") ?: "",
                jabatan = document.getString("jabatan") ?: ""
            )

            Result.success(profile)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}