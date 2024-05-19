package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.usecases.EliminarUsuarioInput
import com.example.appat.domain.usecases.EliminarUsuarioUseCase
import kotlinx.coroutines.launch

class EliminarUsuarioViewModel(private val eliminarUsuarioUseCase: EliminarUsuarioUseCase) : ViewModel() {

    fun eliminarUsuario(userId: String, token: String?, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = eliminarUsuarioUseCase(EliminarUsuarioInput(userId, token))
            result.onSuccess {
                onSuccess()
            }
            result.onFailure {
                onError(it)
            }
        }
    }
}
