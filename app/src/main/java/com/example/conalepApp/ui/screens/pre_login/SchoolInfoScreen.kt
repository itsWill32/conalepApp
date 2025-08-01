package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.conalepApp.R
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolInfoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Plantel",
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
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
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Detalles del plantel",
                        style = MaterialTheme.typography.titleLarge,
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Plantel: 022", fontWeight = FontWeight.Normal)
                    Text("CCT: 07DPT0002C", fontWeight = FontWeight.Normal)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Carreras Técnicas:",
                        style = MaterialTheme.typography.titleMedium,
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("PTB. Autotrónica", fontWeight = FontWeight.Normal)
                    Text("PTB. Construcción", fontWeight = FontWeight.Normal)
                    Text("PTB. Hospitalidad Turística", fontWeight = FontWeight.Normal)
                    Text("PTB. Informática", fontWeight = FontWeight.Normal)
                    Text("PTB. Mantenimiento Automotriz", fontWeight = FontWeight.Normal)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        "Chiapa de corzo",
                        style = MaterialTheme.typography.titleLarge,
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = R.drawable.mapa_chiapas),
                        contentDescription = "Mapa de Chiapas",
                        modifier = Modifier.height(150.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Nuestros Planteles son el resultado de un avanzado estudio de las necesidades propias de cada entorno, lo que ha llevado a hacer las gestiones necesarias ante las instancias Gubernamentales para la realización de los proyectos de factibilidad y creación de los mismos.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(
                    "Dirección: Calle Libertad, Esq. libramiento nº 654, C.P. 29160. Chiapa de Corzo, Chiapas.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    "Teléfono: (961) 61 6 05 53 y 61 6 03 49",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    "Correo: conalepchiapadecorzo@chis.conalep.edu.mx",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}