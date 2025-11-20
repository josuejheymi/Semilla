package com.yey.semilla.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar(
        containerColor = Color(0xFF27AE60),
        contentColor = Color.White
    ) {



        NavigationBarItem(
            selected = currentRoute(navController) == "updates",
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Actualizaciones") },
            label = { Text("Perfil") },
        )
        NavigationBarItem(
            selected = currentRoute(navController) == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = currentRoute(navController) == "medication_list",
            onClick = { navController.navigate("medication_list") },
            icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Medicamentos") },
            label = { Text("Medicamentos") },
        )


    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    return navController.currentBackStackEntry?.destination?.route
}
