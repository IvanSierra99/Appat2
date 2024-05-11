package com.example.appat.data.local
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UsuarioApiService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.141:8000" // URL base
    private val json = Json {
        ignoreUnknownKeys = true // Ignora las claves desconocidas en el JSON recibido
        isLenient = true // Permite comillas y comentarios más flexibles en JSON
    }

    suspend fun getAllUsuarios(): List<UsuarioDTO> {
        val response: HttpResponse = client.get("$baseUrl/usuarios/")
        val responseBody = response.body<String>()
        return json.decodeFromString(responseBody)
    }

    suspend fun createUsuario(usuario: UsuarioDTO): UsuarioDTO {
        val response: HttpResponse = client.post("$baseUrl/usuarios/") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }
        return json.decodeFromString(response.body<String>())
    }

    @OptIn(InternalAPI::class)
    suspend fun updateUsuario(id: Int, usuario: UsuarioDTO): UsuarioDTO {
        val response: HttpResponse = client.put("$baseUrl/usuarios/$id") {
            contentType(ContentType.Application.Json)
            body = usuario
        }
        return json.decodeFromString(response.body<String>())
    }
}
