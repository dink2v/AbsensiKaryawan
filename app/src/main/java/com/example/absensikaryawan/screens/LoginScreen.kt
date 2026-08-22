package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onStaffLogin: () -> Unit,
    onAdminLogin: () -> Unit
) {

    // =========================
    // STATE
    // =========================

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    val userRepository = remember {
        UserRepository()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(55.dp)
            )

            // =========================
            // LOGO
            // =========================

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(48.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Absensi Karyawan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Sistem Presensi Karyawan",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            // =========================
            // LOGIN CARD
            // =========================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "Masuk ke Akun",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Gunakan akun yang telah didaftarkan Admin.",
                        fontSize = 13.sp,
                        color = TextGray
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // =========================
                    // EMAIL
                    // =========================

                    OutlinedTextField(
                        value = email,
                        onValueChange = { value ->
                            email = value
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Email")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // =========================
                    // PASSWORD
                    // =========================

                    OutlinedTextField(
                        value = password,
                        onValueChange = { value ->
                            password = value
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Password")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                        },
                        visualTransformation =
                            PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    // =========================
                    // LOGIN BUTTON
                    // =========================

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = DarkGreen,
                                shape = RoundedCornerShape(13.dp)
                            )
                            .clickable(
                                enabled = !isLoading
                            ) {

                                if (
                                    email.isBlank() ||
                                    password.isBlank()
                                ) {

                                    errorMessage =
                                        "Email dan password wajib diisi."

                                    return@clickable
                                }

                                scope.launch {

                                    isLoading = true
                                    errorMessage = ""

                                    try {

                                        // =========================
                                        // FIREBASE AUTH
                                        // =========================

                                        FirebaseAuth
                                            .getInstance()
                                            .signInWithEmailAndPassword(
                                                email.trim(),
                                                password
                                            )
                                            .await()

                                        // =========================
                                        // USER FIREBASE
                                        // =========================

                                        val currentUser =
                                            FirebaseAuth
                                                .getInstance()
                                                .currentUser

                                        if (currentUser == null) {

                                            errorMessage =
                                                "Login gagal. User Firebase tidak ditemukan."

                                            return@launch
                                        }

                                        // =========================
                                        // EMAIL FIREBASE
                                        // =========================

                                        val userEmail =
                                            currentUser.email

                                        if (userEmail.isNullOrEmpty()) {

                                            errorMessage =
                                                "Email akun Firebase tidak ditemukan."

                                            FirebaseAuth
                                                .getInstance()
                                                .signOut()

                                            return@launch
                                        }

                                        // =========================
                                        // PROFILE FIRESTORE
                                        // =========================

                                        val userProfile =
                                            userRepository
                                                .getUserByEmail(
                                                    userEmail
                                                )

                                        if (userProfile == null) {

                                            errorMessage =
                                                "Email $userEmail belum terdaftar di data users Firebase."

                                            FirebaseAuth
                                                .getInstance()
                                                .signOut()

                                            return@launch
                                        }

                                        // =========================
                                        // ROLE
                                        // =========================

                                        if (userProfile.isAdmin) {

                                            onAdminLogin()

                                        } else {

                                            onStaffLogin()
                                        }

                                    } catch (e: Exception) {

                                        errorMessage =
                                            when {
                                                e.message
                                                    ?.contains(
                                                        "password",
                                                        ignoreCase = true
                                                    ) == true ->
                                                    "Password salah."

                                                e.message
                                                    ?.contains(
                                                        "no user record",
                                                        ignoreCase = true
                                                    ) == true ->
                                                    "Email belum terdaftar di Firebase Authentication."

                                                e.message
                                                    ?.contains(
                                                        "badly formatted",
                                                        ignoreCase = true
                                                    ) == true ->
                                                    "Format email tidak valid."

                                                else ->
                                                    e.message
                                                        ?: "Login gagal."
                                            }

                                    } finally {

                                        isLoading = false
                                    }
                                }
                            }
                            .padding(
                                horizontal = 16.dp,
                                vertical = 15.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                if (isLoading) {
                                    "Memproses..."
                                } else {
                                    "Masuk"
                                },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    // =========================
                    // INFO LOGIN
                    // =========================

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Akun dibuat dan dikelola oleh Admin / HRD.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }

            // =========================
            // ERROR
            // =========================

            if (errorMessage.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF0F0)
                    )
                ) {

                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFC62828)
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // =========================
            // FOOTER
            // =========================

            Text(
                text = "Belum memiliki akun?",
                fontSize = 12.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Hubungi Admin / HRD untuk mendapatkan akses.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Absensi Karyawan • Versi 1.0",
                fontSize = 11.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}