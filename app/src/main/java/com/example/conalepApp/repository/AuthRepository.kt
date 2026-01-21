package com.example.conalepApp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.conalepApp.api.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import android.os.Environment
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    private val apiService = ApiClient.apiService

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NOMBRE_KEY = stringPreferencesKey("user_nombre")
        private val USER_APELLIDO_PATERNO_KEY = stringPreferencesKey("user_apellido_paterno")
        private val USER_APELLIDO_MATERNO_KEY = stringPreferencesKey("user_apellido_materno")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val USER_GRADO_KEY = stringPreferencesKey("user_grado")
        private val USER_GRUPO_KEY = stringPreferencesKey("user_grupo")
        private val USER_MATRICULA_KEY = stringPreferencesKey("user_matricula")
        private val USER_TELEFONO_KEY = stringPreferencesKey("user_telefono")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userData: Flow<User?> = context.dataStore.data.map { preferences ->
        val id = preferences[USER_ID_KEY]?.toIntOrNull()
        val nombre = preferences[USER_NOMBRE_KEY]
        val apellidoPaterno = preferences[USER_APELLIDO_PATERNO_KEY]
        val apellidoMaterno = preferences[USER_APELLIDO_MATERNO_KEY]
        val email = preferences[USER_EMAIL_KEY]
        val userType = preferences[USER_TYPE_KEY]
        val grado = preferences[USER_GRADO_KEY]
        val grupo = preferences[USER_GRUPO_KEY]
        val matricula = preferences[USER_MATRICULA_KEY]
        val telefono = preferences[USER_TELEFONO_KEY]

        if (id != null && nombre != null && apellidoPaterno != null &&
            apellidoMaterno != null && email != null && userType != null
        ) {
            User(
                id = id,
                nombre = nombre,
                apellido_paterno = apellidoPaterno,
                apellido_materno = apellidoMaterno,
                email = email,
                user_type = userType,
                grado = grado,
                grupo = grupo,
                matricula = matricula,
                telefono = telefono
            )
        } else null
    }

    // ===== AUTENTICACIÓN CON OTP =====
    suspend fun requestOTP(email: String): Result<Boolean> {
        return try {
            val response = apiService.requestOTP(RequestOTPRequest(email))
            if (response.isSuccessful && response.body() != null) {
                val otpResponse = response.body()!!
                if (otpResponse.success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(otpResponse.error ?: otpResponse.message))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOTP(email: String, code: String): Result<User> {
        return try {
            val response = apiService.verifyOTP(VerifyOTPRequest(email, code))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                if (loginResponse.success && loginResponse.user != null) {
                    saveAuthData(loginResponse.token!!, loginResponse.user)
                    Result.success(loginResponse.user)
                } else {
                    val errorMsg = loginResponse.error ?: loginResponse.message
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Código inválido o expirado"
                    400 -> "Datos incorrectos"
                    404 -> "Usuario no encontrado"
                    else -> "Error de conexión: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de conexión"))
        }
    }

    // ===== PERFIL Y LOGOUT =====
    suspend fun getProfile(): Result<User> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Boolean> {
        return try {
            val token = getStoredToken()
            if (!token.isNullOrEmpty()) {
                apiService.logout("Bearer $token")
            }
            clearAuthData()
            Result.success(true)
        } catch (e: Exception) {
            clearAuthData()
            Result.success(true)
        }
    }

    suspend fun healthCheck(): Result<Boolean> {
        return try {
            val response = apiService.healthCheck()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== MATERIAS =====
    suspend fun getMateriasMaestro(): Result<List<MateriaMaestroAPI>> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getMateriasMaestro("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener materias"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMateriasAlumno(): Result<List<MateriaAlumnoAPI>> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getMateriasAlumno("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener materias"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== ASISTENCIAS =====
    suspend fun getAlumnosParaAsistencia(materiaId: Int): Result<AlumnosAsistenciaResponse> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getAlumnosParaAsistencia(materiaId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener alumnos"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarAsistencias(
        materiaId: Int,
        fecha: String,
        asistencias: List<AsistenciaItem>
    ): Result<Boolean> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val request = GuardarAsistenciasRequest(fecha, asistencias)
            val response = apiService.guardarAsistencias(materiaId, "Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al guardar asistencias"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAsistenciasPorFecha(materiaId: Int, fecha: String): Result<AsistenciasFechaResponse> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getAsistenciasPorFecha(materiaId, fecha, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "No hay asistencias para esta fecha"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistorialAsistencias(materiaId: Int, limite: Int = 10): Result<HistorialAsistenciasResponse> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getHistorialAsistencias(materiaId, limite, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "No hay historial disponible"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== NOTIFICACIONES =====
    suspend fun getDestinatariosParaNotificacion(): Result<NotificacionDestinatarios> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getDestinatariosParaNotificacion("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener destinatarios"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearNotificacion(
        titulo: String,
        mensaje: String,
        tipoDestinatario: String,
        destinatarios: List<Int>
    ): Result<CrearNotificacionResult> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val request = CrearNotificacionRequest(titulo, mensaje, tipoDestinatario, destinatarios)
            val response = apiService.crearNotificacion("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al crear notificación"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisNotificaciones(status: String? = null): Result<List<NotificacionItem>> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getMisNotificaciones("Bearer $token", status)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener notificaciones"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisNotificacionesAlumno(): Result<List<NotificacionItem>> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            val response = apiService.getMisNotificacionesAlumno("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.error ?: "Error al obtener notificaciones"))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== UTILIDADES PRIVADAS =====
    private suspend fun saveAuthData(token: String, user: User) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = user.id.toString()
            preferences[USER_NOMBRE_KEY] = user.nombre
            preferences[USER_APELLIDO_PATERNO_KEY] = user.apellido_paterno
            preferences[USER_APELLIDO_MATERNO_KEY] = user.apellido_materno
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_TYPE_KEY] = user.user_type
            user.grado?.let { preferences[USER_GRADO_KEY] = it }
            user.grupo?.let { preferences[USER_GRUPO_KEY] = it }
            user.matricula?.let { preferences[USER_MATRICULA_KEY] = it }
            user.telefono?.let { preferences[USER_TELEFONO_KEY] = it }
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val token = getStoredToken()
        return !token.isNullOrEmpty()
    }

    suspend fun getCurrentUser(): User? {
        return userData.first()
    }

    private suspend fun getStoredToken(): String? {
        return authToken.first()
    }

    private suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }



    suspend fun descargarReportePDF(
        materiaId: Int,
        fechaInicio: String,
        fechaFin: String
    ): Result<File> {
        return try {
            val token = getStoredToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay token de sesión"))
            }

            // ✅ CORREGIDO: fecha_inicio y fecha_fin con guión bajo
            val params = mapOf(
                "fecha_inicio" to fechaInicio,
                "fecha_fin" to fechaFin
            )

            val response = apiService.descargarReportePDF(
                materiaId = materiaId,
                params = params,
                token = "Bearer $token"
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val fileName = "Reporte_${materiaId}_${System.currentTimeMillis()}.pdf"
                val file = File(downloadsDir, fileName)

                body.byteStream().use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                Result.success(file)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }





}
