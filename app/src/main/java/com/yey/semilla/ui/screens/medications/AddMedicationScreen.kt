package com.yey.semilla.ui.screens.medications

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.MedicationViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    medicationViewModel: MedicationViewModel
) {
    val context = LocalContext.current // Necesario para copiar el archivo

    // --- COLORES ---
    val primaryTeal = Color(0xFF009688)
    val darkTeal = Color(0xFF004D40)
    val backgroundMint = Color(0xFFE0FFFA)

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryTeal,
        unfocusedBorderColor = primaryTeal.copy(alpha = 0.5f),
        focusedLabelColor = primaryTeal,
        unfocusedLabelColor = darkTeal,
        cursorColor = primaryTeal,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    // Campos
    var name by remember { mutableStateOf("") }
    var totalPills by remember { mutableStateOf("") }
    var pillsRemaining by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val currentUser by userViewModel.currentUser.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            color = backgroundMint
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (currentUser == null) {
                    Text("Debes iniciar sesión.", color = Color.Red)
                    return@Column
                }

                Text(
                    text = "Nuevo Medicamento",
                    color = primaryTeal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))

                // CAMPOS
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre del medicamento") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = totalPills,
                    onValueChange = { txt -> totalPills = txt.filter { it.isDigit() } },
                    label = { Text("Total de pastillas") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pillsRemaining,
                    onValueChange = { txt -> pillsRemaining = txt.filter { it.isDigit() } },
                    label = { Text("Pastillas actuales") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // FOTO
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = primaryTeal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryTeal),
                    onClick = { launcher.launch("image/*") }
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (imageUri == null) "Seleccionar imagen" else "Cambiar imagen")
                }

                imageUri?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(150.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN GUARDAR
                Button(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryTeal),
                    enabled = name.isNotBlank() && totalPills.isNotBlank() && !isSaving,
                    onClick = {
                        isSaving = true

                        // 🟢 LÓGICA DE COPIADO:
                        // Si hay imagen seleccionada, la copiamos a la carpeta privada de la app.
                        // Esto devuelve una ruta tipo: /data/user/0/com.yey.semilla/files/images/foto123.jpg
                        val permanentPath = imageUri?.let { uri ->
                            saveImageToInternalStorage(context, uri)
                        }

                        val userId = currentUser!!.id.toInt()

                        val med = MedicationEntity(
                            userId = userId,
                            name = name,
                            totalPills = totalPills.toInt(),
                            pillsRemaining = pillsRemaining.toIntOrNull() ?: totalPills.toInt(),
                            // Guardamos la RUTA PERMANENTE, no la temporal de la galería
                            imageUri = permanentPath
                        )

                        medicationViewModel.addMedication(med)
                        navController.popBackStack()
                    }
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Guardar Medicamento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// 🟢 LA FUNCIÓN MÁGICA: Copia la imagen de la galería a tu app
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        // 1. Abrimos el flujo de datos de la imagen original
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        // 2. Creamos una carpeta "med_images" dentro de la app (si no existe)
        val directory = File(context.filesDir, "med_images")
        if (!directory.exists()) directory.mkdirs()

        // 3. Creamos un nombre único para el archivo (ej: 123e4567-e89b... .jpg)
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(directory, fileName)

        // 4. Copiamos los bytes
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        // 5. Devolvemos la ruta absoluta del archivo nuevo
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}