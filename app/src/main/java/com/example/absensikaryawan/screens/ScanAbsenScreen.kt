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

import com.google.firebase.firestore.FirebaseFirestore

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import java.util.concurrent.atomic.AtomicBoolean

import kotlin.math.hypot


private const val TAG = "ScanAbsenScreen"

private const val SCANNER_FRAME_SIZE_DP = 280f
private const val QR_MIN_SIZE_DP = 45f
private const val QR_MAX_SIZE_RATIO = 0.90f
private const val QR_MIN_OVERLAP_RATIO = 0.80f
private const val QR_FRAME_TOLERANCE_DP = 12f

private const val QR_REQUIRED_VALID_FRAMES = 3
private const val QR_MAX_TRACKING_DISTANCE_DP = 40f

private val ScannerGreen = Color(0xFF00E676)
private val ScannerGreenBright = Color(0xFF69F0AE)
private val ScannerGreenDark = Color(0xFF00C853)


private data class RegisteredQr(
    val url: String,
    val officeName: String
)


private fun normalizeQrValue(
    value: String
): String {
    return value
        .trim()
        .removeSuffix("/")
}


private fun getRegisteredOffice(
    qrValue: String,
    registeredQrCodes: List<RegisteredQr>
): RegisteredQr? {

    val normalizedValue =
        normalizeQrValue(qrValue)

    return registeredQrCodes.firstOrNull { registeredQr ->
        normalizeQrValue(
            registeredQr.url
        ).equals(
            normalizedValue,
            ignoreCase = true
        )
    }
}


