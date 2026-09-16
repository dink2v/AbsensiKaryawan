package com.example.absensikaryawan.repository

import com.example.absensikaryawan.models.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    private val notificationsCollection =
        db.collection("notifications")

    // ==========================================================
    // CREATE NOTIFICATION
    // ==========================================================

    fun createNotification(
        notification: Notification,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val document = notificationsCollection.document()

        val notificationWithId = notification.copy(
            id = document.id
        )

        document
            .set(notificationWithId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    // ==========================================================
    // LISTEN REALTIME NOTIFICATIONS
    // ==========================================================

    fun listenNotifications(
        userId: String,
        onNotificationsChanged: (List<Notification>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {

        return notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, exception ->

                // ==============================================
                // ERROR
                // ==============================================

                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }

                // ==============================================
                // SNAPSHOT KOSONG
                // ==============================================

                if (snapshot == null) {
                    onNotificationsChanged(emptyList())
                    return@addSnapshotListener
                }

                // ==============================================
                // CONVERT FIRESTORE -> MODEL
                // ==============================================

                val notifications =
                    snapshot.documents.mapNotNull { document ->

                        try {

                            // ----------------------------------
                            // TIMESTAMP FIRESTORE
                            // ----------------------------------

                            val firestoreTimestamp =
                                document.getTimestamp("timestamp")

                            val timestampMillis =
                                firestoreTimestamp
                                    ?.toDate()
                                    ?.time
                                    ?: 0L

                            // ----------------------------------
                            // MODEL NOTIFICATION
                            // ----------------------------------

                            Notification(
                                id =
                                    document.id,

                                userId =
                                    document.getString(
                                        "userId"
                                    ).orEmpty(),

                                type =
                                    document.getString(
                                        "type"
                                    ).orEmpty(),

                                title =
                                    document.getString(
                                        "title"
                                    ).orEmpty(),

                                message =
                                    document.getString(
                                        "message"
                                    ).orEmpty(),

                                timestamp =
                                    timestampMillis,

                                isRead =
                                    document.getBoolean(
                                        "isRead"
                                    ) ?: false,

                                relatedId =
                                    document.getString(
                                        "relatedId"
                                    ).orEmpty()
                            )

                        } catch (
                            exception: Exception
                        ) {

                            // Jika satu dokumen bermasalah,
                            // jangan sampai seluruh list gagal.

                            null
                        }
                    }

                // ==============================================
                // KIRIM HASIL
                // ==============================================

                onNotificationsChanged(
                    notifications
                )
            }
    }

    // ==========================================================
    // MARK AS READ
    // ==========================================================

    fun markAsRead(
        notificationId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        notificationsCollection
            .document(notificationId)
            .update(
                "isRead",
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
    // MARK ALL AS READ
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
            .whereEqualTo(
                "isRead",
                false
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val batch =
                    db.batch()

                snapshot.documents.forEach { document ->

                    batch.update(
                        document.reference,
                        "isRead",
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
    // DELETE NOTIFICATION
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