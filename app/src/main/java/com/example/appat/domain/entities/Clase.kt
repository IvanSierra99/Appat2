package com.example.appat.domain.entities

import java.util.UUID

//Todo: lista Alumnos
data class Clase(
    val nombre: String,
    val claseId: String = UUID.randomUUID().toString()
) {


}