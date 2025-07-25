package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.theme.conalepFooter
import com.example.conalepApp.ui.theme.conalepGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLoginDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Nuestro Plantel") },
                    selected = false,
                    onClick = {
                        navController.navigate("school_info")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Carreras Técnicas") },
                    selected = false,
                    onClick = {
                        navController.navigate("careers_list")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Acerca de nosotros") },
                    selected = false,
                    onClick = {
                        navController.navigate("about_us")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
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
                        Button(
                            onClick = { showLoginDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                        ) {
                            Text("Inicia sesión", fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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

    if (showLoginDialog) {
        LoginDialog(
            onDismiss = { showLoginDialog = false },
            onLogin = {
                showLoginDialog = false
                navController.navigate("dashboard") {
                    popUpTo("landing") { inclusive = true }
                }
            }
        )
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
                fontWeight = FontWeight.Bold
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
        Icon(Icons.Filled.Groups, contentDescription = "Familia", modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Estamos listos para recibirte en la familia conalep",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
        ) {
            Text("Ver más", fontWeight = FontWeight.Bold)
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
        Text("Oferta educativa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Conoce cual de nuestras carreras es para ti", fontWeight = FontWeight.Bold)
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
            StatItem(icon = { Icon(Icons.Filled.Warehouse, contentDescription = null) }, number = "32", text = "Entidades\nfederativas")
            StatItem(icon = { Icon(Icons.Filled.School, contentDescription = null) }, number = "5", text = "Carreras")
            StatItem(icon = { Icon(Icons.Filled.Warehouse, contentDescription = null) }, number = "313", text = "Planteles en\noperación")
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
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
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

@Composable
fun LoginDialog(onDismiss: () -> Unit, onLogin: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Inicia sesión", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
            ) {
                Text("Ingresar")
            }
        },
        dismissButton = {}
    )
}