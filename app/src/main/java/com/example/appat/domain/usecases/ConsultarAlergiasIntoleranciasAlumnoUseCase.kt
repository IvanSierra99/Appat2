package com.example.appat.domain.usecases

interface ConsultarAlergiasIntoleranciasAlumnoUseCase {

    fun consultarAlergiasIntolerancias(idAlumno: String) {
        // Implementar la lógica para consultar las alergias e intolerancias de un alumno
        // Buscar al alumno por su ID
        // Recuperar la información de alergias e intolerancias del alumno de la base de datos
        // Devolver la información al usuario
    }
}
class ConsultarAlergiasIntoleranciasAlumnoUseCaseImpl(

): ConsultarAlergiasIntoleranciasAlumnoUseCase {

    override fun consultarAlergiasIntolerancias(idAlumno: String) {
        //super.consultarAlergiasIntolerancias(idAlumno)
    }
}