package com.example.appat.di
// appModule.kt
import CrearUsuarioViewModel
import com.example.appat.data.local.RetrofitClient
import org.koin.dsl.module
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.data.repositories.UsuarioRepositoryImpl
import com.example.appat.domain.usecases.CrearUsuarioUseCase
import com.example.appat.domain.usecases.CrearUsuarioUseCaseImpl
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    // Usar RetrofitClient para proporcionar UsuarioApiService
    single { RetrofitClient.instance }

    // Repositorio
    single<UsuarioRepository> { UsuarioRepositoryImpl(apiService = get()) }

    // Casos de uso
    factory<CrearUsuarioUseCase> { CrearUsuarioUseCaseImpl(usuarioRepository = get()) }

    // ViewModel
    viewModel { CrearUsuarioViewModel(crearUsuarioUseCase = get()) }
}

