package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
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
import com.example.conalepApp.api.AlumnoAsistencia
import com.example.conalepApp.api.AsistenciaItem
import com.example.conalepApp.api.ClaseInfo
import com.example.conalepApp.data.AttendanceStatus
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AlumnoConAsistenciaEdit(
    val alumno: AlumnoAsistencia,
    var estado: AttendanceStatus = AttendanceStatus.PRESENT
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceEditScreen(
    navController: NavController,
    materiaId: Int = 0,
    fecha: String = ""
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var claseInfo by remember { mutableStateOf<ClaseInfo?>(null) }
    var alumnosConAsistencia by remember { mutableStateOf<List<AlumnoConAsistenciaEdit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    var estadosIniciales by remember { mutableStateOf<Map<Int, AttendanceStatus>>(emptyMap()) }

    LaunchedEffect(materiaId, fecha) {
        if (materiaId <= 0 || fecha.isEmpty()) {
            errorMessage = "Parámetros inválidos (ID: $materiaId, Fecha: $fecha)"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            authRepository.getAlumnosParaAsistencia(materiaId)
                .onSuccess { response ->
                    claseInfo = response.clase

                    authRepository.getAsistenciasPorFecha(materiaId, fecha)
                        .onSuccess { asistenciasResponse ->
                            val asistenciasMap = asistenciasResponse.asistencias.associateBy { it.alumno_id }

                            alumnosConAsistencia = response.alumnos.map { alumno ->
                                val asistenciaExistente = asistenciasMap[alumno.alumno_id]
                                val estado = when(asistenciaExistente?.estado_asistencia) {
                                    "Presente" -> AttendanceStatus.PRESENT
                                    "Ausente" -> AttendanceStatus.ABSENT
                                    "Retardo" -> AttendanceStatus.LATE
                                    "Justificado" -> AttendanceStatus.PERMISSION
                                    else -> AttendanceStatus.PRESENT
                                }
                                AlumnoConAsistenciaEdit(alumno, estado)
                            }

                            estadosIniciales = alumnosConAsistencia.associate {
                                it.alumno.alumno_id to it.estado
                            }

                            isLoading = false
                        }
                        .onFailure {
                            errorMessage = "No se encontraron asistencias para esta fecha"
                            isLoading = false
                        }
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Error al cargar datos"
                    isLoading = false
                }
        }
    }

    LaunchedEffect(alumnosConAsistencia) {
        if (estadosIniciales.isNotEmpty()) {
            hasChanges = alumnosConAsistencia.any { alumno ->
                estadosIniciales[alumno.alumno.alumno_id] != alumno.estado
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Editar asistencia",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) {
                        }
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onSurface
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = conalepGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cargando asistencia...", color = conalepGreen)
                        }
                    }
                }
            } else if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                            ) {
                                Text("Volver al historial", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                item {
                    AttendanceEditHeaderReal(
                        claseInfo = claseInfo,
                        fecha = fecha,
                        hasChanges = hasChanges,
                        onSave = {
                            scope.launch {
                                isSaving = true
                                val asistencias = alumnosConAsistencia.map { alumnoConAsistencia ->
                                    AsistenciaItem(
                                        alumno_id = alumnoConAsistencia.alumno.alumno_id,
                                        estado = when(alumnoConAsistencia.estado) {
                                            AttendanceStatus.PRESENT -> "Presente"
                                            AttendanceStatus.ABSENT -> "Ausente"
                                            AttendanceStatus.PERMISSION -> "Justificado"
                                            AttendanceStatus.LATE -> "Retardo"
                                        }
                                    )
                                }

                                authRepository.guardarAsistencias(materiaId, fecha, asistencias)
                                    .onSuccess {
                                        estadosIniciales = alumnosConAsistencia.associate {
                                            it.alumno.alumno_id to it.estado
                                        }
                                        hasChanges = false
                                        navController.popBackStack()
                                    }
                                    .onFailure { exception ->
                                        errorMessage = exception.message ?: "Error al guardar cambios"
                                    }
                                isSaving = false
                            }
                        },
                        onHistory = { materiaId ->
                            navController.navigate("attendance_history/$materiaId")
                        },
                        isSaving = isSaving
                    )
                }

                item {
                    AttendanceSummaryEdit(roster = alumnosConAsistencia)
                }

                if (hasChanges) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),

                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "⚠️",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Tienes cambios sin guardar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Lista de estudiantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )
                }

                items(alumnosConAsistencia, key = { it.alumno.alumno_id }) { alumnoConAsistencia ->
                    StudentAttendanceRowEdit(
                        alumnoConAsistencia = alumnoConAsistencia,
                        onStatusChange = { newStatus ->
                            alumnosConAsistencia = alumnosConAsistencia.map {
                                if (it.alumno.alumno_id == alumnoConAsistencia.alumno.alumno_id) {
                                    it.copy(estado = newStatus)
                                } else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceEditHeaderReal(
    claseInfo: ClaseInfo?,
    fecha: String,
    hasChanges: Boolean,
    onSave: () -> Unit,
    onHistory: (Int) -> Unit,
    isSaving: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Editando asistencia",
                color = conalepGreen,
                fontWeight = FontWeight.Bold
            )

            if (claseInfo != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Chip(label = claseInfo.nombre_clase, isSelected = true, fontWeight = FontWeight.Light)
                    Spacer(modifier = Modifier.width(8.dp))
                    Chip(label = claseInfo.codigo_clase, fontWeight = FontWeight.Light)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatearFechaEdit(fecha),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallIconButtonEdit(
                        text = if (isSaving) "Guardando..." else if (hasChanges) "Guardar cambios" else "Guardar",
                        icon = Icons.Default.Description,
                        onClick = onSave,
                        enabled = !isSaving && hasChanges,
                        highlighted = hasChanges
                    )
                    SmallIconButtonEdit(
                        text = "Historial",
                        icon = Icons.Default.History,
                        onClick = {
                            claseInfo?.let { onHistory(it.clase_id) }
                        },
                        enabled = !isSaving
                    )
                }
            }
        }
    }
}

