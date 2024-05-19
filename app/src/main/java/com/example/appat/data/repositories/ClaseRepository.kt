package com.example.appat.data.repositories

import com.example.appat.data.local.AlumnoDTO
import com.example.appat.data.local.ApiService
import com.example.appat.data.local.ClaseDTO
import com.example.appat.domain.entities.Clase
import com.example.appat.domain.entities.Alumno

interface ClaseRepository {
    suspend fun createClase(clase: Clase, token: String?): Clase
    suspend fun getClaseById(claseId: String, token: String?): Clase
    suspend fun updateClase(clase: Clase, token: String?): Clase
    suspend fun getClasesByCentroEscolar(centroEscolarId: String, token: String?): List<Clase>

}

class ClaseRepositoryImpl(private val apiService: ApiService) : ClaseRepository {
    override suspend fun createClase(clase: Clase, token: String?): Clase {
        val claseDTO = ClaseMapper.toDTO(clase)
        val createdClaseDTO = apiService.createClase(claseDTO, token)
        return ClaseMapper.toDomain(createdClaseDTO)
    }

    override suspend fun getClaseById(claseId: String, token: String?): Clase {
        val claseDTO = apiService.getClaseById(claseId, token)
        return ClaseMapper.toDomain(claseDTO)
    }

    override suspend fun getClasesByCentroEscolar(centroEscolarId: String, token: String?): List<Clase> {
        val clasesDTO = apiService.getClasesByCentroEscolar(centroEscolarId, token)
        return clasesDTO.map { ClaseMapper.toDomain(it) }
    }

    override suspend fun updateClase(clase: Clase, token: String?): Clase {
        val claseDTO = ClaseMapper.toDTO(clase)
        val updatedClaseDTO = apiService.updateClase(claseDTO, token)
        return ClaseMapper.toDomain(updatedClaseDTO)
    }

    object ClaseMapper {
        fun toDTO(clase: Clase): ClaseDTO {
            return ClaseDTO(

                claseId = clase.claseId,
                nombre = clase.nombre,
                cursoId = clase.cursoId,
                alumnos = clase.alumnos.map { AlumnoRepositoryImpl.AlumnoMapper.toDTO(it) }
            )
        }

        fun toDomain(claseDTO: ClaseDTO): Clase {
            return Clase(
                claseId = claseDTO.claseId,
                nombre = claseDTO.nombre,
                cursoId = claseDTO.cursoId ?: "",
                alumnos = claseDTO.alumnos.map { AlumnoRepositoryImpl.AlumnoMapper.toDomain(it) }.toMutableList()
            )
        }
    }
}
