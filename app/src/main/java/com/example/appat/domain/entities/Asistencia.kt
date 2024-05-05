package com.example.appat.domain.entities

import java.util.Date
import java.util.UUID

data class Asistencia(
    val fecha: Date,
    val alumnoId: String,
    val presente: Boolean,
    val habitual: Boolean,
) {

}