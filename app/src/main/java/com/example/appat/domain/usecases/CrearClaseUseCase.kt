package com.example.appat.domain.usecases

interface CrearClaseUseCase {

    // Describe la funcionalidad principal del caso de uso
    fun crearClase(nombre: String) {
        // Implementar la lógica para crear una nueva clase en el sistema
        // Validar el nombre de la clase
        // Guardar la información de la nueva clase en la base de datos
        // En caso de éxito, notificar al usuario y registrar la acción
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class CrearClaseUseCaseImpl(

): CrearClaseUseCase {

    override fun crearClase(nombre: String) {
        //super.crearClase(nombre)
    }
}