package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.Curso

data class CrearCursoInput(
    val nombre: String,
    val etapa: String,
    val centroEscolarId: String,
    val token: String?
)
interface CrearCursoUseCase: UseCaseSuspend<CrearCursoInput, AppResult<Curso, Throwable>>

class CrearCursoUseCaseImpl(private val cursoRepository: CursoRepository) : CrearCursoUseCase {
    override suspend fun invoke(params: CrearCursoInput): AppResult<Curso, Throwable> {
        val curso = Curso(
            nombre = params.nombre,
            etapa = params.etapa,
            centroEscolarId = params.centroEscolarId  // Asignar el centroEscolarId desde el input
        )
        return appRunCatching { cursoRepository.createCurso(curso, params.token) }
    }

}
