package com.example.absensikaryawan.repository

import com.example.absensikaryawan.models.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    private val notificationsCollection =
        db.collection("notifications")

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

    fun listenNotifications(
        userId: String,
        onNotificationsChanged: (List<Notification>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {

        return notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onNotificationsChanged(emptyList())
                    return@addSnapshotListener
                }

                val notifications =
                    snapshot.documents.mapNotNull { document ->
                        document.toObject(Notification::class.java)
                    }

                onNotificationsChanged(notifications)
            }
    }

    fun markAsRead(
        notificationId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        notificationsCollection
            .document(notificationId)
            .update("isRead", true)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun markAllAsRead(
        userId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        notificationsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->

                val batch = db.batch()

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