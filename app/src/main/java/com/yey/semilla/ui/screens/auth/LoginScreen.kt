package com.yey.semilla.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay
// El LoginScreen es un Composable (una pieza de UI) que permite al usuario ingresar sus credenciales. Muestra cómo la UI se conecta con el UserViewModel para manejar el estado (éxito/fallo del login) y las acciones (el botón de ingreso).
@Composable
fun LoginScreen(
    navController: NavController,
    onLogin: (String, String) -> Unit,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observa cambios en loginSuccess y currentUser automáticamente
    val loginSuccess by userViewModel.loginSuccess.collectAsState() //Convierte Flow de ViewModel a estado de Compose
    val currentUser by userViewModel.currentUser.collectAsState()

    var loginError by remember { mutableStateOf<String?>(null) }

    //  Navegar cuando el login funciona
    LaunchedEffect(loginSuccess) {
        if (loginSuccess && currentUser != null) {
            loginError = null
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        // 🔥 Mostrar mensaje de error
        if (loginError != null) {
            Text(
                text = loginError!!,
                color = MaterialTheme.colorScheme.error,
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
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    loginError = "Completa ambos campos."
                } else {
                    // Ejecuta el login
                    onLogin(email, password)

                    //  Esperamos al ViewModel para ver si falló
                    // SIN usar LaunchedEffect dentro del botón
                    // Usamos un callback seguro
                    loginError = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        //🔥 Este efecto detecta login fallido
        LaunchedEffect(email, password, loginSuccess) {
            if (!loginSuccess && email.isNotEmpty() && password.isNotEmpty()) {
                // Esperamos un poco para que el ViewModel responda
                delay(150)

                if (!userViewModel.loginSuccess.value) {
                    loginError = "Correo o contraseña incorrectos."
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Register.route) }
        ) {
            Text("Crear una cuenta")
        }
    }
}
