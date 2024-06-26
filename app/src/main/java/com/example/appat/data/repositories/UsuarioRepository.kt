package com.example.appat.data.repositories

import android.content.Context
import android.util.Log
import com.example.appat.data.local.ApiService
import com.example.appat.data.local.CentroEscolarDTO
import com.example.appat.data.local.UsuarioDTO
import com.example.appat.domain.entities.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

interface UsuarioRepository {
    suspend fun createUser(usuario: Usuario): Usuario
    suspend fun login(username: String, password: String): Usuario
    suspend fun getUsersByCentroEscolar(centroEscolarId: String, token: String?): List<Usuario>
    suspend fun updateUser(usuario: Usuario): Usuario
    suspend fun getUserById(userId: String, token: String?): Usuario
    suspend fun deleteUser(userId: String, token: String?)

}

class UsuarioRepositoryImpl(
    private val apiService: ApiService,
    private val context: Context
): UsuarioRepository {
    override suspend fun createUser(usuario: Usuario): Usuario {
        val token = usuario.token
        val usuarioDTO = usuario.toDTO()
        try {
            val createdUsuarioDTO = apiService.createUsuario(usuarioDTO, token)
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
            val usuario = loginResponse.user
            return usuario.toDomain(loginResponse.token)  // Suponiendo que tienes un método toDomain en UsuarioDTO
        } catch (e: Exception) {
            Log.d("LoginRepo", "Attempting to login with username: $username")
            throw Exception("Login failed: ${e.localizedMessage}")
        }
    }

    override suspend fun getUsersByCentroEscolar(centroEscolarId: String, token: String?): List<Usuario> {
        try {
            val response = apiService.getUsersByCentroEscolar(centroEscolarId, token)
            return response.map { it.toDomain() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateUser(usuario: Usuario): Usuario {
        val token = usuario.token
        val usuarioDTO = usuario.toDTO()
        try {
            val updatedUsuarioDTO = apiService.updateUsuario(usuarioDTO.userId, usuarioDTO, token)
            return updatedUsuarioDTO.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserById(userId: String, token: String?): Usuario {
        try {
            val usuarioDTO = apiService.getUserById(userId, token)
            return usuarioDTO.toDomain(token)
        } catch (e: Exception) {
            throw e
        }
    }
    override suspend fun deleteUser(userId: String, token: String?) {
        try {
            apiService.deleteUser(userId, token)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun Usuario.toDTO(): UsuarioDTO {
        return UsuarioDTO(
            userId = this.userId,
            firstName = this.nombre.nombre, // Cambio de nombre de campo
            lastName = this.apellido1.apellido, // Cambio de nombre de campo
            username = this.username.username,
            email = this.correo.correo,
            password = this.contraseña?.contraseña,
            rol = this.rol.rol,
            centroEscolar = this.centroEscolar?.toDTO(),
            centroEscolarId = this.centroEscolar?.centroId,
            cursos = this.cursos
        )
    }

    private fun UsuarioDTO.toDomain(token: String? = null): Usuario {
        return Usuario(
            userId = this.userId,
            nombre = Nombre(this.firstName), // Cambio de nombre de campo
            apellido1 = Apellido(this.lastName), // Cambio de nombre de campo
            username = Username.fromCompleteString(this.username),
            correo = Correo(this.email),
            contraseña = this.password?.let { Contraseña.crearNueva(it) },
            rol = Rol(this.rol),
            centroEscolar = this.centroEscolar?.toDomain(),
            cursos = this.cursos,
            token = token
        )
    }

    private fun CentroEscolar.toDTO(): CentroEscolarDTO {
        return CentroEscolarDTO(
            centroId = this.centroId,
            nombre = this.nombre,
            direccion = this.direccion,
            telefono = this.telefono,
            correo = this.correo,
        )
    }

    private fun CentroEscolarDTO.toDomain(): CentroEscolar {
        return CentroEscolar(
            centroId = this.centroId,
            nombre = this.nombre,
            direccion = this.direccion,
            telefono = this.telefono,
            correo = this.correo,
        )
    }
}