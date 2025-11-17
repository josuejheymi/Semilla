package com.yey.semilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.yey.semilla.data.local.database.AppDatabase
import com.yey.semilla.domain.repository.UserRepositoryImpl
import com.yey.semilla.ui.navigation.AppNavHost
import com.yey.semilla.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciamos DB y Repository
        val db = AppDatabase.getInstance(this)
        val userRepo = UserRepositoryImpl(db.userDao())

        // Creamos el ViewModel
        userViewModel = UserViewModel(userRepo)

        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                // Pasamos el ViewModel al NavHost
                AppNavHost(navController = navController, userViewModel = userViewModel)
            }
        }
    }
}
