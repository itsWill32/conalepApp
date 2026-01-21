package com.example.conalepApp.ui.screens.post_login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.api.User
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import com.example.conalepApp.repository.AuthRepository
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val result = authRepository.getProfile()
            result.onSuccess { userData ->
                user = userData
                isLoading = false
            }.onFailure { exception ->
                errorMessage = exception.message ?: "Error al cargar el perfil"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Error inesperado: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Perfil",
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )
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
                LoadingProfileContent(innerPadding)
            }
            errorMessage.isNotEmpty() -> {
                ErrorProfileContent(
                    innerPadding = innerPadding,
                    errorMessage = errorMessage,
                    onRetry = {
                        isLoading = true
                        errorMessage = ""
                        scope.launch {
                            try {
                                val result = authRepository.getProfile()
                                result.onSuccess { userData ->
                                    user = userData
                                    isLoading = false
                                }.onFailure { exception ->
                                    errorMessage = exception.message ?: "Error al cargar el perfil"
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error inesperado: ${e.message}"
                                isLoading = false
                            }
                        }
                    }
                )
            }
            user != null -> {
                ProfileContent(
                    innerPadding = innerPadding,
                    user = user!!,
                    onLogout = {
                        scope.launch {
                            try {
                                authRepository.logout()
                                navController.navigate("landing") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error al cerrar sesión: ${e.message}"
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingProfileContent(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = conalepGreen)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Cargando perfil...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorProfileContent(
    innerPadding: PaddingValues,
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Error al cargar el perfil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    innerPadding: PaddingValues,
    user: User,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfileHeader(user = user)
        PersonalInfoCard(user = user)
        ConfigurationCard(onLogout = onLogout)
    }
}

@Composable
fun ConfigurationCard(onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Configuración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_outlined),
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Cerrar sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Text(
                        "Salir de la aplicación",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = when {
                        user.isMaestro -> R.drawable.ic_profile_person
                        user.isAlumno -> R.drawable.ic_profile_person
                        else -> R.drawable.ic_profile_person
                    }
                ),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Tipo de usuario",
                    style = MaterialTheme.typography.labelMedium,
                    color = conalepGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when {
                        user.isMaestro -> "Maestro"
                        user.isAlumno -> "Alumno"
                        user.isAdministrador -> "Administrador"
                        else -> user.user_type.replaceFirstChar { it.uppercase() }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                user.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal
            )
            Text(
                "${user.apellido_paterno} ${user.apellido_materno}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun PersonalInfoCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Información personal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Información no editable
            InfoRow(
                iconRes = R.drawable.ic_profile_person,
                label = "Nombre completo",
                value = user.fullName,
                isEditing = false,
                onValueChange = {}
            )

            InfoRow(
                iconRes = R.drawable.ic_email_outlined,
                label = "E-mail",
                value = user.email,
                isEditing = false,
                onValueChange = {}
            )

            // Información específica para alumnos
            if (user.isAlumno) {
                user.matricula?.let { matricula ->
                    InfoRow(
                        iconRes = R.drawable.ic_profile_person,
                        label = "Matrícula",
                        value = matricula,
                        isEditing = false,
                        onValueChange = {}
                    )
                }

                user.grado?.let { grado ->
                    InfoRow(
                        iconRes = R.drawable.ic_profile_person,
                        label = "Grado",
                        value = "$grado${user.grupo ?: ""}",
                        isEditing = false,
                        onValueChange = {}
                    )
                }
            }

            // Teléfono (sin editar por ahora)
            InfoRow(
                iconRes = R.drawable.ic_phone_outlined,
                label = "Teléfono",
                value = user.telefono ?: "No especificado",
                isEditing = false,
                onValueChange = {}
            )
        }
    }
}


@Composable
fun InfoRow(
    @DrawableRes iconRes: Int,
    label: String,
    value: String,
    isEditing: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            if (isEditing && label == "Teléfono") {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ingresa tu teléfono") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = conalepGreen
                    )
                )
            } else {
                Text(
                    if (value.isBlank()) "No especificado" else value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = if (value.isBlank()) Color.Gray else Color.Unspecified
                )
            }
        }
    }
}
