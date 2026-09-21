package com.example.absensikaryawan.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.example.absensikaryawan.MainActivity
import com.example.absensikaryawan.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        private const val CHANNEL_ID =
            "absensi_karyawan_notifications"

        private const val CHANNEL_NAME =
            "Notifikasi Absensi"

        private const val CHANNEL_DESCRIPTION =
            "Notifikasi aktivitas aplikasi Absensi Karyawan"

        private const val NOTIFICATION_ID =
            1001
    }


    // ==========================================================
    // FCM TOKEN
    // ==========================================================

    override fun onNewToken(token: String) {

        super.onNewToken(token)

        saveTokenToFirestore(token)
    }


    // ==========================================================
    // SIMPAN TOKEN FCM KE FIRESTORE
    // ==========================================================

    private fun saveTokenToFirestore(
        token: String
    ) {

        val firebaseUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (firebaseUser == null) {

            return
        }

        val uid =
            firebaseUser.uid

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .update(
                "fcmToken",
                token
            )
            .addOnSuccessListener {

                // Token berhasil disimpan.
            }
            .addOnFailureListener {

                // Tidak mengganggu aplikasi jika
                // penyimpanan token gagal.
            }
    }


    // ==========================================================
    // MENERIMA PESAN FCM
    // ==========================================================

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(
            remoteMessage
        )


        val title =
            remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: "Absensi Karyawan"


        val message =
            remoteMessage.notification?.body
                ?: remoteMessage.data["message"]
                ?: "Ada aktivitas baru."


        showNotification(
            title = title,
            message = message
        )
    }


    // ==========================================================
    // MENAMPILKAN NOTIFIKASI ANDROID
    // ==========================================================

    private fun showNotification(
        title: String,
        message: String
    ) {

        createNotificationChannel()


        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
            }


        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )

                // ==================================================
                // ICON
                // ==================================================

                .setSmallIcon(
                    R.mipmap.ic_launcher
                )

                // ==================================================
                // TITLE
                // ==================================================

                .setContentTitle(
                    title
                )

                // ==================================================
                // MESSAGE
                // ==================================================

                .setContentText(
                    message
                )

                // ==================================================
                // PESAN PANJANG
                // ==================================================

                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(
                            message
                        )
                )

                // ==================================================
                // PRIORITY
                // ==================================================

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                // ==================================================
                // AUTO CLOSE
                // ==================================================

                .setAutoCancel(
                    true
                )

                // ==================================================
                // KETIKA NOTIFIKASI DIKLIK
                // ==================================================

                .setContentIntent(
                    pendingIntent
                )

                .build()


        // ==========================================================
        // CEK IZIN NOTIFIKASI
        // ==========================================================

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.TIRAMISU ||

            NotificationManagerCompat
                .from(this)
                .areNotificationsEnabled()
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    NOTIFICATION_ID,
                    notification
                )
        }
    }


    // ==========================================================
    // NOTIFICATION CHANNEL
    // ==========================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        CHANNEL_DESCRIPTION

                    enableVibration(
                        true
                    )
                }


            val notificationManager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager


            notificationManager
                .createNotificationChannel(
                    channel
                )
        }
    }
}
