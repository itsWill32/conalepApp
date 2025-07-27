package com.example.conalepApp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.conalepApp.data.Teacher
import com.example.conalepApp.ui.theme.conalepGreen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfessorHeader(teacher: Teacher) {
    val sdf = SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es", "ES"))
    val currentDate = sdf.format(Date())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Profesor", color = conalepGreen, fontWeight = FontWeight.Bold)
                Text(teacher.name, fontWeight = FontWeight.Light)
            }
            Text(currentDate.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Light)
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(number, color = conalepGreen, fontWeight = FontWeight.Bold)
            Text(label, fontWeight = FontWeight.Light, textAlign = TextAlign.Center, fontSize = 12.sp)
        }
    }
}