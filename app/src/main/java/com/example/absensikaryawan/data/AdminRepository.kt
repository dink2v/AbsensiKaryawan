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
                ?: return Result.failure(Exception("User belum login"))

            // ABSEN CONTROL access is independent from users.isAdmin,
            // which is used by the existing employee app login flow.
            val adminDocument = firestore
                .collection("controlAdmins")
                .document(currentUser.uid)
                .get()
                .await()

            if (!adminDocument.exists()) {
                return Result.failure(Exception("Akun tidak terdaftar sebagai admin ABSEN CONTROL"))
            }

            val userDocument = firestore
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()

            val profile = AdminProfile(
                nama = userDocument.getString("nama")
                    ?: currentUser.displayName
                    ?: "",
                divisi = userDocument.getString("divisi") ?: "",
                jabatan = userDocument.getString("jabatan") ?: ""
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
