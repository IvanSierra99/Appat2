package com.example.appat.ui.screens

import CrearUsuarioViewModel
import FakeUsuarioRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
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
    val roles = listOf("ADMINISTRADOR", "COORDINADOR", "MONITOR")
    var expanded by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    var emailError by remember { mutableStateOf(false) }

    val camposObligatoriosLlenos = nombre.isNotEmpty() && apellido1.isNotEmpty() && rol.isNotEmpty()

    LaunchedEffect(email) {
        if(Correo.isValidEmail(email)){
            emailError = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TextFields para los datos del usuario

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombre.isEmpty()
        )
        OutlinedTextField(
            value = apellido1,
            onValueChange = { apellido1 = it },
            label = { Text("Apellido 1") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            isError = apellido1.isEmpty()
        )
        OutlinedTextField(
            value = apellido2,
            onValueChange = { apellido2 = it },
            label = { Text("Apellido 2 (opcional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !Correo.isValidEmail(email) || email.isEmpty()
        )
        if (emailError) {
            Text(
                "El formato del correo electrónico es incorrecto.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            TextField(
                readOnly = true,
                value = rol.ifEmpty { "Seleccione un rol" },
                onValueChange = {},
                label = { Text("Rol") },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse menu" else "Expand menu"
                    )
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                roles.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            rol = label
                            expanded = false
                        },
                        text = { Text(label) }
                    )
                }
            }
        }

        // Botón para crear el usuario
        Button(modifier = Modifier.padding(top = 16.dp),
            enabled = camposObligatoriosLlenos,
            onClick = {
                if (!Correo.isValidEmail(email)) {
                    emailError = true
                } else {
                    emailError = false
                    val inputUsuario = CrearUsuariInput(
                        nombre = nombre,
                        apellido1 = apellido1,
                        apellido2 = apellido2,
                        correo = email,
                        rol = rol
                    )
                    crearUsuarioViewModel.createUser(
                        input = inputUsuario,
                        onSuccess = { usuarioCreado ->
                            onUsuarioCreado(usuarioCreado)
                            showError = false  // Reset error visibility
                        },
                        onError = { error ->
                            showError = true
                            errorMessage = error.message.toString()
                        }
                    )
                }
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
/*
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
*/