package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.domain.model.ReminderEntity
import com.yey.semilla.domain.model.ReminderWithMedication
import com.yey.semilla.domain.model.UserEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.ReminderViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel
import com.yey.semilla.ui.viewmodel.WeatherViewModel
import com.yey.semilla.ui.viewmodel.AirQualityUiState
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
    val remindersState by reminderViewModel.reminders.collectAsState()

    // Estado del clima / calidad del aire
    val airState by weatherViewModel.state.collectAsState()

    // Llamamos a la API UNA sola vez cuando se entra a Home
    LaunchedEffect(Unit) {
        // Aqui se Elige las coordenadas que quieras:
        // Cape Town (ejemplo que probaste): -33.92, 18.42
        // Santiago, Chile (más realista para tu app): -33.45, -70.66
        weatherViewModel.loadAirQuality(
            latitude = -33.45,
            longitude = -70.66
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFD4EFDF) // Color del container del sub menú
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Menú",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Mi Perfil",color = Color(0xFF00A9E0)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )

                NavigationDrawerItem(
                    label = { Text("Medicamentos",color = Color(0xFF00A9E0)) },
                    selected = false,
                    onClick = { navController.navigate("medication_list") }
                )

                NavigationDrawerItem(
                    label = { Text("Ajustes", color = Color(0xFF00A9E0)) },
                    selected = false,
                    onClick = { /* TODO: ir a ajustes */ }
                )

                NavigationDrawerItem(
                    label = { Text("Editar Perfil",color = Color(0xFF00A9E0)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                )

                NavigationDrawerItem(
                    label = { Text("Lista de usuarios",color = Color(0xFF00A9E0 )) },
                    selected = false,
                    onClick = { navController.navigate(Screen.UserList.route) }
                )

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        userViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
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
                        Text(text = "Hola, ${user?.name ?: "Invitado"}")
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFFFFFFF),
                        titleContentColor = Color(0xFF000000)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
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
                    Text("+")
                }
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB2FFB2)) // Color de fondo de HOME
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // 🟢 Tarjeta IMC
                user?.let { usr ->
                    IMCCard(usr)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 🟣 Tarjeta de Condiciones Ambientales (API externa)
                AirQualityCard(airState)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Tus recordatorios",
                    color = Color(0xFF000000),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Ordenamos por hora antes de mostrar - llamanado a la funcion que esta en reminder
                val sortedReminders = remindersState.sortedBy { it.reminder.time }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(sortedReminders) { item ->
                        ReminderCardWithMedication(item)
                    }
                }
            }
        }
    }
}

/************************************************************
 *   🟢 TARJETA IMC (IMC + categoría + color)
 ************************************************************/
@Composable
fun IMCCard(user: UserEntity) {

    // Si altura está guardada en cm:
    val alturaMetros = user.altura / 100
    val imc = user.peso / alturaMetros.pow(2)

    val (categoria, color) = when {
        imc < 18.5 -> "Bajo peso" to Color(0xFF64B5F6)  // Azul suave
        imc < 25.0 -> "Normal" to Color(0xFF81C784)     // Verde
        imc < 30.0 -> "Sobrepeso" to Color(0xFFFFF176)  // Amarillo
        else -> "Obesidad" to Color(0xFFE57373)         // Rojo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estado corporal",
                color = Color(0xFF000000),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("IMC: ${"%.1f".format(imc)}", color = Color(0xFF000000), style = MaterialTheme.typography.bodyLarge)
            Text("Clasificación: $categoria",color = Color(0xFF000000), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/************************************************************
 *   🟣 TARJETA CONDICIONES AMBIENTALES (Open-Meteo)
 ************************************************************/
@Composable
fun AirQualityCard(state: AirQualityUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Condiciones ambientales",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1B5E20)
            )
            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> {
                    Text("Cargando datos de calidad del aire…",color = Color.Magenta)
                }

                state.error != null -> {
                    Text(
                        "No se pudo obtener la información (sin conexión o servicio caído).",
                        color = Color.Red
                    )
                }

                else -> {
                    state.uvIndex?.let { uv ->
                        Text("Índice UV: ${"%.1f".format(uv)} ${uvAdvice(uv)}",color = Color(0xFFA57865))
                    }
                    state.aqi?.let { aqi ->
                        Text("AQI europeo: ${"%.1f".format(aqi)}",color = Color.Gray)
                    }
                }
            }
        }
    }
}

fun uvAdvice(uv: Double): String = when {
    uv < 3 -> "(Bajo, puedes salir tranquilo 😎)"
    uv < 6 -> "(Moderado, usa bloqueador)"
    uv < 8 -> "(Alto, evita sol al mediodía)"
    else -> "(Muy alto, protégete bien ☀️)"
}

/************************************************************
 *   TARJETA DE CADA RECORDATORIO
 ************************************************************/
@Composable
fun ReminderCardWithMedication(reminderWithMedication: ReminderWithMedication) {
    Card(
        colors = cardColors(containerColor = Color(0xFFFFFFFF)), // fondo card
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC8E6C9)) // color detrás de la card
            .padding(vertical = 6.dp), // espacio entre cards
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val med: MedicationEntity = reminderWithMedication.medication
        val rem: ReminderEntity = reminderWithMedication.reminder

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = med.name, color = Color(0xFF0000FF),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Total: ${med.totalPills} — Restantes: ${med.pillsRemaining}",color = Color.Green)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hora: ${rem.time}",
                color = Color.Black,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            med.imageUri?.let { uriStr ->
                Image(
                    painter = rememberAsyncImagePainter(uriStr),
                    contentDescription = med.name,
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
