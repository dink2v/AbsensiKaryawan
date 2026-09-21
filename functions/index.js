const { setGlobalOptions } = require("firebase-functions");
const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");

const { initializeApp } = require("firebase-admin/app");
const { getAuth } = require("firebase-admin/auth");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();

setGlobalOptions({
    maxInstances: 10
});


// =====================================================
// HAPUS AKUN KARYAWAN
// =====================================================

exports.hapusAkunKaryawan = onCall(async (request) => {

    // =====================================================
    // CEK LOGIN ADMIN
    // =====================================================

    if (!request.auth) {
        throw new HttpsError(
            "unauthenticated",
            "Anda harus login terlebih dahulu."
        );
    }

    const adminUid = request.auth.uid;

    const db = getFirestore();
    const auth = getAuth();


    // =====================================================
    // CEK DATA ADMIN
    // =====================================================

    const adminRef = db
        .collection("users")
        .doc(adminUid);

    const adminSnapshot = await adminRef.get();

    if (!adminSnapshot.exists) {
        throw new HttpsError(
            "permission-denied",
            "Data admin tidak ditemukan."
        );
    }

    const adminData = adminSnapshot.data();

    if (adminData.isAdmin !== true) {
        throw new HttpsError(
            "permission-denied",
            "Hanya admin yang dapat menghapus akun karyawan."
        );
    }


    // =====================================================
    // AMBIL UID KARYAWAN
    // =====================================================

    const targetUid = request.data?.uid;

    if (
        typeof targetUid !== "string" ||
        targetUid.trim() === ""
    ) {
        throw new HttpsError(
            "invalid-argument",
            "UID karyawan tidak valid."
        );
    }


    // =====================================================
    // CEGAH ADMIN MENGHAPUS DIRINYA SENDIRI
    // =====================================================

    if (targetUid === adminUid) {
        throw new HttpsError(
            "failed-precondition",
            "Admin tidak dapat menghapus akun sendiri."
        );
    }


    // =====================================================
    // CEK DATA USER DI FIRESTORE
    // =====================================================

    const userRef = db
        .collection("users")
        .doc(targetUid);

    const userSnapshot = await userRef.get();

    if (!userSnapshot.exists) {
        throw new HttpsError(
            "not-found",
            "Data karyawan tidak ditemukan di Firestore."
        );
    }


    // =====================================================
    // CEK FIREBASE AUTHENTICATION
    // =====================================================

    let authUserExists = true;

    try {

        await auth.getUser(targetUid);

    } catch (error) {

        if (error.code === "auth/user-not-found") {

            authUserExists = false;

        } else {

            console.error(
                "Gagal mengecek Firebase Authentication:",
                error
            );

            throw new HttpsError(
                "internal",
                "Gagal memeriksa akun Firebase Authentication."
            );
        }
    }


    // =====================================================
    // HAPUS FIREBASE AUTHENTICATION
    // =====================================================

    if (authUserExists) {

        try {

            await auth.deleteUser(targetUid);

            console.log(
                "Firebase Authentication berhasil dihapus:",
                targetUid
            );

        } catch (error) {

            console.error(
                "Gagal menghapus Firebase Authentication:",
                error
            );

            throw new HttpsError(
                "internal",
                "Gagal menghapus akun Firebase Authentication."
            );
        }
    }


    // =====================================================
    // HAPUS USERS/{UID} FIRESTORE
    // =====================================================

    try {

        await userRef.delete();

        console.log(
            "Dokumen users berhasil dihapus:",
            targetUid
        );

    } catch (error) {

        console.error(
            "Gagal menghapus dokumen Firestore:",
            error
        );

        throw new HttpsError(
            "internal",
            "Akun Authentication sudah dihapus, tetapi data Firestore gagal dihapus."
        );
    }


    // =====================================================
    // SELESAI
    // =====================================================

    return {
        success: true,
        uid: targetUid,
        message: "Akun berhasil dihapus."
    };
});


// =====================================================
// FCM PUSH NOTIFICATION
// =====================================================
//
// Trigger setiap ada dokumen baru:
//
// notifications/{notificationId}
//
// =====================================================

exports.kirimNotifikasiFCM = onDocumentCreated(
    "notifications/{notificationId}",
    async (event) => {

        // =================================================
        // AMBIL SNAPSHOT
        // =================================================

        const snapshot = event.data;

        if (!snapshot) {
            console.log(
                "Snapshot notification tidak ditemukan."
            );
            return;
        }


        // =================================================
        // AMBIL DATA NOTIFICATION
        // =================================================

        const notificationData = snapshot.data();

        const userId = notificationData.userId;


        // =================================================
        // CEK USER ID
        // =================================================

        if (
            typeof userId !== "string" ||
            userId.trim() === ""
        ) {
            console.log(
                "Notification tidak memiliki userId yang valid."
            );
            return;
        }


        const db = getFirestore();


        // =================================================
        // AMBIL USER
        // =================================================

        const userRef = db
            .collection("users")
            .doc(userId);

        const userSnapshot = await userRef.get();


        if (!userSnapshot.exists) {

            console.log(
                "User tidak ditemukan:",
                userId
            );

            return;
        }


        // =================================================
        // AMBIL FCM TOKEN
        // =================================================

        const userData = userSnapshot.data();

        const fcmToken = userData.fcmToken;


        if (
            typeof fcmToken !== "string" ||
            fcmToken.trim() === ""
        ) {

            console.log(
                "User belum memiliki FCM token:",
                userId
            );

            return;
        }


        // =================================================
        // DATA NOTIFIKASI
        // =================================================

        const title =
            typeof notificationData.title === "string" &&
            notificationData.title.trim() !== ""
                ? notificationData.title
                : "Absensi Karyawan";


        const message =
            typeof notificationData.message === "string" &&
            notificationData.message.trim() !== ""
                ? notificationData.message
                : "Ada aktivitas baru.";


        const type =
            typeof notificationData.type === "string"
                ? notificationData.type
                : "";


        const relatedId =
            typeof notificationData.relatedId === "string"
                ? notificationData.relatedId
                : "";


        // =================================================
        // KIRIM PUSH NOTIFICATION
        // =================================================

        try {

            const response = await getMessaging().send({

                token: fcmToken,

                notification: {
                    title: title,
                    body: message
                },

                data: {
                    title: title,
                    message: message,
                    type: type,
                    relatedId: relatedId
                },

                android: {
                    priority: "high",

                    notification: {
                        channelId:
                            "absensi_karyawan_notifications"
                    }
                }

            });


            console.log(
                "FCM berhasil dikirim:",
                response
            );


        } catch (error) {

            console.error(
                "Gagal mengirim FCM:",
                error
            );


            // =============================================
            // HAPUS TOKEN YANG SUDAH TIDAK VALID
            // =============================================

            const errorCode =
                error?.errorInfo?.code;


            if (
                errorCode ===
                    "messaging/registration-token-not-registered" ||
                errorCode ===
                    "messaging/invalid-registration-token"
            ) {

                try {

                    await userRef.update({
                        fcmToken: null
                    });


                    console.log(
                        "FCM token tidak valid dan sudah dihapus:",
                        userId
                    );

                } catch (deleteTokenError) {

                    console.error(
                        "Gagal menghapus FCM token:",
                        deleteTokenError
                    );
                }
            }
        }
    }
);