package com.yey.semilla.ui.screens.home.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.data.local.model.MedicationEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    medications: List<MedicationEntity>
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)}
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFE0FFFA)// Color de FOndo
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

            // ---------- POPUP SELECTOR DE HORA ----------
            if (showTimePicker) {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val h = timePickerState.hour.toString().padStart(2, '0')
                            val m = timePickerState.minute.toString().padStart(2, '0')
                            time = "$h:$m"
                            showTimePicker = false
                        }) { Text("OK", color = Color.Black) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(
                                "Cancelar",
                                color = Color.Red
                            )
                        }
                    },
                    text = { TimePicker(state = timePickerState) }
                )
            }

            // ===================== UI PRINCIPAL ======================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Esto centra horizontalmente iniciar sesion
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "Agregar Recordatorio",
                    color = Color(0xFF009688),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(20.dp))

                // ------- SI NO HAY MEDICAMENTOS -------
                if (medications.isEmpty()) {
                    Text("No tienes medicamentos registrados.", color = Color.Red)
                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = { navController.navigate(Screen.AddMedication.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar medicamento")
                    }
                    return@Surface
                }

                // --------- DROPDOWN DE MEDICAMENTOS ---------
                var expanded by remember { mutableStateOf(false) }

                Text("Selecciona un medicamento:", color = Color(0xFF009688))
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMedication?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Medicamento", color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        medications.forEach { med ->
                            DropdownMenuItem(
                                text = { Text(med.name, color = Color.Gray) },
                                onClick = {
                                    selectedMedication = med
                                    expanded = false
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Agregar medicamento", color = Color(0xFF000000)) },
                            trailingIcon = { Icon(Icons.Default.Add, "Agregar medicamento") },
                            onClick = {
                                expanded = false
                                navController.navigate(Screen.AddMedication.route)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // --------- HORA ---------
                OutlinedTextField(
                    value = if (time.isEmpty()) "Selecciona una hora" else time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora") },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                Icons.Default.AccessTime,
                                "Seleccionar hora",
                                tint = Color(0xFF009688)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                // --------- VECES POR DÍA ---------
                OutlinedTextField(
                    value = timesPerDay,
                    onValueChange = { value ->
                        timesPerDay = value.filter { it.isDigit() }.ifEmpty { "1" }
                    },
                    label = { Text("Veces al día", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(30.dp))

                // --------- GUARDAR ---------
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688),
                        contentColor = Color.White
                    ), onClick = {
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
    }
}
