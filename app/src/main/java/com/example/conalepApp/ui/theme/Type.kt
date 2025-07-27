package com.example.conalepApp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.conalepApp.R

val InriaSans = FontFamily(
    Font(R.font.inria_sans_light, FontWeight.Light),
    Font(R.font.inria_sans_regular, FontWeight.Normal),
    Font(R.font.inria_sans_bold, FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = InriaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InriaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = InriaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = InriaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
)