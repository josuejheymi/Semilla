package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.ReminderEntity
import com.yey.semilla.domain.model.ReminderWithMedication
import com.yey.semilla.domain.model.UserEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.AirQualityUiState
import com.yey.semilla.ui.viewmodel.ReminderViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel
import com.yey.semilla.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    userViewModel: UserViewModel,
    weatherViewModel: WeatherViewModel,
    user: UserEntity?
) {
    // 1. Observamos la lista de recordatorios
    val remindersState by reminderViewModel.reminders.collectAsState()

    // 2. Observamos el estado del clima
    val airState by weatherViewModel.state.collectAsState()

    // Cargar datos del usuario al entrar
    LaunchedEffect(user?.id) {
        user?.let { usuario ->
            reminderViewModel.loadForUser(usuario.id)
        }
    }

    // Cargar clima
    LaunchedEffect(Unit) {
        weatherViewModel.loadAirQuality(latitude = -33.45, longitude = -70.66)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFD4EFDF)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Menú", color = Color.Black, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))

                NavigationDrawerItem(label = { Text("Mi Perfil", color = Color(0xFF00A9E0)) }, selected = false, onClick = { navController.navigate(Screen.Profile.route) })
                NavigationDrawerItem(label = { Text("Medicamentos", color = Color(0xFF00A9E0)) }, selected = false, onClick = { navController.navigate("medication_list") })
                NavigationDrawerItem(label = { Text("Ajustes", color = Color(0xFF00A9E0)) }, selected = false, onClick = { /* TODO */ })
                NavigationDrawerItem(label = { Text("Editar Perfil", color = Color(0xFF00A9E0)) }, selected = false, onClick = { navController.navigate(Screen.EditProfile.route) })
                NavigationDrawerItem(label = { Text("Lista de usuarios", color = Color(0xFF00A9E0)) }, selected = false, onClick = { navController.navigate(Screen.UserList.route) })
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        userViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                    }
                )
            }
        },
        modifier = Modifier.background(color = Color.Black)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row {
                            Text(text = "Hola,", color = Color.Black, fontWeight = FontWeight.Thin)
                            Text(text = " ${user?.name ?: "Invitado"}", color = Color.Black, fontWeight = FontWeight.Thin)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFFFFFF)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.Black)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddReminder.route) },
                    containerColor = Color(0xFF27AE60),
                    contentColor = Color.White
                ) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 50.sp, color = Color.Black)
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB2FFB2))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Tarjetas superiores
                user?.let { usr ->
                    IMCCard(usr)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AirQualityCard(airState)
                Spacer(modifier = Modifier.height(20.dp))

                Text("Tus recordatorios", color = Color(0xFF000000), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                val sortedReminders = remindersState.sortedBy { it.reminder.time }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(sortedReminders) { item ->
                        ReminderCardWithMedication(item)
                    }
                }
            }
        }
    }
}

// ---------------- COMPONENTS ----------------

@Composable
fun IMCCard(user: UserEntity) {
    val alturaMetros = if (user.altura > 0) user.altura / 100 else 1.0
    val imc = if (user.peso > 0 && user.altura > 0) user.peso / alturaMetros.pow(2) else 0.0
    val (categoria, color) = when {
        imc == 0.0 -> "Faltan datos" to Color.Gray
        imc < 18.5 -> "Bajo peso" to Color(0xFF64B5F6)
        imc < 25.0 -> "Normal" to Color(0xFF81C784)
        imc < 30.0 -> "Sobrepeso" to Color(0xFFFFF176)
        else -> "Obesidad" to Color(0xFFE57373)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estado corporal", color = Color(0xFF000000), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("IMC: ${"%.1f".format(imc)}", color = Color(0xFF000000), style = MaterialTheme.typography.bodyLarge)
            Text("Clasificación: $categoria", color = Color(0xFF000000), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AirQualityCard(state: AirQualityUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Condiciones ambientales", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1B5E20))
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading -> Text("Cargando...", color = Color.Magenta)
                state.error != null -> Text("Sin datos de clima", color = Color.Red)
                else -> {
                    state.uvIndex?.let { uv -> Text("Índice UV: ${"%.1f".format(uv)} ${uvAdvice(uv)}", color = Color(0xFFA57865)) }
                    state.aqi?.let { aqi -> Text("AQI europeo: ${"%.1f".format(aqi)}", color = Color.Gray) }
                }
            }
        }
    }
}

fun uvAdvice(uv: Double): String = when {
    uv < 3 -> "(Bajo)"
    uv < 6 -> "(Moderado)"
    uv < 8 -> "(Alto)"
    else -> "(Muy alto)"
}

// 🟢 CARD DE RECORDATORIO (Con lógica anti-espacios en blanco)
@Composable
fun ReminderCardWithMedication(reminderWithMedication: ReminderWithMedication) {
    Card(
        colors = cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB2FFB2))
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val med = reminderWithMedication.medication
        val rem = reminderWithMedication.reminder

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            Text(text = med.name, color = Color(0xFF0000FF), style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Total: ${med.totalPills} — Restantes: ${med.pillsRemaining}", color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Hora: ${rem.time}", color = Color.Black, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================================
            // 🟢 ZONA DE IMAGEN INTELIGENTE
            // ==========================================================
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))

            // 1. Verificamos si existe una URI (texto)
            if (!med.imageUri.isNullOrBlank()) {

                // 2. Intentamos crear el "Pintor" con Coil
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(med.imageUri)
                        .crossfade(true)
                        .build()
                )

                // 3. Verificamos el ESTADO de la carga
                // Si está cargando o fue exitoso -> Mostramos la imagen
                // Si dio ERROR (ej: archivo borrado) -> Mostramos Placeholder
                if (painter.state is AsyncImagePainter.State.Error) {
                    PlaceholderBox(modifier = imageModifier)
                } else {
                    Image(
                        painter = painter,
                        contentDescription = med.name,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }

            } else {
                // Si la URI era nula desde el principio -> Placeholder
                PlaceholderBox(modifier = imageModifier)
            }
        }
    }
}

// 🟢 COMPONENTE REUTILIZABLE (El cuadrito gris)
@Composable
fun PlaceholderBox(modifier: Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFEEEEEE)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Sin imagen", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}