package com.example.appat.data.local

data class UsuarioData(
    val id: String,  // Asegúrate de tener un identificador si lo usas en las API calls
    val nombre: String,
    val apellido1: String,
    val apellido2: String,
    val username: String,
    val correo: String,
    val rol: String
)
