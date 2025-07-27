package com.example.conalepApp.ui.screens.post_login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.data.User
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = DummyData.loggedInUser

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
            AccountInfoCard()
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    val nameParts = user.name.split(" ")
    val firstName = nameParts.firstOrNull() ?: ""
    val lastName = nameParts.drop(1).joinToString(" ")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = user.profilePic),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Carrera",
                    style = MaterialTheme.typography.labelMedium,
                    color = conalepGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.career,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(firstName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Normal)
            Text(lastName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Normal)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Informacion personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { /* TODO */ }) {
                    Text("Editar")
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            InfoRow(iconRes = R.drawable.ic_profile_person, label = "Nombre", value = user.name)
            InfoRow(iconRes = R.drawable.ic_email_outlined, label = "E-mail", value = user.email)
            InfoRow(iconRes = R.drawable.ic_phone_outlined, label = "Telefono", value = user.phone)
            InfoRow(iconRes = R.drawable.ic_location_outlined, label = "Dirección", value = user.address)
        }
    }
}

@Composable
fun InfoRow(@DrawableRes iconRes: Int, label: String, value: String) {
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
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun AccountInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Informacion de cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}