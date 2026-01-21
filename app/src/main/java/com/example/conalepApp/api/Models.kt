package com.example.conalepApp.api
import com.google.gson.annotations.SerializedName
// ===== AUTENTICACIÓN =====
data class LoginRequest(
    val email: String
)

data class RequestOTPRequest(
    val email: String
)

data class VerifyOTPRequest(
    val email: String,
    val code: String
)

data class OTPResponse(
    val success: Boolean,
    val message: String,
    val data: OTPData? = null,
    val error: String? = null
)

data class OTPData(
    val email: String,
    val expiresIn: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: User? = null,
    val error: String? = null
)

data class User(
    val id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val email: String,
    @SerializedName("userType")  // ✅ AGREGA ESTO
    val user_type: String,
    // Campos opcionales según tipo de usuario
    val grado: String? = null,
    val grupo: String? = null,
    val matricula: String? = null,
    val telefono: String? = null
) {
    val fullName: String
        get() = "$nombre $apellido_paterno $apellido_materno"

    val isAlumno: Boolean
        get() = user_type == "alumno"

    val isMaestro: Boolean
        get() = user_type == "maestro"

    val isAdministrador: Boolean
        get() = user_type == "administrador"
}


data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null
)

// ===== MATERIAS =====
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

// ===== ASISTENCIAS =====
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

// ===== NOTIFICACIONES =====
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
    val grado: Int,
    val grupo: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellido_paterno $apellido_materno"
}

data class NotificacionDestinatarios(
    val mis_materias: List<MateriaBasica>,
    val mis_alumnos: List<AlumnoNotificacion>,
    val alumnos_por_materia: Map<String, List<AlumnoNotificacion>>? = null,
    val total_materias: Int? = null,
    val total_alumnos: Int? = null
)

data class CrearNotificacionRequest(
    val titulo: String,
    val mensaje: String,
    val tipo_destinatario: String,
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
    val mensaje: String,
    val status: String
)
data class GenerarReportePDFRequest(
    val clase_id: Int,
    val fecha_inicio: String,  // yyyy-MM-dd
    val fecha_fin: String      // yyyy-MM-dd
)

data class ReportePDFResponse(
    val success: Boolean,
    val message: String,
    val data: ReportePDFData? = null,
    val error: String? = null
)

data class ReportePDFData(
    val nombre_archivo: String,
    val url: String,
    val tamano: Long
)
