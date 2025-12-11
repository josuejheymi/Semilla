package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel
import kotlin.math.pow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerfilScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // Obtenemos el usuario actual desde el ViewModel
    val userState by userViewModel.currentUser.collectAsState()
    val user = userState

    if (user == null) {
        // Si no hay usuario (por ejemplo, no hay login), mostramos mensaje sencillo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay usuario cargado")
        }
        return
    }

    // ⚠ IMPORTANTE: en tu app la altura se guarda en CENTÍMETROS
    // (porque en el registro pones "Altura (cm)" y guardas directamente ese número)
    // Por eso acá la convertimos a METROS para el IMC:
    val alturaMetros = user.altura / 100.0
    val imc = user.peso / alturaMetros.pow(2)

    val imcMsg = when {
        imc < 18.5 -> "Bajo peso"
        imc < 25 -> "Normal"
        imc < 30 -> "Sobrepeso"
        else -> "Obesidad"
    }

    val fechaNacimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(user.fechanacimiento))

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFE0FFFA)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // FOTO
                if (user.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(user.photoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Imagen por defecto
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // NOMBRE + EMAIL
                Text(
                    user.name,
                    color = Color(0xFF202020),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    user.email,
                    color = Color(0xFF202020),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(20.dp))

                // INFO PERSONAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información del usuario",
                            color = Color(0xFF202020),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        Text("Género: ${user.genero}", color = Color(0xFF555555))
                        Text("Fecha de nacimiento: $fechaNacimiento", color = Color(0xFF555555))
                        Text("Peso: ${user.peso} kg", color = Color(0xFF555555))
                        // Mostramos la altura en cm para que sea consistente con el registro
                        Text("Altura: ${user.altura} cm", color = Color(0xFF555555))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // IMC
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Estado físico",
                            color = Color(0xFF202020),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        Text("IMC: ${"%.2f".format(imc)}", color = Color(0xFF555555))
                        Text("Clasificación: $imcMsg", color = Color(0xFF555555))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // BOTÓN EDITAR → navega a la pantalla de edición
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688),
                        contentColor = Color.White
                    ),
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
