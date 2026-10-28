package com.example.absensikaryawan.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    // ==========================================
    // AMBIL NAMA USER YANG SEDANG LOGIN
    // ==========================================

    fun getCurrentUser():
            Result<com.google.firebase.auth.FirebaseUser> {

        return try {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User belum login"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserName(): Result<String> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Result.failure(Exception("User belum login"))
            } else {
                val email = currentUser.email

                if (email.isNullOrEmpty()) {
                    Result.failure(Exception("Email user tidak ditemukan"))
                } else {
                    val result = firestore
                        .collection("users")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .await()

                    if (result.isEmpty) {
                        Result.failure(Exception("Data user tidak ditemukan"))
                    } else {
                        val nama = result.documents.first().getString("nama") ?: ""
                        Result.success(nama)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // AMBIL PROFILE USER / ADMIN ABSEN CONTROL
    // ==========================================

    suspend fun getUserByEmail(
        email: String,
        uid: String = ""
    ): UserProfile? {
        // Admin Absensi Control disimpan pada controlAdmins/{uid},
        // bukan selalu pada koleksi users. Cek profil admin lebih dulu
        // agar akun admin yang sama dapat dipakai di kedua aplikasi.
        if (uid.isNotBlank()) {
            try {
                val adminDocument = firestore
                    .collection("controlAdmins")
                    .document(uid)
                    .get()
                    .await()

                val role = adminDocument.getString("role")
                    ?.trim()
                    ?.lowercase()
                    ?: ""
                val isActive = adminDocument.getBoolean("active") == true

                if (
                    adminDocument.exists() &&
                    isActive &&
                    role in setOf("admin", "manager", "owner")
                ) {
                    val data = adminDocument.data.orEmpty()

                    return UserProfile(
                        uid = uid,
                        nama = data["nama"] as? String
                            ?: data["name"] as? String
                            ?: data["displayName"] as? String
                            ?: "",
                        email = data["email"] as? String ?: email,
                        divisi = data["divisi"] as? String ?: "",
                        jabatan = role.replaceFirstChar { it.uppercase() },
                        usernameTele = data["usernameTele"] as? String ?: "",
                        isAdmin = true,
                        atasan = "",
                        owner = ""
                    )
                }
            } catch (_: Exception) {
                // Jika aturan Firestore tidak mengizinkan membaca controlAdmins,
                // lanjutkan pemeriksaan profil karyawan seperti sebelumnya.
            }
        }

        return try {
            val result = firestore
                .collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (result.isEmpty) {
                null
            } else {
                val document = result.documents.first()

                UserProfile(
                    uid = document.id,
                    nama = document.getString("nama") ?: "",
                    email = document.getString("email") ?: email,
                    divisi = document.getString("divisi") ?: "",
                    jabatan = document.getString("jabatan") ?: "",
                    usernameTele = document.getString("usernameTele") ?: "",
                    isAdmin = document.getBoolean("isAdmin") ?: false,
                    atasan = document.getString("atasan") ?: "",
                    owner = document.getString("owner") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
