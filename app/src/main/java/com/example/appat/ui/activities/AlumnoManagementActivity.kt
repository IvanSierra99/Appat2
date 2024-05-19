package com.example.appat.ui.activities

import MyAppTopBar
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appat.R
import com.example.appat.domain.entities.Alumno
import com.example.appat.domain.entities.Curso
import com.example.appat.ui.viewmodel.AlumnoManagementViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlumnoManagementActivity : ComponentActivity() {
    private val alumnoManagementViewModel: AlumnoManagementViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)

        setContent {
            Scaffold(
                topBar = {
                    Column(modifier = Modifier.background(color = colorResource(id = R.color.light_primary))) {
                        MyAppTopBar(
                            onMenuClick = {
                                // Acción al hacer clic en el botón del menú
                            },
                            schoolName = nombreCentro
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(color = colorResource(id = R.color.light_primary)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SearchBar(modifier = Modifier.weight(0.8f).height(56.dp), onSearch = { query ->
                                alumnoManagementViewModel.updateSearchQuery(query)
                            })
                            SortDropdownMenu(modifier = Modifier.weight(0.2f).height(56.dp), onSortSelected = { sortOrder ->
                                alumnoManagementViewModel.updateSortOrder(sortOrder)
                            })
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            startActivity(Intent(this, RegistrarAlumnoActivity::class.java))
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar Alumno")
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = colorResource(id = R.color.light_primary)
                ) {
                    AlumnoList(alumnoManagementViewModel, paddingValues)
                }
            }
        }

        // Cargar los cursos del centro escolar
        centroEscolarId?.let { alumnoManagementViewModel.obtenerCursosPorCentro(it, token) }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)

        // Volver a cargar los cursos al volver a la actividad
        centroEscolarId?.let { alumnoManagementViewModel.obtenerCursosPorCentro(it, token) }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, onSearch: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onSearch(searchQuery)
        },
        placeholder = { Text(text = "Buscar...") },
        modifier = modifier
            .padding(end = 0.dp)
            .background(color = colorResource(id = R.color.light_primary))
    )
}

@Composable
fun SortDropdownMenu(modifier: Modifier = Modifier, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Apellido (A-Z)") }

    Box(modifier = modifier.fillMaxWidth().padding(0.dp)) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().padding(0.dp).height(56.dp),
            shape = RectangleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Center the content horizontally
                verticalArrangement = Arrangement.Center,  // Center the content vertically
                modifier = Modifier.fillMaxWidth().padding(0.dp)  // Ensure it fills the available width and remove padding
            ) {
                Text(
                    text = "Ordenar",
                    fontSize = 14.sp,  // Adjusted font size for better visibility
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(top = 12.dp)
                )

                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Ordenar",
                    modifier = Modifier
                        .size(25.dp) // Size of the icon itself
                )

            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val sortOptions = listOf(
                "Apellido (A-Z)",
                "Apellido (Z-A)",
                "Nombre (A-Z)",
                "Nombre (Z-A)"
            )

            sortOptions.forEach { sortOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sortOption,
                            fontWeight = if (sortOption == selectedSort) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        selectedSort = sortOption
                        expanded = false
                        onSortSelected(sortOption)
                    }
                )
            }
        }
    }
}

@Composable
fun AlumnoList(viewModel: AlumnoManagementViewModel, paddingValues: PaddingValues) {
    val cursos by viewModel.filteredCursos.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val etapaOrder = listOf("INFANTIL", "CICLO_INICIAL", "CICLO_MEDIO", "CICLO_SUPERIOR", "PRIMARIA", "ESO", "BACHILLERATO")
        val sortedCursos = cursos.sortedBy { etapaOrder.indexOf(it.etapa) }
        sortedCursos.forEachIndexed { index, curso ->
            item {
                Text(
                    text = getEtapaName(curso.etapa),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            curso.clases?.forEach { clase ->
                item {
                    Text(
                        text = "${curso.nombre} ${clase.nombre}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(clase.alumnos) { alumno ->
                    AlumnoItem(alumno = alumno, curso = curso, clase = clase.nombre, onClick = {
                        val intent = Intent(context, EditAlumnoActivity::class.java)
                        intent.putExtra("ALUMNO_ID", alumno.alumnoId)
                        context.startActivity(intent)
                    })
                }
            }
            if (index < sortedCursos.size - 1) {
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AlumnoItem(alumno: Alumno, curso: Curso, clase: String, onClick: () -> Unit) {
    val backgroundColor = when {
        alumno.alergias.any { it.severidad == "Alergia Grave" } -> Color(0xFFFFCDD2)
        alumno.alergias.any { it.severidad == "Alergia" } -> Color(0xFFFFD699)
        alumno.alergias.any { it.severidad == "Intolerancia" } -> Color(0xFFFFF9C4)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${alumno.apellido}, ${alumno.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                /*Text(
                    text = "Clase: ${clase}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp
                )*/
            }
            if (alumno.alergias.isNotEmpty()) {
                Text(
                    text = "Alergias: ${alumno.alergias.joinToString(", ") { it.nombre }}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "Alergias: -",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp
                )
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
