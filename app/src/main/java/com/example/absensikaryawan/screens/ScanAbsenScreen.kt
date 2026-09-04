package com.example.absensikaryawan.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.CameraSelector
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.core.content.ContextCompat

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

import java.util.concurrent.atomic.AtomicBoolean

import kotlin.math.hypot


private const val TAG = "ScanAbsenScreen"


/*
 * ============================================================
 * KONFIGURASI SCANNER
 * ============================================================
 */

private const val SCANNER_FRAME_SIZE_DP = 280f

private const val QR_MIN_SIZE_DP = 45f

private const val QR_MAX_SIZE_RATIO = 0.90f

private const val QR_MIN_OVERLAP_RATIO = 0.80f

private const val QR_FRAME_TOLERANCE_DP = 12f


/*
 * ============================================================
 * MULTI FRAME VALIDATION
 * ============================================================
 */

private const val QR_REQUIRED_VALID_FRAMES = 3

private const val QR_MAX_TRACKING_DISTANCE_DP = 40f


/*
 * ============================================================
 * WARNA SCANNER
 * ============================================================
 */

private val ScannerGreen = Color(0xFF00E676)

private val ScannerGreenBright = Color(0xFF69F0AE)

private val ScannerGreenDark = Color(0xFF00C853)


@Composable
fun ScanAbsenScreen(
    onBack: () -> Unit,
    onQrScanned: (String, String) -> Unit,

    /*
     * Sekarang Absen Luar Kantor mengirimkan:
     *
     * String pertama  = Lokasi
     * String kedua    = Alasan / Keperluan
     */
    onAbsenLuarKantor: (String, String) -> Unit
) {

    val context = LocalContext.current


    /*
     * ========================================================
     * STATE ABSEN QR
     * ========================================================
     */

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


    /*
     * ========================================================
     * STATE ABSEN LUAR KANTOR
     * ========================================================
     */

    var showAbsenLuarKantorGuide by remember {
        mutableStateOf(false)
    }

    var showAbsenLuarKantorForm by remember {
        mutableStateOf(false)
    }

    /*
     * Lokasi diketik manual oleh user.
     */
    var lokasiLuarKantor by remember {
        mutableStateOf("")
    }

    /*
     * Alasan / keperluan diketik manual oleh user.
     */
    var alasanLuarKantor by remember {
        mutableStateOf("")
    }


    /*
     * ========================================================
     * VALIDASI QR
     * ========================================================
     *
     * 0 = belum ada QR
     * 1 = frame pertama
     * 2 = frame kedua
     * 3 = QR terkunci
     */

    var validationProgress by remember {
        mutableStateOf(0)
    }


    /*
     * ========================================================
     * PERMISSION KAMERA
     * ========================================================
     */

    var kameraDiizinkan by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    /*
     * ========================================================
     * QR OVERLAY
     * ========================================================
     */

    var qrBoundingBox by remember {
        mutableStateOf<Rect?>(null)
    }

    var qrCornerPoints by remember {
        mutableStateOf<List<Point>?>(null)
    }


    /*
     * ========================================================
     * SCANNER RESET KEY
     * ========================================================
     */

    var scannerResetKey by remember {
        mutableStateOf(0)
    }


    /*
     * ========================================================
     * SCANNER LOCK
     * ========================================================
     */

    val scanLock = remember {
        AtomicBoolean(false)
    }


    /*
     * ========================================================
     * PERMISSION LAUNCHER
     * ========================================================
     */

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            kameraDiizinkan = granted

            if (!granted) {

                Log.w(
                    TAG,
                    "Izin kamera ditolak"
                )
            }
        }


    /*
     * ========================================================
     * REQUEST CAMERA
     * ========================================================
     */

    LaunchedEffect(Unit) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }


    /*
     * ========================================================
     * RESET SCANNER
     * ========================================================
     */

    fun resetScanner() {

        qrData = ""

        qrBoundingBox = null

        qrCornerPoints = null

        sudahScan = false

        sedangKirim = false

        validationProgress = 0

        scanLock.set(false)

        scannerResetKey++

        Log.d(
            TAG,
            "Scanner di-reset"
        )
    }


    /*
     * ========================================================
     * MAIN SCREEN
     * ========================================================
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {


        /*
         * ====================================================
         * HEADER
         * ====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
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
                        "Kembali"
                )
            }

            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "Scan Kehadiran",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Arahkan kamera ke QR Code",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector =
                    Icons.Default.QrCodeScanner,

                contentDescription =
                    null,

                tint =
                    MaterialTheme.colorScheme.primary,

                modifier =
                    Modifier.size(30.dp)
            )
        }


        /*
         * ====================================================
         * INSTRUCTION
         * ====================================================
         */

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                ),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CameraAlt,

                    contentDescription =
                        null,

                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text =
                            "Posisikan QR di dalam kotak",

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            "Pastikan QR terlihat jelas dan tidak terlalu jauh.",

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        /*
         * ====================================================
         * CAMERA CARD
         * ====================================================
         */

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = 16.dp
                ),

            shape =
                RoundedCornerShape(20.dp),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize()
            ) {

                if (kameraDiizinkan) {

                    QRScannerCamera(
                        scanLock = scanLock,

                        sudahScan =
                            sudahScan,

                        qrBoundingBox =
                            qrBoundingBox,

                        qrCornerPoints =
                            qrCornerPoints,

                        scannerResetKey =
                            scannerResetKey,

                        onValidationProgress = {
                                progress ->

                            validationProgress =
                                progress
                        },

                        onQrDetected = {
                                value,
                                box,
                                points ->

                            qrData =
                                value

                            qrBoundingBox =
                                box

                            qrCornerPoints =
                                points

                            sudahScan =
                                true

                            validationProgress =
                                QR_REQUIRED_VALID_FRAMES

                            Log.d(
                                TAG,
                                "QR BERHASIL DIKUNCI: $value"
                            )
                        }
                    )

                } else {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Warning,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(50.dp),

                            tint =
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Kamera belum diizinkan",

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Izinkan akses kamera untuk melakukan scan QR.",

                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {

                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        ) {

                            Text(
                                text =
                                    "Izinkan Kamera"
                            )
                        }
                    }
                }


                /*
                 * =================================================
                 * STATUS PILL
                 * =================================================
                 */

                Surface(
                    modifier = Modifier
                        .align(
                            Alignment.TopCenter
                        )
                        .padding(
                            top = 12.dp
                        ),

                    shape =
                        RoundedCornerShape(50),

                    color =
                        if (sudahScan)
                            Color(0xFF1B5E20)
                        else
                            Color.Black.copy(
                                alpha = 0.65f
                            )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 7.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                if (sudahScan)
                                    Icons.Default.CheckCircle
                                else
                                    Icons.Default.QrCodeScanner,

                            contentDescription =
                                null,

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(17.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                when {

                                    sudahScan ->
                                        "QR TERKUNCI"

                                    validationProgress > 0 ->
                                        "MEMERIKSA QR $validationProgress/$QR_REQUIRED_VALID_FRAMES"

                                    else ->
                                        "MENCARI QR"
                                },

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold,

                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }
                }


                /*
                 * =================================================
                 * VALIDATION INDICATOR
                 * =================================================
                 */

                if (
                    !sudahScan &&
                    validationProgress > 0
                ) {

                    Surface(
                        modifier = Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                bottom = 16.dp
                            ),

                        shape =
                            RoundedCornerShape(50),

                        color =
                            Color.Black.copy(
                                alpha = 0.70f
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            repeat(
                                QR_REQUIRED_VALID_FRAMES
                            ) { index ->

                                val active =
                                    index <
                                            validationProgress

                                Box(
                                    modifier =
                                        Modifier
                                            .size(8.dp)
                                            .clip(
                                                RoundedCornerShape(50)
                                            )
                                            .background(
                                                if (active)
                                                    ScannerGreen
                                                else
                                                    Color.White.copy(
                                                        alpha = 0.35f
                                                    )
                                            )
                                )

                                if (
                                    index <
                                    QR_REQUIRED_VALID_FRAMES - 1
                                ) {

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    "Memastikan QR stabil",

                                color =
                                    Color.White,

                                style =
                                    MaterialTheme.typography.labelSmall,

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }
                }


                /*
                 * =================================================
                 * RESET BUTTON
                 * =================================================
                 */

                if (sudahScan) {

                    IconButton(
                        onClick = {
                            resetScanner()
                        },

                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(8.dp)
                            .clip(
                                RoundedCornerShape(50)
                            )
                            .background(
                                Color.Black.copy(
                                    alpha = 0.60f
                                )
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,

                            contentDescription =
                                "Scan ulang",

                            tint =
                                Color.White
                        )
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        /*
         * ====================================================
         * QR BERHASIL DIKUNCI
         * ====================================================
         */

        if (
            sudahScan &&
            qrData.isNotBlank()
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFE8F5E9)
                    ),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,

                        contentDescription =
                            null,

                        tint =
                            ScannerGreenDark,

                        modifier =
                            Modifier.size(28.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "QR berhasil dikunci",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF1B5E20)
                        )

                        Text(
                            text =
                                "QR sudah tervalidasi. Tekan Konfirmasi Absen untuk melanjutkan.",

                            style =
                                MaterialTheme.typography.bodySmall,

                            color =
                                Color(0xFF33691E)
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
        }


        /*
         * ====================================================
         * CATATAN
         * ====================================================
         */

        OutlinedTextField(
            value =
                catatan,

            onValueChange = {
                catatan = it
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),

            label = {
                Text(
                    "Catatan (opsional)"
                )
            },

            placeholder = {
                Text(
                    "Contoh: Hadir seperti biasa"
                )
            },

            maxLines = 3,

            shape =
                RoundedCornerShape(14.dp)
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        /*
         * ====================================================
         * BUTTON KONFIRMASI ABSEN
         * ====================================================
         */

        Button(
            onClick = {

                if (
                    qrData.isNotBlank() &&
                    !sedangKirim
                ) {

                    sedangKirim = true

                    onQrScanned(
                        qrData,
                        catatan
                    )
                }
            },

            enabled =
                sudahScan &&
                        qrData.isNotBlank() &&
                        !sedangKirim,

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                )
                .height(52.dp),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
        ) {

            if (sedangKirim) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(21.dp),

                    color =
                        Color.White,

                    strokeWidth = 2.dp
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Memproses..."
                )

            } else {

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
                        "Konfirmasi Absen"
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        /*
         * ====================================================
         * ABSEN LUAR KANTOR
         * ====================================================
         */

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                )
                .clickable {

                    /*
                     * Tampilkan panduan terlebih dahulu.
                     */
                    showAbsenLuarKantorGuide =
                        true
                },

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                ),

            shape =
                RoundedCornerShape(14.dp)
        ) {

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Absen Luar Kantor",

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Gunakan jika sedang bertugas di luar kantor.",

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector =
                        Icons.Default.Info,

                    contentDescription =
                        null,

                    tint =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )
    }


    /*
     * ========================================================
     * DIALOG PANDUAN ABSEN LUAR KANTOR
     * ========================================================
     */

    if (showAbsenLuarKantorGuide) {

        AbsenLuarKantorGuideDialog(

            onDismiss = {

                showAbsenLuarKantorGuide =
                    false
            },

            onContinue = {

                showAbsenLuarKantorGuide =
                    false

                /*
                 * Setelah panduan selesai,
                 * buka form pengisian.
                 */
                showAbsenLuarKantorForm =
                    true
            }
        )
    }


    /*
     * ========================================================
     * FORM ABSEN LUAR KANTOR
     * ========================================================
     */

    if (showAbsenLuarKantorForm) {

        AbsenLuarKantorFormDialog(

            lokasi =
                lokasiLuarKantor,

            alasan =
                alasanLuarKantor,

            onLokasiChange = {
                lokasiLuarKantor = it
            },

            onAlasanChange = {
                alasanLuarKantor = it
            },

            onDismiss = {

                showAbsenLuarKantorForm =
                    false
            },

            onSubmit = {

                /*
                 * Tutup form.
                 */
                showAbsenLuarKantorForm =
                    false

                /*
                 * Kirim lokasi dan alasan
                 * ke navigation / proses pengajuan.
                 */
                onAbsenLuarKantor(
                    lokasiLuarKantor.trim(),
                    alasanLuarKantor.trim()
                )
            }
        )
    }
}


