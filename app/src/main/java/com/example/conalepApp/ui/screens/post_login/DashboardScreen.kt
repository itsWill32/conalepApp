package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.api.MateriaAlumnoAPI
import com.example.conalepApp.api.MateriaMaestroAPI
import com.example.conalepApp.api.NotificacionItem
import com.example.conalepApp.api.User
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var user by remember { mutableStateOf<User?>(null) }
    var materiasAlumno by remember { mutableStateOf<List<MateriaAlumnoAPI>>(emptyList()) }
    var materiasMaestro by remember { mutableStateOf<List<MateriaMaestroAPI>>(emptyList()) }
    var notificaciones by remember { mutableStateOf<List<NotificacionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        scope.launch {
            val currentUser = authRepository.getCurrentUser()

            if (currentUser == null) {
                error = "Sesión no válida"
                isLoading = false
                return@launch
            }

            user = currentUser

            // Cargar datos según el tipo de usuario
            if (currentUser.isAlumno) {
                authRepository.getMateriasAlumno().fold(
                    onSuccess = { list -> materiasAlumno = list },
                    onFailure = { /* Ignorar error en dashboard */ }
                )

                authRepository.getMisNotificacionesAlumno().fold(
                    onSuccess = { list -> notificaciones = list },
                    onFailure = { /* Ignorar error en dashboard */ }
                )
            } else if (currentUser.isMaestro) {
                authRepository.getMateriasMaestro().fold(
                    onSuccess = { list -> materiasMaestro = list },
                    onFailure = { /* Ignorar error en dashboard */ }
                )

                authRepository.getMisNotificaciones().fold(
                    onSuccess = { list -> notificaciones = list },
                    onFailure = { /* Ignorar error en dashboard */ }
                )
            }

            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = conalepGreen)
            }

            user == null || error != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = error ?: "No hay sesión activa")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                authRepository.logout()
                                navController.navigate("landing") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF1F5F9)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header con info del usuario
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF2C8769),
                                                Color(0xFF246E55)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${user!!.nombre.firstOrNull()?.uppercase() ?: ""}${user!!.apellido_paterno.firstOrNull()?.uppercase() ?: ""}",
                                            color = Color.White,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "¡Hola, ${user!!.nombre}!",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = when {
                                                user!!.isAlumno -> "Alumno"
                                                user!!.isMaestro -> "Maestro"
                                                user!!.isAdministrador -> "Administrador"
                                                else -> "Usuario"
                                            },
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Menú de acciones rápidas
                    item {
                        Text(
                            text = "Acceso rápido",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                icon = Icons.Default.Person,
                                label = "Mi Perfil",
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("profile") }
                            )

                            QuickActionCard(
                                icon = Icons.Default.Book,
                                label = if (user!!.isMaestro) "Mis Clases" else "Materias",
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("subjects") }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                icon = Icons.Default.Notifications,
                                label = "Notificaciones",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (user!!.isMaestro) {
                                        navController.navigate("teacher_notifications")
                                    } else {
                                        navController.navigate("notifications")
                                    }
                                }
                            )

                            QuickActionCard(
                                icon = Icons.Default.Settings,
                                label = "Ajustes",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Próximamente")
                                    }
                                }
                            )
                        }
                    }

                    // Sección de materias
                    if (user!!.isAlumno && materiasAlumno.isNotEmpty()) {
                        item {
                            Text(
                                text = "Mis Materias",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }

                        items(materiasAlumno.take(3)) { materia ->
                            MateriaAlumnoCard(materia)
                        }

                        if (materiasAlumno.size > 3) {
                            item {
                                TextButton(
                                    onClick = { navController.navigate("subjects") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver todas (${materiasAlumno.size})")
                                }
                            }
                        }
                    }

                    if (user!!.isMaestro && materiasMaestro.isNotEmpty()) {
                        item {
                            Text(
                                text = "Mis Clases",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }

                        items(materiasMaestro.take(3)) { materia ->
                            MateriaMaestroCard(
                                materia = materia,
                                onClick = {
                                    navController.navigate("attendance/${materia.clase_id}")
                                }
                            )
                        }

                        if (materiasMaestro.size > 3) {
                            item {
                                TextButton(
                                    onClick = { navController.navigate("subjects") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver todas (${materiasMaestro.size})")
                                }
                            }
                        }
                    }

                    // Notificaciones recientes
                    if (notificaciones.isNotEmpty()) {
                        item {
                            Text(
                                text = "Notificaciones recientes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }

                        items(notificaciones.take(3)) { notif ->
                            NotificationCard(notif)
                        }

                        if (notificaciones.size > 3) {
                            item {
                                TextButton(
                                    onClick = {
                                        if (user!!.isMaestro) {
                                            navController.navigate("teacher_notifications")
                                        } else {
                                            navController.navigate("notifications")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver todas (${notificaciones.size})")
                                }
                            }
                        }
                    }

                    // Botón de cerrar sesión
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    authRepository.logout()
                                    navController.navigate("landing") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cerrar sesión")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = conalepGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = conalepGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MateriaAlumnoCard(materia: MateriaAlumnoAPI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = conalepGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = conalepGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = materia.nombre_clase,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = materia.codigo_clase,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B)
                    )
                )
            }
        }
    }
}

@Composable
private fun MateriaMaestroCard(
    materia: MateriaMaestroAPI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = conalepGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = conalepGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = materia.nombre_clase,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${materia.codigo_clase} • ${materia.total_estudiantes} alumnos",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B)
                    )
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(notif: NotificacionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = notif.titulo,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notif.mensaje,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF64748B)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
