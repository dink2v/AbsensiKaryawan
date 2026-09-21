package com.example.absensikaryawan.repository

import com.example.absensikaryawan.models.ChatMessage
import com.example.absensikaryawan.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class AdminChatRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val chatRoomsCollection =
        firestore.collection("chatRoomas")

    private val notificationRepository =
        NotificationRepository()


    // ============================================================
    // LIST CHAT STAFF
    // ============================================================

    fun listenChatRooms(
        onRoomsChanged: (List<ChatRoom>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        return chatRoomsCollection
            .orderBy(
                "updatedAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onRoomsChanged(emptyList())
                    return@addSnapshotListener
                }

                val rooms =
                    snapshot.documents.mapNotNull { document ->

                        val staffId =
                            document.getString("staffId")
                                ?: document.id

                        val staffName =
                            document.getString("staffName")
                                ?: "Staff"

                        val lastMessage =
                            document.getString("lastMessage")
                                ?: ""

                        val updatedAt =
                            document.getLong("updatedAt")
                                ?: 0L

                        ChatRoom(
                            staffId = staffId,
                            staffName = staffName,
                            lastMessage = lastMessage,
                            updatedAt = updatedAt
                        )
                    }

                onRoomsChanged(rooms)
            }
    }


    // ============================================================
    // LIST PESAN STAFF
    // ============================================================

    fun listenMessages(
        staffUid: String,
        onMessagesChanged: (List<ChatMessage>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        return chatRoomsCollection
            .document(staffUid)
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


    // ============================================================
    // ADMIN KIRIM PESAN
    // ============================================================

    fun sendMessage(
        staffUid: String,
        message: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val cleanMessage =
            message.trim()

        if (cleanMessage.isEmpty()) {

            onError(
                Exception("Pesan tidak boleh kosong.")
            )

            return
        }

        val currentUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (currentUser == null) {

            onError(
                Exception("Admin belum login.")
            )

            return
        }

        val adminUid =
            currentUser.uid

        val adminName =
            currentUser.displayName
                ?: "ADMIN"

        val timestamp =
            System.currentTimeMillis()

        val chatRoomRef =
            chatRoomsCollection
                .document(staffUid)

        val messageRef =
            chatRoomRef
                .collection("messages")
                .document()

        val messageData =
            hashMapOf<String, Any>(

                "senderId" to adminUid,

                "senderName" to adminName,

                "senderType" to "ADMIN",

                "message" to cleanMessage,

                "timestamp" to timestamp
            )

        val roomData =
            hashMapOf<String, Any>(

                "lastMessage" to cleanMessage,

                "updatedAt" to timestamp
            )

        firestore
            .runBatch { batch ->

                batch.set(
                    messageRef,
                    messageData
                )

                batch.set(
                    chatRoomRef,
                    roomData,
                    SetOptions.merge()
                )
            }
            .addOnSuccessListener {

                // =================================================
                // CHAT BERHASIL
                // KIRIM NOTIFIKASI KE STAFF
                // =================================================

                notifyStaffNewMessage(
                    staffUid = staffUid,
                    adminName = adminName,
                    message = cleanMessage,
                    relatedId = staffUid
                )

                // Notifikasi tidak boleh membuat chat dianggap gagal.
                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }


    // ============================================================
    // NOTIFIKASI PESAN ADMIN KE STAFF
    // ============================================================

    private fun notifyStaffNewMessage(
        staffUid: String,
        adminName: String,
        message: String,
        relatedId: String
    ) {

        val notification =
            Notification(
                userId = staffUid,

                type = "CHAT",

                title = "Pesan Baru dari Admin",

                message = "$adminName: $message",

                timestamp =
                    System.currentTimeMillis(),

                isRead = false,

                relatedId = relatedId
            )

        notificationRepository.createNotification(
            notification = notification
        )
    }
}


// ================================================================
// CHAT ROOM MODEL
// ================================================================

data class ChatRoom(

    val staffId: String = "",

    val staffName: String = "",

    val lastMessage: String = "",

    val updatedAt: Long = 0L
)