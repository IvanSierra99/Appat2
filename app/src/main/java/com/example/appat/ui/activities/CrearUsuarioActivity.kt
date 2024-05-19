package com.example.appat.ui.activities

import MyAppTopBar
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.domain.entities.Correo
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.CrearUsuariInput
import com.example.appat.ui.viewmodel.CrearUsuarioViewModel
import kotlinx.coroutines.launch

class CrearUsuarioActivity : ComponentActivity() {
    private val crearUsuarioViewModel: CrearUsuarioViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val token = sharedPreferences.getString("token", null)

        setContent {
            CrearUsuarioScreenWithViewModel(crearUsuarioViewModel, centroEscolarId, nombreCentro, token)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioScreenWithViewModel(viewModel: CrearUsuarioViewModel, centroEscolarId: String?, nombreCentro: String?, token: String?) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    // Obtenemos el ViewModel usando Koin
    Scaffold(
        topBar = {
            MyAppTopBar(
                onMenuClick = {
                    // Acciones al hacer clic en el botón del menú de navegación
                },
                schoolName = nombreCentro
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
    )  { innerPadding ->
        // The innerPadding adjusts the padding to avoid overlap with the scaffold's app bars or snackbar
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = colorResource(id = R.color.light_primary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CrearUsuarioScreen(viewModel, centroEscolarId, token) { usuarioCreado ->
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
}

@Composable
private fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioScreen(
    crearUsuarioViewModel: CrearUsuarioViewModel,
    centroEscolarId: String?,
    token: String?,
    onUsuarioCreado: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
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
        if (Correo.isValidEmail(email)) {
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
        verticalArrangement = Arrangement.Center,
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
            label = { Text("Apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = apellido1.isEmpty()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                roles.forEach { label ->
                    DropdownMenuItem(
                        onClick = { rol = label; expanded = false },
                        text = { Text(label) }
                    )
                }
            }
        }

        // Botón para crear el usuario
        Button(
            modifier = Modifier.padding(top = 16.dp),
            enabled = camposObligatoriosLlenos,
            onClick = {
                if (!Correo.isValidEmail(email)) {
                    emailError = true
                } else {
                    emailError = false
                    val inputUsuario = CrearUsuariInput(
                        nombre = nombre,
                        apellido1 = apellido1,
                        correo = email,
                        rol = rol,
                        centroEscolarId = centroEscolarId,
                        token = token
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
            }
        ) {
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