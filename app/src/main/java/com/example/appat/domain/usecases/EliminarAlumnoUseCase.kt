package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.AlumnoRepository

data class EliminarAlumnoInput(val alumnoId: String, val token: String?)

interface EliminarAlumnoUseCase : UseCaseSuspend<EliminarAlumnoInput, AppResult<Unit, Throwable>>

class EliminarAlumnoUseCaseImpl(
    private val alumnoRepository: AlumnoRepository
) : EliminarAlumnoUseCase {
    override suspend fun invoke(params: EliminarAlumnoInput): AppResult<Unit, Throwable> {
        return appRunCatching {
            alumnoRepository.deleteAlumno(params.alumnoId, params.token)
        }
    }
}
