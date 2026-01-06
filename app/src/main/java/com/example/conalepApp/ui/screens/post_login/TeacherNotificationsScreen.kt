package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.api.NotificacionItem
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherNotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var notificaciones by remember { mutableStateOf<List<NotificacionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Todas", "Pendientes", "Aprobadas", "Rechazadas")
    val statusMap = mapOf(
        0 to null,
        1 to "Pendiente",
        2 to "Aprobada",
        3 to "Rechazada"
    )

    fun cargarNotificaciones(status: String? = null) {
        scope.launch {
            isLoading = true
            errorMessage = ""

            authRepository.getMisNotificaciones(status)
                .onSuccess { notificacionesList ->
                    notificaciones = notificacionesList
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Error al cargar notificaciones"
                    isLoading = false
                }
        }
    }

    LaunchedEffect(Unit) {
        cargarNotificaciones()
    }

    LaunchedEffect(selectedTab) {
        cargarNotificaciones(statusMap[selectedTab])
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Notificaciones",
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
        },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_notification") },
                containerColor = conalepGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear notificación", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = conalepGreen
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = conalepGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando notificaciones...")
                    }
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                errorMessage,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { cargarNotificaciones(statusMap[selectedTab]) },
                                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (notificaciones.isEmpty()) {
                        item {
                            EmptyNotificationsCard(
                                onCreateClick = { navController.navigate("create_notification") }
                            )
                        }
                    } else {
                        items(notificaciones) { notificacion ->
                            TeacherNotificationCard(notificacion = notificacion)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EmptyNotificationsCard(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "📢",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No hay notificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Cuando envíes notificaciones a tus alumnos, aparecerán aquí",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear notificación")
            }
        }
    }
}

@Composable
fun TeacherNotificationCard(notificacion: NotificacionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    notificacion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen,
                    modifier = Modifier.weight(1f)
                )

                StatusChip(status = notificacion.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                notificacion.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tipo: ${getTipoDestinatarioText(notificacion.tipo_destinatario)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    notificacion.fecha_creacion ?: "Sin fecha",
                    style = MaterialTheme.typography.bodySmall,
                    color = conalepGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            if (notificacion.destinatarios_info?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Destinatarios: ${notificacion.destinatarios_info}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status) {
        "Pendiente" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "⏳ Pendiente")
        "Aprobada" -> Triple(Color(0xFFE8F5E8), Color(0xFF2E7D32), "✅ Aprobada")
        "Rechazada" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "❌ Rechazada")
        else -> Triple(Color.Gray, Color.White, status)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun getTipoDestinatarioText(tipo: String): String {
    return when (tipo) {
        "Alumno_Especifico" -> "Alumnos específicos"
        "Materia_Completa" -> "Materia completa"
        "Multiples_Materias" -> "Múltiples materias"
        else -> tipo
    }
}
