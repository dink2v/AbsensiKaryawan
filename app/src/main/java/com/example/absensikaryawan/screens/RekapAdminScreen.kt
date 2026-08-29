package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


// ==========================================================
// MODEL
// ==========================================================

private data class DataRekapAbsensi(

    val id: String,

    val uid: String,

    val nama: String,

    val tanggal: String,

    val jamMasuk: String,

    val jamPulang: String,

    val catatan: String
)


// ==========================================================
// REKAP ADMIN
// ==========================================================

@Composable
fun RekapAdminScreen() {

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }


    var daftarRekap by remember {

        mutableStateOf(
            emptyList<DataRekapAbsensi>()
        )
    }


    var loading by remember {

        mutableStateOf(true)
    }


    var errorMessage by remember {

        mutableStateOf("")
    }


    var searchQuery by remember {

        mutableStateOf("")
    }


    var refreshKey by remember {

        mutableIntStateOf(0)
    }


    // ======================================================
    // LOAD FIRESTORE
    // ======================================================

    LaunchedEffect(refreshKey) {

        try {

            loading = true

            errorMessage = ""


            val snapshot =
                db
                    .collection("attendance")
                    .get()
                    .await()


            daftarRekap =
                snapshot.documents
                    .map { document ->

                        DataRekapAbsensi(

                            id =
                                document.id,

                            uid =
                                document.getString(
                                    "uid"
                                ) ?: "",

                            nama =
                                document.getString(
                                    "nama"
                                ) ?: "Tanpa Nama",

                            tanggal =
                                document.getString(
                                    "tanggal"
                                ) ?: "",

                            jamMasuk =
                                document.getString(
                                    "jamMasuk"
                                ) ?: "",

                            jamPulang =
                                document.getString(
                                    "jamPulang"
                                ) ?: "",

                            catatan =
                                document.getString(
                                    "catatan"
                                ) ?: ""
                        )
                    }
                    .sortedWith(

                        compareByDescending<DataRekapAbsensi> {

                            it.tanggal

                        }.thenBy {

                            it.nama.lowercase()
                        }
                    )

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Gagal mengambil data rekap."

        } finally {

            loading = false
        }
    }


    // ======================================================
    // SEARCH
    // ======================================================

    val hasilPencarian =

        remember(
            daftarRekap,
            searchQuery
        ) {

            if (searchQuery.isBlank()) {

                daftarRekap

            } else {

                val query =
                    searchQuery
                        .trim()
                        .lowercase()


                daftarRekap.filter { data ->

                    data.nama
                        .lowercase()
                        .contains(query)

                            ||

                            data.tanggal
                                .lowercase()
                                .contains(query)

                            ||

                            data.jamMasuk
                                .lowercase()
                                .contains(query)

                            ||

                            data.jamPulang
                                .lowercase()
                                .contains(query)
                }
            }
        }


    // ======================================================
    // STATISTIK
    // ======================================================

    val totalData =
        daftarRekap.size


    val totalHadir =
        daftarRekap.count {

            it.jamMasuk.isNotBlank()
        }


    val totalPulang =
        daftarRekap.count {

            it.jamPulang.isNotBlank()
        }


    // ======================================================
    // UI
    // ======================================================

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
                            horizontal = 20.dp,
                            vertical = 14.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(

                    modifier =
                        Modifier.size(44.dp),

                    shape =
                        RoundedCornerShape(13.dp),

                    color =
                        Color(0xFFE6EEE9)
                ) {

                    Box(

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Assessment,

                            contentDescription =
                                null,

                            tint =
                                PrimaryGreen,

                            modifier =
                                Modifier.size(24.dp)
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )


                // TANPA WEIGHT
                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 40.dp)
                ) {

                    Text(

                        text =
                            "Rekap Absensi",

                        fontSize =
                            21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark
                    )


                    Text(

                        text =
                            "Data kehadiran seluruh karyawan",

                        fontSize =
                            12.sp,

                        color =
                            TextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }


                IconButton(

                    onClick = {

                        refreshKey++
                    }
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Refresh,

                        contentDescription =
                            "Refresh",

                        tint =
                            PrimaryGreen
                    )
                }
            }


            // ==================================================
            // SUMMARY
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(9.dp)
            ) {

                // TOTAL
                RekapSummaryCard(

                    modifier =
                        Modifier
                            .fillMaxWidth(
                                fraction = 0.31f
                            ),

                    icon =
                        Icons.Default.Assessment,

                    title =
                        "Total",

                    value =
                        totalData.toString()
                )


                // MASUK
                RekapSummaryCard(

                    modifier =
                        Modifier
                            .fillMaxWidth(
                                fraction = 0.31f
                            ),

                    icon =
                        Icons.Default.CheckCircle,

                    title =
                        "Masuk",

                    value =
                        totalHadir.toString()
                )


                // PULANG
                RekapSummaryCard(

                    modifier =
                        Modifier
                            .fillMaxWidth(
                                fraction = 0.31f
                            ),

                    icon =
                        Icons.Default.AccessTime,

                    title =
                        "Pulang",

                    value =
                        totalPulang.toString()
                )
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // SEARCH
            // ==================================================

            OutlinedTextField(

                value =
                    searchQuery,

                onValueChange = {

                    searchQuery = it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        ),

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(14.dp),

                placeholder = {

                    Text(

                        text =
                            "Cari nama atau tanggal...",

                        fontSize =
                            13.sp
                    )
                },

                leadingIcon = {

                    Icon(

                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Cari",

                        tint =
                            PrimaryGreen
                    )
                },

                trailingIcon = {

                    if (
                        searchQuery.isNotBlank()
                    ) {

                        IconButton(

                            onClick = {

                                searchQuery = ""
                            }
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Close,

                                contentDescription =
                                    "Hapus pencarian"
                            )
                        }
                    }
                },

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            PrimaryGreen,

                        unfocusedBorderColor =
                            Color(0xFFD1D5DB),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // CONTENT
            // ==================================================

            when {

                loading -> {

                    RekapLoading()
                }


                errorMessage.isNotBlank() -> {

                    RekapError(

                        message =
                            errorMessage,

                        onRetry = {

                            refreshKey++
                        }
                    )
                }


                hasilPencarian.isEmpty() -> {

                    RekapEmpty(

                        searchQuery =
                            searchQuery
                    )
                }


                else -> {

                    LazyColumn(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp),

                        contentPadding =
                            PaddingValues(

                                start = 20.dp,

                                top = 0.dp,

                                end = 20.dp,

                                bottom = 24.dp
                            )
                    ) {

                        items(

                            items =
                                hasilPencarian,

                            key = {

                                it.id
                            }

                        ) { data ->

                            RekapAttendanceCard(

                                data =
                                    data
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================
// SUMMARY CARD
// ==========================================================

@Composable
private fun RekapSummaryCard(

    modifier: Modifier,

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    value: String

) {

    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(11.dp)
        ) {

            Surface(

                modifier =
                    Modifier.size(34.dp),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    Color(0xFFE6EEE9)
            ) {

                Box(

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            icon,

                        contentDescription =
                            null,

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(18.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            Text(

                text =
                    title,

                fontSize =
                    10.sp,

                color =
                    TextGray
            )


            Text(

                text =
                    value,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// ATTENDANCE CARD
// ==========================================================

@Composable
private fun RekapAttendanceCard(

    data: DataRekapAbsensi

) {

    val sudahMasuk =
        data.jamMasuk.isNotBlank()


    val sudahPulang =
        data.jamPulang.isNotBlank()


    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(17.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
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
                    .padding(15.dp)
        ) {


            // ==================================================
            // NAMA
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(

                    modifier =
                        Modifier.size(45.dp),

                    shape =
                        CircleShape,

                    color =
                        Color(0xFFE6EEE9)
                ) {

                    Box(

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text =
                                getRekapInitials(
                                    data.nama
                                ),

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                PrimaryGreen
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )


                Column(

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(

                        text =
                            data.nama.ifBlank {
                                "Tanpa Nama"
                            },

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextDark,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Row(

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.CalendarMonth,

                            contentDescription =
                                null,

                            tint =
                                TextGray,

                            modifier =
                                Modifier.size(13.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )


                        Text(

                            text =
                                data.tanggal.ifBlank {
                                    "-"
                                },

                            fontSize =
                                11.sp,

                            color =
                                TextGray
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // ==================================================
            // STATUS
            // ==================================================

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(10.dp),

                color =
                    if (sudahPulang) {

                        Color(0xFFE8F5E9)

                    } else {

                        Color(0xFFFFF7ED)
                    }
            ) {

                Text(

                    text =
                        if (sudahPulang) {

                            "✓ Absensi Lengkap"

                        } else if (sudahMasuk) {

                            "● Sudah Absen Masuk"

                        } else {

                            "Belum Absen"
                        },

                    modifier =
                        Modifier.padding(10.dp),

                    fontSize =
                        11.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        if (sudahPulang) {

                            Color(0xFF15803D)

                        } else if (sudahMasuk) {

                            Color(0xFFC2410C)

                        } else {

                            TextGray
                        }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // ==================================================
            // JAM
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                RekapTimeBox(

                    modifier =
                        Modifier.fillMaxWidth(
                            fraction = 0.48f
                        ),

                    title =
                        "Jam Masuk",

                    value =
                        if (sudahMasuk) {

                            data.jamMasuk

                        } else {

                            "--:--:--"
                        }
                )


                RekapTimeBox(

                    modifier =
                        Modifier.fillMaxWidth(
                            fraction = 0.48f
                        ),

                    title =
                        "Jam Pulang",

                    value =
                        if (sudahPulang) {

                            data.jamPulang

                        } else {

                            "--:--:--"
                        }
                )
            }


            // ==================================================
            // CATATAN
            // ==================================================

            if (
                data.catatan.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Surface(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(10.dp),

                    color =
                        Color(0xFFF8FAF9)
                ) {

                    Column(

                        modifier =
                            Modifier.padding(10.dp)
                    ) {

                        Text(

                            text =
                                "Catatan",

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextGray
                        )


                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )


                        Text(

                            text =
                                data.catatan,

                            fontSize =
                                12.sp,

                            color =
                                TextDark
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// TIME BOX
// ==========================================================

@Composable
private fun RekapTimeBox(

    modifier: Modifier,

    title: String,

    value: String

) {

    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(11.dp),

        color =
            Color(0xFFF8FAF9)
    ) {

        Column(

            modifier =
                Modifier.padding(11.dp)
        ) {

            Text(

                text =
                    title,

                fontSize =
                    10.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )


            Text(

                text =
                    value,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
            )
        }
    }
}


// ==========================================================
// LOADING
// ==========================================================

@Composable
private fun RekapLoading() {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(40.dp)
        )


        CircularProgressIndicator(

            color =
                PrimaryGreen
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Text(

            text =
                "Memuat rekap absensi...",

            fontSize =
                13.sp,

            color =
                TextGray
        )
    }
}


// ==========================================================
// ERROR
// ==========================================================

@Composable
private fun RekapError(

    message: String,

    onRetry: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )


        Icon(

            imageVector =
                Icons.Default.Warning,

            contentDescription =
                null,

            tint =
                Color(0xFFB91C1C),

            modifier =
                Modifier.size(42.dp)
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Text(

            text =
                "Gagal memuat rekap",

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )


        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        Text(

            text =
                message,

            fontSize =
                12.sp,

            color =
                Color(0xFFB91C1C),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        IconButton(

            onClick =
                onRetry
        ) {

            Icon(

                imageVector =
                    Icons.Default.Refresh,

                contentDescription =
                    "Coba lagi",

                tint =
                    PrimaryGreen
            )
        }
    }
}


// ==========================================================
// EMPTY
// ==========================================================

@Composable
private fun RekapEmpty(

    searchQuery: String

) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )


        Surface(

            modifier =
                Modifier.size(70.dp),

            shape =
                CircleShape,

            color =
                Color(0xFFE6EEE9)
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =

                        if (
                            searchQuery.isBlank()
                        ) {

                            Icons.Default.Assessment

                        } else {

                            Icons.Default.Search
                        },

                    contentDescription =
                        null,

                    tint =
                        TextGray,

                    modifier =
                        Modifier.size(32.dp)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        Text(

            text =

                if (
                    searchQuery.isBlank()
                ) {

                    "Belum Ada Rekap"

                } else {

                    "Data Tidak Ditemukan"
                },

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextDark
        )


        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        Text(

            text =

                if (
                    searchQuery.isBlank()
                ) {

                    "Belum ada data absensi di Firestore."

                } else {

                    "Coba gunakan kata kunci pencarian lain."
                },

            fontSize =
                12.sp,

            color =
                TextGray,

            textAlign =
                TextAlign.Center
        )
    }
}


// ==========================================================
// INITIAL
// ==========================================================

private fun getRekapInitials(

    nama: String

): String {

    val parts =
        nama
            .trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }


    return when {

        parts.isEmpty() ->

            "?"


        parts.size == 1 ->

            parts[0]
                .take(2)
                .uppercase()


        else ->

            "${parts.first().first()}${parts.last().first()}"
                .uppercase()
    }
}