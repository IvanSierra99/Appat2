package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.CrearCursoInput
import com.example.appat.domain.usecases.CrearCursoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrearCursoViewModel(private val crearCursoUseCase: CrearCursoUseCase) : ViewModel() {
    private val _state = MutableStateFlow<CursoState>(CursoState.Idle)
    val state: StateFlow<CursoState> = _state

    fun crearCurso(
        input: CrearCursoInput,
        onSuccess: (Curso) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            _state.value = CursoState.Loading
            val result = crearCursoUseCase.invoke(input)
            result.onSuccess {
                _state.value = CursoState.Success(it)
                onSuccess(it)
            }.onFailure {
                _state.value = CursoState.Error(it)
                onError(it)
            }
        }
    }
}




sealed class CursoState {
    object Idle : CursoState()
    object Loading : CursoState()
    data class Success(val curso: Curso) : CursoState()
    data class Error(val error: Throwable) : CursoState()
}
