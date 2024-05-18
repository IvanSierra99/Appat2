package com.example.appat.di
// appModule.kt
import CrearUsuarioViewModel
import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.appat.data.local.ApiService
import com.example.appat.domain.usecases.*
import com.example.appat.ui.viewmodel.*
import com.example.appat.data.repositories.*
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
    single<ClaseRepository> { ClaseRepositoryImpl(apiService = get()) }
    single<CursoRepository> { CursoRepositoryImpl(apiService = get()) }

    factory<CrearUsuarioUseCase> { CrearUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<IniciarSesionUseCase> { IniciarSesionUseCaseImpl(usuarioRepository = get()) }
    factory<ModificarUsuarioUseCase> { ModificarUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<EliminarUsuarioUseCase> { EliminarUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<CrearClaseUseCase> { CrearClaseUseCaseImpl(claseRepository = get(), cursoRepository = get()) }
    factory<CrearCursoUseCase> { CrearCursoUseCaseImpl(cursoRepository = get()) }

    viewModel { CrearUsuarioViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UserManagementViewModel(get()) }
    viewModel { EditUserViewModel(get()) }
    viewModel { EliminarUsuarioViewModel(get()) }
    viewModel { CrearClaseViewModel(get()) }
    viewModel { CrearCursoViewModel(get()) }

}

