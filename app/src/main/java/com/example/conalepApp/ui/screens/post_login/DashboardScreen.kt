package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserHeaderCard(navController)
            Text("Inicio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            NotificationsSummaryCard(navController)
            SubjectsSummaryCard(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHeaderCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navController.navigate("profile") }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = DummyData.loggedInUser.profilePic),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(DummyData.loggedInUser.name.split(" ").take(2).joinToString(" "), fontWeight = FontWeight.Bold)
                Text("Estudiante", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { navController.navigate("notifications") }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSummaryCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp),
        onClick = { navController.navigate("notifications") }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Últimas notificaciones", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DummyData.notifications.firstOrNull()?.title ?: "No hay notificaciones",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsSummaryCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp),
        onClick = { navController.navigate("subjects") }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mis materias", style = MaterialTheme.typography.titleMedium)
        }
    }
}