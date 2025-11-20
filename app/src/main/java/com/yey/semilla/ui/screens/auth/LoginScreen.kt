package com.yey.semilla.ui.screens.auth

import androidx.compose.animation.animateContentSize
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
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE0FFFA)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),  // Espacio entre los bordes y el contenido
            horizontalAlignment = Alignment.CenterHorizontally, // Esto centra horizontalmente iniciar sesion
            verticalArrangement = Arrangement.Center
        ) {

            Text("Iniciar Sesión",
                color = Color(0xFF009688),
                style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            //   Mostrar mensaje de error
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
                ),shape = RoundedCornerShape(12.dp) // Define el tipo de forma, este caso son bordes redondeados
                ,onClick = {
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

            // Este efecto detecta login fallido
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

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009688),
                    contentColor = Color.White
                ),onClick = { navController.navigate(Screen.Register.route) }
            ) {
                Text("Crear una cuenta")
            }
        }
    }
}
