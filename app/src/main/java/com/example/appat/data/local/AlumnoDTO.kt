package com.example.appat.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlergiaDTO(
    @SerialName("nombre") val nombre: String,
    @SerialName("severidad") val severidad: String
)

@Serializable
data class AlumnoDTO(
    @SerialName("alumnoId") val alumnoId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("apellido") val apellido: String,
    @SerialName("clase_id") val claseId: String,
    @SerialName("alergias") val alergias: List<AlergiaDTO> = emptyList(),
    @SerialName("dias_habituales") val diasHabituales: List<String> = emptyList()
)
