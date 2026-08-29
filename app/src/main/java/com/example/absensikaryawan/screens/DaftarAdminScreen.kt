package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope


// ==========================================================
// DAFTAR KARYAWAN / STAFF
// ==========================================================

@Composable
fun DaftarKaryawanScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {

    var nama by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var jabatan by remember {
        mutableStateOf("")
    }

    var divisi by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var konfirmasiPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var konfirmasiVisible by remember {
        mutableStateOf(false)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scope =
        rememberCoroutineScope()


    // ======================================================
    // UI
    // ======================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .statusBarsPadding()
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Kembali",

                    tint =
                        PrimaryGreen
                )
            }

            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )

            Icon(
                imageVector =
                    Icons.Default.Group,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(26.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Column {

                Text(
                    text =
                        "Tambah Karyawan",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )

                Text(
                    text =
                        "Daftarkan akun staff",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }
        }


        // ==================================================
        // FORM
        // ==================================================

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    )
        ) {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                ) {

                    // ======================================
                    // JUDUL
                    // ======================================

                    Text(
                        text =
                            "Informasi Karyawan",

                        fontSize =
                            17.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Isi data karyawan yang akan didaftarkan.",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    // ======================================
                    // NAMA
                    // ======================================

                    KaryawanTextField(
                        value =
                            nama,

                        onValueChange = {
                            nama = it
                            errorMessage = ""
                        },

                        label =
                            "Nama Lengkap",

                        placeholder =
                            "Masukkan nama lengkap",

                        icon =
                            Icons.Default.Person
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // ======================================
                    // EMAIL
                    // ======================================

                    KaryawanTextField(
                        value =
                            email,

                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },

                        label =
                            "Email",

                        placeholder =
                            "Masukkan email karyawan",

                        icon =
                            Icons.Default.Email
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // ======================================
                    // JABATAN
                    // ======================================

                    KaryawanTextField(
                        value =
                            jabatan,

                        onValueChange = {
                            jabatan = it
                            errorMessage = ""
                        },

                        label =
                            "Jabatan",

                        placeholder =
                            "Contoh: Staff",

                        icon =
                            Icons.Default.Badge
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // ======================================
                    // DIVISI
                    // ======================================

                    KaryawanTextField(
                        value =
                            divisi,

                        onValueChange = {
                            divisi = it
                            errorMessage = ""
                        },

                        label =
                            "Divisi",

                        placeholder =
                            "Contoh: IT",

                        icon =
                            Icons.Default.Group
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // ======================================
                    // PASSWORD
                    // ======================================

                    OutlinedTextField(
                        value =
                            password,

                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(14.dp),

                        label = {
                            Text(
                                text =
                                    "Password"
                            )
                        },

                        placeholder = {
                            Text(
                                text =
                                    "Minimal 6 karakter"
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen
                            )
                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    passwordVisible =
                                        !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            passwordVisible
                                        ) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },

                                    contentDescription =
                                        null,

                                    tint =
                                        TextGray
                                )
                            }
                        },

                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                unfocusedBorderColor =
                                    Color(0xFFD1D5DB),

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // ======================================
                    // KONFIRMASI PASSWORD
                    // ======================================

                    OutlinedTextField(
                        value =
                            konfirmasiPassword,

                        onValueChange = {
                            konfirmasiPassword = it
                            errorMessage = ""
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(14.dp),

                        label = {
                            Text(
                                text =
                                    "Konfirmasi Password"
                            )
                        },

                        placeholder = {
                            Text(
                                text =
                                    "Ulangi password"
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen
                            )
                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    konfirmasiVisible =
                                        !konfirmasiVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            konfirmasiVisible
                                        ) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },

                                    contentDescription =
                                        null,

                                    tint =
                                        TextGray
                                )
                            }
                        },

                        visualTransformation =
                            if (konfirmasiVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    PrimaryGreen,

                                unfocusedBorderColor =
                                    Color(0xFFD1D5DB),

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White
                            )
                    )


                    // ======================================
                    // ERROR
                    // ======================================

                    if (
                        errorMessage.isNotBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                errorMessage,

                            fontSize =
                                12.sp,

                            color =
                                Color(0xFFB91C1C)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )


                    // ======================================
                    // BUTTON
                    // ======================================

                    Button(
                        onClick = {

                            // ==============================
                            // VALIDASI
                            // ==============================

                            if (
                                nama.isBlank() ||
                                email.isBlank() ||
                                jabatan.isBlank() ||
                                divisi.isBlank() ||
                                password.isBlank() ||
                                konfirmasiPassword.isBlank()
                            ) {

                                errorMessage =
                                    "Semua data wajib diisi."

                                return@Button
                            }


                            if (
                                password.length < 6
                            ) {

                                errorMessage =
                                    "Password minimal 6 karakter."

                                return@Button
                            }


                            if (
                                password !=
                                konfirmasiPassword
                            ) {

                                errorMessage =
                                    "Konfirmasi password tidak sama."

                                return@Button
                            }


                            scope.launch {

                                loading = true
                                errorMessage = ""

                                var secondaryAuth:
                                        FirebaseAuth? = null

                                try {

                                    // ==================================
                                    // FIREBASE APP UTAMA
                                    // ==================================

                                    val mainApp =
                                        FirebaseApp
                                            .getInstance()


                                    // ==================================
                                    // AUTH INSTANCE KEDUA
                                    // ==================================

                                    val secondaryAppName =
                                        "StaffRegistrationApp"


                                    val secondaryApp =
                                        try {

                                            FirebaseApp
                                                .getInstance(
                                                    secondaryAppName
                                                )

                                        } catch (
                                            e: Exception
                                        ) {

                                            FirebaseApp
                                                .initializeApp(
                                                    mainApp.applicationContext,
                                                    mainApp.options,
                                                    secondaryAppName
                                                )!!
                                        }


                                    secondaryAuth =
                                        FirebaseAuth
                                            .getInstance(
                                                secondaryApp
                                            )


                                    // ==================================
                                    // BUAT AKUN AUTH
                                    // ==================================

                                    val result =
                                        secondaryAuth
                                            .createUserWithEmailAndPassword(
                                                email.trim(),
                                                password
                                            )
                                            .await()


                                    val newUser =
                                        result.user


                                    if (
                                        newUser == null
                                    ) {

                                        throw Exception(
                                            "Akun gagal dibuat."
                                        )
                                    }


                                    val uid =
                                        newUser.uid


                                    // ==================================
                                    // SIMPAN DATA USERS
                                    // ==================================

                                    val data =
                                        hashMapOf<String, Any>(
                                            "uid" to uid,
                                            "nama" to nama.trim(),
                                            "email" to email.trim(),
                                            "jabatan" to jabatan.trim(),
                                            "divisi" to divisi.trim(),
                                            "usernameTele" to "",
                                            "isAdmin" to false
                                        )


                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .set(data)
                                        .await()


                                    // ==================================
                                    // LOGOUT AUTH KEDUA
                                    // ==================================

                                    secondaryAuth.signOut()


                                    loading = false

                                    onSuccess()

                                } catch (
                                    e: FirebaseAuthUserCollisionException
                                ) {

                                    loading = false

                                    errorMessage =
                                        "Email tersebut sudah terdaftar."

                                } catch (
                                    e: Exception
                                ) {

                                    loading = false

                                    errorMessage =
                                        e.message
                                            ?: "Gagal mendaftarkan karyawan."
                                }
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(52.dp),

                        enabled =
                            !loading,

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PrimaryGreen
                            )
                    ) {

                        if (loading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(22.dp),

                                color =
                                    Color.White,

                                strokeWidth =
                                    2.dp
                            )

                        } else {

                            Text(
                                text =
                                    "Daftarkan Karyawan",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }
}


// ==========================================================
// TEXT FIELD
// ==========================================================

@Composable
private fun KaryawanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {

    OutlinedTextField(
        value =
            value,

        onValueChange =
            onValueChange,

        modifier =
            Modifier.fillMaxWidth(),

        singleLine =
            true,

        shape =
            RoundedCornerShape(14.dp),

        label = {
            Text(
                text =
                    label
            )
        },

        placeholder = {
            Text(
                text =
                    placeholder
            )
        },

        leadingIcon = {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen
            )
        },

        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor =
                    PrimaryGreen,

                unfocusedBorderColor =
                    Color(0xFFD1D5DB),

                focusedContainerColor =
                    Color.White,

                unfocusedContainerColor =
                    Color.White
            )
    )
}