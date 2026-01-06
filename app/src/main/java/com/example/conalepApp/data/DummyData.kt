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

data class AttendanceRecord(
    val subjectName: String,
    val date: String,
    val present: Int,
    val absent: Int,
    val late: Int
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
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten procesar y comunicar información, utilizando herramientas avanzadas para la elaboración de documentos digitales; implementar proyectos de software para la automatización de procesos de la organización; dar mantenimiento a software y/o tecnologías de información y comunicación, y administrar áreas de tecnologías de información y comunicación.",
            profile = "Desempeñar funciones técnico operativas inherentes al desarrollo e implantación de soluciones de tecnologías de información basados en la automatización, organización, codificación, recuperación de la información y optimización de recursos informáticos a fin de impulsar la competitividad, las buenas prácticas y toma de decisiones en organizaciones o empresas de cualquier ámbito.",
            imageRes1 = R.drawable.informatica_details1_1,
            imageRes2 = R.drawable.informatica_details2
        ),
        careers[1] to CareerDetails(
            fullName = "Profesional técnico bachiller en Autotrónica",
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten mantener los sistemas eléctricos, electrónicos y de inyección de vehículos automotrices; realizar el servicio a unidades de control electrónicas, empleando dispositivos de escaneo y diagnóstico; realizar el servicio a sensores y actuadores y diagnosticar fallas en vehículos y sistemas automotrices.",
            profile = "Realizar el mantenimiento de sistemas automotrices mecánicos, hidráulicos y eléctricos, controlados principalmente por medios electrónicos, aplicando normas técnicas vigentes, especificaciones y manuales de fabricantes, para lograr el óptimo funcionamiento del vehículo automotriz a gasolina, a diesel e híbrido, así como la satisfacción del cliente.",
            imageRes1 = R.drawable.autotronica_details1,
            imageRes2 = R.drawable.carrera_autotronica
        ),
        careers[2] to CareerDetails(
            fullName = "Profesional técnico bachiller en Hospitalidad Turística",
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten brindar atención al huésped/cliente en su arribo, estancia y partida, empleando la logística administrativa establecida por la empresa de alojamiento; así como vender servicios y productos turísticos, empleando procedimientos mercadotécnicos definidos por la empresa.",
            profile = "Realizar actividades de servicio, atención y promoción en la industria de la hospitalidad turística, apegándose a las políticas, técnicas y procesos de trabajo, a fin de satisfacer las necesidades y requerimientos del turista.",
            imageRes1 = R.drawable.hospitalidad_details1,
            imageRes2 = R.drawable.carrera_turismo
        ),
        careers[3] to CareerDetails(
            fullName = "Profesional técnico bachiller en Construcción",
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten realizar levantamientos y trazos topográficos; dibujar e interpretar planos de acuerdo con las especificaciones de diseño de la obra; así como supervisar y controlar las obras de acuerdo con las especificaciones del proyecto y los procedimientos constructivos.",
            profile = "Formar Profesionales Técnicos y Profesionales Técnicos-Bachiller competentes para desempeñarse en el nivel de mandos medios en actividades de supervisión y control durante la construcción y restauración de obras civiles privadas y públicas aplicando las técnicas constructivas y de control con el fin de cumplir con las especificaciones del proyecto y las necesidades de los clientes.",
            imageRes1 = R.drawable.construccion_details1,
            imageRes2 = R.drawable.carrera_construccion
        ),
        careers[4] to CareerDetails(
            fullName = "Profesional técnico bachiller en Mantenimiento Automotriz",
            description = "Es la persona que se prepara para participar activa y competitivamente en el mercado productivo, desarrollando competencias profesionales básicas que le permiten identificar las características técnicas de maquinaria, equipo y componentes de vehículos y sistemas automotrices; realizar servicios de lubricación, afinación, carburación, puesta a punto y verificación de emisiones de gases contaminantes de vehículos automotrices a gasolina; programar el mantenimiento de vehículos automotrices ycomercializar servicios de mantenimiento.",
            profile = "Realizar el mantenimiento de sistemas automotrices mecánicos, hidráulicos, eléctricos y electrónicos, aplicando normas técnicas vigentes, especificaciones y manuales de fabricantes, para lograr el óptimo funcionamiento del vehículo automotriz a gasolina o a diesel y la satisfacción del cliente.",
            imageRes1 = R.drawable.automotriz_details1,
            imageRes2 = R.drawable.carrera_automotriz
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
            iconRes = R.drawable.ic_subjects
        ),
        Subject(
            name = "Química",
            group = "3º - B",
            studentCount = 22,
            classroom = "Aula 201",
            iconRes = R.drawable.ic_subjects
        ),
        Subject(
            name = "Física",
            group = "2º - B",
            studentCount = 32,
            classroom = "Aula 211",
            iconRes = R.drawable.ic_subjects
        )
    )

    val classRoster = listOf(
        StudentAttendance(1, "José Alberto Carrasco", AttendanceStatus.PRESENT),
        StudentAttendance(2, "Adrián Espinoza Enríquez", AttendanceStatus.ABSENT),
        StudentAttendance(3, "Deyvit Sánchez Reyes", AttendanceStatus.PERMISSION),
        StudentAttendance(4, "William de Jesús García", AttendanceStatus.LATE)
    )

    val attendanceHistory = listOf(
        AttendanceRecord("Matemáticas", "15 de julio, 2025", 25, 2, 1),
        AttendanceRecord("Química", "14 de julio, 2025", 20, 1, 1),
        AttendanceRecord("Física", "13 de julio, 2025", 30, 2, 0)
    )

}