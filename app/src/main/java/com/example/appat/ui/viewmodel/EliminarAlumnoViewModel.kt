package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.core.onFailure
import com.example.appat.core.onSuccess
import com.example.appat.domain.usecases.EliminarAlumnoInput
import com.example.appat.domain.usecases.EliminarAlumnoUseCase
import kotlinx.coroutines.launch

class EliminarAlumnoViewModel(private val eliminarAlumnoUseCase: EliminarAlumnoUseCase) : ViewModel() {

    fun eliminarAlumno(alumnoId: String, token: String?, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val input = EliminarAlumnoInput(alumnoId, token)
            val result = eliminarAlumnoUseCase.invoke(input)
            result.onSuccess {
                onSuccess()
            }
            result.onFailure {
                onError(it)
            }
        }
    }
}
