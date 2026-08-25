package com.example.absensikaryawan.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.ui.platform.LocalContext

import androidx.core.content.ContextCompat

import androidx.lifecycle.compose.LocalLifecycleOwner

import com.example.absensikaryawan.data.AbsensiDataStore
import com.example.absensikaryawan.data.AbsensiHistoryDataStore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean


@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanAbsenScreen(
    onBack: () -> Unit,
    onQrScanned: (String, String) -> Unit
) {

    // ==========================================================
    // CONTEXT
    // ==========================================================

    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current

    // ==========================================================
    // FIREBASE
    // ==========================================================

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    // ==========================================================
    // DATASTORE
    // ==========================================================

    val absensiDataStore =
        remember {
            AbsensiDataStore(context)
        }

    val historyDataStore =
        remember {
            AbsensiHistoryDataStore(context)
        }

    // ==========================================================
    // COROUTINE
    // ==========================================================

    val scope =
        rememberCoroutineScope()

    // ==========================================================
    // STATE
    // ==========================================================

    var qrData by remember {
        mutableStateOf("")
    }

    var catatan by remember {
        mutableStateOf("")
    }

    var sudahScan by remember {
        mutableStateOf(false)
    }

    var sedangKirim by remember {
        mutableStateOf(false)
    }

    var kameraDiizinkan by remember {
        mutableStateOf(false)
    }

    // ==========================================================
    // ANTI DOUBLE SCAN
    // ==========================================================

    /*
     * Lock ini berbeda dengan state Compose.
     *
     * AtomicBoolean digunakan karena callback ML Kit
     * dapat berjalan sangat cepat dan beberapa frame QR
     * bisa terdeteksi hampir bersamaan.
     *
     * false = scanner masih boleh menerima QR
     * true  = scanner sudah mengunci QR
     */

    val scanLock =
        remember {
            AtomicBoolean(false)
        }

    // ==========================================================
    // CAMERA PERMISSION
    // ==========================================================

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            kameraDiizinkan =
                granted

            Log.d(
                "SCAN_DEBUG",
                "IZIN KAMERA = $granted"
            )
        }

    LaunchedEffect(Unit) {

        kameraDiizinkan =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (!kameraDiizinkan) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
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
                Modifier.fillMaxSize()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Kembali",

                        tint =
                            PrimaryGreen
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text =
                        "Scan QR Absen",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark
                )
            }

            // ==================================================
            // CAMERA
            // ==================================================

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .padding(16.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                if (
                    kameraDiizinkan &&
                    !sudahScan
                ) {

                    AndroidView(

                        modifier =
                            Modifier.fillMaxSize(),

                        factory = { ctx ->

                            val previewView =
                                PreviewView(ctx)

                            val cameraProviderFuture =
                                ProcessCameraProvider
                                    .getInstance(ctx)

                            cameraProviderFuture.addListener(

                                {

                                    try {

                                        val cameraProvider =
                                            cameraProviderFuture
                                                .get()

                                        // ==================================
                                        // PREVIEW
                                        // ==================================

                                        val preview =
                                            Preview.Builder()
                                                .build()

                                        preview.setSurfaceProvider(
                                            previewView.surfaceProvider
                                        )

                                        // ==================================
                                        // QR SCANNER
                                        // ==================================

                                        val scanner =
                                            BarcodeScanning
                                                .getClient()

                                        // ==================================
                                        // IMAGE ANALYSIS
                                        // ==================================

                                        val imageAnalysis =
                                            ImageAnalysis.Builder()
                                                .setBackpressureStrategy(
                                                    ImageAnalysis
                                                        .STRATEGY_KEEP_ONLY_LATEST
                                                )
                                                .build()

                                        imageAnalysis.setAnalyzer(

                                            ContextCompat
                                                .getMainExecutor(ctx)

                                        ) { imageProxy ->

                                            val mediaImage =
                                                imageProxy.image

                                            if (
                                                mediaImage == null
                                            ) {

                                                imageProxy.close()

                                                return@setAnalyzer
                                            }

                                            val image =
                                                InputImage.fromMediaImage(
                                                    mediaImage,
                                                    imageProxy
                                                        .imageInfo
                                                        .rotationDegrees
                                                )

                                            scanner
                                                .process(image)
                                                .addOnSuccessListener {

                                                        barcodes ->

                                                    // ==================================
                                                    // CEK STATE UI
                                                    // ==================================

                                                    if (sudahScan) {

                                                        return@addOnSuccessListener
                                                    }

                                                    // ==================================
                                                    // CEK LOCK
                                                    // ==================================

                                                    if (
                                                        scanLock.get()
                                                    ) {

                                                        Log.d(
                                                            "SCAN_DEBUG",
                                                            "SCAN DIABAIKAN - LOCK AKTIF"
                                                        )

                                                        return@addOnSuccessListener
                                                    }

                                                    // ==================================
                                                    // CARI QR
                                                    // ==================================

                                                    for (
                                                    barcode
                                                    in barcodes
                                                    ) {

                                                        val value =
                                                            barcode.rawValue

                                                        if (
                                                            !value
                                                                .isNullOrBlank()
                                                        ) {

                                                            // ==================================
                                                            // LOCK SECEPAT MUNGKIN
                                                            // ==================================

                                                            val berhasilLock =
                                                                scanLock.compareAndSet(
                                                                    false,
                                                                    true
                                                                )

                                                            if (
                                                                !berhasilLock
                                                            ) {

                                                                Log.d(
                                                                    "SCAN_DEBUG",
                                                                    "QR DIABAIKAN - SUDAH ADA SCAN"
                                                                )

                                                                break
                                                            }

                                                            // ==================================
                                                            // QR BERHASIL TERKUNCI
                                                            // ==================================

                                                            Log.d(
                                                                "SCAN_DEBUG",
                                                                "================================"
                                                            )

                                                            Log.d(
                                                                "SCAN_DEBUG",
                                                                "QR TERBACA"
                                                            )

                                                            Log.d(
                                                                "SCAN_DEBUG",
                                                                "QR DATA = $value"
                                                            )

                                                            Log.d(
                                                                "SCAN_DEBUG",
                                                                "ANTI DOUBLE SCAN = LOCK"
                                                            )

                                                            Log.d(
                                                                "SCAN_DEBUG",
                                                                "================================"
                                                            )

                                                            qrData =
                                                                value

                                                            sudahScan =
                                                                true

                                                            break
                                                        }
                                                    }
                                                }

                                                .addOnFailureListener {

                                                        error ->

                                                    Log.e(
                                                        "SCAN_DEBUG",
                                                        "GAGAL SCAN QR",
                                                        error
                                                    )
                                                }

                                                .addOnCompleteListener {

                                                    imageProxy.close()
                                                }
                                        }

                                        // ==================================
                                        // CAMERA
                                        // ==================================

                                        cameraProvider.unbindAll()

                                        cameraProvider.bindToLifecycle(

                                            lifecycleOwner,

                                            CameraSelector
                                                .DEFAULT_BACK_CAMERA,

                                            preview,

                                            imageAnalysis
                                        )

                                        Log.d(
                                            "SCAN_DEBUG",
                                            "CAMERA BERHASIL DIBUKA"
                                        )

                                    } catch (
                                        e: Exception
                                    ) {

                                        Log.e(
                                            "SCAN_DEBUG",
                                            "GAGAL MEMBUKA CAMERA",
                                            e
                                        )
                                    }

                                },

                                ContextCompat
                                    .getMainExecutor(ctx)
                            )

                            previewView
                        }
                    )

                } else {

                    // ==================================================
                    // HASIL SCAN
                    // ==================================================

                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color.White
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(

                                imageVector =
                                    if (sudahScan) {

                                        Icons.Default.CheckCircle

                                    } else {

                                        Icons.Default.QrCodeScanner
                                    },

                                contentDescription =
                                    null,

                                tint =
                                    PrimaryGreen,

                                modifier =
                                    Modifier.size(55.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Text(

                                text =
                                    if (sudahScan) {

                                        "QR Berhasil Dibaca"

                                    } else {

                                        "Kamera Membutuhkan Izin"
                                    },

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextDark
                            )

                            if (sudahScan) {

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(

                                    text =
                                        qrData,

                                    fontSize =
                                        13.sp,

                                    color =
                                        TextGray
                                )
                            }
                        }
                    }
                }
            }

            // ==================================================
            // CATATAN
            // ==================================================

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        )
            ) {

                Text(

                    text =
                        "Catatan (opsional)",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                OutlinedTextField(

                    value =
                        catatan,

                    onValueChange = {
                        catatan = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    placeholder = {

                        Text(
                            text =
                                "Tambahkan catatan..."
                        )
                    },

                    maxLines =
                        3,

                    shape =
                        RoundedCornerShape(12.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ==================================================
            // BUTTON SIMPAN
            // ==================================================

            Button(

                onClick = {

                    if (
                        sudahScan &&
                        qrData.isNotBlank() &&
                        !sedangKirim
                    ) {

                        sedangKirim =
                            true

                        val tanggal =
                            SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(Date())

                        val jam =
                            SimpleDateFormat(
                                "HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())

                        scope.launch {

                            try {

                                // ==================================
                                // CEK USER LOGIN
                                // ==================================

                                val currentUser =
                                    auth.currentUser

                                if (
                                    currentUser == null
                                ) {

                                    Log.e(
                                        "SCAN_DEBUG",
                                        "USER BELUM LOGIN"
                                    )

                                    sedangKirim =
                                        false

                                    // BUKA LOCK KEMBALI
                                    scanLock.set(false)

                                    return@launch
                                }

                                val uid =
                                    currentUser.uid

                                // ==================================
                                // SIMPAN ABSENSI UTAMA
                                // ==================================

                                absensiDataStore.simpanAbsen(

                                    jam =
                                        jam,

                                    tanggal =
                                        tanggal,

                                    qrData =
                                        qrData,

                                    catatan =
                                        catatan
                                )

                                // ==================================
                                // SIMPAN HISTORY LOCAL
                                // ==================================

                                historyDataStore
                                    .simpanAbsenMasuk(

                                        tanggal =
                                            tanggal,

                                        jamMasuk =
                                            jam,

                                        qrData =
                                            qrData,

                                        catatan =
                                            catatan
                                    )

                                // ==================================
                                // DATA FIRESTORE
                                // ==================================

                                val data =
                                    hashMapOf(

                                        "uid" to uid,

                                        "tanggal" to tanggal,

                                        "jamMasuk" to jam,

                                        "jamPulang" to "",

                                        "qrData" to qrData,

                                        "catatan" to catatan
                                    )

                                // ==================================
                                // SIMPAN KE ATTENDANCE
                                // ==================================

                                db.collection("attendance")
                                    .add(data)
                                    .await()

                                Log.d(
                                    "SCAN_DEBUG",
                                    "================================"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "ABSEN BERHASIL DISIMPAN"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "FIRESTORE BERHASIL"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "ANTI DOUBLE SCAN = AKTIF"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "UID = $uid"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "TANGGAL = $tanggal"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "JAM MASUK = $jam"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "QR = $qrData"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "CATATAN = $catatan"
                                )

                                Log.d(
                                    "SCAN_DEBUG",
                                    "================================"
                                )

                                // ==================================
                                // LANJUT KE HALAMAN BERHASIL
                                // ==================================

                                onQrScanned(
                                    qrData,
                                    catatan
                                )

                            } catch (
                                e: Exception
                            ) {

                                Log.e(
                                    "SCAN_DEBUG",
                                    "================================"
                                )

                                Log.e(
                                    "SCAN_DEBUG",
                                    "GAGAL MENYIMPAN ABSEN",
                                    e
                                )

                                Log.e(
                                    "SCAN_DEBUG",
                                    "================================"
                                )

                                // ==================================
                                // BUKA LOCK JIKA GAGAL
                                // ==================================

                                scanLock.set(false)

                                sedangKirim =
                                    false
                            }
                        }
                    }
                },

                enabled =
                    sudahScan &&
                            qrData.isNotBlank() &&
                            !sedangKirim,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    )
            ) {

                Icon(

                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(

                    text =
                        if (sedangKirim) {

                            "Menyimpan..."

                        } else {

                            "Simpan Absen"
                        },

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}