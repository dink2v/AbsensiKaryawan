const { setGlobalOptions } = require("firebase-functions");
const { onCall, HttpsError } = require("firebase-functions/v2/https");

const { initializeApp } = require("firebase-admin/app");
const { getAuth } = require("firebase-admin/auth");
const { getFirestore } = require("firebase-admin/firestore");

initializeApp();

setGlobalOptions({
    maxInstances: 10
});


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