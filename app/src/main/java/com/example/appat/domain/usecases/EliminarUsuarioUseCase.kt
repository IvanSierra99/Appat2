package com.example.appat.domain.usecases

interface EliminarUsuarioUseCase {

    fun eliminarUsuario(idUsuario: String) {
        // Implementar la lógica para eliminar un usuario del sistema
        // Buscar al usuario por su ID
        // Eliminar al usuario de la base de datos o servicio de autenticación
        // En caso de éxito, notificar al usuario y registrar la acción
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class EliminarUsuarioUseCaseImpl(

): EliminarUsuarioUseCase {

    override fun eliminarUsuario(idUsuario: String) {
        //super.eliminarUsuario(idUsuario)
    }
}