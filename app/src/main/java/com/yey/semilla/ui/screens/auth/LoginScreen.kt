package com.yey.semilla.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay

// Pantalla de Login: conecta la UI con el UserViewModel para manejar el estado del login.
@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observa cambios en loginSuccess y currentUser automáticamente
    val loginSuccess by userViewModel.loginSuccess.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    var loginError by remember { mutableStateOf<String?>(null) }
    var loginAttemptCount by remember { mutableStateOf(0) }

    // 🚀 Navegar cuando el login funciona
    LaunchedEffect(loginSuccess, currentUser) {
        if (loginSuccess && currentUser != null) {
            loginError = null
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // ❌ Mostrar mensaje de error solo después de intentar loguear
    LaunchedEffect(loginAttemptCount) {
        if (loginAttemptCount == 0) return@LaunchedEffect

        // Espera un poco para que el ViewModel termine el login()
        delay(200)

        if (!loginSuccess || currentUser == null) {
            loginError = "Correo o contraseña incorrectos."
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE0FFFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Iniciar Sesión",
                color = Color(0xFF009688),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(24.dp))

            // Mensaje de error
            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = Color(0xFFFF0000),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    loginError = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    loginError = null
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66BB6A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        loginError = "Completa ambos campos."
                    } else {
                        // Ejecuta el login directamente contra el ViewModel
                        loginError = null
                        userViewModel.login(email, password)
                        loginAttemptCount++   // Marca que se intentó hacer login
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009688),
                    contentColor = Color.White
                ),
                onClick = { navController.navigate(Screen.Register.route) }
            ) {
                Text("Crear una cuenta")
            }
        }
    }
}
