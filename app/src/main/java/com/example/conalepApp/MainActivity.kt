package com.example.conalepApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.conalepApp.navigation.AppNavigation
import com.example.conalepApp.ui.theme.ConalepAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConalepAppTheme {
                AppNavigation()
            }
        }
    }
}