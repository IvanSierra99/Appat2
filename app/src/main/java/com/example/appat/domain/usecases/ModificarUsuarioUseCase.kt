package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.data.repositories.UsuarioRepository
import com.example.appat.domain.entities.Curso
import com.example.appat.domain.entities.Usuario

interface ModificarUsuarioUseCase: UseCaseSuspend<ModificarUsuarioInput, AppResult<Usuario, Throwable>> {
    suspend fun obtenerUsuariosPorCentro(centroEscolarId: String, token: String?): AppResult<List<Usuario>, Throwable>
    suspend fun obtenerUsuarioPorId(userId: String, token: String?): AppResult<Usuario, Throwable>
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable>
}

data class ModificarUsuarioInput(
    val usuario: Usuario
)

class ModificarUsuarioUseCaseImpl(
    private val usuarioRepository: UsuarioRepository,
    private val cursoRepository: CursoRepository
) : ModificarUsuarioUseCase {
    override suspend fun invoke(params: ModificarUsuarioInput): AppResult<Usuario, Throwable> {
        return appRunCatching {
            usuarioRepository.updateUser(params.usuario)
        }
    }

    override suspend fun obtenerUsuariosPorCentro(centroEscolarId: String, token: String?): AppResult<List<Usuario>, Throwable> {
        return appRunCatching {
            usuarioRepository.getUsersByCentroEscolar(centroEscolarId, token)
        }
    }

    override suspend fun obtenerUsuarioPorId(userId: String, token: String?): AppResult<Usuario, Throwable> {
        return appRunCatching {
            usuarioRepository.getUserById(userId, token)
        }
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable> {
        return appRunCatching {
            cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
        }
    }
}
