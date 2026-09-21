package com.example.absensikaryawan.repository

import com.example.absensikaryawan.models.ChatMessage
import com.example.absensikaryawan.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class ChatRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    private val chatRoomsCollection =
        firestore.collection("chatRooms")

    private val usersCollection =
        firestore.collection("users")

    private val notificationRepository =
        NotificationRepository()

    fun listenMessages(
        uid: String,
        onMessagesChanged: (List<ChatMessage>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        return chatRoomsCollection
            .document(uid)
            .collection("messages")
            .orderBy(
                "timestamp",
                Query.Direction.ASCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onMessagesChanged(emptyList())
                    return@addSnapshotListener
                }

                val messages =
                    snapshot.documents.mapNotNull { document ->

                        ChatMessage(
                            id = document.id,

                            senderId =
                                document.getString("senderId")
                                    ?: "",

                            senderName =
                                document.getString("senderName")
                                    ?: "",

                            senderType =
                                document.getString("senderType")
                                    ?: "",

                            message =
                                document.getString("message")
                                    ?: "",

                            timestamp =
                                document.getLong("timestamp")
                                    ?: 0L
                        )
                    }

                onMessagesChanged(messages)
            }
    }

    fun sendMessage(
        message: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            onError(
                Exception("User belum login.")
            )

            return
        }

        val uid =
            currentUser.uid

        val userName =
            currentUser.displayName
                ?: "Staff"

        val cleanMessage =
            message.trim()

        if (cleanMessage.isEmpty()) {

            onError(
                Exception("Pesan tidak boleh kosong.")
            )

            return
        }

        val chatRoomRef =
            chatRoomsCollection
                .document(uid)

        val messageRef =
            chatRoomRef
                .collection("messages")
                .document()

        val timestamp =
            System.currentTimeMillis()

        val messageData =
            hashMapOf<String, Any>(
                "senderId" to uid,
                "senderName" to userName,
                "senderType" to "STAFF",
                "message" to cleanMessage,
                "timestamp" to timestamp
            )

        val chatRoomData =
            hashMapOf<String, Any>(
                "staffId" to uid,
                "staffName" to userName,
                "lastMessage" to cleanMessage,
                "updatedAt" to timestamp
            )

        firestore
            .runBatch { batch ->

                batch.set(
                    chatRoomRef,
                    chatRoomData,
                    SetOptions.merge()
                )

                batch.set(
                    messageRef,
                    messageData
                )
            }
            .addOnSuccessListener {

                // Chat berhasil disimpan.
                // Selanjutnya kirim notifikasi ke semua Admin.
                notifyAdminsNewMessage(
                    staffId = uid,
                    staffName = userName,
                    message = cleanMessage,
                    relatedId = uid
                )

                // Notifikasi tidak boleh mengganggu keberhasilan chat.
                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

    /**
     * Mengirim notifikasi pesan baru kepada semua user Admin.
     */
    private fun notifyAdminsNewMessage(
        staffId: String,
        staffName: String,
        message: String,
        relatedId: String
    ) {

        usersCollection
            .whereEqualTo("isAdmin", true)
            .get()
            .addOnSuccessListener { snapshot ->

                snapshot.documents.forEach { adminDocument ->

                    val adminUid =
                        adminDocument.id

                    val notification =
                        Notification(
                            userId = adminUid,
                            type = "CHAT",
                            title = "Pesan Baru dari Staff",
                            message = "$staffName: $message",
                            timestamp = System.currentTimeMillis(),
                            isRead = false,
                            relatedId = relatedId
                        )

                    notificationRepository.createNotification(
                        notification = notification
                    )
                }
            }
            .addOnFailureListener {
                // Sengaja dikosongkan.
                // Kegagalan notifikasi tidak membatalkan chat.
            }
    }
}