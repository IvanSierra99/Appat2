package com.example.appat.ui.screens

import CrearUsuarioViewModel
import FakeUsuarioRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appat.domain.entities.*
import com.example.appat.domain.usecases.CrearUsuariInput
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioScreen(
    crearUsuarioViewModel: CrearUsuarioViewModel,
    onUsuarioCreado: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var apellido2 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TextFields para los datos del usuario

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apellido1,
            onValueChange = { apellido1 = it },
            label = { Text("Apellido 1") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apellido2,
            onValueChange = { apellido2 = it },
            label = { Text("Apellido 2 (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown para seleccionar el rol
        Row (

        ){
            DropdownMenu(expanded = true, onDismissRequest = { /*TODO*/ }) {
                DropdownMenuItem(
                    onClick = { rol = "ADMINISTRADOR" },
                    text = { Text("Administrador") }
                )
                DropdownMenuItem(
                    onClick = { rol = "COORDINADOR" },
                    text = { Text("Coordinador") }
                )
                DropdownMenuItem(
                    onClick = { rol = "MONITOR" },
                    text = { Text("Monitor") }
                )
            }
        }


        // Botón para crear el usuario

        Button(onClick = {
            val usuario = Usuario(
                nombre = Nombre(nombre),
                apellido1 = Apellido(apellido1),
                apellido2 = Apellido(apellido2),
                correo = Correo(email),
                username = Username(),
                rol = Rol(rol)
            )
            crearUsuarioViewModel.createUser(
                usuario = usuario,
                onUsuarioCreado = { usuarioCreado ->
                    onUsuarioCreado(usuarioCreado)
                    showError = false  // Reset error visibility
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Usuario creado con éxito",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                onError = { error ->
                    showError = true
                    errorMessage = "Usuario no creado"
                }
            )
        }) {
            Text("Crear usuario")
        }

// Mostrar mensaje de error si es necesario
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// Vista previa del compositor

@Preview(showBackground = true)
@Composable
fun CrearUsuarioScreenPreview() {
    val crearUsuarioUseCase = CrearUsuarioUseCaseImpl(FakeUsuarioRepository(File("com/example/appat/data/local/fake_data.json")))
    val crearUsuarioViewModel = CrearUsuarioViewModel(crearUsuarioUseCase)

    CrearUsuarioScreen(
        crearUsuarioViewModel = crearUsuarioViewModel,
        onUsuarioCreado = {}
    )
}
