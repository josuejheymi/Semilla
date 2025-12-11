package com.yey.semilla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// AUTH
import com.yey.semilla.ui.screens.auth.LoginScreen
import com.yey.semilla.ui.screens.auth.RegisterScreen
import com.yey.semilla.ui.screens.auth.SplashScreen

// HOME
import com.yey.semilla.ui.screens.home.HomeScreen
import com.yey.semilla.ui.screens.home.PerfilScreen
import com.yey.semilla.ui.screens.home.reminder.AddReminderScreen
import com.yey.semilla.ui.screens.home.user.EditProfileScreen

// USERS
import com.yey.semilla.ui.screens.home.user.UserListScreen
import com.yey.semilla.ui.screens.home.user.UserRegisterScreen

// MEDICATIONS
import com.yey.semilla.ui.screens.medications.AddMedicationScreen
import com.yey.semilla.ui.screens.medications.MedicationListScreen
import com.yey.semilla.ui.viewmodel.MedicationViewModel

// VIEWMODELS
import com.yey.semilla.ui.viewmodel.ReminderViewModel
import com.yey.semilla.ui.viewmodel.UserViewModel

// --------------------------------------------------------------
// Rutas de navegación definidas como objetos organizados
// --------------------------------------------------------------
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
    object EditProfile : Screen("edit_profile")

    object AddMedication : Screen("add_medication")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    reminderViewModel: ReminderViewModel,
    medicationViewModel: MedicationViewModel
) {

    // Observa si el login fue correcto
    val loginSuccess = userViewModel.loginSuccess.collectAsState().value

    // Si el login cambia a "true", se ejecuta automáticamente
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {

            // Obtiene el usuario actual
            val user = userViewModel.currentUser.value

            if (user != null) {
                // Carga medicamentos y recordatorios del usuario correcto
                reminderViewModel.loadForUser(user.id)
            }

            // Navega a Home y elimina Login del historial
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // Define todas las pantallas y su navegación
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ----------------------------------------------------------
        // AUTH
        // ----------------------------------------------------------
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }


        composable(Screen.Register.route) {
            RegisterScreen(navController, userViewModel)
        }

        // ----------------------------------------------------------
        // USERS (para pruebas o uso interno)
        // ----------------------------------------------------------
        composable(Screen.UserList.route) {
            UserListScreen(navController, userViewModel)
        }

        composable(Screen.UserRegister.route) {
            UserRegisterScreen(navController, userViewModel)
        }

        // ----------------------------------------------------------
        // PERFIL
        // ----------------------------------------------------------
        composable(Screen.EditProfile.route) {
            val user = userViewModel.currentUser.collectAsState().value
            if (user != null) {
                EditProfileScreen(navController, userViewModel, user)
            } else {
                // Si quieres, redirigir al login
                navController.navigate(Screen.Login.route)
            }
        }

        composable(Screen.Profile.route) {
            PerfilScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        // ----------------------------------------------------------
        // HOME PRINCIPAL
        // ----------------------------------------------------------
        composable(Screen.Home.route) {
            val user = userViewModel.currentUser.collectAsState().value
            HomeScreen(
                navController = navController,
                reminderViewModel = reminderViewModel,
                userViewModel = userViewModel,
                user = user
            )
        }

        // ----------------------------------------------------------
        // REMINDERS
        // ----------------------------------------------------------
        composable(Screen.AddReminder.route) {
            val meds = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(navController, reminderViewModel, meds)
        }

        composable(Screen.EditReminder.route) {
            val meds = reminderViewModel.medications.collectAsState().value
            AddReminderScreen(navController, reminderViewModel, meds)
        }

        // ----------------------------------------------------------
        // MEDICATIONS
        // ----------------------------------------------------------
        composable(Screen.AddMedication.route) {
            AddMedicationScreen(
                navController = navController,
                userViewModel = userViewModel,
                medicationViewModel = medicationViewModel
            )
        }


        composable("medication_list") {
            MedicationListScreen(
                navController = navController,
                userViewModel = userViewModel,
                medicationViewModel = medicationViewModel
            )
        }
    }
}
