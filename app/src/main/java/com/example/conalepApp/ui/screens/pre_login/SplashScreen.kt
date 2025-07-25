package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        navController.navigate("landing") { popUpTo("splash") { inclusive = true } }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(conalepGreen),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.begin_logo),
            contentDescription = "Logo Conalep",
            modifier = Modifier.size(150.dp)
        )
    }
}