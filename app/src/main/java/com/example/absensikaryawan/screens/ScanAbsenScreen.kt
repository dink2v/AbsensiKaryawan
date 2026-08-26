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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.util.concurrent.atomic.AtomicBoolean


// ==========================================================
// SCAN ABSEN SCREEN
// ==========================================================

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanAbsenScreen(

    onBack: () -> Unit,

    onQrScanned: (
        String,
        String
    ) -> Unit

) {

    // ==========================================================
    // CONTEXT
    // ==========================================================

    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current


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
     * Lock ini memastikan QR yang sama tidak terbaca
     * berkali-kali dari frame kamera.
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


    // ==========================================================
    // CEK PERMISSION
    // ==========================================================

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

                    onClick =
                        onBack

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


                            // ==================================
                            // CAMERA PROVIDER
                            // ==================================

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

                                            previewView
                                                .surfaceProvider
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
                                            ImageAnalysis
                                                .Builder()
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


                                            // ==================================
                                            // INPUT IMAGE
                                            // ==================================

                                            val image =
                                                InputImage
                                                    .fromMediaImage(

                                                        mediaImage,

                                                        imageProxy
                                                            .imageInfo
                                                            .rotationDegrees
                                                    )


                                            // ==================================
                                            // PROSES QR
                                            // ==================================

                                            scanner
                                                .process(image)

                                                .addOnSuccessListener {

                                                        barcodes ->


                                                    // ==================================
                                                    // SUDAH SCAN
                                                    // ==================================

                                                    if (sudahScan) {

                                                        return@addOnSuccessListener
                                                    }


                                                    // ==================================
                                                    // LOCK
                                                    // ==================================

                                                    if (
                                                        scanLock.get()
                                                    ) {

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
                                                            // KUNCI SCAN
                                                            // ==================================

                                                            val berhasilLock =

                                                                scanLock
                                                                    .compareAndSet(

                                                                        false,

                                                                        true
                                                                    )


                                                            if (
                                                                !berhasilLock
                                                            ) {

                                                                break
                                                            }


                                                            // ==================================
                                                            // QR BERHASIL
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

                                                                "LOCK = AKTIF"
                                                            )

                                                            Log.d(

                                                                "SCAN_DEBUG",

                                                                "================================"
                                                            )


                                                            // ==================================
                                                            // SIMPAN HASIL SCAN KE UI
                                                            // ==================================

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


                            // ==================================
                            // ICON
                            // ==================================

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


                            // ==================================
                            // TITLE
                            // ==================================

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


                            // ==================================
                            // QR DATA
                            // ==================================

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

                        catatan =
                            it
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
            // BUTTON
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


                        scope.launch {

                            try {

                                // ==================================
                                // KIRIM QR KE APP NAVIGATION
                                // ==================================

                                Log.d(

                                    "SCAN_DEBUG",

                                    "KIRIM QR KE APP NAVIGATION"
                                )


                                Log.d(

                                    "SCAN_DEBUG",

                                    "QR = $qrData"
                                )


                                Log.d(

                                    "SCAN_DEBUG",

                                    "CATATAN = $catatan"
                                )


                                /*
                                 * PENTING:
                                 *
                                 * ScanAbsenScreen TIDAK menyimpan
                                 * Firestore.
                                 *
                                 * ScanAbsenScreen TIDAK menyimpan
                                 * DataStore.
                                 *
                                 * Semua keputusan:
                                 *
                                 * MASUK / PULANG
                                 *
                                 * dilakukan oleh AppNavigation.
                                 */

                                onQrScanned(

                                    qrData,

                                    catatan
                                )

                            } catch (
                                e: Exception
                            ) {

                                Log.e(

                                    "SCAN_DEBUG",

                                    "GAGAL MENGIRIM HASIL SCAN",

                                    e
                                )


                                // ==================================
                                // BUKA LOCK JIKA GAGAL
                                // ==================================

                                scanLock.set(false)

                                sudahScan =
                                    false

                                qrData =
                                    ""

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

                            "Memproses..."

                        } else {

                            "Konfirmasi Absen"
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