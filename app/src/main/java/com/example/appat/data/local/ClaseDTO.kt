package com.example.appat.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaseDTO(
    @SerialName("claseId") val claseId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("curso_id") val cursoId: String? = "",
    @SerialName("alumnos") val alumnos: List<AlumnoDTO> = emptyList()
)
