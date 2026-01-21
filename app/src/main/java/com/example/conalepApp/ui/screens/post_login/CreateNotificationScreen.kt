package com.example.conalepApp.ui.screens.postlogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import androidx.compose.foundation.horizontalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipoDestinatario by remember { mutableStateOf("TODOS_MIS_ALUMNOS") }

    var errorMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val tiposDestinatario = listOf(
        "TODOS_MIS_ALUMNOS" to "Todos mis alumnos",
        "ALUMNOS_CLASE" to "Por materia",
        "ALUMNOS_ESPECIFICOS" to "Alumnos específicos"
    )

    var selectedMaterias by remember { mutableStateOf(emptySet<Int>()) }
    var selectedAlumnos by remember { mutableStateOf(emptySet<Int>()) }

    var destinatarios by remember { mutableStateOf<com.example.conalepApp.api.NotificacionDestinatarios?>(null) }
    var isLoadingDestinatarios by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            authRepository.getDestinatariosParaNotificacion()
                .onSuccess { data ->
                    destinatarios = data
                    isLoadingDestinatarios = false
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Error al cargar destinatarios"
                    isLoadingDestinatarios = false
                }
        }
    }

    fun enviarNotificacion() {
        if (titulo.isBlank() || mensaje.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }

        val destinatariosIds = when (tipoDestinatario) {
            "TODOS_MIS_ALUMNOS" -> emptyList()
            "ALUMNOS_CLASE" -> {
                if (selectedMaterias.isEmpty()) {
                    errorMessage = "Selecciona al menos una materia"
                    return
                }
                selectedMaterias.toList()
            }
            "ALUMNOS_ESPECIFICOS" -> {
                if (selectedAlumnos.isEmpty()) {
                    errorMessage = "Selecciona al menos un alumno"
                    return
                }
                selectedAlumnos.toList()
            }
            else -> emptyList()
        }

        isSending = true
        errorMessage = ""

        scope.launch {
            authRepository.crearNotificacion(
                titulo = titulo,
                mensaje = mensaje,
                tipoDestinatario = tipoDestinatario,
                destinatarios = destinatariosIds
            ).fold(
                onSuccess = {
                    showSuccess = true
                    titulo = ""
                    mensaje = ""
                    tipoDestinatario = "TODOS_MIS_ALUMNOS"
                    selectedMaterias = emptySet()
                    selectedAlumnos = emptySet()
                    searchQuery = ""
                },
                onFailure = { error ->
                    errorMessage = error.message ?: "Error al enviar notificación"
                }
            )
            isSending = false
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = conalepGreen) },
            title = { Text("¡Notificación enviada!") },
            text = { Text("Tu notificación está pendiente de aprobación por un administrador.") },
            confirmButton = {
                TextButton(onClick = { showSuccess = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Nueva Notificación",
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
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "ℹ️ Tu notificación será revisada por un administrador antes de ser enviada a los alumnos.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF1E40AF)
                )
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                placeholder = { Text("Ej. Recordatorio de tarea") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = conalepGreen,
                    focusedLabelColor = conalepGreen
                )
            )

            Column {
                Text(
                    "¿A quién va dirigida?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tiposDestinatario.forEach { (tipo, label) ->
                        FilterChip(
                            selected = tipoDestinatario == tipo,
                            onClick = {
                                tipoDestinatario = tipo
                                selectedMaterias = emptySet()
                                selectedAlumnos = emptySet()
                                searchQuery = ""
                            },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = conalepGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            when (tipoDestinatario) {
                "ALUMNOS_CLASE" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Selecciona materias",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (isLoadingDestinatarios) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                // ✅ CORREGIDO: mis_materias en lugar de materias
                                destinatarios?.mis_materias?.forEach { materia ->
                                    val isSelected = selectedMaterias.contains(materia.clase_id)
                                    Card(
                                        onClick = {
                                            selectedMaterias = if (isSelected) {
                                                selectedMaterias - materia.clase_id
                                            } else {
                                                selectedMaterias + materia.clase_id
                                            }
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) conalepGreen else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    materia.nombre_clase,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    "Código: ${materia.codigo_clase}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                "${materia.total_alumnos} alumnos",
                                                fontSize = 12.sp,
                                                color = if (isSelected) conalepGreen else Color(0xFF64748B),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                if (selectedMaterias.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "${selectedMaterias.size} ${if (selectedMaterias.size == 1) "materia seleccionada" else "materias seleccionadas"}",
                                        fontSize = 12.sp,
                                        color = conalepGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                "ALUMNOS_ESPECIFICOS" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Selecciona alumnos",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar por nombre, matrícula o grupo") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = null)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = conalepGreen,
                                    focusedLabelColor = conalepGreen
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isLoadingDestinatarios) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                // ✅ CORREGIDO: mis_alumnos en lugar de alumnos
                                val alumnosFiltrados = destinatarios?.mis_alumnos?.filter { alumno ->
                                    val query = searchQuery.lowercase()
                                    alumno.nombre.lowercase().contains(query) ||
                                            alumno.apellido_paterno.lowercase().contains(query) ||
                                            alumno.apellido_materno.lowercase().contains(query) ||
                                            alumno.matricula.lowercase().contains(query) ||
                                            "${alumno.grado}${alumno.grupo}".lowercase().contains(query)
                                } ?: emptyList()

                                if (alumnosFiltrados.isEmpty()) {
                                    Text(
                                        "No se encontraron alumnos",
                                        modifier = Modifier.padding(16.dp),
                                        color = Color(0xFF64748B)
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 320.dp)
                                    ) {
                                        items(alumnosFiltrados) { alumno ->
                                            val isSelected = selectedAlumnos.contains(alumno.alumno_id)
                                            Card(
                                                onClick = {
                                                    selectedAlumnos = if (isSelected) {
                                                        selectedAlumnos - alumno.alumno_id
                                                    } else {
                                                        selectedAlumnos + alumno.alumno_id
                                                    }
                                                },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) conalepGreen else Color(0xFFE2E8F0)
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            "${alumno.apellido_paterno} ${alumno.apellido_materno} ${alumno.nombre}",
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 14.sp
                                                        )
                                                        Text(
                                                            "${alumno.matricula} • ${alumno.grado}${alumno.grupo}",
                                                            fontSize = 12.sp,
                                                            color = Color(0xFF64748B)
                                                        )
                                                    }
                                                    if (isSelected) {
                                                        Icon(
                                                            Icons.Default.CheckCircle,
                                                            contentDescription = null,
                                                            tint = conalepGreen,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (selectedAlumnos.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "${selectedAlumnos.size} ${if (selectedAlumnos.size == 1) "alumno seleccionado" else "alumnos seleccionados"}",
                                            fontSize = 12.sp,
                                            color = conalepGreen,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje") },
                placeholder = { Text("Escribe tu mensaje aquí...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = conalepGreen,
                    focusedLabelColor = conalepGreen
                )
            )

            Button(
                onClick = { enviarNotificacion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSending,
                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Text("Enviar Notificación", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
