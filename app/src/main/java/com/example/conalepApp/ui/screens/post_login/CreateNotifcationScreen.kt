package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
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
import com.example.conalepApp.api.AlumnoNotificacion
import com.example.conalepApp.api.MateriaBasica
import com.example.conalepApp.api.NotificacionDestinatarios
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipoDestinatario by remember { mutableStateOf("Materia_Completa") }
    var destinatarios by remember { mutableStateOf<NotificacionDestinatarios?>(null) }
    var selectedMaterias by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedAlumnos by remember { mutableStateOf<Set<Int>>(emptySet()) }

    var isLoading by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val tiposDestinatario = listOf(
        "Materia_Completa" to "Materia completa",
        "Multiples_Materias" to "Múltiples materias",
        "Alumno_Especifico" to "Alumnos específicos"
    )

    // Cargar destinatarios disponibles
    LaunchedEffect(Unit) {
        scope.launch {
            authRepository.getDestinatariosParaNotificacion()
                .onSuccess { response ->
                    destinatarios = response
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Error al cargar destinatarios"
                    isLoading = false
                }
        }
    }

    fun enviarNotificacion() {
        if (titulo.isBlank() || mensaje.isBlank()) {
            errorMessage = "El título y mensaje son obligatorios"
            return
        }

        val destinatariosIds = when (tipoDestinatario) {
            "Materia_Completa" -> selectedMaterias.take(1).toList()
            "Multiples_Materias" -> selectedMaterias.toList()
            "Alumno_Especifico" -> selectedAlumnos.toList()
            else -> emptyList()
        }

        if (destinatariosIds.isEmpty()) {
            errorMessage = "Debe seleccionar al menos un destinatario"
            return
        }

        scope.launch {
            isSending = true
            errorMessage = ""

            authRepository.crearNotificacion(titulo, mensaje, tipoDestinatario, destinatariosIds)
                .onSuccess { result ->
                    successMessage = "Notificación enviada para aprobación exitosamente"
                    isSending = false
                    // Navegar de regreso después de un delay
                    kotlinx.coroutines.delay(2000)
                    navController.popBackStack()
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Error al enviar notificación"
                    isSending = false
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Crear Notificación",
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
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = conalepGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando opciones...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Mensajes de error/éxito
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (successMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            successMessage,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de la notificación") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = conalepGreen,
                        focusedLabelColor = conalepGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mensaje
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = conalepGreen,
                        focusedLabelColor = conalepGreen
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tipo de destinatario
                Text(
                    "Tipo de destinatario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                tiposDestinatario.forEach { (valor, texto) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (tipoDestinatario == valor),
                                onClick = {
                                    tipoDestinatario = valor
                                    selectedMaterias = emptySet()
                                    selectedAlumnos = emptySet()
                                }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (tipoDestinatario == valor),
                            onClick = {
                                tipoDestinatario = valor
                                selectedMaterias = emptySet()
                                selectedAlumnos = emptySet()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = conalepGreen)
                        )
                        Text(
                            text = texto,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selección de destinatarios
                when (tipoDestinatario) {
                    "Materia_Completa" -> {
                        SeleccionMateriaUnica(
                            materias = destinatarios?.materias ?: emptyList(),
                            selectedMateria = selectedMaterias.firstOrNull(),
                            onMateriaSelected = { selectedMaterias = setOf(it) }
                        )
                    }
                    "Multiples_Materias" -> {
                        SeleccionMultiplesMaterias(
                            materias = destinatarios?.materias ?: emptyList(),
                            selectedMaterias = selectedMaterias,
                            onMateriasChanged = { selectedMaterias = it }
                        )
                    }
                    "Alumno_Especifico" -> {
                        SeleccionAlumnosConFiltros(
                            alumnos = destinatarios?.alumnos ?: emptyList(),
                            selectedAlumnos = selectedAlumnos,
                            onAlumnosChanged = { selectedAlumnos = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón enviar
                Button(
                    onClick = { enviarNotificacion() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSending && titulo.isNotBlank() && mensaje.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviando...")
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar Notificación")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información adicional
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "ℹ️ Las notificaciones deben ser aprobadas por un administrador antes de ser enviadas a los alumnos.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}

@Composable
fun SeleccionMateriaUnica(
    materias: List<MateriaBasica>,
    selectedMateria: Int?,
    onMateriaSelected: (Int) -> Unit
) {
    Text(
        "Selecciona una materia:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = conalepGreen
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(materias) { materia ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedMateria == materia.clase_id)
                        Color(0xFFE8F5E8) else Color(0xFFF8F9FA)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMateria == materia.clase_id,
                        onClick = { onMateriaSelected(materia.clase_id) },
                        colors = RadioButtonDefaults.colors(selectedColor = conalepGreen)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            materia.nombre_clase,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "${materia.codigo_clase} • ${materia.total_alumnos} alumnos",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeleccionMultiplesMaterias(
    materias: List<MateriaBasica>,
    selectedMaterias: Set<Int>,
    onMateriasChanged: (Set<Int>) -> Unit
) {
    Text(
        "Selecciona las materias:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = conalepGreen
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(materias) { materia ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedMaterias.contains(materia.clase_id))
                        Color(0xFFE8F5E8) else Color(0xFFF8F9FA)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedMaterias.contains(materia.clase_id),
                        onCheckedChange = { checked ->
                            val newSet = if (checked) {
                                selectedMaterias + materia.clase_id
                            } else {
                                selectedMaterias - materia.clase_id
                            }
                            onMateriasChanged(newSet)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = conalepGreen)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            materia.nombre_clase,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "${materia.codigo_clase} • ${materia.total_alumnos} alumnos",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionAlumnosConFiltros(
    alumnos: List<AlumnoNotificacion>,
    selectedAlumnos: Set<Int>,
    onAlumnosChanged: (Set<Int>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var gradoFiltro by remember { mutableStateOf("Todos") }
    var grupoFiltro by remember { mutableStateOf("Todos") }
    var showFilters by remember { mutableStateOf(false) }

    // Obtener grados únicos
    val grados = remember(alumnos) {
        listOf("Todos") + alumnos.map { it.grado }.distinct().sorted()
    }

    // Obtener grupos únicos según el grado seleccionado
    val grupos = remember(alumnos, gradoFiltro) {
        if (gradoFiltro == "Todos") {
            listOf("Todos") + alumnos.map { it.grupo }.distinct().sorted()
        } else {
            listOf("Todos") + alumnos.filter { it.grado == gradoFiltro }
                .map { it.grupo }.distinct().sorted()
        }
    }

    // Filtrar alumnos
    val filteredAlumnos = remember(alumnos, searchQuery, gradoFiltro, grupoFiltro) {
        alumnos.filter { alumno ->
            // Filtro por texto
            val cumpleTexto = if (searchQuery.isBlank()) true else {
                alumno.nombreCompleto.contains(searchQuery, ignoreCase = true) ||
                        alumno.matricula.contains(searchQuery, ignoreCase = true)
            }

            // Filtro por grado
            val cumpleGrado = gradoFiltro == "Todos" || alumno.grado == gradoFiltro

            // Filtro por grupo
            val cumpleGrupo = grupoFiltro == "Todos" || alumno.grupo == grupoFiltro

            cumpleTexto && cumpleGrado && cumpleGrupo
        }
    }

    Text(
        "Selecciona los alumnos:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = conalepGreen
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Campo de búsqueda
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("Buscar por nombre o matrícula") },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = conalepGreen
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = conalepGreen,
            focusedLabelColor = conalepGreen
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Botón para mostrar filtros avanzados
    TextButton(
        onClick = { showFilters = !showFilters },
        colors = ButtonDefaults.textButtonColors(contentColor = conalepGreen)
    ) {
        Icon(
            if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Filtros por grado y grupo")
    }

    // Filtros avanzados
    if (showFilters) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filtro por grado
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Grado:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        var expandedGrado by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedGrado,
                            onExpandedChange = { expandedGrado = !expandedGrado }
                        ) {
                            OutlinedTextField(
                                value = gradoFiltro,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedGrado
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = conalepGreen
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedGrado,
                                onDismissRequest = { expandedGrado = false }
                            ) {
                                grados.forEach { grado ->
                                    DropdownMenuItem(
                                        text = { Text(grado) },
                                        onClick = {
                                            gradoFiltro = grado
                                            if (grado != "Todos") {
                                                grupoFiltro = "Todos"
                                            }
                                            expandedGrado = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Filtro por grupo
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Grupo:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        var expandedGrupo by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedGrupo,
                            onExpandedChange = { expandedGrupo = !expandedGrupo }
                        ) {
                            OutlinedTextField(
                                value = grupoFiltro,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedGrupo
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = conalepGreen
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedGrupo,
                                onDismissRequest = { expandedGrupo = false }
                            ) {
                                grupos.forEach { grupo ->
                                    DropdownMenuItem(
                                        text = { Text(grupo) },
                                        onClick = {
                                            grupoFiltro = grupo
                                            expandedGrupo = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        gradoFiltro = "Todos"
                        grupoFiltro = "Todos"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Limpiar filtros", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Mostrar contador
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Mostrando ${filteredAlumnos.size} de ${alumnos.size} alumnos",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        if (selectedAlumnos.isNotEmpty()) {
            Text(
                "${selectedAlumnos.size} seleccionados",
                style = MaterialTheme.typography.bodySmall,
                color = conalepGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Botones de selección rápida
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                val nuevosSeleccionados = selectedAlumnos + filteredAlumnos.map { it.alumno_id }
                onAlumnosChanged(nuevosSeleccionados.toSet())
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen),
            contentPadding = PaddingValues(8.dp)
        ) {
            Text("Seleccionar filtrados", fontSize = 12.sp)
        }

        Button(
            onClick = {
                val idsADesmarcar = filteredAlumnos.map { it.alumno_id }.toSet()
                val nuevosSeleccionados = selectedAlumnos - idsADesmarcar
                onAlumnosChanged(nuevosSeleccionados)
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            contentPadding = PaddingValues(8.dp)
        ) {
            Text("Deseleccionar filtrados", fontSize = 12.sp)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Lista de alumnos
    LazyColumn(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (filteredAlumnos.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No se encontraron alumnos con los filtros aplicados",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(filteredAlumnos) { alumno ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAlumnos.contains(alumno.alumno_id))
                            Color(0xFFE8F5E8) else Color(0xFFF8F9FA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedAlumnos.contains(alumno.alumno_id),
                            onCheckedChange = { checked ->
                                val newSet = if (checked) {
                                    selectedAlumnos + alumno.alumno_id
                                } else {
                                    selectedAlumnos - alumno.alumno_id
                                }
                                onAlumnosChanged(newSet)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = conalepGreen)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                alumno.nombreCompleto,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    alumno.matricula,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = conalepGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${alumno.grado}º${alumno.grupo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            if (alumno.materias_compartidas.isNotEmpty()) {
                                Text(
                                    "Materias: ${alumno.materias_compartidas}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Blue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}