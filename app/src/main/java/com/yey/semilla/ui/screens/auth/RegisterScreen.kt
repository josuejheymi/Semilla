package com.yey.semilla.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel

/**
 * 📝 PANTALLA DE REGISTRO (RegisterScreen)
 *
 * Propósito: Composable para la creación de una nueva cuenta de usuario.
 * Se encarga de capturar la información de texto y la URI de la foto.
 *
 * @param navController Controlador de navegación para redirigir al Login tras el registro.
 * @param userViewModel ViewModel para ejecutar la lógica de guardar el usuario en la DB.
 */
@Composable
fun RegisterScreen(navController: NavController, userViewModel: UserViewModel) {
    // ESTADO: Variables reactivas que almacenan los datos de los campos de texto.
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers para galería y cámara
    val launcherGallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photoUri = uri
    }

    val launcherCamera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // Si quieres usar bitmap directo, puedes guardarlo en URI temporal o convertir a archivo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Registrar Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Foto del usuario
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { launcherGallery.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                Image(
                    //carga la imagen desde un URI en Compose
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Agregar Foto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // CAMPOS DE TEXTO: Captura de datos básicos.
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        // BOTÓN GUARDAR: Ejecuta la lógica de registro.
        Button(onClick = {
            // Lógica de validación mínima.
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                // Llama al ViewModel para guardar el usuario.
                userViewModel.addUser(
                    name = name,
                    email = email,
                    password = password,
                    // Se usan valores por defecto para los campos de salud/fecha de nacimiento
                    // que deberían ser capturados en la UI real.
                    fechanacimiento = 0L,
                    genero = "Otro",
                    peso = 0.0,
                    altura = 0.0,
                    // Convierte la URI a String para almacenarla en la DB.
                    photoUri = photoUri?.toString()
                )
                // Navega de vuelta a la pantalla de Login y limpia la pila de navegación.
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}
