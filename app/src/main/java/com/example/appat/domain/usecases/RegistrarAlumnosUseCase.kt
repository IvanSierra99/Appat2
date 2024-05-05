package com.example.appat.domain.usecases

interface RegistrarAlumnosUseCase {

    // Describe la funcionalidad principal del caso de uso
    fun registrarAlumno(nombre: String, apellido1: String, apellido2: String, clase: String,
                        alergias: List<String>, intolerancias: List<String>) {


    }
}
class RegistrarAlumnosUseCaseImpl(

): RegistrarAlumnosUseCase{

    override fun registrarAlumno(nombre: String, apellido1: String, apellido2: String,
        clase: String, alergias: List<String>, intolerancias: List<String>) {
        //super.registrarAlumno(nombre, apellido1, apellido2, clase, alergias, intolerancias)
    }
}