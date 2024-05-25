package com.example.appat.ui.activities

import DefaultDrawerContent
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
import com.example.appat.domain.entities.*
import com.example.appat.ui.viewmodel.EditUserViewModel
import com.example.appat.ui.viewmodel.EliminarUsuarioViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditUserActivity : ComponentActivity() {
    private val editUserViewModel: EditUserViewModel by viewModel()
    private val eliminarUsuarioViewModel: EliminarUsuarioViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("USER_ID")
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val token = sharedPreferences.getString("token", null)

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            MyAppTopBar(
                onMenuClick = { },
                schoolName = nombreCentro,
                drawerState = drawerState,
                drawerContent = { DefaultDrawerContent(this, drawerState) },
                content = { paddingValues ->
                    EditUserScreenWithViewModel(
                        editUserViewModel,
                        eliminarUsuarioViewModel,
                        userId,
                        token,
                        paddingValues
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreenWithViewModel(
    editUserViewModel: EditUserViewModel,
    eliminarUsuarioViewModel: EliminarUsuarioViewModel,
    userId: String?,
    token: String?,
    paddingValues: PaddingValues
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as? Activity

    var isUserLoading by remember { mutableStateOf(true) }
    var isCoursesLoading by remember { mutableStateOf(true) }
    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val roles = listOf("COORDINADOR", "MONITOR", "ADMINISTRADOR")
    var expandedRol by remember { mutableStateOf(false) }
    var expandedCursos by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    var emailError by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val selectedCursosIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(userId) {
        userId?.let {
            editUserViewModel.obtenerUsuarioPorId(it, token)
        }
    }

    val usuario by editUserViewModel.usuario.collectAsState()

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre.nombre
            apellido1 = it.apellido1.apellido
            email = it.correo.correo
            rol = it.rol.rol
            selectedCursosIds.clear()
            selectedCursosIds.addAll(it.cursos)
            isUserLoading = false
        }
    }

    LaunchedEffect(usuario?.centroEscolar?.centroId) {
        usuario?.centroEscolar?.centroId?.let {
            editUserViewModel.getCursosByCentroEscolar(it, token)
        }
    }

    val cursos by editUserViewModel.cursos.collectAsState()

    LaunchedEffect(cursos) {
        if (cursos.isNotEmpty()) {
            isCoursesLoading = false
        }
    }

    val etapaMapping = mapOf(
        "INFANTIL" to "Infantil",
        "PRIMARIA" to "Primaria",
        "ESO" to "ESO",
        "BACHILLERATO" to "Bachillerato",
        "CICLO_INICIAL" to "Ciclo Inicial",
        "CICLO_MEDIO" to "Ciclo Medio",
        "CICLO_SUPERIOR" to "Ciclo Superior"
    )

    val isFormValid = remember {
        derivedStateOf {
            email.isEmpty() || Correo.isValidEmail(email) &&
                    password == confirmPassword
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = colorResource(id = R.color.light_primary)
        ) {
            if (isUserLoading || isCoursesLoading) { // Muestra un indicador de carga mientras se cargan los datos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                        isError = email.isNotEmpty() && !Correo.isValidEmail(email)
                    )
                    if (emailError && email.isNotEmpty() && !Correo.isValidEmail(email)) {
                        Text(
                            "El formato del correo electrónico es incorrecto.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(imageVector = image, contentDescription = "Toggle password visibility")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña") },
                        visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                                Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    if (password != confirmPassword) {
                        Text(
                            "Las contraseñas no coinciden.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedRol,
                        onExpandedChange = { expandedRol = !expandedRol },
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
                                    imageVector = if (expandedRol) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (expandedRol) "Collapse menu" else "Expand menu"
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRol,
                            onDismissRequest = { expandedRol = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            roles.forEach { label ->
                                DropdownMenuItem(
                                    onClick = { rol = label; expandedRol = false },
                                    text = { Text(label) }
                                )
                            }
                        }
                    }

                    // Dropdown para selección múltiple de cursos
                    Text("Cursos:", modifier = Modifier.padding(top = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedCursos,
                        onExpandedChange = { expandedCursos = !expandedCursos },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedCursosIds.joinToString(", ") { cursoId ->
                                cursos.find { it.cursoId == cursoId }?.let { curso ->
                                    "${curso.nombre} (${etapaMapping[curso.etapa] ?: curso.etapa})"
                                } ?: cursoId
                            },
                            onValueChange = {},
                            label = { Text("Seleccione cursos") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedCursos) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (expandedCursos) "Collapse menu" else "Expand menu"
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCursos,
                            onDismissRequest = { expandedCursos = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cursos.forEach { curso ->
                                DropdownMenuItem(
                                    onClick = {
                                        if (selectedCursosIds.contains(curso.cursoId)) {
                                            selectedCursosIds.remove(curso.cursoId)
                                        } else {
                                            selectedCursosIds.add(curso.cursoId)
                                        }
                                    },
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedCursosIds.contains(curso.cursoId),
                                                onCheckedChange = {
                                                    if (it) {
                                                        selectedCursosIds.add(curso.cursoId)
                                                    } else {
                                                        selectedCursosIds.remove(curso.cursoId)
                                                    }
                                                }
                                            )
                                            Text("${curso.nombre} (${etapaMapping[curso.etapa] ?: curso.etapa})")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Botón para modificar el usuario
                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = isFormValid.value,
                        onClick = {
                            emailError = email.isNotEmpty() && !Correo.isValidEmail(email)
                            if (!emailError) {
                                val inputUsuario = Usuario(
                                    userId = usuario?.userId ?: "",
                                    nombre = Nombre(if (nombre.isNotEmpty()) nombre else usuario?.nombre?.nombre ?: ""),
                                    apellido1 = Apellido(if (apellido1.isNotEmpty()) apellido1 else usuario?.apellido1?.apellido ?: ""),
                                    username = Username(usuario?.username?.username ?: ""),
                                    correo = if (email.isNotEmpty()) Correo(email) else usuario?.correo ?: Correo(""),
                                    rol = if (rol.isNotEmpty()) Rol(rol) else usuario?.rol ?: Rol(""),
                                    centroEscolar = usuario?.centroEscolar,
                                    cursos = selectedCursosIds.toList(),
                                    token = token,
                                    contraseña = if (password.isNotEmpty()) Contraseña.crearNueva(password) else null
                                )
                                editUserViewModel.modificarUsuario(
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

                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            showDeleteDialog = true // Mostrar el diálogo de confirmación
                        }
                    ) {
                        Text("Eliminar usuario")
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

    // Dialog de confirmación para eliminar usuario
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este usuario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let {
                            eliminarUsuarioViewModel.eliminarUsuario(
                                it,
                                token,
                                onSuccess = {
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Usuario eliminado exitosamente",
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
                        showDeleteDialog = false // Cerrar el diálogo después de la eliminación
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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