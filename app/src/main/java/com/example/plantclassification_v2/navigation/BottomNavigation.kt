package com.example.plantclassification_v2.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomMenu(
    navController: NavController
){
    val menuItems = listOf<BottomMenuScreen>(
        BottomMenuScreen.MainScreen,
        BottomMenuScreen.HistoryScreen,
        BottomMenuScreen.InfoScreen
    )
    Column {
        Divider(modifier = Modifier
            .fillMaxWidth(),
            color = Color.Gray,
            thickness = 0.5.dp
        )
        BottomNavigation(
            contentColor = Color.White,
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 20.dp,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            menuItems.forEach {
                BottomNavigationItem(
                    label = { Text(text = it.title) },
                    alwaysShowLabel = true,
                    selectedContentColor = MaterialTheme.colorScheme.outline,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    selected = currentRoute == it.route,
                    onClick = {
                        if (currentRoute != it.route) {
                            navController.navigate(it.route) {
                                popUpTo(it.route) { inclusive = true }
                            }
                        }
                    }, icon = { Icon(imageVector = it.icon, contentDescription = it.title) }
                )
            }
        }
    }

}

@Composable
fun BackBottom(
    navController: NavController,
    initHistoryPage:()->Unit = {}
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
                if (currentRoute == Screen.HistoryScreen.route){
                    initHistoryPage()
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}