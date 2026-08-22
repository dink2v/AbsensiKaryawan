@file:OptIn(
    androidx.camera.core.ExperimentalGetImage::class
)

package com.example.absensikaryawan.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.absensikaryawan.data.AbsensiDataStore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


// ======================================================
// SCAN ABSEN SCREEN
// ======================================================

@Composable
fun ScanAbsenScreen(
    onBack: () -> Unit,
    onQrScanned: (String) -> Unit
) {

    val context = LocalContext.current

    // ==================================================
    // DATASTORE
    // ==================================================

    val absensiDataStore = remember {
        AbsensiDataStore(context)
    }

    val scope = rememberCoroutineScope()

    // ==================================================
    // CEK STATUS ABSEN
    // ==================================================

    val sudahAbsen by absensiDataStore
        .sudahAbsen
        .collectAsState(initial = false)

    // ==================================================
    // IZIN CAMERA
    // ==================================================

    var hasCameraPermission by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    // ==================================================
    // REQUEST CAMERA
    // ==================================================

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // ==================================================
    // CAMERA BELUM DIIZINKAN
    // ==================================================

    if (!hasCameraPermission) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),

            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "Izin kamera diperlukan",
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = {

                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                ) {

                    Text("Izinkan Kamera")
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = onBack
                ) {

                    Text("Kembali")
                }
            }
        }

        return
    }

    // ==================================================
    // QR SCANNER
    // ==================================================

    QRScannerCamera(

        onBack = onBack,

        modePulang = sudahAbsen,

        onQrScanned = { qrData ->

            // ==================================================
            // WAKTU SEKARANG
            // ==================================================

            val sekarang = Date()

            val jamSekarang =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(sekarang)

            val tanggalSekarang =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(sekarang)

            // ==================================================
            // SIMPAN ABSEN
            // ==================================================

            scope.launch {

                if (!sudahAbsen) {

                    // ==========================================
                    // ABSEN MASUK
                    // ==========================================

                    absensiDataStore.simpanAbsen(
                        jam = jamSekarang,
                        tanggal = tanggalSekarang,
                        qrData = qrData,
                        catatan = ""
                    )

                } else {

                    // ==========================================
                    // ABSEN PULANG
                    // ==========================================

                    absensiDataStore.simpanPulang(
                        jam = jamSekarang
                    )
                }

                // ==================================================
                // KIRIM HASIL QR
                // ==================================================

                onQrScanned(qrData)

                // ==================================================
                // KEMBALI
                // ==================================================

                onBack()
            }
        }
    )
}


// ======================================================
// QR SCANNER CAMERA
// ======================================================

