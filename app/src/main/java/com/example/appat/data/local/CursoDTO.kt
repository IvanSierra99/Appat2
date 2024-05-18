package com.example.appat.data.local

import com.example.appat.domain.entities.Clase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CursoDTO(
    @SerialName("cursoId") val cursoId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("etapa") val etapa: String,
    @SerialName("centro_escolar_id") val centroEscolarId: String? = null, // Opcional
    @SerialName("clases") val clases: List<ClaseDTO> = emptyList()
)
