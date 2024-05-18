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
        val cursoDTO = curso.toDTO()
        val createdCursoDTO = apiService.createCurso(cursoDTO, token)
        return createdCursoDTO.toDomain()
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): List<Curso> {
        val cursoDTOs = apiService.getCursosByCentroEscolar(centroEscolarId, token)
        return cursoDTOs.map { it.toDomain() }
    }

    override suspend fun getCursoById(cursoId: String, token: String?): Curso {
        val cursoDTO = apiService.getCursoById(cursoId, token)
        return cursoDTO.toDomain()
    }

    override suspend fun updateCurso(curso: Curso, token: String?): Curso {
        val cursoDTO = CursoDTO(
            cursoId = curso.cursoId,
            nombre = curso.nombre,
            etapa = curso.etapa,
            centroEscolarId = curso.centroEscolarId,
            clases = curso.clases?.map { ClaseDTO(claseId = it.claseId, nombre = it.nombre, cursoId = it.cursoId) } ?: emptyList()
        )
        val updatedCursoDTO = apiService.updateCurso(cursoDTO, token)
        return updatedCursoDTO.toDomain()
    }
    private fun Curso.toDTO(): CursoDTO {
        return CursoDTO(
            cursoId = this.cursoId,
            nombre = this.nombre,
            etapa = this.etapa,
            centroEscolarId =this.centroEscolarId,
            clases = this.clases?.map { ClaseDTO(claseId = it.claseId, nombre = it.nombre, cursoId = it.cursoId) } ?: emptyList()
        )
    }

    private fun CursoDTO.toDomain(): Curso {
        return Curso(
            cursoId = this.cursoId,
            nombre = this.nombre,
            etapa = this.etapa,
            centroEscolarId = this.centroEscolarId,
            clases = this.clases?.map { Clase(claseId = it.claseId, nombre = it.nombre, cursoId = this.cursoId) } ?: emptyList()
        )
    }
}
