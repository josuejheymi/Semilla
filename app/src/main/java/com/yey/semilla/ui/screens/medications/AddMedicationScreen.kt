package com.yey.semilla.ui.screens.medications

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.MedicationViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    medicationViewModel: MedicationViewModel
) {
    // Campos del formulario
    var name by remember { mutableStateOf("") }
    var totalPills by remember { mutableStateOf("") }
    var pillsRemaining by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Usuario actual (para saber el userId)
    val currentUser by userViewModel.currentUser.collectAsState()

    // Selector de imagen (galería)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Si no hay usuario logueado
                if (currentUser == null) {
                    Text(
                        text = "Debes iniciar sesión para registrar medicamentos.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    ) {
                        Text("Ir al login")
                    }
                    return@Column
                }

                Text(
                    text = "Agregar Medicamento",
                    color = Color(0xFF009688),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del medicamento", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Total de pastillas
                OutlinedTextField(
                    value = totalPills,
                    onValueChange = { txt -> totalPills = txt.filter { it.isDigit() } },
                    label = { Text("Total de pastillas", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pastillas restantes
                OutlinedTextField(
                    value = pillsRemaining,
                    onValueChange = { txt -> pillsRemaining = txt.filter { it.isDigit() } },
                    label = { Text("Pastillas restantes", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón seleccionar imagen
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688),
                        contentColor = Color.White
                    ),
                    onClick = { launcher.launch("image/*") }
                ) {
                    Text("Seleccionar imagen")
                }

                imageUri?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Guardar medicamento
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66FFCC),
                        contentColor = Color.Black
                    ),
                    onClick = {
                        Log.d("AddMedication", "Botón presionado. Validando campos...")

                        if (name.isNotEmpty() && totalPills.isNotEmpty()) {

                            val userId = currentUser!!.id.toInt() // por si tu id es Long

                            val med = MedicationEntity(
                                userId = userId,
                                name = name,
                                totalPills = totalPills.toInt(),
                                pillsRemaining = pillsRemaining.toIntOrNull() ?: totalPills.toInt(),
                                // De momento NO subimos la imagen al backend, así evitamos líos de permisos
                                imageUri = null
                                // Si luego quieres guardar el path local:
                                // imageUri = imageUri?.toString()
                            )

                            Log.d("AddMedication", "Enviando medicamento al ViewModel: ${med.name}")
                            medicationViewModel.addMedication(med)

                            navController.popBackStack()
                        } else {
                            Log.e("AddMedication", "Error: Campos vacíos")
                        }
                    }
                ) {
                    Text("Guardar Medicamento")
                }
            }
        }
    }
}
