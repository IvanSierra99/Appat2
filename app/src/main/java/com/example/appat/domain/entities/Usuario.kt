package com.example.appat.domain.entities

import java.util.UUID
import java.util.regex.Pattern

//Todo: añadir horario como medida de seguridad, configurar nombre de usuario unico
data class Usuario(
    val userId: String = UUID.randomUUID().toString(),
    val nombre: Nombre,
    val apellido1: Apellido,
    val apellido2: Apellido? = null, // Hacer apellido2 opcional
    val username: Username = Username.fromNameAndSurname(nombre.nombre, apellido1.apellido),
    val correo: Correo,
    val contraseña: Contraseña = Contraseña.generarAleatoria(),
    val rol: Rol
)

data class Nombre(val nombre: String){
    init {
        require(nombre.isNotEmpty()){
            "No puede estar vacío"
        }
    }
}

data class Apellido(val apellido: String) {
    init {
        require(apellido.isNotEmpty()) {
            "No puede estar vacío"
        }
    }
}

data class Username private constructor(val username: String) {
    companion object {
        // Genera un username a partir del nombre y el primer apellido
        fun fromNameAndSurname(nombre: String, apellido: String): Username {
            val base = nombre.take(2).lowercase() + apellido.take(2).lowercase()
            val numeroAleatorio = (100..999).random()
            return Username(base + numeroAleatorio.toString())
        }

        // Crea un Username desde un string completo
        fun fromCompleteString(username: String): Username {
            return Username(username)
        }
    }
}

data class Correo(val correo: String) {

    init{
        require(correo.isNotEmpty()){
            "No puede estar vacío"
        }
        require(isValidEmail(correo)) {
            "El correo no tiene un formato válido"
        }
    }

    companion object {
        private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        )

        private fun isValidEmail(email: String): Boolean {
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
        }
    }
}

data class Contraseña private constructor(val contraseña: String) {
    init{
        require(contraseña.isNotEmpty()){
            "No puede estar vacío"
        }
        require(Contraseña.esContraseñaSegura(contraseña)) {
            "La contraseña no es segura (mínimo 8 caracteres, mayusculas, mínusculas y números)"
        }
    }
    companion object {
        fun generarAleatoria(): Contraseña {
            val longitud = 12 // Puedes ajustar la longitud de la contraseña según tus necesidades
            val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+"
            val contraseñaAleatoria = (1..longitud)
                .map { caracteres.random() }
                .joinToString("")
            return Contraseña(contraseñaAleatoria)
        }

        fun crearNueva(contraseña: String): Contraseña {
            require(esContraseñaSegura(contraseña)) { "La contraseña no cumple con los requisitos de seguridad" }
            return Contraseña(contraseña)
        }

        private fun esContraseñaSegura(contraseña: String): Boolean {
            // Puedes personalizar estas condiciones según tus requisitos de seguridad
            val longitudMínima = 8
            val contieneMayúscula = contraseña.any { it.isUpperCase() }
            val contieneMinúscula = contraseña.any { it.isLowerCase() }
            val contieneNúmero = contraseña.any { it.isDigit() }
            return contraseña.length >= longitudMínima && contieneMayúscula && contieneMinúscula && contieneNúmero
        }
    }
}

data class Rol(val rol: String) {

    init {
        require(rol.isNotEmpty()) { "El rol no puede estar vacío" }
        require(rol.trim().uppercase() in listOf("ADMINISTRADOR", "COORDINADOR", "MONITOR")) {
            "Ha de ser un rol existente (administrador, coordinador, monitor)"
        }
    }
}