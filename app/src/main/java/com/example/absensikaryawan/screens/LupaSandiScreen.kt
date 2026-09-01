package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LupaSandiScreen(
    onBack: () -> Unit
) {

    // ==========================================================
    // STATE
    // ==========================================================

    var email by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    val scope =
        rememberCoroutineScope()

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }


    // ==========================================================
    // UI
    // ==========================================================

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            Background
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier =
                    Modifier.height(40.dp)
            )


            // ==================================================
            // HEADER
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(

                    modifier =
                        Modifier
                            .clickable {
                                onBack()
                            }
                            .padding(4.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(24.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    Text(

                        text =
                            "Kembali",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            PrimaryGreen
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(35.dp)
            )


            // ==================================================
            // ICON
            // ==================================================

            Card(

                shape =
                    RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            DarkGreen
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            5.dp
                    )
            ) {

                Icon(

                    imageVector =
                        Icons.Default.LockReset,

                    contentDescription =
                        "Reset Password",

                    tint =
                        Color.White,

                    modifier =
                        Modifier
                            .padding(20.dp)
                            .size(48.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            Text(

                text =
                    "Lupa Sandi?",

                fontSize =
                    27.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            Text(

                text =
                    "Masukkan email yang terdaftar untuk menerima link reset password.",

                modifier =
                    Modifier.fillMaxWidth(),

                fontSize =
                    13.sp,

                color =
                    TextGray,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // ==================================================
            // CARD RESET
            // ==================================================

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
                            3.dp
                    )
            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                ) {

                    Text(

                        text =
                            "Reset Password",

                        fontSize =
                            20.sp,

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
                            "Link untuk membuat password baru akan dikirim ke email kamu.",

                        fontSize =
                            12.sp,

                        color =
                            TextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    // ==================================================
                    // EMAIL
                    // ==================================================

                    OutlinedTextField(

                        value =
                            email,

                        onValueChange = { value ->

                            email =
                                value

                            errorMessage =
                                ""

                            successMessage =
                                ""
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {

                            Text(
                                "Email"
                            )
                        },

                        leadingIcon = {

                            Icon(

                                imageVector =
                                    Icons.Default.Email,

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen
                            )
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                13.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    // ==================================================
                    // KIRIM BUTTON
                    // ==================================================

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(

                                    color =
                                        DarkGreen,

                                    shape =
                                        RoundedCornerShape(
                                            13.dp
                                        )
                                )
                                .clickable(

                                    enabled =
                                        !isLoading

                                ) {

                                    if (
                                        email.isBlank()
                                    ) {

                                        errorMessage =
                                            "Email wajib diisi."

                                        return@clickable
                                    }


                                    if (
                                        !android.util.Patterns.EMAIL_ADDRESS
                                            .matcher(
                                                email.trim()
                                            )
                                            .matches()
                                    ) {

                                        errorMessage =
                                            "Format email tidak valid."

                                        return@clickable
                                    }


                                    scope.launch {

                                        isLoading =
                                            true

                                        errorMessage =
                                            ""

                                        successMessage =
                                            ""

                                        try {

                                            // ==================================================
                                            // FIREBASE PASSWORD RESET
                                            // ==================================================

                                            auth
                                                .sendPasswordResetEmail(
                                                    email.trim()
                                                )
                                                .await()


                                            successMessage =
                                                "Link reset password telah dikirim ke email kamu. Silakan cek inbox atau folder spam."


                                        } catch (
                                            e: Exception
                                        ) {

                                            errorMessage =
                                                when {

                                                    e.message
                                                        ?.contains(
                                                            "badly formatted",
                                                            ignoreCase =
                                                                true
                                                        ) == true ->

                                                        "Format email tidak valid."


                                                    e.message
                                                        ?.contains(
                                                            "user-not-found",
                                                            ignoreCase =
                                                                true
                                                        ) == true ->

                                                        "Email belum terdaftar di Firebase Authentication."


                                                    e.message
                                                        ?.contains(
                                                            "no user record",
                                                            ignoreCase =
                                                                true
                                                        ) == true ->

                                                        "Email belum terdaftar di Firebase Authentication."


                                                    else ->

                                                        e.message
                                                            ?: "Gagal mengirim link reset password."
                                                }

                                        } finally {

                                            isLoading =
                                                false
                                        }
                                    }
                                }
                                .padding(

                                    horizontal =
                                        16.dp,

                                    vertical =
                                        15.dp
                                ),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Send,

                            contentDescription =
                                null,

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(19.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )


                        Text(

                            text =
                                if (
                                    isLoading
                                ) {
                                    "Mengirim..."
                                } else {
                                    "Kirim Link Reset"
                                },

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )
                    }
                }
            }


            // ==================================================
            // SUCCESS
            // ==================================================

            if (
                successMessage.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =
                                Color(0xFFE8F5E9)
                        )
                ) {

                    Text(

                        text =
                            successMessage,

                        modifier =
                            Modifier.padding(
                                14.dp
                            ),

                        textAlign =
                            TextAlign.Center,

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            Color(0xFF2E7D32)
                    )
                }
            }


            // ==================================================
            // ERROR
            // ==================================================

            if (
                errorMessage.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =
                                Color(0xFFFFF0F0)
                        )
                ) {

                    Text(

                        text =
                            errorMessage,

                        modifier =
                            Modifier.padding(
                                14.dp
                            ),

                        textAlign =
                            TextAlign.Center,

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            Color(0xFFC62828)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            // ==================================================
            // INFO
            // ==================================================

            Text(

                text =
                    "Pastikan kamu memiliki akses ke email tersebut.",

                fontSize =
                    11.sp,

                color =
                    TextGray,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    "Absensi Karyawan • Versi 1.0",

                fontSize =
                    11.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
        }
    }
}