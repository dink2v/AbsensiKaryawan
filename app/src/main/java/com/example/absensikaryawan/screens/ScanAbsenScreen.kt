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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work

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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

import androidx.compose.ui.focus.onFocusChanged

import androidx.core.content.ContextCompat

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.util.concurrent.atomic.AtomicBoolean

import kotlin.math.hypot


private const val TAG =
    "ScanAbsenScreen"


/*
 * ============================================================
 * QR KANTOR RESMI
 * ============================================================
 *
 * QR INI ADALAH QR FINAL YANG SUDAH DIBUAT.
 *
 * Jangan ubah URL kecuali QR kantor memang diganti.
 */

private const val QR_KANTOR_MALANG =
    "https://q.me-qr.com/x5ie23mg"

private const val QR_KANTOR_BLITAR =
    "https://q.me-qr.com/hbywvgy7"

private const val QR_KANTOR_KEDIRI =
    "https://q.me-qr.com/14vy2ipr"


/*
 * ============================================================
 * KONFIGURASI SCANNER
 * ============================================================
 */

private const val SCANNER_FRAME_SIZE_DP =
    280f

private const val QR_MIN_SIZE_DP =
    45f

private const val QR_MAX_SIZE_RATIO =
    0.90f

private const val QR_MIN_OVERLAP_RATIO =
    0.80f

private const val QR_FRAME_TOLERANCE_DP =
    12f


/*
 * ============================================================
 * MULTI FRAME VALIDATION
 * ============================================================
 */

private const val QR_REQUIRED_VALID_FRAMES =
    3

private const val QR_MAX_TRACKING_DISTANCE_DP =
    40f


/*
 * ============================================================
 * WARNA SCANNER
 * ============================================================
 */

private val ScannerGreen =
    Color(0xFF00E676)

private val ScannerGreenBright =
    Color(0xFF69F0AE)

private val ScannerGreenDark =
    Color(0xFF00C853)


/*
 * ============================================================
 * DATA QR KANTOR
 * ============================================================
 */

private data class RegisteredQr(
    val url: String,
    val officeName: String
)


private val registeredQrCodes =
    listOf(

        RegisteredQr(
            url =
                QR_KANTOR_MALANG,

            officeName =
                "KANTOR MALANG"
        ),

        RegisteredQr(
            url =
                QR_KANTOR_BLITAR,

            officeName =
                "KANTOR BLITAR"
        ),

        RegisteredQr(
            url =
                QR_KANTOR_KEDIRI,

            officeName =
                "KANTOR KEDIRI"
        )
    )


/*
 * ============================================================
 * FUNGSI VALIDASI QR
 * ============================================================
 */

private fun getRegisteredOffice(
    qrValue: String
): RegisteredQr? {

    val normalizedValue =
        qrValue
            .trim()
            .removeSuffix("/")

    return registeredQrCodes
        .firstOrNull { registeredQr ->

            registeredQr.url
                .removeSuffix("/")
                .equals(
                    normalizedValue,
                    ignoreCase = true
                )
        }
}


/*
 * ============================================================
 * SCAN ABSEN SCREEN
 * ============================================================
 */

