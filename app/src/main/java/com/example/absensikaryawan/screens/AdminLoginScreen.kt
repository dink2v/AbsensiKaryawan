package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminLoginScreen(
    onBack: () -> Unit
) {

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login Admin",
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Username")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                // Login admin akan dibuat pada tahap berikutnya
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Masuk"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextButton(
            onClick = onBack
        ) {
            Text(
                text = "Kembali"
            )
        }
    }
}