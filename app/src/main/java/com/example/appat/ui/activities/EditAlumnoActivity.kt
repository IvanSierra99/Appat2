package com.example.appat.ui.activities

import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentAlpha
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno
import com.example.appat.ui.viewmodel.ModificarAlumnoViewModel
import com.example.appat.ui.viewmodel.EliminarAlumnoViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAlumnoActivity : ComponentActivity() {
    private val modificarAlumnoViewModel: ModificarAlumnoViewModel by viewModel()
    private val eliminarAlumnoViewModel: EliminarAlumnoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alumnoId = intent.getStringExtra("ALUMNO_ID")
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)

        setContent {
            EditAlumnoScreenWithViewModel(
                modificarAlumnoViewModel,
                eliminarAlumnoViewModel,
                alumnoId,
                centroEscolarId,
                token
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlumnoScreenWithViewModel(
    modificarAlumnoViewModel: ModificarAlumnoViewModel,
    eliminarAlumnoViewModel: EliminarAlumnoViewModel,
    alumnoId: String?,
    centroEscolarId: String?,
    token: String?
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as? Activity

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var claseId by remember { mutableStateOf("") }
    var selectedAlergias by remember { mutableStateOf<List<Alergia>>(emptyList()) }
    var expandedClase by remember { mutableStateOf(false) }
    var showAlergiaDialog by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(alumnoId) {
        alumnoId?.let {
            modificarAlumnoViewModel.obtenerAlumnoPorId(it, token)
        }
    }

    LaunchedEffect(Unit) {
        centroEscolarId?.let {
            modificarAlumnoViewModel.getCursosByCentroEscolar(it, token)
        }
        modificarAlumnoViewModel.getAlergias(token)
    }

    val alumno by modificarAlumnoViewModel.alumno.collectAsState()
    val cursos by modificarAlumnoViewModel.cursos.collectAsState()
    val alergias by modificarAlumnoViewModel.alergias.collectAsState()

    LaunchedEffect(alumno) {
        alumno?.let {
            nombre = it.nombre
            apellido = it.apellido
            claseId = it.claseId
            selectedAlergias = it.alergias
        }
    }

    val isFormValid = remember {
        derivedStateOf {
            nombre.isNotEmpty() && apellido.isNotEmpty() && claseId.isNotEmpty()
        }
    }

    // Mapeo para mostrar las etapas de forma legible
    val etapaMapping = mapOf(
        "INFANTIL" to "Infantil",
        "PRIMARIA" to "Primaria",
        "ESO" to "ESO",
        "BACHILLERATO" to "Bachillerato",
        "CICLO_INICIAL" to "Ciclo Inicial",
        "CICLO_MEDIO" to "Ciclo Medio",
        "CICLO_SUPERIOR" to "Ciclo Superior"
    )

    Scaffold(
        topBar = {
            MyAppTopBar(
                onMenuClick = {
                    // Acciones al hacer clic en el botón del menú de navegación
                },
                schoolName = "Editar Alumno"
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
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                ExposedDropdownMenuBox(
                    expanded = expandedClase,
                    onExpandedChange = { expandedClase = !expandedClase },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TextField(
                        readOnly = true,
                        value = cursos.flatMap { it.clases ?: emptyList() }
                            .find { it.claseId == claseId }
                            ?.let { clase ->
                                val curso = cursos.find { it.cursoId == clase.cursoId }
                                "${curso?.nombre}, ${etapaMapping[curso?.etapa]} ${clase.nombre}"
                            } ?: "Seleccione una clase",
                        onValueChange = {},
                        label = { Text("Clase") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedClase) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (expandedClase) "Collapse menu" else "Expand menu"
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedClase,
                        onDismissRequest = { expandedClase = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        cursos.sortedBy { it.etapa }.forEach { curso ->
                            curso.clases?.sortedBy { it.nombre }?.forEach { clase ->
                                DropdownMenuItem(
                                    onClick = {
                                        claseId = clase.claseId
                                        expandedClase = false
                                    },
                                    text = {
                                        Text(buildAnnotatedString {
                                            append("${curso.nombre}, ${etapaMapping[curso.etapa]} ")
                                            append(AnnotatedString(clase.nombre, spanStyle = SpanStyle(fontWeight = FontWeight.Bold)))
                                        })
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAlergiaDialog = true }
                ) {
                    TextField(
                        value = selectedAlergias.joinToString { it.nombre },
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = if (showAlergiaDialog) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (showAlergiaDialog) "Collapse menu" else "Expand menu",
                                tint = LocalContentColor.current.copy(LocalContentAlpha.current)
                            )
                        },
                        label = { Text("Alergias") },
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                            disabledLabelColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                            disabledTrailingIconColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (showAlergiaDialog) {
                    AlertDialog(
                        onDismissRequest = { showAlergiaDialog = false },
                        title = { Text("Seleccione las alergias") },
                        text = {
                            Column {
                                alergias.forEach { alergia ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clickable {
                                                val currentAlergias = selectedAlergias.toMutableList()
                                                if (currentAlergias.contains(alergia)) {
                                                    currentAlergias.remove(alergia)
                                                } else {
                                                    currentAlergias.add(alergia)
                                                }
                                                selectedAlergias = currentAlergias
                                            }
                                    ) {
                                        Checkbox(
                                            checked = selectedAlergias.contains(alergia),
                                            onCheckedChange = null // Handled by Row onClick
                                        )
                                        Text(alergia.nombre)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showAlergiaDialog = false }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        alumno?.let {
                            val updatedAlumno = it.copy(
                                nombre = nombre,
                                apellido = apellido,
                                claseId = claseId,
                                alergias = selectedAlergias
                            )
                            modificarAlumnoViewModel.modificarAlumno(updatedAlumno, token,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Alumno modificado exitosamente",
                                            duration = SnackbarDuration.Short,
                                            actionLabel = "OK"
                                        )
                                        context?.finish()
                                    }
                                },
                                onError = { error ->
                                    showError = true
                                    errorMessage = error.message.toString()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid.value
                ) {
                    Text("Modificar alumno")
                }

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        alumnoId?.let {
                            eliminarAlumnoViewModel.eliminarAlumno(it, token,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Alumno eliminado exitosamente",
                                            duration = SnackbarDuration.Short,
                                            actionLabel = "OK"
                                        )
                                        context?.finish()
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
                    Text("Eliminar alumno")
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
