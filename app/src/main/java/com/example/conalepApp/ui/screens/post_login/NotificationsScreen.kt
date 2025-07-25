package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.conalepApp.ui.components.BottomNavigationBar

@Composable
fun NotificationsScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Text("Pantalla de Notificaciones", modifier = Modifier.padding(padding))
    }
}