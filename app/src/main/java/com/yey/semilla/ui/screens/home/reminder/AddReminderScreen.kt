package com.yey.semilla.ui.screens.home.reminder

import android.R
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yey.semilla.domain.model.MedicationEntity
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
    // Definimos tus colores principales aquí para reutilizarlos
    val primaryTeal = Color(0xFF009688)
    val darkTeal = Color(0xFF004D40) // Un verde más oscuro para textos importantes
    val backgroundMint = Color(0xFFE0FFFA)

    // Configuración de colores para los Inputs (Text Fields)
    // Esto quita el gris y pone tus colores verdes/negros
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryTeal,
        unfocusedBorderColor = primaryTeal.copy(alpha = 0.5f), // Un poco más suave cuando no escribes
        focusedLabelColor = primaryTeal,
        unfocusedLabelColor = darkTeal, // Color de la etiqueta cuando no escribes (Ya no gris)
        cursorColor = primaryTeal,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White, // Fondo blanco para que resalte
        unfocusedContainerColor = Color.White
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundMint
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
                    containerColor = Color.White, // Fondo blanco para el popup
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val h = timePickerState.hour.toString().padStart(2, '0')
                            val m = timePickerState.minute.toString().padStart(2, '0')
                            time = "$h:$m"
                            showTimePicker = false
                        }) { Text("OK", color = primaryTeal, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancelar", color = Color.Red)
                        }
                    },
                    text = {
                        // Forzamos colores del reloj
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                selectorColor = primaryTeal,
                                clockDialSelectedContentColor = Color.White,
                                clockDialUnselectedContentColor = Color.Yellow,
                                timeSelectorSelectedContainerColor = Color.Black,
                                timeSelectorSelectedContentColor = Color.White,
                                timeSelectorUnselectedContentColor = Color.Black,
                                timeSelectorUnselectedContainerColor = Color.Green.copy(alpha = 0.2f)
                            )
                        )
                    }
                )
            }

            // ===================== UI PRINCIPAL ======================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // Un poco más de margen
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Agregar Recordatorio",
                    color = primaryTeal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(30.dp))

                // ------- SI NO HAY MEDICAMENTOS -------
                if (medications.isEmpty()) {
                    Text("No tienes medicamentos registrados.", color = Color.Red, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate(Screen.AddMedication.route) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
                    ) {
                        Text("Agregar medicamento", color = Color.White)
                    }
                    return@Surface
                }

                // --------- DROPDOWN DE MEDICAMENTOS ---------
                var expanded by remember { mutableStateOf(false) }

                // Texto de ayuda más visible
                Text(
                    text = "Selecciona un medicamento:",
                    color = darkTeal,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMedication?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Medicamento") }, // El color se maneja en customTextFieldColors
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = customTextFieldColors, // <--- AQUI APLICAMOS LOS COLORES
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        medications.forEach { med ->
                            DropdownMenuItem(
                                text = { Text(med.name, color = Color.Black) }, // Texto negro
                                onClick = {
                                    selectedMedication = med
                                    expanded = false
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Agregar nuevo...", color = primaryTeal, fontWeight = FontWeight.Bold) },
                            trailingIcon = { Icon(Icons.Default.Add, "Agregar", tint = primaryTeal) },
                            onClick = {
                                expanded = false
                                navController.navigate(Screen.AddMedication.route)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --------- HORA ---------
                OutlinedTextField(
                    value = if (time.isEmpty()) "" else time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (time.isEmpty()) "Seleccionar Hora" else "Hora") },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, "Seleccionar hora", tint = primaryTeal)
                        }
                    },
                    colors = customTextFieldColors, // <--- COLORES APLICADOS
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // --------- VECES POR DÍA ---------
                OutlinedTextField(
                    value = timesPerDay,
                    onValueChange = { value ->
                        timesPerDay = value.filter { it.isDigit() }.ifEmpty { "1" }
                    },
                    label = { Text("Veces al día") },
                    colors = customTextFieldColors, // <--- COLORES APLICADOS
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(40.dp))

                // --------- GUARDAR ---------
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryTeal,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        disabledContentColor = Color.White
                    ),
                    onClick = {
                        val med = selectedMedication ?: return@Button

                        if (time.isNotEmpty()) {
                            reminderViewModel.addReminder(
                                medicationId = med.id,
                                startDate = startDate,
                                endDate = endDate,
                                timesPerDay = timesPerDay.toInt(),
                                time = time,
                                isEnabled = true
                            )

                            navController.popBackStack()
                        }
                    },
                    enabled = selectedMedication != null && time.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp), // Un poco más alto para mejor tacto
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Guardar Recordatorio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}