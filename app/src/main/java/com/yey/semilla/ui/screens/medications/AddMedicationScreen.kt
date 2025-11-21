package com.yey.semilla.ui.screens.medications

import android.net.Uri
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
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    userId: Int
) {
    var name by remember { mutableStateOf("") }
    var totalPills by remember { mutableStateOf("") }
    var pillsRemaining by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Seleccionar imagen de galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)}

    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFE0FFFA)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {

                Text(
                    "Agregar Medicamento",
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
                    onValueChange = { totalPills = it.filter { c -> c.isDigit() } },
                    label = { Text("Total de pastillas", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pastillas restantes
                OutlinedTextField(
                    value = pillsRemaining,
                    onValueChange = { pillsRemaining = it.filter { c -> c.isDigit() } },
                    label = { Text("Pastillas restantes", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selección de imagen
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
                        if (name.isNotEmpty() && totalPills.isNotEmpty()) {

                            val med = MedicationEntity(
                                userId = userId,
                                name = name,
                                totalPills = totalPills.toInt(),
                                pillsRemaining = pillsRemaining.toIntOrNull() ?: totalPills.toInt(),
                                imageUri = imageUri?.toString()
                            )

                            reminderViewModel.addMedication(med)
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("Guardar Medicamento")
                }
            }
        }
    }
}
