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
import com.example.appat.R
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.usecases.CrearCursoInput
import com.example.appat.ui.viewmodel.CrearCursoViewModel
import com.example.appat.ui.viewmodel.CursoState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrearCursoActivity : ComponentActivity() {
    private val crearCursoViewModel: CrearCursoViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val token = sharedPreferences.getString("token", null)

        setContent {
            CrearCursoScreenWithViewModel(crearCursoViewModel, centroEscolarId, nombreCentro, token)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearCursoScreenWithViewModel(viewModel: CrearCursoViewModel, centroEscolarId: String?, nombreCentro: String?, token: String?) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val activity = LocalContext.current as? Activity

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
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Curso creado exitosamente",
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
}
