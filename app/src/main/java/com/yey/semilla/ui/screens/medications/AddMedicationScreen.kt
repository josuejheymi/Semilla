package com.yey.semilla.ui.screens.medications

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.data.local.model.MedicationEntity
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Agregar Medicamento", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(20.dp))

        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del medicamento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total de pastillas
        OutlinedTextField(
            value = totalPills,
            onValueChange = { totalPills = it.filter { c -> c.isDigit() } },
            label = { Text("Total de pastillas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pastillas restantes
        OutlinedTextField(
            value = pillsRemaining,
            onValueChange = { pillsRemaining = it.filter { c -> c.isDigit() } },
            label = { Text("Pastillas restantes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selección de imagen
        Button(
            modifier = Modifier.fillMaxWidth(),
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
