package com.example.plantclassification_v2.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomMenuScreen(
    val route:String,
    val icon: ImageVector,
    val title:String
) {
    object MainScreen:BottomMenuScreen(route = "main_screen", icon = Icons.Default.Home, title = "首页")
    object HistoryScreen:BottomMenuScreen(route = "history_screen", icon = Icons.Default.DateRange, title = "历史记录")
    object InfoScreen:BottomMenuScreen(route = "info_screen", icon = Icons.Default.Info, title = "关于")
}