package com.yey.semilla.ui.screens.home.user

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.data.local.model.UserEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    user: UserEntity
) {

    // Estados editables
    var name by remember { mutableStateOf(user.name) }
    var peso by remember { mutableStateOf(user.peso.toString()) }
    var altura by remember { mutableStateOf(user.altura.toString()) }
    var genero by remember { mutableStateOf(user.genero) }
    var birthDate by remember { mutableStateOf(user.fechanacimiento) }
    var imageUri by remember { mutableStateOf<Uri?>(user.photoUri?.let { Uri.parse(it) }) }

    // Selección de imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Perfil") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF009688),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)}
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO DE PERFIL
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Text("Cambiar foto", color = Color.DarkGray)
                }
            }

            Spacer(Modifier.height(20.dp))

            // CAMPOS
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = altura,
                onValueChange = { altura = it.filter { c -> c.isDigit() } },
                label = { Text("Altura (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Género con selector
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    label = { Text("Género") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Hombre") },
                        onClick = {
                            genero = "Hombre"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Mujer") },
                        onClick = {
                            genero = "Mujer"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botón Guardar
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009688),
                    contentColor = Color.White
                ),
                onClick = {
                    val updatedUser = user.copy(
                        name = name,
                        peso = peso.toDoubleOrNull() ?: user.peso,
                        altura = altura.toDoubleOrNull() ?: user.altura,
                        genero = genero,
                        fechanacimiento = birthDate,
                        photoUri = imageUri?.toString()
                    )

                    userViewModel.updateUser(updatedUser)
                    navController.popBackStack()
                }
            ) {
                Text("Guardar cambios")
            }
        }
    }
}
