package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.ModificarUsuarioInput
import com.example.appat.domain.usecases.ModificarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(private val modificarUsuarioUseCase: ModificarUsuarioUseCase) : ViewModel() {
    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    val users: StateFlow<List<Usuario>> = _users

    fun obtenerUsuariosPorCentro(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            val result = modificarUsuarioUseCase.obtenerUsuariosPorCentro(centroEscolarId, token)
            result.onSuccess {
                _users.value = it
            }.onFailure {
            // Manejar errores
            }
        }
    }
}
