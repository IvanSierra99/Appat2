package com.example.appat.domain.entities

import java.util.UUID

//Todo: lista Alumnos
data class Clase(
    val claseId: String = UUID.randomUUID().toString(),
    val nombre: String,
    val cursoId: String?,
    val alumnos: MutableList<Alumno> = mutableListOf()
) {


}