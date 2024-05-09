package com.example.appat.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UsuarioDTO(
    @SerialName("userId") val userId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("apellido1") val apellido1: String,
    @SerialName("apellido2") val apellido2: String? = null,
    @SerialName("username") val username: String,
    @SerialName("correo") val correo: String,
    @SerialName("contraseña") val contraseña: String,
    @SerialName("rol") val rol: String
)
