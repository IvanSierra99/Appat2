package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.ModificarAlumnoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlumnoManagementViewModel(
    private val modificarInformacionAlumnoUseCase: ModificarAlumnoUseCase
) : ViewModel() {

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> get() = _cursos

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _sortOrder = MutableStateFlow("Apellido (A-Z)")
    val sortOrder: StateFlow<String> get() = _sortOrder

    private val _alergiaFilter = MutableStateFlow<String?>(null)
    val alergiaFilter: StateFlow<String?> get() = _alergiaFilter

    fun obtenerCursosPorCentro(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            try {
                val cursos = modificarInformacionAlumnoUseCase.getCursosByCentroEscolar(centroEscolarId, token)
                _cursos.value = cursos
            } catch (e: Exception) {
                e.printStackTrace() // Imprime la traza de la excepción para depuración
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOrder(order: String) {
        _sortOrder.value = order
    }

    fun filterByAlergia(alergia: String) {
        _alergiaFilter.value = alergia
    }

    fun clearAlergiaFilter() {
        _alergiaFilter.value = null
    }

    val filteredCursos: StateFlow<List<Curso>> = combine(_cursos, _searchQuery, _sortOrder, _alergiaFilter) { cursos, query, order, alergia ->
        val filtered = cursos.map { curso ->
            curso.copy(
                clases = curso.clases?.map { clase ->
                    clase.copy(
                        alumnos = clase.alumnos.filter { alumno ->
                            (alergia == null || alumno.alergias.any { it.severidad == alergia }) &&
                                    (query.isBlank() || alumno.nombre.contains(query, ignoreCase = true) || alumno.apellido.contains(query, ignoreCase = true))
                        }.toMutableList()
                    )
                }?.filter { it.alumnos.isNotEmpty() }?.toMutableList()
            )
        }.filter { it.clases?.isNotEmpty() == true }

        val sorted = when (order) {
            "Apellido (A-Z)" -> filtered.map { curso ->
                curso.copy(
                    clases = curso.clases?.map { clase ->
                        clase.copy(
                            alumnos = clase.alumnos.sortedBy { it.apellido }.toMutableList()
                        )
                    }?.toMutableList()
                )
            }
            "Apellido (Z-A)" -> filtered.map { curso ->
                curso.copy(
                    clases = curso.clases?.map { clase ->
                        clase.copy(
                            alumnos = clase.alumnos.sortedByDescending { it.apellido }.toMutableList()
                        )
                    }?.toMutableList()
                )
            }
            "Nombre (A-Z)" -> filtered.map { curso ->
                curso.copy(
                    clases = curso.clases?.map { clase ->
                        clase.copy(
                            alumnos = clase.alumnos.sortedBy { it.nombre }.toMutableList()
                        )
                    }?.toMutableList()
                )
            }
            "Nombre (Z-A)" -> filtered.map { curso ->
                curso.copy(
                    clases = curso.clases?.map { clase ->
                        clase.copy(
                            alumnos = clase.alumnos.sortedByDescending { it.nombre }.toMutableList()
                        )
                    }?.toMutableList()
                )
            }
            else -> filtered
        }

        sorted
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
