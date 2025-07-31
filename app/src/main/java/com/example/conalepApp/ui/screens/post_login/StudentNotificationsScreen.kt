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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.api.NotificacionItem
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StudentNotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var notificaciones by remember { mutableStateOf<List<NotificacionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Función para cargar notificaciones
    fun cargarNotificaciones() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            authRepository.getMisNotificacionesAlumno()
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

    // Cargar notificaciones al inicio
    LaunchedEffect(Unit) {
        cargarNotificaciones()
    }

    // Agrupar notificaciones por fecha
    val groupedNotifications = remember(notificaciones) {
        notificaciones.groupBy { notificacion ->
            formatearFechaParaGrupo(notificacion.fecha_creacion ?: "Sin fecha")
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
                actions = {
                    IconButton(onClick = { cargarNotificaciones() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = conalepGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        when {
            isLoading -> {
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
            }

            errorMessage.isNotEmpty() -> {
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
                                onClick = { cargarNotificaciones() },
                                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            notificaciones.isEmpty() -> {
                EmptyNotificationsView()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedNotifications.forEach { (date, notifications) ->
                        stickyHeader {
                            StudentNotificationGroupHeader(date = date)

                        }
                        items(notifications) { notification ->
                            StudentNotificationCard(notification = notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "📬",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No tienes notificaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Cuando tus profesores envíen notificaciones, aparecerán aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StudentNotificationGroupHeader(date: String) {
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
        // Nota: Funcionalidad de "Marcar como leído" se puede implementar después
        // TextButton(onClick = { /* TODO */ }) {
        //     Text(
        //         "Marcar como leído",
        //         fontWeight = FontWeight.Bold,
        //         color = conalepGreen
        //     )
        // }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentNotificationCard(notification: NotificacionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de notificación basado en el tipo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.tipo_destinatario).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getNotificationIcon(notification.tipo_destinatario)),
                    contentDescription = "Icono de notificación",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.titulo,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    notification.mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar tipo de notificación
                Text(
                    getTipoNotificacionText(notification.tipo_destinatario),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                // Indicador de nueva notificación (opcional)
                // if (!notification.isRead) {
                //     Box(
                //         modifier = Modifier
                //             .size(8.dp)
                //             .clip(CircleShape)
                //             .background(Color(0xFF4CAF50))
                //     )
                // }
                // Spacer(modifier = Modifier.height(8.dp))

                Text(
                    formatearTiempoRelativo(notification.fecha_creacion ?: "Sin fecha"),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen
                )
            }
        }
    }
}

// Funciones helper para formatear fechas y obtener iconos
private fun formatearFechaParaGrupo(fechaCreacion: String): String {
    if (fechaCreacion.isNullOrBlank()) {
        return "Sin fecha"
    }
    return try {
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = formatoEntrada.parse(fechaCreacion)

        if (fecha != null) {
            val ahora = Date()
            val diffMillis = ahora.time - fecha.time
            val diffHoras = diffMillis / (1000 * 60 * 60)
            val diffDias = diffMillis / (1000 * 60 * 60 * 24)

            when {
                diffHoras < 24 -> "Hoy"
                diffDias == 1L -> "Ayer"
                diffDias < 7 -> {
                    val formatoDia = SimpleDateFormat("EEEE", Locale("es", "ES"))
                    formatoDia.format(fecha)
                }
                else -> {
                    val formatoFecha = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                    formatoFecha.format(fecha)
                }
            }
        } else {
            fechaCreacion
        }
    } catch (e: Exception) {
        "Sin fecha"
    }
}

private fun formatearTiempoRelativo(fechaCreacion: String): String {
    if (fechaCreacion.isNullOrBlank()) {
        return "Sin fecha"
    }
    return try {
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = formatoEntrada.parse(fechaCreacion)

        if (fecha != null) {
            val ahora = Date()
            val diffMillis = ahora.time - fecha.time
            val diffMinutos = diffMillis / (1000 * 60)
            val diffHoras = diffMillis / (1000 * 60 * 60)
            val diffDias = diffMillis / (1000 * 60 * 60 * 24)

            when {
                diffMinutos < 1 -> "Ahora"
                diffMinutos < 60 -> "Hace ${diffMinutos}min"
                diffHoras < 24 -> "Hace ${diffHoras}h"
                diffDias == 1L -> "Ayer"
                else -> {
                    val formatoFecha = SimpleDateFormat("dd/MM", Locale.getDefault())
                    formatoFecha.format(fecha)
                }
            }
        } else {
            fechaCreacion
        }
    } catch (e: Exception) {
        "Sin fecha"
    }
}

private fun getNotificationIcon(tipoDestinatario: String): Int {
    return when (tipoDestinatario) {
        "Materia_Completa" -> R.drawable.ic_book
        "Multiples_Materias" -> R.drawable.ic_book
        "Alumno_Especifico" -> R.drawable.ic_book
        else -> R.drawable.ic_notifications_bell
    }
}

private fun getNotificationColor(tipoDestinatario: String): Color {
    return when (tipoDestinatario) {
        "Materia_Completa" -> Color(0xFF2196F3)
        "Multiples_Materias" -> Color(0xFF9C27B0)
        "Alumno_Especifico" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}

private fun getTipoNotificacionText(tipoDestinatario: String): String {
    return when (tipoDestinatario) {
        "Materia_Completa" -> "📚 Notificación de materia"
        "Multiples_Materias" -> "📚 Notificación general"
        "Alumno_Especifico" -> "👤 Notificación personal"
        else -> "📢 Notificación"
    }
}