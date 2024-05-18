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
import com.example.appat.domain.entities.Clase
import com.example.appat.ui.viewmodel.CrearClaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrearClaseActivity : ComponentActivity() {
    private val crearClaseViewModel: CrearClaseViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrearClaseScreenWithViewModel(crearClaseViewModel)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearClaseScreenWithViewModel(viewModel: CrearClaseViewModel) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val activity = LocalContext.current as? Activity

        val sharedPreferences = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null) ?: ""
        val centroEscolarId = sharedPreferences?.getString("centroEscolarId", null) ?: ""

        Scaffold(
            topBar = {
                MyAppTopBar(
                    onMenuClick = {
                        // Acciones al hacer clic en el botón del menú de navegación
                    },
                    schoolName = "Crear Clase"
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
                    CrearClaseScreen(viewModel, centroEscolarId, token) { claseCreada ->
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Clase creada exitosamente",
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
    fun CrearClaseScreen(
        viewModel: CrearClaseViewModel,
        centroEscolarId: String,
        token: String,
        onClaseCreada: (Clase) -> Unit
    ) {
        var nombreClase by remember { mutableStateOf("") }
        var cursoId by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }

        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        val camposObligatoriosLlenos = nombreClase.isNotEmpty() && cursoId.isNotEmpty()

        LaunchedEffect(Unit) {
            viewModel.getCursosByCentroEscolar(centroEscolarId, token)
        }

        val cursos by viewModel.cursos.collectAsState()

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
                value = nombreClase,
                onValueChange = { nombreClase = it },
                label = { Text("Nombre de la Clase") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreClase.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    readOnly = true,
                    value = cursos.find { it.cursoId == cursoId }?.nombre ?: "Seleccione un curso",
                    onValueChange = {},
                    label = { Text("Curso") },
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
                    cursos.forEach { curso ->
                        DropdownMenuItem(
                            onClick = {
                                cursoId = curso.cursoId
                                expanded = false
                            },
                            text = { Text(curso.nombre) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.crearClase(nombreClase, cursoId, token,
                        onSuccess = { claseCreada ->
                            onClaseCreada(claseCreada)
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
                Text("Crear Clase")
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
