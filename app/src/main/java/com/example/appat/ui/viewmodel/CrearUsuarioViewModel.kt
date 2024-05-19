package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuariInput
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import kotlinx.coroutines.launch

class CrearUsuarioViewModel(val crearUsuarioUseCase: CrearUsuarioUseCase) : ViewModel() {
    fun createUser(input: CrearUsuariInput, onSuccess: (Usuario) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = crearUsuarioUseCase(input)
            result.onSuccess {
                onSuccess(it)
            }
            result.onFailure {
                onError(it)
            }
        }
    }
}

