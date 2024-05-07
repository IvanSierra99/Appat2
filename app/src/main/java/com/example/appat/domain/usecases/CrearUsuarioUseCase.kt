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
    private val usuarioRepository: UsuarioRepositoryImpl
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
        val usuario = Usuario(
            nombre = Nombre(input.nombre),
            apellido1 = Apellido(input.apellido1),
            apellido2 = input.apellido2?.let { Apellido(it) }, // Crear Apellido solo si apellido2 no es null
            correo = Correo(input.correo),
            rol = Rol(input.rol)
        )
        return appRunCatching {
            usuarioRepository.createUser(usuario)
        }
    }

    private fun CrearUsuariInput.toUser() = Usuario(
        nombre = Nombre(this.nombre),
        apellido1 = Apellido(this.apellido1),
        apellido2 = this.apellido2?.let { Apellido(it) }, // Crear Apellido solo si apellido2 no es null
        correo = Correo(this.correo),
        rol = Rol(this.rol)
    )
}
