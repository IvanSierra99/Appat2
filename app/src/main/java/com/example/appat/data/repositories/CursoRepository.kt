package com.example.appat.data.repositories

import com.example.appat.data.local.ApiService
import com.example.appat.data.local.ClaseDTO
import com.example.appat.data.local.CursoDTO
import com.example.appat.domain.entities.Clase
import com.example.appat.domain.entities.Curso

interface CursoRepository {
    suspend fun createCurso(curso: Curso, token: String?): Curso
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso>
    suspend fun getCursoById(cursoId: String, token: String?): Curso
    suspend fun updateCurso(curso: Curso, token: String?): Curso
}

class CursoRepositoryImpl(private val apiService: ApiService) : CursoRepository {
    override suspend fun createCurso(curso: Curso, token: String?): Curso {
        val cursoDTO = CursoMapper.toDTO(curso)
        val createdCursoDTO = apiService.createCurso(cursoDTO, token)
        return CursoMapper.toDomain(createdCursoDTO)
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso> {
        val cursoDTOs = apiService.getCursosByCentroEscolar(centroEscolarId, token)
        return cursoDTOs.map { CursoMapper.toDomain(it) }
    }

    override suspend fun getCursoById(cursoId: String, token: String?): Curso {
        val cursoDTO = apiService.getCursoById(cursoId, token)
        return CursoMapper.toDomain(cursoDTO)
    }

    override suspend fun updateCurso(curso: Curso, token: String?): Curso {
        val cursoDTO = CursoMapper.toDTO(curso)
        val updatedCursoDTO = apiService.updateCurso(cursoDTO, token)
        return CursoMapper.toDomain(updatedCursoDTO)
    }
    object CursoMapper {
        fun toDTO(curso: Curso): CursoDTO {
            return CursoDTO(
                cursoId = curso.cursoId,
                nombre = curso.nombre,
                etapa = curso.etapa,
                centroEscolarId = curso.centroEscolarId,
                clases = curso.clases?.map { ClaseRepositoryImpl.ClaseMapper.toDTO(it) } ?: emptyList()
            )
        }

        fun toDomain(cursoDTO: CursoDTO): Curso {
            return Curso(
                cursoId = cursoDTO.cursoId,
                nombre = cursoDTO.nombre,
                etapa = cursoDTO.etapa,
                centroEscolarId = cursoDTO.centroEscolarId,
                clases = cursoDTO.clases?.map { ClaseRepositoryImpl.ClaseMapper.toDomain(it) }?.toMutableList() ?: mutableListOf()
            )
        }
    }

}
