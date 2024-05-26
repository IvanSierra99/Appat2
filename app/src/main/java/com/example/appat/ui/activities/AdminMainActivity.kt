package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appat.R

class AdminMainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val rol = sharedPreferences.getString("rol", "")

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            MyAppTopBar(
                onMenuClick = { },
                schoolName = nombreCentro,
                drawerState = drawerState,
                drawerContent = { DefaultDrawerContent(this, drawerState, rol) },
                content = { paddingValues ->
                    AdminScreen(paddingValues)
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminScreen(paddingValues: PaddingValues) {
        val context = LocalContext.current as? Activity

        Scaffold(
            modifier = Modifier.padding(paddingValues),
            content = { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    color = colorResource(id = R.color.light_primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AdminButton(
                                    label = "Gestión de Usuarios",
                                    onClick = { context?.startActivity(Intent(context, UserManagementActivity::class.java)) },
                                    imageResId = R.drawable.monitor // Cambia esto a la imagen adecuada
                                )
                                AdminButton(
                                    label = "Gestión de Alumnos",
                                    onClick = { context?.startActivity(Intent(context, AlumnoManagementActivity::class.java)) },
                                    imageResId = R.drawable.alumnos // Cambia esto a la imagen adecuada
                                )
                                AdminButton(
                                    label = "Informes",
                                    onClick = { context?.startActivity(Intent(context, InformeAsistenciaActivity::class.java))},
                                    imageResId = R.drawable.informes // Cambia esto a la imagen adecuada
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AdminButton(
                                    label = "Gestión de Clases",
                                    onClick = { context?.startActivity(Intent(context, ClaseManagementActivity::class.java)) },
                                    imageResId = R.drawable.aula // Cambia esto a la imagen adecuada
                                )
                                AdminButton(
                                    label = "Gestión de Asistencia",
                                    onClick = { context?.startActivity(Intent(context, AsistenciaManagementActivity::class.java)) },
                                    imageResId = R.drawable.asistencia // Cambia esto a la imagen adecuada
                                )
                                AdminButton(
                                    label = "Menú del mes",
                                    onClick = { /* Aquí puedes añadir la funcionalidad futura */ },
                                    imageResId = R.drawable.menu // Cambia esto a la imagen adecuada
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun AdminButton(label: String, onClick: () -> Unit, imageResId: Int) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(16.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            elevation = CardDefaults.cardElevation(8.dp), // Elevación para proporcionar la sombra
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorResource(id = R.color.light_primary))
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(8.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.White.copy(alpha = 0.8f))
                            .width(200.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colorResource(id = R.color.primary_text),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
