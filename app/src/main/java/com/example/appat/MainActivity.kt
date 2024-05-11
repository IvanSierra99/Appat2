package com.example.appat

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appat.di.appModule
import com.example.appat.ui.theme.AppatTheme
import org.koin.core.context.GlobalContext.startKoin
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appat.ui.activities.CrearUsuarioActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.android.ext.koin.androidFileProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import com.example.appat.ui.activities.LoginActivity
import kotlinx.coroutines.delay

class Appat : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@Appat)
            androidFileProperties()
            modules(appModule)
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppatTheme {
                SplashScreen {
                    finish() // Asegúrate de llamar finish() aquí para cerrar la actividad después de navegar
                }
            }
        }
    }
}
@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.logo2), contentDescription = "Logo")
        LaunchedEffect(key1 = true) {
            delay(5000)
            // Usa el contexto obtenido de LocalContext.current
            context.startActivity(Intent(context, LoginActivity::class.java))
            onNavigateToLogin() // Llama a esta función para cerrar la actividad después de iniciar la otra
        }
    }
}
@Composable
fun MainScreen() {
    // Contexto local para manejar operaciones relacionadas con la UI, como iniciar una Activity
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Intent para iniciar CrearUsuarioActivity
                    context.startActivity(Intent(context, CrearUsuarioActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Usuario")
            }
        }
    }
}