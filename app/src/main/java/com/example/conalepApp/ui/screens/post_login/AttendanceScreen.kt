package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.api.AlumnoAsistencia
import com.example.conalepApp.api.AsistenciaItem
import com.example.conalepApp.api.ClaseInfo
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AlumnoConEstado(
    val alumno: AlumnoAsistencia,
    var estado: String = "Presente"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController, materiaId: Int) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var claseInfo by remember { mutableStateOf<ClaseInfo?>(null) }
    var alumnosConEstado by remember { mutableStateOf<List<AlumnoConEstado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    LaunchedEffect(materiaId) {
        if (materiaId == 0) {
            errorMessage = "ID de materia inválido"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            authRepository.getAlumnosParaAsistencia(materiaId)
                .onSuccess { response ->
                    claseInfo = response.clase
                    alumnosConEstado = response.alumnos.map { alumno ->
                        AlumnoConEstado(alumno, "Presente")
                    }
                }
                .onFailure {
                    errorMessage = "Error al cargar alumnos"
                }
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tomar Asistencia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("attendance_history/$materiaId")
                    }) {
                        Icon(Icons.Default.History, "Historial", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = conalepGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9))
        ) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = conalepGreen)
                }

                errorMessage.isNotEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage, color = Color.Red)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Header con info de clase
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = claseInfo?.nombre_clase ?: "",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                                Text(
                                    text = "Código: ${claseInfo?.codigo_clase ?: ""}",
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Fecha: $selectedDate",
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Total alumnos: ${alumnosConEstado.size}",
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resumen de estados
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val presentes = alumnosConEstado.count { it.estado == "Presente" }
                            val ausentes = alumnosConEstado.count { it.estado == "Ausente" }
                            val retardos = alumnosConEstado.count { it.estado == "Retardo" }
                            val justificados = alumnosConEstado.count { it.estado == "Justificado" }

                            StatChip("Presentes", presentes, Color(0xFF22C55E))
                            StatChip("Ausentes", ausentes, Color(0xFFEF4444))
                            StatChip("Retardos", retardos, Color(0xFFF59E0B))
                            StatChip("Justificados", justificados, Color(0xFF3B82F6))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista de alumnos
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(alumnosConEstado) { item ->
                                AlumnoAttendanceItem(
                                    alumnoConEstado = item,
                                    onEstadoChange = { nuevoEstado ->
                                        alumnosConEstado = alumnosConEstado.map {
                                            if (it.alumno.alumno_id == item.alumno.alumno_id) {
                                                it.copy(estado = nuevoEstado)
                                            } else {
                                                it
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón guardar
                        Button(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    val asistencias = alumnosConEstado.map {
                                        AsistenciaItem(it.alumno.alumno_id, it.estado)
                                    }

                                    authRepository.guardarAsistencias(
                                        materiaId = materiaId,
                                        fecha = selectedDate,
                                        asistencias = asistencias
                                    ).onSuccess {
                                        snackbarHostState.showSnackbar("Asistencia guardada correctamente")
                                        navController.popBackStack()
                                    }.onFailure {
                                        snackbarHostState.showSnackbar("Error al guardar asistencia")
                                    }
                                    isSaving = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                        ) {
                            Text(if (isSaving) "Guardando..." else "Guardar asistencia")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color) {
    Surface(
        modifier = Modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = color)
            )
        }
    }
}

@Composable
private fun AlumnoAttendanceItem(
    alumnoConEstado: AlumnoConEstado,
    onEstadoChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(conalepGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = alumnoConEstado.alumno.nombre.firstOrNull()?.uppercase() ?: "",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alumnoConEstado.alumno.nombreCompleto,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Mat: ${alumnoConEstado.alumno.matricula} | ${alumnoConEstado.alumno.grado}${alumnoConEstado.alumno.grupo}",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttendanceButton(
                    label = "Presente",
                    selected = alumnoConEstado.estado == "Presente",
                    color = Color(0xFF22C55E),
                    onClick = { onEstadoChange("Presente") }
                )
                AttendanceButton(
                    label = "Ausente",
                    selected = alumnoConEstado.estado == "Ausente",
                    color = Color(0xFFEF4444),
                    onClick = { onEstadoChange("Ausente") }
                )
                AttendanceButton(
                    label = "Retardo",
                    selected = alumnoConEstado.estado == "Retardo",
                    color = Color(0xFFF59E0B),
                    onClick = { onEstadoChange("Retardo") }
                )
                AttendanceButton(
                    label = "Justificado",
                    selected = alumnoConEstado.estado == "Justificado",
                    color = Color(0xFF3B82F6),
                    onClick = { onEstadoChange("Justificado") }
                )
            }
        }
    }
}

@Composable
private fun RowScope.AttendanceButton(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        color = if (selected) color else color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else color,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}
