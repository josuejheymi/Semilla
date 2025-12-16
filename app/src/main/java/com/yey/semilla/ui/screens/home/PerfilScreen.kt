package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout // 🟢 NUEVO IMPORT
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
// import androidx.compose.material.icons.filled.Logout <-- ESTE YA NO SE USA
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Composable
fun PerfilScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // region [CONFIG] PALETA DE COLORES
    val primaryTeal = Color(0xFF009688)
    val darkTeal = Color(0xFF004D40)
    val backgroundMint = Color(0xFFE0FFFA)
    val alertRed = Color.Red
    // endregion

    // region [LÓGICA] ESTADOS Y CÁLCULOS
    val userState by userViewModel.currentUser.collectAsState()
    val user = userState

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = primaryTeal)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cargando perfil...", color = darkTeal)
        }
        return
    }

    val alturaMetros = if (user.altura > 0) user.altura / 100.0 else 1.0
    val imc = if (user.altura > 0) user.peso / alturaMetros.pow(2) else 0.0

    val imcMsg = when {
        imc == 0.0 -> "Sin datos"
        imc < 18.5 -> "Bajo peso"
        imc < 25 -> "Normal"
        imc < 30 -> "Sobrepeso"
        else -> "Obesidad"
    }

    val imcColor = when {
        imc < 18.5 -> Color(0xFF2196F3)
        imc < 25 -> Color(0xFF4CAF50)
        imc < 30 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    val fechaNacimiento = try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(user.fechanacimiento))
    } catch (_: Exception) { // 🟢 CAMBIO: Usamos '_' porque no usamos la variable 'e'
        "Sin fecha"
    }
    // endregion

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundMint
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // region [UI] FOTO DE PERFIL
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(130.dp)
                        .border(3.dp, primaryTeal, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                ) {
                    if (user.photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(user.photoUri),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                // endregion

                Spacer(Modifier.height(16.dp))

                // region [UI] TEXTOS CABECERA
                Text(
                    text = user.name,
                    color = darkTeal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                // endregion

                Spacer(Modifier.height(24.dp))

                // region [UI] TARJETA INFO PERSONAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Información Personal",
                            color = primaryTeal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // 🟢 CAMBIO: Usamos HorizontalDivider en lugar de Divider
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = backgroundMint
                        )

                        InfoRow(label = "Género", value = user.genero)
                        InfoRow(label = "Nacimiento", value = fechaNacimiento)
                        InfoRow(label = "Peso", value = "${user.peso} kg")
                        InfoRow(label = "Altura", value = "${user.altura} cm")
                    }
                }
                // endregion

                Spacer(Modifier.height(16.dp))

                // region [UI] TARJETA IMC
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Estado Físico",
                            color = primaryTeal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // 🟢 CAMBIO: HorizontalDivider
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = backgroundMint
                        )

                        InfoRow(label = "IMC", value = "%.2f".format(imc))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Clasificación", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                            Text(text = imcMsg, fontWeight = FontWeight.Bold, color = imcColor)
                        }
                    }
                }
                // endregion

                Spacer(Modifier.height(30.dp))

                // region [UI] BOTONES DE ACCIÓN

                // 1. BOTÓN EDITAR
                Button(
                    onClick = { navController.navigate(Screen.EditProfile.route) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Editar Perfil", fontSize = 16.sp)
                }

                Spacer(Modifier.height(12.dp))

                // 2. BOTÓN CERRAR SESIÓN
                OutlinedButton(
                    onClick = {
                        userViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = alertRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, alertRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // 🟢 CAMBIO: Icono AutoMirrored para soporte internacional
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
                // endregion

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// region [HELPER] COMPONENTES REUTILIZABLES
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
// endregion