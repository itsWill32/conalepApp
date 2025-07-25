package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Acerca de nosotros",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Sobre nuestra aplicacion",
                color = conalepGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = "Nuestra aplicación es la plataforma digital oficial de la institución de bachilleres, diseñada para facilitar la comunicación y la gestión académica. Sirve como una \"landing page informativa\" para la comunidad, ofreciendo acceso rápido a noticias y eventos importantes. Además, integra funcionalidades clave para el día a día escolar: los **profesores pueden pasar lista** de manera eficiente, y tanto **profesores como alumnos pueden consultar fácilmente las materias** que imparten o cursan, respectivamente. Nuestro objetivo es optimizar la experiencia educativa para todos.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DeveloperCard(
                    name = "Adrián Espinoza Enríquez",
                    role = "Desarrollador de Software",
                    imageRes = R.drawable.perfil_alberto
                )
                DeveloperCard(
                    name = "William de Jesús Espinoza García",
                    role = "Diseñador UX/UI",
                    imageRes = R.drawable.perfil_alberto
                )
            }

            DeveloperCard(
                name = "José Alberto Carrasco Sánchez",
                role = "Diseñador UX/UI",
                imageRes = R.drawable.perfil_alberto
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Asesor laboral",
                color = conalepGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                "José Alonso Macías Montoya",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DeveloperCard(name: String, role: String, imageRes: Int) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto de $name",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Estilos corregidos para la tarjeta
            Text(
                role,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
            Text(
                name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}