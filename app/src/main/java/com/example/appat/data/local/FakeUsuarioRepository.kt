import com.example.appat.domain.entities.Usuario
import com.example.appat.data.repositories.UsuarioRepository
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class FakeUsuarioRepository(private val dataFile: File) : UsuarioRepository {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val users = ConcurrentHashMap<String, Usuario>() // Use a ConcurrentHashMap for thread-safety

    override suspend fun createUser(usuario: Usuario): Usuario {
        val userId = UUID.randomUUID().toString() // Generate a unique ID
        val newUser = usuario.copy(userId = userId) // Add the generated ID
        users[userId] = newUser
        return newUser
    }

    // Add other repository methods as needed (e.g., getUserById, updateUser, deleteUser)
}