package com.example.appat.di
// appModule.kt
import CrearUsuarioViewModel
import com.example.appat.data.local.ApiService
import org.koin.dsl.module
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.data.repositories.UsuarioRepositoryImpl
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import com.example.appat.domain.usecases.EliminarUsuarioUseCase
import com.example.appat.domain.usecases.EliminarUsuarioUseCaseImpl
import io.ktor.client.HttpClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.appat.domain.usecases.IniciarSesionUseCase
import com.example.appat.domain.usecases.IniciarSesionUseCaseImpl
import com.example.appat.domain.usecases.ModificarUsuarioUseCase
import com.example.appat.domain.usecases.ModificarUsuarioUseCaseImpl
import com.example.appat.ui.viewmodel.EditUserViewModel
import com.example.appat.ui.viewmodel.EliminarUsuarioViewModel
import com.example.appat.ui.viewmodel.LoginViewModel
import com.example.appat.ui.viewmodel.UserManagementViewModel
import org.koin.android.ext.koin.androidContext

val appModule = module {


    // Usar RetrofitClient para proporcionar UsuarioApiService
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    // Configura Json según tus necesidades
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            engine {
                // Configurar el timeout de conexión
                endpoint {
                    connectTimeout = 5000 // Tiempo en milisegundos
                    requestTimeout = 5000 // Tiempo en milisegundos
                }
            }
        }
    }

    single<ApiService> { ApiService(get()) }

    single<UsuarioRepository> { UsuarioRepositoryImpl(apiService = get(), context = androidContext()) }

    factory<CrearUsuarioUseCase> { CrearUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<IniciarSesionUseCase> { IniciarSesionUseCaseImpl(usuarioRepository = get()) }
    factory<ModificarUsuarioUseCase> { ModificarUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<EliminarUsuarioUseCase> { EliminarUsuarioUseCaseImpl(usuarioRepository = get()) }

    viewModel { CrearUsuarioViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UserManagementViewModel(get()) }
    viewModel { EditUserViewModel(get()) }
    viewModel { EliminarUsuarioViewModel(get()) }

}

