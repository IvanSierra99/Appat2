package com.example.appat.ui.viewmodel

import android.app.Application
import android.content.Context
import android.provider.Settings.Global.putString
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.IniciarSesionUseCase
import com.example.appat.domain.usecases.IniciarSesionInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val iniciarSesionUseCase: IniciarSesionUseCase) : ViewModel() {
    private val _loginState = MutableStateFlow<Usuario?>(null)
    val loginState: StateFlow<Usuario?> = _loginState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun login(username: String, password: String) {
        Log.d("LoginViewModel", "Attempting to login with username: $username")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = iniciarSesionUseCase.invoke(IniciarSesionInput(username, password))
                result.onSuccess {
                    _loginState.value = it
                    Log.d("LoginViewModel", "Login successful for user: ${it.username}")
                }
                result.onFailure {
                    _errorState.value = it.message
                    Log.e("LoginViewModel", "Login failed: ${it.message}")
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Unknown error"
                Log.e("LoginViewModel", "Exception during login: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

