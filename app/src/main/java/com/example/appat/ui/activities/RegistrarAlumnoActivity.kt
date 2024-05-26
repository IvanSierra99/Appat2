package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import com.example.appat.R
import com.example.appat.domain.entities.Alergia
import com.example.appat.domain.entities.Alumno
import com.example.appat.ui.viewmodel.RegistrarAlumnoViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrarAlumnoActivity : ComponentActivity() {
    private val registrarAlumnoViewModel: RegistrarAlumnoViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                    RegistrarAlumnoScreenWithViewModel(
                        registrarAlumnoViewModel,
                        centroEscolarId,
                        token,
                        paddingValues
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RegistrarAlumnoScreenWithViewModel(
        viewModel: RegistrarAlumnoViewModel,
        centroEscolarId: String?,
        token: String?,
        paddingValues: PaddingValues
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

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
                    RegistrarAlumnoScreen(viewModel, centroEscolarId, token) { alumnoCreado ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Alumno creado exitosamente",
                                duration = SnackbarDuration.Short,
                                actionLabel = "OK"
                            )
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
    fun RegistrarAlumnoScreen(
        viewModel: RegistrarAlumnoViewModel,
        centroEscolarId: String?,
        token: String?,
        onAlumnoCreado: (Alumno) -> Unit
    ) {
        var nombreAlumno by remember { mutableStateOf("") }
        var apellidoAlumno by remember { mutableStateOf("") }
        var claseId by remember { mutableStateOf("") }
        var selectedAlergias by remember { mutableStateOf<List<Alergia>>(emptyList()) }
        var diasHabituales by remember { mutableStateOf<List<String>>(emptyList()) }
        var expandedClase by remember { mutableStateOf(false) }
        var showAlergiaDialog by remember { mutableStateOf(false) }
        var showDiasDialog by remember { mutableStateOf(false) }

        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        val camposObligatoriosLlenos =
            nombreAlumno.isNotEmpty() && apellidoAlumno.isNotEmpty() && claseId.isNotEmpty()

        LaunchedEffect(Unit) {
            viewModel.getCursosByCentroEscolar(centroEscolarId ?: "", token)
            viewModel.getAlergias(token)
        }

        val cursos by viewModel.cursos.collectAsState()
        val alergias by viewModel.alergias.collectAsState()

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

        val daysOfWeek = listOf("MO", "TU", "WE", "TH", "FR", "SA", "SU")
        val daysOfWeekNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

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
                value = nombreAlumno,
                onValueChange = { nombreAlumno = it },
                label = { Text("Nombre del Alumno") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreAlumno.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = apellidoAlumno,
                onValueChange = { apellidoAlumno = it },
                label = { Text("Apellido del Alumno") },
                modifier = Modifier.fillMaxWidth(),
                isError = apellidoAlumno.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expandedClase,
                onExpandedChange = { expandedClase = !expandedClase },
                modifier = Modifier.fillMaxWidth()
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
                                        append(
                                            AnnotatedString(
                                                clase.nombre,
                                                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
                                            )
                                        )
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
                                    modifier = Modifier
                                        .fillMaxWidth()
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDiasDialog = true }
            ) {
                TextField(
                    value = diasHabituales.joinToString { daysOfWeekNames[daysOfWeek.indexOf(it)] },
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            imageVector = if (showDiasDialog) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (showDiasDialog) "Collapse menu" else "Expand menu",
                            tint = LocalContentColor.current.copy(LocalContentAlpha.current)
                        )
                    },
                    label = { Text("Días Habituales") },
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                        disabledLabelColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                        disabledTrailingIconColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showDiasDialog) {
                AlertDialog(
                    onDismissRequest = { showDiasDialog = false },
                    title = { Text("Seleccione los días habituales") },
                    text = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clickable {
                                        val currentDays = diasHabituales.toMutableList()
                                        if (currentDays.containsAll(daysOfWeek)) {
                                            currentDays.clear()
                                        } else {
                                            currentDays.addAll(daysOfWeek)
                                        }
                                        diasHabituales = currentDays.distinct()
                                    }
                            ) {
                                Checkbox(
                                    checked = diasHabituales.containsAll(daysOfWeek),
                                    onCheckedChange = null // Handled by Row onClick
                                )
                                Text("Todos")
                            }
                            daysOfWeek.forEachIndexed { index, day ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .clickable {
                                            val currentDays = diasHabituales.toMutableList()
                                            if (currentDays.contains(day)) {
                                                currentDays.remove(day)
                                            } else {
                                                currentDays.add(day)
                                            }
                                            diasHabituales = currentDays
                                        }
                                ) {
                                    Checkbox(
                                        checked = diasHabituales.contains(day),
                                        onCheckedChange = null // Handled by Row onClick
                                    )
                                    Text(daysOfWeekNames[index])
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDiasDialog = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.registrarAlumno(
                        nombreAlumno,
                        apellidoAlumno,
                        claseId,
                        selectedAlergias,
                        diasHabituales,
                        token,
                        onSuccess = { alumnoCreado ->
                            onAlumnoCreado(alumnoCreado)
                            showError = false
                            nombreAlumno = ""
                            apellidoAlumno = ""
                            claseId = ""
                            selectedAlergias = emptyList()
                            diasHabituales = emptyList()
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
                Text("Registrar Alumno")
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

}