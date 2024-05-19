package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.ModificarAlumnoInput
import com.example.appat.domain.usecases.ModificarAlumnoUseCase
import com.example.appat.domain.usecases.RegistrarAlumnoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModificarAlumnoViewModel(
    private val modificarAlumnoUseCase: ModificarAlumnoUseCase,
    private val registrarAlumnoUseCase: RegistrarAlumnoUseCase // Para obtener cursos y alergias
) : ViewModel() {

    private val _alumno = MutableStateFlow<Alumno?>(null)
    val alumno: StateFlow<Alumno?> get() = _alumno

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> get() = _cursos

    private val _alergias = MutableStateFlow<List<Alergia>>(emptyList())
    val alergias: StateFlow<List<Alergia>> get() = _alergias

    fun obtenerAlumnoPorId(alumnoId: String, token: String?) {
        viewModelScope.launch {
            val result = modificarAlumnoUseCase.obtenerAlumnoPorId(alumnoId, token)
            result.onSuccess {
                _alumno.value = it
            }
            result.onFailure {
                // Manejar el error
            }
        }
    }

    fun modificarAlumno(alumno: Alumno, token: String?, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val input = ModificarAlumnoInput(
                alumnoId = alumno.alumnoId,
                nombre = alumno.nombre,
                apellido = alumno.apellido,
                claseId = alumno.claseId,
                alergias = alumno.alergias,
                token = token
            )
            val result = modificarAlumnoUseCase.invoke(input)
            result.onSuccess {
                onSuccess()
            }
            result.onFailure {
                onError(it)
            }
        }
    }

    fun getCursosByCentroEscolar(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            registrarAlumnoUseCase.getCursosByCentroEscolar(centroEscolarId, token).onSuccess {
                _cursos.value = it
            }.onFailure {
                _cursos.value = emptyList()
            }
        }
    }

    fun getAlergias(token: String?) {
        viewModelScope.launch {
            registrarAlumnoUseCase.getAlergias(token)
                .onSuccess {
                    _alergias.value = it
                }.onFailure {
                    _alergias.value = emptyList()
                }
        }
    }
}
