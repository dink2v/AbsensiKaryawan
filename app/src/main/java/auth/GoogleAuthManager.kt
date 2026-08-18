package com.example.absensikaryawan.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.absensikaryawan.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
    private val context: Context
) {

    private val auth = FirebaseAuth.getInstance()

    private val credentialManager =
        CredentialManager.create(context)

    suspend fun signIn(): Result<Boolean> {

        return try {

            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        context.getString(
                            R.string.default_web_client_id
                        )
                    )
                    .setAutoSelectEnabled(false)
                    .build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            val result =
                credentialManager.getCredential(
                    context = context,
                    request = request
                )

            val googleCredential =
                GoogleIdTokenCredential
                    .createFrom(result.credential.data)

            val firebaseCredential =
                GoogleAuthProvider.getCredential(
                    googleCredential.idToken,
                    null
                )

            auth.signInWithCredential(firebaseCredential)
                .await()

            Result.success(true)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}