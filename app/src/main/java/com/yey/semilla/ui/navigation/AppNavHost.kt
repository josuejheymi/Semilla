package com.yey.semilla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yey.semilla.ui.screens.home.user.UserListScreen
import com.yey.semilla.ui.screens.home.user.UserRegisterScreen
import com.yey.semilla.ui.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object UserList : Screen("user_list")
    object UserRegister : Screen("user_register")
}

@Composable
fun AppNavHost(navController: NavHostController, userViewModel: UserViewModel) {
    NavHost(navController = navController, startDestination = Screen.UserList.route) {
        composable(Screen.UserList.route) {
            UserListScreen(navController, userViewModel)
        }
        composable(Screen.UserRegister.route) {
            UserRegisterScreen(navController, userViewModel)
        }
    }
}
