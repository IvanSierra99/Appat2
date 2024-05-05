package com.example.appat.data.repositories

import com.example.appat.data.local.UsuarioApiService
import com.example.appat.data.local.UsuarioData
import com.example.appat.domain.entities.Usuario

interface UsuarioRepository {
    suspend fun createUser(usuario: UsuarioData): UsuarioData
}

class UsuarioRepositoryImpl(private val apiService: UsuarioApiService): UsuarioRepository {
    override suspend fun createUser(usuario: UsuarioData): UsuarioData {
        val response = apiService.createUsuario(usuario)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al crear usuario: ${response.message()}")
        }
    }
}