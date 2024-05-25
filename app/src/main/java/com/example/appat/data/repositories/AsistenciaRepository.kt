package com.example.appat.data.repositories

import com.example.appat.data.local.ApiService
import com.example.appat.data.local.AsistenciaDTO
import com.example.appat.domain.entities.Asistencia
import java.time.LocalDate

interface AsistenciaRepository {
    suspend fun getAsistenciaByDateAndCentro(fecha: LocalDate, centroEscolarId: String, token: String?): Asistencia
    suspend fun updateAsistencia(asistencia: Asistencia, token: String?): Asistencia
}

class AsistenciaRepositoryImpl(private val apiService: ApiService) : AsistenciaRepository {
    override suspend fun getAsistenciaByDateAndCentro(fecha: LocalDate, centroEscolarId: String, token: String?): Asistencia {
        val asistenciaDTO = apiService.getAsistenciaByDateAndCentro(fecha.toString(), centroEscolarId, token)
        return AsistenciaMapper.toDomain(asistenciaDTO)
    }


    override suspend fun updateAsistencia(asistencia: Asistencia, token: String?): Asistencia {
        val asistenciaDTO = AsistenciaMapper.toDTO(asistencia)
        val updatedAsistenciaDTO = apiService.updateAsistencia(asistenciaDTO, token)
        return AsistenciaMapper.toDomain(updatedAsistenciaDTO)
    }
}

object AsistenciaMapper {
    fun toDTO(asistencia: Asistencia): AsistenciaDTO {
        return AsistenciaDTO(
            asistenciaId = asistencia.asistenciaId,
            fecha = asistencia.fecha,
            centroEscolarId = asistencia.centroEscolarId,
            habitualIds = asistencia.habitualIds,
            noHabitualIds = asistencia.noHabitualIds
        )
    }

    fun toDomain(asistenciaDTO: AsistenciaDTO): Asistencia {
        return Asistencia(
            asistenciaId = asistenciaDTO.asistenciaId,
            fecha = asistenciaDTO.fecha,
            centroEscolarId = asistenciaDTO.centroEscolarId,
            habitualIds = asistenciaDTO.habitualIds,
            noHabitualIds = asistenciaDTO.noHabitualIds
        )
    }
}
