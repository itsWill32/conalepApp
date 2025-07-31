package com.example.conalepApp.api

import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiService {
    //auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<User>>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Any>>

    //verificar conexión
    @GET("api/health")
    suspend fun healthCheck(): Response<ApiResponse<Any>>

    //Materias
    @GET("api/materias/maestro")
    suspend fun getMateriasMaestro(@Header("Authorization") token: String): Response<ApiResponse<List<MateriaMaestroAPI>>>

    @GET("api/materias/alumno")
    suspend fun getMateriasAlumno(@Header("Authorization") token: String): Response<ApiResponse<List<MateriaAlumnoAPI>>>

    //ASISTENCIAS
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
    // Agregar estos endpoints a tu interface ApiService

    // NOTIFICACIONES - Para Maestros
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

    // NOTIFICACIONES - Para Alumnos
    @GET("api/notificaciones/alumno/mis-notificaciones")
    suspend fun getMisNotificacionesAlumno(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<NotificacionItem>>>

}

