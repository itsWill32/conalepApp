package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.data.AttendanceRecord
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.components.ProfessorHeader
import com.example.conalepApp.ui.components.SummaryCards
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceHistoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Materias", color = conalepGreen, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
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
            items(DummyData.attendanceHistory) { record ->
                HistoryItemCard(record = record, navController = navController)
            }
        }
    }
}

@Composable
fun HistoryItemCard(record: AttendanceRecord, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(record.subjectName, style = MaterialTheme.typography.titleMedium)
            Text(record.date, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${record.present} presentes", color = conalepGreen, fontSize = 12.sp)
                    Text("${record.absent} faltas", color = Color.Red, fontSize = 12.sp)
                    Text("${record.late} retardo", color = Color(0xFFD07F1B), fontSize = 12.sp)
                }
                Button(
                    onClick = { navController.navigate("attendance") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Editar")
                }
            }
        }
    }
}