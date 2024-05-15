package com.example.appat.data.local
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json

class ApiService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.128:8000"
    private val json = Json {
        ignoreUnknownKeys = true // Ignora las claves desconocidas en el JSON recibido
        isLenient = true // Permite comillas y comentarios más flexibles en JSON
    }

    suspend fun createUsuario(usuario: UsuarioDTO, token: String?): UsuarioDTO {
        val response: HttpResponse = client.post("$baseUrl/usuarios/") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
            if (token != null) {
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
            }
        }
        return json.decodeFromString(response.body<String>())
    }

    suspend fun getUsersByCentroEscolar(centroEscolarId: String, token: String?): List<UsuarioDTO> {
        val response: HttpResponse = client.get("$baseUrl/usuarios/?centro_escolar_id=$centroEscolarId") {
            if (token != null) {
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
            }
        }
        return json.decodeFromString(response.bodyAsText())
    }

    @OptIn(InternalAPI::class)
    suspend fun updateUsuario(id: String, usuario: UsuarioDTO, token: String?): UsuarioDTO {
        val response: HttpResponse = client.patch("$baseUrl/usuarios/$id/") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
            if (token != null) {
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
            }
        }
        return json.decodeFromString(response.body<String>())
    }

    suspend fun getUserById(userId: String, token: String?): UsuarioDTO {
        val response: HttpResponse = client.get("$baseUrl/usuarios/$userId/") {
            if (token != null) {
                headers {
                    append(HttpHeaders.Authorization, "Token $token")
                }
            }
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun login(username: String, password: String): LoginResponse {
        val loginUrl = "$baseUrl/api-token-auth/"
        Log.d("LoginAPI1", "Attempting to login with username: $username")
        try {
            val response: HttpResponse = client.post(loginUrl) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }
            val responseBody = response.bodyAsText()
            Log.d("LoginAPI", "Response status: ${response.status}")
            Log.d("LoginAPI", "Response body: $responseBody")
            if (response.status == HttpStatusCode.OK) {
                return Json { ignoreUnknownKeys = true }.decodeFromString<LoginResponse>(responseBody)
            } else {
                Log.d("LoginAPI", "Attempting to login with username: $username")
                println("Login failed with status: ${response.status} and body: ${response.bodyAsText()}")
                throw Exception("Login failed: ${response.status.description} - ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            Log.d("LoginAPI", "Attempting to login with username: $username")
            println("Exception during login: ${e.message}")
            throw e
        }
    }
}
