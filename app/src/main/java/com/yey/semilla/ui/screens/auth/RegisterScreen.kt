package com.yey.semilla.ui.screens.auth

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Importante para el scroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions // Para mejorar el teclado
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType // Para teclado numérico
import androidx.compose.ui.text.input.PasswordVisualTransformation // Para ocultar contraseña
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.yey.semilla.ui.viewmodel.UserViewModel
import com.yey.semilla.utils.Validators // 🟢 IMPORTANTE: Tu utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current

    // ---------------- CAMPOS DE TEXTO ----------------
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Seleccionar") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf(0L) }

    // ---------------- ERRORES ----------------
    var localError by remember { mutableStateOf<String?>(null) }
    val vmError by userViewModel.errorMessage.collectAsState()
    val errorToShow = localError ?: vmError

    // ---------------- FOTO PERFIL ----------------
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Permisos y Launchers (Cámara/Galería)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) selectedImageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) selectedImageUri = tempCameraUri }

    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timeStamp.jpg")
        return androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // ---------------- DATE PICKER ----------------
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            fechaNacimiento = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR) - 20,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // ---------------- GÉNERO DROPDOWN ----------------
    var generoExpanded by remember { mutableStateOf(false) }
    val generos = listOf("Hombre", "Mujer", "Otro")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE0FFFA)
    ) {
        // 🟢 CAMBIO: Usamos LazyColumn para permitir SCROLL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Agregamos espacio al final para que el botón no quede pegado
            contentPadding = PaddingValues(bottom = 50.dp)
        ) {

            item {
                Text(
                    "Crear cuenta",
                    color = Color(0xFF009688),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))

                // ERRORES
                errorToShow?.let {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                }

                // FOTO
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                    } else {
                        Text("Foto", color = Color.DarkGray)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                            tempCameraUri = createImageUri(context)
                            tempCameraUri?.let { cameraLauncher.launch(it) }
                        }
                    ) { Text("Cámara") }

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                        onClick = { galleryLauncher.launch("image/*") }
                    ) { Text("Galería") }
                }
                Spacer(Modifier.height(20.dp))
            }

            // CAMPOS DEL FORMULARIO
            item {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                // GÉNERO
                ExposedDropdownMenuBox(
                    expanded = generoExpanded,
                    onExpandedChange = { generoExpanded = !generoExpanded }
                ) {
                    OutlinedTextField(
                        value = genero, onValueChange = {}, readOnly = true,
                        label = { Text("Género") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = generoExpanded,
                        onDismissRequest = { generoExpanded = false }
                    ) {
                        generos.forEach { gen ->
                            DropdownMenuItem(
                                text = { Text(gen) },
                                onClick = { genero = gen; generoExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            item {
                // FECHA
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    onClick = { datePicker.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (fechaNacimiento == 0L) "Seleccionar fecha de nacimiento" else SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaNacimiento)))
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it.filter { c -> c.isDigit() } },
                    label = { Text("Altura (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // 🟢 AQUÍ USAMOS TUS VALIDATORS
                        // Esto conecta tu UI con los Tests Unitarios

                        localError = when {
                            !Validators.isNameValid(name) -> "El nombre es obligatorio."
                            !Validators.isEmailValid(email) -> "Ingresa un email válido."
                            !Validators.isPasswordValid(password) -> "La contraseña debe tener al menos 6 caracteres."
                            genero == "Seleccionar" -> "Por favor selecciona un género."
                            fechaNacimiento == 0L -> "Debes indicar tu fecha de nacimiento."
                            !Validators.arePhysicalStatsValid(peso, altura) -> "Ingresa un peso y altura válidos."
                            else -> null
                        }

                        if (localError == null) {
                            userViewModel.addUser(
                                name = name,
                                email = email,
                                password = password,
                                genero = genero,
                                fechanacimiento = fechaNacimiento,
                                peso = peso.toDoubleOrNull() ?: 0.0,
                                altura = altura.toDoubleOrNull() ?: 0.0,
                                photoUri = selectedImageUri?.toString()
                            )
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}