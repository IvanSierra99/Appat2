package com.example.appat.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDTO(
    @SerialName("userId") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String? = null,
    @SerialName("rol") val rol: String,
    @SerialName("centro_escolar") val centroEscolar: CentroEscolarDTO? = null,
    @SerialName("centro_escolar_id") val centroEscolarId: String? = null
)
