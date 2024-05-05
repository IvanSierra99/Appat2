package com.example.appat.domain.usecases

interface IniciarSesionMonitorUseCase {
    fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        // Implementar la lógica para verificar las credenciales del monitor
        // En caso de éxito, autenticar al monitor y conceder acceso a las funcionalidades
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class IniciarSesionMonitorUseCaseImpl(

): IniciarSesionMonitorUseCase {

    override fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        //super.iniciarSesion(nombreUsuario, contrasena)
    }
}