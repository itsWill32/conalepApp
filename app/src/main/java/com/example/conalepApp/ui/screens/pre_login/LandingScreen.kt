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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.theme.conalepFooter
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import com.example.conalepApp.repository.AuthRepository
import com.example.conalepApp.api.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLoginDialog by remember { mutableStateOf(false) }

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

    if (showLoginDialog) {
        LoginDialog(
            onDismiss = { showLoginDialog = false },
            onLogin = { user ->
                showLoginDialog = false
                navController.navigate("dashboard") {
                    popUpTo("landing") { inclusive = true }
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
fun LoginDialog(onDismiss: () -> Unit, onLogin: (User) -> Unit) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var emailText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
                }
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Inicia sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Correo electrónico",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emailText,
                        onValueChange = {
                            emailText = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = conalepGreen,

                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        placeholder = {
                            Text("test@conalep.edu.mx")
                        },
                        enabled = !isLoading
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (emailText.isBlank()) {
                                errorMessage = "Por favor ingresa tu email"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = ""
                            scope.launch {
                                authRepository.login(emailText)
                                    .onSuccess { user ->
                                        isLoading = false
                                        onLogin(user)
                                    }
                                    .onFailure { exception ->
                                        isLoading = false
                                        errorMessage = exception.message ?: "Error de conexión"
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = conalepGreen),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Ingresar",
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
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