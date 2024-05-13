package com.example.appat

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.appat.di.appModule
import com.example.appat.ui.activities.LoginActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

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

        // Inicia LoginActivity directamente
        startActivity(Intent(this, LoginActivity::class.java))
        // Finaliza MainActivity para que el usuario no pueda volver a ella presionando el botón Atrás
        finish()
    }
}
