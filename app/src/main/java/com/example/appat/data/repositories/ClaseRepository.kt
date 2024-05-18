package com.example.appat.data.repositories

import com.example.appat.data.local.ApiService
import com.example.appat.data.local.ClaseDTO
import com.example.appat.domain.entities.Clase

interface ClaseRepository {
    suspend fun createClase(clase: Clase, token: String?): Clase
}

class ClaseRepositoryImpl(private val apiService: ApiService) : ClaseRepository {
    override suspend fun createClase(clase: Clase, token: String?): Clase {
        val claseDTO = clase.toDTO()
        val createdClaseDTO = apiService.createClase(claseDTO, token)
        return createdClaseDTO.toDomain()
    }

    private fun Clase.toDTO(): ClaseDTO {
        return ClaseDTO(
            claseId = this.claseId,
            nombre = this.nombre,
            cursoId = this.cursoId
        )
    }

    private fun ClaseDTO.toDomain(): Clase {
        return Clase(
            claseId = this.claseId,
            nombre = this.nombre,
            cursoId = this.cursoId
        )
    }
}