/*
 * ============================================================
 * QR SCANNER CAMERA
 * ============================================================
 */

@Composable
private fun QRScannerCamera(
    scanLock: AtomicBoolean,
    sudahScan: Boolean,
    qrBoundingBox: Rect?,
    qrCornerPoints: List<Point>?,
    scannerResetKey: Int,

    onValidationProgress: (Int) -> Unit,

    onQrDetected: (
        String,
        Rect,
        List<Point>?
    ) -> Unit
) {

    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current


    /*
     * ========================================================
     * CAMERA CONTROLLER
     * ========================================================
     */

    val cameraController =
        remember(context) {

            LifecycleCameraController(
                context
            ).apply {

                cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA

                setEnabledUseCases(
                    CameraController.IMAGE_ANALYSIS
                )
            }
        }


    /*
     * ========================================================
     * PREVIEW VIEW
     * ========================================================
     */

    val previewView =
        remember(
            context,
            cameraController
        ) {

            PreviewView(context).apply {

                implementationMode =
                    PreviewView.ImplementationMode.COMPATIBLE

                scaleType =
                    PreviewView.ScaleType.FILL_CENTER

                controller =
                    cameraController
            }
        }


    /*
     * ========================================================
     * ML KIT QR SCANNER
     * ========================================================
     */

    val scanner =
        remember {

            val options =
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE
                    )
                    .build()

            BarcodeScanning.getClient(
                options
            )
        }


    /*
     * ========================================================
     * ANALYZER
     * ========================================================
     */

    DisposableEffect(
        cameraController,
        lifecycleOwner,
        previewView,
        scannerResetKey
    ) {

        /*
         * ====================================================
         * STATE VALIDASI MULTI FRAME
         * ====================================================
         */

        var validFrameCount =
            0

        var lastQrValue =
            ""

        var lastQrCenterX =
            Float.NaN

        var lastQrCenterY =
            Float.NaN


        /*
         * ====================================================
         * RESET VALIDATION
         * ====================================================
         */

        fun resetValidation() {

            validFrameCount =
                0

            lastQrValue =
                ""

            lastQrCenterX =
                Float.NaN

            lastQrCenterY =
                Float.NaN

            onValidationProgress(
                0
            )
        }


        /*
         * ====================================================
         * ML KIT ANALYZER
         * ====================================================
         */

        val analyzer =

            MlKitAnalyzer(
                listOf(scanner),

                CameraController
                    .COORDINATE_SYSTEM_VIEW_REFERENCED,

                ContextCompat.getMainExecutor(
                    context
                )

            ) { result ->

                /*
                 * Kalau sudah lock,
                 * jangan proses QR lagi.
                 */
                if (scanLock.get()) {

                    return@MlKitAnalyzer
                }


                /*
                 * Ambil barcode.
                 */
                val barcodes =
                    result.getValue(scanner)


                /*
                 * Tidak ada barcode.
                 */
                if (
                    barcodes.isNullOrEmpty()
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Cari QR yang memiliki value.
                 */
                val barcode =
                    barcodes.firstOrNull {

                        !it.rawValue
                            .isNullOrBlank()
                    }


                if (barcode == null) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Ambil value QR.
                 */
                val value =
                    barcode.rawValue


                if (value.isNullOrBlank()) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Bounding box.
                 */
                val boundingBox =
                    barcode.boundingBox


                if (boundingBox == null) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Ukuran PreviewView.
                 */
                val previewWidth =
                    previewView.width.toFloat()

                val previewHeight =
                    previewView.height.toFloat()


                if (
                    previewWidth <= 0f ||
                    previewHeight <= 0f
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Density.
                 */
                val density =
                    context.resources
                        .displayMetrics
                        .density


                /*
                 * =================================================
                 * SCANNER FRAME
                 * =================================================
                 */

                val scannerFrameSizePx =
                    SCANNER_FRAME_SIZE_DP *
                            density

                val tolerancePx =
                    QR_FRAME_TOLERANCE_DP *
                            density


                val scannerLeft =
                    (
                            previewWidth -
                                    scannerFrameSizePx
                            ) / 2f -
                            tolerancePx


                val scannerTop =
                    (
                            previewHeight -
                                    scannerFrameSizePx
                            ) / 2f -
                            tolerancePx


                val scannerRight =
                    (
                            previewWidth +
                                    scannerFrameSizePx
                            ) / 2f +
                            tolerancePx


                val scannerBottom =
                    (
                            previewHeight +
                                    scannerFrameSizePx
                            ) / 2f +
                            tolerancePx


                /*
                 * =================================================
                 * QR BOUNDING BOX
                 * =================================================
                 */

                val qrLeft =
                    boundingBox.left.toFloat()

                val qrTop =
                    boundingBox.top.toFloat()

                val qrRight =
                    boundingBox.right.toFloat()

                val qrBottom =
                    boundingBox.bottom.toFloat()


                val qrWidth =
                    boundingBox.width().toFloat()

                val qrHeight =
                    boundingBox.height().toFloat()


                /*
                 * =================================================
                 * VALIDASI UKURAN
                 * =================================================
                 */

                val minQrSizePx =
                    QR_MIN_SIZE_DP *
                            density


                val maxQrSizePx =
                    scannerFrameSizePx *
                            QR_MAX_SIZE_RATIO


                /*
                 * QR terlalu kecil.
                 */
                if (
                    qrWidth < minQrSizePx ||
                    qrHeight < minQrSizePx
                ) {

                    Log.d(
                        TAG,
                        "QR ditolak: terlalu kecil"
                    )

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * QR terlalu besar.
                 */
                if (
                    qrWidth > maxQrSizePx ||
                    qrHeight > maxQrSizePx
                ) {

                    Log.d(
                        TAG,
                        "QR ditolak: terlalu besar"
                    )

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * VALIDASI POSISI
                 * =================================================
                 */

                val qrCenterX =
                    (
                            qrLeft +
                                    qrRight
                            ) / 2f


                val qrCenterY =
                    (
                            qrTop +
                                    qrBottom
                            ) / 2f


                val centerInsideFrame =
                    qrCenterX >= scannerLeft &&
                            qrCenterX <= scannerRight &&
                            qrCenterY >= scannerTop &&
                            qrCenterY <= scannerBottom


                if (!centerInsideFrame) {

                    Log.d(
                        TAG,
                        "QR ditolak: center di luar frame"
                    )

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * OVERLAP
                 * =================================================
                 */

                val intersectionLeft =
                    maxOf(
                        qrLeft,
                        scannerLeft
                    )


                val intersectionTop =
                    maxOf(
                        qrTop,
                        scannerTop
                    )


                val intersectionRight =
                    minOf(
                        qrRight,
                        scannerRight
                    )


                val intersectionBottom =
                    minOf(
                        qrBottom,
                        scannerBottom
                    )


                val intersectionWidth =
                    maxOf(
                        0f,
                        intersectionRight -
                                intersectionLeft
                    )


                val intersectionHeight =
                    maxOf(
                        0f,
                        intersectionBottom -
                                intersectionTop
                    )


                val intersectionArea =
                    intersectionWidth *
                            intersectionHeight


                val qrArea =
                    maxOf(
                        1f,
                        qrWidth *
                                qrHeight
                    )


                val overlapRatio =
                    intersectionArea /
                            qrArea


                if (
                    overlapRatio <
                    QR_MIN_OVERLAP_RATIO
                ) {

                    Log.d(
                        TAG,
                        "QR ditolak: overlap = $overlapRatio"
                    )

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * MULTI FRAME VALIDATION
                 * =================================================
                 */

                val maxTrackingDistancePx =
                    QR_MAX_TRACKING_DISTANCE_DP *
                            density


                /*
                 * =================================================
                 * FRAME PERTAMA
                 * =================================================
                 */

                if (
                    validFrameCount == 0 ||
                    lastQrValue.isBlank()
                ) {

                    validFrameCount =
                        1

                    lastQrValue =
                        value

                    lastQrCenterX =
                        qrCenterX

                    lastQrCenterY =
                        qrCenterY

                    onValidationProgress(
                        validFrameCount
                    )

                    Log.d(
                        TAG,
                        "QR validation frame 1/$QR_REQUIRED_VALID_FRAMES"
                    )

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * QR YANG SAMA
                 * =================================================
                 */

                val sameQr =
                    value ==
                            lastQrValue


                /*
                 * =================================================
                 * JARAK PERPINDAHAN CENTER
                 * =================================================
                 */

                val centerDistance =
                    hypot(
                        qrCenterX -
                                lastQrCenterX,

                        qrCenterY -
                                lastQrCenterY
                    )


                /*
                 * =================================================
                 * POSISI STABIL
                 * =================================================
                 */

                val stablePosition =
                    centerDistance <=
                            maxTrackingDistancePx


                /*
                 * =================================================
                 * RESET JIKA TIDAK STABIL
                 * =================================================
                 */

                if (
                    !sameQr ||
                    !stablePosition
                ) {

                    validFrameCount =
                        1

                    lastQrValue =
                        value

                    lastQrCenterX =
                        qrCenterX

                    lastQrCenterY =
                        qrCenterY

                    onValidationProgress(
                        validFrameCount
                    )

                    Log.d(
                        TAG,
                        """
                        QR validation reset
                        sameQr=$sameQr
                        centerDistance=$centerDistance
                        maxDistance=$maxTrackingDistancePx
                        frame=1/$QR_REQUIRED_VALID_FRAMES
                        """.trimIndent()
                    )

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * FRAME VALID BERIKUTNYA
                 * =================================================
                 */

                validFrameCount++

                lastQrValue =
                    value

                lastQrCenterX =
                    qrCenterX

                lastQrCenterY =
                    qrCenterY

                onValidationProgress(
                    validFrameCount.coerceAtMost(
                        QR_REQUIRED_VALID_FRAMES
                    )
                )


                Log.d(
                    TAG,
                    """
                    QR validation
                    frame=$validFrameCount/$QR_REQUIRED_VALID_FRAMES
                    value=$value
                    centerDistance=$centerDistance
                    overlap=$overlapRatio
                    """.trimIndent()
                )


                /*
                 * Belum mencapai 3 frame.
                 */
                if (
                    validFrameCount <
                    QR_REQUIRED_VALID_FRAMES
                ) {

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * QR VALID / LOCK
                 * =================================================
                 */

                if (
                    !scanLock.compareAndSet(
                        false,
                        true
                    )
                ) {

                    return@MlKitAnalyzer
                }


                /*
                 * Corner points.
                 */
                val cornerPoints =
                    barcode.cornerPoints
                        ?.toList()


                Log.d(
                    TAG,
                    """
                    QR BERHASIL LOCK
                    value=$value
                    qrWidth=$qrWidth
                    qrHeight=$qrHeight
                    overlap=$overlapRatio
                    centerInside=$centerInsideFrame
                    validationFrames=$validFrameCount
                    """.trimIndent()
                )


                /*
                 * Kirim hasil.
                 */
                onQrDetected(
                    value,
                    boundingBox,
                    cornerPoints
                )
            }


        /*
         * ====================================================
         * PASANG ANALYZER
         * ====================================================
         */

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(
                context
            ),
            analyzer
        )


        /*
         * ====================================================
         * BIND CAMERA
         * ====================================================
         */

        cameraController.bindToLifecycle(
            lifecycleOwner
        )


        /*
         * ====================================================
         * DISPOSE
         * ====================================================
         */

        onDispose {

            Log.d(
                TAG,
                "Dispose QR scanner"
            )

            cameraController
                .clearImageAnalysisAnalyzer()

            cameraController.unbind()

            scanner.close()
        }
    }


    /*
     * ========================================================
     * CAMERA UI
     * ========================================================
     */

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        AndroidView(
            modifier =
                Modifier.fillMaxSize(),

            factory = {
                previewView
            },

            update = {

                it.controller =
                    cameraController
            }
        )


        /*
         * =================================================
         * SCANNER FRAME
         * =================================================
         */

        if (!sudahScan) {

            ScannerFrame(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )


            /*
             * Scanner line bergerak.
             */
            ScannerLine(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        /*
         * =================================================
         * QR DETECTION OVERLAY
         * =================================================
         */

        if (sudahScan) {

            QRDetectionOverlay(
                boundingBox =
                    qrBoundingBox,

                cornerPoints =
                    qrCornerPoints,

                modifier =
                    Modifier.fillMaxSize()
            )
        }
    }
}


/*
 * ============================================================
 * SCANNER FRAME
 * ============================================================
 */

@Composable
private fun ScannerFrame(
    modifier: Modifier = Modifier
) {

    val transition =
        rememberInfiniteTransition(
            label = "scannerFrame"
        )


    val cornerAlpha by
    transition.animateFloat(

        initialValue =
            0.70f,

        targetValue =
            1f,

        animationSpec =
            infiniteRepeatable(

                animation =
                    tween(
                        durationMillis = 1100,
                        easing =
                            LinearEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label =
            "cornerAlpha"
    )


    Box(
        modifier = modifier
            .size(
                SCANNER_FRAME_SIZE_DP.dp
            )
    ) {

        Box(
            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .size(52.dp)
                .alpha(cornerAlpha)
                .border(
                    width = 4.dp,
                    color =
                        ScannerGreen,
                    shape =
                        RoundedCornerShape(
                            topStart = 20.dp
                        )
                )
        )


        Box(
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .size(52.dp)
                .alpha(cornerAlpha)
                .border(
                    width = 4.dp,
                    color =
                        ScannerGreen,
                    shape =
                        RoundedCornerShape(
                            topEnd = 20.dp
                        )
                )
        )


        Box(
            modifier = Modifier
                .align(
                    Alignment.BottomStart
                )
                .size(52.dp)
                .alpha(cornerAlpha)
                .border(
                    width = 4.dp,
                    color =
                        ScannerGreen,
                    shape =
                        RoundedCornerShape(
                            bottomStart = 20.dp
                        )
                )
        )


        Box(
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .size(52.dp)
                .alpha(cornerAlpha)
                .border(
                    width = 4.dp,
                    color =
                        ScannerGreen,
                    shape =
                        RoundedCornerShape(
                            bottomEnd = 20.dp
                        )
                )
        )
    }
}


/*
 * ============================================================
 * SCANNER LINE
 * ============================================================
 */

@Composable
private fun ScannerLine(
    modifier: Modifier = Modifier
) {

    val transition =
        rememberInfiniteTransition(
            label = "scannerLine"
        )


    val position by
    transition.animateFloat(

        initialValue =
            -118f,

        targetValue =
            118f,

        animationSpec =
            infiniteRepeatable(

                animation =
                    tween(
                        durationMillis = 1800,
                        easing =
                            LinearEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label =
            "scannerPosition"
    )


    Box(
        modifier = modifier
            .size(
                SCANNER_FRAME_SIZE_DP.dp
            )
    ) {

        Box(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .offset(
                    y = position.dp
                )
                .width(250.dp)
                .height(8.dp)
                .clip(
                    RoundedCornerShape(50)
                )
                .background(
                    ScannerGreen.copy(
                        alpha = 0.20f
                    )
                )
        )


        Box(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .offset(
                    y = position.dp
                )
                .width(250.dp)
                .height(2.dp)
                .clip(
                    RoundedCornerShape(50)
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            ScannerGreenDark,
                            ScannerGreenBright,
                            ScannerGreenDark,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}


/*
 * ============================================================
 * QR DETECTION OVERLAY
 * ============================================================
 */

@Composable
private fun QRDetectionOverlay(
    boundingBox: Rect?,
    cornerPoints: List<Point>?,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        if (
            cornerPoints != null &&
            cornerPoints.size >= 4
        ) {

            val path =
                Path().apply {

                    moveTo(
                        cornerPoints[0].x.toFloat(),
                        cornerPoints[0].y.toFloat()
                    )

                    for (
                    i in 1 until cornerPoints.size
                    ) {

                        lineTo(
                            cornerPoints[i].x.toFloat(),
                            cornerPoints[i].y.toFloat()
                        )
                    }

                    close()
                }


            drawPath(
                path = path,

                color =
                    ScannerGreen,

                style =
                    Stroke(
                        width = 5f,
                        cap =
                            StrokeCap.Round
                    )
            )


            cornerPoints.forEach { point ->

                drawCircle(
                    color =
                        ScannerGreen,

                    radius =
                        8f,

                    center =
                        androidx.compose.ui.geometry.Offset(
                            point.x.toFloat(),
                            point.y.toFloat()
                        )
                )
            }

        } else if (
            boundingBox != null
        ) {

            drawRect(

                color =
                    ScannerGreen,

                topLeft =
                    androidx.compose.ui.geometry.Offset(
                        boundingBox.left.toFloat(),
                        boundingBox.top.toFloat()
                    ),

                size =
                    androidx.compose.ui.geometry.Size(
                        boundingBox.width().toFloat(),
                        boundingBox.height().toFloat()
                    ),

                style =
                    Stroke(
                        width = 5f
                    )
            )
        }
    }
}


/*
 * ============================================================
 * PANDUAN ABSEN LUAR KANTOR
 * ============================================================
 */

@Composable
private fun AbsenLuarKantorGuideDialog(
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {

    AlertDialog(

        onDismissRequest =
            onDismiss,

        icon = {

            Icon(
                imageVector =
                    Icons.Default.LocationOn,

                contentDescription =
                    null,

                tint =
                    MaterialTheme.colorScheme.primary,

                modifier =
                    Modifier.size(42.dp)
            )
        },

        title = {

            Text(
                text =
                    "Absen Luar Kantor",

                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column {

                Text(
                    text =
                        "Gunakan fitur ini jika kamu sedang bertugas di luar kantor.",

                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Cara pengisian:",

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                /*
                 * STEP 1
                 */

                GuideStep(
                    number = "1",

                    text =
                        "Masukkan lokasi tempat kamu bertugas."
                )


                /*
                 * STEP 2
                 */

                GuideStep(
                    number = "2",

                    text =
                        "Masukkan alasan atau keperluan bertugas di luar kantor."
                )


                /*
                 * STEP 3
                 */

                GuideStep(
                    number = "3",

                    text =
                        "Periksa kembali data yang sudah dimasukkan."
                )


                /*
                 * STEP 4
                 */

                GuideStep(
                    number = "4",

                    text =
                        "Tekan Kirim Pengajuan jika semua data sudah benar."
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Pastikan informasi yang diberikan sesuai dengan kegiatan sebenarnya.",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        confirmButton = {

            Button(
                onClick =
                    onContinue
            ) {

                Text(
                    text =
                        "Lanjutkan"
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    text =
                        "Batal"
                )
            }
        }
    )
}


/*
 * ============================================================
 * FORM ABSEN LUAR KANTOR
 * ============================================================
 */

@Composable
private fun AbsenLuarKantorFormDialog(
    lokasi: String,
    alasan: String,

    onLokasiChange: (String) -> Unit,
    onAlasanChange: (String) -> Unit,

    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {

    /*
     * Form hanya valid jika:
     *
     * 1. Lokasi diisi
     * 2. Alasan / Keperluan diisi
     */

    val formValid =
        lokasi.isNotBlank() &&
                alasan.isNotBlank()


    AlertDialog(

        onDismissRequest =
            onDismiss,

        icon = {

            Icon(
                imageVector =
                    Icons.Default.LocationOn,

                contentDescription =
                    null,

                tint =
                    MaterialTheme.colorScheme.primary,

                modifier =
                    Modifier.size(42.dp)
            )
        },

        title = {

            Text(
                text =
                    "Isi Absen Luar Kantor",

                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column {

                Text(
                    text =
                        "Masukkan informasi sesuai dengan kegiatan bertugas di luar kantor.",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                /*
                 * =================================================
                 * LOKASI
                 * =================================================
                 */

                OutlinedTextField(

                    value =
                        lokasi,

                    onValueChange =
                        onLokasiChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {

                        Text(
                            "Lokasi"
                        )
                    },

                    placeholder = {

                        Text(
                            "Masukkan lokasi tempat bertugas"
                        )
                    },

                    singleLine = true,

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.LocationOn,

                            contentDescription =
                                null
                        )
                    },

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                /*
                 * =================================================
                 * ALASAN / KEPERLUAN
                 * =================================================
                 */

                OutlinedTextField(

                    value =
                        alasan,

                    onValueChange =
                        onAlasanChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {

                        Text(
                            "Alasan / Keperluan"
                        )
                    },

                    placeholder = {

                        Text(
                            "Masukkan alasan bertugas di luar kantor"
                        )
                    },

                    minLines = 3,

                    maxLines = 4,

                    shape =
                        RoundedCornerShape(12.dp)
                )
            }
        },

        confirmButton = {

            Button(

                onClick =
                    onSubmit,

                enabled =
                    formValid
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        "Kirim Pengajuan"
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    text =
                        "Batal"
                )
            }
        }
    )
}


/*
 * ============================================================
 * GUIDE STEP
 * ============================================================
 */

@Composable
private fun GuideStep(
    number: String,
    text: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                ),

        verticalAlignment =
            Alignment.Top
    ) {

        Surface(
            modifier =
                Modifier.size(24.dp),

            shape =
                RoundedCornerShape(50),

            color =
                MaterialTheme.colorScheme.primaryContainer
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        number,

                    style =
                        MaterialTheme.typography.labelSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(8.dp)
        )


        Text(
            text =
                text,

            modifier =
                Modifier.weight(1f),

            style =
                MaterialTheme.typography.bodySmall
        )
    }
}