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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.domain.entities.Correo
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.entities.Usuario
import com.example.appat.domain.usecases.CrearUsuariInput
import com.example.appat.ui.viewmodel.CrearUsuarioViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrearUsuarioActivity : ComponentActivity() {
    private val crearUsuarioViewModel: CrearUsuarioViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val token = sharedPreferences.getString("token", null)
        val rol = sharedPreferences.getString("rol", "")

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            MyAppTopBar(
                onMenuClick = { },
                schoolName = nombreCentro,
                drawerState = drawerState,
                drawerContent = { DefaultDrawerContent(this, drawerState, rol) },
                content = { paddingValues ->
                    CrearUsuarioScreenWithViewModel(
                        crearUsuarioViewModel,
                        centroEscolarId,
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
fun CrearUsuarioScreenWithViewModel(
    viewModel: CrearUsuarioViewModel,
    centroEscolarId: String?,
    token: String?,
    paddingValues: PaddingValues
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity

    LaunchedEffect(Unit) {
        centroEscolarId?.let { viewModel.getCursosByCentroEscolar(it, token) }
    }

    val cursos by viewModel.cursos.collectAsState()

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CrearUsuarioScreen(viewModel, centroEscolarId, token, cursos) { usuarioCreado ->
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
    cursosDisponibles: List<Curso>,
    onUsuarioCreado: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    val roles = listOf("ADMINISTRADOR", "COORDINADOR", "MONITOR")
    var expandedRol by remember { mutableStateOf(false) }
    var expandedCursos by remember { mutableStateOf(false) }
    val selectedCursos = remember { mutableStateListOf<Curso>() }

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
            expanded = expandedRol,
            onExpandedChange = { expandedRol = !expandedRol },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
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
                value = selectedCursos.joinToString(", ") { "${it.nombre} (${it.etapa})" },
                onValueChange = {},
                label = { Text("Cursos") },
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
                cursosDisponibles.forEach { curso ->
                    DropdownMenuItem(
                        onClick = {
                            if (selectedCursos.contains(curso)) {
                                selectedCursos.remove(curso)
                            } else {
                                selectedCursos.add(curso)
                            }
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedCursos.contains(curso),
                                    onCheckedChange = {
                                        if (it) {
                                            selectedCursos.add(curso)
                                        } else {
                                            selectedCursos.remove(curso)
                                        }
                                    }
                                )
                                Text("${curso.nombre} (${curso.etapa})")
                            }
                        }
                    )
                }
            }
        }

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
                        token = token,
                        cursos = selectedCursos.map { it.cursoId }
                    )
                    crearUsuarioViewModel.createUser(
                        input = inputUsuario,
                        onSuccess = { usuarioCreado ->
                            onUsuarioCreado(usuarioCreado)
                            showError = false
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

        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
