
package com.example.conalepApp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.conalepApp.api.ApiClient
import com.example.conalepApp.api.LoginRequest
import com.example.conalepApp.api.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.conalepApp.api.MateriaMaestroAPI
import com.example.conalepApp.api.MateriaAlumnoAPI
import com.example.conalepApp.api.AlumnosAsistenciaResponse
import com.example.conalepApp.api.AsistenciaItem
import com.example.conalepApp.api.GuardarAsistenciasRequest
import com.example.conalepApp.api.AsistenciasFechaResponse
import com.example.conalepApp.api.CrearNotificacionRequest
import com.example.conalepApp.api.CrearNotificacionResult
import com.example.conalepApp.api.HistorialAsistenciasResponse
import com.example.conalepApp.api.NotificacionItem
import com.example.conalepApp.api.NotificacionDestinatarios


// Extensión para DataStore
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

    // Obtener token guardado
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Obtener datos del usuario guardado
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
            apellidoMaterno != null && email != null && userType != null) {
            User(
                id = id,
                nombre = nombre,
                apellido_paterno = apellidoPaterno,
                apellido_materno = apellidoMaterno,
                email = email,
                userType = userType,
                grado = grado,
                grupo = grupo,
                matricula = matricula,
                telefono = telefono
            )
        } else null
    }

    // Login con email
    suspend fun login(email: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                if (loginResponse.success) {
                    // Guardar token y datos del usuario
                    saveAuthData(loginResponse.token, loginResponse.user)
                    Result.success(loginResponse.user)
                } else {
                    Result.failure(Exception(loginResponse.message))
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener perfil del usuario
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

    // Logout
    suspend fun logout(): Result<Boolean> {
        return try {
            val token = getStoredToken()
            if (!token.isNullOrEmpty()) {
                apiService.logout("Bearer $token")
            }

            // Limpiar datos locales
            clearAuthData()
            Result.success(true)
        } catch (e: Exception) {
            // Aún si falla la llamada al servidor, limpiamos datos locales
            clearAuthData()
            Result.success(true)
        }
    }

    // Verificar conectividad con el servidor
    suspend fun healthCheck(): Result<Boolean> {
        return try {
            val response = apiService.healthCheck()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funciones privadas para manejo de datos
    private suspend fun saveAuthData(token: String, user: User) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = user.id.toString()
            preferences[USER_NOMBRE_KEY] = user.nombre
            preferences[USER_APELLIDO_PATERNO_KEY] = user.apellido_paterno
            preferences[USER_APELLIDO_MATERNO_KEY] = user.apellido_materno
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_TYPE_KEY] = user.userType

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

    // Método para obtener el usuario actual desde el almacenamiento local
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

    suspend fun guardarAsistencias(materiaId: Int, fecha: String, asistencias: List<AsistenciaItem>): Result<Boolean> {
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


    // Para Maestros - Obtener destinatarios disponibles
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

    // Para Maestros - Crear notificación
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

    // Para Maestros - Ver mis notificaciones
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

    // Para Alumnos - Ver mis notificaciones
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
                    Result.failure(
                        Exception(
                            apiResponse.error ?: "Error al obtener notificaciones"
                        )
                    )
                }
            } else {
                Result.failure(Exception("Error de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}