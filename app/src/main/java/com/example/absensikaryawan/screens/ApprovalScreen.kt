package com.example.absensikaryawan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absensikaryawan.data.PengajuanRepository
import kotlinx.coroutines.launch


@Composable
fun ApprovalScreen() {

    // ==========================================================
    // REPOSITORY
    // ==========================================================

    val repository = remember {
        PengajuanRepository()
    }

    val scope = rememberCoroutineScope()


    // ==========================================================
    // STATE
    // ==========================================================

    var daftarPengajuan by remember {
        mutableStateOf(
            emptyList<Map<String, Any>>()
        )
    }

    var sedangMemuat by remember {
        mutableStateOf(false)
    }

    var sedangDiproses by remember {
        mutableStateOf("")
    }

    var pesanError by remember {
        mutableStateOf("")
    }


    // ==========================================================
    // LOAD DATA
    // ==========================================================

    fun muatData() {

        scope.launch {

            sedangMemuat = true
            pesanError = ""

            val result =
                repository.ambilSemuaPengajuan()

            result.onSuccess { data ->

                daftarPengajuan = data
            }

            result.onFailure { error ->

                pesanError =
                    error.message
                        ?: "Gagal mengambil data pengajuan."
            }

            sedangMemuat = false
        }
    }


    // ==========================================================
    // LOAD SAAT SCREEN DIBUKA
    // ==========================================================

    LaunchedEffect(Unit) {

        muatData()
    }


    // ==========================================================
    // MAIN SCREEN
    // ==========================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Approval Pengajuan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "Tinjau pengajuan karyawan",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }


                IconButton(
                    onClick = {

                        if (
                            !sedangMemuat &&
                            sedangDiproses.isEmpty()
                        ) {

                            muatData()
                        }
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }


            // ==================================================
            // CONTENT
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // ==================================================
                // INFO CARD
                // ==================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftGreen
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(30.dp)
                        )


                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )


                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Pengajuan Menunggu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )


                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )


                            Text(
                                text = "Periksa dan tentukan persetujuan pengajuan karyawan.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                // ==================================================
                // TITLE
                // ==================================================

                Text(
                    text = "Daftar Pengajuan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ==================================================
                // ERROR
                // ==================================================

                if (pesanError.isNotEmpty()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE7E7)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Gagal memuat data",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )


                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )


                            Text(
                                text = pesanError,
                                fontSize = 12.sp,
                                color = Color(0xFF7F1D1D)
                            )


                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )


                            Button(
                                onClick = {
                                    muatData()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(17.dp)
                                )


                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )


                                Text(
                                    text = "Coba Lagi"
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }


                // ==================================================
                // LOADING
                // ==================================================

                if (sedangMemuat) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 30.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            color = PrimaryGreen
                        )


                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )


                        Text(
                            text = "Memuat pengajuan...",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }


                // ==================================================
                // EMPTY
                // ==================================================

                if (
                    !sedangMemuat &&
                    pesanError.isEmpty() &&
                    daftarPengajuan.isEmpty()
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(40.dp)
                            )


                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )


                            Text(
                                text = "Belum ada pengajuan.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }


                // ==================================================
                // LIST PENGAJUAN
                // ==================================================

                if (
                    !sedangMemuat &&
                    daftarPengajuan.isNotEmpty()
                ) {

                    daftarPengajuan.forEach { pengajuan ->

                        val documentId =
                            pengajuan["documentId"]
                                ?.toString()
                                ?: ""

                        val nama =
                            pengajuan["nama"]
                                ?.toString()
                                ?: "Karyawan"

                        val jenis =
                            pengajuan["jenis"]
                                ?.toString()
                                ?: "Pengajuan"

                        val tanggalMulai =
                            pengajuan["tanggalMulai"]
                                ?.toString()
                                ?: ""

                        val tanggalSelesai =
                            pengajuan["tanggalSelesai"]
                                ?.toString()
                                ?: ""

                        val jamPulang =
                            pengajuan["jamPulang"]
                                ?.toString()
                                ?: ""

                        val jamKeluar =
                            pengajuan["jamKeluar"]
                                ?.toString()
                                ?: ""

                        val jamKembali =
                            pengajuan["jamKembali"]
                                ?.toString()
                                ?: ""

                        val alasan =
                            pengajuan["alasan"]
                                ?.toString()
                                ?: ""

                        val status =
                            pengajuan["status"]
                                ?.toString()
                                ?.lowercase()
                                ?: "menunggu"


                        ApprovalRequestCard(
                            nama = nama,
                            jenis = jenis,
                            tanggalMulai = tanggalMulai,
                            tanggalSelesai = tanggalSelesai,
                            jamPulang = jamPulang,
                            jamKeluar = jamKeluar,
                            jamKembali = jamKembali,
                            alasan = alasan,
                            status = status,
                            sedangDiproses =
                                sedangDiproses == documentId,

                            onSetujui = {

                                if (
                                    documentId.isNotEmpty() &&
                                    sedangDiproses.isEmpty()
                                ) {

                                    sedangDiproses =
                                        documentId

                                    scope.launch {

                                        val result =
                                            repository.updateStatusPengajuan(
                                                documentId = documentId,
                                                status = "disetujui"
                                            )

                                        result.onSuccess {

                                            sedangDiproses = ""

                                            muatData()
                                        }


                                        result.onFailure { error ->

                                            pesanError =
                                                error.message
                                                    ?: "Gagal menyetujui pengajuan."

                                            sedangDiproses = ""
                                        }
                                    }
                                }
                            },

                            onTolak = {

                                if (
                                    documentId.isNotEmpty() &&
                                    sedangDiproses.isEmpty()
                                ) {

                                    sedangDiproses =
                                        documentId

                                    scope.launch {

                                        val result =
                                            repository.updateStatusPengajuan(
                                                documentId = documentId,
                                                status = "ditolak"
                                            )

                                        result.onSuccess {

                                            sedangDiproses = ""

                                            muatData()
                                        }


                                        result.onFailure { error ->

                                            pesanError =
                                                error.message
                                                    ?: "Gagal menolak pengajuan."

                                            sedangDiproses = ""
                                        }
                                    }
                                }
                            }
                        )


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}


// ==========================================================
// APPROVAL REQUEST CARD
// ==========================================================

@Composable
private fun ApprovalRequestCard(
    nama: String,
    jenis: String,
    tanggalMulai: String,
    tanggalSelesai: String,
    jamPulang: String,
    jamKeluar: String,
    jamKembali: String,
    alasan: String,
    status: String,
    sedangDiproses: Boolean,
    onSetujui: () -> Unit,
    onTolak: () -> Unit
) {

    val statusText: String

    val statusIcon: ImageVector

    val statusColor: Color


    if (status == "disetujui") {

        statusText = "Disetujui"

        statusIcon = Icons.Default.CheckCircle

        statusColor = PrimaryGreen

    } else if (status == "ditolak") {

        statusText = "Ditolak"

        statusIcon = Icons.Default.Cancel

        statusColor = Color(0xFFB91C1C)

    } else {

        statusText = "Menunggu"

        statusIcon = Icons.Default.Pending

        statusColor = Color(0xFFD97706)
    }


    // ==========================================================
    // INITIAL
    // ==========================================================

    val namaBersih =
        nama.trim()

    val daftarNama =
        namaBersih.split(" ")

    var initials =
        ""

    if (daftarNama.isNotEmpty()) {

        initials =
            daftarNama[0]
                .take(1)
                .uppercase()
    }

    if (daftarNama.size > 1) {

        initials =
            initials +
                    daftarNama[1]
                        .take(1)
                        .uppercase()
    }

    if (initials.isEmpty()) {

        initials = "K"
    }


    // ==========================================================
    // CARD
    // ==========================================================

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // ==================================================
            // USER
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = SoftGreen,
                            shape = RoundedCornerShape(50.dp)
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = initials,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }


                Spacer(
                    modifier = Modifier.width(12.dp)
                )


                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = nama,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )


                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )


                    Text(
                        text = jenis,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }


                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // ==================================================
            // TANGGAL
            // ==================================================

            if (tanggalMulai.isNotEmpty()) {

                Text(
                    text = "Tanggal",
                    fontSize = 11.sp,
                    color = TextGray
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                if (
                    tanggalSelesai.isNotEmpty() &&
                    tanggalSelesai != tanggalMulai
                ) {

                    Text(
                        text =
                            "$tanggalMulai - $tanggalSelesai",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                } else {

                    Text(
                        text = tanggalMulai,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }
            }


            // ==================================================
            // JAM
            // ==================================================

            if (
                jamPulang.isNotEmpty() ||
                jamKeluar.isNotEmpty() ||
                jamKembali.isNotEmpty()
            ) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                if (jamPulang.isNotEmpty()) {

                    Text(
                        text = "Jam Pulang: $jamPulang",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }


                if (jamKeluar.isNotEmpty()) {

                    Text(
                        text = "Jam Keluar: $jamKeluar",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }


                if (jamKembali.isNotEmpty()) {

                    Text(
                        text = "Jam Kembali: $jamKembali",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }


            // ==================================================
            // ALASAN
            // ==================================================

            if (alasan.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Text(
                    text = "Alasan",
                    fontSize = 11.sp,
                    color = TextGray
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                Text(
                    text = alasan,
                    fontSize = 13.sp,
                    color = TextDark
                )
            }


            // ==================================================
            // BUTTON
            // ==================================================

            if (status == "menunggu") {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Button(
                        onClick = onSetujui,
                        enabled = !sedangDiproses,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        )
                    ) {

                        if (sedangDiproses) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )


                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )


                            Text(
                                text = "Setujui",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                    Button(
                        onClick = onTolak,
                        enabled = !sedangDiproses,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB91C1C)
                        )
                    ) {

                        if (sedangDiproses) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )


                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )


                            Text(
                                text = "Tolak",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}