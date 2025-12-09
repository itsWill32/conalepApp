package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
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
import com.example.conalepApp.api.ClaseInfo
import com.example.conalepApp.api.HistorialItem
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.components.ProfessorHeader
import com.example.conalepApp.ui.components.SummaryCards
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
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

    LaunchedEffect(materiaId) {
        if (materiaId == 0) {
            errorMessage = "ID de materia inválido"
            isLoading = false
            return@LaunchedEffect
        }

        scope.launch {
            authRepository.getHistorialAsistencias(materiaId).fold(
                onSuccess = { response ->
                    claseInfo = response.clase
                    historialItems = response.historial
                    isLoading = false
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "Error al cargar historial"
                    isLoading = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Historial", color = conalepGreen, fontWeight = FontWeight.Bold)
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
                item {
                    ProfessorHeader(teacher = DummyData.teacher)
                }

                item {
                    SummaryCards(teacher = DummyData.teacher)
                }

                // Info de la materia
                claseInfo?.let { clase ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    clase.nombre_clase,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = conalepGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Código: ${clase.codigo_clase}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Botón de descarga de PDF
                item {
                    Button(
                        onClick = { navController.navigate("attendance_report/$materiaId") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Descargar Reporte PDF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

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
                    Text(
                        "${historialItem.presentes} presentes",
                        color = conalepGreen,
                        fontSize = 12.sp
                    )
                    Text(
                        "${historialItem.ausentes} faltas",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                    Text(
                        "${historialItem.retardos} retardos",
                        color = Color(0xFFD07F1B),
                        fontSize = 12.sp
                    )
                    Text(
                        "${historialItem.justificados} justificados",
                        color = Color.Blue,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("attendance_edit/$materiaId/${historialItem.fecha_asistencia}")
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
        val formatoSalida = SimpleDateFormat("EEEE dd 'de' MMMM, yyyy", Locale("es", "ES"))
        val fechaParsed = formatoEntrada.parse(fecha)
        fechaParsed?.let { formatoSalida.format(it) } ?: fecha
    } catch (e: Exception) {
        fecha
    }
}
