package com.example.appat.domain.usecases

interface ConsultarMenuEscolarUseCase {

    fun consultarMenuEscolar(dia: String) {
        // Implementar la lógica para consultar el menú escolar del día actual o el día especificado
        // Recuperar el menú escolar del día indicado de la base de datos o servicio web
        // Devolver el menú al usuario
    }
}
class ConsultarMenuEscolaUseCaseImpl(

): ConsultarMenuEscolarUseCase {

    override fun consultarMenuEscolar(dia: String) {
        //super.consultarMenuEscolar(dia)
    }
}