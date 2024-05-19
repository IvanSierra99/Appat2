package com.example.appat.data.repositories

import com.example.appat.data.local.AlergiaDTO
import com.example.appat.data.local.AlumnoDTO
import com.example.appat.data.local.ApiService
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno

interface AlumnoRepository {
    suspend fun createAlumno(alumno: Alumno, token: String?): Alumno
    suspend fun getAlergias(token: String?): List<Alergia>
    suspend fun getAlumnoById(alumnoId: String, token: String?): Alumno
    suspend fun updateAlumno(alumno: Alumno, token: String?): Alumno
    suspend fun deleteAlumno(alumnoId: String, token: String?)
}

class AlumnoRepositoryImpl(private val apiService: ApiService) : AlumnoRepository {
    override suspend fun createAlumno(alumno: Alumno, token: String?): Alumno {
        val alumnoDTO = AlumnoMapper.toDTO(alumno)
        val createdAlumnoDTO = apiService.createAlumno(alumnoDTO, token)
        return AlumnoMapper.toDomain(createdAlumnoDTO)
    }

    override suspend fun getAlergias(token: String?): List<Alergia> {
        val alergiasDTO = apiService.getAlergias(token)
        return alergiasDTO.map { AlergiaMapper.toDomain(it) }
    }

    override suspend fun getAlumnoById(alumnoId: String, token: String?): Alumno {
        val alumnoDTO = apiService.getAlumnoById(alumnoId, token)
        return AlumnoMapper.toDomain(alumnoDTO)
    }

    override suspend fun updateAlumno(alumno: Alumno, token: String?): Alumno {
        val alumnoDTO = AlumnoMapper.toDTO(alumno)
        val updatedAlumnoDTO = apiService.updateAlumno(alumnoDTO, token)
        return AlumnoMapper.toDomain(updatedAlumnoDTO)
    }

    override suspend fun deleteAlumno(alumnoId: String, token: String?) {
        apiService.deleteAlumno(alumnoId, token)
    }

    object AlumnoMapper {
        fun toDTO(alumno: Alumno): AlumnoDTO {
            return AlumnoDTO(
                alumnoId = alumno.alumnoId,
                nombre = alumno.nombre,
                apellido = alumno.apellido,
                claseId = alumno.claseId,
                alergias = alumno.alergias.map { AlergiaMapper.toDTO(it) }
            )
        }

        fun toDomain(alumnoDTO: AlumnoDTO): Alumno {
            return Alumno(
                alumnoId = alumnoDTO.alumnoId,
                nombre = alumnoDTO.nombre,
                apellido = alumnoDTO.apellido,
                claseId = alumnoDTO.claseId,
                alergias = alumnoDTO.alergias.map { AlergiaMapper.toDomain(it) }
            )
        }
    }

    object AlergiaMapper {
        fun toDTO(alergia: Alergia): AlergiaDTO {
            return AlergiaDTO(
                nombre = alergia.nombre,
                severidad = alergia.severidad
            )
        }

        fun toDomain(alergiaDTO: AlergiaDTO): Alergia {
            return Alergia(
                nombre = alergiaDTO.nombre,
                severidad = alergiaDTO.severidad
            )
        }
    }

}
