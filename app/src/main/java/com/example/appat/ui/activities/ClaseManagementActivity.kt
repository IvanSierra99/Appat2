package com.example.appat.ui.activities

import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appat.R
import com.example.appat.domain.entities.Curso
import com.example.appat.ui.viewmodel.CrearClaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClaseManagementActivity : ComponentActivity() {
    private val crearClaseViewModel: CrearClaseViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClaseManagementScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ClaseManagementScreen() {
        val context = LocalContext.current as? Activity
        var expanded by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val sharedPreferences = context?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null) ?: ""
        val centroEscolarId = sharedPreferences?.getString("centroEscolarId", null) ?: ""

        LaunchedEffect(Unit) {
            crearClaseViewModel.getCursosByCentroEscolar(centroEscolarId, token)
        }

        val cursos by crearClaseViewModel.cursos.collectAsState()

        val etapaOrder = mapOf(
            "INFANTIL" to 0,
            "CICLO_INICIAL" to 1,
            "CICLO_MEDIO" to 2,
            "CICLO_SUPERIOR" to 3,
            "PRIMARIA" to 4,
            "ESO" to 5,
            "BACHILLERATO" to 6
        )

        Scaffold(
            topBar = {
                MyAppTopBar(
                    onMenuClick = {
                        // Acciones al hacer clic en el botón del menú de navegación
                    },
                    schoolName = "Gestión de Clases"
                )
            },
            floatingActionButton = {
                Box {
                    FloatingActionButton(onClick = {
                        expanded = true
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Crear Clase o Curso")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.wrapContentSize()
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                context?.startActivity(Intent(context, CrearClaseActivity::class.java))
                            },
                            text = { Text("Clase") },
                            modifier = Modifier.wrapContentSize()
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                context?.startActivity(Intent(context, CrearCursoActivity::class.java))
                            },
                            text = { Text("Curso") },
                            modifier = Modifier.wrapContentSize()
                        )
                    }
                }
            },
            content = { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = colorResource(id = R.color.light_primary)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        cursos.groupBy { it.etapa }
                            .toSortedMap(compareBy { etapaOrder[it] })
                            .forEach { (etapa, cursos) ->
                            item {
                                Text(
                                    text = getEtapaName(etapa),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(cursos) { curso ->
                                CursoItem(curso)
                            }
                        }
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null) ?: ""
        val centroEscolarId = sharedPreferences?.getString("centroEscolarId", null) ?: ""
        crearClaseViewModel.getCursosByCentroEscolar(centroEscolarId, token)
    }

    @Composable
    fun CursoItem(curso: Curso) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { /* Acción cuando se hace clic en el curso */ }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = curso.nombre, style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
                if (curso.clases?.isNotEmpty() == true) {
                    val clasesText = curso.clases.joinToString(separator = ", ") { it.nombre }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding()
                    ) {
                        Text(text = "Clases:", style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                        Text(
                            text = clasesText,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else {
                    Text(text = "Clases: -", style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                }
            }
        }
    }

    private fun getEtapaName(etapa: String): String {
        return when (etapa) {
            "INFANTIL" -> "Infantil"
            "PRIMARIA" -> "Primaria"
            "ESO" -> "ESO"
            "BACHILLERATO" -> "Bachillerato"
            "CICLO_INICIAL" -> "Ciclo Inicial"
            "CICLO_MEDIO" -> "Ciclo Medio"
            "CICLO_SUPERIOR" -> "Ciclo Superior"
            else -> etapa
        }
    }
}