@Composable
fun ScanAbsenScreen(
    onBack: () -> Unit,
    onQrScanned: (String, String) -> Unit
) {

    val context =
        LocalContext.current


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
     * KANTOR HASIL VALIDASI
     * ========================================================
     */

    var kantorTerdeteksi by remember {
        mutableStateOf("")
    }


    /*
     * ========================================================
     * ERROR QR
     * ========================================================
     */

    var qrTidakValid by remember {
        mutableStateOf(false)
    }

    var qrErrorMessage by remember {
        mutableStateOf("")
    }


    /*
     * ========================================================
     * STATE ABSEN LUAR KANTOR
     * ========================================================
     */

    var absenLuarKantorTerbuka by remember {
        mutableStateOf(false)
    }

    var lokasiLuarKantor by remember {
        mutableStateOf("")
    }

    var alasanLuarKantor by remember {
        mutableStateOf("")
    }


    /*
     * ========================================================
     * VALIDASI QR
     * ========================================================
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

    val scanLock =
        remember {
            AtomicBoolean(false)
        }


    /*
     * ========================================================
     * SCROLL STATE
     * ========================================================
     */

    val scrollState =
        rememberScrollState()


    /*
     * ========================================================
     * COROUTINE
     * ========================================================
     */

    val coroutineScope =
        rememberCoroutineScope()


    /*
     * ========================================================
     * BRING INTO VIEW REQUESTER
     * ========================================================
     */

    val catatanBringIntoViewRequester =
        remember {
            BringIntoViewRequester()
        }

    val lokasiBringIntoViewRequester =
        remember {
            BringIntoViewRequester()
        }

    val alasanBringIntoViewRequester =
        remember {
            BringIntoViewRequester()
        }


    /*
     * ========================================================
     * FOCUS STATE
     * ========================================================
     */

    var catatanFocused by remember {
        mutableStateOf(false)
    }

    var lokasiFocused by remember {
        mutableStateOf(false)
    }

    var alasanFocused by remember {
        mutableStateOf(false)
    }


    /*
     * ========================================================
     * FUNGSI SCROLL FIELD
     * ========================================================
     */

    fun scrollToField(
        requester: BringIntoViewRequester,
        extraScroll: Int = 0
    ) {

        coroutineScope.launch {

            delay(150)

            requester.bringIntoView()

            delay(300)

            requester.bringIntoView()

            if (extraScroll > 0) {

                delay(150)

                scrollState.animateScrollTo(
                    scrollState.value +
                            extraScroll
                )
            }
        }
    }


    /*
     * ========================================================
     * AUTO SCROLL CATATAN
     * ========================================================
     */

    LaunchedEffect(
        catatanFocused,
        catatan
    ) {

        if (catatanFocused) {

            scrollToField(
                requester =
                    catatanBringIntoViewRequester
            )
        }
    }


    /*
     * ========================================================
     * AUTO SCROLL LOKASI
     * ========================================================
     */

    LaunchedEffect(
        lokasiFocused,
        lokasiLuarKantor
    ) {

        if (lokasiFocused) {

            scrollToField(
                requester =
                    lokasiBringIntoViewRequester
            )
        }
    }


    /*
     * ========================================================
     * AUTO SCROLL ALASAN
     * ========================================================
     */

    LaunchedEffect(
        alasanFocused,
        alasanLuarKantor
    ) {

        if (alasanFocused) {

            coroutineScope.launch {

                delay(150)

                alasanBringIntoViewRequester
                    .bringIntoView()

                delay(350)

                alasanBringIntoViewRequester
                    .bringIntoView()

                delay(250)

                alasanBringIntoViewRequester
                    .bringIntoView()

                delay(150)

                scrollState.animateScrollTo(
                    scrollState.maxValue
                )
            }
        }
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

            kameraDiizinkan =
                granted

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

        kantorTerdeteksi = ""

        qrBoundingBox = null

        qrCornerPoints = null

        sudahScan = false

        sedangKirim = false

        validationProgress = 0

        qrTidakValid = false

        qrErrorMessage = ""

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
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    scrollState
                )
                .imePadding()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
    ) {


        /*
         * ====================================================
         * HEADER
         * ====================================================
         */

        Row(
            modifier =
                Modifier
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
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Arahkan kamera ke QR Code",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Icon(
                imageVector =
                    Icons.Default.QrCodeScanner,

                contentDescription =
                    null,

                tint =
                    MaterialTheme
                        .colorScheme
                        .primary,

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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primaryContainer
                ),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            /*
             * Sengaja dipertahankan.
             */
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(420.dp)
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


                /*
                 * CAMERA
                 */

                if (kameraDiizinkan) {

                    QRScannerCamera(
                        scanLock =
                            scanLock,

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

                            /*
                             * =================================
                             * VALIDASI FINAL QR
                             * =================================
                             */

                            val registeredOffice =
                                getRegisteredOffice(
                                    value
                                )


                            if (
                                registeredOffice == null
                            ) {

                                /*
                                 * QR BUKAN MILIK
                                 * SISTEM.
                                 */

                                qrTidakValid =
                                    true

                                qrErrorMessage =
                                    "QR Code tidak terdaftar sebagai QR kantor."

                                qrData =
                                    ""

                                kantorTerdeteksi =
                                    ""

                                sudahScan =
                                    false

                                qrBoundingBox =
                                    null

                                qrCornerPoints =
                                    null

                                validationProgress =
                                    0

                                /*
                                 * Buka scanner lagi.
                                 */

                                scanLock.set(false)

                                scannerResetKey++


                                Log.w(
                                    TAG,
                                    """
                                    QR TIDAK VALID
                                    value=$value
                                    """.trimIndent()
                                )

                                return@QRScannerCamera
                            }


                            /*
                             * =================================
                             * QR VALID
                             * =================================
                             */

                            qrTidakValid =
                                false

                            qrErrorMessage =
                                ""

                            qrData =
                                registeredOffice.url

                            kantorTerdeteksi =
                                registeredOffice.officeName

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
                                """
                                QR VALID
                                kantor=${registeredOffice.officeName}
                                value=${registeredOffice.url}
                                """.trimIndent()
                            )
                        }
                    )

                } else {

                    /*
                     * CAMERA PERMISSION UI
                     */

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
                                MaterialTheme
                                    .colorScheme
                                    .error
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
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {

                                permissionLauncher
                                    .launch(
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
                    modifier =
                        Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .padding(
                                top = 12.dp
                            ),

                    shape =
                        RoundedCornerShape(50),

                    color =
                        when {

                            sudahScan ->
                                Color(0xFF1B5E20)

                            qrTidakValid ->
                                MaterialTheme
                                    .colorScheme
                                    .error

                            else ->
                                Color.Black.copy(
                                    alpha = 0.65f
                                )
                        }
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
                                when {

                                    sudahScan ->
                                        Icons.Default.CheckCircle

                                    qrTidakValid ->
                                        Icons.Default.Warning

                                    else ->
                                        Icons.Default.QrCodeScanner
                                },

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

                                    qrTidakValid ->
                                        "QR TIDAK VALID"

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
                                MaterialTheme
                                    .typography
                                    .labelMedium
                        )
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

                        modifier =
                            Modifier
                                .align(
                                    Alignment.TopEnd
                                )
                                .padding(8.dp)
                                .clip(
                                    RoundedCornerShape(50.dp)
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


        /*
         * ====================================================
         * QR TIDAK VALID
         * ====================================================
         */

        if (qrTidakValid) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .errorContainer
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
                            Icons.Default.Warning,

                        contentDescription =
                            null,

                        tint =
                            MaterialTheme
                                .colorScheme
                                .error,

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
                                "QR TIDAK VALID",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                qrErrorMessage,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Gunakan QR resmi kantor Malang, Blitar, atau Kediri.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            fontWeight =
                                FontWeight.Medium,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer
                        )
                    }
                }
            }
        }


        /*
         * ====================================================
         * QR BERHASIL DIKUNCI
         * ====================================================
         */

        if (
            sudahScan &&
            qrData.isNotBlank()
        ) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Card(
                modifier =
                    Modifier
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
                                "QR VALID",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF1B5E20)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                kantorTerdeteksi,

                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF1B5E20)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "QR resmi kantor berhasil dikenali. Kamu dapat melanjutkan konfirmasi absen.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                Color(0xFF33691E)
                        )
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


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

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    )
                    .bringIntoViewRequester(
                        catatanBringIntoViewRequester
                    )
                    .onFocusChanged { focusState ->

                        catatanFocused =
                            focusState.isFocused

                        if (
                            focusState.isFocused
                        ) {

                            scrollToField(
                                requester =
                                    catatanBringIntoViewRequester
                            )
                        }
                    },

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

            maxLines =
                3,

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

                    sedangKirim =
                        true

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

            modifier =
                Modifier
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
                        MaterialTheme
                            .colorScheme
                            .primary
                )
        ) {

            if (sedangKirim) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(21.dp),

                    color =
                        Color.White,

                    strokeWidth =
                        2.dp
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
                Modifier.height(10.dp)
        )


        /*
         * ====================================================
         * BUTTON ABSEN DI LUAR KANTOR
         * ====================================================
         */

        Button(
            onClick = {

                absenLuarKantorTerbuka =
                    !absenLuarKantorTerbuka
            },

            modifier =
                Modifier
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
                        MaterialTheme
                            .colorScheme
                            .surface,

                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .primary
                ),

            elevation =
                ButtonDefaults.buttonElevation(
                    defaultElevation =
                        1.dp
                )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Work,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Text(
                text =
                    "Absen di Luar Kantor",

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            Icon(
                imageVector =
                    if (
                        absenLuarKantorTerbuka
                    )
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,

                contentDescription =
                    if (
                        absenLuarKantorTerbuka
                    )
                        "Tutup"
                    else
                        "Buka"
            )
        }


        /*
         * ====================================================
         * FORM ABSEN LUAR KANTOR
         * ====================================================
         */

        if (
            absenLuarKantorTerbuka
        ) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {

                    /*
                     * =================================================
                     * HEADER FORM
                     * =================================================
                     */

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Work,

                            contentDescription =
                                null,

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,

                            modifier =
                                Modifier.size(24.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Absen dari lokasi tugas",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )


                    Text(
                        text =
                            "Gunakan jika kamu langsung menuju lokasi klien atau tempat tugas tanpa datang ke kantor terlebih dahulu.",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    /*
                     * =================================================
                     * LOKASI
                     * =================================================
                     */

                    Text(
                        text =
                            "Lokasi",

                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,

                        fontWeight =
                            FontWeight.SemiBold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    OutlinedTextField(
                        value =
                            lokasiLuarKantor,

                        onValueChange = {
                            lokasiLuarKantor =
                                it
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(
                                    lokasiBringIntoViewRequester
                                )
                                .onFocusChanged { focusState ->

                                    lokasiFocused =
                                        focusState.isFocused

                                    if (
                                        focusState.isFocused
                                    ) {

                                        scrollToField(
                                            requester =
                                                lokasiBringIntoViewRequester
                                        )
                                    }
                                },

                        placeholder = {
                            Text(
                                "Contoh: SMK Negeri 1 Blitar"
                            )
                        },

                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.LocationOn,

                                contentDescription =
                                    null
                            )
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    /*
                     * =================================================
                     * ALASAN / KEPERLUAN
                     * =================================================
                     */

                    Text(
                        text =
                            "Alasan / Keperluan",

                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,

                        fontWeight =
                            FontWeight.SemiBold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    OutlinedTextField(
                        value =
                            alasanLuarKantor,

                        onValueChange = {
                            alasanLuarKantor =
                                it
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(
                                    alasanBringIntoViewRequester
                                )
                                .onFocusChanged { focusState ->

                                    alasanFocused =
                                        focusState.isFocused

                                    if (
                                        focusState.isFocused
                                    ) {

                                        coroutineScope.launch {

                                            delay(200)

                                            alasanBringIntoViewRequester
                                                .bringIntoView()

                                            delay(350)

                                            alasanBringIntoViewRequester
                                                .bringIntoView()

                                            delay(250)

                                            scrollState.animateScrollTo(
                                                scrollState.maxValue
                                            )

                                            delay(150)

                                            alasanBringIntoViewRequester
                                                .bringIntoView()
                                        }
                                    }
                                },

                        placeholder = {
                            Text(
                                "Contoh: Bertemu klien untuk keperluan pekerjaan"
                            )
                        },

                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.Work,

                                contentDescription =
                                    null
                            )
                        },

                        minLines =
                            3,

                        maxLines =
                            5,

                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    /*
                     * =================================================
                     * KIRIM ABSEN
                     * =================================================
                     */

                    Button(
                        onClick = {

                            /*
                             * Logic Firestore tetap
                             * disambungkan di navigation/repository.
                             */
                        },

                        enabled =
                            lokasiLuarKantor
                                .trim()
                                .isNotEmpty() &&
                                    alasanLuarKantor
                                        .trim()
                                        .isNotEmpty(),

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),

                        shape =
                            RoundedCornerShape(14.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Send,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Kirim Absen",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }


            /*
             * =================================================
             * EXTRA BOTTOM SPACE
             * =================================================
             */

            Spacer(
                modifier =
                    Modifier.height(
                        if (
                            alasanFocused ||
                            lokasiFocused
                        )
                            220.dp
                        else
                            80.dp
                    )
            )
        }


        /*
         * ====================================================
         * BOTTOM SPACE
         * ====================================================
         */

        Spacer(
            modifier =
                Modifier.height(40.dp)
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
                    CameraSelector
                        .DEFAULT_BACK_CAMERA

                setEnabledUseCases(
                    CameraController
                        .IMAGE_ANALYSIS
                )
            }
        }


    /*
     * ========================================================
     * PREVIEW
     * ========================================================
     */

    val previewView =
        remember(
            context,
            cameraController
        ) {

            PreviewView(
                context
            ).apply {

                implementationMode =
                    PreviewView
                        .ImplementationMode
                        .COMPATIBLE

                scaleType =
                    PreviewView
                        .ScaleType
                        .FILL_CENTER

                controller =
                    cameraController
            }
        }


    /*
     * ========================================================
     * ML KIT
     * ========================================================
     */

    val scanner =
        remember {

            val options =
                BarcodeScannerOptions
                    .Builder()
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
         * ANALYZER
         * ====================================================
         */

        val analyzer =
            MlKitAnalyzer(
                listOf(scanner),

                CameraController
                    .COORDINATE_SYSTEM_VIEW_REFERENCED,

                ContextCompat
                    .getMainExecutor(
                        context
                    )

            ) { result ->

                /*
                 * Sudah lock.
                 */

                if (
                    scanLock.get()
                ) {

                    return@MlKitAnalyzer
                }


                /*
                 * Barcode.
                 */

                val barcodes =
                    result.getValue(
                        scanner
                    )


                if (
                    barcodes.isNullOrEmpty()
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * Cari QR yang mempunyai value.
                 */

                val barcode =
                    barcodes.firstOrNull {

                        !it.rawValue
                            .isNullOrBlank()
                    }


                if (
                    barcode == null
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * QR VALUE
                 */

                val value =
                    barcode.rawValue


                if (
                    value.isNullOrBlank()
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * BOUNDING BOX
                 */

                val boundingBox =
                    barcode.boundingBox


                if (
                    boundingBox == null
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * PREVIEW SIZE
                 */

                val previewWidth =
                    previewView
                        .width
                        .toFloat()

                val previewHeight =
                    previewView
                        .height
                        .toFloat()


                if (
                    previewWidth <= 0f ||
                    previewHeight <= 0f
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * DENSITY
                 */

                val density =
                    context
                        .resources
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
                            (
                                    previewWidth -
                                            scannerFrameSizePx
                                    ) / 2f
                            ) - tolerancePx


                val scannerTop =
                    (
                            (
                                    previewHeight -
                                            scannerFrameSizePx
                                    ) / 2f
                            ) - tolerancePx


                val scannerRight =
                    (
                            (
                                    previewWidth +
                                            scannerFrameSizePx
                                    ) / 2f
                            ) + tolerancePx


                val scannerBottom =
                    (
                            (
                                    previewHeight +
                                            scannerFrameSizePx
                                    ) / 2f
                            ) + tolerancePx


                /*
                 * =================================================
                 * QR BOX
                 * =================================================
                 */

                val qrLeft =
                    boundingBox.left
                        .toFloat()

                val qrTop =
                    boundingBox.top
                        .toFloat()

                val qrRight =
                    boundingBox.right
                        .toFloat()

                val qrBottom =
                    boundingBox.bottom
                        .toFloat()


                val qrWidth =
                    boundingBox.width()
                        .toFloat()

                val qrHeight =
                    boundingBox.height()
                        .toFloat()


                /*
                 * =================================================
                 * UKURAN QR
                 * =================================================
                 */

                val minQrSizePx =
                    QR_MIN_SIZE_DP *
                            density

                val maxQrSizePx =
                    scannerFrameSizePx *
                            QR_MAX_SIZE_RATIO


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
                 * POSISI
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


                if (
                    !centerInsideFrame
                ) {

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
                        "QR ditolak: overlap=$overlapRatio"
                    )

                    resetValidation()

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * MULTI FRAME
                 * =================================================
                 */

                val maxTrackingDistancePx =
                    QR_MAX_TRACKING_DISTANCE_DP *
                            density


                /*
                 * FRAME PERTAMA
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
                 * QR SAMA
                 */

                val sameQr =
                    value ==
                            lastQrValue


                /*
                 * JARAK
                 */

                val centerDistance =
                    hypot(
                        qrCenterX -
                                lastQrCenterX,

                        qrCenterY -
                                lastQrCenterY
                    )


                val stablePosition =
                    centerDistance <=
                            maxTrackingDistancePx


                /*
                 * RESET VALIDASI
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
                 * FRAME BERIKUTNYA
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
                 * Belum 3 frame.
                 */

                if (
                    validFrameCount <
                    QR_REQUIRED_VALID_FRAMES
                ) {

                    return@MlKitAnalyzer
                }


                /*
                 * =================================================
                 * LOCK
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
                 * CORNER POINTS
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
                 * =================================================
                 * KIRIM KE VALIDATOR
                 * =================================================
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

        cameraController
            .setImageAnalysisAnalyzer(
                ContextCompat
                    .getMainExecutor(
                        context
                    ),

                analyzer
            )


        /*
         * ====================================================
         * BIND CAMERA
         * ====================================================
         */

        cameraController
            .bindToLifecycle(
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

            cameraController
                .unbind()

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

        /*
         * CAMERA PREVIEW
         */

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
         * SCANNER FRAME
         */

        if (
            !sudahScan
        ) {

            ScannerFrame(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )


            /*
             * SCANNER LINE
             */

            ScannerLine(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        /*
         * QR DETECTION OVERLAY
         */

        if (
            sudahScan
        ) {

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
            label =
                "scannerFrame"
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
                        durationMillis =
                            1100,

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
        modifier =
            modifier
                .size(
                    SCANNER_FRAME_SIZE_DP.dp
                )
    ) {

        /*
         * TOP LEFT
         */

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .size(52.dp)
                    .alpha(
                        cornerAlpha
                    )
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


        /*
         * TOP RIGHT
         */

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .size(52.dp)
                    .alpha(
                        cornerAlpha
                    )
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


        /*
         * BOTTOM LEFT
         */

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomStart
                    )
                    .size(52.dp)
                    .alpha(
                        cornerAlpha
                    )
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


        /*
         * BOTTOM RIGHT
         */

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .size(52.dp)
                    .alpha(
                        cornerAlpha
                    )
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
            label =
                "scannerLine"
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
                        durationMillis =
                            1800,

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
        modifier =
            modifier
                .size(
                    SCANNER_FRAME_SIZE_DP.dp
                )
    ) {

        /*
         * GLOW
         */

        Box(
            modifier =
                Modifier
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


        /*
         * MAIN LINE
         */

        Box(
            modifier =
                Modifier
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
        modifier =
            modifier
    ) {

        /*
         * =================================================
         * CORNER POINTS
         * =================================================
         */

        if (
            cornerPoints != null &&
            cornerPoints.size >= 4
        ) {

            val path =
                Path().apply {

                    moveTo(
                        cornerPoints[0]
                            .x
                            .toFloat(),

                        cornerPoints[0]
                            .y
                            .toFloat()
                    )

                    for (
                    i in 1 until cornerPoints.size
                    ) {

                        lineTo(
                            cornerPoints[i]
                                .x
                                .toFloat(),

                            cornerPoints[i]
                                .y
                                .toFloat()
                        )
                    }

                    close()
                }


            /*
             * OUTLINE
             */

            drawPath(
                path =
                    path,

                color =
                    ScannerGreen,

                style =
                    Stroke(
                        width = 5f,

                        cap =
                            StrokeCap.Round
                    )
            )


            /*
             * CORNER DOTS
             */

            cornerPoints.forEach { point ->

                drawCircle(
                    color =
                        ScannerGreen,

                    radius =
                        8f,

                    center =
                        androidx.compose.ui
                            .geometry
                            .Offset(
                                point.x.toFloat(),
                                point.y.toFloat()
                            )
                )
            }

        } else if (
            boundingBox != null
        ) {

            /*
             * FALLBACK BOX
             */

            drawRect(

                color =
                    ScannerGreen,

                topLeft =
                    androidx.compose.ui
                        .geometry
                        .Offset(
                            boundingBox.left
                                .toFloat(),

                            boundingBox.top
                                .toFloat()
                        ),

                size =
                    androidx.compose.ui
                        .geometry
                        .Size(
                            boundingBox.width()
                                .toFloat(),

                            boundingBox.height()
                                .toFloat()
                        ),

                style =
                    Stroke(
                        width = 5f
                    )
            )
        }
    }
}