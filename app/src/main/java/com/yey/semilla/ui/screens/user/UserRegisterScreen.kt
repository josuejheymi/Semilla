package com.yey.semilla.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.ui.viewmodel.UserViewModel

@Composable
fun UserRegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registrar Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                userViewModel.addUser(
                    name = name,
                    email = email,
                    password = password,
                    fechanacimiento = 0L,
                    genero = "Otro",
                    peso = 0.0,
                    altura = 0.0
                )
                navController.popBackStack()
            }
        }) {
            Text("Guardar")
        }
    }
}