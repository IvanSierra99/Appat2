package com.example.appat.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AsistenciaDTO(
    @SerialName("asistenciaId") val asistenciaId: String,
    @SerialName("fecha") val fecha: String,
    @SerialName("centro_escolar") val centroEscolarId: String,
    @SerialName("habitual_ids") val habitualIds: List<String> = listOf(),
    @SerialName("no_habitual_ids") val noHabitualIds: List<String> = listOf()
)
