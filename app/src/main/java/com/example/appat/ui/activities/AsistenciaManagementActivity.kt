package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appat.R
import com.example.appat.domain.entities.Alumno
import com.example.appat.domain.entities.Curso
import com.example.appat.ui.viewmodel.AsistenciaManagementViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate

class AsistenciaManagementActivity : ComponentActivity() {
    private val asistenciaManagementViewModel: AsistenciaManagementViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)
        val cursosSet = sharedPreferences.getStringSet("cursos", emptySet()) // Obtener la lista de cursos

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            MyAppTopBar(
                onMenuClick = { },
                schoolName = nombreCentro,
                drawerState = drawerState,
                drawerContent = { DefaultDrawerContent(this, drawerState) },
                content = { paddingValues ->
                    Scaffold(
                        modifier = Modifier.padding(paddingValues),
                        topBar = {
                            Column(modifier = Modifier.background(color = colorResource(id = R.color.accent).copy(0.6f))) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, end = 8.dp, start = 8.dp)
                                        .background(color = colorResource(id = R.color.transparent)),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AttendanceSearchBar(
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .height(56.dp)
                                            .padding(end = 4.dp), onSearch = { query ->
                                            asistenciaManagementViewModel.updateSearchQuery(query)
                                        })
                                    AttendanceSortDropdownMenu(
                                        modifier = Modifier
                                            .weight(0.2f)
                                            .height(56.dp),
                                        onSortSelected = { sortOrder ->
                                            asistenciaManagementViewModel.updateSortOrder(sortOrder)
                                        })
                                }
                                FilterLegend(asistenciaManagementViewModel)
                                HorizontalDivider(
                                    modifier = Modifier,
                                    thickness = 2.dp,
                                    color = colorResource(id = R.color.primary_text)
                                )
                            }
                        },
                        content = { innerPadding ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                color = colorResource(id = R.color.light_primary)
                            ) {
                                AlumnoList(asistenciaManagementViewModel, paddingValues, token)
                            }
                        }
                    )
                }
            )
        }

        // Cargar los cursos y la asistencia del centro escolar
        centroEscolarId?.let {
            asistenciaManagementViewModel.obtenerCursosPorCentro(it, token, cursosSet?.toList())
            asistenciaManagementViewModel.obtenerAsistencia(LocalDate.now(), it, token)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)
        val cursosSet = sharedPreferences.getStringSet("cursos", emptySet())

        // Volver a cargar los cursos y la asistencia al volver a la actividad
        centroEscolarId?.let {
            asistenciaManagementViewModel.obtenerCursosPorCentro(it, token, cursosSet?.toList())
            asistenciaManagementViewModel.obtenerAsistencia(LocalDate.now(), it, token)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSearchBar(modifier: Modifier = Modifier, onSearch: (String) -> Unit) {
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
            .background(color = colorResource(id = R.color.transparent)),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(id = R.color.white),
        )
    )
}

@Composable
fun AttendanceSortDropdownMenu(modifier: Modifier = Modifier, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Apellido (A-Z)") }

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(0.dp)) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = colorResource(id = R.color.accent).copy(0.8f)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Center the content horizontally
                verticalArrangement = Arrangement.Center,  // Center the content vertically
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)  // Ensure it fills the available width and remove padding
            ) {
                Text(
                    text = "Ordenar",
                    color = colorResource(id = R.color.primary_text),
                    fontSize = 14.sp,  // Adjusted font size for better visibility
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(top = 12.dp)
                )

                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Ordenar",
                    tint = colorResource(id = R.color.primary_text),
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
fun AlumnoList(viewModel: AsistenciaManagementViewModel, paddingValues: PaddingValues, token: String?) {
    val cursos by viewModel.filteredCursos.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
        ),
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
                    AlumnoItem(
                        alumno = alumno,
                        curso = curso,
                        clase = clase.nombre,
                        token = token,
                        viewModel = viewModel
                    )
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
fun AlumnoItem(alumno: Alumno, curso: Curso, clase: String, token: String?, viewModel: AsistenciaManagementViewModel) {
    val backgroundColor = when {
        alumno.alergias.any { it.severidad == "Alergia Grave" } -> Color(0xFFFFCDD2)
        alumno.alergias.any { it.severidad == "Alergia" } -> Color(0xFFFFD699)
        alumno.alergias.any { it.severidad == "Intolerancia" } -> Color(0xFFFFF9C4)
        else -> Color.White
    }

    val asistencia by viewModel.currentAsistencia.collectAsState()
    val isChecked = remember { mutableStateOf(asistencia?.habitualIds?.contains(alumno.alumnoId) == true) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                showDialog = true
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "${alumno.apellido}, ${alumno.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
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
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                        val updatedAsistencia = asistencia?.let { currentAsistencia ->
                            if (it) {
                                currentAsistencia.copy(habitualIds = currentAsistencia.habitualIds.toMutableList().apply { add(alumno.alumnoId) })
                            } else {
                                currentAsistencia.copy(habitualIds = currentAsistencia.habitualIds.toMutableList().apply { remove(alumno.alumnoId) })
                            }
                        }
                        if (updatedAsistencia != null) {
                            viewModel.registrarAsistencia(updatedAsistencia, token)
                        }
                    }
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "${alumno.apellido}, ${alumno.nombre}")
            },
            text = {
                Column {
                    Text(text = "Alergias: ${alumno.alergias.joinToString(", ") { it.nombre }}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun FilterLegend(viewModel: AsistenciaManagementViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { viewModel.clearAlergiaFilter() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .weight(0.25f)
                .padding(2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "Todos",
                color = colorResource(id = R.color.primary_text),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )
        }
        Button(
            onClick = { viewModel.filterByAlergia("Intolerancia") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF9C4)),
            modifier = Modifier
                .weight(0.25f)
                .padding(2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "Intol.",
                color = colorResource(id = R.color.primary_text),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )
        }
        Button(
            onClick = { viewModel.filterByAlergia("Alergia") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD699)),
            modifier = Modifier
                .weight(0.25f)
                .padding(2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "Alergia",
                color = colorResource(id = R.color.primary_text),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )
        }
        Button(
            onClick = { viewModel.filterByAlergia("Alergia Grave") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2)),
            modifier = Modifier
                .weight(0.25f)
                .padding(2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "A.Grave",
                color = colorResource(id = R.color.primary_text),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )
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
