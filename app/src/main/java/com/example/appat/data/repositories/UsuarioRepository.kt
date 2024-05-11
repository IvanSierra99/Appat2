package com.example.appat.data.repositories

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
}

class UsuarioRepositoryImpl(private val apiService: UsuarioApiService): UsuarioRepository {
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

    private fun Usuario.toDTO(): UsuarioDTO {
        return UsuarioDTO(
            userId = this.userId,
            nombre = this.nombre.nombre,
            apellido1 = this.apellido1.apellido,
            apellido2 = this.apellido2?.apellido,
            username = this.username.username,
            correo = this.correo.correo,
            contraseña = this.contraseña.contraseña,
            rol = this.rol.rol
        )
    }

    private fun UsuarioDTO.toDomain(): Usuario {
        return Usuario(
            userId = this.userId,
            nombre = Nombre(this.nombre),
            apellido1 = Apellido(this.apellido1),
            apellido2 = this.apellido2?.let { ApellidoOpcional(it) },
            username = Username.fromCompleteString(this.username), // Asegúrate de que el constructor no sea privado
            correo = Correo(this.correo),
            contraseña = Contraseña.crearNueva(this.contraseña), // Ajusta según la lógica de creación de contraseña
            rol = Rol(this.rol)
        )
    }
}