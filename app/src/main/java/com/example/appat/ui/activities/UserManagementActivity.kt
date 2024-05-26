package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.domain.entities.Usuario
import com.example.appat.ui.viewmodel.UserManagementViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserManagementActivity : ComponentActivity() {
    private val userManagementViewModel: UserManagementViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
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
                    Scaffold(
                        modifier = Modifier.padding(paddingValues),
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    startActivity(Intent(this, CrearUsuarioActivity::class.java))
                                },
                                containerColor = colorResource(id = R.color.accent)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Agregar Usuario")
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = colorResource(id = R.color.light_primary)
                        ) {
                            UserList(userManagementViewModel, innerPadding)
                        }
                    }
                }
            )
        }

        // Cargar los usuarios del centro escolar del administrador
        centroEscolarId?.let { userManagementViewModel.obtenerUsuariosPorCentro(it, token) }
    }
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)

        // Volver a cargar los usuarios al volver a la actividad
        centroEscolarId?.let { userManagementViewModel.obtenerUsuariosPorCentro(it, token) }
    }
}

@Composable
fun UserList(viewModel: UserManagementViewModel, paddingValues: PaddingValues) {
    val users by viewModel.users.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val sortedUsers = users.sortedBy { it.rol.rol }
        items(sortedUsers) { user ->
            UserItem(user = user, onClick = {
                // Navegar a la vista de edición del usuario
                val intent = Intent(context, EditUserActivity::class.java)
                intent.putExtra("USER_ID", user.userId)
                context.startActivity(intent)
            })
        }
    }
}

@Composable
fun UserItem(user: Usuario, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${user.nombre.nombre} ${user.apellido1.apellido}", style = MaterialTheme.typography.bodyMedium)
            Text(text = user.correo.correo, style = MaterialTheme.typography.bodyMedium)
            Text(text = user.rol.rol, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
