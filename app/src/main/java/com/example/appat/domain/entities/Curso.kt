package com.example.appat.domain.entities

import java.util.UUID

data class Curso(
    val cursoId: String = UUID.randomUUID().toString(),
    val nombre: String,
    val etapa: String,
    val centroEscolarId: String?,
    val clases: List<Clase> = listOf()
)
