package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.domain.entities.*
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.data.repositories.UsuarioRepositoryImpl

interface CrearUsuarioUseCase: UseCaseSuspend<CrearUsuariInput, AppResult<Usuario, Throwable>>

data class CrearUsuariInput(
    val nombre: String,
    val apellido1: String,
    val apellido2: String? = null, // Hacer apellido2 opcional
    val correo: String,
    val rol: String
)
class CrearUsuarioUseCaseImpl(
    private val usuarioRepository: UsuarioRepository
) : CrearUsuarioUseCase {

    /*Todo contraseña generada automaticamente, enviar correo con nombre de usuario y contraseña
        // Validar los datos de entrada (nombre, apellido1, apellido2, rol, contrasena)
        // Crear un nuevo objeto Usuario con los datos ingresados
        val nuevoUsuario = Usuario(nombre, apellido1, apellido2, rol, contrasena)
        // Guardar el nuevo usuario en el repositorio
        usuarioRepository.guardarUsuario(nuevoUsuario)
        // Registrar la acción de creación de usuario
        // Notificar al usuario el éxito de la creación*/

    override suspend fun invoke(input: CrearUsuariInput): AppResult<Usuario, Throwable> {
        val usuario = input.toUser() // Utilizamos la extensión para convertir el input a Usuario
        return appRunCatching {
            usuarioRepository.createUser(usuario)
        }
    }

    // Definimos la extensión para convertir CrearUsuariInput a Usuario
    private fun CrearUsuariInput.toUser() = Usuario(
        nombre = Nombre(this.nombre),
        apellido1 = Apellido(this.apellido1),
        correo = Correo(this.correo),
        rol = Rol(this.rol)
    )
}
