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

    @OptIn(InternalAPI::class)
    suspend fun login(username: String, password: String): LoginResponse {
        val loginUrl = "$baseUrl/api-token-auth/"
        try {
            val response: HttpResponse = client.post(loginUrl) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }
            if (response.status == HttpStatusCode.OK) {
                return Json { ignoreUnknownKeys = true }.decodeFromString<LoginResponse>(response.bodyAsText())
            } else {
                println("Login failed with status: ${response.status} and body: ${response.bodyAsText()}")
                throw Exception("Login failed: ${response.status.description} - ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Exception during login: ${e.message}")
            throw e
        }
    }
}
