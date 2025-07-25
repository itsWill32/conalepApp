package com.example.conalepApp.ui.screens.pre_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.data.DummyData
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerDetailScreen(navController: NavController, careerName: String) {
    val decodedCareerName = remember(careerName) {
        URLDecoder.decode(careerName, StandardCharsets.UTF_8.toString())
    }
    val details = remember(decodedCareerName) {
        DummyData.careerDetailsMap[decodedCareerName]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrera técnica") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (details != null) {
                Text(details.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(details.description, style = MaterialTheme.typography.bodyLarge)

                Image(
                    painter = painterResource(id = details.imageRes1),
                    contentDescription = "Imagen de la carrera ${details.fullName}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Text("Perfil de egreso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(details.profile, style = MaterialTheme.typography.bodyLarge)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text("Plan de estudios", textDecoration = TextDecoration.Underline)
                        }
                        TextButton(onClick = { /*TODO*/ }) {
                            Text("Perfil de egreso", textDecoration = TextDecoration.Underline)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = details.imageRes2),
                        contentDescription = "Segunda imagen de la carrera",
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Text("Detalles no encontrados para: $decodedCareerName")
            }
        }
    }
}