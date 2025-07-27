package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.data.NotificationItem
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val groupedNotifications = remember {
        DummyData.notifications.groupBy {
            when {
                it.timestamp.contains("min") || it.timestamp.contains("hora") -> "Hoy"
                it.timestamp.contains("Ayer") -> "Ayer"
                else -> it.timestamp
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notificaciones",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedNotifications.forEach { (date, notifications) ->
                stickyHeader {
                    NotificationGroupHeader(date = date)
                }
                items(notifications) { notification ->
                    NotificationItemCard(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationGroupHeader(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = conalepGreen
        )
        TextButton(onClick = { /* TODO */ }) {
            Text(
                "Marcar como leído",
                fontWeight = FontWeight.Bold,
                color = conalepGreen
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItemCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "Icono de tarea",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.title,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen
                )
                Text(
                    notification.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    notification.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen
                )
            }
        }
    }
}