@Composable
fun SmallIconButtonEdit(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    highlighted: Boolean = false
) {
    val backgroundColor = if (highlighted) Color(0xFFFF6B35) else conalepGreen

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.Gray
        ),
        enabled = enabled
    ) {
        if (!enabled && text.contains("Guardando")) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(icon, contentDescription = text, modifier = Modifier.size(14.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.White)
    }
}

@Composable
fun AttendanceSummaryEdit(roster: List<AlumnoConAsistenciaEdit>) {
    val lateColor = Color(0xFFD07F1B)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val presentCount = roster.count { it.estado == AttendanceStatus.PRESENT }
        val absentCount = roster.count { it.estado == AttendanceStatus.ABSENT }
        val permissionCount = roster.count { it.estado == AttendanceStatus.PERMISSION }
        val lateCount = roster.count { it.estado == AttendanceStatus.LATE }

        SummaryCard(modifier = Modifier.weight(1f), count = presentCount, label = "Presente", color = conalepGreen)
        SummaryCard(modifier = Modifier.weight(1f), count = absentCount, label = "Ausente", color = Color.Red)
        SummaryCard(modifier = Modifier.weight(1f), count = permissionCount, label = "Permiso", color = Color.Blue)
        SummaryCard(modifier = Modifier.weight(1f), count = lateCount, label = "Retardo", color = lateColor)
    }
}

@Composable
fun StudentAttendanceRowEdit(
    alumnoConAsistencia: AlumnoConAsistenciaEdit,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${alumnoConAsistencia.alumno.matricula} - ${alumnoConAsistencia.alumno.nombreCompleto}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row {
                StatusIconButton(
                    status = AttendanceStatus.PRESENT,
                    isSelected = alumnoConAsistencia.estado == AttendanceStatus.PRESENT,
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) }
                )
                StatusIconButton(
                    status = AttendanceStatus.ABSENT,
                    isSelected = alumnoConAsistencia.estado == AttendanceStatus.ABSENT,
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) }
                )
                StatusIconButton(
                    status = AttendanceStatus.PERMISSION,
                    isSelected = alumnoConAsistencia.estado == AttendanceStatus.PERMISSION,
                    onClick = { onStatusChange(AttendanceStatus.PERMISSION) }
                )
                StatusIconButton(
                    status = AttendanceStatus.LATE,
                    isSelected = alumnoConAsistencia.estado == AttendanceStatus.LATE,
                    onClick = { onStatusChange(AttendanceStatus.LATE) }
                )
            }
        }
    }
}

private fun formatearFechaEdit(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("EEEE dd 'de' MMMM, yyyy", Locale("es", "ES"))
        val fechaParsed = formatoEntrada.parse(fecha)
        fechaParsed?.let { formatoSalida.format(it) } ?: fecha
    } catch (e: Exception) {
        fecha
    }
}