package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Clase
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.CrearClaseInput
import com.example.appat.domain.usecases.CrearClaseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrearClaseViewModel(
    private val crearClaseUseCase: CrearClaseUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ClaseState>(ClaseState.Idle)
    val state: StateFlow<ClaseState> = _state

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    fun crearClase(nombre: String, cursoId: String, token: String?, onSuccess: (Clase) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            _state.value = ClaseState.Loading
            val result = crearClaseUseCase.invoke(CrearClaseInput(nombre, cursoId, token))
            result.onSuccess {
                _state.value = ClaseState.Success(it)
                onSuccess(it)
            }.onFailure {
                _state.value = ClaseState.Error(it)
                onError(it)
            }
        }
    }

    fun getCursosByCentroEscolar(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            val result = crearClaseUseCase.getCursosByCentroEscolar(centroEscolarId, token)
            result.onSuccess { _cursos.value = it }
            result.onFailure { _cursos.value = emptyList() }
        }
    }
}

sealed class ClaseState {
    object Idle : ClaseState()
    object Loading : ClaseState()
    data class Success(val clase: Clase) : ClaseState()
    data class Error(val error: Throwable) : ClaseState()
}
