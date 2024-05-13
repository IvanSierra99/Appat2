package com.example.appat.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDTO(
    @SerialName("userId") val userId: String,
    @SerialName("first_name") val firstName: String, // Cambio de nombre de campo
    @SerialName("last_name") val lastName: String, // Cambio de nombre de campo
    @SerialName("username") val username: String,
    @SerialName("email") val correo: String,
    @SerialName("password") val password: String,
    @SerialName("rol") val rol: String
)