package com.example.appat

import CrearUsuarioViewModel
import FakeUsuarioRepository
import android.app.Application
import android.os.Bundle
import android.util.Log
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
import androidx.room.DeleteTable.Entries
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import com.example.appat.ui.screens.CrearUsuarioScreen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.logging.Level


class Appat : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Appat)
            modules(appModule)
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppatTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Obteniendo el ViewModel con Koin
    val crearUsuarioViewModel: CrearUsuarioViewModel = viewModel()

    // Ahora puedes usar el ViewModel en tu UI
    var showCreateUserScreen by remember { mutableStateOf(false) }

    if (showCreateUserScreen) {
        CrearUsuarioScreen(crearUsuarioViewModel = crearUsuarioViewModel, onUsuarioCreado = {})
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
/*
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {

    AppatTheme {
        MainScreen()
    }
}
*/