package com.example.appat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appat.domain.usecases.ConsultarGenerarInformeAsistenciaUseCase
import com.example.appat.domain.usecases.DatosInformeInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class InformeAsistenciaViewModel(
    private val consultarGenerarInformeAsistenciaUseCase: ConsultarGenerarInformeAsistenciaUseCase
) : ViewModel() {

    private val _datosInforme = MutableStateFlow<DatosInformeInput?>(null)
    val datosInforme: StateFlow<DatosInformeInput?> = _datosInforme

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun consultarInformeDiario(fecha: LocalDate, centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val informe = consultarGenerarInformeAsistenciaUseCase.consultarGenerarInformeAsistencia(fecha, centroEscolarId, token)
                if (informe.total == 0) { // Asumiendo que 'total' es cero cuando no hay registros
                    _error.value = "Este día no tiene asistencia."
                    _isLoading.value = false
                } else {
                    _datosInforme.value = informe
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al consultar datos."
                _isLoading.value = false
            }
        }
    }

    fun consultarInformeMensual(centroEscolarId: String, token: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val informe = consultarGenerarInformeAsistenciaUseCase.consultarGenerarInformeMensual(centroEscolarId, token)
                _datosInforme.value = informe
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error al consultar datos."
                _isLoading.value = false
            }
        }
    }

    fun resetError() {
        _error.value = null
    }
}
