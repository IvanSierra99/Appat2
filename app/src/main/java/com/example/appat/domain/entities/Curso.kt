package com.example.appat.domain.entities

import com.example.appat.domain.entities.Clase
import java.util.UUID

//Todo: si es necesario
data class Curso(
    val cursoId: String = UUID.randomUUID().toString(),
    val nombre: String,
    val ciclo: String,
    val clases: List<Clase>
) {

}