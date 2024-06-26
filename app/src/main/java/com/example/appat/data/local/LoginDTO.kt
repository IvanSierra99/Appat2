package com.example.appat.data.local
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("token") val token: String,
    @SerialName("user") val user: UsuarioDTO
)
