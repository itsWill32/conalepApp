package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
                        "Nuestro Plantel",
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen,
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
            // Hero Section con mapa
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
                    Text(
                        "CONALEP 022",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = conalepGreen
                    )

                    Text(
                        "Chiapa de Corzo, Chiapas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Image(
                        painter = painterResource(id = R.drawable.mapa_chiapas),
                        contentDescription = "Mapa de Chiapas",
                        modifier = Modifier
                            .height(140.dp)
                            .padding(vertical = 8.dp)
                    )
                }
            }

            // Información del plantel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = conalepGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Datos del Plantel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = conalepGreen
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    InfoRow(label = "Plantel:", value = "022")
                    InfoRow(label = "CCT:", value = "07DPT0002C")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Carreras técnicas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = conalepGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Carreras Técnicas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = conalepGreen
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    CareerItem(icon = Icons.Default.DirectionsCar, career = "Autotrónica")
                    CareerItem(icon = Icons.Default.Business, career = "Construcción")
                    CareerItem(icon = Icons.Default.Hotel, career = "Hospitalidad Turística")
                    CareerItem(icon = Icons.Default.Computer, career = "Informática")
                    CareerItem(icon = Icons.Default.Build, career = "Mantenimiento Automotriz")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = conalepGreen,
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        "Nuestros planteles son el resultado de un avanzado estudio de las necesidades propias de cada entorno, lo que ha llevado a hacer las gestiones necesarias ante las instancias Gubernamentales para la realización de los proyectos de factibilidad y creación de los mismos.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de contacto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = conalepGreen.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ContactMail,
                            contentDescription = null,
                            tint = conalepGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Información de Contacto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = conalepGreen
                        )
                    }

                    HorizontalDivider(color = conalepGreen.copy(alpha = 0.2f))

                    ContactInfoRow(
                        icon = Icons.Default.LocationOn,
                        text = "Calle Libertad, Esq. libramiento nº 654\nC.P. 29160, Chiapa de Corzo, Chiapas"
                    )

                    ContactInfoRow(
                        icon = Icons.Default.Phone,
                        text = "(961) 61 6 05 53 y 61 6 03 49"
                    )

                    ContactInfoRow(
                        icon = Icons.Default.Email,
                        text = "conalepchiapadecorzo@chis.conalep.edu.mx"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CareerItem(icon: ImageVector, career: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = conalepGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = conalepGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            "PTB. $career",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = conalepGreen,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = conalepGreen.copy(alpha = 0.9f)
        )
    }
}
