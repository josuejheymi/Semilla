package com.yey.semilla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yey.semilla.ui.screens.auth.LoginScreen
import com.yey.semilla.ui.screens.auth.RegisterScreen
import com.yey.semilla.ui.screens.auth.SplashScreen
import com.yey.semilla.ui.screens.home.HomeScreen
import com.yey.semilla.ui.screens.home.AddReminderScreen
import com.yey.semilla.ui.screens.home.user.UserListScreen
import com.yey.semilla.ui.screens.home.user.UserRegisterScreen
import com.yey.semilla.ui.viewmodel.ReminderViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object UserList : Screen("user_list")
    object UserRegister : Screen("user_register")
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddReminder : Screen("add_reminder")
    object EditReminder : Screen("edit_reminder/{reminderId}") {
        fun createRoute(reminderId: Int) = "edit_reminder/$reminderId"
    }
    object Profile : Screen("profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    reminderViewModel: ReminderViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ---------------- USUARIO / AUTH ----------------
        composable(Screen.UserList.route) {
            UserListScreen(navController, userViewModel)
        }

        composable(Screen.UserRegister.route) {
            UserRegisterScreen(navController, userViewModel)
        }

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, userViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, userViewModel)
        }

        // ---------------- RECORDATORIOS ----------------
        composable(Screen.Home.route) {
            HomeScreen(navController, reminderViewModel)
        }

        composable(Screen.AddReminder.route) {
            // Pasamos la lista de medicamentos desde el ViewModel
            val medications = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(
                navController = navController,
                reminderViewModel = reminderViewModel,
                medications = medications
            )
        }

        composable(Screen.EditReminder.route) { backStackEntry ->
            // Por ahora solo reutilizamos AddReminderScreen sin edición
            val medications = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(
                navController = navController,
                reminderViewModel = reminderViewModel,
                medications = medications
            )
        }

        // ---------------- PERFIL ----------------
        composable(Screen.Profile.route) {
            // ProfileScreen(navController, userViewModel) -> pendiente
        }
    }
}
