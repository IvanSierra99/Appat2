package com.example.appat.data.local

import com.example.appat.domain.entities.Usuario
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {
    @GET("usuarios/")
    fun getAllUsuarios(): Call<List<Usuario>>

    @POST("usuarios/")
    suspend fun createUsuario(@Body usuario: Usuario): Response<Usuario>

    @PUT("usuarios/{id}/")
    fun updateUsuario(@Path("id") id: Int, @Body usuario: Usuario): Call<Usuario>

    @DELETE("usuarios/{id}/")
    fun deleteUsuario(@Path("id") id: Int): Call<Unit>
}
