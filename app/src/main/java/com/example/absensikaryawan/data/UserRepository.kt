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

            val user =
                FirebaseAuth
                    .getInstance()
                    .currentUser

            if (user != null) {

                Result.success(user)

            } else {

                Result.failure(
                    Exception("User belum login")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
    suspend fun getCurrentUserName(): Result<String> {

        return try {

            val currentUser =
                FirebaseAuth
                    .getInstance()
                    .currentUser

            if (currentUser == null) {

                Result.failure(
                    Exception("User belum login")
                )

            } else {

                val email =
                    currentUser.email

                if (email.isNullOrEmpty()) {

                    Result.failure(
                        Exception(
                            "Email user tidak ditemukan"
                        )
                    )

                } else {

                    val result =
                        firestore
                            .collection("users")
                            .whereEqualTo(
                                "email",
                                email
                            )
                            .limit(1)
                            .get()
                            .await()

                    if (result.isEmpty) {

                        Result.failure(
                            Exception(
                                "Data user tidak ditemukan"
                            )
                        )

                    } else {

                        val nama =
                            result
                                .documents
                                .first()
                                .getString("nama")
                                ?: ""

                        Result.success(nama)
                    }
                }
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==========================================
    // AMBIL PROFILE USER
    // ==========================================

    suspend fun getUserByEmail(
        email: String
    ): UserProfile? {

        return try {

            val result =
                firestore
                    .collection("users")
                    .whereEqualTo(
                        "email",
                        email
                    )
                    .limit(1)
                    .get()
                    .await()

            if (result.isEmpty) {

                null

            } else {

                val document =
                    result.documents.first()

                UserProfile(

                    nama =
                        document.getString("nama")
                            ?: "",

                    email =
                        document.getString("email")
                            ?: email,

                    divisi =
                        document.getString("divisi")
                            ?: "",

                    jabatan =
                        document.getString("jabatan")
                            ?: "",

                    usernameTele =
                        document.getString("usernameTele")
                            ?: "",

                    isAdmin =
                        document.getBoolean("isAdmin")
                            ?: false
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }
}