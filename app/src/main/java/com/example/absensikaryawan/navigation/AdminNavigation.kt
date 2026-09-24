package com.example.absensikaryawan.navigation

import android.util.Log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.absensikaryawan.ThemeDataStore
import com.example.absensikaryawan.data.PengajuanData
import com.example.absensikaryawan.screens.ThemeMode
import com.example.absensikaryawan.repository.ChatRoom

import com.example.absensikaryawan.repository.screens.AdminChatDetailScreen
import com.example.absensikaryawan.repository.screens.AdminChatListScreen
import com.example.absensikaryawan.repository.screens.AdminDashboardScreen
import com.example.absensikaryawan.repository.screens.AdminNotifikasiScreen
import com.example.absensikaryawan.repository.screens.AdminSettingsScreen
import com.example.absensikaryawan.repository.screens.ApprovalScreen
import com.example.absensikaryawan.repository.screens.BantuanScreen
import com.example.absensikaryawan.repository.screens.DetailPengajuanScreen
import com.example.absensikaryawan.repository.screens.KaryawanScreen
import com.example.absensikaryawan.repository.screens.ProfileScreen
import com.example.absensikaryawan.repository.screens.RekapAdminScreen
import com.example.absensikaryawan.screens.TampilanScreen
import com.example.absensikaryawan.repository.screens.TentangAplikasiScreen
import com.example.absensikaryawan.repository.screens.AdminQrSettingScreen

import kotlinx.coroutines.launch


// ==========================================================
// WARNA BOTTOM NAV
// ==========================================================

private val BottomNavGreen =
    Color(0xFF2E7D32)


// ==========================================================
// ADMIN SCREEN
// ==========================================================

private enum class AdminScreen {

    Dashboard,

    Notifikasi,

    Chat,

    Approval,

    ApprovalDetail,

    Karyawan,

    Rekap,

    Settings,

    QrKantor,

    Profile,

    Tampilan,

    Bantuan,

    TentangAplikasi
}


// ==========================================================
// ADMIN BOTTOM ITEM
// ==========================================================

private data class AdminBottomItem(

    val screen: AdminScreen,

    val label: String,

    val icon:
    androidx.compose.ui.graphics.vector.ImageVector
)


// ==========================================================
// ADMIN NAVIGATION
// ==========================================================

