package com.yey.semilla.ui.screens.home.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.UserViewModel

@Composable
fun UserListScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val users by userViewModel.users.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Usuarios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(users) { user ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { /* futuro detalle */ }) {
                    Text("${user.name} - ${user.email}", modifier = Modifier.padding(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.UserRegister.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Agregar Usuario")
        }
    }
}