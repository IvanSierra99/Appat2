package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.ModificarUsuarioUseCase
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.usecases.ModificarUsuarioInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditUserViewModel(private val modificarUsuarioUseCase: ModificarUsuarioUseCase) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun obtenerUsuarioPorId(userId: String, token: String?) {
        viewModelScope.launch {
            val result = modificarUsuarioUseCase.obtenerUsuarioPorId(userId, token)
            result.onSuccess {
                _usuario.value = it
            }
            result.onFailure {
                // Manejar el error
            }
        }
    }

    fun modificarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = modificarUsuarioUseCase.invoke(ModificarUsuarioInput(usuario))
            result.onSuccess {
                onSuccess()
            }
            result.onFailure {
                onError(it)
            }
        }
    }
}
