package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuariInput
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Curso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrearUsuarioViewModel(
    private val crearUsuarioUseCase: CrearUsuarioUseCase
) : ViewModel() {
    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    fun createUser(input: CrearUsuariInput, onSuccess: (Usuario) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = crearUsuarioUseCase(input)
            result.onSuccess {
                onSuccess(it)
            }.onFailure {
                onError(it)
            }
        }
    }

    fun getCursosByCentroEscolar(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            val result = crearUsuarioUseCase.getCursosByCentroEscolar(centroEscolarId, token)
            result.onSuccess { _cursos.value = it }
            result.onFailure { _cursos.value = emptyList() }
        }
    }
}

