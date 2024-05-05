package com.example.appat.domain.usecases

interface IniciarSesionUseCase {

    fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        // Implementar la lógica para verificar las credenciales del usuario
        // En caso de éxito, autenticar al usuario y conceder acceso a las funcionalidades
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class IniciarSesionUseCaseImpl(
    //private val usuarioRepository: UsuarioRepository
) : IniciarSesionUseCase {

    override fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        // Validar los datos de entrada (nombreUsuario, contrasena)
        // Buscar al usuario en el repositorio
        /*val usuario = usuarioRepository.buscarUsuario(nombreUsuario)
        if (usuario != null) {
            // Verificar la contraseña
            if (usuario.contrasena == contrasena) {
                // Iniciar sesión del usuario
                // Registrar la acción de inicio de sesión
                // Notificar al usuario el éxito del inicio de sesión
            } else {
                // Contraseña incorrecta
                throw ContrasenaIncorrectaException()
            }
        } else {
            // Usuario no encontrado
            throw UsuarioNoEncontradoException()
        }*/
    }
}