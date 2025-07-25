package com.example.conalepApp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conalepApp.ui.screens.post_login.*
import com.example.conalepApp.ui.screens.pre_login.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("landing") { LandingScreen(navController) }
        composable("school_info") { SchoolInfoScreen(navController) }
        composable("careers_list") { CareersListScreen(navController) }
        composable("career_detail/{careerName}") { backStackEntry ->
            CareerDetailScreen(
                navController = navController,
                careerName = backStackEntry.arguments?.getString("careerName").orEmpty()
            )
        }
        composable("about_us") { AboutUsScreen(navController) }
        composable("about_school") { AboutSchoolScreen(navController) }


        composable("dashboard") { DashboardScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }
        composable("subjects") { SubjectsScreen(navController) }
        composable("attendance") { AttendanceScreen(navController) }

    }
}