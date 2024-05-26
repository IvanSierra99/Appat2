package com.example.appat.di
// appModule.kt
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
import com.example.appat.data.repositories.*
import com.example.appat.ui.viewmodel.*
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
    single<AlumnoRepository> { AlumnoRepositoryImpl(apiService = get()) }
    single<AsistenciaRepository> { AsistenciaRepositoryImpl(apiService = get())}

    factory<CrearUsuarioUseCase> { CrearUsuarioUseCaseImpl(usuarioRepository = get(), cursoRepository = get()) }
    factory<IniciarSesionUseCase> { IniciarSesionUseCaseImpl(usuarioRepository = get()) }
    factory<ModificarUsuarioUseCase> { ModificarUsuarioUseCaseImpl(usuarioRepository = get(), cursoRepository = get()) }
    factory<EliminarUsuarioUseCase> { EliminarUsuarioUseCaseImpl(usuarioRepository = get()) }
    factory<CrearClaseUseCase> { CrearClaseUseCaseImpl(claseRepository = get(), cursoRepository = get()) }
    factory<CrearCursoUseCase> { CrearCursoUseCaseImpl(cursoRepository = get()) }
    factory<RegistrarAlumnoUseCase> { RegistrarAlumnoUseCaseImpl(alumnoRepository = get(), claseRepository = get(), cursoRepository = get()) }
    factory<ModificarAlumnoUseCase> { ModificarAlumnoUseCaseImpl(cursoRepository = get(), alumnoRepository = get()) }
    factory<EliminarAlumnoUseCase> { EliminarAlumnoUseCaseImpl(alumnoRepository = get()) }
    factory<RegistrarAsistenciaAlumnoUseCase> { RegistrarAsistenciaAlumnoUseCaseImpl(cursoRepository = get(), asistenciaRepository = get())}
    factory<ConsultarGenerarInformeAsistenciaUseCase> { ConsultarGenerarInformeAsistenciaUseCaseImpl(asistenciaRepository = get(), cursoRepository = get()) }

    viewModel { CrearUsuarioViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UserManagementViewModel(get()) }
    viewModel { EditUserViewModel(get()) }
    viewModel { EliminarUsuarioViewModel(get()) }
    viewModel { CrearClaseViewModel(get()) }
    viewModel { CrearCursoViewModel(get()) }
    viewModel { RegistrarAlumnoViewModel(get()) }
    viewModel { AlumnoManagementViewModel(get()) }
    viewModel { ModificarAlumnoViewModel(get(), get()) }
    viewModel { EliminarAlumnoViewModel(get()) }
    viewModel { AsistenciaManagementViewModel(get())}
    viewModel { InformeAsistenciaViewModel(get()) }
}

