package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.local.UsuarioData
import com.example.appat.domain.entities.*
import com.example.appat.data.repositories.UsuarioRepository

interface CrearUsuarioUseCase: UseCaseSuspend<CrearUsuariInput, AppResult<Usuario, Throwable>>

data class CrearUsuariInput(
    val nombre: String,
    val apellido1: String,
    val apellido2: String,
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
        val usuarioData = UsuarioData(
            id = "",  // Asumiendo que el ID es generado por el servidor o no necesario en la creación
            nombre = input.nombre,
            apellido1 = input.apellido1,
            apellido2 = input.apellido2,
            username = "",  // Asumir que el username se genera o se obtiene de otra manera
            correo = input.correo,
            rol = input.rol
        )
        return appRunCatching {
            usuarioRepository.createUser(usuarioData)
        }
    }

    private fun CrearUsuariInput.toUser() = Usuario(
        nombre = Nombre(this.nombre),
        apellido1 = Apellido(this.apellido1),
        apellido2 = Apellido(this.apellido2),
        username = Username(),
        correo = Correo(this.correo),
        //contraseña = "kawodkof",
        rol = Rol(this.rol)
    )
}