@Composable
fun ScanAbsenScreen(
    externalResetKey: Int = 0,
    onBack: () -> Unit,
    onQrScanned: (String, String, String) -> Unit
) {

    val context = LocalContext.current

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    var registeredQrCodes by remember {
        mutableStateOf(
            emptyList<RegisteredQr>()
        )
    }

    var qrSettingsLoading by remember {
        mutableStateOf(true)
    }

    var qrSettingsError by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        qrSettingsLoading = true
        qrSettingsError = ""

        try {

            val snapshot =
                db.collection("qr_settings")
                    .get()
                    .await()

            val qrList =
                snapshot.documents.mapNotNull { document ->

                    val qrData =
                        document
                            .getString("qrData")
                            ?.trim()
                            ?: ""

                    val officeName =
                        document
                            .getString("officeName")
                            ?.trim()
                            .orEmpty()

                    val aktif =
                        document
                            .getBoolean("aktif")
                            ?: false

                    if (
                        aktif &&
                        qrData.isNotBlank()
                    ) {

                        RegisteredQr(
                            url = qrData,
                            officeName =
                                if (
                                    officeName.isNotBlank()
                                ) {
                                    "KANTOR $officeName"
                                } else {
                                    "KANTOR"
                                }
                        )

                    } else {
                        null
                    }
                }

            registeredQrCodes = qrList

            if (qrList.isEmpty()) {

                qrSettingsError =
                    "Belum ada QR kantor aktif. Hubungi Admin untuk mengatur QR kantor."

                Log.w(
                    TAG,
                    "Tidak ada QR kantor aktif."
                )

            } else {

                Log.d(
                    TAG,
                    "QR aktif: ${qrList.size}"
                )

                qrList.forEach { qr ->

                    Log.d(
                        TAG,
                        "QR: ${qr.officeName} -> ${qr.url}"
                    )
                }
            }

        } catch (e: Exception) {

            registeredQrCodes = emptyList()

            qrSettingsError =
                "Gagal mengambil pengaturan QR kantor. Scanner tidak dapat digunakan."

            Log.e(
                TAG,
                "Gagal mengambil qr_settings",
                e
            )

        } finally {

            qrSettingsLoading = false
        }
    }


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

    var kantorTerdeteksi by remember {
        mutableStateOf("")
    }

    var qrTidakValid by remember {
        mutableStateOf(false)
    }

    var qrErrorMessage by remember {
        mutableStateOf("")
    }

    var absenLuarKantorTerbuka by remember {
        mutableStateOf(false)
    }

    var lokasiLuarKantor by remember {
        mutableStateOf("")
    }

    var alasanLuarKantor by remember {
        mutableStateOf("")
    }

    var validationProgress by remember {
        mutableStateOf(0)
    }

    var kameraDiizinkan by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var qrBoundingBox by remember {
        mutableStateOf<Rect?>(null)
    }

    var qrCornerPoints by remember {
        mutableStateOf<List<Point>?>(null)
    }

    var scannerResetKey by remember {
        mutableStateOf(0)
    }

    val scanLock = remember {
        AtomicBoolean(false)
    }

    val scrollState =
        rememberScrollState()

    val coroutineScope =
        rememberCoroutineScope()

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

    var catatanFocused by remember {
        mutableStateOf(false)
    }

    var lokasiFocused by remember {
        mutableStateOf(false)
    }

    var alasanFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(externalResetKey) {

        if (externalResetKey > 0) {

            qrData = ""
            kantorTerdeteksi = ""
            sudahScan = false
            qrTidakValid = false
            qrErrorMessage = ""
            qrBoundingBox = null
            qrCornerPoints = null
            validationProgress = 0

            scanLock.set(false)

            scannerResetKey++
        }
    }

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
                    scrollState.value + extraScroll
                )
            }
        }
    }


    LaunchedEffect(
        catatanFocused,
        catatan
    ) {

        if (catatanFocused) {

            scrollToField(
                catatanBringIntoViewRequester
            )
        }
    }


    LaunchedEffect(
        lokasiFocused,
        lokasiLuarKantor
    ) {

        if (lokasiFocused) {

            scrollToField(
                lokasiBringIntoViewRequester
            )
        }
    }


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

                scrollState.animateScrollTo(
                    scrollState.maxValue
                )

                delay(150)

                alasanBringIntoViewRequester
                    .bringIntoView()
            }
        }
    }


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
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


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

                if (kameraDiizinkan) {

                    QRScannerCamera(
                        scanLock = scanLock,
                        sudahScan = sudahScan,
                        qrBoundingBox = qrBoundingBox,
                        qrCornerPoints = qrCornerPoints,
                        scannerResetKey = scannerResetKey,
                        qrSettingsLoading = qrSettingsLoading,
                        qrSettingsError = qrSettingsError,

                        scannerEnabled =
                            !qrSettingsLoading &&
                                    qrSettingsError.isBlank() &&
                                    registeredQrCodes.isNotEmpty(),

                        onValidationProgress = {
                                progress ->

                            validationProgress =
                                progress
                        },

                        onQrDetected = {
                                value,
                                box,
                                points ->

                            if (
                                qrSettingsLoading ||
                                qrSettingsError.isNotBlank() ||
                                registeredQrCodes.isEmpty()
                            ) {

                                qrTidakValid = true

                                qrErrorMessage =
                                    "QR kantor belum berhasil diverifikasi. Silakan tunggu atau hubungi Admin."

                                scanLock.set(false)

                                return@QRScannerCamera
                            }


                            val registeredOffice =
                                getRegisteredOffice(
                                    qrValue = value,
                                    registeredQrCodes =
                                        registeredQrCodes
                                )


                            if (
                                registeredOffice == null
                            ) {

                                qrTidakValid = true

                                qrErrorMessage =
                                    "QR Code tidak terdaftar sebagai QR kantor aktif."

                                qrData = ""
                                kantorTerdeteksi = ""
                                sudahScan = false
                                qrBoundingBox = null
                                qrCornerPoints = null
                                validationProgress = 0

                                scanLock.set(false)

                                scannerResetKey++

                                Log.w(
                                    TAG,
                                    "QR TIDAK VALID: $value"
                                )

                                return@QRScannerCamera
                            }


                            qrTidakValid = false
                            qrErrorMessage = ""

                            qrData =
                                registeredOffice.url

                            kantorTerdeteksi =
                                registeredOffice.officeName

                            qrBoundingBox = box
                            qrCornerPoints = points

                            sudahScan = true

                            validationProgress =
                                QR_REQUIRED_VALID_FRAMES

                            Log.d(
                                TAG,
                                """
                                QR VALID
                                kantor=${registeredOffice.officeName}
                                value=${registeredOffice.url}
                                sumber=Firestore
                                """.trimIndent()
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


                if (
                    qrSettingsLoading
                ) {

                    Surface(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.Center
                                )
                                .padding(20.dp),

                        shape =
                            RoundedCornerShape(16.dp),

                        color =
                            Color.Black.copy(
                                alpha = 0.78f
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 13.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(20.dp),

                                color =
                                    ScannerGreen,

                                strokeWidth =
                                    2.dp
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Text(
                                text =
                                    "Memuat QR kantor...",

                                color =
                                    Color.White,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }


                if (
                    !qrSettingsLoading &&
                    qrSettingsError.isNotBlank()
                ) {

                    Surface(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.Center
                                )
                                .padding(20.dp),

                        shape =
                            RoundedCornerShape(16.dp),

                        color =
                            MaterialTheme
                                .colorScheme
                                .errorContainer
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(18.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
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
                                    Modifier.size(32.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "QR Kantor Belum Siap",

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(5.dp)
                            )

                            Text(
                                text =
                                    qrSettingsError,

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer
                            )
                        }
                    }
                }


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

                            qrSettingsLoading ->
                                Color.Black.copy(
                                    alpha = 0.65f
                                )

                            qrSettingsError.isNotBlank() ->
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

                                    qrTidakValid ||
                                            qrSettingsError.isNotBlank() ->
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

                                    qrSettingsLoading ->
                                        "MEMUAT QR KANTOR"

                                    qrSettingsError.isNotBlank() ->
                                        "QR KANTOR TIDAK SIAP"

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
                                "Gunakan QR kantor yang sudah diaktifkan oleh Admin.",

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

            maxLines = 3,

            shape =
                RoundedCornerShape(14.dp)
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Button(
            onClick = {

                if (
                    qrData.isNotBlank() &&
                    kantorTerdeteksi.isNotBlank() &&
                    !sedangKirim
                ) {

                    sedangKirim = true

                    onQrScanned(
                        qrData,
                        catatan,
                        kantorTerdeteksi
                    )
                }
            },

            enabled =
                sudahScan &&
                        qrData.isNotBlank() &&
                        kantorTerdeteksi.isNotBlank() &&
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
                    text = "Memproses..."
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
                    defaultElevation = 1.dp
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
                    null
            )
        }


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
                        defaultElevation = 2.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {

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
                            lokasiLuarKantor = it
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

                        singleLine = true,

                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


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
                            alasanLuarKantor = it
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

                        minLines = 3,
                        maxLines = 5,

                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    Button(
                        onClick = {
                            // Logic luar kantor tetap
                            // ditangani navigation/repository.
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


        Spacer(
            modifier =
                Modifier.height(40.dp)
        )
    }
}


@Composable
private fun QRScannerCamera(
    scanLock: AtomicBoolean,
    sudahScan: Boolean,
    qrBoundingBox: Rect?,
    qrCornerPoints: List<Point>?,
    scannerResetKey: Int,
    qrSettingsLoading: Boolean,
    qrSettingsError: String,
    scannerEnabled: Boolean,
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
     * PENTING:
     * Scanner dibuat di dalam DisposableEffect.
     *
     * Jadi ketika scannerResetKey berubah,
     * scanner lama ditutup dan scanner BARU dibuat.
     *
     * Ini mencegah kasus:
     * "reset → scanner tidak membaca QR lagi"
     */
    DisposableEffect(
        cameraController,
        lifecycleOwner,
        previewView,
        scannerResetKey,
        scannerEnabled
    ) {

        val scanner =
            run {

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


        var validFrameCount = 0
        var lastQrValue = ""

        var lastQrCenterX =
            Float.NaN

        var lastQrCenterY =
            Float.NaN


        fun resetValidation() {

            validFrameCount = 0
            lastQrValue = ""
            lastQrCenterX = Float.NaN
            lastQrCenterY = Float.NaN

            onValidationProgress(0)
        }


        val analyzer =
            MlKitAnalyzer(
                listOf(scanner),

                CameraController
                    .COORDINATE_SYSTEM_VIEW_REFERENCED,

                ContextCompat
                    .getMainExecutor(context)

            ) { result ->

                if (!scannerEnabled) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                if (scanLock.get()) {
                    return@MlKitAnalyzer
                }


                val barcodes =
                    result.getValue(scanner)


                if (barcodes.isNullOrEmpty()) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                val barcode =
                    barcodes.firstOrNull {
                        !it.rawValue.isNullOrBlank()
                    }


                if (barcode == null) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                val value =
                    barcode.rawValue


                if (value.isNullOrBlank()) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                val boundingBox =
                    barcode.boundingBox


                if (boundingBox == null) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


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


                val density =
                    context.resources
                        .displayMetrics
                        .density


                val scannerFrameSizePx =
                    SCANNER_FRAME_SIZE_DP * density

                val tolerancePx =
                    QR_FRAME_TOLERANCE_DP * density


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


                val minQrSizePx =
                    QR_MIN_SIZE_DP * density

                val maxQrSizePx =
                    scannerFrameSizePx *
                            QR_MAX_SIZE_RATIO


                if (
                    qrWidth < minQrSizePx ||
                    qrHeight < minQrSizePx
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                if (
                    qrWidth > maxQrSizePx ||
                    qrHeight > maxQrSizePx
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


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

                    resetValidation()

                    return@MlKitAnalyzer
                }


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
                        qrWidth * qrHeight
                    )

                val overlapRatio =
                    intersectionArea / qrArea


                if (
                    overlapRatio <
                    QR_MIN_OVERLAP_RATIO
                ) {

                    resetValidation()

                    return@MlKitAnalyzer
                }


                val maxTrackingDistancePx =
                    QR_MAX_TRACKING_DISTANCE_DP *
                            density


                if (
                    validFrameCount == 0 ||
                    lastQrValue.isBlank()
                ) {

                    validFrameCount = 1

                    lastQrValue = value

                    lastQrCenterX =
                        qrCenterX

                    lastQrCenterY =
                        qrCenterY

                    onValidationProgress(
                        validFrameCount
                    )

                    return@MlKitAnalyzer
                }


                val sameQr =
                    value == lastQrValue


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


                if (
                    !sameQr ||
                    !stablePosition
                ) {

                    validFrameCount = 1

                    lastQrValue = value

                    lastQrCenterX =
                        qrCenterX

                    lastQrCenterY =
                        qrCenterY

                    onValidationProgress(1)

                    return@MlKitAnalyzer
                }


                validFrameCount++

                lastQrValue = value

                lastQrCenterX =
                    qrCenterX

                lastQrCenterY =
                    qrCenterY

                onValidationProgress(
                    validFrameCount.coerceAtMost(
                        QR_REQUIRED_VALID_FRAMES
                    )
                )


                if (
                    validFrameCount <
                    QR_REQUIRED_VALID_FRAMES
                ) {
                    return@MlKitAnalyzer
                }


                if (
                    !scanLock.compareAndSet(
                        false,
                        true
                    )
                ) {
                    return@MlKitAnalyzer
                }


                val cornerPoints =
                    barcode.cornerPoints?.toList()


                Log.d(
                    TAG,
                    """
                    QR BERHASIL LOCK
                    value=$value
                    qrWidth=$qrWidth
                    qrHeight=$qrHeight
                    overlap=$overlapRatio
                    validationFrames=$validFrameCount
                    """.trimIndent()
                )


                onQrDetected(
                    value,
                    boundingBox,
                    cornerPoints
                )
            }


        cameraController
            .setImageAnalysisAnalyzer(
                ContextCompat
                    .getMainExecutor(context),
                analyzer
            )


        cameraController.bindToLifecycle(
            lifecycleOwner
        )


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


        if (
            !sudahScan &&
            !qrSettingsLoading &&
            qrSettingsError.isBlank()
        ) {

            ScannerFrame(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )

            ScannerLine(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


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
        initialValue = 0.70f,
        targetValue = 1f,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1100,
                        easing = LinearEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "cornerAlpha"
    )


    Box(
        modifier =
            modifier.size(
                SCANNER_FRAME_SIZE_DP.dp
            )
    ) {

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .size(52.dp)
                    .alpha(cornerAlpha)
                    .border(
                        width = 4.dp,
                        color = ScannerGreen,
                        shape =
                            RoundedCornerShape(
                                topStart = 20.dp
                            )
                    )
        )

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .size(52.dp)
                    .alpha(cornerAlpha)
                    .border(
                        width = 4.dp,
                        color = ScannerGreen,
                        shape =
                            RoundedCornerShape(
                                topEnd = 20.dp
                            )
                    )
        )

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomStart
                    )
                    .size(52.dp)
                    .alpha(cornerAlpha)
                    .border(
                        width = 4.dp,
                        color = ScannerGreen,
                        shape =
                            RoundedCornerShape(
                                bottomStart = 20.dp
                            )
                    )
        )

        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .size(52.dp)
                    .alpha(cornerAlpha)
                    .border(
                        width = 4.dp,
                        color = ScannerGreen,
                        shape =
                            RoundedCornerShape(
                                bottomEnd = 20.dp
                            )
                    )
        )
    }
}


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
        initialValue = -118f,
        targetValue = 118f,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1800,
                        easing = LinearEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "scannerPosition"
    )


    Box(
        modifier =
            modifier.size(
                SCANNER_FRAME_SIZE_DP.dp
            )
    ) {

        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
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
            modifier =
                Modifier
                    .align(Alignment.Center)
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


            drawPath(
                path = path,
                color = ScannerGreen,

                style =
                    Stroke(
                        width = 5f,
                        cap = StrokeCap.Round
                    )
            )


            cornerPoints.forEach { point ->

                drawCircle(
                    color = ScannerGreen,
                    radius = 8f,

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

            drawRect(

                color = ScannerGreen,

                topLeft =
                    androidx.compose.ui
                        .geometry
                        .Offset(
                            boundingBox.left.toFloat(),
                            boundingBox.top.toFloat()
                        ),

                size =
                    androidx.compose.ui
                        .geometry
                        .Size(
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