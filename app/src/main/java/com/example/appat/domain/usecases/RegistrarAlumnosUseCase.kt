package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.AlumnoRepository
import com.example.appat.data.repositories.ClaseRepository
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno
import com.example.appat.domain.entities.Curso

data class RegistrarAlumnoInput(
    val nombre: String,
    val apellido: String,
    val claseId: String,
    val alergias: List<Alergia>,
    val diasHabituales: List<String>,
    val token: String?
)

interface RegistrarAlumnoUseCase : UseCaseSuspend<RegistrarAlumnoInput, AppResult<Alumno, Throwable>> {
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable>
    suspend fun getAlergias(token: String?): AppResult<List<Alergia>, Throwable>
}

class RegistrarAlumnoUseCaseImpl(
    private val alumnoRepository: AlumnoRepository,
    private val cursoRepository: CursoRepository,
    private val claseRepository: ClaseRepository
) : RegistrarAlumnoUseCase {
    override suspend fun invoke(params: RegistrarAlumnoInput): AppResult<Alumno, Throwable> {
        val alumno = Alumno(
            nombre = params.nombre,
            apellido = params.apellido,
            claseId = params.claseId,
            alergias = params.alergias,
            diasHabituales = params.diasHabituales
        )

        return appRunCatching {
            // Create the student
            val createdAlumno = alumnoRepository.createAlumno(alumno, params.token)

            // Fetch the class, update its list of students, and save the updated class
            val clase = claseRepository.getClaseById(params.claseId, params.token)
            val updatedClase = clase.copy(alumnos = clase.alumnos.toMutableList().apply { add(createdAlumno) })
            claseRepository.updateClase(updatedClase, params.token)

            createdAlumno
        }
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable> {
        return appRunCatching {
            cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
        }
    }

    override suspend fun getAlergias(token: String?): AppResult<List<Alergia>, Throwable> {
        return appRunCatching {
            alumnoRepository.getAlergias(token)
        }
    }
}
