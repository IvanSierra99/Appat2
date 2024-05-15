package com.example.appat.ui.activities

import MyAppTopBar
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.domain.entities.Apellido
import com.example.appat.domain.entities.Correo
import com.example.appat.domain.entities.Nombre
import com.example.appat.domain.entities.Rol
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.entities.Contraseña
import com.example.appat.ui.viewmodel.EditUserViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditUserActivity : ComponentActivity() {
    private val editUserViewModel: EditUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("USER_ID")
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        setContent {
            EditUserScreenWithViewModel(editUserViewModel, userId, token)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreenWithViewModel(viewModel: EditUserViewModel, userId: String?, token: String?) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as? Activity

    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val roles = listOf("ADMINISTRADOR", "COORDINADOR", "MONITOR")
    var expanded by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val isFormValid by remember { derivedStateOf {
            !passwordError && !emailError
        }
    }

    LaunchedEffect(email) {
        if (email.isNotEmpty() && !Correo.isValidEmail(email)) {
            emailError = true
        } else {
            emailError = false
        }
    }

    LaunchedEffect(password, confirmPassword) {
        passwordError = password.isNotEmpty() && password != confirmPassword
    }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.obtenerUsuarioPorId(it, token)
        }
    }

    val usuario by viewModel.usuario.collectAsState()

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre.nombre
            apellido1 = it.apellido1.apellido
            email = it.correo.correo
            rol = it.rol.rol
        }
    }

    Scaffold(
        topBar = {
            MyAppTopBar(
                onMenuClick = {
                    // Acciones al hacer clic en el botón del menú de navegación
                },
                schoolName = "Editar Usuario"
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = colorResource(id = R.color.light_primary)
        ) {
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
                )
                OutlinedTextField(
                    value = apellido1,
                    onValueChange = { apellido1 = it },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError
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

                // TextFields para la contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña nueva") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    isError = passwordError
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña nueva") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                            Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                        }
                    },
                    isError = passwordError
                )
                if (passwordError) {
                    Text(
                        "Las contraseñas no coinciden.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }

                // Botón para modificar el usuario
                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = isFormValid,
                    onClick = {
                        if (!emailError) {
                            val inputUsuario = Usuario(
                                userId = userId ?: "",
                                nombre = Nombre(nombre),
                                apellido1 = Apellido(apellido1),
                                correo = if (email.isNotEmpty()) Correo(email) else usuario!!.correo,
                                rol = Rol(rol),
                                centroEscolar = usuario?.centroEscolar,
                                token = token
                            ).copy(contraseña = if (password.isNotEmpty()) Contraseña.crearNueva(password) else null)
                            viewModel.modificarUsuario(
                                inputUsuario,
                                onSuccess = {
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Usuario modificado exitosamente",
                                            duration = SnackbarDuration.Short,
                                            actionLabel = "OK"
                                        )
                                        if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                                            context?.finish()
                                        }
                                    }
                                },
                                onError = { error ->
                                    showError = true
                                    errorMessage = error.message.toString()
                                }
                            )
                        }
                    }
                ) {
                    Text("Modificar usuario")
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
