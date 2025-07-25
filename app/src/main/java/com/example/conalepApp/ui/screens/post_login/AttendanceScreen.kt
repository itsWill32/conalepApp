package com.example.conalepApp.ui.screens.post_login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.conalepApp.R
import com.example.conalepApp.data.AttendanceStatus
import com.example.conalepApp.data.DummyData
import com.example.conalepApp.data.StudentAttendance
import com.example.conalepApp.ui.components.BottomNavigationBar
import com.example.conalepApp.ui.theme.conalepDarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    val roster = remember { mutableStateListOf(*DummyData.classRoster.toTypedArray()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pase de lista") },
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
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { AttendanceHeader() }
            item { AttendanceSummary(roster = roster) }
            item { Text("Lista de estudiantes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            items(roster, key = { it.id }) { student ->
                StudentAttendanceRow(
                    student = student,
                    onStatusChange = { newStatus ->
                        val index = roster.indexOf(student)
                        if (index != -1) {
                            roster[index] = student.copy(status = newStatus)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StudentAttendanceRow(student: StudentAttendance, onStatusChange: (AttendanceStatus) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${student.id} - ${student.name}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            StatusIconButton(
                status = AttendanceStatus.PRESENT,
                isSelected = student.status == AttendanceStatus.PRESENT,
                onClick = { onStatusChange(AttendanceStatus.PRESENT) }
            )
            StatusIconButton(
                status = AttendanceStatus.ABSENT,
                isSelected = student.status == AttendanceStatus.ABSENT,
                onClick = { onStatusChange(AttendanceStatus.ABSENT) }
            )
            StatusIconButton(
                status = AttendanceStatus.PERMISSION,
                isSelected = student.status == AttendanceStatus.PERMISSION,
                onClick = { onStatusChange(AttendanceStatus.PERMISSION) }
            )
            StatusIconButton(
                status = AttendanceStatus.LATE,
                isSelected = student.status == AttendanceStatus.LATE,
                onClick = { onStatusChange(AttendanceStatus.LATE) }
            )
        }
    }
}

@Composable
fun StatusIconButton(status: AttendanceStatus, isSelected: Boolean, onClick: () -> Unit) {
    val (iconRes, label, color) = when (status) {
        AttendanceStatus.PRESENT -> Triple(R.drawable.ic_present, "Presente", Color(0xFF4CAF50))
        AttendanceStatus.ABSENT -> Triple(R.drawable.ic_absent, "Ausente", Color(0xFFF44336))
        AttendanceStatus.PERMISSION -> Triple(R.drawable.ic_permission, "Permiso", Color(0xFF2196F3))
        AttendanceStatus.LATE -> Triple(R.drawable.ic_delay, "Retardo", Color(0xFFFF9800))
    }

    val iconColor = if (isSelected) Color.White else Color.Black
    val textColor = if (isSelected) color else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(if (isSelected) color else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }

        Text(label, fontSize = 10.sp, color = textColor, modifier = Modifier.height(16.dp))
    }
}


@Composable
fun AttendanceHeader() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pase de lista", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Chip(label = "Matemáticas", isSelected = true)
                Spacer(modifier = Modifier.width(8.dp))
                Chip(label = "3º - B")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lunes 14 de julio, 2025", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallIconButton(text = "Registrar", icon = Icons.Default.Description)
                    SmallIconButton(text = "Historial", icon = Icons.Default.History)
                }
            }
        }
    }
}

@Composable
fun SmallIconButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Button(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = conalepDarkGreen)
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun AttendanceSummary(roster: List<StudentAttendance>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val presentCount = roster.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = roster.count { it.status == AttendanceStatus.ABSENT }
        val permissionCount = roster.count { it.status == AttendanceStatus.PERMISSION }
        val lateCount = roster.count { it.status == AttendanceStatus.LATE }

        SummaryCard(modifier = Modifier.weight(1f), count = presentCount, label = "Presente")
        SummaryCard(modifier = Modifier.weight(1f), count = absentCount, label = "Ausente")
        SummaryCard(modifier = Modifier.weight(1f), count = permissionCount, label = "Permiso")
        SummaryCard(modifier = Modifier.weight(1f), count = lateCount, label = "Retardo")
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, count: Int, label: String) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun Chip(label: String, isSelected: Boolean = false) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp, color = contentColor, fontWeight = FontWeight.Bold)
    }
}