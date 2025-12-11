package com.yey.semilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

        // 2) Repositorio de usuarios (Room + backend via UserViewModel)
        val userRepository = UserRepositoryImpl(db.userDao())

        // 3) Repositorio de recordatorios (Room + Spring Boot)
        val reminderRepository = ReminderRepositoryImpl(
            reminderDao = db.reminderDao(),
            medicationDao = db.medicationDao(),
            api = RetrofitClient.api          //  ahora también pasa la API
        )

        // 4) Repositorio de medicamentos (Room + Spring Boot)
        val medicationRepository = MedicationRepositoryImpl(
            medicationDao = db.medicationDao(),
            api = RetrofitClient.api          //  ya la tenías aquí
        )

        // 5) ViewModels

        // UserViewModel
        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(userRepository)
        }

        // ReminderViewModel
        val reminderViewModel: ReminderViewModel by viewModels {
            ReminderViewModelFactory(reminderRepository)
        }

        // MedicationViewModel
        val medicationViewModel: MedicationViewModel by viewModels {
            MedicationViewModelFactory(medicationRepository)
        }
        //weatherViewModel
        val weatherViewModel: WeatherViewModel by viewModels {
            WeatherViewModelFactory()
        }
        // 6) Cargar Compose
        setContent {
            SemillaTheme {
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
