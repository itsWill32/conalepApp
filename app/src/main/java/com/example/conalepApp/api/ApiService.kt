package com.example.conalepApp.api

import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody
import retrofit2.http.Streaming

interface ApiService {
    // ===== AUTENTICACIÓN =====
    @POST("api/auth/mobile/request-code")
    suspend fun requestOTP(@Body request: RequestOTPRequest): Response<OTPResponse>

    @POST("api/auth/mobile/verify-code")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<LoginResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/mobile/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<User>>

    @POST("api/auth/mobile/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Any>>

    @GET("api/health")
    suspend fun healthCheck(): Response<ApiResponse<Any>>

    // ===== MATERIAS =====
    @GET("api/materias/maestro")
    suspend fun getMateriasMaestro(@Header("Authorization") token: String): Response<ApiResponse<List<MateriaMaestroAPI>>>

    @GET("api/materias/alumno")
    suspend fun getMateriasAlumno(@Header("Authorization") token: String): Response<ApiResponse<List<MateriaAlumnoAPI>>>

    // ===== ASISTENCIAS =====
    @GET("api/asistencias/materia/{materiaId}/alumnos")
    suspend fun getAlumnosParaAsistencia(
        @Path("materiaId") materiaId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<AlumnosAsistenciaResponse>>

    @POST("api/asistencias/materia/{materiaId}/guardar")
    suspend fun guardarAsistencias(
        @Path("materiaId") materiaId: Int,
        @Header("Authorization") token: String,
        @Body request: GuardarAsistenciasRequest
    ): Response<ApiResponse<Any>>

    @GET("api/asistencias/materia/{materiaId}/fecha")
    suspend fun getAsistenciasPorFecha(
        @Path("materiaId") materiaId: Int,
        @Query("fecha") fecha: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<AsistenciasFechaResponse>>

    @GET("api/asistencias/materia/{materiaId}/historial")
    suspend fun getHistorialAsistencias(
        @Path("materiaId") materiaId: Int,
        @Query("limite") limite: Int = 10,
        @Header("Authorization") token: String
    ): Response<ApiResponse<HistorialAsistenciasResponse>>

    // ===== PDF =====
    @Streaming  // ✅ AGREGADO
    @GET("api/asistencias/materia/{materiaId}/pdf")
    suspend fun descargarReportePDF(
        @Path("materiaId") materiaId: Int,
        @QueryMap params: Map<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    // ===== NOTIFICACIONES - Para Maestros =====
    @GET("api/notificaciones/maestro/destinatarios")
    suspend fun getDestinatariosParaNotificacion(
        @Header("Authorization") token: String
    ): Response<ApiResponse<NotificacionDestinatarios>>

    @POST("api/notificaciones/maestro/crear")
    suspend fun crearNotificacion(
        @Header("Authorization") token: String,
        @Body request: CrearNotificacionRequest
    ): Response<ApiResponse<CrearNotificacionResult>>

    @GET("api/notificaciones/maestro/mis-notificaciones")
    suspend fun getMisNotificaciones(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<NotificacionItem>>>

    // ===== NOTIFICACIONES - Para Alumnos =====
    @GET("api/notificaciones/alumno/mis-notificaciones")
    suspend fun getMisNotificacionesAlumno(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<NotificacionItem>>>
}
