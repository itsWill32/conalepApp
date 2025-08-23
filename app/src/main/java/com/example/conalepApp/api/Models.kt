package com.example.conalepApp.api

data class LoginRequest(
    val email: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val email: String,
    val userType: String,
    // Campos opcionales según tipo de usuario
    val grado: String? = null,
    val grupo: String? = null,
    val matricula: String? = null,
    val telefono: String? = null
) {
    val fullName: String
        get() = "$nombre $apellido_paterno $apellido_materno"

    val isAlumno: Boolean
        get() = userType == "alumno"

    val isMaestro: Boolean
        get() = userType == "maestro"

    val isAdministrador: Boolean
        get() = userType == "administrador"
}

data class MateriaMaestroAPI(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String,
    val total_estudiantes: Int
)

data class MateriaAlumnoAPI(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String,
    val profesor_nombre: String,
    val profesor_apellido_paterno: String,
    val profesor_apellido_materno: String,
    val fecha_inscripcion: String
)

data class AlumnoAsistencia(
    val alumno_id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val matricula: String,
    val grado: String,
    val grupo: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellido_paterno $apellido_materno"
}

data class ClaseInfo(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String
)

data class AlumnosAsistenciaResponse(
    val clase: ClaseInfo,
    val alumnos: List<AlumnoAsistencia>,
    val total_alumnos: Int
)

data class AsistenciaItem(
    val alumno_id: Int,
    val estado: String
)

data class GuardarAsistenciasRequest(
    val fecha: String,
    val asistencias: List<AsistenciaItem>
)
data class AsistenciaExistente(
    val alumno_id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val matricula: String,
    val estado_asistencia: String,
    val fecha_asistencia: String
)

data class AsistenciasFechaResponse(
    val clase: ClaseInfo,
    val fecha: String,
    val asistencias: List<AsistenciaExistente>,
    val total_alumnos: Int
)

data class HistorialItem(
    val fecha_asistencia: String,
    val total_registros: Int,
    val presentes: Int,
    val ausentes: Int,
    val retardos: Int,
    val justificados: Int
)

data class HistorialAsistenciasResponse(
    val clase: ClaseInfo,
    val historial: List<HistorialItem>
)

data class MateriaBasica(
    val clase_id: Int,
    val nombre_clase: String,
    val codigo_clase: String,
    val total_alumnos: Int
)

data class AlumnoNotificacion(
    val alumno_id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val matricula: String,
    val grado: String,
    val grupo: String,
    val materias_compartidas: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellido_paterno $apellido_materno"
}

data class NotificacionDestinatarios(
    val materias: List<MateriaBasica>,
    val alumnos: List<AlumnoNotificacion>,
    val total_materias: Int,
    val total_alumnos: Int
)

data class CrearNotificacionRequest(
    val titulo: String,
    val mensaje: String,
    val tipo_destinatario: String, // "Alumno_Especifico", "Materia_Completa", "Multiples_Materias"
    val destinatarios: List<Int>
)

data class NotificacionItem(
    val notificacion_id: Int,
    val titulo: String,
    val mensaje: String,
    val tipo_destinatario: String,
    val status: String,
    val fecha_creacion: String?,
    val destinatarios_info: String? = null,
    val creado_por_tipo: String? = null
)
data class CrearNotificacionResult(
    val notificacion_id: Int,
    val status: String,
    val titulo: String,
    val destinatarios: Int
)
