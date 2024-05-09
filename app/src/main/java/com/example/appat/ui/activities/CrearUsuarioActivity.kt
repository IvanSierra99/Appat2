package com.example.appat.ui.activities

import CrearUsuarioViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appat.ui.screens.CrearUsuarioScreen
import com.example.appat.ui.theme.AppatTheme
import org.koin.java.KoinJavaComponent.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrearUsuarioActivity : ComponentActivity() {
    private val crearUsuarioViewModel: CrearUsuarioViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppatTheme {
                CrearUsuarioScreenWithViewModel(crearUsuarioViewModel)
            }
        }
    }
}

@Composable
fun CrearUsuarioScreenWithViewModel(viewModel: CrearUsuarioViewModel) {
    // Obtenemos el ViewModel usando Koin
    CrearUsuarioScreen(viewModel) { usuarioCreado ->
        // Manejar el usuario creado, por ejemplo, mostrar un mensaje o navegar a otra pantalla
    }
}
