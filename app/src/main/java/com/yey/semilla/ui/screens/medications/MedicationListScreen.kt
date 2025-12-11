package com.yey.semilla.ui.screens.medications

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.yey.semilla.domain.model.MedicationEntity
import com.yey.semilla.ui.components.BottomNavigationBar
import com.yey.semilla.ui.navigation.Screen
import com.yey.semilla.ui.viewmodel.MedicationViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    medicationViewModel: MedicationViewModel
) {
    // Usuario actual (desde UserViewModel)
    val currentUser by userViewModel.currentUser.collectAsState()

    // Lista de medicamentos (desde MedicationViewModel)
    val meds by medicationViewModel.medications.collectAsState()

    // Cuando cambie el usuario logueado, cargamos sus medicamentos
    LaunchedEffect(currentUser?.id) {
        currentUser?.let { user ->
            medicationViewModel.loadMedications(user.id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Medicamentos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddMedication.route) },
                containerColor = Color(0xFF2ECC71)
            ) {
                Text("+", color = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // Si no hay usuario logueado
            if (currentUser == null) {
                Text(
                    text = "Debes iniciar sesión para ver tus medicamentos.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 20.dp)
                )
                return@Column
            }

            // Si la lista está vacía
            if (meds.isEmpty()) {
                Text(
                    text = "No tienes medicamentos registrados.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 20.dp)
                )
                return@Column
            }

            // Lista de medicamentos
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(meds) { med ->
                    MedicationCard(med)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MedicationCard(med: MedicationEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE9F7EF)  // Verde suave
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            if (med.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(med.imageUri),
                    contentDescription = med.name,
                    modifier = Modifier
                        .size(70.dp)
                )
                Spacer(Modifier.width(16.dp))
            }

            Column {
                Text(
                    text = med.name,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Total: ${med.totalPills}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Restantes: ${med.pillsRemaining}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
