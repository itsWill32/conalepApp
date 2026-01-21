package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSchoolScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Conócenos",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                conalepGreen.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = conalepGreen
                    )

                    Text(
                        "CONALEP CHIAPAS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )

                    Text(
                        "Organismo Público Descentralizado",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Descripción institucional
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "El colegio de Educación Profesional Técnica del Estado de Chiapas \"CONALEP CHIAPAS\", es un Organismo Público Descentralizado de la Administración Pública Estatal, sectorizado a la Secretaría de Educación, con personalidad jurídica y patrimonio propio, autonomía, administrativa, presupuestal, técnica, de gestión, de operación y ejecución.",
                        textAlign = TextAlign.Justify,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    Text(
                        "El colegio forma parte del Sistema Nacional de Colegios de Educación Profesional Técnica y opera con base en el modelo pedagógico aprobado por la Secretaría de Educación Pública a través del Colegio Nacional de Educación Profesional Técnica (CONALEP).",
                        textAlign = TextAlign.Justify,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    Text(
                        "El CONALEP CHIAPAS, tiene por objeto la impartición de Educación Profesional Técnica con la finalidad de satisfacer la demanda de personal técnico calificado para el sistema productivo del país, así como educación de bachillerato dentro del tipo medio superior, a fin de que los estudiantes puedan continuar con otro tipo de estudios.",
                        textAlign = TextAlign.Justify,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Misión
            MissionVisionCard(
                icon = Icons.Default.EmojiEvents,
                title = "Misión",
                description = "Formar profesionales técnicos en bachiller, prestar servicios de capacitación y evaluar competencias laborales con fines de certificación a través de un modelo educativo y de capacitación pertinente, equitativo, flexible y de calidad, sustentado en valores y vinculado con el sector productivo y la comunidad.",
                color = Color(0xFF10B981)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visión
            MissionVisionCard(
                icon = Icons.Default.Lightbulb,
                title = "Visión",
                description = "Ser la institución líder a nivel medio superior en el Estado, en la formación de profesionales técnicos que el sector productivo requiere, mediante un modelo educativo de calidad para la competitividad, reconocidos como la mejor opción en los servicios de capacitación y certificación laboral.",
                color = Color(0xFFF59E0B)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Valores y Principios
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = conalepGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Nuestros Principios",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )
                }

                Text(
                    "En el desempeño de sus funciones, las Personas Servidoras Públicas, deberán observar los Principios siguientes:",
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                PrinciplesGrid()
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MissionVisionCard(icon: ImageVector, title: String, description: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                description,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PrinciplesGrid() {
    val principles = listOf(
        "Legalidad", "Honradez", "Lealtad", "Imparcialidad", "Eficiencia",
        "Economía", "Disciplina", "Profesionalismo", "Objetividad", "Transparencia",
        "Rendición de Cuentas", "Competencia por Mérito", "Eficacia", "Integridad", "Equidad"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        principles.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { principle ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = conalepGreen.copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                principle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = conalepGreen,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
                // Rellenar espacios vacíos en la última fila
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
