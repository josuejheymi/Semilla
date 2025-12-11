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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.yey.semilla.ui.viewmodel.UserViewModel
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

    // Permisos cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) selectedImageUri = uri }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) selectedImageUri = tempCameraUri }

    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timeStamp.jpg")
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
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
    val generos = listOf("Hombre", "Mujer")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE0FFFA)
    ) {

        // ---------------- UI ----------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Crear cuenta",
                color = Color(0xFF009688),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            // ERRORES (validación + backend)
            errorToShow?.let {
                Text(
                    text = it,
                    color = Color(0xFFFF0000),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
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
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Foto", color = Color.DarkGray)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688),
                        contentColor = Color.White
                    ),
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                        tempCameraUri = createImageUri(context)
                        tempCameraUri?.let { uri ->
                            cameraLauncher.launch(uri)
                        }
                    }
                ) { Text("Cámara") }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF009688),
                        contentColor = Color.White
                    ),
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Text("Galería")
                }
            }

            Spacer(Modifier.height(20.dp))

            // CAMPOS
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            // GÉNERO (Dropdown)
            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = !generoExpanded }
            ) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    generos.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                genero = it
                                generoExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Fecha nacimiento
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009688),
                    contentColor = Color.White
                ),
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (fechaNacimiento == 0L)
                        "Seleccionar fecha de nacimiento"
                    else
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date(fechaNacimiento))
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it.filter { c -> c.isDigit() } },
                label = { Text("Altura (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009688),
                    contentColor = Color.White
                ),
                onClick = {
                    // VALIDACIONES LOCALES
                    localError = when {
                        name.isEmpty() -> "El nombre es obligatorio."
                        !email.contains("@") -> "Ingresa un email válido."
                        password.length < 6 -> "La contraseña debe tener mínimo 6 caracteres."
                        genero == "Seleccionar" -> "Selecciona un género."
                        fechaNacimiento == 0L -> "Selecciona una fecha de nacimiento."
                        peso.isEmpty() || altura.isEmpty() ->
                            "Debes ingresar peso y altura."
                        else -> null
                    }

                    if (localError == null) {
                        // Llamamos al ViewModel (esto ya crea en backend + local)
                        userViewModel.addUser(
                            name = name,
                            email = email,
                            password = password,
                            genero = genero,
                            fechanacimiento = fechaNacimiento,
                            peso = peso.toDouble(),
                            altura = altura.toDouble(),
                            photoUri = selectedImageUri?.toString()
                        )

                        // Navegamos al login
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }
        }
    }
}
