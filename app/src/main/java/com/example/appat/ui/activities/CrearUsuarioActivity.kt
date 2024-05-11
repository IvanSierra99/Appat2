package com.example.appat.ui.activities

import CrearUsuarioViewModel
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appat.ui.screens.CrearUsuarioScreen
import com.example.appat.ui.theme.AppatTheme
import org.koin.java.KoinJavaComponent.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    // Obtenemos el ViewModel usando Koin
    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    )  { innerPadding ->
        // The innerPadding adjusts the padding to avoid overlap with the scaffold's app bars or snackbar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CrearUsuarioScreen(viewModel) { usuarioCreado ->
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Usuario creado exitosamente",
                        duration = SnackbarDuration.Short,
                        actionLabel = "OK"
                    )
                    if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                        activity?.finish()
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        )
    }
}