@Composable
fun AdminNavigation(
    onLogout: () -> Unit
) {

    // ======================================================
    // CONTEXT
    // ======================================================

    val context =
        LocalContext.current


    // ======================================================
    // THEME DATASTORE
    // ======================================================

    val themeDataStore =
        remember {

            _root_ide_package_.com.example.absensikaryawan.ThemeDataStore(
                context
            )
        }


    // ======================================================
    // THEME MODE
    // ======================================================

    val selectedThemeMode by
    themeDataStore.themeMode.collectAsState(
        initial =
            ThemeMode.TERANG
    )


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope =
        rememberCoroutineScope()


    // ======================================================
    // SCREEN
    // ======================================================

    var currentScreen by
    remember {

        mutableStateOf(
            AdminScreen.Dashboard
        )
    }


    // ======================================================
    // CHAT ROOM TERPILIH
    // ======================================================

    var chatRoomTerpilih by
    remember {

        mutableStateOf<ChatRoom?>(
            null
        )
    }


    // ======================================================
    // APPROVAL TERPILIH
    // ======================================================

    var selectedApprovalId by
    remember {

        mutableStateOf("")
    }

    var selectedApproval by
    remember {

        mutableStateOf<PengajuanData?>(
            null
        )
    }


    // ======================================================
    // BOTTOM MENU
    // ======================================================

    val bottomItems =
        listOf(

            AdminBottomItem(

                screen =
                    AdminScreen.Dashboard,

                label =
                    "Beranda",

                icon =
                    Icons.Default.Home
            ),

            AdminBottomItem(

                screen =
                    AdminScreen.Approval,

                label =
                    "Approval",

                Icons.AutoMirrored.Filled.NoteAdd
            ),

            AdminBottomItem(

                screen =
                    AdminScreen.Karyawan,

                label =
                    "Karyawan",

                icon =
                    Icons.Default.People
            ),

            AdminBottomItem(

                screen =
                    AdminScreen.Rekap,

                label =
                    "Rekap",

                icon =
                    Icons.Default.Assessment
            ),

            AdminBottomItem(

                screen =
                    AdminScreen.Settings,

                label =
                    "Setting",

                icon =
                    Icons.Default.Settings
            )
        )


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        bottomBar = {

            NavigationBar(

                containerColor =
                    Color.White,

                tonalElevation =
                    6.dp
            ) {

                bottomItems.forEach { item ->

                    val selected =

                        when (
                            item.screen
                        ) {

                            AdminScreen.Dashboard ->

                                currentScreen ==
                                        AdminScreen.Dashboard

                            AdminScreen.Approval ->

                                currentScreen ==
                                        AdminScreen.Approval

                            AdminScreen.Karyawan ->

                                currentScreen ==
                                        AdminScreen.Karyawan

                            AdminScreen.Rekap ->

                                currentScreen ==
                                        AdminScreen.Rekap

                            AdminScreen.Settings ->

                                currentScreen in
                                        setOf(

                                            AdminScreen.Settings,

                                            AdminScreen.Profile,

                                            AdminScreen.Tampilan,

                                            AdminScreen.Bantuan,

                                            AdminScreen.TentangAplikasi
                                        )

                            else ->
                                false
                        }


                    NavigationBarItem(

                        selected =
                            selected,

                        onClick = {

                            Log.d(
                                "ADMIN_NAV",
                                "BOTTOM = ${item.label}"
                            )

                            currentScreen =
                                item.screen

                            if (
                                item.screen !=
                                AdminScreen.Chat
                            ) {

                                chatRoomTerpilih =
                                    null
                            }

                            if (
                                item.screen !=
                                AdminScreen.Approval
                            ) {

                                selectedApproval =
                                    null

                                selectedApprovalId =
                                    ""
                            }
                        },

                        icon = {

                            Icon(

                                imageVector =
                                    item.icon,

                                contentDescription =
                                    item.label,

                                modifier =
                                    Modifier.size(

                                        if (
                                            item.screen ==
                                            AdminScreen.Dashboard
                                        ) {

                                            25.dp

                                        } else {

                                            23.dp
                                        }
                                    )
                            )
                        },

                        label = {

                            Text(

                                text =
                                    item.label,

                                fontSize =
                                    11.sp
                            )
                        },

                        colors =
                            NavigationBarItemDefaults.colors(

                                selectedIconColor =
                                    BottomNavGreen,

                                selectedTextColor =
                                    BottomNavGreen,

                                unselectedIconColor =
                                    Color.Gray,

                                unselectedTextColor =
                                    Color.Gray,

                                indicatorColor =
                                    BottomNavGreen.copy(
                                        alpha =
                                            0.12f
                                    )
                            )
                    )
                }
            }
        }

    ) { paddingValues ->

        // ==================================================
        // CONTENT
        // ==================================================

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
        ) {

            when (
                currentScreen
            ) {

                // ==================================================
                // DASHBOARD
                // ==================================================

                AdminScreen.Dashboard -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminDashboardScreen(

                        onApproval = {

                            Log.d(
                                "ADMIN_NAV",
                                "APPROVAL DIKLIK"
                            )

                            selectedApprovalId =
                                ""

                            selectedApproval =
                                null

                            currentScreen =
                                AdminScreen.Approval
                        },

                        onEmployees = {

                            Log.d(
                                "ADMIN_NAV",
                                "KARYAWAN DIKLIK"
                            )

                            currentScreen =
                                AdminScreen.Karyawan
                        },

                        onRecap = {

                            Log.d(
                                "ADMIN_NAV",
                                "REKAP DIKLIK"
                            )

                            currentScreen =
                                AdminScreen.Rekap
                        },

                        onSettings = {

                            Log.d(
                                "ADMIN_NAV",
                                "SETTING DIKLIK"
                            )

                            currentScreen =
                                AdminScreen.Settings
                        },

                        onChat = {

                            Log.d(
                                "ADMIN_NAV",
                                "CHAT DIKLIK"
                            )

                            currentScreen =
                                AdminScreen.Chat
                        },

                        onNotification = {

                            Log.d(
                                "ADMIN_NAV",
                                "NOTIFIKASI ADMIN DIKLIK"
                            )

                            currentScreen =
                                AdminScreen.Notifikasi
                        }
                    )
                }


                // ==================================================
                // NOTIFIKASI
                // ==================================================

                AdminScreen.Notifikasi -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminNotifikasiScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Dashboard
                        },

                        onApprovalClick = { relatedId ->

                            Log.d(
                                "ADMIN_NAV",
                                "NOTIFIKASI -> APPROVAL: $relatedId"
                            )

                            selectedApprovalId =
                                relatedId

                            selectedApproval =
                                null

                            currentScreen =
                                AdminScreen.Approval
                        },

                        onRekapClick = {

                            Log.d(
                                "ADMIN_NAV",
                                "NOTIFIKASI -> REKAP"
                            )

                            currentScreen =
                                AdminScreen.Rekap
                        },

                        onChatClick = {

                            Log.d(
                                "ADMIN_NAV",
                                "NOTIFIKASI -> CHAT"
                            )

                            currentScreen =
                                AdminScreen.Chat
                        }
                    )
                }


                // ==================================================
                // CHAT
                // ==================================================

                AdminScreen.Chat -> {

                    val room =
                        chatRoomTerpilih

                    if (
                        room == null
                    ) {

                        _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminChatListScreen(

                            onBack = {

                                currentScreen =
                                    AdminScreen.Dashboard
                            },

                            onStaffClick = { selectedRoom ->

                                chatRoomTerpilih =
                                    selectedRoom
                            }
                        )

                    } else {

                        _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminChatDetailScreen(

                            room =
                                room,

                            onBack = {

                                chatRoomTerpilih =
                                    null
                            }
                        )
                    }
                }


                // ==================================================
                // APPROVAL
                // ==================================================

                AdminScreen.Approval -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ApprovalScreen(

                        initialDocumentId =
                            selectedApprovalId,

                        onDetailClick = { pengajuan ->

                            Log.d(
                                "ADMIN_NAV",
                                "DETAIL APPROVAL: ${pengajuan.id}"
                            )

                            selectedApproval =
                                pengajuan

                            selectedApprovalId =
                                ""

                            currentScreen =
                                AdminScreen.ApprovalDetail
                        }
                    )
                }


                // ==================================================
                // APPROVAL DETAIL
                // ==================================================

                AdminScreen.ApprovalDetail -> {

                    val pengajuan =
                        selectedApproval

                    if (
                        pengajuan != null
                    ) {

                        _root_ide_package_.com.example.absensikaryawan.repository.screens.DetailPengajuanScreen(

                            documentId =
                                pengajuan.id,

                            jenis =
                                pengajuan.jenis,

                            tanggal =
                                pengajuan.tanggal,

                            status =
                                pengajuan.status,

                            jamPulang =
                                pengajuan.jamPulang,

                            jamKeluar =
                                pengajuan.jamKeluar,

                            jamKembali =
                                pengajuan.jamKembali,

                            tanggalMulai =
                                pengajuan.tanggalMulai,

                            tanggalSelesai =
                                pengajuan.tanggalSelesai,

                            alasan =
                                pengajuan.alasan,

                            // ==========================================
                            // DATA JALUR APPROVAL
                            // ==========================================

                            approvalChain =
                                pengajuan.approvalChain,

                            approvalStatuses =
                                pengajuan.approvalStatuses,

                            currentApproverUid =
                                pengajuan.currentApproverUid,

                            currentApproverName =
                                pengajuan.currentApproverName,

                            currentApproverJabatan =
                                pengajuan.currentApproverJabatan,

                            approvalLocked =
                                pengajuan.approvalLocked,

                            onBack = {

                                selectedApproval =
                                    null

                                currentScreen =
                                    AdminScreen.Approval
                            }
                        )

                    } else {

                        currentScreen =
                            AdminScreen.Approval
                    }
                }


                // ==================================================
                // KARYAWAN
                // ==================================================

                AdminScreen.Karyawan -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.KaryawanScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Dashboard
                        }
                    )
                }


                // ==================================================
                // REKAP
                // ==================================================

                AdminScreen.Rekap -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.RekapAdminScreen()
                }


                // ==================================================
                // SETTINGS
                // ==================================================

                AdminScreen.Settings -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminSettingsScreen(

                        onTampilan = {

                            currentScreen =
                                AdminScreen.Tampilan
                        },

                        onQrKantor = {

                            currentScreen =
                                AdminScreen.QrKantor
                        },

                        onBantuan = {

                            currentScreen =
                                AdminScreen.Bantuan
                        },

                        onTentangAplikasi = {

                            currentScreen =
                                AdminScreen.TentangAplikasi
                        },

                        onLogout = {

                            Log.d(
                                "ADMIN_NAV",
                                "ADMIN LOGOUT"
                            )

                            onLogout()
                        }
                    )
                }


                // ==================================================
                // QR KANTOR
                // ==================================================

                AdminScreen.QrKantor -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.AdminQrSettingScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Settings
                        }
                    )
                }


                // ==================================================
                // PROFILE
                // ==================================================

                AdminScreen.Profile -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.ProfileScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Settings
                        }
                    )
                }


                // ==================================================
                // TAMPILAN
                // ==================================================

                AdminScreen.Tampilan -> {

                    _root_ide_package_.com.example.absensikaryawan.screens.TampilanScreen(

                        selectedMode =
                            selectedThemeMode,

                        onModeSelected = { mode ->

                            scope.launch {

                                themeDataStore
                                    .saveThemeMode(
                                        mode
                                    )
                            }
                        },

                        onBack = {

                            currentScreen =
                                AdminScreen.Settings
                        }
                    )
                }


                // ==================================================
                // BANTUAN
                // ==================================================

                AdminScreen.Bantuan -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.BantuanScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Settings
                        },

                        onChatAdmin = {

                            currentScreen =
                                AdminScreen.Chat
                        }
                    )
                }


                // ==================================================
                // TENTANG APLIKASI
                // ==================================================

                AdminScreen.TentangAplikasi -> {

                    _root_ide_package_.com.example.absensikaryawan.repository.screens.TentangAplikasiScreen(

                        onBack = {

                            currentScreen =
                                AdminScreen.Settings
                        }
                    )
                }
            }
        }
    }
}