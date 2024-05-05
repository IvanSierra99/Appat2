package com.example.appat.data.local

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {
    @GET("usuarios/")
    fun getAllUsuarios(): Call<List<UsuarioData>>

    @POST("usuarios/")
    suspend fun createUsuario(@Body usuario: UsuarioData): Response<UsuarioData>

    @PUT("usuarios/{id}/")
    fun updateUsuario(@Path("id") id: Int, @Body usuario: UsuarioData): Call<UsuarioData>

    @DELETE("usuarios/{id}/")
    fun deleteUsuario(@Path("id") id: Int): Call<Unit>
}
