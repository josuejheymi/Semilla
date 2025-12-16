package com.yey.semilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yey.semilla.data.local.database.AppDatabase
import com.yey.semilla.data.remote.RetrofitClient
import com.yey.semilla.data.repository.MedicationRepositoryImpl
import com.yey.semilla.data.repository.ReminderRepositoryImpl
import com.yey.semilla.data.repository.UserRepositoryImpl
import com.yey.semilla.ui.navigation.AppNavHost
import com.yey.semilla.ui.theme.SemillaTheme
import com.yey.semilla.ui.viewmodel.*
import com.yey.semilla.ui.viewmodel.WeatherViewModel
import com.yey.semilla.ui.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Base de datos Room
        val db = AppDatabase.getInstance(this)

        // 2) Repositorios
        val userRepository = UserRepositoryImpl(db.userDao())

        val reminderRepository = ReminderRepositoryImpl(
            reminderDao = db.reminderDao(),
            medicationDao = db.medicationDao(),
            api = RetrofitClient.api
        )

        val medicationRepository = MedicationRepositoryImpl(
            medicationDao = db.medicationDao(),
            api = RetrofitClient.api
        )

        // 5) ViewModels
        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(userRepository)
        }

        val reminderViewModel: ReminderViewModel by viewModels {
            ReminderViewModelFactory(reminderRepository)
        }

        val medicationViewModel: MedicationViewModel by viewModels {
            MedicationViewModelFactory(medicationRepository)
        }

        val weatherViewModel: WeatherViewModel by viewModels {
            WeatherViewModelFactory()
        }

        // 6) Cargar Compose
        setContent {
            SemillaTheme {
                // Agregamos una Surface que ocupa TODO el tamaño (fillMaxSize)
                // Esto le da una estructura sólida a la app para manejar el scroll y el teclado.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        userViewModel = userViewModel,
                        reminderViewModel = reminderViewModel,
                        medicationViewModel = medicationViewModel,
                        weatherViewModel = weatherViewModel
                    )
                }
            }
        }
    }
}