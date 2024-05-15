package com.example.appat.domain.usecases

import android.util.Log
import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.core.asSuccess
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.domain.entities.Usuario

interface IniciarSesionUseCase: UseCaseSuspend<IniciarSesionInput, AppResult<Usuario, Throwable>>

data class IniciarSesionInput(
    val username: String,
    val password: String
)

class IniciarSesionUseCaseImpl(
    private val usuarioRepository: UsuarioRepository
) : IniciarSesionUseCase {
    override suspend fun invoke(input: IniciarSesionInput): AppResult<Usuario, Throwable> {
        Log.d("LoginUseCase", "Attempting to login with username:")
        return appRunCatching {
            usuarioRepository.login(input.username, input.password)
        }
    }
}

