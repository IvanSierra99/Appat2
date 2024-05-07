package com.example.appat.data.repositories

import com.example.appat.data.local.UsuarioApiService
import com.example.appat.domain.entities.Usuario

interface UsuarioRepository {
    suspend fun createUser(usuario: Usuario): Usuario
}

class UsuarioRepositoryImpl(private val apiService: UsuarioApiService): UsuarioRepository {
    override suspend fun createUser(usuario: Usuario): Usuario {
        val response = apiService.createUsuario(usuario)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al crear usuario: ${response.message()}")
        }
    }
}