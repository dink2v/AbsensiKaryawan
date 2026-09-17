package com.example.absensikaryawan.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

// ==========================================================
// BOTTOM NAVIGATION BAR
// Tombol Scan (index 2) dibuat menonjol di tengah sebagai FAB
// ==========================================================

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        BottomNavItem(title = "Beranda", icon = Icons.Default.Home),
        BottomNavItem(title = "Pengajuan", icon = Icons.Default.NoteAdd),
        BottomNavItem(title = "Scan", icon = Icons.Default.QrCodeScanner),
        BottomNavItem(title = "Riwayat", icon = Icons.Default.History),
        BottomNavItem(title = "Setting", icon = Icons.Default.Settings)
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        // ==================================================
        // NAVIGATION BAR DASAR
        // Item index 2 (Scan) dikosongkan supaya jadi
        // "lubang" tempat FAB menumpuk di atasnya
        // ==================================================

        NavigationBar(
            modifier = Modifier.height(74.dp),
            containerColor = Color.White,
            tonalElevation = 6.dp
        ) {

            items.forEachIndexed { index, item ->

                if (index == 2) {

                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        enabled = false,
                        icon = { },
                        label = { },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )

                } else {

                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { onItemSelected(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedItem == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray,
                            indicatorColor = SoftGreen
                        )
                    )
                }
            }
        }

        // ==================================================
        // FAB SCAN — mengambang di tengah, menembus ke atas
        // NavigationBar supaya jadi pusat perhatian
        // ==================================================

        FloatingActionButton(
            onClick = { onItemSelected(2) },
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-26).dp)
                .size(60.dp)
        ) {

            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}