package com.example.appat.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CentroEscolarDTO(
    @SerialName("centroId") val centroId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("direccion") val direccion: String,
    @SerialName("telefono") val telefono: String,
    @SerialName("correo") val correo: String
)