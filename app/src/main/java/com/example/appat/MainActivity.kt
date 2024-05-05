package com.example.appat

import CrearUsuarioViewModel
import FakeUsuarioRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appat.di.appModule
import com.example.appat.ui.theme.AppatTheme
import org.koin.core.context.GlobalContext.startKoin
import androidx.compose.runtime.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import com.example.appat.ui.screens.CrearUsuarioScreen
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppatTheme {
                MainScreen()
            }
        }
        startKoin {

            modules(appModule)
        }
    }
}

@Composable
fun MainScreen() {
    var showCreateUserScreen by remember { mutableStateOf(false) }

    if (showCreateUserScreen) {
        // Suponiendo que tienes un ViewModel inicializado correctamente
        val viewModel: CrearUsuarioViewModel = CrearUsuarioViewModel(CrearUsuarioUseCaseImpl(FakeUsuarioRepository(
            File("com/example/appat/data/local/fake_data.json")
        )))
        CrearUsuarioScreen(crearUsuarioViewModel = viewModel, onUsuarioCreado = { /* manejar usuario creado */ })
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Button(onClick = { showCreateUserScreen = true }) {
                Text("Crear Usuario")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppatTheme {
        MainScreen()
    }
}
