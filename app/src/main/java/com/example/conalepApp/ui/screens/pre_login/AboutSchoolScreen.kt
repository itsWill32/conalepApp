package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSchoolScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conócenos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "El colegio de Educación Profesional Técnica del Estado de Chiapas \"CONALEP CHIAPAS\", es un Organismo Público Descentralizado de la Administración Pública Estatal, sectorizado a la Secretaría de Educación, con personalidad jurídica y patrimonio propio, autonomía, administrativa, presupuestal, técnica, de gestión, de operación y ejecución.",
                textAlign = TextAlign.Justify
            )
            Text(
                "El colegio forma parte del Sistema Nacional de Colegios de Educación Profesional Técnica y opera con base en el modelo pedagógico aprobado por la Secretaría de Educación Pública a través del Colegio Nacional de Educación Profesional Técnica (CONALEP).",
                textAlign = TextAlign.Justify
            )
            Text(
                "El CONALEP CHIAPAS, tiene por objeto la impartición de Educación Profesional Técnica con la finalidad de satisfacer la demanda de personal técnico calificado para el sistema productivo del país, así como educación de bachillerato dentro del tipo medio superior, a fin de que los estudiantes puedan continuar con otro tipo de estudios.",
                textAlign = TextAlign.Justify
            )

            SectionTitle(title = "Misión")
            Text(
                "Formar profesionales técnicos en bachiller, prestar servicios de capacitación y evaluar competencias laborales con fines de certificación a través de un modelo educativo y de capacitación pertinente, equitativo, flexible y de calidad, sustentado en valores y vinculado con el sector productivo y la comunidad.",
                textAlign = TextAlign.Center
            )

            SectionTitle(title = "Visión")
            Text(
                "Ser la institución líder a nivel medio superior en el Estado, en la formación de profesionales técnicos que el sector productivo requiere, mediante un modelo educativo de calidad para la competitividad, reconocidos como la mejor opción en los servicios de capacitación y certificación laboral.",
                textAlign = TextAlign.Center
            )

            Text(
                "En el desempeño de sus funciones, las Personas Servidoras Públicas, deberán observar los Principios siguientes:",
                textAlign = TextAlign.Center
            )

            PrinciplesList()
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PrinciplesList() {
    val principles = listOf(
        "Legalidad", "Honradez", "Lealtad", "Imparcialidad", "Eficiencia",
        "Economía", "Disciplina", "Profesionalismo", "Objetividad", "Transparencia",
        "Rendición de Cuentas", "Competencia por Mérito", "Eficacia", "Integridad", "Equidad"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        principles.forEach { principle ->
            Text(text = principle)
        }
    }
}