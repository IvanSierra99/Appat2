package com.example.appat.domain.usecases

import com.example.appat.domain.entities.Alergia

interface ModificarInformacionAlumnoUseCase {

    fun modificarInformacionAlumno(idAlumno: String, nombre: String, apellido1: String,
                                   apellido2: String, clase: String, alergias: List<Alergia>,
                                   intolerancias: List<Alergia>) {
        // Implementar la lógica para modificar la información de un alumno
        // Buscar al alumno por su ID
        // Actualizar la información del alumno en la base de datos
        // En caso de éxito, notificar al usuario y registrar la acción
        // En caso de fallo, mostrar mensajes de error correspondientes
    }
}
class ModificarInformacionAlumnoUseCaseImpl(

): ModificarInformacionAlumnoUseCase {

    override fun modificarInformacionAlumno(idAlumno: String, nombre: String, apellido1: String,
        apellido2: String, clase: String, alergias: List<Alergia>, intolerancias: List<Alergia>) {

    }
}