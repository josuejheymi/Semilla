package com.yey.semilla.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.data.local.model.ReminderEntity

import com.yey.semilla.ui.viewmodel.ReminderViewModel

@Composable
fun AddReminderScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    medications: List<MedicationEntity>
) {
    var selectedMedication by remember { mutableStateOf<MedicationEntity?>(null) }
    var time by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Agregar Recordatorio", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de medicamento
        Text("Selecciona un medicamento:")
        DropdownMenu(
            expanded = selectedMedication == null,
            onDismissRequest = { /* TODO */ }
        ) {
            medications.forEach { med ->
                DropdownMenuItem(text = { Text(med.name) },
                    onClick = { selectedMedication = med })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de hora
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Hora (HH:mm)") },
            placeholder = { Text("08:00") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            selectedMedication?.let {
                val reminder = ReminderEntity(
                    medicationId = it.id,
                    time = time
                )
                reminderViewModel.addReminder(reminder)
                navController.popBackStack() // regresar al Home
            }
        }) {
            Text("Guardar")
        }
    }
}