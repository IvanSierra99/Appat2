package com.example.appat.domain.entities

data class CentroEscolar(
    val centroId: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String,
    val administradorId: String? = null
)