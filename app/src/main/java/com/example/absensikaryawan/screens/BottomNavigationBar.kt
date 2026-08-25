package com.example.absensikaryawan.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        BottomNavItem(
            title = "Beranda",
            icon = Icons.Default.Home
        ),

        BottomNavItem(
            title = "Pengajuan",
            icon = Icons.Default.NoteAdd
        ),

        BottomNavItem(
            title = "Scan",
            icon = Icons.Default.QrCodeScanner
        ),

        BottomNavItem(
            title = "Riwayat",
            icon = Icons.Default.History
        ),

        BottomNavItem(
            title = "Setting",
            icon = Icons.Default.Settings
        )
    )

    NavigationBar {

        items.forEachIndexed { index, item ->

            NavigationBarItem(

                selected =
                    selectedItem == index,

                onClick = {
                    onItemSelected(index)
                },

                icon = {

                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                label = {

                    Text(
                        text = item.title
                    )
                }
            )
        }
    }
}