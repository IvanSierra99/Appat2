package com.example.appat.domain.entities

import java.util.Date
import java.util.UUID

data class Asistencia(
    val asistenciaId: String = UUID.randomUUID().toString(),
    val fecha: String,
    val centroEscolarId: String,
    val habitualIds: List<String> = listOf(),
    val noHabitualIds: List<String> = listOf()
) {

}