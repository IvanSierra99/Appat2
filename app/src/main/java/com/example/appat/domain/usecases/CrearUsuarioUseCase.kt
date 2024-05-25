package com.example.appat.domain.usecases

import com.example.appat.core.AppResult
import com.example.appat.core.UseCaseSuspend
import com.example.appat.core.appRunCatching
import com.example.appat.data.repositories.ClaseRepository
import com.example.appat.data.repositories.CursoRepository
import com.example.appat.domain.entities.*
import com.example.appat.data.repositories.UsuarioRepository

interface CrearUsuarioUseCase: UseCaseSuspend<CrearUsuariInput, AppResult<Usuario, Throwable>> {
    suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable>
}

data class CrearUsuariInput(
    val nombre: String,
    val apellido1: String,
    val apellido2: String? = null, // Hacer apellido2 opcional
    val correo: String,
    val rol: String,
    val centroEscolarId: String?,
    val token: String?,
    val cursos: List<String> = emptyList()
)

class CrearUsuarioUseCaseImpl(
    private val usuarioRepository: UsuarioRepository,
    private val cursoRepository: CursoRepository
) : CrearUsuarioUseCase {

    override suspend fun invoke(params: CrearUsuariInput): AppResult<Usuario, Throwable> {
        val usuario = params.toUser() // Utilizamos la extensión para convertir el input a Usuario
        return appRunCatching {
            usuarioRepository.createUser(usuario)
        }
    }

    override suspend fun getCursosByCentroEscolar(centroEscolarId: String, token: String?): AppResult<List<Curso>, Throwable> {
        return appRunCatching {
            cursoRepository.getCursosByCentroEscolar(centroEscolarId, token)
        }
    }

    private fun CrearUsuariInput.toUser() = Usuario(
        nombre = Nombre(this.nombre),
        apellido1 = Apellido(this.apellido1),
        correo = Correo(this.correo),
        rol = Rol(this.rol),
        centroEscolar = centroEscolarId?.let { CentroEscolar(it, "", "", "", "") },
        cursos = this.cursos,
        token = token
    )
}
