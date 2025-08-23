package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.data.Subject
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.components.ProfessorHeader
import com.example.conalepApp.ui.components.SummaryCards
import com.example.conalepApp.ui.theme.conalepDarkGreen
import com.example.conalepApp.ui.theme.conalepGreen
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.api.User
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.items
import com.example.conalepApp.R

data class MateriaMaestro(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String,
    val total_estudiantes: Int
)

data class MateriaAlumno(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String,
    val profesor_nombre: String,
    val profesor_apellido_paterno: String,
    val profesor_apellido_materno: String,
    val fecha_inscripcion: String
) {
    val profesorCompleto: String
        get() = "$profesor_nombre $profesor_apellido_paterno $profesor_apellido_materno"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf<User?>(null) }
    var materiasMaestro by remember { mutableStateOf<List<MateriaMaestro>>(emptyList()) }
    var materiasAlumno by remember { mutableStateOf<List<MateriaAlumno>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authRepository.userData.collect { user ->
            currentUser = user
            if (user != null) {
                scope.launch {
                    try {
                        if (user.isMaestro) {
                            authRepository.getMateriasMaestro()
                                .onSuccess { materiasAPI ->
                                    materiasMaestro = materiasAPI.map {
                                        MateriaMaestro(
                                            clase_id = it.clase_id,
                                            nombre_clase = it.nombre_clase,
                                            codigo_clase = it.codigo_clase,
                                            total_estudiantes = it.total_estudiantes
                                        )
                                    }
                                }
                                .onFailure { exception ->
                                    errorMessage = exception.message ?: "Error al cargar materias del maestro"
                                }
                        } else if (user.isAlumno) {
                            authRepository.getMateriasAlumno()
                                .onSuccess { materiasAPI ->
                                    materiasAlumno = materiasAPI.map {
                                        MateriaAlumno(
                                            clase_id = it.clase_id,
                                            nombre_clase = it.nombre_clase,
                                            codigo_clase = it.codigo_clase,
                                            profesor_nombre = it.profesor_nombre,
                                            profesor_apellido_paterno = it.profesor_apellido_paterno,
                                            profesor_apellido_materno = it.profesor_apellido_materno,
                                            fecha_inscripcion = it.fecha_inscripcion
                                        )
                                    }
                                }
                                .onFailure { exception ->
                                    errorMessage = exception.message ?: "Error al cargar materias del alumno"
                                }
                        }
                        isLoading = false
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error al cargar materias"
                        isLoading = false
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Materias",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentUser?.isMaestro == true) {
                item { ProfessorHeader(teacher = DummyData.teacher) }
                item {
                    SummaryCards(
                        teacher = DummyData.teacher.copy(
                            totalStudents = materiasMaestro.sumOf { it.total_estudiantes },
                            activeSubjects = materiasMaestro.size
                        )
                    )
                }
            }

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
                when {
                    currentUser?.isMaestro == true -> {
                        items(materiasMaestro) { materia ->
                            SubjectCardMaestro(materia = materia, navController = navController)
                        }
                    }
                    currentUser?.isAlumno == true -> {
                        items(materiasAlumno) { materia ->
                            SubjectCardAlumno(materia = materia)
                        }
                    }
                    else -> {
                        item {
                            Text(
                                "No se pudo determinar el tipo de usuario",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectCardMaestro(materia: MateriaMaestro, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_subjects),
                contentDescription = materia.nombre_clase,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(materia.nombre_clase, fontWeight = FontWeight.Normal)
                Text(materia.codigo_clase, fontSize = 12.sp, fontWeight = FontWeight.Light)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${materia.total_estudiantes} estudiantes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        navController.navigate("attendance/${materia.clase_id}")
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = conalepDarkGreen)
                ) {
                    Text("Tomar asistencia", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun SubjectCardAlumno(materia: MateriaAlumno) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_subjects),
                contentDescription = materia.nombre_clase,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(materia.nombre_clase, fontWeight = FontWeight.Normal)
                Text(materia.codigo_clase, fontSize = 12.sp, fontWeight = FontWeight.Light)
                Text(
                    "Profesor: ${materia.profesorCompleto}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    color = conalepGreen
                )
            }
        }
    }
}


private suspend fun getMateriasMaestroFromAPI(): List<MateriaMaestro> {
    return listOf(
        MateriaMaestro(1, "Matemáticas", "MAT101", 28),
        MateriaMaestro(2, "Programación", "PRO101", 25)
    )
}

private suspend fun getMateriasAlumnoFromAPI(): List<MateriaAlumno> {
    return listOf(
        MateriaAlumno(1, "Física", "FIS301", "Carlos", "Ruiz", "Hernández", "2025-01-15"),
        MateriaAlumno(2, "Base de Datos", "BD201", "María Elena", "García", "López", "2025-01-15")
    )
}
