package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.AsistenciaRepository
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.Asistencia
import com.example.appat.domain.entities.Curso
import java.time.LocalDate

data class RegistrarAsistenciaInput(
    val asistencia: Asistencia,
    val token: String?
)

interface RegistrarAsistenciaAlumnoUseCase : UseCaseSuspend<RegistrarAsistenciaInput, AppResult<Asistencia, Throwable>> {
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso>
    suspend fun registrarAsistencia(asistencia: Asistencia, token: String?): Asistencia
    suspend fun getAsistenciaByDateAndCentro(fecha: LocalDate, centroEscolarId: String, token: String?): Asistencia
}

class RegistrarAsistenciaAlumnoUseCaseImpl(
    private val cursoRepository: CursoRepository,
    private val asistenciaRepository: AsistenciaRepository
): RegistrarAsistenciaAlumnoUseCase {

    override suspend fun invoke(params: RegistrarAsistenciaInput): AppResult<Asistencia, Throwable> {
        return appRunCatching {
            val asistencia = asistenciaRepository.updateAsistencia(params.asistencia, params.token)
            asistencia
        }
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso> {
        return cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
    }

    override suspend fun registrarAsistencia(asistencia: Asistencia, token: String?): Asistencia {
        return asistenciaRepository.updateAsistencia(asistencia, token)
    }

    override suspend fun getAsistenciaByDateAndCentro(fecha: LocalDate, centroEscolarId: String, token: String?): Asistencia {
        return asistenciaRepository.getAsistenciaByDateAndCentro(fecha, centroEscolarId, token)
    }
}
