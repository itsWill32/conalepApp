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

        //   PRE-LOGIN
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

        // POST-LOGIN
        composable("dashboard") { DashboardScreen(navController) }

        composable("profile") { ProfileScreen(navController) }

        composable("notifications") { NotificationsScreen(navController) }
        composable("subjects") { SubjectsScreen(navController) }

        // Rutas de Asistencia
        composable("attendance/{materiaId}") { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getString("materiaId")?.toIntOrNull() ?: 0
            AttendanceScreen(navController, materiaId)
        }
        // Ruta alternativa por si se llama sin argumentos (fallback)
        composable("attendance") {
            AttendanceScreen(navController, 0)
        }

        composable("attendance_history/{materiaId}") { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getString("materiaId")?.toIntOrNull() ?: 0
            AttendanceHistoryScreen(navController, materiaId)
        }

        composable("attendance_edit/{materiaId}/{fecha}") { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getString("materiaId")?.toIntOrNull() ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            AttendanceEditScreen(navController, materiaId, fecha)
        }

        // Rutas de Maestros
        composable("teacher_notifications") {
            TeacherNotificationsScreen(navController)
        }

        composable("create_notification") {
            CreateNotificationScreen(navController)
        }
    }
}