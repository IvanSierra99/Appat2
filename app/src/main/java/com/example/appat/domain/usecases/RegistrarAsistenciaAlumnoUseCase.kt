package com.example.appat.domain.usecases

interface RegistrarAsistenciaAlumnoUseCase {

    fun registrarAsistencia(idClase: String, alumnosPresentes: List<String>) {
        // Implementar la lógica para registrar la asistencia de los alumnos
        // Buscar la clase por su ID
        // Actualizar la información de asistencia de los alumnos en la base de datos
        // En caso de éxito, notificar al usuario y registrar la acción
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class RegistrarAsistenciaAlumnoUseCaseImpl(

): RegistrarAsistenciaAlumnoUseCase {

    override fun registrarAsistencia(idClase: String, alumnosPresentes: List<String>) {
        //super.registrarAsistencia(idClase, alumnosPresentes)
    }
}