package com.yey.semilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.yey.semilla.data.local.database.AppDatabase
import com.yey.semilla.domain.repository.ReminderRepositoryImpl
import com.yey.semilla.domain.repository.UserRepositoryImpl
import com.yey.semilla.ui.navigation.AppNavHost
import com.yey.semilla.ui.theme.SemillaTheme
import com.yey.semilla.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Inicializamos la base de datos Room
        val db = AppDatabase.getInstance(this)

        // 🔥 Creamos los repositorios reales que acceden a la DB
        val userRepository = UserRepositoryImpl(db.userDao())
        val reminderRepository = ReminderRepositoryImpl(db.reminderDao(), db.medicationDao())

        // 🔥 ViewModel de usuario
        // Se crea usando una Factory que le entrega el userRepository
        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(userRepository)
        }

        // 🔥 ViewModel de recordatorios
        // 🚫 YA NO RECIBE userId AQUÍ
        // El usuario activo se carga dinámicamente luego de login.
        val reminderViewModel: ReminderViewModel by viewModels {
            ReminderViewModelFactory(reminderRepository)
        }

        // 🔥 Composición de la UI usando Jetpack Compose
        setContent {
            SemillaTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    userViewModel = userViewModel,
                    reminderViewModel = reminderViewModel
                )
            }
        }

    }
}
