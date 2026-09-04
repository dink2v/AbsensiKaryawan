package com.example.absensikaryawan.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScanSuccessAnimationPreview() {

    var animationKey by remember {
        mutableIntStateOf(0)
    }

    var stage by remember {
        mutableStateOf(ScanAnimationStage.QR)
    }

    LaunchedEffect(animationKey) {

        stage = ScanAnimationStage.QR

        delay(1000)

        stage = ScanAnimationStage.VERIFYING

        delay(1000)

        stage = ScanAnimationStage.SUCCESS
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Konfirmasi Absensi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = when (stage) {

                    ScanAnimationStage.QR ->
                        "QR Code berhasil terdeteksi"

                    ScanAnimationStage.VERIFYING ->
                        "Sedang memverifikasi absensi..."

                    ScanAnimationStage.SUCCESS ->
                        "Absensi berhasil diverifikasi"
                },
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(45.dp)
            )

            /*
             * =====================================================
             * AREA ANIMASI UTAMA
             * QR → VERIFY → CHECK
             * =====================================================
             */

            Box(
                modifier = Modifier.size(250.dp),
                contentAlignment = Alignment.Center
            ) {

                if (stage == ScanAnimationStage.SUCCESS) {
                    SuccessPulse()
                }

                AnimatedContent(
                    targetState = stage,
                    transitionSpec = {

                        (
                                fadeIn(
                                    animationSpec = tween(350)
                                ) +
                                        scaleIn(
                                            initialScale = 0.75f,
                                            animationSpec = tween(
                                                450,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                ).togetherWith(
                                fadeOut(
                                    animationSpec = tween(250)
                                ) +
                                        scaleOut(
                                            targetScale = 0.7f,
                                            animationSpec = tween(300)
                                        )
                            )
                    },
                    label = "scan_content"
                ) { currentStage ->

                    when (currentStage) {

                        ScanAnimationStage.QR -> {

                            QRScanningAnimation(
                                verifying = false
                            )
                        }

                        ScanAnimationStage.VERIFYING -> {

                            QRScanningAnimation(
                                verifying = true
                            )
                        }

                        ScanAnimationStage.SUCCESS -> {

                            SuccessAnimation()
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            /*
             * =====================================================
             * STATUS
             * =====================================================
             */

            if (stage != ScanAnimationStage.SUCCESS) {

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFE8F5E9)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 10.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = PrimaryGreen
                        ) {}

                        Text(
                            text = if (
                                stage == ScanAnimationStage.QR
                            ) {
                                "QR siap diverifikasi"
                            } else {
                                "Memverifikasi..."
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryGreen
                        )
                    }
                }
            }

            /*
             * =====================================================
             * SUCCESS INFORMATION
             * =====================================================
             */

            if (stage == ScanAnimationStage.SUCCESS) {

                SuccessInformation()
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            /*
             * =====================================================
             * BUTTON
             * =====================================================
             */

            Button(
                onClick = {
                    animationKey++
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {

                Text(
                    text = "Ulangi Animasi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Prototype • QR → Check",
                fontSize = 10.sp,
                color = TextGray
            )
        }
    }
}


/*
 * =============================================================
 * SUCCESS INFORMATION
 * =============================================================
 */

@Composable
private fun SuccessInformation() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "ABSENSI BERHASIL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Absensi masuk berhasil disimpan.",
            fontSize = 13.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Surface(
            shape = RoundedCornerShape(13.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 28.dp,
                    vertical = 13.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Jam Masuk",
                    fontSize = 10.sp,
                    color = TextGray
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "08:52:31",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}


/*
 * =============================================================
 * QR SCANNING
 * =============================================================
 */

@Composable
private fun QRScanningAnimation(
    verifying: Boolean
) {

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "qr_scan"
        )

    val scanPosition by infiniteTransition.animateFloat(
        initialValue = -0.35f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_position"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "qr_pulse"
    )

    Box(
        modifier = Modifier
            .size(190.dp)
            .scale(pulseScale)
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 5.dp
        ) {

            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(125.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {

                            translationY =
                                scanPosition *
                                        145.dp.toPx()
                        }
                        .height(3.dp)
                        .background(
                            color = PrimaryGreen,
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }

        ScannerCorners(
            color =
                if (verifying) {
                    PrimaryGreen
                } else {
                    Color(0xFF8BAA98)
                }
        )

        if (verifying) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = PrimaryGreen
                ) {

                    Text(
                        text = "Memverifikasi",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


/*
 * =============================================================
 * SCANNER CORNERS
 * =============================================================
 */

@Composable
private fun ScannerCorners(
    color: Color
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(35.dp)
                .border(
                    width = 3.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        topStart = 12.dp
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(35.dp)
                .border(
                    width = 3.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        topEnd = 12.dp
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(35.dp)
                .border(
                    width = 3.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        bottomStart = 12.dp
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(35.dp)
                .border(
                    width = 3.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        bottomEnd = 12.dp
                    )
                )
        )
    }
}


/*
 * =============================================================
 * SUCCESS CHECK
 * =============================================================
 */

@Composable
private fun SuccessAnimation() {

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "success_scale"
    )

    Box(
        modifier = Modifier
            .size(150.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = Color(0xFFE8F5E9)
        ) {}

        Surface(
            modifier = Modifier.size(112.dp),
            shape = CircleShape,
            color = PrimaryGreen
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(62.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }
    }
}


/*
 * =============================================================
 * SUCCESS PULSE
 * =============================================================
 */

@Composable
private fun SuccessPulse() {

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "success_pulse"
        )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .size(180.dp)
            .scale(pulseScale)
            .alpha(pulseAlpha)
            .border(
                width = 3.dp,
                color = PrimaryGreen,
                shape = CircleShape
            )
    )
}


/*
 * =============================================================
 * ENUM
 * =============================================================
 */

private enum class ScanAnimationStage {

    QR,

    VERIFYING,

    SUCCESS
}