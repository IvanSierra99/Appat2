package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.UsuarioRepository

interface EliminarUsuarioUseCase : UseCaseSuspend<EliminarUsuarioInput, AppResult<Unit, Throwable>>

data class EliminarUsuarioInput(val idUsuario: String, val token: String?)

class EliminarUsuarioUseCaseImpl(
    private val usuarioRepository: UsuarioRepository
) : EliminarUsuarioUseCase {
    override suspend fun invoke(input: EliminarUsuarioInput): AppResult<Unit, Throwable> {
        return appRunCatching {
            usuarioRepository.deleteUser(input.idUsuario, input.token)
        }
    }
}
