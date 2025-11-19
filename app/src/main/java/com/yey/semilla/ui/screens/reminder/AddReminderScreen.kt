package com.yey.semilla.ui.screens.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    medications: List<MedicationEntity>
) {
    var selectedMedication by remember { mutableStateOf<MedicationEntity?>(null) }
    var time by remember { mutableStateOf("") }
    var timesPerDay by remember { mutableStateOf("1") }

    val startDate = System.currentTimeMillis()
    val endDate: Long? = null

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = true
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val h = timePickerState.hour.toString().padStart(2, '0')
                    val m = timePickerState.minute.toString().padStart(2, '0')
                    time = "$h:$m"
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Agregar Recordatorio", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(20.dp))

        if (medications.isEmpty()) {
            Text("No tienes medicamentos registrados.")
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate(Screen.AddMedication.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar medicamento")
            }
            return
        }

        var expanded by remember { mutableStateOf(false) }

        Text("Selecciona un medicamento:")
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedMedication?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Medicamento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                medications.forEach { med ->
                    DropdownMenuItem(
                        text = { Text(med.name) },
                        onClick = {
                            selectedMedication = med
                            expanded = false
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text("➕ Agregar medicamento") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screen.AddMedication.route)
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = if (time.isEmpty()) "Selecciona una hora" else time,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora") },
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.AccessTime, "Seleccionar hora")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = timesPerDay,
            onValueChange = { value ->
                timesPerDay = value.filter { it.isDigit() }.ifEmpty { "1" }
            },
            label = { Text("Veces al día") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                val med = selectedMedication ?: return@Button
                if (time.isNotEmpty()) {
                    reminderViewModel.addReminder(
                        medicationId = med.id,
                        startDate = startDate,
                        endDate = endDate,
                        timesPerDay = timesPerDay.toInt(),
                        time = time
                    )
                    navController.popBackStack()
                }
            },
            enabled = selectedMedication != null && time.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
