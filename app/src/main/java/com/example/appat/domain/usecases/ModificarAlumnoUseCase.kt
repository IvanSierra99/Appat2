package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.AlumnoRepository
import com.example.appat.domain.entities.Curso
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno

data class ModificarAlumnoInput(
    val alumnoId: String,
    val nombre: String,
    val apellido: String,
    val claseId: String,
    val alergias: List<Alergia>,
    val diasHabituales: List<String>,
    val token: String?
)
interface ModificarAlumnoUseCase :
    UseCaseSuspend<ModificarAlumnoInput, AppResult<Alumno, Throwable>> {
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso>
    suspend fun obtenerAlumnoPorId(alumnoId: String, token: String?): AppResult<Alumno, Throwable>

}

class ModificarAlumnoUseCaseImpl(
    private val cursoRepository: CursoRepository,
    private val alumnoRepository: AlumnoRepository
): ModificarAlumnoUseCase {

    override suspend fun invoke(params: ModificarAlumnoInput): AppResult<Alumno, Throwable> {
        return appRunCatching {
            val alumno = alumnoRepository.getAlumnoById(params.alumnoId, params.token)
            val alumnoModificado = alumno.copy(
                nombre = params.nombre,
                apellido = params.apellido,
                claseId = params.claseId,
                alergias = params.alergias,
                diasHabituales = params.diasHabituales
            )
            alumnoRepository.updateAlumno(alumnoModificado, params.token)
            alumnoModificado
        }
    }

    override suspend fun obtenerAlumnoPorId(alumnoId: String, token: String?): AppResult<Alumno, Throwable> {
        return appRunCatching {
            alumnoRepository.getAlumnoById(alumnoId, token)
        }
    }
    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso> {
        return cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
    }
}
