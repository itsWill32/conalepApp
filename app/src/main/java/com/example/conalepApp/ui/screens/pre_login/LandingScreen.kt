package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.theme.conalepFooter
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.conalepApp.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showLoginDialog by remember { mutableStateOf(false) }
    var showOTPDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.fillMaxWidth(0.40f),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                "Menú",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterStart),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { scope.launch { drawerState.close() } },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar Menú",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        DrawerMenuItem(text = "Nuestro plantel") {
                            navController.navigate("school_info")
                            scope.launch { drawerState.close() }
                        }
                        DrawerMenuItem(text = "Carreras técnicas") {
                            navController.navigate("careers_list")
                            scope.launch { drawerState.close() }
                        }
                        DrawerMenuItem(text = "Conócenos") {
                            navController.navigate("about_school")
                            scope.launch { drawerState.close() }
                        }
                        DrawerMenuItem(text = "Acerca de") {
                            navController.navigate("about_us")
                            scope.launch { drawerState.close() }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Image(
                                    painter = painterResource(id = R.drawable.conalep_logo_green),
                                    contentDescription = "Logo Conalep",
                                    modifier = Modifier.height(32.dp)
                                )
                            },
                            actions = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { showLoginDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = conalepGreen),
                                        modifier = Modifier.height(35.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Text("Inicia sesión", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    IconButton(onClick = {
                                        scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                                    }) {
                                        Icon(
                                            Icons.Filled.Menu,
                                            contentDescription = "Menú",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { HeaderSection() }
                        item { ValuesSection(navController) }
                        item { FamilySection() }
                        item { EducationOfferSection(navController) }
                        item { StatsBannerSection() }
                        item { FooterSection() }
                    }
                }
            }
        }
    }

    // Diálogo 1: Ingreso de correo
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showLoginDialog = false
                    email = ""
                    errorMessage = ""
                }
            },
            title = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Ingresa tu correo institucional",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("test@conalep.edu.mx") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage.isNotEmpty()
                    )
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            errorMessage = "Ingresa tu correo"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = ""
                        scope.launch {
                            authRepository.requestOTP(email)
                                .onSuccess {
                                    isLoading = false
                                    showLoginDialog = false
                                    showOTPDialog = true
                                }
                                .onFailure { exception ->
                                    isLoading = false
                                    errorMessage = exception.message ?: "Error al enviar código"
                                }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                ) {
                    Text(if (isLoading) "Enviando..." else "Enviar código")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLoginDialog = false
                        email = ""
                        errorMessage = ""
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo 2: Verificación de OTP
    if (showOTPDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showOTPDialog = false
                    otpCode = ""
                    errorMessage = ""
                }
            },
            title = { Text("Verificar Código", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Código enviado a:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        email,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = conalepGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                otpCode = it
                                errorMessage = ""
                            }
                        },
                        label = { Text("Código OTP") },
                        placeholder = { Text("123456") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage.isNotEmpty()
                    )
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (otpCode.length != 6) {
                            errorMessage = "El código debe tener 6 dígitos"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = ""
                        scope.launch {
                            authRepository.verifyOTP(email, otpCode)
                                .onSuccess { user ->
                                    isLoading = false
                                    showOTPDialog = false
                                    email = ""
                                    otpCode = ""
                                    navController.navigate("dashboard") {
                                        popUpTo("landing") { inclusive = true }
                                    }
                                }
                                .onFailure { exception ->
                                    isLoading = false
                                    errorMessage = exception.message ?: "Código incorrecto"
                                }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                ) {
                    Text(if (isLoading) "Verificando..." else "Verificar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showOTPDialog = false
                        showLoginDialog = true
                        otpCode = ""
                        errorMessage = ""
                    },
                    enabled = !isLoading
                ) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    )
}

@Composable
fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.graduada_header),
            contentDescription = "Header Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Conectando talento con tecnología",
                style = MaterialTheme.typography.headlineLarge,
                color = conalepGreen,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El Colegio Nacional de Educación Profesional Técnica es una Institución líder en la formación de Profesionales Técnicos y Profesionales Técnicos Bachiller en México, que cursan programas reconocidos por su calidad.",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.begin_logo),
                contentDescription = "Logo",
                modifier = Modifier.height(50.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuesSection(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = { navController.navigate("about_school") }
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.valores_conalep),
                contentDescription = "Valores Conalep",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "VALORES CONALEP",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FamilySection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            Icons.Filled.Groups,
            contentDescription = "Familia",
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Estamos listos para recibirte en la familia conalep",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
        ) {
            Text("Ver más", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun EducationOfferSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Oferta educativa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Conoce cual de nuestras carreras es para ti", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        val careers = DummyData.careers
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EducationCareerCard(modifier = Modifier.weight(1f), careerName = careers[0], imageRes = R.drawable.carrera_informatica, navController = navController)
            EducationCareerCard(modifier = Modifier.weight(1f), careerName = careers[1], imageRes = R.drawable.carrera_autotronica, navController = navController)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EducationCareerCard(modifier = Modifier.weight(1f), careerName = careers[2], imageRes = R.drawable.carrera_turismo, navController = navController)
            EducationCareerCard(modifier = Modifier.weight(1f), careerName = careers[3], imageRes = R.drawable.carrera_construccion, navController = navController)
        }
        Spacer(modifier = Modifier.height(8.dp))
        EducationCareerCard(modifier = Modifier.fillMaxWidth(0.5f), careerName = careers[4], imageRes = R.drawable.carrera_automotriz, navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationCareerCard(modifier: Modifier = Modifier, careerName: String, imageRes: Int, navController: NavController) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = { navController.navigate("careers_list") }
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = careerName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 50f
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    careerName.replace("Profesional técnico en ", ""),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatsBannerSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = conalepGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(icon = { Icon(Icons.Filled.Warehouse, contentDescription = null, tint = Color.White) }, number = "32", text = "Entidades\nfederativas")
            StatItem(icon = { Icon(Icons.Filled.School, contentDescription = null, tint = Color.White) }, number = "5", text = "Carreras")
            StatItem(icon = { Icon(Icons.Filled.Warehouse, contentDescription = null, tint = Color.White) }, number = "313", text = "Planteles en\noperación")
        }
    }
}

@Composable
fun StatItem(icon: @Composable () -> Unit, number: String, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProvideTextStyle(value = LocalTextStyle.current.copy(color = Color.White)) {
            icon()
            Text(number, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text, textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FooterSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(conalepFooter)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.footer_logo),
            contentDescription = "Logo Footer",
            modifier = Modifier.height(50.dp)
        )
        Column {
            Text("Información en línea", color = Color.White, fontWeight = FontWeight.Bold)
            Text("> Organigrama", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Light)
            Text("> Calendario escolar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Light)
            Text("> Comité de Ética", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}
