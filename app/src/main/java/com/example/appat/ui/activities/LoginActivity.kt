package com.example.appat.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appat.R
import com.example.appat.ui.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Passing this@LoginActivity to the LoginScreen
            LoginScreen(this@LoginActivity, loginViewModel)

        }
    }

}

@Composable
fun LoginScreen(activity: LoginActivity, loginViewModel: LoginViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val loginState by loginViewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // To close the keyboard when tapping outside
    val focusManager = LocalFocusManager.current

    var hasAttemptedLogin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .clickable(onClick = { focusManager.clearFocus() }, indication = null, interactionSource = remember { MutableInteractionSource() })
            .wrapContentSize(Alignment.Center),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(300.dp)  // Tamaño del icono, ajusta según necesites
                .align(Alignment.CenterHorizontally)  // Centrar horizontalmente en la columna
                .padding(start = 16.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),

        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                hasAttemptedLogin = true  // Marcar que se ha intentado un login
                loginViewModel.login(username, password)
                      },
            modifier = Modifier.padding(top = 16.dp),
            enabled = username.isNotEmpty() && password.isNotEmpty()
        ) {
            Text("Login")
        }
        SnackbarHost(hostState = snackbarHostState)
    }

    LaunchedEffect(loginState) {
        if (hasAttemptedLogin) {  // Solo reacciona si se ha intentado el login
            try {
                loginState?.let { user ->
                    saveUserData(
                        activity,
                        user.token,
                        user.username.username,
                        user.centroEscolar?.nombre,
                        user.centroEscolar?.centroId )
                    if (user.rol.rol.trim().uppercase() == "ADMINISTRADOR") {
                        val intent = Intent(activity, AdminMainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    } else {
                        snackbarHostState.showSnackbar(
                            "Access not implemented for role: ${user.rol}",
                            duration = SnackbarDuration.Long
                        )
                    }
                } ?: snackbarHostState.showSnackbar(
                    "Login failed. Please try again.",
                    duration = SnackbarDuration.Short
                )
            } catch (e: Error) {
                snackbarHostState.showSnackbar(
                    "Login failed. Please try again.",
                    duration = SnackbarDuration.Short
                )
            }

        }
    }

}

fun saveUserData(context: Context, token: String?, username: String, nombreCentro: String?, centroId: String?) {
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("token", token)
        putString("username", username)
        putString("nombreCentro", nombreCentro)
        putString("centroEscolarId", centroId)
        apply()
    }
}
