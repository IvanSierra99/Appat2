package com.example.appat.data.repositories

import android.content.Context
import com.example.appat.core.AppResult
import com.example.appat.data.local.UsuarioApiService
import com.example.appat.data.local.UsuarioDTO
import com.example.appat.domain.entities.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

interface UsuarioRepository {
    suspend fun createUser(usuario: Usuario): Usuario
    suspend fun login(username: String, password: String): Usuario
}

class UsuarioRepositoryImpl(
    private val apiService: UsuarioApiService,
    private val context: Context
): UsuarioRepository {
    override suspend fun createUser(usuario: Usuario): Usuario {
        val usuarioDTO = usuario.toDTO()
        try {
            val createdUsuarioDTO = apiService.createUsuario(usuarioDTO)
            return createdUsuarioDTO.toDomain()
        } catch (e: ClientRequestException) {
            // Captura errores de respuesta HTTP 4xx
            throw Exception("Error del cliente al crear usuario: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            // Captura errores de respuesta HTTP 5xx
            throw Exception("Error del servidor al crear usuario: ${e.response.status.description}")
        } catch (e: SerializationException) {
            // Captura errores de serialización/deserialización
            throw Exception("Error de serialización al crear usuario: ${e.localizedMessage}")
        } catch (e: Exception) {
            // Captura cualquier otro tipo de error
            throw Exception("Error genérico al crear usuario: ${e.localizedMessage}")
        }
    }

    override suspend fun login(username: String, password: String): Usuario {
        try {
            val loginResponse = apiService.login(username, password)
            val usuario = loginResponse.user.toDomain()
            saveUserData(usuario)
            return usuario  // Suponiendo que tienes un método toDomain en UsuarioDTO
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.localizedMessage}")
        }
    }

    private fun Usuario.toDTO(): UsuarioDTO {
        return UsuarioDTO(
            userId = this.userId,
            firstName = this.nombre.nombre, // Cambio de nombre de campo
            lastName = this.apellido1.apellido, // Cambio de nombre de campo
            username = this.username.username,
            correo = this.correo.correo,
            password = this.contraseña.contraseña,
            rol = this.rol.rol
        )
    }

    private fun UsuarioDTO.toDomain(): Usuario {
        return Usuario(
            userId = this.userId,
            nombre = Nombre(this.firstName), // Cambio de nombre de campo
            apellido1 = Apellido(this.lastName), // Cambio de nombre de campo
            username = Username.fromCompleteString(this.username),
            correo = Correo(this.correo),
            contraseña = Contraseña.crearNueva(this.password),
            rol = Rol(this.rol)
        )
    }

    private fun saveUserData(usuario: Usuario) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("userId", usuario.userId.toString())
            putString("username", usuario.username.username)
            apply()
        }
    }
}