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
import com.example.conalepApp.api.AsistenciaExistente
import com.example.conalepApp.api.AsistenciaItem
import com.example.conalepApp.api.ClaseInfo
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch

data class AlumnoEditEstado(
    val alumnoId: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val matricula: String,
    var estado: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellidoPaterno $apellidoMaterno"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceEditScreen(navController: NavController, materiaId: Int, fecha: String) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var claseInfo by remember { mutableStateOf<ClaseInfo?>(null) }
    var alumnosEdit by remember { mutableStateOf<List<AlumnoEditEstado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(materiaId, fecha) {
        if (materiaId == 0 || fecha.isEmpty()) {
            errorMessage = "Parámetros inválidos"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            authRepository.getAsistenciasPorFecha(materiaId, fecha).fold(
                onSuccess = { response ->
                    claseInfo = response.clase
                    alumnosEdit = response.asistencias.map {
                        AlumnoEditEstado(
                            alumnoId = it.alumno_id,
                            nombre = it.nombre,
                            apellidoPaterno = it.apellido_paterno,
                            apellidoMaterno = it.apellido_materno,
                            matricula = it.matricula,
                            estado = it.estado_asistencia
                        )
                    }
                },
                onFailure = {
                    errorMessage = "Error al cargar asistencias"
                }
            )
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Asistencia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = conalepGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                                    text = "Fecha: $fecha",
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val presentes = alumnosEdit.count { it.estado == "Presente" }
                            val ausentes = alumnosEdit.count { it.estado == "Ausente" }
                            val retardos = alumnosEdit.count { it.estado == "Retardo" }
                            val justificados = alumnosEdit.count { it.estado == "Justificado" }

                            EditStatChip("Presentes", presentes, Color(0xFF22C55E))
                            EditStatChip("Ausentes", ausentes, Color(0xFFEF4444))
                            EditStatChip("Retardos", retardos, Color(0xFFF59E0B))
                            EditStatChip("Justificados", justificados, Color(0xFF3B82F6))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(alumnosEdit) { item ->
                                AlumnoEditItem(
                                    alumno = item,
                                    onEstadoChange = { nuevoEstado ->
                                        alumnosEdit = alumnosEdit.map {
                                            if (it.alumnoId == item.alumnoId) {
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

                        Button(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    val asistencias = alumnosEdit.map {
                                        AsistenciaItem(it.alumnoId, it.estado)
                                    }

                                    authRepository.guardarAsistencias(
                                        materiaId = materiaId,
                                        fecha = fecha,
                                        asistencias = asistencias
                                    ).fold(
                                        onSuccess = {
                                            snackbarHostState.showSnackbar("Asistencia actualizada")
                                            navController.popBackStack()
                                        },
                                        onFailure = {
                                            snackbarHostState.showSnackbar("Error al actualizar")
                                        }
                                    )
                                    isSaving = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                        ) {
                            Text(if (isSaving) "Guardando..." else "Actualizar asistencia")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditStatChip(label: String, count: Int, color: Color) {
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
private fun AlumnoEditItem(
    alumno: AlumnoEditEstado,
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
                        text = alumno.nombre.firstOrNull()?.uppercase() ?: "",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alumno.nombreCompleto,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Mat: ${alumno.matricula}",
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
                EditAttendanceButton(
                    label = "Presente",
                    selected = alumno.estado == "Presente",
                    color = Color(0xFF22C55E),
                    onClick = { onEstadoChange("Presente") }
                )
                EditAttendanceButton(
                    label = "Ausente",
                    selected = alumno.estado == "Ausente",
                    color = Color(0xFFEF4444),
                    onClick = { onEstadoChange("Ausente") }
                )
                EditAttendanceButton(
                    label = "Retardo",
                    selected = alumno.estado == "Retardo",
                    color = Color(0xFFF59E0B),
                    onClick = { onEstadoChange("Retardo") }
                )
                EditAttendanceButton(
                    label = "Justificado",
                    selected = alumno.estado == "Justificado",
                    color = Color(0xFF3B82F6),
                    onClick = { onEstadoChange("Justificado") }
                )
            }
        }
    }
}

@Composable
private fun RowScope.EditAttendanceButton(
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
