package com.yey.semilla.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.data.local.model.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

@Composable
fun PerfilScreen(
    navController: NavController,
    user: UserEntity?
) {
    if (user == null) {
        Text("No hay usuario cargado")
        return
    }

    // Calcular IMC con altura en METROS
    val alturaMetros = user.altura
    val imc = user.peso / alturaMetros.pow(2)

    val imcMsg = when {
        imc < 18.5 -> "Bajo peso"
        imc < 25 -> "Normal"
        imc < 30 -> "Sobrepeso"
        else -> "Obesidad"
    }

    val fechaNacimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(user.fechanacimiento))

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {

        // FOTO
        if (user.photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(user.photoUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier.Companion
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } else {
            // Imagen por defecto
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.Companion.size(120.dp)
            )
        }

        Spacer(Modifier.Companion.height(20.dp))

        // NOMBRE
        Text(user.name, style = MaterialTheme.typography.headlineMedium)
        Text(user.email, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.Companion.height(20.dp))

        // INFO PERSONAL
        Card(
            modifier = Modifier.Companion.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.Companion.padding(16.dp)) {
                Text("Información del usuario", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.Companion.height(12.dp))

                Text("Género: ${user.genero}")
                Text("Fecha de nacimiento: $fechaNacimiento")
                Text("Peso: ${user.peso} kg")
                Text("Altura: ${user.altura} m")
            }
        }

        Spacer(Modifier.Companion.height(20.dp))

        // IMC
        Card(
            modifier = Modifier.Companion.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.Companion.padding(16.dp)) {
                Text("Estado físico", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.Companion.height(12.dp))

                Text("IMC: ${"%.2f".format(imc)}")
                Text("Clasificación: $imcMsg")
            }
        }
    }
}