package com.example.appat.di

import org.koin.dsl.module
import com.example.appat.domain.usecases.CrearClaseUseCase // Replace with your package structure
import com.example.appat.domain.usecases.CrearClaseUseCaseImpl // Replace with your package structure

val appModule = module {
    // Bind the interface to its implementation
    single { CrearClaseUseCaseImpl() as CrearClaseUseCase }
}
