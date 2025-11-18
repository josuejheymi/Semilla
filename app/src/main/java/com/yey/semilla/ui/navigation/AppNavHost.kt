package com.yey.semilla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// 👉 AUTH
import com.yey.semilla.ui.screens.auth.LoginScreen
import com.yey.semilla.ui.screens.auth.RegisterScreen
import com.yey.semilla.ui.screens.auth.SplashScreen

// 👉 HOME & REMINDERS
import com.yey.semilla.ui.screens.home.HomeScreen
import com.yey.semilla.ui.screens.home.reminder.AddReminderScreen

// 👉 USERS
import com.yey.semilla.ui.screens.home.user.UserListScreen
import com.yey.semilla.ui.screens.home.user.UserRegisterScreen

// 👉 MEDICATIONS
import com.yey.semilla.ui.screens.medications.AddMedicationScreen

// 👉 VIEWMODELS
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

    // 👉 Nuevo
    object AddMedication : Screen("add_medication")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    reminderViewModel: ReminderViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ---------------- AUTH ----------------
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController, userViewModel) }
        composable(Screen.Register.route) { RegisterScreen(navController, userViewModel) }

        // ---------------- USERS ----------------
        composable(Screen.UserList.route) { UserListScreen(navController, userViewModel) }
        composable(Screen.UserRegister.route) { UserRegisterScreen(navController, userViewModel) }

        // ---------------- HOME ----------------
        composable(Screen.Home.route) {
            val user = userViewModel.currentUser.collectAsState().value
            HomeScreen(navController, reminderViewModel, userViewModel, user)
        }

        // ---------------- REMINDERS ----------------
        composable(Screen.AddReminder.route) {
            val meds = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(navController, reminderViewModel, meds)
        }

        composable(Screen.EditReminder.route) { backStackEntry ->
            val meds = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(navController, reminderViewModel, meds)
        }

        // ---------------- MEDICATIONS ----------------
        composable(Screen.AddMedication.route) {
            val user = userViewModel.currentUser.collectAsState().value
            AddMedicationScreen(navController, reminderViewModel, user!!.id)
        }

        // ---------------- PROFILE ----------------
        composable(Screen.Profile.route) {
            // Próximamente: Perfil
        }
    }
}
