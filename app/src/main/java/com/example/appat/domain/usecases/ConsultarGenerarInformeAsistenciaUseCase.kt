package com.example.appat.domain.usecases

import com.example.appat.data.repositories.AsistenciaRepository
import com.example.appat.data.repositories.CursoRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

class DateProgression(override val start: LocalDate, override val endInclusive: LocalDate) : Iterable<LocalDate>, ClosedRange<LocalDate> {
    override fun iterator(): Iterator<LocalDate> = DateIterator(start, endInclusive)
}

class DateIterator(start: LocalDate, private val endInclusive: LocalDate) : Iterator<LocalDate> {
    private var current = start

    override fun hasNext(): Boolean = current <= endInclusive

    override fun next(): LocalDate {
        val next = current
        current = current.plusDays(1)
        return next
    }
}

data class DatosInformeInput(
    val fecha: String,
    val total: Int,
    val habituales: Int,
    val noHabituales: Int,
    val etapas: Map<String, Int>,
    val alumnosNoHabituales: List<AlumnoNoHabitual>
)

data class AlumnoNoHabitual(
    val apellido: String,
    val nombre: String,
    var dias: List<String> = emptyList()
)

interface ConsultarGenerarInformeAsistenciaUseCase {
    suspend fun consultarGenerarInformeAsistencia(fecha: LocalDate, centroEscolarId: String, token: String?): DatosInformeInput
    suspend fun consultarGenerarInformeMensual(centroEscolarId: String, token: String?): DatosInformeInput
}

class ConsultarGenerarInformeAsistenciaUseCaseImpl(
    private val asistenciaRepository: AsistenciaRepository,
    private val cursoRepository: CursoRepository
) : ConsultarGenerarInformeAsistenciaUseCase {

    private val etapaMapping = mapOf(
        "INFANTIL" to "Infantil",
        "PRIMARIA" to "Primaria",
        "ESO" to "ESO",
        "BACHILLERATO" to "Bachillerato",
        "CICLO_INICIAL" to "Ciclo Inicial",
        "CICLO_MEDIO" to "Ciclo Medio",
        "CICLO_SUPERIOR" to "Ciclo Superior"
    )

    override suspend fun consultarGenerarInformeAsistencia(fecha: LocalDate, centroEscolarId: String, token: String?): DatosInformeInput {
        val asistencia = asistenciaRepository.getAsistenciaByDateAndCentro(fecha, centroEscolarId, token)
        val cursos = cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)

        val total = asistencia.habitualIds.size + asistencia.noHabitualIds.size
        val habituales = asistencia.habitualIds.size
        val noHabituales = asistencia.noHabitualIds.size

        val etapas = mutableMapOf<String, Int>()
        val alumnosNoHabituales = mutableMapOf<String, AlumnoNoHabitual>()
        val date = fecha

        cursos.forEach { curso ->
            curso.clases?.forEach { clase ->
                clase.alumnos.forEach { alumno ->
                    val etapa = etapaMapping[curso.etapa] ?: curso.etapa

                    if (alumno.alumnoId in asistencia.noHabitualIds) {
                        val alumnoNoHabitual = alumnosNoHabituales.getOrPut(alumno.alumnoId) {
                            AlumnoNoHabitual(alumno.apellido, alumno.nombre)
                        }
                        alumnoNoHabitual.dias += date.toString()
                        etapas[etapa] = etapas.getOrDefault(etapa, 0) + 1
                    }
                    if (alumno.alumnoId in asistencia.habitualIds) {
                        etapas[etapa] = etapas.getOrDefault(etapa, 0) + 1
                    }
                }
            }
        }

        return DatosInformeInput(
            fecha = fecha.format(DateTimeFormatter.ISO_DATE),
            total = total,
            habituales = habituales,
            noHabituales = noHabituales,
            etapas = etapas,
            alumnosNoHabituales = alumnosNoHabituales.values.toList()
        )
    }

    override suspend fun consultarGenerarInformeMensual(centroEscolarId: String, token: String?): DatosInformeInput {
        val currentMonth = LocalDate.now().withDayOfMonth(1)
        val endOfMonth = currentMonth.plusMonths(1).minusDays(1)

        val cursos = cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
        val etapas = mutableMapOf<String, Int>()
        val alumnosNoHabituales = mutableMapOf<String, AlumnoNoHabitual>()

        var total = 0
        var habituales = 0
        var noHabituales = 0

        for (date in currentMonth..endOfMonth) {
            try {
                val asistencia = asistenciaRepository.getAsistenciaByDateAndCentro(date, centroEscolarId, token)
                total += asistencia.habitualIds.size + asistencia.noHabitualIds.size
                habituales += asistencia.habitualIds.size
                noHabituales += asistencia.noHabitualIds.size

                cursos.forEach { curso ->
                    curso.clases?.forEach { clase ->
                        clase.alumnos.forEach { alumno ->
                            val etapa = etapaMapping[curso.etapa] ?: curso.etapa
                            if (alumno.alumnoId in asistencia.noHabitualIds) {
                                val alumnoNoHabitual = alumnosNoHabituales.getOrPut(alumno.alumnoId) {
                                    AlumnoNoHabitual(alumno.apellido, alumno.nombre)
                                }
                                alumnoNoHabitual.dias += date.dayOfMonth.toString()
                                etapas[etapa] = etapas.getOrDefault(etapa, 0) + 1
                            }
                            if (alumno.alumnoId in asistencia.habitualIds) {
                                etapas[etapa] = etapas.getOrDefault(etapa, 0) + 1
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions such as no data for a specific date
            }
        }

        return DatosInformeInput(
            fecha = "${currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))}",
            total = total,
            habituales = habituales,
            noHabituales = noHabituales,
            etapas = etapas,
            alumnosNoHabituales = alumnosNoHabituales.values.toList()
        )
    }
}
