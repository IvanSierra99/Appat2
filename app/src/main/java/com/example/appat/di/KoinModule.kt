package com.example.appat.di
// appModule.kt
import CrearUsuarioViewModel
import com.example.appat.data.local.UsuarioApiService
import org.koin.dsl.module
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.data.repositories.UsuarioRepositoryImpl
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import io.ktor.client.HttpClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
        }
    }

    single<UsuarioApiService> { UsuarioApiService(get()) }

    single<UsuarioRepository> { UsuarioRepositoryImpl(apiService = get()) }

    factory<CrearUsuarioUseCase> { CrearUsuarioUseCaseImpl(usuarioRepository = get()) }

    viewModel { CrearUsuarioViewModel(get()) }

}

