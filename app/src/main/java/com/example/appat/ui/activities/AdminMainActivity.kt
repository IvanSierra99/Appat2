package com.example.appat.ui.activities

import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.appat.R


class AdminMainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        setContent {
            AdminScreen(nombreCentro)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminScreen(nombreCentro: String?) {
        // Contexto local para manejar operaciones relacionadas con la UI, como iniciar una Activity
        val context = LocalContext.current as? Activity
        Scaffold(
            topBar = {
                MyAppTopBar(
                    onMenuClick = {
                        // Acciones al hacer clic en el botón del menú de navegación
                    },
                    schoolName = nombreCentro
                )
            },
            content = {paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = colorResource(id = R.color.light_primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                context?.startActivity(Intent(context, CrearUsuarioActivity::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Crear Usuario")
                        }
                    }
                }
            }
        )
    }
}