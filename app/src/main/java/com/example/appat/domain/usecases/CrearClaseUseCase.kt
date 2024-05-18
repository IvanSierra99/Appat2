package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.ClaseRepository
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.Clase
import com.example.appat.domain.entities.Curso

data class CrearClaseInput(val nombre: String, val cursoId: String, val token: String?)

interface CrearClaseUseCase : UseCaseSuspend<CrearClaseInput, AppResult<Clase, Throwable>> {
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable>
}
class CrearClaseUseCaseImpl(
    private val claseRepository: ClaseRepository,
    private val cursoRepository: CursoRepository
) : CrearClaseUseCase {
    override suspend fun invoke(input: CrearClaseInput): AppResult<Clase, Throwable> {
        val clase = Clase(
            nombre = input.nombre,
            cursoId = input.cursoId
        )

        return appRunCatching {
            // Create the class
            val createdClase = claseRepository.createClase(clase, input.token)

            // Fetch the course, update its list of classes, and save the updated course
            val curso = cursoRepository.getCursoById(input.cursoId, input.token)
            val updatedCurso = curso.copy(clases = curso.clases + createdClase)
            cursoRepository.updateCurso(updatedCurso, input.token)

            createdClase
        }
    }
    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable> {
        return appRunCatching {
            cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
        }
    }
}
