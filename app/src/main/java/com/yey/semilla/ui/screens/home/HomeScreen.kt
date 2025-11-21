package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.data.local.model.ReminderEntity
import com.yey.semilla.data.local.model.ReminderWithMedication
import com.yey.semilla.data.local.model.UserEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.ReminderViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    userViewModel: UserViewModel,
    user: UserEntity?
) {
    val remindersState by reminderViewModel.reminders.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFD4EFDF) //Color del container del sub menu
            ) {

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Menú",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )

                NavigationDrawerItem(
                    label = { Text("Medicamentos") },
                    selected = false,
                    onClick = { navController.navigate("medication_list") }
                )
                NavigationDrawerItem(
                    label = { Text("Ajustes") },
                    selected = false,
                    onClick = {  }
                )
                NavigationDrawerItem(
                    label = { Text("Editar Perfil") },
                    selected = false,
                    onClick = { navController.navigate(Screen.EditProfile.route) }
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
        modifier = Modifier.background(color =Color.Black )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Hola, ${user?.name ?: "Invitado"}")
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFFFFFFF), //fondo de container del saludo
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
                    onClick = { navController.navigate(Screen.AddReminder.route)},
                    containerColor = Color(0xFF27AE60),
                    contentColor = Color.White

                ) {
                    Text("+")
                }
            },
            bottomBar = {
                BottomNavigationBar(navController)}
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB2FFB2)) // Color de fondo de HOME
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // 🟢🟡🟠🔴 TARJETA IMC
                user?.let { usr ->
                    IMCCard(usr)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(text = "Tus recordatorios", color = Color(0xFF000000), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                // ORDENAMOS POR HORA ANTES DE MOSTRAR -Los recordatorios llegan desde el ViewModel sin orden específico.
                //Para ordenarlos por hora, los convierto de formato HH:mm a minutos totales del día (hora × 60 + minuto), y con sortedBy los muestro en orden cronológico antes de renderizar la lista.
                val sortedReminders = remindersState.sortedBy { reminder ->
                    val time = reminder.reminder.time       // "HH:mm"
                    val parts = time.split(":")
                    val hour = parts[0].toInt()
                    val minute = parts[1].toInt()
                    hour * 60 + minute   // convertimos a minutos totales
                }
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
 *   🟢 TARJETA IMC (IMC + categoría + color bonito)
 ************************************************************/
@Composable
fun IMCCard(user: UserEntity) {

    // IMC = Peso / (altura^2)
    val alturaMetros = user.altura / 100
    val imc = user.peso / alturaMetros.pow(2)

    val (categoria, color) = when {
        imc < 18.5 -> "Bajo peso" to Color(0xFF64B5F6)  // Azul suave
        imc < 25.0 -> "Normal" to Color(0xFF81C784)     // Verde
        imc < 30.0 -> "Sobrepeso" to Color(0xFFFFF176)  // Amarillo
        else -> "Obesidad" to Color(0xFFE57373)         // Rojo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Estado corporal",color = Color(0xFF000000),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("IMC: ${"%.1f".format(imc)}", style = MaterialTheme.typography.bodyLarge)
            Text("Clasificación: $categoria", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


/************************************************************
 *   TARJETA DE CADA RECORDATORIO
 ************************************************************/
@Composable
fun ReminderCardWithMedication(reminderWithMedication: ReminderWithMedication) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)), // color de fondo de la card
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC8E6C9)) // color del borde
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

            Text(text = med.name, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Total: ${med.totalPills} — Restantes: ${med.pillsRemaining}")
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Hora: ${rem.time}", color = Color.Black, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
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
