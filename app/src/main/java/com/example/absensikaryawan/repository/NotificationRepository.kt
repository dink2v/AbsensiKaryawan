package com.example.absensikaryawan.repository

import android.util.Log
import com.example.absensikaryawan.models.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    private val notificationsCollection =
        db.collection("notifications")

    // ==========================================================
    // BUAT NOTIFIKASI
    // ==========================================================

    fun createNotification(
        notification: Notification,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = { exception ->
            Log.e(
                "NotificationRepository",
                "Gagal menyimpan notifikasi",
                exception
            )
        }
    ) {

        val document =
            notificationsCollection.document()

        val notificationWithId =
            notification.copy(
                id = document.id
            )

        document
            .set(
                mapOf(
                    "id" to notificationWithId.id,
                    "userId" to notificationWithId.userId,
                    "type" to notificationWithId.type,
                    "title" to notificationWithId.title,
                    "message" to notificationWithId.message,
                    "timestamp" to notificationWithId.timestamp,
                    "read" to notificationWithId.isRead,
                    "relatedId" to notificationWithId.relatedId
                )
            )
            .addOnSuccessListener {

                Log.i(
                    "NotificationRepository",
                    "Notifikasi tersimpan: " +
                            "userId=${notificationWithId.userId}, " +
                            "type=${notificationWithId.type}, " +
                            "id=${document.id}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

    // ==========================================================
    // LISTEN NOTIFIKASI REAL-TIME
    // ==========================================================

    fun listenNotifications(
        userId: String,
        onNotificationsChanged: (List<Notification>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {

        return notificationsCollection
            .whereEqualTo(
                "userId",
                userId
            )
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {

                    onError(exception)

                    return@addSnapshotListener
                }

                if (snapshot == null) {

                    onNotificationsChanged(
                        emptyList()
                    )

                    return@addSnapshotListener
                }

                val notifications =
                    snapshot.documents
                        .mapNotNull { document ->

                            try {

                                val timestampMillis =
                                    when (
                                        val timestamp =
                                            document.get("timestamp")
                                    ) {

                                        is Number ->
                                            timestamp.toLong()

                                        else ->
                                            document
                                                .getTimestamp(
                                                    "timestamp"
                                                )
                                                ?.toDate()
                                                ?.time
                                                ?: 0L
                                    }

                                val isRead =
                                    document.getBoolean("read")
                                        ?: document.getBoolean("isRead")
                                        ?: false

                                Notification(

                                    id =
                                        document.id,

                                    userId =
                                        document
                                            .getString("userId")
                                            .orEmpty(),

                                    type =
                                        document
                                            .getString("type")
                                            .orEmpty(),

                                    title =
                                        document
                                            .getString("title")
                                            .orEmpty(),

                                    message =
                                        document
                                            .getString("message")
                                            .orEmpty(),

                                    timestamp =
                                        timestampMillis,

                                    isRead =
                                        isRead,

                                    relatedId =
                                        document
                                            .getString("relatedId")
                                            .orEmpty()
                                )

                            } catch (
                                exception: Exception
                            ) {

                                Log.e(
                                    "NotificationRepository",
                                    "Gagal membaca notifikasi",
                                    exception
                                )

                                null
                            }
                        }
                        .sortedByDescending {
                            it.timestamp
                        }

                onNotificationsChanged(
                    notifications
                )
            }
    }

    // ==========================================================
    // TANDAI SATU NOTIFIKASI SUDAH DIBACA
    // ==========================================================

    fun markAsRead(
        notificationId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        notificationsCollection
            .document(notificationId)
            .update(
                "read",
                true
            )
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

    // ==========================================================
    // TANDAI SEMUA NOTIFIKASI SUDAH DIBACA
    // ==========================================================

    fun markAllAsRead(
        userId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        notificationsCollection
            .whereEqualTo(
                "userId",
                userId
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val batch =
                    db.batch()

                snapshot.documents.forEach { document ->

                    batch.update(
                        document.reference,
                        "read",
                        true
                    )
                }

                batch.commit()
                    .addOnSuccessListener {

                        onSuccess()
                    }
                    .addOnFailureListener { exception ->

                        onError(exception)
                    }
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

    // ==========================================================
    // HAPUS NOTIFIKASI
    // ==========================================================

    fun deleteNotification(
        notificationId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        notificationsCollection
            .document(notificationId)
            .delete()
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }
}