package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.RegistrarAlumnoInput
import com.example.appat.domain.usecases.RegistrarAlumnoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrarAlumnoViewModel(
    private val registrarAlumnoUseCase: RegistrarAlumnoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AlumnoState>(AlumnoState.Idle)
    val state: StateFlow<AlumnoState> = _state

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    private val _alergias = MutableStateFlow<List<Alergia>>(emptyList())
    val alergias: StateFlow<List<Alergia>> = _alergias

    fun registrarAlumno(
        nombre: String,
        apellido: String,
        claseId: String,
        alergias: List<Alergia>,
        diasHabituales: List<String>,
        token: String?,
        onSuccess: (Alumno) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            _state.value = AlumnoState.Loading
            val result = registrarAlumnoUseCase.invoke(RegistrarAlumnoInput(nombre, apellido, claseId, alergias, diasHabituales, token))
            result.onSuccess {
                _state.value = AlumnoState.Success(it)
                onSuccess(it)
            }.onFailure {
                _state.value = AlumnoState.Error(it)
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

sealed class AlumnoState {
    object Idle : AlumnoState()
    object Loading : AlumnoState()
    data class Success(val alumno: Alumno) : AlumnoState()
    data class Error(val error: Throwable) : AlumnoState()
}
