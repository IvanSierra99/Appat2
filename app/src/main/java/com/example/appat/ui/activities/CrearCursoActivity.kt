package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appat.R
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.entities.Clase
import com.example.appat.domain.usecases.CrearCursoInput
import com.example.appat.ui.viewmodel.CrearCursoViewModel
import com.example.appat.ui.viewmodel.CrearClaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrearCursoActivity : ComponentActivity() {
    private val crearCursoViewModel: CrearCursoViewModel by viewModel()
    private val crearClaseViewModel: CrearClaseViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
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
                    MainScreen(
                        crearCursoViewModel,
                        crearClaseViewModel,
                        centroEscolarId,
                        token,
                        paddingValues
                    )
                }
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainScreen(
        viewModel: CrearCursoViewModel,
        claseViewModel: CrearClaseViewModel,
        centroEscolarId: String?,
        token: String?,
        paddingValues: PaddingValues
    ) {
        val cursoCreado = remember { mutableStateOf<Curso?>(null) }
        val showDialog = remember { mutableStateOf(false) }
        val showAddClassesDialog = remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        var allClassesCreated by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.padding(paddingValues),
            snackbarHost = { CustomSnackbarHost(snackbarHostState) }
        ) {
            CrearCursoScreenWithViewModel(
                viewModel,
                centroEscolarId,
                token
            ) { curso ->
                cursoCreado.value = curso
                showAddClassesDialog.value = true
            }

            if (showAddClassesDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showAddClassesDialog.value = false
                        // Reset course fields if the dialog is dismissed
                        cursoCreado.value = null
                    },
                    title = { Text("¿Quieres añadir clases?") },
                    text = { Text("El curso ha sido creado. ¿Deseas agregar clases al curso?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showAddClassesDialog.value = false
                                showDialog.value = true
                            }
                        ) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showAddClassesDialog.value = false
                                // Reset course fields if "No" is selected
                                cursoCreado.value = null
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            if (showDialog.value) {
                Dialog(onDismissRequest = {
                    showDialog.value = false
                    if (!allClassesCreated) {
                        cursoCreado.value = null
                    }
                }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column {
                            CrearClaseScreen(
                                viewModel = claseViewModel,
                                curso = cursoCreado.value!!,
                                onClaseCreada = {
                                    cursoCreado.value?.clases?.add(it)
                                    // Reiniciar el diálogo para crear una nueva clase
                                    showDialog.value = false
                                    showDialog.value = true
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    showDialog.value = false
                                    allClassesCreated = true
                                },
                                modifier = Modifier.align(Alignment.End)
                                    .padding(end = 16.dp, bottom = 16.dp)
                            ) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
            LaunchedEffect(allClassesCreated) {
                if (allClassesCreated) {
                    snackbarHostState.showSnackbar(
                        message = "Todas las clases han sido agregadas y el curso se ha creado exitosamente",
                        duration = SnackbarDuration.Short,
                        actionLabel = "OK"
                    )
                    cursoCreado.value = null
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearCursoScreenWithViewModel(viewModel: CrearCursoViewModel, centroEscolarId: String?, token: String?, onCursoCreado: (Curso) -> Unit) {
        Scaffold(
            snackbarHost = { CustomSnackbarHost(remember { SnackbarHostState() }) },
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
                    CrearCursoScreen(viewModel, centroEscolarId, token) { cursoCreado ->
                        onCursoCreado(cursoCreado)
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
    fun CrearCursoScreen(
        crearCursoViewModel: CrearCursoViewModel,
        centroEscolarId: String?,
        token: String?,
        onCursoCreado: (Curso) -> Unit
    ) {
        var nombreCurso by remember { mutableStateOf("") }
        var etapa by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        val etapasMap = mapOf(
            "Infantil" to "INFANTIL",
            "Primaria" to "PRIMARIA",
            "ESO" to "ESO",
            "Bachillerato" to "BACHILLERATO",
            "Ciclo Inicial" to "CICLO_INICIAL",
            "Ciclo Medio" to "CICLO_MEDIO",
            "Ciclo Superior" to "CICLO_SUPERIOR"
        )

        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        val camposObligatoriosLlenos = nombreCurso.isNotEmpty() && etapa.isNotEmpty()

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
                value = nombreCurso,
                onValueChange = { nombreCurso = it },
                label = { Text("Nombre del Curso") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreCurso.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    readOnly = true,
                    value = etapasMap.entries.find { it.value == etapa }?.key ?: "Seleccione una etapa",
                    onValueChange = {},
                    label = { Text("Etapa del Curso") },
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
                    etapasMap.forEach { (label, value) ->
                        DropdownMenuItem(
                            onClick = {
                                etapa = value
                                expanded = false
                            },
                            text = { Text(label) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val inputCurso = CrearCursoInput(
                        nombre = nombreCurso,
                        etapa = etapa,
                        centroEscolarId = centroEscolarId ?: "",
                        token = token
                    )
                    crearCursoViewModel.crearCurso(
                        input = inputCurso,
                        onSuccess = { cursoCreado ->
                            onCursoCreado(cursoCreado)
                            showError = false  // Reset error visibility
                            nombreCurso = ""
                            etapa = ""
                        },
                        onError = { error ->
                            showError = true
                            errorMessage = error.message.toString()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = camposObligatoriosLlenos
            ) {
                Text("Crear Curso")
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearClaseScreen(
        viewModel: CrearClaseViewModel,
        curso: Curso,
        onClaseCreada: (Clase) -> Unit
    ) {
        val sharedPreferences = LocalContext.current.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null) ?: ""

        var nombreClase by remember { mutableStateOf("") }
        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        val camposObligatoriosLlenos = nombreClase.isNotEmpty()

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                value = nombreClase,
                onValueChange = { nombreClase = it },
                label = { Text("Nombre de la Clase") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreClase.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.crearClase(nombreClase, curso.cursoId, token,
                        onSuccess = { claseCreada ->
                            onClaseCreada(claseCreada)
                            showError = false  // Reset error visibility
                            nombreClase = ""  // Reset the class name field for new input
                        },
                        onError = { error ->
                            showError = true
                            errorMessage = error.message.toString()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = camposObligatoriosLlenos
            ) {
                Text("Agregar Clase")
            }

            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            val clasesText = curso.clases?.joinToString(separator = ", ") { it.nombre }
            Text("Clases en el curso: $clasesText", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
