package com.example.appat.domain.entities

import java.time.LocalDate
import java.util.UUID
//Todo: alergias, intolerancias, diasHabituales, listaAsistencia
data class Alumno(
    val alumnoId: String = UUID.randomUUID().toString(),
    val nombre: String,
    val apellido: String,
    val claseId: String,
    val alergias: List<Alergia> = emptyList()
) {

}