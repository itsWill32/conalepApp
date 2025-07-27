package com.example.conalepApp.ui.screens.post_login

import androidx.annotation.DrawableRes
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
import com.example.conalepApp.ui.theme.conalepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    var roster by remember { mutableStateOf(DummyData.classRoster) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pase de lista",
                        color = conalepGreen,
                        fontWeight = FontWeight.Bold
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
            item { AttendanceHeader(navController = navController) }
            item { AttendanceSummary(roster = roster) }
            item {
                Text(
                    "Lista de estudiantes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = conalepGreen
                )
            }

            items(roster, key = { it.id }) { student ->
                StudentAttendanceRow(
                    student = student,
                    onStatusChange = { newStatus ->
                        roster = roster.map {
                            if (it.id == student.id) it.copy(status = newStatus) else it
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceHeader(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pase de lista",
                color = conalepGreen,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Chip(label = "Matemáticas", isSelected = true, fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.width(8.dp))
                Chip(label = "3º - B", fontWeight = FontWeight.Light)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Lunes 14 de julio, 2025",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallIconButton(text = "Registrar", icon = Icons.Default.Description, onClick = { /* TODO */ })
                    SmallIconButton(text = "Historial", icon = Icons.Default.History, onClick = { navController.navigate("attendance_history") })
                }
            }
        }
    }
}

@Composable
fun SmallIconButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = conalepDarkGreen)
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
fun AttendanceSummary(roster: List<StudentAttendance>) {
    val lateColor = Color(0xFFD07F1B)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val presentCount = roster.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = roster.count { it.status == AttendanceStatus.ABSENT }
        val permissionCount = roster.count { it.status == AttendanceStatus.PERMISSION }
        val lateCount = roster.count { it.status == AttendanceStatus.LATE }

        SummaryCard(modifier = Modifier.weight(1f), count = presentCount, label = "Presente", color = conalepGreen)
        SummaryCard(modifier = Modifier.weight(1f), count = absentCount, label = "Ausente", color = Color.Red)
        SummaryCard(modifier = Modifier.weight(1f), count = permissionCount, label = "Permiso", color = Color.Blue)
        SummaryCard(modifier = Modifier.weight(1f), count = lateCount, label = "Retardo", color = lateColor)
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, count: Int, label: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                label,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun StudentAttendanceRow(student: StudentAttendance, onStatusChange: (AttendanceStatus) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${student.id} - ${student.name}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal
            )

            Row {
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
}

@Composable
fun StatusIconButton(status: AttendanceStatus, isSelected: Boolean, onClick: () -> Unit) {
    val (iconRes, label, color) = when (status) {
        AttendanceStatus.PRESENT -> Triple(R.drawable.ic_present, "Presente", Color(0xFF4CAF50))
        AttendanceStatus.ABSENT -> Triple(R.drawable.ic_absent, "Ausente", Color(0xFFF44336))
        AttendanceStatus.PERMISSION -> Triple(R.drawable.ic_permission, "Permiso", Color(0xFF2196F3))
        AttendanceStatus.LATE -> Triple(R.drawable.ic_delay, "Retardo", Color(0xFFD07F1B))
    }

    val iconColor = if (isSelected) Color.White else Color.Black
    val textColor = if (isSelected) color else Color.Transparent
    val textHeight = 16.dp

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
        Text(label, fontSize = 10.sp, color = textColor, modifier = Modifier.height(textHeight))
    }
}

@Composable
fun Chip(label: String, isSelected: Boolean = false, fontWeight: FontWeight = FontWeight.Bold) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp, color = contentColor, fontWeight = fontWeight)
    }
}