@Composable
private fun QRScannerCamera(
    onBack: () -> Unit,
    modePulang: Boolean,
    onQrScanned: (String) -> Unit
) {

    val lifecycleOwner =
        LocalLifecycleOwner.current

    // ==================================================
    // CAMERA EXECUTOR
    // ==================================================

    val cameraExecutor = remember {

        Executors.newSingleThreadExecutor()
    }

    // ==================================================
    // BARCODE SCANNER
    // ==================================================

    val barcodeScanner = remember {

        BarcodeScanning.getClient()
    }

    // ==================================================
    // CEGAH SCAN BERULANG
    // ==================================================

    val qrSudahTerbaca =
        remember {

            AtomicBoolean(false)
        }

    // ==================================================
    // CLEANUP
    // ==================================================

    DisposableEffect(Unit) {

        onDispose {

            barcodeScanner.close()

            cameraExecutor.shutdown()
        }
    }

    // ==================================================
    // CAMERA CONTAINER
    // ==================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // CAMERA PREVIEW
        // ==================================================

        AndroidView(

            modifier = Modifier.fillMaxSize(),

            factory = { ctx ->

                val previewView =
                    PreviewView(ctx)

                // ==================================================
                // CAMERA PROVIDER
                // ==================================================

                val cameraProviderFuture =
                    ProcessCameraProvider
                        .getInstance(ctx)

                cameraProviderFuture.addListener({

                    try {

                        val cameraProvider =
                            cameraProviderFuture.get()

                        // ==================================================
                        // PREVIEW
                        // ==================================================

                        val preview =
                            Preview.Builder()
                                .build()
                                .also {

                                    it.surfaceProvider =
                                        previewView
                                            .surfaceProvider
                                }

                        // ==================================================
                        // IMAGE ANALYSIS
                        // ==================================================

                        val imageAnalyzer =
                            ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                    ImageAnalysis
                                        .STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build()

                        // ==================================================
                        // ANALYZER
                        // ==================================================

                        imageAnalyzer.setAnalyzer(

                            cameraExecutor

                        ) { imageProxy ->

                            // ==================================================
                            // QR SUDAH DITEMUKAN
                            // ==================================================

                            if (
                                qrSudahTerbaca.get()
                            ) {

                                imageProxy.close()

                                return@setAnalyzer
                            }

                            // ==================================================
                            // MEDIA IMAGE
                            // ==================================================

                            val mediaImage =
                                imageProxy.image

                            if (mediaImage == null) {

                                imageProxy.close()

                                return@setAnalyzer
                            }

                            // ==================================================
                            // INPUT IMAGE
                            // ==================================================

                            val image =
                                InputImage.fromMediaImage(

                                    mediaImage,

                                    imageProxy
                                        .imageInfo
                                        .rotationDegrees
                                )

                            // ==================================================
                            // SCAN QR
                            // ==================================================

                            barcodeScanner
                                .process(image)

                                .addOnSuccessListener {

                                        barcodes ->

                                    // ==================================================
                                    // CEK SUDAH SCAN
                                    // ==================================================

                                    if (
                                        qrSudahTerbaca.get()
                                    ) {

                                        return@addOnSuccessListener
                                    }

                                    // ==================================================
                                    // CARI QR
                                    // ==================================================

                                    for (
                                    barcode in barcodes
                                    ) {

                                        val rawValue =
                                            barcode.rawValue

                                        if (
                                            !rawValue
                                                .isNullOrEmpty()
                                        ) {

                                            // ==============================
                                            // KUNCI SCAN
                                            // ==============================

                                            if (
                                                qrSudahTerbaca
                                                    .compareAndSet(
                                                        false,
                                                        true
                                                    )
                                            ) {

                                                // ==============================
                                                // QR DITEMUKAN
                                                // ==============================

                                                onQrScanned(
                                                    rawValue
                                                )
                                            }

                                            break
                                        }
                                    }
                                }

                                .addOnFailureListener {

                                    // ==================================================
                                    // FRAME GAGAL DIBACA
                                    // ==================================================

                                    // Tidak melakukan apa-apa.
                                    // Scanner akan lanjut membaca frame berikutnya.
                                }

                                .addOnCompleteListener {

                                    // ==================================================
                                    // WAJIB CLOSE
                                    // ==================================================

                                    imageProxy.close()
                                }
                        }

                        // ==================================================
                        // CAMERA SELECTOR
                        // ==================================================

                        val cameraSelector =
                            CameraSelector
                                .DEFAULT_BACK_CAMERA

                        // ==================================================
                        // LEPAS CAMERA SEBELUMNYA
                        // ==================================================

                        cameraProvider.unbindAll()

                        // ==================================================
                        // HUBUNGKAN CAMERA
                        // ==================================================

                        cameraProvider.bindToLifecycle(

                            lifecycleOwner,

                            cameraSelector,

                            preview,

                            imageAnalyzer
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // ==================================================
        // HEADER
        // ==================================================

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ==================================================
            // BUTTON KEMBALI
            // ==================================================

            Button(
                onClick = onBack
            ) {

                Text("Kembali")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // ==================================================
            // JUDUL
            // ==================================================

            Text(

                text =
                    if (modePulang) {

                        "SCAN QR UNTUK ABSEN PULANG"

                    } else {

                        "SCAN QR UNTUK ABSEN MASUK"
                    },

                color = Color.White
            )
        }
    }
}