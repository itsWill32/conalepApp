package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.data.AttendanceRecord
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.components.ProfessorHeader
import com.example.conalepApp.ui.components.SummaryCards
import com.example.conalepApp.ui.theme.conalepGreen
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.api.ClaseInfo
import com.example.conalepApp.api.HistorialItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceHistoryScreen(navController: NavController, materiaId: Int = 0) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var claseInfo by remember { mutableStateOf<ClaseInfo?>(null) }
    var historialItems by remember { mutableStateOf<List<HistorialItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Cargar historial de asistencias
    LaunchedEffect(materiaId) {
        if (materiaId <= 0) {
            errorMessage = "ID de materia inválido"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            authRepository.getHistorialAsistencias(materiaId)
                .onSuccess { response ->
                    claseInfo = response.clase
                    historialItems = response.historial
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Error al cargar historial"
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial", color = conalepGreen, fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = conalepGreen)
                    }
                }
            } else if (errorMessage.isNotEmpty()) {
                item {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item { ProfessorHeader(teacher = DummyData.teacher) }
                item { SummaryCards(teacher = DummyData.teacher) }

                if (historialItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
                        ) {
                            Text(
                                "No hay historial de asistencias para esta materia",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(historialItems) { historialItem ->
                        HistorialItemCardReal(
                            historialItem = historialItem,
                            claseInfo = claseInfo,
                            navController = navController,
                            materiaId = materiaId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(record: AttendanceRecord, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(record.subjectName, style = MaterialTheme.typography.titleMedium)
            Text(record.date, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${record.present} presentes", color = conalepGreen, fontSize = 12.sp)
                    Text("${record.absent} faltas", color = Color.Red, fontSize = 12.sp)
                    Text("${record.late} retardo", color = Color(0xFFD07F1B), fontSize = 12.sp)
                }
                Button(
                    onClick = { navController.navigate("attendance") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
@Composable
fun HistorialItemCardReal(
    historialItem: HistorialItem,
    claseInfo: ClaseInfo?,
    navController: NavController,
    materiaId: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                claseInfo?.nombre_clase ?: "Materia",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                formatearFecha(historialItem.fecha_asistencia),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${historialItem.presentes} presentes", color = conalepGreen, fontSize = 12.sp)
                    Text("${historialItem.ausentes} faltas", color = Color.Red, fontSize = 12.sp)
                    Text("${historialItem.retardos} retardos", color = Color(0xFFD07F1B), fontSize = 12.sp)
                    Text("${historialItem.justificados} justificados", color = Color.Blue, fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        navController.navigate("attendance_edit/${materiaId}/${historialItem.fecha_asistencia}")
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Editar")
                }
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
        val fechaParsed = formatoEntrada.parse(fecha)
        fechaParsed?.let { formatoSalida.format(it) } ?: fecha
    } catch (e: Exception) {
        fecha
    }
}