package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.data.Subject
import com.example.conalepApp.data.Teacher
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Materias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfessorHeader(teacher = DummyData.teacher) }
            item { SummaryCards(teacher = DummyData.teacher) }
            items(DummyData.subjects) { subject ->
                SubjectCard(subject = subject, navController = navController)
            }
        }
    }
}

@Composable
fun ProfessorHeader(teacher: Teacher) {
    val sdf = SimpleDateFormat("EEEE dd 'de' MMMM, yyyy", Locale("es", "ES"))
    val currentDate = sdf.format(Date())

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Profesor", style = MaterialTheme.typography.labelMedium)
                Text(teacher.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Text(currentDate.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SummaryCards(teacher: Teacher) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), number = teacher.totalStudents.toString(), label = "Total estudiantes")
        StatCard(modifier = Modifier.weight(1f), number = teacher.activeSubjects.toString(), label = "Materias activas")
        StatCard(modifier = Modifier.weight(1f), number = teacher.classDays.toString(), label = "Días de clase")
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, number: String, label: String) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(number, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectCard(subject: Subject, navController: NavController) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = subject.iconRes),
                contentDescription = subject.name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name, fontWeight = FontWeight.Bold)
                Text(subject.group, fontSize = 12.sp)
                if (subject.days.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                        subject.days.forEach { day ->
                            Chip(label = day)
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("${subject.studentCount} estudiantes", fontSize = 12.sp)
                if (subject.schedule.isNotEmpty()) {
                    Text(subject.schedule, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("attendance") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = conalepGreen)
                ) {
                    Text("Tomar asistencia", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp)
    }
}