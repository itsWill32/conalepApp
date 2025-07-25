package com.example.conalepApp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.conalepApp.R
import com.example.conalepApp.ui.theme.conalepGreen

@Composable
fun BottomNavigationBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(conalepGreen)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                BottomNavItem("profile", R.drawable.ic_settings_outlined),
                BottomNavItem("notifications", R.drawable.ic_messages_outline),
                BottomNavItem("dashboard", R.drawable.ic_home_outlined),
                BottomNavItem("subjects", R.drawable.ic_school_outlined),
                BottomNavItem("profile", R.drawable.ic_person_outlined)
            )

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                CustomBottomNavItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(val route: String, @DrawableRes val iconRes: Int)

@Composable
fun CustomBottomNavItem(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.route,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(24.dp)
                .height(2.dp)
                .background(
                    if (isSelected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}