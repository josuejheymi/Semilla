package com.yey.semilla.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel
import com.yey.semilla.utils.Validators
import com.yey.semilla.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    //  COLORES
    val primaryTeal = Color(0xFF009688)
    val backgroundMint = Color(0xFFE0FFFA)

    //  CORRECCIÓN DE COLORES DE TEXTO
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryTeal,
        unfocusedBorderColor = primaryTeal,
        focusedContainerColor = Color.White,  // Fondo blanco
        unfocusedContainerColor = Color.White, // Fondo blanco
        focusedTextColor = Color.Black,       // 🟢 TEXTO NEGRO AL ESCRIBIR
        unfocusedTextColor = Color.Black,     // 🟢 TEXTO NEGRO AL NO ESCRIBIR
        cursorColor = primaryTeal
    )

    // Variables de los campos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // Para el ojito
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observamos al ViewModel
    val loginSuccess by userViewModel.loginSuccess.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    // LÓGICA DE NAVEGACIÓN (Simple: Si hay user, entra)
    LaunchedEffect(loginSuccess, currentUser) {
        if (loginSuccess && currentUser != null) {
            errorMessage = null
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // UI PRINCIPAL
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundMint // Fondo Menta
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // 1. ICONO DE CABECERA (Cambiado a Usuario)
            Image(
                painter = painterResource(id = R.drawable.emogi), // 🟢 Carga tu imagen "emogi.jpg"
                contentDescription = "Emoji saludando",
                modifier = Modifier.size(100.dp) // Ajusta el tamaño según prefieras
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
            )

            Spacer(Modifier.height(30.dp))

            // MENSAJE DE ERROR (Rojo)
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // 2. CAMPO EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = primaryTeal) },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors, // 🟢 Aquí aplicamos el color negro de letra
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // 3. CAMPO CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = primaryTeal) },
                // EL OJITO PARA VER/OCULTAR
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = null, tint = Color.Gray)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors, // 🟢 Aquí aplicamos el color negro de letra
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(30.dp))

            // 4. BOTÓN INGRESAR
            Button(
                onClick = {
                    // VALIDACIONES SIMPLES (Fáciles de explicar)
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Por favor completa todos los campos."
                    } else if (!Validators.isEmailValid(email)) {
                        errorMessage = "El formato del email es incorrecto."
                    } else {
                        // Si todo está bien, llamamos al Login
                        userViewModel.login(email, password)
                        // Pequeño truco visual: si falla, el ViewModel no actualizará loginSuccess
                        // y podrias poner un mensaje genérico si tarda mucho, pero así es más simple.
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ingresar", fontSize = 18.sp)
            }

            Spacer(Modifier.height(20.dp))

            // 5. LINK A REGISTRO
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?", color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Regístrate aquí",
                    color = primaryTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
        }
    }
}