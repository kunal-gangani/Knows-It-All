package com.example.know_it_all.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.know_it_all.presentation.ui.screen.auth.LoginScreen
import com.example.know_it_all.presentation.ui.screen.auth.RegisterScreen
import com.example.know_it_all.presentation.ui.screen.main.RadarScreen
import com.example.know_it_all.presentation.ui.screen.main.TradeScreen
import com.example.know_it_all.presentation.ui.screen.main.VaultScreen
import com.example.know_it_all.presentation.ui.screen.main.SkillProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Radar : Screen("radar")
    object Trade : Screen("trade")
    object Vault : Screen("vault")
    object SkillProfile : Screen("skill_profile")
}

@Composable
fun KnowItAllNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Radar.route) {
            RadarScreen(navController)
        }
        composable(Screen.Trade.route) {
            TradeScreen(navController)
        }
        composable(Screen.Vault.route) {
            VaultScreen(navController)
        }
        composable(Screen.SkillProfile.route) {
            SkillProfileScreen(navController)
        }
    }
}
