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
import com.yey.semilla.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = 1
        val db = AppDatabase.getInstance(this)

        val userRepository = UserRepositoryImpl(db.userDao())
        val reminderRepository = ReminderRepositoryImpl(db.reminderDao(), db.medicationDao())

        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(userRepository)
        }
        val reminderViewModel: ReminderViewModel by viewModels {
            ReminderViewModelFactory(reminderRepository, userId)
        }

        setContent {
            val navController = rememberNavController()
            AppNavHost(
                navController = navController,
                userViewModel = userViewModel,
                reminderViewModel = reminderViewModel
            )
        }
    }
}
