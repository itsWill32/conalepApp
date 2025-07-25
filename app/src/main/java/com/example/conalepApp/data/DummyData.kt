package com.example.conalepApp.data

import com.example.conalepApp.R


data class CareerDetails(
    val fullName: String,
    val description: String,
    val profile: String,
    val imageRes1: Int,
    val imageRes2: Int
)

data class User(
    val name: String,
    val career: String,
    val email: String,
    val phone: String,
    val address: String,
    val profilePic: Int
)

data class NotificationItem(
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean
)

data class Subject(
    val name: String,
    val group: String,
    val studentCount: Int,
    val classroom: String,
    val schedule: String,
    val days: List<String>,
    val iconRes: Int
)

data class Teacher(
    val name: String,
    val totalStudents: Int,
    val activeSubjects: Int,
    val classDays: Int
)

enum class AttendanceStatus {
    PRESENT, ABSENT, PERMISSION, LATE
}

data class StudentAttendance(
    val id: Int,
    val name: String,
    var status: AttendanceStatus
)



object DummyData {

    val loggedInUser = User(
        name = "José Alberto Carrasco Sánchez",
        career = "Profesional Técnico en Informática",
        email = "223532@ids.upchiapas.edu.mx",
        phone = "+52 971-161-3927",
        address = "9a. Av. Nte. Ote",
        profilePic = R.drawable.perfil_alberto
    )

    val careers = listOf(
        "Profesional técnico en Informática",
        "Profesional técnico en Autotrónica",
        "Profesional técnico en Hospitalidad Turística",
        "Profesional técnico en Construcción",
        "Profesional técnico en Mantenimiento Automotriz"
    )

    val careerDetailsMap = mapOf(
        careers[0] to CareerDetails(
            fullName = "Profesional técnico bachiller en Informatica",
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten procesar y comunicar información...",
            profile = "Desempeñar funciones técnico operativas inherentes al desarrollo e implantación de soluciones de tecnologías de información basados en la automatización...",
            imageRes1 = R.drawable.informatica_details1,
            imageRes2 = R.drawable.informatica_details2
        ),
        careers[1] to CareerDetails(
            fullName = "Profesional técnico bachiller en Autotrónica",
            description = "Descripción de ejemplo para Autotrónica...",
            profile = "Perfil de egreso de ejemplo para Autotrónica...",
            imageRes1 = R.drawable.informatica_details1,
            imageRes2 = R.drawable.informatica_details2
        ),
        careers[2] to CareerDetails(
            fullName = "Profesional técnico bachiller en Hospitalidad Turística",
            description = "Descripción de ejemplo para Hospitalidad Turística...",
            profile = "Perfil de egreso de ejemplo para Hospitalidad Turística...",
            imageRes1 = R.drawable.informatica_details1,
            imageRes2 = R.drawable.informatica_details2
        ),
        careers[3] to CareerDetails(
            fullName = "Profesional técnico bachiller en Construcción",
            description = "Descripción de ejemplo para Construcción...",
            profile = "Perfil de egreso de ejemplo para Construcción...",
            imageRes1 = R.drawable.informatica_details1,
            imageRes2 = R.drawable.informatica_details2
        ),
        careers[4] to CareerDetails(
            fullName = "Profesional técnico bachiller en Mantenimiento Automotriz",
            description = "Descripción de ejemplo para Mantenimiento Automotriz...",
            profile = "Perfil de egreso de ejemplo para Mantenimiento Automotriz...",
            imageRes1 = R.drawable.informatica_details1,
            imageRes2 = R.drawable.informatica_details2
        )
    )

    val notifications = listOf(
        NotificationItem("Nueva tarea", "Realizar un mapa mental sobre la revolución mexicana", "Hace 30 min", false),
        NotificationItem("Calificación Publicada", "Se ha publicado tu calificación de la Unidad 2 de Cálculo.", "Hace 2 horas", false),
        NotificationItem("Aviso General", "Suspensión de clases el día 5 de mayo.", "Ayer", true),
        NotificationItem("Falta registrada", "Se registró una falta en la materia de Física.", "Ayer", true),
        NotificationItem("Evento Próximo", "No olvides la conferencia de Ciberseguridad el Julio 08, 2025.", "Julio 08, 2025", true)
    )

    val teacher = Teacher(
        name = "José Alonso Macías Montoya",
        totalStudents = 82,
        activeSubjects = 3,
        classDays = 4
    )

    val subjects = listOf(
        Subject(
            name = "Matemáticas",
            group = "1º - B",
            studentCount = 28,
            classroom = "",
            schedule = "",
            days = emptyList(),
            iconRes = R.drawable.ic_subjects
        ),
        Subject(
            name = "Química",
            group = "3º - B",
            studentCount = 22,
            classroom = "Aula 201",
            schedule = "12:00 - 14:00",
            days = listOf("Mier", "Jue", "Vie"),
            iconRes = R.drawable.ic_subjects
        ),
        Subject(
            name = "Física",
            group = "2º - B",
            studentCount = 32,
            classroom = "Aula 211",
            schedule = "9:00 - 11:00",
            days = listOf("Lun", "Mier", "Vie"),
            iconRes = R.drawable.ic_subjects
        )
    )

    val classRoster = listOf(
        StudentAttendance(1, "José Alberto Carrasco", AttendanceStatus.PRESENT),
        StudentAttendance(2, "Adrián Espinoza Enríquez", AttendanceStatus.ABSENT),
        StudentAttendance(3, "Deyvit Sánchez Reyes", AttendanceStatus.PERMISSION),
        StudentAttendance(4, "William de Jesús García", AttendanceStatus.LATE)
    )
}